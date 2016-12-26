package io.sugo.pio.ports.metadata;

/**
 * A rule that transforms meta data, e.g. delivering it from one port to another or by generating
 * new.
 *
 */
public interface MDTransformationRule {
	public void transformMD();
}
