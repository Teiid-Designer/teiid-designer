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
import org.teiid.designer.transformation.ui.UiConstants;
import org.teiid.designer.ui.common.graphics.GlobalUiColorManager;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.common.util.WizardUtil;
import org.teiid.designer.ui.common.widget.Label;
import org.teiid.designer.ui.common.wizard.AbstractWizardPage;

public class GenerateArchiveVdbPageOne extends AbstractWizardPage implements UiConstants {

	private final String EMPTY = StringConstants.EMPTY_STRING;
	private final int GROUP_HEIGHT = 400;

	Font monospaceFont;
    private Text xmlContentsBox;
		
	private GenerateArchiveVdbManager vdbManager;

	/**
	 * ShowDDlPage constructor
     * @param importManager the ImportManager
	 * @since 8.1
	 */
	public GenerateArchiveVdbPageOne(GenerateArchiveVdbManager vdbManager) {
        super(GenerateArchiveVdbPageOne.class.getSimpleName(), ""); //Messages.ShowDDLPage_title); 
        this.vdbManager = vdbManager;
        setTitle("Dynamic VDB Contents");
	}
	

	@Override
	public void createControl(Composite parent) {
        monospaceFont = new Font(null, "Monospace", 10, SWT.BOLD);
		// Create page
		final Composite mainPanel = new Composite(parent, SWT.NONE);

		mainPanel.setLayout(new GridLayout(1, false));
		mainPanel.setLayoutData(new GridData()); 
		mainPanel.setSize(mainPanel.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		setControl(mainPanel);
		
        Composite summaryPanel = WidgetFactory.createPanel(mainPanel, SWT.NO_SCROLL, 1);
//        summaryPanel.setLayout(new GridLayout(2, false));
        GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).margins(10,  10).applyTo(summaryPanel);
        GridDataFactory.fillDefaults().grab(true,  false).applyTo(summaryPanel);
        // ----------------------------------------
        // XML File controls 
        // ----------------------------------------
        WidgetFactory.createLabel(summaryPanel, GridData.VERTICAL_ALIGN_CENTER, "Dynamic VDB File");

        Label dynamicVdbFileName = new Label(summaryPanel, SWT.BORDER);
        GridDataFactory.fillDefaults().grab(true,  false).applyTo(dynamicVdbFileName);
        dynamicVdbFileName.setText(vdbManager.getDynamicVdbFile().getName());
        dynamicVdbFileName.setForeground(GlobalUiColorManager.EMPHASIS_COLOR);
        
        WidgetFactory.createLabel(summaryPanel, GridData.VERTICAL_ALIGN_CENTER, "VDB Name");
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
        Group theGroup = WidgetFactory.createGroup(parent, "File Contents",  GridData.FILL_BOTH, 1);
        GridLayoutFactory.fillDefaults().numColumns(1).equalWidth(false).margins(10,  10).applyTo(theGroup);
        GridDataFactory.fillDefaults().span(2, 1).applyTo(theGroup);
        ((GridData)theGroup.getLayoutData()).heightHint = GROUP_HEIGHT;
        ((GridData)theGroup.getLayoutData()).widthHint = 400;

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
