/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.jdbc.ui.wizards;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.IProfileListener;
import org.eclipse.datatools.connectivity.ProfileManager;
import org.eclipse.datatools.connectivity.db.generic.ui.wizard.NewJDBCFilteredCPWizard;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.teiid.core.designer.event.IChangeListener;
import org.teiid.core.designer.event.IChangeNotifier;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.designer.core.workspace.DotProjectUtils;
import org.teiid.designer.datatools.JdbcTranslatorHelper;
import org.teiid.designer.datatools.ui.actions.EditConnectionProfileAction;
import org.teiid.designer.jdbc.JdbcException;
import org.teiid.designer.jdbc.JdbcManager;
import org.teiid.designer.jdbc.JdbcSource;
import org.teiid.designer.jdbc.relational.JdbcImporter;
import org.teiid.designer.jdbc.relational.util.JdbcModelProcessorManager;
import org.teiid.designer.jdbc.ui.InternalModelerJdbcUiPluginConstants;
import org.teiid.designer.jdbc.ui.util.JdbcUiUtil;
import org.teiid.designer.ui.common.InternalUiConstants;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.common.util.WidgetUtil;
import org.teiid.designer.ui.common.util.WizardUtil;
import org.teiid.designer.ui.common.widget.DefaultScrolledComposite;
import org.teiid.designer.ui.common.wizard.AbstractWizardPage;


/**
 * @since 8.0
 */
