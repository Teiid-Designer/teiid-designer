/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

package com.metamatrix.modeler.internal.ui.table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.impl.EStringToStringMapEntryImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.Resource.Diagnostic;
import org.eclipse.emf.edit.provider.INotifyChangedListener;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.xsd.XSDFacet;
import com.metamatrix.metamodels.core.Annotation;
import com.metamatrix.metamodels.core.AnnotationContainer;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.metamodel.MetamodelDescriptor;
import com.metamatrix.modeler.core.notification.util.NotificationUtilities;
import com.metamatrix.modeler.core.transaction.SourcedNotification;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.internal.ui.wizards.NewModelWizard;
import com.metamatrix.modeler.ui.UiConstants;

/**
 * TableNotificationHandler
 */
public class TableNotificationHandler implements INotifyChangedListener, UiConstants {

    /** the ModelTableEditor that created this handler */
    private ModelTableEditor tableEditor;

    /** the EMF Resource that this handler's TreeView is displaying */
    private Resource emfResource;

    /** key = EClass, value = TableViewer */
    private HashMap viewersByClass = new HashMap();

    /** key = ModelObjectTableModel, value = TableViewer */
    private HashMap modelsByClass = new HashMap();

    /** Collection of <code>Class</code>es that should not be added as a tabe to the table editor. */
    private List excludedTypes = new ArrayList();

    TableNotificationHandler( ModelTableEditor editor ) {
        this.tableEditor = editor;
        this.emfResource = editor.getEmfResource();
    }

    public void addTable( EClass eClass,
                          TableViewer tableViewer,
                          ModelObjectTableModel model ) {
        viewersByClass.put(eClass, tableViewer);
        modelsByClass.put(eClass, model);
    }

    /* (non-Javadoc)
      * @see org.eclipse.emf.edit.provider.INotifyChangedListener#notifyChanged(org.eclipse.emf.common.notify.Notification)
      */
    public void notifyChanged( final Notification notification ) {
        // determine if the notification is within the scope of the table
        // jh 10/24: to handle SourcedNotifications correctly, I am moving calls to
        // checkNotification to the handleNotification method.

        handleNotification(notification);
    }

    /**
     * Determine if the specified notification is within the scope of this table and should therefore be handled.
     * 
     * @param notification
     * @return
     */
    private boolean checkNotification( Notification notification ) {
        // get the target of the notification
        EObject target = NotificationUtilities.getEObject(notification);

        if (target == null) {
            // might have been an add or remove beneath the root
            if (NotificationUtilities.isAdded(notification)) {
                EObject[] children = NotificationUtilities.getAddedChildren(notification);
                if (children != null && children.length > 0) {
                    target = children[0];
                }
            } else if (NotificationUtilities.isRemoved(notification)) {
                EObject[] children = NotificationUtilities.getRemovedChildren(notification);
                if (children != null && children.length > 0) {
                    target = children[0];
                }
            }

        }

        if (target != null) {
            // get target resource in order to compare with this editor's resource
            Resource resource = target.eResource();
            if (resource == null && NotificationUtilities.isRemoved(notification)) {
                final Object notifier = notification.getNotifier();
                if (notifier instanceof Resource) {
                    resource = (Resource)notifier;
                } else if (notifier instanceof EObject) {
                    resource = ((EObject)notifier).eResource();
                }
            }

            return ((resource != null) && resource.equals(this.emfResource));
        }
        return false;

    }

