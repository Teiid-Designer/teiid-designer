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
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.teiid.designer.core.refactor.AbstractRefactorModelHandler;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.core.workspace.WorkspaceResourceFinderUtil;
import org.teiid.designer.ui.UiPlugin;
import org.teiid.designer.ui.refactor.RefactorResourcesUtils;
import org.teiid.designer.vdb.VdbUtil;
import org.teiid.designer.vdb.ui.VdbUiConstants;
import org.teiid.designer.vdb.ui.editor.VdbEditor;
/**
 *
 */
public class VdbUiRefactorHandler extends AbstractRefactorModelHandler {

	@Override
	public boolean preProcess(RefactorType refactorType, IResource refactoredResource, IProgressMonitor monitor) {
	    if (RefactorResourcesUtils.isClosedProject(refactoredResource)) {
	        /*
	         * By definition, a closed project will not contain any open VDB editors
	         */
            return true;
        }

	    // Find and show affected VDBs

	    final Collection<IFile> allVdbResourcesInProject =
	        WorkspaceResourceFinderUtil.getProjectFileResources(refactoredResource.getProject(),
	                                                                                            WorkspaceResourceFinderUtil.VDB_RESOURCE_FILTER);

    	Collection<IFile> targetVdbs = new ArrayList<IFile>();
    	Collection<VdbEditor> openVdbEditors = new ArrayList<VdbEditor>();

        for( IFile theVdb : allVdbResourcesInProject ) {
    	    if (refactoredResource instanceof IFolder) {
    	        IFolder folder = (IFolder) refactoredResource;
    	        try {
                    IResource[] members = folder.members();
                    for (int i = 0; i < members.length; ++i) {
                        preProcess(refactorType, members[i], monitor);
                    }
                } catch (CoreException ex) {
                    VdbUiConstants.Util.log(ex);
                    return false;
                }

    	    } else if (refactoredResource instanceof IFile) {
    	        try {
                    if( VdbUtil.modelInVdb(theVdb, (IFile)refactoredResource) ) {
                        targetVdbs.add(theVdb);
                        VdbEditor vdbEditor = getVdbEditorForFile(theVdb);
                        if( vdbEditor != null ) {
                            openVdbEditors.add(vdbEditor);
                        }
                    }
                } catch (Exception ex) {
                    VdbUiConstants.Util.log(ex);
                    return false;
                }
    	    }
    	}

    	if( targetVdbs.isEmpty() ) return true;

    	for( VdbEditor editor : openVdbEditors ) {
    	    closeVdbEditor(editor);
    	}

		return true;
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
}