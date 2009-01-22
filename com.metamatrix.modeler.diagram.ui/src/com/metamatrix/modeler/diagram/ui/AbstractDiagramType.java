/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui;


import org.eclipse.core.resources.IResource;
import org.eclipse.emf.ecore.EObject;

import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.diagram.ui.editor.DiagramController;
import com.metamatrix.modeler.diagram.ui.editor.DiagramEditor;
import com.metamatrix.modeler.diagram.ui.editor.DiagramSelectionHandler;
import com.metamatrix.modeler.diagram.ui.editor.DiagramViewer;
import com.metamatrix.modeler.diagram.ui.editor.IDiagramSelectionHandler;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;
import com.metamatrix.modeler.diagram.ui.notation.uml.model.DefaultClassContentAdapter;
import com.metamatrix.modeler.diagram.ui.notation.uml.model.IClassifierContentAdapter;
import com.metamatrix.modeler.diagram.ui.pakkage.IPackageDiagramProvider;
import com.metamatrix.modeler.diagram.ui.util.DiagramUiUtilities;
import com.metamatrix.modeler.diagram.ui.util.colors.ColorPaletteManager;
import com.metamatrix.modeler.diagram.ui.util.colors.DefaultColorPaletteManager;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectUtilities;

/**
 * @author PForhan
 */
public abstract class AbstractDiagramType implements IDiagramType {

    //
    // Class variables:
    //
    private ColorPaletteManager colorPaletteManager;

    //
    // Instance variables:
    //
    private String typeId;

    //
    // Implementation of IDiagramType methods:
    //
    public boolean dependsOnResource(DiagramModelNode root, IResource res) {
        // Query all the objects in the diagram to see from whence they come.
        if( root != null ) {
            boolean result = false;
            //start txn
            boolean requiredStart = ModelerCore.startTxn(false, false, "Check Resource Dependency", root); //$NON-NLS-1$
            boolean succeeded = false;
            try {
                result = ModelObjectUtilities.didResourceContainAny(res, DiagramUiUtilities.getEObjects(root));
                succeeded = true;
            } finally {
                //if we started the txn, commit it.
                if(requiredStart){
                    if(succeeded) {
                        ModelerCore.commitTxn();
                    } else {
                        ModelerCore.rollbackTxn();
                    }
                }
            }
            return result;
        }
        return false;
    }

    public IClassifierContentAdapter getClassifierContentAdapter() {
        return new DefaultClassContentAdapter();
    }

    public ColorPaletteManager getColorPaletteManager() {
        if( colorPaletteManager == null )
            colorPaletteManager = new DefaultColorPaletteManager();
            
        return colorPaletteManager;
    }

    public DiagramController getDiagramController(DiagramEditor editor) {
        return null;
    }
    
    @Override
    public Class<DiagramController> getDiagramControllerClass() {
        return null;
    }

    public Diagram getDiagramForGoToMarkerEObject(EObject eObject) {
        return null;
    }

    public Object getDiagramSelectionStandin(Diagram diagram) {
        return diagram;
    }

    public EObject getInitialSelection(Object object) {
        return null;
    }

    public IPackageDiagramProvider getPackageDiagramProvider() {
        return null;
    }

    public EObject getRevealedEObject(Diagram diagram, Object object) {
        return null;
    }

    public String getType() {
        return typeId;
    }

    public boolean isDiagramLarge(Diagram diagram) {
    	return false;
    }

    public boolean isDiagramTooLarge(Diagram diagram) {
    	return false;
    }

    public boolean isTransientDiagram(Diagram diagram) {
        return false;
    }

    public void setDisplayName(String displayName) {
        // does nothing. Name set by plugin constants.
    }

    public void setType(String diagramType) {
        typeId = diagramType;
    }

    public IDiagramSelectionHandler getSelectionHandler(DiagramViewer viewer) {
        return new DiagramSelectionHandler(viewer);
    }
    
    public void setColorPaletteManager(ColorPaletteManager colorPaletteManager) {
        this.colorPaletteManager = colorPaletteManager;
    }
}
