/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ui.viewsupport;

import java.util.HashSet;
import org.eclipse.core.resources.IResource;
import org.teiid.core.designer.util.StringConstants;
import org.teiid.designer.core.workspace.ModelUtil;


/** 
 * @since 8.0
 */
public class ModelFileCache implements StringConstants {

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
