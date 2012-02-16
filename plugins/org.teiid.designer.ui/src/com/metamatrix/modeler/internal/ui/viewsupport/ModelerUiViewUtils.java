/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.viewsupport;

import java.util.Properties;

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

import com.metamatrix.modeler.internal.ui.explorer.ModelExplorerResourceNavigator;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.UiConstants.Extensions;
import com.metamatrix.modeler.ui.viewsupport.IPropertiesContext;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.ui.internal.product.ProductCustomizerMgr;
import com.metamatrix.ui.internal.util.UiUtil;
import com.metamatrix.ui.internal.util.WidgetUtil;



/** 
 * @since 5.0
 */
public class ModelerUiViewUtils {

    /** 
     * @since 5.0
     */
    public ModelerUiViewUtils() {
        super();
    }
    
    
    public static void openModelResourceNavigator(ISelection selection) {
        // open navigation view
        String viewId = ProductCustomizerMgr.getInstance().getProductCharacteristics().getPrimaryNavigationViewId();
        
        if (viewId != null) {
            IWorkbenchPage page = UiUtil.getWorkbenchPage();
            IViewPart view = page.findView(viewId);
            
            // if the view is not found in current perspective then open it
            if (view == null) {
                try {
                    view = page.showView(viewId);
                } catch (PartInitException theException) {
                    UiConstants.Util.log(IStatus.ERROR, theException, theException.getLocalizedMessage());
                    WidgetUtil.showError(theException.getLocalizedMessage());
                }
            }
            
            if (view != null && selection != null ) {
                // use the views selection provider (if one exists) to select object
                ISelectionProvider selectionProvider = view.getViewSite().getSelectionProvider();
                
                if (selectionProvider != null) {
                    selectionProvider.setSelection(new StructuredSelection(selection));
                }
            }
        }
    }
    
    public static void refreshModelExplorerResourceNavigatorTree() {
        // activate the Model Explorer view (must do this last)
        Display.getDefault().asyncExec(new Runnable() {

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
	public static void launchWizard(String id, IStructuredSelection selection) {
		ModelerUiViewUtils.launchWizard(id, selection, null);
	}
	
    /**
     * Launches new or import type wizard given a wizard ID and an initial selection
     * 
     * @param id
     * @param selection
     */
	public static void launchWizard(final String id, final IStructuredSelection selection, final Properties properties) {
		
        Display.getDefault().asyncExec(new Runnable() {

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
        				ModelerUiViewUtils.launchWizard(wizard, selection, properties);
        			}
        		} catch (CoreException e) {
        			e.printStackTrace();
        		}
            }
        });

	}

	/**
	 * Launches the given wizard and initializes with the given selection
	 * 
	 * @param wizard
	 * @param selection
	 */
	public static void launchWizard(IWorkbenchWizard wizard, IStructuredSelection selection) {
		ModelerUiViewUtils.launchWizard(wizard, selection, null);
	}
	
	/**
	 * Launches the given wizard and initializes with the given selection
	 * 
	 * @param wizard
	 * @param selection
	 */
	public static void launchWizard(final IWorkbenchWizard wizard, final IStructuredSelection selection, final Properties properties) {
        Display.getDefault().asyncExec(new Runnable() {

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
