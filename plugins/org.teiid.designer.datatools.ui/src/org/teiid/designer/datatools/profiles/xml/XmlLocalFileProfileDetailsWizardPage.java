/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.datatools.profiles.xml;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Properties;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.datatools.connectivity.ui.wizards.ConnectionProfileDetailsPage;
import org.eclipse.datatools.connectivity.ui.wizards.NewConnectionProfileWizard;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.teiid.designer.core.util.StringUtilities;
import org.teiid.designer.datatools.ui.DatatoolsUiConstants;
import org.teiid.designer.ui.common.util.WidgetFactory;


public class XmlLocalFileProfileDetailsWizardPage  extends ConnectionProfileDetailsPage
		implements Listener, DatatoolsUiConstants {
	
    private Composite scrolled;

    private Label profileLabel;
    private CLabel profileText;
    private Label descriptionLabel;
    private Text descriptionText;
    private Label localFileNameLabel;
    private Text localFilePathText;
	
	private Button fileSystemSourceBrowseButton;
	
    /**
     * @param wizardPageName
     */
    public XmlLocalFileProfileDetailsWizardPage( String pageName ) {
        super(pageName, UTIL.getString("XmlLocalFileProfileDetailsWizardPage.Name"), //$NON-NLS-1$
              AbstractUIPlugin.imageDescriptorFromPlugin(DatatoolsUiConstants.PLUGIN_ID, "icons/ldap.gif")); //$NON-NLS-1$
    }

	@Override
	public void createCustomControl(Composite parent) {
        GridData gd;

        Group group = new Group(parent, SWT.BORDER);
        group.setText(UTIL.getString("Common.Properties.Label")); //$NON-NLS-1$
        FillLayout fillLayout = new FillLayout();
        fillLayout.marginHeight = 10;
        group.setLayout(fillLayout);
        group.setFont(JFaceResources.getBannerFont());

        scrolled = new Composite(group, SWT.SCROLL_PAGE);
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 3;
        scrolled.setLayout(gridLayout);

        profileLabel = new Label(scrolled, SWT.NONE);
        profileLabel.setText(UTIL.getString("Common.Profile.Label")); //$NON-NLS-1$
        
        profileText = WidgetFactory.createLabel(scrolled, SWT.SINGLE | SWT.BORDER);
        gd = new GridData();
        gd.horizontalSpan = 2;
        profileText.setLayoutData(gd);
        profileText.setText(((XmlLocalFileConnectionProfileWizard)getWizard()).getProfileName());

        descriptionLabel = new Label(scrolled, SWT.NONE);
        descriptionLabel.setText(UTIL.getString("Common.Description.Label")); //$NON-NLS-1$
        gd = new GridData();
        descriptionLabel.setLayoutData(gd);

        descriptionText = WidgetFactory.createTextBox(scrolled, SWT.SINGLE | SWT.BORDER | SWT.READ_ONLY, GridData.FILL);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalSpan = 2;
        descriptionText.setLayoutData(gd);
        String description = ((XmlLocalFileConnectionProfileWizard)getWizard()).getProfileDescription();
        descriptionText.setText(description);
        descriptionText.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));

        localFileNameLabel = new Label(scrolled, SWT.NONE);
        localFileNameLabel.setText(UTIL.getString("Common.FileName")); //$NON-NLS-1$
        gd = new GridData();
        localFileNameLabel.setLayoutData(gd);

        localFilePathText = new Text(scrolled, SWT.SINGLE | SWT.BORDER);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        localFilePathText.setLayoutData(gd);
        
		fileSystemSourceBrowseButton = new Button(scrolled, SWT.PUSH);
        fileSystemSourceBrowseButton.setText(UTIL.getString("Common.BROWSE_BUTTON_LBL_UI_")); //$NON-NLS-1$
        fileSystemSourceBrowseButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
        
		// Add widgets to page
		Group descriptionGroup = WidgetFactory.createGroup(scrolled, UTIL.getString("Common.Description"), GridData.FILL_HORIZONTAL, 3); //$NON-NLS-1$

        Text descriptionText = new Text(descriptionGroup,  SWT.WRAP | SWT.READ_ONLY);
        gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
        gd.heightHint = 150;
        gd.widthHint = 300;
        descriptionText.setLayoutData(gd);
        descriptionText.setText(UTIL.getString("XmlLocalFileProfileDetailsWizardPage.descriptionMessage")); //$NON-NLS-1$
        descriptionText.setBackground(scrolled.getBackground());
        descriptionText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
        
        setPingButtonVisible(false);
        setPingButtonEnabled(false);
        setAutoConnectOnFinishDefault(false);
        setCreateAutoConnectControls(false);
        setShowAutoConnect(false);
        setShowAutoConnectOnFinish(false);
        setPageComplete(false);
        addListeners();

	}
	
    /**
     * 
     */
    private void addListeners() {
        localFilePathText.addListener(SWT.Modify, this);
        

		fileSystemSourceBrowseButton.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleFileSystemSourceBrowseButtonPressed();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// /NO OP
			}
		});
    }
    
    /**
     * Open an appropriate source browser so that the user can specify a source to import from
     */
    protected void handleFileSystemSourceBrowseButtonPressed() {
        String selectedFilePath = null;

        final FileDialog dialog = new FileDialog(localFilePathText.getShell(), SWT.OPEN);

        final String currentSourceString = localFilePathText.getText();
        if (currentSourceString != null) {
            final int lastSeparatorIndex = currentSourceString.lastIndexOf(File.separator);
            if (lastSeparatorIndex != -1) dialog.setFilterPath(currentSourceString.substring(0, lastSeparatorIndex));
        }
        selectedFilePath = dialog.open();

        if (selectedFilePath != null) {
            if (!selectedFilePath.equals(localFilePathText.getText())) {
            	localFilePathText.setText(selectedFilePath);
            }
        } else {
        	localFilePathText.setText(StringUtilities.EMPTY_STRING);
        }
    }
    
    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
     */
    @Override
    public void handleEvent( Event event ) {

        if (event.widget == localFilePathText) {
            Properties properties = ((NewConnectionProfileWizard)getWizard()).getProfileProperties();
            String fileUrl = localFilePathText.getText();
            properties.setProperty(IXmlProfileConstants.LOCAL_FILE_PATH_PROP_ID, localFilePathText.getText());
            IPath fullPath = new Path(fileUrl);
			String directoryUrl = fullPath.removeLastSegments(1).toString();
            properties.setProperty(IXmlProfileConstants.TEIID_PARENT_DIRECTORY_KEY, directoryUrl);
        }

        updateState();
    }
    
    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.datatools.connectivity.ui.wizards.ConnectionProfileDetailsPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public void createControl( Composite parent ) {
        super.createControl(parent);
        updateState();
    }
    
    void updateState() {
        setPingButtonVisible(false);
        setPingButtonEnabled(false);

        profileText.setText(((NewConnectionProfileWizard)getWizard()).getProfileName());
        descriptionText.setText(((NewConnectionProfileWizard)getWizard()).getProfileDescription());

        Properties properties = ((NewConnectionProfileWizard)getWizard()).getProfileProperties();
        if (null == properties.get(IXmlProfileConstants.LOCAL_FILE_PATH_PROP_ID)
                || properties.get(IXmlProfileConstants.LOCAL_FILE_PATH_PROP_ID).toString().isEmpty()) {
                setErrorMessage(UTIL.getString("Common.File.Error.Empty.Message")); //$NON-NLS-1$
                return;
        }
        setErrorMessage(null);

		File localFile = new File(properties.get(IXmlProfileConstants.LOCAL_FILE_PATH_PROP_ID).toString());

		if( !localFile.exists() ) {
			setErrorMessage(UTIL.getString("Common.File.Error.DoesNotExist.Message", localFile)); //$NON-NLS-1$
			return;
		}
        
        // Check to see if URL is a parseable xml file, regardless of extension
        String urlString = properties.get(IXmlProfileConstants.LOCAL_FILE_PATH_PROP_ID).toString();
        
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            File xmlFile = new File(urlString);
            dBuilder.parse(new FileInputStream(xmlFile));
        } catch (Exception ex) {
            setErrorMessage(UTIL.getString("XmlLocalProfileDetailsWizardPage.InvalidXml.Message", urlString, ex.getMessage())); //$NON-NLS-1$
            return;
        }
        
        setErrorMessage(null);
        setPageComplete(true);
        setMessage(UTIL.getString("Click.Next.or.Finish")); //$NON-NLS-1$

    }
    
    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.wizard.WizardPage#canFlipToNextPage()
     */
    @Override
    public boolean canFlipToNextPage() {
        return internalComplete(super.canFlipToNextPage());
    }

	/**
	 * @param complete
	 * @return
	 */
	private boolean internalComplete(boolean complete) {
		Properties properties = ((NewConnectionProfileWizard) getWizard()).getProfileProperties();
		if (complete && (null == properties.get(IXmlProfileConstants.LOCAL_FILE_PATH_PROP_ID) || 
				properties.get(IXmlProfileConstants.LOCAL_FILE_PATH_PROP_ID).toString().isEmpty())) {
			complete = false;
		}

		return complete;
	}
	
    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.datatools.connectivity.internal.ui.wizards.BaseWizardPage#getSummaryData()
     */
    @Override
    public List getSummaryData() {
        List result = super.getSummaryData();
        result.add(new String[] {UTIL.getString("Common.URL.Label"), localFilePathText.getText()}); //$NON-NLS-1$
        return result;
    }
}
