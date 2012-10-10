/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.modelgenerator.xml.wizards;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.teiid.core.designer.PluginUtil;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.designer.modelgenerator.xml.XmlImporterUiPlugin;


/**
 * This class represents the preference page for setting the
 * Modeler Logging Preferences.
 *
 * @since 8.0
 */
public class PreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage
{
    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(PreferencePage.class);
    private PluginUtil util = XmlImporterUiPlugin.getDefault().getPluginUtil();
    private String getString(String id)
    {
        return util.getString(I18N_PREFIX + id);
    }

    
    public PreferencePage() {
		super(GRID);
		setPreferenceStore(XmlImporterUiPlugin.getDefault().getPreferenceStore());
        setDescription(getString("description")); //$NON-NLS-1$

        initializeDefaults();
	}
/**
 * Sets the default values of the preferences.
 */
	private void initializeDefaults() {
		IPreferenceStore store = getPreferenceStore();
        String cDefault = getString("C_threshold.default"); //$NON-NLS-1$
        String pDefault = getString("P_threshold.default"); //$NON-NLS-1$
        String fDefault = getString("F_threshold.default"); //$NON-NLS-1$
		store.setDefault(XmlImporterUiPlugin.C_threshold, cDefault);
        store.setDefault(XmlImporterUiPlugin.P_threshold, pDefault);
		store.setDefault(XmlImporterUiPlugin.F_threshold, fDefault);

        String requestTableDefault = getString("requestTable.default"); //$NON-NLS-1$
        store.setDefault(XmlImporterUiPlugin.requestTable, requestTableDefault);
        
        String mergedChildSepDefault = getString("mergedChildSep.default"); //$NON-NLS-1$
		store.setDefault(XmlImporterUiPlugin.mergedChildSep,
				mergedChildSepDefault);
    }
	
/**
 * Creates the field editors. Field editors are abstractions of
 * the common GUI blocks needed to manipulate various types
 * of preferences. Each field editor knows how to save and
 * restore itself.
 */

	@Override
    public void createFieldEditors() {
        String pLabel = getString("P_threshold.label"); //$NON-NLS-1$
        String cLabel = getString("C_threshold.label"); //$NON-NLS-1$
        String fLabel = getString("F_threshold.label"); //$NON-NLS-1$
        addField(new IntegerFieldEditor(XmlImporterUiPlugin.C_threshold, cLabel, getFieldEditorParent(), 4));
        addField(new IntegerFieldEditor(XmlImporterUiPlugin.P_threshold, pLabel, getFieldEditorParent(), 4));
        addField(new IntegerFieldEditor(XmlImporterUiPlugin.F_threshold, fLabel, getFieldEditorParent(), 4));

        String requestTableLabel = getString("requestTable.label"); //$NON-NLS-1$
        addField(new StringFieldEditor(XmlImporterUiPlugin.requestTable, requestTableLabel, getFieldEditorParent()));

        String mergedChildSepLabel = getString("mergedChildSep.label"); //$NON-NLS-1$
        addField(new StringFieldEditor(XmlImporterUiPlugin.mergedChildSep, mergedChildSepLabel, getFieldEditorParent()));
    }
	
	@Override
	public void init(IWorkbench workbench) {
	}
}
