/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package com.metamatrix.modeler.modelgenerator.wsdl.ui.internal.wizards;

import java.util.ArrayList;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.text.source.VerticalRuler;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Tree;

import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.modelgenerator.wsdl.model.Model;
import com.metamatrix.modeler.modelgenerator.wsdl.model.Operation;
import com.metamatrix.modeler.modelgenerator.wsdl.model.WSDLElement;
import com.metamatrix.modeler.modelgenerator.wsdl.ui.ModelGeneratorWsdlUiConstants;
import com.metamatrix.modeler.modelgenerator.wsdl.ui.internal.util.ModelGeneratorWsdlUiUtil;
import com.metamatrix.modeler.transformation.ui.editors.sqleditor.SqlTextViewer;
import com.metamatrix.ui.graphics.ColorManager;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.util.WizardUtil;
import com.metamatrix.ui.internal.wizard.AbstractWizardPage;
import com.metamatrix.ui.tree.AbstractTreeContentProvider;

public class OperationsDetailsPage  extends AbstractWizardPage implements ModelGeneratorWsdlUiConstants {
    /** Used as a prefix to properties file keys. */
    private static final String PREFIX = I18nUtil.getPropertyPrefix(SelectWsdlOperationsPage.class);

    /** <code>IDialogSetting</code>s key for saved dialog height. */
    private static final String DIALOG_HEIGHT = "dialogHeight"; //$NON-NLS-1$

    /** <code>IDialogSetting</code>s key for saved dialog width. */
    private static final String DIALOG_WIDTH = "dialogWidth"; //$NON-NLS-1$

    /** <code>IDialogSetting</code>s key for saved dialog X position. */
    private static final String DIALOG_X = "dialogX"; //$NON-NLS-1$

    /** <code>IDialogSetting</code>s key for saved dialog Y position. */
    private static final String DIALOG_Y = "dialogY"; //$NON-NLS-1$

    /** The import manager. */
    WSDLImportWizardManager importManager;
    
    // ========== UI COMPONENTS =========================
    
    /** The checkbox treeViewer */
    private TreeViewer treeViewer;
    private Tree tree;
    
    TabFolder tabFolder;
    
    TabItem requestTab;
	TreeViewer requestXmlTreeViewer;
	TextViewer requestSqlTextViewer;
	IDocument requestSqlDocument;
	Action requestCreateColumnAction, requestSetRootPathAction;
	Button requestAddColumnButton, requestDeleteButton, requestUpButton, requestDownButton;
	
    TabItem responseTab;
    TreeViewer responseXmlTreeViewer;
	TextViewer responseSqlTextViewer;
	IDocument responseSqlDocument;
	Action responseCreateColumnAction, responseSetRootPathAction;
	Button responseAddColumnButton, responseDeleteButton, responseUpButton, responseDownButton;
    
    // ==================================================
    public OperationsDetailsPage( WSDLImportWizardManager theImportManager ) {
        super(OperationsDetailsPage.class.getSimpleName(), "Operations Details Title");
        this.importManager = theImportManager;
        this.importManager.setSelectedOperations(new ArrayList());
        setImageDescriptor(ModelGeneratorWsdlUiUtil.getImageDescriptor(Images.NEW_MODEL_BANNER));
    }
    
    /**
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     * @since 4.2
     */
    public void createControl( Composite theParent ) {
        final int COLUMNS = 1;
        Composite pnlMain = WidgetFactory.createPanel(theParent, SWT.NONE, GridData.FILL_BOTH);
        GridLayout layout = new GridLayout(COLUMNS, false);
        pnlMain.setLayout(layout);
        setControl(pnlMain);

        SashForm splitter = new SashForm(pnlMain, SWT.HORIZONTAL);
        GridData gid = new GridData();
        gid.grabExcessHorizontalSpace = gid.grabExcessVerticalSpace = true;
        gid.horizontalAlignment = gid.verticalAlignment = GridData.FILL;
        splitter.setLayoutData(gid);

        createOperationsListPanel(splitter);

        createTabbedDetailsPanel(splitter);

        splitter.setWeights(new int[] {30, 70});

        restoreState();
    }
    
