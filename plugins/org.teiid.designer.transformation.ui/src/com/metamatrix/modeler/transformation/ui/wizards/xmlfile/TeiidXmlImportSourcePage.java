/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.ui.wizards.xmlfile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.IProfileListener;
import org.eclipse.datatools.connectivity.ProfileManager;
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
import org.eclipse.jface.wizard.Wizard;
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
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.INewWizard;
import org.teiid.core.util.Base64;
import org.teiid.core.util.ObjectConverterUtil;
import org.teiid.designer.datatools.connection.ConnectionInfoHelper;
import org.teiid.designer.datatools.connection.IConnectionInfoHelper;
import org.teiid.designer.datatools.profiles.ws.IWSProfileConstants;
import org.teiid.designer.datatools.profiles.xml.IXmlProfileConstants;
import org.teiid.designer.datatools.ui.actions.EditConnectionProfileAction;
import org.teiid.designer.datatools.ui.dialogs.NewTeiidFilteredCPWizard;

import com.metamatrix.common.protocol.URLHelper;
import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.core.util.StringUtilities;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceItem;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.core.workspace.ModelWorkspaceManager;
import com.metamatrix.modeler.internal.ui.explorer.ModelExplorerContentProvider;
import com.metamatrix.modeler.internal.ui.explorer.ModelExplorerLabelProvider;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelIdentifier;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelProjectSelectionStatusValidator;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelResourceSelectionValidator;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelWorkspaceViewerFilter;
import com.metamatrix.modeler.transformation.ui.UiConstants;
import com.metamatrix.modeler.transformation.ui.UiPlugin;
import com.metamatrix.modeler.transformation.ui.wizards.file.FlatFileRelationalModelFactory;
import com.metamatrix.modeler.transformation.ui.wizards.file.TeiidMetadataImportInfo;
import com.metamatrix.modeler.transformation.ui.wizards.file.TeiidMetadataImportSourcePage;
import com.metamatrix.modeler.ui.viewsupport.DesignerPropertiesUtil;
import com.metamatrix.modeler.ui.viewsupport.ModelingResourceFilter;
import com.metamatrix.ui.internal.InternalUiConstants;
import com.metamatrix.ui.internal.product.ProductCustomizerMgr;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.util.WidgetUtil;
import com.metamatrix.ui.internal.util.WizardUtil;
import com.metamatrix.ui.internal.viewsupport.FileSystemLabelProvider;
import com.metamatrix.ui.internal.wizard.AbstractWizardPage;

