/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.tools.textimport.ui.wizards;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.teiid.core.id.InvalidIDException;
import org.teiid.core.id.ObjectID;
import org.teiid.core.id.UUID;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.ui.viewsupport.NewModelProjectWorker;
import com.metamatrix.modeler.tools.textimport.ui.UiConstants;



/** 
 * @since 4.2
 */
public abstract class AbstractObjectProcessor implements UiConstants {
    private static final String XMI_EXTENSION           = "xmi";//$NON-NLS-1$
    
    private static final String I18N_PREFIX             	= "AbstractObjectProcessor"; //$NON-NLS-1$
    private static final String SEPARATOR               	= "."; //$NON-NLS-1$
    private static final String ADD_VALUE_ERROR         	= I18N_PREFIX + SEPARATOR + "addValueError"; //$NON-NLS-1$
    private static final String GET_MODEL_CONTENTS_ERROR 	= I18N_PREFIX + SEPARATOR + "getModelContentsError"; //$NON-NLS-1$
    private static final String MODEL_OBJ_NOTFOUND_ERROR 	= I18N_PREFIX + SEPARATOR + "modelObjectNotFound"; //$NON-NLS-1$
    private final static String MANY_ROWS_TITLE 			= getString("manyRowsTitle");//$NON-NLS-1$
    private final static String MANY_ROWS_MESSAGE_KEY 		= "manyRowsMessage";//$NON-NLS-1$
    private static final String COULD_NOT_PARSE_KEY 		= "couldNotParse.message"; //$NON-NLS-1$
    public static final String FINISHED 					= getString("finished"); //$NON-NLS-1$

    //============================================================================================================================
    // Static Methods
    
    private static String getString(final String id) {
        return Util.getString(I18N_PREFIX + SEPARATOR + id);
    }
    
    private static String getString(final String id, Object obj) {
        return Util.getString(I18N_PREFIX + SEPARATOR + id, obj);
    }
    
    private static String getString(String key, Object value, Object value2) {
        return Util.getString(key, value, value2);
    }
    
    private static boolean isTransactionable = ModelerCore.getPlugin() != null;
    
    //  ============================================================================================================================
    // CONSTRUCTORS

    /** 
     * 
     * @since 4.2
     */
    public AbstractObjectProcessor() {
    }
    
    public boolean confirmLargeImport(Shell shell, int nRows, int threshold) {
    	if( nRows > threshold ) {
    		String iSize = Integer.toString(nRows);
    		return MessageDialog.openQuestion(shell, MANY_ROWS_TITLE, getString(MANY_ROWS_MESSAGE_KEY, iSize));
    	}
    	return true;
    }
    
    public void logParsingError(String rowString ) {
    	Util.log(IStatus.WARNING, getString(COULD_NOT_PARSE_KEY, rowString) );
    }
        
    public Collection loadLinesFromFile(String fileStr) {
        Collection rows = new ArrayList();
        if(fileStr!=null && fileStr.length() > 0){
            FileReader fr=null;
            BufferedReader in=null;
            rows = new ArrayList();
            
            try{
                fr=new FileReader(fileStr);
                in = new BufferedReader(fr);
                String str;
                while ((str = in.readLine()) != null) {
                    if( str.length() > 1 ) {
                        rows.add(str);
                    }
                }        
            }catch(Exception e){
                Util.log(IStatus.ERROR, e, getString("problemLoadingFileContentsMessage", fileStr)); //$NON-NLS-1$
            }
            finally{
                try{
                    fr.close();
                }catch(java.io.IOException e){}
                try{
                    in.close();
                }catch(java.io.IOException e){}

            }
        }
        return rows;
    }
    
    public void addValue(final Object owner, final Object value, EList feature) {
        try {
            if( isTransactionable ) {
                ModelerCore.getModelEditor().addValue(owner, value, feature);
            } else {
                feature.add(value);
            }
        } catch (ModelerCoreException err) {
            Util.log(IStatus.ERROR, err, getString(ADD_VALUE_ERROR, value, owner));
        }
    }
    
