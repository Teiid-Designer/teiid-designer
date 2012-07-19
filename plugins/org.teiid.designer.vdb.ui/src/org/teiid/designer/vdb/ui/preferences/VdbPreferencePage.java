/*
 * See the COPYRIGHT.txt file distributed with this work for information
 * regarding copyright ownership.
 *
 * This software is made available by Red Hat, Inc. under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution and is
 * available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * See the AUTHORS.txt file in the distribution for a full listing of
 * individual contributors.
 */
package org.teiid.designer.vdb.ui.preferences;

import static org.teiid.designer.vdb.ui.VdbUiConstants.Util;
import static org.teiid.designer.vdb.ui.preferences.VdbPreferenceConstants.SYNCHRONIZE_WITHOUT_WARNING;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.teiid.core.util.I18nUtil;
import org.teiid.designer.ui.common.preferences.IEditorPreferencesComponent;
import org.teiid.designer.ui.common.preferences.IEditorPreferencesValidationListener;
import org.teiid.designer.vdb.ui.VdbUiPlugin;


/**
 * The <code>VdbPreferencePage</code> is the UI for managing general VDB Editor-related preferences.
 *
 * @since 8.0
 */
public final class VdbPreferencePage implements IEditorPreferencesComponent {

    /**
     * The editor used to enable and disable if a warning dialog should be displayed before synchronizing VDB entries.
     */
    private BooleanFieldEditor synchronizeWithoutWarningEditor;

    /**
     * {@inheritDoc}
     *
     * @see org.teiid.designer.ui.common.preferences.IEditorPreferencesComponent#addValidationListener(org.teiid.designer.ui.common.preferences.IEditorPreferencesValidationListener)
     */
    @Override
    public void addValidationListener( IEditorPreferencesValidationListener listener ) {
        // nothing to do
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.ui.common.preferences.IEditorPreferencesComponent#createEditorPreferencesComponent(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public Composite createEditorPreferencesComponent( Composite parent ) {
        Composite panel = new Composite(parent, SWT.NONE);
        panel.setLayout(new GridLayout(2, false));
        panel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        // create the field editor
        String label = Util.getString(I18nUtil.getPropertyPrefix(VdbPreferencePage.class) + "editor.label"); //$NON-NLS-1$
        this.synchronizeWithoutWarningEditor = new BooleanFieldEditor(SYNCHRONIZE_WITHOUT_WARNING, label, panel);
        this.synchronizeWithoutWarningEditor.setPreferenceStore(getPreferenceStore());
        String toolTip = Util.getString(I18nUtil.getPropertyPrefix(VdbPreferencePage.class) + "editor.toolTip"); //$NON-NLS-1$
        this.synchronizeWithoutWarningEditor.getDescriptionControl(panel).setToolTipText(toolTip);

        // populate editor with current preference value
        this.synchronizeWithoutWarningEditor.load();

        return panel;
    }

    private IPreferenceStore getPreferenceStore() {
        return VdbUiPlugin.singleton.getPreferenceStore();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.ui.common.preferences.IEditorPreferencesComponent#getName()
     */
    @Override
    public String getName() {
        return Util.getString(I18nUtil.getPropertyPrefix(VdbPreferencePage.class) + "name"); //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.ui.common.preferences.IEditorPreferencesComponent#getTooltip()
     */
    @Override
    public String getTooltip() {
        return Util.getString(I18nUtil.getPropertyPrefix(VdbPreferencePage.class) + "toolTip"); //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.ui.common.preferences.IEditorPreferencesComponent#performDefaults()
     */
    @Override
    public void performDefaults() {
        this.synchronizeWithoutWarningEditor.loadDefault();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.ui.common.preferences.IEditorPreferencesComponent#performOk()
     */
    @Override
    public boolean performOk() {
        this.synchronizeWithoutWarningEditor.store();
        VdbUiPlugin.singleton.savePreferences();
        return true;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.teiid.designer.ui.common.preferences.IEditorPreferencesComponent#removeValidationListener(org.teiid.designer.ui.common.preferences.IEditorPreferencesValidationListener)
     */
    @Override
    public void removeValidationListener( IEditorPreferencesValidationListener listener ) {
        // nothing to do
    }

    /**
     * {@inheritDoc}
     *
     * @see org.teiid.designer.ui.common.preferences.IEditorPreferencesComponent#validate()
     */
    @Override
    public void validate() {
        // nothing to do
    }

}
