package org.teiid.designer.ui.preferences;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.core.designer.util.StringUtilities;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.runtime.registry.TeiidRuntimeRegistry;
import org.teiid.designer.runtime.spi.ITeiidServerManager;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion.VersionID;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion;
import org.teiid.designer.ui.UiConstants;
import org.teiid.designer.ui.UiPlugin;


/**
 * @since 8.0
 */
public class DefaultTeiidServerPreferenceContributor implements IGeneralPreferencePageContributor, UiConstants {

    private static final String PREF_ID = ITeiidServerManager.DEFAULT_TEIID_SERVER_VERSION_ID;

    private static final String PREFIX = I18nUtil.getPropertyPrefix(DefaultTeiidServerPreferenceContributor.class);

    private Shell shell;

    private Combo versionCombo;

    /**
     * {@inheritDoc}
     *
     * @see org.teiid.designer.ui.preferences.IGeneralPreferencePageContributor#createPreferenceEditor(org.eclipse.swt.widgets.Composite)
     */
    @Override
	public void createPreferenceEditor( Composite parent ) {
        shell = parent.getShell();
        Composite panel = new Composite(parent, SWT.NONE);
        GridLayoutFactory.swtDefaults().numColumns(2).applyTo(panel);

        versionCombo = new Combo(panel, SWT.DROP_DOWN);
        versionCombo.setFont(JFaceResources.getDialogFont());
        versionCombo.setToolTipText(Util.getStringOrKey(PREFIX + "toolTip")); //$NON-NLS-1$
        GridDataFactory.swtDefaults().grab(true, true).align(SWT.LEFT, SWT.CENTER).applyTo(versionCombo);

        Label title = new Label(panel, SWT.NONE);
        title.setText(Util.getStringOrKey(PREFIX + "title")); //$NON-NLS-1$
        GridDataFactory.swtDefaults().grab(true, true).align(SWT.LEFT, SWT.CENTER).applyTo(title);

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
            value = TeiidServerVersion.deriveUltimateDefaultServerVersion().toString();
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

    private boolean hasOpenEditors() {
        for (IWorkbenchWindow window : PlatformUI.getWorkbench().getWorkbenchWindows()) {
            for (IWorkbenchPage page : window.getPages()) {
                if (page.getEditorReferences().length > 0)
                    return true;
            }
        }
        return false;
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

        if (versionString.equals(getPreferenceStoreValue(false)))
            return true; // same value - no change

        if (ModelerCore.getTeiidServerManager().getDefaultServer() == null && hasOpenEditors()) {
            // No default teiid instance and open editors so modelling diagrams may close which could surprise!
            boolean changeVersion = MessageDialog.openQuestion(shell,
                                                                        Util.getStringOrKey(PREFIX + "versionChangeQuestionTitle"), //$NON-NLS-1$
                                                                        Util.getStringOrKey(PREFIX + "versionChangeQuestionMessage")); //$NON-NLS-1$

            if (! changeVersion)
                return false;
        }

        try {
            for (ITeiidServerVersion regVersion : TeiidRuntimeRegistry.getInstance().getSupportedVersions()) {
                if (regVersion.compareTo(version)) {
                    getPreferenceStore().setValue(PREF_ID, regVersion.toString());
                    return true;
                }
            }
        } catch (Exception ex) {
            Util.log(ex);
        }

        boolean changeVersion = MessageDialog.openQuestion(shell,
                                                           Util.getStringOrKey(PREFIX + "unsupportedVersionQuestionTitle"), //$NON-NLS-1$
                                                           Util.getStringOrKey(PREFIX + "unsupportedVersionQuestionMesssage")); //$NON-NLS-1$
        if (changeVersion) {
            getPreferenceStore().setValue(PREF_ID, version.toString());
            return true;
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
            Collection<ITeiidServerVersion> registeredServerVersions = TeiidRuntimeRegistry.getInstance().getSupportedVersions();
            items = TeiidServerVersion.orderVersions(registeredServerVersions, true);
        } catch (Exception ex) {
            Util.log(ex);
            for (VersionID versionId : VersionID.values()) {
                items.add(versionId.toString());
            }
        }

        versionCombo.setItems(items.toArray(new String[0]));
        versionCombo.setText(value);
    }
}
