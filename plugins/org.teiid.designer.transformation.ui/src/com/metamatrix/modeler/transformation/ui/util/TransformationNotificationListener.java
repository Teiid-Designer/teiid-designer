/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.ui.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EventObject;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.edit.provider.INotifyChangedListener;
import org.eclipse.emf.mapping.MappingHelper;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Display;
import org.teiid.query.sql.lang.Command;
import org.teiid.query.sql.lang.Query;
import org.teiid.query.sql.lang.QueryCommand;
import org.teiid.query.sql.lang.Select;
import com.metamatrix.core.event.EventObjectListener;
import com.metamatrix.core.event.EventSourceException;
import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.metamodels.relational.Table;
import com.metamatrix.metamodels.transformation.SqlAlias;
import com.metamatrix.metamodels.transformation.SqlTransformation;
import com.metamatrix.metamodels.transformation.SqlTransformationMappingRoot;
import com.metamatrix.metamodels.transformation.TransformationPackage;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.notification.util.NotificationUtilities;
import com.metamatrix.modeler.core.query.QueryValidationResult;
import com.metamatrix.modeler.core.query.QueryValidator;
import com.metamatrix.modeler.core.transaction.SourcedNotification;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceNotification;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceNotificationListener;
import com.metamatrix.modeler.diagram.ui.util.DiagramUiUtilities;
import com.metamatrix.modeler.internal.core.resource.EmfResource;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.core.workspace.ModelWorkspaceManager;
import com.metamatrix.modeler.internal.transformation.util.AttributeMappingHelper;
import com.metamatrix.modeler.internal.transformation.util.SqlMappingRootCache;
import com.metamatrix.modeler.internal.transformation.util.TransformationHelper;
import com.metamatrix.modeler.internal.transformation.util.TransformationMappingHelper;
import com.metamatrix.modeler.internal.transformation.util.TransformationSqlHelper;
import com.metamatrix.modeler.internal.ui.editors.ModelEditor;
import com.metamatrix.modeler.internal.ui.refactor.actions.RenameRefactorAction;
import com.metamatrix.modeler.internal.ui.undo.ModelerUndoManager;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.transformation.TransformationPlugin;
import com.metamatrix.modeler.transformation.ui.PluginConstants;
import com.metamatrix.modeler.transformation.ui.UiConstants;
import com.metamatrix.modeler.transformation.ui.UiPlugin;
import com.metamatrix.modeler.transformation.ui.actions.ITransformationDiagramActionConstants;
import com.metamatrix.modeler.transformation.ui.actions.ImportTransformationSqlFromTextAction;
import com.metamatrix.modeler.transformation.ui.actions.ReconcileTransformationAction;
import com.metamatrix.modeler.transformation.ui.editors.TransformationObjectEditorPage;
import com.metamatrix.modeler.transformation.ui.editors.sqleditor.SqlEditorPanel;
import com.metamatrix.modeler.transformation.validation.TransformationValidator;
import com.metamatrix.modeler.ui.editors.ModelEditorManager;
import com.metamatrix.modeler.ui.editors.ModelObjectEditorPage;
import com.metamatrix.modeler.ui.event.ModelResourceEvent;
import com.metamatrix.ui.internal.dialog.RadioMessageDialog;
import com.metamatrix.ui.internal.util.UiUtil;

/**
 * TransformationNotificationListener The notification listener is instantiated upon initialization of the plugin, and registered
 * as a Notification listener. The listener responds to the following notifications SqlTransformation o SQL strings changed - the
 * listener will use the SQlTransformationHelper to reconcile the target attributes to the sql. o SqlAlias added or removed - the
 * listener will use the SqlTransformationHelper to modify the SqlTransformation SQL strings SqlTable o Children added/removed -
 * respond to addition or removal of children (Sql Columns). o rename - responds to table rename SqlColumn o Change - responds to
 * changes to the Column (eg rename).
 */
public class TransformationNotificationListener implements INotifyChangedListener, EventObjectListener, UiConstants {
    static final String ADD_SQL_ELEM_GRP_REFS_TITLE = UiConstants.Util.getString("TransformationNotificationListener.addSQLElemGrpAttrsTitle"); //$NON-NLS-1$
    static final String REMOVE_SQL_ELEM_GRP_REFS_TITLE = UiConstants.Util.getString("TransformationNotificationListener.removeSQLElemGrpRefsTitle"); //$NON-NLS-1$
    static final String REMOVE_SQL_ELEMS_TITLE = UiConstants.Util.getString("TransformationNotificationListener.removeSQLElemsTitle"); //$NON-NLS-1$
    static final String REMOVE_SQL_ELEMS_MSG = UiConstants.Util.getString("TransformationNotificationListener.removeSQLElemsMsg"); //$NON-NLS-1$
    private static final String DEFAULT_REMOVED_SOURCE = UiConstants.Util.getString("TransformationNotificationListener.defaultRemovedSource"); //$NON-NLS-1$
    private static final String DEFAULT_ADDED_SOURCE = UiConstants.Util.getString("TransformationNotificationListener.defaultAddedSource"); //$NON-NLS-1$
    static final String SAVE_BEFORE_CHANGE_TITLE = UiConstants.Util.getString("TransformationNotificationListener.saveBeforeChangesTitle"); //$NON-NLS-1$
    // SBC == SAVE_BEFORE_CHANGE
    static final String SBC_GROUP_TITLE = UiConstants.Util.getString("TransformationNotificationListener.saveBeforeChangesRadioTitle"); //$NON-NLS-1$
    private static final String SBC_RADIO_SAVE = UiConstants.Util.getString("TransformationNotificationListener.saveBeforeChangesRadioButtonSave"); //$NON-NLS-1$
    private static final String SBC_RADIO_IGNORE = UiConstants.Util.getString("TransformationNotificationListener.saveBeforeChangesRadioButtonIgnore"); //$NON-NLS-1$
    private static final String SBC_RADIO_HALT = UiConstants.Util.getString("TransformationNotificationListener.saveBeforeChangesRadioButtonHalt"); //$NON-NLS-1$
    private static final String SBC_NOTE = UiConstants.Util.getString("TransformationNotificationListener.saveBeforeChangesNote"); //$NON-NLS-1$

    private static final boolean NOT_SIGNIFICANT = false;
    private static final boolean IS_UNDOABLE = true;
    private static final boolean NOT_UNDOABLE = false;

    static int uiIntegerResult = 0;
    static boolean uiBooleanResult = false;

    // Allow user to shut off notifications - defaults to false
    private boolean ignoreNotifications = false;
    private boolean ignoreTableSupportsUpdateChangedFalse = false;
    private boolean ignoreTableSupportsUpdateChangedTrue = false;
    boolean setColumnsUpdateableOnTableUpdateable = false;

    /**
     * Construct an instance of TransformationNotificationListener.
     */
    public TransformationNotificationListener() {
        super();
        // add listener for workspace Project close and Model delete notifications
        WorkspaceNotificationListener listener = new WorkspaceNotificationListener();
        ModelWorkspaceManager.getModelWorkspaceManager().addNotificationListener(listener);

        try {
            com.metamatrix.modeler.ui.UiPlugin.getDefault().getEventBroker().addListener(ModelResourceEvent.class, this);
        } catch (EventSourceException e) {
            Util.log(IStatus.ERROR, e, e.getMessage());
        }
    }

    public void setIgnoreNotifications( boolean shouldIgnore ) {
        this.ignoreNotifications = shouldIgnore;
    }

    /**
     * @see org.eclipse.emf.edit.provider.INotifyChangedListener#notifyChanged(org.eclipse.emf.common.notify.Notification)
     */
    public void notifyChanged( Notification notification ) {
        // If notifications are shut off, just return
        if (ignoreNotifications) return;

        // ----------------------------------------------------------------------------------------------
        // NOTE: Transaction boundaries are taken care of by the specific handler. Dont start one here
        // ----------------------------------------------------------------------------------------------
        // Check for SourcedNotification (we should only get SourcedNotifications)
        if (notification instanceof SourcedNotification) {
            if (sourceIsNotThis((SourcedNotification)notification)
                && sourceIsNotTransformationMappingHelper((SourcedNotification)notification)) {

                Object source = ((SourcedNotification)notification).getSource();
                if (source == null) {
                    source = notification.getNotifier();
                }
                // Undos - special handler
                if (source instanceof ModelerUndoManager) {
                    Collection notifications = ((SourcedNotification)notification).getNotifications();
                    handleUndo(notifications, source);
                } else {
                    Collection notifications = ((SourcedNotification)notification).getNotifications();
                    handleNotifications(notifications, source);
                }
            }
        } else { // handle single Notification
            Collection notifications = new ArrayList(1);
            notifications.add(notification);
            handleNotifications(notifications, null);
        }

    }

    private boolean sourceIsNotTransformationMappingHelper( SourcedNotification sn ) {
        Object source = sn.getSource();

        if (source instanceof TransformationMappingHelper) return false;

        return true;
    }

    private boolean sourceIsNotThis( SourcedNotification sn ) {
        Object source = sn.getSource();

        if (source == null) return true;

        return !(sn.getSource().equals(this));
    }

