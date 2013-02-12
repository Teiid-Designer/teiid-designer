/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.modelgenerator.wsdl.model;

import org.teiid.designer.query.proc.wsdl.model.IPart;

/**
 * @since 8.0
 */
public interface Part extends IPart, WSDLElement {

	void setElementName(String name);
	
	void setElementNamespace(String namespace);
	
	void setTypeName(String name);
	
	void setTypeNamespace(String namespace);
	
	@Override
    Message getMessage();
	
	@Override
	Part copy();
	
}
