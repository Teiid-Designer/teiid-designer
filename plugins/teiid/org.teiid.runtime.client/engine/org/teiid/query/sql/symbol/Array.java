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

import java.util.List;
import org.teiid.core.types.DataTypeManagerService.DefaultDataTypes;
import org.teiid.core.util.ArgCheck;
import org.teiid.query.parser.LanguageVisitor;
import org.teiid.query.parser.TeiidParser;
import org.teiid.query.sql.lang.SimpleNode;

/**
 *
 */
public class Array extends SimpleNode implements Expression {

	private Class<?> type;
	private List<Expression> expressions;
	private boolean implicit;

	/**
     * @param p
     * @param id
     */
    public Array(TeiidParser p, int id) {
        super(p, id);
    }
	
	@Override
	public Class<?> getType() {
		return type;
	}
	
	/**
	 * @param type
	 */
	public void setType(Class<?> type) {
		if (type != null) {
			ArgCheck.isTrue(type.isArray(), "type not array"); //$NON-NLS-1$
		}
		this.type = type;
	}

	/**
	 * @return component type
	 */
	public Class<?> getComponentType() {
		if (this.type != null) {
			return this.type.getComponentType();
		}
		return null;
	}
	
	/**
	 * @param baseType
	 */
	public void setComponentType(Class<?> baseType) {
		if (baseType != null) {
		    DefaultDataTypes dataType = getTeiidParser().getDataTypeService().getDataType(baseType);
		    this.type = dataType.getTypeArrayClass();
		} else {
			this.type = null;
		}
	}
	
	/**
	 * @return expressions
	 */
	public List<Expression> getExpressions() {
		return expressions;
	}

	/**
     * @param expressions the expressions to set
     */
    public void setExpressions(List<Expression> expressions) {
        this.expressions = expressions;
    }

    /**
     * @return true, if the array has been implicitly constructed, such as with vararg parameters
     *
     */
    public boolean isImplicit() {
        return implicit;
    }

    /**
     * @param implicit
     */
    public void setImplicit(boolean implicit) {
        this.implicit = implicit;
    }

	@Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((this.expressions == null) ? 0 : this.expressions.hashCode());
        result = prime * result + (this.implicit ? 1231 : 1237);
        result = prime * result + ((this.type == null) ? 0 : this.type.hashCode());
        return result;
    }
	
	@Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        Array other = (Array)obj;
        if (this.expressions == null) {
            if (other.expressions != null)
                return false;
        } else if (!this.expressions.equals(other.expressions))
            return false;
        if (this.implicit != other.implicit)
            return false;
        if (this.type == null) {
            if (other.type != null)
                return false;
        } else if (!this.type.equals(other.type))
            return false;
        return true;
    }

	@Override
    public void acceptVisitor(LanguageVisitor visitor) {
        visitor.visit(this);
    }
    
    @Override
    public Array clone() {
        Array clone = new Array(this.parser, this.id);

        if(getExpressions() != null)
            clone.setExpressions(cloneList(getExpressions()));
        if(getComponentType() != null)
            clone.setComponentType(getComponentType());
        
        clone.implicit = implicit;

        return clone;
    }	
}
