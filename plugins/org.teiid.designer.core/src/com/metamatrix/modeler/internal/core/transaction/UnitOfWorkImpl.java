/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.transaction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.CompoundCommand;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreEList;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.command.RemoveCommand;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.edit.provider.ChangeNotifier;
import com.metamatrix.core.MetaMatrixRuntimeException;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.ModelerCoreRuntimeException;
import com.metamatrix.modeler.core.container.Container;
import com.metamatrix.modeler.core.transaction.TransactionStateConstants;
import com.metamatrix.modeler.core.transaction.Undoable;
import com.metamatrix.modeler.core.transaction.UnitOfWork;
import com.metamatrix.modeler.core.util.ExternalResourceImportsHelper;
import com.metamatrix.modeler.core.util.ProcessedNotificationResult;
import com.metamatrix.modeler.internal.core.resource.EmfResourceSet;

/**
 * @author lphillips
 * @since 3.1
 */
public class UnitOfWorkImpl implements UnitOfWork {

    private final TxnNotificationFilter filter;
    private final Set resourcesChanged;
    private final Collection removedEObjects;
    private Object id;
    private int state;
    private CompoundCommand txnCommand;
    private Container container;
    private boolean significant;
    private boolean isUndoable;
    private String description;
    private Object source;
    private boolean overrideRollback;
    private boolean alreadyExecuted;
    private final Set processedNotificationResults;

    // ==================================================================================
    // C O N S T R U C T O R S
    // ==================================================================================

    /**
     * Constructor for UnitOfWorkImpl.
     */
    public UnitOfWorkImpl( final ResourceSet resources ) {
        this(resources, Integer.MAX_VALUE);
    }

    /**
     * Constructor for UnitOfWorkImpl.
     * 
     * @param The resourceSet to use for editing domain creation
     * @param The max wait time for commit and rollback blocking.
     */
    public UnitOfWorkImpl( final ResourceSet resources,
                           final int waitTimeouts ) {
        if (resources == null) {
            final String msg = ModelerCore.Util.getString("UnitOfWorkImpl.The_ResourceSet_reference_may_not_be_null"); //$NON-NLS-1$
            throw new IllegalArgumentException(msg);
        }
        // if ( !(resources instanceof EmfResourceSet )) {
        //            final String msg = ModelerCore.Util.getString("UnitOfWorkImpl.The_supplied_ResourceSet_must_be_an_implementation_of_EmfResourceSet"); //$NON-NLS-1$
        // throw new IllegalArgumentException(msg);
        // }
        this.resourcesChanged = new HashSet();
        this.removedEObjects = new HashSet();
        if (resources instanceof EmfResourceSet) {
            this.container = ((EmfResourceSet)resources).getContainer();
        } else {
            try {
                this.container = ModelerCore.getModelContainer();
            } catch (CoreException err) {
                throw new MetaMatrixRuntimeException(err);
            }
        }
        this.filter = new TxnNotificationFilter(resources);
        setState(TransactionStateConstants.UNINITIALIZED);
        this.isUndoable = true;
        processedNotificationResults = new HashSet();
    }

    // ==================================================================================
    // P U B L I C M E T H O D S
    // ==================================================================================

    public Collection getRemovedEObjects() {
        return new HashSet(this.removedEObjects);
    }

    /**
     * Begin the transaction.
     */
    public void begin() {
        if (isStarted()) {
            CoreArgCheck.isTrue(false, ModelerCore.Util.getString("UnitOfWorkImpl.Transaction_already_started_1")); //$NON-NLS-1$
        }

        if (state != TransactionStateConstants.COMPLETE && state != TransactionStateConstants.UNINITIALIZED
            && state != TransactionStateConstants.FAILED) {
            throw new ModelerCoreRuntimeException(
                                                  ModelerCore.Util.getString("UnitOfWorkImpl.Invalid_transaction_state_prior_to_begin", TransactionStateConstants.getDisplayValue(this.state))); //$NON-NLS-1$
        }

        this.description = null;
        this.id = UnitOfWorkProviderImpl.getIdFactory().create();
        this.significant = true;
        this.isUndoable = true;
        this.filter.clear();
        this.resourcesChanged.clear();
        this.removedEObjects.clear();
        setState(TransactionStateConstants.STARTED);
        overrideRollback = false;
        this.processedNotificationResults.clear();
    }

    /**
     * @return
     */
    public Object getId() {
        return this.id;
    }

