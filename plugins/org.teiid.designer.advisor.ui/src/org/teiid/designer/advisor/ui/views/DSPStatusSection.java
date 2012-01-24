/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.advisor.ui.views;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.teiid.designer.advisor.ui.AdvisorUiConstants;
import org.teiid.designer.advisor.ui.AdvisorUiPlugin;
import org.teiid.designer.advisor.ui.core.AdvisorHyperLinkListener;
import org.teiid.designer.advisor.ui.scope.VdbNature;
import org.teiid.designer.advisor.ui.util.DSPPluginImageHelper;
import org.teiid.designer.advisor.ui.util.LabelLabelLinkRow;

import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelProjectSelectionStatusValidator;
import com.metamatrix.modeler.ui.viewsupport.ModelingResourceFilter;
import com.metamatrix.ui.internal.util.WidgetUtil;

/**
 * 
 */
public class DSPStatusSection implements AdvisorUiConstants.Groups {
    private FormToolkit toolkit;

    private Section section;

    private final DSPPluginImageHelper imageHelper = AdvisorUiPlugin.getImageHelper();
    private final AdvisorHyperLinkListener linkListener;
    private ImageHyperlink projectSelectionLink;
    private Label statusSummaryImage;
    private ImageHyperlink statusSummaryHelpLink;

    // private final Advisor advisor;

    // private LabelLabelLinkRow projectRow;
    private LabelLabelLinkRow sourcesRow;
    private LabelLabelLinkRow viewsRow;
    private LabelLabelLinkRow connectorsRow;
    private LabelLabelLinkRow validationRow;
    private LabelLabelLinkRow schemasRow;
    private LabelLabelLinkRow vdbsRow;

    private static final int MAX_NAME_CHARS = 20;

    // ------------ TEXT ----------------------------
    //private static final String NO_PROJECT_TEXT = "NO PROJECT SELECTED          "; //$NON-NLS-1$
    private static final String CRETURN = "\n"; //$NON-NLS-1$
    //private static final String NO_PROJECT_DEFINED = "No Project defined                  "; //$NON-NLS-1$
    private static final String TEN_SPACES = "          "; //$NON-NLS-1$
    private static final String TWENTY_FIVE_SPACES = "                         "; //$NON-NLS-1$
    private static final String ELIPSIS = "..."; //$NON-NLS-1$
    private static final String AUTOVALIDATE_OFF_WARNING = DSPAdvisorI18n.AutovalidateOffMessage;

    /**
     * 
     */
    public DSPStatusSection( FormToolkit toolkit,
                             Composite parent,
                             AdvisorHyperLinkListener linkListener ) {
        super();
        this.toolkit = toolkit;
        this.linkListener = linkListener;
        // this.advisor = advisor;

        this.section = this.toolkit.createSection(parent, ExpandableComposite.TITLE_BAR | Section.DESCRIPTION);

        initSection();
    }

