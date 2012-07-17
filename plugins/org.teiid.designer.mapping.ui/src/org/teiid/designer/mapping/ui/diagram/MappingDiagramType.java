/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.mapping.ui.diagram;

import org.eclipse.emf.ecore.EObject;
import org.teiid.designer.diagram.ui.AbstractDiagramType;
import org.teiid.designer.diagram.ui.editor.CanOpenContextException;
import org.teiid.designer.diagram.ui.editor.DiagramController;
import org.teiid.designer.diagram.ui.editor.DiagramEditor;
import org.teiid.designer.diagram.ui.editor.DiagramViewer;
import org.teiid.designer.diagram.ui.editor.IDiagramActionAdapter;
import org.teiid.designer.diagram.ui.editor.IDiagramSelectionHandler;
import org.teiid.designer.diagram.ui.figure.DiagramFigureFactory;
import org.teiid.designer.diagram.ui.model.DiagramModelFactory;
import org.teiid.designer.diagram.ui.notation.uml.model.IClassifierContentAdapter;
import org.teiid.designer.diagram.ui.pakkage.IPackageDiagramProvider;
import org.teiid.designer.diagram.ui.part.DiagramEditPartFactory;
import org.teiid.designer.diagram.ui.preferences.DiagramColorObject;
import org.teiid.designer.diagram.ui.util.colors.ColorPaletteManager;
import org.teiid.designer.mapping.ui.PluginConstants;
import org.teiid.designer.mapping.ui.UiConstants;
import org.teiid.designer.mapping.ui.actions.MappingDiagramActionAdapter;
import org.teiid.designer.mapping.ui.editor.MappingDiagramController;
import org.teiid.designer.mapping.ui.figure.MappingColorPaletteManager;
import org.teiid.designer.mapping.ui.figure.MappingDiagramFigureFactory;
import org.teiid.designer.mapping.ui.model.MappingDiagramModelFactory;
import org.teiid.designer.mapping.ui.part.MappingDiagramPartFactory;
import org.teiid.designer.metamodels.diagram.Diagram;
import org.teiid.designer.metamodels.transformation.MappingClass;
import org.teiid.designer.metamodels.transformation.StagingTable;
import org.teiid.designer.metamodels.transformation.TreeMappingRoot;
import org.teiid.designer.metamodels.xml.XmlDocument;
import org.teiid.designer.transformation.util.TransformationHelper;
import org.teiid.designer.ui.editors.ModelEditorPage;
import org.teiid.designer.ui.viewsupport.ModelObjectUtilities;
import org.teiid.designer.ui.viewsupport.ModelUtilities;


/**
 * MappingDiagramType
 */
public class MappingDiagramType extends AbstractDiagramType {
    // ============================================================================================================================
    // FIELDS
    // ============================================================================================================================
    private static DiagramEditPartFactory editPartFactory;
    private static DiagramModelFactory modelFactory;
    private static DiagramFigureFactory figureFactory;
    private static ColorPaletteManager colorPaletteManager;
    private static DiagramColorObject bkgdColorObject;
    private static IPackageDiagramProvider mappingDiagramProvider;

    private static final String CANT_OPEN_MESSAGE = "Cannot Open Transformation.\n\n" + //$NON-NLS-1$
                                                    "Model is read-only and must be writable for underlying \n" + //$NON-NLS-1$
                                                    "transformation to be constructed and displayed.\n"; //$NON-NLS-1$

    // ============================================================================================================================
    // CONSTRUCTORS
    // ============================================================================================================================

    /**
     * Construct an instance of PackageDiagramType.
     */
    public MappingDiagramType() {
        super();
    }

    // ============================================================================================================================
    // METHODS implementing IDiagramType
    // ============================================================================================================================

