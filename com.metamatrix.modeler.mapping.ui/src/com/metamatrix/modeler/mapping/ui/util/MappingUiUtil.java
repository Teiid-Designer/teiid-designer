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

package com.metamatrix.modeler.mapping.ui.util;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.diagram.ui.editor.DiagramController;
import com.metamatrix.modeler.diagram.ui.editor.DiagramEditor;
import com.metamatrix.modeler.internal.mapping.factory.MappingClassFactory;
import com.metamatrix.modeler.internal.mapping.factory.TreeMappingAdapter;
import com.metamatrix.modeler.internal.ui.editors.ModelEditor;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelIdentifier;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.mapping.ui.UiConstants;
import com.metamatrix.modeler.mapping.ui.editor.MappingDiagramController;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.ui.AbstractUiPlugin;

/**
 * @since 5.0
 */
public class MappingUiUtil implements UiConstants {
    /*
     * jh Defect 21277: This new utility class was motivated by the need to ensure that actions
     *    that add or delete mapping objects modify the current TreeMappingAdapter.
     */

    /**
     * Method finds and returns the current TreeMappingAdapter, if any. MappingUiUtil.getCurrentTreeMappingAdapter()
     */
    public static TreeMappingAdapter getCurrentTreeMappingAdapter() {
        ModelEditor editor = getActiveEditor();
        if (editor != null && editor.getCurrentPage() != null && editor.getCurrentPage() instanceof DiagramEditor) {
            DiagramController controller = ((DiagramEditor)editor.getCurrentPage()).getDiagramController();

            if (controller instanceof MappingDiagramController) {
                return ((MappingDiagramController)controller).getMappingAdapter();
            }
        }
        return null;
    }

    /**
     * Method finds and returns the current MappingClassFactory, if any. MappingUiUtil.getCurrentTreeMappingAdapter()
     */
    public static MappingClassFactory getCurrentMappingClassFactory() {
        ModelEditor editor = getActiveEditor();
        if (editor != null && editor.getCurrentPage() != null && editor.getCurrentPage() instanceof DiagramEditor) {
            DiagramController controller = ((DiagramEditor)editor.getCurrentPage()).getDiagramController();

            if (controller instanceof MappingDiagramController) {
                return ((MappingDiagramController)controller).getDocumentTreeController().getViewer().getMappingClassFactory();
            }
        }
        return null;
    }

    public static ModelEditor getActiveEditor() {
        IWorkbenchPage page = UiPlugin.getDefault().getCurrentWorkbenchWindow().getActivePage();

        // see if active page is available:
        if (page == null) {
            // not available, see if we have any reference to a page:
            page = AbstractUiPlugin.getLastValidPage();

            if (page == null) {
                // still no page; exit:
                return null;
            } // endif
        } // endif

        IEditorPart editor = page.getActiveEditor();

        if (editor instanceof ModelEditor) {
            return (ModelEditor)editor;
        }
        return null;
    }

    /**
     * Indicates if the specified <code>Diagram</code> is from a logical XML document model.
     * 
     * @param theDiagram the diagram being checked
     * @return <code>true</code> if contained in a logical model; <code>false</code> otherwise.
     * @since 5.0.2
     */
    public static boolean isLogicalModelType( Diagram theDiagram ) {
        boolean result = false;

        ModelResource model = ModelUtilities.getModelResource(theDiagram.getTarget().eResource(), true);

        if ((model != null) && ModelIdentifier.isLogicalModelType(model)) {
            result = true;
        }

        return result;
    }

}
