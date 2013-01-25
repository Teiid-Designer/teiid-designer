/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.query.sql.symbol;

import org.teiid.designer.query.sql.ILanguageVisitor;
import org.teiid.designer.query.sql.lang.IExpression;
import org.teiid.designer.udf.IFunctionDescriptor;

/**
 *
 */
public interface IFunction<F extends IFunctionDescriptor, LV extends ILanguageVisitor>
    extends IExpression<LV> {

    /**
     * Get name of function
     * 
     * @return name
     */
    String getName();
    
    /**
     * Get function arguments
     * 
     * @return array of arguments
     */
    IExpression[] getArgs();

    /**
     * Get argument at given index
     * 
     * @param index
     * 
     * @return argument
     */
    IExpression getArg(int index);
    
    /**
     * Is function implicit
     * 
     * @return true if implicit
     */
    boolean isImplicit();

    /**
     * Get a descriptor for his function
     * 
     * @return descriptor
     */
    F getFunctionDescriptor();

    /**
     * Set the function descriptor
     * 
     * @param fd
     */
    void setFunctionDescriptor(F fd);

    /**
     * Set type of function
     * 
     * @param type New type
     */
    void setType(Class<?> type);

}