    /* 
     * Notifications handler.  Gathers all like notifications and handles them together.
     * Only the relevant notifications will be processed by this listener.
     * @param notifications the collection of all notifications
     */
    private void handleNotifications( Collection notifications,
                                      Object source ) {
        // --------------------------------------------------------
        // Do a first-pass - ignore irrelevant notifications
        // --------------------------------------------------------
        Collection validNotifications = filterNotifications(notifications);
        // ----------------------------------------
        // Process remaining valid notifications
        // ----------------------------------------
        if (!validNotifications.isEmpty()) {
            // ---------------------------------------------
            // SqlAliases Added to SqlTransformation
            // ---------------------------------------------
            Collection sqlAliasAdds = getSqlAliasAddNotifications(validNotifications);
            if (!sqlAliasAdds.isEmpty()) {
                handleSqlAliasAddNotifications(sqlAliasAdds, source);
            }
            // ---------------------------------------------
            // SqlAliases Removed from SqlTransformation
            // ---------------------------------------------
            Collection sqlAliasRemoves = getSqlAliasRemoveNotifications(validNotifications);
            if (!sqlAliasRemoves.isEmpty()) {
                handleSqlAliasRemoveNotifications(sqlAliasRemoves, source);
            }
            // ---------------------------------------------
            // SqlAliases Changed
            // ---------------------------------------------
            Collection sqlAliasChanges = getSqlAliasChangeNotifications(validNotifications);
            if (!sqlAliasChanges.isEmpty()) {
                handleSqlAliasChangeNotifications(sqlAliasChanges, source);
            }
            // ---------------------------------------------
            // Virtual Table Columns Added
            // ---------------------------------------------
            Collection virtualTableColAdds = getTargetVirtualTableColumnAddNotifications(validNotifications);
            if (!virtualTableColAdds.isEmpty()) {
                handleTargetVirtualTableColumnAddNotifications(virtualTableColAdds, source);
            }
            // ---------------------------------------------
            // Virtual Table Columns Removed
            // ---------------------------------------------
            Collection virtualTableColRemoves = getTargetVirtualTableColumnRemoveNotifications(validNotifications);
            if (!virtualTableColRemoves.isEmpty()) {
                handleTargetVirtualTableColumnRemoveNotifications(virtualTableColRemoves, source);
            }
            // ---------------------------------------------
            // Procedure ResultSet Columns Added
            // ---------------------------------------------
            Collection resultSetColAdds = getTargetProcedureResultSetColumnAddNotifications(validNotifications);
            if (!resultSetColAdds.isEmpty()) {
                handleTargetProcedureResultSetColumnAddNotifications(resultSetColAdds, source);
            }
            // ---------------------------------------------
            // Procedure ResultSet Columns Removed
            // ---------------------------------------------
            Collection resultSetColRemoves = getTargetProcedureResultSetColumnRemoveNotifications(validNotifications);
            if (!resultSetColRemoves.isEmpty()) {
                handleTargetProcedureResultSetColumnRemoveNotifications(resultSetColRemoves, source);
            }
            // ---------------------------------------------
            // Procedure ResultSet Added
            // ---------------------------------------------
            Collection resultSetAdds = getTargetProcedureResultSetOrParamAddNotifications(validNotifications);
            if (!resultSetAdds.isEmpty()) {
                handleTargetProcedureResultSetOrParamAddNotifications(resultSetAdds, source);
            }
            // ---------------------------------------------
            // Procedure ResultSet Removed
            // ---------------------------------------------
            Collection resultSetRemoves = getTargetProcedureResultSetOrParamRemoveNotifications(validNotifications);
            if (!resultSetRemoves.isEmpty()) {
                handleTargetProcedureResultSetOrParamRemoveNotifications(resultSetRemoves, source);
            }
            // -------------------------------------------------------
            // SQL statements Changed - only process Select changes
            // -------------------------------------------------------
            Collection sqlUIDStatementChanges = getSqlSelectUIDStatementChangeNotifications(validNotifications);
            if (!sqlUIDStatementChanges.isEmpty()) {
                handleSqlUIDStatementChangeNotifications(sqlUIDStatementChanges, source);
            }
            // -------------------------------------------------------
            // Sql Table or Column renamed
            // -------------------------------------------------------
            Collection tableOrColumnRenames = getSqlTableAndColumnRenameNotifications(validNotifications);
            if (!tableOrColumnRenames.isEmpty()) {
                handleSqlTableAndColumnRenameNotifications(tableOrColumnRenames, source);
            }
            // -------------------------------------------------------
            // Sql Table or Column removed
            // -------------------------------------------------------
            Collection tableOrColumnRemoves = getSqlTableAndColumnRemoveNotifications(validNotifications);
            if (!tableOrColumnRemoves.isEmpty()) {
                handleSqlTableAndColumnRemoveNotifications(tableOrColumnRemoves, source);
            }
            // ---------------------------------------------
            // Virtual Table Changed
            // ---------------------------------------------
            Collection virtualTableChanges = getTargetVirtualTableChangeNotifications(validNotifications);
            if (!virtualTableChanges.isEmpty()) {
                handleTargetVirtualTableChangeNotifications(virtualTableChanges, source);
            }
            // ---------------------------------------------------------
            // Sql Column Changed - handles attribute property changes
            // ---------------------------------------------------------
            Collection columnChanges = getSqlColumnChangeNotifications(validNotifications);
            if (!columnChanges.isEmpty()) {
                handleSqlColumnChangeNotifications(columnChanges, source);
            }
            // ---------------------------------------------------------
            // Sql Column Added
            // ---------------------------------------------------------
            Collection columnAdds = getSqlColumnAddNotifications(validNotifications);
            if (!columnAdds.isEmpty()) {
                handleSqlTableColumnAddNotifications(columnAdds, source);
            }
            // ---------------------------------------------------------
            // Model Refactor/Rename
            // This is fired as a mappingRoot change notification
            // with RenameRefactorAction as the source
            // ---------------------------------------------------------
            Collection modelRenames = getModelRenameNotifications(validNotifications, source);
            if (!modelRenames.isEmpty()) {
                handleModelRenameNotifications(modelRenames, source);
            }
        }
    }

    /* 
     * Do a first pass to filter out totally irrelevant notifications.  Will only keep notifications
     * where one of the following has changed - 1) SqlTransformationMappingRoot, 2) SqlTransformation,
     * 3) SqlTable, 4) SqlColumn, 5)SqlProcedure, 6)SqlColumnSet - Virtual Proc ResultSet
     * @param notifications the collection of all notifications
     * @return new collection of relevant notifications
     */
    private Collection filterNotifications( Collection notifications ) {
        Collection goodNotifications = new ArrayList(notifications.size());
        Iterator iter = notifications.iterator();
        while (iter.hasNext()) {
            Notification notification = (Notification)iter.next();
            // Get the object that changed
            Object changedObj = ModelerCore.getModelEditor().getChangedObject(notification);
            // Only keep notifications where one of the following has changed:
            // 1) SqlTransformationMappingRoot
            // 2) SqlTransformation
            // 3) SqlTable
            // 4) SqlColumn
            // 5) SqlProcedure
            // 6) ColumnSet - Virtual Procedure ResultSet
            if (changedObj != null && changedObj instanceof EObject && !DiagramUiUtilities.isDiagramObject((EObject)changedObj)) {
                if (changedObj instanceof SqlTransformation || changedObj instanceof SqlTransformationMappingRoot
                    || TransformationHelper.isSqlTable(changedObj) || TransformationHelper.isSqlColumn(changedObj)
                    || TransformationHelper.isSqlProcedure(changedObj)
                    || TransformationHelper.isSqlProcedureParameter(changedObj)
                    || TransformationHelper.isSqlColumnSet(changedObj)) {
                    goodNotifications.add(notification);
                } else if (NotificationUtilities.isRemoved(notification)) {
                    Object removedObj = notification.getOldValue();
                    if (TransformationHelper.isSqlTable(removedObj) ||
                    	changedObj instanceof ModelAnnotation) {
                        goodNotifications.add(notification);
                    }
                }
            }
        }
        return goodNotifications;
    }

    /* 
     * Get all SqlAlias Add Notifications.
     * @param notifications the collection of all notifications
     * @return the SqlAlias Add Notifications
     */
    private Collection getSqlAliasAddNotifications( Collection notifications ) {
        Collection result = null;
        Iterator iter = notifications.iterator();
        while (iter.hasNext()) {
            Notification notification = (Notification)iter.next();
            if (NotificationUtilities.isAdded(notification)) {
                // Get the added children - check whether they are SqlAliases
                EObject[] newChildren = NotificationUtilities.getAddedChildren(notification);

                // If any of the added children are SqlAlias, add notification to result
                if (containsSqlAlias(newChildren)) {
                    if (result == null) {
                        result = new ArrayList(notifications.size());
                    }
                    result.add(notification);
                    // Remove from notifications collection
                    iter.remove();
                }
            }
        }
        if (result == null) {
            result = Collections.EMPTY_LIST;
        }
        return result;
    }

    /* 
     * Get all SqlAlias Remove Notifications.
     * @param notifications the collection of all notifications
     * @return the SqlAlias Remove Notifications
     */
    private Collection getSqlAliasRemoveNotifications( Collection notifications ) {
        Collection result = null;
        Iterator iter = notifications.iterator();
        while (iter.hasNext()) {
            Notification notification = (Notification)iter.next();
            if (NotificationUtilities.isRemoved(notification)) {
                // Get the removed children - check whether they are SqlAliases
                EObject[] removedChildren = NotificationUtilities.getRemovedChildren(notification);

                // If any of the removed children are SqlAlias, add notification to result
                if (containsSqlAlias(removedChildren)) {
                    if (result == null) {
                        result = new ArrayList(notifications.size());
                    }
                    result.add(notification);
                    // Remove from notifications collection
                    iter.remove();
                }
            }
        }
        if (result == null) {
            result = Collections.EMPTY_LIST;
        }
        return result;
    }

    /* 
     * Get all SqlAlias Change Notifications.
     * @param notifications the collection of all notifications
     * @return the SqlAlias Change Notifications
     */
    private Collection getSqlAliasChangeNotifications( Collection notifications ) {
        Collection result = null;
        Iterator iter = notifications.iterator();
        while (iter.hasNext()) {
            Notification notification = (Notification)iter.next();
            if (NotificationUtilities.isChanged(notification)) {
                // If SqlAlias feature changed, add notification to result
                if (sqlAliasesChanged(notification)) {
                    if (result == null) {
                        result = new ArrayList(notifications.size());
                    }
                    result.add(notification);
                    // Remove from notifications collection
                    iter.remove();
                }
            }
        }
        if (result == null) {
            result = Collections.EMPTY_LIST;
        }
        return result;
    }

    /* 
     * Get all SqlStatement Change Notifications for UID SQL changes.
     * @param notifications the collection of all notifications
     * @return the SqlStatement UID Change Notifications
     */
    private Collection getSqlSelectUIDStatementChangeNotifications( Collection notifications ) {
        Collection result = null;
        Iterator iter = notifications.iterator();
        while (iter.hasNext()) {
            Notification notification = (Notification)iter.next();
            if (NotificationUtilities.isChanged(notification)) {
                // Determine if Select UID Sql Statements changed.
                if (sqlSelectUIDStatementChanged(notification)) {
                    if (result == null) {
                        result = new ArrayList(notifications.size());
                    }
                    result.add(notification);
                    // Remove from notifications collection
                    iter.remove();
                }
            }
        }
        if (result == null) {
            result = Collections.EMPTY_LIST;
        }
        return result;
    }

    /* 
     * Get all SqlStatement Change Notifications for User SQL changes.
     * @param notifications the collection of all notifications
     * @return the SqlStatement User Change Notifications
     */
    private Collection getSqlSelectUserStatementChangeNotifications( Collection notifications ) {
        Collection result = null;
        Iterator iter = notifications.iterator();
        while (iter.hasNext()) {
            Notification notification = (Notification)iter.next();
            if (NotificationUtilities.isChanged(notification)) {
                // Determine if Select User Sql Statements changed.
                if (sqlSelectUserStatementChanged(notification)) {
                    if (result == null) {
                        result = new ArrayList(notifications.size());
                    }
                    result.add(notification);
                    // Remove from notifications collection
                    iter.remove();
                }
            }
        }
        if (result == null) {
            result = Collections.EMPTY_LIST;
        }
        return result;
    }

    /* 
     * Get all Notifications involving rename of a SqlTable,SqlColumn,Procedure or ProcedureParameter
     * @param notifications the collection of all notifications
     * @return the SqlTable and SqlColumn rename Notifications
     */
    private Collection getSqlTableAndColumnRenameNotifications( Collection notifications ) {
        Collection result = null;
        Iterator iter = notifications.iterator();
        while (iter.hasNext()) {
            Notification notification = (Notification)iter.next();
            if (NotificationUtilities.isChanged(notification)) {
                // Get the object that was changed
                Object changedObj = ModelerCore.getModelEditor().getChangedObject(notification);
                // If changedObject is SqlTable or SqlColumn, see if the name changed
                if (TransformationHelper.isSqlColumn(changedObj) || TransformationHelper.isSqlTable(changedObj)
                    || TransformationHelper.isSqlProcedure(changedObj)
                    || TransformationHelper.isSqlProcedureParameter(changedObj)) {
                    // get Name feature from the changedObject
                    Object nameFeature = ModelerCore.getModelEditor().getNameFeature((EObject)changedObj);
                    // get feature that changed from the notification
                    Object feature = notification.getFeature();
                    // If name feature changed, add notification to list
                    if (feature != null && feature.equals(nameFeature)) {
                        if (result == null) {
                            result = new ArrayList(notifications.size());
                        }
                        result.add(notification);
                        // Remove from notifications collection
                        iter.remove();
                    }
                }
            }
        }
        if (result == null) {
            result = Collections.EMPTY_LIST;
        }
        return result;
    }

