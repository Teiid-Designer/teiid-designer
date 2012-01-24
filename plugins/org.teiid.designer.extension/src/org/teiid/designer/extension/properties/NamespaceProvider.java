/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension.properties;

/**
 * The <code>NamespaceProvider</code> provides a namespace prefix for those classes needing one.
 */
public interface NamespaceProvider {

    /**
     * @return the namespace prefix (can be <code>null</code> or empty)
     */
    String getNamespacePrefix();

    /**
     * @return the namespace URI (can be <code>null</code> or empty)
     */
    String getNamespaceUri();

}
