/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.ui.diagram;

import org.eclipse.emf.ecore.EObject;
import org.teiid.designer.core.metamodel.aspect.sql.SqlAspect;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.diagram.ui.AbstractDiagramType;
import org.teiid.designer.diagram.ui.editor.CanOpenContextException;
import org.teiid.designer.diagram.ui.editor.DiagramViewer;
import org.teiid.designer.diagram.ui.editor.IDiagramActionAdapter;
import org.teiid.designer.diagram.ui.editor.IDiagramSelectionHandler;
import org.teiid.designer.diagram.ui.figure.DiagramFigureFactory;
import org.teiid.designer.diagram.ui.model.DiagramModelFactory;
import org.teiid.designer.diagram.ui.part.DiagramEditPartFactory;
import org.teiid.designer.diagram.ui.preferences.DiagramColorObject;
import org.teiid.designer.metamodels.diagram.Diagram;
import org.teiid.designer.transformation.aspects.sql.MappingClassSqlAspect;
import org.teiid.designer.transformation.ui.PluginConstants;
import org.teiid.designer.transformation.ui.UiConstants;
import org.teiid.designer.transformation.ui.actions.TransformationActionAdapter;
import org.teiid.designer.transformation.ui.figure.TransformationDiagramFigureFactory;
import org.teiid.designer.transformation.ui.model.TransformationDiagramModelFactory;
import org.teiid.designer.transformation.ui.part.TransformationDiagramPartFactory;
import org.teiid.designer.transformation.ui.util.TransformationDiagramUtil;
import org.teiid.designer.transformation.util.TransformationHelper;
import org.teiid.designer.ui.editors.ModelEditorPage;
import org.teiid.designer.ui.util.DiagramProxy;
import org.teiid.designer.ui.viewsupport.ModelObjectUtilities;
import org.teiid.designer.ui.viewsupport.ModelUtilities;


/**
 * PackageDiagramType
 */
public class TransformationDiagramType extends AbstractDiagramType {
    // ============================================================================================================================
    // FIELDS
    // ============================================================================================================================
    private static DiagramEditPartFactory editPartFactory;
    private static DiagramModelFactory modelFactory;
    private static DiagramFigureFactory figureFactory;
    protected DiagramColorObject bkgdColorObject;

    public String displayName;

    private static final String CANT_OPEN_MESSAGE = "Cannot Open Transformation.\n\n" + //$NON-NLS-1$
                                                    "Model is read-only and must be writable for underlying \n" + //$NON-NLS-1$
                                                    "transformation to be constructed and displayed.\n"; //$NON-NLS-1$

    // ============================================================================================================================
    // CONSTRUCTORS
    // ============================================================================================================================

    /**
     * Construct an instance of PackageDiagramType.
     */
    public TransformationDiagramType() {
        super();
    }

    // ============================================================================================================================
    // METHODS implementing IDiagramType
    // ============================================================================================================================

