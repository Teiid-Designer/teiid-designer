/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.search.runtime;

import java.util.Properties;

/**
 * AnnotatedObjectRecord
 * recordType|objectID|name|fullname|uri|tags|description|modelPath|metaclassURI|
 */

public interface AnnotatedObjectRecord extends ResourceObjectRecord {

    String getDescription();

    Properties getProperties();
    
}