    /**
     * Refresh this object's ModelTableEditor as necessary to reflect the specified notification.
     * 
     * @param notification
     */
    private void handleNotification( final Notification notification ) {

        // Defect 14521: To avoid duplicate entries when responding to Notifications from a newly
        // created model, bail out if the source is a NewModelWizard.
        if (notification instanceof SourcedNotification
            && ((SourcedNotification)notification).getSource() instanceof NewModelWizard) {
            return;
        }

        if (notification instanceof SourcedNotification) {
            Collection notifications = ((SourcedNotification)notification).getNotifications();
            Iterator iter = notifications.iterator();
            List deleteNotificationsToProcess = new ArrayList();
            List nonDeleteNotificationsToProcess = new ArrayList();
            while (iter.hasNext()) {
                Notification ntfTemp = (Notification)iter.next();

                if (checkNotification(ntfTemp)) {
                    if (NotificationUtilities.isRemoved(ntfTemp)) {
                        deleteNotificationsToProcess.add(ntfTemp);
                    } else {
                        nonDeleteNotificationsToProcess.add(ntfTemp);
                    }

                }
            }
            if (nonDeleteNotificationsToProcess.size() > 0 || deleteNotificationsToProcess.size() > 0) {
                // System.out.println("------------Start Notifications-------");
                // System.out.println("Non Delete Notifier Count = " + nonDeleteNotificationsToProcess.size());
                // System.out.println("Delete Notifier Count = " + deleteNotificationsToProcess.size());
                // long start = System.currentTimeMillis();
                if (nonDeleteNotificationsToProcess.size() > 0) {
                    for (int i = 0; i < nonDeleteNotificationsToProcess.size(); i++) {
                        handleSingleNotification((Notification)nonDeleteNotificationsToProcess.get(i));
                    }
                }
                if (deleteNotificationsToProcess.size() > 0) {
                    for (int i = 0; i < deleteNotificationsToProcess.size(); i++) {
                        handleSingleNotification((Notification)deleteNotificationsToProcess.get(i));
                    }
                }
                // long elapsed = System.currentTimeMillis() - start;
                // System.out.println("Elapsed Time = " + elapsed);
                // System.out.println("------------End Notifications-------");
            }
        } else {
            if (checkNotification(notification)) {
                // System.out.println("------------Start Notifications-------");
                // long start = System.currentTimeMillis();
                handleSingleNotification(notification);
                // long elapsed = System.currentTimeMillis() - start;
                // System.out.println("Elapsed Time = " + elapsed);
                // System.out.println("------------End Notifications-------");
            }
        }

    }

    /**
     * Refresh this object's ModelTableEditor as necessary to reflect the specified notification.
     * 
     * @param notification
     */
    private void handleSingleNotification( final Notification notification ) {
        if (Util.isDebugEnabled("tableEditorNotification")) { //$NON-NLS-1$
            Util.debug("tableEditorNotification", //$NON-NLS-1$
                       "TableNotificationHandler got a notifyChanged( ) " + NotificationUtilities.paramString(notification)); //$NON-NLS-1$
        }
        if (NotificationUtilities.isEObjectNotifier(notification)) {
            handleSingleEObjectNotification(notification);
        } else {
            handleSingleResourceNotification(notification);
        }
    }

