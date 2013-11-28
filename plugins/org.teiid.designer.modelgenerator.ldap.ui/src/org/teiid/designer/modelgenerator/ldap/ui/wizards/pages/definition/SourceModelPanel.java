/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.modelgenerator.ldap.ui.wizards.pages.definition;

import java.util.Properties;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.teiid.core.designer.event.IChangeListener;
import org.teiid.core.designer.event.IChangeNotifier;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.core.workspace.ModelWorkspaceManager;
import org.teiid.designer.datatools.connection.ConnectionInfoHelper;
import org.teiid.designer.datatools.connection.IConnectionInfoHelper;
import org.teiid.designer.modelgenerator.ldap.ui.ModelGeneratorLdapUiConstants;
import org.teiid.designer.modelgenerator.ldap.ui.wizards.LdapImportWizardManager;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.common.util.WidgetUtil;
import org.teiid.designer.ui.viewsupport.DesignerPropertiesUtil;
import org.teiid.designer.ui.viewsupport.MetamodelSelectionUtilities;
import org.teiid.designer.ui.viewsupport.ModelProjectSelectionStatusValidator;
import org.teiid.designer.ui.viewsupport.ModelUtilities;
import org.teiid.designer.ui.viewsupport.ModelingResourceFilter;

/**
 * @since 8.0
 */
public class SourceModelPanel implements IChangeListener, ModelGeneratorLdapUiConstants {
    // Source Model Definition
    private Text sourceModelFileText;
    private Text sourceModelContainerText;
//    private Text sourceModelHelpText;

    private IConnectionInfoHelper connectionInfoHelper;

    private final LdapImportWizardManager importManager;

    private final ModelWorkspaceManager modelWorkspaceManager = ModelWorkspaceManager.getModelWorkspaceManager();

    private boolean refreshing = false;

    /**
     * Create new instance
     *
     * @param parent
     * @param importManager
     */
    public SourceModelPanel(Composite parent,
                            LdapImportWizardManager importManager) {
        super();
        this.importManager = importManager;
        this.importManager.addChangeListener(this);
        init(parent);
    }

    private String getString(String key, Object... properties) {
        return ModelGeneratorLdapUiConstants.UTIL.getString(getClass().getSimpleName() + "_" + key, properties); //$NON-NLS-1$
    }

