/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ui.viewsupport;

import java.util.Properties;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.RefreshAction;
import org.eclipse.ui.wizards.IWizardDescriptor;
import org.teiid.designer.core.workspace.ModelProject;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.core.workspace.ModelWorkspaceManager;
import org.teiid.designer.ui.UiConstants;
import org.teiid.designer.ui.UiPlugin;
import org.teiid.designer.ui.UiConstants.Extensions;
import org.teiid.designer.ui.common.product.ProductCustomizerMgr;
import org.teiid.designer.ui.common.util.UiUtil;
import org.teiid.designer.ui.common.util.WidgetUtil;
import org.teiid.designer.ui.explorer.ModelExplorerResourceNavigator;


/** 
 * @since 8.0
 */
public class ModelerUiViewUtils {

	private static IViewPart cachedView;

    /** 
     * @since 5.0
     */
    public ModelerUiViewUtils() {
        super();
    }
    
    
    public static void openModelResourceNavigator(ISelection selection) {
        // open navigation view
        String viewId = ProductCustomizerMgr.getInstance().getProductCharacteristics().getPrimaryNavigationViewId();
        
        openView(viewId, selection);
    }
    
    public static IViewPart openView(final String viewId, final ISelection selection, final boolean synchronous) {
    	IViewPart theView = null;
    	cachedView = null;
        if (viewId != null) {
        	
        	if( synchronous ) {
                Display.getDefault().syncExec(new Runnable() {

                    @Override
					public void run() {
                        openView(viewId, selection);
                    }
                });
        	} else {
                Display.getDefault().asyncExec(new Runnable() {

                    @Override
					public void run() {
                        openView(viewId, selection);
                    }
                });
        	}
        }
        theView = cachedView;
        cachedView = null;
        return theView;
    }
    
    private static void openView(String viewId, ISelection selection) {
        IWorkbenchPage page = UiUtil.getWorkbenchPage();
        cachedView = page.findView(viewId);
        
        // if the view is not found in current perspective then open it
        if (cachedView == null) {
            try {
            	cachedView = page.showView(viewId);
            } catch (PartInitException theException) {
                UiConstants.Util.log(IStatus.ERROR, theException, theException.getLocalizedMessage());
                WidgetUtil.showError(theException.getLocalizedMessage());
            }
        }
        
        if (cachedView != null && selection != null ) {
            // use the views selection provider (if one exists) to select object
            ISelectionProvider selectionProvider = cachedView.getViewSite().getSelectionProvider();
            
            if (selectionProvider != null) {
                selectionProvider.setSelection(new StructuredSelection(selection));
            }

        }
    }
    
    public static void refreshModelExplorerResourceNavigatorTree() {
        // activate the Model Explorer view (must do this last)
        Display.getDefault().asyncExec(new Runnable() {

            @Override
			public void run() {
                ModelExplorerResourceNavigator view = (ModelExplorerResourceNavigator)UiUtil.getViewPart(Extensions.Explorer.VIEW);

                if (view != null) {
                    view.getTreeViewer().refresh(true);
                }
            }
        });
    }
    
    public static void refreshWorkspace() {
        // activate the Model Explorer view (must do this last)
        Display.getDefault().asyncExec(new Runnable() {
            @Override
			public void run() {
                RefreshAction refreshAction = new RefreshAction(UiPlugin.getDefault().getCurrentWorkbenchWindow());
                
                refreshAction.refreshAll();
            }
        });

    }
    
    /**
     * Launches new or import type wizard given a wizard ID and an initial selection
     * 
     * @param id
     * @param selection
     */
	public static void launchWizard(String id, IStructuredSelection selection, boolean synchronous) {
		ModelerUiViewUtils.launchWizard(id, selection, null, synchronous);
	}
	
