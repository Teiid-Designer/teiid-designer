/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.ui.wizards.xmlfile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.text.source.VerticalRuler;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.teiid.core.types.DataTypeManager;

import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.core.util.StringUtilities;
import com.metamatrix.modeler.transformation.ui.PluginConstants;
import com.metamatrix.modeler.transformation.ui.UiConstants;
import com.metamatrix.modeler.transformation.ui.UiPlugin;
import com.metamatrix.modeler.transformation.ui.editors.sqleditor.SqlTextViewer;
import com.metamatrix.modeler.transformation.ui.wizards.file.TeiidColumnInfo;
import com.metamatrix.modeler.transformation.ui.wizards.file.TeiidMetadataImportInfo;
import com.metamatrix.ui.graphics.ColorManager;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.util.WizardUtil;
import com.metamatrix.ui.internal.wizard.AbstractWizardPage;
import com.metamatrix.ui.table.CheckBoxEditingSupport;
import com.metamatrix.ui.table.ComboBoxEditingSupport;
import com.metamatrix.ui.tree.AbstractTreeContentProvider;

public class TeiidXmlImportXmlConfigurationPage extends AbstractWizardPage implements
		UiConstants {
	// ===========================================================================================================================
	// Constants

	private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(TeiidXmlImportXmlConfigurationPage.class);

	private static final String TITLE = getString("title"); //$NON-NLS-1$
	private static final String INITIAL_MESSAGE = getString("initialMessage"); //$NON-NLS-1$

    private final String EMPTY = ""; //$NON-NLS-1$
    private final int NAME_PROP = 0;
    private final int DEFAULT_VALUE_PROP = 1;
    private final int XML_PATH_PROP = 2;

	private static String getString(final String id) {
		return Util.getString(I18N_PREFIX + id);
	}

	private TeiidMetadataImportInfo info;

	// Target SQL Variables
	Group headerGroup;
	TreeViewer xmlTreeViewer;
	TextViewer sqlTextViewer;
	IDocument sqlDocument;
	Text rootPathText, selectedFileText;
	Button parseRowButton;
	Action createColumnAction, setRootPathAction;
	Button addColumnButton, deleteButton, upButton, downButton;
	
	EditColumnsPanel columnsPanel;	
	
	private TeiidXmlFileInfo fileInfo;
	
	boolean creatingControl = false;

	boolean synchronizing = false;

	/**
	 * @since 4.0
	 */
	public TeiidXmlImportXmlConfigurationPage(TeiidMetadataImportInfo info) {
		super(TeiidXmlImportXmlConfigurationPage.class.getSimpleName(), TITLE);
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

		setMessage(INITIAL_MESSAGE);

        createFileContentsGroup(mainPanel);

        createColumnInfoGroup(mainPanel);
        
        createXmlTableSqlGroup(mainPanel);
        
		creatingControl = false;

		setPageComplete(false);
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

	private boolean validatePage() {

		setThisPageComplete(StringUtilities.EMPTY_STRING, NONE);
		
		return true;
	}
	
    private void setThisPageComplete( String message, int severity) {
    	WizardUtil.setPageComplete(this, message, severity);
    }

	private void synchronizeUI() {
		synchronizing = true;

		selectedFileText.setText(fileInfo.getDataFile().getName());
    	
    	this.rootPathText.setText(this.fileInfo.getRootPath());

		synchronizing = false;
	}
    
    private void createXmlTableSqlGroup(Composite parent) {
    	Group xmlTableOptionsGroup = WidgetFactory.createGroup(parent, getString("teiidXMLTableGroup"), SWT.NONE, 1); //$NON-NLS-1$
    	xmlTableOptionsGroup.setLayout(new GridLayout(1, false));
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
    
    private void createFileContentsGroup(Composite parent) {
    	Group fileContentsGroup = WidgetFactory.createGroup(parent, getString("fileContentsGroup"), SWT.NONE, 1, 4); //$NON-NLS-1$
    	fileContentsGroup.setLayout(new GridLayout(4, false));
    	GridData gd = new GridData(GridData.FILL_BOTH);
    	gd.heightHint = 160;
    	fileContentsGroup.setLayoutData(gd);
    	
		Label selectedFileLabel = new Label(fileContentsGroup, SWT.NONE);
		selectedFileLabel.setText(getString("selectedXmlFile")); //$NON-NLS-1$
		
        selectedFileText = new Text(fileContentsGroup, SWT.BORDER | SWT.SINGLE);
        selectedFileText.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
        selectedFileText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
		selectedFileText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		selectedFileText.setEditable(false);
    	
    	this.xmlTreeViewer = new TreeViewer(fileContentsGroup, SWT.BORDER | SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL);
        GridData data = new GridData(GridData.FILL_BOTH);
        data.horizontalSpan=4;
        this.xmlTreeViewer.getControl().setLayoutData(data);
        this.xmlTreeViewer.setContentProvider(new AbstractTreeContentProvider() {
        	
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
    	
        this.xmlTreeViewer.setLabelProvider(new LabelProvider() {

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
        this.xmlTreeViewer.getControl().setMenu(columnMenuManager.createContextMenu(parent));
        this.xmlTreeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            /**
             * {@inheritDoc}
             * 
             * @see oblafond@redhat.comrg.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
             */
            @Override
            public void selectionChanged( final SelectionChangedEvent event ) {
            	columnMenuManager.removeAll();
                IStructuredSelection sel = (IStructuredSelection)xmlTreeViewer.getSelection();
                if (sel.size() == 1) {
                	addColumnButton.setEnabled(true);
					columnMenuManager.add(createColumnAction);
					columnMenuManager.add(setRootPathAction);
                } else {
                	addColumnButton.setEnabled(false);
                }

            }
        });
        
        this.xmlTreeViewer.addTreeListener(new ITreeViewerListener() {
			
			@Override
			public void treeExpanded(TreeExpansionEvent e) {
                IStructuredSelection sel = (IStructuredSelection)xmlTreeViewer.getSelection();
                addColumnButton.setEnabled(sel.size() == 1);
			}
			
			@Override
			public void treeCollapsed(TreeExpansionEvent e) {
                IStructuredSelection sel = (IStructuredSelection)xmlTreeViewer.getSelection();
                addColumnButton.setEnabled(sel.size() == 1);
			}
		} );
        
        this.createColumnAction = new Action(getString("createColumnActionLabel")) { //$NON-NLS-1$
            @Override
            public void run() {
            	createColumn();
            }
		};
		
        this.setRootPathAction = new Action(getString("setAsRootpathActionLabel")) { //$NON-NLS-1$
            @Override
            public void run() {
            	setRootPath();
            }
		};
		
    	addColumnButton = new Button(fileContentsGroup, SWT.PUSH);
    	addColumnButton.setText(getString("addColumnButtonLabel")); //$NON-NLS-1$
    	gd = new GridData();
    	gd.horizontalSpan = 1;
    	addColumnButton.setLayoutData(gd);
    	addColumnButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection sel = (IStructuredSelection)xmlTreeViewer.getSelection();
		    	Object obj = sel.getFirstElement();
		    	if( obj instanceof XmlElement ) {
		    		createColumn();
		    	} else {
					String newName = "column_" + (fileInfo.getColumnInfoList().length + 1); //$NON-NLS-1$
					fileInfo.addColumn(newName, false, TeiidColumnInfo.DEFAULT_DATATYPE, null, null);
		    	}
				handleInfoChanged(false);
			}
    		
		});
    	addColumnButton.setEnabled(false);


    	//LayoutDebugger.debugLayout(fileContentsGroup);
    }
    
    

    
    private void createColumnInfoGroup(Composite parent) {
    	Group columnInfoGroup = WidgetFactory.createGroup(parent, getString("columnInfoGroup"), SWT.NONE, 1); //$NON-NLS-1$
    	columnInfoGroup.setLayout(new GridLayout(2, false));
    	GridData gd = new GridData(GridData.FILL_BOTH);
    	gd.heightHint = 150;
    	columnInfoGroup.setLayoutData(gd);

    	Label prefixLabel = new Label(columnInfoGroup, SWT.NONE);
    	prefixLabel.setToolTipText(getString("rootPathTooltip")); //$NON-NLS-1$
    	prefixLabel.setText(getString("rootPathLabel")); //$NON-NLS-1$
        
    	rootPathText = WidgetFactory.createTextField(columnInfoGroup, SWT.NONE);
    	gd = new GridData(GridData.FILL_HORIZONTAL);
    	gd.minimumWidth = 50;
    	gd.horizontalSpan=1;
    	gd.grabExcessHorizontalSpace = true;
    	rootPathText.setLayoutData(gd);
    	rootPathText.setToolTipText(getString("rootPathTooltip")); //$NON-NLS-1$
    	rootPathText.addModifyListener(new ModifyListener() {
    		public void modifyText( final ModifyEvent event ) {
    			if( !synchronizing ) {
        			fileInfo.setRootPath(rootPathText.getText());
        			handleInfoChanged(false);
    			}
    		}
    	});

    	Composite leftToolbarPanel = new Composite(columnInfoGroup, SWT.NONE);
    	leftToolbarPanel.setLayout(new GridLayout());
	  	GridData ltpGD = new GridData(GridData.FILL_VERTICAL);
	  	ltpGD.heightHint=120;
	  	leftToolbarPanel.setLayoutData(ltpGD);
    	
    	Button addButton = new Button(leftToolbarPanel, SWT.PUSH);
    	addButton.setText(getString("addLabel")); //$NON-NLS-1$
    	addButton.setToolTipText(getString("addButtonTooltip")); //$NON-NLS-1$
    	addButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    	addButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
//				IStructuredSelection sel = (IStructuredSelection)xmlTreeViewer.getSelection();
//		    	Object obj = sel.getFirstElement();
//		    	if( obj instanceof XmlElement ) {
//		    		createColumn();
//		    	} else {
					String newName = "column_" + (fileInfo.getColumnInfoList().length + 1); //$NON-NLS-1$
					fileInfo.addColumn(newName, false, TeiidColumnInfo.DEFAULT_DATATYPE, null, null);
//		    	}
				handleInfoChanged(false);
			}
    		
		});
    	
    	deleteButton = new Button(leftToolbarPanel, SWT.PUSH);
    	deleteButton.setText(getString("deleteLabel")); //$NON-NLS-1$
    	deleteButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    	deleteButton.setEnabled(false);
    	deleteButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				TeiidColumnInfo info = columnsPanel.getSelectedColumn();
				if( info != null ) {
					fileInfo.removeColumn(info);
					handleInfoChanged(false);
					deleteButton.setEnabled(false);
					columnsPanel.selectRow(-1);
				}
			}
    		
		});
    	
    	upButton = new Button(leftToolbarPanel, SWT.PUSH);
    	upButton.setText(getString("upLabel")); //$NON-NLS-1$
    	upButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    	upButton.setEnabled(false);
    	upButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				TeiidColumnInfo info = columnsPanel.getSelectedColumn();
				if( info != null ) {
					int selectedIndex = columnsPanel.getSelectedIndex();
					fileInfo.moveColumnUp(info);
					handleInfoChanged(false);
					columnsPanel.selectRow(selectedIndex-1);
					downButton.setEnabled(fileInfo.canMoveDown(info));
					upButton.setEnabled(fileInfo.canMoveUp(info));
				}
			}
    		
		});
    	
    	downButton = new Button(leftToolbarPanel, SWT.PUSH);
    	downButton.setText(getString("downLabel")); //$NON-NLS-1$
    	downButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    	downButton.setEnabled(false);
    	downButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				TeiidColumnInfo info = columnsPanel.getSelectedColumn();
				if( info != null ) {
					int selectedIndex = columnsPanel.getSelectedIndex();
					fileInfo.moveColumnDown(info);
					handleInfoChanged(false);
					columnsPanel.selectRow(selectedIndex+1);
					downButton.setEnabled(fileInfo.canMoveDown(info));
					upButton.setEnabled(fileInfo.canMoveUp(info));
				}
			}
    		
		});
    	
    	columnsPanel = new EditColumnsPanel(columnInfoGroup, SWT.NONE);
    	
    	columnsPanel.addSelectionListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection sel = (IStructuredSelection)event.getSelection();
				
				if( sel.isEmpty()) {
					deleteButton.setEnabled(false);
					upButton.setEnabled(false);
					downButton.setEnabled(false);
				} else {
					boolean enable = true;
					Object[] objs = sel.toArray();
					TeiidColumnInfo columnInfo = null;
					for( Object obj : objs) {
						if(  !(obj instanceof TeiidColumnInfo)) {
							enable = false;
							break;
						} else {
							columnInfo = (TeiidColumnInfo)obj;
						}
					} 
					if( objs.length == 0 ) {
						enable = false;
					}
					deleteButton.setEnabled(enable);
					if( enable ) {
						upButton.setEnabled(fileInfo.canMoveUp(columnInfo));
						downButton.setEnabled(fileInfo.canMoveDown(columnInfo));
					}
					
				}
				
			}
		});
    	
    	
    	//LayoutDebugger.debugLayout(columnInfoGroup);
       
    }
    
    private String getSpaces(int nSpaces) {
    	StringBuffer sb = new StringBuffer(nSpaces);
    	for( int i=0; i<nSpaces; i++ ) {
    		sb.append(StringUtilities.SPACE);
    	}
    	return sb.toString();
    }
    
    private void setRootPath() {
    	IStructuredSelection sel = (IStructuredSelection)xmlTreeViewer.getSelection();
    	Object obj = sel.getFirstElement();
    	if( obj instanceof XmlElement ) {
    		String pathValue = ((XmlElement)obj).getFullPath();
    		this.fileInfo.setRootPath(pathValue);
    		handleInfoChanged(false);
    	}
    }
    
    private void createColumn() {
    	IStructuredSelection sel = (IStructuredSelection)xmlTreeViewer.getSelection();
    	Object obj = sel.getFirstElement();
    	if( obj instanceof XmlElement ) {
    		XmlElement element = (XmlElement)obj;
    		String newName =  element.getName();
    		String rootPath = this.fileInfo.getRootPath();
    		fileInfo.addColumn(newName, false, TeiidColumnInfo.DEFAULT_DATATYPE, null, rootPath, element);
    		
			handleInfoChanged(false);
    	}
    
    }
    
    private void handleInfoChanged(boolean reloadFileContents) {
    	if( synchronizing ) return;
    	
    	synchronizeUI();

    	this.columnsPanel.refresh();
    	
    	updateSqlText();
        
        validatePage();
    }
    
    private void loadFileContentsViewer() {
    	this.xmlTreeViewer.setInput(fileInfo); //.getRootNode());
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
    
    Object[] getNodeChildren( Object element ) {
        if (element instanceof TeiidXmlFileInfo) {
            return new Object[] {this.fileInfo.getRootNode()};
        }
        return ((XmlElement)element).getChildrenDTDElements();
    }

    boolean getNodeHasChildren( Object element ) {
        XmlElement node = (XmlElement)element;
        Object[] children = node.getChildrenDTDElements();

        return (children.length > 0);
    }
    Image getNodeImage( Object element ) {
        // There is an EMF bug that prevents maxOccurs values other than 1 from being stored correctly for particles with model
        // group definition content, so just show image of underlying model group
    	return null;
    }
    
    String getNodeName( Object element ) {
        XmlElement node = (XmlElement)element;

        return node.getName();
    }

    Object getNodeParent( Object element ) {
        return ((XmlElement)element).getParent();
    }

	class ColumnDataLabelProvider extends ColumnLabelProvider {

		private final int columnNumber;

		public ColumnDataLabelProvider(int columnNumber) {
			this.columnNumber = columnNumber;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see org.eclipse.jface.viewers.ColumnLabelProvider#getText(java.lang.Object)
		 */
		@Override
		public String getText(Object element) {
			if( element instanceof TeiidColumnInfo ) {
				switch (this.columnNumber) {
					case 0: {
						if(element instanceof TeiidColumnInfo) {
							return ((TeiidColumnInfo)element).getName();
						}
					}
					case 1: {
						return EMPTY;
					}
					case 2: {
						if(element instanceof TeiidColumnInfo) {
							return ((TeiidColumnInfo)element).getDatatype();
						}
					}
					case 3: {
						if(element instanceof TeiidColumnInfo) {
							return ((TeiidColumnInfo)element).getDefaultValue();
						}
					}
					case 4: {
						if(element instanceof TeiidColumnInfo) {
							return ((TeiidColumnInfo)element).getRelativePath();
						}
					}
				}
			}
			return EMPTY;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see org.eclipse.jface.viewers.CellLabelProvider#getToolTipText(java.lang.Object)
		 */
		@Override
		public String getToolTipText(Object element) {
			switch (this.columnNumber) {
			case 0: {
				return "Tooltip 1"; //getString("columnNameColumnTooltip"); //$NON-NLS-1$
			}
			case 1: {
				return "Tooltip 2"; //getString("datatypeColumnTooltip"); //$NON-NLS-1$
			}
		}
		return "unknown tooltip"; //$NON-NLS-1$
		}

		@Override
		public Image getImage(Object element) {
			if( this.columnNumber == 0 ) {
				return UiPlugin.getDefault().getImage(UiConstants.Images.COLUMN_ICON);
			} else if( this.columnNumber == 1 ) {
				if(element instanceof TeiidColumnInfo) {
					if( ((TeiidColumnInfo)element).getOrdinality() ) {
						return UiPlugin.getDefault().getImage(PluginConstants.Images.CHECKED_BOX_ICON);
					} else {
						return UiPlugin.getDefault().getImage(PluginConstants.Images.UNCHECKED_BOX_ICON);
					}
				}
				return null;
			}
			return null;
		}
		
		
	}
    
    class ColumnInfoTextEditingSupport extends EditingSupport {
    	
		private TextCellEditor editor;
		private int type;

		/**
		 * Create a new instance of the receiver.
		 * 
		 * @param viewer
		 */
		public ColumnInfoTextEditingSupport(ColumnViewer viewer, int type) {
			super(viewer);
			this.type = type;
			this.editor = new TextCellEditor((Composite) viewer.getControl());
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.EditingSupport#canEdit(java.lang.Object)
		 */
		protected boolean canEdit(Object element) {
			return true;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.EditingSupport#getCellEditor(java.lang.Object)
		 */
		protected CellEditor getCellEditor(Object element) {
			return editor;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.EditingSupport#getValue(java.lang.Object)
		 */
		protected Object getValue(Object element) {
			if( element instanceof TeiidColumnInfo ) {
				switch(this.type) {
					case NAME_PROP: {
						return ((TeiidColumnInfo)element).getName();
					}
					case DEFAULT_VALUE_PROP: {
						return ((TeiidColumnInfo)element).getDefaultValue();
					}
					case XML_PATH_PROP: {
						return ((TeiidColumnInfo)element).getRelativePath();
					}
				}
			}
			return EMPTY;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.EditingSupport#setValue(java.lang.Object,
		 *      java.lang.Object)
		 */
		protected void setValue(Object element, Object value) {
			if( element instanceof TeiidColumnInfo ) {
				switch(this.type) {
					case NAME_PROP: {
						String oldValue = ((TeiidColumnInfo)element).getName();
						String newValue = (String)value;
						if( newValue != null && newValue.length() > 0 && !newValue.equalsIgnoreCase(oldValue)) {
							((TeiidColumnInfo)element).setName(newValue);
							columnsPanel.refresh(element);
					    	fileInfo.columnChanged((TeiidColumnInfo)element);
							handleInfoChanged(false);
						}
					} break;
					case DEFAULT_VALUE_PROP: {
						String oldValue = ((TeiidColumnInfo)element).getDefaultValue();
						String newValue = (String)value;
						if( newValue != null && newValue.length() > 0 && !newValue.equalsIgnoreCase(oldValue)) {
							((TeiidColumnInfo)element).setDefaultValue(newValue);
							columnsPanel.refresh(element);
							fileInfo.columnChanged((TeiidColumnInfo)element);
							handleInfoChanged(false);
						}
					} break;
					case XML_PATH_PROP: {
						String oldValue = ((TeiidColumnInfo)element).getRelativePath();
						String newValue = (String)value;
						if( newValue != null && newValue.length() > 0 && !newValue.equalsIgnoreCase(oldValue)) {
							((TeiidColumnInfo)element).setRelativePath(newValue);
							columnsPanel.refresh(element);
							fileInfo.columnChanged((TeiidColumnInfo)element);
							handleInfoChanged(false);
						}
					} break;
				}
				
			}
		}

	}
    
    class DatatypeComboEditingSupport extends ComboBoxEditingSupport {
    	
    	private String[] datatypes;
        /**
         * @param viewer
         */
        public DatatypeComboEditingSupport( ColumnViewer viewer ) {
            super(viewer);
    		Set<String> unsortedDatatypes = DataTypeManager.getAllDataTypeNames();
    		Collection<String> dTypes = new ArrayList<String>();
    		
    		String[] sortedStrings = unsortedDatatypes.toArray(new String[unsortedDatatypes.size()]);
    		Arrays.sort(sortedStrings);
    		for( String dType : sortedStrings ) {
    			dTypes.add(dType);
    		}
    		
    		datatypes = dTypes.toArray(new String[dTypes.size()]);
    		
        }


        @Override
        protected String getElementValue( Object element ) {
        	return ((TeiidColumnInfo)element).getDatatype();
        }

        @Override
        protected String[] refreshItems( Object element ) {
            return datatypes;
        }

        @Override
        protected void setElementValue( Object element,
                                        String newValue ) {
        	if( !((TeiidColumnInfo)element).getOrdinality() ) {
	            ((TeiidColumnInfo)element).setDatatype(newValue);
	            handleInfoChanged(false);
        	}
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
	        column.getColumn().setText(getString("columnName") + getSpaces(25)); //$NON-NLS-1$
	        column.setEditingSupport(new ColumnInfoTextEditingSupport(this.columnsViewer, NAME_PROP));
	        column.setLabelProvider(new ColumnDataLabelProvider(0));
	        column.getColumn().pack();
	        
	        column = new TableViewerColumn(this.columnsViewer, SWT.LEFT);
	        column.getColumn().setText(getString("forOrdinality")); //$NON-NLS-1$ 
	        column.setLabelProvider(new ColumnDataLabelProvider(1));
	        column.setEditingSupport(new OrdinalityEditingSupport(this.columnsViewer));
	        column.getColumn().pack();

	        column = new TableViewerColumn(this.columnsViewer, SWT.LEFT);
	        column.getColumn().setText(getString("datatype") + getSpaces(2)); //$NON-NLS-1$ 
	        column.setLabelProvider(new ColumnDataLabelProvider(2));
	        column.setEditingSupport(new DatatypeComboEditingSupport(this.columnsViewer));
	        column.getColumn().pack();
	        
	        column = new TableViewerColumn(this.columnsViewer, SWT.LEFT);
	        column.getColumn().setText(getString("defaultValue") + getSpaces(2)); //$NON-NLS-1$ 
	        column.setLabelProvider(new ColumnDataLabelProvider(3));
	        column.setEditingSupport(new ColumnInfoTextEditingSupport(this.columnsViewer, DEFAULT_VALUE_PROP));
	        column.getColumn().pack();
	        
	        column = new TableViewerColumn(this.columnsViewer, SWT.LEFT);
	        column.getColumn().setText(getString("path")); //$NON-NLS-1$ 
	        column.getColumn().setToolTipText(getString("pathTooltip")); //$NON-NLS-1$
	        column.setLabelProvider(new ColumnDataLabelProvider(4));
	        column.setEditingSupport(new ColumnInfoTextEditingSupport(this.columnsViewer, XML_PATH_PROP));
	        column.getColumn().pack();
	        
	        if( fileInfo != null ) {
		        for( TeiidColumnInfo row : fileInfo.getColumnInfoList() ) {
		        	this.columnsViewer.add(row);
		        }
	        }
		}
        
		public void refresh() {
	    	this.columnsViewer.getTable().removeAll();
	        for( TeiidColumnInfo row : fileInfo.getColumnInfoList() ) {
	        	this.columnsViewer.add(row);
	        }
		}
		
		public void refresh(Object element) {
			this.columnsViewer.refresh(element);
		}
		
		public void addSelectionListener(ISelectionChangedListener listener) {
			this.columnsViewer.addSelectionChangedListener(listener);
		}
		
		public TeiidColumnInfo getSelectedColumn() {
			
			IStructuredSelection selection = (IStructuredSelection)this.columnsViewer.getSelection();
			for( Object obj : selection.toArray()) {
				if( obj instanceof TeiidColumnInfo ) {
					return (TeiidColumnInfo) obj;
				}
			}
			
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
    
    class OrdinalityEditingSupport extends CheckBoxEditingSupport {

		public OrdinalityEditingSupport(ColumnViewer viewer) {
			super(viewer);
		}

		@Override
		protected void setElementValue(Object element, Object newValue) {
			if( element instanceof TeiidColumnInfo && newValue instanceof Boolean) {
				TeiidColumnInfo info = (TeiidColumnInfo)element;
				if(info.getOrdinality() ) {
					fileInfo.setOrdinality(info, false);
					handleInfoChanged(false);
				} else {
					fileInfo.setOrdinality(info, true);
					handleInfoChanged(false);
				}
			}
		}
    	
    }
	    
}
