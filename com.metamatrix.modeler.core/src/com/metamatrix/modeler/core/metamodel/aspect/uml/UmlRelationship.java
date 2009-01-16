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

package com.metamatrix.modeler.core.metamodel.aspect.uml;

/**
 * UmlRelationship
 */
public interface UmlRelationship extends UmlDiagramAspect {
    
    /**
     * Return the name of the relationship if it exists otherwise
     * and empty string will be returned.
     * @param eObject
     * @return
     */
    String getName(Object eObject);
    
    /**
     * Return the tooltip string displayed in the diagram for
     * this relationship.
     * @param eObject
     * @return
     */
    String getToolTip(Object eObject);

}
