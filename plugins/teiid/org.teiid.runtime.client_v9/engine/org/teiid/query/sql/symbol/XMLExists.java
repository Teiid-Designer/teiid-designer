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

import org.teiid.designer.query.sql.symbol.IXMLExists;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.query.parser.LanguageVisitor;
import org.teiid.query.sql.lang.Criteria;

/**
 * XMLExists
 */
public class XMLExists extends Criteria implements IXMLExists<LanguageVisitor> {
	
	private XMLQuery xmlQuery;
	
	/**
     * @param p
     * @param id
     */
    public XMLExists(ITeiidServerVersion p, int id) {
        super(p, id);
    }

	/**
	 * @return xmlQuery
	 */
	public XMLQuery getXmlQuery() {
		return xmlQuery;
	}

	/**
     * @param xmlQuery the xmlQuery to set
     */
    public void setXmlQuery(XMLQuery xmlQuery) {
        this.xmlQuery = xmlQuery;
    }

	@Override
	public void acceptVisitor(LanguageVisitor visitor) {
		visitor.visit(this);
	}

	@Override
    public XMLExists clone() {
        XMLExists clone = new XMLExists(getTeiidVersion(), this.id);

        if(getXmlQuery() != null)
            clone.setXmlQuery(getXmlQuery().clone());

        return clone;
    }
	
	@Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((this.xmlQuery == null) ? 0 : this.xmlQuery.hashCode());
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
        XMLExists other = (XMLExists)obj;
        if (this.xmlQuery == null) {
            if (other.xmlQuery != null)
                return false;
        } else if (!this.xmlQuery.equals(other.xmlQuery))
            return false;
        return true;
    }
	
}
