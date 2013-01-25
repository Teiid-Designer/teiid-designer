/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.query.sql.lang;

import java.util.List;
import org.teiid.designer.query.sql.ILanguageVisitor;

/**
 *
 */
public interface IBatchedUpdateCommand<C extends ICommand, E extends IExpression, LV extends ILanguageVisitor> 
    extends ICommand<E, LV> {

    /**
     * Gets the List of updates contained in this batch
     * 
     * @return
     */
    public List<C> getUpdateCommands();

}
