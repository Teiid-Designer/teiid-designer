/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ui.preferences;

import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.teiid.designer.ui.PluginConstants;
import org.teiid.designer.ui.UiConstants;
import org.teiid.designer.ui.UiPlugin;



/**
 * This general preference controls whether an the logical model gets created as Relational (default)
 * or as UML whem importing via MetaIntegration importers 
 */
public class LogicalAsRelationalPreferenceContributor implements IGeneralPreferencePageContributor,
                                                            UiConstants {
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTANTS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    private static final String PREF_ID = PluginConstants.Prefs.General.LOGICAL_AS_RELATIONAL;
    
     
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    private Button btn;
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /** 
     * @see org.teiid.designer.ui.preferences.IGeneralPreferencePageContributor#createPreferenceEditor(org.eclipse.swt.widgets.Composite)
     * @since 5.0
     */
    @Override
	public void createPreferenceEditor(Composite theParent) {
        this.btn = new Button(theParent, SWT.CHECK);
        this.btn.setText(Util.getStringOrKey("LogicalAsRelationalPreferenceContributor.btn.text")); //$NON-NLS-1$
        
        // initialize state
        refresh();
    }

    /**
     * Obtains the <code>IPreferenceStore</code> where this preference is being persisted.
     * @return the preference store
     * @since 5.0
     */
    private IPreferenceStore getPreferenceStore() {
        return UiPlugin.getDefault().getPreferenceStore();
    }

    /**
     * Obtains the {@link IPreferenceStore}'s default or current value for this preference
     * @param theDefaultFlag the flag indicating if the default or current value is being requested
     * @return the value
     * @since 5.0
     */
    private boolean getPreferenceStoreValue(boolean theDefaultFlag) {
    	boolean result = false;
        IPreferenceStore prefStore = getPreferenceStore();
        String value = null;
        
        if (theDefaultFlag) {
            value = prefStore.getDefaultString(PREF_ID);
        } else {
            value = prefStore.getString(PREF_ID);
        }

        if ( (value.length() == 0) || (value.equals(MessageDialogWithToggle.ALWAYS) )) {
            result = true;
        }

        return result;
    }

    /** 
     * @see org.teiid.designer.ui.preferences.IGeneralPreferencePageContributor#getName()
     * @since 5.0
     */
    @Override
	public String getName() {
        return Util.getStringOrKey("LogicalAsRelationalPreferenceContributor.name"); //$NON-NLS-1$
    }

    /** 
     * @see org.teiid.designer.ui.preferences.IGeneralPreferencePageContributor#getToolTip()
     * @since 5.0
     */
    @Override
	public String getToolTip() {
        return Util.getStringOrKey("LogicalAsRelationalPreferenceContributor.toolTip"); //$NON-NLS-1$
    }

    /** 
     * @see org.teiid.designer.ui.preferences.IGeneralPreferencePageContributor#performCancel()
     * @since 5.0
     */
    @Override
	public boolean performCancel() {
        return true;
    }

    /** 
     * @see org.teiid.designer.ui.preferences.IGeneralPreferencePageContributor#performDefaults()
     * @since 5.0
     */
    @Override
	public boolean performDefaults() {
        this.btn.setSelection(getPreferenceStoreValue(true));
        return true;
    }

    /** 
     * @see org.teiid.designer.ui.preferences.IGeneralPreferencePageContributor#performOk()
     * @since 5.0
     */
    @Override
	public boolean performOk() {
        String value = MessageDialogWithToggle.NEVER;
        
        if (this.btn.getSelection()) {
            value = MessageDialogWithToggle.ALWAYS;
        }

        getPreferenceStore().setValue(PREF_ID, value);
        return true;
    }
    
    /** 
     * @see org.teiid.designer.ui.preferences.IGeneralPreferencePageContributor#refresh()
     * @since 5.0
     */
    @Override
	public void refresh() {
        this.btn.setSelection(getPreferenceStoreValue(false));
    }
    
    /** 
     * @see org.teiid.designer.ui.preferences.IGeneralPreferencePageContributor#setWorkbench(org.eclipse.ui.IWorkbench)
     * @since 5.0
     */
    @Override
	public void setWorkbench(IWorkbench theWorkbench) {
    }

}
