/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.transformation.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import com.metamatrix.metamodels.transformation.DataFlowLink;
import com.metamatrix.metamodels.transformation.DataFlowMappingRoot;
import com.metamatrix.metamodels.transformation.DataFlowNode;
import com.metamatrix.metamodels.transformation.JoinNode;
import com.metamatrix.metamodels.transformation.OperationNode;
import com.metamatrix.metamodels.transformation.OperationNodeGroup;
import com.metamatrix.metamodels.transformation.ProjectionNode;
import com.metamatrix.metamodels.transformation.SourceNode;
import com.metamatrix.metamodels.transformation.TargetNode;
import com.metamatrix.metamodels.transformation.TransformationFactory;
import com.metamatrix.metamodels.transformation.TransformationPackage;


/** 
 * @since 4.3
 */
public class TestDataFlowMethods extends TestCase {
    // -------------------------------------------------
    // Variables initialized during one-time startup ...
    // -------------------------------------------------
    
    // ---------------------------------------
    // Variables initialized for each test ...
    // ---------------------------------------
    
    // =========================================================================
    //                        F R A M E W O R K
    // =========================================================================
    
    /**
     * Constructor for TestMetadataLoadingCache.
     * @param name
     */
    public TestDataFlowMethods(String name) {
        super(name);
    }
    
    // =========================================================================
    //                        T E S T   C O N T R O L
    // =========================================================================
    
