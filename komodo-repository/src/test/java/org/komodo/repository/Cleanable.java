/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.komodo.repository;


/**
 * Indicates that the object state can be cleared back to an initial state.
 */
public interface Cleanable {

    /**
     *  Cleans/clears object state.
     *  
     * @throws Exception if there is a problem cleaning state
     */
    void clean() throws Exception;

}
