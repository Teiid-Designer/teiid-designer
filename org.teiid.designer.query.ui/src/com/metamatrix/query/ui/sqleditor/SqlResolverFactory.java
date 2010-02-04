/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.query.ui.sqleditor;

import org.eclipse.emf.ecore.EObject;
import com.metamatrix.query.metadata.QueryMetadataInterface;

/**
 * SqlResolverFactory
 */
public interface SqlResolverFactory {

    public void setCurrentEObject(EObject eObj);

    public QueryMetadataInterface getQueryMetadata();

}
