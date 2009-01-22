/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.ui.diagram;

import org.eclipse.emf.ecore.EObject;

import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.modeler.diagram.ui.preferences.DiagramColorObject;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectUtilities;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.transformation.ui.PluginConstants;
import com.metamatrix.modeler.transformation.ui.UiConstants;

/**
 * PackageDiagramType
 */
public class DependencyDiagramType extends TransformationDiagramType {
    //============================================================================================================================
    // FIELDS
    //============================================================================================================================

    //
    // Constructors
    //
    /**
     * Construct an instance of PackageDiagramType.
     * 
     */
    public DependencyDiagramType() {
        super();
    }

    //============================================================================================================================
    // METHODS implementing IDiagramType
    //============================================================================================================================

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.transformation.ui.diagram.TransformationDiagramType#getBackgroundColorObject(java.lang.String)
     */
    @Override
    public DiagramColorObject getBackgroundColorObject(String extensionID) {
        if( bkgdColorObject == null ) {
            bkgdColorObject = new DiagramColorObject(getDisplayName(), PluginConstants.Prefs.Appearance.DEPENDENCY_BKGD_COLOR);
        }
            
        return bkgdColorObject;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.IDiagramType#getDisplayName()
     */
    @Override
    public String getDisplayName() {
        return UiConstants.Util.getString("DiagramNames.dependencyDiagram"); //$NON-NLS-1$) ;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.IDiagramType#canOpenContext(java.lang.Object)
     */
    @Override
    public boolean canOpenContext(Object input) {
        boolean canOpen = false;
        
        if( input instanceof Diagram &&
            ((Diagram)input).getType() != null &&
            ((Diagram)input).getType().equals(PluginConstants.DEPENDENCY_DIAGRAM_TYPE_ID))
            canOpen = true;
            
        return canOpen;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.IDiagramType#getDiagramForContext(java.lang.Object)
     */
    @Override
    public Diagram getDiagramForContext(Object input) {
        Diagram depDiagram = null;
        
        if( input instanceof Diagram &&
            ((Diagram)input).getType() != null &&
            ((Diagram)input).getType().equals(PluginConstants.DEPENDENCY_DIAGRAM_TYPE_ID))
            depDiagram = (Diagram)input;
            
        return depDiagram;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.IDiagramType#getDisplayedPath(com.metamatrix.metamodels.diagram.Diagram, org.eclipse.emf.ecore.EObject)
     */
    @Override
    public String getDisplayedPath(Diagram diagram, EObject eObject) {
        String path = null;
        if( diagram.getType() != null && diagram.getType().equals(PluginConstants.DEPENDENCY_DIAGRAM_TYPE_ID)) {
            // Check to see if the modelResource for this class is same as diagram.
            if( ! ModelUtilities.areModelResourcesSame(diagram, eObject) )
                path = ModelObjectUtilities.getTrimmedFullPath(eObject);
            else  
                path = ModelObjectUtilities.getTrimmedRelativePath(eObject);
        }
        
        return path;
    }
}