    /* 
     * Get all Notifications involving deletion of a SqlTable,SqlColumn,SqlProcedure or SqlProcedureParameter
     * @param notifications the collection of all notifications
     * @return the SqlTable and SqlColumn remove Notifications
     */
    private Collection getSqlTableAndColumnRemoveNotifications( Collection notifications ) {
        Collection result = null;
        Iterator iter = notifications.iterator();
        while (iter.hasNext()) {
            Notification notification = (Notification)iter.next();
            if (NotificationUtilities.isRemoved(notification)) {
                Object removedObj = notification.getOldValue();
                if (TransformationHelper.isSqlTable(removedObj) || TransformationHelper.isSqlColumn(removedObj)
                    || TransformationHelper.isSqlProcedure(removedObj)
                    || TransformationHelper.isSqlProcedureParameter(removedObj)) {
                    if (result == null) {
                        result = new ArrayList(notifications.size());
                    }
                    result.add(notification);
                    // Remove from notifications collection
                    iter.remove();
                }
            }
        }
        if (result == null) {
            result = Collections.EMPTY_LIST;
        }
        return result;
    }

    /* 
     * Get all Notifications involving SqlColumn change 
     * @param notifications the collection of all notifications
     * @return the SqlColumn change Notifications
     */
    private Collection getSqlColumnChangeNotifications( Collection notifications ) {
        Collection result = null;
        Iterator iter = notifications.iterator();
        while (iter.hasNext()) {
            Notification notification = (Notification)iter.next();
            if (NotificationUtilities.isChanged(notification)) {
                // Get the object that was changed
                Object changedObj = ModelerCore.getModelEditor().getChangedObject(notification);
                // If changedObject is SqlTable or SqlColumn, see if the name changed
                if (TransformationHelper.isSqlColumn(changedObj)) {
                    if (result == null) {
                        result = new ArrayList(notifications.size());
                    }
                    result.add(notification);
                    // Remove from notifications collection
                    iter.remove();
                }
            }
        }
        if (result == null) {
            result = Collections.EMPTY_LIST;
        }
        return result;
    }

    /* 
     * Get all Notifications involving addition of a SqlColumn
     * @param notifications the collection of all notifications
     * @return the SqlColumn add Notifications
     */
    private Collection getSqlColumnAddNotifications( Collection notifications ) {
        Collection result = null;
        Iterator iter = notifications.iterator();
        while (iter.hasNext()) {
            Notification notification = (Notification)iter.next();
            if (NotificationUtilities.isAdded(notification)) {
                // Get the added children - check whether they are SqlColumns
                EObject[] newChildren = NotificationUtilities.getAddedChildren(notification);
                for (int i = 0; i < newChildren.length; i++) {
                    EObject child = newChildren[i];
                    if (TransformationHelper.isSqlColumn(child)) {
                        if (result == null) {
                            result = new ArrayList(notifications.size());
                        }
                        result.add(notification);
                        // Remove from notifications collection
                        iter.remove();
                        break;
                    }
                }
            }
        }
        if (result == null) {
            result = Collections.EMPTY_LIST;
        }
        return result;
    }

    /* 
     * Get all Virtual Target Table Change Notifications.
     * @param notifications the collection of all notifications
     * @return the Virtual Table Change Notifications
     */
    private Collection getTargetVirtualTableChangeNotifications( Collection notifications ) {
        Collection result = null;
        Iterator iter = notifications.iterator();
        while (iter.hasNext()) {
            Notification notification = (Notification)iter.next();
            if (NotificationUtilities.isChanged(notification)) {
                // Get the object that was added to
                Object changedObj = ModelerCore.getModelEditor().getChangedObject(notification);
                if (TransformationHelper.isVirtualSqlTable(changedObj)
                    && TransformationHelper.isValidSqlTransformationTarget(changedObj) && isCriticalFeatureChange(notification)) {
                    if (result == null) {
                        result = new ArrayList(notifications.size());
                    }
                    result.add(notification);
                    // Remove from notifications collection
                    iter.remove();
                }
            }
        }
        if (result == null) {
            result = Collections.EMPTY_LIST;
        }
        return result;
    }

    private boolean isCriticalFeatureChange( Notification notification ) {
        if (notification.getFeature() instanceof EStructuralFeature) {
            EStructuralFeature esf = (EStructuralFeature)notification.getFeature();
            if (esf.getFeatureID() == TransformationPackage.MAPPING_CLASS__RECURSION_LIMIT) return false;
        }

        return true;
    }

    private Notification getTableSupportsUpdateFeatureChange( Collection notifications ) {
        Iterator iter = notifications.iterator();
        while (iter.hasNext()) {
            Notification notification = (Notification)iter.next();
            if (TransformationHelper.isSupportsUpdateTableChangeNotification(notification)) {
                return notification;
            }
        }

        return null;
    }
    
    private Notification getTableMaterializedViewChange( Collection notifications ) {
        Iterator iter = notifications.iterator();
        while (iter.hasNext()) {
            Notification notification = (Notification)iter.next();
            if (TransformationHelper.isSetMaterializedTableChangeNotification(notification)) {
                return notification;
            }
        }

        return null;
    }

    /* 
     * Get all Virtual Target Table Column Add Notifications.
     * @param notifications the collection of all notifications
     * @return the Virtual Table Column Add Notifications
     */
    private Collection getTargetVirtualTableColumnAddNotifications( Collection notifications ) {
        Collection result = null;
        Iterator iter = notifications.iterator();
        while (iter.hasNext()) {
            Notification notification = (Notification)iter.next();
            if (NotificationUtilities.isAdded(notification)) {
                // Get the object that was added to
                Object changedObj = ModelerCore.getModelEditor().getChangedObject(notification);
                if (TransformationHelper.isVirtualSqlTable(changedObj)
                    && TransformationHelper.isValidSqlTransformationTarget(changedObj)) {
                    if (result == null) {
                        result = new ArrayList(notifications.size());
                    }
                    result.add(notification);
                    // Remove from notifications collection
                    iter.remove();
                }
            }
        }
        if (result == null) {
            result = Collections.EMPTY_LIST;
        }
        return result;
    }

    /* 
     * Get all Virtual Target Table Column Remove Notifications.
     * @param notifications the collection of all notifications
     * @return the Virtual Table Column Remove Notifications
     */
    private Collection getTargetVirtualTableColumnRemoveNotifications( Collection notifications ) {
        Collection result = null;
        Iterator iter = notifications.iterator();
        while (iter.hasNext()) {
            Notification notification = (Notification)iter.next();
            if (NotificationUtilities.isRemoved(notification)) {
                // Get the object that was removed from
                Object changedObj = ModelerCore.getModelEditor().getChangedObject(notification);
                if (TransformationHelper.isVirtualSqlTable(changedObj)
                    && TransformationHelper.isValidSqlTransformationTarget(changedObj)) {
                    if (result == null) {
                        result = new ArrayList(notifications.size());
                    }
                    result.add(notification);
                    // Remove from notifications collection
                    iter.remove();
                }
            }
        }
        if (result == null) {
            result = Collections.EMPTY_LIST;
        }
        return result;
    }

    /* 
     * Get all Procedure ResultSet Column Add Notifications. (columns added to ResultSet)
     * @param notifications the collection of all notifications
     * @return the Procedure ResultSet Column Add Notifications
     */
    private Collection getTargetProcedureResultSetColumnAddNotifications( Collection notifications ) {
        Collection result = null;
        Iterator iter = notifications.iterator();
        while (iter.hasNext()) {
            Notification notification = (Notification)iter.next();
            if (NotificationUtilities.isAdded(notification)) {
                // Get the object that was added to (resultSet)
                Object changedObj = ModelerCore.getModelEditor().getChangedObject(notification);
                if (TransformationHelper.isSqlColumnSet(changedObj)) {
                    // parent - procedure
                    EObject procedure = ((EObject)changedObj).eContainer();
                    if (TransformationHelper.isSqlVirtualProcedure(procedure)
                        && TransformationHelper.isValidSqlTransformationTarget(procedure)) {
                        if (result == null) {
                            result = new ArrayList(notifications.size());
                        }
                        result.add(notification);
                        // Remove from notifications collection
                        iter.remove();
                    }
                }
            }
        }
        if (result == null) {
            result = Collections.EMPTY_LIST;
        }
        return result;
    }

    /* 
     * Get all Procedure ResultSet Column Remove Notifications. (columns removed from ResultSet)
     * @param notifications the collection of all notifications
     * @return the Procedure ResultSet Column Remove Notifications
     */
    private Collection getTargetProcedureResultSetColumnRemoveNotifications( Collection notifications ) {
        Collection result = null;
        Iterator iter = notifications.iterator();
        while (iter.hasNext()) {
            Notification notification = (Notification)iter.next();
            if (NotificationUtilities.isRemoved(notification)) {
                // Get the object that was removed from (resultSet)
                Object changedObj = ModelerCore.getModelEditor().getChangedObject(notification);
                if (TransformationHelper.isSqlColumnSet(changedObj)) {
                    // parent - procedure
                    EObject procedure = ((EObject)changedObj).eContainer();
                    if (TransformationHelper.isSqlVirtualProcedure(procedure)
                        && TransformationHelper.isValidSqlTransformationTarget(procedure)) {
                        if (result == null) {
                            result = new ArrayList(notifications.size());
                        }
                        result.add(notification);
                        // Remove from notifications collection
                        iter.remove();
                    }
                }
            }
        }
        if (result == null) {
            result = Collections.EMPTY_LIST;
        }
        return result;
    }

    /* 
     * Get all Procedure ResultSet Add Notifications. (When resultSets are added to procedures)
     * @param notifications the collection of all notifications
     * @return the Procedure ResultSet Add Notifications
     */
    private Collection getTargetProcedureResultSetOrParamAddNotifications( Collection notifications ) {
        Collection result = null;
        Iterator iter = notifications.iterator();
        while (iter.hasNext()) {
            Notification notification = (Notification)iter.next();
            if (NotificationUtilities.isAdded(notification)) {
                // Get the object that was added to - Procedure in this case
                Object changedObj = ModelerCore.getModelEditor().getChangedObject(notification);
                if (TransformationHelper.isSqlVirtualProcedure(changedObj)
                    && TransformationHelper.isValidSqlTransformationTarget(changedObj)) {
                    if (result == null) {
                        result = new ArrayList(notifications.size());
                    }
                    result.add(notification);
                    // Remove from notifications collection
                    iter.remove();
                }
            }
        }
        if (result == null) {
            result = Collections.EMPTY_LIST;
        }
        return result;
    }

    /* 
     * Get all Procedure ResultSet Remove Notifications. (When resultSets are removed from procedures)
     * @param notifications the collection of all notifications
     * @return the Procedure ResultSet Remove Notifications
     */
    private Collection getTargetProcedureResultSetOrParamRemoveNotifications( Collection notifications ) {
        Collection result = null;
        Iterator iter = notifications.iterator();
        while (iter.hasNext()) {
            Notification notification = (Notification)iter.next();
            if (NotificationUtilities.isRemoved(notification)) {
                // Get the object that was removed from - Procedure in this case
                Object changedObj = ModelerCore.getModelEditor().getChangedObject(notification);
                if (TransformationHelper.isSqlVirtualProcedure(changedObj)
                    && TransformationHelper.isValidSqlTransformationTarget(changedObj)) {
                    if (result == null) {
                        result = new ArrayList(notifications.size());
                    }
                    result.add(notification);
                    // Remove from notifications collection
                    iter.remove();
                }
            }
        }
        if (result == null) {
            result = Collections.EMPTY_LIST;
        }
        return result;
    }

    /* 
     * Get all ModelRename Notifications.
     * @param notifications the collection of all notifications
     * @return the Model Rename Notifications
     */
    private Collection getModelRenameNotifications( Collection notifications,
                                                    Object source ) {
        Collection result = null;
        Iterator iter = notifications.iterator();
        while (iter.hasNext()) {
            Notification notification = (Notification)iter.next();
            if (NotificationUtilities.isRemoved(notification)) {
                // Get the object that was changed
                Object changedObj = ModelerCore.getModelEditor().getChangedObject(notification);
                // changed object - MappingRoot, and source RenameRefactorAction
                if (changedObj instanceof ModelAnnotation && source instanceof RenameRefactorAction) {
                    if (result == null) {
                        result = new ArrayList(notifications.size());
                    }
                    result.add(notification);
                    // Remove from notifications collection
                    iter.remove();
                }
            }
        }
        if (result == null) {
            result = Collections.EMPTY_LIST;
        }
        return result;
    }

