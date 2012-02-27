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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.teiid.designer.advisor.ui.AdvisorUiConstants;
import org.teiid.designer.advisor.ui.AdvisorUiPlugin;
import org.teiid.designer.advisor.ui.core.AdvisorHyperLinkListener;
import org.teiid.designer.advisor.ui.util.DSPPluginImageHelper;
import org.teiid.designer.advisor.ui.util.HyperLinkLabelRow;

import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.modeler.internal.ui.forms.FormUtil;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelProjectSelectionStatusValidator;
import com.metamatrix.modeler.ui.viewsupport.ModelingResourceFilter;
import com.metamatrix.ui.internal.util.WidgetUtil;

/**
 * 
 */
public class DSPStatusSection implements AdvisorUiConstants.Groups {
    private FormToolkit toolkit;

    private Section section;
    private Composite sectionBody;
    
    private Button changeProjectButton;
    private Label projectNameLabel;

    private final DSPPluginImageHelper imageHelper = AdvisorUiPlugin.getImageHelper();
    private final AdvisorHyperLinkListener linkListener;

    private HyperLinkLabelRow sourcesRow;
    private HyperLinkLabelRow viewsRow;
    private HyperLinkLabelRow connectorsRow;
    private HyperLinkLabelRow validationRow;
    private HyperLinkLabelRow schemasRow;
    private HyperLinkLabelRow vdbsRow;
    private HyperLinkLabelRow testRow;

    private static final int MAX_NAME_CHARS = 20;
    
    private static final int SEPARATOR_HEIGHT = 3;

    // ------------ TEXT ----------------------------
    private static final String CRETURN = "\n"; //$NON-NLS-1$
    private static final String TEN_SPACES = "          "; //$NON-NLS-1$
    private static final String TWENTY_FIVE_SPACES = "                         "; //$NON-NLS-1$
    private static final String ELIPSIS = "..."; //$NON-NLS-1$
    //private static final String AUTOVALIDATE_OFF_WARNING = DSPAdvisorI18n.AutovalidateOffMessage;

    /**
     * 
     */
    public DSPStatusSection( FormToolkit toolkit,
                             Composite parent,
                             AdvisorHyperLinkListener linkListener ) {
        super();
        this.toolkit = toolkit;
        this.linkListener = linkListener;

        
        createSection(parent);
    }

