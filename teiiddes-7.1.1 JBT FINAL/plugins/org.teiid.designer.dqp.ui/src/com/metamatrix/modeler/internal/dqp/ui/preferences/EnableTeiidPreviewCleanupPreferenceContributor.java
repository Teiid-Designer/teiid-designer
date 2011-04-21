/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.dqp.ui.preferences;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.osgi.service.prefs.BackingStoreException;
import org.teiid.designer.runtime.PreferenceConstants;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.dqp.DqpPlugin;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.internal.ui.preferences.IGeneralPreferencePageContributor;
import com.metamatrix.ui.internal.util.WidgetFactory;

/**
 * @since 5.0
 */
public class EnableTeiidPreviewCleanupPreferenceContributor implements IGeneralPreferencePageContributor, DqpUiConstants {

    private static final String PREFIX = I18nUtil.getPropertyPrefix(EnableTeiidPreviewCleanupPreferenceContributor.class);

    private Button chkEnabled;

    /**
     * @see com.metamatrix.modeler.internal.ui.preferences.IGeneralPreferencePageContributor#createPreferenceEditor(org.eclipse.swt.widgets.Composite)
     * @since 5.0
     */
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
     * @see com.metamatrix.modeler.internal.ui.preferences.IGeneralPreferencePageContributor#getName()
     * @since 5.0
     */
    public String getName() {
        return UTIL.getStringOrKey(PREFIX + "name"); //$NON-NLS-1$
    }

    /**
     * Obtains the <code>IEclipsePreferences</code> where this preference is being persisted.
     * 
     * @return the preferences
     */
    private IEclipsePreferences getPreferences() {
        return DqpPlugin.getInstance().getPreferences();
    }

    /**
     * @see com.metamatrix.modeler.internal.ui.preferences.IGeneralPreferencePageContributor#getToolTip()
     * @since 5.0
     */
    public String getToolTip() {
        return UTIL.getStringOrKey(PREFIX + "toolTip"); //$NON-NLS-1$
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
        this.chkEnabled.setSelection(PreferenceConstants.PREVIEW_TEIID_CLEANUP_ENABLED_DEFAULT);

        // save
        try {
            getPreferences().flush();
            return true;
        } catch (BackingStoreException e) {
            return false;
        }
    }

    /**
     * @see com.metamatrix.modeler.internal.ui.preferences.IGeneralPreferencePageContributor#performOk()
     * @since 5.0
     */
    public boolean performOk() {
        IEclipsePreferences prefs = getPreferences();
        prefs.putBoolean(PreferenceConstants.PREVIEW_TEIID_CLEANUP_ENABLED, this.chkEnabled.getSelection());

        // save
        try {
            prefs.flush();
            return true;
        } catch (BackingStoreException e) {
            return false;
        }
    }

    /**
     * @see com.metamatrix.modeler.internal.ui.preferences.IGeneralPreferencePageContributor#refresh()
     * @since 5.0
     */
    public void refresh() {
        IEclipsePreferences prefs = getPreferences();
        boolean enable = prefs.getBoolean(PreferenceConstants.PREVIEW_TEIID_CLEANUP_ENABLED,
                                          PreferenceConstants.PREVIEW_TEIID_CLEANUP_ENABLED_DEFAULT);
        this.chkEnabled.setSelection(enable);
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.metamatrix.modeler.internal.ui.preferences.IGeneralPreferencePageContributor#setPreferencePage(org.eclipse.jface.preference.PreferencePage)
     */
    public void setPreferencePage( PreferencePage preferencePage ) {
    }

    /**
     * @see com.metamatrix.modeler.internal.ui.preferences.IGeneralPreferencePageContributor#setWorkbench(org.eclipse.ui.IWorkbench)
     * @since 5.0
     */
    public void setWorkbench( IWorkbench theWorkbench ) {
    }

}
