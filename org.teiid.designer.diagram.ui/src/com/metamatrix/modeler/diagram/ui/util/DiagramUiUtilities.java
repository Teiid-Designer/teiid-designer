/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.EditPart;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.FontMetrics;
import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.metamodels.diagram.AbstractDiagramEntity;
import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.metamodels.diagram.DiagramContainer;
import com.metamatrix.metamodels.diagram.DiagramEntity;
import com.metamatrix.metamodels.diagram.DiagramFactory;
import com.metamatrix.metamodels.diagram.DiagramLink;
import com.metamatrix.metamodels.diagram.DiagramLinkType;
import com.metamatrix.metamodels.diagram.DiagramPosition;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.metamodel.MetamodelDescriptor;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelAspect;
import com.metamatrix.modeler.core.metamodel.aspect.uml.UmlPackage;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.modeler.diagram.ui.DiagramUiPlugin;
import com.metamatrix.modeler.diagram.ui.PluginConstants;
import com.metamatrix.modeler.diagram.ui.connection.BinaryAssociation;
import com.metamatrix.modeler.diagram.ui.connection.DiagramUmlAssociation;
import com.metamatrix.modeler.diagram.ui.connection.DiagramUmlDependency;
import com.metamatrix.modeler.diagram.ui.connection.DiagramUmlGeneralization;
import com.metamatrix.modeler.diagram.ui.connection.NodeConnectionEditPart;
import com.metamatrix.modeler.diagram.ui.connection.NodeConnectionModel;
import com.metamatrix.modeler.diagram.ui.editor.DiagramEditor;
import com.metamatrix.modeler.diagram.ui.editor.DiagramEditorUtil;
import com.metamatrix.modeler.diagram.ui.figure.DiagramFigure;
import com.metamatrix.modeler.diagram.ui.figure.LabeledRectangleFigure;
import com.metamatrix.modeler.diagram.ui.model.AbstractFreeDiagramModelNode;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;
import com.metamatrix.modeler.diagram.ui.notation.uml.model.UmlClassifierContainerNode;
import com.metamatrix.modeler.diagram.ui.notation.uml.model.UmlClassifierNode;
import com.metamatrix.modeler.diagram.ui.notation.uml.part.UmlClassifierContainerEditPart;
import com.metamatrix.modeler.diagram.ui.notation.uml.part.UmlClassifierEditPart;
import com.metamatrix.modeler.diagram.ui.part.DiagramEditPart;
import com.metamatrix.modeler.internal.ui.util.DiagramProxy;
import com.metamatrix.modeler.internal.ui.viewsupport.MarkerUtilities;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectUtilities;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.ui.graphics.GlobalUiFontManager;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;

/**
 * @author blafond To change the template for this generated type comment go to Window>Preferences>Java>Code Generation>Code and
 *         Comments
 */
public class DiagramUiUtilities {

    private static DiagramFactory diagramFactory;
    static {
        diagramFactory = DiagramFactory.eINSTANCE;
    }

    public static final int LOG_NONE = -1;

    public static final int UML_DIAGRAM = 0;
    public static final int TABLE_DIAGRAM = 1;
    public static int iDE = 0;

    private static Font baseFont = GlobalUiFontManager.getFont(new FontData("Verdana", 10, 0)); //$NON-NLS-1$
    private static Font toolTipFont = GlobalUiFontManager.getFont(new FontData("Veranda", 8, 0)); //$NON-NLS-1$

    public static Font getFont() {
        return baseFont;
    }

    public static Font getToolTipFont() {
        return toolTipFont;
    }

    public static void setLoggingLevel( int newLevel ) {
    }

    public static String getFontString( Font theFont ) {
        FontData[] fontDataArray = theFont.getFontData();
        FontData firstFont = fontDataArray[0];
        firstFont.getHeight();
        String message = "  Font =" + "( " + firstFont.getName() + //$NON-NLS-2$ //$NON-NLS-1$
                         ", " + firstFont.getHeight() + //$NON-NLS-1$
                         ", " + firstFont.getStyle() + ")";//$NON-NLS-2$ //$NON-NLS-1$

        return message;
    }

    /**
     * Returns the largest substring of <i>s</i> in Font <i>f</i> that can be confined to the number of pixels in
     * <i>availableWidth<i>.
     * 
     * @since 2.0
     */
    public static int getLargestSubstringConfinedTo( String s,
                                                     Font f,
                                                     int availableWidth ) {
        FontMetrics metrics = FigureUtilities.getFontMetrics(f);
        int min, max;
        float avg = metrics.getAverageCharWidth();
        min = 0;
        max = s.length() + 1;

        // The size of the current guess
        int guess = 0, guessSize = 0;
        while ((max - min) > 1) {
            // Pick a new guess size
            // New guess is the last guess plus the missing width in pixels
            // divided by the average character size in pixels
            guess = guess + (int)((availableWidth - guessSize) / avg);

            if (guess >= max) guess = max - 1;
            if (guess <= min) guess = min + 1;

            // Measure the current guess
            guessSize = FigureUtilities.getTextExtents(s.substring(0, guess), f).width;

            if (guessSize < availableWidth)
            // We did not use the available width
            min = guess;
            else
            // We exceeded the available width
            max = guess;
        }
        return min;
    }

    public int getFontAscent( Font someFont ) {
        return FigureUtilities.getFontMetrics(someFont).getAscent();
    }

    public int getFontDescent( Font someFont ) {
        return FigureUtilities.getFontMetrics(someFont).getDescent();
    }

