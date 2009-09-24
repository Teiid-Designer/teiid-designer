/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.mapping.ui.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.provider.INotifyChangedListener;
import com.metamatrix.metamodels.transformation.InputBinding;
import com.metamatrix.metamodels.transformation.MappingClassSet;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.notification.util.NotificationUtilities;
import com.metamatrix.modeler.core.transaction.SourcedNotification;
import com.metamatrix.modeler.core.workspace.ModelMappingClassSets;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.mapping.factory.ModelMapperFactory;
import com.metamatrix.modeler.internal.mapping.factory.TreeMappingAdapter;
import com.metamatrix.modeler.internal.ui.undo.ModelerUndoManager;
import com.metamatrix.modeler.mapping.PluginConstants;
import com.metamatrix.modeler.mapping.ui.UiConstants;
import com.metamatrix.ui.internal.InternalUiConstants;

/**
 * MappingNotificationListener The notification listener is instantiated upon initialization of the plugin, and registered as a
 * Notification listener. The listener responds to the following notifications XML Document Nodes o Need to reconcile the mapping
 * class set and tree root mappings when we get a remove notification. (i.e. delete any unmapped mapping classes (modeler-side
 * cleanup) This class provides a way for the mapping world to listen for any kind of notification which it may need to clean up
 * stuff behind the scenes. In particular, when a document node is deleted, there may be mapping classes that hang around and mess
 * up the model... Initial implementation 1/15/04 BML
 */
public class MappingNotificationListener implements INotifyChangedListener {
    private static final String THIS_CLASS = "MappingNotificationListener"; //$NON-NLS-1$
    private static final boolean NOT_SIGNIFICANT = false;
    private static final boolean IS_UNDOABLE = true;

    /**
     * Construct an instance of TransformationNotificationListener.
     */
    public MappingNotificationListener() {
        super();
    }

    /* (non-Javadoc)
     * @see org.eclipse.emf.edit.provider.INotifyChangedListener#notifyChanged(org.eclipse.emf.common.notify.Notification)
     */
    public void notifyChanged( Notification notification ) {

        if (notification instanceof SourcedNotification) {
            if (isValidSource((SourcedNotification)notification) && shouldHandleNotification(notification)) {
                UiConstants.Util.start("MappingNotificationListener.notifyChanged()", InternalUiConstants.Debug.Metrics.NOTIFICATIONS); //$NON-NLS-1$
                boolean requiredStart = false;
                boolean succeeded = false;
                try {
                    // -------------------------------------------------
                    // Let's wrap this in a transaction!!!
                    // will result in only one transaction?
                    // -------------------------------------------------

                    requiredStart = ModelerCore.startTxn(NOT_SIGNIFICANT, IS_UNDOABLE, "Reconcile Mapping Class Set", this); //$NON-NLS-1$$

                    handleNotification(notification);
                    succeeded = true;
                } catch (ModelerCoreException ex) {
                    PluginConstants.Util.log(IStatus.ERROR, ex, ex.getClass().getName() + ":" + THIS_CLASS + ".notifyModel()"); //$NON-NLS-1$  //$NON-NLS-2$
                } finally {
                    if (requiredStart) {
                        if (succeeded) {
                            ModelerCore.commitTxn();
                        } else {
                            ModelerCore.rollbackTxn();
                        }
                    }
                }
                UiConstants.Util.stop("MappingNotificationListener.notifyChanged()", InternalUiConstants.Debug.Metrics.NOTIFICATIONS); //$NON-NLS-1$
            }
        } else {
            if (shouldHandleNotification(notification)) {
                UiConstants.Util.start("MappingNotificationListener.notifyChanged()", InternalUiConstants.Debug.Metrics.NOTIFICATIONS); //$NON-NLS-1$
                boolean requiredStart = false;
                boolean succeeded = false;
                try {
                    // -------------------------------------------------
                    // Let's wrap this in a transaction!!!
                    // will result in only one transaction?
                    // -------------------------------------------------

                    requiredStart = ModelerCore.startTxn(NOT_SIGNIFICANT, IS_UNDOABLE, "Reconcile Mapping Class Set", this); //$NON-NLS-1$$

                    handleNotification(notification);
                    succeeded = true;
                } catch (ModelerCoreException ex) {
                    PluginConstants.Util.log(IStatus.ERROR, ex, ex.getClass().getName() + ":" + THIS_CLASS + ".notifyModel()"); //$NON-NLS-1$  //$NON-NLS-2$
                } finally {
                    if (requiredStart) {
                        if (succeeded) {
                            ModelerCore.commitTxn();
                        } else {
                            ModelerCore.rollbackTxn();
                        }
                    }
                }
                UiConstants.Util.stop("MappingNotificationListener.notifyChanged()", InternalUiConstants.Debug.Metrics.NOTIFICATIONS); //$NON-NLS-1$
            }
        }

    }

