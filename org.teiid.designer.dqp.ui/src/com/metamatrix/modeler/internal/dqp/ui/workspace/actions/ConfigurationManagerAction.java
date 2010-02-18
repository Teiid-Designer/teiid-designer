/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.dqp.ui.workspace.actions;

import java.util.List;
import org.apache.log4j.lf5.viewer.configure.ConfigurationManager;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.teiid.designer.runtime.ServerAdmin;
import com.metamatrix.modeler.dqp.DqpPlugin;
import com.metamatrix.modeler.dqp.internal.workspace.WorkspaceConfigurationManager;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;


/** 
 * @since 5.0
 */
public abstract class ConfigurationManagerAction extends Action implements ISelectionChangedListener {

    private static ServerAdmin admin;
    private static WorkspaceConfigurationManager workspaceConfig;
    
    /** The current selection or <code>null</code>. */
    private ISelection selection;
    
    /** The current event or <code>null</code> if last event was a workbench selection. */
    private SelectionChangedEvent selectionEvent;
    
    /** 
     * 
     * @since 5.0
     */
    public ConfigurationManagerAction() {
        super();
    }

    /** 
     * @param theText
     * @since 5.0
     */
    public ConfigurationManagerAction(String theText) {
        super(theText);
    }

    /** 
     * @param theText
     * @param theImage
     * @since 5.0
     */
    public ConfigurationManagerAction(String theText,
                                      ImageDescriptor theImage) {
        super(theText, theImage);
    }

    /** 
     * @param theText
     * @param theStyle
     * @since 5.0
     */
    public ConfigurationManagerAction(String theText,
                                      int theStyle) {
        super(theText, theStyle);
    }

    public ServerAdmin getAdmin() {
        if( this.admin ==  null) {
            this.admin = DqpPlugin.getInstance().getServerAdmin();
        }
        return this.admin;
    }
    
    /**
     * Saves both the configuration.xml file and the WorkspaceConfig.def file 
     * 
     * @since 5.0
     */
    public void save() {
        if( admin != null ) {
            try {
                admin.saveConfig();
            } catch (Exception theException) {
                DqpUiConstants.UTIL.log(IStatus.ERROR, theException.getMessage());
            }
        }
        if( workspaceConfig != null ) {
            try {
                workspaceConfig.save();
            } catch (Exception theException) {
                DqpUiConstants.UTIL.log(IStatus.ERROR, theException.getMessage());
            }
        }
    }
    
    public WorkspaceConfigurationManager getWorkspaceConfig() {
        if(workspaceConfig == null) {
            workspaceConfig = DqpPlugin.getInstance().getWorkspaceConfig();
        }
        return workspaceConfig;
    }

    public void selectionChanged(SelectionChangedEvent theEvent) {
        selection = theEvent.getSelection();
        selectionEvent = theEvent;
        setEnablement();
    }
    
    abstract protected void setEnablement();
    
    /**
     * Gets the selected object in the current selection. If more than one object is selected, the first
     * is returned.
     * @return the selected object or <code>null</code> if none selected
     */
    public Object getSelectedObject() {
        return SelectionUtilities.getSelectedObject(selection);
    }

    /**
     * Gets all objects in the current selection.
     * @return the list of all selected objects or an empty list
     */
    public List getSelectedObjects() {
        return SelectionUtilities.getSelectedObjects(selection);
    }

    /**
     * Gets the current workbench selection.
     * @return the current selection or <code>null</code>
     */
    public ISelection getSelection() {
        return selection;
    }
    
    /**
     * Gets the current selection event.
     * @return the selection event or <code>null</code>
     */
    public SelectionChangedEvent getSelectionEvent() {
        return selectionEvent;
    }
    
    /**
     * Indicates if the current selection is empty.
     * @return <code>true</code> if there is no object selected; <code>false</code> otherwise.
     */
    public boolean isEmptySelection() {
        return ((selection == null) || selection.isEmpty());
    }

    /**
     * Indicates if the current selection has multiple objects selected.
     * @return <code>true</code> if multiple objects are selected; <code>false</code> otherwise.
     */
    public boolean isMultiSelection() {
        return SelectionUtilities.isMultiSelection(selection);
    }
}
