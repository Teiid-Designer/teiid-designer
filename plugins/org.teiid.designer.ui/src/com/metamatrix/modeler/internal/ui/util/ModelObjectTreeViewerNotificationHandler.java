/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.resources.IResource;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.edit.provider.INotifyChangedListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDParticle;
import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.metamodels.diagram.DiagramContainer;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.notification.util.NotificationUtilities;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;

/**
 * ModelObjectTreeViewerNotificationHandler
 */
public class ModelObjectTreeViewerNotificationHandler implements INotifyChangedListener {
    // /////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    //private static final String THIS_CLASS = "ModelObjectTreeViewerNotificationHandler"; //$NON-NLS-1$
    // private boolean logDebug = false;

    private TreeViewer treeViewer;
    private static int nNotifications = 0;

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    // /////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Construct an instance of ModelObjectTreeViewerNotificationHandler.
     */
    public ModelObjectTreeViewerNotificationHandler( TreeViewer tv ) {
        super();
        this.treeViewer = tv;
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    /* (non-Javadoc)
     * @see org.eclipse.emf.edit.provider.INotifyChangedListener#notifyChanged(org.eclipse.emf.common.notify.Notification)
     */
    public void notifyChanged( final Notification notification ) {
        // This check verifies that a tree exists, and it isn't disposed.
        if (!treeIsValid()) return;

        // for all other events, just refresh the tree in the UI thread.
        Display.getDefault().asyncExec(new Runnable() {
            public void run() {
                handleNotification(notification);
            }
        });
    }

    protected ModelObjectNotificationHelper handleNotification( Notification notification ) {
        ModelObjectNotificationHelper notificationHelper = new ModelObjectNotificationHelper(notification);
        // System.out.println(" MOTVNH.handleNotification() Notifications = " + (nNotifications + 1));
        // System.out.println(" MOTVNH.handleNotification() HELPER = \n" + notificationHelper.toString());
        if (notificationHelper.shouldHandleNotification() && treeIsValid() && !notificationHelper.allChangesAreIgnorable()) {
            Iterator iter = notificationHelper.getAddOrRemoveTargets().iterator();
            EObject nextTarget = null;
            while (iter.hasNext()) {
                nextTarget = (EObject)iter.next();
                getTreeViewer().refresh(nextTarget);

                // The tree view of an XSD resource that contains compositors (sequence/choice/all)
                // has tree nodes backed by XSDParticles and not the XSDModelGroup, XSDElementDeclaration
                // etc. associated with the XSDParticle. So attempt a second refresh using the
                // the XSDParticle instance (see defect 21088)
                if (nextTarget instanceof XSDModelGroup) {
                    nextTarget = ((XSDModelGroup)nextTarget).eContainer();
                    getTreeViewer().refresh(nextTarget);
                }
                if (nextTarget.eContainer() instanceof XSDParticle) {
                    nextTarget = nextTarget.eContainer();
                    getTreeViewer().refresh(nextTarget);
                }
            }

            iter = notificationHelper.getChangeTargets().iterator();
            while (iter.hasNext()) {
                nextTarget = (EObject)iter.next();
                getTreeViewer().refresh(nextTarget);

                // The tree view of an XSD resource that contains compositors (sequence/choice/all)
                // has tree nodes backed by XSDParticles and not the XSDModelGroup, XSDElementDeclaration
                // etc. associated with the XSDParticle. So attempt a second refresh using the
                // the XSDParticle instance (see defect 21088)
                if (nextTarget instanceof XSDModelGroup) {
                    nextTarget = ((XSDModelGroup)nextTarget).eContainer();
                    getTreeViewer().refresh(nextTarget);
                }
                if (nextTarget.eContainer() instanceof XSDParticle) {
                    nextTarget = nextTarget.eContainer();
                    getTreeViewer().refresh(nextTarget);
                }
            }

            iter = notificationHelper.getLeftoverNotifications().iterator();
            while (iter.hasNext()) {
                handleSingleNotification((Notification)iter.next());
            }

            List refreshedResources = new ArrayList();

            // Now we ask to see if the model has changed
            if (!notificationHelper.getModifiedResources().isEmpty()) {
                iter = notificationHelper.getModifiedResources().iterator();
                IResource nextResource = null;
                while (iter.hasNext()) {
                    nextResource = (IResource)iter.next();
                    // System.out.println("  -->> [" + nNotifications +
                    // "]  MOTVNH.handleNotification(1)  calling Refresh for Resource = " + nextResource.getName());
                    getTreeViewer().refresh(nextResource);
                    refreshedResources.add(nextResource);
                }
            }

            // Now we ask to see if the model has changed
            if (notificationHelper.getModelChildrenChanged()) {
                iter = notificationHelper.getChangeModels().iterator();
                IResource nextResource = null;
                while (iter.hasNext()) {
                    nextResource = (IResource)iter.next();
                    if (!refreshedResources.contains(nextResource)) {
                        // System.out.println("  -->> [" + nNotifications +
                        // "]  MOTVNH.handleNotification(2)  calling Refresh for Resource = " + nextResource.getName());
                        getTreeViewer().refresh(nextResource);
                    }
                }
            }
        }
        nNotifications++;
        return notificationHelper;
    }

    public boolean shouldHandleNotification( Notification notification ) {
        return true;
    }

    // ------------------------------------------------------------------
    // private helper methods.
    // ------------------------------------------------------------------

    private void handleSingleNotification( Notification notification ) {
        if (NotificationUtilities.isAdded(notification)) {
            performAdd(notification);
        } else if (NotificationUtilities.isRemoved(notification)) {
            performRemove(notification);
        } else if (NotificationUtilities.isChanged(notification)) {
            performChange(notification);
        }
    }

    private void performAdd( Notification notification ) {
        Object targetObject = ModelerCore.getModelEditor().getChangedObject(notification);
        // if( logDebug )
        //             UiConstants.Util.print(THIS_CLASS + ".performAdd() START:  TargetObject = "  + targetObject ); //$NON-NLS-1$

        if (targetObject != null && targetObject instanceof EObject && treeIsValid()) {
            // we know that the object is not a child of a model resource !!!!!

            // if( logDebug )
            //                 UiConstants.Util.print(THIS_CLASS + ".performAdd() TargetEObject = " + targetObject ); //$NON-NLS-1$

            if (targetObject instanceof DiagramContainer) {
                // get the added objects, get their target's and
                Collection refreshTargets = new HashSet();
                EObject targetEObject = null;
                EObject[] newChildren = NotificationUtilities.getAddedChildren(notification);
                for (int i = 0; i < newChildren.length; i++) {
                    targetEObject = ((Diagram)newChildren[i]).getTarget();
                    if (targetEObject != null) {
                        if (!(targetEObject instanceof ModelAnnotation)) {
                            refreshTargets.add(targetEObject);
                        }
                    }
                }
                refreshTree(refreshTargets);
            }

        }

        // if( logDebug )
        //             UiConstants.Util.print(THIS_CLASS + ".performAdd() END" ); //$NON-NLS-1$
    }

    private void refreshTree( Collection objects ) {
        for (Iterator iter = objects.iterator(); iter.hasNext();) {
            Object nextObj = iter.next();
            if (nextObj != null) {
                getTreeViewer().refresh(nextObj);
            }
        }
    }

    private void performRemove( Notification notification ) {
        Object targetObject = ModelerCore.getModelEditor().getChangedObject(notification);

        // if( logDebug )
        //             UiConstants.Util.print(THIS_CLASS + ".performRemove() START:  TargetObject = "  + targetObject ); //$NON-NLS-1$

        if (targetObject instanceof DiagramContainer) {
            // get the added objects, get their target's and
            Collection refreshTargets = new HashSet();
            EObject targetEObject = null;
            EObject[] oldChildren = NotificationUtilities.getRemovedChildren(notification);
            for (int i = 0; i < oldChildren.length; i++) {
                targetEObject = ((Diagram)oldChildren[i]).getTarget();
                if (targetEObject != null) {
                    if (!(targetEObject instanceof ModelAnnotation)) {
                        refreshTargets.add(targetEObject);
                    }
                }
            }
            refreshTree(refreshTargets);
        } else if (targetObject != null && targetObject instanceof EObject && treeIsValid()) {
            // we know that the object is not a child of a model resource !!!!!

            // if( logDebug )
            //                 UiConstants.Util.print(THIS_CLASS + ".performRemove() TargetEObject = " + targetObject ); //$NON-NLS-1$

            // if an annotation then a ModelImport was deleted. getParent(ModelAnnotation) gets the model so
            // refresh the model.
            if (targetObject instanceof ModelAnnotation) {
                ITreeContentProvider cp = (ITreeContentProvider)treeViewer.getContentProvider();
                targetObject = cp.getParent(targetObject);
            }

            getTreeViewer().refresh(targetObject);
        }

        // if( logDebug )
        //             UiConstants.Util.print(THIS_CLASS + ".performRemove() END" ); //$NON-NLS-1$
    }

    private void performChange( Notification notification ) {
        Object targetObject = ModelerCore.getModelEditor().getChangedObject(notification);

        if (targetObject instanceof Resource) {
            ModelResource modelResource;
            modelResource = ModelUtilities.getModelResource((Resource)targetObject, false);
            if (modelResource == null || modelResource.getModelProject().isOpen()) {
                // don't process notifications on items in closed projects
                return;
            }
        }

        // if( logDebug )
        //             UiConstants.Util.print(THIS_CLASS + ".performChange() START:  TargetObject = "  + targetObject ); //$NON-NLS-1$

        if (targetObject != null && targetObject instanceof EObject && treeIsValid()) {

            // we know that the object is not a child of a model resource !!!!!
            // if( logDebug )
            //                UiConstants.Util.print(THIS_CLASS + ".performChange() TargetEObject = " + targetObject ); //$NON-NLS-1$
            if (targetObject instanceof Diagram) {
                EObject targetEObject = ((Diagram)targetObject).getTarget();
                if (!(targetEObject instanceof ModelAnnotation)) {
                    getTreeViewer().refresh(targetEObject);
                }

            } else {
                EObject parentObject = ((EObject)targetObject).eContainer();
                if (parentObject == null) {
                    if (!(targetObject instanceof ModelAnnotation)) {
                        getTreeViewer().refresh(null);
                    }
                } else {
                    getTreeViewer().refresh(parentObject);
                }
            }
        }

        // if( logDebug )
        //             UiConstants.Util.print(THIS_CLASS + ".performChange() END" ); //$NON-NLS-1$
    }

    protected TreeViewer getTreeViewer() {
        return this.treeViewer;
    }

    private boolean treeIsValid() {
        if (getTreeViewer() != null && getTreeViewer().getTree() != null && !getTreeViewer().getTree().isDisposed()) return true;

        return false;
    }
}
