/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core;

import java.util.Properties;

/**
 * ExtensionDescriptor
 *
 * @since 8.0
 */
public interface ExternalResourceSetDescriptor extends ExtensionDescriptor {
    
    /**
     * Return the {@link java.util.Properties} associated with this external resource set.
     * @return Properties.
     */
    Properties getProperties();

}
