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
package com.metamatrix.modeler.vdb.ui.preferences;

import static com.metamatrix.modeler.vdb.ui.VdbUiConstants.Util;
import static com.metamatrix.modeler.vdb.ui.preferences.VdbPreferenceConstants.SYNCHRONIZE_WITHOUT_WARNING;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.vdb.ui.VdbUiConstants;
import com.metamatrix.modeler.vdb.ui.VdbUiPlugin;

/**
 * The <code>VdbPreferencePage</code> is the UI for managing general VDB-related preferences.
 */
public final class VdbPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

    /**
     * The editor used to enable and disable if a warning dialog should be displayed before synchronizing VDB entries.
     */
    private BooleanFieldEditor synchronizeWithoutWarningEditor;

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Control createContents( Composite parent ) {
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

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.dialogs.DialogPage#getDescription()
     */
    @Override
    public String getDescription() {
        return Util.getString(I18nUtil.getPropertyPrefix(VdbPreferencePage.class) + "description"); //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.dialogs.DialogPage#getImage()
     */
    @Override
    public Image getImage() {
        return VdbUiPlugin.singleton.getImage(VdbUiConstants.Images.IMPORT_VDB_ICON);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.dialogs.DialogPage#getMessage()
     */
    @Override
    public String getMessage() {
        return Util.getString(I18nUtil.getPropertyPrefix(VdbPreferencePage.class) + "message"); //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.preference.PreferencePage#getPreferenceStore()
     */
    @Override
    public IPreferenceStore getPreferenceStore() {
        return VdbUiPlugin.singleton.getPreferenceStore();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.dialogs.DialogPage#getTitle()
     */
    @Override
    public String getTitle() {
        return Util.getString(I18nUtil.getPropertyPrefix(VdbPreferencePage.class) + "title"); //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
     */
    @Override
    public void init( IWorkbench workbench ) {
        // nothing to do
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
     */
    @Override
    protected void performDefaults() {
        this.synchronizeWithoutWarningEditor.loadDefault();
        super.performDefaults();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.preference.PreferencePage#performOk()
     */
    @Override
    public boolean performOk() {
        this.synchronizeWithoutWarningEditor.store();
        return super.performOk();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.dialogs.DialogPage#setVisible(boolean)
     */
    @Override
    public void setVisible( boolean visible ) {
        super.setVisible(visible);

        if (visible) {
            this.synchronizeWithoutWarningEditor.setFocus();
        }
    }

}
