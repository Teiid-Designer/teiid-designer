/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.webservice.ui.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchWindow;
import org.teiid.core.designer.ModelerCoreException;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.core.designer.util.ResourceNameUtil;
import org.teiid.core.designer.util.StringConstants;
import org.teiid.core.designer.util.StringUtilities;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.util.ModelVisitor;
import org.teiid.designer.core.util.ModelVisitorProcessor;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.core.workspace.ModelWorkspaceItem;
import org.teiid.designer.core.workspace.ModelWorkspaceManager;
import org.teiid.designer.metamodels.relational.BaseTable;
import org.teiid.designer.metamodels.relational.Catalog;
import org.teiid.designer.metamodels.relational.Column;
import org.teiid.designer.metamodels.relational.PrimaryKey;
import org.teiid.designer.metamodels.relational.Schema;
import org.teiid.designer.metamodels.relational.Table;
import org.teiid.designer.metamodels.relational.extension.RestModelExtensionConstants;
import org.teiid.designer.query.IQueryService;
import org.teiid.designer.relational.RelationalConstants;
import org.teiid.designer.relational.model.RelationalModel;
import org.teiid.designer.relational.model.RelationalParameter;
import org.teiid.designer.relational.model.RelationalViewProcedure;
import org.teiid.designer.transformation.model.RelationalViewModelFactory;
import org.teiid.designer.transformation.ui.Messages;
import org.teiid.designer.ui.actions.SortableSelectionAction;
import org.teiid.designer.ui.common.eventsupport.SelectionUtilities;
import org.teiid.designer.ui.common.graphics.GlobalUiColorManager;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.common.util.WidgetUtil;
import org.teiid.designer.ui.common.widget.ScrollableTitleAreaDialog;
import org.teiid.designer.ui.editors.ModelEditor;
import org.teiid.designer.ui.editors.ModelEditorManager;
import org.teiid.designer.ui.explorer.ModelExplorerContentProvider;
import org.teiid.designer.ui.explorer.ModelExplorerLabelProvider;
import org.teiid.designer.ui.viewsupport.ModelIdentifier;
import org.teiid.designer.ui.viewsupport.ModelProjectSelectionStatusValidator;
import org.teiid.designer.ui.viewsupport.ModelResourceSelectionValidator;
import org.teiid.designer.ui.viewsupport.ModelUtilities;
import org.teiid.designer.ui.viewsupport.ModelWorkspaceViewerFilter;
import org.teiid.designer.ui.viewsupport.ModelingResourceFilter;
import org.teiid.designer.webservice.ui.IInternalUiConstants;
import org.teiid.designer.webservice.ui.WebServiceUiPlugin;

public class GenerateRestVirtualProceduresAction extends SortableSelectionAction implements IInternalUiConstants {
	private static final char SQL_ESCAPE_CHAR = '\"';  //$NON-NLS-1$
	private static final String DEFAULT_REST_XML_GROUPTAG = "elems";  //$NON-NLS-1$
	private static final String DEFAULT_REST_XML_ELEMENTTAG = "elem";  //$NON-NLS-1$ 
	private static final String RESTPROC_SUFFIX = getString("restProcSuffix");  //$NON-NLS-1$
	private static final String VIEW_XML_TAG_LABEL = getString("viewXmlTagLabel");  //$NON-NLS-1$
	private static final String COLUMN_XML_TAG_LABEL = getString("columnXmlTagLabel");  //$NON-NLS-1$
	
	private static final String getString(String key) {
		return UTIL.getString("generateRestVirtualProceduresAction." + key); //$NON-NLS-1$
	}
	
	private static final String getString(String key, Object value) {
		return UTIL.getString("generateRestVirtualProceduresAction." + key, value); //$NON-NLS-1$
	}
	
	private IContainer viewModelFolder;

    public GenerateRestVirtualProceduresAction() {
        super();
        setImageDescriptor(WebServiceUiPlugin.getDefault().getImageDescriptor(WebServiceUiPlugin.Images.NEW_PROCEDURE_ICON));
        setToolTipText(getString("tooltip"));  //$NON-NLS-1$
    }

    /**
     * 
     */
    @Override
    public boolean isApplicable( final ISelection selection ) {
        return isValidSelection(selection);
    }

    /**
     * Valid selections include Relational Tables, Procedures or Relational Models. The roots instance variable will populated
     * with all Tables and Procedures contained within the current selection.
     * 
     * @return
     * @since 4.1
     */
    @Override
    protected boolean isValidSelection( ISelection selection ) {
        if (SelectionUtilities.isEmptySelection(selection) || SelectionUtilities.isMultiSelection(selection)) {
            return false;
        }
        
        if( !SelectionUtilities.isAllIResourceObjects(selection)	) {
        	return false;
        }

        final ModelResource modelResource = getModelResource(selection);
        if (modelResource != null) {
            try {
                return  (ModelIdentifier.isRelationalSourceModel(modelResource) || ModelIdentifier.isRelationalViewModel(modelResource)) && hasTable(modelResource);
            } catch (ModelWorkspaceException err) {
            	UTIL.log(err);
            } catch (ModelerCoreException err) {
            	UTIL.log(err);
            }
        } 

        return false;
    }
    
    private ModelResource getModelResource(ISelection selection) {
        final Object obj = SelectionUtilities.getSelectedObject(selection);

        if (obj instanceof IFile) {
            return ModelerCore.getModelWorkspace().findModelResource((IFile)obj);
        }
        return null;
    }
    
    /*
     * A relational model may be Empty or have no tables or procedures. In this case the wizard can't create anything.
     */
    private boolean hasTable(ModelResource mr) throws ModelWorkspaceException, ModelerCoreException {
    	TableFinder visitor = new TableFinder();
    	final int mode = ModelVisitorProcessor.MODE_VISIBLE_CONTAINMENTS;   // show only those objects visible to user
        final ModelVisitorProcessor processor = new ModelVisitorProcessor(visitor,mode);
        
        processor.walk(mr, ModelVisitorProcessor.DEPTH_INFINITE);
        
        return visitor.hasTable();
    }

