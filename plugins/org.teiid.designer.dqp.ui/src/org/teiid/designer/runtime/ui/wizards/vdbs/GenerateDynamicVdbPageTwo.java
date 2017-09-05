/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime.ui.wizards.vdbs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.teiid.core.designer.util.StringConstants;
import org.teiid.designer.runtime.ui.DqpUiConstants;
import org.teiid.designer.runtime.ui.Messages;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.common.util.style.XmlRegion;
import org.teiid.designer.ui.common.util.style.XmlRegionAnalyzer;
import org.teiid.designer.ui.common.wizard.AbstractWizardPage;

/**
 * Page 2 of the Generate Dynamic Vdb Wizard
 */
public class GenerateDynamicVdbPageTwo extends AbstractWizardPage implements DqpUiConstants, StringConstants {

    private Font monospaceFont;
    private StyledText xmlContentsBox;
    private Button generateXmlButton;
//    private Button exportXmlToFileSystemButton;

    private GenerateDynamicVdbManager vdbManager;

    /**
     * ShowDDlPage constructor
     * @param vdbManager the vdb manager
     * @since 8.1
     */
    public GenerateDynamicVdbPageTwo(GenerateDynamicVdbManager vdbManager) {
        super(GenerateDynamicVdbPageTwo.class.getSimpleName(), ""); //$NON-NLS-1$
        this.vdbManager = vdbManager;
        setTitle(Messages.GenerateDynamicVdbPageTwo_title);
    }

    private Font monospaceFont(Composite composite) {
        if (monospaceFont == null) {
            monospaceFont = new Font(composite.getDisplay(), "Monospace", 12, SWT.NORMAL); //$NON-NLS-1$
            composite.addDisposeListener(new DisposeListener() {

                @Override
                public void widgetDisposed(DisposeEvent e) {
                    if (monospaceFont == null)
                        return;

                    monospaceFont.dispose();
                }
            });
        }

        return monospaceFont;
    }

    @Override
    public void createControl(Composite parent) {
        monospaceFont(parent);

        // Create page
        final Composite mainPanel = new Composite(parent, SWT.NONE);

        mainPanel.setLayout(new GridLayout(1, false));
        mainPanel.setLayoutData(new GridData());
        mainPanel.setSize(mainPanel.computeSize(SWT.DEFAULT, SWT.DEFAULT));

        createButtonPanel(mainPanel);

        // Create DDL display group
        createXMLDisplayGroup(mainPanel);

        setControl(mainPanel);
        
        setPageComplete(false);
    }

    /**
     * Taken from
     * https://vzurczak.wordpress.com/2012/09/07/xml-syntax-highlighting-with-a-styled-text
     * BSD Licensed
     *
     * Computes style ranges from XML regions.
     * @param regions an ordered list of XML regions
     * @return an ordered list of style ranges for SWT styled text
     */
    private List<StyleRange> computeStyleRanges(List<XmlRegion> regions) {

        List<StyleRange> styleRanges = new ArrayList<StyleRange>();
        if (regions == null)
            return styleRanges;

        for (XmlRegion xr : regions) {

            // The style itself depends on the region type
            // In this example, we use colors from the system
            StyleRange sr = new StyleRange();
            switch (xr.getXmlRegionType()) {
                case MARKUP:
                    sr.foreground = Display.getDefault().getSystemColor(SWT.COLOR_DARK_GREEN);
                    sr.fontStyle = SWT.BOLD;
                    break;
                case ATTRIBUTE:
                    sr.foreground = Display.getDefault().getSystemColor(SWT.COLOR_DARK_RED);
                    break;
                case ATTRIBUTE_VALUE:
                    sr.foreground = Display.getDefault().getSystemColor(SWT.COLOR_DARK_BLUE);
                    break;
                case MARKUP_VALUE:
                case COMMENT:
                    sr.foreground = Display.getDefault().getSystemColor(SWT.COLOR_BLUE);
                    break;
                case INSTRUCTION:
                    sr.foreground = Display.getDefault().getSystemColor(SWT.COLOR_DARK_GRAY);
                    break;
                case CDATA:
                    sr.foreground = Display.getDefault().getSystemColor(SWT.COLOR_BLACK);
                    sr.fontStyle = SWT.BOLD;
                    break;
                case WHITESPACE:
                    break;
                default:
                    break;
            }

            // Define the position and limit
            sr.start = xr.getStart();
            sr.length = xr.getEnd() - xr.getStart();
            styleRanges.add(sr);
        }

        return styleRanges;
    }

    /**
     * Set the xml content string of the style text box and
     * compute the highlighting colouration using the
     * {@link XmlRegionAnalyzer}
     *
     * @param xml
     */
    private void setXmlContents(String xml) {
        if (xml == null) {
        	this.xmlContentsBox.setText(EMPTY_STRING);
        } else {
	        this.xmlContentsBox.setText(xml);
	
	        if (xml.length() > 0) {
	            XmlRegionAnalyzer analyzer = new XmlRegionAnalyzer();
	            List<XmlRegion> xmlRegions = analyzer.analyzeXml(xml);
	            List<StyleRange> styleRanges = computeStyleRanges(xmlRegions);
	            this.xmlContentsBox.setStyleRanges(styleRanges.toArray(new StyleRange[0]));
	        }
        }
    }

