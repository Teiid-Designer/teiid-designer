/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime.ui.wizards.vdbs;

import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.teiid.core.designer.util.StringConstants;
import org.teiid.designer.komodo.vdb.DynamicModel;
import org.teiid.designer.runtime.ui.DqpUiConstants;
import org.teiid.designer.runtime.ui.Messages;
import org.teiid.designer.ui.common.graphics.GlobalUiColorManager;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.common.util.WidgetUtil;
import org.teiid.designer.ui.common.util.WizardUtil;
import org.teiid.designer.ui.common.widget.Label;
import org.teiid.designer.ui.common.wizard.AbstractWizardPage;
import org.teiid.designer.ui.viewsupport.ModelProjectSelectionStatusValidator;
import org.teiid.designer.ui.viewsupport.SingleProjectOrFolderFilter;
import org.teiid.designer.vdb.Vdb;

/**
 * Page 2 of Generate Archive Wizard
 */
public class GenerateArchiveVdbPageTwo extends AbstractWizardPage implements DqpUiConstants, StringConstants {

    private Text ouputVdbNameFld;

    private Text vdbArchiveFileNameFld;

    private Label vdbArchiveLocationText;

    private ListViewer sourceModelsViewer;

    private ListViewer viewModelsViewer;

    private Button ddlAsDescriptionOption;

    private final GenerateArchiveVdbManager vdbManager;

    /**
     * ShowDDlPage constructor
     * @param vdbManager the Manager
     * @since 8.1
     */
    public GenerateArchiveVdbPageTwo(GenerateArchiveVdbManager vdbManager) {
        super(GenerateArchiveVdbPageTwo.class.getSimpleName(), EMPTY_STRING);
        this.vdbManager = vdbManager;
        setTitle(Messages.GenerateArchiveVdbPageTwo_title);
    }

    @Override
    public void createControl(Composite parent) {
        // Create page
        final Composite mainPanel = new Composite(parent, SWT.NONE);

        mainPanel.setLayout(new GridLayout(2, false));
        mainPanel.setLayoutData(new GridData());
        mainPanel.setSize(mainPanel.computeSize(SWT.DEFAULT, SWT.DEFAULT));

        setControl(mainPanel);

        createHeaderPanel(mainPanel);

        // Create Source Models List group //$NON-NLS-1$
        createSourceModelsGroup(mainPanel);

        createViewModelsGroup(mainPanel);

        createGenerateButtonPanel(mainPanel);

        setPageComplete(false);
    }