    @Override
    public void run() {
        final IWorkbenchWindow iww = WebServiceUiPlugin.getDefault().getCurrentWorkbenchWindow();

        // Target virtual model may NOT be the original selected model

        ModelResource modelResource = getModelResource(getSelection());
        
        if( modelResource != null ) {
        	SelectViewsAndTablesDialog dialog = 
        			new SelectViewsAndTablesDialog(iww.getShell(), modelResource);
        	
        	if( dialog.open() == Dialog.OK ) {
        		EObject[] selectedViewsAndTables = dialog.getCheckedObjects();
        		String viewXmlTag = dialog.getViewXmlTag();
        		String columnXmlTag = dialog.getColumnXmlTag();
        		String restMethod = dialog.getRestMethodValue();
        		IContainer folder = dialog.getViewModelFolder();
        		
        		boolean cancelled = false;
        		Collection<RestProcedureInfo> procedureInfos = new ArrayList<RestProcedureInfo>(selectedViewsAndTables.length);
        		if( dialog.getSetIndividualTags() ) {
        			for( EObject obj : selectedViewsAndTables ) {
	        			RestProcedureInfo info = new RestProcedureInfo(obj);
	        			
	        			SetTagsDialog tagsDialog = new SetTagsDialog(iww.getShell(), info);
	        			if( tagsDialog.open() != Dialog.OK ) {
	        				// Need to cancel the whole thing!
	        				cancelled = true;
	        				break;
	        			}
	        			procedureInfos.add(info);
	        		}
        		} else {
        			for( EObject obj : selectedViewsAndTables ) {
	        			RestProcedureInfo info = new RestProcedureInfo(obj);
	        			info.setViewTag(viewXmlTag);
	        			info.setColumnTag(columnXmlTag);
	        			procedureInfos.add(info);
        			}
        		}

        		if( !cancelled ) {
	        		try {
						String modelName = modelResource.getUnderlyingResource().getFullPath().removeFileExtension().lastSegment();
						String modelNameWithExtension = dialog.getViewModelName();;
						if( !modelNameWithExtension.toUpperCase().endsWith(StringConstants.DOT_XMI.toUpperCase())) {
							modelNameWithExtension = modelNameWithExtension + StringConstants.DOT_XMI;
						}
						
						if( selectedViewsAndTables.length > 0 ) {
							generateRestProcedures(modelNameWithExtension, modelName, folder, procedureInfos, restMethod);
						}
					} catch (ModelWorkspaceException e) {
						UTIL.log(e);
					}
        		}
        	}
        } else {
        	// Show ERROR/WARNING?
        }
    }
    
    private Set<String> getAllSQLReservedWords() {
        IQueryService service = ModelerCore.getTeiidQueryService();
        return service.getReservedWords();
    }
    
    private void generateRestProcedures(String modelNameWithExtension, String modelName, IContainer folder, Collection<RestProcedureInfo> procedureInfos, String restMethod) {
    	String[] columnNames = null;
    	
    	Set<String> reservedWords = getAllSQLReservedWords();
    	
    	
    	Collection<RelationalViewProcedure> viewProcedures = new ArrayList<RelationalViewProcedure>();
    	
    	for( RestProcedureInfo info : procedureInfos ) {
        	String viewXmlTag = info.getViewTag();
        	String columnXmlTag = info.getColumnTag();
        	
    		EObject eObj = info.getTable(); 
    		Collection<Column> pkColumns = new ArrayList<Column>();
    		
    		if( eObj instanceof BaseTable ) {
    			columnNames = getColumnNames((BaseTable)eObj);
    			pkColumns = getPKColumns((BaseTable)eObj);
    		} 
    		String viewName = getName(eObj);
    		String viewQualifiedName = modelName + '.' + getName(eObj);
    		String procName = viewName + RESTPROC_SUFFIX;
    		if( columnNames.length > 0 ) {
    			String colStr = getColumnsString(columnNames, reservedWords);
    			// CREATE VIRTUAL PROCEDURE productInfoRestProc () 
    			//      RETURNS (result XML) OPTIONS ("REST:METHOD" 'GET', "REST:URI" 'rest') 
    			//   AS 
    			//     BEGIN 
    			//   SELECT XMLELEMENT(NAME Elems, XMLAGG(XMLELEMENT(NAME Elem, XMLFOREST(RowId,ID_WORKSPACE,ID_PANEL_PROVIDER)))) AS result FROM SvcView;

    			
    			RelationalViewProcedure viewProcedure = new RelationalViewProcedure(procName);

    			Map<String, String> paramToColumnMap = new LinkedHashMap<String, String>();
    			
    			if( ! pkColumns.isEmpty() ) {
    				// create IN parameter for each column with "pk_<columnName>_in
    				for( Column col : pkColumns ) {
    					String colName = getName(col);
    					String paramName = "pk_" + colName + "_in";
    					String dTypeName = getName(col.getType());
    					RelationalParameter param = new RelationalParameter(paramName);
    					param.setDatatype(dTypeName);
    					param.setDirection(RelationalConstants.DIRECTION.IN);
    					paramToColumnMap.put(colName, paramName);
    					viewProcedure.addParameter(param);
    				}
    			}
    			
    			String restURI = viewName;
    			if( !paramToColumnMap.isEmpty() ) {
    				int count = 0;
    				int mapSize = paramToColumnMap.values().size();
    				StringBuilder sb = new StringBuilder();
    				sb.append(viewName).append("/");
    				for( String paramName : paramToColumnMap.values() ) {
    					sb.append("{").append(paramName).append("}");
    					count++;
    					if( count < mapSize ) {
    						sb.append("/");
    					}
    				}
    				restURI = sb.toString();
    			}
    			
    			viewProcedure.setRestEnabled(true);
    			viewProcedure.setRestUri(restURI);
    			
    			String generatedSql = getRestProcedureDdl(procName, viewXmlTag, columnXmlTag, colStr, viewQualifiedName, viewName, reservedWords, paramToColumnMap);
    			
    			viewProcedure.setTransformationSQL(generatedSql);
    			viewProcedure.setRestMethod(restMethod);
    			viewProcedures.add(viewProcedure);
    			
//    			System.out.println(" GENERATED SQL for " + viewName + ":\n\n" + generatedSql);
    		}
    	}
    	
    	if( !viewProcedures.isEmpty() ) {
    		createViewProceduresInTxn(modelNameWithExtension, folder, viewProcedures);
    	}
    }
    
