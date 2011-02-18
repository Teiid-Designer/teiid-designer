/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.aspects.sql;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import com.metamatrix.core.util.CoreArgCheck;
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
        CoreArgCheck.isInstanceOf(MappingClassObject.class, eObject); 
        MappingClassObject entity = (MappingClassObject) eObject;       
        return entity.getName();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#getNameInSource(org.eclipse.emf.ecore.EObject)
     */
    public String getNameInSource(EObject eObject) {
        CoreArgCheck.isInstanceOf(MappingClassObject.class, eObject); 
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
