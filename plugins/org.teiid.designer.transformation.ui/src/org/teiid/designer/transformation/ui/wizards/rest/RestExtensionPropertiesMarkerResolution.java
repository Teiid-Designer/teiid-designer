/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.ui.wizards.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IMarkerResolution;
import org.teiid.core.designer.ModelerCoreException;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.extension.ModelExtensionUtils;
import org.teiid.designer.core.util.ModelObjectCollector;
import org.teiid.designer.core.workspace.ModelObjectAnnotationHelper;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.extension.ExtensionPlugin;
import org.teiid.designer.metamodels.relational.Procedure;
import org.teiid.designer.metamodels.relational.extension.RelationalModelExtensionAssistant;
import org.teiid.designer.metamodels.relational.extension.RelationalModelExtensionConstants;
import org.teiid.designer.metamodels.relational.extension.RestModelExtensionAssistant;
import org.teiid.designer.metamodels.relational.extension.RestModelExtensionConstants;
import org.teiid.designer.transformation.ui.Messages;
import org.teiid.designer.transformation.ui.UiConstants;
import org.teiid.designer.transformation.ui.UiPlugin;
import org.teiid.designer.ui.common.viewsupport.UiBusyIndicator;
import org.teiid.designer.ui.editors.ModelEditor;
import org.teiid.designer.ui.editors.ModelEditorManager;
import org.teiid.designer.ui.viewsupport.ModelUtilities;

public class RestExtensionPropertiesMarkerResolution implements	IMarkerResolution, RestModelExtensionConstants {
	
	private Map<EObject, Properties> existingRestPropertyMap;
	
	@Override
	public String getLabel() {
		return Messages.restMedQuickFixLabel;
	}

	@Override
	public void run(IMarker marker) {
		/*
			1) Gather up a cache of all "exten. prop. overrides", their values, ext. prop IDs and their target objects
			2) Throw away the old MEDs, inject/apply new MEDs
			3) Reset new exten. overrides using the map of old ID's to new IDs
		*/
		
		this.existingRestPropertyMap = new HashMap<EObject, Properties>();
		
		IResource resource = marker.getResource();

        // Fix the Marked Model Resource
        if(ModelUtilities.isModelFile(resource)) {
            final IFile theFile = (IFile)resource;

            
            final ModelEditor editor = ModelEditorManager.getModelEditorForFile(theFile, false);
            // If editor is open and dirty, ask user whether to save
            if ((editor != null) && editor.isDirty()) {
                boolean saveEditor = MessageDialog.openQuestion(getShell(), Messages.quickFixModelDirtyTitle, Messages.quickFixModelDirtyMsg);
                if(!saveEditor) {
                    return;
                } else {
                    // Add the selected Med
                    UiBusyIndicator.showWhile(Display.getDefault(), new Runnable() {
                        @Override
                        public void run() {
                            editor.doSave(new NullProgressMonitor());
                            fixInTxn(theFile);
                        }
                    });   
                    return;
                }
            }
            // Add the selected Med
            UiBusyIndicator.showWhile(Display.getDefault(), new Runnable() {
                @Override
                public void run() {
                	fixInTxn(theFile);
                }
            });

            // Fix the Marked mxd File
        } 
        
        this.existingRestPropertyMap.clear();

	}
	
	private void fixInTxn(IResource modelFile) {
		boolean requiredStart = ModelerCore.startTxn(true, false, "Fix out-dated rest model extensions", modelFile);
		boolean succeeded = false;

		try {
			replaceRestMedAndSetProperties(modelFile);
			succeeded = true;
		} finally {
			// if we started the transaction, commit it.
			if (requiredStart) {
				if (succeeded) {
					ModelerCore.commitTxn();
				} else {
					ModelerCore.rollbackTxn();
				}
			}
		}
	}
	