    private void createViewProceduresInTxn( String modelName, IContainer folder, Collection<RelationalViewProcedure> viewProcedures ) {
    	IPath modelPath = folder.getFullPath().append(modelName + ".xmi");  //$NON-NLS-1$
    	
    	ModelResource modelResource = null;
    	
    	if( modelPath.toFile().exists() ) {
    		// Get the ModelResource from the ModelWorkspaceManager
    		 ModelWorkspaceItem item = ModelWorkspaceManager.getModelWorkspaceManager().findModelWorkspaceItem(modelPath, IResource.FILE);
    		 try {
				IResource iRes = item.getCorrespondingResource();
				modelResource = ModelUtilities.getModelResource(iRes);
			} catch (ModelWorkspaceException e) {
	    		MessageDialog.openError(Display.getCurrent().getActiveShell(),
	                    Messages.createRelationalViewProcedureExceptionMessage,
	                    e.getMessage());
				IStatus status = new Status(IStatus.ERROR, PLUGIN_ID, Messages.createRelationalViewProcedureExceptionMessage);
				UTIL.log(status);
			}
    		 
    	} else {
    		// Create the model
    		try {
        		RelationalViewModelFactory factory = new RelationalViewModelFactory();
        		modelResource = factory.createRelationalViewModel(folder, modelName);
                // Save model
        		modelResource.save(new NullProgressMonitor(), false);
			} catch (ModelWorkspaceException e) {
	    		MessageDialog.openError(Display.getCurrent().getActiveShell(),
	                    Messages.createRelationalViewProcedureExceptionMessage,
	                    e.getMessage());
				IStatus status = new Status(IStatus.ERROR, PLUGIN_ID, Messages.createRelationalViewProcedureExceptionMessage);
				UTIL.log(status);
			}
    	}
    	
        boolean requiredStart = ModelerCore.startTxn(true, true, Messages.createRelationalViewProcedureTitle, this);
        boolean succeeded = false;
        try {
            ModelEditor editor = ModelEditorManager.getModelEditorForFile((IFile)modelResource.getCorrespondingResource(), true);
            if (editor != null) {
                boolean isDirty = editor.isDirty();

                RelationalViewModelFactory factory = new RelationalViewModelFactory();
                
                RelationalModel relModel = new RelationalModel("dummy"); //$NON-NLS-1$
                for( RelationalViewProcedure viewProcedure : viewProcedures ) {
                	relModel.addChild(viewProcedure);
                }
                
                factory.build(modelResource, relModel, new NullProgressMonitor());

                if (!isDirty && editor.isDirty()) {
                    editor.doSave(new NullProgressMonitor());
                }
                succeeded = true; 
            }
        } catch (Exception e) {
            MessageDialog.openError(Display.getCurrent().getActiveShell(),
                                    Messages.createRelationalViewProcedureExceptionMessage,
                                    e.getMessage());
            IStatus status = new Status(IStatus.ERROR, PLUGIN_ID, Messages.createRelationalViewProcedureExceptionMessage, e);
            UTIL.log(status);
        } finally {
            // if we started the txn, commit it.
            if (requiredStart) {
                if (succeeded) {
                    ModelerCore.commitTxn();
                } else {
                    ModelerCore.rollbackTxn();
                }
            }
        }
    }
    
    private String[] getColumnNames(Table eObj) {
    	Collection<String> names = new ArrayList<String>();
    	
		for( Object col : ((Table)eObj).getColumns() ) {
			names.add(getName((EObject)col));
		}
	
		return (String[])names.toArray(new String[names.size()]);
    }
    
    private Collection<Column> getPKColumns(BaseTable table) {
    	Collection<Column> pkColumns = new ArrayList<Column>();
    	
    	if( table.getPrimaryKey() != null ) {
    		PrimaryKey pk = table.getPrimaryKey();
    		for( Object colRef : pk.getColumns() ) {
    			pkColumns.add((Column)colRef);
    		}
    	}

    	return pkColumns;
    }

	/**
	 * Get stringified column name list, separated by commas
	 * 
	 * @param colList
	 * @return column string
	 */
	private static String getColumnsString(String[] colList, Set<String> reservedWords) {
		StringBuilder sb = new StringBuilder();
		for (String colName : colList) {
			if (!sb.toString().isEmpty()) {
				sb.append(","); //$NON-NLS-1$
			}
			sb.append(escapeSQLName(colName, reservedWords));
		}
		return sb.toString();
	}

    private String getName(EObject eObj) {
    	return ModelerCore.getModelEditor().getName(eObj);
    }
    
	/**
	 * Get the procedure for exposing a view as REST
	 * 
	 * @param xmlTagGroup the outer tag for element grouping
	 * @param xmlTagIndiv  the individual element tag
	 * @param colNames the list of columns
	 * @param srcView  the view source
	 * @return the REST procedure
	 */
	private String getRestProcedureDdl(String procName, 
			String xmlTagGroup, String xmlTagIndiv, 
			String colNamesString, String viewName,
			String resturi, Set<String> reservedWords,
			Map<String, String> paramToColumnMap) {
		StringBuilder sb = new StringBuilder();
		sb.append("BEGIN \n");   				//$NON-NLS-1$
		sb.append(" SELECT XMLELEMENT(NAME ");  //$NON-NLS-1$
		sb.append(xmlTagGroup); 				//$NON-NLS-1$
		
		// 
		// XMLATTRIBUTES (<procName>.p1 as p1, <procName.p2 as p2.....)
		//
		if( !paramToColumnMap.isEmpty() ) {
			int count = 0;
			int mapSize = paramToColumnMap.values().size();
			sb.append(", XMLATTRIBUTES (");
			for( String col : paramToColumnMap.keySet() ) {
				String paramName = paramToColumnMap.get(col);
				String fullParamName = procName + "." + paramName;
				sb.append(fullParamName).append(" as ").append(paramName);
				count++;
				if( count < mapSize ) {
					sb.append(", ");
				}
			}
			sb.append(")");
		}
		
		sb.append(", XMLAGG(XMLELEMENT(NAME "); //$NON-NLS-1$
		sb.append(xmlTagIndiv); 				//$NON-NLS-1$
		sb.append(", XMLFOREST("); 				//$NON-NLS-1$
		sb.append(colNamesString);
		sb.append(")))) AS xml_out \n"); 		//$NON-NLS-1$
		sb.append(" FROM "); 					//$NON-NLS-1$
		sb.append(escapeSQLName(viewName, reservedWords));
		if( !paramToColumnMap.isEmpty() ) {
			int count = 0;
			int mapSize = paramToColumnMap.values().size();
			sb.append(" WHERE ");
			for( String col : paramToColumnMap.keySet() ) {
				String paramName = paramToColumnMap.get(col);
				String fullParamName = procName + "." + paramName;
				sb.append(fullParamName).append(" = ").append(col);
				count++;
				if( count < mapSize ) {
					sb.append(" AND ");
				}
			}
		}
		sb.append(";\n"); 						//$NON-NLS-1$
		sb.append(" END;"); 					//$NON-NLS-1$
		return sb.toString();
	}

    
    class TableFinder implements ModelVisitor {
    	Collection<EObject> tables = new ArrayList<EObject>();
    	
    	boolean hasTableOrProcedure = false;

		@Override
		public boolean visit(EObject object) throws ModelerCoreException {
			// Tables are contained by Catalogs, Schemas and Resources
	        if (object instanceof Table) {
	        	tables.add(object);
	            return false;
	        }
	        if (object instanceof Catalog) {
	            // catalogs will contain tables
	            return true;
	        }
	        if (object instanceof Schema) {
	            // schemas will contain tables
	            return true;
	        }
	        return false;
		}

