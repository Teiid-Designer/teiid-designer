/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.preferences;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;


/**
 * This contributor is used to contribute preferences to the general preference page. 
 * @since 5.0
 * @see com.metamatrix.modeler.ui.UiConstants.ExtensionPoints.GeneralPreferenceContributor
 */
public interface IGeneralPreferencePageContributor {

    /**
     * Constructs the preference editor. 
     * @param theParent the parent
     * @since 5.0
     */
    void createPreferenceEditor(Composite theParent);
    
    /**
     * The internationalized name of the preference suitable for display on a tab. 
     * @return the name
     * @since 5.0
     */
    String getName();
    
    /**
     * The internationalized tooltip of the preference. 
     * @return the tooltip
     * @since 5.0
     */
    String getToolTip();
    
    /**
     * Informs the contributor that the cancel button was selected by the user. 
     * @return <code>true</code> if cancelling completed successfully; <code>false</code> otherwise.
     * @since 5.0
     */
    boolean performCancel();
    
    /**
     * This method is called when the user presses the restore defaults button. 
     * @return <code>true</code> if restoring defaults completed successfully; <code>false</code> otherwise.
     * @since 5.0
     */
    boolean performDefaults();
    
    /**
     * This method is called when the preference page is OK'd by the user.
     * @return <code>true</code> if finishing completed successfully; <code>false</code> otherwise.
     * @since 5.0
     */
    boolean performOk();
    
    /**
     * Reloads value from preference store. 
     * @since 5.0
     */
    void refresh();

    /**
     * Sets the <code>IWorkbench</code> the preference page is being created in.
     * @param theWorkbench the workbench
     * @since 5.0
     */
    void setWorkbench(IWorkbench theWorkbench);
    
}
