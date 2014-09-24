/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.datatools.profiles.ws;

import java.net.URL;
import java.util.List;
import java.util.Properties;

import org.eclipse.datatools.connectivity.ui.wizards.ConnectionProfileDetailsPage;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.teiid.designer.datatools.ui.DatatoolsUiConstants;
import org.teiid.designer.modelgenerator.wsdl.WSDLReader;
import org.teiid.designer.modelgenerator.wsdl.model.Model;
import org.teiid.designer.modelgenerator.wsdl.model.ModelGenerationException;
import org.teiid.designer.modelgenerator.wsdl.model.Port;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.common.viewsupport.UiBusyIndicator;
import org.teiid.designer.ui.common.widget.Label;

/**
 *
 */
public class WSSoapProfileEndPointWizardPage extends ConnectionProfileDetailsPage implements DatatoolsUiConstants {
	
    private Composite scrolled;

    private Label profileLabel;
    private CLabel profileText;
    private Label descriptionLabel;
    private Text descriptionText;
    private Text defaultBindingText;
	private Combo endPointCombo;

	private WSSoapConnectionProfileWizard wizard;

	private Properties profileProperties;

    private Model wsdlModel;
    
	/**
	 * @param pageName
	 */
	public WSSoapProfileEndPointWizardPage(String pageName) {
		super(pageName, UTIL.getString("WSSoapProfileEndPointWizardPage.Name"), //$NON-NLS-1$
	              AbstractUIPlugin.imageDescriptorFromPlugin(DatatoolsUiConstants.PLUGIN_ID, "icons/full/obj16/web-service-cp.png")); //$NON-NLS-1$
		setShowPing(false);
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
    
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.datatools.connectivity.ui.wizards.ConnectionProfileDetailsPage
	 * #createCustomControl(org.eclipse.swt.widgets.Composite)
	 */
    
	@Override
	public void createCustomControl(Composite parent) {
	    
        wizard = (WSSoapConnectionProfileWizard) getWizard();
        profileProperties = wizard.getProfileProperties();
        
        Group group = WidgetFactory.createSimpleGroup(parent, null);
        
        scrolled = new Composite(group, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(2).margins(5, 5).applyTo(scrolled);

        Label title = new Label(scrolled, SWT.NONE);
        title.setText(UTIL.getString("Common.Properties.Label")); //$NON-NLS-1$
        title.setFont(JFaceResources.getBannerFont());
        GridDataFactory.fillDefaults().grab(true, false).span(2, 1).align(SWT.CENTER, SWT.CENTER).applyTo(title);
        
        profileLabel = new Label(scrolled, SWT.NONE);
        profileLabel.setText(UTIL.getString("Common.Profile.Label")); //$NON-NLS-1$
        GridDataFactory.swtDefaults().applyTo(profileLabel);
        
        profileText = WidgetFactory.createLabel(scrolled, wizard.getProfileName(), SWT.SINGLE);
        GridDataFactory.swtDefaults().applyTo(profileText);

        descriptionLabel = new Label(scrolled, SWT.NONE);
        descriptionLabel.setText(UTIL.getString("Common.Description.Label")); //$NON-NLS-1$
        GridDataFactory.swtDefaults().applyTo(descriptionLabel);

        descriptionText = WidgetFactory.createTextBox(scrolled, SWT.SINGLE | SWT.BORDER | SWT.READ_ONLY, GridData.FILL);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(descriptionText);
        descriptionText.setText(wizard.getProfileDescription());
        descriptionText.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
        
		Label label = WidgetFactory.createLabel(scrolled, UTIL.getString("WSSoapProfileEndPointWizardPage.EndPointLabel")); //$NON-NLS-1$
		GridDataFactory.swtDefaults().align(SWT.LEFT, SWT.CENTER).applyTo(label);
		
		endPointCombo = WidgetFactory.createCombo(scrolled, SWT.READ_ONLY, GridData.FILL_HORIZONTAL, new String[0], true);
		endPointCombo.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
		GridDataFactory.fillDefaults().applyTo(endPointCombo);
		endPointCombo.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				handlePortNameSelected();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		endPointCombo.setVisibleItemCount(10);
		
		label = WidgetFactory.createLabel(scrolled, UTIL.getString("WSSoapProfileEndPointWizardPage.EndPointBindingLabel"));
		GridData gd = new GridData();
		gd.verticalAlignment=SWT.CENTER;
		label.setLayoutData(gd);
		label.setToolTipText(UTIL.getString("WSSoapProfileEndPointWizardPage.EndPointBindingTooltip"));
		
		defaultBindingText = new Text(scrolled, SWT.BORDER | SWT.SINGLE);
		defaultBindingText.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
		defaultBindingText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
		GridDataFactory.fillDefaults().applyTo(defaultBindingText);
		defaultBindingText.setToolTipText(UTIL.getString("WSSoapProfileEndPointWizardPage.EndPointBindingTooltip"));
	}
	
	@Override
	public void setVisible(boolean visible) {
	    if (visible == true && endPointCombo != null) {
	        /*
	         * Depending on the size of the WSDL selected in the connection profile,
	         * this can take a little while so indicate the user should wait.
	         */
	        UiBusyIndicator.showWhile(getControl().getDisplay(), new Runnable() {
	            @Override
	            public void run() {
	                try {
	                    WSDLReader wsdlReader = wizard.getWsdlReader();
	                    wsdlModel = wsdlReader.getModel();
	                    if(wsdlModel != null ) {
	                        endPointCombo.setItems(wsdlModel.getModelablePortNames());
	                    } else {
	                        endPointCombo.removeAll();
	                    }   
	                } catch (ModelGenerationException ex) {
	                    setErrorMessage(ex.getLocalizedMessage());
	                }
	                String endPointName = profileProperties.getProperty(IWSProfileConstants.END_POINT_NAME_PROP_ID);
			        if (null != endPointName && !endPointName.isEmpty()) {
			        	if( endPointCombo.getItemCount() == 1 ) {
			        		endPointCombo.select(0);
			        	} else if( endPointCombo.getItemCount() > 0 ) {
			        		String[] endPoints = endPointCombo.getItems();
			        		int i=0;
			        		for( String next : endPoints) {
			        			if( next.equals(endPointName)) {
			        				endPointCombo.select(i);
			        				break;
			        			}
			        			i++;
			        		}
			        	}
			            
			        }
	            }
	        });
	    }
	    
	    profileText.setText(wizard.getProfileName());
	    descriptionText.setText(wizard.getProfileDescription());
        
	    super.setVisible(visible);
	}
	
	@Override
	public boolean isPageComplete() {
        String endPointName = profileProperties.getProperty(IWSProfileConstants.END_POINT_NAME_PROP_ID);
        if (null == endPointName || endPointName.isEmpty()) {
            return false;
        }
        
        String portURI = profileProperties.getProperty(IWSProfileConstants.END_POINT_URI_PROP_ID);
        if (null == portURI || portURI.isEmpty()) {
            return false;
        }
        
        String soapBinding = profileProperties.getProperty(IWSProfileConstants.SOAP_BINDING);
        if (null == soapBinding || soapBinding.isEmpty()) {
            return false;
        }
        
        return true;
	}
	
	private  void updateState() {
		
        String endPointName = profileProperties.getProperty(IWSProfileConstants.END_POINT_NAME_PROP_ID);
        if (null == endPointName || endPointName.isEmpty()) {
            setErrorMessage(UTIL.getString("Common.EndPoint.Error.Message")); //$NON-NLS-1$
            return;
        }
        
        setErrorMessage(null);
        
        try {
            Port port = wsdlModel.getPort(endPointName);
            new URL(port.getLocationURI());
            String bindingType = port.getBindingType();
            profileProperties.setProperty(IWSProfileConstants.END_POINT_URI_PROP_ID, port.getLocationURI());
            if( bindingType != null ) {
	            profileProperties.setProperty(IWSProfileConstants.SOAP_BINDING, bindingType);
	            defaultBindingText.setText(bindingType);
            }
            
        } catch(Exception e) {
            setErrorMessage(UTIL.getString("Common.EndPoint.Invalid.Message")); //$NON-NLS-1$
            return;
        }
        
        setErrorMessage(null);
        setPageComplete(true);
        setMessage(UTIL.getString("Click.Next.or.Finish")); //$NON-NLS-1$
    }
	
	private void handlePortNameSelected() {
		// Need to apply this to the connection
		String endPoint = endPointCombo.getText();
		
		if (endPoint != null) {
			endPoint = endPoint.trim();
			profileProperties.setProperty(IWSProfileConstants.END_POINT_NAME_PROP_ID, endPoint);
		}
		
        updateState();
	}
	
	@Override
    public List<String[]> getSummaryData() {
        @SuppressWarnings("unchecked")
		List<String[]> result = super.getSummaryData();
    
        String endPointName = profileProperties.getProperty(IWSProfileConstants.END_POINT_NAME_PROP_ID);
        result.add(new String[] { UTIL.getString("WSSoapProfileEndPointWizardPage.EndPointLabel"), endPointName }); //$NON-NLS-1$
        
        String endPointURI = profileProperties.getProperty(IWSProfileConstants.END_POINT_URI_PROP_ID);
        result.add(new String[] { UTIL.getString("WSSoapProfileEndPointWizardPage.EndPointURILabel"), endPointURI }); //$NON-NLS-1$
        
        String binding = profileProperties.getProperty(IWSProfileConstants.SOAP_BINDING);
        result.add(new String[] { UTIL.getString("WSSoapProfileEndPointWizardPage.EndPointBindingLabel"), binding }); //$NON-NLS-1$
        
        return result;
        
    }
}