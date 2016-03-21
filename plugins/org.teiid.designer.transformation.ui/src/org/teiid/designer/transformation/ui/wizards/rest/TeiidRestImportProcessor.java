/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.transformation.ui.wizards.rest;

import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.widgets.Shell;
import org.teiid.core.designer.ModelerCoreException;
import org.teiid.core.designer.util.StringUtilities;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.core.workspace.ModelWorkspaceItem;
import org.teiid.designer.core.workspace.ModelWorkspaceManager;
import org.teiid.designer.datatools.connection.ConnectionInfoHelper;
import org.teiid.designer.datatools.connection.DataSourceConnectionHelper;
import org.teiid.designer.datatools.connection.IConnectionInfoProvider;
import org.teiid.designer.datatools.profiles.ws.IWSProfileConstants;
import org.teiid.designer.datatools.profiles.ws.WSConnectionInfoProvider;
import org.teiid.designer.datatools.profiles.xml.XmlFileConnectionInfoProvider;
import org.teiid.designer.datatools.profiles.xml.XmlUrlConnectionInfoProvider;
import org.teiid.designer.datatools.ui.DatatoolsUiConstants;
import org.teiid.designer.runtime.spi.ITeiidServer;
import org.teiid.designer.transformation.ui.wizards.file.FlatFileRelationalModelFactory;
import org.teiid.designer.transformation.ui.wizards.file.TeiidMetadataImportInfo;
import org.teiid.designer.transformation.ui.wizards.file.TeiidMetadataImportProcessor;
import org.teiid.designer.transformation.ui.wizards.xmlfile.TeiidXmlFileInfo;
import org.teiid.designer.transformation.ui.wizards.xmlfile.XmlFileViewModelFactory;
import org.teiid.designer.ui.editors.ModelEditor;
import org.teiid.designer.ui.editors.ModelEditorManager;

/**
 * @since 8.6
 */
public class TeiidRestImportProcessor extends TeiidMetadataImportProcessor {
	
	public TeiidRestImportProcessor(TeiidMetadataImportInfo info, Shell shell) {
		super(info, shell);
	}

    @Override
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
                			factory.createViewProcedure(editor.getModelResource(), info, relationalModelName);
                		
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
     * @see org.teiid.designer.transformation.ui.wizards.file.TeiidMetadataImportProcessor#createViewsInNewModel(java.lang.String)
     */
    @Override
	protected ModelResource createViewsInNewModel(String sourceModelName) throws ModelerCoreException {
    	XmlFileViewModelFactory factory = new XmlFileViewModelFactory();
    	
        // View Model Name
        String viewModelName = this.getInfo().getViewModelName();
    	
    	if (!viewModelName.toLowerCase().endsWith(DEFAULT_EXTENSION_LCASE)) {
            viewModelName = viewModelName + DEFAULT_EXTENSION_LCASE;
        }
    	
        // Create the View Model, at the specified location
        ModelResource modelResource = factory.createViewRelationalModel(this.getInfo().getViewModelLocation(), viewModelName);
    	
    	// Create View Procedure in the model
        for( TeiidXmlFileInfo info : this.getInfo().getXmlFileInfos()) {
        	if( info.doProcess() ) {
        		factory.createViewProcedure(modelResource, info, sourceModelName);
        	}
        }

        return modelResource;
    }

	@Override
	protected void addConnectionProfileInfoToModel(ModelResource sourceModel, TeiidMetadataImportInfo info) throws ModelWorkspaceException {
    	// Inject the connection profile info into the model
    	if (info.getConnectionProfile() != null) {
    		IConnectionInfoProvider provider = null;
    		if( getInfo().isXmlLocalFileMode() ) {
    			provider = new XmlFileConnectionInfoProvider();
    		} else if( getInfo().isRestUrlFileMode() ) {
    			if( IWSProfileConstants.TEIID_WS_CONNECTION_PROFILE_ID.equalsIgnoreCase(info.getConnectionProfile().getProviderId()) ) {
    				provider = new WSConnectionInfoProvider();
    			} else {
    				provider = new XmlUrlConnectionInfoProvider();
    			}
    		}
    		if( provider != null ) {
    			provider.setConnectionInfo(sourceModel, info.getConnectionProfile());
    		}
    		
    		String jndiName = info.getJBossJndiName();
    		if( !StringUtilities.isEmpty(jndiName) ) {
    			ConnectionInfoHelper helper = new ConnectionInfoHelper();
    			helper.setJNDIName(sourceModel, jndiName);
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
	
	@Override
    protected void handleCreateDataSource() {
    	if( getInfo().doCreateDataSource() && DataSourceConnectionHelper.isServerConnected() ) {
            ITeiidServer teiidServer = ModelerCore.getTeiidServerManager().getDefaultServer();
        	if( teiidServer.isConnected() ) {
        		String dsName = getInfo().getJBossJndiName();
        		String jndiName = getInfo().getJBossJndiName();
        		DataSourceConnectionHelper helper = new DataSourceConnectionHelper(getSourceModel(), getInfo().getConnectionProfile());
        		
	        	Properties connProps = helper.getModelConnectionProperties();
//	        	String translatorType = helper.getTranslatorType();
	        	
	        	String dsType = helper.getDataSourceType(); //FILE_DS_TYPE;
//	        	if( translatorType.equalsIgnoreCase("WS")) {
//	        		dsType = WS_DS_TYPE;
//	        	}
        		try {
					teiidServer.getOrCreateDataSource(dsName, jndiName, dsType, connProps);
				} catch (Exception e) {
					DatatoolsUiConstants.UTIL.log(e);
				}
        		try {
					teiidServer.getOrCreateDataSource(dsName, jndiName, dsType, connProps);
				} catch (Exception e) {
					DatatoolsUiConstants.UTIL.log(e);
				}
        	}
    	}
    }
}