    /* (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.IDiagramType#getEditPartFactory()
     */
    public DiagramEditPartFactory getEditPartFactory() {
        if (editPartFactory == null) editPartFactory = new TransformationDiagramPartFactory();

        return editPartFactory;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.IDiagramType#getFigureFactory()
     */
    public DiagramFigureFactory getFigureFactory() {
        if (figureFactory == null) figureFactory = new TransformationDiagramFigureFactory();

        return figureFactory;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.IDiagramType#getModelFactory()
     */
    public DiagramModelFactory getModelFactory() {
        if (modelFactory == null) modelFactory = new TransformationDiagramModelFactory();

        return modelFactory;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.IDiagramType#getActionAdapter()
     */
    public IDiagramActionAdapter getActionAdapter( ModelEditorPage editor ) {
        return new TransformationActionAdapter(editor);
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.IDiagramType#getDisplayName()
     */
    public String getDisplayName() {
        return UiConstants.Util.getString("DiagramNames.transformationDiagram"); //$NON-NLS-1$) ;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.IDiagramType#setDisplayName(java.lang.String)
     */
    @Override
    public void setDisplayName( String displayName ) {
        this.displayName = displayName;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.IDiagramType#getSelectionHandler()
     */
    @Override
    public IDiagramSelectionHandler getSelectionHandler( DiagramViewer viewer ) {
        IDiagramSelectionHandler handler = new TransformationDiagramSelectionHandler(viewer);
        return handler;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.IDiagramType#canOpenContext(java.lang.Object)
     */
    public boolean canOpenContext( Object input ) throws CanOpenContextException {
        boolean canOpen = false;
        boolean readOnlyFailure = false;
        if (input instanceof EObject) {
            EObject eObj = (EObject)input;

            // Note that transient diagrams will appear stale (i.e. eObj.eResource() == NULL
            // so we need to defer to their "target" eObjects to make the call
            boolean eObjectIsStale = ModelObjectUtilities.isStale(eObj);

            if (eObj instanceof Diagram && ((Diagram)eObj).getType() != null
                && ((Diagram)eObj).getType().equals(PluginConstants.TRANSFORMATION_DIAGRAM_TYPE_ID)) {
                // Need to check if this table has a transformation and if not, then if it's read-only,
                // present dialog explaining the delema.
                EObject virtualGroupEObject = ((Diagram)eObj).getTarget();
                if (virtualGroupEObject != null && !ModelObjectUtilities.isStale(virtualGroupEObject)) {
                    EObject transformation = TransformationHelper.getTransformationMappingRoot(virtualGroupEObject);
                    if (transformation != null) {
                        canOpen = true;
                    } else if (!ModelObjectUtilities.isReadOnly(virtualGroupEObject)) {
                        canOpen = true;
                    } else {
                        readOnlyFailure = true;
                    }
                }
            } else if (!eObjectIsStale) {
                if (TransformationDiagramUtil.isStandardVirtualSqlTable(eObj)) {
                    // Need to check if this table has a transformation and if not, then if it's read-only,
                    // present dialog explaining the delema.
                    EObject transformation = TransformationHelper.getTransformationMappingRoot((EObject)input);
                    if (transformation != null) {
                        canOpen = true;
                    } else if (!ModelObjectUtilities.isReadOnly((EObject)input)) {
                        canOpen = true;
                    } else {
                        readOnlyFailure = true;
                    }
                } else if (TransformationHelper.isTransformationObject(input)) {
                    canOpen = true;
                }
            }
        }

        if (!canOpen && readOnlyFailure) {
            throw new CanOpenContextException(CANT_OPEN_MESSAGE);
        }

        return canOpen;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.IDiagramType#getDiagramForContext(java.lang.Object)
     */
    public Diagram getDiagramForContext( Object actualInput ) {
        Diagram transDiagram = null;
        Object input = actualInput;
        if (input instanceof DiagramProxy) {
            input = ((DiagramProxy)actualInput).getTarget();
            if (input == null) {
                // We have a package diagram under model resource
                // Need to set the input to the ModelResource
                input = ((DiagramProxy)actualInput).getModelResource();
            }
        }

        if (input instanceof Diagram && ((Diagram)input).getType() != null
            && ((Diagram)input).getType().equals(PluginConstants.TRANSFORMATION_DIAGRAM_TYPE_ID)) {

            EObject virtualGroupEObject = ((Diagram)input).getTarget();
            EObject transformation = TransformationHelper.getTransformationMappingRoot(virtualGroupEObject);
            if (transformation != null) {
                transDiagram = (Diagram)input;
            } else if (!ModelObjectUtilities.isReadOnly((EObject)input)) {
                transDiagram = (Diagram)input;
            }
        } else if (TransformationDiagramUtil.isStandardVirtualSqlTable(input)) {
            EObject transformation = TransformationHelper.getTransformationMappingRoot((EObject)input);
            if (transformation != null) {
                EObject eObject = (EObject)input;
                ModelResource modelResource = ModelUtilities.getModelResourceForModelObject(eObject);
                transDiagram = TransformationDiagramUtil.getTransformationDiagram(modelResource, eObject, true, true);
            } else if (!ModelObjectUtilities.isReadOnly((EObject)input)) {
                EObject eObject = (EObject)input;
                ModelResource modelResource = ModelUtilities.getModelResourceForModelObject(eObject);
                transDiagram = TransformationDiagramUtil.getTransformationDiagram(modelResource, eObject, true, true);
            }
        } else if (TransformationHelper.isTransformationObject(input)) {
            // Check the SQL Aspect and make sure it's not a Mapping Class
            if (TransformationHelper.isSqlTransformationMappingRoot(input)) {
                EObject virtualGroup = TransformationHelper.getTransformationLinkTarget((EObject)input);
                SqlAspect sqlAspect = org.teiid.designer.core.metamodel.aspect.sql.SqlAspectHelper.getSqlAspect(virtualGroup);
                if (sqlAspect instanceof MappingClassSqlAspect) {
                    // Do nothing
                } else {
                    // Get the transformation diagram
                    EObject eObject = (EObject)input;
                    ModelResource modelResource = ModelUtilities.getModelResourceForModelObject(eObject);
                    transDiagram = TransformationDiagramUtil.getTransformationDiagram(modelResource, eObject);
                }
            } else {
                // Get the transformation diagram
                EObject eObject = (EObject)input;
                ModelResource modelResource = ModelUtilities.getModelResourceForModelObject(eObject);
                transDiagram = TransformationDiagramUtil.getTransformationDiagram(modelResource, eObject);
            }
        }

        return transDiagram;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.IDiagramType#getBackgroundColorObject()
     */
    public DiagramColorObject getBackgroundColorObject( String extensionID ) {
        if (bkgdColorObject == null) {
            bkgdColorObject = new DiagramColorObject(getDisplayName(), PluginConstants.Prefs.Appearance.TRANSFORM_BKGD_COLOR);
        }

        return bkgdColorObject;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.IDiagramType#getDiagramForGoToMarkerEObject(org.eclipse.emf.ecore.EObject)
     */
    @Override
    public Diagram getDiagramForGoToMarkerEObject( EObject eObject ) {
        return getDiagramForContext(eObject);
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.IDiagramType#getDisplayedPath(org.teiid.designer.metamodels.diagram.Diagram, org.eclipse.emf.ecore.EObject)
     */
    public String getDisplayedPath( Diagram diagram,
                                    EObject eObject ) {
        String path = null;
        if (diagram.getType() != null && diagram.getType().equals(PluginConstants.TRANSFORMATION_DIAGRAM_TYPE_ID)) {
            // Check to see if the modelResource for this class is same as diagram.
            if (diagram.getTarget() == null || !diagram.getTarget().equals(eObject)) {
                if (!ModelUtilities.areModelResourcesSame(diagram, eObject)) path = ModelObjectUtilities.getTrimmedFullPath(eObject);
                else path = ModelObjectUtilities.getTrimmedRelativePath(eObject);
            }
        }

        return path;
    }

    /**
     * @see org.teiid.designer.diagram.ui.IDiagramType#getRevealedEObject(java.lang.Object)
     * @since 4.2
     */
    @Override
    public EObject getRevealedEObject( Diagram diagram,
                                       Object object ) {
        if (diagram.getType() != null
            && (diagram.getType().equals(PluginConstants.TRANSFORMATION_DIAGRAM_TYPE_ID) || diagram.getType().equals(PluginConstants.DEPENDENCY_DIAGRAM_TYPE_ID))) {
            return diagram.getTarget();
        }

        return null;
    }
}
