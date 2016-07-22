/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.vdb.ui.util;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.teiid.core.designer.util.StringConstants;
import org.teiid.designer.core.validation.rules.StringNameValidator;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.vdb.VdbConstants;
import org.teiid.designer.vdb.VdbVersionUtil;

/**
 * Provides methods to validate vdb names based on version
 * 
 * @author blafond
 *
 */
public class VdbNameValidator extends StringNameValidator {

	/**
	 * Initializes the validator to default values and adds _ - and . as valid chars
	 */
	public VdbNameValidator() {
		super(StringNameValidator.DEFAULT_MINIMUM_LENGTH,
              StringNameValidator.DEFAULT_MAXIMUM_LENGTH,
              new char[] {'_', '-', '.'});
	}

	/**
	 * @param name
	 * @param serverVersion
	 * @return status of the name validation
	 */
	public IStatus isValidVdbFileName(String name, ITeiidServerVersion serverVersion) {
		IStatus result = Status.OK_STATUS;
		
		// TODO Auto-generated method stub
		// If it ends in a .vdb, remove the .vdb
		String nameWithoutExt = name;
		if( name.endsWith(VdbConstants.DOT_VDB) ) {
			nameWithoutExt = name.substring(0, name.length()-4);
		}
		boolean isValid = isValidName(nameWithoutExt);
		
		if( isValid  && nameWithoutExt.contains(StringConstants.DOT) ) {
			// remove the first segment up to first DOT
			int firstDotIndex = nameWithoutExt.indexOf(StringConstants.DOT);
	    	String versionString = nameWithoutExt.substring(firstDotIndex + 1);
	    	
	    	result = VdbVersionUtil.isVdbNameWithVersionValid(serverVersion, versionString);
		}
		
		
		return result;
	}
	
	

}
