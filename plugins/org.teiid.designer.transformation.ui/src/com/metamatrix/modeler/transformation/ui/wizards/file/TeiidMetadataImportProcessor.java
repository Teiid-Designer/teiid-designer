/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.ui.wizards.file;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.teiid.designer.datatools.connection.IConnectionInfoProvider;
import org.teiid.designer.datatools.profiles.flatfile.FlatFileConnectionInfoProvider;

import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceItem;
import com.metamatrix.modeler.internal.core.workspace.ModelWorkspaceManager;
import com.metamatrix.modeler.internal.ui.editors.ModelEditor;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.transformation.ui.UiConstants;
import com.metamatrix.modeler.ui.editors.ModelEditorManager;
import com.metamatrix.ui.internal.util.WidgetUtil;

public class TeiidMetadataImportProcessor implements UiConstants {
	private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(TeiidMetadataImportProcessor.class);
	protected static final String DEFAULT_EXTENSION_LCASE = ".xmi"; //$NON-NLS-1$
	
    private static String getString( final String id ) {
        return Util.getString(I18N_PREFIX + id);
    }
    
    private static String getString( final String id, final Object param ) {
        return Util.getString(I18N_PREFIX + id, param);
    }
	
	TeiidMetadataImportInfo info;
	ModelResource sourceModel;
	ModelResource viewModel;
	IStatus createStatus;
	Shell shell;
	
	public TeiidMetadataImportProcessor(TeiidMetadataImportInfo info, Shell shell) {
		super();
		this.info = info;
		this.shell = shell;
		createStatus = Status.OK_STATUS;
	}
	
	public IStatus execute() {
		final WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
            @Override
            public void execute( final IProgressMonitor theMonitor ) {
            	theMonitor.beginTask(getString("task.creatingSourceModel", info.getSourceModelName()), 100); //$NON-NLS-1$
                
            	if( info.sourceModelExists() ) {
            		createStatus = createProceduresInExistingSourceModelInTxn(theMonitor);
            	} else {
            		createStatus = createSourceModelInTxn(theMonitor);
            		if( createStatus.isOK() ) {
            			createStatus = createProceduresInExistingSourceModelInTxn(theMonitor);
            		}
            	}

                if( info.viewModelExists() ) {
                	createStatus = createViewsInExistingModelInTxn(theMonitor);
            	} else {
            		createStatus = createViewModelInTxn(theMonitor);
            	}
                theMonitor.worked(50);
                
                theMonitor.done();
            }
        };
        try {
            new ProgressMonitorDialog(shell).run(false, true, op);
        } catch (final InterruptedException e) {
        } catch (final InvocationTargetException e) {
            UiConstants.Util.log(e.getTargetException());
        } catch (final Exception err) {
            Throwable t = err;

            if (err instanceof InvocationTargetException) {
                t = err.getCause();
            }

            WidgetUtil.showError(t);
        }
        
