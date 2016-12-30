package io.sugo.pio.ports.metadata;

/**
 * Can be used to indicate whether we know the attribute set exactly, or only know that it is a
 * subset/superset of a given set.
 * 
 * @author Simon Fischer
 * */
public enum SetRelation {
	UNKNOWN("unknown"), SUPERSET("\u2287"), SUBSET("\u2286"), EQUAL("=");

	private String description;

	private SetRelation(String description) {
		this.description = description;
	}

	public SetRelation merge(SetRelation relation) {
		switch (relation) {
			case EQUAL:
				return this;
			case SUBSET:
				switch (this) {
					case SUBSET:
					case EQUAL:
						return SUBSET;
					case SUPERSET:
					case UNKNOWN:
						return UNKNOWN;
				}
			case SUPERSET:
				switch (this) {
					case SUPERSET:
					case EQUAL:
						return SUPERSET;
					case SUBSET:
					case UNKNOWN:
						return UNKNOWN;
				}
			case UNKNOWN:
			default:
				return UNKNOWN;
		}
	}

	@Override
	public String toString() {
		return description;
	}
}