		@Override
		public boolean visit(Resource resource) throws ModelerCoreException {
			return true;
		}
		
		public boolean hasTable() {
			return !tables.isEmpty();
		}
		
		public EObject[] getTablesViews() {
			return (EObject[])tables.toArray(new EObject[tables.size()]);
		}
    	
    }
    
    class SelectViewsAndTablesDialog extends ScrollableTitleAreaDialog {

        private final String TITLE = UTIL.getString("SelectViewsAndTablesDialog.title"); //$NON-NLS-1$
        private final String INITIAL_MSG = UTIL.getString("SelectViewsAndTablesDialog.originalMessage"); //$NON-NLS-1$
        
        private ModelResource modelResource;
        private CheckboxTableViewer tableViewer;
        private Text viewXmlTag, columnXmlTag;
        private Button selectAllButton, deselectAllButton;
        
        Collection<EObject> selectedTablesAndViews = new ArrayList<EObject>();
        EObject[] allTablesAndViews;
        EObject[] tablesAndViews;
        String viewXmlTagValue;
        String columnXmlTagValue;
        Button setAllTagsTheSame;
    	private boolean setIndividualTags;
        
    	private Text viewModelContainerText;
        String viewModelName;
    	private Text viewModelFileText;

    	private String restMethodValue = RestModelExtensionConstants.METHODS.GET;

        /**
         * Construct an instance of ModelStatisticsDialog.
         */
        public SelectViewsAndTablesDialog(Shell shell, ModelResource resource) {
            super(shell);
            this.modelResource = resource;
            this.allTablesAndViews = getTables(modelResource);
            if( ModelIdentifier.isRelationalViewModel(resource) ) {
            	viewModelFolder = (IContainer)resource.getResource().getParent();
            	this.viewModelName = resource.getResource().getFullPath().removeFileExtension().lastSegment();
            } else {
            	viewModelFolder = (IContainer)resource.getResource().getProject();
            	viewModelName = null;
            }
        }

        /* (non-Javadoc)
         * @see org.eclipse.jface.window.Window#createDialogArea(org.eclipse.swt.widgets.Composite)
         */
        @Override
        protected Control createDialogArea(Composite parent) {
            setTitle(TITLE);
            setMessage(INITIAL_MSG);
            Composite composite = (Composite)super.createDialogArea(parent);
            GridLayoutFactory.swtDefaults().margins(5, 5).applyTo(composite);
            composite.setLayoutData(new GridData(GridData.FILL_BOTH));
            
            Composite labelPanel = WidgetFactory.createPanel(composite, SWT.NONE, GridData.FILL, 1, 2);
            WidgetFactory.createLabel(labelPanel, getString("model")); //$NON-NLS-1$
            org.teiid.designer.ui.common.widget.Label label = WidgetFactory.createLabel(labelPanel, modelResource.getItemName());
            label.setForeground(GlobalUiColorManager.EMPHASIS_COLOR);
            
            { // VIEW MODEL DEFINITION
        		Group viewGroup = WidgetFactory.createGroup(composite, getString("viewModelDefinitionGroup"), SWT.NONE, 2, 3); //$NON-NLS-1$
        		GridData gd_vg = new GridData(GridData.FILL_HORIZONTAL);
        		gd_vg.horizontalSpan = 2;
        		viewGroup.setLayoutData(gd_vg);

        		Label locationLabel = new Label(viewGroup, SWT.NULL);
        		locationLabel.setText(getString("location")); //$NON-NLS-1$

        		viewModelContainerText = new Text(viewGroup, SWT.BORDER | SWT.SINGLE);
        		if( viewModelFolder != null ) {
        			viewModelContainerText.setText(viewModelFolder.getName());
        		}
        		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        		viewModelContainerText.setLayoutData(gridData);
        		viewModelContainerText.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
        		viewModelContainerText.setEditable(false);

        		Button browseButton = new Button(viewGroup, SWT.PUSH);
        		gridData = new GridData();
        		browseButton.setLayoutData(gridData);
        		browseButton.setText(getString("browse")); //$NON-NLS-1$
        		browseButton.addSelectionListener(new SelectionAdapter() {
        			@Override
        			public void widgetSelected(SelectionEvent e) {
        				handleViewModelLocationBrowse();
        			}
        		});

        		Label fileLabel = new Label(viewGroup, SWT.NULL);
        		fileLabel.setText(getString("name")); //$NON-NLS-1$

        		viewModelFileText = new Text(viewGroup, SWT.BORDER | SWT.SINGLE);
        		if( ! StringUtilities.isEmpty(this.viewModelName) ) {
        			viewModelFileText.setText(this.viewModelName);
        		}
        		gridData = new GridData(GridData.FILL_HORIZONTAL);
        		viewModelFileText.setLayoutData(gridData);
        		viewModelFileText.addModifyListener(new ModifyListener() {
        			@Override
        			public void modifyText(ModifyEvent e) {
        				// Check view file name for existing if "location" is already
        				// set
        				handleViewModelTextChanged();
        			}
        		});

        		browseButton = new Button(viewGroup, SWT.PUSH);
        		gridData = new GridData();
        		browseButton.setLayoutData(gridData);
        		browseButton.setText(getString("browse")); //$NON-NLS-1$
        		browseButton.addSelectionListener(new SelectionAdapter() {
        			@Override
        			public void widgetSelected(SelectionEvent e) {
        				handleViewModelBrowse();
        			}
        		});

            }
            
            setAllTagsTheSame = new Button(composite, SWT.CHECK);
            setAllTagsTheSame.setText("Set all tag names to default");
            setAllTagsTheSame.setSelection(true);
            setAllTagsTheSame.addSelectionListener(new SelectionListener() {
				
				@Override
				public void widgetSelected(SelectionEvent e) {
					boolean enableSameTags = setAllTagsTheSame.getSelection();
					viewXmlTag.setEnabled(enableSameTags);
					columnXmlTag.setEnabled(enableSameTags);
					setIndividualTags = !setAllTagsTheSame.getSelection();
					
				}
				
				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
				}
			});
            
            
            {
            	Composite optionsGroup = WidgetFactory.createGroup(composite, getString("options"));
            	GridLayoutFactory.swtDefaults().numColumns(2).applyTo(optionsGroup);
            	optionsGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            	
            	// REST METHOD
            	Label restMethodLabel = new Label(optionsGroup, SWT.NONE);
            	restMethodLabel.setText(getString("restMethodLabel")); //$NON-NLS-1$
            	
            	Label restMethodValueLabel = new Label(optionsGroup, SWT.NONE);
            	restMethodValueLabel.setText("GET"); //$NON-NLS-1$
            	
            	// Add View XML tag & Column XML tag
            	
            	Label label1 = new Label(optionsGroup, SWT.NONE);
            	label1.setText(VIEW_XML_TAG_LABEL);
            	viewXmlTag = new Text(optionsGroup, SWT.BORDER);
            	viewXmlTagValue = DEFAULT_REST_XML_GROUPTAG;
            	viewXmlTag.setText(viewXmlTagValue);
            	viewXmlTag.setEditable(true);
            	viewXmlTag.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            	viewXmlTag.addModifyListener(new ModifyListener() {
					
					@Override
					public void modifyText(ModifyEvent arg0) {
						viewXmlTagValue = viewXmlTag.getText();
						if( viewXmlTagValue == null ) {
							viewXmlTagValue = StringConstants.EMPTY_STRING;
						}
						validate();
					}
				});
            	
            	Label label2 = new Label(optionsGroup, SWT.NONE);
            	label2.setText(COLUMN_XML_TAG_LABEL);
            	columnXmlTag = new Text(optionsGroup, SWT.BORDER);
            	columnXmlTagValue = DEFAULT_REST_XML_ELEMENTTAG;
            	columnXmlTag.setText(columnXmlTagValue);
            	columnXmlTag.setEditable(true);
            	columnXmlTag.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            	columnXmlTag.addModifyListener(new ModifyListener() {
					
					@Override
					public void modifyText(ModifyEvent arg0) {
						columnXmlTagValue = columnXmlTag.getText();
						if( columnXmlTagValue == null ) {
							columnXmlTagValue = StringConstants.EMPTY_STRING;
						}
						validate();
					}
				});
            }
            
