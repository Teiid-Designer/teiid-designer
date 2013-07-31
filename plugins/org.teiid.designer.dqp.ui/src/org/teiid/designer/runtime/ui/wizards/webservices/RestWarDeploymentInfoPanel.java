/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime.ui.wizards.webservices;

import java.util.Collections;
import java.util.Properties;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.designer.runtime.ui.DqpUiConstants;
import org.teiid.designer.runtime.ui.DqpUiPlugin;
import org.teiid.designer.runtime.ui.DqpUiStringUtil;
import org.teiid.designer.ui.common.InternalUiConstants;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.common.util.WidgetUtil;
import org.teiid.designer.ui.viewsupport.DesignerProperties;


/**
 * @since 8.0
 */
public abstract class RestWarDeploymentInfoPanel extends Composite implements InternalModelerWarUiConstants {

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTANTS
    // /////////////////////////////////////////////////////////////////////////////////////////////
    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(RestWarDeploymentInfoPanel.class);
    protected static final String INITIAL_MESSAGE = getString("initialMessage"); //$NON-NLS-1$

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    // /////////////////////////////////////////////////////////////////////////////////////////////
    protected IDialogSettings settings;
    protected RestWarDeploymentInfoDialog dialog;
    protected Text txfWarFileDeploymentLocation;
    protected Text txfContext;
    protected Text txfJNDIName;
    protected Button checkboxIncludeRestJars;
    private Button warBrowseButton;
    private Button restoreDefaultButton;

    protected IFile theVdb;

    protected String WARFILELOCATION;
    protected String CONTEXTNAME;
    protected String JNDI_NAME;
    
    protected String TARGETVERSION;
    Combo targetVersionCombo;
    private static String _81_or_less = getString("81Text"); //$NON-NLS-1$
    protected static String _82_or_greater = getString("82Text"); //$NON-NLS-1$
    private static final String[] TARGET_ARRAY = { _81_or_less, _82_or_greater };
    
    private Button selectEngineJar;
    private Button selectCommonCoreJar;
    
    protected String ENGINEJARFILELOCATION;
    protected String COMMONCOREJARFILELOCATION;
    
    protected Text engineJarFileLocation;
    protected Text commonCoreJarFileLocation;
    
    private DesignerProperties designerProperties;

    /**
     * @param parent
     * @param dialog
     * @param theVdb
     * @param theVdbContext
     * @since 7.4
     */
    public RestWarDeploymentInfoPanel( Composite parent,
                                       RestWarDeploymentInfoDialog dialog,
                                       IFile theVdb,
                                       Properties designerProperties) {
        super(parent, SWT.NONE);
        this.dialog = dialog;
        this.theVdb = theVdb;
        this.setLayout(new GridLayout());
        this.setLayoutData(new GridData(GridData.FILL_BOTH));
        this.designerProperties = (DesignerProperties)designerProperties;
        init(this);
    }

    /**
     * @param id
     * @return
     * @since 7.4
     */
    protected static String getString( final String id ) {
        return DqpUiStringUtil.getString(I18N_PREFIX + id);
    }

    /**
     * @since 7.4
     */
    protected void loadData() {

        try {
            // war file location
            String text = (this.settings.get(WARFILELOCATION) == null ? RestWarDataserviceModel.getInstance().getWarFileLocation() : this.settings.get(WARFILELOCATION));
            txfWarFileDeploymentLocation.setText(text);

            // context name should be populated as default, not from the
            // settings.
            text = RestWarDataserviceModel.getInstance().getContextName();
            txfContext.setText(text);

            // JNDI Name
            text = RestWarDataserviceModel.getInstance().getJndiName();
            txfJNDIName.setText(text);
            if( designerProperties != null ) {
            	String vdbJndiName = this.designerProperties.getVdbJndiName();
            	if( vdbJndiName != null ) {
            		txfJNDIName.setText(vdbJndiName);
            	}
            }

        } catch (RuntimeException err) {
            DqpUiConstants.UTIL.log(err);
        }

    }

    /**
     * @param isValid
     * @since 7.4
     */
    protected void setDialogMessage( boolean isValid ) {

        this.dialog.setMessage(INITIAL_MESSAGE);
        this.dialog.setOkButtonEnable(isValid);
    }

