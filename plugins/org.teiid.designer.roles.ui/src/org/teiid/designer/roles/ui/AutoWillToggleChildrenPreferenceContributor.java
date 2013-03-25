/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.roles.ui;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.designer.ui.PluginConstants;
import org.teiid.designer.ui.UiConstants;
import org.teiid.designer.ui.UiPlugin;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.preferences.IGeneralPreferencePageContributor;

/**
 *
 */
public class AutoWillToggleChildrenPreferenceContributor implements
		IGeneralPreferencePageContributor, UiConstants {

	// /////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	// /////////////////////////////////////////////////////////////////////////////////////////////
	
	private static final String PREF_ID = PluginConstants.Prefs.General.AUTO_WILL_TOGGLE_WITH_CHILDREN;

	private static final String PREFIX = I18nUtil
			.getPropertyPrefix(AutoWillToggleChildrenPreferenceContributor.class);

	// /////////////////////////////////////////////////////////////////////////////////////////////
	// FIELDS
	// /////////////////////////////////////////////////////////////////////////////////////////////


    private Button chkEnabled;

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.ui.preferences.IGeneralPreferencePageContributor#createPreferenceEditor(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public void createPreferenceEditor( Composite theParent ) {
        Composite pnl = new Composite(theParent, SWT.NONE);
        pnl.setLayout(new GridLayout());
        pnl.setLayoutData(new GridData());

        this.chkEnabled = WidgetFactory.createCheckBox(pnl, getName());
        this.chkEnabled.setLayoutData(new GridData());
        this.chkEnabled.setToolTipText(getToolTip());

        // initialize state
        refresh();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.ui.preferences.IGeneralPreferencePageContributor#getName()
     */
    @Override
    public String getName() {
        return RolesUiPlugin.UTIL.getStringOrKey(PREFIX + "name"); //$NON-NLS-1$
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
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.ui.preferences.IGeneralPreferencePageContributor#getToolTip()
     */
    @Override
    public String getToolTip() {
        return RolesUiPlugin.UTIL.getStringOrKey(PREFIX + "toolTip"); //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.ui.preferences.IGeneralPreferencePageContributor#performCancel()
     */
    @Override
    public boolean performCancel() {
        return true;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.ui.preferences.IGeneralPreferencePageContributor#performDefaults()
     */
    @Override
    public boolean performDefaults() {
        this.chkEnabled.setSelection(getPreferenceStoreValue(true));
        return true;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.ui.preferences.IGeneralPreferencePageContributor#performOk()
     */
    @Override
    public boolean performOk() {
        String value = MessageDialogWithToggle.NEVER;
        
        if (this.chkEnabled.getSelection()) {
            value = MessageDialogWithToggle.ALWAYS;
        }

        getPreferenceStore().setValue(PREF_ID, value);
        return true;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.ui.preferences.IGeneralPreferencePageContributor#refresh()
     */
    @Override
    public void refresh() {
        this.chkEnabled.setSelection(getPreferenceStoreValue(false));
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.ui.preferences.IGeneralPreferencePageContributor#setWorkbench(org.eclipse.ui.IWorkbench)
     */
    @Override
    public void setWorkbench( IWorkbench theWorkbench ) {
        // nothing to do
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

}