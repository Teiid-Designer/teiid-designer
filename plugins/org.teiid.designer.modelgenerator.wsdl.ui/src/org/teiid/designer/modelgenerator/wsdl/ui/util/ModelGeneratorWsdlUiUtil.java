/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.modelgenerator.wsdl.ui.util;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.teiid.core.designer.util.FileUtils;
import org.teiid.designer.modelgenerator.wsdl.ui.ModelGeneratorWsdlUiPlugin;



/** 
 * @since 8.0
 */
public class ModelGeneratorWsdlUiUtil implements FileUtils.Constants {

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Don't allow construction.
     * @since 4.2
     */
    private ModelGeneratorWsdlUiUtil() {}
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * Convenience method to retrieve a Web Service UI image. 
     * @param theImageName the name of the image being requested
     * @return the image or <code>null</code> if not found
     * @since 4.2
     */
    public static Image getImage(String theImageName) {
        return ModelGeneratorWsdlUiPlugin.getDefault().getImage(theImageName);
    }
    
    /**
     * Convenience method to retrieve a Web Service UI image descriptors. 
     * @param theImageName the name of the image descriptor being requested
     * @return the image descriptor or <code>null</code> if not found
     * @since 4.2
     */
    public static ImageDescriptor getImageDescriptor(String theImageName) {
        return ModelGeneratorWsdlUiPlugin.getDefault().getImageDescriptor(theImageName);
    }
    
}
