/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package com.metamatrix.modeler.transformation.ui.wizards.sqlbuilder;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.text.source.VerticalRuler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.modeler.transformation.ui.Messages;
import com.metamatrix.modeler.transformation.ui.UiConstants.Images;
import com.metamatrix.modeler.transformation.ui.UiPlugin;
import com.metamatrix.modeler.transformation.ui.editors.sqleditor.SqlTextViewer;
import com.metamatrix.ui.graphics.ColorManager;
import com.metamatrix.ui.internal.util.WidgetFactory;

/*
 * SQLTemplateDialog - Display the relevant SQL Templates for Tables or Procedures, so that user can choose some SQL
 * to start editing.
 */
public class SQLTemplateDialog  extends TitleAreaDialog {

    public static int TABLE_TEMPLATES = 1;
    public static int PROC_TEMPLATES = 2;
    public static int ALL_TEMPLATES = 3;

    //=============================================================
    // Instance variables
    //=============================================================
    TextViewer sqlTextViewer;
    IDocument sqlDocument;
    int templatesToShow = -1;
    TabFolder tabFolder;
    TabItem selectTemplatesTab;
    TabItem procedureTemplatesTab;
    
    Button selectRB, selectJoinRB, unionRB, flatFileSourceRB;
    Button xmlFileLocalSourceRB, xmlFileUrlSourceRB;
    Button insertDefaultProcRB, updateDefaultProcRB, deleteDefaultProcRB, 
    	soapCreateProcRB, soapExtractProcRB, restProcedureRB;

    //=============================================================
    // Constructors
    //=============================================================
    /**
     * SQLTemplateDialog constructor.
     * 
     * @param parent parent of this dialog
     * @param templatesToShow flag to determine which types of Templates to display (TABLE_TEMPLATES, PROC_TEMPLATES or
     *        ALL_TEMPLATES)
     */
    public SQLTemplateDialog( Shell parent,
                              int templatesToShow ) {
        super(parent);
        this.templatesToShow = templatesToShow;
    }
    
    @Override
    protected void configureShell( Shell shell ) {
        super.configureShell(shell);
        shell.setText(Messages.sqlTemplateDialogTitle);
    }
    
    /* (non-Javadoc)
    * @see org.eclipse.jface.window.Window#setShellStyle(int)
    */
    @Override
    protected void setShellStyle( int newShellStyle ) {
        super.setShellStyle(newShellStyle | SWT.RESIZE | SWT.MAX);
    }
        
    //=============================================================
    // Instance methods
    //=============================================================

    @Override
    protected Control createDialogArea(Composite parent) {
        setTitle(Messages.sqlTemplateDialogTitle);
        setTitleImage(UiPlugin.getDefault().getImage(Images.IMPORT_TEIID_METADATA));
        setMessage(Messages.sqlTemplateDialogTitleMessage);
    	
        Composite composite = (Composite)super.createDialogArea(parent);
        //------------------------------        
        // Set layout for the Composite
        //------------------------------        
        GridLayout gridLayout = new GridLayout();
        composite.setLayout(gridLayout);
        gridLayout.numColumns = 1;
        GridData gridData = new GridData(GridData.FILL_BOTH);
        gridData.grabExcessHorizontalSpace = true;
        gridData.widthHint = 500;
        composite.setLayoutData(gridData);
        
        // Create the RadioButton Group for Template selection
        createSqlTemplateOptionsGroup(composite);
        
        // Create the SQL Text area for the SQL display
        createSqlGroup(composite);

        // Init SQL Text area with selection
        setSQLTemplateArea();

        return composite;
    }
    