    @SuppressWarnings( "unused" )
    private void init(Composite parent) {

        this.connectionInfoHelper = new ConnectionInfoHelper();

        SOURCE_MODEL_INFO: {
            Group group = WidgetFactory.createGroup(parent, getString("sourceModelDefinition"), GridData.FILL_HORIZONTAL, 1, 3); //$NON-NLS-1$
            Label locationLabel = new Label(group, SWT.NULL);
            locationLabel.setText(getString("location")); //$NON-NLS-1$

            this.sourceModelContainerText = new Text(group, SWT.BORDER | SWT.SINGLE);
            GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
            this.sourceModelContainerText.setLayoutData(gridData);
            WidgetUtil.colorizeWidget(this.sourceModelContainerText, WidgetUtil.TEXT_COLOR_BLUE, true);
            this.sourceModelContainerText.setEditable(false);

            Button browseButton = new Button(group, SWT.PUSH);
            gridData = new GridData();

            browseButton.setLayoutData(gridData);
            browseButton.setText(getString("browseElipsis")); //$NON-NLS-1$
            browseButton.setToolTipText(getString("browseToSelectModelsLocation", getString("source_lower"))); //$NON-NLS-1$ //$NON-NLS-2$
            browseButton.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    handleSourceModelLocationBrowse();
                }
            });

            Label fileLabel = new Label(group, SWT.NULL);
            fileLabel.setText(getString("name")); //$NON-NLS-1$
            fileLabel.setToolTipText(getString("sourceNameTooltip")); //$NON-NLS-1$

            this.sourceModelFileText = new Text(group, SWT.BORDER | SWT.SINGLE);
            gridData = new GridData(GridData.FILL_HORIZONTAL);
            gridData.widthHint = 200;
            this.sourceModelFileText.setLayoutData(gridData);
            this.sourceModelFileText.setToolTipText(getString("sourceNameTooltip")); //$NON-NLS-1$
            WidgetUtil.colorizeWidget(this.sourceModelFileText, WidgetUtil.TEXT_COLOR_BLUE, false);
            this.sourceModelFileText.addModifyListener(new ModifyListener() {
                @Override
                public void modifyText(ModifyEvent e) {
                    // Check view file name for existing if "location" is already
                    // set
                    handleSourceModelTextChanged();
                }
            });

            browseButton = new Button(group, SWT.PUSH);
            gridData = new GridData();
            browseButton.setLayoutData(gridData);
            browseButton.setText(getString("browseElipsis")); //$NON-NLS-1$
            browseButton.setToolTipText(getString("browseToSelect_0_Model", getString("source_lower"))); //$NON-NLS-1$ //$NON-NLS-2$
            browseButton.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    handleSourceModelBrowse();
                }
            });
        }
    }

    /**
     * Refreshes the panel
     */
    public void refresh() {
        // Set field values from import manager\
        refreshing = true;

        refreshUiFromManager();

        refreshing = false;
    }

    /**
     * Uses the standard container selection dialog to choose the new value for
     * the container field.
     */
    private void handleSourceModelLocationBrowse() {
        final IContainer folder = WidgetUtil.showFolderSelectionDialog(ModelerCore.getWorkspace().getRoot(),
                                                                       new ModelingResourceFilter(),
                                                                       new ModelProjectSelectionStatusValidator());

        if (folder != null && sourceModelContainerText != null) {
            // viewModelContainerText.setText(folder.getFullPath().makeRelative().toString());
            this.importManager.setSourceModelLocation(folder);
            this.sourceModelContainerText.setText(folder.getFullPath().makeRelative().toString());
        }

        notifyChanged();
    }

    private void handleSourceModelBrowse() {
        IFile modelFile = MetamodelSelectionUtilities.selectSourceModelInWorkspace();

        if (modelFile != null) {
            IContainer folder = modelFile.getParent();
            String modelName = modelFile.getFullPath().lastSegment();
            this.importManager.setSourceModelExists(true);
            this.importManager.setSourceModelLocation(folder);
            this.importManager.setSourceModelName(modelName);
            this.sourceModelContainerText.setText(folder.getFullPath().makeRelative().toString());
            this.sourceModelFileText.setText(modelName);
        }

        notifyChanged();
    }

    private void handleSourceModelTextChanged() {
        if (refreshing) return;

        String newName = ""; //$NON-NLS-1$
        if (this.sourceModelFileText.getText() != null) {
            if (this.sourceModelFileText.getText().length() == 0) {
                this.importManager.setSourceModelName(newName);
                this.importManager.setSourceModelExists(false);
            } else {
                newName = this.sourceModelFileText.getText();
                this.importManager.setSourceModelName(newName);
                this.importManager.setSourceModelExists(sourceModelExists());
            }

        }

        notifyChanged();
    }

    /**
     * @return if source model already exists
     */
    public boolean sourceModelExists() {
        if (this.importManager.getSourceModelLocation() == null) {
            return false;
        }

        return modelWorkspaceManager.modelExists(importManager.getSourceModelLocation().getFullPath().toOSString(),
                                                 this.sourceModelFileText.getText());
    }

    private void refreshUiFromManager() {
        this.refreshing = true;
        IContainer sourceLocation = this.importManager.getSourceModelLocation();
        if (sourceLocation != null) {
            this.sourceModelContainerText.setText(sourceLocation.getFullPath().makeRelative().toString());
        }
        if (this.importManager.getSourceModelName() != null) {
            if (!this.sourceModelFileText.getText().equals(this.importManager.getSourceModelName())) {
                this.sourceModelFileText.setText(this.importManager.getSourceModelName());
            }
        }

        updateDesignerProperties();

        this.refreshing = false;
    }

    /**
     * Determine if the Source Model has ConnectionProfile set.
     *
     * @return true if source model has connection profile.
     */
    public boolean sourceModelHasConnectionProfile() {
        if (!sourceModelExists()) {
            return false;
        }

        try {
            IResource sourceModel = ModelUtilities.getModelFile(this.importManager.getSourceModelLocation().getFullPath().toOSString(),
                                                                this.sourceModelFileText.getText());

            ModelResource smr = ModelUtilities.getModelResourceForIFile((IFile)sourceModel, false);
            if (smr != null) {
                IConnectionProfile profile = connectionInfoHelper.getConnectionProfile(smr);
                if (profile == null || this.importManager.getConnectionProfile() == null) {
                    return false;
                }
                return true;
            }
        } catch (ModelWorkspaceException ex) {
            ModelGeneratorLdapUiConstants.UTIL.log(IStatus.ERROR, ex, getString("errorDeterminingSourceModelHasProfile")); //$NON-NLS-1$
        }

        return false;
    }

    /**
     * @return source model has same connection profile as previously set
     */
    public boolean sourceModelHasSameConnectionProfile() {
        if (!sourceModelExists()) {
            return false;
        }

        try {
            IResource sourceModel = ModelUtilities.getModelFile(this.importManager.getSourceModelLocation().getFullPath().toOSString(),
                                                                this.sourceModelFileText.getText());

            ModelResource smr = ModelUtilities.getModelResourceForIFile((IFile)sourceModel, false);
            if (smr != null) {
                IConnectionProfile profile = connectionInfoHelper.getConnectionProfile(smr);
                if (profile == null || this.importManager.getConnectionProfile() == null) {
                    return false;
                }

                if (profile.getName().equalsIgnoreCase(this.importManager.getConnectionProfile().getName())) {
                    return true;
                }
            }
        } catch (ModelWorkspaceException ex) {
            ModelGeneratorLdapUiConstants.UTIL.log(IStatus.ERROR, ex, getString("errorDeterminingSourceModelHasMatchingProfile")); //$NON-NLS-1$
        }

        return false;
    }

    private void updateDesignerProperties() {
        Properties designerProperties = this.importManager.getDesignerProperties();
        if (designerProperties != null) {
            if (this.sourceModelFileText.getText() != null) {
                DesignerPropertiesUtil.setSourceModelName(designerProperties, this.sourceModelFileText.getText());
            }
            if (this.importManager.getSourceModelLocation() != null) {
                DesignerPropertiesUtil.setProjectName(designerProperties,
                                                      this.importManager.getSourceModelLocation().getProject().getName());
            }
        }
    }

    @Override
    public void stateChanged(IChangeNotifier theSource) {
        refreshUiFromManager();
    }

    private void notifyChanged() {
        this.importManager.notifyChanged();
    }
}
