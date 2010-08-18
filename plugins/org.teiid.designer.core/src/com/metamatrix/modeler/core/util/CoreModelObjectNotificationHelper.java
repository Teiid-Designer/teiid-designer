/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.eclipse.core.resources.IResource;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.metamodels.diagram.DiagramContainer;
import com.metamatrix.metamodels.diagram.DiagramEntity;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.notification.util.NotificationUtilities;
import com.metamatrix.modeler.core.transaction.SourcedNotification;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.workspace.ModelWorkspaceManager;
import com.metamatrix.modeler.internal.core.workspace.WorkspaceResourceFinderUtil;

/**
 * This class provides notification analysis and bundling. It will categorize all notifications, collect target objects and
 * filters out ignorable notifications.
 * 
 * @since 4.2
 */
public class CoreModelObjectNotificationHelper {
    private static final String NEW_LINE = "\n"; //$NON-NLS-1$

    protected Notification primaryNotification = null;
    protected HashSet addOrRemoveTargets = null;
    protected HashSet changeTargets = null;
    protected HashSet changedModels = null;
    protected HashSet modifiedResources = null;
    protected HashSet addedChildren;
    protected boolean modelChildrenChanged = false;
    protected List leftoverNotifications = null;
    protected boolean handleNotification = false;
    protected boolean isDiagramOnlyNotification = true;

    /**
     * @since 4.2
     */
    public CoreModelObjectNotificationHelper( Notification notification ) {
        super();
        this.primaryNotification = notification;
        init();
    }

    private void init() {
        addOrRemoveTargets = new HashSet();
        addedChildren = new HashSet();
        changeTargets = new HashSet();
        changedModels = new HashSet();
        modifiedResources = new HashSet();
        leftoverNotifications = new ArrayList();
        setHandleNotification();

        filterTargets();
    }

    private void setHandleNotification() {
        handleNotification = true;
    }

    /**
     * @return Returns the handleNotification.
     * @since 4.2
     */
    public boolean shouldHandleNotification() {
        return this.handleNotification;
    }

    protected void filterTargets() {
        addOrRemoveTargets.clear();
        changeTargets.clear();
        changedModels.clear();
        modelChildrenChanged = false;
        leftoverNotifications.clear();

        if (primaryNotification instanceof SourcedNotification) {
            // Walk through the notifications.
            // and determine we need to reconcile target attributes (bail on check if already set)
            // determine if any "source" tables need to be reconciled (don't bail on check if already set)
            // Add all tables to reconcileTables list
            Collection notifications = ((SourcedNotification)primaryNotification).getNotifications();
            Iterator iter = notifications.iterator();
            Notification notification = null;
            while (iter.hasNext()) {

                notification = (Notification)iter.next();
                Object targetObject = ModelerCore.getModelEditor().getChangedObject(notification);
                if (targetObject instanceof DiagramContainer || targetObject instanceof ModelAnnotation) {
                    leftoverNotifications.add(notification);
                }

                boolean targetIsResource = targetObject instanceof Resource;

                EObject targetEObject = getEObjectTarget(notification);

                if (targetEObject != null) {
                    addToModifiedResources(targetEObject);

                    if (NotificationUtilities.isAdded(notification)) {

                        handleAddOrRemove(notification, targetEObject);
                        EObject[] newValues = NotificationUtilities.getAddedChildren(notification);
                        if (newValues.length > 0) {
                            addedChildren.addAll(Arrays.asList(newValues));
                            // Check ignorable targets
                            checkNonDiagramRelatedChange(newValues, false);
                        } // endif
                    } else if (NotificationUtilities.isRemoved(notification)) {
                        handleAddOrRemove(notification, targetEObject);
                        // Check ignorable target
                        checkNonDiagramRelatedChange(targetEObject, false);
                    } else if (NotificationUtilities.isChanged(notification)) {
                        handleChanged(notification, targetEObject);
                        // Check ignorable target
                        checkNonDiagramRelatedChange(targetEObject, true);
                    }
                } else if (targetIsResource) {
                    if (NotificationUtilities.isAdded(notification)) {

                        handleAddOrRemove(notification, targetEObject);
                        EObject[] newValues = NotificationUtilities.getAddedChildren(notification);
                        if (newValues.length > 0) {
                            addedChildren.addAll(Arrays.asList(newValues));
                            // Check ignorable targets
                            checkNonDiagramRelatedChange(newValues, false);
                        } // endif
                    } else if (NotificationUtilities.isRemoved(notification)) {
                        handleAddOrRemove(notification, targetEObject);
                        // Check ignorable target
                        checkNonDiagramRelatedChange(targetEObject, false);
                    } else if (NotificationUtilities.isChanged(notification)) {
                        handleChanged(notification, targetEObject);
                        // Check ignorable target
                        checkNonDiagramRelatedChange(targetEObject, true);
                    }

                    // call findIResource since this does NOT open the model
                    IResource resource = WorkspaceResourceFinderUtil.findIResource((Resource)targetObject);

                    if ((resource != null) && ModelWorkspaceManager.getModelWorkspaceManager().isModelOpen(resource)) {
                        modelChildrenChanged = true;
                        changedModels.add(resource);
                        addToModifiedResources(resource);
                    }
                }
            }
        } else {
            Object targetObject = ModelerCore.getModelEditor().getChangedObject(primaryNotification);
            if (targetObject instanceof DiagramContainer || targetObject instanceof ModelAnnotation) {
                leftoverNotifications.add(primaryNotification);
            }

            EObject targetEObject = getEObjectTarget(primaryNotification);

            if (NotificationUtilities.isAdded(primaryNotification) || NotificationUtilities.isRemoved(primaryNotification)) {
                if (targetEObject != null) {
                    addOrRemoveTargets.add(targetEObject);
                    // Check ignorable target
                    checkNonDiagramRelatedChange(targetEObject, false);
                }
            } else if (NotificationUtilities.isChanged(primaryNotification)) {
                if (targetEObject != null) {
                    changeTargets.add(targetEObject);
                    // Check ignorable target
                    checkNonDiagramRelatedChange(targetEObject, true);
                }
            }
        }
    }