    /**
     * Launches new or import type wizard given a wizard ID and an initial selection
     * 
     * @param id
     * @param selection
     */
	public static void launchWizard(final String id, final IStructuredSelection selection, final Properties properties, final boolean synchronous) {
		
		if( synchronous ) {
	        Display.getDefault().syncExec(new Runnable() {
	        	
	            @Override
				public void run() {
	        		// First see if this is a "new wizard".
	        		IWizardDescriptor descriptor = PlatformUI.getWorkbench().getNewWizardRegistry().findWizard(id);
	        		// If not check if it is an "import wizard".
	        		if (descriptor == null) {
	        			descriptor = PlatformUI.getWorkbench().getImportWizardRegistry().findWizard(id);
	        		}
	        		// Or maybe an export wizard
	        		if (descriptor == null) {
	        			descriptor = PlatformUI.getWorkbench().getExportWizardRegistry().findWizard(id);
	        		}
	        		try {
	        			// Then if we have a wizard, open it.
	        			if (descriptor != null) {
	        				IWorkbenchWizard wizard = descriptor.createWizard();
	        				ModelerUiViewUtils.launchWizard(wizard, selection, properties, synchronous);
	        			}
	        		} catch (CoreException e) {
	        			e.printStackTrace();
	        		}
	            }
	        });
		} else {
	        Display.getDefault().asyncExec(new Runnable() {
	
	            @Override
				public void run() {
	        		// First see if this is a "new wizard".
	        		IWizardDescriptor descriptor = PlatformUI.getWorkbench().getNewWizardRegistry().findWizard(id);
	        		// If not check if it is an "import wizard".
	        		if (descriptor == null) {
	        			descriptor = PlatformUI.getWorkbench().getImportWizardRegistry().findWizard(id);
	        		}
	        		// Or maybe an export wizard
	        		if (descriptor == null) {
	        			descriptor = PlatformUI.getWorkbench().getExportWizardRegistry().findWizard(id);
	        		}
	        		try {
	        			// Then if we have a wizard, open it.
	        			if (descriptor != null) {
	        				IWorkbenchWizard wizard = descriptor.createWizard();
	        				ModelerUiViewUtils.launchWizard(wizard, selection, properties, synchronous);
	        			}
	        		} catch (CoreException e) {
	        			e.printStackTrace();
	        		}
	            }
	        });
		}

	}

	/**
	 * Launches the given wizard and initializes with the given selection
	 * 
	 * @param wizard
	 * @param selection
	 */
	public static void launchWizard(IWorkbenchWizard wizard, IStructuredSelection selection, boolean synchronous) {
		ModelerUiViewUtils.launchWizard(wizard, selection, null, synchronous);
	}
	
	/**
	 * Launches the given wizard and initializes with the given selection
	 * 
	 * @param wizard
	 * @param selection
	 */
	public static void launchWizard(final IWorkbenchWizard wizard, final IStructuredSelection selection, final Properties properties, boolean synchronous) {
		if( synchronous ) {
	        Display.getDefault().syncExec(new Runnable() {
	        	
	            @Override
				public void run() {
	        		wizard.init(PlatformUI.getWorkbench(), selection);
	        		if( properties != null && wizard instanceof IPropertiesContext ) {
	        			((IPropertiesContext)wizard).setProperties(properties);
	        		}
	
	        		WizardDialog wd = new WizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), wizard);
	        		wd.create();
	        		wd.setTitle(wizard.getWindowTitle());
	        		wd.open();
	            }
	        });
		} else  {
	        Display.getDefault().asyncExec(new Runnable() {
	
	            @Override
				public void run() {
	        		wizard.init(PlatformUI.getWorkbench(), selection);
	        		if( properties != null && wizard instanceof IPropertiesContext ) {
	        			((IPropertiesContext)wizard).setProperties(properties);
	        		}
	
	        		WizardDialog wd = new WizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), wizard);
	        		wd.create();
	        		wd.setTitle(wizard.getWindowTitle());
	        		wd.open();
	            }
	        });
		}

	}
	
	/**
	 * Method to check if the uer's workspace has any Open Teiid Model Projects
	 * @return true if one or more projects exists and are open
	 */
	public static boolean workspaceHasOpenModelProjects() {
		try {
			ModelProject[] mProjects = ModelWorkspaceManager.getModelWorkspaceManager().getModelWorkspace().getModelProjects();
			
			for( ModelProject proj : mProjects) {
				if( proj.isOpen() ) {
					return true;
				}
			}
		} catch (ModelWorkspaceException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
		
		return false;
	}
	
	/**
	 * Simple method to query user for a new Teiid Model Project
	 * 
	 * @return the new IProject if created or null if user canceled the dialog.
	 */
	public static IProject queryUserToCreateModelProject() {
		Properties newProps = new Properties();
		ModelerUiViewUtils.launchWizard("newModelProject", new StructuredSelection(), newProps, true); //$NON-NLS-1$
		
        IProject project = DesignerPropertiesUtil.getProject(newProps);
        return project;
	}

}