    /* (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.IDiagramType#getEditPartFactory()
     */
    @Override
	public DiagramEditPartFactory getEditPartFactory() {
        if (editPartFactory == null) editPartFactory = new MappingDiagramPartFactory();

        return editPartFactory;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.IDiagramType#getFigureFactory()
     */
    @Override
	public DiagramFigureFactory getFigureFactory() {
        if (figureFactory == null) figureFactory = new MappingDiagramFigureFactory();

        return figureFactory;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.IDiagramType#getModelFactory()
     */
    @Override
	public DiagramModelFactory getModelFactory() {
        if (modelFactory == null) modelFactory = new MappingDiagramModelFactory();

        return modelFactory;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.IDiagramType#getActionAdapter()
     */
    @Override
	public IDiagramActionAdapter getActionAdapter( ModelEditorPage editor ) {
        return new MappingDiagramActionAdapter(editor);
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.IDiagramType#getDisplayName()
     */
    @Override
	public String getDisplayName() {
        return UiConstants.Util.getString("DiagramNames.mappingDiagram"); //$NON-NLS-1$) ;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.IDiagramType#getSelectionHandler()
     */
    @Override
    public IDiagramSelectionHandler getSelectionHandler( DiagramViewer viewer ) {
        IDiagramSelectionHandler handler = new MappingDiagramSelectionHandler(viewer);
        return handler;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.IDiagramType#getDiagramController()
     */
    @Override
    public DiagramController getDiagramController( DiagramEditor editor ) {
        DiagramController controller = new MappingDiagramController(editor);
        return controller;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.IDiagramType#getColorPaletteManager()
     */
    @Override
    public ColorPaletteManager getColorPaletteManager() {
        if (colorPaletteManager == null) colorPaletteManager = new MappingColorPaletteManager();

        return colorPaletteManager;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.IDiagramType#getDiagramControllerClass()
     */
    @Override
    public Class getDiagramControllerClass() {
        return MappingDiagramController.class;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.IDiagramType#canOpenContext(java.lang.Object)
     */
    @Override
	public boolean canOpenContext( Object input ) throws CanOpenContextException {
        boolean canOpen = false;
        boolean readOnlyFailure = false;
        if (input instanceof EObject) {
            EObject eObj = (EObject)input;
            // Note that transient diagrams will appear stale (i.e. eObj.eResource() == NULL
            // so we need to defer to their "target" eObjects to make the call
            boolean eObjectIsStale = ModelObjectUtilities.isStale(eObj);

            if (eObj instanceof Diagram && ((Diagram)eObj).getType() != null) {
                if (((Diagram)eObj).getType().equals(PluginConstants.MAPPING_DIAGRAM_TYPE_ID)) {
                    EObject targetEObj = ((Diagram)eObj).getTarget();
                    if (targetEObj != null && !ModelObjectUtilities.isStale(targetEObj)) canOpen = true;
                } else if (((Diagram)eObj).getType().equals(PluginConstants.MAPPING_TRANSFORMATION_DIAGRAM_TYPE_ID)) {
                    EObject virtualGroupEObject = ((Diagram)eObj).getTarget();
                    if (virtualGroupEObject != null && TransformationHelper.isVirtual(virtualGroupEObject)
                        && !ModelObjectUtilities.isStale(virtualGroupEObject)) {
                        EObject transformation = TransformationHelper.getTransformationMappingRoot(virtualGroupEObject);
                        if (transformation != null) {
                            canOpen = true;
                        } else if (!ModelObjectUtilities.isReadOnly(virtualGroupEObject)) {
                            canOpen = true;
                        } else {
                            readOnlyFailure = true;
                        }
                    }
                }
            } else if (!eObjectIsStale) {
                if (MappingDiagramUtil.isMappingSqlTable(eObj)) {

                    // Need to check if this table has a transformation and if not, then if it's read-only,
                    // present dialog explaining the delema.
                    EObject transformation = TransformationHelper.getTransformationMappingRoot(eObj);
                    if (transformation != null) {
                        canOpen = true;
                    } else if (!ModelObjectUtilities.isReadOnly(eObj)) {
                        canOpen = true;
                    } else {
                        readOnlyFailure = true;
                    }
                } else if (MappingDiagramUtil.isMappingDocument(eObj)) {
                    canOpen = true;
                } else if (MappingDiagramUtil.isInputSet(eObj)) {
                    canOpen = true;
                } else if (MappingDiagramUtil.hasMappingDocument(eObj)) {
                    canOpen = true;
                } else if (MappingDiagramUtil.isTreeMappingRoot(eObj)) {
                    // defect 16988 - pay attention to TreeMappingRoots:
                    canOpen = true;
                } else if (TransformationHelper.isSqlTransformationMappingRoot(eObj)) {
                    // Case where the the T-Root target is a Staging Table
                    EObject virtualGroup = TransformationHelper.getTransformationLinkTarget(eObj);
                    if (virtualGroup instanceof StagingTable || virtualGroup instanceof MappingClass) {
                        canOpen = true;
                    }
                } else {
                    Object container = eObj.eContainer();
                    if (MappingDiagramUtil.isInputSet(container) || MappingDiagramUtil.isMappingClassColumn(eObj)) {
                        canOpen = true;
                    }
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
    @Override
	public Diagram getDiagramForContext( Object input ) {
        Diagram mappingDiagram = null;

        if (input instanceof Diagram
            && ((Diagram)input).getType() != null
            && (((Diagram)input).getType().equals(PluginConstants.MAPPING_DIAGRAM_TYPE_ID) || ((Diagram)input).getType().equals(PluginConstants.MAPPING_TRANSFORMATION_DIAGRAM_TYPE_ID))) {
            mappingDiagram = (Diagram)input;
        } else if (MappingDiagramUtil.isMappingSqlTable(input)) {
            mappingDiagram = ((MappingDiagramProvider)getPackageDiagramProvider()).getDetailedMappingDiagram((EObject)input);
        } else if (MappingDiagramUtil.isInputSet(input)) {
            EObject eoContainer = ((EObject)input).eContainer();
            if (eoContainer != null) {
                mappingDiagram = ((MappingDiagramProvider)getPackageDiagramProvider()).getDetailedMappingDiagram(eoContainer);
            }

        } else if (MappingDiagramUtil.isMappingDocument(input)) {
            mappingDiagram = getPackageDiagramProvider().getPackageDiagram(input, true);
        } else if (MappingDiagramUtil.hasMappingDocument(input)) {
            EObject thePackage = MappingDiagramUtil.getMappingDocument(input);
            if (thePackage != null) {
                mappingDiagram = getPackageDiagramProvider().getPackageDiagram(thePackage, true);
            }
        } else if (MappingDiagramUtil.isInputSetParameter(input) || MappingDiagramUtil.isMappingClassColumn(input)) {
            Object container = ((EObject)input).eContainer();
            if (MappingDiagramUtil.isInputSet(container)) {
                EObject eoContainer = ((EObject)container).eContainer();
                if (eoContainer != null) {
                    mappingDiagram = ((MappingDiagramProvider)getPackageDiagramProvider()).getDetailedMappingDiagram(eoContainer);
                }
            } else if (MappingDiagramUtil.isMappingSqlTable(container)) {
                mappingDiagram = ((MappingDiagramProvider)getPackageDiagramProvider()).getDetailedMappingDiagram((EObject)container);
            }
        } else if (MappingDiagramUtil.isTreeMappingRoot(input)) {
            // defect 16988 - pay attention to TreeMappingRoots:
            EObject targ = ((TreeMappingRoot)input).getTarget();
            mappingDiagram = getPackageDiagramProvider().getPackageDiagram(targ, true);
        } else if (TransformationHelper.isSqlTransformationMappingRoot(input)) {
            // Case where the the T-Root target is a Staging Table
            EObject virtualGroup = TransformationHelper.getTransformationLinkTarget((EObject)input);
            if (virtualGroup instanceof StagingTable || virtualGroup instanceof MappingClass) {
                mappingDiagram = ((MappingDiagramProvider)getPackageDiagramProvider()).getDetailedMappingDiagram(virtualGroup);
            }
        }

        return mappingDiagram;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.IDiagramType#getClassifierContentAdapter()
     */
    @Override
    public IClassifierContentAdapter getClassifierContentAdapter() {
        return new MappingClassContentAdapter();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.IDiagramType#getPackageDiagramProvider()
     */
    @Override
    public IPackageDiagramProvider getPackageDiagramProvider() {
        if (mappingDiagramProvider == null) mappingDiagramProvider = new MappingDiagramProvider();

        return mappingDiagramProvider;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.IDiagramType#getBackgroundColorObject()
     */
    @Override
	public DiagramColorObject getBackgroundColorObject( String extensionID ) {
        if (bkgdColorObject == null) {
            bkgdColorObject = new DiagramColorObject(getDisplayName(), PluginConstants.Prefs.Appearance.MAPPING_BKGD_COLOR);
        }
        if (extensionID.equals(PluginConstants.MAPPING_DIAGRAM_TYPE_ID)) {
            return bkgdColorObject;
        }
        return null;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.IDiagramType#getDiagramForGoToMarkerEObject(org.eclipse.emf.ecore.EObject)
     */
    @Override
    public Diagram getDiagramForGoToMarkerEObject( EObject eObject ) {
        return getDiagramForContext(eObject);
        // Diagram mappingDiagram = null;
        //        
        // if( MappingDiagramUtil.isInputSet(eObject) ||
        // MappingDiagramUtil.isInputSetParameter(eObject) ||
        // MappingDiagramUtil.isMappingSqlTable(eObject) ||
        // MappingDiagramUtil.isMappingClassColumn(eObject)) {
        // mappingDiagram = getDiagramForContext(eObject);
        //
        // } else if (MappingDiagramUtil.hasMappingDocument(eObject)) {
        // // copied from getDiagramForContext, above
        // EObject thePackage = MappingDiagramUtil.getMappingDocument(eObject);
        // if( thePackage != null ) {
        // mappingDiagram = getPackageDiagramProvider().getPackageDiagram(thePackage, forceCreate);
        // }
        // } else if( MappingDiagramUtil.isTreeMappingRoot(eObject) ) {
        // // defect 16988 - pay attention to TreeMappingRoots:
        // EObject targ = ((TreeMappingRoot)eObject).getTarget();
        // mappingDiagram = getPackageDiagramProvider().getPackageDiagram(targ, true);
        // } else if( TransformationHelper.isSqlTransformationMappingRoot(eObject)) {
        // // Case where the the T-Root target is a Staging Table
        // EObject virtualGroup = TransformationHelper.getTransformationLinkTarget(eObject);
        // if( virtualGroup instanceof StagingTable ||
        // virtualGroup instanceof MappingClass ) {
        // mappingDiagram
        // = ((MappingDiagramProvider)getPackageDiagramProvider()).getDetailedMappingDiagram(virtualGroup);
        // }
        // }

        // // make sure not still null:
        // if( mappingDiagram == null ) {
        // mappingDiagram = getPackageDiagramProvider().getPackageDiagram(eObject, forceCreate);
        //            
        // // Let's check.
        // if( mappingDiagram == null ) {
        // if( MappingDiagramUtil.hasMappingDocument(eObject)) {
        // EObject mappingDocument = MappingDiagramUtil.getMappingDocument(eObject);
        // if( mappingDocument != null )
        // mappingDiagram = getPackageDiagramProvider().getPackageDiagram(mappingDocument, forceCreate);
        // }
        // }
        // }
        // return mappingDiagram;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.IDiagramType#getDisplayedPath(org.teiid.designer.metamodels.diagram.Diagram, org.eclipse.emf.ecore.EObject)
     */
    @Override
	public String getDisplayedPath( Diagram diagram,
                                    EObject eObject ) {
        String path = null;
        if (diagram.getType() != null && diagram.getType().equals(PluginConstants.MAPPING_TRANSFORMATION_DIAGRAM_TYPE_ID)) {
            // Check to see if the modelResource for this class is same as diagram.
            if (diagram.getTarget() == null || !diagram.getTarget().equals(eObject)) {
                if (!ModelUtilities.areModelResourcesSame(diagram, eObject)) path = ModelObjectUtilities.getTrimmedFullPath(eObject);
                else path = ModelObjectUtilities.getTrimmedRelativePath(eObject);
            }
        }

        return path;
    }

    /**
     * @see org.teiid.designer.diagram.ui.IDiagramType#isTransientDiagram(org.teiid.designer.metamodels.diagram.Diagram)
     * @since 4.2
     */
    @Override
    public boolean isTransientDiagram( Diagram diagram ) {
        EObject target = diagram.getTarget();
        if (MappingDiagramUtil.isStagingTable(target)) return true;

        if (target instanceof XmlDocument) {
            return true;
        }

        return false;
    }

    /**
     * @see org.teiid.designer.diagram.ui.IDiagramType#getRevealedEObject(java.lang.Object)
     * @since 4.2
     */
    @Override
    public EObject getRevealedEObject( Diagram diagram,
                                       Object object ) {
        if (diagram.getType() != null && diagram.getType().equals(PluginConstants.MAPPING_TRANSFORMATION_DIAGRAM_TYPE_ID)) return diagram.getTarget();

        return null;
    }
}
