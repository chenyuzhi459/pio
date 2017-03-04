package io.sugo.pio.operator.clustering;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;


/**
 * Represents an individual cluster, storing all examples by remembering their ids
 * 
 * @author Sebastian Land
 */
public class Cluster implements Serializable {

	private static final long serialVersionUID = -7437913251661093493L;
	private ArrayList<Object> exampleIds;
	private int clusterId;

	public Cluster(int clusterId) {
		this.clusterId = clusterId;
		this.exampleIds = new ArrayList<Object>();
	}

	/**
	 * Get all ids of the examples associated with this cluster.
	 * 
	 * @return Iterator of String
	 */
	public Collection<Object> getExampleIds() {
		return exampleIds;
	}

	public boolean containsExampleId(Object id) {
		return exampleIds.contains(id);
	}

	/**
	 * Get the id of the cluster.
	 * 
	 * @return Object
	 */
	public int getClusterId() {
		return clusterId;
	}

	/**
	 * Returns the number of examples in this cluster
	 * 
	 * @return number of examples
	 */
	public int getNumberOfExamples() {
		return exampleIds.size();
	}

	public void assignExample(Object exampleId) {
		exampleIds.add(exampleId);
	}

	@Override
	public String toString() {
		return "cluster_" + clusterId;
	}
}