    /* 
     * Handler method for SqlTransformation add notifications.
     * @param notifications the collection of notifications
     * @param txnSource the source for the transaction
     */
    private void handleSqlAliasAddNotifications( Collection notifications,
                                                 Object txnSource ) {
        if (!notifications.isEmpty()) {
            // If any of the added children are SqlAlias, triggers update
            List addedAliases = getSqlAliasesAdded(notifications);

            // Get mapping root for the SqlTransformation
            EObject mappingRoot = getMappingRootFromSqlAliasNotifications(notifications);

            if (!addedAliases.isEmpty()) {
                boolean undoableAdd = NOT_UNDOABLE;
                if (ITransformationDiagramActionConstants.DiagramActions.UNDO_ADD_TRANSFORMATION_SOURCE) {
                    undoableAdd = IS_UNDOABLE;
                }

                // ----------------------------------------------------------------------------------------------------------
                // First transaction-
                // Update the SQL.
                // ----------------------------------------------------------------------------------------------------------
                // Update the sql and reconcile the attributes in one transaction
                boolean requiredStart = ModelerCore.startTxn(NOT_SIGNIFICANT, undoableAdd, "Update for SqlAlias Add", this); //$NON-NLS-1$
                boolean succeeded = false;
                try {
                    // Do not update SQL if source is TransformationObjectEditorPage
                    if (!(txnSource instanceof TransformationObjectEditorPage) && !(txnSource instanceof SqlEditorPanel)) {
                        // If the SELECT is valid, may need to prompt whether to add Elements...
                        boolean addElemsToSelect = false;
                        if (!(txnSource instanceof ImportTransformationSqlFromTextAction)) {
                            addElemsToSelect = shouldAddElemsToSelect(mappingRoot);
                        }
                        TransformationSqlHelper.updateAllSqlOnSqlAliasGroupsAdded(mappingRoot,
                                                                                  addedAliases,
                                                                                  addElemsToSelect,
                                                                                  txnSource);
                    }
                    succeeded = true;
                } finally {
                    // If we started Txn, commit it
                    if (requiredStart) {
                        if (succeeded) {
                            ModelerCore.commitTxn();
                        } else {
                            ModelerCore.rollbackTxn();
                        }
                    }
                }

                // ----------------------------------------------------------------------------------------------------------
                // Second transaction-
                // reconcile the targetAttributes This will add any new attributes to the target group.
                // ----------------------------------------------------------------------------------------------------------
                requiredStart = ModelerCore.startTxn(NOT_SIGNIFICANT, undoableAdd, "SqlAlias Add - reconcile target", txnSource); //$NON-NLS-1$
                succeeded = false;
                try {
                    // Reconcile using the updated Query
                    TransformationMappingHelper.reconcileTargetAttributes(mappingRoot, true, txnSource);
                    succeeded = true;
                } finally {
                    // If we started Txn, commit it
                    if (requiredStart) {
                        if (succeeded) {
                            ModelerCore.commitTxn();
                        } else {
                            ModelerCore.rollbackTxn();
                        }
                    }
                }
                // Ensure that the transformation cached status is updated after txn commit
                SqlMappingRootCache.invalidateSelectStatus(mappingRoot, true, txnSource);

                /*
                 * jh 19135: This is the code we need to execute on an AddSource operation!
                 *           (Copied from another 'handle' method in this listener class.)
                 */
            }
        }
    }

    /* 
     * Handler method for SqlTransformation remove notifications.
     * @param notifications the collection of notifications
     * @param txnSource the source for the transaction
     */
    private void handleSqlAliasRemoveNotifications( Collection notifications,
                                                    Object txnSource ) {
        if (!notifications.isEmpty()) {
            // Get removed SqlAliases
            List removedAliases = getSqlAliasesRemoved(notifications);

            // Get mapping root for the SqlTransformation
            EObject mappingRoot = getMappingRootFromSqlAliasNotifications(notifications);

            if (!removedAliases.isEmpty()) {
                // Start txn
                boolean requiredStart = ModelerCore.startTxn(NOT_SIGNIFICANT, IS_UNDOABLE, "Update for SqlAlias Remove", this); //$NON-NLS-1$
                boolean succeeded = false;
                try {
                    // Do not update SQL if source is TransformationObjectEditorPage
                    if (!(txnSource instanceof TransformationObjectEditorPage) && !(txnSource instanceof SqlEditorPanel)) {
                        // If the SELECT is valid, may need to prompt whether to remove Elements...
                        boolean removeElemsFromSelect = shouldRemoveGroupElemsFromSelect(mappingRoot, removedAliases);
                        TransformationSqlHelper.updateAllSqlOnSqlAliasGroupsRemoved(mappingRoot,
                                                                                    removedAliases,
                                                                                    removeElemsFromSelect,
                                                                                    txnSource);
                    }
                    // invalidate cached status
                    SqlMappingRootCache.invalidateSelectStatus(mappingRoot, true, txnSource);

                    // Reconcile using the updated Query
                    TransformationMappingHelper.reconcileTargetAttributes(mappingRoot, txnSource);

                    succeeded = true;
                } finally {
                    // If we started Txn, commit it
                    if (requiredStart) {
                        if (succeeded) {
                            ModelerCore.commitTxn();
                        } else {
                            ModelerCore.rollbackTxn();
                        }
                    }
                }
            }
        }
    }

    /* 
     * Handler method for SqlTransformation change notifications.
     * @param notifications the collection of notifications
     * @param txnSource the source for the transaction
     */
    private void handleSqlAliasChangeNotifications( Collection notifications,
                                                    Object txnSource ) {
        if (!notifications.isEmpty()) {
            // Get changed SqlAliases - (removed)
            List removedAliases = getSqlAliasesChanged(notifications);

            // Get mapping root for the SqlTransformation
            EObject mappingRoot = getMappingRootFromSqlAliasNotifications(notifications);

            if (!removedAliases.isEmpty()) {
                // Start txn
                boolean requiredStart = ModelerCore.startTxn(NOT_SIGNIFICANT, IS_UNDOABLE, "Update for SqlAlias Change", this); //$NON-NLS-1$
                boolean succeeded = false;
                try {
                    // Do not update SQL if source is TransformationObjectEditorPage
                    if (!(txnSource instanceof TransformationObjectEditorPage) && !(txnSource instanceof SqlEditorPanel)) {
                        // If the SELECT is valid, may need to prompt whether to remove Elements...
                        boolean removeElemsFromSelect = shouldRemoveGroupElemsFromSelect(mappingRoot, removedAliases);
                        TransformationSqlHelper.updateAllSqlOnSqlAliasGroupsRemoved(mappingRoot,
                                                                                    removedAliases,
                                                                                    removeElemsFromSelect,
                                                                                    txnSource);
                    }
                    // Reconcile using the updated Query
                    TransformationMappingHelper.reconcileTargetAttributes(mappingRoot, txnSource);
                    succeeded = true;
                } finally {
                    // If we started Txn, commit it
                    if (requiredStart) {
                        if (succeeded) {
                            ModelerCore.commitTxn();
                        } else {
                            ModelerCore.rollbackTxn();
                        }
                    }
                }
            }
        }
    }

    /* 
     * Handler method for Sql statement change notifications.
     * @param notifications the collection of notifications
     * @param txnSource the source for the transaction
     */
    private void handleSqlUIDStatementChangeNotifications( Collection notifications,
                                                           Object txnSource ) {
        if (!notifications.isEmpty()) {
            // Determine if this Notification is coming from the SqlTransformationHelper
            boolean sourceIsSqlHelper = isSqlHelperSource(txnSource);

            // Source is NOT SQLTransformationHelper and NOT TransformationObjectEditorPage, reconcile
            if (!sourceIsSqlHelper && !(txnSource instanceof TransformationObjectEditorPage)) {
                // Get mapping root for the SqlTransformation
                EObject mappingRoot = getMappingRootFromSqlUIDStatementNotifications(notifications);
                // Start txn
                boolean requiredStart = ModelerCore.startTxn(NOT_SIGNIFICANT, IS_UNDOABLE, "Update for Sql Statment Change", this); //$NON-NLS-1$
                boolean succeeded = false;
                try {
                    // Reconcile the mapping root Inputs / Attributes / etc to conform to the SQL
                    TransformationMappingHelper.reconcileMappingsOnSqlChange(mappingRoot, txnSource);
                    succeeded = true;
                } finally {
                    // If we started Txn, commit it
                    if (requiredStart) {
                        if (succeeded) {
                            ModelerCore.commitTxn();
                        } else {
                            ModelerCore.rollbackTxn();
                        }
                    }
                }

            }
        }
    }

    /* 
     * Handler method for Sql Table and Column rename notifications.  This method will invalidate the 
     * SqlMappingRootCache.
     * @param notifications the collection of notifications
     * @param txnSource the source for the transaction
     */
    private void handleSqlTableAndColumnRenameNotifications( Collection notifications,
                                                             Object txnSource ) {
        if (!notifications.isEmpty()) {
            // Gather up all the groups that have renames
            Set groups = new HashSet();
            Iterator iter = notifications.iterator();
            while (iter.hasNext()) {
                Notification notification = (Notification)iter.next();
                // Get the object that was changed - renamed
                Object changedObj = ModelerCore.getModelEditor().getChangedObject(notification);
                // changedObject is SqlTable
                if (TransformationHelper.isSqlTable(changedObj)) {
                    groups.add(changedObj);
                    // changedObject is SqlColumn
                } else if (TransformationHelper.isSqlColumn(changedObj)) {
                    // parent of column is the source group
                    EObject sourceGrp = ((EObject)changedObj).eContainer();
                    groups.add(sourceGrp);
                } else if (TransformationHelper.isSqlProcedure(changedObj)) {
                    groups.add(changedObj);
                } else if (TransformationHelper.isSqlProcedureParameter(changedObj)) {
                    // parent of parameter is the source procedure
                    EObject sourceProc = ((EObject)changedObj).eContainer();
                    groups.add(sourceProc);
                }
            }
            if (!groups.isEmpty()) {
                TransformationHelper.invalidateCachedRootsWithSourceGroups(groups);
                TransformationHelper.invalidateCachedRootsWithTargetGroups(groups);
            }
        }
    }

    /* 
     * Handler method for SqlTable and SqlColumn remove notifications.  This method will invalidate the 
     * SqlMappingRootCache.
     * @param notifications the collection of notifications
     * @param txnSource the source for the transaction
     */
    private void handleSqlTableAndColumnRemoveNotifications( Collection notifications,
                                                             Object txnSource ) {
        if (!notifications.isEmpty()) {
            // Gather up all the groups that have renames
            Set groups = new HashSet();
            Iterator iter = notifications.iterator();
            while (iter.hasNext()) {
                Notification notification = (Notification)iter.next();
                // Get the object that was removed
                Object removedObj = notification.getOldValue();
                // removed Object is SqlTable
                if (TransformationHelper.isSqlTable(removedObj)) {
                    groups.add(removedObj);
                    // removed Object is SqlColumn
                } else if (TransformationHelper.isSqlColumn(removedObj)) {
                    // parent of column is the sourceGroup
                    Object sourceGrp = ModelerCore.getModelEditor().getChangedObject(notification);
                    groups.add(sourceGrp);
                } else if (TransformationHelper.isSqlProcedure(removedObj)) {
                    groups.add(removedObj);
                } else if (TransformationHelper.isSqlProcedureParameter(removedObj)) {
                    // parent of parameter is the source procedure
                    Object sourceProc = ModelerCore.getModelEditor().getChangedObject(notification);
                    groups.add(sourceProc);
                }
            }
            if (!groups.isEmpty()) {
                TransformationHelper.invalidateCachedRootsWithSourceGroups(groups);
            }
        }
    }

