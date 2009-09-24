/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.preferences;

import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;

import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.internal.ui.PluginConstants;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.UiPlugin;


/**
 * This general preference controls whether an editor is automatically opened when needed by the
 * application. 
 * @since 5.0
 */
public class AutoOpenEditorPreferenceContributor implements IGeneralPreferencePageContributor,
                                                            UiConstants {
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTANTS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    private static final String PREF_ID = PluginConstants.Prefs.General.AUTO_OPEN_EDITOR_IF_NEEDED;
    
    private static final String PREFIX = I18nUtil.getPropertyPrefix(AutoOpenEditorPreferenceContributor.class);
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    private Button btn;
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /** 
     * @see com.metamatrix.modeler.internal.ui.preferences.IGeneralPreferencePageContributor#createPreferenceEditor(org.eclipse.swt.widgets.Composite)
     * @since 5.0
     */
    public void createPreferenceEditor(Composite theParent) {
        this.btn = new Button(theParent, SWT.CHECK);
        this.btn.setText(Util.getStringOrKey(PREFIX + "btn.text")); //$NON-NLS-1$
        
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

        if (value.equals(MessageDialogWithToggle.ALWAYS)) {
            result = true;
        }

        return result;
    }

    /** 
     * @see com.metamatrix.modeler.internal.ui.preferences.IGeneralPreferencePageContributor#getName()
     * @since 5.0
     */
    public String getName() {
        return Util.getStringOrKey(PREFIX + "name"); //$NON-NLS-1$
    }

    /** 
     * @see com.metamatrix.modeler.internal.ui.preferences.IGeneralPreferencePageContributor#getToolTip()
     * @since 5.0
     */
    public String getToolTip() {
        return Util.getStringOrKey(PREFIX + "toolTip"); //$NON-NLS-1$
    }

    /** 
     * @see com.metamatrix.modeler.internal.ui.preferences.IGeneralPreferencePageContributor#performCancel()
     * @since 5.0
     */
    public boolean performCancel() {
        return true;
    }

    /** 
     * @see com.metamatrix.modeler.internal.ui.preferences.IGeneralPreferencePageContributor#performDefaults()
     * @since 5.0
     */
    public boolean performDefaults() {
        this.btn.setSelection(getPreferenceStoreValue(true));
        return true;
    }

    /** 
     * @see com.metamatrix.modeler.internal.ui.preferences.IGeneralPreferencePageContributor#performOk()
     * @since 5.0
     */
    public boolean performOk() {
        String value = MessageDialogWithToggle.NEVER;
        
        if (this.btn.getSelection()) {
            value = MessageDialogWithToggle.ALWAYS;
        }

        getPreferenceStore().setValue(PREF_ID, value);
        return true;
    }
    
    /** 
     * @see com.metamatrix.modeler.internal.ui.preferences.IGeneralPreferencePageContributor#refresh()
     * @since 5.0
     */
    public void refresh() {
        this.btn.setSelection(getPreferenceStoreValue(false));
    }
    
    /** 
     * @see com.metamatrix.modeler.internal.ui.preferences.IGeneralPreferencePageContributor#setWorkbench(org.eclipse.ui.IWorkbench)
     * @since 5.0
     */
    public void setWorkbench(IWorkbench theWorkbench) {
    }

}
