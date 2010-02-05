/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.custom;


import org.eclipse.emf.ecore.EObject;
import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.modeler.diagram.ui.AbstractDiagramType;
import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.modeler.diagram.ui.PluginConstants;
import com.metamatrix.modeler.diagram.ui.custom.actions.CustomDiagramActionAdapter;
import com.metamatrix.modeler.diagram.ui.editor.IDiagramActionAdapter;
import com.metamatrix.modeler.diagram.ui.figure.DiagramFigureFactory;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelFactory;
import com.metamatrix.modeler.diagram.ui.part.DiagramEditPartFactory;
import com.metamatrix.modeler.diagram.ui.preferences.DiagramColorObject;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectUtilities;
import com.metamatrix.modeler.ui.editors.ModelEditorPage;

/**
 * CustomDiagramType
 */
public class CustomDiagramType extends AbstractDiagramType {

    //============================================================================================================================
    // FIELDS
    //============================================================================================================================
    private static DiagramEditPartFactory    editPartFactory;
    private static DiagramModelFactory       modelFactory;
    private static DiagramFigureFactory      figureFactory;
    private static DiagramColorObject       bkgdColorObject;
    
    //============================================================================================================================
    // CONSTRUCTORS
    //============================================================================================================================

    /**
     * Construct an instance of PackageDiagramType.
     * 
     */
    public CustomDiagramType() {
        super();
    }

    //============================================================================================================================
    // METHODS implementing IDiagramType
    //============================================================================================================================

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.IDiagramType#getEditPartFactory()
     */
    public DiagramEditPartFactory getEditPartFactory() {
        if( editPartFactory == null )
            editPartFactory = new CustomDiagramPartFactory();
            
        return editPartFactory;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.IDiagramType#getFigureFactory()
     */
    public DiagramFigureFactory getFigureFactory() {
        if( figureFactory == null )
            figureFactory = new CustomDiagramFigureFactory();
            
        return figureFactory;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.IDiagramType#getModelFactory()
     */
    public DiagramModelFactory getModelFactory() {
        if( modelFactory == null )
            modelFactory = new CustomDiagramModelFactory();
            
        return modelFactory;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.IDiagramType#getActionAdapter()
     */
    public IDiagramActionAdapter getActionAdapter(ModelEditorPage editor) {
        return new CustomDiagramActionAdapter(editor);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.IDiagramType#getDisplayName()
     */
    public String getDisplayName() {
        return DiagramUiConstants.Util.getString("DiagramNames.customDiagram"); //$NON-NLS-1$) ;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.IDiagramType#canOpenContext(java.lang.Object)
     */
    public boolean canOpenContext(Object input) {
        boolean canOpen = false;
        
        if( input instanceof Diagram &&
            ((Diagram)input).getType() != null &&
            ((Diagram)input).getType().equals(PluginConstants.CUSTOM_DIAGRAM_TYPE_ID))
            canOpen = true;
            
        return canOpen;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.IDiagramType#getDiagramForContext(java.lang.Object)
     */
    public Diagram getDiagramForContext(Object input) {
        Diagram customDiagram = null;
        
        if( input instanceof Diagram &&
            ((Diagram)input).getType() != null &&
            ((Diagram)input).getType().equals(PluginConstants.CUSTOM_DIAGRAM_TYPE_ID))
            customDiagram = (Diagram)input;
            
        return customDiagram;
    }
    
    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.IDiagramType#getBackgroundColorObject()
     */
    public DiagramColorObject getBackgroundColorObject(String extensionID) {
        if( bkgdColorObject == null ) {
            bkgdColorObject = new DiagramColorObject(getDisplayName(), PluginConstants.Prefs.Appearance.CUSTOM_BKGD_COLOR);
        }
            
        return bkgdColorObject;
    }
    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.IDiagramType#getDisplayedPath(org.eclipse.emf.ecore.EObject)
     */
    public String getDisplayedPath(Diagram diagram, EObject eObject) {
        String path = null;
        // BML 10/4/04 - Decided today to just put the full path name on all objects in custom diagram.
        if( diagram.getType() != null && diagram.getType().equals(PluginConstants.CUSTOM_DIAGRAM_TYPE_ID)) {
            path = ModelObjectUtilities.getTrimmedFullPath(eObject);
        }
        
        return path;
    }
}