    public EList getModelResourceContents(ModelResource resource ) {
    	EList eList = null;
    	
    	try {
			eList = resource.getEmfResource().getContents();
		} catch (ModelWorkspaceException e) {
			 Util.log(IStatus.ERROR, e, getString(GET_MODEL_CONTENTS_ERROR, resource));
		}
		
		return eList;
    }
    
    protected EObject getEObject(String modelObjectIdentifier) {
        EObject result = null;
        
        if(modelObjectIdentifier!=null) {
            // If identifier is a uuid, lookup by uuid
            if(isStringifiedUUID(modelObjectIdentifier)) {
                result = lookupEObject(modelObjectIdentifier);
            // otherwise, the identifier is a path
            } else if( !isModelResourcePath(modelObjectIdentifier)) {
                IPath fullObjectPath = new Path(modelObjectIdentifier);
                ModelResource mr = findModelResource(fullObjectPath);
                int modelSegIndex = getExistingModelPathIndex(modelObjectIdentifier);
                
                if( mr != null ) {
                	IPath relativePath = null;
                    try {
                    	relativePath = fullObjectPath.removeFirstSegments(modelSegIndex);
                        result = ModelerCore.getModelEditor().findObjectByPath(mr, relativePath);
                    } catch (ModelWorkspaceException err) {
                        Util.log(IStatus.WARNING, getString(MODEL_OBJ_NOTFOUND_ERROR, modelObjectIdentifier)); 
                    }
                }
            }
        }
        
        return result;
    }
    
    /**
     * Find the model (if one exists) along the provided path string.  Return the index of the path segment
     * that is the model.  If no model was found, return value is -1.
     * @param path the path string which may have a model in it.
     * @return the path segment index of the model, -1 if none found.
     */
    protected int getExistingModelPathIndex(String pathStr) {
        int modelIndex = -1;
        // Walk the segments one at a time, starting at last one.
        ModelResource mr = null;
        IPath path = new Path(pathStr);
        int nSegs = path.segmentCount();
        if( nSegs > 1 ) {
            // First find and open the project (first segment) and open it.
            String projSeg = path.segment(0);
            
            // Check if Project exists - if it doesnt, return -1
            IProject existProj = ResourcesPlugin.getWorkspace().getRoot().getProject(projSeg);
            if( !existProj.exists() ) {
            	return -1;
            }
            
            // If project exists, continue processing
            if( existProj.exists() ) {
                if( !existProj.isOpen() ) {
                    try {
                        existProj.open(new NullProgressMonitor());
                    } catch (CoreException e) {
                        UiConstants.Util.log(e);
                    }
                }
                for(int i=nSegs; i>1; i--) {
                    IPath workingPath = path.uptoSegment(i);
                    String osPathStr = workingPath.toOSString();
                    if(osPathStr!=null && osPathStr.length()>0) {
                        mr = getModelResource(osPathStr);
                    }
                    if(mr!=null) {
                        modelIndex = i;
                        break;
                    }
                }
            }
        }
        return modelIndex;
    }
    
    /**
     * Lookup the EObject by its UUID
     */
    private EObject lookupEObject(final String uuidString) {
        EObject result = null;
        if(uuidString!=null) {
            try {
                result = (EObject)ModelerCore.getModelContainer().getEObjectFinder().find(uuidString);
            } catch (CoreException err) {
                // just return null result
            }
        }
        return result;
    }
    
    protected static boolean isStringifiedUUID( final String str ) {
        boolean result = false;
        String string = str;

        try {
            // strip the protocol before trying to determine if this a valid UUID
            int index = string.indexOf(UUID.PROTOCOL + ObjectID.DELIMITER);
            if(index == -1) {
                index = string.indexOf(UUID.PROTOCOL.toUpperCase() + ObjectID.DELIMITER);
                if (index != -1) {
                    string = string.toLowerCase();
                }
            }
            if(index != -1) {
                index = index + (UUID.PROTOCOL + ObjectID.DELIMITER).length();
                string = string.substring(index);
                UUID.stringToObject(string);
                result = true;                
            } else {
                result = false;
            }   
        } catch ( InvalidIDException e ) {
            result = false;
        }
        return result;
    }
    
