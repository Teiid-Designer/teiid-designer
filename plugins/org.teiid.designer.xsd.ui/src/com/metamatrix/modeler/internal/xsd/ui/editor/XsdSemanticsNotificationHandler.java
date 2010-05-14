/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.xsd.ui.editor;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPart;
import com.metamatrix.modeler.core.notification.util.NotificationUtilities;
import com.metamatrix.modeler.internal.ui.actions.TreeViewerRenameAction;
import com.metamatrix.modeler.internal.ui.util.ModelObjectNotificationHelper;
import com.metamatrix.modeler.internal.ui.util.ModelObjectTreeViewerNotificationHandler;

/**
 * XsdSemanticsNotificationHandler is the notification handler for the semantic editor tree.
 */
public class XsdSemanticsNotificationHandler extends ModelObjectTreeViewerNotificationHandler {

    private IWorkbenchPart part;
    private Resource resource;
    TreeViewerRenameAction renameAction;

    /**
     * Construct an instance of XsdSemanticsNotificationHandler.
     */
    public XsdSemanticsNotificationHandler( TreeViewer viewer,
                                            Resource resource,
                                            IWorkbenchPart part ) {
        super(viewer);
        this.resource = resource;
        this.part = part;
        this.renameAction = new TreeViewerRenameAction();
        this.renameAction.setTreeViewer(viewer, (ILabelProvider)viewer.getLabelProvider());
    }

    /**
     * @see com.metamatrix.modeler.internal.ui.util.ModelObjectTreeViewerNotificationHandler#handleNotification(org.eclipse.emf.common.notify.Notification)
     */
    @Override
    protected ModelObjectNotificationHelper handleNotification( Notification notification ) {
        EObject target = NotificationUtilities.getEObject(notification);
        if (target != null && this.resource.equals(target.eResource())) {
            ModelObjectNotificationHelper notHelp = super.handleNotification(notification);
            // for added objects, expand the target:
            if (NotificationUtilities.isAdded(notification) && getTreeViewer() != null) {

                EObject[] children = NotificationUtilities.getAddedChildren(notification);
                if (children != null && children.length == 1) {
                    getTreeViewer().expandToLevel(children[0], 0);
                    IStructuredSelection selection = new StructuredSelection(children);
                    getTreeViewer().setSelection(selection);

                    // use my page, not the active page:
                    if (part.getSite().getPage().getActivePart() == this.part) {
                        this.renameAction.selectionChanged(part, selection);
                        Display.getCurrent().asyncExec(new Runnable() {
                            public void run() {
                                renameAction.doRun(false);
                            }
                        });
                    }
                }
            }
            return notHelp;
        }
        return null;
    }

}
