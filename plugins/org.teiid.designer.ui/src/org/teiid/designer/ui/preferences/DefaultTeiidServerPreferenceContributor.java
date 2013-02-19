package org.teiid.designer.ui.preferences;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.util.StringUtilities;
import org.teiid.designer.runtime.registry.TeiidRuntimeRegistry;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion;
import org.teiid.designer.ui.UiConstants;
import org.teiid.designer.ui.UiPlugin;


/**
 * @since 8.0
 */
public class DefaultTeiidServerPreferenceContributor implements IGeneralPreferencePageContributor, UiConstants {

    private static final String PREF_ID = ModelerCore.DEFAULT_TEIID_SERVER_VERSION_ID;

    private static final String PREFIX = I18nUtil.getPropertyPrefix(DefaultTeiidServerPreferenceContributor.class);

    private Combo versionCombo;

    /**
     * {@inheritDoc}
     *
     * @see org.teiid.designer.ui.preferences.IGeneralPreferencePageContributor#createPreferenceEditor(org.eclipse.swt.widgets.Composite)
     */
    @Override
	public void createPreferenceEditor( Composite parent ) {
        Composite panel = new Composite(parent, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(2).applyTo(panel);

        versionCombo = new Combo(panel, SWT.DROP_DOWN | SWT.READ_ONLY);
        versionCombo.setFont(JFaceResources.getDialogFont());
        versionCombo.setToolTipText(Util.getStringOrKey(PREFIX + "toolTip")); //$NON-NLS-1$
        GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).applyTo(versionCombo);

        Label title = new Label(panel, SWT.NONE);
        title.setText(Util.getStringOrKey(PREFIX + "title")); //$NON-NLS-1$
        GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).applyTo(title);

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
        return Util.getStringOrKey(PREFIX + "name"); //$NON-NLS-1$
    }

    /**
     * Obtains the <code>IPreferenceStore</code> where this preference is being persisted.
     *
     * @return the preference store
     */
    private IPreferenceStore getPreferenceStore() {
        return UiPlugin.getDefault().getPreferenceStore();
    }

    /**
     * Obtains the {@link IPreferenceStore}'s default or current value for this preference
     *
     * @param defaultFlag indicates if the default or current value is being requested
     * @return the requested value
     */
    private String getPreferenceStoreValue( boolean defaultFlag ) {
        IPreferenceStore prefStore = getPreferenceStore();
        String value = null;

        if (defaultFlag) {
            value = prefStore.getDefaultString(PREF_ID);
        } else {
            value = prefStore.getString(PREF_ID);
        }

        if (StringUtilities.isEmpty(value)) {
            value = ITeiidServerVersion.DEFAULT_TEIID_8_SERVER_ID;
        }

        return value;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.teiid.designer.ui.preferences.IGeneralPreferencePageContributor#getToolTip()
     */
    @Override
	public String getToolTip() {
        return Util.getStringOrKey(PREFIX + "toolTip"); //$NON-NLS-1$
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
        update(getPreferenceStoreValue(true));
        return true;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.teiid.designer.ui.preferences.IGeneralPreferencePageContributor#performOk()
     */
    @Override
	public boolean performOk() {
        // persist value
        String versionString = versionCombo.getText();
        ITeiidServerVersion version = new TeiidServerVersion(versionString);

        try {
            for (ITeiidServerVersion regVersion : TeiidRuntimeRegistry.getInstance().getRegisteredServerVersions()) {
                if (regVersion.compareTo(version)) {
                    getPreferenceStore().setValue(PREF_ID, regVersion.toString());
                    return true;
                }
            }
        } catch (Exception ex) {
            Util.log(ex);
        }

        // No runtime client to support default version
        return false;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.teiid.designer.ui.preferences.IGeneralPreferencePageContributor#refresh()
     */
    @Override
	public void refresh() {
        update(getPreferenceStoreValue(false));
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
     * Updates the radio buttons selection states corresponding to the new value.
     *
     * @param value the new value
     */
    private void update( String value ) {

        List<String> items = new ArrayList<String>();

        try {
            Collection<ITeiidServerVersion> registeredServerVersions = TeiidRuntimeRegistry.getInstance().getRegisteredServerVersions();
            for (ITeiidServerVersion version : registeredServerVersions) {
                items.add(version.toString());
            }
        } catch (Exception ex) {
            Util.log(ex);
            items.add(ITeiidServerVersion.DEFAULT_TEIID_7_SERVER_ID);
            items.add(ITeiidServerVersion.DEFAULT_TEIID_8_SERVER_ID);
        }

        versionCombo.setItems(items.toArray(new String[0]));
        versionCombo.setText(value);
    }
}
