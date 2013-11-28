/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.ui.wizards.file;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.IProfileListener;
import org.eclipse.datatools.connectivity.ProfileManager;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.dialogs.FilteredList.FilterMatcher;
import org.eclipse.ui.internal.misc.StringMatcher;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.util.StringUtilities;
import org.teiid.designer.core.util.URLHelper;
import org.teiid.designer.core.workspace.DotProjectUtils;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.core.workspace.ModelWorkspaceItem;
import org.teiid.designer.core.workspace.ModelWorkspaceManager;
import org.teiid.designer.datatools.connection.ConnectionInfoHelper;
import org.teiid.designer.datatools.connection.IConnectionInfoHelper;
import org.teiid.designer.datatools.profiles.flatfile.IFlatFileProfileConstants;
import org.teiid.designer.datatools.profiles.xml.IXmlProfileConstants;
import org.teiid.designer.datatools.ui.actions.EditConnectionProfileAction;
import org.teiid.designer.datatools.ui.dialogs.NewTeiidFilteredCPWizard;
import org.teiid.designer.metamodels.relational.Procedure;
import org.teiid.designer.transformation.ui.UiConstants;
import org.teiid.designer.transformation.ui.UiPlugin;
import org.teiid.designer.transformation.ui.wizards.xmlfile.TeiidXmlFileInfo;
import org.teiid.designer.ui.common.InternalUiConstants;
import org.teiid.designer.ui.common.product.ProductCustomizerMgr;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.common.util.WidgetUtil;
import org.teiid.designer.ui.common.util.WizardUtil;
import org.teiid.designer.ui.common.viewsupport.FileSystemLabelProvider;
import org.teiid.designer.ui.common.wizard.AbstractWizardPage;
import org.teiid.designer.ui.explorer.ModelExplorerContentProvider;
import org.teiid.designer.ui.explorer.ModelExplorerLabelProvider;
import org.teiid.designer.ui.viewsupport.ModelIdentifier;
import org.teiid.designer.ui.viewsupport.ModelNameUtil;
import org.teiid.designer.ui.viewsupport.ModelObjectUtilities;
import org.teiid.designer.ui.viewsupport.ModelProjectSelectionStatusValidator;
import org.teiid.designer.ui.viewsupport.ModelResourceSelectionValidator;
import org.teiid.designer.ui.viewsupport.ModelUtilities;
import org.teiid.designer.ui.viewsupport.ModelWorkspaceViewerFilter;
import org.teiid.designer.ui.viewsupport.ModelingResourceFilter;


/**
 * @since 8.0
 */