public class JdbcSourceSelectionPage extends AbstractWizardPage
    implements IChangeNotifier, InternalModelerJdbcUiPluginConstants, InternalModelerJdbcUiPluginConstants.Widgets,
    InternalUiConstants.Widgets, CoreStringUtil.Constants {

    // ===========================================================================================================================
    // Constants

	// Connection Profile Filters
	private static final String CP_FILTER_SETTINGS = "org.eclipse.datatools.connectivity.sqm.filterSettings";  //$NON-NLS-1$
	private static final String SCHEMA_FILTER = "DatatoolsSchemaFilterPredicate";  //$NON-NLS-1$
	private static final String TABLE_FILTER = "DatatoolsTableFilterPredicate";  //$NON-NLS-1$
	private static final String STORED_PROC_FILTER = "DatatoolsSPFilterPredicate";  //$NON-NLS-1$
			
    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(JdbcSourceSelectionPage.class);

    private static final String TITLE = getString("title"); //$NON-NLS-1$
    
    private static final String TITLE_WITH_VDB_SOURCE = TITLE + " (VDB source model)"; //$NON-NLS-1$

    private static final String INITIAL_MESSAGE = getString("initialMessage"); //$NON-NLS-1$

    private static final int PROFILE_COLUMN_COUNT = 3;
    private static final int EDIT_PANEL_COLUMN_COUNT = 2;

    private static final String SOURCE_LABEL = getString("sourceLabel"); //$NON-NLS-1$
    private static final String NEW_BUTTON = Util.getString("Widgets.newLabel"); //$NON-NLS-1$
    private static final String EDIT_BUTTON = Util.getString("Widgets.editLabel"); //$NON-NLS-1$

    private static final String INVALID_PAGE_MESSAGE = getString("invalidPageMessage"); //$NON-NLS-1$
    private static final String TEIID_PROFILE_OPTIONS_GROUP_LABEL = getString("teiidProfileOptionsGroupLabel"); //$NON-NLS-1$
    private static final String IS_VDB_SOURCE_MODEL_CHECKBOX = getString("isVdbSourceModelCheckboxLabel"); //$NON-NLS-1$
    private static final String IS_VDB_SOURCE_MODEL_CHECKBOX_MESSAGE = getString("isVdbSourceModelCheckboxLabel.message"); //$NON-NLS-1$

    // ===========================================================================================================================
    // Static Methods

    /**
     * @since 4.0
     */
    private static String getString( final String id ) {
        return Util.getString(I18N_PREFIX + id);
    }

    // ===========================================================================================================================
    // Variables

    JdbcManager mgr;
    private JdbcSource src;
    private Connection connection;
    private ListenerList notifier;
    private String password;
    private String metadataProcessor;

    private ILabelProvider srcLabelProvider;
    private Combo srcCombo;
    private Button editCPButton;
    private Button isVdbSourceModelCheckBox;
    private Composite editPanel;
    private Composite teiidProfileGroup;
    private CLabel driverLabel, urlLabel, userNameLabel;
    private Text pwdText;
    private Map enableMap;
    private Composite processorPanel;
    private ILabelProvider processorLabelProvider;
    private Combo processorCombo;
    
    // Need to cash the profile when connection is selected so we can use it in Finish method to 
    // inject the connection info into model.
    private IConnectionProfile connectionProfile;
    
    private boolean isTeiidConnection;
    private boolean isVdbSourceModel = false;
    
    private String initialProfileName;

    private JdbcImporter importer;
    
    // ===========================================================================================================================
    // Constructors

    /**
     * @since 4.0
     */
    public JdbcSourceSelectionPage() {
        this(null);
    }

    /**
     * @since 4.0
     */
    public JdbcSourceSelectionPage( final JdbcSource source ) {
        super(JdbcSourceSelectionPage.class.getSimpleName(), TITLE);
        this.src = source;
        this.mgr = JdbcUiUtil.getJdbcManager();
        this.notifier = new ListenerList(ListenerList.IDENTITY);
        // Set page incomplete initially
        setPageComplete(false);
    }

    // ===========================================================================================================================
    // Methods

    /**
     * @since 4.0
     */
    @Override
	public void addChangeListener( final IChangeListener listener ) {
        this.notifier.add(listener);
    }

    /**
     * Creates a connection to the JDBC source if one has not already been established.
     * 
     * @return True if a connection has been successfully established (possibly in a prior call to this method).
     * @since 5.0
     */
    public boolean connect() {
        if (this.connection == null) {
            this.connection = JdbcUiUtil.connect(getSource(), getPassword());
            if (this.connection == null) {
                return false;
            }
            fireStateChanged();
        }
        return true;
    }

    /**
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     * @since 4.0
     */
    @SuppressWarnings("unused")
	@Override
	public void createControl( final Composite parent ) {
        final Composite hostPanel = new Composite(parent, SWT.NONE);
        hostPanel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        hostPanel.setLayout(new GridLayout(1, false));
        
        // Create page            
        DefaultScrolledComposite scrolledComposite = new DefaultScrolledComposite(hostPanel, SWT.H_SCROLL | SWT.V_SCROLL);
    	scrolledComposite.setExpandHorizontal(true);
    	scrolledComposite.setExpandVertical(true);
        GridLayoutFactory.fillDefaults().equalWidth(false).applyTo(scrolledComposite);
        GridDataFactory.fillDefaults().grab(true,  false);

        final Composite mainPanel = scrolledComposite.getPanel(); //new Composite(scrolledComposite, SWT.NONE);
        mainPanel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        mainPanel.setLayout(new GridLayout(1, false));

        // Add widgets to page
        
        // ---------------------------------------------------------------------------
        // ----------- Connection Profile SOURCE Panel ---------------------------------
        // ---------------------------------------------------------------------------
        Group profileGroup = WidgetFactory.createGroup(mainPanel, SOURCE_LABEL, SWT.NONE, 2, PROFILE_COLUMN_COUNT);
        profileGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        
        ArrayList sourceList = new ArrayList(this.mgr.getJdbcSources().size());
        for (Iterator iter = this.mgr.getJdbcSources().iterator(); iter.hasNext();) {
            Object source = iter.next();
            if (source != null && !sourceList.contains(source)) {
                sourceList.add(source);
            }
        }
        this.srcLabelProvider = new LabelProvider() {

            @Override
            public String getText( final Object source ) {
                return ((JdbcSource)source).getName();
            }
        };
        this.srcCombo = WidgetFactory.createCombo(profileGroup,
                                                  SWT.READ_ONLY,
                                                  GridData.FILL_HORIZONTAL,
                                                  sourceList,
                                                  this.src,
                                                  this.srcLabelProvider,
                                                  true);
        this.srcCombo.addModifyListener(new ModifyListener() {

            @Override
			public void modifyText( final ModifyEvent event ) {
                sourceModified();
            }
        });

        this.srcCombo.setVisibleItemCount(10);

        WidgetFactory.createButton(profileGroup, NEW_BUTTON).addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected( final SelectionEvent event ) {
                launchSourceWizard();
            }
        });
        
        editCPButton = WidgetFactory.createButton(profileGroup, EDIT_BUTTON);
        editCPButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected( final SelectionEvent event ) {
                editConnectionProfile();
            }
        });
        
        // ---------------------------------------------------------------------------
        // ----------- JDBC Metadata Processor Panel ---------------------------------
        // ---------------------------------------------------------------------------
        Group processorPanel = WidgetFactory.createGroup(mainPanel, getString("processorCombo"), SWT.NONE, 1); //$NON-NLS-1$
        processorPanel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        
        Collection<String> processors = JdbcModelProcessorManager.getMetadataProcessorNames();
        ArrayList processorList = new ArrayList(processors.size());
        for (Iterator iter = processors.iterator(); iter.hasNext();) {
            Object source = iter.next();
            if (source != null && !processorList.contains(source)) {
            	processorList.add(source);
            }
        }
        this.processorLabelProvider = new LabelProvider() {

            @Override
            public String getText( final Object source ) {
                return (String)source;
            }
        };
        this.processorCombo = WidgetFactory.createCombo(processorPanel,
                                                  SWT.READ_ONLY,
                                                  GridData.FILL_HORIZONTAL,
                                                  processorList,
                                                  this.metadataProcessor,
                                                  this.processorLabelProvider,
                                                  true);
        this.processorCombo.addModifyListener(new ModifyListener() {

            @Override
			public void modifyText( final ModifyEvent event ) {
                processorModified();
            }
        });
        
        this.processorCombo.setVisibleItemCount(10);
        this.processorCombo.setToolTipText(getString("processorComboTooltip")); //$NON-NLS-1$

        
        // ---------------------------------------------------------------------------
        // ----------- Connection Properties EDIT Panel ---------------------------------
        // ---------------------------------------------------------------------------
        this.editPanel = WidgetFactory.createGroup(mainPanel, getString("propertiesLabel"), //$NON-NLS-1$
                                                   GridData.HORIZONTAL_ALIGN_FILL, // | GridData.FILL_VERTICAL,
                                                   1,
                                                   EDIT_PANEL_COLUMN_COUNT);
        WidgetFactory.createLabel(this.editPanel, DRIVER_LABEL);
        this.driverLabel = WidgetFactory.createLabel(this.editPanel, GridData.FILL_HORIZONTAL);
        WidgetFactory.createLabel(this.editPanel, URL_LABEL);
        this.urlLabel = WidgetFactory.createLabel(this.editPanel, GridData.FILL_HORIZONTAL);
        WidgetFactory.createLabel(this.editPanel, USER_NAME_LABEL);
        this.userNameLabel = WidgetFactory.createLabel(this.editPanel, GridData.FILL_HORIZONTAL);
        WidgetFactory.createLabel(this.editPanel, PASSWORD_LABEL);
        this.pwdText = WidgetFactory.createTextField(this.editPanel, GridData.FILL_HORIZONTAL);
        this.pwdText.setEchoChar('*');
        this.pwdText.addModifyListener(new ModifyListener() {

            @Override
			public void modifyText( final ModifyEvent event ) {
                passwordModified();
            }
        });
        
        TEIID_PROFILE_GROUP: {
	        this.teiidProfileGroup = WidgetFactory.createGroup(mainPanel,  TEIID_PROFILE_OPTIONS_GROUP_LABEL,
	        		GridData.HORIZONTAL_ALIGN_FILL, 1, 1);
	        
	        this.isVdbSourceModelCheckBox = WidgetFactory.createCheckBox(teiidProfileGroup, IS_VDB_SOURCE_MODEL_CHECKBOX, 0, 1);
	        this.isVdbSourceModelCheckBox.setToolTipText(getString("isVdbSourceModelCheckboxTooltip")); //$NON-NLS-1$
	        this.isVdbSourceModelCheckBox.addSelectionListener(new SelectionAdapter() {
	
				@Override
	            public void widgetSelected(final SelectionEvent event) {
	            	isVdbSourceModelCheckBoxSelected();
	            }
	        });
	        Text descriptionText = new Text(teiidProfileGroup,  SWT.WRAP | SWT.READ_ONLY);
	        GridData gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
	        gd.heightHint = 60;
	        gd.widthHint = 500;
	        descriptionText.setLayoutData(gd);
	        descriptionText.setText(IS_VDB_SOURCE_MODEL_CHECKBOX_MESSAGE);
	        descriptionText.setBackground(teiidProfileGroup.getBackground());
	        descriptionText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));

        }
        
        sourceModified();
        
        if( this.initialProfileName != null ) {
        	selectConnectionProfile(this.initialProfileName);
        }
        
        if (validatePage()) {
            setMessage(INITIAL_MESSAGE);
        }


