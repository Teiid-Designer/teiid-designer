package com.metamatrix.modeler.internal.ui.actions;

import static com.metamatrix.modeler.ui.UiConstants.Util;
import static com.metamatrix.modeler.ui.UiConstants.Extensions.PERSPECTIVE;
import static com.metamatrix.modeler.ui.UiConstants.Extensions.Explorer.VIEW;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import com.metamatrix.modeler.internal.ui.search.ModelObjectSelectionDialog;
import com.metamatrix.modeler.ui.editors.ModelEditorManager;
import com.metamatrix.ui.internal.util.WidgetUtil;

public final class FindModelObjectHandler extends AbstractHandler {

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands.ExecutionEvent)
     */
    @Override
    public Object execute( ExecutionEvent event ) throws ExecutionException {
        Shell shell = HandlerUtil.getActiveShell(event);
        ModelObjectSelectionDialog dialog = new ModelObjectSelectionDialog(shell);

        if (!dialog.userCancelledDuringLoad()) {
            dialog.open();

            if (dialog.getReturnCode() == Window.OK) {
                EObject eObj = dialog.getSelectedEObject();
                try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                if (eObj != null) {
                    // switch to Teiid Designer perspective
                	// NOT SURE WHY THE HandlerUtil method below doesn't work for this class. Works for other
                	// Search action handlers (Transformations, Metadata and Relationships
                	// TODO: if we ever need to
                    // IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
                	// IWorkbench workbench =  window.getWorkbench();
                    IWorkbench workbench =  PlatformUI.getWorkbench();
                    IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
                    try {
                        workbench.showPerspective(PERSPECTIVE, window);
                    } catch (Exception theException) {
                        Util.log(theException);
                    }

                    // open model explorer
                    IWorkbenchPage page = window.getActivePage();
                    IViewPart view = page.findView(VIEW);

                    // if the model explorer view is not found it is not open in perspective
                    if (view == null) {
                        try {
                            view = page.showView(VIEW);
                        } catch (PartInitException theException) {
                            Util.log(theException);
                            WidgetUtil.showError(theException.getLocalizedMessage());
                        }
                    }

                    // should always have a model explorer view here unless it couldn't be constructed
                    if (view != null) {
                        ISelectionProvider selectionProvider = view.getViewSite().getSelectionProvider();
                        selectionProvider.setSelection(new StructuredSelection(eObj));
                    }

                    // now open the object in an editor
                    ModelEditorManager.open(eObj, !ModelEditorManager.isOpen(eObj));
                }
            }
        }

        return null; // per javadoc
    }

}
