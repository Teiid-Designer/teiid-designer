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

package com.metamatrix.modeler.internal.core.workspace;

import org.eclipse.core.resources.IResource;

import com.metamatrix.modeler.core.workspace.ResourceFilter;

/** ResourceFilter that only accepts Resources that are models.
 *  Uses ModelUtil.isModelFile to determine this.
 * @author PForhan
 */
public class ModelResourceFilter implements ResourceFilter {

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.workspace.ResourceFilter#accept(org.eclipse.core.resources.IResource)
     */
    public boolean accept(IResource res) {
        return ModelUtil.isModelFile(res,true);
    }

}
