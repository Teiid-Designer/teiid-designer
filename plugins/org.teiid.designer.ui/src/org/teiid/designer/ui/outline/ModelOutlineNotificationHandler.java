/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ui.outline;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchWindow;
import org.teiid.designer.core.notification.util.NotificationUtilities;
import org.teiid.designer.core.transaction.SourcedNotification;
import org.teiid.designer.metamodels.core.ModelAnnotation;
import org.teiid.designer.metamodels.diagram.Diagram;
import org.teiid.designer.ui.actions.TreeViewerRenameAction;
import org.teiid.designer.ui.util.ModelObjectNotificationHelper;
import org.teiid.designer.ui.util.ModelObjectTreeViewerNotificationHandler;


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
     * @see org.teiid.designer.ui.util.ModelObjectTreeViewerNotificationHandler#handleNotification(org.eclipse.emf.common.notify.Notification)
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
                            @Override
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
