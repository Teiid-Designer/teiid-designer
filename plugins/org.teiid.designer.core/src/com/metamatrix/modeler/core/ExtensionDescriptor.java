/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core;

/**
 * ExtensionDescriptor
 */
public interface ExtensionDescriptor {
    
    /**
     * Return the ID for this extension.
     * @return the ID - never null;
     */
    public Object getId();
    
    /**
     * Returns class name specified when the ExtensionDescriptor was constructed. 
     * @return class name associated with this descriptor
     */
    public String getClassName();

    /**
     * Returns the loaded class for this extension using the plug-in 
     * class loader specified when the ExtensionDescriptor was constructed. 
     * The successful loading of the class will <b>always activate</b> the 
     * corresponding plug-in.
     * @return the loaded class or null if the class could not be loaded.
     */
    public Class getExtensionClass();
    
    /**
     * Returns an instance of the loaded class for this extension using the plug-in 
     * class loader specified when the ExtensionDescriptor was constructed. 
     * The successful loading of the class will <b>always activate</b> the 
     * corresponding plug-in.  The <b>same</b> instance will returned for all calls
     * to this method.
     * @return an instance of the loaded class or null if the class 
     * could not be loaded or an instance could not be created.
     */
    public Object getExtensionClassInstance();
    
    /**
     * Returns a new instance of the loaded class for this extension using the plug-in 
     * class loader specified when the ExtensionDescriptor was constructed. 
     * The successful loading of the class will <b>always activate</b> the 
     * corresponding plug-in.  The new instance will returned for all calls
     * to this method.
     * @return an instance of the loaded class or null if the class 
     * could not be loaded or an instance could not be created.
     */
    public Object getNewExtensionClassInstance();
    
    /**
     * Returns whether this descriptor is a multi-descriptor.
     * A multi-descriptor describes an extension involving multiple
     * class declarations.
     * @return <code>true</code> for a multi-descriptor, <code>false</code> otherwise
     * @see #getChildren
     */
    public boolean isMultiDescriptor();
    
    /**
     * Returns a list of descriptor objects immediately contained in this
     * multi-descriptor, or an empty list if this is not a multi-descriptor.
     * @return an array of descriptor objects
     * @see #isMultiStatus
     */
    public ExtensionDescriptor[] getChildren();
    
    /**
     * Return the specified child descriptor object contained in this
     * multi-descriptor, or null if this is not a multi-descriptor.
     * @return the descriptor registered by this id.
     * @see #isMultiStatus
     */
    public ExtensionDescriptor getChildDescriptor(Object id);

}