    private void createHeaderPanel(Composite parent) {

        Composite vdbInfoGroup = WidgetFactory.createGroup(parent,
                                                           Messages.GenerateArchiveVdbPageTwo_vdbDetails,
                                                           GridData.FILL_HORIZONTAL);
        vdbInfoGroup.setLayout(new GridLayout(3, false));
        GridDataFactory.fillDefaults().grab(true, false).span(2, 1).applyTo(vdbInfoGroup);

        WidgetFactory.createLabel(vdbInfoGroup,
                                  GridData.VERTICAL_ALIGN_CENTER,
                                  Messages.GenerateArchiveVdbPageTwo_originalVdbName);
        Label vdbNameFld = new Label(vdbInfoGroup, SWT.NONE);
        GridDataFactory.fillDefaults().span(2, 1).grab(true, false).applyTo(vdbNameFld);
        vdbNameFld.setText(vdbManager.getDynamicVdb().getName());
        vdbNameFld.setForeground(GlobalUiColorManager.EMPHASIS_COLOR);

        Label locationLabel = new Label(vdbInfoGroup, SWT.NONE);
        locationLabel.setText(Messages.GenerateArchiveVdbPageTwo_location);

        vdbArchiveLocationText = new Label(vdbInfoGroup, SWT.NONE);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(vdbArchiveLocationText);
        if (vdbManager.getOutputLocation() != null) {
            vdbArchiveLocationText.setText(vdbManager.getOutputLocation().getFullPath().toString());
        }

        Button browseButton = new Button(vdbInfoGroup, SWT.PUSH);
        GridData buttonGridData = new GridData();
        browseButton.setLayoutData(buttonGridData);
        browseButton.setText(Messages.GenerateArchiveVdbPageTwo_browse);
        browseButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                handleBrowse();
            }
        });

        Label vdbVersionLabel = WidgetFactory.createLabel(vdbInfoGroup, Messages.GenerateArchiveVdbPageTwo_version);
        GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.CENTER).applyTo(vdbVersionLabel);

        final Text vdbVersionText = WidgetFactory.createTextField(vdbInfoGroup);
        GridDataFactory.fillDefaults().span(2, 1).align(SWT.LEFT, SWT.CENTER).applyTo(vdbVersionText);
        ((GridData)vdbVersionText.getLayoutData()).widthHint = 40;

        vdbVersionText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                vdbManager.setVersion(vdbVersionText.getText());
                validatePage();
            }
        });

        vdbVersionText.setText(Integer.toString(vdbManager.getDynamicVdb().getVersion()));

        WidgetFactory.createLabel(vdbInfoGroup, GridData.VERTICAL_ALIGN_CENTER, Messages.GenerateArchiveVdbPageTwo_archiveVdbName);
        ouputVdbNameFld = WidgetFactory.createTextField(vdbInfoGroup, SWT.NONE, GridData.FILL_HORIZONTAL);
        GridDataFactory.fillDefaults().span(2, 1).grab(true, false).applyTo(ouputVdbNameFld);
        ouputVdbNameFld.setText(vdbManager.getOutputVdbName());
        ouputVdbNameFld.setToolTipText(Messages.GenerateArchiveVdbPageTwo_archiveVdbNameTooltip);
        ouputVdbNameFld.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(final ModifyEvent event) {
                vdbManager.setOutputVdbName(ouputVdbNameFld.getText());
                validatePage();
            }
        });

        WidgetFactory.createLabel(vdbInfoGroup,
                                  GridData.VERTICAL_ALIGN_CENTER,
                                  Messages.GenerateArchiveVdbPageTwo_vdbArchiveFileName);
        vdbArchiveFileNameFld = WidgetFactory.createTextField(vdbInfoGroup, SWT.NONE, GridData.FILL_HORIZONTAL);
        GridDataFactory.fillDefaults().span(2, 1).grab(true, false).applyTo(vdbArchiveFileNameFld);
        vdbArchiveFileNameFld.setText(vdbManager.getOutputVdbFileName());
        vdbArchiveFileNameFld.setToolTipText(Messages.GenerateArchiveVdbPageTwo_vdbArchiveFileNameTooltip);
        vdbArchiveFileNameFld.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(final ModifyEvent event) {
                vdbManager.setOutputVdbFileName(vdbArchiveFileNameFld.getText());
                validatePage();
            }
        });

        // Spacer component
        new Label(vdbInfoGroup, SWT.SEPARATOR | SWT.HORIZONTAL);

        ddlAsDescriptionOption = WidgetFactory.createButton(vdbInfoGroup,
                                                            Messages.GenerateArchiveVdbPageTwo_ddlAsDescriptionOptionLabel,
                                                            GridData.FILL_HORIZONTAL, 2, SWT.CHECK);

        GridDataFactory.fillDefaults().span(2, 1).grab(true, false).applyTo(ddlAsDescriptionOption);

        ddlAsDescriptionOption.setToolTipText(Messages.GenerateArchiveVdbPageTwo_ddlAsDescriptionOptionTooltip);
        ddlAsDescriptionOption.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                vdbManager.addConversionOption(Vdb.SET_DDL_AS_DESCRIPTION, Boolean.toString(ddlAsDescriptionOption.getSelection()));
            }
        });
    }

    private void createSourceModelsGroup(Composite parent) {

        Group group = WidgetFactory.createGroup(parent, Messages.GenerateArchiveVdbPageTwo_sourceModels, SWT.FILL, 1, 1);
        GridLayoutFactory.fillDefaults().margins(10, 10).applyTo(group);
        GridData gd_2 = new GridData(GridData.FILL_BOTH);
        gd_2.widthHint = 220;
        group.setLayoutData(gd_2);
        // Add a simple list box entry form with String contents
        this.sourceModelsViewer = new ListViewer(group, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
        GridData data = new GridData(GridData.FILL_BOTH);
        data.horizontalSpan = 1;
        this.sourceModelsViewer.getControl().setLayoutData(data);

        for (Object model : getSourceModels()) {
            this.sourceModelsViewer.add(model);
        }
    }

    private void createViewModelsGroup(Composite parent) {

        Group group = WidgetFactory.createGroup(parent, Messages.GenerateArchiveVdbPageTwo_viewModels, SWT.FILL, 1, 1);
        GridLayoutFactory.fillDefaults().margins(10, 10).applyTo(group);
        GridData gd_2 = new GridData(GridData.FILL_BOTH);
        gd_2.widthHint = 220;
        group.setLayoutData(gd_2);
        // Add a simple list box entry form with String contents
        this.viewModelsViewer = new ListViewer(group, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
        GridData data = new GridData(GridData.FILL_BOTH);
        data.horizontalSpan = 1;
        this.viewModelsViewer.getControl().setLayoutData(data);

        for (Object model : getViewModels()) {
            this.viewModelsViewer.add(model);
        }
    }

    private void createGenerateButtonPanel(Composite parent) {
        Composite buttonPanel = new Composite(parent, SWT.NONE);
        GridDataFactory.fillDefaults().span(2, 1).grab(true, false).applyTo(buttonPanel);
        GridLayoutFactory.fillDefaults().numColumns(1).margins(50, 10).applyTo(buttonPanel);

        final Button genButton = new Button(buttonPanel, SWT.PUSH);
        GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).grab(true, true).applyTo(genButton);
        genButton.setText(Messages.GenerateVdbButton_Title);
        genButton.setToolTipText(Messages.GenerateVdbButton_Tooltip);
        genButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                vdbManager.generate();
                genButton.setEnabled(vdbManager.isGenerateRequired());
            }
        });
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible)
            validatePage();

        super.setVisible(visible);
    }

    /* 
     * Validate the page
     */
    private void validatePage() {
        this.vdbManager.validate();
        IStatus status = vdbManager.getStatus();
        if (status.getSeverity() == IStatus.ERROR) {
            this.setErrorMessage(status.getMessage());
            this.setPageComplete(false);
            return;
        } else if (status.getSeverity() == IStatus.WARNING) {
            this.setErrorMessage(status.getMessage());
            this.setPageComplete(true);
        } else {
            setErrorMessage(null);
            WizardUtil.setPageComplete(this, EMPTY_STRING, NONE);
        }
    }

    Object[] getSourceModels() {
        Collection<String> modelNames = new ArrayList<String>();

        for (DynamicModel model : vdbManager.getDynamicVdb().getDynamicModels()) {
            if (model.getModelType() == DynamicModel.Type.PHYSICAL) {
                modelNames.add(model.getName());
            }
        }

        return modelNames.toArray();
    }

    Object[] getViewModels() {
        Collection<String> modelNames = new ArrayList<String>();

        for (DynamicModel model : vdbManager.getDynamicVdb().getDynamicModels()) {
            if (model.getModelType() == DynamicModel.Type.VIRTUAL) {
                modelNames.add(model.getName());
            }
        }

        return modelNames.toArray();
    }

    void handleBrowse() {
        IProject project = vdbManager.getDynamicVdbFile().getProject();
        final IContainer folder = WidgetUtil.showFolderSelectionDialog(project,
                                                                       new SingleProjectOrFolderFilter(project),
                                                                       new ModelProjectSelectionStatusValidator());

        if (folder != null && vdbArchiveLocationText != null) {
            vdbManager.setOutputLocation(folder);
            vdbArchiveLocationText.setText(folder.getFullPath().makeRelative().toString());
        }

        validatePage();
    }

    @Override
    public boolean isPageComplete() {
        if (vdbManager.isGenerateRequired())
            return false;

        return super.isPageComplete();
    }

    @Override
    public boolean canFlipToNextPage() {
        if (vdbManager.isGenerateRequired())
            return false;

        return super.canFlipToNextPage();
    }

}
