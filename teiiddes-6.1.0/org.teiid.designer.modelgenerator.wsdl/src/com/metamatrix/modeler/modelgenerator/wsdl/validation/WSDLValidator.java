/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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
