/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.datatools.connectivity.ui;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.eclipse.datatools.connectivity.ConnectionProfileConstants;
import org.eclipse.datatools.connectivity.internal.ui.ConnectivityUIPlugin;
import org.eclipse.datatools.connectivity.internal.ui.DriverListCombo;
import org.eclipse.datatools.connectivity.internal.ui.IHelpConstants;
import org.eclipse.datatools.connectivity.internal.ui.wizards.DriverUIContributorComposite;
import org.eclipse.datatools.connectivity.sqm.core.SQMServices;
import org.eclipse.datatools.connectivity.ui.wizards.ConnectionProfileDetailsPage;
import org.eclipse.datatools.connectivity.ui.wizards.ExtensibleNewConnectionProfileWizard;
import org.eclipse.datatools.connectivity.ui.wizards.IDriverUIContributorInformation;
import org.eclipse.datatools.help.HelpUtil;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * This class pulls in the code from ExtensibleProfileDetailsWizardPage
 * so that we can access and set the driver displayed in the driverCombo
 */
public class TeiidProfileDetailsWizardPage extends
		ConnectionProfileDetailsPage implements IDriverUIContributorInformation {

	private String driverCategoryID = ""; //$NON-NLS-1$

	private DriverListCombo driverCombo = null;

	private DriverUIContributorComposite contributedUIComposite = null;

	private Properties properties = null;

	private ChangeListener driverChangeListener;

	public TeiidProfileDetailsWizardPage(String wizardPageName,
			String driverCategoryID) {
		super(wizardPageName);
		this.driverCategoryID = driverCategoryID;
		setTitle(ConnectivityUIPlugin.getDefault().getResourceString(
				"ExtensibleProfileDetailsWizardPage.title")); //$NON-NLS-1$
		setDescription(ConnectivityUIPlugin.getDefault().getResourceString(
				"ExtensibleProfileDetailsWizardPage.description")); //$NON-NLS-1$
	}
	
	public TeiidProfileDetailsWizardPage( String pageName, IWizard wizard) {
        super(pageName);
        this.driverCategoryID = ITeiidDriverConstants.TEIID_CATEGORY;
		setTitle(ConnectivityUIPlugin.getDefault().getResourceString(
				"ExtensibleProfileDetailsWizardPage.title")); //$NON-NLS-1$
		setDescription(ConnectivityUIPlugin.getDefault().getResourceString(
				"ExtensibleProfileDetailsWizardPage.description")); //$NON-NLS-1$
		setWizard(wizard); 
    }

	public void createCustomControl(Composite parent) {
		/*
		 * This bit of code uses the new provider ID mapping functionality added
		 * as an experimental API in DTP 1.7.
		 * <p><strong>EXPERIMENTAL</strong>. This code has been added as
		 * part of a work in progress. There is no guarantee that this API will
		 * work or that it will remain the same. Please do not use this API without
		 * consulting with the DTP Connectivity team.</p>
		 */
		IWizard wiz = getWizard();
		if (wiz instanceof ExtensibleNewConnectionProfileWizard) {
			ExtensibleNewConnectionProfileWizard wizard = (ExtensibleNewConnectionProfileWizard) wiz;
			String tempDriverCategoryID = SQMServices.getProviderIDMappingRegistry().getCategoryIDforProviderID(wizard.getProfileProviderID());
			if (tempDriverCategoryID != null && tempDriverCategoryID.trim().length() > 0)
				this.driverCategoryID = tempDriverCategoryID;
		}

		parent.setLayout(new GridLayout());

		driverCombo = new DriverListCombo();
		driverCombo
				.setLabelText(ConnectivityUIPlugin
						.getDefault()
						.getResourceString(
								"ExtensibleProfileDetailsWizardPage.driverCombo.label")); //$NON-NLS-1$
		driverCombo.setCategory(this.driverCategoryID);
		driverCombo.setNullDriverIsValid(false);
		driverCombo.createContents(parent);

		contributedUIComposite = new DriverUIContributorComposite(parent, this,
				this, false);

		driverChangeListener  = new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				handleDriverComboSelectionChangeEvent(e);
			}
		};
		
		driverCombo.addChangeListener(driverChangeListener);
		if (driverCombo.getCombo().getItemCount() > 0) {
			driverCombo.getCombo().select(0);
		} else if (driverCombo.getErrorMessage() != null) {
			setMessage(driverCombo.getErrorMessage(), DialogPage.INFORMATION);//ErrorMessage(driverCombo.getErrorMessage());
		}
	}

	public void createControl(Composite parent) {
		super.createControl(parent);
		HelpUtil.setHelp( getControl(), HelpUtil.getContextId(IHelpConstants.GENERIC_DB_PROFILE_WIZARD_PAGE, ConnectivityUIPlugin.getDefault().getBundle().getSymbolicName()));
	}

	private void handleDriverComboSelectionChangeEvent(ChangeEvent e) {
		if (driverCombo.getErrorMessage() != null) {
			setErrorMessage(driverCombo.getErrorMessage());
		}
		else {
			setMessage(null);
		}
		if (driverCombo.getSelectedDriverInstance() != null) {
			this.properties = copyProperties(driverCombo
					.getSelectedDriverInstance().getPropertySet()
					.getBaseProperties());
			this.properties.setProperty(
					ConnectionProfileConstants.PROP_DRIVER_DEFINITION_ID,
					driverCombo.getSelectedDriverID());
			if(getWizard().getContainer() instanceof IDriverUIContributorInformation) {
				IDriverUIContributorInformation info = (IDriverUIContributorInformation) getWizard().getContainer();
				this.properties.putAll(info.getProperties());
			}
			
		}
		contributedUIComposite.setDriverTemplateID(driverCombo
				.getSelectedDriverID());
	}

	public boolean determinePageCompletion() {
		boolean isComplete = contributedUIComposite != null && 
		            contributedUIComposite.determineContributorCompletion();
		if (isComplete) {
			setErrorMessage(null);
		}
		this.setPingButtonEnabled(isComplete);
		this.setPageComplete(isComplete);
		return isComplete;
	}

	public Properties getProperties() {
		return this.properties;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
		setPageComplete(determinePageCompletion());
	}

	public List getSummaryData() {
		List summaryData = new ArrayList();
		summaryData = contributedUIComposite.getSummaryData();
		return summaryData;
	}

	private Properties copyProperties(Properties properties) {
		Properties copy = new Properties();
		Enumeration propertyKeys = properties.keys();
		while (propertyKeys.hasMoreElements()) {
			Object key = propertyKeys.nextElement();
			copy.put(key, properties.get(key));
		}
		return copy;
	}

	public boolean isPageComplete() {
		if (driverCombo == null) // means this control hasn't been instantiated yet
			return false;
		if (driverCombo != null && driverCombo.getSelectedDriverID() == null)
			return false;
		if (getErrorMessage() != null)
			return false;
		return super.isPageComplete();
	}

	@Override
	public void dispose() {
		if (this.driverCombo != null) {
			this.driverCombo.dispose();
		}
		super.dispose();
	}

	public void setDriver(String driverID) {
		driverCombo.removeChangeListener(driverChangeListener);
		driverCombo.setSelectionToID(driverID);
		driverCombo.addChangeListener(driverChangeListener);
	}
}