public class TeiidMetadataImportSourcePage extends AbstractWizardPage implements
		UiConstants, InternalUiConstants.Widgets,
		CoreStringUtil.Constants {

	// ===========================================================================================================================
	// Constants

	private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(TeiidMetadataImportSourcePage.class);

	private static final String TITLE = getString("title"); //$NON-NLS-1$
	private static final String INITIAL_MESSAGE = getString("initialMessage"); //$NON-NLS-1$

	private static final String FLAT_FILE_SOURCE_LABEL = getString("sourceLabel"); //$NON-NLS-1$
	private static final String NEW_BUTTON = "New..."; //Util.getString("Widgets.newLabel"); //$NON-NLS-1$
	private static final String EDIT_BUTTON = "Edit..."; //Util.getString("Widgets.editLabel"); //$NON-NLS-1$

	private static final String INVALID_PAGE_MESSAGE = getString("invalidPageMessage"); //$NON-NLS-1$

	// PROPERTY VALUES FROM FLAT FILE CP:
	// 		INCLTYPELINE=NO
	//		INCLCOLUMNNAME=YES
	//		HOME=/home/blafond/TestDesignerFolder/FlatFileData/employee-data
	//		TRAILNULLCOLS=NO
	//		DELIMTYPE=COMMA
	//		CHARSET=UTF-8
	
	private static final String HOME = "HOME"; //$NON-NLS-1$
	private static final String URI = "URI"; //$NON-NLS-1$
	private static final String INCLTYPELINE = "INCLTYPELINE"; //$NON-NLS-1$
	private static final String INCLCOLUMNNAME = "INCLCOLUMNNAME"; //$NON-NLS-1$
	private static final String VALUE_YES = "YES"; //$NON-NLS-1$
	private static final String VALUE_COMMA = "COMMA"; //$NON-NLS-1$
	private static final String CHARSET = "CHARSET"; //$NON-NLS-1$
	
	private static final String EMPTY_STRING = ""; //$NON-NLS-1$
	private static final String DOT_XML = ".XML"; //$NON-NLS-1$
	private static final String FILTER_INIT = "*.*"; //$NON-NLS-1$
	private static final String DOT_TXT_LOWER = ".txt"; //$NON-NLS-1$
	
	private static final String GET_TEXT_FILES = "getTextFiles"; //$NON-NLS-1$

	private static final String ODA_FLAT_FILE_ID = "org.eclipse.datatools.connectivity.oda.flatfile"; //$NON-NLS-1$
	private static final String FLAT_FILE_URL_ID = IFlatFileProfileConstants.FILE_URL_CONNECTION_PROFILE_ID;
			
	//private static final String SCHEMA_LIST_PROPERTY_KEY = "SCHEMAFILELIST";  //$NON-NLS-1$
	private static final String FILE_LIST_PROPERTY_KEY = "FILELIST";  //$NON-NLS-1$ //home/blafond/TestDesignerFolder/example files/xml/employee_info.xml

	private static final String UNKNOWN_FOLDER = getString("unknownFolderText"); //$NON-NLS-1$
	
	private static String getString(final String id) {
		return Util.getString(I18N_PREFIX + id);
	}

	private ILabelProvider srcLabelProvider;
	private Combo srcCombo;
	private Text dataFileFolderText;
	private Text fileFilterText;
	
	private Button editCPButton;
	private TableViewer fileViewer;
	private DataFolderContentProvider fileContentProvider;
	private TableViewerColumn fileNameColumn;
	
    private Text sourceModelContainerText;
    private Text sourceModelFileText;
    private Text sourceHelpText;
    private IPath sourceModelFilePath;
    
    private Text selectedFileText;

	private ProfileManager profileManager = ProfileManager.getInstance();
	private Collection<IConnectionProfile> connectionProfiles;

	private TeiidMetadataImportInfo info;
	
	final private ConnectionProfileInfo profileInfo = new ConnectionProfileInfo();
	
    boolean creatingControl = false;
    
    boolean synchronizing = false;
    
    boolean processingChecks = false;
    
    IStatus fileParsingStatus;
    
    IConnectionInfoHelper connectionInfoHelper;
    
    static String[] TEXT_FILE_EXTENSIONS = {
    	"TXT", "CSV", "TSV"   //$NON-NLS-1$ //$NON-NLS-2$//$NON-NLS-3$
    };

	/**
	 * @param info the TeiidMetadataImportInfo
	 * @since 4.0
	 */
	public TeiidMetadataImportSourcePage(TeiidMetadataImportInfo info) {
		this(null, info);
	}

	/**
	 * @param selection the selection
	 * @param info the TeiidMetadataImportInfo
	 * @since 4.0
	 */
	public TeiidMetadataImportSourcePage(Object selection,	TeiidMetadataImportInfo info) {
		super(TeiidMetadataImportSourcePage.class.getSimpleName(), TITLE);
		// Set page incomplete initially
		this.info = info;
		setPageComplete(false);
		setImageDescriptor(UiPlugin.getDefault().getImageDescriptor(Images.IMPORT_TEIID_METADATA));
		this.connectionInfoHelper = new ConnectionInfoHelper();
	}

	@Override
	public void createControl(Composite parent) {
		// Create page
		final Composite mainPanel = new Composite(parent, SWT.NONE);

		mainPanel.setLayout(new GridLayout());
		mainPanel.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL));
		mainPanel.setSize(mainPanel.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		setControl(mainPanel);
		// Add widgets to page

		createProfileGroup(mainPanel);

		createFolderContentsListGroup(mainPanel);
		
		createSourceModelGroup(mainPanel);
		
		setMessage(INITIAL_MESSAGE);
	}
	
	private void createProfileGroup(Composite parent) {
		// ---------------------------------------------------------------------------
		// ----------- Connection Profile SOURCE Panel
		// ---------------------------------
		// ---------------------------------------------------------------------------
		Group profileGroup = WidgetFactory.createGroup(parent, FLAT_FILE_SOURCE_LABEL, SWT.NONE, 2, 3);
		profileGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		refreshConnectionProfiles();

		this.srcLabelProvider = new LabelProvider() {

			@Override
			public String getText(final Object source) {
				return ((IConnectionProfile) source).getName();
			}
		};
		this.srcCombo = WidgetFactory.createCombo(profileGroup, SWT.READ_ONLY,
				GridData.FILL_HORIZONTAL, (ArrayList) this.connectionProfiles,
				null, // this.src,
				this.srcLabelProvider, true);
		this.srcCombo.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				profileSelectionChanged();
				fileViewer.refresh();

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
			}
		});

		this.srcCombo.setVisibleItemCount(10);

		WidgetFactory.createButton(profileGroup, NEW_BUTTON).addSelectionListener(new SelectionAdapter() {

					@Override
					public void widgetSelected(final SelectionEvent event) {
						createNewConnectionProfile();
					}
				});

		editCPButton = WidgetFactory.createButton(profileGroup, EDIT_BUTTON);
		editCPButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent event) {
				editConnectionProfile();
			}
		});

		editCPButton.setEnabled(false);
	}

	/**
	 * Method to create List box control group for displaying current zip file
	 * project list.
	 * 
	 * 
	 * @param parent
	 * @since 4.2
	 */
	private void createFolderContentsListGroup(Composite parent) {
		String groupLabel = getString("folderContentsGroup"); //$NON-NLS-1$
		if (!info.isFlatFileLocalMode()) {
			groupLabel = getString("folderXmlContentsGroup"); //$NON-NLS-1$
		}

		Group folderContentsGroup = WidgetFactory.createGroup(parent, groupLabel, SWT.FILL, 3, 2);
		GridData gd_1 = new GridData(GridData.FILL_BOTH);
		gd_1.heightHint = 180;
		folderContentsGroup.setLayoutData(gd_1);

		Label locationLabel = new Label(folderContentsGroup, SWT.NONE);
		locationLabel.setText(getString("folderLocation")); //$NON-NLS-1$
		
		dataFileFolderText = new Text(folderContentsGroup, SWT.BORDER | SWT.SINGLE);
		dataFileFolderText.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
		dataFileFolderText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
		dataFileFolderText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		dataFileFolderText.setEditable(false);
		
		Label fileFilterLabel = new Label(folderContentsGroup, SWT.NONE);
		fileFilterLabel.setText(getString("fileFilterLabel")); //$NON-NLS-1$
		
		createFilterTextBox(folderContentsGroup);
		
		createFileTableViewer(folderContentsGroup);
		
		Label selectedFileLabel = new Label(folderContentsGroup, SWT.NONE);
		selectedFileLabel.setText(getString("selectedXmlFile")); //$NON-NLS-1$
		
        selectedFileText = new Text(folderContentsGroup, SWT.BORDER | SWT.SINGLE);
        selectedFileText.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
        selectedFileText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
		selectedFileText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		selectedFileText.setEditable(false);

	}
	
    /**
     * Creates Text Widget for Filter string entry
     * @param parent the parent composite.
     */
    protected void createFilterTextBox( Composite parent ) {
        fileFilterText = new Text(parent, SWT.BORDER);

        GridData data = new GridData();
        data.grabExcessVerticalSpace = false;
        data.grabExcessHorizontalSpace = true;
        data.horizontalAlignment = GridData.FILL;
        data.verticalAlignment = GridData.BEGINNING;
        fileFilterText.setLayoutData(data);
        fileFilterText.setFont(parent.getFont());

        // Initial filter text is empty
        fileFilterText.setText(FILTER_INIT);
        setImportInfoFileFilter(FILTER_INIT);

        Listener listener = new Listener() {
            @Override
            public void handleEvent( Event e ) {
				for( TeiidMetadataFileInfo fileInfo : info.getFileInfos()) {
					fileInfo.setDoProcess(false);
				}
				for( TableItem item : fileViewer.getTable().getItems()) {
						item.setChecked(false);
				}

            	String filterText = fileFilterText.getText();
            	// Set filter on import info
            	setImportInfoFileFilter(filterText);
            	// Update content provider, then refresh viewer
    			fileContentProvider.setFilterString(filterText);
    			fileViewer.refresh();
    			
    			synchronizeUI();
    			validatePage();
            }
        };
        fileFilterText.addListener(SWT.Modify, listener);
    }
    
	private void setImportInfoFileFilter(String filterText) {
		this.info.setFileFilterText(filterText);
	}
	
	private void createFileTableViewer(Composite parent) {

		Table table = new Table(parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.CHECK );
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setLayout(new TableLayout());
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		this.fileViewer = new TableViewer(table);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 160;
		gd.horizontalSpan = 2;
		this.fileViewer.getControl().setLayoutData(gd);
		fileContentProvider = new DataFolderContentProvider();
		fileContentProvider.setFilterString(FILTER_INIT);
		this.fileViewer.setContentProvider(fileContentProvider);
		this.fileViewer.setLabelProvider(new FileSystemLabelProvider());

		// Check events can occur separate from selection events.
		// In this case move the selected node.
		// Also trigger selection of node in model.
		this.fileViewer.getTable().addSelectionListener(
				new SelectionListener() {

					@Override
					public void widgetSelected(SelectionEvent e) {
						if( processingChecks ) {
							return;
						}
						processingChecks = true;
						if (e.detail == SWT.CHECK) {
							
							TableItem tableItem = (TableItem) e.item;
							boolean wasChecked = tableItem.getChecked();
							
							if (tableItem.getData() instanceof File) {
								fileViewer.getTable().setSelection(new TableItem[] { tableItem });
								if (info.isFlatFileLocalMode()) {
									if( wasChecked ) {
										for( TableItem item : fileViewer.getTable().getItems()) {
											if( item != tableItem ) {
												item.setChecked(false);
											}
										}
									}									
									info.setDoProcess((File) tableItem.getData(), wasChecked);
								} else {
									
									if( wasChecked ) {
										for( TableItem item : fileViewer.getTable().getItems()) {
											if( item != tableItem ) {
												item.setChecked(false);
											}
										}
									}
									
									
									info.setDoProcessXml((File) tableItem.getData(), wasChecked);
								}
							}
							if( wasChecked ) {
								TeiidMetadataFileInfo fileInfo = info.getCheckedFileInfo();
								if( fileInfo != null ) {
									if( profileInfo.columnsInFirstLine ) {
										fileInfo.setFirstDataRow(1);
									}
								}
							}
						}
						processingChecks = false;
						synchronizeUI();
						validatePage();
					}

					@Override
					public void widgetDefaultSelected(SelectionEvent e) {
					}
				});

		// create columns
		fileNameColumn = new TableViewerColumn(this.fileViewer, SWT.LEFT);
		fileNameColumn.getColumn().setText(getString("dataFileNameColumn")); //$NON-NLS-1$
		fileNameColumn.setLabelProvider(new DataFileContentColumnLabelProvider());
		fileNameColumn.getColumn().pack();
	}
	
	private void createSourceModelGroup(Composite parent) {
    	Group sourceGroup = WidgetFactory.createGroup(parent, getString("sourceModelDefinitionGroup"), SWT.NONE, 1, 3); //$NON-NLS-1$
    	sourceGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Label locationLabel = new Label(sourceGroup, SWT.NULL);
        locationLabel.setText(getString("location")); //$NON-NLS-1$

        sourceModelContainerText = new Text(sourceGroup, SWT.BORDER | SWT.SINGLE);

        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        sourceModelContainerText.setLayoutData(gridData);
        sourceModelContainerText.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
        sourceModelContainerText.setEditable(false);

        Button browseButton = new Button(sourceGroup, SWT.PUSH);
        gridData = new GridData();
        browseButton.setLayoutData(gridData);
        browseButton.setText(getString("browse")); //$NON-NLS-1$
        browseButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent e ) {
                handleSourceModelLocationBrowse();
            }
        });

        Label fileLabel = new Label(sourceGroup, SWT.NULL);
        fileLabel.setText(getString("name")); //$NON-NLS-1$

        sourceModelFileText = new Text(sourceGroup, SWT.BORDER | SWT.SINGLE);
        gridData = new GridData(GridData.FILL_HORIZONTAL);
        sourceModelFileText.setLayoutData(gridData);
        sourceModelFileText.addModifyListener(new ModifyListener() {
            @Override
			public void modifyText( ModifyEvent e ) {
            	handleSourceModelTextChanged();
            }
        });
        
        browseButton = new Button(sourceGroup, SWT.PUSH);
        gridData = new GridData();
        browseButton.setLayoutData(gridData);
        browseButton.setText(getString("browse")); //$NON-NLS-1$
        browseButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent e ) {
                handleSourceModelBrowse();
            }
        });
        
    	new Label(sourceGroup, SWT.NONE);
    	
        Group helpGroup = WidgetFactory.createGroup(sourceGroup, getString("modelStatus"), SWT.NONE | SWT.BORDER_DASH,2); //$NON-NLS-1$
        helpGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    	
        {        	
        	sourceHelpText = new Text(helpGroup, SWT.WRAP | SWT.READ_ONLY);
        	sourceHelpText.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
        	sourceHelpText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
        	GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        	gd.heightHint = 35;
        	gd.horizontalSpan=3;
        	sourceHelpText.setLayoutData(gd);
        }
        
	}

	void profileSelectionChanged() {
		if (this.srcCombo.getSelectionIndex() > -1) {
			String cpName = this.srcCombo.getItem(this.srcCombo.getSelectionIndex());
			for (IConnectionProfile profile : this.connectionProfiles) {
				if (profile.getName().equalsIgnoreCase(cpName)) {
					setConnectionProfile(profile);
					Properties props = profile.getBaseProperties();
					setDataFolderLocation(props);
					
					String charset = (String) props.get(CHARSET);
					if (charset != null) {
					    this.profileInfo.charset = charset;
					}					
					clearFileListViewer();
					loadFileListViewer();
					
					if( this.info.getFileInfos().isEmpty() ) {
						// Check for FIRST LINE FOR COLUMNS
						String firstLineHasColumns = (String) props.get(INCLCOLUMNNAME);
						if( firstLineHasColumns != null ) {
							this.profileInfo.columnsInFirstLine = firstLineHasColumns.equalsIgnoreCase(VALUE_YES);
						}
						String secondLineDatatypes = (String) props.get(INCLTYPELINE);
						if( secondLineDatatypes != null ) {
							this.profileInfo.includeTypeLine = secondLineDatatypes.equalsIgnoreCase(VALUE_YES);
						}
						String delimiterType = (String) props.get(INCLTYPELINE);
						if( delimiterType != null ) {
							this.profileInfo.delimiterType = delimiterType;
						}
					}
					break;
				}
			}
		} else {
			setConnectionProfile(null);
		}

		synchronizeUI();
		
		validatePage();

		this.editCPButton.setEnabled(getConnectionProfile() != null);
	}
	
	private void setDataFolderLocation(Properties profileBaseProps) {
		if(profileBaseProps!=null) {
			String home = (String) profileBaseProps.get(HOME);
			if (home != null) {
				this.profileInfo.home = home;
				String location = home;
				if (location.length() > 60) {
					int len = location.length();
					location = "..." + location.substring(len - 60, len); //$NON-NLS-1$
				}
				this.dataFileFolderText.setText(location);
				this.dataFileFolderText.setToolTipText(home);
			} else {
				String uri = (String) profileBaseProps.get(URI);
				if( uri != null ) {
					String location = null;
					File aFile = new File(uri);
					if(aFile.exists() && aFile.isFile()) {
						File parentDir = aFile.getParentFile();
						if(parentDir!=null && parentDir.exists() && parentDir.isDirectory()) {
							location = parentDir.getAbsolutePath();
							this.profileInfo.home = location;
						}
					}
					if(location==null) {
						this.dataFileFolderText.setText(UNKNOWN_FOLDER);
						this.dataFileFolderText.setToolTipText(getString("unknownFolderTooltip")); //$NON-NLS-1$
					} else {
						this.dataFileFolderText.setText(location);
						this.dataFileFolderText.setToolTipText(location);
					}
				} else {
					String url = (String) profileBaseProps.get(IFlatFileProfileConstants.URL_PROP_ID);
					if( url != null ) {
						String location = url;
						if (location.length() > 60) {
							int len = location.length();
							location = "..." + location.substring(len - 60, len); //$NON-NLS-1$
						}
						this.dataFileFolderText.setText(location);
						this.dataFileFolderText.setToolTipText(url);
					} else {
						this.dataFileFolderText.setText(EMPTY_STRING);
						this.dataFileFolderText.setToolTipText(EMPTY_STRING);
					}
				}
			}
		} else {
			this.dataFileFolderText.setText(EMPTY_STRING);
			this.dataFileFolderText.setToolTipText(EMPTY_STRING);
		}
	}

	private void setConnectionProfile(IConnectionProfile profile) {
		if(profile==null) {
			this.fileViewer.setInput(null);
			clearFileListViewer();
		}
		this.info.setConnectionProfile(profile);
	}

	private IConnectionProfile getConnectionProfile() {
		return this.info.getConnectionProfile();
	}

	private void clearFileListViewer() {
		this.info.clearXmlFileInfos();
		this.info.clearFileInfos();
		fileViewer.remove(fileViewer.getTable().getItems());
	}

	private void loadFileListViewer() {
		if (getConnectionProfile() != null) {
			if( this.info.isFlatFileLocalMode() ) {
				File folder = getFolderForConnectionProfile();
				File file = getFileForConnectionProfile();
				if (hasExistingFileOrFolder(file,folder)) {
					fileParsingStatus = Status.OK_STATUS;
					if(file!=null && file.exists() && file.isFile()) {
						TeiidMetadataFileInfo fileInfo = this.info.getFileInfo(file);
						if (fileInfo == null) {
							fileInfo = new TeiidMetadataFileInfo(file);
							this.info.addFileInfo(fileInfo);
						}
						fileInfo.setIsUrl(false);
						fileViewer.setInput(file);
						fileViewer.getTable().select(0);
						fileViewer.getTable().getItem(0).setChecked(true);
						info.setDoProcess(fileInfo.getDataFile(), true);
					} else {
						fileViewer.setInput(folder);
					}
					TableItem[] items = fileViewer.getTable().getItems();
					for (TableItem item : items) {
						Object data = item.getData();
						if (data != null && data instanceof File) {
							File theFile = (File) data;
							if (!theFile.isDirectory()) {
								TeiidMetadataFileInfo fileInfo = this.info.getFileInfo(theFile);
								if (fileInfo == null || !fileInfo.getCharset().equals(this.profileInfo.charset)) {
								    this.info.addFileInfo(new TeiidMetadataFileInfo(theFile, this.profileInfo.charset));
								}
								//this.info.validate();
							}
						}
					}
					for (TableColumn column : this.fileViewer.getTable()
							.getColumns()) {
						column.pack();
						column.setWidth(column.getWidth() + 4);
					}
				// Unrecognized selection in CP
				} else {
					this.fileViewer.setInput(null);
					this.dataFileFolderText.setText(UNKNOWN_FOLDER);
					this.dataFileFolderText.setToolTipText(getString("unknownFolderTooltip")); //$NON-NLS-1$
				}
			// -----------------------------
			// FlatFile Remote Mode
			// -----------------------------
			} else {
				String urlString = getUrlStringForConnectionProfile();
				
				if( urlString != null && urlString.trim().length() > 0 ) {
					File theFile = null;
					// Clears the viewer
					// This will be the case if No XML is defined and URL version exists OR if nothing is defined in CP
					fileViewer.setInput("no input"); //$NON-NLS-1$
					// 
			        URL newUrl = null;
			        try {
			        	newUrl = URLHelper.buildURL(urlString);
			        } catch (MalformedURLException e) {
			            Util.log(e);
			            MessageDialog.openError(this.getShell(), 
			            		getString("malformedUrlErrorTitle"),  //$NON-NLS-1$
			            		UiConstants.Util.getString("malformedUrlErrorMessage", urlString, e.getMessage())); //$NON-NLS-1$
			        }
			        
			        if( newUrl != null ) {
				        boolean resolved = true;
				        try {
				            resolved = URLHelper.resolveUrl(newUrl);
				        } catch (Exception e) {
				            resolved = false;
				            
				        }
				        
				        if( resolved ) {
				        	try {
				                String filePath = formatPath(newUrl);
				                theFile = URLHelper.createFileFromUrl(newUrl, CoreStringUtil.createFileName(filePath), DOT_TXT_LOWER);
				            } catch (MalformedURLException theException) {
				            	Util.log(theException);
				            } catch (IOException theException) {
				            	Util.log(theException);
				            }
				        }
			        }
			        
			        if( theFile != null && theFile.exists() ) {
						fileParsingStatus = Status.OK_STATUS;
						TeiidMetadataFileInfo fileInfo = this.info.getFileInfo(theFile);
						if (fileInfo == null) {
							fileInfo = new TeiidMetadataFileInfo(theFile);
							this.info.addFileInfo(fileInfo);
						}
						fileInfo.setIsUrl(true);
						fileInfo.setFileUrl(urlString);
						fileInfo.setCharSet(this.profileInfo.charset);
						fileViewer.setInput(theFile);
						fileViewer.getTable().select(0);
						fileViewer.getTable().getItem(0).setChecked(true);
						info.setDoProcess(fileInfo.getDataFile(), true);
			        }
				} else {
					fileViewer.setInput(null);
					MessageDialog.openError(this.getShell(), 
							getString("invalidXmlConnectionProfileTitle"),  //$NON-NLS-1$
							getString("invalidXmlConnectionProfileMessage")); //$NON-NLS-1$
				}
			}
		}
	}
		
	private boolean hasExistingFileOrFolder(File theFile, File theFolder) {
		if (theFolder != null && theFolder.exists() && theFolder.isDirectory()) {
			return true;
		}
		if (theFile != null && theFile.exists() && theFile.isFile()) {
			return true;
		}
		return false;
	}
	
    /**
     * If the path begins with a "/", we need to strip off since this will be changed to an underscore and create an invalid model
     * name. Also, we need to remove any periods.
     * 
     * @param newUrl the file url
     * @return filePath - reformatted string used for generating the new file name
     */
    public static String formatPath( URL newUrl ) {
        String filePath = newUrl.getPath();
        /*
         * If the path begins with a "/", we need to strip off since this will
         * be changed to an underscore and create an invalid model name.
         */
        while (filePath.startsWith("/")) { //$NON-NLS-1$
            filePath = filePath.substring(1);
        }
        int dotLocation = filePath.indexOf("."); //$NON-NLS-1$
        if (dotLocation > -1) {
            filePath = filePath.substring(0, dotLocation);
        }
        return filePath;
    }

	private File getFolderForConnectionProfile() {
		if (getConnectionProfile() != null) {
			Properties props = getConnectionProfile().getBaseProperties();
			// For folder definition, the HOME property will be set
			String home = (String) props.get(HOME);
			if (home != null) {
				return new File(home);
			}
			
			// Home property not set - look for individual URI
			String fileURI = (String) props.get(URI);
			if(fileURI != null) {
				File aFile = new File(fileURI);
				if(aFile.exists() && aFile.isFile()) {
					return aFile.getParentFile();
				}
			}
		}

		return null;
	}
	
	private File getFileForConnectionProfile() {
		if (getConnectionProfile() != null) {
			Properties props = getConnectionProfile().getBaseProperties();
			String fileListValue = (String) props.get(FILE_LIST_PROPERTY_KEY);
			if (fileListValue != null) {
				return new File(fileListValue);
			}
			String fileUriValue = (String) props.get(URI);
			if(fileUriValue != null) {
				File uriFile = new File(fileUriValue);
				if(uriFile.exists() && uriFile.isFile()) {
					return uriFile;
				}
			}
		}

		return null;
	}
	
	private String getUrlStringForConnectionProfile() {
		if (getConnectionProfile() != null) {
			Properties props = getConnectionProfile().getBaseProperties();
			String fileListValue = (String) props.get(FILE_LIST_PROPERTY_KEY);
			if (fileListValue != null) {
				return fileListValue;
			}
			String url = (String) props.get(IFlatFileProfileConstants.URL_PROP_ID);
			if(url != null) {
				return url;
			}
		}

		return null;
	}

	void createNewConnectionProfile() {
		INewWizard wiz = null;
		
		if( this.info.isFlatFileLocalMode() ) {
            wiz = new NewTeiidFilteredCPWizard(ODA_FLAT_FILE_ID);
		} else {
            wiz = new NewTeiidFilteredCPWizard(FLAT_FILE_URL_ID);
		}

        WizardDialog wizardDialog = new WizardDialog(Display.getCurrent().getActiveShell(), wiz);
		wizardDialog.setBlockOnOpen(true);

		CPListener listener = new CPListener();
		ProfileManager.getInstance().addProfileListener(listener);
		if (wizardDialog.open() == Window.OK) {

			refreshConnectionProfiles();

			resetCPComboItems();
			setConnectionProfile(listener.getChangedProfile());

			selectProfile(listener.getChangedProfile());

		}
		ProfileManager.getInstance().removeProfileListener(listener);
	}

	void selectProfile(IConnectionProfile profile) {
		int index = 0;
		for (String item : this.srcCombo.getItems()) {
			if (item != null && item.equalsIgnoreCase(profile.getName())) {
				this.srcCombo.select(index);
				profileSelectionChanged();
				break;
			}
			index++;
		}
	}

	void resetCPComboItems() {
		if (this.srcCombo != null) {
			WidgetUtil.setComboItems(this.srcCombo,
					this.connectionProfiles, this.srcLabelProvider,
					true);
		}
	}

	void editConnectionProfile() {
		if (getConnectionProfile() != null) {
			IConnectionProfile currentProfile = getConnectionProfile();
			EditConnectionProfileAction action = new EditConnectionProfileAction(getShell(), currentProfile);

			CPListener listener = new CPListener();
			ProfileManager.getInstance().addProfileListener(listener);

			action.run();

			// Update the Combo Box
			if (action.wasFinished()) {
				setConnectionProfile(listener.getChangedProfile());
				this.refreshConnectionProfiles();
				WidgetUtil.setComboItems(this.srcCombo,
						this.connectionProfiles,
						this.srcLabelProvider, true);

				WidgetUtil.setComboText(this.srcCombo, getConnectionProfile(),this.srcLabelProvider);

				selectConnectionProfile(currentProfile.getName());

				ProfileManager.getInstance().removeProfileListener(listener);

				profileSelectionChanged();
			} else {
				// Remove the listener if the dialog is canceled
				ProfileManager.getInstance().removeProfileListener(listener);
			}
		}
	}
	
    /**
     * Uses the standard container selection dialog to choose the new value for the container field.
     */
    void handleSourceModelLocationBrowse() {
        final IContainer folder = WidgetUtil.showFolderSelectionDialog(ModelerCore.getWorkspace().getRoot(),
                                                                       new ModelingResourceFilter(),
                                                                       new ModelProjectSelectionStatusValidator());

        if (folder != null && sourceModelContainerText != null) {
            this.info.setSourceModelLocation(folder.getFullPath().makeRelative());
            this.sourceModelFilePath = this.info.getSourceModelLocation();
            this.sourceModelContainerText.setText(this.info.getSourceModelLocation().makeRelative().toString());
        } else {
        	this.info.setSourceModelLocation(new Path(StringUtilities.EMPTY_STRING));
            this.sourceModelContainerText.setText(StringUtilities.EMPTY_STRING);
        }
        
    	if( this.sourceModelFileText.getText() != null && this.sourceModelFileText.getText().length() > -1 ) {
    		this.info.setSourceModelExists(sourceModelExists());
    	}

        validatePage();
    }
	
    void handleSourceModelBrowse() {
        final Object[] selections = WidgetUtil.
        		showWorkspaceObjectSelectionDialog(getString("selectSourceModelTitle"), //$NON-NLS-1$
                     getString("selectSourceModelMessage"), //$NON-NLS-1$
                     false,
                     null,
                     sourceModelFilter,
                     new ModelResourceSelectionValidator(false),
                     new ModelExplorerLabelProvider(),
                     new ModelExplorerContentProvider() ); 

        if (selections != null && selections.length == 1 && sourceModelFileText != null) {
        	if( selections[0] instanceof IFile) {
        		IFile modelFile = (IFile)selections[0];
        		IPath folderPath = modelFile.getFullPath().removeLastSegments(1);
        		String modelName = modelFile.getFullPath().lastSegment();
        		info.setSourceModelExists(true);
        		info.setSourceModelLocation(folderPath);
        		info.setSourceModelName(modelName);
        	}
        }
        
        if( this.info.getSourceModelName() != null ) {
        	this.sourceModelFilePath = this.info.getSourceModelLocation();
        	this.sourceModelContainerText.setText(this.info.getSourceModelLocation().makeRelative().toString());
        	this.sourceModelFileText.setText(this.info.getSourceModelName());
        } else {
        	this.sourceModelFileText.setText(StringUtilities.EMPTY_STRING);
        }
        
        this.info.setSourceModelExists(sourceModelExists());
        
        
        validatePage();
    }
    
    void handleSourceModelTextChanged() {
    	if( synchronizing ) return;
    	
    	String newName = ""; //$NON-NLS-1$
    	if( this.sourceModelFileText.getText() != null && this.sourceModelFileText.getText().length() > -1 ) {
    		newName = this.sourceModelFileText.getText();
    		this.info.setSourceModelName(newName);
    		this.sourceModelFilePath = this.info.getSourceModelLocation();
    		this.info.setSourceModelExists(sourceModelExists());
    		
    	}

    	validatePage();
    }
    
    void synchronizeUI(){
    	synchronizing = true;
    	
    	if(this.info.isFlatFileUrlMode() || this.info.isFlatFileLocalMode()) {
    		setFlatFileModelNames();
        } else {
        	String fileName = EMPTY_STRING;
        	for(TeiidXmlFileInfo xmlFileInfo : this.info.getXmlFileInfos() ) {
        		if( xmlFileInfo.doProcess() ) {
        			fileName = xmlFileInfo.getDataFile().getName();
        			break;
        		}
        	}
        	this.selectedFileText.setText(fileName);
        }
    	
        if( this.info.getSourceModelLocation() != null ) {
        	this.sourceModelContainerText.setText(this.info.getSourceModelLocation().makeRelative().toString());
        } else {
        	this.sourceModelContainerText.setText(StringUtilities.EMPTY_STRING);
        }
        
        if( this.info.getSourceModelName() != null ) {
        	this.sourceModelFilePath = this.info.getSourceModelLocation();
        	this.sourceModelFileText.setText(this.info.getSourceModelName());
        } else {
        	this.sourceModelFileText.setText(StringUtilities.EMPTY_STRING);
        }
                
        synchronizing = false;
    }
    
    private void setFlatFileModelNames() {
    	String fileName = EMPTY_STRING;
    	String fileName_wo_extension = null;
    	for(TeiidMetadataFileInfo tmFileInfo : this.info.getFileInfos() ) {
    		if( tmFileInfo.doProcess() ) {
    			fileName = tmFileInfo.getDataFile().getName();
    			IPath path = new Path(fileName);
    			fileName_wo_extension = path.removeFileExtension().toString();
    			
    			break;
    		}
    	}
    	this.selectedFileText.setText(fileName);
    	if( fileName_wo_extension != null && (this.info.getSourceModelName() == null || this.info.getSourceModelName().length() == 0) ) {
    		this.info.setSourceModelName(fileName_wo_extension + "_source"); //$NON-NLS-1$
    	}
    	if( fileName_wo_extension != null && (this.info.getViewModelName() == null || this.info.getViewModelName().length() == 0) ) {
    		this.info.setViewModelName(fileName_wo_extension + "_view"); //$NON-NLS-1$
    	}
    }

    private boolean sourceModelExists() {
    	if( this.sourceModelFilePath == null ) {
    		return false;
    	}
		
		IPath modelPath = new Path(sourceModelFilePath.toOSString()).append(this.sourceModelFileText.getText());
		if( !modelPath.toString().toUpperCase().endsWith(".XMI")) { //$NON-NLS-1$
			modelPath = modelPath.addFileExtension("xmi"); //$NON-NLS-1$
		}
		
		ModelWorkspaceItem item = ModelWorkspaceManager.getModelWorkspaceManager().findModelWorkspaceItem(modelPath, IResource.FILE);
		if( item != null ) {
			return true;
		}
    		
    	return false;
    }

	public void selectConnectionProfile(String name) {
		if (name == null) {
			return;
		}

		int cpIndex = -1;
		int i = 0;
		for (String item : srcCombo.getItems()) {
			if (item != null && item.length() > 0) {
				if (item.toUpperCase().equalsIgnoreCase(name.toUpperCase())) {
					cpIndex = i;
					break;
				}
			}
			i++;
		}
		if (cpIndex > -1) {
			srcCombo.select(cpIndex);
            profileSelectionChanged();
		}
	}

	private void refreshConnectionProfiles() {
		this.connectionProfiles = new ArrayList<IConnectionProfile>();

		if( this.info.isFlatFileLocalMode() ) {
			final IConnectionProfile[] tempProfiles = profileManager.getProfilesByCategory(ODA_FLAT_FILE_ID);
			for (final IConnectionProfile profile : tempProfiles) {
				connectionProfiles.add(profile);
			}
		} else if( this.info.isFlatFileUrlMode() ) {
			final IConnectionProfile[] tempProfiles = profileManager.getProfilesByCategory(IXmlProfileConstants.TEIID_CATEGORY);
			for (final IConnectionProfile profile : tempProfiles) {
				if(profile.getProviderId().equalsIgnoreCase(FLAT_FILE_URL_ID)) {
					connectionProfiles.add(profile);
				}
			}
		}
	}

	private boolean validatePage() {
		
		setSourceHelpMessage();
		
		String folderText = this.dataFileFolderText.getText();
		if(folderText!=null && folderText.equals(UNKNOWN_FOLDER)) {
            setThisPageComplete(getString("unknownFolderErrorMsg"), ERROR); //$NON-NLS-1$
			return false;
		}
		
		// Check for model file selected
		boolean fileSelected = false;
		for( TableItem item : this.fileViewer.getTable().getItems()) {
			if( item.getChecked() ) {
				fileSelected =  true;
				break;
			}
		}
		if( !fileSelected ) {
            if (info.isFlatFileLocalMode()||info.isFlatFileUrlMode()) {
                setThisPageComplete(getString("noDataFilesSelected"), ERROR);//$NON-NLS-1$
            }
			return false;
		}
			
		if( fileParsingStatus.getSeverity() == IStatus.ERROR) {
			setThisPageComplete(fileParsingStatus.getMessage(), ERROR);
			return false;
		}
		
		// Check for at least ONE open non-hidden Model Project
		Collection<IProject> openModelProjects = DotProjectUtils.getOpenModelProjects();

        // No open projects
        if (openModelProjects.size() == 0) {
			setThisPageComplete(getString("noOpenProjectsMessage"), ERROR);//$NON-NLS-1$
			return false;
		} else if (this.srcCombo.getText().length() == 0) {
			setThisPageComplete(INVALID_PAGE_MESSAGE, ERROR);
			return false;
		} 

    	// =============== SOURCE MODEL INFO CHECKS ==================
        String container = sourceModelContainerText.getText();
        if (CoreStringUtil.isEmpty(container)) {
        	setThisPageComplete(Util.getString(I18N_PREFIX + "sourceFileLocationMustBeSpecified"), ERROR); //$NON-NLS-1$
            return false;
        }				
        IProject project = getTargetProject();
        if (project == null) {
        	setThisPageComplete(Util.getString(I18N_PREFIX + "sourceFileLocationMustBeSpecified"), ERROR); //$NON-NLS-1$
            return false;
        }
        
        String fileText = sourceModelFileText.getText().trim();

        IStatus status = ModelNameUtil.validate(fileText, ModelerCore.MODEL_FILE_EXTENSION, null,
        		ModelNameUtil.IGNORE_CASE );
        if( status.getSeverity() == IStatus.ERROR ) {
        	setThisPageComplete(status.getMessage(), ERROR);
            return false;
        }
        
        // We've got a valid source model
        // If Existing, need to check for the wrong connection profile
        if( info.sourceModelExists() && !sourceModelHasSameConnectionProfile() ) {
        	setThisPageComplete(Util.getString(I18N_PREFIX + "connectionProfileForModelIsDifferent", fileText), ERROR); //$NON-NLS-1$
            return false;
        }
        
		if( ! isKnownTextFileExtension(info.getCheckedFileInfo().getDataFile()) ) {
			setThisPageComplete(getString("nonStandardFileExtensionSelected"), WARNING);//$NON-NLS-1$
		} else {
			setThisPageComplete(EMPTY_STRING, NONE);
		}


		return openModelProjects.size() > 0;
	}
	
    private void setThisPageComplete( String message, int severity) {
    	WizardUtil.setPageComplete(this, message, severity);
    }
	
    public IProject getTargetProject() {
        IProject result = null;
        String containerName = getSourceContainerName();

        if (!CoreStringUtil.isEmpty(containerName)) {
            IWorkspaceRoot root = ModelerCore.getWorkspace().getRoot();
            IResource resource = root.findMember(new Path(containerName));

            if (resource.exists()) {
                result = resource.getProject();
            }
        }

        return result;
    }
	
    public String getSourceContainerName() {
        String result = null;

        if (ProductCustomizerMgr.getInstance().getProductCharacteristics().isHiddenProjectCentric()) {
            result = getHiddenProjectPath();
        } else {
            result = sourceModelContainerText.getText().trim();
        }

        return result;
    }
    
    private String getHiddenProjectPath() {
        String result = null;
        IProject hiddenProj = ProductCustomizerMgr.getInstance().getProductCharacteristics().getHiddenProject(false);

        if (hiddenProj != null) {
            result = hiddenProj.getFullPath().makeRelative().toString();
        }

        return result;
    }
	
    private void setSourceHelpMessage() {
    	if( creatingControl ) return;
    	String proceedureName = "getTextFiles()"; //$NON-NLS-1$
    	
        if( info.sourceModelExists() ) {
	    	if(  sourceHasProcedure() ) {
	    		this.sourceHelpText.setText(Util.getString(I18N_PREFIX + "existingSourceModelHasProcedure", info.getSourceModelName(), proceedureName)); //$NON-NLS-1$
	    	} else {
	    		this.sourceHelpText.setText(Util.getString(I18N_PREFIX + "existingSourceModelHasNoProcedure", info.getSourceModelName(), proceedureName)); //$NON-NLS-1$
	    	}
        } else {
        	if( info.getSourceModelName() == null  || info.getSourceModelName().length() == 0) {
        		this.sourceHelpText.setText(Util.getString(I18N_PREFIX + "sourceModelUndefined")); //$NON-NLS-1$
        	} else {
        		this.sourceHelpText.setText(Util.getString(I18N_PREFIX + "sourceModelWillBeCreated", info.getSourceModelName(), proceedureName)); //$NON-NLS-1$
        	}
        }
    }
    
    private boolean sourceHasProcedure() {
    	if( this.sourceModelFilePath == null ) {
    		return false;
    	}
    	
    	IPath modelPath = new Path(sourceModelFilePath.toOSString()).append(this.sourceModelFileText.getText());
		if( !modelPath.toString().toUpperCase().endsWith(".XMI")) { //$NON-NLS-1$
			modelPath = modelPath.addFileExtension("xmi"); //$NON-NLS-1$
		}
    	
    	IResource sourceModel = ModelerCore.getWorkspace().getRoot().getFile(modelPath);
    	ModelResource smr = ModelUtilities.getModelResourceForIFile((IFile)sourceModel, false);
    	if( smr != null ) {
    		try {
    			for( Object obj : smr.getAllRootEObjects() ) {

                    EObject eObj = (EObject)obj;
                    if (eObj instanceof Procedure  && GET_TEXT_FILES.equalsIgnoreCase(ModelObjectUtilities.getName(eObj)) ) {
                        return true;
                    }
                }
            } catch (ModelWorkspaceException err) {
                Util.log(err);
            }
    	}
    	
    	return false;
    }
    
    private boolean sourceModelHasSameConnectionProfile() {
    	if( this.sourceModelFilePath == null ) {
    		return false;
    	}
    	
    	IPath modelPath = new Path(sourceModelFilePath.toOSString()).append(this.sourceModelFileText.getText());
		if( !modelPath.toString().toUpperCase().endsWith(".XMI")) { //$NON-NLS-1$
			modelPath = modelPath.addFileExtension("xmi"); //$NON-NLS-1$
		}
    	
    	IResource sourceModel = ModelerCore.getWorkspace().getRoot().getFile(modelPath);
    	ModelResource smr = ModelUtilities.getModelResourceForIFile((IFile)sourceModel, false);
    	if( smr != null ) {
			IConnectionProfile profile = connectionInfoHelper.getConnectionProfile(smr);
			if( profile == null  || this.info.getConnectionProfile() == null) {
				return false;
			}
			
			if( profile.getName().equalsIgnoreCase(this.info.getConnectionProfile().getName()) ) {
				return true;
			}
    	}
    	
    	return false;
    }
    
    class ConnectionProfileInfo  {
    	// 		INCLTYPELINE=NO
    	//		INCLCOLUMNNAME=YES
    	//		HOME=/home/blafond/TestDesignerFolder/FlatFileData/employee-data
    	//		TRAILNULLCOLS=NO
    	//		DELIMTYPE=COMMA
    	//		CHARSET=UTF-8
    	public boolean includeTypeLine = false;
    	public boolean columnsInFirstLine = false;
    	public String home;
    	public String delimiterType = VALUE_COMMA;
    	public String charset="UTF-8";  //$NON-NLS-1$
    }

