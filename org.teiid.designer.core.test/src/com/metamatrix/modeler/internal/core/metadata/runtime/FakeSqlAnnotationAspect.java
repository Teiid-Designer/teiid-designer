/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.metadata.runtime;

import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import com.metamatrix.modeler.core.index.IndexConstants;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAnnotationAspect;

/**
 * FakeSqlModelAspect
 */
public class FakeSqlAnnotationAspect implements SqlAnnotationAspect {

    public String name, fullName, nameInSource, description;
    public IPath path;
    public Object uuid, parentUuid;
    public List keywords;
    public Map tags;

    public String getDescription(EObject eObject) { return description; }

    public List getKeywords(EObject eObject) { return keywords; }

    public String getName(EObject eObject) { return name; }

    public String getFullName(EObject eObject) { return fullName; }

    public String getNameInSource(EObject eObject) { return nameInSource; }

    public IPath getPath(EObject eObject) { return path; }

    public Object getObjectID(EObject eObject) { return uuid; }

    public Object getParentObjectID(EObject eObject) { return parentUuid; }

    public boolean isRecordType(char recordType) { return (recordType == IndexConstants.RECORD_TYPE.ANNOTATION ); }

    public boolean isQueryable(EObject eObject) { return true; }

    public String getID() { return null; }

    public MetamodelEntity getMetamodelEntity() { return null; }

    public Map getTags(EObject eObject) { return tags; }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#updateObject(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject)
     */
    public void updateObject(EObject targetObject, EObject sourceObject) {

    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAnnotationAspect#getURI(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    public URI getURI(EObject eObject) {
        return null;
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAnnotationAspect#getEClass(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    public URI getMetaclassURI(EObject eObject) {
        return null;
    }

}
