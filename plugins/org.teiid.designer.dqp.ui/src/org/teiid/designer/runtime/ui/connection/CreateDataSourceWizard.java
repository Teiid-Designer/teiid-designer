/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime.ui.connection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.core.designer.util.StringConstants;
import org.teiid.core.designer.util.StringUtilities;
import org.teiid.designer.core.validation.rules.StringNameValidator;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.datatools.connection.ConnectionInfoProviderFactory;
import org.teiid.designer.datatools.connection.IConnectionInfoProvider;
import org.teiid.designer.datatools.ui.dialogs.ConnectionProfileWorker;
import org.teiid.designer.datatools.ui.dialogs.IProfileChangedListener;
import org.teiid.designer.runtime.spi.ITeiidServer;
import org.teiid.designer.runtime.ui.DqpUiConstants;
import org.teiid.designer.transformation.ui.UiConstants.Images;
import org.teiid.designer.transformation.ui.UiPlugin;
import org.teiid.designer.ui.common.graphics.GlobalUiColorManager;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.common.util.WidgetUtil;
import org.teiid.designer.ui.common.widget.ScrollableTitleAreaDialog;
import org.teiid.designer.ui.viewsupport.ModelIdentifier;


/**
 * 
 *
 * @since 8.0
 */
public class CreateDataSourceWizard extends ScrollableTitleAreaDialog implements IProfileChangedListener {
    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(CreateDataSourceWizard.class);

    private static final String TITLE = getString("title"); //$NON-NLS-1$

    private static final String DEFAULT_NAME = getString("defaultName"); //$NON-NLS-1$
    private static final String PASSWORD = getString("passwordStr"); //$NON-NLS-1$
    private static final String HIDDEN_PASSWORD = "********"; //$NON-NLS-1$
    private static final String NEW_BUTTON = DqpUiConstants.UTIL.getString("Button.newLabel"); //$NON-NLS-1$
    private static final String EDIT_BUTTON = DqpUiConstants.UTIL.getString("Button.editLabel"); //$NON-NLS-1$

    private static String getString( final String id ) {
        return DqpUiConstants.UTIL.getString(I18N_PREFIX + id);
    }

    private static String getString( final String id,
                                     final Object value ) {
        return DqpUiConstants.UTIL.getString(I18N_PREFIX + id, value);
    }

    private ModelResource selectedModelResource;
    // private TeiidDataSource teiidDataSource;
    String dataSourceName;

    Combo modelsCombo;
    Button useModelCheckBox;
    Combo connectionProfilesCombo;
    Button useConnectionProfileCheckBox;
    private Button newCPButton;
    private Button editCPButton;
    private ILabelProvider profileLabelProvider;
    Text dataSourceNameText;
    TableViewer propsViewer;
    private TableContentProvider propertiesContentProvider;

    Map<String, ModelResource> relationalModelsMap;

    private ITeiidServer teiidServer;
    // private JdbcManager jdbcManager;
    private ConnectionInfoProviderFactory providerFactory;
    // private IConnectionProfile selectedProfile;
    private StringNameValidator dataSourceNameValidator;

    boolean hasModelResources = false;

    private Properties teiidDataSourceProperties;
    private IConnectionInfoProvider currentProvider;

    ConnectionProfileWorker profileWorker;

    private boolean connRequiresPassword = false;

    /**
     * @since 4.0
     */
    public CreateDataSourceWizard( Shell shell,
    							   ITeiidServer teiidServer,
                                   Collection<ModelResource> relationalModels,
                                   ModelResource initialSelection ) {
        super(shell, 2);
        this.relationalModelsMap = new HashMap<String, ModelResource>();
        for (ModelResource mr : relationalModels) {
            this.relationalModelsMap.put(ModelUtil.getName(mr), mr);
        }
        this.hasModelResources = !relationalModelsMap.isEmpty();
        this.teiidServer = teiidServer;
        this.selectedModelResource = initialSelection;
        this.providerFactory = new ConnectionInfoProviderFactory();
        this.teiidDataSourceProperties = new Properties();
    }
    