//        scrolledComposite.setContent(mainPanel);
//        scrolledComposite.setMinSize(mainPanel.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        scrolledComposite.sizeScrolledPanel();
        
        setControl(hostPanel);

        updateWidgetsState();
    }

    /**
     * @since 4.0
     */
    public Connection getConnection() {
        return this.connection;
    }
    
    /**
     * @since 4.0
     */
    public IConnectionProfile getConnectionProfile() {
        return this.connectionProfile;
    }

    /**
     * @see org.eclipse.jface.wizard.WizardPage#getNextPage()
     * @since 5.0
     */
    @Override
    public IWizardPage getNextPage() {
        if (!connect()) {
            return null;
        }
        return super.getNextPage();
    }

    /**
     * @since 4.0
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * @since 4.0
     */
    public JdbcSource getSource() {
        return this.src;
    }
    
    /**
     * @since 4.0
     */
    public String getMetadataProcessor() {
        return this.metadataProcessor;
    } 

    /**
     * @since 7.0
     */
    void launchSourceWizard() {
        NewJDBCFilteredCPWizard wiz = new NewJDBCFilteredCPWizard();
        WizardDialog wizardDialog = new WizardDialog(Display.getCurrent().getActiveShell(), wiz);
        wizardDialog.setBlockOnOpen(true);

        CPListener listener = new CPListener();
        ProfileManager.getInstance().addProfileListener(listener);
        
        if (wizardDialog.open() == Window.OK) {
            try {
                this.src = listener.getJdbcSource();
                JdbcSource theNewJdbcSource = this.src;
                this.mgr.reload(null);
                WidgetUtil.setComboItems(this.srcCombo, this.mgr.getJdbcSources(), this.srcLabelProvider, true);

                if (theNewJdbcSource != null) {
                    WidgetUtil.setComboText(this.srcCombo, theNewJdbcSource, this.srcLabelProvider);
                }

            } catch (JdbcException e) {
                e.printStackTrace();
            } finally {
            	sourceModified();
            	// Remove the listener if there is a problem
            	ProfileManager.getInstance().removeProfileListener(listener);
            }
        } else {
        	// Remove the listener if the dialog is canceled
        	ProfileManager.getInstance().removeProfileListener(listener);
        }
    }
    
    void editConnectionProfile() {
    	if( this.connectionProfile != null ) {
    		IConnectionProfile currentProfile = this.connectionProfile;
    		EditConnectionProfileAction action = new EditConnectionProfileAction(getShell(), currentProfile);
    		
    		CPListener listener = new CPListener();
            ProfileManager.getInstance().addProfileListener(listener);
            
    		action.run();
    		
    		// Update the Combo Box
    		if( action.wasFinished() )   {
	            try {
	            	this.src = listener.getJdbcSource();
	                this.mgr.reload(null);
	                WidgetUtil.setComboItems(this.srcCombo, this.mgr.getJdbcSources(), this.srcLabelProvider, true);
	
	                WidgetUtil.setComboText(this.srcCombo, src, this.srcLabelProvider);
	                
	                selectConnectionProfile(currentProfile.getName());
	
	            } catch (JdbcException e) {
	                e.printStackTrace();
	            } finally {
	            	// Remove the listener if there is a problem
	            	ProfileManager.getInstance().removeProfileListener(listener);
	            }
	    		
	    		sourceModified();
    		} else {
    			// Remove the listener if the dialog is canceled
    			ProfileManager.getInstance().removeProfileListener(listener);
    		}
    	}
    }

    /**
     * @since 4.0
     */
    void passwordModified() {
        this.password = this.pwdText.getText();
        this.connection = null;
        validatePage();
    }

    /**
     * @since 4.0
     */
    @Override
	public void removeChangeListener( final IChangeListener listener ) {
        this.notifier.remove(listener);
    }
    
    public void selectConnectionProfile(String name) {
    	if( name == null ) {
    		return;
    	}
    	
    	this.initialProfileName = name;
    	
    	int cpIndex = -1;
    	int i = 0;
    	if( srcCombo != null && !srcCombo.isDisposed() ) {
	    	for( String item : srcCombo.getItems()) { 
	    		if( item != null && item.length() > 0 ) {
	    			if( item.toUpperCase().equalsIgnoreCase(name.toUpperCase())) {
	    				cpIndex = i;
	    				break;
	    			}
	    		}
	    		i++;
	    	}
	    	if( cpIndex > -1 ) {
	    		srcCombo.select(cpIndex);
	    	}
    	}
    	
    	updateWidgetsState();
    }

    /**
     * @since 4.0
     */
    void sourceModified() {
        final String text = this.srcCombo.getText();
        if (text.length() > 0) {
            if (this.enableMap != null) {
                WidgetUtil.restore(this.enableMap);
                this.enableMap = null;
            }
            this.src = this.mgr.findSources(text)[0];
            this.driverLabel.setText(this.src.getDriverName());
            this.urlLabel.setText(this.src.getUrl());
            JdbcUiUtil.setText(this.userNameLabel, this.src.getUsername());
            if (null != this.src.getPassword()) {
                JdbcUiUtil.setText(this.pwdText, this.src.getPassword());
            }
            // cache the profile stored in the JdbcManager
            this.connectionProfile = this.mgr.getConnectionProfile(text);
        } else {
            this.src = null;
            this.driverLabel.setText(EMPTY_STRING);
            this.urlLabel.setText(EMPTY_STRING);
            this.userNameLabel.setText(EMPTY_STRING);
            if (this.enableMap == null) {
                this.enableMap = WidgetUtil.disable(this.editPanel);
            }
            this.connectionProfile = null;
        }
        this.connection = null;
        
        // Need to sync up with the metadata processor
        if( this.connectionProfile != null ) {
        	String translator = JdbcTranslatorHelper.getModelProcessorType(this.connectionProfile);
        	String processorType = JdbcModelProcessorManager.getProcessorNameWithType(translator);
        	String[] items = this.processorCombo.getItems();
        	int index = -1;
        	int matchIndex = -1;
        	for( String item : items ) {
        		index++;
        		if( item.equalsIgnoreCase(processorType) ) {
        			matchIndex = index;
        			break;
        		}
        	}
        	
        	if( matchIndex > -1 ) {
        		this.processorCombo.select(matchIndex);
        		processorModified();
        	} else {
        		matchIndex = -1;
        		index = -1;
            	for( String item : items ) {
            		index++;
            		if( item.equalsIgnoreCase(JdbcModelProcessorManager.JDBC_DEFAULT) ) {
            			matchIndex = index;
            			break;
            		}
            	}
            	this.processorCombo.select(matchIndex);
        		processorModified();
        	}
        	
        	// Update the connection profile filters
        	Properties props = this.connectionProfile.getProperties(CP_FILTER_SETTINGS);
        	if(props!=null) {
        		String schemaFilterStr = (String)props.get(SCHEMA_FILTER);
        		String tableFilterStr = (String)props.get(TABLE_FILTER);
        		String storedProcFilterStr = (String)props.get(STORED_PROC_FILTER);
        		if(!CoreStringUtil.isEmpty(schemaFilterStr)) {
        			this.importer.setSchemaFilter(schemaFilterStr);
        		}
        		if(!CoreStringUtil.isEmpty(tableFilterStr)) {
        			this.importer.setTableFilter(tableFilterStr);
        		}
        		if(!CoreStringUtil.isEmpty(storedProcFilterStr)) {
        			this.importer.setStoredProcFilter(storedProcFilterStr);
        		}
        	}

        }
        
        validatePage();
        
        this.editCPButton.setEnabled(this.connectionProfile != null);
        if( this.connectionProfile != null) {
        	// Check for Teiid connection
        	Properties props = connectionProfile.getBaseProperties();
        	String vendor = props.getProperty("org.eclipse.datatools.connectivity.db.vendor"); //$NON-NLS-1$
        	if( vendor != null && vendor.equalsIgnoreCase("TEIID") ) { //$NON-NLS-1$
        		this.isTeiidConnection = true;
        	} else {
        		this.isTeiidConnection = false;
        	}
        }
        
        updateWidgetsState();
    }

    void processorModified() {
    	final String text = this.processorCombo.getText();
        if (text.length() > 0) {
            if (this.enableMap != null) {
                WidgetUtil.restore(this.enableMap);
                this.enableMap = null;
            }
            // get the actual processor type and set it's value based on the combo box text
            this.metadataProcessor = JdbcModelProcessorManager.getProcessorTypeWithName(text);
        } else {
            if (this.enableMap == null) {
                this.enableMap = WidgetUtil.disable(this.processorPanel);
            }
        }
        validatePage();
    }
    

    void isVdbSourceModelCheckBoxSelected() {
    	this.isVdbSourceModel = this.isVdbSourceModelCheckBox.getSelection();
    	this.importer.setIsVdbSourceModel(this.isVdbSourceModel);
    }
    

    void updateWidgetsState() {
        if (getControl() == null)
            return;
        
    	this.teiidProfileGroup.setVisible(isTeiidConnection());
    	boolean doSetSelected = !this.isVdbSourceModelCheckBox.isVisible();
    	this.isVdbSourceModelCheckBox.setVisible(isTeiidConnection());
    	if( doSetSelected ) {
    		this.isVdbSourceModelCheckBox.setSelection(true);
    	}
    	if( !this.isTeiidConnection ) {
    		this.isVdbSourceModelCheckBox.setSelection(false);
    	}
    	isVdbSourceModelCheckBoxSelected();
    	
        if( this.importer.isVdbSourceModel() ) {
        	this.setTitle(TITLE_WITH_VDB_SOURCE);
        } else {
        	this.setTitle(TITLE);
        }
    }
    
    /**
     * @since 4.0
     */
    private boolean validatePage() {
        // Show isTeiidSourceModelCheckBox or not, select or not and notify checked or not
        this.isVdbSourceModelCheckBox.setVisible(isTeiidConnection);
        this.isVdbSourceModelCheckBox.setSelection(isTeiidConnection);
    	
        // Check for at least ONE open non-hidden Model Project
        Collection<IProject> openModelProjects = DotProjectUtils.getOpenModelProjects();

        if (openModelProjects.size() == 0) {
            WizardUtil.setPageComplete(this, getString("noOpenProjectsMessage"), ERROR); //$NON-NLS-1$
        } else if (this.srcCombo.getText().length() == 0) {
            WizardUtil.setPageComplete(this, INVALID_PAGE_MESSAGE, ERROR);
        } else if (null == this.password) {
            WizardUtil.setPageComplete(this, getString("noPasswordMessage"), ERROR); //$NON-NLS-1$
        } else {
            WizardUtil.setPageComplete(this);
        }
        fireStateChanged();

        return openModelProjects.size() > 0;
    }

    void fireStateChanged() {
        Object[] listeners = this.notifier.getListeners();

        for (Object listener : listeners) {
            ((IChangeListener)listener).stateChanged(this);
        }
    }
    
    /**
     * @return is a a teiid connection
     */
    public boolean isTeiidConnection() {
    	return this.isTeiidConnection;
    }
    
    /**
     * @return the vdb name if is teiid JDBC connection to vdb
     */
    public String getVdbName() {
        if( this.connectionProfile != null) {
        	// Check for Teiid connection
        	Properties props = connectionProfile.getBaseProperties();
        	String teiidURL = props.getProperty("org.eclipse.datatools.connectivity.db.URL"); //$NON-NLS-1$
        	// EXAMPLE:  jdbc:teiid:PartsTestVDB@mm://localhost:31000
        	if( teiidURL != null && teiidURL.startsWith("jdbc:teiid")) {  //$NON-NLS-1$
        		int atIndex = teiidURL.indexOf('@');
        		String vdbName = teiidURL.substring(11, atIndex);
        		if( vdbName != null ) {
        			return vdbName;
        		}
        	}
        }
		
		return null;
    }

	/**
	 * @param importer the importer to set
	 */
	public void setImporter(JdbcImporter importer) {
		this.importer = importer;
	}

    public class CPListener implements IProfileListener {

        IConnectionProfile latestProfile;

        @Override
        public void profileAdded( IConnectionProfile profile ) {
            latestProfile = profile;
            fireStateChanged();
        }

        @Override
        public void profileChanged( IConnectionProfile profile ) {
        	latestProfile = profile;
        }

        @Override
        public void profileDeleted( IConnectionProfile profile ) {
            // nothing
        }

        public JdbcSource getJdbcSource() {
        	if(latestProfile!=null) {
        		return mgr.getJdbcSource(latestProfile);
        	}
        	return null;
        }
    }
}