    /**
     * @param status
     * @since 7.4
     */
    protected void setDialogMessage( IStatus status ) {
        boolean isError = (status.getSeverity() == IStatus.ERROR);

        /**
         * Need to convert the error status code from 4 to 3 because error code in setMessage() is not mapped correctly or
         * IStatus.ERROR != IMessageProvider.ERROR
         */
        int statusCode = (status.getSeverity() == IStatus.ERROR ? IMessageProvider.ERROR : status.getSeverity());

        this.dialog.setMessage(INITIAL_MESSAGE);
        if (!status.isOK()) {
            this.dialog.setMessage(status.getMessage(), statusCode);
        }

        this.dialog.setOkButtonEnable(!isError);
    }

    /**
     * @since 7.4
     */
    protected abstract void validatePage();

    /**
     * @param parent
     * @since 7.4
     */
    private void init( Composite parent ) {

        createDeploymentInfoComposite(parent);
        createVersionInfoComposite(parent);
        createRestoreDefaultAndIncludeJars(parent);

        this.settings = WidgetUtil.initializeSettings(this, DqpUiPlugin.getDefault());

        addListeners();
    }

    /**
     * @param parent
     * @since 7.4
     */
    private void createDeploymentInfoComposite( final Composite parent ) {

        // Create info group panel
        String text = getString("grpPanelText"); //$NON-NLS-1$
        Group pnlContents = WidgetFactory.createGroup(parent, text, GridData.FILL_HORIZONTAL, 3, 3);
        // ------------------------------------
        // REST Service WAR components
        // ------------------------------------
        // contextLabel
        CONTEXTNAME = getString("contextLabel"); //$NON-NLS-1$       
        WidgetFactory.createLabel(pnlContents, GridData.HORIZONTAL_ALIGN_BEGINNING, 1, CONTEXTNAME);

        // context name
        txfContext = WidgetFactory.createTextField(pnlContents, GridData.FILL_HORIZONTAL, 2);
        text = getString("contextTooltip"); //$NON-NLS-1$
        txfContext.setToolTipText(text);

        // JNDILabel
        JNDI_NAME = getString("jndiLabel"); //$NON-NLS-1$       
        WidgetFactory.createLabel(pnlContents, GridData.HORIZONTAL_ALIGN_BEGINNING, 1, JNDI_NAME);

        // jndi name
        txfJNDIName = WidgetFactory.createTextField(pnlContents, GridData.FILL_HORIZONTAL, 2);
        text = getString("jndiTooltip"); //$NON-NLS-1$
        txfJNDIName.setToolTipText(text);

        // WAR file save location Label
        this.WARFILELOCATION = getString("warFileSaveLocationLabel"); //$NON-NLS-1$       
        WidgetFactory.createLabel(pnlContents, GridData.HORIZONTAL_ALIGN_BEGINNING, 1, this.WARFILELOCATION);

        // WAR file save location textfield
        this.txfWarFileDeploymentLocation = WidgetFactory.createTextField(pnlContents, GridData.FILL_HORIZONTAL, 1);
        text = getString("warFileSaveLocationTooltip"); //$NON-NLS-1$
        this.txfWarFileDeploymentLocation.setToolTipText(text);

        // WAR folder browse button
        this.warBrowseButton = WidgetFactory.createButton(pnlContents, InternalUiConstants.Widgets.BROWSE_BUTTON);
        this.warBrowseButton.setText(getString("changeButtonText")); //$NON-NLS-1$
        this.warBrowseButton.setToolTipText(text);
        this.warBrowseButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent theEvent ) {
                handleWarBrowseSourceSelected();
            }
        });

    }
    
    /**
     * @param parent
     * @since 7.4
     */
    private void createVersionInfoComposite( final Composite parent ) {

        // Create target group panel
        String text = getString("targetGrpPanelText"); //$NON-NLS-1$
        Group pnlContents = WidgetFactory.createGroup(parent, text, GridData.FILL_HORIZONTAL, 2, 2);
        // ------------------------------------
        // Target and jar components
        // ------------------------------------
        // targetVersionLabel
        TARGETVERSION = getString("targetVersionLabel"); //$NON-NLS-1$       
        WidgetFactory.createLabel(pnlContents, GridData.HORIZONTAL_ALIGN_BEGINNING, 1, TARGETVERSION);
        
        ILabelProvider targetVersion = new LabelProvider() {

            @Override
            public String getText( final Object source ) {
                return (String)source;
            }

            @Override
            public Image getImage( final Object source ) {
                return null;
            }
        };
        this.targetVersionCombo = WidgetFactory.createCombo(pnlContents,
                                                         SWT.READ_ONLY,
                                                         GridData.FILL_HORIZONTAL | GridData.HORIZONTAL_ALIGN_CENTER,
                                                         Collections.EMPTY_LIST,
                                                         _82_or_greater, 
                                                         targetVersion,
                                                         false);
        this.targetVersionCombo.setItems(TARGET_ARRAY);
        this.targetVersionCombo.setText(_82_or_greater);
        
        this.targetVersionCombo.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText( final ModifyEvent event ) {
            	if( targetVersionCombo.getSelectionIndex() > -1 ) {
                	handleVersionSelected();
            	}
            }
        });
        
        Group jarPnlContents = WidgetFactory.createGroup(pnlContents, getString("jarLocations"), GridData.FILL_HORIZONTAL, 4, 4); //$NON-NLS-1$
        

        // Teiid Engine Jar  Label
        this.ENGINEJARFILELOCATION = getString("engineJarLabel"); //$NON-NLS-1$       
        WidgetFactory.createLabel(jarPnlContents, GridData.HORIZONTAL_ALIGN_BEGINNING, 1, this.ENGINEJARFILELOCATION);

        // Engine Jar file location textfield
        this.engineJarFileLocation = WidgetFactory.createTextField(jarPnlContents, GridData.FILL_HORIZONTAL, 2);
        text = getString("engineJarTooltip"); //$NON-NLS-1$
        this.engineJarFileLocation.setToolTipText(text);
        
        // Select Engine Jar browse button
        this.selectEngineJar = WidgetFactory.createButton(jarPnlContents, InternalUiConstants.Widgets.BROWSE_BUTTON, GridData.END, 1);
        this.selectEngineJar.setText(getString("browseButtonText")); //$NON-NLS-1$
        this.selectEngineJar.setToolTipText(text);
        this.selectEngineJar.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent theEvent ) {
                handleEngineJarBrowseSourceSelected();
            }
        });

        // Teiid Common Core Label
        this.COMMONCOREJARFILELOCATION = getString("commonCoreJarLabel"); //$NON-NLS-1$       
        WidgetFactory.createLabel(jarPnlContents, GridData.HORIZONTAL_ALIGN_BEGINNING, 1, this.COMMONCOREJARFILELOCATION);

        // Common Core Jar file location textfield
        this.commonCoreJarFileLocation = WidgetFactory.createTextField(jarPnlContents, GridData.FILL_HORIZONTAL, 2);
        text = getString("commonCoreJarTooltip"); //$NON-NLS-1$
        this.commonCoreJarFileLocation.setToolTipText(text);
        
        // Select Common Core Jar browse button
        this.selectCommonCoreJar = WidgetFactory.createButton(jarPnlContents, InternalUiConstants.Widgets.BROWSE_BUTTON, GridData.HORIZONTAL_ALIGN_END, 1);
        this.selectCommonCoreJar.setText(getString("browseButtonText")); //$NON-NLS-1$
        this.selectCommonCoreJar.setToolTipText(text);
        this.selectCommonCoreJar.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent theEvent ) {
                handleCommonCoreJarBrowseSourceSelected();
            }
        });

    }

    /**
     * @param parent
     * @since 7.4
     */
    private void createRestoreDefaultAndIncludeJars( final Composite parent ) {

        // Create page
        Composite restoreDefault = WidgetFactory.createPanel(parent, SWT.NONE, GridData.FILL_HORIZONTAL, 1);

        GridLayout layout = new GridLayout();
        restoreDefault.setLayout(layout);
        layout.numColumns = 2;

        this.checkboxIncludeRestJars = WidgetFactory.createCheckBox(restoreDefault, getString("includeJars.text"), //$NON-NLS-1$
                                                                    GridData.FILL_HORIZONTAL,
                                                                    true);
        String text = getString("includeJars.tooltip"); //$NON-NLS-1$
        this.checkboxIncludeRestJars.setToolTipText(text);
        this.checkboxIncludeRestJars.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent theEvent ) {
                handleIncludeJarsSelected();
            }
        });

        // Restore default button
        text = getString("restoreDefaultButtonText"); //$NON-NLS-1$ 
        this.restoreDefaultButton = WidgetFactory.createButton(restoreDefault, text, GridData.END);
        text = getString("restoreDefaultTooltip"); //$NON-NLS-1$
        this.restoreDefaultButton.setToolTipText(text);
        this.restoreDefaultButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( final SelectionEvent event ) {
                restoreDefaultButtonPressed();
            }
        });

    }

    /**
     * @since 7.4
     */
    private void addListeners() {

        ModifyListener modifyListener = new ModifyListener() {
            @Override
            public void modifyText( ModifyEvent theEvent ) {
                validatePage();
                setWarFileNameInDialog();
            }
        };

        this.txfWarFileDeploymentLocation.addModifyListener(modifyListener);
        this.txfContext.addModifyListener(modifyListener);
        this.txfJNDIName.addModifyListener(modifyListener);
        this.targetVersionCombo.addModifyListener(modifyListener);
        this.engineJarFileLocation.addModifyListener(modifyListener);
        this.commonCoreJarFileLocation.addModifyListener(modifyListener);
    }

    protected void setWarFileNameInDialog() {
        dialog.setWarFileName(txfContext.getText());
    }

    void restoreDefaultButtonPressed() {
        this.txfWarFileDeploymentLocation.setText(WarDataserviceModel.getInstance().getWarFilenameDefault());
        this.txfContext.setText(WarDataserviceModel.getInstance().getContextNameDefault());
        this.txfJNDIName.setText(WarDataserviceModel.getInstance().getJndiNameDefault());
        this.targetVersionCombo.setText(_82_or_greater);
        this.engineJarFileLocation.setText(""); //$NON-NLS-1$
        this.commonCoreJarFileLocation.setText(""); //$NON-NLS-1$
        handleVersionSelected();
    }

    void handleWarBrowseSourceSelected() {
    	DirectoryDialog folderDialog = new DirectoryDialog(getShell());
        folderDialog.setText(getString("warTitle")); //$NON-NLS-1$
        folderDialog.setMessage(getString("warMessage")); //$NON-NLS-1$
        folderDialog.setFilterPath(txfWarFileDeploymentLocation.getText());
        String selectedUnit = folderDialog.open();

        if (selectedUnit != null) {
            this.txfWarFileDeploymentLocation.setText(selectedUnit);
        }
    }
    
    void handleEngineJarBrowseSourceSelected() {
    	FileDialog fileDialog = new FileDialog(getShell());
        fileDialog.setText(getString("warTitle")); //$NON-NLS-1$
        fileDialog.setFilterPath(engineJarFileLocation.getText());
        fileDialog.setFilterExtensions(new String[]{"jar"}); //$NON-NLS-1$
        String selectedUnit = fileDialog.open();

        if (selectedUnit != null) {
            this.engineJarFileLocation.setText(selectedUnit);
        }

    }

    void handleCommonCoreJarBrowseSourceSelected() {
        FileDialog fileDialog = new FileDialog(getShell());
        fileDialog.setText(getString("warTitle")); //$NON-NLS-1$
        fileDialog.setFilterPath(commonCoreJarFileLocation.getText());
        fileDialog.setFilterExtensions(new String[]{"jar"}); //$NON-NLS-1$
        String selectedUnit = fileDialog.open();

        if (selectedUnit != null) {
            this.commonCoreJarFileLocation.setText(selectedUnit);
        }

    }
    
    void handleIncludeJarsSelected() {

        RestWarDataserviceModel.getInstance().setIncludeJars(this.checkboxIncludeRestJars.getSelection());
    }
    
    void handleVersionSelected() {

    	boolean version82_OrGreater = false;
    	if (this.targetVersionCombo.getText().equals(getString("82Text"))){ //$NON-NLS-1$
            version82_OrGreater = true;
            this.engineJarFileLocation.setEnabled(true);
            this.selectEngineJar.setEnabled(true);
            this.commonCoreJarFileLocation.setEnabled(true);
            this.selectCommonCoreJar.setEnabled(true);
    	}else{
            this.engineJarFileLocation.setEnabled(false);
            this.selectEngineJar.setEnabled(false);
            this.commonCoreJarFileLocation.setEnabled(false);
            this.selectCommonCoreJar.setEnabled(false);
    	}
        RestWarDataserviceModel.getInstance().setVersion82_OrGreater(version82_OrGreater);
    }
}