    /* 
     * Handler method for Sql Column change notifications.  This method will invalidate the 
     * SqlMappingRootCache - the column type or other important feature may have changed.
     * @param notifications the collection of notifications
     * @param txnSource the source for the transaction
     */
    private void handleSqlColumnChangeNotifications( Collection notifications,
                                                     Object txnSource ) {
        if (!notifications.isEmpty()) {
            // Gather up all the groups that have renames
            Set groups = new HashSet();
            Iterator iter = notifications.iterator();
            while (iter.hasNext()) {
                Notification notification = (Notification)iter.next();
                // Get the object that was changed - renamed
                Object changedObj = ModelerCore.getModelEditor().getChangedObject(notification);
                if (TransformationHelper.isSqlColumn(changedObj)) {
                    // parent of column is the group
                    EObject grp = ((EObject)changedObj).eContainer();
                    groups.add(grp);
                }
            }
            if (!groups.isEmpty()) {
                TransformationHelper.invalidateCachedRootsWithSourceGroups(groups);
                TransformationHelper.invalidateCachedRootsWithTargetGroups(groups);
            }
        }
    }

    /* 
     * Handler method for Table Column Add notifications
     * @param notifications the collection of notifications
     * @param txnSource the source for the transaction
     */
    private void handleSqlTableColumnAddNotifications( Collection notifications,
                                                       Object txnSource ) {
        if (!notifications.isEmpty()) {
            // Get Tables Added To
            Set tables = getSqlTablesAddedTo(notifications);

            if (!tables.isEmpty()) {
                SqlMappingRootCache.invalidateRootsWithSourceGroups(tables);
            }
        }
    }

    /* 
     * Handler method for Virtual Table Change notifications.  Sole purpose of this method is
     * to invalidate the mappingRoot cache to cover case where allows update has changed.
     * @param notifications the collection of notifications
     * @param txnSource the source for the transaction
     */
    private void handleTargetVirtualTableChangeNotifications( Collection notifications,
                                                              Object txnSource ) {
        if (!notifications.isEmpty()) {
            // Get mapping root for the SqlTransformation
            EObject mappingRoot = getMappingRootFromVTableNotifications(notifications);
            // Handle changes to table 'supportsUpdate' state
            // Assumption is that it is always safe to change columns to not updateable, if the table 'supportsUpdate'
            // is set to false. This is default behavior, handled in TableImpl.
            Notification notif = getTableSupportsUpdateFeatureChange(notifications);
            if (notif != null) {
                // Handle the case where 'supportsUpdate' is set to true.
                boolean isSupportsUpdate = notif.getNewBooleanValue();
                if (isSupportsUpdate) {
                    // If the ignore flag hasnt been set, show the dialog
                    if (!ignoreTableSupportsUpdateChangedTrue) {
                        final String dialogTitle = UiConstants.Util.getString("TransformationNotificationListener.tableSupportsUpdateChanged.title"); //$NON-NLS-1$
                        final String disableFutureMessage = UiConstants.Util.getString("TransformationNotificationListener.tableSupportsUpdateChangedTrue.disableFutureDialogMessage"); //$NON-NLS-1$
                        final String message = UiConstants.Util.getString("TransformationNotificationListener.tableSupportsUpdateChangedTrue.message"); //$NON-NLS-1$
                        // Prompt whether to add the Group Elements to the query
                        // put on SWT thread
                        UiUtil.runInSwtThread(new Runnable() {
                            public void run() {
                                // Ensure that preference is false initially, this may be reset
                                UiPlugin.getDefault().getPreferenceStore().setValue(PluginConstants.Prefs.TableSupportsUpdateChange.IGNORE_TABLE_SUPPORTSUPDATE_CHANGED_TRUE,
                                                                                    ""); //$NON-NLS-1$
                                // show the info dialog
                                MessageDialogWithToggle dialog = MessageDialogWithToggle.openYesNoQuestion(UiUtil.getWorkbenchWindow().getShell(),
                                                                                                           dialogTitle,
                                                                                                           message,
                                                                                                           disableFutureMessage,
                                                                                                           false,
                                                                                                           UiPlugin.getDefault().getPreferenceStore(),
                                                                                                           PluginConstants.Prefs.TableSupportsUpdateChange.IGNORE_TABLE_SUPPORTSUPDATE_CHANGED_TRUE);
                                int retCode = dialog.getReturnCode();
                                if (retCode == IDialogConstants.YES_ID) {
                                    setColumnsUpdateableOnTableUpdateable = true;
                                } else {
                                    setColumnsUpdateableOnTableUpdateable = false;
                                }
                            }
                        },
                                              true);
                        String ignoreChangesStr = UiPlugin.getDefault().getPreferenceStore().getString(PluginConstants.Prefs.TableSupportsUpdateChange.IGNORE_TABLE_SUPPORTSUPDATE_CHANGED_TRUE);
                        if (MessageDialogWithToggle.ALWAYS.equals(ignoreChangesStr)) {
                            ignoreTableSupportsUpdateChangedTrue = true;
                        }
                    }
                    if (setColumnsUpdateableOnTableUpdateable) {
                        Object table = notif.getNotifier();
                        TransformationHelper.setTableColumnsSupportsUpdate(table, true);
                    }
                    // Handle the case where 'supportsUpdate' is set to false
                } else {
                    // If the ignore flag hasnt been set, show the dialog
                    if (!ignoreTableSupportsUpdateChangedFalse) {
                        final String dialogTitle = UiConstants.Util.getString("TransformationNotificationListener.tableSupportsUpdateChanged.title"); //$NON-NLS-1$
                        final String disableFutureMessage = UiConstants.Util.getString("TransformationNotificationListener.tableSupportsUpdateChangedFalse.disableFutureDialogMessage"); //$NON-NLS-1$
                        final String message = UiConstants.Util.getString("TransformationNotificationListener.tableSupportsUpdateChangedFalse.message"); //$NON-NLS-1$
                        // Show Table supports update dialog
                        UiUtil.runInSwtThread(new Runnable() {
                            public void run() {
                                // Ensure that preference is false initially, this may be reset
                                UiPlugin.getDefault().getPreferenceStore().setValue(PluginConstants.Prefs.TableSupportsUpdateChange.IGNORE_TABLE_SUPPORTSUPDATE_CHANGED_FALSE,
                                                                                    ""); //$NON-NLS-1$
                                // show the info dialog
                                MessageDialogWithToggle.openInformation(UiUtil.getWorkbenchWindow().getShell(),
                                                                        dialogTitle,
                                                                        message,
                                                                        disableFutureMessage,
                                                                        false,
                                                                        UiPlugin.getDefault().getPreferenceStore(),
                                                                        PluginConstants.Prefs.TableSupportsUpdateChange.IGNORE_TABLE_SUPPORTSUPDATE_CHANGED_FALSE);
                            }
                        },
                                              true);
                        String ignoreChangesStr = UiPlugin.getDefault().getPreferenceStore().getString(PluginConstants.Prefs.TableSupportsUpdateChange.IGNORE_TABLE_SUPPORTSUPDATE_CHANGED_FALSE);
                        if (MessageDialogWithToggle.ALWAYS.equals(ignoreChangesStr)) {
                            ignoreTableSupportsUpdateChangedFalse = true;
                        }
                    }
                }
            }
            
            notif = getTableMaterializedViewChange(notifications);
            if( notif != null ) {
            	boolean isMaterialized = notif.getNewBooleanValue();
            	if( !isMaterialized ) {
            		// Need to remove the reference
                    Object table = notif.getNotifier();
                    if( TransformationHelper.isVirtualSqlTable(table) ) {
                    	((Table)table).setMaterializedTable(null);
                    }
            	}
            	
            }

            SqlMappingRootCache.invalidateSelectStatus(mappingRoot, true, txnSource);
        }
    }

    /* 
     * Handler method for Virtual Table Column Add notifications
     * @param notifications the collection of notifications
     * @param txnSource the source for the transaction
     */
    private void handleTargetVirtualTableColumnAddNotifications( Collection notifications,
                                                                 Object txnSource ) {
        if (!notifications.isEmpty()) {
            // Get added Columns
            List addedColumns = getSqlColumnsAdded(notifications);

            // Get mapping root for the SqlTransformation
            EObject mappingRoot = getMappingRootFromVTableNotifications(notifications);
            if (!addedColumns.isEmpty()) {
                // Start txn
                boolean requiredStart = ModelerCore.startTxn(NOT_SIGNIFICANT, IS_UNDOABLE, "Create atttr mappings", this); //$NON-NLS-1$
                boolean succeeded = false;
                try {
                    Iterator iter = addedColumns.iterator();
                    boolean colsAdded = false;
                    while (iter.hasNext()) {
                        Object column = iter.next();
                        // Add Attribute Mapping for the new Attributes
                        if (TransformationHelper.isSqlColumn(column)) {
                            AttributeMappingHelper.createAttributeMapping(mappingRoot, (EObject)column, txnSource);
                            colsAdded = true;
                        }
                    }
                    if (colsAdded) {
                        SqlMappingRootCache.invalidateSelectStatus(mappingRoot, true, txnSource);
                    }
                    succeeded = true;
                } finally {
                    // If we start txn, commit it
                    if (requiredStart) {
                        if (succeeded) {
                            ModelerCore.commitTxn();
                        } else {
                            ModelerCore.rollbackTxn();
                        }
                    }
                }

            }
        }
    }

