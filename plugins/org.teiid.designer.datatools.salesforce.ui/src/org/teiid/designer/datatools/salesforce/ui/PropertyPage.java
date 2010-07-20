/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.datatools.salesforce.ui;

import java.util.Properties;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.ui.wizards.ProfileDetailsPropertyPage;
import org.eclipse.datatools.help.ContextProviderDelegate;
import org.eclipse.help.IContext;
import org.eclipse.help.IContextProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.teiid.designer.datatools.salesforce.ISalesForceProfileConstants;

public class PropertyPage extends ProfileDetailsPropertyPage implements IContextProvider {

    private ContextProviderDelegate contextProviderDelegate = new ContextProviderDelegate(
                                                                                          Activator.getDefault().getBundle().getSymbolicName());
    private Composite scrolled;
    private Label usernameLabel;
    private Text usernameText;
    private Label passwordLabel;
    private Text passwordText;
    private Label urlLabel;
    private Text urlText;

    public PropertyPage() {
        super();
    }

    @Override
    public IContext getContext( Object target ) {
        return contextProviderDelegate.getContext(target);
    }

    @Override
    public int getContextChangeMask() {
        return contextProviderDelegate.getContextChangeMask();
    }

    @Override
    public String getSearchExpression( Object target ) {
        return contextProviderDelegate.getSearchExpression(target);
    }

    @Override
    protected void createCustomContents( Composite parent ) {
        GridData gd;

        Group group = new Group(parent, SWT.BORDER);
        group.setText(Messages.getString("Common.Properties.Label")); //$NON-NLS-1$
        FillLayout fl = new FillLayout();
        fl.type = SWT.HORIZONTAL;
        group.setLayout(new FillLayout());

        scrolled = new Composite(group, SWT.NONE);
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        scrolled.setLayout(gridLayout);

        usernameLabel = new Label(scrolled, SWT.NONE);
        usernameLabel.setText(Messages.getString("Common.Username.Label")); //$NON-NLS-1$
        usernameLabel.setToolTipText(Messages.getString("Common.Username.ToolTip")); //$NON-NLS-1$
        gd = new GridData();
        gd.verticalAlignment = GridData.BEGINNING;
        usernameLabel.setLayoutData(gd);

        usernameText = new Text(scrolled, SWT.SINGLE | SWT.BORDER);
        usernameText.setToolTipText(Messages.getString("Common.Username.ToolTip")); //$NON-NLS-1$
        gd = new GridData();
        gd.horizontalAlignment = GridData.FILL;
        gd.verticalAlignment = GridData.BEGINNING;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalSpan = 1;
        usernameText.setLayoutData(gd);

        passwordLabel = new Label(scrolled, SWT.NONE);
        passwordLabel.setText(Messages.getString("Common.Password.Label")); //$NON-NLS-1$
        passwordLabel.setToolTipText(Messages.getString("Common.Password.ToolTip")); //$NON-NLS-1$
        gd = new GridData();
        gd.verticalAlignment = GridData.BEGINNING;
        passwordLabel.setLayoutData(gd);

        passwordText = new Text(scrolled, SWT.SINGLE | SWT.BORDER | SWT.PASSWORD);
        passwordText.setToolTipText(Messages.getString("Common.Password.ToolTip")); //$NON-NLS-1$
        gd = new GridData();
        gd.horizontalAlignment = GridData.FILL;
        gd.verticalAlignment = GridData.BEGINNING;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalSpan = 1;
        passwordText.setLayoutData(gd);

        urlLabel = new Label(scrolled, SWT.NONE);
        urlLabel.setText(Messages.getString("Common.URL.Label")); //$NON-NLS-1$
        urlLabel.setToolTipText(Messages.getString("Common.URL.ToolTip")); //$NON-NLS-1$
        gd = new GridData();
        gd.verticalAlignment = GridData.BEGINNING;
        urlLabel.setLayoutData(gd);

        urlText = new Text(scrolled, SWT.SINGLE | SWT.BORDER);
        urlText.setToolTipText(Messages.getString("Common.URL.ToolTip")); //$NON-NLS-1$
        gd = new GridData();
        gd.horizontalAlignment = GridData.FILL;
        gd.verticalAlignment = GridData.BEGINNING;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalSpan = 1;
        urlText.setLayoutData(gd);

        initControls();
        addlisteners();
    }

    /**
     * 
     */
    private void addlisteners() {
        usernameText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText( ModifyEvent e ) {
                validate();
            }
        });

        passwordText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText( ModifyEvent e ) {
                validate();
            }
        });

        urlText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText( ModifyEvent e ) {
                validate();
            }
        });

    }

    protected void validate() {
        String errorMessage = null;
        boolean valid = true;
        if (null == usernameText.getText() || usernameText.getText().isEmpty()) {
            errorMessage = Messages.getString("Common.Username.Error.Message"); //$NON-NLS-1$
            valid = false;
        }
        if (null == passwordText.getText() || passwordText.getText().isEmpty()) {
            errorMessage = Messages.getString("Common.Password.Error.Message"); //$NON-NLS-1$
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
        if (null != props.get(ISalesForceProfileConstants.USERNAME_PROP_ID)) {
            usernameText.setText((String)props.get(ISalesForceProfileConstants.USERNAME_PROP_ID));
        }
        if (null != props.get(ISalesForceProfileConstants.PASSWORD_PROP_ID)) {
            passwordText.setText((String)props.get(ISalesForceProfileConstants.PASSWORD_PROP_ID));
        }
        if (null != props.get(ISalesForceProfileConstants.URL_PROP_ID)) {
            urlText.setText((String)props.get(ISalesForceProfileConstants.URL_PROP_ID));
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
        result.setProperty(ISalesForceProfileConstants.USERNAME_PROP_ID, usernameText.getText());
        result.setProperty(ISalesForceProfileConstants.PASSWORD_PROP_ID, passwordText.getText());
        if (null != urlText.getText() && !urlText.getText().isEmpty()) {
            result.setProperty(ISalesForceProfileConstants.URL_PROP_ID, urlText.getText());
        }
        return result;
    }

}
