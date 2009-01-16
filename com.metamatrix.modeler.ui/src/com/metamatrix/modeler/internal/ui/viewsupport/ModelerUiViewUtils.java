/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

package com.metamatrix.modeler.internal.ui.viewsupport;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.RefreshAction;

import com.metamatrix.modeler.internal.ui.explorer.ModelExplorerResourceNavigator;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.modeler.ui.UiConstants.Extensions;
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
}