    private void createOperationsListPanel( Composite parent) {
    	Composite panel = WidgetFactory.createPanel(parent);
    	
        GridLayout layout = new GridLayout(1, false);
        panel.setLayout(layout);

        // --------------------------
        // Group for checkbox tree
        // --------------------------
        Group operationsGroup = WidgetFactory.createGroup(panel, "Operations", GridData.FILL_BOTH, 1, 1);

        // ----------------------------
        // TreeViewer
        // ----------------------------
        this.treeViewer = WidgetFactory.createTreeViewer(operationsGroup, SWT.SINGLE | SWT.CHECK, GridData.FILL_BOTH);

        this.tree = this.treeViewer.getTree();

        tree.setLayoutData(new GridData(GridData.FILL_BOTH));
        OperationsListProvider provider = new OperationsListProvider();
        this.treeViewer.setContentProvider(provider);
        this.treeViewer.setLabelProvider(provider);

        this.treeViewer.setInput(null);
        
        // --------------------------
        // Group for checkbox tree
        // --------------------------
        Group optionsGroup = WidgetFactory.createGroup(panel, "Options", GridData.FILL_BOTH, 1, 2);
        
    }
    
    private void createTabbedDetailsPanel( Composite parent) {
    	//Composite panel = WidgetFactory.createPanel(parent);
        tabFolder = new TabFolder(parent, SWT.TOP | SWT.BORDER);
        tabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));

        createRequestTab(tabFolder);
        createResponseTab(tabFolder);
    }
    
    private void createRequestTab(TabFolder tabFolder) {
    	Composite panel = WidgetFactory.createPanel(tabFolder);
        this.requestTab = new TabItem(tabFolder, SWT.NONE);
        this.requestTab.setControl(panel);
        this.requestTab.setText("Request Procedure Details");
        
        createRequestSchemaContentsGroup(panel);
        createRequestColumnInfoGroup(panel);
        createRequestSqlGroup(panel);
    }
    
    private void createRequestSchemaContentsGroup(Composite parent) {
    	Group schemaContentsGroup = WidgetFactory.createGroup(parent, "Schema Contents", SWT.NONE, 1, 4); //$NON-NLS-1$
    	schemaContentsGroup.setLayout(new GridLayout(4, false));
    	GridData gd = new GridData(GridData.FILL_BOTH);
    	gd.heightHint = 160;
    	schemaContentsGroup.setLayoutData(gd);
		
    	
    	this.requestXmlTreeViewer = new TreeViewer(schemaContentsGroup, SWT.BORDER | SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL);
        GridData data = new GridData(GridData.FILL_BOTH);
        data.horizontalSpan=4;
        this.requestXmlTreeViewer.getControl().setLayoutData(data);
        this.requestXmlTreeViewer.setContentProvider(new AbstractTreeContentProvider() {
        	
            @Override
            public Object[] getChildren( Object element ) {
                return getNodeChildren(element);
            }

            public Object getParent( Object element ) {
                return getNodeParent(element);
            }

            @Override
            public boolean hasChildren( Object element ) {
                return getNodeHasChildren(element);
            }

        });
    	
        this.requestXmlTreeViewer.setLabelProvider(new LabelProvider() {

            @Override
            public Image getImage( Object element ) {
                return getNodeImage(element);
            }

            @Override
            public String getText( Object element ) {
                return getNodeName(element);
            }
        });
        
     // Add a Context Menu
        final MenuManager columnMenuManager = new MenuManager();
        this.requestXmlTreeViewer.getControl().setMenu(columnMenuManager.createContextMenu(parent));
        this.requestXmlTreeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            /**
             * {@inheritDoc}
             * 
             * @see oblafond@redhat.comrg.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
             */
            @Override
            public void selectionChanged( final SelectionChangedEvent event ) {
            	columnMenuManager.removeAll();
                IStructuredSelection sel = (IStructuredSelection)requestXmlTreeViewer.getSelection();
                if (sel.size() == 1) {
                	requestAddColumnButton.setEnabled(true);
					columnMenuManager.add(requestCreateColumnAction);
					columnMenuManager.add(requestSetRootPathAction);
                } else {
                	requestAddColumnButton.setEnabled(false);
                }

            }
        });
        
        this.requestXmlTreeViewer.addTreeListener(new ITreeViewerListener() {
			
			@Override
			public void treeExpanded(TreeExpansionEvent e) {
                IStructuredSelection sel = (IStructuredSelection)requestXmlTreeViewer.getSelection();
                requestAddColumnButton.setEnabled(sel.size() == 1);
			}
			
			@Override
			public void treeCollapsed(TreeExpansionEvent e) {
                IStructuredSelection sel = (IStructuredSelection)requestXmlTreeViewer.getSelection();
                requestAddColumnButton.setEnabled(sel.size() == 1);
			}
		} );
        
        this.requestCreateColumnAction = new Action("Add new column") { //$NON-NLS-1$
            @Override
            public void run() {
            	createRequestColumn();
            }
		};
		
        this.requestSetRootPathAction = new Action("Set as root path") { //$NON-NLS-1$
            @Override
            public void run() {
            	setRequestRootPath();
            }
		};
		
		requestAddColumnButton = new Button(schemaContentsGroup, SWT.PUSH);
		requestAddColumnButton.setText("Add selection as new column"); //$NON-NLS-1$
    	gd = new GridData();
    	gd.horizontalSpan = 1;
    	requestAddColumnButton.setLayoutData(gd);
    	requestAddColumnButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection sel = (IStructuredSelection)requestXmlTreeViewer.getSelection();
		    	Object obj = sel.getFirstElement();
		    	// TODO:
//		    	if( obj instanceof XmlElement ) {
//		    		createRequestColumn();
//		    	} else {
//					String newName = "column_" + (fileInfo.getColumnInfoList().length + 1); //$NON-NLS-1$
//					fileInfo.addColumn(newName, false, TeiidColumnInfo.DEFAULT_DATATYPE, null, null);
//		    	}
//				handleInfoChanged(false);
			}
    		
		});
    	requestAddColumnButton.setEnabled(false);
    }
    
    private void createRequestColumnInfoGroup(Composite parent) {
    	Group columnInfoGroup = WidgetFactory.createGroup(parent, "Column Info", SWT.NONE, 1);
    	columnInfoGroup.setLayout(new GridLayout(2, false));
    	GridData gd = new GridData(GridData.FILL_BOTH);
    	gd.heightHint = 150;
    	columnInfoGroup.setLayoutData(gd);
    }
    
    private void createRequestSqlGroup(Composite parent) {
    	Group group = WidgetFactory.createGroup(parent, "Generated SQL Statement", SWT.NONE, 1); //$NON-NLS-1$
    	group.setLayout(new GridLayout(1, false));
    	GridData gd = new GridData(GridData.FILL_BOTH);
    	gd.heightHint = 100;
    	group.setLayoutData(gd);
    	
    	ColorManager colorManager = new ColorManager();
        int styles = SWT.V_SCROLL | SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.FULL_SELECTION;

        requestSqlTextViewer = new SqlTextViewer(group, new VerticalRuler(0), styles, colorManager);
        requestSqlDocument = new Document();
        requestSqlTextViewer.setInput(requestSqlDocument);
        requestSqlTextViewer.setEditable(false);
        requestSqlTextViewer.getTextWidget().setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
        requestSqlDocument.set(CoreStringUtil.Constants.EMPTY_STRING);
        requestSqlTextViewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));

        //updateSqlText();
    }
    
    private void createResponseTab(TabFolder tabFolder) {
    	Composite panel = WidgetFactory.createPanel(tabFolder);
        this.responseTab = new TabItem(tabFolder, SWT.NONE);
        this.responseTab.setControl(panel);
        this.responseTab.setText("Request Procedure Details");
        
        createResponseSchemaContentsGroup(panel);
        createResponseColumnInfoGroup(panel);
        createResponseSqlGroup(panel);
    }
    
    private void createResponseSchemaContentsGroup(Composite parent) {
    	Group schemaContentsGroup = WidgetFactory.createGroup(parent, "Schema Contents", SWT.NONE, 1, 4); //$NON-NLS-1$
    	schemaContentsGroup.setLayout(new GridLayout(4, false));
    	GridData gd = new GridData(GridData.FILL_BOTH);
    	gd.heightHint = 160;
    	schemaContentsGroup.setLayoutData(gd);
		
    	
    	this.responseXmlTreeViewer = new TreeViewer(schemaContentsGroup, SWT.BORDER | SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL);
        GridData data = new GridData(GridData.FILL_BOTH);
        data.horizontalSpan=4;
        this.responseXmlTreeViewer.getControl().setLayoutData(data);
        this.responseXmlTreeViewer.setContentProvider(new AbstractTreeContentProvider() {
        	
            @Override
            public Object[] getChildren( Object element ) {
                return getNodeChildren(element);
            }

            public Object getParent( Object element ) {
                return getNodeParent(element);
            }

            @Override
            public boolean hasChildren( Object element ) {
                return getNodeHasChildren(element);
            }

        });
    	
        this.responseXmlTreeViewer.setLabelProvider(new LabelProvider() {

            @Override
            public Image getImage( Object element ) {
                return getNodeImage(element);
            }

            @Override
            public String getText( Object element ) {
                return getNodeName(element);
            }
        });
        
     // Add a Context Menu
        final MenuManager columnMenuManager = new MenuManager();
        this.responseXmlTreeViewer.getControl().setMenu(columnMenuManager.createContextMenu(parent));
        this.responseXmlTreeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            /**
             * {@inheritDoc}
             * 
             * @see oblafond@redhat.comrg.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
             */
            @Override
            public void selectionChanged( final SelectionChangedEvent event ) {
            	columnMenuManager.removeAll();
                IStructuredSelection sel = (IStructuredSelection)responseXmlTreeViewer.getSelection();
                if (sel.size() == 1) {
                	responseAddColumnButton.setEnabled(true);
					columnMenuManager.add(responseCreateColumnAction);
					columnMenuManager.add(responseSetRootPathAction);
                } else {
                	responseAddColumnButton.setEnabled(false);
                }

            }
        });
        
        this.responseXmlTreeViewer.addTreeListener(new ITreeViewerListener() {
			
			@Override
			public void treeExpanded(TreeExpansionEvent e) {
                IStructuredSelection sel = (IStructuredSelection)responseXmlTreeViewer.getSelection();
                responseAddColumnButton.setEnabled(sel.size() == 1);
			}
			
			@Override
			public void treeCollapsed(TreeExpansionEvent e) {
                IStructuredSelection sel = (IStructuredSelection)responseXmlTreeViewer.getSelection();
                responseAddColumnButton.setEnabled(sel.size() == 1);
			}
		} );
        
        this.responseCreateColumnAction = new Action("Add as new column") {
            @Override
            public void run() {
            	createResponseColumn();
            }
		};
		
        this.responseSetRootPathAction = new Action("Set as root path") {
            @Override
            public void run() {
            	setRequestRootPath();
            }
		};
		
		requestAddColumnButton = new Button(schemaContentsGroup, SWT.PUSH);
		requestAddColumnButton.setText("Add Selection As New Column");
    	gd = new GridData();
    	gd.horizontalSpan = 1;
    	requestAddColumnButton.setLayoutData(gd);
    	requestAddColumnButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection sel = (IStructuredSelection)requestXmlTreeViewer.getSelection();
		    	Object obj = sel.getFirstElement();
		    	// TODO:
//		    	if( obj instanceof XmlElement ) {
//		    		createRequestColumn();
//		    	} else {
//					String newName = "column_" + (fileInfo.getColumnInfoList().length + 1); //$NON-NLS-1$
//					fileInfo.addColumn(newName, false, TeiidColumnInfo.DEFAULT_DATATYPE, null, null);
//		    	}
//				handleInfoChanged(false);
			}
    		
		});
    	requestAddColumnButton.setEnabled(false);
    }
    
    private void createResponseColumnInfoGroup(Composite parent) {
    	Group columnInfoGroup = WidgetFactory.createGroup(parent, "Column Info", SWT.NONE, 1);
    	columnInfoGroup.setLayout(new GridLayout(2, false));
    	GridData gd = new GridData(GridData.FILL_BOTH);
    	gd.heightHint = 150;
    	columnInfoGroup.setLayoutData(gd);
    }
    
    private void createResponseSqlGroup(Composite parent) {
    	Group group = WidgetFactory.createGroup(parent, "Generated SQL Statement", SWT.NONE, 1); //$NON-NLS-1$
    	group.setLayout(new GridLayout(1, false));
    	GridData gd = new GridData(GridData.FILL_BOTH);
    	gd.heightHint = 100;
    	group.setLayoutData(gd);
    	
    	ColorManager colorManager = new ColorManager();
        int styles = SWT.V_SCROLL | SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.FULL_SELECTION;

        responseSqlTextViewer = new SqlTextViewer(group, new VerticalRuler(0), styles, colorManager);
        responseSqlDocument = new Document();
        responseSqlTextViewer.setInput(responseSqlDocument);
        responseSqlTextViewer.setEditable(false);
        responseSqlTextViewer.getTextWidget().setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
        responseSqlDocument.set(CoreStringUtil.Constants.EMPTY_STRING);
        responseSqlTextViewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));

        //updateSqlText();
    }
    
    /**
     * @see org.eclipse.jface.dialogs.IDialogPage#dispose()
     * @since 4.2
     */
    @Override
    public void dispose() {
        saveState();
    }

    /**
     * Override to replace the NewModelWizard settings with the section devoted to the Web Service Model Wizard.
     * 
     * @see org.eclipse.jface.wizard.WizardPage#getDialogSettings()
     * @since 4.2
     */
    @Override
    protected IDialogSettings getDialogSettings() {
        IDialogSettings settings = super.getDialogSettings();

        if (settings != null) {
            // get the right section of the NewModelWizard settings
            IDialogSettings temp = settings.getSection(DIALOG_SETTINGS_SECTION);

            if (temp == null) {
                settings = settings.addNewSection(DIALOG_SETTINGS_SECTION);
            } else {
                settings = temp;
            }
        }

        return settings;
    }
    
    /**
     * Restores dialog size and position of the last time wizard ran.
     * 
     * @since 4.2
     */
    private void restoreState() {
        IDialogSettings settings = getDialogSettings();

        if (settings != null) {
            Shell shell = getContainer().getShell();

            if (shell != null) {
                try {
                    int x = settings.getInt(DIALOG_X);
                    int y = settings.getInt(DIALOG_Y);
                    int width = settings.getInt(DIALOG_WIDTH);
                    int height = settings.getInt(DIALOG_HEIGHT);
                    shell.setBounds(x, y, width, height);
                } catch (NumberFormatException theException) {
                    // getInt(String) throws exception if not found.
                    // just means no settings exist yet.
                }
            }
        }
    }

    /**
     * Persists dialog size and position.
     * 
     * @since 4.2
     */
    private void saveState() {
        IDialogSettings settings = getDialogSettings();

        if (settings != null) {
            Shell shell = getContainer().getShell();

            if (shell != null) {
                Rectangle r = shell.getBounds();
                settings.put(DIALOG_X, r.x);
                settings.put(DIALOG_Y, r.y);
                settings.put(DIALOG_WIDTH, r.width);
                settings.put(DIALOG_HEIGHT, r.height);
            }
        }
    }

    /**
     * Sets the wizard page status message.
     * 
     * @since 4.2
     */
    void setPageStatus() {
    	// TODO:

        WizardUtil.setPageComplete(this);

        getContainer().updateButtons();
    }

    @Override
    public void setVisible( boolean isVisible ) {
        if (isVisible) {
        	// TODO: 
            setPageStatus();
        }
        super.setVisible(isVisible);
    }

    /**
     * Utility to get localized text from properties file.
     * 
     * @param theKey the key whose localized value is being requested
     * @return the localized text
     */
    private static String getString( String theKey ) {
        return UTIL.getString(new StringBuffer().append(PREFIX).append(theKey).toString());
    }

    Object[] getNodeChildren( Object element ) {
//        if (element instanceof TeiidXmlFileInfo) {
//            return new Object[] {this.fileInfo.getRootNode()};
//        }
//        return ((XmlElement)element).getChildrenDTDElements();
    	return new Object[0];
    }

    boolean getNodeHasChildren( Object element ) {
//        XmlElement node = (XmlElement)element;
//        Object[] children = node.getChildrenDTDElements();
//
//        return (children.length > 0);
    	return false;
    }
    Image getNodeImage( Object element ) {
    	return null;
    }
    
    String getNodeName( Object element ) {
        return "<name>";
    }

    Object getNodeParent( Object element ) {
        return null;
    }
    
    private void createRequestColumn() {
    	
    }
    
    private void setRequestRootPath() {
    	
    }
    
    private void createResponseColumn() {
    	
    }
    
    private void setResponsetRootPath() {
    	
    }
    
    class OperationsListProvider extends LabelProvider implements ITreeContentProvider {
    	private final Image OPERATION_ICON_IMG = ModelGeneratorWsdlUiUtil.getImage(Images.OPERATION_ICON);
    	
        public void dispose() {
        }

        public Object[] getChildren( final Object node ) {
//            if (wsdlModel != null) {
//                if (node instanceof Model) {
//                    return ((Model)node).getServices();
//                } else if (node instanceof Service) {
//                    return ((Service)node).getPorts();
//                } else if (node instanceof Port) {
//                    return new Object[] {((Port)node).getBinding()};
//                } else if (node instanceof Binding) {
//                    return ((Binding)node).getOperations();
//                }
//            }
            return CoreStringUtil.Constants.EMPTY_STRING_ARRAY;
        }

        public Object[] getElements( final Object inputElement ) {
            return getChildren(inputElement);
        }

        public Object getParent( final Object node ) {
//            if (wsdlModel != null) {
//                if (node instanceof Model) {
//                    return null;
//                } else if (node instanceof Service) {
//                    return null;
//                } else if (node instanceof Port) {
//                    return ((Port)node).getService();
//                } else if (node instanceof Binding) {
//                    return ((Binding)node).getPort();
//                } else if (node instanceof Operation) {
//                    return ((Operation)node).getBinding();
//                }
//            }
            return null;
        }

        public boolean hasChildren( final Object node ) {
//            if (wsdlModel != null) {
//                if (node instanceof Model) {
//                    return (((Model)node).getServices().length > 0);
//                } else if (node instanceof Service) {
//                    return (((Service)node).getPorts().length > 0);
//                } else if (node instanceof Port) {
//                    return ((Port)node).getBinding() != null;
//                } else if (node instanceof Binding) {
//                    return (((Binding)node).getOperations().length > 0);
//                }
//            }
            return false;
        }

        public void inputChanged( final Viewer viewer,
                                  final Object oldInput,
                                  final Object newInput ) {
        }
        
        @Override
        public Image getImage( final Object node ) {
            if (node instanceof Operation) {
                return OPERATION_ICON_IMG;
            } 
            return null;
        }

        @Override
        public String getText( final Object node ) {
            if (node instanceof Model) {
                return "theModel"; //$NON-NLS-1$
            } else if (node instanceof WSDLElement) {
                return ((WSDLElement)node).getName();
            }
            return "unknownElement"; //$NON-NLS-1$
        }
    }
    
    class EditColumnsPanel  {
    	TableViewer columnsViewer;
    	
		public EditColumnsPanel(Composite parent, int style) {
			super();
			createPanel(parent);
		}
		
		private void createPanel(Composite parent) {	      
	    	Table table = new Table(parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER );
	        table.setHeaderVisible(true);
	        table.setLinesVisible(true);
	        table.setLayout(new TableLayout());
	    	GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
	    	gd.heightHint = 80;
	    	table.setLayoutData(gd);

	        this.columnsViewer = new TableViewer(table);
	        this.columnsViewer.getControl().setLayoutData(gd);
	        
	        // create columns
	        TableViewerColumn column = new TableViewerColumn(this.columnsViewer, SWT.LEFT);
	        column.getColumn().setText("Column name");
//	        column.setEditingSupport(new ColumnInfoTextEditingSupport(this.columnsViewer, NAME_PROP));
//	        column.setLabelProvider(new ColumnDataLabelProvider(0));
	        column.getColumn().pack();
	        

	        column = new TableViewerColumn(this.columnsViewer, SWT.LEFT);
	        column.getColumn().setText("Datatype");
//	        column.setLabelProvider(new ColumnDataLabelProvider(2));
//	        column.setEditingSupport(new DatatypeComboEditingSupport(this.columnsViewer));
	        column.getColumn().pack();
	        
	        column = new TableViewerColumn(this.columnsViewer, SWT.LEFT);
	        column.getColumn().setText("Path");
//	        column.getColumn().setToolTipText(getString("pathTooltip")); //$NON-NLS-1$
//	        column.setLabelProvider(new ColumnDataLabelProvider(4));
//	        column.setEditingSupport(new ColumnInfoTextEditingSupport(this.columnsViewer, XML_PATH_PROP));
	        column.getColumn().pack();
	        
//	        if( fileInfo != null ) {
//		        for( TeiidColumnInfo row : fileInfo.getColumnInfoList() ) {
//		        	this.columnsViewer.add(row);
//		        }
//	        }
		}
        
		public void refresh() {
	    	this.columnsViewer.getTable().removeAll();
//	        for( TeiidColumnInfo row : fileInfo.getColumnInfoList() ) {
//	        	this.columnsViewer.add(row);
//	        }
		}
		
		public void refresh(Object element) {
			this.columnsViewer.refresh(element);
		}
		
		public void addSelectionListener(ISelectionChangedListener listener) {
			this.columnsViewer.addSelectionChangedListener(listener);
		}
		
		public Object getSelectedColumn() {
			
//			IStructuredSelection selection = (IStructuredSelection)this.columnsViewer.getSelection();
//			for( Object obj : selection.toArray()) {
//				if( obj instanceof TeiidColumnInfo ) {
//					return (TeiidColumnInfo) obj;
//				}
//			}
			
			return null;
		}
		
		public int getSelectedIndex() {
			return columnsViewer.getTable().getSelectionIndex();
		}
		
		public void selectRow(int index) {
			if( index > -1 ) {
				columnsViewer.getTable().select(index);
			} else {
				columnsViewer.setSelection(new StructuredSelection());
			}
		}
    	
    }
}
