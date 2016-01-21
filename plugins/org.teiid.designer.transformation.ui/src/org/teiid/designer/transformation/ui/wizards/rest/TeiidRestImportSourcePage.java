/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.ui.wizards.rest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.runtime.IStatus;
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
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.teiid.core.designer.util.Base64;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.core.designer.util.FileUtils;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.core.designer.util.StringConstants;
import org.teiid.datatools.connectivity.model.Parameter;
import org.teiid.designer.core.util.URLHelper;
import org.teiid.designer.datatools.connection.ConnectionInfoHelper;
import org.teiid.designer.datatools.connection.IConnectionInfoHelper;
import org.teiid.designer.datatools.profiles.ws.IWSProfileConstants;
import org.teiid.designer.datatools.profiles.xml.IXmlProfileConstants;
import org.teiid.designer.datatools.ui.actions.EditConnectionProfileAction;
import org.teiid.designer.datatools.ui.dialogs.NewTeiidFilteredCPWizard;
import org.teiid.designer.transformation.ui.UiConstants;
import org.teiid.designer.transformation.ui.UiPlugin;
import org.teiid.designer.transformation.ui.wizards.file.TeiidMetadataImportInfo;
import org.teiid.designer.transformation.ui.wizards.file.TeiidMetadataImportSourcePage;
import org.teiid.designer.transformation.ui.wizards.xmlfile.TeiidXmlFileInfo;
import org.teiid.designer.ui.common.ICredentialsCommon;
import org.teiid.designer.ui.common.InternalUiConstants;
import org.teiid.designer.ui.common.dialog.FileUiUtils;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.common.util.WidgetUtil;
import org.teiid.designer.ui.common.util.WizardUtil;
import org.teiid.designer.ui.common.viewsupport.FileSystemLabelProvider;
import org.teiid.designer.ui.common.widget.DefaultScrolledComposite;
import org.teiid.designer.ui.common.wizard.AbstractWizardPage;
import org.teiid.designer.ui.viewsupport.DesignerPropertiesUtil;

/**
 * @since 8.6
 */
