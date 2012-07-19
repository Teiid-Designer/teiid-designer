/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.association;

import java.util.Collection;
import java.util.List;

import org.teiid.designer.core.ModelerCoreException;

/**
 * AssociationProvider
 *
 * @since 8.0
 */
public interface AssociationProvider {
    
    Collection getNewAssociationDescriptors(final List eObjects) throws ModelerCoreException;

}
