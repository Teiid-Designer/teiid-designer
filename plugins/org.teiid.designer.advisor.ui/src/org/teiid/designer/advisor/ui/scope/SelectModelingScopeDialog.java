/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.advisor.ui.scope;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;

import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.widget.Dialog;

/**
 * 
 */
public class SelectModelingScopeDialog extends Dialog {
    IProject selectedProject;

    private Button radioGeneralModeling;
    private Button radioRelationalModeling;
    private Button radioXMLModeling;
    private Button radioWebServiceModeling;

    private Button vdbProjectCheckBox;

    /**
     * @param parent
     * @param title
     */
    public SelectModelingScopeDialog( Shell parent,
                                      IProject project ) {
        super(parent, "Select Modeling Scope");
        this.selectedProject = project;
    }

    // =============================================================
    // Instance methods
    // =============================================================

    @Override
    protected Control createDialogArea( Composite parent ) {
        Composite mainPanel = (Composite)super.createDialogArea(parent);
        init(mainPanel);
        return mainPanel;
    }

    /**
     * Initialize the panel.
     */
    private void init( Composite parent ) {
        // ------------------------------
        // Set layout for the Composite
        // ------------------------------
        GridLayout gridLayout = new GridLayout();
        parent.setLayout(gridLayout);
        gridLayout.numColumns = 1;
        GridData gridData = new GridData(GridData.FILL_BOTH);
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = true;
        parent.setLayoutData(gridData);

        WidgetFactory.createLabel(parent, GridData.HORIZONTAL_ALIGN_BEGINNING, 1, "Select modeling scope for the project: "
                                                                                  + selectedProject.getName() + "     ");
        Group grpScaling = WidgetFactory.createGroup(parent, "Modeling Scope", GridData.FILL_HORIZONTAL);

        radioGeneralModeling = WidgetFactory.createRadioButton(grpScaling, "General");
        radioRelationalModeling = WidgetFactory.createRadioButton(grpScaling, "Relational");
        radioXMLModeling = WidgetFactory.createRadioButton(grpScaling, "XML + Relational");
        radioWebServiceModeling = WidgetFactory.createRadioButton(grpScaling, "Web Services + XML + Relational");

        vdbProjectCheckBox = WidgetFactory.createCheckBox(parent, "VDB Project");

        try {
            setIntialScope();
        } catch (CoreException e) {
            UiConstants.Util.log(e);
        }
    }

    private void setIntialScope() throws CoreException {
        if (selectedProject.hasNature(WebServicesModelingNature.NATURE_ID)) {
            radioWebServiceModeling.setSelection(true);
        } else if (selectedProject.hasNature(XmlModelingNature.NATURE_ID)) {
            radioXMLModeling.setSelection(true);
        } else if (selectedProject.hasNature(RelationalModelingNature.NATURE_ID)) {
            radioRelationalModeling.setSelection(true);
        } else {
            radioGeneralModeling.setSelection(true);
        }
        vdbProjectCheckBox.setSelection(selectedProject.hasNature(VdbNature.NATURE_ID));
    }

    @Override
    public void create() {
        super.create();
        setOkEnabled(true);
    }

    @Override
    protected void okPressed() {
        // Set the nature of the selectedProject;
        String[] natures = ModelerCore.NATURES;
        if (radioRelationalModeling.getSelection()) {
            natures = RelationalModelingNature.NATURES;
        } else if (radioXMLModeling.getSelection()) {
            natures = XmlModelingNature.NATURES;
        } else if (radioWebServiceModeling.getSelection()) {
            natures = WebServicesModelingNature.NATURES;
        }

        if (vdbProjectCheckBox.getSelection()) {
            natures = addVdbNature(natures);
        }
        try {
            IProjectDescription desc = selectedProject.getDescription();
            desc.setNatureIds(natures);
            selectedProject.setDescription(desc, new NullProgressMonitor());
        } catch (CoreException e) {
            UiConstants.Util.log(e);
        }
        super.okPressed();
    }

    private String[] addVdbNature( String[] natures ) {
        String[] newStrings = new String[natures.length + 1];
        newStrings[0] = VdbNature.NATURE_ID;
        int i = 1;
        for (String nature : natures) {
            newStrings[i++] = nature;
        }

        return newStrings;
    }

    public void setOkEnabled( boolean enabled ) {
        getButton(IDialogConstants.OK_ID).setEnabled(enabled);
    }

}
