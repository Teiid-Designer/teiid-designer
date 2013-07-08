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
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.osgi.service.prefs.BackingStoreException;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.designer.runtime.DqpPlugin;
import org.teiid.designer.runtime.PreferenceConstants;
import org.teiid.designer.runtime.ui.DqpUiConstants;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.preferences.IGeneralPreferencePageContributor;


/**
 * Allows setting the timeout preference for Teiid Importer timeout
 * @since 8.2
 */
public class TeiidImporterTimeoutPreferenceContributor implements IGeneralPreferencePageContributor, DqpUiConstants {

    private static final String PREFIX = I18nUtil.getPropertyPrefix(TeiidImporterTimeoutPreferenceContributor.class);

    private Text timeoutSecTextField;

    /**
     * @see org.teiid.designer.ui.preferences.IGeneralPreferencePageContributor#createPreferenceEditor(org.eclipse.swt.widgets.Composite)
     * @since 5.0
     */
    @Override
	public void createPreferenceEditor( Composite theParent ) {
        Composite pnl = new Composite(theParent, SWT.NONE);
        pnl.setLayout(new GridLayout(2,false));
        pnl.setLayoutData(new GridData());

        this.timeoutSecTextField = WidgetFactory.createTextField(pnl, GridData.FILL_HORIZONTAL, 1);
        this.timeoutSecTextField.setToolTipText(getToolTip());
        
        Label label = new Label(pnl,SWT.NONE);
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
    	this.timeoutSecTextField.setText(PreferenceConstants.TEIID_IMPORTER_TIMEOUT_SEC_DEFAULT);

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
        String timeoutStr = this.timeoutSecTextField.getText();
        // If value entered is valid, set it.  Otherwise, set to the default
        if(!CoreStringUtil.isEmpty(timeoutStr)) {
        	try {
				Integer.parseInt(timeoutStr);
	            prefs.put(PreferenceConstants.TEIID_IMPORTER_TIMEOUT_SEC, timeoutStr);
			} catch (NumberFormatException ex) {
	            prefs.put(PreferenceConstants.TEIID_IMPORTER_TIMEOUT_SEC, PreferenceConstants.TEIID_IMPORTER_TIMEOUT_SEC_DEFAULT);
			}
        } else {
            prefs.put(PreferenceConstants.TEIID_IMPORTER_TIMEOUT_SEC, PreferenceConstants.TEIID_IMPORTER_TIMEOUT_SEC_DEFAULT);
        }

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
        String timeoutSec = prefs.get(PreferenceConstants.TEIID_IMPORTER_TIMEOUT_SEC, PreferenceConstants.TEIID_IMPORTER_TIMEOUT_SEC_DEFAULT);
        this.timeoutSecTextField.setText(timeoutSec);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.ui.preferences.IGeneralPreferencePageContributor#setPreferencePage(org.eclipse.jface.preference.PreferencePage)
     */
    public void setPreferencePage( PreferencePage preferencePage ) {
    }

    /**
     * @see org.teiid.designer.ui.preferences.IGeneralPreferencePageContributor#setWorkbench(org.eclipse.ui.IWorkbench)
     * @since 5.0
     */
    @Override
	public void setWorkbench( IWorkbench theWorkbench ) {
    }

}
