/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.query.metadata;

import java.util.List;
import org.teiid.designer.query.sql.lang.ICommand;


/**
 *
 */
public interface IQueryNode {

    /**
     * @param binding
     */
    void addBinding(String binding);

    /**
     * @return list of bindings
     */
    List<String> getBindings();

    /**
     * @return command
     */
    ICommand getCommand();

    /**
     * @return sql of the query
     */
    String getQuery();

}
