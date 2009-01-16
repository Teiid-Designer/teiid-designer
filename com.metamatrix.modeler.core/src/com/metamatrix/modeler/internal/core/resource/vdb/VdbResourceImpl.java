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

package com.metamatrix.modeler.internal.core.resource.vdb;

import java.util.zip.ZipEntry;

import org.eclipse.emf.common.util.URI;

import com.metamatrix.modeler.internal.core.resource.xmi.MtkXmiResourceImpl;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;


/** 
 * VdbResourceImpl
 * @since 4.2
 */
public class VdbResourceImpl extends MtkXmiResourceImpl {

    /** 
     * @param uri
     * @since 4.2
     */
    public VdbResourceImpl(URI uri) {
        super(uri);
    }
    
    /** 
     * @see org.eclipse.emf.ecore.resource.impl.ResourceImpl#useZip()
     * @since 4.2
     */
    @Override
    public boolean useZip() {
        return true;
    }

    /** 
     * @see org.eclipse.emf.ecore.resource.impl.ResourceImpl#isContentZipEntry(java.util.zip.ZipEntry)
     * @since 4.2
     */
    @Override
    public boolean isContentZipEntry(final ZipEntry zipEntry) {
        if(zipEntry != null && zipEntry.getName().equalsIgnoreCase(ModelUtil.MANIFEST_MODEL_NAME)) {
            return true;
        }
        return false;
    }
}
