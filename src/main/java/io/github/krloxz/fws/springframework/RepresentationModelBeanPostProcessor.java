package io.github.krloxz.fws.springframework;

import java.util.HashMap;
import java.util.Map;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.reactive.ReactiveRepresentationModelAssembler;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.HandlerResult;
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.server.ServerWebExchange;

import io.github.krloxz.fws.freelancer.application.FreelancerDtoAssembler;
import io.github.krloxz.fws.freelancer.application.dtos.FreelancerDto;
import io.github.krloxz.fws.freelancer.application.dtos.PageDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * {@link BeanPostProcessor} that maps {@link Flux} and {@link Mono} instances returned by Spring
 * Controllers into {@link RepresentationModel} instances using the
 * {@link ReactiveRepresentationModelAssembler}s that are registered in the Spring container.
 * <p>
 * This class helps to decouple Spring Controllers and Spring HATEOAS, so that they continue to
 * return domain DTO's only and the controller code is better organized and easier to read.
 *
 * @author Carlos Gomez
 */
@Component
@SuppressWarnings("unchecked")
class RepresentationModelBeanPostProcessor implements BeanPostProcessor {

  @SuppressWarnings("rawtypes")
  private final Map<Class<?>, ReactiveRepresentationModelAssembler> assemblersByDomainType = new HashMap<>();

  @Override
  public Object postProcessAfterInitialization(final Object bean, final String beanName) {
    if (bean instanceof final ReactiveRepresentationModelAssembler assembler) {
      registerAssembler(assembler);
    }
    if (bean instanceof final RequestMappingHandlerAdapter handlerAdapter) {
      return proxyBeanToInterceptHandleMethod(handlerAdapter);
    }
    return bean;
  }

  private void registerAssembler(final ReactiveRepresentationModelAssembler<?, ?> assembler) {
    final var domainType = ResolvableType.forInstance(assembler)
        .as(ReactiveRepresentationModelAssembler.class)
        .resolveGeneric(0);
    this.assemblersByDomainType.put(domainType, assembler);
  }

  private Object proxyBeanToInterceptHandleMethod(final RequestMappingHandlerAdapter handlerAdapter) {
    final var factory = new ProxyFactory(handlerAdapter);
    factory.addAdvice((MethodInterceptor) this::interceptHandleMethod);
    return factory.getProxy();
  }

  private Object interceptHandleMethod(final MethodInvocation invocation) throws Throwable {
    if ("handle".equals(invocation.getMethod().getName())) {
      final var handlerResultMono = (Mono<HandlerResult>) invocation.proceed();
      return handlerResultMono.map(
          handlerResult -> toHandlerResultWithReturnValueAsRepresentationModel(handlerResult, invocation));
    }
    return invocation.proceed();
  }

  private HandlerResult toHandlerResultWithReturnValueAsRepresentationModel(
      final HandlerResult handlerResult, final MethodInvocation invocation) {

    final var domainType = getDomainType(handlerResult);
    if (!this.assemblersByDomainType.containsKey(domainType)) {
      return handlerResult;
    }

    final var assembler = this.assemblersByDomainType.get(domainType);
    final var exchange = (ServerWebExchange) invocation.getArguments()[0];
    final var returnType = handlerResult.getReturnType().resolve();

    Object newReturnValue;
    MethodParameter newReturnType;

    if (Mono.class.isAssignableFrom(returnType)) {
      final var monoType = handlerResult.getReturnType().resolveGeneric(0);
      if (PageDto.class.isAssignableFrom(monoType)) {
        final var returnMono = (Mono<?>) handlerResult.getReturnValue();
        // TODO: Extend the assembler interface to support pages
        newReturnValue = returnMono.flatMap(
            page -> ((FreelancerDtoAssembler) assembler).toPagedModel((PageDto<FreelancerDto>) page, exchange));
        newReturnType = getReturnType(
            assembler.getClass(), "toPagedModel", PageDto.class, ServerWebExchange.class);
      } else {
        final var returnMono = (Mono<?>) handlerResult.getReturnValue();
        newReturnValue = returnMono.flatMap(dto -> assembler.toModel(dto, exchange));
        newReturnType = getReturnType(
            assembler.getClass(), "toModel", domainType, ServerWebExchange.class);
      }
    } else if (Flux.class.isAssignableFrom(returnType)) {
      final var returnFlux = (Flux<?>) handlerResult.getReturnValue();
      newReturnValue = assembler.toCollectionModel(returnFlux, exchange);
      newReturnType = getReturnType(
          assembler.getClass(), "toCollectionModel", Flux.class, ServerWebExchange.class);
    } else {
      throw new IllegalStateException("Unsupported return type: " + returnType);
    }

    final var newHandlerResult = new HandlerResult(
        handlerResult.getHandler(),
        newReturnValue,
        newReturnType,
        handlerResult.getBindingContext());
    newHandlerResult.setExceptionHandler(handlerResult.getExceptionHandler());

    return newHandlerResult;
  }

  private Class<?> getDomainType(final HandlerResult handlerResult) {
    var domainType = handlerResult.getReturnType().resolveGeneric(0);
    if (PageDto.class.isAssignableFrom(domainType)) {
      domainType = handlerResult.getReturnType().resolveGeneric(0, 0);
    }
    return domainType;
  }

  private MethodParameter getReturnType(final Class<?> clazz, final String methodName, final Class<?>... parameters) {
    try {
      return new MethodParameter(clazz.getMethod(methodName, parameters), -1);
    } catch (final Exception e) {
      throw new IllegalStateException("Unexpected exception found while getting return type", e);
    }
  }

}