    public int getFontLeading( Font someFont ) {
        return FigureUtilities.getFontMetrics(someFont).getLeading();
    }

    /**
     * Get a DiagramEntity
     */
    public static DiagramEntity getDiagramEntity( EObject eObj,
                                                  Diagram diagram ) {
        // Check to see if diagram has child entity already
        DiagramEntity diagramEntity = null;

        AbstractDiagramEntity de = findDiagramEntity(diagram, eObj);
        if (de == null) {
            diagramEntity = diagramFactory.createDiagramEntity();
            if (diagram != null) diagramEntity.setDiagram(diagram);
            diagramEntity.setModelObject(eObj);
            try {
                ModelerCore.getModelEditor().addValue(diagram, diagramEntity, diagram.getDiagramEntity());
            } catch (ModelerCoreException err) {
                DiagramUiConstants.Util.log(err);
            }
            DiagramEntityManager.addEntity(diagram, diagramEntity, eObj);
        } else if (de instanceof DiagramEntity) {
            diagramEntity = (DiagramEntity)de;
        }
        return diagramEntity;
    }

    /**
     * Create a DiagramEntity
     */
    public static DiagramLink createDiagramLink( EObject eObj,
                                                 Diagram diagram ) {
        DiagramLink diagramLink = diagramFactory.createDiagramLink();
        if (diagram != null) {
            diagramLink.setDiagram(diagram);
            // diagramEntity.basicSetDiagram(diagram);
        }
        diagramLink.setModelObject(eObj);
        DiagramEntityManager.addEntity(diagram, diagramLink, eObj);
        return diagramLink;
    }

    /**
     * Create a DiagramEntity
     */
    public static DiagramLink createDiagramLink( EObject eObj,
                                                 Diagram diagram,
                                                 int routerStyle ) {
        DiagramLink diagramLink = diagramFactory.createDiagramLink();
        if (diagram != null) {
            diagramLink.setDiagram(diagram);
            // diagramEntity.basicSetDiagram(diagram);
        }
        diagramLink.setModelObject(eObj);
        diagramLink.setType(DiagramLinkType.get(routerStyle));
        DiagramEntityManager.addEntity(diagram, diagramLink, eObj);
        return diagramLink;
    }

    /**
     * Create a DiagramPosition for DiagramLink
     */
    public static DiagramPosition createDiagramPosition( DiagramLink diagramLink,
                                                         Point position ) {
        DiagramPosition diagramPosition = diagramFactory.createDiagramPosition();
        if (diagramLink != null) {
            diagramPosition.setDiagramLink(diagramLink);
        }
        diagramPosition.setXPosition(position.x);
        diagramPosition.setYPosition(position.y);
        return diagramPosition;
    }

    /**
     * Create a DiagramPosition for DiagramLink
     */
    public static List createDiagramPositions( DiagramLink diagramLink,
                                               List points ) {
        List positions = new ArrayList(points.size());
        Iterator iter = points.iterator();
        Object nextObj = null;
        while (iter.hasNext()) {
            nextObj = iter.next();
            if (nextObj instanceof Point) {
                positions.add(createDiagramPosition(diagramLink, (Point)nextObj));
            }
        }
        // DiagramPosition diagramPosition = diagramFactory.createDiagramPosition();
        // if( diagramLink != null ) {
        // diagramPosition.setDiagramLink(diagramLink);
        // }

        return positions;
    }

    public static void deleteDiagramEntity( final DiagramEntity diagramEntity,
                                            final Object txnSource ) {
        if (diagramEntity != null && diagramEntity.getDiagram() != null) {
            boolean requiresStart = ModelerCore.startTxn(false, false, "Delete Diagram Entity", txnSource); //$NON-NLS-1$
            boolean succeeded = false;
            try {
                ModelObjectUtilities.delete(diagramEntity, false, false, txnSource, false);
                succeeded = true;
            } finally {
                if (requiresStart) {
                    if (succeeded) {
                        ModelerCore.commitTxn();
                    } else {
                        ModelerCore.rollbackTxn();
                    }
                }
            }
        }

    }

    public static void deleteDiagramEntities( final List diagramEntities,
                                              final Object txnSource ) {
        if (diagramEntities != null && !diagramEntities.isEmpty()) {
            boolean requiresStart = ModelerCore.startTxn(false, false, "Delete Diagram Entities", txnSource); //$NON-NLS-1$
            boolean succeeded = false;
            try {
                ModelObjectUtilities.delete(diagramEntities, false, false, txnSource, false);
                succeeded = true;
            } finally {
                if (requiresStart) {
                    if (succeeded) {
                        ModelerCore.commitTxn();
                    } else {
                        ModelerCore.rollbackTxn();
                    }
                }
            }
        }

    }

    /**
     * Get a DiagramModelNode for input eObject
     */
    public static DiagramModelNode getDiagramModelNode( EObject eObj,
                                                        DiagramModelNode diagramRootModelNode ) {
        List currentChildren = diagramRootModelNode.getChildren();

        if (currentChildren != null && !currentChildren.isEmpty()) {
            Iterator iter = currentChildren.iterator();
            while (iter.hasNext()) {
                DiagramModelNode childModelNode = (DiagramModelNode)iter.next();
                if (childModelNode.getModelObject() != null && childModelNode.getModelObject() != null) {
                    if (eObj.equals(childModelNode.getModelObject())) return childModelNode;
                }
            }
        }

        return null;
    }