    /*
     * Create the Group of Radio Buttons for template type selection
     */
    private void createSqlTemplateOptionsGroup( Composite parent ) {
        Group theGroup = WidgetFactory.createGroup(parent, Messages.sqlTemplateDialogOptionsGroup, SWT.NONE, 1, 2);
    	theGroup.setLayout(new GridLayout(2, false));
    	theGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
    	
        // **********************************
        // SQL Template Options
        // **********************************
        tabFolder = new TabFolder(theGroup, SWT.TOP);
        tabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));

        if (templatesToShow == TABLE_TEMPLATES || templatesToShow == ALL_TEMPLATES) {
            createSelectTemplatesTab(tabFolder);
        }

        if (templatesToShow == PROC_TEMPLATES || templatesToShow == ALL_TEMPLATES) {
            createProcedureTemplatesTab(tabFolder);
        }

        tabFolder.addSelectionListener(new SelectionListener() {
            public void widgetSelected( SelectionEvent e ) {
                setSQLTemplateArea();
            }

            public void widgetDefaultSelected( SelectionEvent e ) {
                widgetSelected(e);
            }
        });
    }

    /*
     * Create the Select Templates Tab
     */
    void createSelectTemplatesTab( TabFolder folderParent ) {
        // build the SELECT tab
        Composite thePanel = createSelectTemplatesPanel(folderParent);

        this.selectTemplatesTab = new TabItem(folderParent, SWT.NONE);
        this.selectTemplatesTab.setControl(thePanel);
        this.selectTemplatesTab.setText(Messages.sqlTemplateDialogSelectTabTitle);
    }

    /*
     * Create the Select Templates Tab
     */
    void createProcedureTemplatesTab( TabFolder folderParent ) {
        // build the SELECT tab
        Composite thePanel = createProcedureTemplatesPanel(folderParent);

        this.procedureTemplatesTab = new TabItem(folderParent, SWT.NONE);
        this.procedureTemplatesTab.setControl(thePanel);
        this.procedureTemplatesTab.setText(Messages.sqlTemplateDialogProceduresTabTitle);
    }

    private Composite createSelectTemplatesPanel( Composite parent ) {
        Composite thePanel = WidgetFactory.createPanel(parent, SWT.NONE, 1, 1);
        thePanel.setLayout(new GridLayout(1, false));
        GridData panelGD = new GridData(GridData.FILL_BOTH);
        panelGD.heightHint = 200;
        thePanel.setLayoutData(panelGD);

        // Simple SELECT Query
        this.selectRB = WidgetFactory.createRadioButton(thePanel, Messages.sqlTemplateDialogSelectLabel, SWT.NONE, 1, true);
        this.selectRB.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( final SelectionEvent event ) {
                setSQLTemplateArea();
            }
        });

        // SELECT With Join Criteria
        this.selectJoinRB = WidgetFactory.createRadioButton(thePanel,
                                                            Messages.sqlTemplateDialogSelectJoinLabel,
                                                            SWT.NONE,
                                                            2,
                                                            false);
        this.selectJoinRB.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( final SelectionEvent event ) {
                setSQLTemplateArea();
            }
        });

        // UNION Query
        this.unionRB = WidgetFactory.createRadioButton(thePanel, Messages.sqlTemplateDialogUnionLabel, SWT.NONE, 1, false);
        this.unionRB.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( final SelectionEvent event ) {
                setSQLTemplateArea();
            }
        });

        // Flat File Source Query
        this.flatFileSourceRB = WidgetFactory.createRadioButton(thePanel,
                                                                Messages.sqlTemplateDialogFlatFileSrcLabel,
                                                                SWT.NONE,
                                                                1,
                                                                false);
        this.flatFileSourceRB.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( final SelectionEvent event ) {
                setSQLTemplateArea();
            }
        });

        // XML File Local Source Query
        this.xmlFileLocalSourceRB = WidgetFactory.createRadioButton(thePanel,
                                                                    Messages.sqlTemplateDialogXmlFileLocalSrcLabel,
                                                                    SWT.NONE,
                                                                    1,
                                                                    false);
        this.xmlFileLocalSourceRB.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( final SelectionEvent event ) {
                setSQLTemplateArea();
            }
        });

        // XML File URL Source Query
        this.xmlFileUrlSourceRB = WidgetFactory.createRadioButton(thePanel,
                                                                  Messages.sqlTemplateDialogXmlFileUrlSrcLabel,
                                                                  SWT.NONE,
                                                                  1,
                                                                  false);
        this.xmlFileUrlSourceRB.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( final SelectionEvent event ) {
                setSQLTemplateArea();
            }
        });

        return thePanel;
    }

    private Composite createProcedureTemplatesPanel( Composite parent ) {
        Composite thePanel = WidgetFactory.createPanel(parent, SWT.NONE, 1, 1);
        thePanel.setLayout(new GridLayout(1, false));
        GridData panelGD = new GridData(GridData.FILL_BOTH);
        panelGD.heightHint = 200;
        thePanel.setLayoutData(panelGD);

        // Default Insert Procedure SQL
        this.insertDefaultProcRB = WidgetFactory.createRadioButton(thePanel,
                                                                   Messages.sqlTemplateDialogInsDefaultProcLabel,
                                                                   SWT.NONE,
                                                                   1,
                                                                   true);
        this.insertDefaultProcRB.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( final SelectionEvent event ) {
                setSQLTemplateArea();
            }
        });

        // Default Update Procedure SQL
        this.updateDefaultProcRB = WidgetFactory.createRadioButton(thePanel,
                                                                   Messages.sqlTemplateDialogUpdDefaultProcLabel,
                                                                   SWT.NONE,
                                                                   1,
                                                                   false);
        this.updateDefaultProcRB.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( final SelectionEvent event ) {
                setSQLTemplateArea();
            }
        });

        // Default Delete Procedure SQL
        this.deleteDefaultProcRB = WidgetFactory.createRadioButton(thePanel,
                                                                   Messages.sqlTemplateDialogDelDefaultProcLabel,
                                                                   SWT.NONE,
                                                                   1,
                                                                   false);
        this.deleteDefaultProcRB.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( final SelectionEvent event ) {
                setSQLTemplateArea();
            }
        });

        // SOAP WebService "Create" Procedure SQL
        this.soapCreateProcRB = WidgetFactory.createRadioButton(thePanel,
                                                                Messages.sqlTemplateDialogSoapCreateProcLabel,
                                                                SWT.NONE,
                                                                1,
                                                                false);
        this.soapCreateProcRB.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( final SelectionEvent event ) {
                setSQLTemplateArea();
            }
        });

        // SOAP WebService Extract Procedure SQL
        this.soapExtractProcRB = WidgetFactory.createRadioButton(thePanel,
                                                                 Messages.sqlTemplateDialogSoapExtractProcLabel,
                                                                 SWT.NONE,
                                                                 1,
                                                                 false);
        this.soapExtractProcRB.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( final SelectionEvent event ) {
                setSQLTemplateArea();
            }
        });
        
        // REST Procedure SQL
        this.restProcedureRB = WidgetFactory.createRadioButton(thePanel,
                                                                 Messages.sqlTemplateDialogRestProcLabel,
                                                                 SWT.NONE,
                                                                 1,
                                                                 false);
        this.restProcedureRB.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( final SelectionEvent event ) {
                setSQLTemplateArea();
            }
        });

        return thePanel;
    }

    /*
     * Set the SQL Display Area, based on the selected Template type
     */
    private void setSQLTemplateArea() {
        // Table Template buttons
        if (this.tabFolder.getSelection()[0] == selectTemplatesTab) {
            if (this.selectRB.getSelection()) {
                sqlTextViewer.getDocument().set(SQLTemplates.SELECT_SIMPLE);
            } else if (this.selectJoinRB.getSelection()) {
                sqlTextViewer.getDocument().set(SQLTemplates.SELECT_JOIN);
            } else if (this.unionRB.getSelection()) {
                sqlTextViewer.getDocument().set(SQLTemplates.SELECT_UNION);
            } else if (this.flatFileSourceRB.getSelection()) {
                sqlTextViewer.getDocument().set(SQLTemplates.SELECT_FLATFILE_SRC);
            } else if (this.xmlFileLocalSourceRB.getSelection()) {
                sqlTextViewer.getDocument().set(SQLTemplates.SELECT_XMLFILE_LOCAL_SRC);
            } else if (this.xmlFileUrlSourceRB.getSelection()) {
                sqlTextViewer.getDocument().set(SQLTemplates.SELECT_XMLFILE_URL_SRC);
            }
        }

        // Procedure Template buttons
        if (this.tabFolder.getSelection()[0] == procedureTemplatesTab) {
            if (this.insertDefaultProcRB.getSelection()) {
                sqlTextViewer.getDocument().set(SQLTemplates.PROC_INSERT_DEFAULT);
            } else if (this.updateDefaultProcRB.getSelection()) {
                sqlTextViewer.getDocument().set(SQLTemplates.PROC_UPDATE_DEFAULT);
            } else if (this.deleteDefaultProcRB.getSelection()) {
                sqlTextViewer.getDocument().set(SQLTemplates.PROC_DELETE_DEFAULT);
            } else if (this.soapCreateProcRB.getSelection()) {
                sqlTextViewer.getDocument().set(SQLTemplates.PROC_SOAP_WS_CREATE);
            } else if (this.soapExtractProcRB.getSelection()) {
                sqlTextViewer.getDocument().set(SQLTemplates.PROC_SOAP_WS_EXTRACT);
            } else if (this.restProcedureRB.getSelection()) {
                sqlTextViewer.getDocument().set(SQLTemplates.REST_PROCEDURE);
            }
        }

    }
    
    /*
     * Create the Group containing the SQL Text Viewer (not editable)
     */
    private void createSqlGroup( Composite parent ) {
        Group textTableOptionsGroup = WidgetFactory.createGroup(parent, Messages.sqlTemplateDialogSqlAreaGroup, SWT.NONE, 2, 1);
        textTableOptionsGroup.setLayout(new GridLayout(1, false));
        GridData gd = new GridData(GridData.FILL_BOTH);
        gd.heightHint = 120;
        gd.horizontalSpan = 2;
        textTableOptionsGroup.setLayoutData(gd);

        ColorManager colorManager = new ColorManager();
        int styles = SWT.V_SCROLL | SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.FULL_SELECTION;

        sqlTextViewer = new SqlTextViewer(textTableOptionsGroup, new VerticalRuler(0), styles, colorManager);
        sqlDocument = new Document();
        sqlTextViewer.setInput(sqlDocument);
        sqlTextViewer.setEditable(false);
        sqlTextViewer.getTextWidget().setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
        sqlDocument.set(CoreStringUtil.Constants.EMPTY_STRING);
        sqlTextViewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
    }

    /*
     * Get the currently selected Template SQL
     */
    public String getSQL() {
        return sqlDocument.get();
    }

    @Override
    public void create() {
        super.create();
        getButton(IDialogConstants.OK_ID).setEnabled(true);
    }
        
    @Override
    protected void okPressed() {
        super.okPressed();
    }

}