    protected boolean isModelResourcePath(String location) {
    	IPath objectPath = new Path(location);
    	
        IFile modelFile = ModelerCore.getWorkspace().getRoot().getFile(objectPath);
        IPath nextPath = null;
        
        if( modelFile != null && isModelProjectResource(modelFile) && !(modelFile instanceof IFolder) ) {
            nextPath = objectPath.addFileExtension(XMI_EXTENSION);
            modelFile = ModelerCore.getWorkspace().getRoot().getFile(nextPath);
            ModelResource mr = ModelerCore.getModelWorkspace().findModelResource(modelFile);
            if( mr != null )
            	return true;
        }
        
        return false;
    }
    
    protected ModelResource getModelResource(String location) {
    	IPath objectPath = new Path(location);
    	
        IFile modelFile = ModelerCore.getWorkspace().getRoot().getFile(objectPath);
        IPath nextPath = null;
        
        if( modelFile != null && isModelProjectResource(modelFile) && !(modelFile instanceof IFolder) ) {
            nextPath = objectPath.addFileExtension(XMI_EXTENSION);
            modelFile = ModelerCore.getWorkspace().getRoot().getFile(nextPath);
            ModelResource mr = ModelerCore.getModelWorkspace().findModelResource(modelFile);
            if( mr != null )
            	return mr;
        }
        
        return null;
    }
    
    protected ModelResource findModelResource(IPath objectPath) {
        // Walk the path segements until we find an IResource that isModelFile()
        
//        ModelResource mr = ModelerCore.getModelWorkspace().findModelResource(objectPath);
        ModelResource mr = null;
        
        int nSeg = objectPath.segmentCount();
        IPath nextPath = null;
        for( int i=0; i<nSeg-1 && mr == null; i++) {
            try {
                nextPath = objectPath.removeLastSegments(i+1);
                
                IFile modelFile = ModelerCore.getWorkspace().getRoot().getFile(nextPath);
                
                if( modelFile != null && isModelProjectResource(modelFile) && !(modelFile instanceof IFolder) ) {
                    // append the xmi
                    nextPath = nextPath.addFileExtension(XMI_EXTENSION);
                    modelFile = ModelerCore.getWorkspace().getRoot().getFile(nextPath);
                    mr = ModelerCore.getModelWorkspace().findModelResource(modelFile);
                }
            } catch (IllegalArgumentException ex ) {
                // If the path doesn't exist in workspace, the following error is thrown in getFile()..
                // Path must include project and resource name: /BAD_PROJECT
                // We don't want to bail because one row can't be converted to relationship
                // Just log it.
                String msg = getString("modelNotFound", objectPath); //$NON-NLS-1$
                UiConstants.Util.log(IStatus.WARNING, msg);
            }
        }
        
        return mr;
    }
    
    /**
     * Helper method that determines that the given resource is a IFile
     * and exists in a modeling project.
     */
    private static boolean isModelProjectResource( final IResource resource ) {
        if ( resource != null ) {
            IProject proj = resource.getProject();
            if(proj != null && ModelerCore.hasModelNature(proj)) {
                return true;    
            }
        }
        return false;
    }
    
    protected IProject createProject(IProject nonExistingProject, IProgressMonitor monitor) {
        NewModelProjectWorker worker = new NewModelProjectWorker();
        if( !nonExistingProject.exists() ) {
            return worker.createNewProject(null, nonExistingProject.getName(), monitor);
        }
        
        UiConstants.Util.log(getString("projectExists", nonExistingProject.getName()));//$NON-NLS-1$ 
        
        return null;
    }
}
