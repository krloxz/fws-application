package io.github.krloxz.fws;

import static io.github.krloxz.fws.springframework.AffordanceLink.affordanceLinkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.CollectionModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.krloxz.fws.freelancer.application.FreelancersApiController;
import io.github.krloxz.fws.project.application.ProjectsApiController;

/**
 * Restful controller that exposes the resources that compose the Freelancer Web Services API.
 *
 * @author Carlos Gomez
 */
@RestController
@RequestMapping("/")
public class FwsApiController {

  /**
   * @return the resources that compose the Freelancer Web Services API
   */
  @GetMapping
  public CollectionModel<Object> listResources() {
    return CollectionModel.empty(
        affordanceLinkTo(methodOn(FwsApiController.class).listResources()).withSelfRel(),
        affordanceLinkTo(methodOn(FreelancersApiController.class).list(null)).withRel("freelancers"),
        affordanceLinkTo(methodOn(ProjectsApiController.class).projects(null)).withRel("projects"));
  }

}