    /**
     * Refresh this object's ModelTableEditor as necessary to reflect the specified notification.
     * 
     * @param notification
     */
    private void handleSingleEObjectNotification( final Notification notification ) {
        if (Util.isDebugEnabled("tableEditorNotification")) { //$NON-NLS-1$
            Util.debug("tableEditorNotification", //$NON-NLS-1$
                       "TableNotificationHandler got a notifyChanged( ) " + NotificationUtilities.paramString(notification)); //$NON-NLS-1$
        }

        EObject target = NotificationUtilities.getEObject(notification);

        if (target instanceof EStringToStringMapEntryImpl) {
            target = target.eContainer();
        }

        if (target instanceof AnnotationContainer) {
            refreshAnnotations((AnnotationContainer)target, notification);
        } else if (target instanceof Annotation) {
            refreshAnnotation((Annotation)target, notification);
        } else {
            if (NotificationUtilities.isAdded(notification)) {
                EObject[] addedChildren = NotificationUtilities.getAddedChildren(notification);
                if (addedChildren != null && addedChildren.length > 0) {
                    HashMap typeMap = sortObjectsByType(addedChildren);
                    // walk through the EClass types and distribute the new objects to the correct tables
                    for (Iterator iter = typeMap.keySet().iterator(); iter.hasNext();) {
                        EClass eClass = (EClass)iter.next();
                        ModelObjectTableModel model = (ModelObjectTableModel)modelsByClass.get(eClass);
                        if (model != null) {
                            // found the table for this type. add the rows.
                            model.addRows((ArrayList)typeMap.get(eClass));
                        } else {
                            // no table was found for this type.
                            // check to see if the type is of the PrimaryMetamodel
                            EObject anObject = (EObject)((ArrayList)typeMap.get(eClass)).get(0);
                            MetamodelDescriptor descriptor = ModelerCore.getModelEditor().getMetamodelDescriptor(anObject);
                            if (descriptor != null && descriptor.isPrimary()) {
                                // there is not a tab for this type: add it to the table
                                if (anObject instanceof Diagnostic) {
                                    // screen these out - they are just "noteworthy issues" from EMF
                                } else if (isObjectTypeAddable(anObject) && tableEditor.canAddTable(anObject)) {
                                    tableEditor.addTable(eClass, (ArrayList)typeMap.get(eClass));
                                }
                            }

                        }
                    }
                }
            }

            else if (NotificationUtilities.isRemoved(notification)) {
                EObject[] removedChildren = NotificationUtilities.getRemovedChildren(notification);
                for (int i = 0; i < removedChildren.length; ++i) {

                    HashMap typeMap = sortObjectsByType(removedChildren);
                    for (Iterator iter = typeMap.keySet().iterator(); iter.hasNext();) {
                        EClass eClass = (EClass)iter.next();
                        ModelObjectTableModel model = (ModelObjectTableModel)modelsByClass.get(eClass);
                        if (model != null) {
                            model.removeRows((ArrayList)typeMap.get(eClass));
                        }
                    }
                }
            }

            else if (NotificationUtilities.isChanged(notification)) {
                // refresh target
                TableViewer viewer = (TableViewer)viewersByClass.get(target.eClass());
                if (viewer != null && !viewer.getTable().isDisposed()) {
                    ModelObjectTableModel model = (ModelObjectTableModel)modelsByClass.get(target.eClass());
                    Object row = model.getRowElementForInstance(target);
                    if (row != null) {
                        viewer.refresh(row);
                    }
                }

                // refresh target's children. if the target's name changed the location property of
                // the children needs to be updated
                Object[] kids = ModelUtilities.getModelContentProvider().getChildren(target);

                if ((kids != null) && (kids.length > 0)) {
                    for (int i = 0; i < kids.length; i++) {
                        if (kids[i] instanceof EObject) {
                            EClass childClass = ((EObject)kids[i]).eClass();
                            viewer = (TableViewer)viewersByClass.get(childClass);

                            if ((viewer != null) && !viewer.getTable().isDisposed()) {
                                ModelObjectTableModel model = (ModelObjectTableModel)modelsByClass.get(childClass);
                                Object row = model.getRowElementForInstance((EObject)kids[i]);

                                if (row != null) {
                                    viewer.refresh(row);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Indicates if the type of the specified object can be added to the table.
     * 
     * @param theObject the object whose type is being checked
     * @return <code>true</code>if the type can be added; <code>false</code> otherwise.
     * @since 4.2
     */
    private boolean isObjectTypeAddable( Object theObject ) {
        boolean result = false;
        Class type = theObject.getClass();

        if (!excludedTypes.contains(type)) {
            if (theObject instanceof XSDFacet) {
                this.excludedTypes.add(type);
            } else {
                result = true;
            }
        }

        return result;
    }

    /**
     * When the first <code>Annotation</code> is added the notification received is for the adding of the
     * <code>AnnotationContainer</code>. You will not receive notifications for the new <code>Annotation</code>. So here we must
     * check to see if the container has any.
     */
    private void refreshAnnotations( AnnotationContainer theAnnotationContainer,
                                     Notification theNotification ) {
        Annotation annotation;
        EObject annotatedEObject = null;

        if (NotificationUtilities.isRemoved(theNotification)) {
            annotation = (Annotation)theNotification.getOldValue();
        } else {
            annotation = (Annotation)theNotification.getNewValue();
        }

        annotatedEObject = annotation.getAnnotatedObject();
        if (annotatedEObject == null) {
            return;
        }
        // if (NotificationUtilities.isRemoved(theNotification)) {
        TableViewer viewer = (TableViewer)viewersByClass.get(annotatedEObject.eClass());
        if (viewer != null) {
            if (!viewer.getTable().isDisposed()) {
                ModelObjectTableModel model = (ModelObjectTableModel)modelsByClass.get(annotatedEObject.eClass());
                Object row = model.getRowElementForInstance(annotatedEObject);
                if (row != null) {
                    viewer.refresh(row);
                }
            }
        }
    }

    /**
     * When the first <code>Annotation</code> is added the notification received is for the adding of the
     * <code>AnnotationContainer</code>. You will not receive notifications for the new <code>Annotation</code>. So here we must
     * check to see if the container has any.
     */
    private void refreshAnnotations( AnnotationContainer theAnnotationContainer ) {
        EObject annotatedEObject = null;

        // We are assuming here that we won't get a REMOVE notification for the AnnotationContainer
        // We also assume that if we get an ADD for the container, then we just need to get it's contents
        // and find each annotated object.

        List annotations = theAnnotationContainer.getAnnotations();

        for (Iterator iter = annotations.iterator(); iter.hasNext();) {
            annotatedEObject = ((Annotation)iter.next()).getAnnotatedObject();

            // if (NotificationUtilities.isRemoved(theNotification)) {
            TableViewer viewer = (TableViewer)viewersByClass.get(annotatedEObject.eClass());
            if (viewer != null) {
                if (!viewer.getTable().isDisposed()) {
                    ModelObjectTableModel model = (ModelObjectTableModel)modelsByClass.get(annotatedEObject.eClass());
                    Object row = model.getRowElementForInstance(annotatedEObject);
                    if (row != null) {
                        viewer.refresh(row);
                    }
                }
            }
        }
    }

    private void refreshAnnotation( Annotation annotation,
                                    Notification theNotification ) {
        EObject annotatedEObject = annotation.getAnnotatedObject();
        if (annotatedEObject == null) {
            return;
        }
        // if (NotificationUtilities.isRemoved(theNotification)) {
        TableViewer viewer = (TableViewer)viewersByClass.get(annotatedEObject.eClass());
        if (viewer != null) {
            if (!viewer.getTable().isDisposed()) {
                ModelObjectTableModel model = (ModelObjectTableModel)modelsByClass.get(annotatedEObject.eClass());
                Object row = model.getRowElementForInstance(annotatedEObject);
                if (row != null) {
                    viewer.refresh(row);
                }
            }
        }
    }

    /**
     * Refresh this object's ModelTableEditor as necessary to reflect the specified notification.
     * 
     * @param notification
     */
    private void handleSingleResourceNotification( final Notification notification ) {

        if (Util.isDebugEnabled("tableEditorNotification")) { //$NON-NLS-1$
            Util.debug("tableEditorNotification", //$NON-NLS-1$
                       "TableNotificationHandler got a notifyChanged( ) " + NotificationUtilities.paramString(notification)); //$NON-NLS-1$
        }

        if (NotificationUtilities.isAdded(notification)) {
            EObject[] addedChildren = NotificationUtilities.getAddedChildren(notification);

            if (addedChildren != null && addedChildren.length > 0) {
                if (addedChildren[0] instanceof AnnotationContainer) {
                    refreshAnnotations((AnnotationContainer)addedChildren[0]);
                } else {
                    // changes for defect 13339 (the for loop is not required since the typeMap below
                    // contains all the objects in the addedChildren collection
                    // for (int i = 0; i < addedChildren.length; ++i) {
                    HashMap typeMap = sortObjectsByType(addedChildren);
                    // walk through the EClass types and distribute the new objects to the correct tables
                    for (Iterator iter = typeMap.keySet().iterator(); iter.hasNext();) {
                        EClass eClass = (EClass)iter.next();
                        ModelObjectTableModel model = (ModelObjectTableModel)modelsByClass.get(eClass);
                        if (model != null) {
                            // found the table for this type. add the rows.
                            model.addRows((ArrayList)typeMap.get(eClass));
                        } else {
                            // no table was found for this type.
                            // check to see if the type is of the PrimaryMetamodel
                            EObject anObject = (EObject)((ArrayList)typeMap.get(eClass)).get(0);
                            MetamodelDescriptor descriptor = ModelerCore.getModelEditor().getMetamodelDescriptor(anObject);
                            if (descriptor != null && descriptor.isPrimary()) {
                                // there is not a tab for this type: add it to the table
                                if (tableEditor.canAddTable(anObject)) {
                                    tableEditor.addTable(eClass, (ArrayList)typeMap.get(eClass));
                                }
                            }

                        }
                    }
                    // } changes for defect 13339
                }
            }
        } else if (NotificationUtilities.isRemoved(notification)) {
            EObject[] removedChildren = NotificationUtilities.getRemovedChildren(notification);
            if (removedChildren != null && removedChildren.length > 0) {
                for (int i = 0; i < removedChildren.length; ++i) {

                    HashMap typeMap = sortObjectsByType(removedChildren);
                    for (Iterator iter = typeMap.keySet().iterator(); iter.hasNext();) {
                        EClass eClass = (EClass)iter.next();
                        ModelObjectTableModel model = (ModelObjectTableModel)modelsByClass.get(eClass);
                        if (model != null) {
                            model.removeRows((ArrayList)typeMap.get(eClass));
                        }
                    }
                }
            }
        }

        // in this method, we do not have a target, so this code will now work.
        // //// if ( NotificationUtilities.isChanged(notification) ) {
        // ////
        // //// TableViewer viewer = (TableViewer)viewersByClass.get(target.eClass());
        // //// if (viewer != null && !viewer.getTable().isDisposed()) {
        // //// ModelObjectTableModel model = (ModelObjectTableModel) modelsByClass.get(target.eClass());
        // //// Object row = model.getRowElementForInstance(target);
        // //// if ( row != null ) {
        // //// viewer.refresh(row);
        // //// } else {
        // //// viewer.refresh();
        // //// }
        // //// }
        // ////
        // //// }

    }

    /**
     * Build a HashMap of the content of the specified Collection of EObjects recursively.
     * 
     * @param a Collection of EObject instances of various EClass types.
     * @return a HashMap of key=EClass types and value=ArrayList of EObjects of the key type.
     */
    private HashMap sortObjectsByType( EObject[] objectArray ) {
        HashMap result = new HashMap();
        for (int i = 0; i < objectArray.length; ++i) {
            loadObjectTypeMap(objectArray[i], result);
        }
        return result;
    }

    /**
     * Recursively fill the specified object HashMap beginning with the specified object.
     */
    private void loadObjectTypeMap( EObject o,
                                    HashMap objectTypeMap ) {
        if (o != null) {
            ArrayList list = (ArrayList)objectTypeMap.get(o.eClass());
            if (list == null) {
                list = new ArrayList();
                objectTypeMap.put(o.eClass(), list);
            }
            list.add(o);
            for (Iterator iter = o.eContents().iterator(); iter.hasNext();) {
                loadObjectTypeMap((EObject)iter.next(), objectTypeMap);
            }
        }
    }

}