    /** 
     * Construct the test suite, which uses a one-time setup call
     * and a one-time tear-down call.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite("TestDataFlowMethods"); //$NON-NLS-1$
        suite.addTestSuite(TestDataFlowMethods.class);
    
        return new TestSetup(suite) { // junit.extensions package
    
            // One-time setup and teardown
            @Override
            public void setUp() throws Exception {
                oneTimeSetUp();
            }
    
            @Override
            public void tearDown() {
                oneTimeTearDown();
            }
        };
    }
    
    // =========================================================================
    //                                 M A I N
    // =========================================================================
    
    public static void main(String args[]) {
        junit.textui.TestRunner.run(suite());
        System.exit(0);
    }
    
    // =========================================================================
    //                 S E T   U P   A N D   T E A R   D O W N
    // =========================================================================
    
    /**
     * @see TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
    }
    
    /**
     * @see TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
    }
    
    public static void oneTimeSetUp() {
        TransformationPackageImpl.init();
    }
    
    public static void oneTimeTearDown() {
    }
    
    // =========================================================================
    //                      H E L P E R   M E T H O D S
    // =========================================================================
    
    public DataFlowMappingRoot helpCreateEmptyFlow() {
        return TransformationFactory.eINSTANCE.createDataFlowMappingRoot();
    }
    
    public DataFlowMappingRoot helpCreateFlowWithTwoSourceandTargets() {
        DataFlowMappingRoot root = helpCreateEmptyFlow() ;
        SourceNode source1 = TransformationFactory.eINSTANCE.createSourceNode();
        SourceNode source2 = TransformationFactory.eINSTANCE.createSourceNode();
        TargetNode target  = TransformationFactory.eINSTANCE.createTargetNode();
        root.getNodes().add(source1);
        root.getNodes().add(source2);
        root.getNodes().add(target);
        return root;
    }
    
    public DataFlowMappingRoot helpCreateFlowWithJoin() {
        DataFlowMappingRoot root = helpCreateEmptyFlow() ;
        SourceNode source1 = TransformationFactory.eINSTANCE.createSourceNode();
        SourceNode source2 = TransformationFactory.eINSTANCE.createSourceNode();
        TargetNode target  = TransformationFactory.eINSTANCE.createTargetNode();
        JoinNode join      = TransformationFactory.eINSTANCE.createJoinNode();
        DataFlowLink linkA = TransformationFactory.eINSTANCE.createDataFlowLink();
        DataFlowLink linkB = TransformationFactory.eINSTANCE.createDataFlowLink();
        DataFlowLink linkC = TransformationFactory.eINSTANCE.createDataFlowLink();
        linkA.setInputNode(source1);
        linkA.setOutputNode(join);
        linkB.setInputNode(source2);
        linkB.setOutputNode(join);
        linkC.setInputNode(join);
        linkC.setOutputNode(target);
        root.getNodes().add(source1);
        root.getNodes().add(source2);
        root.getNodes().add(join);
        root.getNodes().add(target);
        return root;
    }
    
    public DataFlowMappingRoot helpCreateFlowWithJoinAndProjection() {
        DataFlowMappingRoot root = helpCreateEmptyFlow() ;
        SourceNode source1  = TransformationFactory.eINSTANCE.createSourceNode();
        SourceNode source2  = TransformationFactory.eINSTANCE.createSourceNode();
        TargetNode target   = TransformationFactory.eINSTANCE.createTargetNode();
        JoinNode join       = TransformationFactory.eINSTANCE.createJoinNode();
        ProjectionNode proj = TransformationFactory.eINSTANCE.createProjectionNode();
        DataFlowLink linkA  = TransformationFactory.eINSTANCE.createDataFlowLink();
        DataFlowLink linkB  = TransformationFactory.eINSTANCE.createDataFlowLink();
        DataFlowLink linkC  = TransformationFactory.eINSTANCE.createDataFlowLink();
        DataFlowLink linkD  = TransformationFactory.eINSTANCE.createDataFlowLink();
        linkA.setInputNode(source1);
        linkA.setOutputNode(join);
        linkB.setInputNode(source2);
        linkB.setOutputNode(join);
        linkC.setInputNode(join);
        linkC.setOutputNode(proj);
        linkD.setInputNode(proj);
        linkD.setOutputNode(target);
        root.getNodes().add(source1);
        root.getNodes().add(source2);
        root.getNodes().add(join);
        root.getNodes().add(proj);
        root.getNodes().add(target);
        return root;
    }
    
    public DataFlowMappingRoot helpCreateFlowWithNodeGroups() {
        DataFlowMappingRoot root = helpCreateFlowWithJoinAndProjection() ;
        
        List joinNodes = new ArrayList();
        addNodeTypeToList(root.getNodes(), TransformationPackage.JOIN_NODE, true, joinNodes);
        JoinNode join = (JoinNode)joinNodes.get(0);
        
        List projNodes = new ArrayList();
        addNodeTypeToList(root.getNodes(), TransformationPackage.PROJECTION_NODE, true, projNodes);
        ProjectionNode proj = (ProjectionNode)projNodes.get(0);
        
        OperationNodeGroup group1  = TransformationFactory.eINSTANCE.createOperationNodeGroup();
        group1.getContents().add(join);
        group1.getContents().add(proj);
        
        OperationNodeGroup group2  = TransformationFactory.eINSTANCE.createOperationNodeGroup();
        group2.getContents().add(group1);
        
        root.getNodes().add(group2);

        return root;
    }
    
    protected void addNodeTypeToList(final List nodes, final int theClassifierID, final boolean recurse, final List result) {
        for (final Iterator iter = nodes.iterator(); iter.hasNext();) {
            final DataFlowNode node = (DataFlowNode)iter.next();
            if (node != null) {
                final int classifierID = node.eClass().getClassifierID();
                if (classifierID == theClassifierID) {
                    result.add(node);
                }
                if (classifierID == TransformationPackage.OPERATION_NODE_GROUP && recurse) {
                    OperationNodeGroup operationNodeGroup = (OperationNodeGroup)node;
                    addNodeTypeToList(operationNodeGroup.getContents(), theClassifierID, recurse, result);
                }
            } 
        } // for
    }
    
    // =========================================================================
    //                         T E S T   C A S E S
    // =========================================================================
    
    public void testGetSourceNodesForEmptyFlow() {
        DataFlowMappingRoot root = helpCreateEmptyFlow();
        assertNotNull(root.getSourceNodes());
        assertEquals(0,root.getSourceNodes().size());
    }
    
    public void testGetTargetNodesForEmptyFlow() {
        DataFlowMappingRoot root = helpCreateEmptyFlow();
        assertNull(root.getTarget());
        assertNotNull(root.getTargetNodes());
        assertEquals(0,root.getTargetNodes().size());
    }
    
    public void testGetSourceNodes() {
        DataFlowMappingRoot root = helpCreateFlowWithTwoSourceandTargets();
        assertNotNull(root.getSourceNodes());
        assertEquals(2,root.getSourceNodes().size());
    }
    
    public void testGetTargetNodes() {
        DataFlowMappingRoot root = helpCreateFlowWithTwoSourceandTargets();
        assertNull(root.getTarget());
        assertNotNull(root.getTargetNodes());
        assertEquals(1,root.getTargetNodes().size());
    }
    
    public void testGetSourceNodes2() {
        DataFlowMappingRoot root = helpCreateFlowWithJoin();
        assertNotNull(root.getSourceNodes());
        assertEquals(2,root.getSourceNodes().size());
    }
    
    public void testGetTargetNodes2() {
        DataFlowMappingRoot root = helpCreateFlowWithJoin();
        assertNull(root.getTarget());
        assertNotNull(root.getTargetNodes());
        assertEquals(1,root.getTargetNodes().size());
    }
    
    public void testGetInputNodes() {
        DataFlowMappingRoot root = helpCreateFlowWithJoin();
        
        List joinNodes = new ArrayList();
        addNodeTypeToList(root.getNodes(), TransformationPackage.JOIN_NODE, true, joinNodes);
        assertEquals(1,joinNodes.size());
        JoinNode join = (JoinNode)joinNodes.get(0);

        assertEquals(2,join.getInputNodes().size());
        assertEquals(2,join.getInputLinks().size());
        assertEquals(0,join.getMinInputs());
        assertEquals(2,join.getMaxInputs());
        assertEquals(0,join.getMinOutputs());
        assertEquals(1,join.getMaxOutputs());
    }
    
    public void testGetOutputNodes() {
        DataFlowMappingRoot root = helpCreateFlowWithJoin();
        
        List joinNodes = new ArrayList();
        addNodeTypeToList(root.getNodes(), TransformationPackage.JOIN_NODE, true, joinNodes);
        assertEquals(1,joinNodes.size());
        JoinNode join = (JoinNode)joinNodes.get(0);

        assertEquals(1,join.getOutputNodes().size());
        assertEquals(1,join.getOutputLinks().size());
    }
    
    public void testGetInputNodes2() {
        DataFlowMappingRoot root = helpCreateFlowWithJoinAndProjection();
        
        List projNodes = new ArrayList();
        addNodeTypeToList(root.getNodes(), TransformationPackage.PROJECTION_NODE, true, projNodes);
        assertEquals(1,projNodes.size());
        ProjectionNode proj = (ProjectionNode)projNodes.get(0);

        assertEquals(1,proj.getInputNodes().size());
        assertEquals(1,proj.getInputLinks().size());
        assertEquals(0,proj.getMinInputs());
        assertEquals(1,proj.getMaxInputs());
        assertEquals(0,proj.getMinOutputs());
        assertEquals(1,proj.getMaxOutputs());
    }
    
    public void testGetOutputNodes2() {
        DataFlowMappingRoot root = helpCreateFlowWithJoinAndProjection();
        
        List projNodes = new ArrayList();
        addNodeTypeToList(root.getNodes(), TransformationPackage.PROJECTION_NODE, true, projNodes);
        assertEquals(1,projNodes.size());
        ProjectionNode proj = (ProjectionNode)projNodes.get(0);

        assertEquals(1,proj.getOutputNodes().size());
        assertEquals(1,proj.getOutputLinks().size());
    }
    
    public void testGetAllContents() {
        DataFlowMappingRoot root = helpCreateFlowWithNodeGroups();
        
        List opGroups = new ArrayList();
        addNodeTypeToList(root.getNodes(), TransformationPackage.OPERATION_NODE_GROUP, false, opGroups);
        assertEquals(1,opGroups.size());
        
        OperationNodeGroup group2 = (OperationNodeGroup)opGroups.get(0);
        assertEquals(2,group2.getAllContents().size());
        assertEquals(1,group2.getContents().size());
        assertTrue(group2.getContents().get(0) instanceof OperationNodeGroup);
        
        OperationNodeGroup group1 = (OperationNodeGroup)group2.getContents().get(0);
        assertEquals(2,group1.getAllContents().size());
        assertEquals(2,group1.getContents().size());
        assertTrue(group1.getContents().get(0) instanceof OperationNode);
        assertTrue(group1.getContents().get(1) instanceof OperationNode);
    }
}