public class TeiidXmlImportSourcePage extends AbstractWizardPage
		implements UiConstants, InternalUiConstants.Widgets,
		CoreStringUtil.Constants {

	// ===========================================================================================================================
	// Constants

	private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(TeiidMetadataImportSourcePage.class);

	private static final String TITLE = getString("title"); //$NON-NLS-1$
	private static final String XML_TITLE = getString("xmlTitle"); //$NON-NLS-1$
	private static final String INITIAL_MESSAGE = getString("initialMessage"); //$NON-NLS-1$

	private static final String FLAT_FILE_SOURCE_LABEL = getString("sourceLabel"); //$NON-NLS-1$
	private static final String NEW_BUTTON = "New..."; //Util.getString("Widgets.newLabel"); //$NON-NLS-1$
	private static final String EDIT_BUTTON = "Edit..."; //Util.getString("Widgets.editLabel"); //$NON-NLS-1$

	private static final String INVALID_PAGE_MESSAGE = getString("invalidPageMessage"); //$NON-NLS-1$
	//private static final String HOME = "HOME"; //$NON-NLS-1$
	private static final String EMPTY_STRING = ""; //$NON-NLS-1$
	private static final String DOT_XML = ".XML"; //$NON-NLS-1$
	private static final String DOT_XML_LOWER = ".xml"; //$NON-NLS-1$

	private static final String DEFAULT_EXTENSION = ".xmi"; //$NON-NLS-1$
	private static final String GET_TEXT_FILES = "getTextFiles()"; //$NON-NLS-1$
	private static final String INVOKE_HTTP = "invokeHttp()"; //$NON-NLS-1$

	private static final String XML_URL_FILE_ID = IXmlProfileConstants.FILE_URL_CONNECTION_PROFILE_ID;
	private static final String XML_FILE_ID = IXmlProfileConstants.LOCAL_FILE_CONNECTION_PROFILE_ID;
	private static final String TEIID_WS_ID = IWSProfileConstants.TEIID_WS_CONNECTION_PROFILE_ID;

	//private static final String SCHEMA_LIST_PROPERTY_KEY = "SCHEMAFILELIST";  //$NON-NLS-1$
	private static final String LOCAL_FILE_NAME_KEY = IXmlProfileConstants.LOCAL_FILE_PATH_PROP_ID;
	private static final String FILE_URL_NAME_KEY = IXmlProfileConstants.URL_PROP_ID;
	
	//private static final String CONTENT_TYPE_XML = "application/xml"; //$NON-NLS-1$

	private static String getString(final String id) {
		return Util.getString(I18N_PREFIX + id);
	}

	private ILabelProvider srcLabelProvider;
	private Combo srcCombo;
	private Text dataFileFolderText;
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

	boolean creatingControl = false;

	boolean synchronizing = false;

	boolean processingChecks = false;

	IStatus fileParsingStatus;

	IConnectionInfoHelper connectionInfoHelper;

    Properties designerProperties;

	/**
	 * @since 4.0
	 */
	public TeiidXmlImportSourcePage(TeiidMetadataImportInfo info) {
		this(null, info);
	}

	/**
	 * @since 4.0
	 */
	public TeiidXmlImportSourcePage(Object selection,
			TeiidMetadataImportInfo info) {
		super(TeiidMetadataImportSourcePage.class.getSimpleName(), TITLE);
		// Set page incomplete initially
		this.info = info;
		setPageComplete(false);
		setImageDescriptor(UiPlugin.getDefault().getImageDescriptor(
				Images.IMPORT_TEIID_METADATA));
		this.connectionInfoHelper = new ConnectionInfoHelper();
	}

	@Override
	public void createControl(Composite parent) {
		// Create page
		final Composite mainPanel = new Composite(parent, SWT.NONE);

		mainPanel.setLayout(new GridLayout());
		mainPanel.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL
				| GridData.HORIZONTAL_ALIGN_FILL));
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
		Group profileGroup = WidgetFactory.createGroup(parent,
				FLAT_FILE_SOURCE_LABEL, SWT.NONE, 2);
		profileGroup.setLayout(new GridLayout(3, false));
		profileGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

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
		this.srcCombo.addModifyListener(new ModifyListener() {

			public void modifyText(final ModifyEvent event) {
				// profileModified();
				// fileViewer.refresh();
			}
		});
		this.srcCombo.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				profileModified();
				fileViewer.refresh();

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
			}
		});

		this.srcCombo.setVisibleItemCount(10);

		WidgetFactory.createButton(profileGroup, NEW_BUTTON)
				.addSelectionListener(new SelectionAdapter() {

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
		String groupLabel = getString("folderXmlContentsGroup"); //$NON-NLS-1$

		Group folderContentsGroup = WidgetFactory.createGroup(parent,
				groupLabel, SWT.FILL, 3, 2);
		GridData gd_1 = new GridData(GridData.FILL_BOTH);
		gd_1.heightHint = 180;
		folderContentsGroup.setLayoutData(gd_1);

		Label locationLabel = new Label(folderContentsGroup, SWT.NONE);
		locationLabel.setText(getString("folderLocation")); //$NON-NLS-1$

		dataFileFolderText = new Text(folderContentsGroup, SWT.BORDER
				| SWT.SINGLE);
		dataFileFolderText.setBackground(WidgetUtil.getReadOnlyBackgroundColor());
		dataFileFolderText.setForeground(WidgetUtil.getDarkBlueColor());
		dataFileFolderText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		dataFileFolderText.setEditable(false);

		createFileTableViewer(folderContentsGroup);

		Label selectedFileLabel = new Label(folderContentsGroup, SWT.NONE);
		selectedFileLabel.setText(getString("selectedXmlFile")); //$NON-NLS-1$

		selectedFileText = new Text(folderContentsGroup, SWT.BORDER | SWT.SINGLE);
		selectedFileText.setBackground(WidgetUtil.getReadOnlyBackgroundColor());
		selectedFileText.setForeground(WidgetUtil.getDarkBlueColor());
		selectedFileText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		selectedFileText.setEditable(false);

	}

	private void createFileTableViewer(Composite parent) {

		Table table = new Table(parent, SWT.SINGLE | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.BORDER | SWT.CHECK);
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
		this.fileViewer.setContentProvider(fileContentProvider);
		this.fileViewer.setLabelProvider(new FileSystemLabelProvider());

		// Check events can occur separate from selection events.
		// In this case move the selected node.
		// Also trigger selection of node in model.
		this.fileViewer.getTable().addSelectionListener(
				new SelectionListener() {

					public void widgetSelected(SelectionEvent e) {
						if (processingChecks) {
							return;
						}
						processingChecks = true;
						if (e.detail == SWT.CHECK) {

							TableItem tableItem = (TableItem) e.item;
							boolean wasChecked = tableItem.getChecked();

							if (tableItem.getData() instanceof File) {
								fileViewer.getTable().setSelection(new TableItem[] { tableItem });
								if (wasChecked) {
									for (TableItem item : fileViewer.getTable().getItems()) {
										if (item != tableItem) {
											item.setChecked(false);
										}
									}
								}

								info.setDoProcessXml((File) tableItem.getData(), wasChecked);
							}
						}
						processingChecks = false;
						synchronizeUI();
						validatePage();
					}

					public void widgetDefaultSelected(SelectionEvent e) {
					}
				});

		// create columns
		fileNameColumn = new TableViewerColumn(this.fileViewer, SWT.LEFT);
		if (this.info.getFileMode() == TeiidMetadataImportInfo.FILE_MODE_TEIID_XML_URL) {
			fileNameColumn.getColumn().setText("XML Data File URL" ); //getString("dataFileNameColumn")); //$NON-NLS-1$
		} else {
			fileNameColumn.getColumn().setText(getString("xmlDataFileNameColumn")); //$NON-NLS-1$
		}
		fileNameColumn.setLabelProvider(new DataFileContentColumnLabelProvider());
		fileNameColumn.getColumn().pack();
	}

	private void createSourceModelGroup(Composite parent) {
		Group sourceGroup = WidgetFactory.createGroup(parent,getString("sourceModelDefinitionGroup"), SWT.NONE, 1); //$NON-NLS-1$
		sourceGroup.setLayout(new GridLayout(3, false));
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
			public void widgetSelected(SelectionEvent e) {
				handleSourceModelLocationBrowse();
			}
		});

		Label fileLabel = new Label(sourceGroup, SWT.NULL);
		fileLabel.setText(getString("name")); //$NON-NLS-1$

		sourceModelFileText = new Text(sourceGroup, SWT.BORDER | SWT.SINGLE);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		sourceModelFileText.setLayoutData(gridData);
		sourceModelFileText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				handleSourceModelTextChanged();
			}
		});

		browseButton = new Button(sourceGroup, SWT.PUSH);
		gridData = new GridData();
		browseButton.setLayoutData(gridData);
		browseButton.setText(getString("browse")); //$NON-NLS-1$
		browseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleSourceModelBrowse();
			}
		});

		new Label(sourceGroup, SWT.NONE);

		Group helpGroup = WidgetFactory.createGroup(sourceGroup,
				getString("modelStatus"), SWT.NONE | SWT.BORDER_DASH, 2); //$NON-NLS-1$
		helpGroup.setLayout(new GridLayout(1, false));
		helpGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		{
			sourceHelpText = new Text(helpGroup, SWT.WRAP | SWT.READ_ONLY);
			sourceHelpText.setBackground(WidgetUtil.getReadOnlyBackgroundColor());
			sourceHelpText.setForeground(WidgetUtil.getDarkBlueColor());
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.heightHint = 40;
			gd.horizontalSpan = 3;
			sourceHelpText.setLayoutData(gd);
		}

	}

	void profileModified() {
		if (this.srcCombo.getSelectionIndex() > -1) {
			String cpName = this.srcCombo.getItem(this.srcCombo.getSelectionIndex());
			for (IConnectionProfile profile : this.connectionProfiles) {
				if (profile.getName().equalsIgnoreCase(cpName)) {
					setConnectionProfile(profile);
					Properties props = profile.getBaseProperties();
					String home = (String) props.get(IXmlProfileConstants.TEIID_PARENT_DIRECTORY_KEY);
					if (home != null) {
						String location = home;
						if (location.length() > 60) {
							int len = location.length();
							location = "..." + location.substring(len - 60, len); //$NON-NLS-1$
						}
						this.dataFileFolderText.setText(location);
						this.dataFileFolderText.setToolTipText(home);
					} else {
						String URL = (String) props.get(IXmlProfileConstants.URL_PROP_ID);
						if( URL != null ) {
							String location = URL;
							if (location.length() > 60) {
								int len = location.length();
								location = "..." + location.substring(len - 60, len); //$NON-NLS-1$
							}
							this.dataFileFolderText.setText(location);
							this.dataFileFolderText.setToolTipText(URL);
						} else {
							this.dataFileFolderText.setText(EMPTY_STRING);
							this.dataFileFolderText.setToolTipText(EMPTY_STRING);
						}
					}

					clearFileListViewer();
					loadFileListViewer();
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

    public void setDesignerProperties( Properties properties ) {
        this.designerProperties = properties;
    }

	private void setConnectionProfile(IConnectionProfile profile) {
		this.info.setConnectionProfile(profile);
	}

	private IConnectionProfile getConnectionProfile() {
		return this.info.getConnectionProfile();
	}
	
	private boolean isRestConnectionProfile() {
		IConnectionProfile profile = this.info.getConnectionProfile();
		if( profile != null ) {
			return profile.getProviderId().equals(TEIID_WS_ID);
		}
		
		return false;
	}

	private void clearFileListViewer() {
		this.info.clearXmlFileInfos();
		this.info.clearFileInfos();
		fileViewer.remove(fileViewer.getTable().getItems());
	}

	private void loadFileListViewer() {
		if (getConnectionProfile() != null) {
			File theXmlFile = getFileForConnectionProfile();
			String urlString = getUrlStringForConnectionProfile();

			if (theXmlFile != null && theXmlFile.exists()) {
				setXmlFile(theXmlFile, false, null);
			} else if (urlString != null && urlString.trim().length() > 0) {
				File xmlFile = null;
				// Clears the viewer
				// This will be the case if No XML is defined and URL
				// version exists OR if nothing is defined in CP
				fileViewer.setInput("no input"); //$NON-NLS-1$
				
				if( isRestConnectionProfile() ) {
					xmlFile = getXmlFileFromRestUrl(getConnectionProfile());
				} else {
					xmlFile = getXmlFileFromUrl(urlString);
				}
				if (xmlFile != null && xmlFile.exists()) {
					setXmlFile(xmlFile, true, urlString);
				}
			} else {
				fileViewer.setInput(null);
				MessageDialog.openError(this.getShell(),
						getString("invalidXmlConnectionProfileTitle"), //$NON-NLS-1$
						getString("invalidXmlConnectionProfileMessage")); //$NON-NLS-1$
			}
		}
	}
	
	private File getXmlFileFromRestUrl(IConnectionProfile profile) {
		Properties props = profile.getBaseProperties();
		String endpoint = (String) props.get(IWSProfileConstants.URL_PROP_ID);
		String username = (String) props.get(IWSProfileConstants.USERNAME_PROP_ID);
		String password = (String) props.get(IWSProfileConstants.PASSWORD_PROP_ID);
		File xmlFile = null;
		FileOutputStream fos = null;
		
		try {
			
			final URL url = new URL(endpoint);
			final HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
			String filePath = formatPath(url);
			
			//TODO Validate content is XML
//			if( !CONTENT_TYPE_XML.equalsIgnoreCase(httpConn.getContentType())) {
//				return null;
//			}
			
			if (username != null && !username.isEmpty()) {
				httpConn.setRequestProperty("Authorization", "Basic " + Base64.encodeBytes((username + ':' + password).getBytes())); //$NON-NLS-1$ //$NON-NLS-2$
			}
			
			httpConn.setDoOutput(true);
			InputStream is = httpConn.getInputStream();
			xmlFile = File.createTempFile(CoreStringUtil.createFileName(filePath),DOT_XML_LOWER);
			FileOutputStream os = new FileOutputStream(xmlFile);
			ObjectConverterUtil.write(os, is, -1);
			
		} catch (MalformedURLException ex) {
			throw new RuntimeException(ex);
		} catch (ProtocolException ex) {
			throw new RuntimeException(ex);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return xmlFile;
	}
	
	private File getXmlFileFromUrl(String urlString) {
		File xmlFile = null;
		URL newUrl = null;
		try {
			newUrl = URLHelper.buildURL(urlString);
		} catch (MalformedURLException e) {
			Util.log(e);
			MessageDialog.openError(
							this.getShell(),
							getString("malformedUrlErrorTitle"), //$NON-NLS-1$
							UiConstants.Util.getString("malformedUrlErrorMessage", urlString, e.getMessage())); //$NON-NLS-1$
		}

		if (newUrl != null) {
			boolean resolved = true;
			try {
				resolved = URLHelper.resolveUrl(newUrl);
			} catch (Exception e) {
				resolved = false;

			}

			if (resolved) {
				try {
					String filePath = formatPath(newUrl);
					xmlFile = URLHelper.createFileFromUrl(newUrl,CoreStringUtil.createFileName(filePath),DOT_XML_LOWER);
				} catch (MalformedURLException theException) {
					Util.log(theException);
				} catch (IOException theException) {
					Util.log(theException);
				}
			}
		}
		
		return xmlFile;
	}
	
	private void setXmlFile(File xmlFile, boolean isUrl, String urlString) {
		fileViewer.setInput(xmlFile);
		TeiidXmlFileInfo fileInfo = this.info.getXmlFileInfo(xmlFile);
		if (fileInfo == null) {
			fileInfo = new TeiidXmlFileInfo(xmlFile);
			fileInfo.setIsUrl(isUrl);
			if( isUrl ) {
				fileInfo.setXmlFileUrl(urlString);
			}
			this.info.addXmlFileInfo(fileInfo);
		}
		fileViewer.getTable().select(0);
		fileViewer.getTable().getItem(0).setChecked(true);
		info.setDoProcessXml(fileInfo.getDataFile(), true);
		fileParsingStatus = fileInfo.getParsingStatus();
		if (fileParsingStatus.getSeverity() == IStatus.ERROR) {
			MessageDialog.openError(this.getShell(),
					getString("parsingErrorTitle"), //$NON-NLS-1$
					fileParsingStatus.getMessage());
		}
	}

	/**
	 * If the path begins with a "/", we need to strip off since this will be
	 * changed to an underscore and create an invalid model name. Also, we need
	 * to remove any periods.
	 * 
	 * @param newUrl
	 * @return filePath - reformatted string used for generating the new file
	 *         name
	 */
	public static String formatPath(URL newUrl) {
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

	private File getFileForConnectionProfile() {
		if (getConnectionProfile() != null) {
			Properties props = getConnectionProfile().getBaseProperties();
			String fileListValue = (String) props.get(LOCAL_FILE_NAME_KEY);
			if (fileListValue != null) {
				return new File(fileListValue);
			}
		}

		return null;
	}

	private String getUrlStringForConnectionProfile() {
		if (getConnectionProfile() != null) {
			Properties props = getConnectionProfile().getBaseProperties();
			if(isRestConnectionProfile()) {
				String fileListValue = (String) props.get(IWSProfileConstants.URL_PROP_ID);
				if (fileListValue != null) {
					return fileListValue;
				}
			} else {
				String fileListValue = (String) props.get(FILE_URL_NAME_KEY);
				if (fileListValue != null) {
					return fileListValue;
				}
			}
		}

		return null;
	}

	void profileChanged() {
		profileModified();
	}

	void createNewConnectionProfile() {
		INewWizard wiz = null;

		if (this.info.getFileMode() == TeiidMetadataImportInfo.FILE_MODE_TEIID_XML_URL) {
			// We need to create a Dialog to ask user to choose either a XML File URL CP or a WS REST CP
			TeiidXmlConnectionOptionsDialog dialog = new TeiidXmlConnectionOptionsDialog(Display.getCurrent().getActiveShell());
			if (dialog.open() == Window.OK) {
				boolean isRestProfile = dialog.isRestProfile();
				if( isRestProfile ) {
					wiz = (INewWizard) new NewTeiidFilteredCPWizard(TEIID_WS_ID);
				} else {
					wiz = (INewWizard) new NewTeiidFilteredCPWizard(XML_URL_FILE_ID);
				}
			}
			
			
		} else {
			wiz = (INewWizard) new NewTeiidFilteredCPWizard(XML_FILE_ID);
		}
		
		if( wiz != null ) {
			WizardDialog wizardDialog = new WizardDialog(Display.getCurrent().getActiveShell(), (Wizard) wiz);
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
	}

	void selectProfile(IConnectionProfile profile) {
		int index = 0;
		for (String item : this.srcCombo.getItems()) {
			if (item != null && item.equalsIgnoreCase(profile.getName())) {
				this.srcCombo.select(index);
				profileModified();
				break;
			}
			index++;
		}
	}

	void resetCPComboItems() {
		if (this.srcCombo != null) {
			WidgetUtil.setComboItems(this.srcCombo,
					(ArrayList) this.connectionProfiles, this.srcLabelProvider,
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
						(ArrayList) this.connectionProfiles,
						this.srcLabelProvider, true);

				WidgetUtil.setComboText(this.srcCombo, getConnectionProfile(),this.srcLabelProvider);

				selectConnectionProfile(currentProfile.getName());

				ProfileManager.getInstance().removeProfileListener(listener);

				profileModified();
			} else {
				// Remove the listener if the dialog is canceled
				ProfileManager.getInstance().removeProfileListener(listener);
			}
		}
	}

	/**
	 * Uses the standard container selection dialog to choose the new value for
	 * the container field.
	 */
	void handleSourceModelLocationBrowse() {
		final IContainer folder = WidgetUtil.showFolderSelectionDialog(
				ResourcesPlugin.getWorkspace().getRoot(),
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
		final Object[] selections = WidgetUtil
				.showWorkspaceObjectSelectionDialog(
						getString("selectSourceModelTitle"), //$NON-NLS-1$
						getString("selectSourceModelMessage"), //$NON-NLS-1$
						false, null, sourceModelFilter,
						new ModelResourceSelectionValidator(false),
						new ModelExplorerLabelProvider(),
						new ModelExplorerContentProvider());

		if (selections != null && selections.length == 1
				&& sourceModelFileText != null) {
			if (selections[0] instanceof IFile) {
				IFile modelFile = (IFile) selections[0];
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
        	this.sourceModelContainerText.setText(StringUtilities.EMPTY_STRING);
        }
        
        this.info.setSourceModelExists(sourceModelExists());

		validatePage();
	}

	void handleSourceModelTextChanged() {
		if (synchronizing)
			return;

		String newName = ""; //$NON-NLS-1$
		if (this.sourceModelFileText.getText() != null && this.sourceModelFileText.getText().length() > -1) {
			newName = this.sourceModelFileText.getText();
			this.info.setSourceModelName(newName);
			this.info.setSourceModelExists(sourceModelExists());

		}

		validatePage();
	}
	
	void synchronizeUI() {
		synchronizing = true;

		if (this.info.getSourceModelLocation() != null) {
			this.sourceModelFilePath = this.info.getSourceModelLocation();
			this.sourceModelContainerText.setText(this.info.getSourceModelLocation().makeRelative().toString());
		} else {
			this.sourceModelContainerText.setText(StringUtilities.EMPTY_STRING);
		}

		if (this.info.getSourceModelName() != null) {
			this.sourceModelFileText.setText(this.info.getSourceModelName());
		} else {
			this.sourceModelFileText.setText(StringUtilities.EMPTY_STRING);
		}

		String fileName = EMPTY_STRING;
		for (TeiidXmlFileInfo xmlFileInfo : this.info.getXmlFileInfos()) {
			if (xmlFileInfo.doProcess()) {
				fileName = xmlFileInfo.getDataFile().getName();
				break;
			}
		}
		this.selectedFileText.setText(fileName);

		synchronizing = false;
	}

	private boolean sourceModelExists() {
		if (this.sourceModelFilePath == null) {
			return false;
		}

		IPath modelPath = new Path(sourceModelFilePath.toOSString()).append(this.sourceModelFileText.getText());
		if (!modelPath.toString().toUpperCase().endsWith(".XMI")) { //$NON-NLS-1$
			modelPath = modelPath.addFileExtension("xmi"); //$NON-NLS-1$
		}

		ModelWorkspaceItem item = ModelWorkspaceManager.getModelWorkspaceManager().findModelWorkspaceItem(modelPath,IResource.FILE);
		if (item != null) {
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
            profileModified();
		}
	}

    private void setProfileFromProperties() {
        // Check for Connection Profile in properties
    	if( this.designerProperties != null ) {
            String profileName = DesignerPropertiesUtil.getConnectionProfileName(this.designerProperties);
            if (profileName != null && !profileName.isEmpty()) {
                // Select profile
                selectConnectionProfile(profileName);
            }
    	}
    }

	private void refreshConnectionProfiles() {
		this.connectionProfiles = new ArrayList<IConnectionProfile>();
		
		
			final IConnectionProfile[] tempProfiles = profileManager.getProfilesByCategory(IXmlProfileConstants.TEIID_CATEGORY);
			for (final IConnectionProfile profile : tempProfiles) {
				if( this.info.isXmlLocalFileMode() && profile.getProviderId().equalsIgnoreCase(XML_FILE_ID)) {
					connectionProfiles.add(profile);
				} else if( this.info.isXmlUrlFileMode() && profile.getProviderId().equalsIgnoreCase(XML_URL_FILE_ID) ) {
					connectionProfiles.add(profile);
				} else if( this.info.isXmlUrlFileMode() && profile.getProviderId().equalsIgnoreCase(TEIID_WS_ID) ) {
					connectionProfiles.add(profile);
				}
			}

			return;
//		}
	
//		if( this.info.isXmlUrlFileMode() ) {
//			final IConnectionProfile[] tempProfiles = profileManager.getProfilesByCategory(XML_URL_FILE_ID);
//			for (final IConnectionProfile profile : tempProfiles) {
//				connectionProfiles.add(profile);
//			}
//			return;
//		}
	}

	private boolean validatePage() {
		setSourceHelpMessage();
		
		// Check for model file selected
		boolean fileSelected = false;
		for (TableItem item : this.fileViewer.getTable().getItems()) {
			if (item.getChecked()) {
				fileSelected = true;
				break;
			}
		}
		if (!fileSelected) {
			setThisPageComplete(getString("noXmlDataFilesSelected"), ERROR);//$NON-NLS-1$
			return false;
		}

		if (fileParsingStatus.getSeverity() == IStatus.ERROR) {
			setThisPageComplete(fileParsingStatus.getMessage(), ERROR);
			return false;
		}

		// Check for at least ONE open non-hidden Model Project
		boolean validProj = false;
		for (IProject proj : ResourcesPlugin.getWorkspace().getRoot()
				.getProjects()) {
			try {
				boolean result = proj.isOpen()
						&& !proj.hasNature(ModelerCore.HIDDEN_PROJECT_NATURE_ID)
						&& proj.hasNature(ModelerCore.NATURE_ID);
				if (result) {
					validProj = true;
					break;
				}
			} catch (CoreException e) {
				UiConstants.Util.log(e);
			}
		}

		if (!validProj) {
			setThisPageComplete(getString("noOpenProjectsMessage"), ERROR);//$NON-NLS-1$
			return false;
		} else if (this.srcCombo.getText().length() == 0) {
			setThisPageComplete(INVALID_PAGE_MESSAGE, ERROR);
			return false;
		}

		// =============== SOURCE MODEL INFO CHECKS ==================
		String container = sourceModelContainerText.getText();
		if (CoreStringUtil.isEmpty(container)) {
			setThisPageComplete( getString("sourceFileLocationMustBeSpecified"), ERROR); //$NON-NLS-1$
			return false;
		}
		IProject project = getTargetProject();
		if (project == null) {
			setThisPageComplete(getString("sourceFileLocationMustBeSpecified"), ERROR); //$NON-NLS-1$
			return false;
		}

		String fileText = sourceModelFileText.getText().trim();

		if (fileText.length() == 0) {
			setThisPageComplete(getString("sourceFileNameMustBeSpecified"), ERROR); //$NON-NLS-1$
			return false;
		}
		String fileNameMessage = ModelUtilities.validateModelName(fileText,DEFAULT_EXTENSION);
		if (fileNameMessage != null) {
			setThisPageComplete(Util.getString(I18N_PREFIX + "illegalFileName", fileNameMessage), ERROR); //$NON-NLS-1$
			return false;
		}

		// We've got a valid source model
		// If Existing, need to check for the wrong connection profile
		if (info.sourceModelExists() && !sourceModelHasSameConnectionProfile()) {
			setThisPageComplete(Util.getString(I18N_PREFIX + "connectionProfileForModelIsDifferent", fileText), ERROR); //$NON-NLS-1$
			return false;
		}

		setThisPageComplete(EMPTY_STRING, NONE);

		return validProj;
	}

	private void setThisPageComplete(String message, int severity) {
		WizardUtil.setPageComplete(this, message, severity);
	}

	public IProject getTargetProject() {
		IProject result = null;
		String containerName = getSourceContainerName();

		if (!CoreStringUtil.isEmpty(containerName)) {
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
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
		if (creatingControl)
			return;
		String procedureName = GET_TEXT_FILES;
		if( info.isXmlUrlFileMode() ) {
			procedureName = INVOKE_HTTP;
		}
		if (info.sourceModelExists()) {
			if (sourceHasProcedure()) {
				this.sourceHelpText.setText(Util.getString(I18N_PREFIX + "existingSourceModelHasProcedure", info.getSourceModelName(), procedureName)); //$NON-NLS-1$
			} else {
				this.sourceHelpText.setText(Util.getString(I18N_PREFIX + "existingSourceModelHasNoProcedure", info.getSourceModelName(), procedureName)); //$NON-NLS-1$
			}
		} else {
			if (info.getSourceModelName() == null || info.getSourceModelName().length() == 0) {
				this.sourceHelpText.setText(Util.getString(I18N_PREFIX + "sourceModelUndefined")); //$NON-NLS-1$
			} else {
				this.sourceHelpText.setText(Util.getString(I18N_PREFIX + "sourceModelWillBeCreated", info.getSourceModelName(), procedureName)); //$NON-NLS-1$
			}
		}
	}

	private boolean sourceHasProcedure() {
		if (this.sourceModelFilePath == null) {
			return false;
		}

		IPath modelPath = new Path(sourceModelFilePath.toOSString()).append(this.sourceModelFileText.getText());
		if (!modelPath.toString().toUpperCase().endsWith(".XMI")) { //$NON-NLS-1$
			modelPath = modelPath.addFileExtension("xmi"); //$NON-NLS-1$
		}

		IResource sourceModel = ResourcesPlugin.getWorkspace().getRoot().getFile(modelPath);
		ModelResource smr = ModelUtilities.getModelResourceForIFile((IFile) sourceModel, false);
		if (smr != null) {
			if( info.isXmlLocalFileMode() ) {
				return FlatFileRelationalModelFactory.procedureExists(smr, FlatFileRelationalModelFactory.GET_TEXT_FILES);
			} else if( info.isXmlUrlFileMode() ) {
				return FlatFileRelationalModelFactory.procedureExists(smr, FlatFileRelationalModelFactory.INVOKE_HTTP);
			}
		}

		return false;
	}

	private boolean sourceModelHasSameConnectionProfile() {
		if (this.sourceModelFilePath == null) {
			return false;
		}

		IPath modelPath = new Path(sourceModelFilePath.toOSString()).append(this.sourceModelFileText.getText());
		if (!modelPath.toString().toUpperCase().endsWith(".XMI")) { //$NON-NLS-1$
			modelPath = modelPath.addFileExtension("xmi"); //$NON-NLS-1$
		}

		IResource sourceModel = ResourcesPlugin.getWorkspace().getRoot().getFile(modelPath);
		ModelResource smr = ModelUtilities.getModelResourceForIFile(
				(IFile) sourceModel, false);
		if (smr != null) {
			IConnectionProfile profile = connectionInfoHelper.getConnectionProfile(smr);
			if (profile == null || this.info.getConnectionProfile() == null) {
				return false;
			}

			if (profile.getName().equalsIgnoreCase(
					this.info.getConnectionProfile().getName())) {
				return true;
			}
		}

		return false;
	}

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

		// /////////////////////////////////////////////////////////////////////////////////////////////
		// CONSTANTS
		// /////////////////////////////////////////////////////////////////////////////////////////////

		private Object[] NO_CHILDREN = new Object[0];

		// /////////////////////////////////////////////////////////////////////////////////////////////
		// METHODS
		// /////////////////////////////////////////////////////////////////////////////////////////////

		/**
		 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
		 * @since 4.2
		 */
		public void dispose() {
		}

		/**
		 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
		 * @since 4.2
		 */
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
		public Object[] getElements(Object theInput) {
			if (theInput instanceof File && ((File) theInput).isDirectory()) {
				File[] allFiles = ((File) theInput).listFiles();
				Collection<File> goodFilesList = new ArrayList<File>();

				for (File theFile : allFiles) {
					if (!theFile.isDirectory()) {
						if (theFile.getName().toUpperCase().endsWith(DOT_XML)) {
							goodFilesList.add(theFile);
						}
					}
				}
				return goodFilesList.toArray(new File[0]);
			} else if (theInput instanceof File) {
				Collection<File> goodFilesList = new ArrayList<File>();

				File theFile = ((File) theInput);

				if (theFile.getName().toUpperCase().endsWith(DOT_XML)) {
					goodFilesList.add(theFile);
				}
				return goodFilesList.toArray(new File[0]);
			}

			Collection<File> goodFilesList = new ArrayList<File>();
			return goodFilesList.toArray(new File[0]);
		}

		/**
		 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
		 * @since 4.2
		 */
		public Object getParent(Object theElement) {
			return ((theElement instanceof File) ? ((File) theElement)
					.getParentFile() : null);
		}

		/**
		 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
		 * @since 4.2
		 */
		public boolean hasChildren(Object theElement) {
			Object[] kids = getChildren(theElement);
			return ((kids != null) && (kids.length > 0));
		}

		/**
		 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer,
		 *      java.lang.Object, java.lang.Object)
		 * @since 4.2
		 */
		public void inputChanged(Viewer theViewer, Object theOldInput,
				Object theNewInput) {
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
			if (element instanceof File) {
				return ((File) element).getName();
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
		public boolean select(final Viewer viewer, final Object parent,
				final Object element) {
			boolean doSelect = false;
			if (element instanceof IResource) {
				// If the project is closed, dont show
				boolean projectOpen = ((IResource) element).getProject()
						.isOpen();
				if (projectOpen) {
					// Show open projects
					if (element instanceof IProject) {
						doSelect = true;
					} else if (element instanceof IContainer) {
						doSelect = true;
						// Show webservice model files, and not .xsd files
					} else if (element instanceof IFile && ModelUtil.isModelFile((IFile) element)) {
						ModelResource theModel = null;
						try {
							theModel = ModelUtil.getModelResource((IFile) element, true);
						} catch (Exception ex) {
							ModelerCore.Util.log(ex);
						}
						if (theModel != null
								&& ModelIdentifier.isRelationalSourceModel(theModel)) {
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

		if (visible) {
			{
				this.setTitle(XML_TITLE);
				this.fileNameColumn.getColumn().setText(getString("xmlDataFileNameColumn")); //$NON-NLS-1$
			}
			refreshConnectionProfiles();
			
			resetCPComboItems();
			
			loadFileListViewer();
			
			fileNameColumn.getColumn().pack();
			
			synchronizeUI();

            setProfileFromProperties();
		}
	}
}
