/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.dqp.ui.preferences;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
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
 * The <code>AutoCreateDataSourcePreferenceContributor</code> is the UI that manages the preference that controls if a data source
 * is automatically created on the current Teiid server if the name of the data source matches the default name.
 */
public class AutoCreateDataSourcePreferenceContributor implements IGeneralPreferencePageContributor, DqpUiConstants {

    /**
     * The i18n key prefix.
     */
    private static final String PREFIX = I18nUtil.getPropertyPrefix(AutoCreateDataSourcePreferenceContributor.class);

    private Button chkEnabled;

    /**
     * {@inheritDoc}
     * 
     * @see com.metamatrix.modeler.internal.ui.preferences.IGeneralPreferencePageContributor#createPreferenceEditor(org.eclipse.swt.widgets.Composite)
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
     * @see com.metamatrix.modeler.internal.ui.preferences.IGeneralPreferencePageContributor#getName()
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
     * {@inheritDoc}
     * 
     * @see com.metamatrix.modeler.internal.ui.preferences.IGeneralPreferencePageContributor#getToolTip()
     */
    @Override
    public String getToolTip() {
        return UTIL.getStringOrKey(PREFIX + "toolTip"); //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.metamatrix.modeler.internal.ui.preferences.IGeneralPreferencePageContributor#performCancel()
     */
    @Override
    public boolean performCancel() {
        return true;
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.metamatrix.modeler.internal.ui.preferences.IGeneralPreferencePageContributor#performDefaults()
     */
    @Override
    public boolean performDefaults() {
        this.chkEnabled.setSelection(PreferenceConstants.AUTO_CREATE_DATA_SOURCE_DEFAULT);

        // save
        try {
            getPreferences().flush();
            return true;
        } catch (BackingStoreException e) {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.metamatrix.modeler.internal.ui.preferences.IGeneralPreferencePageContributor#performOk()
     */
    @Override
    public boolean performOk() {
        IEclipsePreferences prefs = getPreferences();
        prefs.putBoolean(PreferenceConstants.AUTO_CREATE_DATA_SOURCE, this.chkEnabled.getSelection());

        // save
        try {
            prefs.flush();
            return true;
        } catch (BackingStoreException e) {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.metamatrix.modeler.internal.ui.preferences.IGeneralPreferencePageContributor#refresh()
     */
    @Override
    public void refresh() {
        IEclipsePreferences prefs = getPreferences();
        boolean enable = prefs.getBoolean(PreferenceConstants.AUTO_CREATE_DATA_SOURCE,
                                          PreferenceConstants.AUTO_CREATE_DATA_SOURCE_DEFAULT);
        this.chkEnabled.setSelection(enable);
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.metamatrix.modeler.internal.ui.preferences.IGeneralPreferencePageContributor#setWorkbench(org.eclipse.ui.IWorkbench)
     */
    @Override
    public void setWorkbench( IWorkbench theWorkbench ) {
        // nothing to do
    }

}
