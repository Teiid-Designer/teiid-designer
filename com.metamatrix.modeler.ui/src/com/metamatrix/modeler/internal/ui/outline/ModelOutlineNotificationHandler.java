/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.outline;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchWindow;
import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.modeler.core.notification.util.NotificationUtilities;
import com.metamatrix.modeler.core.transaction.SourcedNotification;
import com.metamatrix.modeler.internal.ui.actions.TreeViewerRenameAction;
import com.metamatrix.modeler.internal.ui.util.ModelObjectNotificationHelper;
import com.metamatrix.modeler.internal.ui.util.ModelObjectTreeViewerNotificationHandler;

public class ModelOutlineNotificationHandler extends ModelObjectTreeViewerNotificationHandler {

    private ModelOutlineTreeViewer viewer;
    private IWorkbenchWindow window;
    TreeViewerRenameAction renameAction;

    /**
     * Construct an instance of ModelOutlineNotificationHandler.
     * 
     * @param tv
     */
    public ModelOutlineNotificationHandler( ModelOutlineTreeViewer outlinePage ) {
        super(outlinePage.getTree());
        this.viewer = outlinePage;
        this.window = viewer.getSite().getWorkbenchWindow();
        this.renameAction = new TreeViewerRenameAction();
        this.renameAction.setTreeViewer(outlinePage.getTree(), (ILabelProvider)outlinePage.getTree().getLabelProvider());
    }

    /**
     * @see com.metamatrix.modeler.internal.ui.util.ModelObjectTreeViewerNotificationHandler#handleNotification(org.eclipse.emf.common.notify.Notification)
     */
    @Override
    protected ModelObjectNotificationHelper handleNotification( Notification notification ) {
        ModelObjectNotificationHelper notHelp = super.handleNotification(notification);
        // for added objects, expand the target:

        if (NotificationUtilities.getEObject(notification) instanceof ModelAnnotation) {
            if (!getTreeViewer().getControl().isDisposed()) getTreeViewer().refresh();
        }

        if (NotificationUtilities.isAdded(notification) && getTreeViewer() != null) {

            EObject[] children = NotificationUtilities.getAddedChildren(notification);
            if (children != null && children.length == 1) {
                EObject child = children[0];
                if (!(child instanceof Diagram)) {
                    getTreeViewer().expandToLevel(children[0], 0);
                    IStructuredSelection selection = new StructuredSelection(children);
                    getTreeViewer().setSelection(selection);

                    if (this.window.getActivePage().getActivePart() == this.viewer.getWorkbenchPart()) {
                        this.renameAction.selectionChanged(this.viewer.getWorkbenchPart(), selection);
                        Display.getCurrent().asyncExec(new Runnable() {
                            public void run() {
                                renameAction.doRun(false);
                            }
                        });
                    }
                }
            }
        } else if (NotificationUtilities.isMoved(notification)) {
            if (notification instanceof SourcedNotification
                && ((SourcedNotification)notification).getSource() instanceof ModelOutlineTreeViewerDropAdapter) {
                getTreeViewer().refresh();
            }
        }
        return notHelp;
    }
}
