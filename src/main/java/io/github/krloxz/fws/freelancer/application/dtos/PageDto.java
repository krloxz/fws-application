package io.github.krloxz.fws.freelancer.application.dtos;

import java.util.List;

/**
 * DTO to carry a page of elements.
 *
 * @param <T>
 *        the type of elements in the page
 * @param elements
 *        the elements in the page
 * @param number
 *        the 0-based page number
 * @param size
 *        the number of elements in the page
 * @param totalElements
 *        the total number of elements in the collection
 * @author Carlos Gomez
 */
public record PageDto<T>(List<T> elements, int number, int size, int totalElements) {

  // Empty

}
