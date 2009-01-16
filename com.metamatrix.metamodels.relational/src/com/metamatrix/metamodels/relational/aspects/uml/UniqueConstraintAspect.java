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

package com.metamatrix.metamodels.relational.aspects.uml;

import org.eclipse.emf.ecore.EObject;

import com.metamatrix.core.util.StringUtil;
import com.metamatrix.metamodels.relational.RelationalPlugin;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;

/**
 * KeyAspect
 */
public class UniqueConstraintAspect extends UniqueKeyAspect {
    /**
     * Construct an instance of UniqueConstraintAspect.
     * @param entity
     */
    public UniqueConstraintAspect(MetamodelEntity entity){
        super();
        setMetamodelEntity(entity);
    }

    
    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodels.aspects.UmlDiagramAspect#getStereotype(java.lang.Object)
     */
    public String getStereotype(Object eObject) {
        return RelationalPlugin.getPluginResourceLocator().getString("_UI_UniqueConstraint_type"); //$NON-NLS-1$
    }

    public int getEndCount(Object obj) {
        return 0;
    }

    public String getRoleName(Object assoc, int end) {
        return RelationalPlugin.getPluginResourceLocator().getString("_UI_UniqueConstraint_type"); //$NON-NLS-1$
    }
    
    public EObject getEnd(Object constraint, int end){
        return null;
    }

    public EObject getEndTarget(Object constraint, int end){
        return null;
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlRelationship#getName(java.lang.Object)
     */
    public String getName(Object eObject) {
        return StringUtil.Constants.EMPTY_STRING;
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlRelationship#getToolTip(java.lang.Object)
     */
    public String getToolTip(Object eObject) {
        final StringBuffer sb = new StringBuffer(200);
        sb.append(this.getStereotype(eObject));
        return sb.toString();
    }

}
