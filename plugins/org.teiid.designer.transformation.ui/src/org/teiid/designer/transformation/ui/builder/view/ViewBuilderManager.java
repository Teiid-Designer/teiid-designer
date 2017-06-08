/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.transformation.ui.builder.view;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchWindow;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.metamodels.relational.BaseTable;
import org.teiid.designer.query.sql.ISQLConstants;
import org.teiid.designer.relational.RelationalConstants;
import org.teiid.designer.relational.model.RelationalColumn;
import org.teiid.designer.relational.model.RelationalModel;
import org.teiid.designer.relational.model.RelationalViewTable;
import org.teiid.designer.transformation.model.RelationalModelConverter;
import org.teiid.designer.transformation.model.RelationalViewModelFactory;
import org.teiid.designer.transformation.ui.Messages;
import org.teiid.designer.transformation.ui.UiConstants;
import org.teiid.designer.transformation.ui.UiPlugin;
import org.teiid.designer.ui.common.util.WidgetUtil;
import org.teiid.designer.ui.editors.ModelEditorManager;
import org.teiid.designer.ui.explorer.ModelExplorerContentProvider;
import org.teiid.designer.ui.explorer.ModelExplorerLabelProvider;
import org.teiid.designer.ui.viewsupport.ModelUtilities;
import org.teiid.designer.ui.viewsupport.ModelWorkspaceViewerFilter;

public class ViewBuilderManager implements RelationalConstants, ISQLConstants {
    
	private IProject modelProject;
    private ModelResource sourceModel;
    private EObject sourceTable;
    private String sourceTableName;
    private ModelResource targetModel;
    private RelationalViewTable view;
    
    private Set<RelationalColumn> selectedColumns = new HashSet<RelationalColumn>();
	
	public ViewBuilderManager(BaseTable sourceTable ) {
		super();
		
		this.sourceTable = sourceTable;
		
    	RelationalModelConverter converter = new RelationalModelConverter();
    	
    	view = (RelationalViewTable)converter.convert(this.sourceTable, true);
		
        Resource resource = sourceTable.eResource();

        sourceModel = ModelUtilities.getModelResource(resource, true);
        modelProject = sourceModel.getModelProject().getProject();
        sourceTableName = view.getName();
	}
	
	public ViewBuilderManager(ModelResource viewModel ) {
		super();
		
    	this.targetModel = viewModel;
        modelProject = targetModel.getModelProject().getProject();
	}
	
    public IProject getModelProject() {
		return modelProject;
	}

	public void setModelProject(IProject modelProject) {
		this.modelProject = modelProject;
	}

	public ModelResource getSourceModel() {
		return sourceModel;
	}

	public void setSourceModel(ModelResource sourceModel) {
		this.sourceModel = sourceModel;
	}

	public EObject getSourceable() {
		return sourceTable;
	}

	public void setSourceTable(EObject selectedTable) {
		this.sourceTable = selectedTable;
		
		RelationalModelConverter converter = new RelationalModelConverter();
    	
    	view = (RelationalViewTable)converter.convert(this.sourceTable, true);
		
        Resource resource = sourceTable.eResource();

        sourceModel = ModelUtilities.getModelResource(resource, true);
        modelProject = sourceModel.getModelProject().getProject();
        sourceTableName = view.getName();
	}

	public String getSourceTableName() {
		return sourceTableName;
	}

	public void setSourceTableName(String sourceTableName) {
		this.sourceTableName = sourceTableName;
	}

	public ModelResource getTargetModel() {
		return targetModel;
	}

	public void setTargetModel(ModelResource targetModel) {
		this.targetModel = targetModel;
	}

	public RelationalViewTable getView() {
		return view;
	}

	public void setView(RelationalViewTable view) {
		this.view = view;
	}

	public Collection<RelationalColumn> getSelectedColumns() {
		return selectedColumns;
	}

	public void addSelectedColumn(RelationalColumn column) {
		this.selectedColumns.add(column);
	}

