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

package org.teiid.translator;

import java.io.Serializable;
import java.util.Set;
import org.teiid.core.util.EquivalenceUtil;
import org.teiid.core.util.HashCodeUtil;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.query.parser.TeiidParser;
import org.teiid.query.sql.lang.Comment;

public class CacheDirective implements Serializable {
	
	public static enum Scope {
		NONE,
		SESSION,
		USER,
		VDB;
	}
	
	public enum Invalidation {
		/**
		 * No invalidation - the default
		 */
		NONE,
		/**
		 * Invalidate after new results have been obtained
		 */
		LAZY,
		/**
		 * Invalidate immediately
		 */
		IMMEDIATE
	}

	private static final long serialVersionUID = -4119606289701982511L;

	private final TeiidParser teiidParser;

	private Boolean prefersMemory;
	private Boolean updatable;
	private Boolean readAll;
	private Long ttl;
	private Scope scope;
	private Invalidation invalidation = Invalidation.NONE;
	
	public CacheDirective(TeiidParser teiidParser) {
	    this.teiidParser = teiidParser;
	}
	
	public CacheDirective(TeiidParser teiidParser, Boolean prefersMemory, Long ttl) {
	    this(teiidParser);
		this.prefersMemory = prefersMemory;
		this.ttl = ttl;
	}

	/**
     * @return the teiidParser
     */
    public TeiidParser getTeiidParser() {
        return this.teiidParser;
    }

    /**
     * @return version
     */
    public ITeiidServerVersion getTeiidVersion() {
        return this.getTeiidParser().getVersion();
    }

    /**
     * @return comments from parser
     */
    public Set<Comment> getComments() {
        return getTeiidParser().getComments();
    }

	public Boolean getPrefersMemory() {
		return prefersMemory;
	}
	
	public void setPrefersMemory(Boolean prefersMemory) {
		this.prefersMemory = prefersMemory;
	}
	
	/**
	 * Get the time to live in milliseconds
	 * @return
	 */
	public Long getTtl() {
		return ttl;
	}
	
	/**
	 * Set the time to live in milliseconds
	 * @param ttl
	 */
	public void setTtl(Long ttl) {
		this.ttl = ttl;
	}
	
	/**
	 * Get whether the result is updatable and therefore sensitive to data changes.
	 * @return
	 */
	public Boolean getUpdatable() {
		return updatable;
	}
	
	public void setUpdatable(Boolean updatable) {
		this.updatable = updatable;
	}
	
	public Scope getScope() {
		return this.scope;
	}

	public void setScope(Scope scope) {
		this.scope = scope;
	}
	
	/**
	 * Whether the engine should read and cache the entire results.
	 * @return
	 */
	public Boolean getReadAll() {
		return readAll;
	}
	
	public void setReadAll(Boolean readAll) {
		this.readAll = readAll;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof CacheDirective)) {
			return false;
		}
		CacheDirective other = (CacheDirective)obj;
		return EquivalenceUtil.areEqual(this.prefersMemory, other.prefersMemory)
		&& EquivalenceUtil.areEqual(this.readAll, other.readAll) 
		&& EquivalenceUtil.areEqual(this.ttl, other.ttl) 
		&& EquivalenceUtil.areEqual(this.updatable, other.updatable)
		&& EquivalenceUtil.areEqual(this.scope, other.scope)
		&& EquivalenceUtil.areEqual(this.invalidation, other.invalidation);
	}
	
	@Override
	public int hashCode() {
		return HashCodeUtil.hashCode(1, scope, ttl, updatable);
	}
	
	public Invalidation getInvalidation() {
		return invalidation;
	}
	
	public void setInvalidation(Invalidation invalidation) {
		this.invalidation = invalidation;
	}

}
