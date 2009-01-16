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
package com.metamatrix.modeler.webservice.ui.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.resources.IResource;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.provider.INotifyChangedListener;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.metamodels.transformation.SqlTransformation;
import com.metamatrix.metamodels.transformation.SqlTransformationMappingRoot;
import com.metamatrix.metamodels.webservice.Input;
import com.metamatrix.metamodels.webservice.Interface;
import com.metamatrix.metamodels.webservice.Operation;
import com.metamatrix.metamodels.webservice.Output;
import com.metamatrix.metamodels.webservice.WebServicePackage;
import com.metamatrix.metamodels.xml.XmlDocument;
import com.metamatrix.modeler.core.transaction.SourcedNotification;
import com.metamatrix.modeler.internal.transformation.util.SqlMappingRootCache;
import com.metamatrix.modeler.internal.transformation.util.TransformationHelper;
import com.metamatrix.modeler.internal.ui.undo.ModelerUndoManager;
import com.metamatrix.modeler.internal.ui.util.ModelObjectNotificationHelper;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelIdentifier;
import com.metamatrix.modeler.internal.webservice.ui.IInternalUiConstants;
import com.metamatrix.modeler.transformation.ui.PluginConstants;
import com.metamatrix.modeler.transformation.ui.UiPlugin;
import com.metamatrix.modeler.webservice.ui.editor.OperationObjectEditorPage;
import com.metamatrix.query.ui.sqleditor.SqlEditorPanel;
import com.metamatrix.ui.internal.util.UiUtil;

public class WebServiceNotificationListener implements INotifyChangedListener, IInternalUiConstants {

    private static final String I18N_PFX = I18nUtil.getPropertyPrefix(WebServiceNotificationListener.class);

    private static final String CONFIRM_SQLUPDATE_TITLE = UTIL.getString(I18N_PFX + "confirmSQLUpdateTitle"); //$NON-NLS-1$
    private static final String CONFIRM_SQLUPDATE_MSG = UTIL.getString(I18N_PFX + "confirmSQLUpdateMsg"); //$NON-NLS-1$

    // Required class variable that can be used in Runnable code to insure dialogs are placed in UI (SWT) thread
    boolean uiBooleanResult = false;

    /**
     * Notifications handler. Gathers all like notifications and handles them together. Only the relevant notifications will be
     * processed by this listener.
     * 
     * @param notifications the collection of all notifications
     * @param transactionSource
     */
    private void handleNotifications( Collection notifications,
                                      Object transactionSource ) {
        // Here's where we do some additional work if we detect something that needs to be done as a result of
        // the notification
        for (Iterator noteIter = notifications.iterator(); noteIter.hasNext();) {
            // NOTE: We are listening for ONE particular notification here. You can't use NotificationUtilities.isAdded()
            // method
            // because it doesn't pass the standard criteria. The "Source" added to the transformation isn't a container-based
            // add,
            // so we have to trust the notification.getEventType() and check the notifier and newValue objects
            Notification note = (Notification)noteIter.next();
            Object src = note.getNotifier();
            Object newVal = note.getNewValue();
            Object oldVal = note.getOldValue();
            switch (note.getEventType()) {
                case Notification.ADD: {
                    if (src instanceof Interface && newVal instanceof Operation) {
                        WebServiceUiUtil.initializeProcedure((Operation)newVal, transactionSource, false);
                    } else {
                        processChangedXmlDocumentAsSource(src, newVal, true, transactionSource);
                    }
                    break;
                }
                case Notification.REMOVE: {
                    processChangedXmlDocumentAsSource(src, oldVal, false, transactionSource);
                    break;
                }
                case Notification.SET: {
                    if (src instanceof Input && note.getFeatureID(Input.class) == WebServicePackage.INPUT__CONTENT_ELEMENT) {
                        boolean shouldReplace = true;
                        if (!(transactionSource instanceof OperationObjectEditorPage.MySqlPanelDropTargetListener)
                            && !(transactionSource instanceof SqlEditorPanel)) {
                            uiBooleanResult = false;

                            // put on SWT thread
                            UiUtil.runInSwtThread(new Runnable() {
                                public void run() {
                                    uiBooleanResult = confirmReplaceProcedure();
                                }
                            }, true);
                            shouldReplace = uiBooleanResult;
                        }
                        if (!(transactionSource instanceof SqlEditorPanel)) {
                            WebServiceUiUtil.initializeProcedure(((Input)src).getOperation(), transactionSource, shouldReplace);
                        }
                    } else if (src instanceof Output && note.getFeatureID(Output.class) == WebServicePackage.OUTPUT__XML_DOCUMENT) {
                        boolean shouldReplace = true;
                        if (!(transactionSource instanceof OperationObjectEditorPage.MySqlPanelDropTargetListener)
                            && !(transactionSource instanceof SqlEditorPanel)) {
                            uiBooleanResult = false;

                            // put on SWT thread
                            UiUtil.runInSwtThread(new Runnable() {
                                public void run() {
                                    uiBooleanResult = confirmReplaceProcedure();
                                }
                            }, true);
                            shouldReplace = uiBooleanResult;
                        }
                        if (!(transactionSource instanceof SqlEditorPanel)) {
                            WebServiceUiUtil.initializeProcedure(((Output)src).getOperation(), transactionSource, shouldReplace);
                        }
                    } else if (transactionSource instanceof SqlEditorPanel && src instanceof SqlTransformation) {
                        // If user sets the SQL to NULL, we need to assume they want to clear the source document from the
                        // Output message property and clear the content via element.
                        if (newVal == null) {
                            SqlTransformationMappingRoot mappingRoot = (SqlTransformationMappingRoot)((EObject)src).eContainer();
                            // Check if root's target is Operation
                            EObject target = mappingRoot.getTarget();
                            if (target != null && TransformationHelper.isOperation(target)) {
                                String sql = SqlMappingRootCache.getSelectSql(mappingRoot);
                                if (sql == null || sql.trim().length() == 0) {
                                    // Then we have an EMPTY SQL here and we need to clean out the XML Doc AND Content via element
                                    // properties
                                    WebServiceUiUtil.clearXmlDocumentAsSource(mappingRoot, true, transactionSource);
                                }
                            }
                        }
                    }
                    break;
                }
            }
        }
    }

