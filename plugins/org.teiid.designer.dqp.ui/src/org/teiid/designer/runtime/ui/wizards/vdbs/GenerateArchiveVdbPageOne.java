/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime.ui.wizards.vdbs;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.teiid.core.designer.util.StringConstants;
import org.teiid.designer.runtime.ui.DqpUiConstants;
import org.teiid.designer.runtime.ui.Messages;
import org.teiid.designer.ui.common.graphics.GlobalUiColorManager;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.common.util.WizardUtil;
import org.teiid.designer.ui.common.widget.Label;
import org.teiid.designer.ui.common.wizard.AbstractWizardPage;

public class GenerateArchiveVdbPageOne extends AbstractWizardPage implements DqpUiConstants {

	private final String EMPTY = StringConstants.EMPTY_STRING;

	Font monospaceFont;
    private Text xmlContentsBox;
		
	private GenerateArchiveVdbManager vdbManager;

	/**
	 * ShowDDlPage constructor
     * @param importManager the ImportManager
	 * @since 8.1
	 */
	public GenerateArchiveVdbPageOne(GenerateArchiveVdbManager vdbManager) {
        super(GenerateArchiveVdbPageOne.class.getSimpleName(), "");  //$NON-NLS-N$
        this.vdbManager = vdbManager;
        setTitle(Messages.GenerateArchiveVdbPageOne_title);
	}
	

	@Override
	public void createControl(Composite parent) {
        monospaceFont = new Font(null, "Monospace", 10, SWT.BOLD); //$NON-NLS-N$
		// Create page
		final Composite mainPanel = new Composite(parent, SWT.NONE);

		mainPanel.setLayout(new GridLayout(1, false));
		mainPanel.setLayoutData(new GridData()); 
		mainPanel.setSize(mainPanel.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		setControl(mainPanel);
		
        Composite summaryPanel = WidgetFactory.createPanel(mainPanel, SWT.NO_SCROLL, 1);
        GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).margins(10,  10).applyTo(summaryPanel);
        GridDataFactory.fillDefaults().grab(true,  false).applyTo(summaryPanel);
        // ----------------------------------------
        // XML File controls 
        // ----------------------------------------
        WidgetFactory.createLabel(summaryPanel, GridData.VERTICAL_ALIGN_CENTER, Messages.GenerateArchiveVdbPageOne_dynamicVdbFile);

        Label dynamicVdbFileName = new Label(summaryPanel, SWT.BORDER);
        GridDataFactory.fillDefaults().grab(true,  false).applyTo(dynamicVdbFileName);
        dynamicVdbFileName.setText(vdbManager.getDynamicVdbFile().getName());
        dynamicVdbFileName.setForeground(GlobalUiColorManager.EMPHASIS_COLOR);
        
        WidgetFactory.createLabel(summaryPanel, GridData.VERTICAL_ALIGN_CENTER, Messages.GenerateArchiveVdbPageOne_vdbName);
        Label vdbNameFld = new Label(summaryPanel, SWT.BORDER);
        GridDataFactory.fillDefaults().grab(true,  false).applyTo(vdbNameFld);
        vdbNameFld.setText(vdbManager.getDynamicVdb().getName());
        vdbNameFld.setForeground(GlobalUiColorManager.EMPHASIS_COLOR);
	    
	    // Create DDL display group
		createXMLDisplayGroup(mainPanel);
        
		setPageComplete(false);
	}
	
    /*
     * Create the Group containing the DDL Contents (not editable)
     */
    private void createXMLDisplayGroup( Composite parent ) {
        Group theGroup = WidgetFactory.createGroup(parent, Messages.GenerateArchiveVdbPageOne_vdbXmlContents,  GridData.FILL_BOTH, 1);
        GridLayoutFactory.fillDefaults().numColumns(1).equalWidth(false).margins(10,  10).applyTo(theGroup);
        GridDataFactory.fillDefaults().span(2, 1).grab(true, true).applyTo(theGroup);

        xmlContentsBox = WidgetFactory.createTextBox(theGroup);
        
        xmlContentsBox.setEditable(false);
        xmlContentsBox.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));

        xmlContentsBox.setFont(monospaceFont);
        
        String xml = vdbManager.getXmlFileAsString();
        this.xmlContentsBox.setText(xml);
    }

    /**
     * Set the DDL display contents
     * @param ddlText the DDL to display
     */
    public void setDDL(String ddlText) {
        xmlContentsBox.setText(ddlText);
    }
    
    /**
     * Get the DDL display contents
     * @return the DDL display contents
     */
    public String getDDL() {
        return xmlContentsBox.getText();
    } 

    @Override
    public void setVisible( boolean visible ) {
        if (visible) {

            validatePage();
            
            getControl().setVisible(visible);
        } else {
            super.setVisible(visible);
        }
    }

    /* 
     * Validate the page
     */
	private boolean validatePage() {
        setThisPageComplete(EMPTY, NONE);
		return true;
	}

	private void setThisPageComplete(String message, int severity) {
		WizardUtil.setPageComplete(this, message, severity);
	}


	@Override
	public void dispose() {
		super.dispose();
		if(!monospaceFont.isDisposed() ) {
			monospaceFont.dispose();
		}
	}
	
	

}
