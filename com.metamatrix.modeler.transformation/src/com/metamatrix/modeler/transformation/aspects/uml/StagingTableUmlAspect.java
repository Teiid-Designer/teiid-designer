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

package com.metamatrix.modeler.transformation.aspects.uml;


/**
 * StagingTableUmlAspect
 */
public class StagingTableUmlAspect extends MappingClassUmlAspect {

    /**
     * Construct an instance of StagingTableUmlAspect.
     * 
     */
    public StagingTableUmlAspect() {
        super();
    }
    
    /**
     * @see com.metamatrix.modeler.transformation.aspects.uml.MappingClassUmlAspect#getStereotype(java.lang.Object)
     */
    @Override
    public String getStereotype(Object eObject) {
        return com.metamatrix.metamodels.transformation.TransformationPlugin.Util.getString("_UI_StagingTable_type"); //$NON-NLS-1$
    }


}
