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

}