    /* 
     * Notifications handler.  Gathers all like notifications and handles them together.
     * Only the relevant notifications will be processed by this listener.
     * @param notifications the collection of all notifications
     */
    private void handleNotification( Notification notification ) throws ModelerCoreException {

        if (notification instanceof SourcedNotification) {
            boolean handled = false;
            Object source = ((SourcedNotification)notification).getSource();
            if (source == null || !source.equals(this)) {
                Collection notifications = ((SourcedNotification)notification).getNotifications();
                Iterator iter = notifications.iterator();
                Notification nextNotification = null;

                while (iter.hasNext() && !handled) {
                    nextNotification = (Notification)iter.next();

                    Object targetObject = ModelerCore.getModelEditor().getChangedObject(nextNotification);
                    if (targetObject != null && targetObject instanceof EObject) {
                        if (ModelMapperFactory.isXmlTreeNode((EObject)targetObject)) {
                            if (nextNotification.getEventType() == Notification.REMOVE
                                || nextNotification.getEventType() == Notification.REMOVE_MANY) {
                                handleRemoveNotification((EObject)targetObject);
                                handled = true;
                            } else if (NotificationUtilities.isRemoved(nextNotification)) {
                                handleRemoveNotification((EObject)targetObject);
                                handled = true;
                            }
                        }
                    }
                }
            }
        } else { // SINGLE NOTIFICATION
            Object targetObject = ModelerCore.getModelEditor().getChangedObject(notification);
            if (targetObject != null && targetObject instanceof EObject) {
                if (ModelMapperFactory.isXmlTreeNode((EObject)targetObject)) {
                    if (notification.getEventType() == Notification.REMOVE
                        || notification.getEventType() == Notification.REMOVE_MANY) {
                        handleRemoveNotification((EObject)targetObject);
                    }
                }
            }
        }
    }

    /**
     * Changed this method to isValidSource() so we could check if the source is the <code>ModelerUndoManager</code> this was
     * causing additional work being done during the "Undo" that shouldn't have to be done. SEE Defect 18433
     * 
     * @param sn
     * @return
     * @since 4.3
     */
    private boolean isValidSource( SourcedNotification sn ) {
        boolean valid = true;
        Object source = sn.getSource();

        if (source != null) {
            if (source.equals(this) || source instanceof ModelerUndoManager) valid = false;
        }

        return valid;
    }

