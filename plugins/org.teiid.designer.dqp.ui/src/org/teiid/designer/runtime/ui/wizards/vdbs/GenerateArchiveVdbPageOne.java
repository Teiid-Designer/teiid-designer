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
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.teiid.core.designer.util.StringConstants;
import org.teiid.designer.runtime.ui.DqpUiConstants;
import org.teiid.designer.runtime.ui.Messages;
import org.teiid.designer.runtime.ui.wizards.vdbs.style.XmlRegion;
import org.teiid.designer.runtime.ui.wizards.vdbs.style.XmlRegionAnalyzer;
import org.teiid.designer.ui.common.graphics.GlobalUiColorManager;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.common.util.WizardUtil;
import org.teiid.designer.ui.common.widget.Label;
import org.teiid.designer.ui.common.wizard.AbstractWizardPage;

/**
 * Page 1 of Generate Archive Vdb Wizard
 */
public class GenerateArchiveVdbPageOne extends AbstractWizardPage implements DqpUiConstants, StringConstants {

	private Font monospaceFont;

    private StyledText xmlContentsBox;
		
	private GenerateArchiveVdbManager vdbManager;

	/**
	 * ShowDDlPage constructor
     * @param vdbManager the manager
	 * @since 8.1
	 */
	public GenerateArchiveVdbPageOne(GenerateArchiveVdbManager vdbManager) {
        super(GenerateArchiveVdbPageOne.class.getSimpleName(), EMPTY_STRING);
        this.vdbManager = vdbManager;
        setTitle(Messages.GenerateArchiveVdbPageOne_title);
	}
	
	private Font monospaceFont(Composite composite) {
        if (monospaceFont == null) {
            monospaceFont = new Font(composite.getDisplay(), "Monospace", 12, SWT.NORMAL);  //$NON-NLS-1$
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
        if (xml == null)
            xml = EMPTY_STRING;

        this.xmlContentsBox.setText(xml);

        if (xml.length() > 0) {
            XmlRegionAnalyzer analyzer = new XmlRegionAnalyzer();
            List<XmlRegion> xmlRegions = analyzer.analyzeXml(xml);
            List<StyleRange> styleRanges = computeStyleRanges(xmlRegions);
            this.xmlContentsBox.setStyleRanges(styleRanges.toArray(new StyleRange[0]));
        }
    }

    /*
     * Create the Group containing the DDL Contents (not editable)
     */
    private void createXMLDisplayGroup( Composite parent ) {
        Group theGroup = WidgetFactory.createGroup(parent, Messages.GenerateArchiveVdbPageOne_vdbXmlContents,  GridData.FILL_BOTH, 1);
        GridLayoutFactory.fillDefaults().numColumns(1).equalWidth(false).margins(10,  10).applyTo(theGroup);
        GridDataFactory.fillDefaults().span(2, 1).grab(true, true).applyTo(theGroup);

        xmlContentsBox = new StyledText(theGroup, SWT.READ_ONLY | SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(xmlContentsBox);

        xmlContentsBox.setEditable(false);
        xmlContentsBox.setFont(monospaceFont);
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

            try {
                String xml = vdbManager.getDynamicVdbXml();
                setXmlContents(xml);

                validatePage();
            } catch (Exception ex) {
                //
                // want to avoid validating the page here since
                // we need to see this exception
                //
                this.setErrorMessage(ex.getLocalizedMessage());
                this.setPageComplete(false);
            }

            getControl().setVisible(visible);
        }

        super.setVisible(visible);
    }

    /* 
     * Validate the page
     */
	private boolean validatePage() {
	    WizardUtil.setPageComplete(this, EMPTY_STRING, NONE);
		return true;
	}


	@Override
	public void dispose() {
		super.dispose();
		if(!monospaceFont.isDisposed() ) {
			monospaceFont.dispose();
		}
	}
	
	

}
