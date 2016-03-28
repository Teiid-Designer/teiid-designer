package org.teiid.designer.runtime.ui.preview;

import java.util.Properties;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.source.VerticalRuler;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.teiid.designer.runtime.ui.DqpUiConstants;
import org.teiid.designer.runtime.ui.Messages;
import org.teiid.designer.transformation.ui.editors.sqleditor.SqlTextViewer;
import org.teiid.designer.ui.common.graphics.ColorManager;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.explorer.ModelExplorerLabelProvider;

public class PreviewDataInputDialog extends TitleAreaDialog implements DqpUiConstants {

	private EObject previewableEObject;

	private ILabelProvider labelProvider = new ModelExplorerLabelProvider();

    // Table SQL Text Tab Controls
	private SqlTextViewer sqlTextViewer;
	private Document sqlDocument;
	
	private TabItem sqlTab;
	private TabItem vdbXmlTab;
	
    private StyledText xmlContentsBox;

	Properties designerProperties;
	
    private Font monospaceFont;
    
    String sqlText;
    String dynVdbXml;

	/**
	 * @since 5.5.3
	 */
	public PreviewDataInputDialog(Shell parentShell, String initialSQL, String dynVdbXml) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		
		this.sqlText = initialSQL;
		this.dynVdbXml = dynVdbXml;
	}

	/**
	 * @since 5.5.3
	 */
	public PreviewDataInputDialog(Shell parentShell, Properties properties, String initialSQL, String dynVdbXml) {
		this(parentShell, initialSQL, dynVdbXml);
		this.designerProperties = properties;
	}

	/**
	 * @see org.eclipse.jface.dialogs.Dialog#close()
	 * @since 5.5.3
	 */
	@Override
	public boolean close() {

		if (this.labelProvider != null) {
			this.labelProvider.dispose();
		}

		return super.close();
	}

	/**
	 * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
	 * @since 5.5.3
	 */
	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText("Custom Preview Data"); //$NON-NLS-1$
	}

	/**
	 * @see org.eclipse.jface.dialogs.Dialog#createButtonBar(org.eclipse.swt.widgets.Composite)
	 * @since 5.5.3
	 */
	@Override
	protected Control createButtonBar(Composite parent) {
		Control buttonBar = super.createButtonBar(parent);
		getButton(OK).setEnabled(true);

		// set the first selection so that initial validation state is set
		// (doing it here since the selection handler uses OK
		// button)

		return buttonBar;
	}

	/**
	 * @see org.eclipse.jface.dialogs.TitleAreaDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 * @since 5.5.3
	 */
	@Override
	protected Control createDialogArea(Composite parent) {

        monospaceFont(parent);
        
		Composite pnlOuter = (Composite) super.createDialogArea(parent);
		GridLayoutFactory.fillDefaults().numColumns(1).margins(10,  10).applyTo(pnlOuter);
		GridDataFactory.fillDefaults().grab(true,  true).applyTo(pnlOuter);

		// set title
		setTitle(Messages.PreviewDataInputDialog_title);
		setMessage(Messages.PreviewDataInputDialog_initialMessage);

		TabFolder folder = createTabFolder(pnlOuter);
		
		createSQLTab(folder);
		
		createVdbXmlTabTab(folder);

		return pnlOuter;
	}

	@Override
	protected Control createContents(Composite parent) {
		Control control = super.createContents(parent);

		return control;
	}

	public EObject getPreviewableEObject() {
		return this.previewableEObject;
	}
	
    protected TabFolder createTabFolder(Composite parent) {
        TabFolder tabFolder = new TabFolder(parent, SWT.TOP | SWT.BORDER);
        GridDataFactory.fillDefaults().grab(true,  true).applyTo(tabFolder);
        return tabFolder;
    }
    
    /*
     * Create the SQL Tab
     */
    private void createSQLTab( TabFolder folderParent ) {
        Composite thePanel = createSQLPanel(folderParent);

        this.sqlTab = new TabItem(folderParent, SWT.NONE);
        this.sqlTab.setControl(thePanel);
        this.sqlTab.setText(Messages.PreviewDataInputDialog_previewSqlLabel);
    }
    
    /*
     * Create the SQL Display tab panel
     */
    private Composite createSQLPanel( Composite parent ) {
        Composite thePanel = WidgetFactory.createPanel(parent, SWT.NONE, 1, 1);
        GridLayoutFactory.fillDefaults().margins(10, 10).applyTo(thePanel);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(thePanel);

        createSqlGroup(thePanel);
        return thePanel;
    }
    
    /*
     * The SQL Display area portion of the SQL Tab
     */
    private void createSqlGroup( Composite parent ) {
        Group textTableOptionsGroup = WidgetFactory.createGroup(parent, Messages.PreviewDataInputDialog_sqlQueryLabel, SWT.NONE, 2, 1);
        GridDataFactory.fillDefaults().grab(true, true).span(2, 1).applyTo(textTableOptionsGroup);

        ColorManager colorManager = new ColorManager();
        int styles = SWT.V_SCROLL | SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.FULL_SELECTION ;

        sqlTextViewer = new SqlTextViewer(textTableOptionsGroup, new VerticalRuler(0), styles, colorManager);
        sqlDocument = new Document();
        sqlTextViewer.setInput(sqlDocument);
        sqlTextViewer.getTextWidget().setFont(monospaceFont);
        sqlTextViewer.getTextWidget().addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				sqlText = sqlTextViewer.getTextWidget().getText();
			}
		});
        sqlTextViewer.setEditable(true);
        sqlDocument.set(this.sqlText);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(sqlTextViewer.getControl());
        
        
    }
    
    public String getSQL() {
    	return sqlText;
    }
    
    /*
     * Create the SQL Tab
     */
    private void createVdbXmlTabTab( TabFolder folderParent ) {
        Composite thePanel = createVdbXmlPanel(folderParent);

        this.vdbXmlTab = new TabItem(folderParent, SWT.NONE);
        this.vdbXmlTab.setControl(thePanel);
        this.vdbXmlTab.setText(Messages.PreviewDataInputDialog_previewXMLLabel);
    }
    
    /*
     * Create the SQL Display tab panel
     */
    private Composite createVdbXmlPanel( Composite parent ) {
        Composite thePanel = WidgetFactory.createPanel(parent, SWT.NONE, 1, 1);
        GridLayoutFactory.fillDefaults().margins(10, 10).applyTo(thePanel);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(thePanel);

        createXMLDisplayGroup(thePanel);
        return thePanel;
    }
    
    /*
     * Create the Group containing the DDL Contents (not editable)
     */
    private void createXMLDisplayGroup(Composite parent) {
        Group theGroup = WidgetFactory.createGroup(parent, Messages.PreviewDataInputDialog_previewDynamicVdbXmlTabLabel, GridData.FILL_BOTH, 1);
        GridLayoutFactory.fillDefaults().numColumns(1).equalWidth(false).margins(10, 10).applyTo(theGroup);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(theGroup);

        xmlContentsBox = new StyledText(theGroup, SWT.READ_ONLY | SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
        GridDataFactory.fillDefaults().grab(true, true).hint(400, 300).applyTo(xmlContentsBox);

        xmlContentsBox.setEditable(false);
        xmlContentsBox.setFont(monospaceFont);
        
        xmlContentsBox.setText(this.dynVdbXml);

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
}