    /**
     * Get a DiagramModelNode for input eObject
     */
    public static DiagramModelNode getRootDiagramModelNode( DiagramModelNode diagramModelNode ) {
        DiagramModelNode rootNode = null;

        if (diagramModelNode.getModelObject() != null) {
            DiagramModelNode parentNode = diagramModelNode;

            while (rootNode == null && diagramModelNode.getParent() != null && diagramModelNode.getModelObject() != null) {
                if (parentNode.getModelObject() instanceof Diagram) {
                    rootNode = parentNode;
                } else {
                    parentNode = parentNode.getParent();
                }
            }
        }

        return rootNode;
    }

    public static DiagramModelNode getModelNode( EObject someModelObject,
                                                 DiagramModelNode diagramModelNode ) {
        if (diagramModelNode.getModelObject() != null && diagramModelNode.getModelObject() == someModelObject) { // diagramModelNode.getModelObject().equals(someModelObject))
            // {
            return diagramModelNode;
        }
        DiagramModelNode matchedNode = null;
        // Check the children
        List contents = diagramModelNode.getChildren();
        if (contents != null && !contents.isEmpty()) {
            Iterator iter = contents.iterator();
            Object nextObj = null;
            DiagramModelNode nextNode = null;

            while (iter.hasNext() && matchedNode == null) {
                nextObj = iter.next();
                if (nextObj instanceof DiagramModelNode) {
                    nextNode = (DiagramModelNode)nextObj;
                    matchedNode = getModelNode(someModelObject, nextNode);
                }
            }
        }

        return matchedNode;
    }

    public static boolean diagramContainsEObject( EObject eObj,
                                                  DiagramModelNode diagramRootModelNode ) {
        if (getDiagramModelNode(eObj, diagramRootModelNode) != null) return true;

        return false;
    }

    public static DiagramEditPart getDiagramEditPart( DiagramEditPart someDiagramEditPart,
                                                      DiagramModelNode targetDiagramModelNode ) {
        // All edit parts have link back to viewer, who's contents is the diagram edit part (yeah!)
        DiagramEditPart diagramEP = (DiagramEditPart)someDiagramEditPart.getViewer().getContents();
        // now we can rely on the recursive getEditPart(node)
        DiagramEditPart foundEP = (DiagramEditPart)diagramEP.getViewer().getEditPartRegistry().get(targetDiagramModelNode);

        // diagramEP.getEditPart(targetDiagramModelNode);

        return foundEP;
    }

    public static boolean isDiagramObject( EObject eObject ) {
        if (eObject instanceof DiagramContainer || eObject instanceof Diagram || eObject instanceof AbstractDiagramEntity) return true;

        return false;
    }

    public static boolean isNonDrawingDiagramObject( EObject eObject ) {
        if ((eObject instanceof DiagramContainer || eObject instanceof Diagram || eObject instanceof DiagramEntity)) return true;

        return false;
    }

    public static boolean isPackageDiagram( EObject eObject ) {
        if (eObject instanceof Diagram && ((Diagram)eObject).getType() != null
            && ((Diagram)eObject).getType().equals(PluginConstants.PACKAGE_DIAGRAM_TYPE_ID)) return true;

        return false;
    }

    /**
     * Get a String NotationID for the input diagram object. This may or may not be persisted.
     */
    public static String getDiagramNotation( Diagram diagramContainerObject ) {
        String diagramNotation = diagramContainerObject.getNotation();
        // This will be get the default notationID for the current Extension Type;
        if (diagramNotation == null) diagramNotation = DiagramUiPlugin.getDiagramNotationManager().getCurrentExtensionId();

        return diagramNotation;
    }

    /**
     * Get a String NotationID for the input diagram object. This may or may not be persisted.
     */
    public static void setDiagramNotation( String notationId,
                                           Diagram diagramContainerObject ) {
        diagramContainerObject.setNotation(notationId);
    }

    public static void hiliteCurrentSelectionDependencies() {
        DiagramEditor de = DiagramEditorUtil.getVisibleDiagramEditor();
        if (de != null && de.getDiagramViewer() != null) {
            ISelection selection = DiagramEditorUtil.getVisibleDiagramEditor().getDiagramViewer().getSelection();
            if (SelectionUtilities.isSingleSelection(selection)) {
                EObject selectedEO = SelectionUtilities.getSelectedEObject(selection);
                if (selectedEO != null) {
                    DiagramEditorUtil.getVisibleDiagramEditor().getDiagramViewer().getSelectionHandler().hiliteDependencies(selectedEO);
                }
            }
        }
    }

    public static DiagramEditPart getClassifierParent( DiagramEditPart someEditPart ) {
        DiagramEditPart parentEditPart = null;
        if (someEditPart.getParent() != null && someEditPart.getParent() instanceof UmlClassifierContainerEditPart) {
            parentEditPart = (DiagramEditPart)someEditPart.getParent().getParent();
            if (parentEditPart.getParent() != null && parentEditPart.getParent() instanceof UmlClassifierContainerEditPart) {
                parentEditPart = getClassifierParent(parentEditPart);
            } else if (parentEditPart instanceof UmlClassifierEditPart) {
            } else {
                parentEditPart = null;
            }
        } else if (someEditPart instanceof UmlClassifierEditPart) {
            parentEditPart = someEditPart;
        }

        return parentEditPart;
    }

    public static DiagramEditPart getTopClassifierParent( DiagramEditPart someEditPart ) {
        DiagramEditPart parentEditPart = getClassifierParent(someEditPart);

        return parentEditPart;
    }

