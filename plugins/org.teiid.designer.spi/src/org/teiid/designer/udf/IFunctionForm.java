/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.udf;

import java.util.List;

/**
 *
 */
public interface IFunctionForm {


    /**
     * Get the function name
     * 
     * @return name
     */
    String getName();
    
    /**
     * Get the display name of the function
     * 
     * @return display name
     */
    String getDisplayString();

    /**
     * Get the function description
     *  
     * @return description
     */
    String getDescription();

    /**
     * Get the category this function belongs to
     * 
     * @return name of the owning category
     */
    String getCategory();

    /**
     * Get the arguments
     * 
     * @return list of the function's arguments
     */
    List<String> getArgNames();

}
