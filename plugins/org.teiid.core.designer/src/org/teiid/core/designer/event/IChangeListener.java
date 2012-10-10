/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.core.designer.event;

/**
 * The <code>IChangeListener</code> interface is informed of changes from a <code>IContentChangeNotifier</code>
 * whom the listener is registered with.
 *
 * @since 8.0
 */
public interface IChangeListener {

    /**
     * Called whenever the state of the given source has changed.
     * @param theSource the source whose state has changed
     */
    void stateChanged(IChangeNotifier theSource);
}
