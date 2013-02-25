/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.vdb.ui.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.teiid.designer.core.refactor.IRefactorNonModelResourceHandler;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.core.workspace.ResourceFilter;
import org.teiid.designer.core.workspace.WorkspaceResourceFinderUtil;
import org.teiid.designer.ui.UiPlugin;
import org.teiid.designer.vdb.VdbUtil;
import org.teiid.designer.vdb.ui.Messages;
import org.teiid.designer.vdb.ui.editor.VdbEditor;
/**
 *
 */
public class VdbUiRefactorHandler implements IRefactorNonModelResourceHandler {

    /**
     * {@inheritDoc}
     * 
	 * @see org.teiid.designer.core.refactor.IRefactorModelHandler#helpUpdateDependentModelContents(int, org.teiid.designer.core.workspace.ModelResource, java.util.Map, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void helpUpdateDependentModelContents(int type,
			ModelResource modelResource, Map refactoredPaths,
			IProgressMonitor monitor) {
		// No implementation
		
	}

    /**
     * {@inheritDoc}
     * 
	 * @see org.teiid.designer.core.refactor.IRefactorModelHandler#helpUpdateModelContents(int, org.teiid.designer.core.workspace.ModelResource, java.util.Map, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void helpUpdateModelContents(int type,
			ModelResource refactoredModelResource, Map refactoredPaths,
			IProgressMonitor monitor) {
		// No implementation
		
	}

    /**
     * {@inheritDoc}
     * 
	 * @see org.teiid.designer.core.refactor.IRefactorModelHandler#helpUpdateModelContentsForDelete(java.util.Collection, java.util.Collection, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void helpUpdateModelContentsForDelete(
			Collection<Object> deletedResourcePaths,
			Collection<Object> directDependentResources,
			IProgressMonitor monitor) {
		// No implementation
		
	}


    /**
     * {@inheritDoc}
     * 
	 * @see org.teiid.designer.core.refactor.IRefactorNonModelResourceHandler#processNonModel(int, org.eclipse.core.resources.IResource, java.util.Map, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void processNonModel(int type, IResource refactoredResource,
			Map refactoredPaths, IProgressMonitor monitor) throws Exception {
		// No implementation
	}
	
    /**
     * {@inheritDoc}
     * 
	 * @see org.teiid.designer.core.refactor.IRefactorModelHandler#preProcess(int, org.eclipse.core.resources.IResource, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public boolean preProcess(int refactorType, IResource refactoredResource, IProgressMonitor monitor) {
		// Find and show affected VDBs
    	@SuppressWarnings("unchecked")
		final Collection<IFile> allVdbResourcesInProject = 
    			WorkspaceResourceFinderUtil.getAllWorkspaceResources(new VdbResourceFilter(refactoredResource.getProject()));
    	
    	Collection<IFile> targetVdbs = new ArrayList<IFile>();
    	Collection<VdbEditor> openVdbEditors = new ArrayList<VdbEditor>();
    	
    	for( IFile theVdb : allVdbResourcesInProject ) {
    		if( VdbUtil.modelInVdb(theVdb, (IFile)refactoredResource) ) {
    			targetVdbs.add(theVdb);
	    		VdbEditor vdbEditor = getVdbEditorForFile(theVdb);
    			if( vdbEditor != null ) {
    				openVdbEditors.add(vdbEditor);
    			}
    		}
    	}
    	
    	if( targetVdbs.isEmpty() ) return true;
    	
    	String message = NLS.bind(Messages.refactorModelVdbDependencyMessage_openEditors, refactoredResource.getName());
    	if( openVdbEditors.isEmpty()) message = NLS.bind(Messages.refactorModelVdbDependencyMessage_noOpenEditors, refactoredResource.getName());
		boolean result = MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), Messages.refactorModelVdbDependencyTitle, message);
		
		if( result) {
			for( VdbEditor editor : openVdbEditors ) {
				closeVdbEditor(editor);
			}
		}
    	
    	
    	
		return true;
	}

    /**
     * {@inheritDoc}
     * 
	 * @see org.teiid.designer.core.refactor.IRefactorModelHandler#postProcess(int, org.eclipse.core.resources.IResource, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void postProcess(int refactorType, IResource refactoredResource, IProgressMonitor monitor) {
		// Do nothing
	}

	/**
	 * Returns a VDB editor given a vdb resource if editor is open
	 * @param vdb the vdb
	 * @return the vdb editor
	 */
	public static VdbEditor getVdbEditorForFile(IResource vdb) {
        if (vdb != null&& vdb.exists()) {
            IWorkbenchWindow window = UiPlugin.getDefault().getCurrentWorkbenchWindow();

            if (window != null) {
                final IWorkbenchPage page = window.getActivePage();

                if (page != null) {
                    // look through the open editors and see if there is one available for this model file.
                    IEditorReference[] editors = page.getEditorReferences();
                    for (int i = 0; i < editors.length; ++i) {

                        IEditorPart editor = editors[i].getEditor(false);
                        if (editor != null) {
                            IEditorInput input = editor.getEditorInput();
                            if (input instanceof IFileEditorInput) {
                                if (vdb.equals(((IFileEditorInput)input).getFile()) ||
                                		vdb.getFullPath().equals(((IFileEditorInput)input).getFile().getFullPath())) {
                                    // found it;
                                    if (ModelUtil.isVdbArchiveFile(vdb) ) {
                                        return (VdbEditor)editor;
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        
        return null;
    }
    
    /**
     * Simple method to close a VDB editor in a safe runnable thread
     * @param editor
     */
    public static void closeVdbEditor( final VdbEditor editor ) {
        SafeRunner.run(new SafeRunnable() {

            @Override
			public void run() {
            	UiPlugin.getDefault().getCurrentWorkbenchWindow().getActivePage().closeEditor(editor, true);
            }

            @Override
            public void handleException( Throwable e ) {
                // Exception has already being logged by Core. Do nothing.
            }
        });
    }
    
    /**
     * Simple method to close a VDB editor if open
     * @param vdb
     */
    public static void closeVdbEditor( final IResource vdb ) {
    	VdbEditor editor = getVdbEditorForFile(vdb);
    	if( editor != null ) {
    		closeVdbEditor(editor);
    	}
    }
    
    class VdbResourceFilter implements ResourceFilter {
    	IProject project;
    	
        /**
    	 * @param project the target project
    	 */
    	public VdbResourceFilter(IProject project) {
    		super();
    		this.project = project;
    	}

    	@Override
    	public boolean accept( final IResource res ) {
    		if( project != null ) {
    			return res.getProject() == project && ModelUtil.isVdbArchiveFile(res);
    		}
    		
            return ModelUtil.isVdbArchiveFile(res);
        }

    }

}
