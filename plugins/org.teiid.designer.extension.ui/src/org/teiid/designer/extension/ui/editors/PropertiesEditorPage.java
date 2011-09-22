/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension.ui.editors;

import static org.teiid.designer.extension.ui.Messages.medEditorPropertiesPageTitle;
import static org.teiid.designer.extension.ui.Messages.medEditorPropertiesPageToolTip;
import static org.teiid.designer.extension.ui.UiConstants.EditorIds.MED_PROPERTIES_PAGE;

import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.teiid.designer.extension.ui.Messages;

import com.metamatrix.modeler.internal.ui.forms.FormUtil;

/**
 * 
 */
public class PropertiesEditorPage extends MedEditorPage {

    public PropertiesEditorPage( FormEditor medEditor ) {
        super(medEditor, MED_PROPERTIES_PAGE, medEditorPropertiesPageTitle);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.extension.ui.editors.MedEditorPage#createBody(org.eclipse.swt.widgets.Composite,
     *      org.eclipse.ui.forms.widgets.FormToolkit)
     */
    @Override
    protected void createBody( Composite body,
                               FormToolkit toolkit ) {
        body.setLayout(FormUtil.createFormGridLayout(false, 2));

        Composite left = toolkit.createComposite(body, SWT.NONE);
        left.setLayout(FormUtil.createFormPaneGridLayout(false, 1));
        left.setLayoutData(new GridData(GridData.FILL_BOTH));

        Composite right = toolkit.createComposite(body, SWT.NONE);
        right.setLayout(FormUtil.createFormPaneGridLayout(false, 1));
        right.setLayoutData(new GridData(GridData.FILL_BOTH));

        createExtendedMetaclassSection(left, toolkit);
//        SectionPart part = createExtendedMetaclassSection(left, toolkit);
//        managedForm.addPart(part);

        createPropertiesSection(right, toolkit);
//        part = createPropertiesSection(right, toolkit);
//        managedForm.addPart(part);
    }

    private SectionPart createExtendedMetaclassSection( Composite parent,
                                                        FormToolkit toolkit ) {
        Section section = toolkit.createSection(parent, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
        section.setText(Messages.overviewPageExtendedMetaclassTitle);
        section.setDescription(Messages.overviewPageExtendedMetaclassDescription);
        section.setLayout(FormUtil.createClearGridLayout(false, 1));
        section.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

        Composite container = toolkit.createComposite(section);
        container.setLayout(FormUtil.createSectionClientGridLayout(false, 2));
        section.setClient(container);

        StructuredViewer viewer = new TableViewer(container, SWT.H_SCROLL | SWT.V_SCROLL | SWT.SINGLE | toolkit.getBorderStyle());
        Control control = viewer.getControl();
        control.setLayoutData(new GridData(GridData.FILL_BOTH));

        FormUtil.createButtonsContainer(container, toolkit, new String[] { Messages.addButton, Messages.removeButton });
        // TablePart tablePart = getTablePart();
        // fImportViewer = tablePart.getTableViewer();
        // fImportViewer.setContentProvider(new ImportContentProvider());
        // fImportViewer.setLabelProvider(PDEPlugin.getDefault().getLabelProvider());
        toolkit.paintBordersFor(container);

        // makeActions();

        //
        // createSectionToolbar(section, toolkit);
        // initialize();

        return new SectionPart(section);
    }

    /**
     * @param propertiesEditorPage
     * @param right
     * @param toolkit
     * @return
     */
    private SectionPart createPropertiesSection( Composite parent,
                                                 FormToolkit toolkit ) {
        Section section = toolkit.createSection(parent, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
        section.setDescription(Messages.propertiesPageExtensionPropertiesDescription);
        section.setText(Messages.propertiesPageExtensionPropertiesTitle);
        section.setLayout(FormUtil.createClearGridLayout(false, 1));
        section.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

        Composite container = toolkit.createComposite(section);
        container.setLayout(FormUtil.createSectionClientGridLayout(false, 2));
        section.setClient(container);

        StructuredViewer viewer = new TableViewer(container, SWT.H_SCROLL | SWT.V_SCROLL | SWT.SINGLE | toolkit.getBorderStyle());
        Control control = viewer.getControl();
        control.setLayoutData(new GridData(GridData.FILL_BOTH));

        FormUtil.createButtonsContainer(container, toolkit, new String[] { Messages.addButton, Messages.editButton,
                Messages.removeButton });
        // TablePart tablePart = getTablePart();
        // fImportViewer = tablePart.getTableViewer();
        // fImportViewer.setContentProvider(new ImportContentProvider());
        // fImportViewer.setLabelProvider(PDEPlugin.getDefault().getLabelProvider());
        toolkit.paintBordersFor(container);

        // makeActions();

        //
        // createSectionToolbar(section, toolkit);
        // initialize();

        return new SectionPart(section);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.part.EditorPart#getTitleToolTip()
     */
    @Override
    public String getTitleToolTip() {
        return medEditorPropertiesPageToolTip;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.teiid.designer.extension.ui.editors.MedEditorPage#updateAllMessages()
     */
    @Override
    protected void updateAllMessages() {
    }

}
