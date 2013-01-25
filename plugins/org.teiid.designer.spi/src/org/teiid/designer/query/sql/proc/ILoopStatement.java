/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.query.sql.proc;

import org.teiid.designer.query.sql.ILanguageVisitor;
import org.teiid.designer.query.sql.lang.ICommand;
import org.teiid.designer.query.sql.lang.ISubqueryContainer;

/**
 *
 */
public interface ILoopStatement<LV extends ILanguageVisitor, C extends ICommand>
    extends IStatement<LV>, ISubqueryContainer<C> {

}
