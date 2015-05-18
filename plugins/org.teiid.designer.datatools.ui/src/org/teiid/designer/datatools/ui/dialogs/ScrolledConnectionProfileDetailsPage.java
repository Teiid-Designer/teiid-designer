/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.datatools.ui.dialogs;

import org.eclipse.datatools.connectivity.internal.ui.ConnectivityUIPlugin;
import org.eclipse.datatools.connectivity.internal.ui.IHelpConstants;
import org.eclipse.datatools.connectivity.ui.Messages;
import org.eclipse.datatools.connectivity.ui.wizards.ConnectionProfileDetailsPage;
import org.eclipse.datatools.connectivity.ui.wizards.NewConnectionProfileWizard;
import org.eclipse.datatools.help.HelpUtil;
import org.eclipse.help.IContextProvider;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.teiid.designer.ui.common.widget.DefaultScrolledComposite;

/**
 * Extending <code>ConnectionProfileDetailsPage</code> so we can wrap the page in a scrolled composite. This provides
 * profile pages the ability to build a larger page for necessary content and still allow for editing on lower resolution
 * displays
 * 
 * @author blafond
 *
 */
public abstract class ScrolledConnectionProfileDetailsPage extends ConnectionProfileDetailsPage implements IContextProvider {

	
	private boolean defaultAutoConnectOnFinishFlag = true;

	private Button autoConnectOnFinishButton = null;
	private Button autoConnectOnStartupButton = null;

	private boolean _showAutoConnect = true;
	private boolean _showAutoConnectOnFinish = true;
	private boolean _showPing = true;

	/**
	 * @param name
	 */
	public ScrolledConnectionProfileDetailsPage(String name) {
		super(name);
	}

	/**
	 * @param pageName
	 * @param title
	 * @param titleImage
	 */
	public ScrolledConnectionProfileDetailsPage(String pageName, String title,
										ImageDescriptor titleImage) {
		super(pageName, title, titleImage);
	}

