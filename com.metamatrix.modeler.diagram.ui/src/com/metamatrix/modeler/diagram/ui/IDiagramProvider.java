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

package com.metamatrix.modeler.diagram.ui;

import com.metamatrix.metamodels.diagram.Diagram;

/**
 * IDiagramProvider
 * This class provides an interface which can be generically called by the modeler to see if any if a selected
 * generic Diagram type can be deleted. It is intended to be implemented by the diagram content providers...
 */
public interface IDiagramProvider {

    /**
     * This method provides a simple way for each diagram type to control whether or not
     * a diagram can be deleted.  Package, Mapping, Transformation should return false, while
     * Custom diagrams should return true;
     * @return canDelete
     */
    boolean canDelete(Diagram diagram);

}
