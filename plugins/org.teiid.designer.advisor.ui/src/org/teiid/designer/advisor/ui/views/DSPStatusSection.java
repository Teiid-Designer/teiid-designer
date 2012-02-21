/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.advisor.ui.views;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.teiid.designer.advisor.ui.AdvisorUiConstants;
import org.teiid.designer.advisor.ui.AdvisorUiPlugin;
import org.teiid.designer.advisor.ui.core.AdvisorHyperLinkListener;
import org.teiid.designer.advisor.ui.util.DSPPluginImageHelper;
import org.teiid.designer.advisor.ui.util.LabelLabelLinkRow;

import com.metamatrix.core.util.CoreArgCheck;

/**
 * 
 */
public class DSPStatusSection implements AdvisorUiConstants.Groups {
    private FormToolkit toolkit;

    private Section section;

    private final DSPPluginImageHelper imageHelper = AdvisorUiPlugin.getImageHelper();
    private final AdvisorHyperLinkListener linkListener;

    private LabelLabelLinkRow sourcesRow;
    private LabelLabelLinkRow viewsRow;
    private LabelLabelLinkRow connectorsRow;
    private LabelLabelLinkRow validationRow;
    private LabelLabelLinkRow schemasRow;
    private LabelLabelLinkRow vdbsRow;
    LabelLabelLinkRow testRow;

    private static final int MAX_NAME_CHARS = 20;
    
    private static final int SEPARATOR_HEIGHT = 3;

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

        this.section = this.toolkit.createSection(parent, ExpandableComposite.TITLE_BAR);

