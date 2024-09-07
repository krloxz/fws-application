package io.github.krloxz.fws.common;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import java.util.Optional;

import org.springframework.hateoas.AffordanceModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.LinkRelation;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpMethod;

/**
 * A {@link Link} that renders as an affordance.
 * <p>
 * Affordance links are used to describe the capabilities of a resource and are a simple alternative
 * to HAL FORMS. They are rendered as entries of a JSON map:
 *
 * <pre>
 * "relation-name" : {
 *   "href" : "/path/to/resource",
 *   "method" : "HTTP_METHOD"
 *  }
 * </pre>
 * <ul>
 * <li>The relation name describes the purpose of the link and could be any string or a standard
 * IANA-based link relation
 * <li>Alike links, the href property points to an HTTP resource
 * <li>The method specifies the HTTP method to be used when interacting with the resource
 * </ul>
 *
 * @author Carlos Gomez
 */
public class AffordanceLink extends Link {

  private static final long serialVersionUID = 6581720194164681180L;
  private static final LinkRelation AFFORDANCE_REL = LinkRelation.of("affordance");
  private final String method;

  private AffordanceLink(final String href, final LinkRelation relation, final String method) {
    super(href, relation);
    this.method = method;
  }

  /**
   * Creates an affordance link for the invocation of a controller method.
   *
   * @param methodInvocation
   *        a controller method invocation created with
   *        {@link org.springframework.hateoas.server.mvc.WebMvcLinkBuilder#methodOn}
   * @return an affordance link for the given method
   */
  public static AffordanceLink affordanceLinkTo(final Object methodInvocation) {
    return toAffordanceLink(linkTo(methodInvocation).withRel(AFFORDANCE_REL));
  }

  /**
   * Attempts to extract the {@link Link#getAffordances() affordance information} from the given link
   * and creates an affordance link from it. If an affordance is present, the relation name and the
   * method are extracted from it; otherwise, the relation name is extracted from the given link and
   * the method defaults to GET.
   *
   * @param link
   *        the link to convert
   * @return the given link as an affordance link
   */
  public static AffordanceLink toAffordanceLink(final Link link) {
    final Optional<AffordanceModel> firstAffordance = link.getAffordances()
        .stream()
        .findFirst()
        .map(affordance -> affordance.getAffordanceModel(MediaTypes.HAL_FORMS_JSON));
    final var relation = firstAffordance.map(AffordanceModel::getName)
        .map(LinkRelation::of)
        .orElseGet(link::getRel);
    final var method = firstAffordance.map(AffordanceModel::getHttpMethod)
        .map(HttpMethod::name)
        .orElse("GET");
    return new AffordanceLink(link.getHref(), relation, method);
  }

  /**
   * @return the HTTP method to be used when interacting with this link
   */
  public String getMethod() {
    return this.method;
  }

  @Override
  public Link withSelfRel() {
    return withRel(IanaLinkRelations.SELF);
  }

  @Override
  public Link withRel(final String relation) {
    return withRel(LinkRelation.of(relation));
  }

  @Override
  public Link withRel(final LinkRelation relation) {
    return new AffordanceLink(getHref(), relation, this.method);
  }

}
