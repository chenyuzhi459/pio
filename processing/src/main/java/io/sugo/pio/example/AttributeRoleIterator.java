package io.sugo.pio.example;

import java.util.Iterator;


/**
 * An iterator for attribute roles which is able to iterate over all attributes or skip either
 * regular or special attributes.
 * 
 * @author Ingo Mierswa
 */
public class AttributeRoleIterator implements Iterator<AttributeRole> {

	private Iterator<AttributeRole> parent;

	private int type = Attributes.REGULAR;

	private AttributeRole current = null;

	public AttributeRoleIterator(Iterator<AttributeRole> parent, int type) {
		this.parent = parent;
		this.type = type;
	}

	@Override
	public boolean hasNext() {
		while ((current == null) && (parent.hasNext())) {
			AttributeRole candidate = parent.next();
			switch (type) {
				case Attributes.REGULAR:
					if (!candidate.isSpecial()) {
						current = candidate;
					}
					break;
				case Attributes.SPECIAL:
					if (candidate.isSpecial()) {
						current = candidate;
					}
					break;
				case Attributes.ALL:
					current = candidate;
					break;
				default:
					break;
			}
		}
		return current != null;
	}

	@Override
	public AttributeRole next() {
		hasNext();
		AttributeRole returnValue = current;
		current = null;
		return returnValue;
	}

	@Override
	public void remove() {
		parent.remove();
		this.current = null;
	}
}