    /**
     * Call back method for the EmfAdapter to notify the transaction of an event notification Add the notification to the
     * EventStack to be processed on a commit.
     * 
     * @param notification
     */
    public void processNotification( final Notification notification ) throws ModelerCoreException {
        if (!isStarted() && !isRollingBack() && !isCommitting()) {
            int preState = getState();
            setState(TransactionStateConstants.FAILED);
            throw new ModelerCoreException(
                                           ModelerCore.Util.getString("UnitOfWorkImpl.Invalid_transaction_state_prior_to_processing_notification", TransactionStateConstants.getDisplayValue(preState))); //$NON-NLS-1$
        }
        CoreArgCheck.isNotNull(notification);

        // keep track of resources whose modified property has been modified.
        if (notification.getNotifier() instanceof Resource && notification.getEventType() == Notification.SET
            && notification.getFeatureID(Resource.class) == Resource.RESOURCE__IS_MODIFIED) {

            // add if resource has been modified, remove if it is not modified
            if (notification.getNewBooleanValue()) {
                this.resourcesChanged.add(notification.getNotifier());
            } else {
                this.resourcesChanged.remove(notification.getNotifier());
            }
        }

        this.filter.addNotification(notification);
    }

    /**
     * @return the UoW source attribute
     */
    public Object getSource() {
        return this.source;
    }

    /**
     * Set the UoW source attribute
     * 
     * @param object
     */
    public void setSource( Object object ) {
        this.source = object;
    }

    /**
     * Process all the notifications on the event queue, flush the command stack and create the undoable edit
     */
    public void commit() throws ModelerCoreException {
        boolean success = false;
        try {
            if (!isStarted() && !isRollingBack()) {
                int preState = getState();
                throw new ModelerCoreException(
                                               ModelerCore.Util.getString("UnitOfWorkImpl.Invalid_transaction_state_prior_to_commit", TransactionStateConstants.getDisplayValue(preState))); //$NON-NLS-1$
            }

            postProcessNotificationResults();

            setState(TransactionStateConstants.COMMITTING);

            // Create and fire the Undoable Edit event
            if (!overrideRollback && this.txnCommand != null && this.isUndoable) {
                final UnitOfWorkProviderImpl uowp = (UnitOfWorkProviderImpl)this.container.getEmfTransactionProvider();
                final Undoable undoable = new UndoableImpl(this.container, this.txnCommand, new ArrayList(this.resourcesChanged),
                                                           this.id, this.source);
                undoable.setSignificant(this.significant);
                if (this.description != null) {
                    undoable.setDescription(this.description);
                }

                this.txnCommand = null;
                uowp.processUndoable(undoable);
            }

            success = true;
        } catch (Throwable e) {
            success = false;
            setState(TransactionStateConstants.FAILED);
            rollback();
            throw new ModelerCoreException(e, ModelerCore.Util.getString("UnitOfWorkImpl.Error_committing_transaction")); //$NON-NLS-1$
        } finally {
            if (success) {
                setState(TransactionStateConstants.COMPLETE);

                try {

                    // Get the filtered and compressed set of notifications to fire
                    final List notifications = this.filter.getSourcedNotifications(this.source);
                    if (ModelerCore.DEBUG_NOTIFICATIONS) {
                        helpPrintFilterResults(notifications, this.description);
                    }

                    if (!notifications.isEmpty()) {
                        final ChangeNotifier notifier = this.container.getChangeNotifier();
                        try {
                            for (Iterator i = notifications.iterator(); i.hasNext();) {
                                Notification n = (Notification)i.next();
                                notifier.fireNotifyChanged(n);
                            }
                        } catch (Throwable e1) {
                            ModelerCore.Util.log(IStatus.ERROR,
                                                 e1,
                                                 ModelerCore.Util.getString("UnitOfWorkImpl.Error_processing_notification_1")); //$NON-NLS-1$
                        }
                    }

                } catch (Throwable e1) {
                    ModelerCore.Util.log(IStatus.ERROR,
                                         e1,
                                         ModelerCore.Util.getString("UnitOfWorkImpl.Error_processing_notifications____1") + e1.getMessage()); //$NON-NLS-1$
                }
            } else {
                setState(TransactionStateConstants.FAILED);
            }
            // Clear the event manager
            this.filter.clear();

            // remove this UOW from the list of current UOWs
            cleanup();
        }
    }

