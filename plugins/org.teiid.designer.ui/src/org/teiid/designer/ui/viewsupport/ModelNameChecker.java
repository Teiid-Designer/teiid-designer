/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.ui.viewsupport;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.designer.ui.PluginConstants;
import org.teiid.designer.ui.UiConstants;

/**
 * @since 8.0
 *
 */
public class ModelNameChecker extends ModelNameUtil implements UiConstants {
	private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(ModelNameChecker.class);
	
    /**
     * @since 4.2
     */
    private static String getString( final String id ) {
        return Util.getString(I18N_PREFIX + id);
    }
    
    /**
     * @since 4.2
     */
    private static String getString( final String id , final String param) {
        return Util.getString(I18N_PREFIX + id, param);
    }
    
	private boolean ignoreCase = false;
	private boolean noDuplicateModelNames = false;
	private boolean noExistingModelNameAtLocation = false;
	private boolean noDuplicateModelNamesOtherThanLocation = false;
	
	private String originalName;
	private String originalNameWithoutExtension;
	private String originalFileExtension;
	
	private IProject project;
	private IContainer targetContainer;
	
	private IStatus status = Status.OK_STATUS;
	
	
	/**
	 * @param modelName the proposed model name
	 * @param fileExtension the file extension
	 * @param targetContainer the target <code>IProject</code> or <code>IContainer</code>  
	 * @param flags the option flags
	 */
	public ModelNameChecker(String modelName, String fileExtension, IContainer targetContainer, int flags) {
		super();
		CoreArgCheck.isNotNull(modelName, "modelName"); //$NON-NLS-1$
		CoreArgCheck.isNotNull(fileExtension, "fileExtension"); //$NON-NLS-1$
		
		setFlags(flags);
		
		// Check if NO_EXISTING_MODEL_AT_LOCATION is a flag AND resource != null
		if( noExistingModelNameAtLocation ) {
			CoreArgCheck.isNotNull(targetContainer, "resource"); //$NON-NLS-1$
		}
		this.originalName = modelName;
		this.originalFileExtension = fileExtension;
		this.targetContainer = targetContainer;
		if(this.targetContainer != null ) {
			this.project = this.targetContainer.getProject();
		}
	}
	
	/**
	 * @return the validation <code>IStatus</code>
	 */
	public IStatus validate() {
		
		checkNameAndExtension();
		
		if( isOK() ) {
			if( noExistingModelNameAtLocation ) {
				// Check location for existing model with same name
				if( doesModelNameExistAtLocation(targetContainer)) {
					createErrorStatus(getString("sameNameModelExistsInProjectMessage", originalName)); //$NON-NLS-1$
				}
			}
			
			if( isOK() && noDuplicateModelNames && this.project != null ) {
				if( doesModelNameExistInContainer(this.project) ) {
					createErrorStatus(getString("sameNameModelExistsInProjectMessage", originalName)); //$NON-NLS-1$
				}
			}
		}
		
		return status;
	}
	
	private void setFlags(int flags) {
		if( (IGNORE_CASE & flags) == IGNORE_CASE) ignoreCase = true;
		if( (NO_EXISTING_MODEL_AT_LOCATION & flags) == NO_EXISTING_MODEL_AT_LOCATION) noExistingModelNameAtLocation = true;
		if( (NO_DUPLICATE_MODEL_NAMES & flags) == NO_DUPLICATE_MODEL_NAMES) noDuplicateModelNames = true;
		if( (NO_DUPLICATE_MODEL_NAMES_OTHER_THAN_LOCATION & flags) == NO_DUPLICATE_MODEL_NAMES_OTHER_THAN_LOCATION) noDuplicateModelNamesOtherThanLocation = true;

	}
	
    private void createErrorStatus(String message) {
    	status = new Status(IStatus.ERROR, PLUGIN_ID, message);
    }
    
    private boolean isOK() {
    	return status.getSeverity() < IStatus.ERROR;
    }
    
	
	private void checkNameAndExtension() {
    	
    	String finalProposedName = originalName;
    	if( ignoreCase ) {
    		finalProposedName = originalName.toUpperCase();
    	}
    	
    	String finalFileExtension = originalFileExtension;
    	if( ignoreCase ) {
    		finalFileExtension = originalFileExtension.toUpperCase();
    	}
    	
        if (finalProposedName == null || finalProposedName.equals(PluginConstants.EMPTY_STRING)) {
            createErrorStatus(getString("zeroLengthFileMessage")); //$NON-NLS-1$
            return;
        }
        
        boolean removedValidExtension = false;
        if (finalProposedName.endsWith(finalFileExtension)) {
        	finalProposedName = finalProposedName.substring(0, finalProposedName.lastIndexOf(finalFileExtension));
            removedValidExtension = true;
        }

        if (finalProposedName.indexOf('.') != -1) {
            if (!removedValidExtension) {
            	 createErrorStatus(getString("illegalExtensionMessage", finalFileExtension)); //$NON-NLS-1$
            	 return;
            }
        }
        
        originalNameWithoutExtension = finalProposedName;
	}
	
    private boolean doesResourceNameMatch(IResource resource) {
    	// status = createErrorStatus(getString("sameNameModelExistsInProjectMessage", name)); //$NON-NLS-1$
    	

		if( ignoreCase ) {    	
			String nameMinusExt = resource.getName().toUpperCase();
			
			if( nameMinusExt.endsWith(originalFileExtension.toUpperCase() )) {
				nameMinusExt = nameMinusExt.substring(0, nameMinusExt.lastIndexOf(originalFileExtension.toUpperCase()));
			}
			if( nameMinusExt.equals(originalNameWithoutExtension.toUpperCase()) ) {
				return true;
			}
		} else {
			String nameMinusExt = resource.getName();
			if( nameMinusExt.endsWith(originalFileExtension)) {
				nameMinusExt = nameMinusExt.substring(0, nameMinusExt.lastIndexOf(originalFileExtension));
			}
			if( nameMinusExt.equalsIgnoreCase(originalNameWithoutExtension) ) {
				return true;
			}
		}
		
		return false;
    }
    
    /**
     * Method to determine if model name exists in project or not
     * @param container a project or folder target location for the proposed model
     * @return iStatus the status of the model name 
     */
    public boolean doesModelNameExistAtLocation(IContainer container) {
    	try {
			for( IResource iRes : container.members() ) {
				boolean result = doesResourceNameMatch(iRes);
				if( result ) {
					return true;
				}
			}
		} catch (CoreException ex) {
			UiConstants.Util.log(IStatus.ERROR, ex, ex.getMessage());
		}
    	
    	return false;

    }
    
    /**
     * Method to determine if model name exists in project or not
     * @param name the proposed name of the model
     * @param fileExtension the file extension
     * @param container a project or folder target location for the proposed model
     * @return iStatus the status of the model name 
     */
    private boolean doesModelNameExistInContainer( IContainer container) {
    	boolean result = false;
    	
    	try {
			for( IResource iRes : container.members() ) {
				if( noDuplicateModelNamesOtherThanLocation && iRes == targetContainer) {
					continue;
				}
				
				if( iRes instanceof IContainer ) {
					result = doesModelNameExistInContainer((IContainer)iRes);
				} else {
					result = doesResourceNameMatch(iRes);
				}
				if (result ) {
					break;
				}
			}
		} catch (CoreException ex) {
			UiConstants.Util.log(IStatus.ERROR, ex, ex.getMessage());
		}

    	return result;
    }
    
}
