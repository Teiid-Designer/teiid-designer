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

package com.metamatrix.modeler.modelgenerator.wsdl.validation;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.MultiStatus;

public interface WSDLValidator {
	
	/**
	 * 
	 * This method will validate that the WSDL file is valid for use in the current context
	 * 
	 * @param fileUri The location of the WSDL file to validate
	 * @return is the WSDL valid
	 */
	public MultiStatus validateWSDL(String fileUri);
	
	public MultiStatus validateWSDL(String fileUri, IProgressMonitor monitor);

}