    /*
     * Fix Legacy Names in the supplied Model File by replacing the legacy 'com.metamatrix' names with 'org.teiid.designer'
     * @param modelResource the supplied model resource
     */
    private void replaceRestMedAndSetProperties( IResource modelFile ) {
        final ModelResource mr = ModelUtilities.getModelResource(modelFile);
    	
        /* ------------------------------
         * STEP 1:  Find ALL extended REST properties in the model file and cache off properties for object
         -------------------------------- */
    	
    	// Get the list of EObjects in a model and look for Procedure with properties
        try {
			final ModelObjectCollector moc = new ModelObjectCollector(mr.getEmfResource());
			@SuppressWarnings("unchecked")
			List<EObject> eObjects = moc.getEObjects();
			ModelObjectAnnotationHelper helper = new ModelObjectAnnotationHelper();
			for( EObject eObj : eObjects) {
				if( eObj instanceof Procedure ) {
					// find extension properties for eObj
					Properties props = helper.getProperties(eObj, OLD_REST_INFO.OLD_REST_PREFIX);
					// If they exist, get the property "key" values and if they are "rest:URI" or "rest:method" then add these to the existingRestPropertyMap
					if( ! props.isEmpty() ) {
						this.existingRestPropertyMap.put(eObj, props);
					}
				}
			}
		} catch (ModelWorkspaceException e) {
			UiConstants.Util.log(e);
		} catch (ModelerCoreException e) {
			UiConstants.Util.log(e);
		}
    	
    	/* ------------------------------
    	 * STEP 2: remove the old "rest" MED from the model
    	 -------------------------------- */

        // 
        try {
	        ModelExtensionUtils.removeModelExtensionDefinition(mr, OLD_REST_INFO.OLD_REST_NAMESPACE_PREFIX);
		} catch (Exception e) {
			UiConstants.Util.log(e);
		}
        
        /* ------------------------------
         * STEP 3: remove old extension properties
         -------------------------------- */
        
		
        ModelObjectAnnotationHelper helper = new ModelObjectAnnotationHelper();
        try {
			for( EObject eObj : this.existingRestPropertyMap.keySet() ) {
				if( eObj instanceof Procedure ) {
					helper.removeProperties(eObj, OLD_REST_INFO.OLD_REST_PREFIX);
				}
			}
		} catch (ModelerCoreException e) {
			UiConstants.Util.log(e);
		}
        
        
        /* ------------------------------
         * STEP 4: add the new REST MED and RELATIONAL MED to the model
         -------------------------------- */
        
        final String prefix = RelationalModelExtensionConstants.NAMESPACE_PROVIDER.getNamespacePrefix();
        final RelationalModelExtensionAssistant relatinalAssistant = (RelationalModelExtensionAssistant)ExtensionPlugin.getInstance().getRegistry().getModelExtensionAssistant(prefix);
        final RestModelExtensionAssistant restAssistant = RestModelExtensionAssistant.getRestAssistant();
        try {
        	relatinalAssistant.applyMedIfNecessary(modelFile);
			restAssistant.applyMedIfNecessary(modelFile);
		} catch (Exception e) {
			UiConstants.Util.log(e);
		}
        
        /* ------------------------------
         *  STEP 5: Re-add the cached rest properties to the model using the new REST: prefix
         -------------------------------- */
        
        for( EObject eObj : this.existingRestPropertyMap.keySet() ) {
        	if( eObj instanceof Procedure ) {
        		Properties props = this.existingRestPropertyMap.get(eObj);
        		for( Object key : props.keySet() ) {
        			String keyStr = (String)key;
        			String value = props.getProperty(keyStr);
        			if( keyStr.equals(OLD_REST_INFO.OLD_REST_METHOD_KEY)) {
        				RestModelExtensionAssistant.setRestProperty(eObj, PropertyIds.REST_METHOD, value.toUpperCase());
        			} else if( keyStr.equals(OLD_REST_INFO.OLD_REST_URI_KEY)) {
        				RestModelExtensionAssistant.setRestProperty(eObj, PropertyIds.URI, value);
        			}
        		}
        	}
        }
        
        /* ------------------------------
         *  STEP 5:  SAVE ALL CHANGES
         -------------------------------- */
        try {
            ModelResource mdlResc = ModelUtilities.getModelResourceForIFile((IFile)modelFile, false);
            if(mdlResc!=null) {
                mdlResc.save(new NullProgressMonitor(), true);
            }
            modelFile.deleteMarkers(MED_PROBLEM_MARKER_ID, true, IResource.DEPTH_INFINITE);
            modelFile.getParent().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
        } catch (CoreException e) {
        	UiConstants.Util.log(IStatus.ERROR, e, NLS.bind(Messages.saveModelErrorMsg, modelFile.getName()));
        }

    }

    private static Shell getShell() {
        return UiPlugin.getDefault().getCurrentWorkbenchWindow().getShell();
    }
}