		return createStatus;
	}
	
    private IStatus createSourceModelInTxn(IProgressMonitor monitor) {
    	
        boolean requiredStart = ModelerCore.startTxn(true, true, "Import Teiid Metadata Create Source Model", this); //$NON-NLS-1$
        boolean succeeded = false;
        try {
        	sourceModel = createRelationalFileSourceModel();
        	monitor.worked(10);
        	
        	// Inject the connection profile info into the model
        	if (this.info.getConnectionProfile() != null) {
        		addConnectionProfileInfoToModel(sourceModel, this.info.getConnectionProfile());
            }
        	monitor.subTask(getString("task.savingSourceModel", sourceModel.getItemName()) ); //$NON-NLS-1$
            try {
                ModelUtilities.saveModelResource(sourceModel, monitor, false, this);
            } catch (Exception e) {
                throw new InvocationTargetException(e);
            }
            monitor.worked(10);
            if( createStatus.isOK() && sourceModel != null ) {
            	ModelEditorManager.openInEditMode(sourceModel, true, com.metamatrix.modeler.ui.UiConstants.ObjectEditor.IGNORE_OPEN_EDITOR);
            }
        	succeeded = true;
        } catch (Exception e) {
            IStatus status = new Status(IStatus.ERROR, UiConstants.PLUGIN_ID, getString("exceptionMessage_2", info.getSourceModelName()), e); //$NON-NLS-1$
            UiConstants.Util.log(status);
            MessageDialog.openError(shell, getString("exceptionMessage_2", info.getSourceModelName()), e.getMessage()); //$NON-NLS-1$
            return status;
        } finally {
            // if we started the txn, commit it.
            if (requiredStart) {
                if (succeeded) {
                    ModelerCore.commitTxn();
                } else {
                    ModelerCore.rollbackTxn();
                }
            }
        }
        monitor.worked(10);
        if( createStatus.isOK() && sourceModel != null ) {
        	ModelEditorManager.openInEditMode(sourceModel, true, com.metamatrix.modeler.ui.UiConstants.ObjectEditor.IGNORE_OPEN_EDITOR);
        }
        monitor.worked(10);
        
        return Status.OK_STATUS;
    }
    
    
    protected void addConnectionProfileInfoToModel(ModelResource sourceModel, IConnectionProfile profile) throws ModelWorkspaceException {
    	// Inject the connection profile info into the model
    	if (profile != null) {
            IConnectionInfoProvider provider = new FlatFileConnectionInfoProvider();
            provider.setConnectionInfo(sourceModel, profile);
        }
    }
	
    private IStatus createViewsInExistingModelInTxn(IProgressMonitor monitor) {
    	
        boolean requiredStart = ModelerCore.startTxn(true, true, "Import Teiid Metadata Create View Tables", this); //$NON-NLS-1$
        boolean succeeded = false;
        try {
        	monitor.subTask(getString("task.creatingViewTablesInViewModel") + info.getViewModelName()); //$NON-NLS-1$
        	viewModel = createViewsInExistingModel(sourceModel.getPath().removeFileExtension().lastSegment());
        	monitor.worked(10);
        	succeeded = true;
        } catch (Exception e) {
            IStatus status = new Status(IStatus.ERROR, UiConstants.PLUGIN_ID, getString("exceptionMessage_3", info.getViewModelName()), e); //$NON-NLS-1$
            UiConstants.Util.log(status);
            MessageDialog.openError(shell, getString("exceptionMessage_3", info.getViewModelName()), e.getMessage()); //$NON-NLS-1$
            return status;
        } finally {
            // if we started the txn, commit it.
            if (requiredStart) {
                if (succeeded) {
                    ModelerCore.commitTxn();
                } else {
                    ModelerCore.rollbackTxn();
                }
            }
        }
        
        return Status.OK_STATUS;
    }
    
    private IStatus createViewModelInTxn(IProgressMonitor monitor) {
    	ModelResource viewModel = null;
    	
        boolean requiredStart = ModelerCore.startTxn(true, true, "Import Teiid Metadata Create View Model", this); //$NON-NLS-1$
        boolean succeeded = false;
        try {
        	monitor.subTask(getString("task.creatingViewTablesInNewViewModel") + info.getViewModelName()); //$NON-NLS-1$
        	
        	viewModel = createViewsInNewModel(sourceModel.getPath().removeFileExtension().lastSegment());
        	
        	monitor.worked(40);
            if( createStatus.isOK() && sourceModel != null ) {
                try {
                    ModelUtilities.saveModelResource(viewModel, monitor, false, this);
                } catch (Exception e) {
                    throw new InvocationTargetException(e);
                }
            }
            monitor.worked(10);

            if( createStatus.isOK() && viewModel != null ) {
            	ModelEditorManager.openInEditMode(viewModel, true, com.metamatrix.modeler.ui.UiConstants.ObjectEditor.IGNORE_OPEN_EDITOR);
            }
        	succeeded = true;
        } catch (Exception e) {
            IStatus status = new Status(IStatus.ERROR, UiConstants.PLUGIN_ID, getString("exceptionMessage_4", info.getViewModelName()), e); //$NON-NLS-1$
            UiConstants.Util.log(status);
            MessageDialog.openError(shell, getString("exceptionMessage_4", info.getViewModelName()), e.getMessage()); //$NON-NLS-1$
            return status;
        } finally {
            // if we started the txn, commit it.
            if (requiredStart) {
                if (succeeded) {
                    ModelerCore.commitTxn();
                } else {
                    ModelerCore.rollbackTxn();
                }
            }
        }
        
        return Status.OK_STATUS;
    }

    protected ModelResource createViewsInExistingModel(String relationalModelName) throws ModelerCoreException  {
    	if( info.getViewModelLocation() != null && info.getViewModelName() != null ) {
    		IPath modelPath = info.getViewModelLocation().append(info.getViewModelName());
    		if( !modelPath.toString().toUpperCase().endsWith(".XMI")) { //$NON-NLS-1$
    			modelPath = modelPath.addFileExtension(".xmi"); //$NON-NLS-1$
    		}
    		
    		ModelWorkspaceItem item = ModelWorkspaceManager.getModelWorkspaceManager().findModelWorkspaceItem(modelPath, IResource.FILE);
            ModelEditor editor = ModelEditorManager.getModelEditorForFile( (IFile)item.getCorrespondingResource(), true);
            if (editor != null) {
                boolean isDirty = editor.isDirty();
                FlatFileViewModelFactory factory = new FlatFileViewModelFactory();
                
                for( TeiidMetadataFileInfo info : this.info.getFileInfos()) {
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
    
    private ModelResource createRelationalFileSourceModel() throws ModelerCoreException {
    	FlatFileRelationalModelFactory factory = new FlatFileRelationalModelFactory();
    	
    	String modelName = this.info.getSourceModelName();
    	
    	if (!modelName.toLowerCase().endsWith(DEFAULT_EXTENSION_LCASE)) {
    		modelName = modelName + DEFAULT_EXTENSION_LCASE;
        }
    	
    	return factory.createRelationalModel(this.info.getSourceModelLocation(), modelName);
    	
    }
    
    protected ModelResource createViewsInNewModel(String sourceModelName) throws ModelerCoreException {
    	FlatFileViewModelFactory factory = new FlatFileViewModelFactory();
    	
    	String modelName = this.info.getViewModelName();
    	
    	if (!modelName.toLowerCase().endsWith(DEFAULT_EXTENSION_LCASE)) {
    		modelName = modelName + DEFAULT_EXTENSION_LCASE;
        }
    	
    	ModelResource modelResource = factory.createViewRelationalModel(this.info.getViewModelLocation(), modelName);
    	
        for( TeiidMetadataFileInfo info : this.info.getFileInfos()) {
        	if( info.doProcess() ) {
        		factory.createViewTable(modelResource, info, sourceModelName);
        	}
        }

        return modelResource;
    }
    
    private IStatus createProceduresInExistingSourceModelInTxn(IProgressMonitor monitor) {
        boolean requiredStart = ModelerCore.startTxn(true, true, "Import Teiid Metadata Create View Tables", this); //$NON-NLS-1$
        boolean succeeded = false;
        try {
        	monitor.subTask(getString("task.creatingProceduresInSourceModel") + info.getSourceModelName()); //$NON-NLS-1$
        	sourceModel = addProcedureToRelationalSourceModel();
        	monitor.worked(10);
        	succeeded = true;
        } catch (Exception e) {
            IStatus status = new Status(IStatus.ERROR, UiConstants.PLUGIN_ID,getString("exceptionMessage_3", info.getViewModelName()), e); //$NON-NLS-1$
            UiConstants.Util.log(status);
            MessageDialog.openError(shell, getString("exceptionMessage_3", info.getViewModelName()), e.getMessage()); //$NON-NLS-1$
            return status;
        } finally {
            // if we started the txn, commit it.
            if (requiredStart) {
                if (succeeded) {
                    ModelerCore.commitTxn();
                } else {
                    ModelerCore.rollbackTxn();
                }
            }
        }
        
        return Status.OK_STATUS;
    }
 
    protected ModelResource addProcedureToRelationalSourceModel() throws ModelerCoreException {
    	if( info.getSourceModelLocation() != null && info.getSourceModelName() != null ) {
    		IPath modelPath = info.getSourceModelLocation().append(info.getSourceModelName());
    		if( !modelPath.toString().toUpperCase().endsWith(".XMI")) { //$NON-NLS-1$
    			modelPath = modelPath.addFileExtension("xmi"); //$NON-NLS-1$
    		}
    		
    		ModelWorkspaceItem item = ModelWorkspaceManager.getModelWorkspaceManager().findModelWorkspaceItem(modelPath, IResource.FILE);
            ModelEditor editor = ModelEditorManager.getModelEditorForFile( (IFile)item.getCorrespondingResource(), true);
            if (editor != null) {
            	ModelResource mr = editor.getModelResource();
                boolean isDirty = editor.isDirty();
                FlatFileRelationalModelFactory factory = new FlatFileRelationalModelFactory();
                
                factory.addMissingProcedure(mr, FlatFileRelationalModelFactory.GET_TEXT_FILES);
                
                mr.save(null, true);
                
                if (!isDirty && editor.isDirty()) {
                    editor.doSave(new NullProgressMonitor());
                }
                
                return mr;
            }
    	}
    	
    	return null;
    }
    
    public TeiidMetadataImportInfo getInfo() {
    	return this.info;
    }
 
}
