/*
 * JBoss, Home of Professional Open Source.
 * See the COPYRIGHT.txt file distributed with this work for information
 * regarding copyright ownership.  Some portions may be licensed
 * to Red Hat, Inc. under one or more contributor license agreements.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 */

package org.teiid.query.sql.lang;

import org.teiid.designer.query.sql.lang.ISubqueryContainer;

/**
 * This interface defines a common interface for all SQL objects 
 * that contain subqueries. 
 * @param <T> command
 */
public interface SubqueryContainer<T extends Command> extends LanguageObject, ISubqueryContainer<T> {
	
	public static interface Evaluatable<T extends Command> extends SubqueryContainer<T> {
		boolean shouldEvaluate();
		
		void setShouldEvaluate(boolean b);
	}
    /**
     * Returns the subquery Command object
     * @return the subquery Command object
     */
    @Override
    T getCommand();

    /**
     * Sets the subquery Command object
     * @param command the subquery Command object
     */
    @Override
    void setCommand(T command);

}