        initSection();
    }

    private void initSection() {
        int nColumns = 3;

        section.setText(DSPAdvisorI18n.StatusSectionTitle_NoProjectSelected);

        GridData gd = new GridData(GridData.FILL_BOTH | GridData.HORIZONTAL_ALIGN_BEGINNING);
        gd.horizontalSpan = 2;
        section.setLayoutData(gd);

        Composite sectionBody = toolkit.createComposite(section);
        TableWrapLayout layout = new TableWrapLayout();
        layout.numColumns = nColumns;
        layout.verticalSpacing = 3;
        layout.horizontalSpacing = 3;
        sectionBody.setLayout(layout);
        TableWrapData twd = new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.FILL);
        twd.valign = TableWrapData.CENTER;
        sectionBody.setLayoutData(twd);

        // Add Separator
        createSeparator(sectionBody, nColumns, SEPARATOR_HEIGHT);

        
        connectorsRow = new LabelLabelLinkRow(GROUP_CONNECTIONS, toolkit, sectionBody, DSPAdvisorI18n.ConnectionFactoriesLabel
                                                                                    + TWENTY_FIVE_SPACES, this.linkListener);
        sourcesRow = new LabelLabelLinkRow(GROUP_SOURCES, toolkit, sectionBody, DSPAdvisorI18n.SourcesLabel, this.linkListener);
        schemasRow = new LabelLabelLinkRow(GROUP_XML_SCHEMAS, toolkit, sectionBody, DSPAdvisorI18n.XmlSchemaLabel, this.linkListener);
        viewsRow = new LabelLabelLinkRow(GROUP_VIEWS, toolkit, sectionBody, DSPAdvisorI18n.ViewsLabel, this.linkListener);
        vdbsRow = new LabelLabelLinkRow(GROUP_VDBS, toolkit, sectionBody, DSPAdvisorI18n.VDBsLabel, this.linkListener);

        // Add Separator
        createSeparator(sectionBody, nColumns, SEPARATOR_HEIGHT);
        
        validationRow = new LabelLabelLinkRow(GROUP_MODEL_VALIDATION, toolkit, sectionBody, DSPAdvisorI18n.ModelValidationLabel, this.linkListener);
        testRow = new LabelLabelLinkRow(GROUP_TEST, toolkit, sectionBody, DSPAdvisorI18n.TestLabel, this.linkListener);
        
        createSeparator(sectionBody, nColumns, SEPARATOR_HEIGHT);

        updateStatus(DSPValidationConstants.STATUS_MSGS.NO_PROJECT_SELECTED);

        sectionBody.pack(true);
        section.setClient(sectionBody);
        // newSection.pack(true);
        section.setExpanded(true);

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
            this.validationRow.setLinkTooltip(DSPAdvisorI18n.NoProjectMessage + CRETURN + DSPAdvisorI18n.Status_ClickForActions);
            this.sourcesRow.setLinkTooltip(DSPAdvisorI18n.NoProjectMessage + CRETURN + DSPAdvisorI18n.Status_ClickForActions);
            this.connectorsRow.setLinkTooltip(DSPAdvisorI18n.NoProjectMessage + CRETURN + DSPAdvisorI18n.Status_ClickForActions);
            this.viewsRow.setLinkTooltip(DSPAdvisorI18n.NoProjectMessage + CRETURN + DSPAdvisorI18n.Status_ClickForActions);
            this.vdbsRow.setLinkTooltip(DSPAdvisorI18n.NoProjectMessage + CRETURN + DSPAdvisorI18n.Status_ClickForActions);
            this.testRow.setLinkTooltip(DSPAdvisorI18n.NoProjectMessage + CRETURN + DSPAdvisorI18n.Status_ClickForActions);

            resetStatusFGColor(true);

            this.validationRow.setImage(imageHelper.EMPTY_BOX_IMAGE);
            this.validationRow.setText(DSPAdvisorI18n.ModelValidationLabel + TEN_SPACES);
            this.sourcesRow.setImage(imageHelper.EMPTY_BOX_IMAGE);
            this.connectorsRow.setImage(imageHelper.EMPTY_BOX_IMAGE);
            
            // =========================================================================================================================
        } else {
        	boolean hasErrors = false;
            // --------------------------------------------------
            // Project Status
            // --------------------------------------------------
            String projectName = status.getCurrentModelProject().getName();
            if (projectName.length() > MAX_NAME_CHARS) {
                projectName = projectName.substring(0, MAX_NAME_CHARS) + ELIPSIS;
            }

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
            // VDBs Status
            // --------------------------------------------------

            this.vdbsRow.update(getButtonImage(GROUP_VDBS, status.getVdbsStatus().getSeverity()),
                                status.getVdbsStatus().getMessage(),
                                status.getVdbsStatus().getMessage(),
                                status.getVdbsStatus().getMessage() + CRETURN + DSPAdvisorI18n.Status_ClickForActions);
            if (status.getVdbsStatus().getSeverity() == IStatus.ERROR) {
                hasErrors = true;
            }

            // --------------------------------------------------
            // Test Status
            // --------------------------------------------------
            
            this.testRow.update(getButtonImage(GROUP_TEST, status.getTestStatus().getSeverity()),
                    status.getTestStatus().getMessage(),
                    status.getTestStatus().getMessage(),
                    status.getTestStatus().getMessage() + CRETURN + DSPAdvisorI18n.Status_ClickForActions);
            
            if (status.getTestStatus().getSeverity() == IStatus.ERROR) {
                hasErrors = true;
            }

            if( hasErrors) {
            	// DO THING FOR NOW
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
            case GROUP_VDBS:
            case GROUP_TEST:{
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
            this.testRow.dim();
        } else {
            this.validationRow.brighten();
            this.schemasRow.brighten();
            this.sourcesRow.brighten();
            this.connectorsRow.brighten();
            this.viewsRow.brighten();
            this.vdbsRow.brighten();
            this.testRow.brighten();
        }

    }

    private void refreshIcons() {
//        statusSummaryImage.redraw();
        validationRow.redraw();
        sourcesRow.redraw();
        schemasRow.redraw();
        connectorsRow.redraw();
        viewsRow.redraw();
        vdbsRow.redraw();
        testRow.redraw();

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
