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
