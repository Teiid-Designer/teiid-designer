package org.teiid.designer.udf;

import java.util.List;
/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/

/**
 *
 */
public interface IFunctionLibrary {
    
    public enum FunctionName {
        // Special type conversion functions
        CONVERT,
        CAST,

        // Special lookup function
        LOOKUP,

        // Special user function
        USER,
        // Special environment variable lookup function
        ENV,
        SESSION_ID,

        // Special pseudo-functions only for XML queries
        CONTEXT,
        ROWLIMIT,
        ROWLIMITEXCEPTION,

        // Misc.
        DECODESTRING,
        DECODEINTEGER,
        COMMAND_PAYLOAD,

        CONCAT,
        CONCAT2,
        CONCAT_OPERATOR,
        SUBSTRING,
        NVL,
        IFNULL,

        FROM_UNIXTIME,
        TIMESTAMPADD,

        PARSETIME,
        PARSEDATE,
        FORMATTIME,
        FORMATDATE,

        NULLIF,
        COALESCE,

        SPACE,
        ARRAY_GET
    }
    
    /**
     * Get the function name according to the given enum value
     * 
     * @param functionName
     * 
     * @return function name of a function in the library
     */
    String getFunctionName(FunctionName functionName);

    /**
     * Categories of the functions in this library
     * 
     * @return names of the categories
     */
    List<String> getFunctionCategories();

    /**
     * Get the functions in the given category
     * 
     * @param category
     * 
     * @return those functions in the given category
     */
    List<IFunctionForm> getFunctionForms(String category);

    /**
     * Find the function with the given name and number
     * of arguments
     * 
     * @param name
     * @param length number of arguments
     * 
     * @return function or null
     */
    IFunctionForm findFunctionForm(String name, int length);

    /**
     * Find a function descriptor given a name and the types of the arguments.
     * This method matches based on case-insensitive function name and
     * an exact match of the number and types of parameter arguments.
     * 
     * @param name Name of the function to resolve
     * @param types Array of classes representing the types
     * 
     * @return Descriptor if found, null if not found
     */
    IFunctionDescriptor findFunction(String name, Class[] types);

}
