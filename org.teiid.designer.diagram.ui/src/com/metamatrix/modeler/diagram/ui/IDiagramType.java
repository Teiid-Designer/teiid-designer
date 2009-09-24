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
import com.metamatrix.modeler.diagram.ui.editor.CanOpenContextException;
import com.metamatrix.modeler.diagram.ui.editor.DiagramController;
import com.metamatrix.modeler.diagram.ui.editor.DiagramEditor;
import com.metamatrix.modeler.diagram.ui.editor.DiagramViewer;
import com.metamatrix.modeler.diagram.ui.editor.IDiagramActionAdapter;
import com.metamatrix.modeler.diagram.ui.editor.IDiagramSelectionHandler;
import com.metamatrix.modeler.diagram.ui.figure.DiagramFigureFactory;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelFactory;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;
import com.metamatrix.modeler.diagram.ui.notation.uml.model.IClassifierContentAdapter;
//import com.metamatrix.modeler.diagram.ui.pakkage.IPackageDiagramManager;
import com.metamatrix.modeler.diagram.ui.pakkage.IPackageDiagramProvider;
import com.metamatrix.modeler.diagram.ui.part.DiagramEditPartFactory;
import com.metamatrix.modeler.diagram.ui.preferences.DiagramColorObject;
import com.metamatrix.modeler.diagram.ui.util.colors.ColorPaletteManager;
import com.metamatrix.modeler.ui.editors.ModelEditorPage;

/**
 * IDiagramType provides a standard set of methods for the diagram editor
 * to access critical compononent factories for diagram models, edit parts,
 * figures, actions and selection handlers.
 */
public interface IDiagramType {
    
    /**
     * Method for getting the diagram type extentionID
     * @return diagramType
     */
    String getType();
    
    /**
     * Method for setting the diagram type extentionID
     * @param diagramType
     */
    void setType(String diagramType);
    
    /**
     * Convienence method for returning the <code>DiagramEditPartFactory</code>
     * for the corresponding plugin.
     * 
     * @return diagramEditPartFactory
     */
    DiagramEditPartFactory getEditPartFactory();
    
    /**
     * Convienence method for returning the <code>DiagramFigureFactory</code>
     * for the corresponding plugin.
     * 
     * @return diagramFigureFactory
     */
    DiagramFigureFactory getFigureFactory();
    
    /**
     * Convienence method for returning the <code>DiagramModelFactory</code>
     * for the corresponding plugin.
     * 
     * @return diagramModelFactory
     */
    DiagramModelFactory getModelFactory();

    /**
     * Convienence method for returning the <code>DiagramActionAdapter</code>
     * for the corresponding plugin.
     * 
     * @return diagramActionAdapter
     */ 
    IDiagramActionAdapter getActionAdapter(ModelEditorPage editor);
    

    /**
     * Convienence method for returning the <code>DiagramActionAdapter</code>
     * for the corresponding plugin.
     * 
     * @return diagramSelectionHandler
     */ 
    IDiagramSelectionHandler getSelectionHandler(DiagramViewer viewer);
    
    /**
     * Convienence method for returning the <code>DiagramController</code>
     * for the corresponding plugin.
     * This controller is assumed to be placed to the left of the diagram and to the
     * right of the diagram toolbar. Any wiring is done by the controller.
     * 
     * @return diagramController
     */ 
    DiagramController getDiagramController(DiagramEditor editor);
    
    /**
     * Convienence method for returning the specific <code>DiagramController</code> class
     * for the corresponding plugin.
     * 
     * @return class of diagram controller. May be null.
     */ 
    Class<DiagramController> getDiagramControllerClass();
    
    /**
     * Method for returning the display name for the diagram type
     * @return displayName
     */
    String getDisplayName();
    
    /**
     * Method for setting the display name for the diagram type
     * @param displayName
     */
    void setDisplayName(String displayName);
    
    /**
     * Convienence method for returning a diagram type's <code>ColorPaletteManager</code>
     * @return colorPaletteManager
     */
    ColorPaletteManager getColorPaletteManager();
    
    /**
     * method used by the DiagramTypeManager's canOpenContext() method to generically ask 
     * each diagram type if it can open with the provided input
     * @return
     */
    boolean canOpenContext(Object input) throws CanOpenContextException;
    
    Diagram getDiagramForGoToMarkerEObject(EObject eObject);
    
    /**
     * This method provides a method to specialize the displayed location or path string
     * on a diagram object.
     * @param eObject
     * @return pathString
     */
    String getDisplayedPath(Diagram diagram, EObject eObject);
    
    /**
     * Method used by the DiagramTypeManager to get the diagram for a specific input
     *
     * @param input
     * @return
     */
    Diagram getDiagramForContext(Object input);
    
    /**
     * Convienence method for returning a diagram type's <code>IClassifierContentAdapter</code>
     * @return classifierContentAdapter
     */
    IClassifierContentAdapter getClassifierContentAdapter();
    
    /**
     * Convienence method for returning a diagram type's <code>IPackageDiagramProvider</code>
     * @return packageDiagramProvider
     */
    IPackageDiagramProvider getPackageDiagramProvider();
    
    
    /**
     * Method which returns a diagram type's background color object for preference purposes
     * @return
     */
    DiagramColorObject getBackgroundColorObject(String extensionID);

    /**
     * This method provides diagram types a way to generically set an initial selection after the diagram
     * has been fully displayed.  A Package diagram is an example where we want to select the "package"
     * object for the user so they can immediately begin adding objects
     * @param object
     * @return eObject
     */
    EObject getInitialSelection(Object object);
    
	/**
	 * This method provides diagram types a way to generically provide the DiagramEditor a way to find out
	 * if the diagram is much too large to display.  If so, then the display diagram should be aborted.
	 * @param diagram
	 * @return tooLarge
	 */
	boolean isDiagramTooLarge(Diagram diagram);
	
	/**
	 * This method provides diagram types a way to generically provide the DiagramEditor a way to find out
	 * if the diagram is large enough to require a progress monitor.
	 * @param diagram
	 * @return tooLarge
	 */
	boolean isDiagramLarge(Diagram diagram);
    
    /**
     * This method provides diagram types a way to generically provide an external way to determine
     * if a diagram is transient.  This is needed because transient means the same thing as a deleted
     * diagram (i.e. eContainer == null)
     * @param diagram
     * @return isTransient
     */
    boolean isTransientDiagram(Diagram diagram);
    
    /**
     * This method provides diagram types a way to define what EObject should be processed when the
     * diagram is the actual selected item on in the viewer. (Actually, the GEF viewer creates a selection
     * based on the viewer.getContents() which, in our case, is the base DiagramEditPart 
     * @param diagram
     * @return EObject
     * @since 4.2
     */
    Object getDiagramSelectionStandin(Diagram diagram);
    
    /**
     * This method provides diagram types a way to generically provide an initial object that needs to be
     * revealed after the diagram has been fully displayed.
     * A Detailed Mapping diagram is an example where we want to reveal the "mapping class"
     * object for the user so it isn't hidden from view
     * @param object
     * @return eObject
     */
    EObject getRevealedEObject(Diagram diagram, Object object);
    
    /** Provides an easy way for us to see if a diagram depends upon the specified
      *  ModelResource
      * 
      * @param res the ModelResource in question
      * @return true if this diagram makes use of the ModelResource
      */
    boolean dependsOnResource(DiagramModelNode root, IResource res);
}
