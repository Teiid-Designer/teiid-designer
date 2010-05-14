/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.association;

import java.util.Collection;
import java.util.List;
import com.metamatrix.modeler.core.ModelerCoreException;

/**
 * AssociationProvider
 */
public interface AssociationProvider {
    
    Collection getNewAssociationDescriptors(final List eObjects) throws ModelerCoreException;

}
