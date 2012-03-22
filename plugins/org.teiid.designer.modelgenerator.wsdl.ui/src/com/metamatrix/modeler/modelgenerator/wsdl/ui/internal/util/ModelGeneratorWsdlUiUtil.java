/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.wsdl.ui.internal.util;

import java.io.File;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.teiid.core.util.FileUtils;

import com.metamatrix.metamodels.relational.aspects.validation.RelationalStringNameValidator;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceItem;
import com.metamatrix.modeler.internal.core.workspace.ModelWorkspaceManager;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.modelgenerator.wsdl.ui.ModelGeneratorWsdlUiConstants;
import com.metamatrix.modeler.modelgenerator.wsdl.ui.ModelGeneratorWsdlUiPlugin;


/** 
 * @since 4.2
 */
public class ModelGeneratorWsdlUiUtil implements FileUtils.Constants {
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTANTS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * Collection of WSDL file extensions. Each element in the collection can be used used in dialog file chooser's to filter
     * out resources.
     */
    public static String[] WSDL_FILE_EXTENSIONS = new String[] {"wsdl"}; //$NON-NLS-1$

    /**
     * Collection of Model file extensions.
     */
    public static String[] MODEL_FILE_EXTENSIONS = new String[] {"xmi"}; //$NON-NLS-1$
    
    /**
     * WSDL extensions prefixed with the file wildcard and extension separator. Suitable for use
     * in the {@link org.eclipse.swt.widgets.FileDialog}.
     */
    public static final String[] FILE_DIALOG_WSDL_EXTENSIONS;
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // INITIALIZER
    ///////////////////////////////////////////////////////////////////////////////////////////////

