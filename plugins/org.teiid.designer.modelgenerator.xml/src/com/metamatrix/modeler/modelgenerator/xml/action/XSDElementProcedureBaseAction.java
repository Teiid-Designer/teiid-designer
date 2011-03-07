package com.metamatrix.modeler.modelgenerator.xml.action;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.xsd.XSDElementDeclaration;

import com.metamatrix.core.PluginUtil;
import com.metamatrix.metamodels.relational.RelationalFactory;
import com.metamatrix.metamodels.relational.RelationalPackage;
import com.metamatrix.metamodels.relational.Schema;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.metamodel.MetamodelDescriptor;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelResourceSelectionValidator;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.modelgenerator.xml.XmlImporterUiPlugin;
import com.metamatrix.modeler.modelgenerator.xsd.procedures.ITraversalCtxFactory;
import com.metamatrix.modeler.modelgenerator.xsd.procedures.ProcedureBuilder;
import com.metamatrix.modeler.ui.actions.SortableSelectionAction;
import com.metamatrix.ui.internal.util.WidgetUtil;

public abstract class XSDElementProcedureBaseAction extends
		SortableSelectionAction implements IObjectActionDelegate,
		IEditorActionDelegate, IViewActionDelegate {

	protected PluginUtil util = XmlImporterUiPlugin.getDefault().getPluginUtil();
	protected ISelection selection;
	private IFile model;
	private RelationalFactory factory = com.metamatrix.metamodels.relational.RelationalPackage.eINSTANCE
			.getRelationalFactory();
	ModelResource modelResource;
	ProcedureBuilder builder;

	@Override
	public boolean isApplicable(ISelection selection) {
		return isXSDElementDeclaration(selection);
	}

	@Override
	protected boolean isValidSelection(ISelection selection) {
		return isXSDElementDeclaration(selection);
	}

	private boolean isXSDElementDeclaration(ISelection selection) {
		boolean result = false;
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection sSelection = (IStructuredSelection) selection;
			if (((IStructuredSelection) selection).size() == 1) {
				if (sSelection.getFirstElement() instanceof XSDElementDeclaration) {
					result = true;
				}
			}
		}
		return result;
	}

	@Override
	public void setSelection(ISelection selection) {
		super.setSelection(selection);
		this.selection = selection;
	}

	@Override
	public void run() {
		super.run();
		XSDElementDeclaration element = null;
		if (null != selection && selection instanceof IStructuredSelection) {
			IStructuredSelection theSelection = (IStructuredSelection) selection;
			Object object = theSelection.getFirstElement();
			if (object instanceof XSDElementDeclaration) {
				element = (XSDElementDeclaration) object;
			} else if (object instanceof Adapter) {
				element = (XSDElementDeclaration) ((Adapter)object).getTarget();
			}
		}

		if (null != element) {
			browseWorkspaceForTargetModel();
		}
		if (null != model) {
			modelResource = ModelerCore.create(model);
			Schema schema = factory.createSchema();
			try {
				modelResource.getEmfResource().getContents().add(schema);
			} catch (ModelWorkspaceException e) {
				String message = util.getString("XSDElementProcedureBaseAction.error.creating.schema.in.resource", new Object[] {modelResource.getResource().getName()}); //$NON-NLS-1$
				MessageDialog.openError(getShell(), message, e.getMessage());
				util.log(IStatus.ERROR, e, 
						message);
			}
			schema.setName(element.getName());
			schema.setNameInSource(element.getName());

			builder = new ProcedureBuilder(schema, modelResource);
			List<XSDElementDeclaration> elements = new ArrayList<XSDElementDeclaration>();
			elements.add(element);
			try {
				builder.build(elements, getTraversalCtxFactory());
			} catch (ModelerCoreException e) {
				String message = util.getString("XSDElementProcedureBaseAction.error.creating.procedure"); //$NON-NLS-1$
				MessageDialog.openError(getShell(), message, e.getMessage());
				util.log(IStatus.ERROR, e, 
						message);

			}

			WorkspaceModifyOperation operation = new WorkspaceModifyOperation() {

				@Override
				public void execute(final IProgressMonitor monitor) {
					try {
						ModelUtilities.saveModelResource(modelResource,
								monitor, false, this);
					} catch (Exception e) {
						String message = util.getString("XSDElementProcedureBaseAction.error.saving.resource", new Object[] {modelResource.getResource().getName()}); //$NON-NLS-1$
						MessageDialog.openError(getShell(), message, e.getMessage());
						util.log(IStatus.ERROR, e, 
								message);
					}

					builder.createTransformations();

					try {
						ModelUtilities.saveModelResource(modelResource,
								monitor, false, this);
					} catch (Exception e) {
						String message = util.getString("XSDElementProcedureBaseAction.error.saving.resource", new Object[] {modelResource.getResource().getName()}); //$NON-NLS-1$
						MessageDialog.openError(getShell(), message, e.getMessage());
						util.log(IStatus.ERROR, e, 
								message);
					}

				}
			};

			IProgressMonitor monitor = new NullProgressMonitor();
			try {
				operation.run(monitor);
			} catch (Exception ex) {
				if (ex instanceof InvocationTargetException) {
					Throwable e = ((InvocationTargetException) ex).getTargetException();
					String message = util.getString("XSDElementProcedureBaseAction.error.creating.procedure"); //$NON-NLS-1$
					MessageDialog.openError(getShell(), message, e.getMessage());
					util.log(IStatus.ERROR, e, 
							message);
				}
				ex.printStackTrace();
			}

		}
	}

	/**
	 * Handler for Workspace Target Relational Model Browse button.
	 */
	void browseWorkspaceForTargetModel() {
		// Open the selection dialog for the target relational model
		MetamodelDescriptor descriptor = ModelerCore.getMetamodelRegistry()
				.getMetamodelDescriptor(RelationalPackage.eNS_URI);
		Object[] resources = WidgetUtil.showWorkspaceObjectSelectionDialog(
				util.getStringOrKey("XSDElementProcedureBaseAction.dialog.browseTargetModel.title"), //$NON-NLS-1$
				util.getStringOrKey("XSDElementProcedureBaseAction.dialog.browseTargetModel.msg"), //$NON-NLS-1$
				false, null,
				filter,
				new ModelResourceSelectionValidator(descriptor, false));
		if ((resources != null) && (resources.length > 0)) {
			model = (IFile) resources[0];
		}
	}
	
	private ViewerFilter filter = new ViewerFilter() {
        @Override
        public boolean select( Viewer theViewer,
                               Object theParent,
                               Object theElement ) {
            boolean result = false;

            if (theElement instanceof IResource) {
                // If the project is closed, dont show
                boolean projectOpen = ((IResource)theElement).getProject().isOpen();
                if (projectOpen) {
                    // Show open projects
                	if (theElement instanceof IProject) {
                        result = true;
                	} else if (theElement instanceof IFile) {
                        if(ModelUtil.isModelFile((IFile)theElement) 
                        		&& !ModelUtil.isXsdFile((IFile)theElement)) {
                        	ModelResource modelResource = ModelerCore.create((IFile)theElement);
                        	try {
								if(!ModelUtil.isPhysical(modelResource.getEmfResource())) {
									result = true;
								}
							} catch (ModelWorkspaceException e) {
								// return false
							}
                        }
                    } else if( theElement instanceof IFolder ) {
                    	result = true;
                    }
                }
            }

            return result;
        }
    };
    
    private Shell getShell() {
        return Display.getCurrent().getActiveShell();
    }

	abstract public ITraversalCtxFactory getTraversalCtxFactory();
	
	@Override
	public void run(IAction action) {
		run();
		
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		setSelection(selection);
	}

	@Override
	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}

	@Override
	public void init(IViewPart view) {
	}

}