    public static DiagramModelNode getClassifierParentNode( DiagramModelNode someDiagramNode ) {
        DiagramModelNode parentClassifierNode = null;
        if (someDiagramNode.getParent() != null && someDiagramNode.getParent() instanceof UmlClassifierContainerNode) {
            parentClassifierNode = someDiagramNode.getParent().getParent();
            if (parentClassifierNode.getParent() != null
                && parentClassifierNode.getParent() instanceof UmlClassifierContainerNode) {
                parentClassifierNode = getClassifierParentNode(parentClassifierNode);
            } else if (parentClassifierNode instanceof UmlClassifierNode) {
            } else {
                parentClassifierNode = null;
            }
        } else if (someDiagramNode instanceof UmlClassifierNode) {
            parentClassifierNode = someDiagramNode;
        }

        return parentClassifierNode;
    }

    public static DiagramModelNode getTopClassifierParentNode( DiagramModelNode someDiagramNode ) {
        DiagramModelNode parentClassifierNode = getClassifierParentNode(someDiagramNode);

        return parentClassifierNode;
    }

    public static EObject getParentClassifier( EObject eObject ) {
        EObject topClassifier = null;

        Object parentObject = null;

        if (DiagramUiPlugin.getDiagramAspectManager().isClassifier(eObject)) {
            // check it's parent
            topClassifier = eObject;

            parentObject = eObject.eContainer();
            if (parentObject != null && parentObject instanceof EObject
                && DiagramUiPlugin.getDiagramAspectManager().isClassifier((EObject)parentObject)) {
                topClassifier = getParentClassifier((EObject)parentObject);
            }
        } else {
            parentObject = eObject.eContainer();
            if (parentObject != null && parentObject instanceof EObject) {
                topClassifier = getParentClassifier((EObject)parentObject);
            }
        }
        return topClassifier;
    }

    public static boolean isStandardUmlPackage( final Object input ) {
        boolean result = false;

        if (input instanceof EObject) {
            EObject eObject = (EObject)input;

            MetamodelAspect theAspect = ModelObjectUtilities.getUmlAspect(eObject);

            if (theAspect instanceof UmlPackage) {
                final String mmUri = eObject.eClass().getEPackage().getNsURI();
                /*  BML 8/4/04 Commented out because the need was replaced by a call to get the MetamodelDescriptor
                 *   and call supportsDiagrams() method.  This should do the trick.  Goutam/Steve stated that this
                 *   is the real question:  Which metamodels provide aspects that support UmlDiagramming?
                //                if( mmUri != null && 
                //                    (mmUri.equals(RELATIONAL_URI) ||
                //                     mmUri.equals(DATA_ACCESS_URI) ||
                //                     mmUri.equals(UML2_URI) ||
                //                     mmUri.equals(EXTENSIONS_URI) ||
                //                     mmUri.equals(WEBSERVICE_URI)) ) {
                //                    result = true;
                //                }
                 * 
                 */
                if (mmUri != null) {
                    MetamodelDescriptor md = ModelerCore.getMetamodelRegistry().getMetamodelDescriptor(mmUri);
                    if (md != null) result = md.supportsDiagrams();
                }
            }
        }

        return result;
    }

    public static boolean hasParentPackage( final Object input ) {
        boolean result = false;

        if (input instanceof EObject) {
            EObject eObject = (EObject)input;

            EObject parentPackage = getParentPackage(eObject);
            if (parentPackage != null) {
                result = true;
            }
        }

        return result;
    }

    public static EObject getParentPackage( final Object input ) {
        EObject packageEObject = null;

        if (input instanceof EObject) {
            EObject eObject = (EObject)input;

            Object parent = eObject.eContainer();

            if (parent != null && parent instanceof EObject) {
                if (isStandardUmlPackage((parent))) packageEObject = (EObject)parent;
                else packageEObject = getParentPackage(parent);
            }
        }

        return packageEObject;
    }

    public static boolean isModelResourceChild( final Object input ) {
        boolean result = false;

        if (input instanceof EObject && !(input instanceof Diagram)) {
            EObject eObject = (EObject)input;

            Object parent = eObject.eContainer();

            if (parent == null) {
                result = true;
            }
        }

        return result;
    }

    public static DiagramModelNode[] getNodeArray( final List nodeList ) {
        if (nodeList != null && !nodeList.isEmpty()) {
            DiagramModelNode[] nodeArray = new DiagramModelNode[nodeList.size()];
            DiagramModelNode nextNode = null;
            Iterator iter = nodeList.iterator();
            int count = 0;
            while (iter.hasNext()) {
                nextNode = (DiagramModelNode)iter.next();
                nodeArray[count] = nextNode;
                count++;
            }
            return nodeArray;
        }
        DiagramModelNode[] emptyArray = new DiagramModelNode[1];

        return emptyArray;
    }

    public static List getConnectedModelNodes( final DiagramModelNode parentDiagramModelNode ) {
        List childNodes = new ArrayList(parentDiagramModelNode.getChildren());

        HashMap connectedNodes = new HashMap();

        Iterator iter = childNodes.iterator();

        Object nextObject = null;
        DiagramModelNode nextDiagramNode = null;

        while (iter.hasNext()) {
            nextObject = iter.next();
            if (nextObject instanceof DiagramModelNode) {
                nextDiagramNode = (DiagramModelNode)nextObject;
                if (diagramNodeIsLinked(nextDiagramNode)) {
                    if (connectedNodes.get(nextDiagramNode) == null) connectedNodes.put(nextDiagramNode, "x"); //$NON-NLS-1$
                }
            }
        }

        if (connectedNodes.isEmpty()) return Collections.EMPTY_LIST;

        return new ArrayList(connectedNodes.keySet());
    }

