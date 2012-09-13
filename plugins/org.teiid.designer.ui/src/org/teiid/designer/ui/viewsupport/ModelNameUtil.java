/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.ui.viewsupport;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.runtime.IStatus;

/**
 * Utility class providing model name analysis and validation methods
 */
public abstract class ModelNameUtil {
	/**
	 * Option to ignore case
	 */
	static public final int IGNORE_CASE = 1;    				// 00000001
	
	/**
	 * Option to force validation to insure no existing model in defined project or folder
	 */
	static public final int NO_EXISTING_MODEL_AT_LOCATION = 2; // 00000010
	
	/**
	 * Option to force validation to insure no model exists in project with the given name
	 */
	static public final int NO_DUPLICATE_MODEL_NAMES = 4;     	// 00000100
	
	/**
	 * Option to force validation to insure no model exists in project with the given name other than in the
	 * target project or folder
	 */
	static public final int NO_DUPLICATE_MODEL_NAMES_OTHER_THAN_LOCATION = 8;     	// 00001000


    /**
     * Determine if the proposed model name is valid, and return an error message if it is not.
     * 
     * @param proposedModelName the proposed model name
     * @param fileExtension the expected file extension
     * @param resource some resource in the target project
     * @param flags ignore case when comparing model names
     * @return status the name status
     */
    public static IStatus validate( String proposedModelName,
                                    String fileExtension,
                                    IContainer resource,
                                    int flags) {
    	
    	ModelNameChecker checker = new ModelNameChecker(proposedModelName, fileExtension, resource, flags);
    	
    	return checker.validate();
    }
    
    /**
     * Determine if the proposed model name is valid, and return an error message if it is not.
     * 
     * @param proposedModelName the proposed model name
     * @param fileExtension the expected file extension
     * @param flags ignore case when comparing model names
     * @return status the name status
     */
    public static IStatus validate( String proposedModelName,
                                    String fileExtension,
                                    int flags) {
    	
    	ModelNameChecker checker = new ModelNameChecker(proposedModelName, fileExtension, null, flags);
    	
    	return checker.validate();
    }
}
