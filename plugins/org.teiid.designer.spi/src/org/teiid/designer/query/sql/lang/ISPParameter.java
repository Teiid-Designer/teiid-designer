/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.query.sql.lang;

import org.teiid.designer.query.sql.symbol.IElementSymbol;


/**
 *
 */
public interface ISPParameter {

    /**
     * Enumerator for types of parameters 
     */
    enum ParameterInfo {
        /** Constant identifying an IN parameter */
        IN,

        /** Constant identifying an OUT parameter */
        OUT,

        /** Constant identifying an INOUT parameter */
        INOUT,

        /** Constant identifying a RETURN parameter */
        RETURN_VALUE,

        /** Constant identifying a RESULT SET parameter */
        RESULT_SET;

        /**
         * Get the index of the enumerator. For compatibility
         * with existing code, the index starts at 1 rather than 0.
         * 
         * @return value of index
         */
        public int index() {
            return ordinal() + 1;
        }
    }

    /**
     * Add a result set column if this parameter is a return
     * result set.
     * 
     * @param colName Name of column
     * @param type Type of column
     * @param id id of column
     */
    void addResultSetColumn(String colName, Class<?> type, Object id);
    
    /**
     * Get element symbol representing this parameter.  The symbol will have the
     * same name and type as the parameter.
     * 
     * @return Element symbol representing the parameter
     */
    IElementSymbol getParameterSymbol();

    /**
     * Get full parameter name,.  If unknown, null is returned.
     * 
     * @return Parameter name
     */
    String getName();

    /**
     * Set full parameter name
     * 
     * @param name Parameter name
     */
    void setName(String name); 

    /**
     * Get type of parameter according to class constants.
     * 
     * @return Parameter type
     */
    int getParameterType();
    
    /**
     * Set parameter type according to class constants.
     * 
     * @param parameterType Type to set
     */
    void setParameterType(ParameterInfo parameterType);

    /**
     * Get the class type
     * 
     * @return class type
     */
    Class<?> getClassType();
    
    /**
     * Set the class type
     * 
     * @param klazz
     */
    void setClassType(Class<?> klazz);

    /**
     * Get the metadata ID
     * 
     * @return the metadata ID object
     */
    Object getMetadataID();
    
    /**
     * Set the metadata ID object
     * 
     * @param object
     */
    void setMetadataID(Object object);

}
