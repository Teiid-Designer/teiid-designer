/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.dqp.config;

/**
 * The <code>IChangeListener</code> interface is informed of changes from a <code>IContentChangeNotifier</code>
 * whom the listener is registered with.
 */
public interface IConfigurationChangeListener {

    /**
     * Called whenever the state of the given source has changed.
     * @param theSource the source whose state has changed
     */
    void stateChanged(ConfigurationChangeEvent event) throws Exception;
}
