/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.relationship.ui.custom;

import org.eclipse.emf.ecore.EObject;
import org.teiid.designer.diagram.ui.AbstractDiagramType;
import org.teiid.designer.diagram.ui.editor.IDiagramActionAdapter;
import org.teiid.designer.diagram.ui.figure.DiagramFigureFactory;
import org.teiid.designer.diagram.ui.model.DiagramModelFactory;
import org.teiid.designer.diagram.ui.part.DiagramEditPartFactory;
import org.teiid.designer.diagram.ui.preferences.DiagramColorObject;
import org.teiid.designer.metamodels.diagram.Diagram;
import org.teiid.designer.relationship.ui.PluginConstants;
import org.teiid.designer.relationship.ui.UiConstants;
import org.teiid.designer.relationship.ui.custom.actions.CustomDiagramActionAdapter;
import org.teiid.designer.ui.editors.ModelEditorPage;
import org.teiid.designer.ui.viewsupport.ModelObjectUtilities;


/**
 * CustomDiagramType
 */
public class CustomDiagramType extends AbstractDiagramType {
    //============================================================================================================================
    // FIELDS
    //============================================================================================================================
    private static DiagramEditPartFactory   editPartFactory;
    private static DiagramModelFactory      modelFactory;
    private static DiagramFigureFactory     figureFactory;
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
     * @See org.teiid.designer.diagram.ui.IDiagramType#getEditPartFactory()
     */
    @Override
	public DiagramEditPartFactory getEditPartFactory() {
        if( editPartFactory == null )
            editPartFactory = new CustomDiagramPartFactory();
            
        return editPartFactory;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.IDiagramType#getFigureFactory()
     */
    @Override
	public DiagramFigureFactory getFigureFactory() {
        if( figureFactory == null )
            figureFactory = new CustomDiagramFigureFactory();
            
        return figureFactory;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.IDiagramType#getModelFactory()
     */
    @Override
	public DiagramModelFactory getModelFactory() {
        if( modelFactory == null )
            modelFactory = new CustomDiagramModelFactory();
            
        return modelFactory;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.IDiagramType#getActionAdapter()
     */
    @Override
	public IDiagramActionAdapter getActionAdapter(ModelEditorPage editor) {
        return new CustomDiagramActionAdapter(editor);
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.IDiagramType#getDisplayName()
     */
    @Override
	public String getDisplayName() {
        return UiConstants.Util.getString("DiagramNames.customRelationshipDiagram"); //$NON-NLS-1$) ;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.IDiagramType#canOpenContext(java.lang.Object)
     */
    @Override
	public boolean canOpenContext(Object input) {
        boolean canOpen = false;
        
        if( input instanceof Diagram &&
            ((Diagram)input).getType() != null &&
            ((Diagram)input).getType().equals(PluginConstants.CUSTOM_RELATIONSHIP_DIAGRAM_TYPE_ID))
            canOpen = true;
            
        return canOpen;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.IDiagramType#getDiagramForContext(java.lang.Object)
     */
    @Override
	public Diagram getDiagramForContext(Object input) {
        Diagram customDiagram = null;
        
        if( input instanceof Diagram &&
            ((Diagram)input).getType() != null &&
            ((Diagram)input).getType().equals(PluginConstants.CUSTOM_RELATIONSHIP_DIAGRAM_TYPE_ID))
            customDiagram = (Diagram)input;
            
        return customDiagram;
    }
    
    /* (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.IDiagramType#getBackgroundColorObject()
     */
    @Override
	public DiagramColorObject getBackgroundColorObject(String extensionID) {
        if( bkgdColorObject == null ) {
            bkgdColorObject = new DiagramColorObject(getDisplayName(), PluginConstants.Prefs.Appearance.CUSTOM_RELATIONSHIP_BKGD_COLOR);
        }
            
        return bkgdColorObject;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.IDiagramType#getDisplayedPath(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public String getDisplayedPath(Diagram diagram, EObject eObject) {
        String path = null;
        // BML 10/4/04 - Decided today to just put the full path name on all objects in custom diagram.
        if( diagram.getType() != null && diagram.getType().equals(PluginConstants.CUSTOM_RELATIONSHIP_DIAGRAM_TYPE_ID)) {
            path = ModelObjectUtilities.getTrimmedFullPath(eObject);
        }
        
        return path;
    }
}
