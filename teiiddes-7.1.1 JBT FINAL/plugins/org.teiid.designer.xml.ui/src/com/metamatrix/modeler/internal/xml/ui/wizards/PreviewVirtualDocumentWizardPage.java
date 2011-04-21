/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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