    public static List getAllSourceConnections( final DiagramModelNode parentDiagramModelNode ) {
        List childNodes = new ArrayList(parentDiagramModelNode.getChildren());
        Iterator iter = childNodes.iterator();

        List connectionModelNodes = new ArrayList(childNodes.size());

        Object nextObject = null;
        DiagramModelNode nextDiagramNode = null;

        while (iter.hasNext()) {
            nextObject = iter.next();
            if (nextObject instanceof DiagramModelNode) {
                nextDiagramNode = (DiagramModelNode)nextObject;
                if (nextDiagramNode.getSourceConnections() != null && !nextDiagramNode.getSourceConnections().isEmpty()) {
                    connectionModelNodes.addAll(nextDiagramNode.getSourceConnections());
                }
            }
        }

        if (connectionModelNodes.isEmpty()) return Collections.EMPTY_LIST;

        return connectionModelNodes;
    }

    public static List getNonConnectedModelNodes( final DiagramModelNode parentDiagramModelNode ) {
        List childNodes = new ArrayList(parentDiagramModelNode.getChildren());

        HashMap nonConnectedNodes = new HashMap();

        Iterator iter = childNodes.iterator();
        Object nextObject = null;
        DiagramModelNode nextDiagramNode = null;
        while (iter.hasNext()) {
            nextObject = iter.next();
            if (nextObject instanceof DiagramModelNode && !(nextObject instanceof AbstractFreeDiagramModelNode)) {
                nextDiagramNode = (DiagramModelNode)nextObject;
                if (!diagramNodeIsLinked(nextDiagramNode) && nonConnectedNodes.get(nextDiagramNode) == null) nonConnectedNodes.put(nextDiagramNode,
                                                                                                                                   "x"); //$NON-NLS-1$
            }
        }

        if (nonConnectedNodes.isEmpty()) return Collections.EMPTY_LIST;

        return new ArrayList(nonConnectedNodes.keySet());
    }

    public static boolean diagramNodeIsLinked( DiagramModelNode diagramModelNode ) {
        if (diagramModelNode.getSourceConnections() != null && !diagramModelNode.getSourceConnections().isEmpty()) return true;

        if (diagramModelNode.getTargetConnections() != null && !diagramModelNode.getTargetConnections().isEmpty()) return true;

        return false;
    }

    public static String getEObjectLabel( EObject eObject ) {
        return DiagramUiPlugin.getDiagramNotationManager().getLabelProvider().getText(eObject);
    }

    public static ImageDescriptor getDecorationIcon( EObject element ) {
        ImageDescriptor icon = null;
        IMarker[] markers = null;

        IResource resrc = null;

        ModelResource modelResource = ModelUtilities.getModelResourceForModelObject(element);
        if (modelResource != null) {
            resrc = modelResource.getResource();
        }
        if (resrc != null) {
            boolean errorOccurred = false;
            try {
                markers = resrc.findMarkers(IMarker.PROBLEM, false, IResource.DEPTH_INFINITE);
            } catch (CoreException ex) {
                DiagramUiConstants.Util.log(ex);
                errorOccurred = true;
            }
            if (!errorOccurred) {
                for (int ndx = markers.length; --ndx >= 0;) {
                    IMarker marker = markers[ndx];

                    EObject targetEObject = ModelObjectUtilities.getMarkedEObject(marker);

                    boolean usable = ((element == targetEObject) || ModelObjectUtilities.isDescendant(element, targetEObject));
                    if (!usable) {
                        continue;
                    }
                    final Object attr = MarkerUtilities.getMarkerAttribute(marker, IMarker.SEVERITY, modelResource); // marker.getAttribute(IMarker.SEVERITY);
                    if (attr == null) {
                        continue;
                    }
                    // Asserting attr is an Integer...
                    final int severity = ((Integer)attr).intValue();
                    if (severity == IMarker.SEVERITY_ERROR) {
                        icon = DiagramUiPlugin.getDefault().getImageDescriptor(PluginConstants.Images.ERROR_ICON);
                        break;
                    }
                    if (icon == null && severity == IMarker.SEVERITY_WARNING) {
                        icon = DiagramUiPlugin.getDefault().getImageDescriptor(PluginConstants.Images.WARNING_ICON);
                    }
                }
            }
        }
        return icon;
    }

    public static int getErrorState( final EObject eObject ) {
        int errorState = DiagramUiConstants.NO_ERRORS;
        IMarker[] markers = null;

        IResource resrc = null;

        ModelResource modelResource = ModelUtilities.getModelResourceForModelObject(eObject);
        if (modelResource != null) {
            resrc = modelResource.getResource();
        }
        if (resrc != null) {
            boolean errorOccurred = false;
            try {
                markers = resrc.findMarkers(IMarker.PROBLEM, false, IResource.DEPTH_INFINITE);
            } catch (CoreException ex) {
                DiagramUiConstants.Util.log(ex);
                errorOccurred = true;
            }
            if (!errorOccurred) {
                for (int ndx = markers.length; --ndx >= 0;) {
                    IMarker marker = markers[ndx];

                    EObject targetEObject = ModelObjectUtilities.getMarkedEObject(marker);

                    boolean usable = (eObject == targetEObject);
                    if (!usable) {
                        continue;
                    }
                    final Object attr = MarkerUtilities.getMarkerAttribute(marker, IMarker.SEVERITY, modelResource);
                    if (attr == null) {
                        continue;
                    }
                    // Asserting attr is an Integer...
                    final int severity = ((Integer)attr).intValue();
                    if (severity == IMarker.SEVERITY_ERROR) {
                        errorState = DiagramUiConstants.HAS_ERROR;
                        break;
                    }
                    if (errorState == DiagramUiConstants.NO_ERRORS && severity == IMarker.SEVERITY_WARNING) {
                        errorState = DiagramUiConstants.HAS_WARNING;
                        break;
                    }
                }
            }
        }

        return errorState;
    }

