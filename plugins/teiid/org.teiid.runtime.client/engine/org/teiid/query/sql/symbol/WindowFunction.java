/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.sql.symbol;

import org.teiid.designer.annotation.Removed;
import org.teiid.designer.query.sql.symbol.IWindowFunction;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.query.parser.LanguageVisitor;
import org.teiid.query.sql.lang.Node;
import org.teiid.query.sql.lang.SingleElementSymbol;

/**
 *
 */
@SuppressWarnings( "unused" )
public interface WindowFunction extends Node, SingleElementSymbol, Expression, IWindowFunction<LanguageVisitor> {

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
    @Removed(Version.TEIID_8_0)
    String getName();

    /**
     * @param name
     */
    @Removed(Version.TEIID_8_0)
    void setName(String name);

    @Override
    Class<?> getType();

    /** Accept the visitor. **/
    @Override
    void acceptVisitor(LanguageVisitor visitor);

}
