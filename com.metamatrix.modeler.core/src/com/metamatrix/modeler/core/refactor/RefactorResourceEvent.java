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

package com.metamatrix.modeler.core.refactor;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;


/** 
 * This class provides an event data payload for resource refactor commands and actions.
 * 
 * @since 5.0
 */
public class RefactorResourceEvent {
    public static final int TYPE_UNKNOWN = -1;
    public static final int TYPE_RENAME = 0;
    public static final int TYPE_MOVE = 1;
    public static final int TYPE_DELETE = 2;
    
    private Object source;
    private IResource resource;
    private IPath originalPath;
    
    private int type = TYPE_UNKNOWN;
    
    /** 
     * 
     * @since 5.0
     */
    public RefactorResourceEvent(IResource theResource, int theType, Object theSource, IPath theOriginalPath) {
        super();
        this.resource = theResource;
        this.type = theType;
        this.source = theSource;
        this.originalPath = theOriginalPath;
    }
    
    public int getType() {
        return this.type;
    }
    
    public Object getSource() {
        return this.source;
    }
    
    public IResource getResource() {
        return this.resource;
    }
    
    public IPath getOriginalPath() {
        return this.originalPath;
    }

}