    public static List getEObjectsWithErrorsOrWarnings( final DiagramModelNode parentDiagramModelNode ) {
        List resultList = new ArrayList();

        List diagramContents = parentDiagramModelNode.getChildren();
        Iterator iter1 = diagramContents.iterator();
        Object nextObject = null;
        DiagramModelNode nextNode = null;
        while (iter1.hasNext()) {
            nextObject = iter1.next();
            if (nextObject instanceof DiagramModelNode) {
                nextNode = (DiagramModelNode)nextObject;
                if (nextNode.getModelObject() != null && (nextNode.hasErrors() || nextNode.hasWarnings())) {
                    resultList.add(nextNode.getModelObject());
                }
                if (nextNode.getChildren() != null && !nextNode.getChildren().isEmpty()) {
                    getEObjectsWithErrorsOrWarnings(resultList, nextNode);
                }
            }
        }

        return resultList;
    }

    public static boolean hasEObjectsWithErrorsOrWarnings( final DiagramModelNode parentDiagramModelNode ) {
        if (parentDiagramModelNode == null) return false;

        List diagramContents = parentDiagramModelNode.getChildren();
        Iterator iter1 = diagramContents.iterator();
        Object nextObject = null;
        DiagramModelNode nextNode = null;
        while (iter1.hasNext()) {
            nextObject = iter1.next();
            if (nextObject instanceof DiagramModelNode) {
                nextNode = (DiagramModelNode)nextObject;
                if (nextNode.getModelObject() != null && (nextNode.hasErrors() || nextNode.hasWarnings())) {
                    return true;
                }
                List kids = nextNode.getChildren();
                if (kids != null && !kids.isEmpty()) {
                    // defect 17153 - we were shortcutting out of this early...
                    // whatever the state of the first non-empty parent node was,
                    // we would return for the entire diagram.
                    if (hasEObjectsWithErrorsOrWarnings(nextNode)) {
                        return true;
                    } // endif
                }
            }
        }

        return false;
    }

    private static List getEObjectsWithErrorsOrWarnings( List resultList,
                                                         DiagramModelNode parentDiagramModelNode ) {

        List nodeContents = parentDiagramModelNode.getChildren();
        Iterator iter1 = nodeContents.iterator();
        Object nextObject = null;
        DiagramModelNode nextNode = null;
        while (iter1.hasNext()) {
            nextObject = iter1.next();
            if (nextObject instanceof DiagramModelNode) {
                nextNode = (DiagramModelNode)nextObject;
                if (nextNode.getModelObject() != null && (nextNode.hasErrors() || nextNode.hasWarnings())) {
                    resultList.add(nextNode.getModelObject());
                }
                if (nextNode.getChildren() != null && !nextNode.getChildren().isEmpty()) {
                    getEObjectsWithErrorsOrWarnings(resultList, nextNode);
                }
            }
        }

        return resultList;
    }

    public static boolean isNestedClassifier( DiagramModelNode targetNode ) {
        if (targetNode.getParent() != null && targetNode.getParent() instanceof UmlClassifierContainerNode) return true;

        return false;
    }

    public static boolean isNestedClassifier( DiagramEditPart editPart ) {
        if (((DiagramModelNode)editPart.getModel()).getParent() != null
            && ((DiagramModelNode)editPart.getModel()).getParent() instanceof UmlClassifierContainerNode) return true;

        return false;
    }

    public static Point getNestedRelativeLocation( DiagramEditPart editPart ) {
        Point relPt = new Point();
        DiagramEditPart classifierPart = editPart;
        DiagramEditPart containerPart = null;
        boolean wasNested = false;
        while (isNestedClassifier(classifierPart)) {
            wasNested = true;
            // Add x,y origin of nested classifier within container
            relPt.x += classifierPart.getFigure().getBounds().x;
            relPt.y += classifierPart.getFigure().getBounds().y;
            // Now get's it's the delta origin from the container
            containerPart = (DiagramEditPart)classifierPart.getParent();
            relPt.x += containerPart.getFigure().getBounds().x;
            relPt.y += containerPart.getFigure().getBounds().y;
            classifierPart = (DiagramEditPart)containerPart.getParent();
        }
        if (wasNested) {
            relPt.x += classifierPart.getFigure().getBounds().x;
            relPt.y += classifierPart.getFigure().getBounds().y;
        }
        return relPt;
    }

    public static boolean isLassoableEditPart( EditPart editPart ) {
        boolean canLasso = false;
        if (editPart instanceof DiagramEditPart && !isNestedClassifier((DiagramEditPart)editPart)) {
            DiagramEditPart dep = (DiagramEditPart)editPart;
            canLasso = dep.isSelectablePart();
        }
        return canLasso;
    }

