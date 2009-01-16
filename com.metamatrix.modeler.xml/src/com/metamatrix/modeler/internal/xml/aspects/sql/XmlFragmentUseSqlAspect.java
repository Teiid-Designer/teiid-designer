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

package com.metamatrix.modeler.internal.xml.aspects.sql;

import org.eclipse.emf.ecore.EObject;

import com.metamatrix.metamodels.xml.XmlDocumentPlugin;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;

/**
 * XmlSequenceSqlAspect
 */
public class XmlFragmentUseSqlAspect extends XmlContainerNodeSqlAspect {

    /**
     * Construct an instance of XmlSequenceSqlAspect.
     * 
     */
    public XmlFragmentUseSqlAspect(final MetamodelEntity entity) {
        super(entity);
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#getName(org.eclipse.emf.ecore.EObject)
     */
    public String getName(final EObject eObject) {
        return XmlDocumentPlugin.getPluginResourceLocator().getString("_UI_XmlAll_type"); //$NON-NLS-1$
    }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#updateObject(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject)
     */
    public void updateObject(EObject targetObject, EObject sourceObject) {

    }

}