    public void helpPrintFilterResults( final List sns,
                                        final String description ) {
        if (sns == null || sns.isEmpty()) {
            return;
        }
        if (!CoreStringUtil.isEmpty(description)) {
            System.out.println("\n" + description); //$NON-NLS-1$
            System.out.println("TxnNotificationFilter result:"); //$NON-NLS-1$
        } else {
            System.out.println("\nTxnNotificationFilter result:"); //$NON-NLS-1$
        }
        for (Iterator i = sns.iterator(); i.hasNext();) {
            SourcedNotificationImpl sn = (SourcedNotificationImpl)i.next();
            System.out.println(sn.getNotifier());
            System.out.println("   primary notification: " + sn.getPrimaryNotification()); //$NON-NLS-1$
            for (Iterator j = sn.getNotifications().iterator(); j.hasNext();) {
                Notification n = (Notification)j.next();
                System.out.println("      notification: " + n);//$NON-NLS-1$
            }
        }
    }

    /**
     * Setter for description attribute. Used when creating the undoable.
     * 
     * @param description
     */
    public void setDescription( String description ) {
        this.description = description;
    }

    /**
     * Undo all the txnCommand, flush the command stack and event queue and create and undoable edit
     * 
     * @see com.metamatrix.api.mtk.core.transaction.MtkTransaction#rollback()
     */
    public void rollback() {
        boolean success = false;
        try {
            if (!isStarted() && !isFailed()) {
                int preState = getState();
                throw new ModelerCoreException(
                                               ModelerCore.Util.getString("UnitOfWorkImpl.Invalid_transaction_state_prior_to_rollback", TransactionStateConstants.getDisplayValue(preState))); //$NON-NLS-1$
            }

            setState(TransactionStateConstants.ROLLING_BACK);

            if (!overrideRollback && this.txnCommand != null && !this.txnCommand.isEmpty()) {
                // Capture the txn cmd info and reset the attribute as this txn is reused for the rollback.
                // If you don't you will get concurrent modifications when rolling back this txnCommand as
                // you add to it via the rollback.
                CompoundCommand tmp = this.txnCommand;
                this.txnCommand = null;
                tmp.undo();
            }

            this.filter.clear();
            this.txnCommand = null;

            // remove this UOW from the list of current UOWs
            cleanup();
            success = true;
        } catch (Exception e) {
            success = false;
            ModelerCore.Util.log(IStatus.ERROR, e, ModelerCore.Util.getString("UnitOfWorkImpl.Error_rolling_back_transaction")); //$NON-NLS-1$
        } finally {
            if (success) {
                setState(TransactionStateConstants.COMPLETE);
            } else {
                setState(TransactionStateConstants.FAILED);
            }
        }
    }

    /**
     * Pass through to the editing domain / command stack to execute the command
     * 
     * @return true if the command was executed, or false if it could not be executed
     * @see com.metamatrix.mtk.emf.container.container.transaction.api.UnitOfWork#executeCommand(Command)
     */
    public boolean executeCommand( final Command command ) throws ModelerCoreException {
        if (!isStarted() && !isRollingBack()) {
            int preState = getState();
            setState(TransactionStateConstants.FAILED);
            throw new ModelerCoreException(
                                           ModelerCore.Util.getString("UnitOfWorkImpl.Transaction_must_be_started_before_you_can_execute_commands", TransactionStateConstants.getDisplayValue(preState))); //$NON-NLS-1$
        }

        if (!command.canExecute()) {
            final String msg = ModelerCore.Util.getString("UnitOfWorkImpl.Command_object_not_executable", command); //$NON-NLS-1$
            if (ModelerCore.DEBUG) {
                ModelerCore.Util.log(IStatus.ERROR, msg);
            }
            command.dispose();
            throw new ModelerCoreException(msg);
        }

        // Ensure txnCommand != null
        if (!overrideRollback) {
            if (this.txnCommand == null) {
                this.txnCommand = new CompoundCommand(command.getLabel());
            }
            // Add new Command to txnCommand
            this.txnCommand.append(command);
        }

        if (!this.alreadyExecuted) {
            try {
                command.execute();
            } catch (RuntimeException exception) {
                final Object[] params = new Object[] {command, exception.getMessage()};
                final String msg = ModelerCore.Util.getString("UnitOfWorkImpl.error_executing_command", params); //$NON-NLS-1$

                command.dispose();
                throw new ModelerCoreException(exception, msg);
            }
        }

        updateRemovedEObjects(command);
        return true;
    }

    /**
     * @see com.metamatrix.api.mtk.core.transaction.MtkTransaction#isStarted()
     */
    public boolean isStarted() {
        return getState() == TransactionStateConstants.STARTED;
    }

    /**
     * @see com.metamatrix.api.mtk.core.transaction.MtkTransaction#isCommitting()
     */
    public boolean isCommitting() {
        return getState() == TransactionStateConstants.COMMITTING;
    }

    /**
     * @see com.metamatrix.api.mtk.core.transaction.MtkTransaction#isCommitting()
     */
    public boolean isComplete() {
        return getState() == TransactionStateConstants.COMPLETE;
    }

