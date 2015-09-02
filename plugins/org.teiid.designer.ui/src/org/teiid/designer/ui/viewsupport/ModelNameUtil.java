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
import org.teiid.core.designer.util.StringConstants;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.ui.UiConstants;

/**
 * Utility class providing model name analysis and validation methods
 * @since 8.0
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
	
	public interface MESSAGES {
		String INVALID_MODEL_NAME = UiConstants.Util.getString("ModelNameUtil.invalidModelName") + StringConstants.SPACE; //$NON-NLS-1$
		String INVALID_SOURCE_MODEL_NAME = UiConstants.Util.getString("ModelNameUtil.invalidSourceModelName") + StringConstants.SPACE; //$NON-NLS-1$
		String INVALID_VIEW_MODEL_NAME = UiConstants.Util.getString("ModelNameUtil.invalidViewModelName") + StringConstants.SPACE; //$NON-NLS-1$
		String INVALID_SCHEMA_FILE_NAME = UiConstants.Util.getString("ModelNameUtil.invalidSchemaFileName") + StringConstants.SPACE; //$NON-NLS-1$

	}

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
    
    /**
     * Method to generate a unique model name if a model name already exits in the project
     * 
     * This is designed for importers that auto-create names during the import
     * 
     * @param proposedNameWithoutExtension the proposed file name
     * @param project the target project
     * @return a unique model name within that project
     */
    public static String getNewUniqueModelName(String proposedNameWithoutExtension, IContainer project) {
    	ModelNameChecker checker = new ModelNameChecker(
    			proposedNameWithoutExtension, ModelerCore.MODEL_FILE_EXTENSION,	project, NO_DUPLICATE_MODEL_NAMES);
    	
    	IStatus status = checker.validate();
    	
    	if( status.getSeverity() == IStatus.ERROR) {
    		// We have duplicate model names
    		int count = 1;
    		String newName = proposedNameWithoutExtension + '_' + Integer.toString(count);
    		
    		while( status.getSeverity() == IStatus.ERROR && count < 20 ) {
    			checker = new ModelNameChecker(newName, ModelerCore.MODEL_FILE_EXTENSION, 
    	    					project, NO_DUPLICATE_MODEL_NAMES);
    			count++;
    			if( checker.validate().getSeverity() != IStatus.ERROR) {
    				return newName;
    			}
    			newName = proposedNameWithoutExtension + '_' + Integer.toString(count);
    		}
    		
    	}
    	
    	return proposedNameWithoutExtension;
    }
}
