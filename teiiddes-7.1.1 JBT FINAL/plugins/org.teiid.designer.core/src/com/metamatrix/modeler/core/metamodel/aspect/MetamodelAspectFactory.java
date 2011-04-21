/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.metamodel.aspect;

import org.eclipse.emf.ecore.EClassifier;

/**
 * MetamodelAspectFactory
 */
public interface MetamodelAspectFactory {
    MetamodelAspect create(EClassifier classifier, MetamodelEntity entity);
}
