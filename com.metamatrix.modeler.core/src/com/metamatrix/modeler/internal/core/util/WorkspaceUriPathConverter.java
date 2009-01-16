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

import org.eclipse.core.resources.IResource;
import org.eclipse.emf.common.util.URI;
import com.metamatrix.modeler.internal.core.workspace.WorkspaceResourceFinderUtil;

/**
 * WorkspaceUriHelper
 */
public class WorkspaceUriPathConverter extends BasicUriPathConverter {

    // ==================================================================================
    // C O N S T R U C T O R S
    // ==================================================================================

    /**
     * Construct an instance of WorkspaceUriHelper.
     */
    public WorkspaceUriPathConverter() {
        super();
    }

    // ==================================================================================
    // O V E R R I D D E N M E T H O D S
    // ==================================================================================

    /**
     * @see com.metamatrix.modeler.core.util.UriPathConverter#makeAbsolute(org.eclipse.emf.common.util.URI,
     *      org.eclipse.emf.common.util.URI)
     */
    @Override
    public URI makeAbsolute( final URI relativeURI,
                             final URI baseURI ) {
        URI resourceURI = relativeURI;
        final IResource iResource = WorkspaceResourceFinderUtil.findIResource(relativeURI);
        if (iResource != null) {
            resourceURI = URI.createFileURI(iResource.getLocation().toOSString());
        }
        return resourceURI;
    }

}
