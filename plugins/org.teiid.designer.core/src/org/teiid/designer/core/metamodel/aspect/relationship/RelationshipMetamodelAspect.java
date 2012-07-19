/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.metamodel.aspect.relationship;

import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.ecore.EObject;
import org.teiid.designer.core.metamodel.aspect.MetamodelAspect;

/**
 * RelationshipMetamodelAspect
 *
 * @since 8.0
 */
public interface RelationshipMetamodelAspect extends MetamodelAspect {

	/**
	 * Returns true if the implementation of this RelationshipMetamodelAspect represents
	 * the specified record type.
	 * @param recordType one of {@link org.teiid.designer.core.index.IndexConstants.RECORD_TYPE}
	 * @return
	 */
	boolean isRecordType(char recordType);

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

}
