/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

package com.metamatrix.modeler.modelgenerator.wsdl.model;

/**
 * 
 * This class represents a services as defined by as WSDL
 * 
 * @author JChoate
 *
 */
public interface Service extends WSDLElement {
	
	/**
	 * 
	 * @return an array of ports defined by the service
	 */
	public Port[] getPorts();
	
	/**
	 * 
	 * @param ports the ports that this service defines
	 */
	public void setPorts(Port[] ports);

	public void setModel(Model theModel);
	
	public Model getModel();
	
}
