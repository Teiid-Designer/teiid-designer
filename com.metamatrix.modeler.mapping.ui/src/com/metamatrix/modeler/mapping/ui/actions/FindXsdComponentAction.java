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

package com.metamatrix.modeler.mapping.ui.actions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;

import org.eclipse.emf.ecore.EObject;

import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.internal.mapping.factory.ModelMapperFactory;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.mapping.ui.UiConstants;
import com.metamatrix.modeler.mapping.ui.UiPlugin;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;
import com.metamatrix.ui.internal.product.ProductCustomizerMgr;
import com.metamatrix.ui.internal.util.UiUtil;
import com.metamatrix.ui.internal.util.WidgetUtil;


/** 
 * @since 4.3
 */
public class FindXsdComponentAction extends MappingAction {
    
    //============================================================================================================================
    // Constants
    
//    private static final String PREFIX = I18nUtil.getPropertyPrefix(FindXsdComponentAction.class);

    //============================================================================================================================
    // Constructors
    
    /**
     * Construct an instance of GenerateDependencyReportAction.
     * 
     */
    public FindXsdComponentAction() {
        super();
        this.setUseWaitCursor(false);
        setImageDescriptor(UiPlugin.getDefault().getImageDescriptor(UiConstants.Images.FIND_XSD_COMPONENT));
    }
    
    //============================================================================================================================
    // ISelectionListener Methods
    
    /**
     * @see org.eclipse.ui.ISelectionListener#selectionChanged(IWorkbenchPart, ISelection)
     * @since 4.0
     */
    @Override
    public void selectionChanged(final IWorkbenchPart part, final ISelection selection) {
        super.selectionChanged(part, selection);
        determineEnablement();
    }

    //============================================================================================================================
    // Action Methods

    /**
     * @see org.eclipse.jface.action.Action#run()
     * @since 4.0
     */
    @Override
    protected void doRun() {
        final EObject eObject = SelectionUtilities.getSelectedEObject(getSelection());
        if( eObject != null && ModelMapperFactory.isXmlTreeNode(eObject) ) {
            // Get the xsd component
            final EObject xsdComponent = ModelMapperFactory.getXsdComponent(eObject);
            if( xsdComponent != null ) {
                Display.getCurrent().asyncExec(new Runnable() {
                    public void run() {
                        try {
                            final ISelection selection = new StructuredSelection(xsdComponent);
                            String viewId = ProductCustomizerMgr.getInstance().getProductCharacteristics().getPrimaryNavigationViewId();
                            
                            IViewPart view = UiUtil.getWorkbenchPage().showView(viewId);
                            ISelectionProvider selProvider = view.getViewSite().getSelectionProvider();
                            if( selProvider != null ) {
                                if( selProvider instanceof TreeViewer ) { 
                                    showEObject((TreeViewer)selProvider, xsdComponent);
                                }
                                selProvider.setSelection(selection);
//                                view.getTreeViewer().setSelection(selection);
                            }
                        } catch (PartInitException err) {
                            UiConstants.Util.log(err);
                            WidgetUtil.showError(err.getLocalizedMessage());
                        }
                    }
                });
            }
        }
        determineEnablement();
    }
    
    //============================================================================================================================
    // Declared Methods
    
    /**
     * @since 4.0
     */
    private void determineEnablement() {
        boolean enable = false;
        final EObject eObject = SelectionUtilities.getSelectedEObject(getSelection());
        if( eObject != null && ModelMapperFactory.isXmlTreeNode(eObject)) {
            enable = true;
        }

        setEnabled(enable);
    }
    
    /** Gets the ancestors of the node in the Project and Model hierarcy
     *  in the order specified
     * 
     * @param node The node to work with
     * @param oldestFirst if true, order the list from "oldest" (most distant) ancestor to nearest ancestor 
     *  (immediate parent).  If false, order from nearest to oldest.
     * @return
     */
   public List getModelExplorerAncestors(EObject node, boolean oldestFirst) {
       List rv = new ArrayList();

       EObject parentNode = node.eContainer();

       while (parentNode != null) {
           rv.add(parentNode);
           parentNode = parentNode.eContainer();
       } // endwhile

       ModelResource mr = ModelUtilities.getModelResourceForModelObject(node);
       IResource res = mr.getResource();
       rv.add(res);
       IResource parentRes = res.getParent();
       while (parentRes != null) {
           rv.add(parentRes);
           parentRes = parentRes.getParent();
       } // endwhile

       if (oldestFirst) {
           Collections.reverse(rv);
       } // endif

       return rv;
   }

   public void showEObject(TreeViewer viewer, EObject nodeToShow) {
       List l = getModelExplorerAncestors(nodeToShow, true);
       
       Iterator itor = l.iterator();
       while (itor.hasNext()) {
           Object parentNode = itor.next();
           viewer.setExpandedState(parentNode, true);
       } // endwhile
   }
    
}
