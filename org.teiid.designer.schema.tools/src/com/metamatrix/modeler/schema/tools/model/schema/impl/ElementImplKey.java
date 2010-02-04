/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.schema.tools.model.schema.impl;

import org.eclipse.xsd.XSDElementDeclaration;
import com.metamatrix.modeler.schema.tools.model.schema.SchemaObjectKey;

public class ElementImplKey implements SchemaObjectKey {

	XSDElementDeclaration elem;
	
	public ElementImplKey(XSDElementDeclaration elem) {
		this.elem = elem; 
	}

	@Override
    public boolean equals(Object obj) {
		boolean result = false;
		if(obj instanceof ElementImplKey){
			ElementImplKey other = (ElementImplKey)obj;
			if(other.elem == this.elem){
				result = true;
			}
		}
		return result;
	}

	@Override
    public int hashCode() {
		return elem.hashCode();
	}
}
