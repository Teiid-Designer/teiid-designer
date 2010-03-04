/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.dqp.ui.workspace.actions;

import java.util.List;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.teiid.designer.runtime.Connector;
import org.teiid.designer.runtime.ConnectorType;
import org.teiid.designer.runtime.ExecutionAdmin;
import org.teiid.designer.runtime.SourceBindingsManager;
import com.metamatrix.modeler.dqp.DqpPlugin;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;

/**
 * @since 5.0
 */
public abstract class ConfigurationManagerAction extends Action implements ISelectionChangedListener {

    private static SourceBindingsManager sourceBindingsManager;

    /**
     * Set based on the connector(s), connector type(s) that are selected.
     */
    private ExecutionAdmin admin;

    /** The current selection or <code>null</code>. */
    private ISelection selection;

    /** The current event or <code>null</code> if last event was a workbench selection. */
    private SelectionChangedEvent selectionEvent;

    /**
     * @param theText
     * @since 5.0
     */
    public ConfigurationManagerAction( String theText ) {
        super(theText);
    }

    protected ExecutionAdmin getAdmin() {
        return this.admin;
    }

    public SourceBindingsManager getWorkspaceConfig() {
        if (sourceBindingsManager == null) {
            sourceBindingsManager = DqpPlugin.getInstance().getSourceBindingsManager();
        }
        return sourceBindingsManager;
    }

    public void selectionChanged( SelectionChangedEvent theEvent ) {
        this.admin = null;
        this.selection = theEvent.getSelection();
        this.selectionEvent = theEvent;
        List selectedObjects = getSelectedObjects();

        if (!selectedObjects.isEmpty()) {
            ExecutionAdmin newAdmin = null;
            ExecutionAdmin tempAdmin = null;

            for (Object obj : selectedObjects) {
                if (obj instanceof ConnectorType) {
                    tempAdmin = ((ConnectorType)obj).getAdmin();
                } else if (obj instanceof Connector) {
                    tempAdmin = ((Connector)obj).getType().getAdmin();
                } else {
                    newAdmin = null;
                    break;
                }

                assert (tempAdmin != null);

                if (newAdmin == null) {
                    newAdmin = tempAdmin;
                } else if (!newAdmin.equals(tempAdmin)) {
                    newAdmin = null;
                    break;
                }
            }

            if (newAdmin == null) {
                // disable if no admin set
                setEnabled(false);
            } else {
                // if we have an admin let the subclass decide enablement
                this.admin = newAdmin;
                setEnablement();
            }
        }

    }

    abstract protected void setEnablement();

    /**
     * Gets the selected object in the current selection. If more than one object is selected, the first is returned.
     * 
     * @return the selected object or <code>null</code> if none selected
     */
    public Object getSelectedObject() {
        return SelectionUtilities.getSelectedObject(selection);
    }

    /**
     * Gets all objects in the current selection.
     * 
     * @return the list of all selected objects or an empty list
     */
    public List getSelectedObjects() {
        return SelectionUtilities.getSelectedObjects(selection);
    }

    /**
     * Gets the current workbench selection.
     * 
     * @return the current selection or <code>null</code>
     */
    public ISelection getSelection() {
        return selection;
    }

    /**
     * Gets the current selection event.
     * 
     * @return the selection event or <code>null</code>
     */
    public SelectionChangedEvent getSelectionEvent() {
        return selectionEvent;
    }

    /**
     * Indicates if the current selection is empty.
     * 
     * @return <code>true</code> if there is no object selected; <code>false</code> otherwise.
     */
    public boolean isEmptySelection() {
        return ((selection == null) || selection.isEmpty());
    }

    /**
     * Indicates if the current selection has multiple objects selected.
     * 
     * @return <code>true</code> if multiple objects are selected; <code>false</code> otherwise.
     */
    public boolean isMultiSelection() {
        return SelectionUtilities.isMultiSelection(selection);
    }
}
