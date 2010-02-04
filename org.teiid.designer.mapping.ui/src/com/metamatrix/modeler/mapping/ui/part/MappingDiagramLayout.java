/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.mapping.ui.part;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.editparts.ZoomManager;
import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.metamodels.transformation.InputSet;
import com.metamatrix.metamodels.transformation.MappingClass;
import com.metamatrix.metamodels.transformation.MappingClassColumn;
import com.metamatrix.metamodels.transformation.StagingTable;
import com.metamatrix.metamodels.transformation.impl.MappingClassImpl;
import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.modeler.diagram.ui.editor.DiagramEditor;
import com.metamatrix.modeler.diagram.ui.editor.DiagramEditorUtil;
import com.metamatrix.modeler.diagram.ui.editor.DiagramViewer;
import com.metamatrix.modeler.diagram.ui.layout.DiagramLayout;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;
import com.metamatrix.modeler.diagram.ui.notation.uml.model.UmlClassifierNode;
import com.metamatrix.modeler.diagram.ui.notation.uml.model.UmlPackageNode;
import com.metamatrix.modeler.diagram.ui.util.DiagramUiUtilities;
import com.metamatrix.modeler.mapping.ui.PluginConstants;
import com.metamatrix.modeler.mapping.ui.editor.MappingAdapterFilter;
import com.metamatrix.modeler.mapping.ui.editor.MappingDiagramController;
import com.metamatrix.modeler.mapping.ui.editor.MappingExtent;
import com.metamatrix.modeler.mapping.ui.model.MappingDiagramNode;
import com.metamatrix.modeler.mapping.ui.model.MappingExtentNode;
import com.metamatrix.modeler.mapping.ui.util.MappingUiUtil;
import com.metamatrix.modeler.transformation.ui.model.TransformationNode;
import com.metamatrix.modeler.transformation.ui.part.TransformationDiagramLayout;
import com.metamatrix.modeler.xsd.util.ModelerXsdUtils;

/**
 * TransformationDiagramLayout
 */
public class MappingDiagramLayout extends DiagramLayout {
    public static final int TOP_MARGIN = TransformationDiagramLayout.TOP_MARGIN;
    public static final int LEFT_PANEL_LEFT_MARGIN = TransformationDiagramLayout.LEFT_PANEL_LEFT_MARGIN;
    public static final int LEFT_PANEL_RIGHT_MARGIN = TransformationDiagramLayout.LEFT_PANEL_RIGHT_MARGIN;
    public static final int RIGHT_PANEL_LEFT_MARGIN = TransformationDiagramLayout.RIGHT_PANEL_LEFT_MARGIN;
    public static final int TABLE_GAP =  TransformationDiagramLayout.TABLE_GAP;
    public static final int MAPPING_CLASS_MARGIN = 120;
    public static final int LEVEL_INCREMENT = 10;
    
    private boolean detailedMapping = false;
    private DiagramModelNode modelRoot = null;
    private DiagramModelNode transformationNode = null;
    private DiagramModelNode diagramModelNode = null;
    private List stagingTableNodes = null;
    private boolean bIsCoarseMapping = false;
//    private List lstTreeOrderedMappingClassList;
    
    
    // jh test only:
//    private static int setSourceNodes_ExecutionsCount = 0;
//    private static int getCurrentSourceNodes_ExecutionsCount = 0;
//    private static int getOrderedMappingClasses_ExecutionsCount = 0;
//    private static int getLeftoverExtentNodes_ExecutionsCount = 0;
//    private static int getLowestExtentNode_ExecutionsCount = 0;
//    private static int getMappingClassForLowestExtentNode_ExecutionsCount = 0;

    
    // end jh test only:

    /**
     * Construct an instance of TransformationDiagramLayout.
     * 
     */
    public MappingDiagramLayout() {
        super();
    }

    /**
     * Construct an instance of TransformationDiagramLayout.
     * @param newNodes
     */
    public MappingDiagramLayout(DiagramModelNode mappingDiagramModelNode, boolean bIsCoarseMapping) {
        super(Collections.EMPTY_LIST);
        this.bIsCoarseMapping = bIsCoarseMapping;
        init(mappingDiagramModelNode);
    }
    
    public int run(int startingX, int startingY) {
        layoutTransformation(startingX, startingY);
        return SUCCESSFUL;
    }
    