    /* 
     * Handler method for Virtual Table Column Remove notifications
     * @param notifications the collection of notifications
     * @param txnSource the source for the transaction
     */
    private void handleTargetVirtualTableColumnRemoveNotifications( Collection notifications,
                                                                    Object txnSource ) {
        if (!notifications.isEmpty()) {
            // Get removed target Elements
            List removedColumns = getSqlColumnsRemoved(notifications);

            // Get mapping root for the SqlTransformation
            EObject mappingRoot = getMappingRootFromVTableNotifications(notifications);
            if (!removedColumns.isEmpty()) {
                // Start txn
                boolean requiredStart = ModelerCore.startTxn(NOT_SIGNIFICANT, IS_UNDOABLE, "Remove atttr mappings", this); //$NON-NLS-1$
                boolean succeeded = false;
                try {
                    // for defect 15131 -- do not overwrite dirty Teditor changes without a prompt
                    boolean overwriteDirty = false;
                    // Do not update SQL if source is TransformationObjectEditorPage
                    if (!(txnSource instanceof TransformationObjectEditorPage)) {
                        // First, discover if editor is dirty:
                        TransformationObjectEditorPage transOEP = null;
                        boolean isDirty = false;
                        // is editor open?
                        ModelResource mdlRsrc = ModelUtilities.getModelResourceForModelObject(mappingRoot);
                        IResource resource = mdlRsrc.getResource();
                        ModelEditor medit = ModelEditorManager.getModelEditorForFile((IFile)resource, false);
                        if (medit != null) {
                            // Yes, check to see if dirty:
                            ModelObjectEditorPage moep = medit.getActiveObjectEditor();
                            if (moep != null && moep instanceof TransformationObjectEditorPage) {
                                transOEP = (TransformationObjectEditorPage)moep;
                                if (transOEP.hasPendingChanges()) {
                                    // yes, pending T-Editor changes:
                                    isDirty = true;
                                } // endif -- has pending
                            } // endif -- instance of TEditor
                        } // endif -- editor is open for object

                        if (isDirty) {
                            // yes, editor is dirty.

                            // build message:
                            final String message = UiConstants.Util.getString("TransformationNotificationListener.saveBeforeChangesMsg", //$NON-NLS-1$
                                                                              resource.getName());

                            // build buttons, and bad notes, if any:
                            final String[] radioButtons = {SBC_RADIO_SAVE, SBC_RADIO_IGNORE, SBC_RADIO_HALT};
                            final IStatus[] radioErrors = {null, null, null};

                            // is saved valid?
                            boolean savedValid = TransformationHelper.isValid(mappingRoot, QueryValidator.SELECT_TRNS);
                            if (!savedValid) {
                                // add a warning:
                                radioErrors[1] = new Status(IStatus.WARNING, TransformationPlugin.PLUGIN_ID, -1, SBC_NOTE, null);
                            } // endif
                            // is dirty valid?
                            String dirtysql = transOEP.getCurrentSqlEditor().getText(); // the Teditor page should be loaded in
                            // transOEP already
                            QueryValidator qv = new TransformationValidator((SqlTransformationMappingRoot)mappingRoot, false);
                            QueryValidationResult qvr = qv.validateSql(dirtysql, QueryValidator.SELECT_TRNS, false);
                            boolean dirtyValid = qvr.isParsable();
                            if (!dirtyValid) {
                                // add a warning:
                                radioErrors[0] = new Status(IStatus.WARNING, TransformationPlugin.PLUGIN_ID, -1, SBC_NOTE, null);
                            } // endif

                            // Note that the first radio in the array (save) is selected by default.
                            // put on SWT thread
                            UiUtil.runInSwtThread(new Runnable() {
                                public void run() {
                                    uiIntegerResult = RadioMessageDialog.openMulti(UiUtil.getWorkbenchWindow().getShell(),
                                                                                   MessageDialog.WARNING,
                                                                                   SAVE_BEFORE_CHANGE_TITLE,
                                                                                   message,
                                                                                   SBC_GROUP_TITLE,
                                                                                   radioButtons,
                                                                                   radioErrors,
                                                                                   0); // 0=default radio=save
                                }
                            }, true);

                            int result = uiIntegerResult;

                            switch (result) {
                                case 0: // save:
                                {
                                    // selected save chgs first:
                                    TransformationHelper.setSelectSqlString(mappingRoot, dirtysql, NOT_SIGNIFICANT, txnSource);
                                    TransformationSqlHelper.updateAllSqlOnElementsRemoved(mappingRoot, removedColumns, txnSource);
                                    overwriteDirty = true;
                                }
                                    break;
                                case 1: // ignore:
                                {
                                    // ignore changes, so overwrite:
                                    TransformationSqlHelper.updateAllSqlOnElementsRemoved(mappingRoot, removedColumns, txnSource);
                                    overwriteDirty = true;
                                }
                                    break;
                                case 2: // halt:
                                {
                                    // don't save changes, don't overwrite, don't do anything:
                                    overwriteDirty = false;
                                }
                                    break;

                                default:
                                    break;
                            } // endswitch
                        } else {
                            // no, not open or not dirty, proceed as normal (ie, how it used to work):
                            // If the SELECT is valid, may need to prompt whether to remove Elements...
                            if (!(txnSource instanceof ReconcileTransformationAction)
                                && shouldRemoveElemsFromSelect(mappingRoot, removedColumns)) {
                                TransformationSqlHelper.updateAllSqlOnElementsRemoved(mappingRoot, removedColumns, txnSource);
                                overwriteDirty = true; // user asked for it
                            } // endif -- shouldRemove
                        } // endif -- isDirty
                    }
                    Iterator iter = removedColumns.iterator();
                    boolean mappingRemoved = false;
                    while (iter.hasNext()) {
                        Object column = iter.next();
                        // Remove Attribute Mappings for the removed Attributes
                        if (TransformationHelper.isSqlColumn(column)) {
                            AttributeMappingHelper.removeAttributeMapping(mappingRoot, (EObject)column, txnSource);
                            mappingRemoved = true;
                        }
                    }
                    if (mappingRemoved) {
                        SqlMappingRootCache.invalidateSelectStatus(mappingRoot, true, txnSource, overwriteDirty);
                    }
                    succeeded = true;
                } finally {
                    // If we start txn, commit it
                    if (requiredStart) {
                        if (succeeded) {
                            ModelerCore.commitTxn();
                        } else {
                            ModelerCore.rollbackTxn();
                        }
                    }
                }
            }
        }
    }

    /* 
     * Handler method for Procedure ResultSet Column Add notifications
     * @param notifications the collection of notifications
     * @param txnSource the source for the transaction
     */
    private void handleTargetProcedureResultSetColumnAddNotifications( Collection notifications,
                                                                       Object txnSource ) {
        if (!notifications.isEmpty()) {
            // Get added Columns
            List addedColumns = getSqlColumnsAdded(notifications);

            // Get mapping root for the SqlTransformation
            EObject mappingRoot = getMappingRootFromProcResultSetColumnNotifications(notifications);
            if (!addedColumns.isEmpty()) {
                // Start txn
                boolean requiredStart = ModelerCore.startTxn(NOT_SIGNIFICANT, IS_UNDOABLE, "Add atttr mappings", this); //$NON-NLS-1$
                boolean succeeded = false;
                try {
                    Iterator iter = addedColumns.iterator();
                    while (iter.hasNext()) {
                        Object column = iter.next();
                        // Add Attribute Mapping for the new Attributes
                        if (TransformationHelper.isSqlColumn(column)) {
                            AttributeMappingHelper.createAttributeMapping(mappingRoot, (EObject)column, txnSource);
                        }
                    }
                    succeeded = true;
                } finally {
                    // If we start txn, commit it
                    if (requiredStart) {
                        if (succeeded) {
                            ModelerCore.commitTxn();
                        } else {
                            ModelerCore.rollbackTxn();
                        }
                    }
                }

            }
        }
    }

    /* 
     * Handler method for Procedure ResultSet Column Remove notifications
     * @param notifications the collection of notifications
     * @param txnSource the source for the transaction
     */
    private void handleTargetProcedureResultSetColumnRemoveNotifications( Collection notifications,
                                                                          Object txnSource ) {
        if (!notifications.isEmpty()) {
            // Get removed Columns
            List removedColumns = getSqlColumnsRemoved(notifications);

            // Get mapping root for the SqlTransformation
            EObject mappingRoot = getMappingRootFromProcResultSetColumnNotifications(notifications);
            if (!removedColumns.isEmpty()) {
                // Start txn
                boolean requiredStart = ModelerCore.startTxn(NOT_SIGNIFICANT, IS_UNDOABLE, "Remove atttr mappings", this); //$NON-NLS-1$
                boolean succeeded = false;
                try {
                    Iterator iter = removedColumns.iterator();
                    while (iter.hasNext()) {
                        Object column = iter.next();
                        // Remove Attribute Mappings for the removed Attributes
                        if (TransformationHelper.isSqlColumn(column)) {
                            AttributeMappingHelper.removeAttributeMapping(mappingRoot, (EObject)column, txnSource);
                        }
                    }
                    succeeded = true;
                } finally {
                    // If we start txn, commit it
                    if (requiredStart) {
                        if (succeeded) {
                            ModelerCore.commitTxn();
                        } else {
                            ModelerCore.rollbackTxn();
                        }
                    }
                }

            }
        }
    }

    /* 
     * Handler method for Procedure ResultSet or Parameter Add notifications.  The added objects
     * may either be a resultSet or ProcedureParameter.
     * @param notifications the collection of notifications
     * @param txnSource the source for the transaction
     */
    private void handleTargetProcedureResultSetOrParamAddNotifications( Collection notifications,
                                                                        Object txnSource ) {
        if (!notifications.isEmpty()) {
            // Get mapping root for the SqlTransformation
            EObject mappingRoot = getMappingRootFromProcResultSetOrParamNotifications(notifications);
            // start txn if not already in txn
            boolean requiredStart = ModelerCore.startTxn(NOT_SIGNIFICANT, IS_UNDOABLE, "Update attr mappings", this); //$NON-NLS-1$
            boolean succeeded = false;
            try {
                // Invalidate cached Status regardless of resultSet or parameter addition
                SqlMappingRootCache.invalidateSelectStatus(mappingRoot, true, txnSource);
                // Update attribute mappings
                AttributeMappingHelper.updateAttributeMappings(mappingRoot, txnSource);
                succeeded = true;
            } finally {
                // If we start txn, commit it
                if (requiredStart) {
                    if (succeeded) {
                        ModelerCore.commitTxn();
                    } else {
                        ModelerCore.rollbackTxn();
                    }
                }
            }

        }
    }

    /* 
     * Handler method for Procedure ResultSet or Parameter Remove notifications.  The removed objects
     * may either be a resultSet or ProcedureParameter.
     * @param notifications the collection of notifications
     * @param txnSource the source for the transaction
     */
    private void handleTargetProcedureResultSetOrParamRemoveNotifications( Collection notifications,
                                                                           Object txnSource ) {
        if (!notifications.isEmpty()) {
            // Get mapping root for the SqlTransformation
            EObject mappingRoot = getMappingRootFromProcResultSetOrParamNotifications(notifications);

            // start txn if not already in txn
            boolean requiredStart = ModelerCore.startTxn(NOT_SIGNIFICANT, IS_UNDOABLE, "Update attr mappings", this); //$NON-NLS-1$
            boolean succeeded = false;

            try {
                // Invalidate cached Status regardless of resultSet or parameter removal
                SqlMappingRootCache.invalidateSelectStatus(mappingRoot, true, txnSource);

                // Get Target attributes
                List targetAttrs = TransformationHelper.getTransformationTargetAttributes(mappingRoot);
                if (targetAttrs != null && !targetAttrs.isEmpty()) {
                    SqlMappingRootCache.invalidateSelectStatus(mappingRoot, true, txnSource);
                    AttributeMappingHelper.updateAttributeMappings(mappingRoot, txnSource);
                    succeeded = true;
                }
            } finally {
                // If we start txn, commit it
                if (requiredStart) {
                    if (succeeded) {
                        ModelerCore.commitTxn();
                    } else {
                        ModelerCore.rollbackTxn();
                    }
                }
            }
        }
    }

    /* 
     * Handler method for ModelRename notifications
     * @param notifications the collection of notifications
     * @param txnSource the source for the transaction
     */
    private void handleModelRenameNotifications( Collection notifications,
                                                 Object txnSource ) {
        if (!notifications.isEmpty()) {
            Iterator iter = notifications.iterator();
            while (iter.hasNext()) {
                Notification notification = (Notification)iter.next();
                if (NotificationUtilities.isChanged(notification)) {
                	// Get all Transformation Roots for the resource and clean out the cache for ALL of them
                	Object changedObj = ModelerCore.getModelEditor().getChangedObject(notification);
                	ModelResource modelResource = ModelUtilities.getModelResource(changedObj);
                	if( modelResource != null ) {
                		
	                    try {
							// Process all transformations in the TransformationContainer
							final EmfResource emfRes = (EmfResource)modelResource.getEmfResource();
							
							final List transformations = emfRes.getModelContents().getTransformations();
							for (Iterator i = transformations.iterator(); i.hasNext();) {
							    EObject eObj = (EObject)i.next();
							    if (eObj instanceof SqlTransformationMappingRoot) {
							        SqlTransformationMappingRoot mappingRoot = (SqlTransformationMappingRoot)eObj;

						        	SqlMappingRootCache.invalidateSelectStatus(mappingRoot, true, txnSource);
						        	SqlMappingRootCache.invalidateInsertStatus(mappingRoot, true, txnSource);
						        	SqlMappingRootCache.invalidateUpdateStatus(mappingRoot, true, txnSource);
						        	SqlMappingRootCache.invalidateDeleteStatus(mappingRoot, true, txnSource);
							    }
							}
						} catch (ModelWorkspaceException e) {
							Util.log(IStatus.ERROR, e, e.getMessage());
						}
                	}
                }
            }
        }
    }