            { // Source Model/Tables Group
	    		Group sourceGroup = WidgetFactory.createGroup(composite, getString("selectTablesGroupLabel"), SWT.NONE, 2, 1); //$NON-NLS-1$
	    		GridData gd_vg = new GridData(GridData.FILL_HORIZONTAL);
	    		gd_vg.horizontalSpan = 2;
	    		sourceGroup.setLayoutData(gd_vg);
	    		
	            Composite buttonPanel = WidgetFactory.createPanel(sourceGroup, SWT.NONE, 1, 1);
	            GridLayoutFactory.fillDefaults().numColumns(5).margins(1, 1).applyTo(buttonPanel);
	            GridDataFactory.fillDefaults().grab(true, false).applyTo(buttonPanel);
	            Label iconLabel = new Label(buttonPanel, SWT.NONE);
				if( ModelIdentifier.isRelationalSourceModel(modelResource)) {
					iconLabel.setImage(WebServiceUiPlugin.getDefault().getImage(Images.SOURCE_MODEL_ICON));
				} else {
					iconLabel.setImage( WebServiceUiPlugin.getDefault().getImage(Images.VIEW_MODEL_ICON));
				}
				
	            Label modelNameLabel = new Label(buttonPanel, SWT.NONE);
	            modelNameLabel.setText(this.modelResource.getItemName());
	            GridDataFactory.swtDefaults().grab(true, false).applyTo(modelNameLabel);
	            modelNameLabel.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
	            
	        	selectAllButton = new Button(buttonPanel, SWT.PUSH);
	        	selectAllButton.setText(getString("selectAllLabel")); //$NON-NLS-1$
	        	GridDataFactory.fillDefaults().applyTo(selectAllButton);
	        	selectAllButton.addSelectionListener(new SelectionAdapter() {
	        		@Override
	    			public void widgetSelected(SelectionEvent e) {
	        			tableViewer.setAllChecked(true);
	        			selectAllButton.setEnabled(false);
	        			deselectAllButton.setEnabled(true);
	        			getButton(IDialogConstants.OK_ID).setEnabled(true);
	        			setErrorMessage(null);
	        			setMessage(getString("msg.clickOkToGenerateProcedures"));
	        			updateCheckedObjects();
	        		}
				});
	        	
	        	deselectAllButton = new Button(buttonPanel, SWT.PUSH);
	        	deselectAllButton.setText(getString("deselectAllLabel")); //$NON-NLS-1$
	        	GridDataFactory.fillDefaults().applyTo(deselectAllButton);
	        	deselectAllButton.addSelectionListener(new SelectionAdapter() {
	        		@Override
	    			public void widgetSelected(SelectionEvent e) {
	        			tableViewer.setAllChecked(false);
	        			deselectAllButton.setEnabled(false);
	        			selectAllButton.setEnabled(true);
	        			getButton(IDialogConstants.OK_ID).setEnabled(false);
	        			setErrorMessage(getString("msg.nothingSelected")); //$NON-NLS-1$
	        			updateCheckedObjects();
	        		}
				});
	        	deselectAllButton.setEnabled(false);
	        	
	            tableViewer = CheckboxTableViewer.newCheckList(sourceGroup, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
	            
	            GridData gd = new GridData(GridData.FILL_BOTH);
	            gd.heightHint = 100;
	            tableViewer.getTable().setLayoutData(gd);
	
	            tableViewer.setLabelProvider(new ILabelProvider() {
					
					@Override
					public void removeListener(ILabelProviderListener listener) {
					}
					
					@Override
					public boolean isLabelProperty(Object element, String property) {
						return false;
					}
					
					@Override
					public void dispose() {
					}
					
					@Override
					public void addListener(ILabelProviderListener listener) {
					}
					
					@Override
					public String getText(Object element) {
						if( element instanceof EObject ) {
							return (ModelerCore.getModelEditor().getName((EObject)element));
						}
						return StringConstants.EMPTY_STRING;
					}
					
					@Override
					public Image getImage(Object element) {
						if( ModelIdentifier.isRelationalSourceModel(modelResource)) {
							return WebServiceUiPlugin.getDefault().getImage(Images.TABLE_ICON);
						}
						return WebServiceUiPlugin.getDefault().getImage(Images.VIEW_ICON);
					}
				});
	            
	            tableViewer.add(allTablesAndViews);
	
	            
	            tableViewer.addCheckStateListener(new ICheckStateListener() {
					
					@Override
					public void checkStateChanged(CheckStateChangedEvent event) {
						updateCheckedObjects();
						
						validate();
					}
				});
            }
            
            sizeScrolledPanel();
            
            return composite;
        }
        
        private void updateCheckedObjects() {
			Collection<EObject> results = new ArrayList<EObject>();
			for( Object obj : tableViewer.getCheckedElements()) {
				if( obj instanceof EObject ) {
					results.add((EObject)obj);
				}
			}
			
			tablesAndViews = (EObject[])results.toArray(new EObject[results.size()]);
        }
        
