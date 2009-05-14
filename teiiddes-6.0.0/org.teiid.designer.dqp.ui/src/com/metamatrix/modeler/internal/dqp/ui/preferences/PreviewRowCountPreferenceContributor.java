/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.dqp.ui.preferences;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.dqp.ui.DqpUiPlugin;
import com.metamatrix.modeler.internal.ui.preferences.IGeneralPreferencePageContributor;
import com.metamatrix.ui.internal.util.WidgetFactory;

/**
 * @since 5.0
 */
public class PreviewRowCountPreferenceContributor implements IGeneralPreferencePageContributor, DqpUiConstants {

    private static final String PREFIX = I18nUtil.getPropertyPrefix(PreviewRowCountPreferenceContributor.class);

    private Text valueText;

    /**
     * @see com.metamatrix.modeler.internal.ui.preferences.IGeneralPreferencePageContributor#createPreferenceEditor(org.eclipse.swt.widgets.Composite)
     * @since 5.0
     */
    public void createPreferenceEditor( Composite theParent ) {
        Composite pnl = new Composite(theParent, SWT.NONE);
        pnl.setLayout(new GridLayout(2, false));
        pnl.setLayoutData(new GridData());

        Label lbl = new Label(pnl, SWT.NONE);
        lbl.setLayoutData(new GridData());
        lbl.setText(UTIL.getStringOrKey(PREFIX + "lbl.text")); //$NON-NLS-1$
        lbl.setToolTipText(getToolTip());

        this.valueText = WidgetFactory.createTextField(pnl);
        GridData gd = new GridData();
        gd.minimumWidth = 50;
        gd.grabExcessHorizontalSpace = true;
        this.valueText.setLayoutData(gd);

        // verify user input
        this.valueText.addVerifyListener(new VerifyListener() {
            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.swt.events.VerifyListener#verifyText(org.eclipse.swt.events.VerifyEvent)
             */
            public void verifyText( VerifyEvent e ) {
                verifyValue(e);
            }
        });

        // initialize state
        refresh();
    }

    /**
     * Obtains the <code>IPreferenceStore</code> where this preference is being persisted.
     * 
     * @return the preference store
     * @since 5.0
     */
    private IPreferenceStore getPreferenceStore() {
        return DqpUiPlugin.getDefault().getPreferenceStore();
    }

    /**
     * Obtains the {@link IPreferenceStore}'s default or current value for this preference.
     * 
     * @param theDefaultFlag the flag indicating if the default or current value is being requested
     * @return the value
     * @since 5.0
     */
    private int getPreferenceStoreValue( boolean theDefaultFlag ) {
        int result;
        IPreferenceStore prefStore = getPreferenceStore();

        if (theDefaultFlag) {
            result = prefStore.getDefaultInt(Preferences.ID_PREVIEW_ROW_LIMIT);
        } else {
            result = prefStore.getInt(Preferences.ID_PREVIEW_ROW_LIMIT);
        }

        return result;
    }

    /**
     * @see com.metamatrix.modeler.internal.ui.preferences.IGeneralPreferencePageContributor#getName()
     * @since 5.0
     */
    public String getName() {
        return UTIL.getStringOrKey(PREFIX + "name"); //$NON-NLS-1$
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
        this.valueText.setText(Integer.toString(getPreferenceStoreValue(true)));
        return true;
    }

    /**
     * @see com.metamatrix.modeler.internal.ui.preferences.IGeneralPreferencePageContributor#performOk()
     * @since 5.0
     */
    public boolean performOk() {
        String value = this.valueText.getText();

        // if value is empty or equal to zero set back to default
        if ((value.length() == 0) || (Integer.parseInt(value) == 0)) {
            value = String.valueOf(getPreferenceStoreValue(false));
            this.valueText.setText(value);
        }

        getPreferenceStore().setValue(Preferences.ID_PREVIEW_ROW_LIMIT, Integer.parseInt(this.valueText.getText()));

        return true;
    }

    /**
     * @see com.metamatrix.modeler.internal.ui.preferences.IGeneralPreferencePageContributor#refresh()
     * @since 5.0
     */
    public void refresh() {
        this.valueText.setText(Integer.toString(getPreferenceStoreValue(false)));
    }

    /**
     * @see com.metamatrix.modeler.internal.ui.preferences.IGeneralPreferencePageContributor#setWorkbench(org.eclipse.ui.IWorkbench)
     * @since 5.0
     */
    public void setWorkbench( IWorkbench theWorkbench ) {
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.metamatrix.modeler.internal.ui.preferences.IGeneralPreferencePageContributor#setPreferencePage(org.eclipse.jface.preference.PreferencePage)
     */
    public void setPreferencePage( PreferencePage preferencePage ) {
    }

    void verifyValue( VerifyEvent e ) {
        if ((e.text != null) && (e.text.length() > 0)) {
            for (char c : e.text.toCharArray()) {
                if (!Character.isDigit(c)) {
                    e.doit = false;
                    break;
                }
            }
        }
    }
}