    /**
     * @see com.metamatrix.api.mtk.core.transaction.MtkTransaction#isRollingBack()
     */
    public boolean isRollingBack() {
        return getState() == TransactionStateConstants.ROLLING_BACK;
    }

    /**
     * @see com.metamatrix.api.mtk.core.transaction.MtkTransaction#isFailed()
     */
    public boolean isFailed() {
        return getState() == TransactionStateConstants.FAILED;
    }

    public boolean requiresStart() {
        switch (this.state) {
            case TransactionStateConstants.COMMITTING:
                return false;
            case TransactionStateConstants.STARTED:
                return false;
            case TransactionStateConstants.ROLLING_BACK:
                return false;
            default:
                return true;
        }
    }

    /**
     * Sets this significant flag which is passed to the undoable edit;
     * 
     * @param b
     */
    public void setSignificant( boolean b ) throws ModelerCoreException {
        if (!isStarted()) {
            int preState = getState();
            throw new ModelerCoreException(
                                           ModelerCore.Util.getString("UnitOfWorkImpl.Invalid_Unit_of_Work_State___May_only_set_isSignificant_on_started_Unit_of_Work_3", TransactionStateConstants.getDisplayValue(preState))); //$NON-NLS-1$
        }

        this.significant = b;
    }

    /**
     * @return the isUndoable flag
     */
    public boolean isUndoable() {
        return this.isUndoable;
    }

    /**
     * Set the isUndoable flag
     * 
     * @param b
     */
    public void setUndoable( boolean b ) {
        this.isUndoable = b;
    }

    /**
     * overrideRollback is a class variable which, when true, suppresses some of the txn commands needed by the UNDO framework.
     * See setOverrideRollback(boolean b)
     * 
     * @return
     * @since 5.0.2
     */
    public boolean isOverrideRollback() {
        return this.overrideRollback;
    }

    /**
     * Method to override the rollback functionality of this class and place it on the Source of the transaction. Even if a txn
     * was NOT undoable, we were still treating it under the hood as undoable and this was potentially causing memory issues for
     * txn's where a large amount of work was being done (i.e. New Model Wizards, XML Document model builder, etc.) If set to
     * TRUE, then the txn Command will NOT be created and txn's will NOT be cached. This is implemented to allow actions like
     * NewModelWizard to NOT care about the undoablity of any of the add/remove/change commands created when building the model.
     * 
     * @return
     * @since 5.0.2
     */
    public void setOverrideRollback( boolean b ) {
        this.overrideRollback = b;
    }

    /**
     * @param alreadyExecuted True if this unit of work has already been executed and all that is left to be done is to handle the
     *        undo stack.
     * @since 5.0.3
     */
    public void setAlreadyExecuted( boolean alreadyExecuted ) {
        this.alreadyExecuted = alreadyExecuted;
    }

    @Override
    public String toString() {
        if (this.description == null) {
            return this.getClass().getName() + " : " + TransactionStateConstants.getDisplayValue(getState()); //$NON-NLS-1$
        }
        return this.description + " : " + TransactionStateConstants.getDisplayValue(getState()); //$NON-NLS-1$
    }

    /**
     * Returns the state.
     * 
     * @return int
     */
    public int getState() {
        return this.state;
    }

    /**
     * Method add a new ProcessedNotificationResult for post-commit processing. Method looks for existing result referencing the
     * same resource, then appends the dereferenced resources to it's list, else it adds a new result to the cache for use in
     * final processing.
     * 
     * @return
     * @since 5.0.2
     */
    public void addProcessedNotificationResult( ProcessedNotificationResult result ) {
        if (processedNotificationResults.isEmpty()) {
            processedNotificationResults.add(result);
        } else {
            boolean foundMatchingResource = false;
            ProcessedNotificationResult nextResult = null;
            for (Iterator iter = processedNotificationResults.iterator(); iter.hasNext();) {
                nextResult = (ProcessedNotificationResult)iter.next();
                if (nextResult.getTargetResource() == result.getTargetResource()) {
                    foundMatchingResource = true;
                    // Add the externalResources to the existing result
                    nextResult.addDereferencedResources(result.getDereferencedResources());
                }
                if (foundMatchingResource) {
                    break;
                }
            }
            if (!foundMatchingResource) {
                processedNotificationResults.add(result);
            }
        }
    }

    private void postProcessNotificationResults() {
        if (!processedNotificationResults.isEmpty()) {
            ExternalResourceImportsHelper.processNotificationResults(processedNotificationResults);
        }
        // Always clear
        processedNotificationResults.clear();
    }

