package io.sugo.pio.ports.metadata;

/**
 * This precondition checks whether the delivered object is of a given type or a collection of the
 * given type (or a collection of such collections etc.).
 * 
 * @author Simon Fischer
 * 
 */
public class CollectionPrecondition implements Precondition {

	private final Precondition nestedPrecondition;

	public CollectionPrecondition(Precondition precondition) {
		this.nestedPrecondition = precondition;
	}

	@Override
	public void assumeSatisfied() {
		nestedPrecondition.assumeSatisfied();
	}

	@Override
	public void check(MetaData md) {
		if (md != null) {
			if (md instanceof CollectionMetaData) {
				check(((CollectionMetaData) md).getElementMetaData());
				return;
			}
		}
		nestedPrecondition.check(md);
	}

	@Override
	public String getDescription() {
		return nestedPrecondition + " (collection)";
	}

	@Override
	public boolean isCompatible(MetaData input, CompatibilityLevel level) {
		if (input instanceof CollectionMetaData) {
			return isCompatible(((CollectionMetaData) input).getElementMetaData(), level);
		} else {
			return nestedPrecondition.isCompatible(input, level);
		}
	}

	@Override
	public MetaData getExpectedMetaData() {
		return new CollectionMetaData(nestedPrecondition.getExpectedMetaData());
	}

}
