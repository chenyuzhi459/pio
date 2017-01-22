package io.sugo.pio.repository;

import java.util.List;


/**
 * This interface defines loading and saving methods which are used by the {@link RepositoryManager}
 * to retrieve and store the {@link Repository}-configuration. Custom implementations should replace
 * the default provider by calling {@link RepositoryManager#setProvider(RepositoryProvider)}.
 *
 * @author Marcel Michel
 *
 */
public interface RepositoryProvider {

	/**
	 * The loading mechanism will be triggered during the {@link RepositoryManager#init()} method.
	 * The implementation should read the configuration and return parsed {@link Repository}s as
	 * {@link List}.
	 *
	 * @return the loaded {@link Repository} entries as {@link List}
	 */
	public List<Repository> load();

	/**
	 * The saving mechanism will be triggered after every
	 * {@link RepositoryManager#addRepository(Repository)} call. The implementation should save the
	 * delivered {@link Repository} entries.
	 *
	 * @param repositories
	 *            the {@link Repository} entries which should be saved
	 */
	public void save(List<Repository> repositories);

}
