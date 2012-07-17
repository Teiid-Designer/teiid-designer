/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.metadata.runtime;

import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.teiid.designer.core.index.IndexConstants;
import org.teiid.designer.core.metamodel.aspect.MetamodelEntity;
import org.teiid.designer.core.metamodel.aspect.sql.SqlAnnotationAspect;


/**
 * FakeSqlModelAspect
 */
public class FakeSqlAnnotationAspect implements SqlAnnotationAspect {

    public String name, fullName, nameInSource, description;
    public IPath path;
    public Object uuid, parentUuid;
    public List keywords;
    public Map tags;

    @Override
	public String getDescription(EObject eObject) { return description; }

    @Override
	public List getKeywords(EObject eObject) { return keywords; }

    @Override
	public String getName(EObject eObject) { return name; }

    @Override
	public String getFullName(EObject eObject) { return fullName; }

    @Override
	public String getNameInSource(EObject eObject) { return nameInSource; }

    @Override
	public IPath getPath(EObject eObject) { return path; }

    @Override
	public Object getObjectID(EObject eObject) { return uuid; }

    @Override
	public Object getParentObjectID(EObject eObject) { return parentUuid; }

    @Override
	public boolean isRecordType(char recordType) { return (recordType == IndexConstants.RECORD_TYPE.ANNOTATION ); }

    @Override
	public boolean isQueryable(EObject eObject) { return true; }

    @Override
	public String getID() { return null; }

    @Override
	public MetamodelEntity getMetamodelEntity() { return null; }

    @Override
	public Map getTags(EObject eObject) { return tags; }

    /*
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlAspect#updateObject(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject)
     */
    @Override
	public void updateObject(EObject targetObject, EObject sourceObject) {

    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlAnnotationAspect#getURI(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
	public URI getURI(EObject eObject) {
        return null;
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlAnnotationAspect#getEClass(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
	public URI getMetaclassURI(EObject eObject) {
        return null;
    }

}
