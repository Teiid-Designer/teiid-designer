/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core;

import org.eclipse.emf.ecore.resource.ResourceSet;

/**
 * ExternalResourceSet is the interface for all extensions of
 * ModelerCore.EXTENSION_POINT.EXTERNAL_RESOURCE_SET. Implementations
 * define a org.eclipse.emf.ecore.resource.ResourceSet that is
 * external to the model container but may be needed by the container
 * for resolution of resource references.
 */
public interface ExternalResourceSet {

    ResourceSet getResourceSet();

}
