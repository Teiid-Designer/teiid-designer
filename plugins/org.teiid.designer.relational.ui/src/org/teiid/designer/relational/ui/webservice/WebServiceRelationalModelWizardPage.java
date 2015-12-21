package org.teiid.designer.relational.ui.webservice;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.teiid.designer.relational.ui.UiConstants;
import org.teiid.designer.ui.common.util.WidgetFactory;


/**
 * @since 8.0
 */
public class WebServiceRelationalModelWizardPage extends WizardPage implements UiConstants{
	
	private Button invokeCB;
	private Button invokeHttpCB;
	
	boolean invokeMethod;
	boolean invokeHttpMethod;
	
	////////////////////////////////////////////////////////////////////////////////
	// Constructors
	////////////////////////////////////////////////////////////////////////////////
	/**
     * Construct an instance of WebServiceRelationalModelWizardPage.
     * @param pageName
     */
    public WebServiceRelationalModelWizardPage(String pageName) {
        super(pageName);

        setTitle(Util.getString("WebServiceRelationalModelWizardPage.title")); //$NON-NLS-1$
        setDescription(Util.getString("WebServiceRelationalModelWizardPage.description")); //$NON-NLS-1$
    }

	@Override
	public void createControl(Composite parent) {
		// 
		
        final Composite mainPanel = new Composite(parent, SWT.NONE);
        mainPanel.setLayoutData(new GridData(GridData.FILL_BOTH));
        mainPanel.setLayout(new GridLayout(2, false));

        final Group optionsGroup = WidgetFactory.createGroup(mainPanel, 
        		Util.getString("WebServiceRelationalModelWizardPage.optionsGroup.title"),  //$NON-NLS-1$
        		GridData.FILL_HORIZONTAL, 2, 1);
        
        invokeCB = WidgetFactory.createCheckBox(optionsGroup, "invoke(binding in String, action in STRING, request in OBJECT, endpoint in STRING, stream in BOOLEAN, result out XML)", 0, 2, true); //$NON-NLS-1$
        invokeCB.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected( final SelectionEvent event ) {
            	invokeMethod = invokeCB.getSelection();
            }
        });
        
        invokeHttpCB = WidgetFactory.createCheckBox(optionsGroup, "invokeHttp(action in STRING, request in OBJECT, endpoint in STRING, stream in BOOLEAN, result out BLOB, contentType out STRING, headers in CLOB)", 0, 2, true); //$NON-NLS-1$
        invokeHttpCB.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected( final SelectionEvent event ) {
            	invokeHttpMethod = invokeHttpCB.getSelection();
            }
        });
        

        super.setControl(mainPanel);
        
        invokeMethod = invokeCB.getSelection();
        invokeHttpMethod = invokeHttpCB.getSelection();
		
	}

	public boolean doGenerateInvoke() {
		return invokeMethod;
	}

	public boolean doGenerateInvokeHttp() {
		return invokeHttpMethod;
	}
}
