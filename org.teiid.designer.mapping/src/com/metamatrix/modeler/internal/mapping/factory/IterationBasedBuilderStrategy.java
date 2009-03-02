/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.mapping.factory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;

import com.metamatrix.modeler.mapping.DebugConstants;
import com.metamatrix.modeler.mapping.PluginConstants;
import com.metamatrix.modeler.mapping.factory.IMappableTree;
import com.metamatrix.modeler.mapping.factory.ITreeToRelationalMapper;
import com.metamatrix.modeler.mapping.factory.MappingClassBuilderStrategy;


/** 
 * @since 5.0.1
 */
public class IterationBasedBuilderStrategy implements MappingClassBuilderStrategy {

    protected ITreeToRelationalMapper mapper;
    protected IMappableTree tree;
    
    /** 
     * @see com.metamatrix.modeler.mapping.factory.MappingClassBuilderStrategy#buildMappingClassMap(org.eclipse.emf.ecore.EObject)
     * @since 4.3
     */
    public Map buildMappingClassMap(EObject theTopNode, IMappableTree tree, ITreeToRelationalMapper mapper) {
        this.mapper = mapper;
        this.tree = tree;
        Map result = recursiveBuild(theTopNode, null, new HashMap());
        
        // null out the instance variables so this object can be held
        this.mapper = null;
        this.tree = null;
        
        return result;
    }


    protected Map recursiveBuild(EObject node, 
                               EObject currentMappingClassStorageNode, 
                               Map classAttributeMap) {

        boolean printDebug = PluginConstants.Util.isDebugEnabled(DebugConstants.MAPPING_CLASS_FACTORY);

        
        // see if the node we are on right now is mappable
        if ( mapper.isMappable(node) ) {
            // if so, then we need to put this node in the attribute list for the currentMappingClassStorageNode
            addAttributeToMap(node, currentMappingClassStorageNode, classAttributeMap);
            if( printDebug ) {
                String message = " buildMappingClassMap():  added Node to Map = " + node; //$NON-NLS-1$
                PluginConstants.Util.print(DebugConstants.MAPPING_CLASS_FACTORY, message);
            }
        }    
        
        // now look at the children of this node and see if any have maxOccurs > 1
        for ( Iterator iter = tree.getChildren(node).iterator() ; iter.hasNext() ; ) {
            EObject child = (EObject) iter.next();
            if ( mapper.isMappable(child) && ! mapper.allowsMappingClass(child) ) {
                // found a mappable attribute, so add it to the attribute list for the currentMappingClassStorageNode
                currentMappingClassStorageNode = addAttributeToMap(child, currentMappingClassStorageNode, classAttributeMap);
                if( printDebug ) {
                    String message = " buildMappingClassMap():  added Node to Map = " + child; //$NON-NLS-1$
                    PluginConstants.Util.print(DebugConstants.MAPPING_CLASS_FACTORY, message);
                }
                
            } else {
                // put a new mapping class here if this node's maxOccurs > 1 or if it's recursive
                if ( shouldContainMappingClass(child) ) {
                    // then this node needs to store a MappingClass
                    classAttributeMap.put(child, new ArrayList());
                    recursiveBuild(child, child, classAttributeMap);
                    if( printDebug ) {
                        String message = " buildMappingClassMap():  added Node to Map = " + child; //$NON-NLS-1$
                        PluginConstants.Util.print(DebugConstants.MAPPING_CLASS_FACTORY, message);
                    }
                } else {
                    // recurse down through the children of this node
                    recursiveBuild(child, currentMappingClassStorageNode, classAttributeMap);
                }
            }
        }    
                   
        return classAttributeMap;
    }

    
    protected boolean shouldContainMappingClass(EObject node) {
        boolean result = false;
        try {
            if ( mapper.allowsMappingClass(node) ) {
                if ( mapper.canIterate(node) ) {
                    result = true;
                } else if ( mapper.isRecursive(node)) {
                    result = true;
// Defect 18035: I have to back out this change because we require mapping classes at choice nodes.
//                } else if ( tree.isChoiceNode(node)) {
//                    // must put a mapping class on virtual choice nodes
//                    result = ModelType.VIRTUAL_LITERAL.equals( ModelUtil.getModel(node).getModelAnnotation().getModelType() );
//                } else {
//                    // see if the node is directly under a choice
//                    EObject parent = tree.getParent(node);
//                    result = tree.isChoiceNode(parent);
                }
            }
        } catch (Exception e) {
            PluginConstants.Util.log(e);
        }
        
        return result;        
    }
    
    /**
     * used by buildMappingClassMap
     */
    protected EObject addAttributeToMap(EObject node, 
                                      EObject currentMappingClassStorageNode, 
                                      Map classAttributeMap) {
                        
                                                
        Collection attributeNodes = null;
        if ( currentMappingClassStorageNode == null ) {

            // there is not yet a storage node for this attribute, so use this one
            EObject testNode = node;
            while ( currentMappingClassStorageNode == null ) {
                
                if ( shouldContainMappingClass(testNode) ) {
                    currentMappingClassStorageNode = testNode;
                } else if ( mapper.isTreeRoot(testNode) ) {    
                    currentMappingClassStorageNode = testNode;
                } else {
                    // next look at the parent of this node
                    EObject parent = tree.getParent(testNode);
                    if ( parent != null && parent == tree.getTreeRoot() ) {
                        // we're at the top of the tree, so use this node
                        currentMappingClassStorageNode = testNode;
                    } else {
                        testNode = parent;
                    }
                }
            }
        }
         
        if ( ! classAttributeMap.keySet().contains(currentMappingClassStorageNode) ) {
            attributeNodes = new ArrayList();
            classAttributeMap.put(currentMappingClassStorageNode, attributeNodes);
        } else {
            // get the attribute list for this storage node
            attributeNodes = (Collection) classAttributeMap.get(currentMappingClassStorageNode);
        }
        
        // add this mappable document node to the attribute list for the currentMappingClassStorageNode
        attributeNodes.add(node);
        
        return currentMappingClassStorageNode;
    } 
    

}