    	/**
    	 * Uses the standard container selection dialog to choose the new value for
    	 * the container field.
    	 */
    	void handleViewModelLocationBrowse() {
    		ViewerFilter filter = new ModelingResourceFilter();
    		if( viewModelFolder != null ) {
    			filter = folderFilter;
    		}
    		final IContainer folder = WidgetUtil.showFolderSelectionDialog(
    				ModelerCore.getWorkspace().getRoot(),
    				filter,
    				new ModelProjectSelectionStatusValidator());
    		
            if (folder != null) {
                this.viewModelContainerText.setText(folder.getFullPath().toString());
                viewModelFolder = folder;
            }

    		validate();
    	}

    	void handleViewModelBrowse() {
    		final Object[] selections = WidgetUtil
    				.showWorkspaceObjectSelectionDialog(
    						getString("selectViewModelTitle"), //$NON-NLS-1$
    						getString("selectViewModelMessage"), //$NON-NLS-1$
    						false, null, virtualModelFilter,
    						new ModelResourceSelectionValidator(false),
    						new ModelExplorerLabelProvider(),
    						new ModelExplorerContentProvider());

    		if (selections != null && selections.length == 1 && viewModelFileText != null) {
    			if (selections[0] instanceof IFile) {
    				IFile modelFile = (IFile) selections[0];
    				viewModelFolder = modelFile.getParent();
    				String modelName = modelFile.getFullPath().removeFileExtension().lastSegment();
    				viewModelName = modelName;
    				viewModelFileText.setText(modelName);
    				viewModelContainerText.setText(viewModelFolder.toString());
    			}
    		}

    		validate();
    	}

    	void handleViewModelTextChanged() {
    		viewModelName = viewModelFileText.getText();
    		validate();
    	}
        
        private void validate() {
			int maxItems = allTablesAndViews.length;
			int checkedItems = tablesAndViews == null ? 0 : tablesAndViews.length;
			
			
			// Validate view model location
			
			if( viewModelFolder == null ) {
				setErrorMessage(getString("viewFileLocationMustBeSpecified")); //$NON-NLS-1$
				getButton(IDialogConstants.OK_ID).setEnabled(false);
				return;
			}
			
			if( StringUtilities.isEmpty(this.viewModelName) ) {
				setErrorMessage(getString("viewFileNameMustBeSpecified")); //$NON-NLS-1$
				getButton(IDialogConstants.OK_ID).setEnabled(false);
				return;
			} else {
				if(viewModelName.contains(StringUtilities.SPACE)){
					setErrorMessage(getString("viewModelNameCannotContainSpaces")); //$NON-NLS-1$
					getButton(IDialogConstants.OK_ID).setEnabled(false);
					return;
				}
			}
			
			// check the selected model is a view model
			String fullModelName = viewModelName;
			if(!fullModelName.toLowerCase().endsWith(ResourceNameUtil.DOT_XMI_FILE_EXTENSION)){
				fullModelName = fullModelName + ResourceNameUtil.DOT_XMI_FILE_EXTENSION;
			}
			IPath modelPath = viewModelFolder.getFullPath().append(fullModelName);
			if (ModelUtil.isModelFile(modelPath)) {
				ModelResource theModel = null;
				try {
		    		ModelWorkspaceItem item = ModelWorkspaceManager.getModelWorkspaceManager().findModelWorkspaceItem(modelPath, IResource.FILE);
		    		if(item != null && item.exists()){
						IResource iRes = item.getCorrespondingResource();
						theModel = ModelUtilities.getModelResource(iRes);
		    		}
				} catch (Exception ex) {
					ModelerCore.Util.log(ex);
				}
				if (theModel != null && !ModelIdentifier.isRelationalViewModel(theModel)) {
					setErrorMessage(getString("msg.selectedModelMustBeViewModel")); //$NON-NLS-1$
					getButton(IDialogConstants.OK_ID).setEnabled(false);
					return;
				}
			}
			
			deselectAllButton.setEnabled( checkedItems > 0 );
			selectAllButton.setEnabled(checkedItems < maxItems);
			if( checkedItems == 0 ) {
				setErrorMessage(getString("msg.noViewsSelected")); //$NON-NLS-1$
				getButton(IDialogConstants.OK_ID).setEnabled(false);
				return;
			}
			
			if( StringUtilities.isEmpty(viewXmlTagValue) ) {
				setErrorMessage(getString("msg.viewXmlTagCannotBeEmpty")); //$NON-NLS-1$
				getButton(IDialogConstants.OK_ID).setEnabled(false);
				return;
			} else {
				char firstChar = viewXmlTagValue.charAt(0);
				if (!Character.isLetter(firstChar)) {
					setErrorMessage(getString("msg.viewXmlTagConnotStartWithANumber")); //$NON-NLS-1$
					getButton(IDialogConstants.OK_ID).setEnabled(false);
					return;
				}
				if(viewXmlTagValue.contains(StringUtilities.SPACE)){
					setErrorMessage(getString("msg.viewXmlTagConnotContainSpaces")); //$NON-NLS-1$
					getButton(IDialogConstants.OK_ID).setEnabled(false);
					return;
				}
			}
			
			if( StringUtilities.isEmpty(columnXmlTagValue) ) {
				setErrorMessage(getString("msg.columnXmlTagCannotBeEmpty")); //$NON-NLS-1$
				getButton(IDialogConstants.OK_ID).setEnabled(false);
				return;
			} else {
				char firstChar = columnXmlTagValue.charAt(0);
				if (!Character.isLetter(firstChar)) {
					setErrorMessage(getString("msg.columnXmlTagConnotStartWithANumber")); //$NON-NLS-1$
					getButton(IDialogConstants.OK_ID).setEnabled(false);
					return;
				}
				if(columnXmlTagValue.contains(StringUtilities.SPACE)){
					setErrorMessage(getString("msg.columnXmlTagConnotContainSpaces")); //$NON-NLS-1$
					getButton(IDialogConstants.OK_ID).setEnabled(false);
					return;
				}
			}
			
			setErrorMessage(null);
			setMessage(getString("msg.clickOkToGenerateProcedures")); //$NON-NLS-1$
			getButton(IDialogConstants.OK_ID).setEnabled(true);
        }

        /* (non-Javadoc)
         * @see org.eclipse.jface.window.Window#create()
         */
        @Override
        public void create() {
            setShellStyle(getShellStyle() | SWT.RESIZE);
            super.create();
            super.getShell().setText(TITLE);
        }


        /* (non-Javadoc)
         * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
         */
        @Override
        protected void createButtonsForButtonBar(Composite parent) {
            createButton(
                parent,
                IDialogConstants.OK_ID,
                IDialogConstants.OK_LABEL,
                true);
            createButton(
                    parent,
                    IDialogConstants.CANCEL_ID,
                    IDialogConstants.CANCEL_LABEL,
                    false);
            
            getButton(IDialogConstants.OK_ID).setEnabled(false);
        }
        