    // ==================================================================================
    // P R I V A T E M E T H O D S
    // ==================================================================================

    /**
     * Sets the state.
     * 
     * @param state The state to set
     */
    private void setState( int state ) {
        this.state = state;

        if (ModelerCore.DEBUG_TRANSACTION) {
            switch (state) {
                case TransactionStateConstants.FAILED:
                    ModelerCore.Util.log(IStatus.INFO, ModelerCore.Util.getString("UnitOfWorkImpl.Setting_state_to_FAILED_12")); //$NON-NLS-1$
                    Thread.dumpStack();
                    break;
                case TransactionStateConstants.STARTED:
                    ModelerCore.Util.log(IStatus.INFO, ModelerCore.Util.getString("UnitOfWorkImpl.Setting_state_to_STARTED_13")); //$NON-NLS-1$
                    break;
                case TransactionStateConstants.COMPLETE:
                    ModelerCore.Util.log(IStatus.INFO, ModelerCore.Util.getString("UnitOfWorkImpl.Setting_state_to_COMPLETE_14")); //$NON-NLS-1$
                    break;
                case TransactionStateConstants.COMMITTING:
                    ModelerCore.Util.log(IStatus.INFO,
                                         ModelerCore.Util.getString("UnitOfWorkImpl.Setting_state_to_COMMITTING_15")); //$NON-NLS-1$
                    break;
                case TransactionStateConstants.ROLLING_BACK:
                    ModelerCore.Util.log(IStatus.INFO,
                                         ModelerCore.Util.getString("UnitOfWorkImpl.Setting_state_to_ROLLING_BACK_16")); //$NON-NLS-1$
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Try to remove this UOW from the UOW Provider and cleanup instance variables.
     */
    private void cleanup() {
        this.id = null;
        this.significant = true;
        this.isUndoable = true;
        this.source = null;
        this.removedEObjects.clear();
        this.resourcesChanged.clear();
        this.filter.clear();

        this.container.getEmfTransactionProvider().cleanup(Thread.currentThread());
    }

    private void updateRemovedEObjects( final Command cmd ) {
        if (cmd instanceof RemoveCommand) {
            final RemoveCommand remove = (RemoveCommand)cmd;
            final Collection list = remove.getCollection();
            final EStructuralFeature sf = remove.getFeature();
            if (sf != null && sf instanceof EReference) {
                final EReference ref = (EReference)sf;
                if (ref.isContainment()) {
                    this.removedEObjects.addAll(list);
                } else if (ref.isContainer()) {
                    this.removedEObjects.addAll(list);
                }
            } else if (list != null && !list.isEmpty()) {
                final EList ownerList = remove.getOwnerList();
                if (ownerList instanceof EcoreEList) {
                    final EcoreEList eList = (EcoreEList)ownerList;
                    final EStructuralFeature listSf = eList.getEStructuralFeature();
                    final Iterator vals = list.iterator();
                    while (vals.hasNext()) {
                        final Object next = vals.next();
                        if (next instanceof EObject) {
                            final EStructuralFeature ownerSf = ((EObject)next).eContainmentFeature();
                            if (ownerSf == listSf && ownerSf != null) {
                                this.removedEObjects.add(next);
                            }
                        }
                    }
                }
            }
        } else if (cmd instanceof SetCommand) {
            final SetCommand set = (SetCommand)cmd;
            final EStructuralFeature sf = set.getFeature();
            if (sf instanceof EReference) {
                final EReference ref = (EReference)sf;
                if (ref.isContainment() || ref.isContainer()) {
                    Object oldVal = set.getOldValue();
                    if (oldVal != null && set.getValue() == null) {
                        if (oldVal instanceof Collection) {
                            this.removedEObjects.addAll((Collection)oldVal);
                        } else if (oldVal instanceof EObject) {
                            this.removedEObjects.add(oldVal);
                        }
                    } else if (oldVal == null && set.getValue() != null) {
                        final Object val = set.getValue();
                        if (val instanceof Collection) {
                            this.removedEObjects.removeAll((Collection)val);
                        } else if (val instanceof EObject) {
                            this.removedEObjects.remove(val);
                        }
                    }
                } else if (ref.isContainer()) {
                    Object oldVal = set.getOldValue();
                    if (oldVal != null && set.getValue() == null) {
                        this.removedEObjects.add(set.getOwner());
                    } else if (oldVal == null && set.getValue() != null) {
                        this.removedEObjects.remove(set.getOwner());
                    }
                }
            }
        } else if (cmd instanceof AddCommand) {
            this.removedEObjects.removeAll(((AddCommand)cmd).getCollection());
        }
    }

}