    static {
        // create WSDL file dialog extension array
        FILE_DIALOG_WSDL_EXTENSIONS = new String[WSDL_FILE_EXTENSIONS.length];
        
        int i = 0;
        for (; i < WSDL_FILE_EXTENSIONS.length; i++) {
            FILE_DIALOG_WSDL_EXTENSIONS[i] = createFileDialogExtension(WSDL_FILE_EXTENSIONS[i]);
        }
        
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Don't allow construction.
     * @since 4.2
     */
    private ModelGeneratorWsdlUiUtil() {}
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * Convenience method to retrieve a Web Service UI image. 
     * @param theImageName the name of the image being requested
     * @return the image or <code>null</code> if not found
     * @since 4.2
     */
    public static Image getImage(String theImageName) {
        return ModelGeneratorWsdlUiPlugin.getDefault().getImage(theImageName);
    }
    
    /**
     * Convenience method to retrieve a Web Service UI image descriptors. 
     * @param theImageName the name of the image descriptor being requested
     * @return the image descriptor or <code>null</code> if not found
     * @since 4.2
     */
    public static ImageDescriptor getImageDescriptor(String theImageName) {
        return ModelGeneratorWsdlUiPlugin.getDefault().getImageDescriptor(theImageName);
    }
    
    /**
     * Indicates if the specified extensions is a valid WSDL file extension. 
     * @param theExtension the extension being checked
     * @return <code>true</code>if valid; <code>false</code> otherwise.
     * @since 4.2
     */
    private static boolean isFileExtensionOfType(String theExtension, String[] extensionTypes) {
        boolean result = false;
        
        if ((theExtension != null) && (theExtension.length() > 0)) {
            for (int i = 0; i < extensionTypes.length; i++) {
                if (theExtension.equalsIgnoreCase(extensionTypes[i])) {
                    result = true;
                    break;
                }
            }
        }
        
        return result;
    }
    
    /**
     * Indicates if the specified workspace file is a WSDL. 
     * @param theFile the file being checked
     * @return <code>true</code>if a WSDL file; <code>false</code> otherwise.
     * @since 4.2
     */
    public static boolean isWsdlFile(final IFile theFile) {
        return isFileExtensionOfType(theFile.getFileExtension(), WSDL_FILE_EXTENSIONS);
    }
    
    /**
     * Indicates if the specified file system file is a WSDL. 
     * @param theFile the file being checked
     * @return <code>true</code>if a WSDL file; <code>false</code> otherwise.
     * @since 4.2
     */
    public static boolean isWsdlFile(final File theFile) {
        boolean result = false;
        String name = theFile.getName();
        int index = name.lastIndexOf(FileUtils.Constants.FILE_EXTENSION_SEPARATOR);
        
        if ((index != -1) && ((index + 2) < name.length())) {
            result = isFileExtensionOfType(name.substring(index + 1), WSDL_FILE_EXTENSIONS);
        }
        
        return result;
    }
    
    /**
     * Indicates if the specified file system file is a Model. 
     * @param theFile the file being checked
     * @return <code>true</code>if a XMI file; <code>false</code> otherwise.
     * @since 4.2
     */
	public static boolean isModelFile(IFile theFile) {
        boolean result = false;
        String name = theFile.getName();
        int index = name.lastIndexOf(FileUtils.Constants.FILE_EXTENSION_SEPARATOR);
        
        if ((index != -1) && ((index + 2) < name.length())) {
            result = isFileExtensionOfType(name.substring(index + 1), MODEL_FILE_EXTENSIONS);
        }
        
        return result;
	}

	/**
     * Creates an extension which can be used in a {@link org.eclipse.swt.widgets.FileDialog}. Prefixes the
     * specified extension with the file name wildcard and the extension separator character.
     * @param theExtension the extension being used
     * @since 4.2
     */
    public static String createFileDialogExtension(String theExtension) {
        return new StringBuffer().append(FILE_NAME_WILDCARD)
                                 .append(FILE_EXTENSION_SEPARATOR_CHAR)
                                 .append(theExtension)
                                 .toString();
    }
    
	public static boolean modelExists(String containerPath, String modelName) {
		if (containerPath == null) {
			return false;
		}

		IPath modelPath = new Path(containerPath).append(modelName);
		if (!modelPath.toString().toUpperCase().endsWith(".XMI")) { //$NON-NLS-1$
			modelPath = modelPath.addFileExtension("xmi"); //$NON-NLS-1$
		}

		ModelWorkspaceItem item = ModelWorkspaceManager.getModelWorkspaceManager().findModelWorkspaceItem(modelPath, IResource.FILE);
		if (item != null) {
			return true;
		}

		return false;
	}
    
	public static IFile getModelFile(String containerPath, String modelName) throws ModelWorkspaceException {
		if (containerPath == null || modelName == null) {
			return null;
		}

		IPath modelPath = new Path(containerPath).append(modelName);
		if (!modelPath.toString().toUpperCase().endsWith(".XMI")) { //$NON-NLS-1$
			modelPath = modelPath.addFileExtension("xmi"); //$NON-NLS-1$
		}

		ModelWorkspaceItem item = ModelWorkspaceManager.getModelWorkspaceManager().findModelWorkspaceItem(modelPath, IResource.FILE);
		if (item != null) {
			return (IFile) item.getCorrespondingResource();
		}

		return null;
	}
	
	public static boolean eObjectExists(String containerPath, String modelName, String childName) {
		try {
    		IFile modelFile = getModelFile(containerPath, modelName);
    		if( modelFile != null ) {
    			ModelResource mr = ModelUtilities.getModelResourceForIFile(modelFile, false);
    			if( mr != null ) {
    				for( Object eObj : mr.getEObjects() ) {
    					String name = ModelerCore.getModelEditor().getName((EObject)eObj);
    					if( name.equals(childName) ) {
    						return true;
    					}
    				}
    			}
    		}
		} catch (ModelWorkspaceException ex ) {
			ModelGeneratorWsdlUiConstants.UTIL.log(ex);
		}
		return false;
	}
	
	public static EObject getExistingEObject(String containerPath, String modelName, String childName) {
		try {
    		IFile modelFile = getModelFile(containerPath, modelName);
    		if( modelFile != null ) {
    			ModelResource mr = ModelUtilities.getModelResourceForIFile(modelFile, false);
    			if( mr != null ) {
    				for( Object eObj : mr.getEObjects() ) {
    					String name = ModelerCore.getModelEditor().getName((EObject)eObj);
    					if( name.equals(childName) ) {
    						return (EObject)eObj;
    					}
    				}
    			}
    		}
		} catch (ModelWorkspaceException ex ) {
			ModelGeneratorWsdlUiConstants.UTIL.log(ex);
		}
		return null;
	}
	
	public static String getUniqueName(String containerPath, String modelName, String targetName, boolean isTable, boolean overwrite) {
		String uniqueName = targetName;
		
		try {
    		IFile modelFile = getModelFile(containerPath, modelName);
    		if( modelFile != null ) {
    			ModelResource mr = ModelUtilities.getModelResourceForIFile(modelFile, false);
    			if( mr != null ) {
    				uniqueName = getUniqueName(mr, targetName, isTable, overwrite);
    			}
    		}
		} catch (ModelWorkspaceException ex ) {
			ModelGeneratorWsdlUiConstants.UTIL.log(ex);
		}
    	
    	return uniqueName;
    }
	
	public static String getUniqueName(ModelResource mr, String targetName, boolean isTable, boolean overwrite) {
		String uniqueName = targetName;
		
		if( mr != null && !overwrite ) {
    		RelationalStringNameValidator nameValidator = new RelationalStringNameValidator(isTable, true);
    		try {
    			// Load the name validator with EObject names
    			for( Object eObj : mr.getEObjects() ) {
    				String name = ModelerCore.getModelEditor().getName((EObject)eObj);
    				nameValidator.addExistingName(name);
    			}
    
    			uniqueName = nameValidator.createValidUniqueName(targetName);
    		} catch (ModelWorkspaceException ex) {
    			ModelGeneratorWsdlUiConstants.UTIL.log(ex);
    		}
		}
    	
    	return uniqueName;
    }
    
}
