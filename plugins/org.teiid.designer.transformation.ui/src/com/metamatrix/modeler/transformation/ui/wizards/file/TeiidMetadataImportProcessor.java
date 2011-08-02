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
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.teiid.designer.datatools.connection.IConnectionInfoProvider;
import org.teiid.designer.datatools.profiles.flatfile.FlatFileConnectionInfoProvider;

import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceItem;
import com.metamatrix.modeler.internal.core.workspace.ModelWorkspaceManager;
import com.metamatrix.modeler.internal.ui.editors.ModelEditor;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.transformation.ui.UiConstants;
import com.metamatrix.modeler.transformation.ui.UiPlugin;
import com.metamatrix.modeler.ui.editors.ModelEditorManager;
import com.metamatrix.ui.internal.util.WidgetUtil;

public class TeiidMetadataImportProcessor implements UiConstants {
	private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(TeiidMetadataImportProcessor.class);
	
    private static String getString( final String id ) {
        return Util.getString(I18N_PREFIX + id);
    }
    
    private static String getString( final String id, final Object param ) {
        return Util.getString(I18N_PREFIX + id, param);
    }
	
	TeiidMetadataImportInfo info;
	ModelResource newSourceModel;
	ModelResource viewModel;
	IStatus createStatus;
	
	public TeiidMetadataImportProcessor(TeiidMetadataImportInfo info) {
		super();
		this.info = info;
		createStatus = Status.OK_STATUS;
	}
	
	public IStatus execute() {
		final IRunnableWithProgress op = new IRunnableWithProgress() {
            public void run( final IProgressMonitor monitor ) throws InvocationTargetException {
                monitor.beginTask(getString("task.creatingSourceModel", info.getSourceModelName()), 100); //$NON-NLS-1$
                createStatus = createSourceModelInTxn(monitor);

                if( info.viewModelExists() ) {
                	createViewsInExistingModelInTxn(monitor);
            	} else {
            		createStatus = createViewModelInTxn(monitor);
            	}
                monitor.worked(50);

                

                monitor.worked(10);
            }
        };

        try {
            final ProgressMonitorDialog dlg = new ProgressMonitorDialog(UiPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell());
            dlg.run(false, true, op);
            if (dlg.getProgressMonitor().isCanceled()) {
                // DO NOTHING
            }
        } catch (final InterruptedException ignored) {

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
        	newSourceModel = createRelationalFileSourceModel();
        	monitor.worked(10);
        	
        	// Inject the connection profile info into the model
        	if (this.info.getConnectionProfile() != null) {
                IConnectionInfoProvider provider = new FlatFileConnectionInfoProvider();
                provider.setConnectionInfo(newSourceModel, this.info.getConnectionProfile());
            }
        	monitor.subTask(getString("task.savingSourceModel", newSourceModel.getItemName()) ); //$NON-NLS-1$
            try {
                ModelUtilities.saveModelResource(newSourceModel, monitor, false, this);
            } catch (Exception e) {
                throw new InvocationTargetException(e);
            }
            monitor.worked(10);
            if( createStatus.isOK() && newSourceModel != null ) {
            	ModelEditorManager.openInEditMode(newSourceModel, true, com.metamatrix.modeler.ui.UiConstants.ObjectEditor.IGNORE_OPEN_EDITOR);
            }
        	succeeded = true;
        } catch (Exception e) {
            MessageDialog.openError(Display.getCurrent().getActiveShell(),
            		getString("exceptionMessage_2", info.getSourceModelName()), e.getMessage()); //$NON-NLS-1$
            IStatus status = new Status(IStatus.ERROR, UiConstants.PLUGIN_ID,
            		getString("exceptionMessage_2", info.getSourceModelName()), e); //$NON-NLS-1$
            UiConstants.Util.log(status);
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
        if( createStatus.isOK() && newSourceModel != null ) {
        	ModelEditorManager.openInEditMode(newSourceModel, true, com.metamatrix.modeler.ui.UiConstants.ObjectEditor.IGNORE_OPEN_EDITOR);
        }
        monitor.worked(10);
        
        return Status.OK_STATUS;
    }
	
    private IStatus createViewsInExistingModelInTxn(IProgressMonitor monitor) {
    	
        boolean requiredStart = ModelerCore.startTxn(true, true, "Import Teiid Metadata Create View Tables", this); //$NON-NLS-1$
        boolean succeeded = false;
        try {
        	monitor.subTask(getString("task.creatingViewTablesInViewModel") + info.getViewModelName()); //$NON-NLS-1$
        	viewModel = createViewsInExistingModel(newSourceModel.getPath().removeFileExtension().lastSegment());
        	monitor.worked(10);
        	succeeded = true;
        } catch (Exception e) {
            MessageDialog.openError(Display.getCurrent().getActiveShell(),
            		getString("exceptionMessage_3", info.getViewModelName()), e.getMessage()); //$NON-NLS-1$
            IStatus status = new Status(IStatus.ERROR, UiConstants.PLUGIN_ID,
            		getString("exceptionMessage_3", info.getViewModelName()), e); //$NON-NLS-1$
            UiConstants.Util.log(status);
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
        	
        	viewModel = createViewsInNewModel(newSourceModel.getPath().removeFileExtension().lastSegment());
        	
        	monitor.worked(40);
            if( createStatus.isOK() && newSourceModel != null ) {
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
            MessageDialog.openError(Display.getCurrent().getActiveShell(),
            		getString("exceptionMessage_4", info.getViewModelName()), e.getMessage()); //$NON-NLS-1$
            IStatus status = new Status(IStatus.ERROR, UiConstants.PLUGIN_ID,
            		getString(".exceptionMessage", info.getViewModelName()), e); //$NON-NLS-1$
            UiConstants.Util.log(status);
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

    private ModelResource createViewsInExistingModel(String relationalModelName) throws ModelerCoreException  {
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
    	
    	return factory.createModel(this.info.getSourceModelLocation(), this.info.getSourceModelName());
    	
    }
    
    private ModelResource createViewsInNewModel(String sourceModelName) throws ModelerCoreException {
    	FlatFileViewModelFactory factory = new FlatFileViewModelFactory();
    	
    	ModelResource modelResource = factory.createViewRelationalModel(this.info.getViewModelLocation(), this.info.getViewModelName());
        for( TeiidMetadataFileInfo info : this.info.getFileInfos()) {
        	if( info.doProcess() ) {
        		factory.createViewTable(modelResource, info, sourceModelName);
        	}
        }

        return modelResource;
    }
}
