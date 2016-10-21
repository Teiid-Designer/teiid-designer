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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import org.teiid.core.types.DataTypeManagerService;
import org.teiid.designer.query.sql.lang.ILanguageObject;
import org.teiid.query.parser.LanguageVisitor;
import org.teiid.query.parser.TeiidNodeFactory.ASTNodes;
import org.teiid.query.parser.TeiidParserSPI;


/**
 * Root interface for all language object interfaces.
 */
public interface LanguageObject extends ILanguageObject<LanguageVisitor> {

    /**
     * @return the data type manager service
     */
	DataTypeManagerService getDataTypeService();

	/**
	 * @param nodeType
	 * @return new instance of an AST Node for the given node type
	 */
	<T extends LanguageObject> T createASTNode(ASTNodes nodeType);

    /**
     * @return ALL the comments related to this and all related objects
     */
    @Override
    Set<Comment> getComments();

    /**
     * Set the comments of this language object
     *
     * @param comments
     */
    void setComments(Set<Comment> comments);

    /**
     * @return copy of this language object
     */
    @Override
    LanguageObject clone();
    
    public static class Util {

		public static <S extends LanguageObject, T extends S> ArrayList<S> deepClone(Collection<T> collection, Class<S> type) {
			if (collection == null) {
				return null;
			}
			ArrayList<S> result = new ArrayList<S>(collection.size());
			for (LanguageObject obj : collection) {
				result.add(type.cast(obj.clone()));
			}
			return result;
		}
		
		@SuppressWarnings("unchecked")
		public static <T extends LanguageObject> T[] deepClone(T[] collection) {
			if (collection == null) {
				return null;
			}
			T[] copy = Arrays.copyOf(collection, collection.length);
			for (int i = 0; i < copy.length; i++) {
				LanguageObject t = copy[i];
				copy[i] = (T) t.clone();
			}
			return copy;
		}
    }
}
