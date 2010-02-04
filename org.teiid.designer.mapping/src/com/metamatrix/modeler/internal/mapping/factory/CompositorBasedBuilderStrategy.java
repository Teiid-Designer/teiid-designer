/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.mapping.factory;

import java.util.Collection;
import java.util.Iterator;
import org.eclipse.emf.ecore.EObject;


/** 
 * This Strategy builds a mapping class wherever an element is a global complex type.
 * @since 5.0.1
 */
public class CompositorBasedBuilderStrategy extends IterationBasedBuilderStrategy {

    @Override
    protected boolean shouldContainMappingClass(EObject node) {

        boolean result = false;

        if ( mapper.allowsMappingClass(node) ) {
            // see if the structure is similar to that of a complex type
            Collection children = tree.getChildren(node);
            for ( Iterator iter = children.iterator() ; iter.hasNext() ; ) {
                EObject child = (EObject) iter.next();
                if ( mapper.isContainerNode(child) ) {
                    result = true;
                    break;
                }
            }

            if ( ! result ) {
                // see if the node is directly under a choice
                EObject parent = tree.getParent(node);
                result = tree.isChoiceNode(parent);
            }
        }
        
        return result;        
    }
    
}
