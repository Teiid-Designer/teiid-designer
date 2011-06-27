/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ui.navigator.model;

import java.util.Iterator;
import java.util.Set;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;

import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.notification.util.IgnorableNotificationSource;
import com.metamatrix.modeler.core.notification.util.NotificationUtilities;
import com.metamatrix.modeler.core.transaction.SourcedNotification;
import com.metamatrix.modeler.internal.ui.actions.ModelObjectAction;
import com.metamatrix.modeler.internal.ui.actions.TreeViewerRenameAction;
import com.metamatrix.modeler.internal.ui.actions.workers.ModelObjectWorker;
import com.metamatrix.modeler.internal.ui.util.ModelObjectNotificationHelper;
import com.metamatrix.modeler.internal.ui.util.ModelObjectTreeViewerNotificationHandler;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.editors.IInlineRenameable;
import com.metamatrix.modeler.ui.wizards.INewModelObjectWizard;

class ModelNavigatorNotificationHandler extends ModelObjectTreeViewerNotificationHandler implements UiConstants {

    private static final EObject[] EMPTY_EOBJECT_ARRAY = new EObject[] {};

    private IWorkbenchPart modelNavigator;
    private TreeViewerRenameAction renameAction;
    private IWorkbenchWindow window;

    /**
     * Construct an instance of ModelExplorerNotificationHandler.
     * 
     * @param tv
     */
    public ModelNavigatorNotificationHandler( TreeViewer viewer,
                                             IWorkbenchPart navigator ) {
        super(viewer);
        this.modelNavigator = navigator;
        this.window = navigator.getSite().getWorkbenchWindow();
        this.renameAction = new TreeViewerRenameAction();
        this.renameAction.setTreeViewer(viewer, (ILabelProvider)viewer.getLabelProvider());
    }

    /**
     * @see com.metamatrix.modeler.internal.ui.util.ModelObjectTreeViewerNotificationHandler#handleNotification(org.eclipse.emf.common.notify.Notification)
     */
    @Override
    protected ModelObjectNotificationHelper handleNotification( Notification notification ) {
        ModelObjectNotificationHelper notificationHelper = super.handleNotification(notification);

        // for added objects, expand the target:
        // Check for SourceNotification
        if (notification instanceof SourcedNotification) {
            Object source = ((SourcedNotification)notification).getSource();

            if (source instanceof IgnorableNotificationSource) {
                return notificationHelper;
            }
        }

        final TreeViewer viewer = getTreeViewer();

        if (NotificationUtilities.isAdded(notification) && !getTreeViewer().getTree().isDisposed()) {
            Set children = notificationHelper.getAddedChildren();

            if (children != null) {
                // remove non-primary stuff:
                Iterator itor = children.iterator();

                while (itor.hasNext()) {
                    EObject child = (EObject)itor.next();

                    if (!ModelerCore.getModelEditor().getMetamodelDescriptor(child).isPrimary()) {
                        // don't respond to the creation of a non-primary metamodel object
                        itor.remove();
                    }
                }

                // stop processing if everything was non-primary:
                if (children.isEmpty()) {
                    return notificationHelper;
                }

                // Added this pre-check on who the source is.
                Object source = ((SourcedNotification)notification).getSource();
                // Defect 19537 - added INewModelObjectWizard as Source because of new XML Documents and other created by these
                // wizards.
                boolean isModelObjectAction = ((source instanceof ModelObjectAction) || (source instanceof ModelObjectWorker) || (source instanceof INewModelObjectWizard));

                // see if the tree was the active part when this occurred
                if (this.window.getActivePage() != null && isModelObjectAction) {
                    // Added check to see if this is active page, if not, set focus.
                    // This solves problem of adding object to model when editor is not open.
                    // ModelEditor was grabbing focus and changing the active part.
                    IWorkbenchPart activePart = this.window.getActivePage().getActivePart();
                    boolean isThisActivePage = (activePart == this.modelNavigator);
                    IInlineRenameable renameablePart = null;

                    if (this.window.getActivePage().getActivePart() instanceof IInlineRenameable) {
                        renameablePart = (IInlineRenameable)activePart;
                    }

                    if (!isThisActivePage) {
                        this.modelNavigator.setFocus();
                    }

                    // show in tree:
                    EObject child = (EObject)children.iterator().next();
                    viewer.expandToLevel(child, 0);
                    IStructuredSelection selection = new StructuredSelection(children.toArray(EMPTY_EOBJECT_ARRAY));
                    viewer.setSelection(selection);

                    // if just one was added, set up for rename:
                    if ((children.size() == 1) && (notification instanceof SourcedNotification)) {
                        // see if the source of the notificaiton was the new child or sibling action
                        if (ModelerCore.getModelEditor().hasName(child)) {
                            this.renameAction.selectionChanged(this.modelNavigator, selection);

                            if (renameablePart == null) {
                                final TreeViewerRenameAction action = this.renameAction;

                                Runnable runnable = new Runnable() {
                                    /**
                                     * {@inheritDoc}
                                     * 
                                     * @see java.lang.Runnable#run()
                                     */
                                    @Override
                                    public void run() {
                                        if (viewer.getControl().isFocusControl())
                                            action.doRun(false);
                                    }
                                };

                                Display.getCurrent().asyncExec(runnable);
                            } else {
                                renameablePart.renameInline(child, renameablePart.getInlineRenameable(child));
                            }
                        }
                    }
                }
            }
        }

        return notificationHelper;
    }

    /**
     * @see com.metamatrix.modeler.internal.ui.util.ModelObjectTreeViewerNotificationHandler#shouldHandleNotification(org.eclipse.emf.common.notify.Notification)
     */
    @Override
    public boolean shouldHandleNotification( Notification notification ) {
        return true;
    }

}