    private void initSection() {
        int nColumns = 3;

        // addTopSectionButtons(topSection);

        // GridLayout gridLayout = new GridLayout();
        // gridLayout.horizontalSpacing = 0;
        // gridLayout.verticalSpacing = 0;
        // container.setLayout(gridLayout);

        section.setText(DSPAdvisorI18n.StatusSectionTitle_NoProjectSelected);
        section.setDescription(DSPAdvisorI18n.StatusSectionDefaultDescription);
        section.getDescriptionControl().setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
        section.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true)); // SWT.HORIZONTAL, SWT.VERTICAL, true, true));

        Composite sectionBody = toolkit.createComposite(section);
        TableWrapLayout layout = new TableWrapLayout();
        layout.numColumns = nColumns;
        layout.verticalSpacing = 3;
        layout.horizontalSpacing = 6;
        sectionBody.setLayout(layout);
        TableWrapData twd = new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.FILL);
        twd.valign = TableWrapData.CENTER;
        sectionBody.setLayoutData(twd);

        // projectRow = new LabelLabelLinkRow(GROUP_PROJECT, toolkit, sectionBody, NO_PROJECT_TEXT, this.linkListener);
        // Add Separator
        createSeparator(sectionBody, nColumns, 5);

        validationRow = new LabelLabelLinkRow(GROUP_MODEL_VALIDATION, toolkit, sectionBody, DSPAdvisorI18n.ModelValidationLabel,
                                              this.linkListener);
        schemasRow = new LabelLabelLinkRow(GROUP_XML_SCHEMAS, toolkit, sectionBody, DSPAdvisorI18n.XmlSchemaLabel,
                                           this.linkListener);
        sourcesRow = new LabelLabelLinkRow(GROUP_SOURCES, toolkit, sectionBody, DSPAdvisorI18n.SourcesLabel, this.linkListener);
        connectorsRow = new LabelLabelLinkRow(GROUP_CONNECTIONS, toolkit, sectionBody, DSPAdvisorI18n.ConnectionFactoriesLabel
                                                                                    + TWENTY_FIVE_SPACES, this.linkListener);
        viewsRow = new LabelLabelLinkRow(GROUP_VIEWS, toolkit, sectionBody, DSPAdvisorI18n.ViewsLabel, this.linkListener);
        vdbsRow = new LabelLabelLinkRow(GROUP_VDBS, toolkit, sectionBody, DSPAdvisorI18n.VDBsLabel, this.linkListener);

        // Add Separator
        createSeparator(sectionBody, nColumns, 5);

        // Need a placeholder for the third column
        // toolkit.createLabel(sectionBody, null);

        addStatusSectionButtons(section);

        updateStatus(DSPValidationConstants.STATUS_MSGS.NO_PROJECT_SELECTED);

        sectionBody.pack(true);
        section.setClient(sectionBody);
        // newSection.pack(true);
        section.setExpanded(true);

    }

    private void addStatusSectionButtons( Section section ) {
        Composite buttonArea = toolkit.createComposite(section);
        // FillLayout buttonLayout = new FillLayout(SWT.HORIZONTAL);
        GridLayout buttonLayout = new GridLayout(3, false);
        buttonArea.setLayout(buttonLayout);
        buttonLayout.horizontalSpacing = 10;
        // buttonLayout.spacing = 5;

        toolkit.adapt(buttonArea, true, true);
        buttonArea.setBackground(section.getTitleBarGradientBackground());
        section.setTextClient(buttonArea);

        projectSelectionLink = toolkit.createImageHyperlink(buttonArea, SWT.WRAP);
        projectSelectionLink.setLayoutData(new GridData(SWT.NONE, SWT.NONE, false, false, 1, 1));
        projectSelectionLink.setBackground(section.getTitleBarBackground());

        projectSelectionLink.setImage(imageHelper.MODEL_PROJECT_IMAGE);
        projectSelectionLink.setToolTipText("Change Project"); // DSPAdvisorI18n.StatusSectionHelpTooltip); //$NON-NLS-1$
        projectSelectionLink.addHyperlinkListener(new HyperlinkAdapter() {
            @Override
            public void linkActivated( HyperlinkEvent e ) {
                Object[] projects = WidgetUtil.showWorkspaceObjectSelectionDialog("Change Project", //$NON-NLS-1$
                                                                                  "Select Model Project for Advisor", //$NON-NLS-1$
                                                                                  false,
                                                                                  null,
                                                                                  new ModelingResourceFilter(
                                                                                                             new ModelProjectViewFilter()),
                                                                                  new ModelProjectSelectionStatusValidator());
                if (projects.length > 0 && AdvisorUiPlugin.getStatusManager().setCurrentProject(((IProject)projects[0]))) {
                    AdvisorUiPlugin.getStatusManager().updateStatus(true);
                }
            }
        });

        statusSummaryImage = toolkit.createLabel(buttonArea, null);
        statusSummaryImage.setBackground(section.getTitleBarBackground());
        statusSummaryImage.setImage(imageHelper.CHECKED_BOX_IMAGE);
        statusSummaryImage.setLayoutData(new GridData(SWT.NONE, SWT.NONE, false, false, 1, 1));

        statusSummaryHelpLink = toolkit.createImageHyperlink(buttonArea, SWT.WRAP);
        statusSummaryHelpLink.setLayoutData(new GridData(SWT.NONE, SWT.NONE, false, false, 1, 1));
        statusSummaryHelpLink.setBackground(section.getTitleBarBackground());
        statusSummaryHelpLink.setImage(imageHelper.HELP_IMAGE);
        statusSummaryHelpLink.setToolTipText(DSPAdvisorI18n.StatusSectionHelpTooltip);
        statusSummaryHelpLink.addHyperlinkListener(new HyperlinkAdapter() {
            @Override
            public void linkActivated( HyperlinkEvent e ) {
                // Point point = sourcesStatusImage.getDisplay().getCursorLocation();
                // AdvisorHelpDialog helpDialog =
                // new AdvisorHelpDialog(getInfoPopActions(GROUP_SOURCES), point.x, point.y, toolkit);
                // helpDialog.open();
                // ModelerHelpUtil.openInfopop(statusSummaryHelpLink,
                // WebServicesHelpConstants.PAGE_IDS.CREATE_WS_OVERVIEW_HELP_ID);
            }
        });
    }

    private void createSeparator( Composite parent,
                                  int nColumns,
                                  int height ) {
        Composite bottomSep = toolkit.createCompositeSeparator(parent);
        TableWrapData layoutData = new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.FILL_GRAB, 1, nColumns);
        layoutData.heightHint = height;
        bottomSep.setLayoutData(layoutData);
    }

    public void updateStatus( Status theStatus ) {
        CoreArgCheck.isInstanceOf(ModelProjectStatus.class, theStatus);

        ModelProjectStatus status = (ModelProjectStatus)theStatus;

        boolean autovalidateOn = ResourcesPlugin.getWorkspace().isAutoBuilding();

        // =========================================================================================================================

        // --------------------------------------------------
        // IF VDB is EMPTY or NO VDB Context, treat this as an summary section
        // error and decorate vdb as error and categories as
        // EMPTY (i.e. not yet addressed)
        // --------------------------------------------------

        if (status == null || status.getCurrentModelProject() == null) {
            // projectStatusImage.setImage(imageHelper.VDB_ERROR_IMAGE);
            // if (status != null) {
            // projectStatusImage.setToolTipText(status.getMessage());
            // projectStatusLabel.setToolTipText(status.getMessage());
            // }

            // this.projectRow.setText(NO_PROJECT_DEFINED);
            // this.projectRow.setLinkTooltip(DSPAdvisorI18n.NoProjectMessage + CRETURN + DSPAdvisorI18n.Status_ClickForActions);
            this.validationRow.setLinkTooltip(DSPAdvisorI18n.NoProjectMessage + CRETURN + DSPAdvisorI18n.Status_ClickForActions);
            this.sourcesRow.setLinkTooltip(DSPAdvisorI18n.NoProjectMessage + CRETURN + DSPAdvisorI18n.Status_ClickForActions);
            this.connectorsRow.setLinkTooltip(DSPAdvisorI18n.NoProjectMessage + CRETURN + DSPAdvisorI18n.Status_ClickForActions);
            this.viewsRow.setLinkTooltip(DSPAdvisorI18n.NoProjectMessage + CRETURN + DSPAdvisorI18n.Status_ClickForActions);
            this.vdbsRow.setLinkTooltip(DSPAdvisorI18n.NoProjectMessage + CRETURN + DSPAdvisorI18n.Status_ClickForActions);

            resetStatusFGColor(true);

            // this.projectRow.setImage(imageHelper.EMPTY_BOX_IMAGE);
            this.validationRow.setImage(imageHelper.EMPTY_BOX_IMAGE);
            this.validationRow.setText(DSPAdvisorI18n.ModelValidationLabel + TEN_SPACES);
            this.sourcesRow.setImage(imageHelper.EMPTY_BOX_IMAGE);
            this.connectorsRow.setImage(imageHelper.EMPTY_BOX_IMAGE);

            this.statusSummaryImage.setImage(imageHelper.PROBLEM_BOX_IMAGE);

            this.projectSelectionLink.setImage(imageHelper.MODEL_PROJECT_IMAGE);
            // =========================================================================================================================
        } else {
            // --------------------------------------------------
            // Project Status
            // --------------------------------------------------
            String projectName = status.getCurrentModelProject().getName();
            if (projectName.length() > MAX_NAME_CHARS) {
                projectName = projectName.substring(0, MAX_NAME_CHARS) + ELIPSIS;
            }

            if (ModelerCore.hasNature(status.getCurrentModelProject(), VdbNature.NATURE_ID)) {
                this.projectSelectionLink.setImage(imageHelper.VDB_PROJECT_IMAGE);
            } else {
                this.projectSelectionLink.setImage(imageHelper.MODEL_PROJECT_IMAGE);
            }
            boolean hasErrors = false;

            // projectRow.setText(projectName);
            // projectRow.setLabelTooltip(status.getMessage());
            // projectRow.setImageTooltip(status.getMessage());
            // --------------------------------------------------
            // Model Validation Status
            // --------------------------------------------------
            // Added capability to show user if Auto-Validate is ON or OFF
            Image validationImage = imageHelper.UNCHECKED_BOX_IMAGE;
            if (status.getModelStatus().getSeverity() == IStatus.ERROR) {
                if (autovalidateOn) {
                    validationImage = imageHelper.PROBLEM_BOX_IMAGE;
                } else {
                    validationImage = imageHelper.WARNING_PROBLEM_BOX_IMAGE;
                }
            } else if (status.getModelStatus().getSeverity() == IStatus.WARNING) {
                if (autovalidateOn) {
                    validationImage = imageHelper.UNCHECKED_BOX_IMAGE;
                } else {
                    validationImage = imageHelper.WARNING_EMPTY_BOX_IMAGE;
                }
            } else {
                if (autovalidateOn) {
                    validationImage = imageHelper.CHECKED_BOX_IMAGE;
                } else {
                    validationImage = imageHelper.WARNING_CHECKED_BOX_IMAGE;
                }
            }
            if (autovalidateOn) {
                this.validationRow.setText(DSPAdvisorI18n.ModelValidationLabel + TEN_SPACES);
            } else {
                this.validationRow.setText(DSPAdvisorI18n.ModelValidationOffLabel);
            }
            String imageMessage = status.getModelStatus().getMessage();
            if (!autovalidateOn) {
                imageMessage = AUTOVALIDATE_OFF_WARNING;
            }
            this.validationRow.update(validationImage,
                                      imageMessage,
                                      status.getModelStatus().getMessage(),
                                      status.getModelStatus().getMessage() + CRETURN + DSPAdvisorI18n.Status_ClickForActions);
            if (status.getModelStatus().getSeverity() == IStatus.ERROR) {
                hasErrors = true;
            }
            // --------------------------------------------------
            // XML Schemas Status
            // --------------------------------------------------
            this.schemasRow.update(getButtonImage(GROUP_XML_SCHEMAS, status.getXmlSchemaFilesStatus().getSeverity()),
                                   status.getXmlSchemaFilesStatus().getMessage(),
                                   status.getXmlSchemaFilesStatus().getMessage(),
                                   status.getXmlSchemaFilesStatus().getMessage() + CRETURN
                                   + DSPAdvisorI18n.Status_ClickForActions);
            if (status.getXmlSchemaFilesStatus().getSeverity() == IStatus.ERROR) {
                hasErrors = true;
            }
            // --------------------------------------------------
            // Sources Status
            // --------------------------------------------------
            this.sourcesRow.update(getButtonImage(GROUP_SOURCES, status.getSourceModelsStatus().getSeverity()),
                                   status.getSourceModelsStatus().getMessage(),
                                   status.getSourceModelsStatus().getMessage(),
                                   status.getSourceModelsStatus().getMessage() + CRETURN + DSPAdvisorI18n.Status_ClickForActions);
            if (status.getSourceModelsStatus().getSeverity() == IStatus.ERROR) {
                hasErrors = true;
            }
            // --------------------------------------------------
            // Connector Bindings Status
            // --------------------------------------------------
            this.connectorsRow.update(getButtonImage(GROUP_CONNECTIONS, status.getConnectionFactoriesStatus().getSeverity()),
                                      status.getConnectionFactoriesStatus().getMessage(),
                                      status.getConnectionFactoriesStatus().getMessage(),
                                      status.getConnectionFactoriesStatus().getMessage() + CRETURN
                                      + DSPAdvisorI18n.Status_ClickForActions);
            if (status.getConnectionFactoriesStatus().getSeverity() == IStatus.ERROR) {
                hasErrors = true;
            }
            // --------------------------------------------------
            // Views Status
            // --------------------------------------------------

            this.viewsRow.update(getButtonImage(GROUP_VIEWS, status.getViewModelsStatus().getSeverity()),
                                 status.getViewModelsStatus().getMessage(),
                                 status.getViewModelsStatus().getMessage(),
                                 status.getViewModelsStatus().getMessage() + CRETURN + DSPAdvisorI18n.Status_ClickForActions);
            if (status.getViewModelsStatus().getSeverity() == IStatus.ERROR) {
                hasErrors = true;
            }
            // --------------------------------------------------
            // Views Status
            // --------------------------------------------------

            this.vdbsRow.update(getButtonImage(GROUP_VDBS, status.getVdbsStatus().getSeverity()),
                                status.getVdbsStatus().getMessage(),
                                status.getVdbsStatus().getMessage(),
                                status.getVdbsStatus().getMessage() + CRETURN + DSPAdvisorI18n.Status_ClickForActions);
            if (status.getVdbsStatus().getSeverity() == IStatus.ERROR) {
                hasErrors = true;
            }

            if (!hasErrors) {
                statusSummaryImage.setImage(imageHelper.CHECKED_BOX_IMAGE);
            } else {
                statusSummaryImage.setImage(imageHelper.PROBLEM_BOX_IMAGE);
            }
        }

        if (status.getCurrentModelProject() != null) {
            section.setText(DSPAdvisorI18n.StatusSectionTitlePrefix + "  " + status.getCurrentModelProject().getName()); //$NON-NLS-1$
        } else {
            section.setText(DSPAdvisorI18n.StatusSectionTitle_NoProjectSelected);
        }

        refreshIcons();

        section.getParent().redraw();
    }

    private Image getButtonImage( int category,
                                  int severity ) {
        Image image = this.imageHelper.UNCHECKED_BOX_IMAGE;

        switch (category) {
            case GROUP_MODEL_VALIDATION:
            case GROUP_SOURCES:
            case GROUP_CONNECTIONS:
            case GROUP_VIEWS:
            case GROUP_VIEW_MAPPINGS:
            case GROUP_XML_SCHEMAS:
            case GROUP_WEBSERVICE_MODELS:
            case GROUP_VDBS: {
                if (severity == IStatus.ERROR) {
                    image = this.imageHelper.PROBLEM_BOX_IMAGE;
                } else if (severity == IStatus.WARNING) {
                    image = this.imageHelper.WARNING_EMPTY_BOX_IMAGE;
                } else {
                    image = this.imageHelper.CHECKED_BOX_IMAGE;
                }
            }
                break;
        }

        return image;
    }

    private void resetStatusFGColor( boolean setToDim ) {
        if (setToDim) {
            this.connectorsRow.dim();
            this.sourcesRow.dim();
            this.schemasRow.dim();
            this.viewsRow.dim();
            this.validationRow.dim();
            this.vdbsRow.dim();
        } else {
            this.validationRow.brighten();
            this.schemasRow.brighten();
            this.sourcesRow.brighten();
            this.connectorsRow.brighten();
            this.viewsRow.brighten();
            this.vdbsRow.brighten();
        }

    }

    private void refreshIcons() {
        statusSummaryImage.redraw();
        validationRow.redraw();
        sourcesRow.redraw();
        schemasRow.redraw();
        connectorsRow.redraw();
        viewsRow.redraw();
        vdbsRow.redraw();

        this.section.redraw();

        this.section.getParent().redraw();
    }

    /**
     * @return section
     */
    public Section getSection() {
        return section;
    }
}
