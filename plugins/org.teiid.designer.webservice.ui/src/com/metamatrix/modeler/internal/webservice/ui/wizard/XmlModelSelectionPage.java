/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.webservice.ui.wizard;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.help.IWorkbenchHelpSystem;
import org.teiid.core.util.FileUtils;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.ui.explorer.ModelExplorerLabelProvider;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelWorkspaceViewerFilter;
import com.metamatrix.modeler.internal.webservice.ui.IInternalUiConstants;
import com.metamatrix.modeler.webservice.IWebServiceModelBuilder;
import com.metamatrix.modeler.webservice.ui.util.WebServiceUiUtil;
import com.metamatrix.ui.internal.InternalUiConstants;
import com.metamatrix.ui.internal.product.ProductCustomizerMgr;
import com.metamatrix.ui.internal.util.UiUtil;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.util.WidgetUtil;
import com.metamatrix.ui.internal.viewsupport.StatusInfo;
import com.metamatrix.ui.internal.wizard.AbstractWizardPage;

/**
 * @since 4.2
 */
public class XmlModelSelectionPage extends AbstractWizardPage
    implements FileUtils.Constants, IInternalUiConstants, IInternalUiConstants.HelpContexts, IInternalUiConstants.Images {

    /** Properties key prefix. */
    private static final String PREFIX = I18nUtil.getPropertyPrefix(XmlModelSelectionPage.class);

    /** Key for XML folder MRU list. */
    private static final String XML_FOLDER_MRU = "xmlFolderList"; //$NON-NLS-1$

    /** The model builder. */
    private IWebServiceModelBuilder builder;

    /** Indicates if initial state has been restored. */
    private boolean initialized;

    /** The XML model workspace location. */
    private String modelLocation;

    /** The XML model name. */
    private String modelName;

    /** Filter used in workspace selection dialogs. */
    private ModelWorkspaceViewerFilter viewerFilter;

    /** Button to browse to choose the model location. */
    private Button btnFolder;

    /** MRU for previously chosen locations. */
    private Combo cbxFolder;

    /** Field for displaying model name. */
    private Text txfModel;

    /** Checkbox indicating if a new model will be generated. */
    private Button chkGenerateModel;

    /**
     * Constructs a <code>XmlModelSelectionPage</code> using the specified builder.
     * 
     * @param theBuilder the model builder
     * @since 4.2
     */
    public XmlModelSelectionPage( IWebServiceModelBuilder theBuilder ) {
        super(XmlModelSelectionPage.class.getSimpleName(), getString("title")); //$NON-NLS-1$

        this.builder = theBuilder;
        this.viewerFilter = new ModelWorkspaceViewerFilter();
        setImageDescriptor(WebServiceUiUtil.getImageDescriptor(NEW_MODEL_BANNER));
        setPageComplete(true);
    }

    /**
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     * @since 4.2
     */
    public void createControl( Composite theParent ) {
        //
        // create main container
        //

        final int COLUMNS = 3;
        Composite pnl = WidgetFactory.createPanel(theParent, SWT.NONE, GridData.FILL_BOTH);
        pnl.setLayout(new GridLayout(COLUMNS, false));
        setControl(pnl);

        IWorkbenchHelpSystem helpSystem = UiUtil.getWorkbench().getHelpSystem();
        helpSystem.setHelp(pnl, XML_MODEL_SELECTION_PAGE);

        //
        // ROW 1: GENERATE MODEL
        //

        this.chkGenerateModel = WidgetFactory.createCheckBox(pnl, getString("checkbox.generateModel"), SWT.NONE, COLUMNS, true); //$NON-NLS-1$
        this.chkGenerateModel.setToolTipText(getString("checkbox.generateModel.tip")); //$NON-NLS-1$
        this.chkGenerateModel.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent theEvent ) {
                handleGenerateModelSelected();
            }
        });

        //
        // ROW 2: FOLDER INFO
        //

        // folder label
        CLabel folderLabel = WidgetFactory.createLabel(pnl, getString("label.folder")); //$NON-NLS-1$

        // folder combo
        this.cbxFolder = WidgetFactory.createCombo(pnl, SWT.READ_ONLY, GridData.FILL_HORIZONTAL);
        this.cbxFolder.addModifyListener(new ModifyListener() {
            public void modifyText( ModifyEvent theEvent ) {
                handleFolderChanged();
            }
        });

        // folder browse button
        this.btnFolder = WidgetFactory.createButton(pnl, InternalUiConstants.Widgets.BROWSE_BUTTON);
        this.btnFolder.setToolTipText(getString("button.browse.folder.tip")); //$NON-NLS-1$
        this.btnFolder.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent theEvent ) {
                handleBrowseFolderSelected();
            }
        });

        //
        // ROW 3: MODEL INFO
        //

        // folder label
        WidgetFactory.createLabel(pnl, getString("label.xmlModel")); //$NON-NLS-1$

        // model combo
        this.txfModel = WidgetFactory.createTextField(pnl, GridData.FILL_HORIZONTAL);
        this.txfModel.addModifyListener(new ModifyListener() {
            public void modifyText( ModifyEvent theEvent ) {
                handleModelChanged();
            }
        });

        if (ProductCustomizerMgr.getInstance().getProductCharacteristics().isHiddenProjectCentric()) {
            folderLabel.setVisible(false);
            btnFolder.setVisible(false);
            cbxFolder.setVisible(false);
        }
    }

    /**
     * @see org.eclipse.jface.dialogs.IDialogPage#dispose()
     * @since 4.2
     */
    @Override
    public void dispose() {
        saveFolderMru();
        super.dispose();
    }

    /**
     * Override to replace the NewModelWizard settings with the section devoted to the Web Service Model Wizard.
     * 
     * @see org.eclipse.jface.wizard.WizardPage#getDialogSettings()
     * @since 4.2
     */
    @Override
    protected IDialogSettings getDialogSettings() {
        IDialogSettings settings = super.getDialogSettings();

        if (settings != null) {
            // get the right section of the NewModelWizard settings
            IDialogSettings temp = settings.getSection(DIALOG_SETTINGS_SECTION);

            if (temp == null) {
                settings = settings.addNewSection(DIALOG_SETTINGS_SECTION);
            } else {
                settings = temp;
            }
        }

        return settings;
    }

    /**
     * Obtains a string representation of the specified location.
     * 
     * @param theContainer the location whose string representation is being requested
     * @return the location text
     * @since 4.2
     */
    private String getLocationText( IContainer theContainer ) {
        return (theContainer == null) ? "" //$NON-NLS-1$
        : theContainer.getFullPath().makeRelative().toString();
    }

    /**
     * Obtains an <code>IPath</code> for the XML model being generated.
     * 
     * @return the path or <code>null</code> if no model is being generated or model information is not complete
     * @since 4.2
     * @see #isModelInfoComplete()
     */
    private IPath getModelPath() {
        return (this.chkGenerateModel.getSelection() && isModelInfoComplete()) ? new Path(getModelPath(this.modelLocation,
                                                                                                       this.modelName)) : null;

    }

    private String getModelPath( final String modelLocation,
                                 final String modelName ) {
        return new StringBuffer().append(modelLocation).append(File.separator).append(modelName).append(FILE_EXTENSION_SEPARATOR_CHAR).append(ModelUtil.EXTENSION_XMI).toString();
    }

    /**
     * Utility to get localized text from properties file.
     * 
     * @param theKey the key whose localized value is being requested
     * @return the localized text
     * @since 4.2
     */
    private static String getString( String theKey ) {
        return UTIL.getString(new StringBuffer().append(PREFIX).append(theKey).toString());
    }

    /**
     * Handler for when the browse location/folder is pushed.
     * 
     * @since 4.2
     */
    void handleBrowseFolderSelected() {
        this.viewerFilter.setShowModels(false);
        Object[] resources = WidgetUtil.showWorkspaceObjectSelectionDialog(getString("dialog.xmlModelLocationChooser.title"), //$NON-NLS-1$
                                                                           getString("dialog.xmlModelLocationChooser.msg"), //$NON-NLS-1$
                                                                           false,
                                                                           this.modelLocation,
                                                                           this.viewerFilter,
                                                                           null,
                                                           				   new ModelExplorerLabelProvider());

        if ((resources != null) && (resources.length > 0)) {
            setFolder(getLocationText((IContainer)resources[0]));
        }
    }

    /**
     * Handler for when the location/folder text is changed. Updates the builder and sets the page status.
     * 
     * @since 4.2
     */
    void handleFolderChanged() {
        this.modelLocation = this.cbxFolder.getText();
        updateModelInfo();
    }

    /**
     * Handler for when the generate model checkbox is selected/deselected. Updates the builder and sets the page status.
     * 
     * @since 4.2
     */
    void handleGenerateModelSelected() {
        boolean enable = this.chkGenerateModel.getSelection();

        this.cbxFolder.setEnabled(enable);
        this.btnFolder.setEnabled(enable);
        this.txfModel.setEnabled(enable);

        updateModelInfo();
    }

    /**
     * Handler for when the model name text is changed. Updates the builder and sets the page status.
     * 
     * @since 4.2
     */
    void handleModelChanged() {
        this.modelName = this.txfModel.getText();
        updateModelInfo();
    }

    /**
     * Indicates if the XML model location and name have been set.
     * 
     * @return <code>true</code>if complete; <code>false</code> otherwise.
     * @since 4.2
     */
    private boolean isModelInfoComplete() {
        return ((this.modelName != null) && (this.modelName.length() > 0) && (this.modelLocation != null) && (this.modelLocation.length() > 0));
    }

    /**
     * Updates the MRU for the XML folder combo to agree with the dialog settings.
     * 
     * @since 4.2
     */
    private void loadFolderMru() {
        IDialogSettings settings = getDialogSettings();

        if (settings != null) {
            // get the right section
            String[] temp = settings.getArray(XML_FOLDER_MRU);

            if (temp != null) {
                List folders = new ArrayList(temp.length);

                for (int i = 0; i < temp.length; i++) {
                    // only add if folder exists in workspace
                    if (ResourcesPlugin.getWorkspace().getRoot().findMember(temp[i]) != null) {
                        folders.add(temp[i]);
                    }
                }

                WidgetUtil.setComboItems(this.cbxFolder, folders, null, true);
            }
        }
    }

    private void saveFolderMru() {
        IDialogSettings settings = getDialogSettings();

        // cbxFolder will be null if wizard is cancelled and this page has not be shown yet
        if ((settings != null) && (this.cbxFolder != null)) {
            WidgetUtil.saveSettings(settings, XML_FOLDER_MRU, this.cbxFolder);
        }
    }

    /**
     * Sets the folder/location into the MRU control.
     * 
     * @param thePath the location/folder
     * @since 4.2
     */
    private void setFolder( String thePath ) {
        if (thePath != null) {
            int index = this.cbxFolder.indexOf(thePath);

            if (index == -1) {
                this.cbxFolder.add(thePath);
                index = this.cbxFolder.indexOf(thePath);
            }

            this.cbxFolder.select(index);
        }
    }

    /**
     * Sets the model name into the MRU control.
     * 
     * @param theName the model name
     * @since 4.2
     */
    private void setModelName( String theName ) {
        this.txfModel.setText(theName);
    }

    /**
     * Sets the wizard page status message.
     * 
     * @since 4.2
     */
    private void setPageStatus( IStatus theStatus ) {
        if (theStatus.getSeverity() == IStatus.ERROR) {
            setErrorMessage(theStatus.getMessage());
            setPageComplete(false);
        } else {
            setErrorMessage(null); // must clear error message
            setMessage(theStatus.getMessage(), theStatus.getSeverity());
            setPageComplete(true);
        }
    }

    /**
     * Sets page complete status and message based on the specified information and the builder's validation state.
     * 
     * @param theSeverity the UI validation severity
     * @param theMessage the UI validation message
     * @since 4.2
     */
    private void setPageStatus( int theSeverity,
                                String theMessage ) {
        IStatus builderStatus = this.builder.validateXSDNamespaces();
        int builderSeverity = builderStatus.getSeverity();

        if (theSeverity > builderSeverity) {
            setPageStatus(new StatusInfo(PLUGIN_ID, theSeverity, (theMessage == null) ? "" : theMessage)); //$NON-NLS-1$
        } else if (theSeverity < builderSeverity) {
            setPageStatus(builderStatus);
        } else {
            String msg = null;

            if ((theMessage == null) || (theMessage.length() == 0)) {
                msg = builderStatus.getMessage();
            } else {
                msg = new StringBuffer().append(builderStatus.getMessage()).append(" : ").append(theMessage).toString(); //$NON-NLS-1$
            }

            setPageStatus(new StatusInfo(PLUGIN_ID, theSeverity, msg));
        }
    }

    /**
     * @see org.eclipse.jface.dialogs.IDialogPage#setVisible(boolean)
     */
    @Override
    public void setVisible( boolean theShowFlag ) {
        // initialize state
        if (theShowFlag) {
            if (!this.initialized) {
                // update XML folder MRU
                loadFolderMru();

                // set initial folder and name based on web service model
                IPath modelPath = this.builder.getModelPath();
                String name = modelPath.removeFileExtension().lastSegment() + getString("xmlModelSuffix"); //$NON-NLS-1$
                this.builder.setXmlModel(modelPath.removeLastSegments(1).append(name).addFileExtension(modelPath.getFileExtension()));

                this.initialized = true;
            }

            // initialized model if necessary
            IPath xmlModel = this.builder.getXmlModel();

            if (xmlModel != null) {
                setFolder(xmlModel.removeLastSegments(1).toOSString());
                setModelName(xmlModel.removeFileExtension().lastSegment());
            }

            // call this method to set the page status
            updateModelInfo();

            // set focus so that the help context will be correct
            this.chkGenerateModel.setFocus();
        } else {
            saveFolderMru();

            // do this in order to allow finish button to be pressed prior to viewing this page
            setPageComplete(true);
        }

        super.setVisible(theShowFlag);
    }

    /**
     * Updates the model info in the model builder and then sets the page status message.
     * 
     * @since 4.2
     */
    private void updateModelInfo() {
        if (this.chkGenerateModel.getSelection()) {
            if (isModelInfoComplete()) {
                // see if model exists or would be a new model
                String temp = null;
                IPath containerPath = new Path(this.modelLocation);
                IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(containerPath);

                if (resource != null) {
                    IPath path = resource.getLocation();
                    temp = path.toOSString();
                }

                // validate characters in the proposed name
                if (ModelUtilities.validateModelName(this.modelName, ModelUtil.EXTENSION_XMI) == null) {
                    String name = getModelPath(temp, this.modelName);
                    boolean exists = new File(name).exists();

                    if (exists) {
                        this.builder.setXmlModel(null);
                        setPageStatus(IStatus.ERROR, getString("page.msg.modelExists")); //$NON-NLS-1$
                    } else if (this.builder.getModelPath().equals(getModelPath())) {
                        this.builder.setXmlModel(null);
                        setPageStatus(IStatus.ERROR, getString("page.msg.sameAsWebServiceModelName")); //$NON-NLS-1$
                    } else {
                        this.builder.setXmlModel(getModelPath());
                        setPageStatus(IStatus.OK, null);
                    }
                } else {
                    this.builder.setXmlModel(null);
                    setPageStatus(IStatus.ERROR, getString("page.msg.modelNameInvalid")); //$NON-NLS-1$
                }
            } else {
                this.builder.setXmlModel(null);
                setPageStatus(IStatus.ERROR, getString("page.msg.modelIncomplete")); //$NON-NLS-1$
            }
        } else {
            this.builder.setXmlModel(null);
            setPageStatus(IStatus.OK, getString("page.msg.xmlModelNotGenerated")); //$NON-NLS-1$
        }
    }
}
