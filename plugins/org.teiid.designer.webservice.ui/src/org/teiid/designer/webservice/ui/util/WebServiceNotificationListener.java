/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.webservice.ui.util;

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
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.designer.core.transaction.SourcedNotification;
import org.teiid.designer.metamodels.transformation.SqlTransformation;
import org.teiid.designer.metamodels.transformation.SqlTransformationMappingRoot;
import org.teiid.designer.metamodels.webservice.Input;
import org.teiid.designer.metamodels.webservice.Interface;
import org.teiid.designer.metamodels.webservice.Operation;
import org.teiid.designer.metamodels.webservice.Output;
import org.teiid.designer.metamodels.webservice.WebServicePackage;
import org.teiid.designer.metamodels.xml.XmlDocument;
import org.teiid.designer.transformation.ui.PluginConstants;
import org.teiid.designer.transformation.ui.UiPlugin;
import org.teiid.designer.transformation.ui.editors.sqleditor.SqlEditorPanel;
import org.teiid.designer.transformation.util.SqlMappingRootCache;
import org.teiid.designer.transformation.util.TransformationHelper;
import org.teiid.designer.ui.common.util.UiUtil;
import org.teiid.designer.ui.undo.ModelerUndoManager;
import org.teiid.designer.ui.util.ModelObjectNotificationHelper;
import org.teiid.designer.ui.viewsupport.ModelIdentifier;
import org.teiid.designer.webservice.ui.IInternalUiConstants;
import org.teiid.designer.webservice.ui.editor.OperationObjectEditorPage;


/**
 * @since 8.0
 */
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
                                @Override
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
                                @Override
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
    @Override
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
