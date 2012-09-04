/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.komodo.engine.service;

/**
 *
 */
public interface KTask<T> {
    
    T execute(Admin admin);
    
    void postExecute(T result);

}
