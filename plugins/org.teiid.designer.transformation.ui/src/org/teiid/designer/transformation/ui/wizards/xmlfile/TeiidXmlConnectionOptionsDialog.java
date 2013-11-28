/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.transformation.ui.wizards.xmlfile;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.designer.transformation.ui.UiConstants;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.common.widget.Dialog;


/**
 * @since 8.0
 */
public class TeiidXmlConnectionOptionsDialog extends Dialog implements UiConstants {
	private static final int WIDTH = 700;
    private static final int HEIGHT = 400;
    
    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(TeiidXmlConnectionOptionsDialog.class);
    private static final String TITLE = getString("title"); //$NON-NLS-1$
    
    boolean isRestProfile = false;
   

	private Button xmlUrlOptionButton, restUrlOptionButton;
    
    private static String getString( final String id ) {
        return Util.getString(I18N_PREFIX + id);
    }
    
    /**
     * 
     * @param parent
     * @param modelName
     * @param props
     */
    public TeiidXmlConnectionOptionsDialog( Shell parent) {
        super(parent, TITLE);
    }

    /**
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Control createDialogArea( Composite parent ) {

        Composite mainPanel = (Composite)super.createDialogArea(parent);
        GridLayout gridLayout = new GridLayout();
        mainPanel.setLayout(gridLayout);
        gridLayout.numColumns = 1;

        GridData gd = new GridData(GridData.FILL_BOTH);
        gd.widthHint = WIDTH;
        gd.heightHint = HEIGHT;
        mainPanel.setLayoutData(gd);
        
		Group mainGroup = WidgetFactory.createGroup(mainPanel,getString("xmlProfileOptions"), SWT.BORDER); //$NON-NLS-1$
		mainGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		this.xmlUrlOptionButton = WidgetFactory.createRadioButton(mainGroup,getString("xmlUrlProfileOption"), true); //$NON-NLS-1$
		this.xmlUrlOptionButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent event) {
				optionButtonSelected();
			}
		});
		
        Text descriptionText = new Text(mainGroup,  SWT.WRAP | SWT.READ_ONLY);
        gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
        gd.heightHint = 60;
        gd.widthHint = 300;
        descriptionText.setLayoutData(gd);
        descriptionText.setText(getString("xmlUrlProfileOption.message")); //$NON-NLS-1$
        descriptionText.setBackground(mainGroup.getBackground());
        descriptionText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
        
		this.restUrlOptionButton = WidgetFactory.createRadioButton(mainGroup,getString("restProfileOption")); //$NON-NLS-1$
		this.restUrlOptionButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent event) {
				optionButtonSelected();
			}
		});
		
		descriptionText = new Text(mainGroup,  SWT.WRAP | SWT.READ_ONLY);
        gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
        gd.heightHint = 60;
        gd.widthHint = 300;
        descriptionText.setLayoutData(gd);
        descriptionText.setText(getString("restProfileOption.message")); //$NON-NLS-1$
        descriptionText.setBackground(mainGroup.getBackground());
        descriptionText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
        
        return mainPanel;
    }
    
	void optionButtonSelected() {
		this.isRestProfile = this.restUrlOptionButton.getSelection();
	}
	
	public boolean isRestProfile() {
		return this.isRestProfile;
	}
}
