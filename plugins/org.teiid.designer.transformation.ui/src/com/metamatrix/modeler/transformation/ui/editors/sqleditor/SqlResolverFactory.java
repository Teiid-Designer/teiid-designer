/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.ui.editors.sqleditor;

import org.eclipse.emf.ecore.EObject;
import org.teiid.query.metadata.QueryMetadataInterface;

/**
 * SqlResolverFactory
 */
public interface SqlResolverFactory {

    public void setCurrentEObject(EObject eObj);

    public QueryMetadataInterface getQueryMetadata();

}
