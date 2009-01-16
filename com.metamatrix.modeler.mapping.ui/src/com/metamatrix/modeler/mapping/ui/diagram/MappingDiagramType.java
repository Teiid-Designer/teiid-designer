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

package com.metamatrix.modeler.mapping.ui.diagram;

import org.eclipse.emf.ecore.EObject;
import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.metamodels.transformation.MappingClass;
import com.metamatrix.metamodels.transformation.StagingTable;
import com.metamatrix.metamodels.transformation.TreeMappingRoot;
import com.metamatrix.metamodels.xml.XmlDocument;
import com.metamatrix.modeler.diagram.ui.AbstractDiagramType;
import com.metamatrix.modeler.diagram.ui.editor.CanOpenContextException;
import com.metamatrix.modeler.diagram.ui.editor.DiagramController;
import com.metamatrix.modeler.diagram.ui.editor.DiagramEditor;
import com.metamatrix.modeler.diagram.ui.editor.DiagramViewer;
import com.metamatrix.modeler.diagram.ui.editor.IDiagramActionAdapter;
import com.metamatrix.modeler.diagram.ui.editor.IDiagramSelectionHandler;
import com.metamatrix.modeler.diagram.ui.figure.DiagramFigureFactory;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelFactory;
import com.metamatrix.modeler.diagram.ui.notation.uml.model.IClassifierContentAdapter;
import com.metamatrix.modeler.diagram.ui.pakkage.IPackageDiagramProvider;
import com.metamatrix.modeler.diagram.ui.part.DiagramEditPartFactory;
import com.metamatrix.modeler.diagram.ui.preferences.DiagramColorObject;
import com.metamatrix.modeler.diagram.ui.util.colors.ColorPaletteManager;
import com.metamatrix.modeler.internal.transformation.util.TransformationHelper;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectUtilities;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.mapping.ui.PluginConstants;
import com.metamatrix.modeler.mapping.ui.UiConstants;
import com.metamatrix.modeler.mapping.ui.actions.MappingDiagramActionAdapter;
import com.metamatrix.modeler.mapping.ui.editor.MappingDiagramController;
import com.metamatrix.modeler.mapping.ui.figure.MappingColorPaletteManager;
import com.metamatrix.modeler.mapping.ui.figure.MappingDiagramFigureFactory;
import com.metamatrix.modeler.mapping.ui.model.MappingDiagramModelFactory;
import com.metamatrix.modeler.mapping.ui.part.MappingDiagramPartFactory;
import com.metamatrix.modeler.ui.editors.ModelEditorPage;

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
     * @see com.metamatrix.modeler.diagram.ui.IDiagramType#getEditPartFactory()
     */
    public DiagramEditPartFactory getEditPartFactory() {
        if (editPartFactory == null) editPartFactory = new MappingDiagramPartFactory();

        return editPartFactory;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.IDiagramType#getFigureFactory()
     */
    public DiagramFigureFactory getFigureFactory() {
        if (figureFactory == null) figureFactory = new MappingDiagramFigureFactory();

        return figureFactory;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.IDiagramType#getModelFactory()
     */
    public DiagramModelFactory getModelFactory() {
        if (modelFactory == null) modelFactory = new MappingDiagramModelFactory();

        return modelFactory;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.IDiagramType#getActionAdapter()
     */
    public IDiagramActionAdapter getActionAdapter( ModelEditorPage editor ) {
        return new MappingDiagramActionAdapter(editor);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.IDiagramType#getDisplayName()
     */
    public String getDisplayName() {
        return UiConstants.Util.getString("DiagramNames.mappingDiagram"); //$NON-NLS-1$) ;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.IDiagramType#getSelectionHandler()
     */
    @Override
    public IDiagramSelectionHandler getSelectionHandler( DiagramViewer viewer ) {
        IDiagramSelectionHandler handler = new MappingDiagramSelectionHandler(viewer);
        return handler;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.IDiagramType#getDiagramController()
     */
    @Override
    public DiagramController getDiagramController( DiagramEditor editor ) {
        DiagramController controller = new MappingDiagramController(editor);
        return controller;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.IDiagramType#getColorPaletteManager()
     */
    @Override
    public ColorPaletteManager getColorPaletteManager() {
        if (colorPaletteManager == null) colorPaletteManager = new MappingColorPaletteManager();

        return colorPaletteManager;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.IDiagramType#getDiagramControllerClass()
     */
    @Override
    public Class getDiagramControllerClass() {
        return MappingDiagramController.class;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.IDiagramType#canOpenContext(java.lang.Object)
     */
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
     * @see com.metamatrix.modeler.diagram.ui.IDiagramType#getDiagramForContext(java.lang.Object)
     */
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
     * @see com.metamatrix.modeler.diagram.ui.IDiagramType#getClassifierContentAdapter()
     */
    @Override
    public IClassifierContentAdapter getClassifierContentAdapter() {
        return new MappingClassContentAdapter();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.IDiagramType#getPackageDiagramProvider()
     */
    @Override
    public IPackageDiagramProvider getPackageDiagramProvider() {
        if (mappingDiagramProvider == null) mappingDiagramProvider = new MappingDiagramProvider();

        return mappingDiagramProvider;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.IDiagramType#getBackgroundColorObject()
     */
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
     * @see com.metamatrix.modeler.diagram.ui.IDiagramType#getDiagramForGoToMarkerEObject(org.eclipse.emf.ecore.EObject)
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
     * @see com.metamatrix.modeler.diagram.ui.IDiagramType#getDisplayedPath(com.metamatrix.metamodels.diagram.Diagram, org.eclipse.emf.ecore.EObject)
     */
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
     * @see com.metamatrix.modeler.diagram.ui.IDiagramType#isTransientDiagram(com.metamatrix.metamodels.diagram.Diagram)
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
     * @see com.metamatrix.modeler.diagram.ui.IDiagramType#getRevealedEObject(java.lang.Object)
     * @since 4.2
     */
    @Override
    public EObject getRevealedEObject( Diagram diagram,
                                       Object object ) {
        if (diagram.getType() != null && diagram.getType().equals(PluginConstants.MAPPING_TRANSFORMATION_DIAGRAM_TYPE_ID)) return diagram.getTarget();

        return null;
    }
}
