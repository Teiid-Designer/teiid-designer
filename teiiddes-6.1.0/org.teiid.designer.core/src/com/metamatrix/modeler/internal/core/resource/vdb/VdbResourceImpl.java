/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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
