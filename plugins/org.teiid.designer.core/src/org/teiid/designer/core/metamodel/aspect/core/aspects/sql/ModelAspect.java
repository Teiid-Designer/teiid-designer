/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.metamodel.aspect.core.aspects.sql;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.index.IndexConstants;
import org.teiid.designer.core.metamodel.aspect.AbstractMetamodelAspect;
import org.teiid.designer.core.metamodel.aspect.MetamodelEntity;
import org.teiid.designer.core.metamodel.aspect.sql.SqlAnnotationAspect;
import org.teiid.designer.core.metamodel.aspect.sql.SqlModelAspect;
import org.teiid.designer.metamodels.core.ModelAnnotation;
import org.teiid.designer.metamodels.core.ModelType;


/**
 * ModelAspect
 *
 * @since 8.0
 */
public class ModelAspect extends AbstractMetamodelAspect implements SqlModelAspect, SqlAnnotationAspect {

    public static final String ASPECT_ID = ModelerCore.EXTENSION_POINT.SQL_ASPECT.ID;    

    public ModelAspect(MetamodelEntity entity) {
        super.setMetamodelEntity(entity);
        super.setID(ASPECT_ID);
    }

    private ModelAnnotation getModelAnnotation(EObject eObject) {
        CoreArgCheck.isInstanceOf(ModelAnnotation.class, eObject);
        ModelAnnotation modelAnn = (ModelAnnotation) eObject;
        return modelAnn;
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlModelAspect#getPrimaryMetamodelURI(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public String getPrimaryMetamodelUri(EObject eObject) {
        final ModelAnnotation modelAnnotation = getModelAnnotation(eObject);
        return modelAnnotation.getPrimaryMetamodelUri();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlModelAspect#getModelType(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public int getModelType(EObject eObject) {
        final ModelAnnotation modelAnnotation = getModelAnnotation(eObject);
        ModelType type = modelAnnotation.getModelType();
        return (type != null ? type.getValue() : ModelType.UNKNOWN);
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlModelAspect#supportsOrderBy(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public boolean supportsOrderBy(EObject eObject) {
        final ModelAnnotation modelAnnotation = getModelAnnotation(eObject);
        return modelAnnotation.isSupportsOrderBy();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlModelAspect#supportsOuterJoin(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public boolean supportsOuterJoin(EObject eObject) {
        final ModelAnnotation modelAnnotation = getModelAnnotation(eObject);
        return modelAnnotation.isSupportsOuterJoin();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlModelAspect#supportsWhereAll(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public boolean supportsWhereAll(EObject eObject) {
        final ModelAnnotation modelAnnotation = getModelAnnotation(eObject);
        return modelAnnotation.isSupportsWhereAll();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlModelAspect#supportsDistinct(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public boolean supportsDistinct(EObject eObject) {
        final ModelAnnotation modelAnnotation = getModelAnnotation(eObject);
        return modelAnnotation.isSupportsDistinct();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlModelAspect#supportsJoin(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public boolean supportsJoin(EObject eObject) {
        final ModelAnnotation modelAnnotation = getModelAnnotation(eObject);
        return modelAnnotation.isSupportsJoin();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlModelAspect#isVisible(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public boolean isVisible(EObject eObject) {
        final ModelAnnotation modelAnnotation = getModelAnnotation(eObject);
        return modelAnnotation.isVisible();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlModelAspect#getMaxSetSize(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public int getMaxSetSize(EObject eObject) {
        final ModelAnnotation modelAnnotation = getModelAnnotation(eObject);
        return modelAnnotation.getMaxSetSize();
    }
    
    /* (non-Javadoc)
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlAspect#isRecordType(char)
     */
    @Override
	public boolean isRecordType(char recordType) {
        return (recordType == IndexConstants.RECORD_TYPE.MODEL || recordType == IndexConstants.RECORD_TYPE.ANNOTATION);
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlAspect#isQueryable(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public boolean isQueryable(final EObject eObject) {
		int type = getModelType(eObject);
        return (type == ModelType.PHYSICAL || type == ModelType.VIRTUAL || type == ModelType.MATERIALIZATION);
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlAspect#getName(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public String getName(EObject eObject) {
        return ModelerCore.getModelEditor().getModelName(eObject);
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlAspect#getNameInSource(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public String getNameInSource(EObject eObject) {
        final ModelAnnotation modelAnnotation = getModelAnnotation(eObject);
        return modelAnnotation.getNameInSource();
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlAnnotationAspect#getDescription(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public String getDescription(EObject eObject) {
        final ModelAnnotation modelAnnotation = getModelAnnotation(eObject);
        return modelAnnotation.getDescription();
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlAnnotationAspect#getKeywords(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public List getKeywords(EObject eObject) {
        return Collections.EMPTY_LIST;
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.AbstractMetamodelAspect#getFullName(org.eclipse.emf.ecore.EObject)
     */
    @Override
    public String getFullName(EObject eObject) {
        // The fullname returned from the aspect path will be of the 
        // form Model/ModelAnnotation but only want the first segment 
        // for the model path so instead just call getName(EObject).
        return this.getName(eObject);
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.AbstractMetamodelAspect#getPath(org.eclipse.emf.ecore.EObject)
     */
    @Override
    public IPath getPath(EObject eObject) {
        // The path returned from the aspect will be of the form
        // Model/ModelAnnotation but only want the first segment for
        // the model path so instead build the path with just the name.
        String name = this.getName(eObject);
        return new Path(name);
    }

    /*
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlAnnotationAspect#getTags(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public Map getTags(EObject eObject) {
        final ModelAnnotation annotation = getModelAnnotation(eObject);
        final EMap tags = annotation.getTags();
        if ( tags != null && tags.size() != 0 ) {
            return tags.map();
        }
        return Collections.EMPTY_MAP;
    }

    /*
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlAspect#updateObject(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject)
     */
    @Override
	public void updateObject(EObject targetObject, EObject sourceObject) {

    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlAspect#getParentObjectID(org.eclipse.emf.ecore.EObject)
     */
    @Override
    public Object getParentObjectID(EObject eObject) {
        return null;
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlAnnotationAspect#getURI(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
	public URI getURI(EObject eObject) {
        return ModelerCore.getModelEditor().getUri(eObject);
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlAnnotationAspect#getMetaclassURI(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
	public URI getMetaclassURI(EObject eObject) {
        return ModelerCore.getModelEditor().getUri(eObject.eClass());
    }
}
