/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.mapping.ui.util;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.diagram.ui.editor.DiagramController;
import org.teiid.designer.diagram.ui.editor.DiagramEditor;
import org.teiid.designer.mapping.factory.MappingClassFactory;
import org.teiid.designer.mapping.factory.TreeMappingAdapter;
import org.teiid.designer.mapping.ui.UiConstants;
import org.teiid.designer.mapping.ui.editor.MappingDiagramController;
import org.teiid.designer.metamodels.diagram.Diagram;
import org.teiid.designer.ui.UiPlugin;
import org.teiid.designer.ui.common.AbstractUiPlugin;
import org.teiid.designer.ui.editors.ModelEditor;
import org.teiid.designer.ui.viewsupport.ModelIdentifier;
import org.teiid.designer.ui.viewsupport.ModelUtilities;


/**
 * @since 8.0
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