        public EObject[] getCheckedObjects() {
        	return tablesAndViews;
        }
        
        /*
         * A relational model may be Empty or have no tables or procedures. In this case the wizard can't create anything.
         */
        private EObject[] getTables(ModelResource mr) {
        	TableFinder visitor = new TableFinder();
        	final int mode = ModelVisitorProcessor.MODE_VISIBLE_CONTAINMENTS;   // show only those objects visible to user
            final ModelVisitorProcessor processor = new ModelVisitorProcessor(visitor,mode);
            
            try {
				processor.walk(mr, ModelVisitorProcessor.DEPTH_INFINITE);
			} catch (ModelerCoreException e) {
				e.printStackTrace();
			}
            
            return visitor.getTablesViews();
        }
        
        public String getViewXmlTag() {
        	return viewXmlTagValue;
        }
        
        public String getColumnXmlTag() {
        	return columnXmlTagValue;
        }
        
        public String getRestMethodValue() {
        	return restMethodValue;
        }
        
    	public IContainer getViewModelFolder() {
    		return viewModelFolder;
    	}
        
    	public String getViewModelName() {
    		return this.viewModelName;
    	}
    	
    	public boolean getSetIndividualTags() {
    		return this.setIndividualTags;
    	}


    }
	
    public static String escapeSQLName(String part, Set<String> reservedWords) {
        if (isReservedWord(part, reservedWords)) {
            return SQL_ESCAPE_CHAR + part + SQL_ESCAPE_CHAR;
        }
        boolean escape = true;
        char start = part.charAt(0);
        if (start == '#' || start == '@' || isLetter(start)) {
            escape = false;
            for (int i = 1; !escape && i < part.length(); i++) {
                char c = part.charAt(i);
                escape = !isLetterOrDigit(c) && c != '_';
            }
        }
        if (escape) {
            return SQL_ESCAPE_CHAR + part + SQL_ESCAPE_CHAR; 
        }
        return part;
    }
    
    public static boolean isReservedWord(String string, Set<String> reservedWords) {
    	if(reservedWords.contains(string.toUpperCase())) {
    		return true;
    	}
    	return false;
    }
    
	public static boolean isLetter(char c) {
        return isBasicLatinLetter(c) || Character.isLetter(c);
    }
    public static boolean isLetterOrDigit(char c) {
        return isBasicLatinLetter(c) || isBasicLatinDigit(c) || Character.isLetterOrDigit(c);
    }

	private static boolean isBasicLatinLetter(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }
    private static boolean isBasicLatinDigit(char c) {
        return c >= '0' && c <= '9';
    }
	
	/**
	 * <p>
	 * Returns whether the specified text is either empty or null.
	 * </p>
	 *
	 * @param text The text to check; may be null;
	 * @return True if the specified text is either empty or null.
	 * @since 4.0
	 */
	public static boolean isEmpty( final String text ) {
		return (text == null || text.length() == 0);
	}

	/**
	 * Compare string values - considered equal if either are null or empty.
	 *
	 * @param thisValue the first value being compared (can be <code>null</code> or empty)
	 * @param thatValue the other value being compared (can be <code>null</code> or empty)
	 * @return <code>true</code> if values are equal or both values are empty
	 */
	public static boolean valuesAreEqual( String thisValue,
			String thatValue ) {
		if (isEmpty(thisValue) && isEmpty(thatValue)) {
			return true;
		}

		return equals(thisValue, thatValue);
	}

	/**
	 * @param thisString the first string being compared (may be <code>null</code>)
	 * @param thatString the other string being compared (may be <code>null</code>)
	 * @return <code>true</code> if the supplied strings are both <code>null</code> or have equal values
	 */
	public static boolean equals( final String thisString,
			final String thatString ) {
		if (thisString == null) {
			return (thatString == null);
		}

		return thisString.equals(thatString);
	}
	
	final ViewerFilter virtualModelFilter = new ModelWorkspaceViewerFilter(true) {

		@Override
		public boolean select(final Viewer viewer, final Object parent,
				final Object element) {
			boolean doSelect = false;
			if (element instanceof IResource) {
				// If the project is closed, dont show
				boolean projectOpen = ((IResource) element).getProject().isOpen();
				if (projectOpen) {
					// Show open projects
					if(element instanceof IContainer) {
						if( viewModelFolder != null ) {
							doSelect = ((IContainer)element).getProject() == viewModelFolder.getProject();
						} else doSelect = true;
						// Show web service model files, and not .xsd files
					} else if( element instanceof IProject) {
						try {
		                	doSelect = ((IProject)element).hasNature(ModelerCore.NATURE_ID);
		                } catch (CoreException e) {
		                	ModelerCore.Util.log(e);
		                }
					} else if (element instanceof IFile && ModelUtil.isModelFile((IFile) element)) {
						ModelResource theModel = null;
						try {
							theModel = ModelUtil.getModelResource((IFile) element, true);
						} catch (Exception ex) {
							ModelerCore.Util.log(ex);
						}
						if (theModel != null && ModelIdentifier.isRelationalViewModel(theModel)) {
							doSelect = true;
						}
					}
				}
			} else if (element instanceof IContainer) {
				doSelect = true;
			}

			return doSelect;
		}
	};
	
	final ViewerFilter folderFilter = new ModelWorkspaceViewerFilter(true) {

		@Override
		public boolean select(final Viewer viewer, final Object parent,
				final Object element) {
			boolean doSelect = false;
			if (element instanceof IResource) {
				// If the project is closed, dont show
				boolean projectOpen = ((IResource) element).getProject().isOpen();
				if (projectOpen) {
					// Show open projects
					if(element instanceof IContainer) {
						if( viewModelFolder != null ) {
							doSelect = ((IContainer)element).getProject() == viewModelFolder.getProject();
						} else doSelect = true;
						// Show web service model files, and not .xsd files
					} else if( element instanceof IProject) {
						try {
		                	doSelect = ((IProject)element).hasNature(ModelerCore.NATURE_ID);
		                } catch (CoreException e) {
		                	ModelerCore.Util.log(e);
		                }
					}
				}
			} else if (element instanceof IContainer) {
				doSelect = true;
			}

			return doSelect;
		}
	};
	
	class RestProcedureInfo {
		EObject table;
		String viewTag;
		String columnTag;
			
		public RestProcedureInfo(EObject table) {
			this(table, null, null);
		}
		
		public RestProcedureInfo(EObject table, String viewTag, String columnTag) {
			super();
			this.table = table;
			this.viewTag = viewTag;
			this.columnTag = columnTag;
		}

