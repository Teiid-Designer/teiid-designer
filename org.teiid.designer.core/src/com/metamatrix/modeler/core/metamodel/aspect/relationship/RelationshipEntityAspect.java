/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.metamodel.aspect.relationship;

import org.eclipse.emf.ecore.EObject;

/**
 * RelationshipEntityAspect.java
 */
public interface RelationshipEntityAspect extends RelationshipMetamodelAspect {

	/**
	 * Get the name of the metamodel entity.
	 * @param eObject The <code>EObject</code> for which name is obtained
	 * @return name of the metamodel entity.
	 */
	String getName(EObject eObject);

}
