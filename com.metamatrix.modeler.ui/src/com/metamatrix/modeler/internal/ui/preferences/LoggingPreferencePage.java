/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.UiPlugin;

/**
 * This class represents the preference page for setting the
 * Modeler Logging Preferences.
 */
public class LoggingPreferencePage
extends FieldEditorPreferencePage
implements IWorkbenchPreferencePage, UiConstants {
	public static final String P_PATH = "pathPreference"; //$NON-NLS-1$
	public static final String P_BOOLEAN = "booleanPreference"; //$NON-NLS-1$
	public static final String P_CHOICE = "choicePreference"; //$NON-NLS-1$
	public static final String P_STRING = "stringPreference"; //$NON-NLS-1$

	public LoggingPreferencePage() {
		super(GRID);
		setPreferenceStore(UiPlugin.getDefault().getPreferenceStore());
		setDescription(Util.getString("LoggingPreferencePage.description")); //$NON-NLS-1$
		initializeDefaults();
	}
/**
 * Sets the default values of the preferences.
 */
	private void initializeDefaults() {
		IPreferenceStore store = getPreferenceStore();
		store.setDefault(P_BOOLEAN, true);
		store.setDefault(P_CHOICE, "choice2"); //$NON-NLS-1$
		store.setDefault(P_STRING, Util.getString("ModelerPreferencePage.Default_value_7")); //$NON-NLS-1$
	}
	
/**
 * Creates the field editors. Field editors are abstractions of
 * the common GUI blocks needed to manipulate various types
 * of preferences. Each field editor knows how to save and
 * restore itself.
 */

	@Override
    public void createFieldEditors() {
		addField(new DirectoryFieldEditor(P_PATH, 
				Util.getString("ModelerPreferencePage.&Directory_preference__8"), getFieldEditorParent())); //$NON-NLS-1$
		addField(
			new BooleanFieldEditor(
				P_BOOLEAN,
				Util.getString("ModelerPreferencePage.&An_example_of_a_boolean_preference_9"), //$NON-NLS-1$
				getFieldEditorParent()));

		addField(new RadioGroupFieldEditor(
			P_CHOICE,
			Util.getString("ModelerPreferencePage.An_example_of_a_multiple-choice_preference_10"), //$NON-NLS-1$
			1,
			new String[][] {
                {Util.getString("ModelerPreferencePage.&Choice_1_11"), "choice1" }, { //$NON-NLS-1$ //$NON-NLS-2$
				 Util.getString("ModelerPreferencePage.C&hoice_2_13"), "choice2" } //$NON-NLS-1$ //$NON-NLS-2$
		}, getFieldEditorParent()));
		addField(
			new StringFieldEditor(P_STRING, Util.getString("ModelerPreferencePage.A_&text_preference__15"), getFieldEditorParent())); //$NON-NLS-1$
	}
	
	public void init(IWorkbench workbench) {
	}
}