    /* 
     * Method to confirm that the user wants to overwrite the existing procedure 
     * @return 'true' if procedure is to be overwritten, 'false' if not.
     */
    boolean confirmReplaceProcedure() {
        boolean replaceProc = false;
        // Check if callbacks are disabled
        IPreferenceStore prefStore = UiPlugin.getDefault().getPreferenceStore();
        boolean disableCallbacks = prefStore.getBoolean(PluginConstants.Prefs.Callbacks.DISABLE_CALLBACKS);
        if (!disableCallbacks) {
            // Prompt whether to add the proc symbols to the target
            replaceProc = MessageDialog.openQuestion(null, CONFIRM_SQLUPDATE_TITLE, CONFIRM_SQLUPDATE_MSG);
        }
        return replaceProc;
    }

    /**
     * Handler method for Undo notifications. Basically, the undo/redo notifications are ignored unless the SQL statement has
     * changed. In the event that the SQL is being changed, then the only thing that needs to be done is to invalidate the
     * SqlMappingRootCache. This insures that the cache will not be stale.
     * 
     * @param notifications the collection of notifications
     */
    private void handleUndo( Collection notifications,
                             Object txnSource ) {
        if (!notifications.isEmpty()) {
            // NO SPECIFIC WORK YET
        }
    }

    /**
     * @see org.eclipse.emf.edit.provider.INotifyChangedListener#notifyChanged(org.eclipse.emf.common.notify.Notification)
     */
    public void notifyChanged( Notification notification ) {

        // ----------------------------------------------------------------------------------------------
        // NOTE: Transaction boundaries are taken care of by the specific
        // handler. Don't start one here
        // ----------------------------------------------------------------------------------------------
        // Check for SourcedNotification (we should only get
        // SourcedNotifications)
        if (notification instanceof SourcedNotification) {
            SourcedNotification note = (SourcedNotification)notification;
            Object src = note.getSource();
            if (src == null || !src.equals(this)) {
                if (src == null) {
                    src = notification.getNotifier();
                }
                // Undos - special handler
                if (src instanceof ModelerUndoManager) {
                    Collection notifications = ((SourcedNotification)notification).getNotifications();
                    handleUndo(notifications, src);
                } else {
                    // Let's do a check here to see if notifications involve Web Service Models

                    ModelObjectNotificationHelper helper = new ModelObjectNotificationHelper(notification);

                    boolean webServiceModelsChanged = false;

                    for (Iterator iter = helper.getModifiedResources().iterator(); iter.hasNext();) {
                        if (ModelIdentifier.isWebServicesViewModel((IResource)iter.next())) {
                            webServiceModelsChanged = true;
                            break;
                        }
                    }
                    if (webServiceModelsChanged) {
                        Collection notifications = ((SourcedNotification)notification).getNotifications();
                        handleNotifications(notifications, src);
                    }
                }
            }
        } else { // handle single Notification
            Collection notifications = new ArrayList(1);
            notifications.add(notification);
            handleNotifications(notifications, notification.getNotifier());
        }
    }

    /**
     * This private method sets additional properties on a Web Services's Operation's Output object In particular, it calls
     * setXmlDocument() and setContentViaElement()
     */
    private void processChangedXmlDocumentAsSource( Object source,
                                                    Object value,
                                                    boolean add,
                                                    Object transactionSource ) {
        // Ensure that targetObject is SqlTransformationMappingRoot
        if (!TransformationHelper.isSqlTransformationMappingRoot(source)) {
            return;
        }
        // Ensure root's target is Web Service Operation object
        Object target = ((SqlTransformationMappingRoot)source).getTarget();
        if (!(target instanceof Operation)) {
            return;
        }
        Operation operation = (add ? (Operation)target : null);
        // Ensure that there is only one XmlDocument (i.e. can't add multiple XML documents as sources)
        XmlDocument xmlDocument = null;
        if (value instanceof List) {
            for (Iterator iter = ((List)value).iterator(); iter.hasNext();) {
                Object obj = iter.next();
                if (obj instanceof XmlDocument) {
                    if (xmlDocument == null) {
                        xmlDocument = (XmlDocument)obj;
                    } else {
                        xmlDocument = null;
                        break;
                    }
                }
            } // for
        } else if (value instanceof XmlDocument) {
            xmlDocument = (XmlDocument)value;
        }
        // Ensure there is only one new value and it's an XML document
        if (xmlDocument != null && operation != null && operation.getOutput() != null) {
            WebServiceUiUtil.addXmlDocumentAsSource((SqlTransformationMappingRoot)source, xmlDocument, transactionSource);
        }
    }
}
