package org.teiid.designer.runtime.ui;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;

import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.internal.vdb.ui.editor.VdbEditor;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.modeler.vdb.ui.VdbUiConstants;

public class VdbRequiresSaveChecker {
	
    public static boolean insureOpenVdbSaved(IFile vdbFile) {
    	CoreArgCheck.isNotNull(vdbFile, "vdbFile"); //$NON-NLS-1$
    	
    	String vdbName = vdbFile.getFullPath().removeFileExtension().lastSegment();
    	
    	
        final IWorkbenchWindow window = UiPlugin.getDefault().getCurrentWorkbenchWindow();
        VdbEditor vdbEditor = null;
        
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
                            if (vdbFile.equals(((IFileEditorInput)input).getFile())) {
                                // found it;
                                if (editor instanceof VdbEditor) {
                                	vdbEditor = (VdbEditor)editor;
                                }
                                break;
                            }
                        }
                    }
                }
            }
        }
        
        if( vdbEditor != null ) {
        	if( vdbEditor.isDirty() ) {
        		// Query the user
        		final VdbEditor tmpVdbEditor = vdbEditor;
        		
        		
        		boolean doSave = MessageDialog.openConfirm(window.getShell(), 
        				DqpUiConstants.UTIL.getString("VdbRequiresSaveChecker.unsavedVdb.title"),  //$NON-NLS-1$
        				DqpUiConstants.UTIL.getString("VdbRequiresSaveChecker.unsavedVdb.message", vdbName));  //$NON-NLS-1$
        		
        		if( doSave ) {
        	        // create VDB resource
        	        final IRunnableWithProgress op = new IRunnableWithProgress() {
        	            @SuppressWarnings("unchecked")
        				public void run( final IProgressMonitor monitor ) throws InvocationTargetException {
        	                try {
        	                	tmpVdbEditor.doSave(new NullProgressMonitor());
				            } catch (final Exception err) {
				                throw new InvocationTargetException(err);
				            } finally {
				                monitor.done();
				            }
        	            }
        	        };
        	        try {
        	            new ProgressMonitorDialog(window.getShell()).run(false, true, op);
        	            return true;
        	        } catch (Throwable err) {
        	            if (err instanceof InvocationTargetException) {
        	                err = ((InvocationTargetException)err).getTargetException();
        	            }
        	            VdbUiConstants.Util.log(err);
        	            return false;
        	        }
        		} else {
        			return false;
        		}
        	}
        }
        
        return true;
    }
}