    @SuppressWarnings("unused")
	private void createSection(Composite parent) {
        int nColumns = 2;

        SECTION : {
	        this.section = this.toolkit.createSection(parent, Section.TITLE_BAR | ExpandableComposite.TWISTIE | ExpandableComposite.EXPANDED);
	        
	        section.setText(DSPAdvisorI18n.Status);
	        section.setTitleBarForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
	
	        GridData gd = new GridData(GridData.FILL, GridData.FILL, true, true); //GridData.FILL_HORIZONTAL); // | GridData.HORIZONTAL_ALIGN_BEGINNING);
	        gd.horizontalSpan = 2;
	        section.setLayoutData(gd);
	
	        sectionBody = toolkit.createComposite(section);
	
	        GridLayout layout = new GridLayout(2, false);
	        layout.numColumns = nColumns;
	        layout.verticalSpacing = 3;
	        layout.horizontalSpacing = 3;
	        sectionBody.setLayout(layout);

	        GridData bodyGD = new GridData(GridData.FILL_BOTH);
	        bodyGD.verticalAlignment = GridData.CENTER;
	        sectionBody.setLayoutData(bodyGD);
        }
        
    	
		SECTION_TOOLBAR : {
			addSectionToolbar();
		}

        PREJECT_LABEL : {
	        
	        Label projectLabel = new Label(sectionBody, SWT.NONE);
	        projectLabel.setText(DSPAdvisorI18n.StatusSectionProjectPrefix);
	        projectLabel.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
	        
	        projectNameLabel = new Label(sectionBody, SWT.NONE);
	        projectNameLabel.setText(DSPAdvisorI18n.NoProjectMessage);
	        projectNameLabel.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));

        }
        
        STATUS_ROWS : {
	        createSeparator(sectionBody, nColumns, SEPARATOR_HEIGHT);
	        
	        connectorsRow = new HyperLinkLabelRow(GROUP_CONNECTIONS, toolkit, sectionBody, DSPAdvisorI18n.ConnectionFactoriesLabel
	                                                                                    + TWENTY_FIVE_SPACES, this.linkListener);
	        sourcesRow = new HyperLinkLabelRow(GROUP_SOURCES, toolkit, sectionBody, DSPAdvisorI18n.SourcesLabel, this.linkListener);
	        schemasRow = new HyperLinkLabelRow(GROUP_XML_SCHEMAS, toolkit, sectionBody, DSPAdvisorI18n.XmlSchemaLabel, this.linkListener);
	        viewsRow = new HyperLinkLabelRow(GROUP_VIEWS, toolkit, sectionBody, DSPAdvisorI18n.ViewsLabel, this.linkListener);
	        vdbsRow = new HyperLinkLabelRow(GROUP_VDBS, toolkit, sectionBody, DSPAdvisorI18n.VDBsLabel, this.linkListener);
	        
	        createSeparator(sectionBody, nColumns, SEPARATOR_HEIGHT);
	        
	        validationRow = new HyperLinkLabelRow(GROUP_MODEL_VALIDATION, toolkit, sectionBody, DSPAdvisorI18n.ModelValidationLabel, this.linkListener);
	        testRow = new HyperLinkLabelRow(GROUP_TEST, toolkit, sectionBody, DSPAdvisorI18n.TestLabel, this.linkListener);
	        
	        createSeparator(sectionBody, nColumns, SEPARATOR_HEIGHT);

        }
        
        updateStatus(DSPValidationConstants.STATUS_MSGS.NO_PROJECT_SELECTED);

        sectionBody.pack(true);
        section.setClient(sectionBody);

        section.setExpanded(true);

    }

    private void createSeparator( Composite parent,
                                  int nColumns,
                                  int height ) {
        Composite bottomSep = toolkit.createCompositeSeparator(parent);
        //TableWrapData layoutData = new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.FILL_GRAB, 1, nColumns);
        GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
        layoutData.horizontalSpan = nColumns;
        layoutData.heightHint = height;
        bottomSep.setLayoutData(layoutData);
    }
    
	
	private void addSectionToolbar() {
        // configure section toolbar
        Button[] buttons = FormUtil.createSectionToolBar(this.section, toolkit,
                                                         new String[] { DSPAdvisorI18n.StatusSectionChangeProject });

        // configure add button
        this.changeProjectButton = buttons[0];
        this.changeProjectButton.setEnabled(true);
        this.changeProjectButton.addSelectionListener(new SelectionAdapter() {

            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
             */
            @Override
            public void widgetSelected( SelectionEvent e ) {
                Object[] projects = WidgetUtil.showWorkspaceObjectSelectionDialog(DSPAdvisorI18n.StatusSectionChangeProjectTooltip,
                        DSPAdvisorI18n.StatusSectionSelectProjectMessage,
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
        this.changeProjectButton.setToolTipText(DSPAdvisorI18n.StatusSectionChangeProjectTooltip);

	}

    public void updateStatus( Status theStatus ) {
        CoreArgCheck.isInstanceOf(ModelProjectStatus.class, theStatus);

        ModelProjectStatus status = (ModelProjectStatus)theStatus;

        boolean autovalidateOn = ResourcesPlugin.getWorkspace().isAutoBuilding();

        // =========================================================================================================================

        if (status == null || status.getCurrentModelProject() == null) {
        	setNoProjectStatus(status);
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

            //String imageMessage = status.getModelStatus().getMessage();
//            if (!autovalidateOn) {
//                imageMessage = AUTOVALIDATE_OFF_WARNING;
//            }
            this.validationRow.update(validationImage,
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
                                   status.getSourceModelsStatus().getMessage() + CRETURN + DSPAdvisorI18n.Status_ClickForActions);
            if (status.getSourceModelsStatus().getSeverity() == IStatus.ERROR) {
                hasErrors = true;
            }
            // --------------------------------------------------
            // Connector Bindings Status
            // --------------------------------------------------
            this.connectorsRow.update(getButtonImage(GROUP_CONNECTIONS, status.getConnectionFactoriesStatus().getSeverity()),
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
                                 status.getViewModelsStatus().getMessage() + CRETURN + DSPAdvisorI18n.Status_ClickForActions);
            if (status.getViewModelsStatus().getSeverity() == IStatus.ERROR) {
                hasErrors = true;
            }
            
            // --------------------------------------------------
            // VDBs Status
            // --------------------------------------------------

            this.vdbsRow.update(getButtonImage(GROUP_VDBS, status.getVdbsStatus().getSeverity()),
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
                    status.getTestStatus().getMessage() + CRETURN + DSPAdvisorI18n.Status_ClickForActions);
            
            if (status.getTestStatus().getSeverity() == IStatus.ERROR) {
                hasErrors = true;
            }

            if( hasErrors) {
            	// DO THING FOR NOW
            }
        }

        if (status.getCurrentModelProject() != null) {
        	projectNameLabel.setText( status.getCurrentModelProject().getName());
        } else {
        	projectNameLabel.setText(DSPAdvisorI18n.StatusSectionTitle_NoProjectSelected);
        }

        refreshIcons();

        section.getParent().redraw();
    }
    
    private void setNoProjectStatus(ModelProjectStatus status) {
    	String message = DSPAdvisorI18n.Status_Project_Not_Selected;
    	int severity = IStatus.OK;
    	this.validationRow.update(getButtonImage(GROUP_MODEL_VALIDATION, severity), message,
    			message + CRETURN + DSPAdvisorI18n.Status_ClickForActions);
    	this.sourcesRow.update(getButtonImage(GROUP_SOURCES, severity), message,
    			message + CRETURN + DSPAdvisorI18n.Status_ClickForActions);
    	this.connectorsRow.update(getButtonImage(GROUP_CONNECTIONS, severity), message,
    			message + CRETURN + DSPAdvisorI18n.Status_ClickForActions);
    	this.viewsRow.update(getButtonImage(GROUP_VIEWS, severity), message,
    			message + CRETURN + DSPAdvisorI18n.Status_ClickForActions);
    	this.testRow.update(getButtonImage(GROUP_TEST, severity), message,
    			message + CRETURN + DSPAdvisorI18n.Status_ClickForActions);
    	this.vdbsRow.update(getButtonImage(GROUP_VDBS, severity), message,
    			message + CRETURN + DSPAdvisorI18n.Status_ClickForActions);
        this.schemasRow.update(getButtonImage(GROUP_XML_SCHEMAS, severity), message,
    			message + CRETURN + DSPAdvisorI18n.Status_ClickForActions);
        
        
        this.validationRow.setLinkTooltip(DSPAdvisorI18n.NoProjectMessage + CRETURN + DSPAdvisorI18n.Status_ClickForActions);
        this.sourcesRow.setLinkTooltip(DSPAdvisorI18n.NoProjectMessage + CRETURN + DSPAdvisorI18n.Status_ClickForActions);
        this.connectorsRow.setLinkTooltip(DSPAdvisorI18n.NoProjectMessage + CRETURN + DSPAdvisorI18n.Status_ClickForActions);
        this.viewsRow.setLinkTooltip(DSPAdvisorI18n.NoProjectMessage + CRETURN + DSPAdvisorI18n.Status_ClickForActions);
        this.vdbsRow.setLinkTooltip(DSPAdvisorI18n.NoProjectMessage + CRETURN + DSPAdvisorI18n.Status_ClickForActions);
        this.testRow.setLinkTooltip(DSPAdvisorI18n.NoProjectMessage + CRETURN + DSPAdvisorI18n.Status_ClickForActions);
        this.schemasRow.setLinkTooltip(DSPAdvisorI18n.NoProjectMessage + CRETURN + DSPAdvisorI18n.Status_ClickForActions);

        resetStatusFGColor(true);

        this.validationRow.setImage(imageHelper.EMPTY_BOX_IMAGE);
        this.validationRow.setText(DSPAdvisorI18n.ModelValidationLabel + TEN_SPACES);
        this.sourcesRow.setImage(imageHelper.EMPTY_BOX_IMAGE);
        this.connectorsRow.setImage(imageHelper.EMPTY_BOX_IMAGE);
        this.viewsRow.setImage(imageHelper.EMPTY_BOX_IMAGE);
        this.vdbsRow.setImage(imageHelper.EMPTY_BOX_IMAGE);
        this.testRow.setImage(imageHelper.EMPTY_BOX_IMAGE);
        this.schemasRow.setImage(imageHelper.EMPTY_BOX_IMAGE);

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
