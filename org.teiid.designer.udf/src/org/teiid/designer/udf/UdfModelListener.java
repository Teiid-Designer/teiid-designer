/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.udf;

public interface UdfModelListener {

    /**
     * @param event the event being processed (never <code>null</code>
     * @since 6.0.0
     */
    void processEvent(UdfModelEvent event);
}