    /* 
     * Handler method for Undo notifications.  Basically, the undo/redo notifications are ignored unless
     * the SQL statement has changed.  In the event that the SQL is being changed, then the only thing that 
     * needs to be done is to invalidate the SqlMappingRootCache.  This insures that the cache will not be
     * stale.
     * @param notifications the collection of notifications
     */
    private void handleUndo( Collection notifications,
                             Object txnSource ) {
        if (!notifications.isEmpty()) {
            // Check for SQL Modifications - if SQL mods, then cache needs to be invalidated
            Collection selectUserNotifications = getSqlSelectUserStatementChangeNotifications(notifications);
            Collection selectUIDNotifications = getSqlSelectUIDStatementChangeNotifications(notifications);
            // If UID SQL changed, invalidate cache status
            if (!selectUIDNotifications.isEmpty()) {
                EObject mappingRoot = getMappingRootFromSqlUIDStatementNotifications(selectUIDNotifications);
                SqlMappingRootCache.invalidateSelectStatus(mappingRoot, true, txnSource);
                // else, If User SQL changed, invalidate cache status
            } else if (!selectUserNotifications.isEmpty()) {
                EObject mappingRoot = getMappingRootFromSqlUserStatementNotifications(selectUserNotifications);
                SqlMappingRootCache.invalidateSelectStatus(mappingRoot, true, txnSource);
            }
        }
    }

    /* 
     * Get MappingRoot from SqlAlias Notifications
     * @param sqlAliasNotifications the SqlAlias Notifications
     * @return the mappingRoot EObject
     */
    private EObject getMappingRootFromSqlAliasNotifications( Collection sqlAliasNotifications ) {
        EObject mappingRoot = null;
        Iterator iter = sqlAliasNotifications.iterator();
        Notification firstNotification = (Notification)iter.next();
        if (firstNotification != null) {
            // The changed Object is the SqlTransformation
            Object changedObj = ModelerCore.getModelEditor().getChangedObject(firstNotification);
            // Mapping root for the SqlTransformation
            mappingRoot = TransformationHelper.getMappingRoot((MappingHelper)changedObj);
        }
        return mappingRoot;
    }

    /* 
     * Get MappingRoot from VirtualTable Notifications
     * @param vTableNotifications the VirtualTable Notifications
     * @return the mappingRoot EObject
     */
    private EObject getMappingRootFromVTableNotifications( Collection vTableNotifications ) {
        EObject mappingRoot = null;
        Iterator iter = vTableNotifications.iterator();
        Notification firstNotification = (Notification)iter.next();
        if (firstNotification != null) {
            // The changed Object is the Transformation target
            Object changedObj = ModelerCore.getModelEditor().getChangedObject(firstNotification);
            // Mapping root for the SqlTransformation
            mappingRoot = TransformationHelper.getTransformationMappingRoot((EObject)changedObj);
        }
        return mappingRoot;
    }

    /* 
     * Get MappingRoot from Procedure ResultSet Notifications
     * @param resultSetNotifications the Procedure ResultSet Notifications
     * @return the mappingRoot EObject
     */
    private EObject getMappingRootFromProcResultSetColumnNotifications( Collection resultSetNotifications ) {
        EObject mappingRoot = null;
        Iterator iter = resultSetNotifications.iterator();
        Notification firstNotification = (Notification)iter.next();
        if (firstNotification != null) {
            // The changed Object is the ResultSet
            Object resultSet = ModelerCore.getModelEditor().getChangedObject(firstNotification);
            if (TransformationHelper.isSqlColumnSet(resultSet)) {
                // The parent of the ResultSet is the Procedure
                EObject procedure = ((EObject)resultSet).eContainer();
                // Mapping root for the SqlTransformation
                mappingRoot = TransformationHelper.getTransformationMappingRoot(procedure);
            }
        }
        return mappingRoot;
    }

    /* 
     * Get MappingRoot from Procedure ResultSet Notifications
     * @param resultSetNotifications the Procedure ResultSet or Parameter Notifications
     * @return the mappingRoot EObject
     */
    private EObject getMappingRootFromProcResultSetOrParamNotifications( Collection resultSetNotifications ) {
        EObject mappingRoot = null;
        Iterator iter = resultSetNotifications.iterator();
        Notification firstNotification = (Notification)iter.next();
        if (firstNotification != null) {
            // The changed Object is the Procedure
            Object procedure = ModelerCore.getModelEditor().getChangedObject(firstNotification);
            if (TransformationHelper.isSqlProcedure(procedure)) {
                // Mapping root for the SqlTransformation
                mappingRoot = TransformationHelper.getTransformationMappingRoot((EObject)procedure);
            }
        }
        return mappingRoot;
    }

    /* 
     * Get MappingRoot from UID SqlStatement change Notifications
     * @param notification the collection of UID SqlStatement Notifications
     * @return the mappingRoot EObject
     */
    private EObject getMappingRootFromSqlUIDStatementNotifications( Collection sqlNotifications ) {
        EObject mappingRoot = null;
        Iterator iter = sqlNotifications.iterator();
        Notification firstNotification = (Notification)iter.next();
        if (firstNotification != null) {
            // The changed Object is the SqlTransformation
            Object changedObj = ModelerCore.getModelEditor().getChangedObject(firstNotification);
            // Mapping root for the UID SqlTransformation.
            mappingRoot = TransformationHelper.getMappingRoot((MappingHelper)changedObj);
        }
        return mappingRoot;
    }

    /* 
     * Get MappingRoot from UID SqlStatement change Notifications
     * @param notification the collection of UID SqlStatement Notifications
     * @return the mappingRoot EObject
     */
    private EObject getMappingRootFromSqlUserStatementNotifications( Collection sqlNotifications ) {
        EObject mappingRoot = null;
        Iterator iter = sqlNotifications.iterator();
        Notification firstNotification = (Notification)iter.next();
        if (firstNotification != null) {
            // The changed Object is the SqlTransformation
            Object changedObj = ModelerCore.getModelEditor().getChangedObject(firstNotification);

            // The parent is the UID SqlTransformation - it has the correct MappingRoot
            EObject parentEObj = ((EObject)changedObj).eContainer();

            // Mapping root for the UID SqlTransformation.
            mappingRoot = TransformationHelper.getMappingRoot((MappingHelper)parentEObj);
        }
        return mappingRoot;
    }

    /* 
     * Get SqlAliases added, given a Collection of Add Notifications
     * @param addNotifications the add Notifications
     * @return the List of SqlAlias objects
     */
    private List getSqlAliasesAdded( Collection addNotifications ) {
        List sqlAliasList = new ArrayList();
        Iterator iter = addNotifications.iterator();
        while (iter.hasNext()) {
            Notification notification = (Notification)iter.next();
            EObject[] newChildren = NotificationUtilities.getAddedChildren(notification);
            for (int i = 0; i < newChildren.length; i++) {
                if (newChildren[i] instanceof SqlAlias && !sqlAliasList.contains(newChildren[i])) {
                    sqlAliasList.add(newChildren[i]);
                }
            }
        }
        return sqlAliasList;
    }

    /* 
     * Get SqlAliases removed, given a Collection of Remove Notifications
     * @param removeNotifications the remove Notifications
     * @return the List of SqlAlias objects
     */
    private List getSqlAliasesRemoved( Collection removeNotifications ) {
        List sqlAliasList = new ArrayList();
        Iterator iter = removeNotifications.iterator();
        while (iter.hasNext()) {
            Notification notification = (Notification)iter.next();
            EObject[] newChildren = NotificationUtilities.getRemovedChildren(notification);
            for (int i = 0; i < newChildren.length; i++) {
                if (newChildren[i] instanceof SqlAlias && !sqlAliasList.contains(newChildren[i])) {
                    sqlAliasList.add(newChildren[i]);
                }
            }
        }
        return sqlAliasList;
    }

    /* 
     * Get SqlAliases changed, given a Collection of Change Notifications
     * @param changeNotifications the change Notifications
     * @return the List of SqlAlias objects
     */
    private List getSqlAliasesChanged( Collection changeNotifications ) {
        List sqlAliasList = new ArrayList();
        Iterator iter = changeNotifications.iterator();
        while (iter.hasNext()) {
            Notification notification = (Notification)iter.next();
            // The old Value represents the SqlAlias or Aliases that were removed
            Object removedAlias = notification.getOldValue();
            if (removedAlias instanceof SqlAlias && !sqlAliasList.contains(removedAlias)) {
                sqlAliasList.add(removedAlias);
            } else if (removedAlias instanceof List) {
                Iterator listIter = ((List)removedAlias).iterator();
                while (listIter.hasNext()) {
                    Object nextObj = listIter.next();
                    if (nextObj instanceof SqlAlias && !sqlAliasList.contains(nextObj)) {
                        sqlAliasList.add(nextObj);
                    }
                }
            }
        }
        return sqlAliasList;
    }

    /* 
     * Get SqlColumns added, given a Collection of Add Notifications
     * @param addNotifications the add Notifications
     * @return the List of SqlColumns
     */
    private List getSqlColumnsAdded( Collection addNotifications ) {
        List tableColumnList = new ArrayList();
        Iterator iter = addNotifications.iterator();
        while (iter.hasNext()) {
            Notification notification = (Notification)iter.next();
            EObject[] newChildren = NotificationUtilities.getAddedChildren(notification);
            for (int i = 0; i < newChildren.length; i++) {
                if (TransformationHelper.isSqlColumn(newChildren[i]) && !tableColumnList.contains(newChildren[i])) {
                    tableColumnList.add(newChildren[i]);
                }
            }
        }
        return tableColumnList;
    }

    /* 
     * Get SqlColumns removed, given a Collection of Remove Notifications
     * @param removeNotifications the remove Notifications
     * @return the List of SqlColumns
     */
    private List getSqlColumnsRemoved( Collection removeNotifications ) {
        List tableColumnList = new ArrayList();
        Iterator iter = removeNotifications.iterator();
        while (iter.hasNext()) {
            Notification notification = (Notification)iter.next();
            EObject[] removedChildren = NotificationUtilities.getRemovedChildren(notification);
            for (int i = 0; i < removedChildren.length; i++) {
                if (TransformationHelper.isSqlColumn(removedChildren[i]) && !tableColumnList.contains(removedChildren[i])) {
                    tableColumnList.add(removedChildren[i]);
                }
            }
        }
        return tableColumnList;
    }

    /* 
     * Get SqlTables added, given a Collection of Column Add Notifications
     * @param addNotifications the add Notifications
     * @return the List of SqlTables
     */
    private Set getSqlTablesAddedTo( Collection addNotifications ) {
        Set tableSet = new HashSet();
        Iterator iter = addNotifications.iterator();
        while (iter.hasNext()) {
            Notification notification = (Notification)iter.next();
            // Get the object that changed - table
            Object table = ModelerCore.getModelEditor().getChangedObject(notification);
            tableSet.add(table);
        }
        return tableSet;
    }

    /* 
     * Check whether any of the supplied EObjects are SqlAliases
     * @param eObjects the array of EObjects
     * @return 'true' if any are SqlAliases, 'false' if not
     */
    private boolean containsSqlAlias( EObject[] eObjects ) {
        boolean hasSqlAlias = false;
        for (int i = 0; i < eObjects.length; i++) {
            if (eObjects[i] instanceof SqlAlias) {
                hasSqlAlias = true;
                break;
            }
        }
        return hasSqlAlias;
    }

    /* 
     * Method to determine whether the Notification source is the TransformationSqlHelper
     * @param source the source to test
     * @return 'true' if the source is the SqlTransformationHelper, 'false' if not.
     */
    private boolean isSqlHelperSource( Object source ) {
        boolean isSqlHelper = false;
        if (source != null && source.equals(TransformationSqlHelper.getInstance())) {
            isSqlHelper = true;
        }
        return isSqlHelper;
    }

