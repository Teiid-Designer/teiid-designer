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

package com.metamatrix.modeler.transformation.aspects.sql;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.metamodels.transformation.MappingClassObject;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;

/**
 * MappingClassObjectSqlAspect
 */
public abstract class MappingClassObjectSqlAspect extends AbstractTransformationSqlAspect {

    protected MappingClassObjectSqlAspect(MetamodelEntity entity) {
        super(entity);
    }

    /**
     * Get the value of the given feature by looking up the EClass of
     * the EObejct.
     * @param featureID The ID of the feature to lookup.
     * @param The EObejct whose feature is lookedup.
     * @return The value of the feature
     */
    protected Object getFeatureValue(EObject eObject, int featureID) {
       EClass eClass = eObject.eClass();
       EStructuralFeature feature = eClass.getEStructuralFeature(featureID);
       return eObject.eGet(feature);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#getName(org.eclipse.emf.ecore.EObject)
     */
    public String getName(EObject eObject) {
        ArgCheck.isInstanceOf(MappingClassObject.class, eObject); 
        MappingClassObject entity = (MappingClassObject) eObject;       
        return entity.getName();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#getNameInSource(org.eclipse.emf.ecore.EObject)
     */
    public String getNameInSource(EObject eObject) {
        ArgCheck.isInstanceOf(MappingClassObject.class, eObject); 
        MappingClassObject entity = (MappingClassObject) eObject;       
        return entity.getName();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#getIndexRecord(java.lang.Object)
     */
    public Object getIndexRecord(Object eObject) {
        return null;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#getObjectID(org.eclipse.emf.ecore.EObject)
     */
    @Override
    public Object getObjectID(EObject eObject) {
        return super.getObjectID(eObject);
    }
    
}