    /* (non-Javadoc)
    * @see org.eclipse.jface.window.Window#setShellStyle(int)
    */
    @Override
    protected void setShellStyle( int newShellStyle ) {
        super.setShellStyle(newShellStyle | SWT.RESIZE | SWT.MAX);
    }

    
    @Override
    protected Control createDialogArea( final Composite parent ) {
        setTitle(TITLE);
        setTitleImage(UiPlugin.getDefault().getImage(Images.IMPORT_TEIID_METADATA));
        setMessage("Create and Deploy a Data Source");
        profileWorker = new ConnectionProfileWorker(this.getShell(), null, this);

        // ===========>>>> Create page composite
        
        final Composite mainPanel = (Composite)super.createDialogArea(parent);
//        mainPanel.setLayoutData(new GridData(GridData.FILL_BOTH));
        ((GridLayout)mainPanel.getLayout()).marginTop = 5;
        ((GridLayout)mainPanel.getLayout()).marginBottom = 10;
        ((GridLayout)mainPanel.getLayout()).verticalSpacing = 8;

        WidgetFactory.createLabel(mainPanel, getString("teiidServer.label")); //$NON-NLS-1$

        if (StringUtilities.isEmpty(teiidServer.getCustomLabel())) {
            WidgetFactory.createLabel(mainPanel, GridData.FILL_HORIZONTAL, 1, teiidServer.getUrl());
        } else {
            WidgetFactory.createLabel(mainPanel, GridData.FILL_HORIZONTAL, 1, teiidServer.getCustomLabel());
        }

        WidgetFactory.createLabel(mainPanel, getString("name.label")); //$NON-NLS-1$

        dataSourceName = getDefaultDataSourceName();
        if (selectedModelResource != null) {
            dataSourceName = ModelUtil.getName(selectedModelResource);
        }

        this.dataSourceNameText = WidgetFactory.createTextField(mainPanel, GridData.FILL_HORIZONTAL, 1, dataSourceName);
        this.dataSourceNameText.setForeground(GlobalUiColorManager.EMPHASIS_COLOR);
        this.dataSourceNameText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText( ModifyEvent e ) {
                dataSourceName = dataSourceNameText.getText();
                validateInputs();
            }
        });
        
        {
        	Text helpText = new Text(mainPanel, SWT.WRAP | SWT.READ_ONLY);
        	helpText.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
        	helpText.setForeground(GlobalUiColorManager.NOTE_COLOR);
        	helpText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        	((GridData)helpText.getLayoutData()).horizontalSpan = 2;
        	helpText.setText(getString("dataSourceNameHelp.txt"));  //$NON-NLS-1$
        }

        // ===========>>>> Create Connections Group
        final Group connectionSourceGroup = WidgetFactory.createGroup(mainPanel,
                                                                      getString("connectionSourceGroup.label"), GridData.FILL_HORIZONTAL, 2, 4); //$NON-NLS-1$

        // ===========>>>> Models selection
        boolean useModelRes = selectedModelResource != null;
        useModelCheckBox = WidgetFactory.createRadioButton(connectionSourceGroup,
                                                           getString("useModelInfo.label"), 0, 4, useModelRes); //$NON-NLS-1$
        useModelCheckBox.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected( final SelectionEvent event ) {
                if (hasModelResources) {
                	resetComboBoxes();
                    useConnectionProfileCheckBox.setSelection(!useModelCheckBox.getSelection());

                    handleModelResourceSelection();

                    setConnectionProperties();

                    propsViewer.refresh();

                    validateInputs();
                }
            }
        });

        WidgetFactory.createLabel(connectionSourceGroup, getString("model.label")); //$NON-NLS-1$
        ArrayList<ModelResource> modelsList = new ArrayList<ModelResource>(relationalModelsMap.values());

        ILabelProvider modelsLabelProvider = new LabelProvider() {

            @Override
            public String getText( final Object source ) {
                return ModelUtil.getName((ModelResource)source);
            }

            @Override
            public Image getImage( final Object source ) {
                return ModelIdentifier.getModelImage((ModelResource)source);
            }
        };
        if (useModelRes) {
            this.modelsCombo = WidgetFactory.createCombo(connectionSourceGroup,
                                                         SWT.READ_ONLY,
                                                         GridData.FILL_HORIZONTAL | GridData.HORIZONTAL_ALIGN_CENTER,
                                                         modelsList,
                                                         selectedModelResource,
                                                         modelsLabelProvider,
                                                         true);
        } else {
            this.modelsCombo = WidgetFactory.createCombo(connectionSourceGroup,
                                                         SWT.READ_ONLY,
                                                         GridData.FILL_HORIZONTAL,
                                                         modelsList,
                                                         modelsLabelProvider,
                                                         true);
        }
        this.modelsCombo.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText( final ModifyEvent event ) {
                if (useModelCheckBox.getSelection()) {
                    handleModelResourceSelection();
                }
            }
        });

        this.modelsCombo.setVisibleItemCount(10);
        GridData modelsComboGD = new GridData(GridData.FILL_BOTH);
        modelsComboGD.horizontalSpan = 3;
        this.modelsCombo.setLayoutData(modelsComboGD);

        // ===========>>>> Connection Profiles
        useConnectionProfileCheckBox = WidgetFactory.createRadioButton(connectionSourceGroup, getString("useProfileInfo.label"), //$NON-NLS-1$
                                                                       0,
                                                                       4,
                                                                       !useModelRes);
        useConnectionProfileCheckBox.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected( final SelectionEvent event ) {
                resetComboBoxes();
                useModelCheckBox.setSelection(!useConnectionProfileCheckBox.getSelection());

                if (useConnectionProfileCheckBox.getSelection()) {
                    handleConnectionProfileSelected();
                }

                propsViewer.refresh();

                validateInputs();
            }
        });

        WidgetFactory.createLabel(connectionSourceGroup, getString("connectionProfile.label")); //$NON-NLS-1$

        profileLabelProvider = new LabelProvider() {

            @Override
            public String getText( final Object source ) {
                return ((IConnectionProfile)source).getName();
            }
        };
        this.connectionProfilesCombo = WidgetFactory.createCombo(connectionSourceGroup,
                                                                 SWT.READ_ONLY,
                                                                 GridData.FILL_HORIZONTAL,
                                                                 profileWorker.getProfiles(),
                                                                 profileLabelProvider,
                                                                 true);
        this.connectionProfilesCombo.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected( SelectionEvent e ) {
                // Need to sync the worker with the current profile
                handleConnectionProfileSelected();
            }

            @Override
            public void widgetDefaultSelected( SelectionEvent e ) {
            }
        });

        this.connectionProfilesCombo.setVisibleItemCount(10);

        newCPButton = WidgetFactory.createButton(connectionSourceGroup, NEW_BUTTON);
        newCPButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected( final SelectionEvent event ) {
                profileWorker.create();
            }
        });

        editCPButton = WidgetFactory.createButton(connectionSourceGroup, EDIT_BUTTON);
        editCPButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected( final SelectionEvent event ) {
                profileWorker.edit();
            }
        });
        
        if( useModelRes ) {
        	this.useConnectionProfileCheckBox.setSelection(false);
	        this.connectionProfilesCombo.setEnabled(false);
	        this.modelsCombo.setEnabled(true);
        } else {
	        this.useConnectionProfileCheckBox.setSelection(true);
	        this.connectionProfilesCombo.setEnabled(true);
	        this.modelsCombo.setEnabled(false);
        }

        // ===========>>>>
        Group propsGroup = WidgetFactory.createGroup(mainPanel, getString("connectionProperties.label"), GridData.FILL_BOTH, 2, 2); //$NON-NLS-1$

        final GridData propertiesGridData = new GridData(GridData.FILL_BOTH);
        propertiesGridData.horizontalSpan = 2;
        propertiesGridData.heightHint = 180;
        propertiesGridData.minimumHeight = 180;
        propertiesGridData.grabExcessVerticalSpace = true;
        propsGroup.setLayoutData(propertiesGridData);

        int tableStyle = SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION;
        propsViewer = new TableViewer(propsGroup, tableStyle);
        Table table = propsViewer.getTable();
        table.setForeground(GlobalUiColorManager.EMPHASIS_COLOR);

        final GridData gridData = new GridData(GridData.FILL_BOTH); // SWT.FILL, SWT.FILL, true, true);
        gridData.grabExcessHorizontalSpace = true;
        table.setLayoutData(gridData);

        /*** Tree table specific code starts ***/

        table.setHeaderVisible(true);
        table.setLinesVisible(true);

        table.setLinesVisible(true);
        table.setHeaderVisible(true);
        TableColumn column1 = new TableColumn(table, SWT.NONE);
        column1.setText(getString("property.name.label")); //$NON-NLS-1$
        column1.setWidth(200);
        TableColumn column2 = new TableColumn(table, SWT.NONE);
        column2.setText(getString("property.value.label")); //$NON-NLS-1$
        column2.setWidth(50);
        table.pack();

        GridData columnData = new GridData(GridData.FILL_BOTH);
        table.setLayoutData(columnData);

        // table.setLayout(layout);

        propertiesContentProvider = new TableContentProvider();
        propsViewer.setContentProvider(new ArrayContentProvider());
        propsViewer.setLabelProvider(propertiesContentProvider);

        if (!hasModelResources) {
            useModelCheckBox.setEnabled(false);
            modelsCombo.setEnabled(false);
        }

        if (profileWorker.getProfiles().isEmpty()) {
            useConnectionProfileCheckBox.setEnabled(false);
            connectionProfilesCombo.setEnabled(false);
        } else {
            this.connectionProfilesCombo.select(0);
            handleConnectionProfileSelected();
        }

        if (profileWorker.getProfiles().isEmpty() && !hasModelResources) {
            setErrorMessage(getString("noConnectionDataError.message")); //$NON-NLS-1$
        } else {
            setConnectionProperties();
        }
        
        sizeScrolledPanel();
        
        resetComboBoxes();

        // ===========>>>> If we're in edit mode, load the UI objects with the info from the input dataRole

        return mainPanel;
    }
    
    private void resetComboBoxes() {
    	
        connectionProfilesCombo.setEnabled(useConnectionProfileCheckBox.getSelection());
        modelsCombo.setEnabled(!useConnectionProfileCheckBox.getSelection());
    	
    	this.modelsCombo.setForeground(getColorForEnablement(this.modelsCombo.getEnabled()));
    	this.connectionProfilesCombo.setForeground(getColorForEnablement(this.connectionProfilesCombo.getEnabled()));
    }
    
    private Color getColorForEnablement(boolean enabled) {
    	if( enabled ) {
    		return GlobalUiColorManager.EMPHASIS_COLOR;
    	} else {
    		return GlobalUiColorManager.EMPHASIS_COLOR_DISABLED;
    	}
    }

    void handleModelResourceSelection() {
        if (modelsCombo.getSelectionIndex() >= 0) {
            String selectedItem = modelsCombo.getItem(modelsCombo.getSelectionIndex());

            this.selectedModelResource = this.relationalModelsMap.get(selectedItem);
        } else {
            this.selectedModelResource = null;
        }
        setConnectionProperties();

    }

    void handleConnectionProfileSelected() {
        int selIndex = connectionProfilesCombo.getSelectionIndex();

        if (selIndex >= 0) {
            String name = connectionProfilesCombo.getItem(selIndex);
            if (name != null) {
                IConnectionProfile profile = profileWorker.getProfile(name);
                profileWorker.setSelection(profile);
                setConnectionProperties();
            }
        }
    }

    public TeiidDataSourceInfo getTeiidDataSourceInfo() {
        TeiidDataSourceInfo info = new TeiidDataSourceInfo(dataSourceName, dataSourceName, teiidDataSourceProperties,
                                                           currentProvider, connRequiresPassword);
        return info;
    }

    void validateInputs() {
        // Check that name != null
        if (this.dataSourceName == null || this.dataSourceName.length() == 0) {
            setErrorMessage(getString("nullNameError.message")); //$NON-NLS-1$
        } else if (useModelCheckBox != null && useModelCheckBox.getSelection() && teiidDataSourceProperties.isEmpty()) {
            setErrorMessage(getString("noValidTeiidPropertiesInModelError.message")); //$NON-NLS-1$
        } else if (useConnectionProfileCheckBox != null && useConnectionProfileCheckBox.getSelection()
                   && teiidDataSourceProperties.isEmpty()) {
            setErrorMessage(getString("noValidTeiidPropertiesError.message")); //$NON-NLS-1$
        } else if (!isValidName(this.dataSourceName)) {
            String msg = checkValidName(this.dataSourceName);
            setErrorMessage(msg);
        } else if (nameExists(this.dataSourceName)) {
            setErrorMessage(getString("dataSourceExists.message", this.dataSourceName)); //$NON-NLS-1$
        } else {
            setErrorMessage(null);
            setMessage(getString("finish.message")); //$NON-NLS-1$
        }

    }

    void setConnectionProperties() {
        Properties props = new Properties();
        currentProvider = null;

        if (useModelCheckBox.getSelection()) {
            if (selectedModelResource != null) {
                try {
                    IConnectionInfoProvider provider = getProvider(selectedModelResource);
                    if (provider != null) {
                        currentProvider = provider;
                        IConnectionProfile modelCP = provider.getConnectionProfile(selectedModelResource);
                        props = provider.getTeiidRelatedProperties(modelCP);
                        connRequiresPassword = provider.requiresPassword(provider.getConnectionProfile(selectedModelResource));
                    }
                } catch (ModelWorkspaceException e) {
                    DqpUiConstants.UTIL.log(e);
                } catch (Exception e) {
                    // DqpUiConstants.UTIL.log(e);
                }
            }
        } else if (profileWorker.getConnectionProfile() != null) {
            try {
                IConnectionInfoProvider provider = getProvider(profileWorker.getConnectionProfile());
                if (provider != null) {
                    currentProvider = provider;
                    props = provider.getTeiidRelatedProperties(profileWorker.getConnectionProfile());
                    connRequiresPassword = provider.requiresPassword(profileWorker.getConnectionProfile());
                }
            } catch (ModelWorkspaceException e) {
                DqpUiConstants.UTIL.log(e);
            } catch (Exception e) {
                DqpUiConstants.UTIL.log(e);
            }
        }

        if (props != null && !props.isEmpty()) {
            teiidDataSourceProperties = props;
            Collection<StringKeyValuePair> propsColl = new ArrayList<StringKeyValuePair>();
            for (Object key : props.keySet()) {
                String keyStr = (String)key;
                String value = props.getProperty((String)key);
                if (keyStr.equalsIgnoreCase(PASSWORD)) {
                    value = HIDDEN_PASSWORD;
                }
                propsColl.add(new StringKeyValuePair(keyStr, value));
            }
            propsViewer.setInput(propsColl);
        } else {
            teiidDataSourceProperties.clear();
            propsViewer.setInput(new ArrayList<StringKeyValuePair>());
        }

        validateInputs();
    }

    private IConnectionInfoProvider getProvider( Object obj ) throws Exception {
        IConnectionInfoProvider provider = null;
        if (obj instanceof ModelResource) {
            provider = providerFactory.getProvider((ModelResource)obj);
        } else if (obj instanceof IConnectionProfile) {
            provider = providerFactory.getProvider((IConnectionProfile)obj);
        }

        return provider;
    }

    private boolean nameExists( String name ) {
        try {
            if (teiidServer.dataSourceExists(name)) {
                return true;
            }
        } catch (Exception e) {
            DqpUiConstants.UTIL.log(e);
        }

        return false;
    }

    private String getDefaultDataSourceName() {
        int i = 1;
        String tempName = DEFAULT_NAME + i;
        if (nameExists(tempName)) {
            i++;
            tempName = DEFAULT_NAME + i;
        }

        return tempName;
    }

    private String checkValidName( String name ) {
        if (dataSourceNameValidator == null) {
            dataSourceNameValidator = new DataSourceNameValidator(StringNameValidator.DEFAULT_MINIMUM_LENGTH,
                                                                  StringNameValidator.DEFAULT_MAXIMUM_LENGTH);
        }
        return dataSourceNameValidator.checkValidName(name);
    }

    private boolean isValidName( String name ) {
        if (dataSourceNameValidator == null) {
            dataSourceNameValidator = new DataSourceNameValidator(StringNameValidator.DEFAULT_MINIMUM_LENGTH,
                                                                  StringNameValidator.DEFAULT_MAXIMUM_LENGTH);
        }
        return dataSourceNameValidator.isValidName(name);
    }

    @Override
    public void profileChanged( IConnectionProfile profile ) {
        resetCPComboItems();

        selectConnectionProfile(profile.getName());

        setConnectionProperties();
    }

    void resetCPComboItems() {
        if (connectionProfilesCombo != null) {
            ArrayList<IConnectionProfile> profileList = new ArrayList<IConnectionProfile>();
            for (IConnectionProfile prof : profileWorker.getProfiles()) {
                profileList.add(prof);
            }

            WidgetUtil.setComboItems(connectionProfilesCombo, profileList, profileLabelProvider, true);
        }
    }

    void selectConnectionProfile( String name ) {
        if (name == null) {
            return;
        }

        int cpIndex = -1;
        int i = 0;
        for (String item : connectionProfilesCombo.getItems()) {
            if (item != null && item.length() > 0) {
                if (item.toUpperCase().equalsIgnoreCase(name.toUpperCase())) {
                    cpIndex = i;
                    break;
                }
            }
            i++;
        }
        if (cpIndex > -1) {
            connectionProfilesCombo.select(cpIndex);
        }
    }

    class StringKeyValuePair {

        private String key;
        private String value;

        public StringKeyValuePair( String key,
                                   String value ) {
            this.key = key;
            this.value = value;
        }

        /**
         * @return key
         */
        public String getKey() {
            return key;
        }

        /**
         * @return value
         */
        public String getValue() {
            return value;
        }

    }

    class DataSourceNameValidator extends StringNameValidator {

        public DataSourceNameValidator( int minLength,
                                        int maxLength ) {
            super(minLength, maxLength, new char[] {UNDERSCORE_CHARACTER, '-', '.'});
        }

        @Override
        public String getValidNonLetterOrDigitMessageSuffix() {
            return DqpUiConstants.UTIL.getString("DataSourceNameValidator.or_other_valid_characters"); //$NON-NLS-1$
        }

    }

    class TableContentProvider implements ITableLabelProvider {

        @Override
        public Image getColumnImage( Object theElement,
                                     int theIndex ) {
            return null;
        }

        /**
         * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
         */
        @Override
        public String getColumnText( Object theElement,
                                     int theColumnIndex ) {
            if (theElement instanceof StringKeyValuePair) {
                StringKeyValuePair prop = (StringKeyValuePair)theElement;
                if (theColumnIndex == 0) {
                    return prop.getKey();
                }

                return prop.getValue();
            }

            return StringConstants.EMPTY_STRING;
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
         */
        @Override
        public void addListener( ILabelProviderListener listener ) {
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
         */
        @Override
        public boolean isLabelProperty( Object element,
                                        String property ) {
            return false;
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
         */
        @Override
        public void removeListener( ILabelProviderListener listener ) {
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
         */
        @Override
        public void dispose() {
        }

    }
}
