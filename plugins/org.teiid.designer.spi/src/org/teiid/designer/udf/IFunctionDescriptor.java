/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.udf;


/**
 *
 */
public interface IFunctionDescriptor {

    /**
     * Get name of function descriptor
     * 
     * @return name of descriptor
     */
    String getName();

    /**
     * Get metadata id
     * 
     * @return id
     */
    Object getMetadataID();

	/**
     * Get return type
     * 
     * @return type
     */
    Class<?> getReturnType();
}
