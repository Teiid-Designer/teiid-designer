/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension.ui.editors;

import static org.teiid.designer.extension.ui.UiConstants.EditorIds.MED_OVERVIEW_PAGE;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.teiid.designer.extension.definition.ModelExtensionDefinition;
import org.teiid.designer.extension.ui.Messages;

import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.modeler.internal.ui.forms.FormUtil;

/**
 * 
 */
public final class OverviewEditorPage extends FormPage {

    private final ModelExtensionDefinition med;

    public OverviewEditorPage( FormEditor medEditor,
                               ModelExtensionDefinition med ) {
        super(medEditor, MED_OVERVIEW_PAGE, Messages.medEditorOverviewPageTitle);
        this.med = med;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.forms.editor.FormPage#createFormContent(org.eclipse.ui.forms.IManagedForm)
     */
    @Override
    protected void createFormContent( IManagedForm managedForm ) {
        FormToolkit toolkit = managedForm.getToolkit();

        ScrolledForm scrolledForm = managedForm.getForm();
        scrolledForm.setText(Messages.medEditorOverviewPageTitle);
        toolkit.decorateFormHeading(scrolledForm.getForm());

        Composite body = scrolledForm.getBody();
        body.setLayout(FormUtil.createFormGridLayout(false, 2));

        Section section = toolkit.createSection(body, ExpandableComposite.NO_TITLE | ExpandableComposite.TITLE_BAR);
        section.setLayout(FormUtil.createClearGridLayout(false, 1));
        section.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

        Composite container = toolkit.createComposite(section);
        container.setLayout(FormUtil.createSectionClientGridLayout(false, 2));
        section.setClient(container);

        toolkit.createLabel(container, Messages.namespacePrefixLabel);
        Text text = toolkit.createText(container, CoreStringUtil.Constants.EMPTY_STRING);
        text.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

        toolkit.createLabel(container, Messages.namespaceUriLabel);
        text = toolkit.createText(container, CoreStringUtil.Constants.EMPTY_STRING);
        text.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

        toolkit.createLabel(container, Messages.extendedMetamodelUriLabel);
        text = toolkit.createText(container, CoreStringUtil.Constants.EMPTY_STRING);
        text.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

        toolkit.createLabel(container, Messages.resourcePathLabel);
        text = toolkit.createText(container, CoreStringUtil.Constants.EMPTY_STRING);
        text.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

        toolkit.createLabel(container, Messages.versionLabel);
        text = toolkit.createText(container, CoreStringUtil.Constants.EMPTY_STRING);
        text.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

        toolkit.createLabel(container, Messages.descriptionLabel);
        text = toolkit.createText(container, CoreStringUtil.Constants.EMPTY_STRING);
        text.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.part.EditorPart#getTitleToolTip()
     */
    @Override
    public String getTitleToolTip() {
        return Messages.medEditorOverviewPageToolTip;
    }

}