    public static boolean isLassoableFigure( IFigure figure ) {
        boolean canLasso = false;
        if (figure instanceof DiagramFigure) {
            if (!(figure instanceof LabeledRectangleFigure)) {
                canLasso = true;
            }
        }
        return canLasso;
    }

    public static boolean isNodeConnected( DiagramModelNode node ) {
        if ((node.getSourceConnections() == null || node.getSourceConnections().isEmpty())
            && (node.getTargetConnections() == null || node.getTargetConnections().isEmpty())) return false;

        return true;
    }

    public static EditPart getSourceEndEditPart( final NodeConnectionEditPart ncep ) {
        DiagramEditPart targetEP = (DiagramEditPart)ncep.getSource();
        NodeConnectionModel ncm = (NodeConnectionModel)ncep.getModel();
        if (ncm instanceof DiagramUmlGeneralization) {
            return targetEP;
        } else if (ncm instanceof DiagramUmlDependency) {
            return targetEP;
        } else if (ncm instanceof DiagramUmlAssociation) {
            DiagramEditPart dep = null;
            // here's where we need to look deeper

            DiagramModelNode diagramNode = ((DiagramModelNode)targetEP.getModel()).getParent();

            EObject endEObject = ((DiagramUmlAssociation)ncm).getBAssociation().getEnd(BinaryAssociation.SOURCE_END);
            if (endEObject != null) {
                DiagramModelNode someSourceNode = DiagramUiUtilities.getModelNode(endEObject, diagramNode);
                if (someSourceNode != null) dep = DiagramUiUtilities.getDiagramEditPart(targetEP, someSourceNode);
                if (dep != null) return dep;
            }
        }

        return targetEP;
    }

    public static EditPart getTargetEndEditPart( final NodeConnectionEditPart ncep ) {
        DiagramEditPart targetEP = (DiagramEditPart)ncep.getTarget();
        NodeConnectionModel ncm = (NodeConnectionModel)ncep.getModel();
        if (ncm instanceof DiagramUmlGeneralization) {
            return targetEP;
        } else if (ncm instanceof DiagramUmlDependency) {
            return targetEP;
        } else if (ncm instanceof DiagramUmlAssociation) {
            DiagramEditPart dep = null;
            // here's where we need to look deeper
            if (targetEP != null && targetEP.getModel() != null) {
                DiagramModelNode diagramNode = ((DiagramModelNode)targetEP.getModel()).getParent();

                EObject endEObject = ((DiagramUmlAssociation)ncm).getBAssociation().getEnd(BinaryAssociation.TARGET_END);
                if (endEObject != null) {
                    DiagramModelNode someTargetNode = DiagramUiUtilities.getModelNode(endEObject, diagramNode);
                    if (someTargetNode != null) dep = DiagramUiUtilities.getDiagramEditPart(targetEP, someTargetNode);
                    if (dep != null) return dep;
                }
            }
        }

        return targetEP;
    }

    public static boolean nodesAreConnected( DiagramModelNode sourceNode,
                                             DiagramModelNode targetNode ) {
        // Just need to look at the source node and see if the target node is at one of the ends...
        List nodeConnections = sourceNode.getSourceConnections();
        NodeConnectionModel nextConn = null;
        Iterator iter = nodeConnections.iterator();
        while (iter.hasNext()) {
            nextConn = (NodeConnectionModel)iter.next();
            if (((DiagramModelNode)nextConn.getTargetNode()).getModelObject().equals(targetNode.getModelObject())) return true;
        }

        nodeConnections = sourceNode.getTargetConnections();
        iter = nodeConnections.iterator();
        while (iter.hasNext()) {
            nextConn = (NodeConnectionModel)iter.next();
            if (((DiagramModelNode)nextConn.getSourceNode()).getModelObject().equals(targetNode.getModelObject())) return true;
        }

        return false;
    }

    public static boolean hasDiagramEntity( Diagram diagram,
                                            EObject eObject ) {
        List dEntities = new ArrayList(diagram.eContents());
        Iterator iter = dEntities.iterator();
        AbstractDiagramEntity nextDE = null;
        while (iter.hasNext()) {
            nextDE = (AbstractDiagramEntity)iter.next();
            EObject someDEMO = nextDE.getModelObject();
            if (someDEMO != null && someDEMO.equals(eObject)) return true;
        }

        return false;
    }

    public static AbstractDiagramEntity findDiagramEntity( Diagram diagram,
                                                           EObject eObject ) {
        List dEntities = new ArrayList(diagram.eContents());
        Iterator iter = dEntities.iterator();
        AbstractDiagramEntity nextDE = null;
        while (iter.hasNext()) {
            nextDE = (AbstractDiagramEntity)iter.next();
            EObject someDEMO = nextDE.getModelObject();
            if (someDEMO != null && someDEMO.equals(eObject)) return nextDE;
        }

        return null;
    }

    public static DiagramLink findDiagramLink( Diagram diagram,
                                               EObject eObject ) {
        List dEntities = new ArrayList(diagram.eContents());
        Iterator iter = dEntities.iterator();
        AbstractDiagramEntity nextDE = null;
        while (iter.hasNext()) {
            nextDE = (AbstractDiagramEntity)iter.next();
            EObject someDEMO = nextDE.getModelObject();
            if (someDEMO != null && nextDE instanceof DiagramLink && someDEMO.equals(eObject)) return (DiagramLink)nextDE;
        }

        return null;
    }

