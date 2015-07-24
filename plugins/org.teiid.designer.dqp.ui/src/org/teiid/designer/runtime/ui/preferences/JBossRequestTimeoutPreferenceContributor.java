/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime.ui.preferences;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.ui.IWorkbench;
import org.osgi.service.prefs.BackingStoreException;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.designer.runtime.DqpPlugin;
import org.teiid.designer.runtime.PreferenceConstants;
import org.teiid.designer.runtime.ui.DqpUiConstants;
import org.teiid.designer.ui.preferences.IGeneralPreferencePageContributor;

/**
 * Allows setting the timeout preference for Jboss request execution timeout
 * @since 9.0.3
 */
public class JBossRequestTimeoutPreferenceContributor implements IGeneralPreferencePageContributor, DqpUiConstants {

    private static final String PREFIX = I18nUtil.getPropertyPrefix(JBossRequestTimeoutPreferenceContributor.class);

    private Spinner timeoutSecTextField;

    /**
     * @see org.teiid.designer.ui.preferences.IGeneralPreferencePageContributor#createPreferenceEditor(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public void createPreferenceEditor(Composite theParent) {
        Composite pnl = new Composite(theParent, SWT.NONE);
        pnl.setLayout(new GridLayout(2, false));
        pnl.setLayoutData(new GridData());

        this.timeoutSecTextField = new Spinner(pnl, SWT.BORDER);
        this.timeoutSecTextField.setToolTipText(getToolTip());
        this.timeoutSecTextField.setMaximum(180);
        this.timeoutSecTextField.setMinimum(10);

        Label label = new Label(pnl, SWT.NONE);
        label.setText(getName());
        label.setToolTipText(getToolTip());

        // initialize state
        refresh();
    }

    /**
     * @see org.teiid.designer.ui.preferences.IGeneralPreferencePageContributor#getName()
     * @since 5.0
     */
    @Override
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
     * @see org.teiid.designer.ui.preferences.IGeneralPreferencePageContributor#getToolTip()
     * @since 5.0
     */
    @Override
    public String getToolTip() {
        return UTIL.getStringOrKey(PREFIX + "toolTip"); //$NON-NLS-1$
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
        this.timeoutSecTextField.setSelection(PreferenceConstants.JBOSS_REQUEST_EXECUTION_TIMEOUT_SEC_DEFAULT);

        // save
        try {
            getPreferences().flush();
            return true;
        } catch (BackingStoreException e) {
            return false;
        }
    }

    /**
     * @see org.teiid.designer.ui.preferences.IGeneralPreferencePageContributor#performOk()
     * @since 5.0
     */
    @Override
    public boolean performOk() {
        IEclipsePreferences prefs = getPreferences();
        int timeout = this.timeoutSecTextField.getSelection();
        prefs.putInt(PreferenceConstants.JBOSS_REQUEST_EXECUTION_TIMEOUT, timeout);

        // save
        try {
            prefs.flush();
            return true;
        } catch (BackingStoreException e) {
            return false;
        }
    }

    /**
     * @see org.teiid.designer.ui.preferences.IGeneralPreferencePageContributor#refresh()
     * @since 5.0
     */
    @Override
    public void refresh() {
        IEclipsePreferences prefs = getPreferences();
        int timeoutSec = prefs.getInt(PreferenceConstants.JBOSS_REQUEST_EXECUTION_TIMEOUT,
                                      PreferenceConstants.JBOSS_REQUEST_EXECUTION_TIMEOUT_SEC_DEFAULT);
        this.timeoutSecTextField.setSelection(timeoutSec);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.teiid.designer.ui.preferences.IGeneralPreferencePageContributor#setPreferencePage(org.eclipse.jface.preference.PreferencePage)
     */
    public void setPreferencePage(PreferencePage preferencePage) {
    }

    /**
     * @see org.teiid.designer.ui.preferences.IGeneralPreferencePageContributor#setWorkbench(org.eclipse.ui.IWorkbench)
     * @since 5.0
     */
    @Override
    public void setWorkbench(IWorkbench theWorkbench) {
    }

}
