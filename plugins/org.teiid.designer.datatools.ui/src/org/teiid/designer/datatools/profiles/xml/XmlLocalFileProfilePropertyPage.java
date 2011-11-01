/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.datatools.profiles.xml;

import java.io.File;
import java.util.Properties;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.ui.wizards.ProfileDetailsPropertyPage;
import org.eclipse.datatools.help.ContextProviderDelegate;
import org.eclipse.help.IContext;
import org.eclipse.help.IContextProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.teiid.designer.datatools.ui.DatatoolsUiConstants;
import org.teiid.designer.datatools.ui.DatatoolsUiPlugin;

import com.metamatrix.core.util.StringUtilities;

public class XmlLocalFileProfilePropertyPage extends ProfileDetailsPropertyPage
		implements IContextProvider, DatatoolsUiConstants {
	
	private static final String FILE_IMPORT_MASK = "*.xml;";//$NON-NLS-1$

	private ContextProviderDelegate contextProviderDelegate = new ContextProviderDelegate(
			DatatoolsUiPlugin.getDefault().getBundle().getSymbolicName());
	private Composite scrolled;
	
	private Label localFilePathLabel;
	private Text localFilePathText;
	
	private Button fileSystemSourceBrowseButton;


	public XmlLocalFileProfilePropertyPage() {
		super();
	}

	@Override
	public IContext getContext(Object target) {
		return contextProviderDelegate.getContext(target);
	}

	@Override
	public int getContextChangeMask() {
		return contextProviderDelegate.getContextChangeMask();
	}

	@Override
	public String getSearchExpression(Object target) {
		return contextProviderDelegate.getSearchExpression(target);
	}

	@Override
	protected Control createContents(Composite parent) {
		Control result = super.createContents(parent);
		this.setPingButtonEnabled(false);
		this.setPingButtonVisible(false);
		return result;
	}

	@Override
	protected void createCustomContents(Composite parent) {
		GridData gd;

		Group group = new Group(parent, SWT.BORDER);
		group.setText(UTIL.getString("Common.Properties.Label")); //$NON-NLS-1$
		FillLayout fl = new FillLayout();
		fl.type = SWT.HORIZONTAL;
		group.setLayout(new FillLayout());

		scrolled = new Composite(group, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		scrolled.setLayout(gridLayout);

		localFilePathLabel = new Label(scrolled, SWT.NONE);
		localFilePathLabel.setText(UTIL.getString("Common.URL.Label")); //$NON-NLS-1$
		localFilePathLabel.setToolTipText(UTIL.getString("Common.URL.ToolTip")); //$NON-NLS-1$
		gd = new GridData();
		gd.verticalAlignment = GridData.BEGINNING;
		localFilePathLabel.setLayoutData(gd);

		localFilePathText = new Text(scrolled, SWT.SINGLE | SWT.BORDER);
		localFilePathText.setToolTipText(UTIL.getString("Common.URL.ToolTip")); //$NON-NLS-1$
		gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		gd.verticalAlignment = GridData.BEGINNING;
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalSpan = 1;
		localFilePathText.setLayoutData(gd);

		fileSystemSourceBrowseButton = new Button(scrolled, SWT.PUSH);
        fileSystemSourceBrowseButton.setText(UTIL.getString("Common.BROWSE_BUTTON_LBL_UI_")); //$NON-NLS-1$
        fileSystemSourceBrowseButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
        
		initControls();
		addlisteners();
	}

	/**
* 
*/
	private void addlisteners() {

		localFilePathText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				validate();
			}
		});

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
        dialog.setFilterExtensions(new String[] {FILE_IMPORT_MASK});

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

	protected void validate() {
		String errorMessage = null;
		boolean valid = true;
		if (null == localFilePathText.getText() || localFilePathText.getText().isEmpty()) {
			errorMessage = UTIL.getString("Common.URL.Error.Message"); //$NON-NLS-1$
			valid = false;
		}

		setErrorMessage(errorMessage);
		setValid(valid);

	}

	/**
	 * 
	 */
	private void initControls() {
		IConnectionProfile profile = getConnectionProfile();
		Properties props = profile.getBaseProperties();
		if (null != props.get(IXmlProfileConstants.LOCAL_FILE_PATH_PROP_ID)) {
			localFilePathText.setText((String) props.get(IXmlProfileConstants.LOCAL_FILE_PATH_PROP_ID));
		}

	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.datatools.connectivity.ui.wizards.ProfileDetailsPropertyPage#collectProperties()
	 */
	@Override
	protected Properties collectProperties() {
		Properties result = super.collectProperties();
		if (null == result) {
			result = new Properties();
		}
		String fileUrl = localFilePathText.getText();
		result.setProperty(IXmlProfileConstants.LOCAL_FILE_PATH_PROP_ID, fileUrl);
        IPath fullPath = new Path(fileUrl);
		String directoryUrl = fullPath.removeLastSegments(1).toString();
		result.setProperty(IXmlProfileConstants.TEIID_PARENT_DIRECTORY_KEY, directoryUrl);

		return result;
	}
}
