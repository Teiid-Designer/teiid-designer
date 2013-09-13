/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime.ui.preferences;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.osgi.service.prefs.BackingStoreException;
import org.teiid.datatools.connectivity.ui.Activator;
import org.teiid.datatools.connectivity.ui.Messages;
import org.teiid.datatools.connectivity.ui.PreferenceConstants;
import org.teiid.designer.ui.UiConstants;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.preferences.IGeneralPreferencePageContributor;


/**
 * @since 8.0
 */
public class EnableQueryPlanPreferenceContributor implements IGeneralPreferencePageContributor, UiConstants {

    private static final String PREFIX = "EnableQueryPlanPreferenceContributor."; //$NON-NLS-1$

    private Button chkEnabled;

    /**
     * @see org.teiid.designer.ui.preferences.IGeneralPreferencePageContributor#createPreferenceEditor(org.eclipse.swt.widgets.Composite)
     * @since 5.0
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
     * @see org.teiid.designer.ui.preferences.IGeneralPreferencePageContributor#getName()
     * @since 5.0
     */
    @Override
	public String getName() {
        return Messages.getString(PREFIX + "name"); //$NON-NLS-1$
    }

    /**
     * Obtains the <code>IEclipsePreferences</code> where this preference is being persisted.
     *
     * @return the preferences
     */
    private IEclipsePreferences getPreferences() {
        return Activator.getDefault().getPreferences();
    }

    /**
     * @see org.teiid.designer.ui.preferences.IGeneralPreferencePageContributor#getToolTip()
     * @since 5.0
     */
    @Override
	public String getToolTip() {
        return Messages.getString(PREFIX + "toolTip"); //$NON-NLS-1$
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
        this.chkEnabled.setSelection(PreferenceConstants.TEIID_QUERYPLANS_ENABLED_DEFAULT);

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
        prefs.putBoolean(PreferenceConstants.TEIID_QUERYPLANS_ENABLED, this.chkEnabled.getSelection());

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
        boolean enable = prefs.getBoolean(PreferenceConstants.TEIID_QUERYPLANS_ENABLED, PreferenceConstants.TEIID_QUERYPLANS_ENABLED_DEFAULT);
        this.chkEnabled.setSelection(enable);
    }

    /**
     * @see org.teiid.designer.ui.preferences.IGeneralPreferencePageContributor#setWorkbench(org.eclipse.ui.IWorkbench)
     * @since 5.0
     */
    @Override
	public void setWorkbench( IWorkbench theWorkbench ) {
    }

}
