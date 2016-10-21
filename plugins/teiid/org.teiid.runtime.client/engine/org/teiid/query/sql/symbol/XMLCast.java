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

package org.teiid.query.sql.symbol;

import org.teiid.designer.annotation.Since;
import org.teiid.designer.query.sql.symbol.IXMLCast;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.query.parser.LanguageVisitor;
import org.teiid.query.sql.lang.SimpleNode;
import org.teiid.query.sql.visitor.SQLStringVisitor;

/**
 * The XMLCast symbol
 */
@Since(Version.TEIID_8_10)
public class XMLCast extends SimpleNode implements Expression, IXMLCast<LanguageVisitor> {
	
	private Expression expression;
	private Class<?> type;
	
	/**
     * @param p
     * @param id
     */
    public XMLCast(ITeiidServerVersion p, int id) {
        super(p, id);
    }
	
	/**
	 * @return expression
	 */
	public Expression getExpression() {
		return expression;
	}
	
	/**
	 * @param expression
	 */
	public void setExpression(Expression expression) {
		this.expression = expression;
	}
	
	@Override
	public void acceptVisitor(LanguageVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public XMLCast clone() {
	    XMLCast clone = new XMLCast(getTeiidVersion(), this.id);

        if(getExpression() != null)
            clone.setExpression(getExpression().clone());
        if(getType() != null)
            clone.setType(getType());
        
        return clone;
	}
	
	@Override
	public Class<?> getType() {
		return type;
	}

	/**
     * @param type the type to set
     */
    public void setType(Class<?> type) {
        this.type = type;
    }

	@Override
	public int hashCode() {
		return expression.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof XMLCast)) {
			return false;
		}
		XMLCast other = (XMLCast)obj;
		return this.expression.equals(other.expression) && getType().equals(other.getType());
	}
	
	@Override
	public String toString() {
		return SQLStringVisitor.getSQLString(this);
	}
	
}