    private boolean shouldHandleNotification( Notification notification ) {
        boolean shouldHandle = false;

        if (notification instanceof SourcedNotification) {
            Object source = ((SourcedNotification)notification).getSource();
            if (source == null || !source.equals(this)) {
                Collection notifications = ((SourcedNotification)notification).getNotifications();
                Iterator iter = notifications.iterator();
                Notification nextNotification = null;

                while (iter.hasNext() && !shouldHandle) {
                    nextNotification = (Notification)iter.next();

                    Object targetObject = ModelerCore.getModelEditor().getChangedObject(nextNotification);
                    if (targetObject != null && targetObject instanceof EObject) {
                        if (ModelMapperFactory.isXmlTreeNode((EObject)targetObject)) {
                            if (nextNotification.getEventType() == Notification.REMOVE
                                || nextNotification.getEventType() == Notification.REMOVE_MANY) {
                                shouldHandle = true;
                            } else if (NotificationUtilities.isRemoved(nextNotification)) {
                                shouldHandle = true;
                            }
                        }
                    }
                }
            }
        } else { // SINGLE NOTIFICATION
            Object targetObject = ModelerCore.getModelEditor().getChangedObject(notification);
            if (targetObject != null && targetObject instanceof EObject) {
                if (ModelMapperFactory.isXmlTreeNode((EObject)targetObject)) {
                    if (notification.getEventType() == Notification.REMOVE
                        || notification.getEventType() == Notification.REMOVE_MANY) {
                        shouldHandle = true;
                    } else if (NotificationUtilities.isRemoved(notification)) {
                        shouldHandle = true;
                    }
                }
            }
        }

        return shouldHandle;
    }

    private void handleRemoveNotification( EObject targetEObject ) throws ModelerCoreException {

        // Find it's tree root!!
        EObject treeRoot = ModelMapperFactory.getTreeRoot(targetEObject);
        if (treeRoot != null) {
            // get the TreeMappingRoot??
            // We have the tree root, now let's get two things, 1) Mapping Transformation Roots and 2) Mapping Class Set
            TreeMappingAdapter tma = new TreeMappingAdapter(treeRoot);

            List currentMappingClasses = tma.getAllMappingClasses();
            List allStagingTables = tma.getAllStagingTables();

            EObject document = tma.getDocument();
            List mappingClasses = null;
            if (document != null) {
                MappingClassSet mcs = getMappingClassSet(treeRoot);
                if (mcs != null) {
                    mappingClasses = new ArrayList(mcs.eContents());
                }
            } else {
                // Had to add back in this case because if you delete the xml document4 root
                // then there getDocument() returns null,when the tree root is really the document.
                // get it???
                MappingClassSet mcs = getMappingClassSet(treeRoot);
                if (mcs != null) {
                    mappingClasses = new ArrayList(mcs.eContents());
                }
            }

            if (mappingClasses != null && !mappingClasses.isEmpty()) {

                Object nextObj = null;
                Iterator iter = currentMappingClasses.iterator();
                while (iter.hasNext()) {
                    nextObj = iter.next();
                    if (nextObj instanceof EObject) {
                        if (mappingClasses.contains(nextObj)) mappingClasses.remove(nextObj);
                    }
                }
                iter = allStagingTables.iterator();
                while (iter.hasNext()) {
                    nextObj = iter.next();
                    if (nextObj instanceof EObject) {
                        if (mappingClasses.contains(nextObj)) mappingClasses.remove(nextObj);
                    }
                }
                if (!mappingClasses.isEmpty()) {
                    // Call delete now!! finally.
                    iter = mappingClasses.iterator();
                    while (iter.hasNext()) {
                        nextObj = iter.next();
                        if (nextObj instanceof EObject && !(nextObj instanceof InputBinding)) {
                            ModelerCore.getModelEditor().delete((EObject)nextObj);
                        }
                    }
                }
            }
        }
    }

    private MappingClassSet getMappingClassSet( EObject treeRoot ) {
        MappingClassSet mappingClassSet = null;

        ModelResource mr = ModelerCore.getModelEditor().findModelResource(treeRoot);

        if (mr != null) {
            try {
                ModelMappingClassSets sets = mr.getModelMappingClassSets();
                List setList = sets.getMappingClassSets(treeRoot);
                if (setList == null || setList.isEmpty()) {
                    mappingClassSet = sets.createNewMappingClassSet(treeRoot);
                } else {
                    mappingClassSet = (MappingClassSet)setList.get(0);
                }
            } catch (ModelWorkspaceException e) {
                PluginConstants.Util.log(e);
            }
        }

        return mappingClassSet;
    }
}
