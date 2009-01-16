/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

package com.metamatrix.ui.internal.preferences;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import com.metamatrix.ui.UiPlugin;

/**
 * EditorPreferencePage
 */
public class EditorPreferencePage extends PreferencePage implements IEditorPreferencesValidationListener, IWorkbenchPreferencePage {

    //////////////////////////////////////////////////////////////////////
    // Instance variables
    //////////////////////////////////////////////////////////////////////
    private List<IEditorPreferencesComponent> editorPreferenceComponents = null;

    //////////////////////////////////////////////////////////////////////
    // Constructors
    //////////////////////////////////////////////////////////////////////
    public EditorPreferencePage() {
		super();
	}

	//////////////////////////////////////////////////////////////////////
	// Instance methods
	//////////////////////////////////////////////////////////////////////
	public void init(IWorkbench workbench) {
	}

	@Override
    public Control createContents(Composite parent) {

	    TabFolder tabFolder = new TabFolder(parent, SWT.NONE);
	    for (int i = 0; i < getEditorPreferenceComponents().size(); i++) {
	        IEditorPreferencesComponent editorPreferencesComponent = getEditorPreferenceComponents().get(i);
	        editorPreferencesComponent.addValidationListener(this);
		    Composite tabComposite = editorPreferencesComponent.createEditorPreferencesComponent(tabFolder);
			TabItem tab = new TabItem(tabFolder, SWT.NONE);
			tab.setText(editorPreferencesComponent.getName());
			tab.setToolTipText(editorPreferencesComponent.getTooltip());
			tab.setControl(tabComposite);
	    }
		return tabFolder;
	}


	@Override
    public boolean performOk() {
	    boolean result = true;
	    for (int i = 0; i < getEditorPreferenceComponents().size(); i++) {
	        boolean tempResult = getEditorPreferenceComponents().get(i).performOk();
	        if (!tempResult) {
	            result = false;
	        }
	    }
	    return result;
	}

	@Override
    public void performDefaults() {
	    for (int i = 0; i < getEditorPreferenceComponents().size(); i++) {
	        getEditorPreferenceComponents().get(i).performDefaults();
	    }
	}

	private IExtension[] getExtensions() {
	    return Platform.getExtensionRegistry().getExtensionPoint(UiPlugin.getDefault().getBundle().getSymbolicName(),
		                                                         "editorPreferences").getExtensions(); //$NON-NLS-1$
	}

	private List<IEditorPreferencesComponent> getEditorPreferenceComponents() {
	    if (this.editorPreferenceComponents == null) {
	        IExtension[] extensions = getExtensions();
	        this.editorPreferenceComponents = new ArrayList<IEditorPreferencesComponent>();
	        for (int i = 0 ; i < extensions.length; i++) {
	            IExtension extension = extensions[i];
	            IConfigurationElement[] configurationElements = extension.getConfigurationElements();
	        	for (int x = 0; x < configurationElements.length; x++) {
	        	    IConfigurationElement configurationElement = configurationElements[x];
	        	    if (configurationElement.getName().equals("editorPreferenceComponent")) { //$NON-NLS-1$
	        	        IEditorPreferencesComponent editorPreferencesComponent;
	        	        try {
	        	            editorPreferencesComponent = (IEditorPreferencesComponent) configurationElement.createExecutableExtension("class"); //$NON-NLS-1$
	        	            this.editorPreferenceComponents.add(editorPreferencesComponent);
	        	        } catch (CoreException e) {
	        	            e.printStackTrace();
	        	        }
	        	    }
	            }
	       	}
    	}
	    return this.editorPreferenceComponents;
	}


    /* (non-Javadoc)
     * @see com.metamatrix.ui.internal.preferences.IEditorPreferencesValidationListener#validationStatus(boolean, java.lang.String)
     */
    public void validationStatus(boolean status, String message) {
        setValid(status);
        setErrorMessage(message);

    }
}