		public EObject getTable() {
			return table;
		}

		public void setTable(EObject table) {
			this.table = table;
		}

		public String getViewTag() {
			return viewTag;
		}

		public void setViewTag(String viewTag) {
			this.viewTag = viewTag;
		}

		public String getColumnTag() {
			return columnTag;
		}

		public void setColumnTag(String columnTag) {
			this.columnTag = columnTag;
		}
		
		
	}
	
    class SetTagsDialog extends MessageDialog implements StringConstants {

        private Button btnOk;

        RestProcedureInfo procedureInfo;
        
        private Text viewXmlTag, columnXmlTag;

        /**
         * @param parentShell the parent shell (may be <code>null</code>)
         * @param existingPropertyNames the existing property names (can be <code>null</code>)
         */
        public SetTagsDialog( Shell parentShell, RestProcedureInfo procedureInfo) {
            super(parentShell, getString("definedRestProcedureTagsDefine"), null,   //$NON-NLS-1$
            		getString("defineTags") , MessageDialog.INFORMATION,  //$NON-NLS-1$
                    new String[] { IDialogConstants.OK_LABEL, IDialogConstants.CANCEL_LABEL }, 0);
            this.procedureInfo = procedureInfo;
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.dialogs.MessageDialog#createButton(org.eclipse.swt.widgets.Composite, int, java.lang.String, boolean)
         */
        @Override
        protected Button createButton( Composite parent,
                                       int id,
                                       String label,
                                       boolean defaultButton ) {
            Button btn = super.createButton(parent, id, label, defaultButton);

            if (id == IDialogConstants.OK_ID) {
                // disable OK button initially
                this.btnOk = btn;
                btn.setEnabled(false);
            }

            return btn;
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.dialogs.MessageDialog#createCustomArea(org.eclipse.swt.widgets.Composite)
         */
        @Override
        protected Control createCustomArea( Composite parent ) {
            Composite pnl = new Composite(parent, SWT.NONE);
            pnl.setLayout(new GridLayout(2, false));
            pnl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
            
            WidgetFactory.createLabel(pnl, getString("table")); //$NON-NLS-1$
            org.teiid.designer.ui.common.widget.Label label = WidgetFactory.createLabel(pnl, ModelerCore.getModelEditor().getName(procedureInfo.getTable()));
            label.setForeground(GlobalUiColorManager.EMPHASIS_COLOR);

            {
            	Composite optionsGroup = WidgetFactory.createGroup(pnl, getString("options")); //$NON-NLS-1$
            	GridLayoutFactory.swtDefaults().numColumns(2).applyTo(optionsGroup);
            	optionsGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            	((GridData)optionsGroup.getLayoutData()).horizontalSpan = 2;
            	
            	// REST METHOD
            	Label restMethodLabel = new Label(optionsGroup, SWT.NONE);
            	restMethodLabel.setText(getString("restMethodLabel")); //$NON-NLS-1$
            	
            	Label restMethodValueLabel = new Label(optionsGroup, SWT.NONE);
            	restMethodValueLabel.setText(getString("get")); //$NON-NLS-1$ 
            
            	
            	// Add View XML tag & Column XML tag
            	
            	Label label1 = new Label(optionsGroup, SWT.NONE);
            	label1.setText(VIEW_XML_TAG_LABEL);
            	viewXmlTag = new Text(optionsGroup, SWT.BORDER);
            	viewXmlTag.setText(EMPTY_STRING);
            	viewXmlTag.setEditable(true);
            	viewXmlTag.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            	viewXmlTag.addModifyListener(new ModifyListener() {
					
					@Override
					public void modifyText(ModifyEvent arg0) {
						String tagValue = viewXmlTag.getText();
						if( tagValue == null ) {
							tagValue = StringConstants.EMPTY_STRING;
						}
						procedureInfo.setViewTag(tagValue);
						updateState();
					}
				});
            	
            	Label label2 = new Label(optionsGroup, SWT.NONE);
            	label2.setText(COLUMN_XML_TAG_LABEL);
            	columnXmlTag = new Text(optionsGroup, SWT.BORDER);

            	columnXmlTag.setText(EMPTY_STRING);
            	columnXmlTag.setEditable(true);
            	columnXmlTag.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            	columnXmlTag.addModifyListener(new ModifyListener() {
					
					@Override
					public void modifyText(ModifyEvent arg0) {
						String tagValue = columnXmlTag.getText();
						if( tagValue == null ) {
							tagValue = StringConstants.EMPTY_STRING;
						}
						procedureInfo.setColumnTag(tagValue);
						updateState();
					}
				});
            }

            messageLabel.setText(getString("defineTagsForTable_0_", ModelerCore.getModelEditor().getName(procedureInfo.getTable())));
			return pnl;
    	}
        


        private void updateState() {
        	String msg = validateName(procedureInfo.getViewTag());
        	
        	if( msg == null ) {
        		msg = validateName(procedureInfo.getColumnTag());
        	}
        	
        	if( msg == null ) {
        		if( procedureInfo.getViewTag().equals(procedureInfo.getColumnTag()) ) {
        			msg = getString("viewAndColumnNamesCannotBeTheSame");  //$NON-NLS-1$
        		} 
        	}
        	
            // update UI controls
            if (StringUtilities.isEmpty(msg)) {
                if (!this.btnOk.isEnabled()) {
                    this.btnOk.setEnabled(true);
                }

                if (this.imageLabel.getImage() != null) {
                    this.imageLabel.setImage(null);
                }

                this.imageLabel.setImage(getInfoImage());
            } else {
                // value is not valid
                if (this.btnOk.isEnabled()) {
                    this.btnOk.setEnabled(false);
                }

                this.imageLabel.setImage(getErrorImage());
            }

            if (!StringUtilities.isEmpty(msg)) {
            	this.messageLabel.setText(msg);
            } else {
            	this.messageLabel.setText(getString("clickOkToContinue")); //$NON-NLS-1$
            }
            
            this.messageLabel.pack();
        }

        /**
         * @param proposedName the proposed property name
         * @return an error message or <code>null</code> if name is valid
         */
        public String validateName( String proposedName ) {
            // must have a name
            if (StringUtilities.isEmpty(proposedName)) {
                return 	getString("tagCannotBeNullOrEmpty");  //$NON-NLS-1$
            }
            
            for( char ch : proposedName.toCharArray()) {
            	if( !isValidChar(ch)) {
            		return getString("invalidTagCharacter", ch);  //$NON-NLS-1$
            	}
            }
            // valid name
            return null;
        }
        
        private boolean isValidChar(char c) {
        	if((Character.isLetter(c) || Character.isDigit(c))) return true;
        	
        	return false;
        }
        
    }
}