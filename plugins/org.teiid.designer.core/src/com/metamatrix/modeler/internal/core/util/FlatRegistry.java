/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.util;

import java.util.HashMap;
import java.util.Map;
import com.metamatrix.modeler.core.Registry;

/**
 */
public class FlatRegistry implements Registry {
    
    private Map entries;
    
    /**
     * Constructor for RegistryImpl.
     */
    public FlatRegistry() {
        this.entries = new HashMap();
    }

    /**
     * @see com.metamatrix.api.mtk.core.Registry#lookup(String)
     */
    public Object lookup(String name) {
        return this.entries.get(name);
    }

    /**
     * @see com.metamatrix.api.mtk.core.Registry#register(String, Object)
     */
    public Object register(String name, Object obj) {
        return this.entries.put(name,obj);
    }

    /**
     * @see com.metamatrix.api.mtk.core.Registry#unregister(String)
     */
    public Object unregister(String name) {
        return this.entries.remove(name);
    }
    
    public int size() {
        return this.entries.size();
    }

}