    /* 
     * Method to determine whether the notification is for change of the SqlAliases.
     * @param notification the notification
     * @return 'true' if the feature changed is SqlAliases, 'false' if not.
     */
    private boolean sqlAliasesChanged( Notification notification ) {
        boolean aliasesChanged = false;

        // Make sure that the changed Object is SqlTransformation
        Object changedObj = ModelerCore.getModelEditor().getChangedObject(notification);
        if (changedObj instanceof SqlTransformation) {
            // See if the changed feature is the aliases
            int changedFeature = notification.getFeatureID(SqlTransformation.class);

            if (changedFeature == TransformationPackage.SQL_TRANSFORMATION__ALIASES) {
                aliasesChanged = true;
            }
        }
        return aliasesChanged;
    }

    /* 
     * Method to determine whether the notification is for a change of one of the SELECT
     * UID SQL statements.
     * @param notification the notification
     * @return 'true' if the feature changed is one of the UID SQL Statements, 'false' if not.
     */
    private boolean sqlSelectUIDStatementChanged( Notification notification ) {
        boolean selectUIDStatementChanged = false;
        int changedFeature = notification.getFeatureID(SqlTransformation.class);
        // Changed Feature is Sql SELECT
        if (changedFeature == TransformationPackage.SQL_TRANSFORMATION__SELECT_SQL) {
            // Get the object that changed - SqlTransformation
            Object changedObj = ModelerCore.getModelEditor().getChangedObject(notification);
            if (changedObj != null && changedObj instanceof SqlTransformation) {
                // Get the Parent of the changed Object (SqlTransformation)
                EObject parent = ((EObject)changedObj).eContainer();
                // Only consider notification if the parent is NOT a SqlTransformation
                // Ignore SqlTransformation that has SqlTransformation parent (UserTransformation -string version)
                if (!TransformationHelper.isSqlTransformation(parent)) {
                    selectUIDStatementChanged = true;
                }
            }
        }
        return selectUIDStatementChanged;
    }

    /* 
     * Method to determine whether the notification is for a change of one of the SELECT
     * User SQL statements.
     * @param notification the notification
     * @return 'true' if the feature changed is one of the User SQL Statements, 'false' if not.
     */
    private boolean sqlSelectUserStatementChanged( Notification notification ) {
        boolean selectUserStatementChanged = false;
        int changedFeature = notification.getFeatureID(SqlTransformation.class);
        // Changed Feature is Sql SELECT
        if (changedFeature == TransformationPackage.SQL_TRANSFORMATION__SELECT_SQL) {
            // Get the object that changed - SqlTransformation
            Object changedObj = ModelerCore.getModelEditor().getChangedObject(notification);
            if (changedObj != null && changedObj instanceof SqlTransformation) {
                // Get the Parent of the changed Object (SqlTransformation)
                EObject parent = ((EObject)changedObj).eContainer();
                // The User SqlTransformation has a SqlTransformation parent
                if (TransformationHelper.isSqlTransformation(parent)) {
                    selectUserStatementChanged = true;
                }
            }
        }
        return selectUserStatementChanged;
    }

    /* 
     * Method to determine whether group elements should be added to the select.  The user will
     * be prompted if the transformation SELECT query is a Valid query other than a SELECT *.  For
     * a valid SELECT * query, it is assumed that the Group Elements are to be added.
     * @param mappingRoot the transformation MappingRoot
     * @return 'true' if elems should be added for added groups, 'false' if not.
     */
    private boolean shouldAddElemsToSelect( Object mappingRoot ) {
        // If the SELECT is valid, may need to prompt whether to add Elements...
        boolean addElemsToSelect = false;
        if (TransformationHelper.isValid(mappingRoot, QueryValidator.SELECT_TRNS)) {
            Command selectCommand = SqlMappingRootCache.getSelectCommand(mappingRoot);
            if (selectCommand instanceof Query) {
                // Check if callbacks are disabled
                IPreferenceStore prefStore = UiPlugin.getDefault().getPreferenceStore();
                boolean disableCallbacks = prefStore.getBoolean(PluginConstants.Prefs.Callbacks.DISABLE_CALLBACKS);
                Select querySelect = ((Query)selectCommand).getSelect();
                // Query is Valid, but not "SELECT *" - ask the user
                if (!TransformationSqlHelper.isSelectStar(querySelect) && !disableCallbacks) {
                    final String message = UiConstants.Util.getString("TransformationNotificationListener.addSQLElemGrpAttrsMsg", //$NON-NLS-1$
                                                                      DEFAULT_ADDED_SOURCE);
                    // Prompt whether to add the Group Elements to the query
                    // put on SWT thread
                    UiUtil.runInSwtThread(new Runnable() {
                        public void run() {
                            uiBooleanResult = MessageDialog.openQuestion(UiUtil.getWorkbenchWindow().getShell(),
                                                                         ADD_SQL_ELEM_GRP_REFS_TITLE,
                                                                         message);
                        }
                    }, true);

                    addElemsToSelect = uiBooleanResult;
                    // Query is Valid "SELECT *", elements should be added
                } else {
                    addElemsToSelect = true;
                }
            }

        }
        return addElemsToSelect;
    }

    /* 
     * Method to determine whether group elements should be removed from the select.  The user will
     * be prompted if the transformation SELECT query is a Valid query other than a SELECT *.  For
     * a valid SELECT * query, it is assumed that the Group Elements are to be removed.
     * @param mappingRoot the transformation MappingRoot
     * @param sqlAliasGroups the SqlAlias groups being removed
     * @return 'true' if elems should be added for added groups, 'false' if not.
     */
    private boolean shouldRemoveGroupElemsFromSelect( Object mappingRoot,
                                                      List sqlAliasGroups ) {
        // If the SELECT is valid, may need to prompt whether to remove Elements...
        boolean removeElemsFromSelect = false;
        if (TransformationHelper.isValid(mappingRoot, QueryValidator.SELECT_TRNS)) {
            Command selectCommand = SqlMappingRootCache.getSelectCommand(mappingRoot);
            if (selectCommand instanceof Query) {
                // Check if callbacks are disabled
                IPreferenceStore prefStore = UiPlugin.getDefault().getPreferenceStore();
                boolean disableCallbacks = prefStore.getBoolean(PluginConstants.Prefs.Callbacks.DISABLE_CALLBACKS);
                Select querySelect = ((Query)selectCommand).getSelect();
                // Query is Valid, but not "SELECT *" - ask the user
                if (!TransformationSqlHelper.isSelectStar(querySelect)
                    && TransformationSqlHelper.hasSqlAliasGroupAttributes((Query)selectCommand, sqlAliasGroups)
                    && !disableCallbacks) {
                    final String message = UiConstants.Util.getString("TransformationNotificationListener.removeSQLElemGrpRefsMsg", //$NON-NLS-1$
                                                                      DEFAULT_REMOVED_SOURCE);
                    // Prompt whether to remove the Group Elements from the query
                    // put on SWT thread
                    UiUtil.runInSwtThread(new Runnable() {
                        public void run() {
                            uiBooleanResult = MessageDialog.openQuestion(UiUtil.getWorkbenchWindow().getShell(),
                                                                         REMOVE_SQL_ELEM_GRP_REFS_TITLE,
                                                                         message);
                        }
                    }, true);
                    removeElemsFromSelect = uiBooleanResult;
                    // Query is Valid "SELECT *", elements should be removed
                } else {
                    removeElemsFromSelect = true;
                }
            }

        }
        return removeElemsFromSelect;
    }

    /* 
     * Method to determine whether elements should be removed from the select.  The user will
     * be prompted if the transformation SELECT query is a Valid query other than a SELECT *.  For
     * a valid SELECT * query, it is assumed that the Group Elements are to be removed.
     * @param mappingRoot the transformation MappingRoot
     * @param sqlColumns the SQL columns being removed
     * @return 'true' if elems should be removed, 'false' if not.
     */
    private boolean shouldRemoveElemsFromSelect( Object mappingRoot,
                                                 List sqlColumns ) {
        // If the SELECT is valid, may need to prompt whether to remove Elements...
        boolean removeElemsFromSelect = false;
        if (TransformationHelper.isValid(mappingRoot, QueryValidator.SELECT_TRNS)) {
            Command selectCommand = SqlMappingRootCache.getSelectCommand(mappingRoot);
            Query selectQuery = null;

            if (selectCommand instanceof QueryCommand) {
                selectQuery = ((QueryCommand)selectCommand).getProjectedQuery();
            }
            if (selectQuery != null) {
                // Check if callbacks are disabled
                IPreferenceStore prefStore = UiPlugin.getDefault().getPreferenceStore();
                boolean disableCallbacks = prefStore.getBoolean(PluginConstants.Prefs.Callbacks.DISABLE_CALLBACKS);
                // Query has the projecte SQL symbols
                if (TransformationSqlHelper.hasSqlElemSymbols(selectQuery, sqlColumns) && !disableCallbacks) {
                    // Prompt whether to remove the Elements from the query
                    // put on SWT thread
                    UiUtil.runInSwtThread(new Runnable() {
                        public void run() {
                            uiBooleanResult = MessageDialog.openQuestion(UiUtil.getWorkbenchWindow().getShell(),
                                                                         REMOVE_SQL_ELEMS_TITLE,
                                                                         REMOVE_SQL_ELEMS_MSG);
                        }
                    }, true);
                    removeElemsFromSelect = uiBooleanResult;
                    // Query is Valid "SELECT *", elements should be removed
                } else {
                    removeElemsFromSelect = true;
                }
            }

        }
        return removeElemsFromSelect;
    }

    /**
     * @see com.metamatrix.core.event.EventObjectListener#processEvent(java.util.EventObject)
     * @since 4.2
     */
    public void processEvent( EventObject obj ) {
        ModelResourceEvent event = (ModelResourceEvent)obj;
        if (event.getType() == ModelResourceEvent.RELOADED) {
            Display.getDefault().asyncExec(new Runnable() {
                public void run() {
                    SqlMappingRootCache.invalidateRootsOnProjectOrModelRemove();
                }
            });
        }
    }

    private class WorkspaceNotificationListener implements ModelWorkspaceNotificationListener {

        public WorkspaceNotificationListener() {
        }

        public void notifyAdd( ModelWorkspaceNotification notification ) {
        }

        public void notifyRemove( ModelWorkspaceNotification notification ) {
            // Invalidate the SqlMappingRoot cache on Project or Model Removal
            if (notification.isPostChange()) {
                // Project Close/Remove notification
                if (notification.isProject()) {
                    Display.getDefault().asyncExec(new Runnable() {
                        public void run() {
                            SqlMappingRootCache.invalidateRootsOnProjectOrModelRemove();
                        }
                    });
                    // Model Remove notification
                } else if (notification.isFile()) {
                    IResource resource = (IResource)notification.getNotifier();
                    if (ModelUtil.isModelFile(resource)) {
                        Display.getDefault().asyncExec(new Runnable() {
                            public void run() {
                                SqlMappingRootCache.invalidateRootsOnProjectOrModelRemove();
                            }
                        });
                    }
                }
            }
        }

        public void notifyMove( ModelWorkspaceNotification notification ) {
        }

        public void notifyRename( ModelWorkspaceNotification notification ) {
        }

        public void notifyOpen( ModelWorkspaceNotification notification ) {
        }

        public void notifyClosing( ModelWorkspaceNotification notification ) {
        }

        public void notifyChanged( Notification theNotification ) {
        }

        public void notifyReloaded( ModelWorkspaceNotification notification ) {
        }

        public void notifyClean( final IProject proj ) {
            Display.getDefault().asyncExec(new Runnable() {
                public void run() {
                    SqlMappingRootCache.invalidateCacheForProject(proj);
                }
            });
        }

    }
}
