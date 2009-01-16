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

package com.metamatrix.modeler.core.metamodel;

import java.util.Map;

/**
 * @since 4.3.1
 */
public interface IPasteContributor {

    // ===========================================================================================================================
    // Controller Methods

    /**
     * Called by {@link com.metamatrix.modeler.internal.core.container.PasteWithRelatedFromClipboardCommand#execute()} after the
     * core paste action has occurred.
     * 
     * @param map
     *            A map of original to pasted objects.
     * @param uri
     *            The metamodel URI of the target model.
     * @since 4.3.1
     */
    void contribute(Map map,
                    String uri);
}
