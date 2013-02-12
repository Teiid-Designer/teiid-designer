/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.modelgenerator.wsdl.model;

import org.teiid.designer.query.proc.wsdl.model.IMessage;

/**
 * @since 8.0
 */
public interface Message extends IMessage, WSDLElement {
	
	@Override
    Part[] getParts();	
	
	void setParts(Part[] parts);
	
	@Override
    Operation getOperation();
	
	@Override
    Fault getFault();
	
	void setType(int Type);

	void setUse(String use);

	void setNamespaceURI(String ns);
	
	void setEncodingStyle(String style);

	@Override
	Message copy();
}
