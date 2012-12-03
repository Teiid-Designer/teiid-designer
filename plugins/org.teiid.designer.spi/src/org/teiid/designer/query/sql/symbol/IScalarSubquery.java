/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.query.sql.symbol;

import org.teiid.designer.query.sql.lang.IExpression;
import org.teiid.designer.query.sql.lang.IQueryCommand;
import org.teiid.designer.query.sql.lang.ISubqueryContainer;

/**
 *
 */
public interface IScalarSubquery extends IExpression, ISubqueryContainer<IQueryCommand> {

}
