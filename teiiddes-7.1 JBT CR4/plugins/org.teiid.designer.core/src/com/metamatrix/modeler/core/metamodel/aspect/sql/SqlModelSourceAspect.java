/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.metamodel.aspect.sql;

import java.util.Properties;
import org.eclipse.emf.ecore.EObject;

/**
 * SqlModelAspect is used to get the model source information 
 * for runtime metadata.
 */
public interface SqlModelSourceAspect extends SqlAspect {
    
    /**
     * Return Properties object for the model source
     * @param eObject The <code>EObject</code> to retrieve model source properties
     * @return Properties 
     */
    Properties getProperties(EObject eObject);

}
