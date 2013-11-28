/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.ui.wizards.xmlfile;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.text.source.VerticalRuler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.designer.core.util.StringUtilities;
import org.teiid.designer.transformation.ui.Messages;
import org.teiid.designer.transformation.ui.UiConstants;
import org.teiid.designer.transformation.ui.UiPlugin;
import org.teiid.designer.transformation.ui.editors.sqleditor.SqlTextViewer;
import org.teiid.designer.transformation.ui.wizards.file.TeiidMetadataImportInfo;
import org.teiid.designer.transformation.ui.wizards.xmlfile.panels.ColumnsInfoPanel;
import org.teiid.designer.transformation.ui.wizards.xmlfile.panels.XmlFileContentsGroup;
import org.teiid.designer.ui.common.graphics.ColorManager;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.common.util.WizardUtil;
import org.teiid.designer.ui.common.wizard.AbstractWizardPage;


/**
 * @since 8.0
 */
public class TeiidXmlImportXmlConfigurationPage extends AbstractWizardPage implements
		UiConstants {
	// ===========================================================================================================================
	// Constants

	private TeiidMetadataImportInfo info;

	// Target SQL Variables
	Group headerGroup;
	TextViewer sqlTextViewer;
	IDocument sqlDocument;
	Button parseRowButton;
	Action createColumnAction, setRootPathAction;
	Button addColumnButton, deleteButton, upButton, downButton;
	Text selectedFileText;
	XmlFileContentsGroup xmlFileContentsGroup;
	ColumnsInfoPanel columnsInfoPanel;
	
	private TeiidXmlFileInfo fileInfo;
	
	boolean creatingControl = false;

	boolean synchronizing = false;

	/**
	 * @since 4.0
	 */
	public TeiidXmlImportXmlConfigurationPage(TeiidMetadataImportInfo info) {
		super(TeiidXmlImportXmlConfigurationPage.class.getSimpleName(), Messages.XmlConfigPageTitle);
		this.info = info;
		setImageDescriptor(UiPlugin.getDefault().getImageDescriptor(Images.IMPORT_TEIID_METADATA));
	}

	@Override
	public void createControl(Composite parent) {
		creatingControl = true;
		// Create page
		final Composite mainPanel = new Composite(parent, SWT.NONE);

		mainPanel.setLayout(new GridLayout());
		mainPanel.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL));
		mainPanel.setSize(mainPanel.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		setControl(mainPanel);

		setMessage(Messages.XmlConfigPageInitialMessage);
		
		createColumnsDefinitionSplitter(mainPanel);
        
        createXmlTableSqlGroup(mainPanel);
        
		creatingControl = false;

		setPageComplete(false);
	}
	
	private void createColumnsDefinitionSplitter(Composite parent) {
		Composite columnsPanel = new Composite(parent, SWT.NONE);
		columnsPanel.setLayout(new GridLayout(2, false));
		columnsPanel.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label selectedFileLabel = new Label(columnsPanel, SWT.NONE);
		selectedFileLabel.setText(Messages.XMLFile);
		
        selectedFileText = new Text(columnsPanel, SWT.BORDER | SWT.SINGLE);
        selectedFileText.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
        selectedFileText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
		selectedFileText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		selectedFileText.setEditable(false);
		
		SashForm splitter = new SashForm(columnsPanel, SWT.HORIZONTAL);
		GridData gid = new GridData(GridData.FILL_BOTH);
		gid.horizontalSpan = 2;
		gid.heightHint = 300;
		
		splitter.setLayoutData(gid);

		xmlFileContentsGroup = new XmlFileContentsGroup(splitter, this);
		columnsInfoPanel = new ColumnsInfoPanel(splitter, this);
		xmlFileContentsGroup.setColumnsInfoPanel(columnsInfoPanel);

		splitter.setWeights(new int[] { 30, 70 });
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);

		if (visible) {
			TeiidXmlFileInfo xmlFileInfo = null;
			for( TeiidXmlFileInfo xmlInfo : info.getXmlFileInfos() ) {
				if( xmlInfo.doProcess() ) {
					xmlFileInfo = xmlInfo;
					break;
				}
			}
			if( xmlFileInfo != null ) {
				this.fileInfo = xmlFileInfo;
				
				loadFileContentsViewer();
			}
			synchronizeUI();
		}
	}

	/* (non-Javadoc)
	 * @see org.teiid.designer.ui.common.wizard.AbstractWizardPage#canFlipToNextPage()
	 */
	@Override
	public boolean canFlipToNextPage() {
		return isFileInfoValid() ? super.canFlipToNextPage() : false;
	}

	private boolean validatePage() {

		if( !isFileInfoValid()) {
			setThisPageComplete(fileInfo.getStatus().getMessage(), IMessageProvider.ERROR);
			return false;
		}
		
		if( fileInfo.getStatus().getSeverity() == IStatus.WARNING ) {
			setThisPageComplete(fileInfo.getStatus().getMessage(), IMessageProvider.WARNING);
			return true;
		}
		
		setThisPageComplete(StringUtilities.EMPTY_STRING, NONE);
		return true;
	}

	private boolean isFileInfoValid() {
		return fileInfo.getStatus().isOK() || fileInfo.getStatus().getSeverity() == IStatus.WARNING;
	}

    private void setThisPageComplete( String message, int severity) {
    	WizardUtil.setPageComplete(this, message, severity);
    }

	private void synchronizeUI() {
		synchronizing = true;

		selectedFileText.setText(fileInfo.getDataFile().getName());

		synchronizing = false;
	}
    
    private void createXmlTableSqlGroup(Composite parent) {
    	Group xmlTableOptionsGroup = WidgetFactory.createGroup(parent, Messages.GeneratedSQLStatement, SWT.NONE, 1);
    	GridData gd = new GridData(GridData.FILL_BOTH);
    	gd.heightHint = 100;
    	xmlTableOptionsGroup.setLayoutData(gd);
    	
    	ColorManager colorManager = new ColorManager();
        int styles = SWT.V_SCROLL | SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.FULL_SELECTION;

        sqlTextViewer = new SqlTextViewer(xmlTableOptionsGroup, new VerticalRuler(0), styles, colorManager);
        sqlDocument = new Document();
        sqlTextViewer.setInput(sqlDocument);
        sqlTextViewer.setEditable(false);
        sqlTextViewer.getTextWidget().setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
        sqlDocument.set(CoreStringUtil.Constants.EMPTY_STRING);
        sqlTextViewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
//        String sqlText = "SELECT \n\ttitle.pmid AS pmid, title.journal AS journal, title.title AS title\n" + 
//        			"FROM \n\t(EXEC getMeds.getTextFiles('medsamp2011.xml')) AS f," +  
//        			"XMLTABLE('$d/MedlineCitationSet/MedlineCitation' PASSING " +
//        			"XMLPARSE(DOCUMENT f.file) AS d " + 
//        			"COLUMNS pmid biginteger PATH 'PMID', journal string PATH 'Article/Journal/Title', title string PATH 'Article/ArticleTitle') AS title";
        updateSqlText();
    }
    
    public void handleInfoChanged(boolean reloadFileContents) {
    	if( synchronizing ) return;
    	
    	synchronizeUI();

    	this.columnsInfoPanel.refresh();
    	
    	updateSqlText();
        
        validatePage();
    }
    
    private void loadFileContentsViewer() {
    	this.xmlFileContentsGroup.loadFileContentsViewer();
    }
    
    public TeiidXmlFileInfo getFileInfo() {
    	return this.fileInfo;
    }
    
    void updateSqlText() {
    	if( this.fileInfo != null ) {
    		if( this.info.getSourceModelName() != null ) {
    			String modelName = this.fileInfo.getModelNameWithoutExtension(this.info.getSourceModelName());
    			sqlTextViewer.getDocument().set(fileInfo.getSqlString(modelName));
    		} else {
    			sqlTextViewer.getDocument().set(fileInfo.getSqlStringTemplate());
    		}
    	}
    }
	
    public void createColumn() {
    	this.xmlFileContentsGroup.createColumn();
    }
}
