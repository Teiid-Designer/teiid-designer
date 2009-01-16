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

import java.util.List;

/**
 * This interface represents a handler that a 
 * {@link com.metamatrix.modeler.core.refactor.OrganizeImportCommand OrganizeImportCommand} uses
 * when there are ambiguous choices.
 */
public interface OrganizeImportHandler {

    /**
     * Request for this handler to identify which, if any, of the supplied choices should be used.
     * @param options the list of choices; never null
     * @return the choice, which must be one of the objects in the <code>options</code> list;
     * may be null if no choice should be made
     */
    public Object choose( final List options );

}
