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

package com.metamatrix.modeler.core.metamodel.core.aspects.sql;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;

import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.index.IndexConstants;
import com.metamatrix.modeler.core.metamodel.aspect.AbstractMetamodelAspect;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAnnotationAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlModelAspect;

/**
 * ModelAspect
 */
public class ModelAspect extends AbstractMetamodelAspect implements SqlModelAspect, SqlAnnotationAspect {

    public static final String ASPECT_ID = ModelerCore.EXTENSION_POINT.SQL_ASPECT.ID;    

    public ModelAspect(MetamodelEntity entity) {
        super.setMetamodelEntity(entity);
        super.setID(ASPECT_ID);
    }

    private ModelAnnotation getModelAnnotation(EObject eObject) {
        ArgCheck.isInstanceOf(ModelAnnotation.class, eObject);
        ModelAnnotation modelAnn = (ModelAnnotation) eObject;
        return modelAnn;
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlModelAspect#getPrimaryMetamodelURI(org.eclipse.emf.ecore.EObject)
     */
    public String getPrimaryMetamodelUri(EObject eObject) {
        final ModelAnnotation modelAnnotation = getModelAnnotation(eObject);
        return modelAnnotation.getPrimaryMetamodelUri();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlModelAspect#getModelType(org.eclipse.emf.ecore.EObject)
     */
    public int getModelType(EObject eObject) {
        final ModelAnnotation modelAnnotation = getModelAnnotation(eObject);
        ModelType type = modelAnnotation.getModelType();
        return (type != null ? type.getValue() : ModelType.UNKNOWN);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlModelAspect#supportsOrderBy(org.eclipse.emf.ecore.EObject)
     */
    public boolean supportsOrderBy(EObject eObject) {
        final ModelAnnotation modelAnnotation = getModelAnnotation(eObject);
        return modelAnnotation.isSupportsOrderBy();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlModelAspect#supportsOuterJoin(org.eclipse.emf.ecore.EObject)
     */
    public boolean supportsOuterJoin(EObject eObject) {
        final ModelAnnotation modelAnnotation = getModelAnnotation(eObject);
        return modelAnnotation.isSupportsOuterJoin();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlModelAspect#supportsWhereAll(org.eclipse.emf.ecore.EObject)
     */
    public boolean supportsWhereAll(EObject eObject) {
        final ModelAnnotation modelAnnotation = getModelAnnotation(eObject);
        return modelAnnotation.isSupportsWhereAll();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlModelAspect#supportsDistinct(org.eclipse.emf.ecore.EObject)
     */
    public boolean supportsDistinct(EObject eObject) {
        final ModelAnnotation modelAnnotation = getModelAnnotation(eObject);
        return modelAnnotation.isSupportsDistinct();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlModelAspect#supportsJoin(org.eclipse.emf.ecore.EObject)
     */
    public boolean supportsJoin(EObject eObject) {
        final ModelAnnotation modelAnnotation = getModelAnnotation(eObject);
        return modelAnnotation.isSupportsJoin();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlModelAspect#isVisible(org.eclipse.emf.ecore.EObject)
     */
    public boolean isVisible(EObject eObject) {
        final ModelAnnotation modelAnnotation = getModelAnnotation(eObject);
        return modelAnnotation.isVisible();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlModelAspect#getMaxSetSize(org.eclipse.emf.ecore.EObject)
     */
    public int getMaxSetSize(EObject eObject) {
        final ModelAnnotation modelAnnotation = getModelAnnotation(eObject);
        return modelAnnotation.getMaxSetSize();
    }
    
    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#isRecordType(char)
     */
    public boolean isRecordType(char recordType) {
        return (recordType == IndexConstants.RECORD_TYPE.MODEL || recordType == IndexConstants.RECORD_TYPE.ANNOTATION);
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#isQueryable(org.eclipse.emf.ecore.EObject)
     */
    public boolean isQueryable(final EObject eObject) {
		int type = getModelType(eObject);
        return (type == ModelType.PHYSICAL || type == ModelType.VIRTUAL || type == ModelType.MATERIALIZATION);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#getName(org.eclipse.emf.ecore.EObject)
     */
    public String getName(EObject eObject) {
        return ModelerCore.getModelEditor().getModelName(eObject);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#getNameInSource(org.eclipse.emf.ecore.EObject)
     */
    public String getNameInSource(EObject eObject) {
        final ModelAnnotation modelAnnotation = getModelAnnotation(eObject);
        return modelAnnotation.getNameInSource();
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAnnotationAspect#getDescription(org.eclipse.emf.ecore.EObject)
     */
    public String getDescription(EObject eObject) {
        final ModelAnnotation modelAnnotation = getModelAnnotation(eObject);
        return modelAnnotation.getDescription();
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAnnotationAspect#getKeywords(org.eclipse.emf.ecore.EObject)
     */
    public List getKeywords(EObject eObject) {
        return Collections.EMPTY_LIST;
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.AbstractMetamodelAspect#getFullName(org.eclipse.emf.ecore.EObject)
     */
    @Override
    public String getFullName(EObject eObject) {
        // The fullname returned from the aspect path will be of the 
        // form Model/ModelAnnotation but only want the first segment 
        // for the model path so instead just call getName(EObject).
        return this.getName(eObject);
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.AbstractMetamodelAspect#getPath(org.eclipse.emf.ecore.EObject)
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
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAnnotationAspect#getTags(org.eclipse.emf.ecore.EObject)
     */
    public Map getTags(EObject eObject) {
        final ModelAnnotation annotation = getModelAnnotation(eObject);
        final EMap tags = annotation.getTags();
        if ( tags != null && tags.size() != 0 ) {
            return tags.map();
        }
        return Collections.EMPTY_MAP;
    }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#updateObject(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject)
     */
    public void updateObject(EObject targetObject, EObject sourceObject) {

    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#getParentObjectID(org.eclipse.emf.ecore.EObject)
     */
    @Override
    public Object getParentObjectID(EObject eObject) {
        return null;
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAnnotationAspect#getURI(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    public URI getURI(EObject eObject) {
        return ModelerCore.getModelEditor().getUri(eObject);
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAnnotationAspect#getMetaclassURI(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    public URI getMetaclassURI(EObject eObject) {
        return ModelerCore.getModelEditor().getUri(eObject.eClass());
    }
}