/**
 * @since 8.0
 */
	public class CPListener implements IProfileListener {

		IConnectionProfile latestProfile;

		@Override
		public void profileAdded(IConnectionProfile profile) {
			latestProfile = profile;
		}

		@Override
		public void profileChanged(IConnectionProfile profile) {
			latestProfile = profile;
		}

		@Override
		public void profileDeleted(IConnectionProfile profile) {
			// nothing
		}

		public IConnectionProfile getChangedProfile() {
			return latestProfile;
		}
	}

	class DataFolderContentProvider implements ITreeContentProvider {

		boolean isFlatFileContent = true;
		private DefaultFilterMatcher filterMatcher = new DefaultFilterMatcher();

		// /////////////////////////////////////////////////////////////////////////////////////////////
		// CONSTANTS
		// /////////////////////////////////////////////////////////////////////////////////////////////

		private Object[] NO_CHILDREN = new Object[0];

		// /////////////////////////////////////////////////////////////////////////////////////////////
		// METHODS
		// /////////////////////////////////////////////////////////////////////////////////////////////

		public void setFilterString(String pattern) {
			this.filterMatcher.setFilter(pattern+"*", true, false); //$NON-NLS-1$
		}
		
		/**
		 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
		 * @since 4.2
		 */
		@Override
		public void dispose() {
		}

		/**
		 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
		 * @since 4.2
		 */
		@Override
		public Object[] getChildren(Object theParent) {
			Object[] result = null;

			if (theParent instanceof File) {
				result = ((File) theParent).listFiles();
			}

			return ((result == null) ? NO_CHILDREN : result);
		}

		/**
		 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
		 * @since 4.2
		 */
		@Override
		public Object[] getElements(Object theInput) {
			if (theInput instanceof File && ((File) theInput).isDirectory()) {
				File[] allFiles = ((File) theInput).listFiles();
				Collection<File> goodFilesList = new ArrayList<File>();

				for (File theFile : allFiles) {
				    if (!theFile.isDirectory()) {
				        if (isFlatFileContent) {
				            if(isValidTextFile(theFile)) {
				                goodFilesList.add(theFile);
				            }
				        } else if (theFile.getName().toUpperCase().endsWith(DOT_XML)) {
				            goodFilesList.add(theFile);
				        }
				    }
				}
				return goodFilesList.toArray(new File[0]);
			} else if (theInput instanceof File) {
				Collection<File> goodFilesList = new ArrayList<File>();
				
				File theFile = ((File) theInput);
				
				if (isFlatFileContent) {
				    if(isValidTextFile(theFile)) {
				        goodFilesList.add(theFile);
				    }
				} else if (theFile.getName().toUpperCase().endsWith(DOT_XML)) {
					goodFilesList.add(theFile);
				}
				return goodFilesList.toArray(new File[0]);
			}

			Collection<File> goodFilesList = new ArrayList<File>();
			return goodFilesList.toArray(new File[0]);
		}
		
		/*
		 * Determine if the supplied file is considered a valid text file.  Valid text file must end in '.txt', '.csv' or have no extension.
		 * @param file the supplied File
		 * @return 'true' if the file is a valid text file, 'false' if not.
		 */
		private boolean isValidTextFile(File file) {
		    boolean isValid = false;
		    String fileName = file.getName().toUpperCase();
		    if(filterMatcher.match(fileName)) {
		    	isValid = true;
		    }
		    return isValid;
		}

		/**
		 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
		 * @since 4.2
		 */
		@Override
		public Object getParent(Object theElement) {
			return ((theElement instanceof File) ? ((File) theElement)
					.getParentFile() : null);
		}

		/**
		 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
		 * @since 4.2
		 */
		@Override
		public boolean hasChildren(Object theElement) {
			Object[] kids = getChildren(theElement);
			return ((kids != null) && (kids.length > 0));
		}

		/**
		 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer,
		 *      java.lang.Object, java.lang.Object)
		 * @since 4.2
		 */
		@Override
		public void inputChanged(Viewer theViewer, Object theOldInput,
				Object theNewInput) {
		}

		public void setIsFlatFileContent(boolean isFlatFileContent) {
			this.isFlatFileContent = isFlatFileContent;
		}
	}
	
	class DefaultFilterMatcher implements FilterMatcher {
		private StringMatcher fMatcher;

		@Override
		public void setFilter(String pattern, boolean ignoreCase,
				boolean ignoreWildCards) {
			fMatcher = new StringMatcher(pattern + '*', ignoreCase,
					ignoreWildCards);
		}

		@Override
		public boolean match(Object element) {
			return fMatcher.match(element.toString());
		}
	}

	class DataFileContentColumnLabelProvider extends ColumnLabelProvider {

		public DataFileContentColumnLabelProvider() {
			super();
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see org.eclipse.jface.viewers.ColumnLabelProvider#getText(java.lang.Object)
		 */
		@Override
		public String getText(Object element) {
			// Element should be a "File"
			if( element instanceof File) {
				String name = ((File) element).getName();
				
				if( isKnownTextFileExtension((File) element)) {
					name =  name + "     <<<<"; //$NON-NLS-1$
				}
				return name;
			}
			return EMPTY_STRING;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see org.eclipse.jface.viewers.CellLabelProvider#getToolTipText(java.lang.Object)
		 */
		@Override
		public String getToolTipText(Object element) {
			return getString("fileNameColumnTooltip"); //$NON-NLS-1$
		}

		@Override
		public Image getImage(Object element) {

			return null;
		}

	}
	
	final ViewerFilter sourceModelFilter = new ModelWorkspaceViewerFilter(true) {

        @Override
        public boolean select( final Viewer viewer,
                               final Object parent,
                               final Object element ) {
            boolean doSelect = false;
            if (element instanceof IResource) {
                // If the project is closed, dont show
                boolean projectOpen = ((IResource)element).getProject().isOpen();
                if (projectOpen) {
                    // Show open projects
                    if (element instanceof IProject) {
                    	try {
		                	doSelect = ((IProject)element).hasNature(ModelerCore.NATURE_ID);
		                } catch (CoreException e) {
		                	ModelerCore.Util.log(e);
		                }
                    } else if (element instanceof IContainer) {
                        doSelect = true;
                        // Show webservice model files, and not .xsd files
                    } else if (element instanceof IFile && ModelUtil.isModelFile((IFile)element)) {
                        ModelResource theModel = null;
                        try {
                            theModel = ModelUtil.getModelResource((IFile)element, true);
                        } catch (Exception ex) {
                            ModelerCore.Util.log(ex);
                        }
                        if (theModel != null && ModelIdentifier.isRelationalSourceModel(theModel)) {
                            doSelect = true;
                        }
                    }
                }
            } else if (element instanceof IContainer) {
                doSelect = true;
            }

            return doSelect;
        }
    };

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		
		if( visible ) {
			this.fileContentProvider.setIsFlatFileContent(true);

			this.setTitle(TITLE);
			this.fileNameColumn.getColumn().setText(getString("dataFileNameColumn")); //$NON-NLS-1$
			
			// If current profile is invalid for this page, it is reset.
			// this may happen if user toggle between local and remote xml...
			IConnectionProfile currentProfile = getConnectionProfile();
			if(currentProfile==null) {
				this.fileViewer.setInput(null);
	    		this.info.setSourceModelName(null);
	    		setSourceHelpMessage();
			} else if(!isValidProfileForPage(currentProfile)) {
				setConnectionProfile(null);
				this.fileViewer.setInput(null);
	    		this.info.setSourceModelName(null);
	    		setSourceHelpMessage();
			}
						
			refreshConnectionProfiles();
			resetCPComboItems();
			
			loadFileListViewer();
			fileNameColumn.getColumn().pack();
			synchronizeUI();
			validatePage();
		}
	}
	
	private boolean isValidProfileForPage(IConnectionProfile profile) {
		boolean isValid = false;
		if( this.info.isFlatFileLocalMode() && profile.getProviderId().equalsIgnoreCase(ODA_FLAT_FILE_ID)) {
			isValid=true;
		} else if( this.info.isFlatFileUrlMode() && profile.getProviderId().equalsIgnoreCase(FLAT_FILE_URL_ID)) {
			isValid=true;
		}
		return isValid;
	}
	
	private boolean isKnownTextFileExtension(File file) {
	    IPath filePath = new Path(file.getPath());
	    String ext = filePath.getFileExtension().toUpperCase();
	    for( String str : TEXT_FILE_EXTENSIONS ) {
	    	if( str.equalsIgnoreCase(ext) ) {
	    		return true;
	    	}
	    }
	    
	    return false;
	}


}