    public static boolean getReadOnlyState( Object someObject ) {
        boolean rOnly = true;
        Diagram someDiagram = null;

        if (someObject instanceof Diagram) {
            someDiagram = (Diagram)someObject;
        } else if (someObject instanceof DiagramModelNode) {
            DiagramModelNode diagramNode = (DiagramModelNode)someObject;
            if (diagramNode.getModelObject() != null && diagramNode.getModelObject() instanceof Diagram) someDiagram = (Diagram)diagramNode.getModelObject();
        }

        if (someDiagram != null) {
            if (someDiagram.getType().equals(PluginConstants.PACKAGE_DIAGRAM_TYPE_ID)) {
                if (someDiagram.getTarget() != null && someDiagram.getTarget() instanceof ModelAnnotation) {
                    rOnly = ModelObjectUtilities.isReadOnly(someDiagram.getTarget());
                } else {
                    rOnly = ModelObjectUtilities.isReadOnly(someDiagram);
                }
            } else {
                rOnly = ModelObjectUtilities.isReadOnly(someDiagram);
            }
        }

        return rOnly;
    }

    public static int getCurrentRouterStyle() {
        int style = DiagramUiConstants.LinkRouter.DIRECT;

        String router = getCurrentRouterStylePreference();
        if (router != null) {
            // Find router integer ID
            boolean selected = false;
            int i = 0;
            String[] routers = DiagramUiConstants.LinkRouter.types;
            while ((!selected) && (i < routers.length)) {
                String thisRouter = routers[i];
                if (thisRouter.equals(router)) {
                    style = i;
                    selected = true;
                } else {
                    i++;
                }
            }
        }

        return style;
    }

    public static int getCurrentRouterStyleID( String type ) {
        int style = DiagramUiConstants.LinkRouter.DIRECT;

        // Find router integer ID
        boolean selected = false;
        int i = 0;
        String[] routers = DiagramUiConstants.LinkRouter.types;
        while ((!selected) && (i < routers.length)) {
            String thisRouter = routers[i];
            if (thisRouter.equals(type)) {
                style = i;
                selected = true;
            } else {
                i++;
            }
        }

        return style;
    }

    public static String getCurrentRouterStylePreference() {
        String router = null;
        // Get preferences....
        String prefName = PluginConstants.Prefs.DIAGRAM_ROUTER_STYLE;
        IPreferenceStore preferenceStore = DiagramUiPlugin.getDefault().getPreferenceStore();
        if (preferenceStore != null) {
            router = preferenceStore.getString(prefName);
        }

        return router;
    }

    public static void setRouterStylePreference( String style ) {
        boolean notFound = true;
        int i = 0;
        // get current
        String currentStyle = getCurrentRouterStylePreference();
        if (!currentStyle.equals(style)) {
            String[] routers = DiagramUiConstants.LinkRouter.types;
            while (notFound && (i < routers.length)) {
                String thisRouter = routers[i];
                if (thisRouter.equals(style)) {
                    IPreferenceStore preferenceStore = DiagramUiPlugin.getDefault().getPreferenceStore();
                    if (preferenceStore != null) {
                        preferenceStore.setValue(PluginConstants.Prefs.DIAGRAM_ROUTER_STYLE, style);
                        DiagramUiPlugin.getDefault().savePluginPreferences();
                    }
                    notFound = false;
                } else {
                    i++;
                }
            }
            DiagramUiPlugin.updateEditorForPreferences();
        }
    }

    public static void setRouterStylePreference( int id ) {
        // get current
        int currentId = getCurrentRouterStyle();
        String[] routers = DiagramUiConstants.LinkRouter.types;
        if (currentId != id && id >= 0 && id < routers.length) {
            IPreferenceStore preferenceStore = DiagramUiPlugin.getDefault().getPreferenceStore();
            if (preferenceStore != null) {
                preferenceStore.setValue(PluginConstants.Prefs.DIAGRAM_ROUTER_STYLE, routers[id]);
                DiagramUiPlugin.getDefault().savePluginPreferences();
            }
            DiagramUiPlugin.updateEditorForPreferences();
        }
    }

    public static Collection getEObjects( DiagramModelNode root ) {
        Collection rv = new HashSet();

        // handle this node first:
        EObject e = root.getModelObject();
        if (e != null) {
            rv.add(e);
        } // endif

        // deal with any children:
        List children = root.getChildren();
        if (children != null) {
            Iterator itor = children.iterator();
            while (itor.hasNext()) {
                DiagramModelNode child = (DiagramModelNode)itor.next();
                rv.addAll(getEObjects(child));
            } // endwhile
        } // endif

        return rv;
    }

    /**
     * Determines the validity state of a diagram object. Due to model refactoring and project open/close events, a diagram may
     * become 'stale'. This method checks both the diagram's resource value and the diagram's target resource value.
     * 
     * @param diagram
     * @return
     * @since 5.0
     */
    public static boolean isValidDiagram( Diagram diagram ) {

        // Proxy was somehow created without a model resource or it went out of scope
        if (diagram instanceof DiagramProxy && ((DiagramProxy)diagram).getModelResource() == null) {
            return false;
        }

        // diagram is bogus and is not associated with a target
        if (diagram.getTarget() == null) {
            return false;
        }

        // Diagram's resource has been removed from the workspace
        if (diagram.getTarget().eResource() == null) {
            return false;
        }

        // If transient, we don't care about it's EResource, but if it's NOT, then the diagram's resource has to be non-null
        boolean isTransient = DiagramUiPlugin.getDiagramTypeManager().isTransientDiagram(diagram);
        if (!isTransient && diagram.eResource() == null) {
            return false;
        }

        return true;
    }
}
