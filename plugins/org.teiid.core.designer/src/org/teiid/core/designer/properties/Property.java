/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.core.designer.properties;

/**
 * The <code>Property</code> class includes a property definition and a value.
 *
 * @since 8.0
 */
public interface Property {

    /**
     * @return the property definition (never <code>null</code>)
     */
    PropertyDefinition getPropertyDefinition();

    /**
     * @return the current property value (can be <code>null</code> or empty
     */
    String getValue();
}