	public void createControl(Composite parent) {
        final Composite hostPanel = new Composite(parent, SWT.NONE);
        hostPanel.setLayout(new GridLayout(1, false));
        hostPanel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        
        // Create page            
        DefaultScrolledComposite scrolledComposite = new DefaultScrolledComposite(hostPanel, SWT.H_SCROLL | SWT.V_SCROLL);
    	scrolledComposite.setExpandHorizontal(true);
    	scrolledComposite.setExpandVertical(true);
        GridLayoutFactory.fillDefaults().equalWidth(false).applyTo(scrolledComposite);
        GridDataFactory.fillDefaults().grab(true,  false);

        final Composite mainPanel = scrolledComposite.getPanel(); //new Composite(scrolledComposite, SWT.NONE);
        mainPanel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        mainPanel.setLayout(new GridLayout(2, false));
        ((GridData)mainPanel.getLayoutData()).minimumWidth = 400;

		// Client shouldn't call setControl again.
		setControl(hostPanel);
		
		// setting help now rather than at the end so that 
		// extenders can override with different context IDs in their
		// custom UI
		getShell().setData( HelpUtil.CONTEXT_PROVIDER_KEY, this);
		HelpUtil.setHelp( getControl(), HelpUtil.getContextId(IHelpConstants.CONTEXT_ID_NEW_CONNECTION_PROFILE_PAGE, ConnectivityUIPlugin.getDefault().getBundle().getSymbolicName()));

		final Composite composite = new Composite(mainPanel, SWT.NONE);
		FillLayout flayout = new FillLayout();
		flayout.marginHeight = 0;
		flayout.marginWidth = 0;
		composite.setLayout(flayout);
        GridData compositeGD = new GridData(GridData.FILL_BOTH);
        compositeGD.horizontalSpan = 2;
		composite.setLayoutData(compositeGD);

		createCustomControl(composite);
		
		
		if (_showAutoConnectOnFinish) {
			autoConnectOnFinishButton = new Button(mainPanel, SWT.CHECK);
			autoConnectOnFinishButton.setText(Messages.ConnectionProfileDetailsPage_Autoconnect_finish);
			autoConnectOnFinishButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));
			autoConnectOnFinishButton.addSelectionListener(new SelectionListener() {
	
				public void widgetDefaultSelected(SelectionEvent e) {
					ScrolledConnectionProfileDetailsPage.this.setAutoConnectFinish(
							ScrolledConnectionProfileDetailsPage.this.autoConnectOnFinishButton.getSelection());
				}
	
				public void widgetSelected(SelectionEvent e) {
					widgetDefaultSelected(e);
				}
			});
			autoConnectOnFinishButton.setSelection(defaultAutoConnectOnFinishFlag);
		}

		if (_showPing) {
			btnPing = new Button(mainPanel, SWT.NONE);
			btnPing.addSelectionListener(new SelectionAdapter() {
	
				public void widgetSelected(SelectionEvent e) {
					testConnection();
				}
			});

			GridData pingGD = new GridData(GridData.HORIZONTAL_ALIGN_END | GridData.FILL_HORIZONTAL);
			if (!_showAutoConnectOnFinish)
				pingGD.horizontalSpan = 2;
			btnPing.setLayoutData(pingGD);
			btnPing.setText(ConnectivityUIPlugin.getDefault().getResourceString(
					"ConnectionProfileDetailsPage.Button.TestConnection")); //$NON-NLS-1$

		}

		if (_showAutoConnect) {
			autoConnectOnStartupButton = new Button(mainPanel, SWT.CHECK);
			autoConnectOnStartupButton.setText(Messages.ConnectionProfileDetailsPage_Autoconnect_startup);
			GridData acStartupGD = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
			if (!_showPing)
				acStartupGD.horizontalSpan = 2;
			autoConnectOnStartupButton.setLayoutData(acStartupGD);
			autoConnectOnStartupButton.addSelectionListener(new SelectionListener() {
	
				public void widgetDefaultSelected(SelectionEvent e) {
					ScrolledConnectionProfileDetailsPage.this.setAutoConnect(
							ScrolledConnectionProfileDetailsPage.this.autoConnectOnStartupButton.getSelection());
				}
	
				public void widgetSelected(SelectionEvent e) {
					widgetDefaultSelected(e);
				}
			});
		}

		if (this.getWizard() instanceof NewConnectionProfileWizard) {
			NewConnectionProfileWizard wiz =
				(NewConnectionProfileWizard) this.getWizard();
			wiz.getProfilePage().setAutoConnectOnFinish(defaultAutoConnectOnFinishFlag);
		}
		
		scrolledComposite.sizeScrolledPanel();
	}

	protected void setPingButtonEnabled(boolean enabled)
    {
        if (btnPing != null && !btnPing.isDisposed())
        {
            btnPing.setEnabled(enabled);
        }
    }

	protected void setPingButtonVisible(boolean visible)
    {
        if (btnPing != null && !btnPing.isDisposed())
        {
            btnPing.setVisible(visible);
        }
    }

	
	private void setAutoConnectFinish ( boolean flag ) {
		if (this.getWizard() instanceof NewConnectionProfileWizard) {
			NewConnectionProfileWizard wiz =
				(NewConnectionProfileWizard) this.getWizard();
			wiz.getProfilePage().setAutoConnectOnFinish(flag);
		}
	}

	private void setAutoConnect ( boolean flag ) {
		if (this.getWizard() instanceof NewConnectionProfileWizard) {
			NewConnectionProfileWizard wiz =
				(NewConnectionProfileWizard) this.getWizard();
			wiz.getProfilePage().setAutoConnect(flag);
		}
	}
	
	protected void setAutoConnectOnFinishDefault( boolean flag ){
		this.defaultAutoConnectOnFinishFlag = flag;
	}
	
	protected boolean getAutoConnectOnFinishDefault() {
		return this.defaultAutoConnectOnFinishFlag;
	}
	
	protected void setShowAutoConnectOnFinish ( boolean flag ) {
		this._showAutoConnectOnFinish = flag;
	}

	protected void setShowAutoConnect ( boolean flag ) {
		this._showAutoConnect = flag;
	}

	/**
	 * Specifies whether to create the controls for the auto connect options on this page.
	 * @param flag true to create related controls; false otherwise
     * @since DTP 1.7.2
	 */
	public void setCreateAutoConnectControls( boolean flag )
	{
        setAutoConnectOnFinishDefault( flag );
        setShowAutoConnectOnFinish( flag );
        setShowAutoConnect( flag );
	}
	
	protected void setShowPing ( boolean flag ) {
		this._showPing = flag;
	}
}
