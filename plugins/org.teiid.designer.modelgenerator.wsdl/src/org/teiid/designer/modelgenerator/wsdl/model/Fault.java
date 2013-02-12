/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.modelgenerator.wsdl.model;

import org.teiid.designer.query.proc.wsdl.model.IFault;

/**
 * @since 8.0
 */
public interface Fault extends IFault, WSDLElement {
	
	void setMessage(Message message);
	
	@Override
    Message getMessage();
	
	@Override
    Operation getOperation();
	
	@Override
    Fault copy();

}