public class TeiidRestImportSourcePage extends AbstractWizardPage implements
		UiConstants, InternalUiConstants.Widgets, CoreStringUtil.Constants {

	// ===========================================================================================================================
	// Constants

	private static final String I18N_PREFIX = I18nUtil
			.getPropertyPrefix(TeiidMetadataImportSourcePage.class);

	private static final String TITLE = getString("title"); //$NON-NLS-1$
	private static final String REST_TITLE = getString("restTitle"); //$NON-NLS-1$
	private static final String INITIAL_MESSAGE = getString("initialMessage"); //$NON-NLS-1$
	private static final String REST_SOURCE_LABEL = getString("restSourceLabel"); //$NON-NLS-1$
	private static final String NEW_BUTTON = "New..."; //Util.getString("Widgets.newLabel"); //$NON-NLS-1$
	private static final String EDIT_BUTTON = "Edit..."; //Util.getString("Widgets.editLabel"); //$NON-NLS-1$
	private static final String EMPTY_STRING = ""; //$NON-NLS-1$
	private static final String DOT_XML = ".XML"; //$NON-NLS-1$
	private static final String DOT_XML_LOWER = ".xml"; //$NON-NLS-1$
	private static final String XML_URL_FILE_ID = IXmlProfileConstants.FILE_URL_CONNECTION_PROFILE_ID;
	private static final String XML_FILE_ID = IXmlProfileConstants.LOCAL_FILE_CONNECTION_PROFILE_ID;
	private static final String TEIID_WS_ID = IWSProfileConstants.TEIID_WS_CONNECTION_PROFILE_ID;
	private static final String LOCAL_FILE_NAME_KEY = IXmlProfileConstants.LOCAL_FILE_PATH_PROP_ID;
	private static final String FILE_URL_NAME_KEY = IXmlProfileConstants.URL_PROP_ID;

	//private static final String CONTENT_TYPE_XML = "application/xml"; //$NON-NLS-1$

	private static final int DEFAULT_READING_SIZE = 8192;

	private static String getString(final String id) {
		return Util.getString(I18N_PREFIX + id);
	}
	
	private static String getString(final String id, String arg) {
		return Util.getString(I18N_PREFIX + id, arg);
	}


	private ILabelProvider srcLabelProvider;
	private Combo srcCombo;
	private Button editCPButton;
	private TableViewer fileViewer;
	private DataFolderContentProvider fileContentProvider;
	private TableViewerColumn fileNameColumn;
	
	Text responseTypeText;

	private Map<String, Object> parameterMap;

	private ProfileManager profileManager = ProfileManager.getInstance();
	private Collection<IConnectionProfile> connectionProfiles;

	private TeiidMetadataImportInfo info;

	boolean creatingControl = false;

	boolean synchronizing = false;

	boolean processingChecks = false;

	IStatus fileParsingStatus;

	IConnectionInfoHelper connectionInfoHelper;

	Properties designerProperties;
	
	boolean controlComplete = false;
	boolean visibleCompleted = false;

	/**
	 * Constructor
	 * 
	 * @since 4.0
	 * @param info
	 *            the import info object
	 */
	public TeiidRestImportSourcePage(TeiidMetadataImportInfo info) {
		this(null, info);
	}

	/**
	 * @since 4.0
	 */
	public TeiidRestImportSourcePage(Object selection,
			TeiidMetadataImportInfo info) {
		super(TeiidMetadataImportSourcePage.class.getSimpleName(), TITLE);
		// Set page incomplete initially
		this.info = info;
		setPageComplete(false);
		setImageDescriptor(UiPlugin.getDefault().getImageDescriptor(
				Images.IMPORT_TEIID_METADATA));
		this.connectionInfoHelper = new ConnectionInfoHelper();
		this.parameterMap = new HashMap<String, Object>();
	}

	@Override
	public void createControl(Composite parent) {
		controlComplete = false;
		
        final Composite hostPanel = new Composite(parent, SWT.NONE);
        hostPanel.setLayout(new GridLayout(1, false));
        hostPanel.setLayoutData(new GridData(GridData.FILL_BOTH));
        
        // Create page            
        DefaultScrolledComposite scrolledComposite = new DefaultScrolledComposite(hostPanel);
        hostPanel.setLayout(new GridLayout(1, false));
        hostPanel.setLayoutData(new GridData(GridData.FILL_BOTH));

        final Composite mainPanel = scrolledComposite.getPanel();
        mainPanel.setLayoutData(new GridData(GridData.FILL_BOTH));
        mainPanel.setLayout(new GridLayout(1, false));

		// Add widgets to page

		createProfileGroup(mainPanel);

		createFolderContentsListGroup(mainPanel);
		
		scrolledComposite.sizeScrolledPanel();
		
		setControl(hostPanel);

		setMessage(INITIAL_MESSAGE);
		controlComplete = true;
	}

	private void createProfileGroup(Composite parent) {
		// ---------------------------------------------------------------------------
		// ----------- Connection Profile SOURCE Panel
		// ---------------------------------
		// ---------------------------------------------------------------------------
		Group profileGroup = WidgetFactory.createGroup(parent, REST_SOURCE_LABEL, SWT.NONE, 2, 3);
		profileGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		((GridData)profileGroup.getLayoutData()).widthHint = 400;

		this.srcLabelProvider = new LabelProvider() {

			@Override
			public String getText(final Object source) {
				return ((IConnectionProfile) source).getName();
			}
		};
		this.srcCombo = WidgetFactory.createCombo(profileGroup, SWT.READ_ONLY,
				GridData.FILL_HORIZONTAL, (ArrayList<IConnectionProfile>) this.connectionProfiles,
				null, // this.src,
				this.srcLabelProvider, true);
		this.srcCombo.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				profileComboSelectionChanged();
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
		
		Composite responseTypePanel = WidgetFactory.createPanel(profileGroup,SWT.NONE, GridData.FILL_HORIZONTAL, 1, 2);
		Label typeLabel = new Label(responseTypePanel, SWT.NONE);
		typeLabel.setText("Response Type"); //$NON-NLS-1$

		this.responseTypeText = new Text(responseTypePanel, SWT.BORDER | SWT.SINGLE);
		this.responseTypeText.setBackground(WidgetUtil.getReadOnlyBackgroundColor());
		this.responseTypeText.setForeground(WidgetUtil.getDarkBlueColor());
		this.responseTypeText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.responseTypeText.setEditable(false);
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
		String groupLabel = getString("folderRestContentsGroup"); //$NON-NLS-1$

		Group folderContentsGroup = WidgetFactory.createGroup(parent,
				groupLabel, SWT.FILL, 3, 3);
		GridData gd_1 = new GridData(GridData.FILL_BOTH);
		gd_1.heightHint = 180;
		folderContentsGroup.setLayoutData(gd_1);

		Label locationLabel = new Label(folderContentsGroup, SWT.NONE);
		locationLabel.setText(getString("folderLocation")); //$NON-NLS-1$

		createFileTableViewer(folderContentsGroup);
		
		Button showFileContentsButton = new Button(folderContentsGroup, SWT.PUSH);
		showFileContentsButton.setText("Show Contents"); //$NON-NLS-1$
		showFileContentsButton.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				if( info.getSourceXmlFileInfo() != null ) {
					FileUiUtils.INSTANCE.showFileContents(getShell(), info.getSourceXmlFileInfo().getDataFile(), 
							"Response Document", info.getSourceXmlFileInfo().getDataFile().getName()); //$NON-NLS-1$
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

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
		gd.horizontalSpan = 3;
		this.fileViewer.getControl().setLayoutData(gd);
		fileContentProvider = new DataFolderContentProvider();
		this.fileViewer.setContentProvider(fileContentProvider);
		this.fileViewer.setLabelProvider(new FileSystemLabelProvider());

		// Check events can occur separate from selection events.
		// In this case move the selected node.
		// Also trigger selection of node in model.
		this.fileViewer.getTable().addSelectionListener(
				new SelectionListener() {

					@Override
					public void widgetSelected(SelectionEvent e) {
						if (processingChecks) {
							return;
						}
						processingChecks = true;
						if (e.detail == SWT.CHECK) {

							TableItem tableItem = (TableItem) e.item;
							boolean wasChecked = tableItem.getChecked();

							if (tableItem.getData() instanceof File) {
								fileViewer.getTable().setSelection(
										new TableItem[] { tableItem });
								if (wasChecked) {
									for (TableItem item : fileViewer.getTable()
											.getItems()) {
										if (item != tableItem) {
											item.setChecked(false);
										}
									}
								}

								info.setDoProcessXml(
										(File) tableItem.getData(), wasChecked);
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
		if (this.info.getFileMode() == TeiidMetadataImportInfo.FILE_MODE_TEIID_XML_URL) {
			fileNameColumn.getColumn().setText("XML Data File URL"); //getString("dataFileNameColumn")); //$NON-NLS-1$
		} else {
			fileNameColumn.getColumn().setText(
					getString("xmlDataFileNameColumn")); //$NON-NLS-1$
		}
		fileNameColumn
				.setLabelProvider(new DataFileContentColumnLabelProvider());
		fileNameColumn.getColumn().pack();
	}

	void profileComboSelectionChanged() {
		boolean changed = false;
		if (this.srcCombo.getSelectionIndex() > -1) {
			String cpName = this.srcCombo.getItem(this.srcCombo
					.getSelectionIndex());
			for (IConnectionProfile profile : this.connectionProfiles) {
				if (profile.getName().equalsIgnoreCase(cpName)) {
					changed = setConnectionProfile(profile, false);
					if (changed) {
						clearFileListViewer();
						loadFileListViewer();
					}
					break;
				}
			}
		} else {
			changed = true;
			setConnectionProfile(null, false);
		}

		if (changed) {
			synchronizeUI();
			validatePage();
			this.editCPButton.setEnabled(getConnectionProfile() != null);
		}
	}

	public void setDesignerProperties(Properties properties) {
		this.designerProperties = properties;
	}

	private boolean setConnectionProfile(IConnectionProfile profile, boolean wasEdited) {
		IConnectionProfile existingProfile = info.getConnectionProfile();
		
		if (existingProfile == null){
			//If existingProfile == null continue since this is a new CP and we want to update the page.
		}else if  
		 (!wasEdited && existingProfile != null && profile != null && existingProfile.getName().equals(profile.getName())) {
			return false;
		}
		
		if (profile == null || isInvalidXmlFileProfile(profile)) {
			this.fileViewer.setInput(null);
			clearFileListViewer();
		}
		
		this.parameterMap.clear();

		this.info.setConnectionProfile(profile);
		return true;
	}

	private IConnectionProfile getConnectionProfile() {
		return this.info.getConnectionProfile();
	}

	private boolean isRestConnectionProfile() {
		IConnectionProfile profile = this.info.getConnectionProfile();
		if (profile != null) {
			return profile.getProviderId().equals(TEIID_WS_ID);
		}

		return false;
	}

	private boolean isValidProfileForPage(IConnectionProfile profile) {
		boolean isValid = false;
		if (this.info.isXmlLocalFileMode()
				&& profile.getProviderId().equalsIgnoreCase(XML_FILE_ID)) {
			isValid = true;
		} else if (this.info.isRestUrlFileMode() 
				&& profile.getProviderId().equalsIgnoreCase(TEIID_WS_ID)) {
			isValid = true;
		}
		return isValid;
	}

	private boolean isInvalidXmlFileProfile(IConnectionProfile profile) {
		// If File ConnectionProfile, make sure it references a file
		File theXmlFile = getFileForConnectionProfile(profile);
		if (theXmlFile != null && theXmlFile.exists() && !theXmlFile.isFile()) {
			return true;
		}
		return false;
	}

	private void clearFileListViewer() {
		this.info.clearXmlFileInfos();
		this.info.clearFileInfos();
		fileViewer.getTable().clearAll();
		this.info.setSourceXmlFileInfo();
	}

	private void loadFileListViewer() {
		if (getConnectionProfile() != null) {
			File theXmlFile = getFileForConnectionProfile(getConnectionProfile());
			String urlString = getUrlStringForConnectionProfile();

			if (theXmlFile != null) {
				if (theXmlFile.exists() && theXmlFile.isFile()) {
					setXmlFile(theXmlFile, false, null);
				}
			} else if (urlString != null && urlString.trim().length() > 0) {
				File xmlFile = null;
				// Clears the viewer
				// This will be the case if No XML is defined and URL
				// version exists OR if nothing is defined in CP
				fileViewer.setInput("no input"); //$NON-NLS-1$
				
				if (isRestConnectionProfile()) {
					xmlFile = getXmlFileFromRestUrl(getConnectionProfile());
				} else {
					xmlFile = getXmlFileFromUrl(urlString);
				}
//				if( this.modelsDefinitionSection.getXmlFileInfo() == null ) {
					if (xmlFile != null && xmlFile.exists()) {
						setXmlFile(xmlFile, true, urlString);
					}
//				} else if (xmlFile != null && xmlFile.exists() ) {
//					resetXmlFile(xmlFile);
//				}
			} else {
				fileViewer.setInput(null);
				MessageDialog.openError(this.getShell(),
						getString("invalidRESTConnectionProfileTitle"), //$NON-NLS-1$
						getString("invalidRESTConnectionProfileMessage")); //$NON-NLS-1$
			}
		}
	}

	private int write(final OutputStream out, final InputStream is)
			throws IOException {
		byte[] l_buffer = new byte[DEFAULT_READING_SIZE];
		int writen = 0;
		try {
			int l_nbytes = 0; // Number of bytes read
			int readLength = l_buffer.length;

			while ((l_nbytes = is.read(l_buffer, 0, readLength)) != -1) {
				out.write(l_buffer, 0, l_nbytes);
				writen += l_nbytes;
			}
			return writen;
		} finally {
			try {
				is.close();
			} finally {
				out.close();
			}
		}
	}

	private File getXmlFileFromRestUrl(IConnectionProfile profile) {
		Properties props = profile.getBaseProperties();
		String endpoint = ConnectionInfoHelper.readEndPointProperty(props);
		String username = (String) props
				.get(ICredentialsCommon.USERNAME_PROP_ID);
		String password = (String) props
				.get(ICredentialsCommon.PASSWORD_PROP_ID);
		File xmlFile = null;
		File jsonFile = null;
		FileOutputStream fos = null;
		
    	for( Object key : props.keySet() )  {
    		String keyStr = (String)key;
    		
    		if( keyStr.startsWith(Parameter.PREFIX) ||
    			keyStr.startsWith(Parameter.HEADER_PREFIX)) {
    			Parameter newParam = new Parameter(keyStr, props.getProperty((String)key));
    			parameterMap.put(newParam.getName(), newParam);
    		}
    	}
		
		String responseType = IWSProfileConstants.XML;
		if(  props.get(IWSProfileConstants.RESPONSE_TYPE_PROPERTY_KEY) != null) {
			responseType = (String)props.get(IWSProfileConstants.RESPONSE_TYPE_PROPERTY_KEY);
		}
		responseTypeText.setText(responseType);

		try {

			endpoint = getUrl(endpoint, parameterMap);
			final URL url = new URL(endpoint);
			final HttpURLConnection httpConn = (HttpURLConnection) url
					.openConnection();
			String filePath = formatPath(url);

			// TODO Validate content is XML
			// if(
			// !CONTENT_TYPE_XML.equalsIgnoreCase(httpConn.getContentType())) {
			// return null;
			// }

			if (username != null && !username.isEmpty()) {
				httpConn.setRequestProperty(
						IWSProfileConstants.AUTHORIZATION_KEY,
						"Basic " + Base64.encodeBytes((username + ':' + password).getBytes())); //$NON-NLS-1$
			}

			httpConn.setDoOutput(false);
			if (props.get(IWSProfileConstants.ACCEPT_PROPERTY_KEY) != null) {
				httpConn.setRequestProperty(
						IWSProfileConstants.ACCEPT_PROPERTY_KEY, (String) props
								.get(IWSProfileConstants.ACCEPT_PROPERTY_KEY));
			} else {
				if( responseType.equalsIgnoreCase(IWSProfileConstants.JSON) ) {
					httpConn.setRequestProperty(
							IWSProfileConstants.ACCEPT_PROPERTY_KEY,
							IWSProfileConstants.CONTENT_TYPE_JSON_VALUE);
				} else {
					httpConn.setRequestProperty(
							IWSProfileConstants.ACCEPT_PROPERTY_KEY,
							IWSProfileConstants.ACCEPT_DEFAULT_VALUE);
				}
			}

			if (props.get(IWSProfileConstants.CONTENT_TYPE_PROPERTY_KEY) != null) {
				httpConn.setRequestProperty(
						IWSProfileConstants.CONTENT_TYPE_PROPERTY_KEY,
						(String) props
								.get(IWSProfileConstants.CONTENT_TYPE_PROPERTY_KEY));
			} else {
				httpConn.setRequestProperty(
						IWSProfileConstants.CONTENT_TYPE_PROPERTY_KEY,
						IWSProfileConstants.CONTENT_TYPE_DEFAULT_VALUE);
			}

			for (Object key : props.keySet()) {
				String keyStr = (String) key;
				if (IWSProfileConstants.AUTHORIZATION_KEY
						.equalsIgnoreCase(keyStr)
						|| ICredentialsCommon.PASSWORD_PROP_ID
								.equalsIgnoreCase(keyStr)
						|| ICredentialsCommon.SECURITY_TYPE_ID
								.equalsIgnoreCase(keyStr)
						|| ICredentialsCommon.USERNAME_PROP_ID
								.equalsIgnoreCase(keyStr)
						|| IWSProfileConstants.END_POINT_URI_PROP_ID
								.equalsIgnoreCase(keyStr)
						|| IWSProfileConstants.CONTENT_TYPE_PROPERTY_KEY
								.equalsIgnoreCase(keyStr)
						|| IWSProfileConstants.ACCEPT_PROPERTY_KEY
								.equalsIgnoreCase(keyStr)
						|| IWSProfileConstants.RESPONSE_TYPE_PROPERTY_KEY
								.equalsIgnoreCase(keyStr)
						|| IWSProfileConstants.PARAMETER_MAP
								.equalsIgnoreCase(keyStr)
						|| keyStr.startsWith(Parameter.PREFIX)) {
					// do nothing;
				} else {
					httpConn.setRequestProperty(getKey(keyStr),
							getValue(props.getProperty(keyStr)));
				}
			}

			InputStream is = httpConn.getInputStream();
			if (props.get(IWSProfileConstants.RESPONSE_TYPE_PROPERTY_KEY)==null || IWSProfileConstants.XML.equals(props.get(IWSProfileConstants.RESPONSE_TYPE_PROPERTY_KEY))) {
				xmlFile = File.createTempFile(
						CoreStringUtil.createFileName(filePath), DOT_XML_LOWER);
				fos = new FileOutputStream(xmlFile);
				write(fos, is);
			} else {
				jsonFile = File.createTempFile(CoreStringUtil.createFileName(filePath), DOT_XML_LOWER);
				fos = new FileOutputStream(jsonFile);
				write(fos, is);
				xmlFile = convertJsonToXml(jsonFile);
			}

		} catch (MalformedURLException ex) {
			Util.log(ex);
			MessageDialog.openError(this.getShell(),
					getString("malformedUrlErrorTitle"), //$NON-NLS-1$
					getString("malformedUrlErrorMessage") + ex.getMessage()); //$NON-NLS-1$
		} catch (ProtocolException ex) {
			Util.log(ex);
			MessageDialog.openError(this.getShell(),
					getString("protocolErrorTitle"), //$NON-NLS-1$
					getString("protocolUrlErrorMessage") + ex.getMessage()); //$NON-NLS-1$

		} catch (IOException ex) {
			Util.log(ex);
			MessageDialog.openError(this.getShell(), getString("ioErrorTitle"), //$NON-NLS-1$
					getString("ioErrorMessage") + ex.getMessage()); //$NON-NLS-1$
		} catch (Exception ex) {
			if( ex instanceof JSONException ) { //$NON-NLS-1$
				
				String message = getString("invalidRESTResponseTypeMessage", responseType); //$NON-NLS-1$
				message+="\r\n"+ex.getLocalizedMessage();
				MessageDialog.openError(this.getShell(),
						getString("invalidRESTConnectionProfileTitle"), //$NON-NLS-1$
						message); //$NON-NLS-1$
			} else {
				MessageDialog.openError(this.getShell(),
						getString("invalidRESTConnectionProfileTitle"), //$NON-NLS-1$
						ex.getLocalizedMessage()); //$NON-NLS-1$
			}
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

	/**
	 * @param keyStr
	 * @return
	 */
	private String getKey(String keyStr) {
		if (keyStr.startsWith(Parameter.HEADER_PREFIX)) keyStr = keyStr.substring(keyStr.indexOf(":")+1);
		return keyStr;
	}
	
	/**
	 * @param value
	 * @return
	 */
	private String getValue(String value) {
		if (value.startsWith(Parameter.Type.Header.name())) value = value.substring(value.indexOf(":")+1);
		return value;
	}
	
	/**
	 * @param jsonFile
	 * @return
	 * @throws IOException 
	 * @throws JSONException 
	 */
	private File convertJsonToXml(File jsonFile) throws IOException, Exception {

		String jsonText = null;
		boolean isArray = false;
		String xml;
		try {
			jsonText = readFile(jsonFile);
		} catch (IOException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
		if (jsonText.trim().startsWith("[")) {
			isArray = true;
		}
			
		if (isArray){
			JSONArray jsonArray = new JSONArray(jsonText);
			xml = "<response>" + XML.toString(jsonArray, "response") + "</response>"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		} else{
			JSONObject jsonObject = new JSONObject(jsonText);
			xml = XML.toString(jsonObject, "response"); //$NON-NLS-1$
		}

		FileUtils.write(xml.getBytes(), jsonFile);
		return jsonFile;

	}

	private String readFile(File f) throws IOException {
		final BufferedReader r = new BufferedReader(new FileReader(f));
		final StringBuilder buf = new StringBuilder();
		String line;
		while ((line = r.readLine()) != null) {
			buf.append(line);
			buf.append("\n"); //$NON-NLS-1$
		}
		r.close();
		return buf.toString();
	}

	private File getXmlFileFromUrl(String urlString) {
		File xmlFile = null;
		URL newUrl = null;
		try {
			newUrl = URLHelper.buildURL(urlString);
		} catch (MalformedURLException e) {
			Util.log(e);
			MessageDialog
					.openError(
							this.getShell(),
							getString("malformedUrlErrorTitle"), //$NON-NLS-1$
							UiConstants.Util
									.getString(
											"malformedUrlErrorMessage", urlString, e.getMessage())); //$NON-NLS-1$
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
					xmlFile = URLHelper.createFileFromUrl(newUrl,
							CoreStringUtil.createFileName(filePath),
							DOT_XML_LOWER);
				} catch (MalformedURLException theException) {
					Util.log(theException);
				} catch (IOException theException) {
					Util.log(theException);
				}
			}
		}

		return xmlFile;
	}

	/**
	 * @return
	 */
	String getUrl(String url, Map parameterMap) {
		StringBuilder previewUrl = new StringBuilder();
		String urlText = url;
		String parameters = null;
		try {
			parameters = buildParameterString(url, parameterMap);
		} catch (UnsupportedEncodingException ex) {
			UiConstants.Util.log(ex);
		}
		previewUrl.append(urlText).append(parameters);
		return previewUrl.toString();
	}

	/**
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	private String buildParameterString(String url,
			Map<String, Parameter> parameterMap) throws UnsupportedEncodingException {

		StringBuilder parameterString = new StringBuilder();
		if (parameterMap == null)
			return parameterString.toString();

		for (String key : parameterMap.keySet()) {
			Parameter value = parameterMap.get(key);
			if (value.getType() == Parameter.Type.URI) {
				parameterString.append(url.endsWith("/") ? StringConstants.EMPTY_STRING : "/").append(value.getDefaultValue()); //$NON-NLS-1$ //$NON-NLS-2$
			}
			if (value.getType() == Parameter.Type.Query) {
				if (parameterString.length() == 0 || !parameterString.toString().contains("?")) { //$NON-NLS-1$
					parameterString.append("?"); //$NON-NLS-1$
				} else {
					parameterString.append("&"); //$NON-NLS-1$  
				}
				parameterString.append(encodeString(key)).append("=").append(encodeString(value.getDefaultValue())); //$NON-NLS-1$
			}
		}

		return parameterString.toString();
	}
	
	private String encodeString(String str) throws UnsupportedEncodingException {
		return  URLEncoder.encode(str, Charset.defaultCharset().displayName());
	}
	

	private void setXmlFile(File xmlFile, boolean isUrl, String urlString) {
		fileViewer.setInput(xmlFile);
		
		TeiidXmlFileInfo oldFileInfo = this.info.getXmlFileInfo(xmlFile);
		String oldVPName = null;
		if( oldFileInfo != null ) {
			oldVPName = oldFileInfo.getViewProcedureName();
			this.info.clearXmlFileInfos();
		} else {
			if( !this.info.getXmlFileInfos().isEmpty() ) {
				this.info.clearXmlFileInfos();
			}
		}
		
		TeiidXmlFileInfo fileInfo = new TeiidXmlFileInfo(xmlFile);
		if( oldVPName != null ) {
			fileInfo.setViewProcedureName(oldVPName);
		}
		
		fileInfo.setIsUrl(isUrl);
		if (isUrl) {
			fileInfo.setParameterMap(this.parameterMap);
			fileInfo.setXmlFileUrl(urlString);
			Properties props = getConnectionProfile().getBaseProperties();
			fileInfo.setResponseType((String)props.get(IWSProfileConstants.RESPONSE_TYPE_PROPERTY_KEY));
		}

		this.info.addXmlFileInfo(fileInfo);
		fileViewer.getTable().select(0);
		fileViewer.getTable().getItem(0).setChecked(true);
		info.setDoProcessXml(fileInfo.getDataFile(), true);
		fileParsingStatus = fileInfo.getParsingStatus();
		if (fileParsingStatus.getSeverity() == IStatus.ERROR) {
			MessageDialog.openError(this.getShell(),
					getString("parsingErrorTitle"), //$NON-NLS-1$
					fileParsingStatus.getMessage());
		}
		this.info.setSourceXmlFileInfo();
	}
	
	private void resetXmlFile(File xmlFile) {
		fileViewer.setInput(xmlFile);
		
		TeiidXmlFileInfo fileInfo = this.info.getXmlFileInfo(xmlFile);
		
		fileViewer.getTable().select(0);
		fileViewer.getTable().getItem(0).setChecked(true);
		if( fileInfo != null ) {
			info.setDoProcessXml(fileInfo.getDataFile(), true);
			fileParsingStatus = fileInfo.getParsingStatus();
			if (fileParsingStatus.getSeverity() == IStatus.ERROR) {
				MessageDialog.openError(this.getShell(),
						getString("parsingErrorTitle"), //$NON-NLS-1$
						fileParsingStatus.getMessage());
			}
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
		String filePath = newUrl.getFile();
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

	private File getFileForConnectionProfile(IConnectionProfile profile) {
		if (profile != null) {
			Properties props = profile.getBaseProperties();
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
			if (isRestConnectionProfile()) {
				String fileListValue = ConnectionInfoHelper
						.readEndPointProperty(props);
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

	void createNewConnectionProfile() {
		INewWizard wiz = null;

		if (this.info.getFileMode() == TeiidMetadataImportInfo.FILE_MODE_TEIID_XML_URL) {
			wiz = new NewTeiidFilteredCPWizard(XML_URL_FILE_ID);
		} else if (this.info.getFileMode() == TeiidMetadataImportInfo.FILE_MODE_TEIID_XML_FILE) {
			wiz = new NewTeiidFilteredCPWizard(XML_FILE_ID);
		} else {
			wiz = new NewTeiidFilteredCPWizard(TEIID_WS_ID);
		}
		// We need to create a Dialog to ask user to choose either a XML File
		// URL CP or a WS REST CP
		// TeiidXmlConnectionOptionsDialog dialog = new
		// TeiidXmlConnectionOptionsDialog(Display.getCurrent().getActiveShell());

		WizardDialog wizardDialog = new WizardDialog(Display.getCurrent()
				.getActiveShell(), wiz);
		wizardDialog.setBlockOnOpen(true);

		CPListener listener = new CPListener();
		ProfileManager.getInstance().addProfileListener(listener);
		
		if (wizardDialog.open() == Window.OK) {

			refreshConnectionProfiles();

			resetCPComboItems();
			setConnectionProfile(listener.getChangedProfile(), true);

			selectProfile(listener.getChangedProfile());
			
			profileComboSelectionChanged();
			loadFileListViewer();
			
			synchronizeUI();
			validatePage();
			this.editCPButton.setEnabled(getConnectionProfile() != null);
		}
		
		ProfileManager.getInstance().removeProfileListener(listener);
	}

	void selectProfile(IConnectionProfile profile) {
		int index = 0;
		for (String item : this.srcCombo.getItems()) {
			if (item != null && item.equalsIgnoreCase(profile.getName())) {
				this.srcCombo.select(index);
				profileComboSelectionChanged();
				break;
			}
			index++;
		}
	}

	void resetCPComboItems() {
		if (this.srcCombo != null) {
			WidgetUtil.setComboItems(this.srcCombo, this.connectionProfiles,
					this.srcLabelProvider, true);
		}
	}

	void editConnectionProfile() {
		if (getConnectionProfile() != null) {
			IConnectionProfile currentProfile = getConnectionProfile();
			EditConnectionProfileAction action = new EditConnectionProfileAction(
					getShell(), currentProfile);

			CPListener listener = new CPListener();
			ProfileManager.getInstance().addProfileListener(listener);

			action.run();

			// Update the Combo Box
			if (action.wasFinished()) {
				setConnectionProfile(listener.getChangedProfile(), true);
				this.refreshConnectionProfiles();
				WidgetUtil.setComboItems(this.srcCombo, this.connectionProfiles, this.srcLabelProvider, true);

				WidgetUtil.setComboText(this.srcCombo, getConnectionProfile(), this.srcLabelProvider);

				// Need to clear the file info
				info.clearXmlFileInfos();
				
				info.setSourceXmlFileInfo();
				
				setConnectionProfile(null, false);
				
				selectConnectionProfile(currentProfile.getName());

				ProfileManager.getInstance().removeProfileListener(listener);

				//profileComboSelectionChanged();
			}
			
			ProfileManager.getInstance().removeProfileListener(listener);
		}
	}





	void synchronizeUI() {
		synchronizing = true;

		String fileName = EMPTY_STRING;

 		for (TeiidXmlFileInfo fileInfo : this.info.getXmlFileInfos()) {
			if (fileInfo.doProcess()) {
				fileName = fileInfo.getDataFile().getName();
				break;
			}
		}
//		this.selectedFileText.setText(fileName);

		synchronizing = false;
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
			profileComboSelectionChanged();
		}
	}

	private void setProfileFromProperties() {
		// Check for Connection Profile in properties
		if (this.designerProperties != null) {
			String profileName = DesignerPropertiesUtil
					.getConnectionProfileName(this.designerProperties);
			if (profileName != null && !profileName.isEmpty()) {
				// Select profile
				selectConnectionProfile(profileName);
			}
		}
	}

	private void refreshConnectionProfiles() {
		this.connectionProfiles = new ArrayList<IConnectionProfile>();

		final IConnectionProfile[] tempProfiles = profileManager
				.getProfilesByCategory(IXmlProfileConstants.TEIID_CATEGORY);
		for (final IConnectionProfile profile : tempProfiles) {
			if (this.info.isXmlLocalFileMode()
					&& profile.getProviderId().equalsIgnoreCase(XML_FILE_ID)) {
				connectionProfiles.add(profile);
			} else if (this.info.isXmlUrlFileMode()
					&& profile.getProviderId()
							.equalsIgnoreCase(XML_URL_FILE_ID)) {
				connectionProfiles.add(profile);
			} else if (this.info.isRestUrlFileMode()
					&& profile.getProviderId().equalsIgnoreCase(TEIID_WS_ID)) {
				connectionProfiles.add(profile);
			}
		}

		return;
	}

	private boolean validatePage() {
		IConnectionProfile connProfile = getConnectionProfile();
		if (connProfile == null) {
			setThisPageComplete(getString("noRestConnectionProfileSelected"), ERROR);//$NON-NLS-1$
			return false;
		}


		// Check for model file selected
		boolean fileSelected = false;
		for (TableItem item : this.fileViewer.getTable().getItems()) {
			if (item.getChecked()) {
				fileSelected = true;
				break;
			}
		}
		if (!fileSelected) {
			setThisPageComplete(getString("noRestResponseFilesSelected"), ERROR);//$NON-NLS-1$
			return false;
		}

		if (fileParsingStatus.getSeverity() == IStatus.ERROR) {
			setThisPageComplete(fileParsingStatus.getMessage(), ERROR);
			return false;
		}

		setThisPageComplete(EMPTY_STRING, NONE);

		return true;
	}


	protected void setThisPageComplete(String message, int severity) {
		WizardUtil.setPageComplete(this, message, severity);
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

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		
		

		if (visible) {
			this.setTitle(REST_TITLE);
			this.fileNameColumn.getColumn().setText(getString("restDataFileNameColumn")); //$NON-NLS-1$

			// If current profile is invalid for this page, it is reset.
			// this may happen if user toggle between local and remote xml...
			IConnectionProfile currentProfile = getConnectionProfile();
			if (currentProfile == null) {
				this.fileViewer.setInput(null);
				this.info.setViewModelName(null);
				this.info.setSourceModelName(null);
			} else if (!isValidProfileForPage(currentProfile)) {
				setConnectionProfile(null, false);
				this.fileViewer.setInput(null);
				this.info.setViewModelName(null);
				this.info.setSourceModelName(null);
			} else if (isInvalidXmlFileProfile(currentProfile)) {
				this.fileViewer.setInput(null);
				this.info.setViewModelName(null);
				this.info.setSourceModelName(null);
			}

			refreshConnectionProfiles();

			resetCPComboItems();

			loadFileListViewer();

			fileNameColumn.getColumn().pack();

			synchronizeUI();

			setProfileFromProperties();
			validatePage();
			
			visibleCompleted = true;
		}
	}

}