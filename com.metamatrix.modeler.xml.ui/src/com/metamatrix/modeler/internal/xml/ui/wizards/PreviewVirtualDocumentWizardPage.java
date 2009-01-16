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

package com.metamatrix.modeler.internal.xml.ui.wizards;

import java.util.Arrays;
import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.metamatrix.metamodels.xml.XmlFragment;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.xml.IVirtualDocumentFragmentSource;
import com.metamatrix.modeler.xml.ui.ModelerXmlUiConstants;
import com.metamatrix.modeler.xml.ui.editor.EditVirtualDocumentsPanel;

/**
 * NewVirtualDocumentWizardPage is the wizard page contribution for building Virtual
 * XMLDocument models from XML Schema files in the workspace.
 */

public class PreviewVirtualDocumentWizardPage extends WizardPage 
		implements ModelerXmlUiConstants, IVirtualDocumentFragmentSource {

    /////////////////////////////////////////////////////////////////////////////////
	// Instance variables
	/////////////////////////////////////////////////////////////////////////////////
	private EditVirtualDocumentsPanel panel;
    private final NewDocumentWizardModel model;

	/////////////////////////////////////////////////////////////////////////////////
	// Constructors
	/////////////////////////////////////////////////////////////////////////////////
	/**
	 * Constructor for NewVirtualDocumentWizardPage.
	 * @param pageName
	 */
	public PreviewVirtualDocumentWizardPage(NewDocumentWizardModel wizModel) {
		super("previewVirtualDocumentPage");  //$NON-NLS-1$
        model = wizModel;
		setTitle(Util.getString("PreviewVirtualDocumentPage.title"));  //$NON-NLS-1$
		setDescription(Util.getString("PreviewVirtualDocumentPage.description")); //$NON-NLS-1$
        setPageComplete(true);
	}

	/////////////////////////////////////////////////////////////////////////////////
	// Instance methods
	/////////////////////////////////////////////////////////////////////////////////
	public void createControl(Composite parent) {
		panel = new EditVirtualDocumentsPanel(parent);
		setControl(panel);
	}

    @Override
    public void dispose() {
        super.dispose();
        Control c = getControl();
        if (c != null) {
            c.dispose();
        } // endif
    }
    
    public Collection getRoots(ModelResource modelResource, IProgressMonitor monitor) {
        if(panel == null) {
            final XmlFragment[] fragments = model.getFragments(modelResource, monitor);
            return Arrays.asList(fragments);
        }
        
        if(panel.getRoots(monitor).isEmpty() ) {
            final XmlFragment[] fragments = model.getFragments(modelResource, monitor);
            panel.setFragments(fragments);
        }
        
        return panel.getRoots(monitor);
    }

    //
    // Implementation of the IVirtualDocumentSource inteface:
    //
    public XmlFragment[] getFragments(ModelResource modelResource, IProgressMonitor monitor) {
        if (panel == null) {
            createControl(model.getWizHolder());
            panel.setVisible(false); // keep the thing hidden
        } // endif

        return panel.getFragments(null, monitor);
    }

    public void updateSourceFragments(final boolean isVisible, final IProgressMonitor monitor) {
        monitor.subTask(Util.getString("PreviewVirtualDocumentPage.subtaskBuilding")); //$NON-NLS-1$
        final XmlFragment[] fragments = model.getFragments(null, monitor);
        if (panel != null
         && fragments != panel.getStartingFragments()) { // array ref is different, need to update
            monitor.subTask(Util.getString("PreviewVirtualDocumentPage.subtaskTree")); //$NON-NLS-1$
            panel.setStartingFragments(fragments, isVisible, monitor);
        } // endif
    }
}