    private void init(DiagramModelNode diagramModelNode) {
        this.diagramModelNode = diagramModelNode;
        // Let's populate the layout components
        if( ((Diagram)diagramModelNode.getModelObject()).getType() != null &&
            ((Diagram)diagramModelNode.getModelObject()).getType().equals(PluginConstants.MAPPING_TRANSFORMATION_DIAGRAM_TYPE_ID) )
            detailedMapping = true;
        setTransformation(getTransformationNode());
        modelRoot = getRootNode();
            
		resetAllExtentNodes();
        
        setSourceNodes();
        
        /*
         * jh fix for Defect 20235: Dropping this test; do setStagingTableNodes() even for DETAILED
         */
            setStagingTableNodes();
    }
    
    public boolean isDetailedMapping() {
        return detailedMapping;
    }
    
    public void setTransformation(DiagramModelNode transNode) {
        transformationNode = transNode;
    }

    private void layoutTransformation(int startingX, int startY) {

        // Define three X positions, root, transformation and source stack
        int transX = 0; 
        int transY = 0;
        int stackX = 0;

        int stackHeight = getStackHeight();
        int startingY = Math.max(startY, TOP_MARGIN);
        
		int initialY = 10;
        if( modelRoot != null ) {
            modelRoot.setPosition(new Point(MAPPING_CLASS_MARGIN, startingY));
            
            transY = modelRoot.getY();             
            transX = MAPPING_CLASS_MARGIN + modelRoot.getWidth() + LEFT_PANEL_RIGHT_MARGIN - transformationNode.getWidth()/2;
            
            transformationNode.setPosition(new Point(transX, transY));
            
			initialY = transY + transformationNode.getHeight()/2 - stackHeight/2;
            if( initialY < 10 )
                initialY = 10;
            stackX = MAPPING_CLASS_MARGIN + modelRoot.getWidth() + LEFT_PANEL_RIGHT_MARGIN + RIGHT_PANEL_LEFT_MARGIN;
        } else {
            stackX = 120; 
        }
        
        int maxStackX = stackX;
        
        if( getComponentCount() > 0 ) {
            DiagramModelNode[] nodeArray = getNodeArray();
            int currentY = startingY;
            int nNodes = nodeArray.length;
            // Place the input set
            DiagramModelNode inputSetNode = getInputSetNode();
            if( inputSetNode != null ) {
                inputSetNode.setPosition(new Point(stackX, currentY));
                // currentY += inputSetNode.getSize().height + 20;
                stackX += inputSetNode.getWidth() + TABLE_GAP;
            }
            for( int i=0; i<nNodes; i++) {
                DiagramModelNode next = nodeArray[i];
                if( !isInputSet(next) ) {
                    if( isDetailedMapping()) {
                        if( next != null && !isRootNode(next) ) {
                            next.setPosition(new Point(stackX, currentY));
                            stackX += next.getWidth() + TABLE_GAP;
            				//currentY += next.getSize().height + 20;
                            //maxStackX = Math.max(maxStackX, (stackX+next.getWidth()));
                        }
                    } else {
                        if( next != null && !isStagingTable(next) ) {
                            int deltaX = getLevel(next)*LEVEL_INCREMENT;
                            next.setPosition(new Point(stackX + deltaX, currentY));
            				currentY += next.getSize().height + 20;
                            maxStackX = Math.max(maxStackX, (stackX+next.getWidth()));
                        }
                    }
                }
            }
        }
        
        // Now let's layout staging table nodes...
        // Only if it's a Coarse Mapping Diagram
        if( !detailedMapping && stagingTableNodes != null && !stagingTableNodes.isEmpty() ) {
            // get their extent edit model node, and get it's Y value and set it on the staging table
            // use the widest mapping class, add 50 pixels and use as the starting X.
            maxStackX += 50;
            Iterator stIter = stagingTableNodes.iterator();
            DiagramModelNode extentNode = null;
            DiagramModelNode nextNode = null;
            
            while( stIter.hasNext() ) {
                nextNode = (DiagramModelNode)stIter.next();
                extentNode = getExtentNodeForStagingTable(nextNode);
                int deltaX =  getLevel(nextNode)*LEVEL_INCREMENT;
                if( extentNode != null ) {
                    int xPos = maxStackX + deltaX;
					((MappingExtentNode)extentNode).update(DiagramUiConstants.DiagramNodeProperties.SIZE);
                    nextNode.setPosition( new Point(xPos, extentNode.getY()));
                }
            }
            
            stackStagingTables();
        }
        
        // layout enumerated types if required
        if (MappingUiUtil.isLogicalModelType((Diagram)this.diagramModelNode.getModelObject())) {
            maxStackX += 50;            
            Map typeNodesProcessedMap = new HashMap();
            List mappingClasses = getOrderedMappingClassList();
            
            // key=MappingClass, value=List of DiagramModelNode of enumeration types
            Map mappingClassTypeNodesMap = getMappingClassEnumeratedTypeNodesMap();
            
            if (!mappingClassTypeNodesMap.isEmpty()) {
                EObject mc = null;
                List enumNodes = null;
                DiagramModelNode mcNode = null;
                DiagramModelNode prevTypeNode = null;
                
                // loop through the ordered mapping classes seeing if they are associated with any
                // enumerated type diagram model nodes.
                for (int numMapClasses = mappingClasses.size(), i = 0; i < numMapClasses; ++i) {
                    mc = (EObject)mappingClasses.get(i);
                    enumNodes = (List)mappingClassTypeNodesMap.get(mc);
                    
                    if ((enumNodes != null) && !enumNodes.isEmpty()) {
                        mcNode = DiagramUiUtilities.getModelNode(mc, this.diagramModelNode);
                    
                        if (mcNode != null) {
                            for (int numNodes = enumNodes.size(), j = 0; j < numNodes; ++j) {
                                DiagramModelNode typeNode = (DiagramModelNode)enumNodes.get(j);
                                
                                // only process the enumerated type node if it hasn't been positioned before
                                if (!typeNodesProcessedMap.containsKey(typeNode)) {
                                    int y = mcNode.getY();
                                    typeNodesProcessedMap.put(typeNode, null);
                                    
                                    // position either at the y position of the mapping class or
                                    // the last y position of the previous enumerated type node
                                    if (prevTypeNode != null) {
                                        y = Math.max(prevTypeNode.getY() + prevTypeNode.getHeight() + 10, y);
                                    }
                                    
                                    prevTypeNode = typeNode;
                                    typeNode.setPosition(new Point(maxStackX, y));
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    @Override
    public DiagramModelNode[] getNodeArray() {

        DiagramModelNode[] nodeArray = super.getNodeArray();
        
        // the Detailed diagram will use this array as-is:
        if ( !bIsCoarseMapping ) {
            return nodeArray;
        }
        
        // the Coarse diagram requires us to synchronize the Mapping Classes in the diagram
        // to their anchor points in the tree, so we'll order this array:
        int iTotalDiagramNodes = nodeArray.length;
        int iDiagramNodesMapped = 0;
        
        
        // now order the diagram nodes to agree with the sequence of List getOrderedMappingClassList()
        DiagramModelNode[] orderedNodeArray = new DiagramModelNode[getComponentCount()];

        Iterator it = getOrderedMappingClassList().iterator();
        while ( it.hasNext() && iDiagramNodesMapped < iTotalDiagramNodes ) {
            
            MappingClassImpl mc = (MappingClassImpl)it.next();
            int count = 0;
            
            if ( mc != null ) {
                
                DiagramModelNode nextNode = null;

                for( int i = 0; i < nodeArray.length; i++ ) {
                    nextNode = nodeArray[ i ];
                    
                    // How can we tell what mapping class this diagram node belongs to?
                    if( nextNode instanceof UmlClassifierNode ) {

                        if ( nextNode.getModelObject() == mc ) { 
                        
                            orderedNodeArray[ iDiagramNodesMapped ] = nextNode;
                            ++iDiagramNodesMapped;
                            break;
                        }
                    }
                }
                count++;
            }
        }
        
//        System.out.println("[MappingDiagramLayout.getNodeArray] iTotalDiagramNodes / iDiagramNodesMapped: " + iTotalDiagramNodes + " / " + iDiagramNodesMapped );
        return orderedNodeArray;
    }
    
    /**
     * Obtains all the <code>DiagramModelNode</code>s that are associated with enumerated types. 
     * @return the diagram model nodes (never <code>null</code>)
     * @since 5.0.2
     */
    private DiagramModelNode[] getEnumeratedTypeNodes() {
        DiagramModelNode[] result = null;
        DiagramModelNode[] nodeArray = super.getNodeArray();
        
        if (nodeArray.length != 0) {
            List temp = new ArrayList();
            
            // loop through all nodes looking for the diagram nodes associated with enumerated types
            for (int i = 0; i < nodeArray.length; ++i) {
                if (ModelerXsdUtils.isEnumeratedType(nodeArray[i].getModelObject())) {
                    temp.add(nodeArray[i]);
                }
            }
            
            if (!temp.isEmpty()) {
                temp.toArray(result = new DiagramModelNode[temp.size()]);
            }
        }
        
        if (result == null) {
            result = new DiagramModelNode[0];
        }
        
        return result;
    }

    /**
     * Obtains a <code>Map</code> keyed by the <code>MappingClass</code> and having a value of a <code>List</code>
     * containing the diagram model nodes for all the associated enumerated types.
     * @return the map
     * @since 5.0.2
     */
    private Map getMappingClassEnumeratedTypeNodesMap() {
        DiagramModelNode[] enumTypeNodes = getEnumeratedTypeNodes();
        Map result = null;

        if (enumTypeNodes.length == 0) {
            result = Collections.EMPTY_MAP;
        } else {
            // loop through all mapping classes finding which of their columns have a type
            // that is an enumerated type and then find the associated diagram model node of that enumerated type.
            List mappingClasses = getOrderedMappingClassList();
            result = new HashMap();
            
            // MAPPING_CLASS_LOOP:
            for (int numMappingClasses = mappingClasses.size(), i = 0; i < numMappingClasses; ++i) {
                MappingClass mc = (MappingClass)mappingClasses.get(i);
                List cols = mc.getColumns();
                
                if (!cols.isEmpty()) {
                    // SEARCH_COLUMNS_LOOP:
                    for (int numCols = cols.size(), j = 0; j < numCols; ++j) {
                        MappingClassColumn col = (MappingClassColumn)cols.get(j);
                        EObject type = col.getType();
                        
                        if (ModelerXsdUtils.isEnumeratedType(type)) {
                            // find enumerated type model node
                            FIND_ENUM_NODE_LOOP:
                            for (int k = 0; k < enumTypeNodes.length; ++k) {
                                if (type == enumTypeNodes[k].getModelObject()) {
                                    List typeNodes = (List)result.get(mc);
                                    
                                    if (typeNodes == null) {
                                        typeNodes = new ArrayList();
                                        result.put(mc, typeNodes);
                                    }
                                    
                                    // make sure node is only added once. could have columns with same type.
                                    if (!typeNodes.contains(enumTypeNodes[k])) {
                                        typeNodes.add(enumTypeNodes[k]);
                                    }
                                    
                                    break FIND_ENUM_NODE_LOOP;
                                }
                            }
                        }
                    }
                }
            }
        }

        return result;
    }
    
    private void setSourceNodes() {
//        System.out.println("[MappingDiagramLayout.setSourceNodes_ExecutionsCount] Execution: " + ++setSourceNodes_ExecutionsCount );
//        Thread.dumpStack();
        Iterator iter = getCurrentSourceNodes().iterator();
//        Iterator iter = this.diagramModelNode.getChildren().iterator();
        DiagramModelNode nextNode = null;
        while( iter.hasNext() ) {
            nextNode = (DiagramModelNode)iter.next();
            if( isDetailedMapping() ) {
            	if( nextNode instanceof UmlClassifierNode ) {
            		add(nextNode);
            	}
            } else {
	            if( nextNode instanceof UmlClassifierNode && !isStagingTable( nextNode ) ) {
	            	add(nextNode);
	            }
            }
        }
//        System.out.println("[MappingDiagramLayout.setSourceNodes] BOT; We have these source nodes: " + getComponents().size() );
    }
    
    
    private void setStagingTableNodes() {
        stagingTableNodes = Collections.EMPTY_LIST;
        if( !isDetailedMapping() ) {
        	stagingTableNodes = new ArrayList(10);
	        Iterator iter = diagramModelNode.getChildren().iterator();
	        DiagramModelNode nextNode = null;
	        while( iter.hasNext() ) {
	            nextNode = (DiagramModelNode)iter.next();
	            if( isStagingTable(nextNode) )
	                stagingTableNodes.add(nextNode);
	        }
        }
    }
    
    private void stackStagingTables() {
    	if( stagingTableNodes != null || !stagingTableNodes.isEmpty() && stagingTableNodes.size() > 1 ) {
	    	orderStagingTableNodes();
	    	
	    	// Now let's walk through the staging tables list.  Keep track of the current table size
	    	// and check the next... if there is an overlap, then we move the lower one.
	    	// Then we do it all over again.
	    	
	    	DiagramModelNode firstST = null;
			DiagramModelNode secondST = null;
	    	
	    	int firstTable = 0;
	    	int secondTable = 1;
	    	int nTables = stagingTableNodes.size();

	    	while( secondTable < nTables ) {
	    		firstST = (DiagramModelNode)stagingTableNodes.get(firstTable);
	    		secondST = (DiagramModelNode)stagingTableNodes.get(secondTable);
	    		if( (firstST.getY() + firstST.getHeight()) > (secondST.getY() - 10) ) {
	    			secondST.setPosition( new Point(firstST.getX(), (firstST.getY() + firstST.getHeight() + 10) ));
	    		}
				// increment the counters
				firstTable++;
				secondTable++;
	    	}
    	}
    	
    	
    }
    /*
     * jhTODO Can't we replace all of this StagingTable logic with a simple 
     *         ordered list created in TreeMappingAdapter constructor, exactly
     *         as I am now creating "lstTreeOrderedMappingClassList" and passing
     *         it to this class? 
     */
    private void orderStagingTableNodes() {
    	if( stagingTableNodes != null || !stagingTableNodes.isEmpty() ) {
			// Create a sorted list
			List sortedStagingTables = new ArrayList();
			boolean extentsLeftOver = true;
			
			// Need to walk through all the extents to get the lowest one...
			
			while( extentsLeftOver) {
				List leftOverExtents = getLeftoverStagingTableExtentNodes(sortedStagingTables);
				if( !leftOverExtents.isEmpty() ) {
					DiagramModelNode lowestStagingTableNode = getStagingTableNodeForLowestExtentNode(leftOverExtents);
					if( lowestStagingTableNode != null ) {
						sortedStagingTables.add(lowestStagingTableNode);
					}
				} else {
					extentsLeftOver = false;
				}
			}
			stagingTableNodes = new ArrayList(sortedStagingTables);
		}

    }
    
	/*
	 * Returns a list of extent nodes that do not reference any of the
	 * mapping classes in the input list.
	 */
	private List getLeftoverStagingTableExtentNodes(List stagingTableNodes) {
		List allExtentNodes = getAllStagingTableExtentNodes();
		List leftOverExtentNodes = new ArrayList(allExtentNodes.size());
    	
		if( stagingTableNodes == null || stagingTableNodes.isEmpty() ) {
			leftOverExtentNodes.addAll(allExtentNodes);
		} else {
			Iterator iter = allExtentNodes.iterator();
			MappingExtentNode nextNode = null;
			DiagramModelNode nextStagingTableNode = null;
			EObject nextReference = null;
	    	
			while( iter.hasNext() ) {
				nextNode = (MappingExtentNode)iter.next();
				nextReference = nextNode.getExtent().getMappingReference();
				if( nextReference != null ) {
					nextStagingTableNode = getStagingTable(nextReference);
					if( nextStagingTableNode != null &&
						!stagingTableNodes.contains(nextStagingTableNode)) {
						leftOverExtentNodes.add(nextNode);
					}
				} else {
					leftOverExtentNodes.add(nextNode);
				}
			}
		}
		if( leftOverExtentNodes.isEmpty() )
			return Collections.EMPTY_LIST;
    		
		return leftOverExtentNodes;
	}
	
	private List getAllStagingTableExtentNodes() {
		if( stagingTableNodes != null && !stagingTableNodes.isEmpty() ) {
			List allExtentNodes = new ArrayList(stagingTableNodes.size());
			
			Iterator iter = stagingTableNodes.iterator();
			DiagramModelNode dmn = null;
			DiagramModelNode nextExtentNode = null;
			while( iter.hasNext() ) {
				dmn = (DiagramModelNode)iter.next();
				if( dmn != null ) {
					nextExtentNode = getExtentNodeForStagingTable(dmn);
					if( nextExtentNode != null )
						allExtentNodes.add(nextExtentNode);
				}
			}
			return allExtentNodes;
		}
		return Collections.EMPTY_LIST;

	}
    
    private DiagramModelNode getTransformationNode() {
        // walk children and look for TransformationNode type
        Iterator iter = this.diagramModelNode.getChildren().iterator();
        DiagramModelNode nextNode = null;
        while( iter.hasNext() ) {
            nextNode = (DiagramModelNode)iter.next();
            if( nextNode instanceof TransformationNode )
                return nextNode;
        }
        
        return null;
    }
    /*
     * jh Lyra enh: we really ought to revise this to work lazily, driven
     *              perhaps by a force flag that we would use at the very 
     *              start of a cycle.  As it is this method is called many
     *              times per cycle; all but the first is completely unnecessary.
     */
    private List getCurrentSourceNodes() {
        DiagramModelNode rootNode = getRootNode();
        
        List currentSourceNodes = new ArrayList();
        
        Iterator iter = diagramModelNode.getChildren().iterator();
        DiagramModelNode nextNode = null;
        while( iter.hasNext() ) {
            nextNode = (DiagramModelNode)iter.next();
            if(  (nextNode instanceof UmlClassifierNode || nextNode instanceof UmlPackageNode) && nextNode != rootNode )
                currentSourceNodes.add(nextNode);
        }
        
        return currentSourceNodes;
    }
    
    private DiagramModelNode getRootNode() {
        DiagramModelNode root = null;
        DiagramModelNode transformationModelNode = getTransformationNode();
        // Get it's target
        if( transformationModelNode != null ) {
            Diagram diagram = transformationModelNode.getDiagram();
            if( diagram != null ) {
                EObject targetObject = diagram.getTarget();
                if( targetObject != null ) {
                    root = DiagramUiUtilities.getDiagramModelNode(targetObject, transformationModelNode.getParent());
                }
            }
        }
        return root;
    }
    
    private DiagramModelNode getInputSetNode() {
        
        DiagramModelNode[] nodeArray = getNodeArray();
        int nNodes = nodeArray.length;
        
        for( int i=0; i<nNodes; i++) {
            DiagramModelNode next = nodeArray[i];
            if( isInputSet(next) ) {
                return next;
            }
        }

        return null;
    }
    
    private boolean isStagingTable(DiagramModelNode classNode) {
        if( classNode.getModelObject() != null && classNode.getModelObject() instanceof StagingTable )
            return true;
            
        return false;
    }
    
    private boolean isInputSet(DiagramModelNode classNode) {
        if( classNode != null && classNode.getModelObject() != null && classNode.getModelObject() instanceof InputSet )
            return true;
            
        return false;
    }
    
    private boolean isRootNode(DiagramModelNode classNode) {
    	if( classNode != null && classNode.getModelObject() != null && getRootNode().getModelObject() != null ) {
    		if( classNode.getModelObject() == getRootNode().getModelObject()) {
    			return true;
    		}
    	}
    	
    	return false;
    }
    
    private DiagramModelNode getExtentNodeForStagingTable(DiagramModelNode stagingTableNode) {
        Iterator iter = diagramModelNode.getChildren().iterator();
        DiagramModelNode nextNode = null;
        while( iter.hasNext() ) {
            nextNode = (DiagramModelNode)iter.next();
            if( nextNode instanceof MappingExtentNode ) {
                EObject mappingReference = ((MappingExtentNode)nextNode).getExtent().getMappingReference();
                if( mappingReference != null && mappingReference.equals(stagingTableNode.getModelObject()))
                    return nextNode;
            }
        }
        
        return null;
    }
    
    private void resetAllExtentNodes() {
//        System.out.println("[MappingDiagramLayout.resetAllExtents] TOP");
        // Need to find the zoom factor here...
        double zoomFactor = 1.0;
        if( ((MappingDiagramNode)diagramModelNode).getViewer() != null ) {
            DiagramViewer viewer = ((MappingDiagramNode)diagramModelNode).getViewer();
            ZoomManager zoomManager = ((ScalableFreeformRootEditPart)viewer.getRootEditPart()).getZoomManager();
            zoomFactor = zoomManager.getZoom();
        }

        
        Iterator iter = diagramModelNode.getChildren().iterator();
        DiagramModelNode nextNode = null;
        MappingExtent nextExtent = null;
        int nextY = 0;
        while( iter.hasNext() ) {
            nextNode = (DiagramModelNode)iter.next();
            if( nextNode instanceof MappingExtentNode ) {
//				((MappingExtentNode)nextNode).update(DiagramUiConstants.DiagramNodeProperties.SIZE);
                nextExtent = ((MappingExtentNode)nextNode).getExtent();
                nextY = (int)(nextExtent.getOffset()/zoomFactor);
                nextNode.setPosition(new Point(MappingExtentNode.X_ORIGIN, nextY));
//                System.out.println("[MappingDiagramLayout.resetAllExtents] Just set nextNode pos to: " + nextNode.getPosition() );
            }
        }
    }
    
    
	private DiagramModelNode getStagingTableNodeForLowestExtentNode(List extentNodes) {
		int lowestY = 99999;
		DiagramModelNode stagingTableNode = null;
		EObject lowestMappingClass = null;
		MappingExtentNode nextNode = null;
		Iterator iter = extentNodes.iterator();
		while( iter.hasNext() ) {
			nextNode = (MappingExtentNode)iter.next();
			if( nextNode.getY() < lowestY ) {
				lowestY = nextNode.getY();
				lowestMappingClass = nextNode.getExtent().getMappingReference();
			}
		}
		if( lowestMappingClass != null ) {
			stagingTableNode = getStagingTable(lowestMappingClass);
		}

    	
		return stagingTableNode;
	}
	
	private DiagramModelNode getStagingTable(EObject stagingTableEObject) {
		if( stagingTableNodes != null && !stagingTableNodes.isEmpty()) {
			Iterator iter = stagingTableNodes.iterator();
			DiagramModelNode nextNode = null;
			while( iter.hasNext() ) {
				nextNode = (DiagramModelNode)iter.next();
				if( nextNode.getModelObject().equals(stagingTableEObject)) {
					return nextNode;
				}
			}
		}
		return null;
	}
    
//    private List getAllExtentNodes() {
//    	List extentNodes = new ArrayList();
//		Iterator iter = diagramModelNode.getChildren().iterator();
//		DiagramModelNode nextNode = null;
//		while( iter.hasNext() ) {
//			nextNode = (DiagramModelNode)iter.next();
//			if( nextNode instanceof MappingExtentNode ) {
//				extentNodes.add(nextNode);
//			}
//		}
//		
//		if( extentNodes.isEmpty() ) {
// 			return Collections.EMPTY_LIST;
//		}
//		
//		return extentNodes;
//    }
    
    private int getStackHeight() {
		int stackHeight = 0;
		List sourceNodes = getCurrentSourceNodes();
    	
		if( sourceNodes != null && !sourceNodes.isEmpty()) {
			DiagramModelNode nextNode = null;
			Iterator iter = sourceNodes.iterator();
			while( iter.hasNext() ) {
				nextNode = (DiagramModelNode)iter.next();
				stackHeight += nextNode.getHeight() + 20;
			}
			stackHeight = stackHeight - 20;
		}
		if( stackHeight < 10 )
			stackHeight = 10;
			
		return stackHeight;
    }
    
    private int getLevel(DiagramModelNode node) {
        EObject eObj = node.getModelObject();

        Diagram theDiagram = (Diagram)diagramModelNode.getModelObject();

        DiagramEditor deEditor = DiagramEditorUtil.getDiagramEditor(theDiagram);
        if( deEditor != null ) {
            MappingDiagramController controller = (MappingDiagramController)deEditor.getDiagramController();
            if( controller != null ) {
                MappingAdapterFilter filter = controller.getMappingFilter();
                
                if( eObj instanceof StagingTable ) {
                    return filter.getLevel((StagingTable)eObj);
                }
                if( eObj instanceof MappingClass ) {
                    return filter.getLevel((MappingClass)eObj);
                }
            }
        }
        return 0;
    }
    
    private List getOrderedMappingClassList() {
        Diagram theDiagram = (Diagram)diagramModelNode.getModelObject();

        DiagramEditor deEditor = DiagramEditorUtil.getDiagramEditor(theDiagram);
        if( deEditor != null ) {
            MappingDiagramController controller = (MappingDiagramController)deEditor.getDiagramController();
            if( controller != null ) {
                MappingAdapterFilter filter = controller.getMappingFilter();

                return filter.getMappingAdapter().getAllMappingClasses();
            }
        }

        return Collections.EMPTY_LIST;

    }
    
    
    
}
