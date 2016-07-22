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

package org.teiid.runtime.client;



/**
 * Thrown when a query cannot be parsed.  This is most likely due to not 
 * following the Query Parser grammar, which defines how queries are parsed.
 */
public class TeiidClientException extends Exception {
	
	private static final long serialVersionUID = 7565287582917117432L;
	private Throwable throwable;

    /**
     * No-arg constructor required by Externalizable semantics.
     */
    public TeiidClientException() {
        super();
    }

    /**
     * Construct an instance from an exception to chain to this one.
     *
     * @param e An exception to nest within this one
     */
    public TeiidClientException(Throwable e) {
        super(e);
    }

    /**
     * Construct an instance with the message specified.
     *
     * @param message A message describing the exception
     */
    public TeiidClientException( String message ) {
        super( message );
    }

    /**
     * Construct an instance from a message and an exception to chain to this one.
     *
     * @param message A message describing the exception
     * @param e An exception to nest within this one
     */
    public TeiidClientException( Throwable e, String message ) {
        super( message );
        throwable = e;
    }

    
    /**
     * @return nested throwable if present
     */
    public Throwable getParseThrowable() {
		return throwable;
	}

    /**
     * Set the nested throwable
     *
     * @param throwable
     */
    public void setParseThrowable(Throwable throwable) {
		this.throwable = throwable;
	}
}
