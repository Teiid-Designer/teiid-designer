/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.diagram.ui.preferences;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.teiid.designer.diagram.ui.DiagramUiConstants;
import org.teiid.designer.diagram.ui.DiagramUiPlugin;
import org.teiid.designer.diagram.ui.PluginConstants;

/**
 * This class represents the preference page for setting the
 * Diagram Appearance Preferences.
 * 
 */
public class AppearancePreferencePage extends PreferencePage
    	implements DiagramUiConstants, PluginConstants, IWorkbenchPreferencePage {
	//////////////////////////////////////////////////////////////////////
    // Static variables
    //////////////////////////////////////////////////////////////////////
    			    
    //////////////////////////////////////////////////////////////////////
    // Instance variables
    //////////////////////////////////////////////////////////////////////
    private AppearanceProcessor processor;
	    
    //////////////////////////////////////////////////////////////////////
    // Constructors
    //////////////////////////////////////////////////////////////////////
    public AppearancePreferencePage() {
		super();
		setPreferenceStore(DiagramUiPlugin.getDefault().getPreferenceStore());
		setDescription(Util.getString("DiagramAppearancePrefPage.description")); //$NON-NLS-1$
	}

	//////////////////////////////////////////////////////////////////////
	// Instance methods
	//////////////////////////////////////////////////////////////////////
	@Override
	public void init(IWorkbench workbench) {
	}
	
	@Override
    public Control createContents(Composite parent) {
		IPreferenceStore preferenceStore = getPreferenceStore();
		Shell shell = getShell();
		processor = new AppearanceProcessor(preferenceStore, shell);
		return processor.createContents(parent);
	}
	
	@Override
    public boolean performOk() {
		return processor.performOk();
	}
	
	@Override
    public void performDefaults() {
		processor.performDefaults();
	}
}//end AppearancePreferencePage
