package io.github.krloxz.fws.springframework;

import org.springframework.hateoas.AffordanceModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.LinkRelation;
import org.springframework.hateoas.MediaTypes;

/**
 * A {@link Link} that renders as an affordance.
 * <p>
 * Affordance links are used as a simple alternative to HAL FORMS that still allows to communicate
 * the capabilities of a resource. They are created from a {@link Link} and are rendered as a link
 * with a relation named after the name of the class method used to create the link (HAL affordance)
 * when the {@link #AFFORDANCE_REL} is used.
 *
 * @author Carlos Gomez
 */
public class AffordanceLink extends Link {

  public static final LinkRelation AFFORDANCE_REL = LinkRelation.of("affordance");
  private static final long serialVersionUID = 6581720194164681180L;
  private final AffordanceModel affordance;

  /**
   * Creates a new {@link AffordanceLink} from a given {@link Link}.
   *
   * @param link
   *        the link to be converted into an affordance link
   */
  public AffordanceLink(final Link link) {
    super(link.getHref(), link.getRel());
    this.affordance = link.getAffordances()
        .get(0)
        .getAffordanceModel(MediaTypes.HAL_FORMS_JSON);
  }

  /**
   * @return the name of the HTTP method of this affordance
   */
  public String getMethod() {
    return this.affordance.getHttpMethod().name();
  }

  /**
   * @return the standard IANA relation of this affordance if it has one, the name of the affordance
   *         otherwise
   */
  @Override
  public LinkRelation getRel() {
    if (AFFORDANCE_REL.equals(super.getRel())) {
      return LinkRelation.of(this.affordance.getName());
    }
    return super.getRel();
  }

}
