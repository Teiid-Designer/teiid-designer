/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

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