    /*
     * Create the Group containing the DDL Contents (not editable)
     */
    private void createXMLDisplayGroup(Composite parent) {
        Group theGroup = WidgetFactory.createGroup(parent, Messages.GenerateDynamicVdbPageTwo_fileContents, GridData.FILL_BOTH, 1);
        GridLayoutFactory.fillDefaults().numColumns(1).equalWidth(false).margins(10, 10).applyTo(theGroup);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(theGroup);

        xmlContentsBox = new StyledText(theGroup, SWT.READ_ONLY | SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
        GridDataFactory.fillDefaults().grab(true, true).minSize(400, 300).applyTo(xmlContentsBox);

        xmlContentsBox.setEditable(false);
        xmlContentsBox.setFont(monospaceFont);

    }

    /*
     * Create the VDB Export to file button 
     */
    private void createButtonPanel(Composite parent) {
        {
            Composite buttonPanel = new Composite(parent, SWT.NONE);
            GridLayoutFactory.fillDefaults().numColumns(4).margins(10, 10).applyTo(buttonPanel);
            GridDataFactory.fillDefaults().grab(true, false).applyTo(buttonPanel);

            generateXmlButton = new Button(buttonPanel, SWT.PUSH);
            GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).grab(false, false).applyTo(generateXmlButton);
            generateXmlButton.setText(Messages.GenerateVdbButton_Title);
            generateXmlButton.setToolTipText(Messages.GenerateVdbButton_Tooltip);
            generateXmlButton.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    vdbManager.generate(false);
                    refreshXml();
                    validatePage();
                }
            });

//            Label spacer = WidgetFactory.createLabel(buttonPanel, GridData.VERTICAL_ALIGN_CENTER, " ");
//            GridDataFactory.fillDefaults().grab(true, false).applyTo(spacer);
//            
//	        WidgetFactory.createLabel(buttonPanel, GridData.VERTICAL_ALIGN_CENTER, Messages.GenerateDynamicVdbPageTwo_exportXmlLabel);
//	
//	        exportXmlToFileSystemButton = new Button(buttonPanel, SWT.PUSH);
//	        exportXmlToFileSystemButton.setText(Messages.GenerateDynamicVdbPageTwo_exportXmlTitle);
//	        exportXmlToFileSystemButton.setToolTipText(Messages.GenerateDynamicVdbPageTwo_exportXmlTooltip);
//	        exportXmlToFileSystemButton.setLayoutData(new GridData());
//	        exportXmlToFileSystemButton.setEnabled(true);
//	        exportXmlToFileSystemButton.addSelectionListener(new SelectionAdapter() {
//	
//	            @Override
//	            public void widgetSelected(SelectionEvent e) {
//	                handleExportDDLToFileSystem();
//	            }
//	        });
        }
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
        	refreshXml();

            validatePage();
        }

        super.setVisible(visible);
    }
    
    private void refreshXml() {
        try {

            String xml = vdbManager.getDynamicVdbXml();
            setXmlContents(xml);
        } catch (Exception ex) {
            //
            // want to avoid validating the page here since
            // we need to see this exception
            //
            this.setErrorMessage(ex.getLocalizedMessage());
            this.setPageComplete(false);
            setXmlContents(EMPTY_STRING);
        }
        
        generateXmlButton.setEnabled(vdbManager.isGenerateRequired());
    }

    /* 
     * Validate the page
     */
    private void validatePage() {
        this.vdbManager.validate();
        IStatus status = vdbManager.getStatus();
        if (status.getSeverity() == IStatus.ERROR) {
            this.setErrorMessage(status.getMessage());
            this.setPageComplete(false);
            return;
        } else if (status.getSeverity() == IStatus.WARNING) {
            this.setErrorMessage(null);
            if( vdbManager.isGenerateRequired() ) {
                setErrorMessage(Messages.GenerateDynamicVdbPageTwo_clickGenerateToCreateVdb);
                this.setPageComplete(false);
            } else {
        		setErrorMessage(null);
        		setMessage(Messages.GenerateDynamicVdbPageTwo_clickFinishToSaveVdb, NONE);
        		this.setPageComplete(true);
            }
        } else {
        	if( vdbManager.isGenerateRequired() ) {
                setErrorMessage(Messages.GenerateDynamicVdbPageTwo_clickGenerateToCreateVdb);
                this.setPageComplete(false);
        	} else {
        		setErrorMessage(null);
        		setMessage(Messages.GenerateDynamicVdbPageTwo_clickFinishToSaveVdb, NONE);
        		this.setPageComplete(true);
        	}
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        if (!monospaceFont.isDisposed()) {
            monospaceFont.dispose();
        }
    }

    /**
     * Export the current string content of the XML display to a user-selected file on file system
     */
    public void handleExportDDLToFileSystem() {
        DirectoryDialog dlg = new DirectoryDialog(getShell(), SWT.SAVE);
        dlg.setText(Messages.GenerateDynamicVdbPageTwo_exportXmlDialogTitle);
        String directory = dlg.open();

        if (directory == null)
            return;

        // Export to the file
        try {
            vdbManager.export(directory);
        } catch (Exception ex) {
            this.setErrorMessage(ex.getLocalizedMessage());
        }
    }

}
