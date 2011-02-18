/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.vdb.internal.edit;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.ecore.resource.Resource;
import com.metamatrix.core.modeler.util.ArgCheck;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.vdb.edit.VdbEditException;
import com.metamatrix.vdb.edit.manifest.ModelReference;

/**
 * RegisteredVdbInputResourceFinder
 */
public class RegisteredVdbInputResourceFinder implements VdbInputResourceFinder {

    private final Map resourcesByPath;
    private final Map pathsByResource;

    /**
     * Construct an instance of RegisteredVdbInputResourceFinder.
     */
    public RegisteredVdbInputResourceFinder() {
        super();
        this.resourcesByPath = new HashMap();
        this.pathsByResource = new HashMap();
    }

    /**
     * @see com.metamatrix.vdb.internal.edit.VdbInputResourceFinder#getEmfResource(org.eclipse.core.runtime.IPath)
     */
    public Resource getEmfResource( final IPath modelPath ) {
        return (Resource)this.resourcesByPath.get(modelPath);
    }

    /**
     * @see com.metamatrix.vdb.internal.edit.VdbInputResourceFinder#getEmfResource(com.metamatrix.vdb.edit.manifest.ModelReference)
     * @since 4.3
     */
    public Resource getEmfResource( final ModelReference modelRef ) {
        final String location = modelRef.getModelLocation();
        if (!StringUtil.isEmpty(location)) {
            final IPath modelPath = new Path(location).makeAbsolute();
            return getEmfResource(modelPath);
        }
        return null;
    }

    /**
     * @see com.metamatrix.vdb.internal.edit.VdbInputResourceFinder#getEmfResourceStream(org.eclipse.emf.ecore.resource.Resource)
     * @since 4.3
     */
    public InputStream getEmfResourceStream( final Resource emfResource ) throws VdbEditException {
        ArgCheck.isNotNull(emfResource);

        if (emfResource.getURI().isFile()) {
            try {
                final File f = new File(emfResource.getURI().toFileString());
                if (f.exists()) {
                    return new FileInputStream(f);
                }
            } catch (FileNotFoundException e) {
                throw new VdbEditException(e);
            }
        }
        return null;
    }

    /**
     * @see com.metamatrix.vdb.internal.edit.VdbInputResourceFinder#getEmfResourcePath(org.eclipse.emf.ecore.resource.Resource)
     */
    public IPath getEmfResourcePath( final Resource emfResource ) {
        return (IPath)this.pathsByResource.get(emfResource);
    }

    public Object register( final IPath modelPath,
                            final Resource emfResource ) {
        this.pathsByResource.put(emfResource, modelPath);
        return this.resourcesByPath.put(modelPath, emfResource);
    }

    public Object unregister( final IPath modelPath ) {
        final Resource emfResource = (Resource)this.resourcesByPath.get(modelPath);
        if (emfResource != null) {
            this.pathsByResource.remove(emfResource);
        }
        return this.resourcesByPath.remove(modelPath);
    }

    public Object unregister( final Resource emfResource ) {
        final IPath modelPath = (IPath)this.pathsByResource.get(emfResource);
        if (modelPath != null) {
            this.resourcesByPath.remove(modelPath);
        }
        return this.pathsByResource.remove(emfResource);
    }

}
