/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.datatools.profiles.ws;

import java.util.Properties;

import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.ui.wizards.ProfileDetailsPropertyPage;
import org.eclipse.datatools.help.ContextProviderDelegate;
import org.eclipse.help.IContext;
import org.eclipse.help.IContextProvider;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.teiid.designer.datatools.ui.DatatoolsUiConstants;
import org.teiid.designer.datatools.ui.DatatoolsUiPlugin;

import com.metamatrix.ui.ICredentialsCommon.SecurityType;
import com.metamatrix.ui.internal.widget.CredentialsComposite;

public class WSSoapPropertyPage extends ProfileDetailsPropertyPage implements IContextProvider, DatatoolsUiConstants {

	private ContextProviderDelegate contextProviderDelegate = new ContextProviderDelegate(DatatoolsUiPlugin
		.getDefault().getBundle().getSymbolicName());
	private Composite scrolled;
	
	private Label urlLabel;
	
	private Text urlText;

 	private CredentialsComposite credentialsComposite;

	public WSSoapPropertyPage() {
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
		gridLayout.numColumns = 2;
		scrolled.setLayout(gridLayout);

		urlLabel = new Label(scrolled, SWT.NONE);
		urlLabel.setText(UTIL.getString("Common.URL.Label")); //$NON-NLS-1$
		urlLabel.setToolTipText(UTIL.getString("Common.URL.ToolTip")); //$NON-NLS-1$
		gd = new GridData();
		gd.verticalAlignment = GridData.BEGINNING;
		urlLabel.setLayoutData(gd);

		urlText = new Text(scrolled, SWT.SINGLE | SWT.BORDER);
		urlText.setToolTipText(UTIL.getString("Common.URL.ToolTip")); //$NON-NLS-1$
		gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		gd.verticalAlignment = GridData.BEGINNING;
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalSpan = 1;
		urlText.setLayoutData(gd);

 		Label spacerLabel = new Label(scrolled, SWT.NONE);
 		spacerLabel.setVisible(false);
 		GridDataFactory.swtDefaults().grab(false, false).applyTo(spacerLabel);
        
 		credentialsComposite = new CredentialsComposite(scrolled, SWT.BORDER);
 		gd = new GridData(GridData.FILL_HORIZONTAL);
 		credentialsComposite.setLayoutData(gd);
        
		initControls();
		addlisteners();
	}

	private void addlisteners() {

 		Listener listener = new Listener() {

			@Override
			public void handleEvent(Event event) {
				validate();
			}
		};
		
 		urlText.addListener(SWT.Modify, listener);
 		credentialsComposite.addSecurityOptionListener(SWT.Modify, listener);
 		credentialsComposite.addUserNameListener(SWT.Modify, listener);
 		credentialsComposite.addPasswordListener(SWT.Modify, listener);
	}

	protected void validate() {
		String errorMessage = null;
		boolean valid = true;
		if (null == urlText.getText() || urlText.getText().isEmpty()) {
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

		if (null != props.get(IWSProfileConstants.WSDL_URI_PROP_ID)) {
			urlText.setText((String) props.get(IWSProfileConstants.WSDL_URI_PROP_ID));
		}
		
 		if (null != props.get(IWSProfileConstants.SECURITY_TYPE_ID)) {
 			credentialsComposite.setSecurityOption((String) props.get(IWSProfileConstants.SECURITY_TYPE_ID));
 		}
		
 		if (null != props.get(IWSProfileConstants.USERNAME_PROP_ID)) {
 	 		credentialsComposite.setUserName((String) props.get(IWSProfileConstants.USERNAME_PROP_ID));
 	 	}
		
		if (null != props.get(IWSProfileConstants.PASSWORD_PROP_ID)) {
	 		credentialsComposite.setPassword((String) props.get(IWSProfileConstants.PASSWORD_PROP_ID));
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
		result.setProperty(IWSProfileConstants.WSDL_URI_PROP_ID, urlText.getText());
		result.setProperty(IWSProfileConstants.SECURITY_TYPE_ID, credentialsComposite.getSecurityOption().name());
		result.setProperty(IWSProfileConstants.USERNAME_PROP_ID, credentialsComposite.getUserName());
		result.setProperty(IWSProfileConstants.PASSWORD_PROP_ID, credentialsComposite.getPassword());
		return result;
	}

}