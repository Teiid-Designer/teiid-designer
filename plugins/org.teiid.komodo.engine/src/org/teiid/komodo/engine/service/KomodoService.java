/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.komodo.engine.service;

/**
 * Service for managing all requests to the teiid server
 */
public class KomodoService {

    private static KomodoService instance;
    
    public static KomodoService getInstance() {
        if (instance == null) {
            instance = new KomodoService();
        }
        
        return instance;
    }
    
    private KomodoService() {
        
    }
    
    public void execute(KTask task) {
        
    }
    
    
}
