/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package com.metamatrix.modeler.transformation.ui.wizards.xmlfile;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.swt.widgets.Shell;
import org.teiid.designer.datatools.connection.IConnectionInfoProvider;
import org.teiid.designer.datatools.profiles.ws.IWSProfileConstants;
import org.teiid.designer.datatools.profiles.ws.WSConnectionInfoProvider;
import org.teiid.designer.datatools.profiles.xml.XmlFileConnectionInfoProvider;
import org.teiid.designer.datatools.profiles.xml.XmlUrlConnectionInfoProvider;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceItem;
import com.metamatrix.modeler.internal.core.workspace.ModelWorkspaceManager;
import com.metamatrix.modeler.internal.ui.editors.ModelEditor;
import com.metamatrix.modeler.transformation.ui.UiConstants;
import com.metamatrix.modeler.transformation.ui.wizards.file.FlatFileRelationalModelFactory;
import com.metamatrix.modeler.transformation.ui.wizards.file.TeiidMetadataImportInfo;
import com.metamatrix.modeler.transformation.ui.wizards.file.TeiidMetadataImportProcessor;
import com.metamatrix.modeler.ui.editors.ModelEditorManager;

public class TeiidXmlFileImportProcessor extends TeiidMetadataImportProcessor implements UiConstants {
	
	public TeiidXmlFileImportProcessor(TeiidMetadataImportInfo info, Shell shell) {
		super(info, shell);
	}

    protected ModelResource createViewsInExistingModel(String relationalModelName) throws ModelerCoreException  {
    	if( getInfo().getViewModelLocation() != null && getInfo().getViewModelName() != null ) {
    		IPath modelPath = getInfo().getViewModelLocation().append(getInfo().getViewModelName());
    		if( !modelPath.toString().toUpperCase().endsWith(".XMI")) { //$NON-NLS-1$
    			modelPath = modelPath.addFileExtension("xmi"); //$NON-NLS-1$
    		}
    		
    		ModelWorkspaceItem item = ModelWorkspaceManager.getModelWorkspaceManager().findModelWorkspaceItem(modelPath, IResource.FILE);
            ModelEditor editor = ModelEditorManager.getModelEditorForFile( (IFile)item.getCorrespondingResource(), true);
            if (editor != null) {
                boolean isDirty = editor.isDirty();
                XmlFileViewModelFactory factory = new XmlFileViewModelFactory();
                
                for( TeiidXmlFileInfo info : this.getInfo().getXmlFileInfos()) {
                	if( info.doProcess() ) {
                		factory.createViewTable(editor.getModelResource(), info, relationalModelName);
                	}
                }
                
                editor.getModelResource().save(null, true);
                
                if (!isDirty && editor.isDirty()) {
                    editor.doSave(new NullProgressMonitor());
                }
                
                return editor.getModelResource();
            }
    	}
    	
    	return null;
    }
    
    /**
     * Create Views. The source Model Name is passed in for use in the transformation SQL
     * 
     * @param sourceModelName the name of the source model {@inheritDoc}
     * @see com.metamatrix.modeler.transformation.ui.wizards.file.TeiidMetadataImportProcessor#createViewsInNewModel(java.lang.String)
     */
    protected ModelResource createViewsInNewModel(String sourceModelName) throws ModelerCoreException {
    	XmlFileViewModelFactory factory = new XmlFileViewModelFactory();
    	
        // View Model Name
        String viewModelName = this.getInfo().getViewModelName();
    	
    	if (!viewModelName.toLowerCase().endsWith(DEFAULT_EXTENSION_LCASE)) {
            viewModelName = viewModelName + DEFAULT_EXTENSION_LCASE;
        }
    	
        // Create the View Model, at the specified location
        ModelResource modelResource = factory.createViewRelationalModel(this.getInfo().getViewModelLocation(), viewModelName);
    	
    	// Create View Tables in the model
        for( TeiidXmlFileInfo info : this.getInfo().getXmlFileInfos()) {
        	if( info.doProcess() ) {
        		factory.createViewTable(modelResource, info, sourceModelName);
        	}
        }

        return modelResource;
    }

	@Override
	protected void addConnectionProfileInfoToModel(ModelResource sourceModel, IConnectionProfile profile) throws ModelWorkspaceException {
    	// Inject the connection profile info into the model
    	if (profile != null) {
    		IConnectionInfoProvider provider = null;
    		if( getInfo().isXmlLocalFileMode() ) {
            provider = new XmlFileConnectionInfoProvider();
    		} else if( getInfo().isXmlUrlFileMode() ) {
    			if( IWSProfileConstants.TEIID_WS_CONNECTION_PROFILE_ID.equalsIgnoreCase(profile.getProviderId()) ) {
    				provider = new WSConnectionInfoProvider();
    			} else {
    				provider = new XmlUrlConnectionInfoProvider();
    			}
    		}
    		if( provider != null ) {
    			provider.setConnectionInfo(sourceModel, profile);
    		}
        }
	}
    
	/**
	 *  Override method to create the 'invokeHttp' procedure
	 */
	@Override
    protected ModelResource addProcedureToRelationalSourceModel() throws ModelerCoreException {
		if( getInfo().isXmlLocalFileMode() ) {
			return super.addProcedureToRelationalSourceModel();
		}
		
		
    	if( getInfo().getSourceModelLocation() != null && getInfo().getSourceModelName() != null ) {
    		IPath modelPath = getInfo().getSourceModelLocation().append(getInfo().getSourceModelName());
    		if( !modelPath.toString().toUpperCase().endsWith(".XMI")) { //$NON-NLS-1$
    			modelPath = modelPath.addFileExtension("xmi"); //$NON-NLS-1$
    		}
    		
    		ModelWorkspaceItem item = ModelWorkspaceManager.getModelWorkspaceManager().findModelWorkspaceItem(modelPath, IResource.FILE);
            ModelEditor editor = ModelEditorManager.getModelEditorForFile( (IFile)item.getCorrespondingResource(), true);
            if (editor != null) {
            	ModelResource mr = editor.getModelResource();
                boolean isDirty = editor.isDirty();
                FlatFileRelationalModelFactory factory = new FlatFileRelationalModelFactory();
                
                factory.addMissingProcedure(mr, FlatFileRelationalModelFactory.INVOKE_HTTP);
                
                mr.save(null, true);
                
                if (!isDirty && editor.isDirty()) {
                    editor.doSave(new NullProgressMonitor());
                }
                
                return mr;
            }
    	}
    	
    	return null;
    }
}
