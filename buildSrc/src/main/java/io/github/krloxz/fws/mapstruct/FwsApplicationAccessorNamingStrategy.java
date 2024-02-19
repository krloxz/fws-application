/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package io.github.krloxz.fws.mapstruct;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeKind;

import org.mapstruct.ap.spi.AccessorNamingStrategy;
import org.mapstruct.ap.spi.ImmutablesAccessorNamingStrategy;

/**
 * A custom {@link AccessorNamingStrategy} designed to support the requirements of the FWS
 * Application:
 * <ul>
 * <li>Support Immutable Objects (see {@link ImmutablesAccessorNamingStrategy})
 * <li>Ignore fluent setters from JOOQ classes and value setters from generated JOOQ records
 * <li>Include getters with no get prefix (e.g. property()) when they don't come from JOOQ classes
 * or generated JOOQ records
 * <li>Get property names from getters with no get prefix
 * </ul>
 *
 * @author Carlos Gomez
 */
public class FwsApplicationAccessorNamingStrategy extends ImmutablesAccessorNamingStrategy {

  @Override
  protected boolean isFluentSetter(final ExecutableElement method) {
    return super.isFluentSetter(method) && !isJooqMethod(method) && !isJooqValueMethod(method);
  }

  @Override
  public boolean isGetterMethod(final ExecutableElement method) {
    return super.isGetterMethod(method) || isModernGetter(method);
  }

  @Override
  public String getPropertyName(final ExecutableElement getterOrSetterMethod) {
    if (isModernGetter(getterOrSetterMethod)) {
      return getterOrSetterMethod.getSimpleName().toString();
    }
    return super.getPropertyName(getterOrSetterMethod);
  }

  private boolean isJooqMethod(final ExecutableElement method) {
    return method.getEnclosingElement().asType().toString().startsWith("org.jooq");
  }

  private boolean isJooqValueMethod(final ExecutableElement method) {
    final var methodName = method.getSimpleName().toString();
    return isEnclosedByJooqRecord(method) && methodName.startsWith("value");
  }

  private boolean isModernGetter(final ExecutableElement method) {
    return !isJooqMethod(method)
        && !isEnclosedByJooqRecord(method)
        && !isSetterMethod(method)
        && !isPresenceCheckMethod(method)
        && method.getParameters().isEmpty()
        && method.getReturnType().getKind() != TypeKind.VOID;
  }

  private boolean isEnclosedByJooqRecord(final ExecutableElement method) {
    final var enclosingType = method.getEnclosingElement().asType();
    final var jooqRecordType = this.elementUtils.getTypeElement("org.jooq.Record").asType();
    return this.typeUtils.isAssignable(enclosingType, jooqRecordType);
  }

}
