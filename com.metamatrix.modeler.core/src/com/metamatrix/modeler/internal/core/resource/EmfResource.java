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

package com.metamatrix.modeler.internal.core.resource;

import com.metamatrix.modeler.core.container.Container;
import com.metamatrix.modeler.core.util.ModelContents;

/**
 * MTK implementation of a Resource
 */
public interface EmfResource extends MMXmiResource {
       
    /**
     * Returns the {@link Container} instance associated with
     * this resource set.
     * @return Container
     */
    Container getContainer();
    
    /**
     * Obtain the helper for the model contents.
     * @return the content helper; may be null if this resource is not loaded
     */
    ModelContents getModelContents();

 
}