    protected void addToModifiedResources( Object someObject ) {
        IResource iResource = null;
        try {
            if (someObject instanceof EObject) {
                ModelResource mr = ModelerCore.getModelEditor().findModelResource((EObject)someObject);
                if (mr != null) {
                    iResource = mr.getUnderlyingResource();
                }
            } else if (someObject instanceof Resource) {
                ModelResource mr = ModelerCore.getModelEditor().findModelResource((Resource)someObject);
                if (mr != null) {
                    iResource = mr.getUnderlyingResource();
                }
            } else if (someObject instanceof IResource) {
                iResource = (IResource)someObject;
            } else if (someObject instanceof ModelResource) {
                iResource = ((ModelResource)someObject).getUnderlyingResource();
            }
        } catch (ModelWorkspaceException error) {

        }

        if (iResource != null) {
            modifiedResources.add(iResource);
        }
    }

    /**
     * @param notification
     * @param targetEObject
     */
    protected void handleAddOrRemove( Notification notification,
                                      EObject targetEObject ) {
        if (targetEObject != null) addOrRemoveTargets.add(targetEObject);
        else leftoverNotifications.add(notification);
    }

    /**
     * @param notification
     * @param targetEObject
     */
    protected void handleChanged( Notification notification,
                                  EObject targetEObject ) {
        if (targetEObject != null) changeTargets.add(targetEObject);
        else leftoverNotifications.add(notification);
    }

    protected EObject getEObjectTarget( Notification notification ) {
        Object targetObject = ModelerCore.getModelEditor().getChangedObject(notification);
        if (targetObject instanceof EObject) return (EObject)targetObject;
        return null;
    }

    /**
     * @return List of the addOrRemoveTargets.
     * @since 4.2
     */
    public List getAddOrRemoveTargets() {
        return new ArrayList(this.addOrRemoveTargets);
    }

    /**
     * @return List of the changed targets
     */
    public List getChangeTargets() {
        return new ArrayList(this.changeTargets);
    }

    /**
     * @return List of the changeModels.
     * @since 4.2
     */
    public List getChangeModels() {
        return new ArrayList(this.changedModels);
    }

    /**
     * @return Returns the movedChildrenChanged.
     * @since 4.2
     */
    public boolean getModelChildrenChanged() {
        return modelChildrenChanged;
    }

    /**
     * @return List of the leftoverNotifications.
     * @since 4.2
     */
    public List getLeftoverNotifications() {
        return leftoverNotifications;
    }

    /**
     * @return Set of the added children
     */
    public Set getAddedChildren() {
        return addedChildren;
    }

    /**
     * @return List of the modified resources.
     * @since 4.2
     */
    public List getModifiedResources() {
        return new ArrayList(this.modifiedResources);
    }

    /*
     * Method which keeps track of whether or not any non-diagram objects are the "targets" for notifications
     * This knowledge can be used by 
     * @param obj
     * @param isSet
     */
    private void checkNonDiagramRelatedChange( Object obj,
                                               boolean isSet ) {
        // Only do the check if still non-diagram notification
        if (isDiagramOnlyNotification && obj != null) {
            if (obj instanceof DiagramEntity) {
                isDiagramOnlyNotification = true;
            } else if (obj instanceof Diagram && isSet) {
                // This case is for diagram name change notifications (i.e. Custom Diagrams)
                isDiagramOnlyNotification = false;
            } else if (obj instanceof List) {
                for (Iterator iter = ((List)obj).iterator(); iter.hasNext();) {
                    Object nextObj = iter.next();
                    if (!(nextObj instanceof Diagram || nextObj instanceof DiagramEntity)) {
                        isDiagramOnlyNotification = false;
                    }
                    if (!isDiagramOnlyNotification) {
                        break;
                    }
                }
            }
        }
    }

    /**
     * Assesses whether or not all the changes from the notifications are ignorable.
     * 
     * @return boolean true if ignorable, false if not
     */
    public boolean allChangesAreIgnorable() {
        if (isDiagramOnlyNotification) {
            // Do a check to see if there are any added/removed/changed targets
            if (addedChildren.isEmpty() && addOrRemoveTargets.isEmpty() && changeTargets.isEmpty()) {
                return true;
            }
            return false;
        }
        return isDiagramOnlyNotification;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(this.getClass().getName() + NEW_LINE);

        sb.append("   # added children        = " + this.addedChildren.size() + NEW_LINE); //$NON-NLS-1$
        sb.append("   # add/remove targets    = " + this.addOrRemoveTargets.size() + NEW_LINE); //$NON-NLS-1$
        sb.append("   # changed targets       = " + this.changeTargets.size() + NEW_LINE); //$NON-NLS-1$
        sb.append("   # changed models        = " + this.changedModels.size() + NEW_LINE); //$NON-NLS-1$
        sb.append("   # changed resources     = " + this.modifiedResources.size() + NEW_LINE); //$NON-NLS-1$
        sb.append("   Model Children Changed  = " + this.modelChildrenChanged + NEW_LINE); //$NON-NLS-1$
        sb.append("   All Changes to Ignorable= " + this.allChangesAreIgnorable() + NEW_LINE); //$NON-NLS-1$

        return sb.toString();
    }
}