    public void run() {
        final IWorkbenchWindow iww = UiPlugin.getDefault().getCurrentWorkbenchWindow();
        
        if( sourceTable == null ) {
        	queryUserForSourceTable();
        }
    	RelationalModelConverter converter = new RelationalModelConverter();
    	
    	setView((RelationalViewTable)converter.convert(sourceTable, true));
        
        final CreateViewFromTableDialog dialog = new CreateViewFromTableDialog(iww.getShell(), this);
        final int rc = dialog.open();
        
        if( rc == Window.OK ) {

    		Collection<RelationalColumn> selected = getSelectedColumns();
    		// Now remove all columns from the view and re-add selected
    		getView().getColumns().clear();
    		getView().getColumns().addAll(selected);
    		
    		String modelName = ModelUtil.getName(sourceModel);
        	// Now we take and construct the basic SQL statement from the view
        	// SELECT c1, c2,.... FROM 
    		StringBuilder sb = new StringBuilder();
    		sb.append(SELECT).append(SPACE);
    		int count = 1;
    		int nCols = view.getColumns().size();
    		for( RelationalColumn col : view.getColumns() ) {
    			sb.append(col.getName());
    			if(count < nCols ) {
    				sb.append(COMMA + SPACE);
    			}
    			count++;
    		}
    		
    		sb.append(SPACE).append(RETURN + TAB + FROM + SPACE).append(modelName + DOT + getSourceTableName());
    		
    		//System.out.println("   SQL = " + sb.toString());
    		
    		getView().setTransformationSQL(sb.toString());
	        
    		getView().setSupportsUpdate(true);
	    	        
	    	        // Hand the table off to the generic edit dialog
//	                TransformationDialogModel dialogModel = new TransformationDialogModel(view, selectedModel);
//	                EditRelationalObjectDialog editDialog = new EditRelationalObjectDialog(shell, dialogModel);

//	    	        dialog.open();
	    	        
	        if( getTargetModel() != null ) {
	        	createViewTableInTxn();
	        }

        }
    	
    }
    
    private EObject createViewTableInTxn( ) {
    	EObject newTable = null;
    	
        boolean requiredStart = ModelerCore.startTxn(true, true, Messages.createRelationalViewTableTitle, this);
        boolean succeeded = false;
        try {
            org.teiid.designer.ui.editors.ModelEditor editor = ModelEditorManager.getModelEditorForFile((IFile)getTargetModel().getCorrespondingResource(), true);
            if (editor != null) {
                boolean isDirty = editor.isDirty();

                RelationalViewModelFactory factory = new RelationalViewModelFactory();
                
                RelationalModel relModel = new RelationalModel("dummy"); //$NON-NLS-1$
                relModel.addChild(getView());
                
                factory.build(getTargetModel(), relModel, new NullProgressMonitor());
    	        //factory.buildObject(table, modelResource, new NullProgressMonitor());

                if (!isDirty && editor.isDirty()) {
                    editor.doSave(new NullProgressMonitor());
                }
                succeeded = true;
                
                for( Object child : getTargetModel().getEObjects() ) {
                	EObject eObj = (EObject)child;
                	if( ModelerCore.getModelEditor().getName(eObj).equalsIgnoreCase(getView().getName()) ) {
                		break;
                	}
                }
            }
        } catch (Exception e) {
            MessageDialog.openError(Display.getCurrent().getActiveShell(),
                                    Messages.createRelationalViewTableExceptionMessage,
                                    e.getMessage());
            IStatus status = new Status(IStatus.ERROR, UiConstants.PLUGIN_ID, Messages.createRelationalViewTableExceptionMessage, e);
            UiConstants.Util.log(status);

            return null;
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
        
        return newTable;
    }
    
    /**
     * Opens selection dialog 
     */
    private void queryUserForSourceTable() {
    	
		final Object[] selections = WidgetUtil.showWorkspaceObjectSelectionDialog(
						"Select Source Table",
						"Select a table as the input for your new view",
						false, null, tableOrViewFilter,
						new TableOrViewSelectionValidator(),
						new ModelExplorerLabelProvider(),
						new ModelExplorerContentProvider());

		if (selections != null && selections.length == 1 ) {
			if (selections[0] instanceof EObject) {
				EObject sourceTable = (EObject) selections[0];
				setSourceTable(sourceTable);
			}
		}
    }
    
	final ViewerFilter tableOrViewFilter = new ModelWorkspaceViewerFilter(true, true, false) {

		@Override
		public boolean select(final Viewer viewer, final Object parent,
				final Object element) {
			boolean doSelect = false;
			if (element instanceof IResource) {
				// If the project is closed, dont show
				boolean projectOpen = ((IResource) element).getProject().isOpen();
				String projectName = ((IResource) element).getProject().getName();
				boolean isSingleProject = false;
				if( projectName != null ) {
					isSingleProject = projectName.equals(getModelProject().getName());
				}
				if (projectOpen && isSingleProject) {
					// Show open projects
					if (element instanceof IProject) {
		                IProject project = (IProject)element;
						try {
		                	doSelect = project.hasNature(ModelerCore.NATURE_ID);
		                } catch (CoreException e) {
		                	ModelerCore.Util.log(e);
		                }
					} else if (element instanceof IContainer) {
						doSelect = true;
						// Show webservice model files, and not .xsd files
					} else if (element instanceof IFile && ModelUtil.isModelFile((IFile) element)) {
						ModelResource theModel = null;
						try {
							theModel = ModelUtil.getModelResource((IFile) element, true);
						} catch (Exception ex) {
							ModelerCore.Util.log(ex);
						}
						if( theModel != null) return true;
					} else if( element instanceof EObject) {
						doSelect = true;
					}
				}
			} else if (element instanceof IContainer) {
				doSelect = true;
			} else if( element instanceof EObject ) {
				return true;
			}

			return doSelect;
		}
	};

}
