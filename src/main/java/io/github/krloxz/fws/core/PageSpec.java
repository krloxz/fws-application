package io.github.krloxz.fws.core;

/**
 * Specification for a page of items to be retrieved.
 *
 * @param number
 *        0-based page number
 * @param size
 *        number of items per page
 * @author Carlos Gomez
 */
public record PageSpec(int number, int size) {

}
