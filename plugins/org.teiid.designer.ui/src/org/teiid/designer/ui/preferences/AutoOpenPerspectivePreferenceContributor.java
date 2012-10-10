package org.teiid.designer.ui.preferences;

import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.designer.core.util.StringUtilities;
import org.teiid.designer.ui.PluginConstants;
import org.teiid.designer.ui.UiConstants;
import org.teiid.designer.ui.UiPlugin;
import org.teiid.designer.ui.common.util.WidgetFactory;


/**
 * @since 8.0
 */
public class AutoOpenPerspectivePreferenceContributor implements IGeneralPreferencePageContributor, UiConstants {

    private static final String PREF_ID = PluginConstants.Prefs.General.AUTO_OPEN_PERSPECTIVE_WHEN_MODEL_EDITOR_OPENED;

    private static final String PREFIX = I18nUtil.getPropertyPrefix(AutoOpenPerspectivePreferenceContributor.class);

    private Button btnAlways;

    private Button btnNever;

    private Button btnPrompt;

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.ui.preferences.IGeneralPreferencePageContributor#createPreferenceEditor(org.eclipse.swt.widgets.Composite)
     */
    @Override
	public void createPreferenceEditor( Composite parent ) {
        Group pnlButtons = WidgetFactory.createGroup(parent,
                                                     Util.getStringOrKey(PREFIX + "lbl.text"), //$NON-NLS-1$
                                                     GridData.FILL_HORIZONTAL,
                                                     1,
                                                     3);
        pnlButtons.setFont(JFaceResources.getDialogFont());
        this.btnAlways = WidgetFactory.createRadioButton(pnlButtons, Util.getStringOrKey(PREFIX + "btnAlways.text")); //$NON-NLS-1$
        this.btnAlways.setToolTipText(Util.getStringOrKey(PREFIX + "btnAlways.toolTip")); //$NON-NLS-1$
        this.btnNever = WidgetFactory.createRadioButton(pnlButtons, Util.getStringOrKey(PREFIX + "btnNever.text")); //$NON-NLS-1$
        this.btnNever.setToolTipText(Util.getStringOrKey(PREFIX + "btnNever.toolTip")); //$NON-NLS-1$
        this.btnPrompt = WidgetFactory.createRadioButton(pnlButtons, Util.getStringOrKey(PREFIX + "btnPrompt.text")); //$NON-NLS-1$
        this.btnPrompt.setToolTipText(Util.getStringOrKey(PREFIX + "btnPrompt.toolTip")); //$NON-NLS-1$

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
            value = MessageDialogWithToggle.PROMPT;
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
        String value = MessageDialogWithToggle.PROMPT;

        if (this.btnAlways.getSelection()) {
            value = MessageDialogWithToggle.ALWAYS;
        } else if (this.btnNever.getSelection()) {
            value = MessageDialogWithToggle.NEVER;
        }

        // persist value
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
        if (MessageDialogWithToggle.ALWAYS.equals(value)) {
            if (!this.btnAlways.getSelection()) {
                this.btnAlways.setSelection(true);
                this.btnNever.setSelection(false);
                this.btnPrompt.setSelection(false);
            }
        } else if (MessageDialogWithToggle.NEVER.equals(value)) {
            if (!this.btnNever.getSelection()) {
                this.btnAlways.setSelection(false);
                this.btnNever.setSelection(true);
                this.btnPrompt.setSelection(false);
            }
        } else {
            if (!this.btnPrompt.getSelection()) {
                this.btnAlways.setSelection(false);
                this.btnNever.setSelection(false);
                this.btnPrompt.setSelection(true);
            }
        }
    }

}
