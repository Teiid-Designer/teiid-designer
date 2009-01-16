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

package com.metamatrix.modeler.core.metamodel.aspect.sql;

import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.ecore.EObject;

import com.metamatrix.modeler.core.metamodel.aspect.MetamodelAspect;

/**
 * SqlAspect
 */
public interface SqlAspect extends MetamodelAspect {

    /**
     * Returns true if, for this implementation of SqlAspect, the eObject
     * being checked can be used in the context of SQL query validation or 
     * resolution.  If false is returned, this aspect may be used to obtain
     * metamodel specific information about this EObject, but this EObject
     * is not necessary for query validation or resolution.
     * <p>Examples of EObjects for which false is returned are:</p>
     * <li>
     * Transformation.InputSet
     * Transformation.InputSetParameter
     * Xsd.XSDSimpleTypeDefinition (if not a global simple type)
     * </li>
     * @param eObject The <code>EObject</code> to be checked
     * @return
     */
    boolean isQueryable(EObject eObject);

    /**
     * Returns true if the implementation of this SqlAspect represents
     * the specified record type.
     * @param recordType one of {@link com.metamatrix.modeler.core.index.IndexConstants.RECORD_TYPE}
     * @return
     */
    boolean isRecordType(char recordType);

    /**
     * Get entity short name
     * @param eObject The <code>EObject</code> for which name is obtained 
     * @return short name of the metamodel entity.
     */
    String getName(EObject eObject);
    
    /**
     * Get entity fully qualified name.
     * @param eObject The <code>EObject</code> for which name is obtained 
     * @return full name of the metamodel entity.
     */
    String getFullName(EObject eObject);    

    /**
     * Get entity name in source
     * @param eObject The <code>EObject</code> for which nameInSource is obtained 
     * @return nameInSource of the metamodel entity.
     */
    String getNameInSource(EObject eObject);
        
    /**
     * Get the ObjectID of the metamodel entity.
     * @param eObject The <code>EObject</code> for which object ID is obtained 
     * @return ObjectID of the metamodel entity.
     */
    Object getObjectID(EObject eObject);
        
    /**
     * Get the ObjectID of the parent metamodel entity.  The parent
     * entity may be the actual eContainer for the specified EObject or
     * may represent a logical parent within the model.
     * @param eObject The <code>EObject</code> for which the parent's object ID is obtained 
     * @return ObjectID of the parent entity.
     */
    Object getParentObjectID(EObject eObject);
    
    /**
     * Get the relative path within the model including the model name.
     * @param eObject The <code>EObject</code> for which path is obtained 
     * @return short name of the table
     */
    IPath getPath(EObject eObject);

    /**
     * Update the targetObject with the properties of the sourceObject,
     * this method may or maynot set all the properties and is not always implemented
     * depending on the aspect.
     * @param targetObject The target object that needs to be updated
     * @param sourceObject The source object whose properties are copied over
     */
    void updateObject(EObject targetObject, EObject sourceObject);

}
