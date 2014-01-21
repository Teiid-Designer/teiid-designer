/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.runtime.client.lang.ast;

import org.teiid.designer.annotation.Removed;
import org.teiid.runtime.client.lang.parser.AbstractTeiidParserVisitor;

/**
 *
 */
@SuppressWarnings( "unused" )
public interface WindowFunction extends Node, SingleElementSymbol, Expression {

    /**
     * @return the function
     */
    AggregateSymbol getFunction();

    /**
     * @param function the function to set
     */
    void setFunction(AggregateSymbol function);

    /**
     * @return the windowSpecification
     */
    WindowSpecification getWindowSpecification();

    /**
     * @param windowSpecification the windowSpecification to set
     */
    void setWindowSpecification(WindowSpecification windowSpecification);

    /**
     * @return name
     */
    @Removed("8.0.0")
    String getName();

    /**
     * @param name
     */
    @Removed("8.0.0")
    void setName(String name);

    @Override
    Class<?> getType();

    /** Accept the visitor. **/
    @Override
    void accept(AbstractTeiidParserVisitor visitor, Object data);

}
