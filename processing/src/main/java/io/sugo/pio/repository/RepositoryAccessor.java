package io.sugo.pio.repository;

/**
 * Marks an object accessing a {@link RepositoryManager} or other resource. Objects of this class
 * will be used as keys to a cache map, so implement hashCode() and equals() properly.
 * 
 *
 */
public interface RepositoryAccessor {

}
