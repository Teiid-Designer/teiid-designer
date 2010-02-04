/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.viewsupport;

import java.util.HashSet;
import org.eclipse.core.resources.IResource;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;


/** 
 * @since 4.3
 */
public class ModelFileCache {
    private static final String XMI = "xmi";  //$NON-NLS-1$
    private static final String XSD = "xsd";  //$NON-NLS-1$
    
    private HashSet modelFileCache;
    
    /**
     * Constructs an <code>EObjectModelerCache</code>.
     */
    public ModelFileCache() {
        this.modelFileCache = new HashSet();
    }
    
    public boolean isModelFile( IResource resource ) {
        // Check file extension, return false if null or NOT .xmi
        String ext = resource.getFileExtension();
        if( ext == null )
            return false;
        
        if( ext.equals(XMI) || ext.equals(XSD) ) {
            // OK
        } else {
            return false;
        }
        String path = resource.getFullPath().toString();
        // First check the model file cache
        if( path != null && modelFileCache.contains(path) ) {
            return true;
        }
        
        // Now if we get here, it's not in either cache.
        boolean isModel = ModelUtil.isModelFile(resource);
        
        if( isModel )
            modelFileCache.add(path);

        return isModel;
    }
}
