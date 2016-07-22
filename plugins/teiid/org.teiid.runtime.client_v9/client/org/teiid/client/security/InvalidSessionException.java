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

package org.teiid.client.security;

import org.teiid.runtime.client.TeiidClientException;

/**
 *
 */
public class InvalidSessionException extends TeiidClientException {
	private static final long serialVersionUID = 594047711693346844L;
	
	/**
     * No-Arg Constructor
     */
    public InvalidSessionException(  ) {
        super( );
    }
    /**
     * Constructs an instance of the exception with the specified detail message. A detail
     * message is a String that describes this particular exception.
     * @param message the detail message
     */
    public InvalidSessionException(String message) {
    	super(message);
    }
    /**
     * Constructs an instance of the exception with no detail message but with a
     * single exception.
     * @param e the exception that is encapsulated by this exception
     */
    public InvalidSessionException(Throwable e) {
        super(e);
    }
    /**
     * Constructs an instance of the exception with the specified detail message
     * and a single exception. A detail message is a String that describes this
     * particular exception.
     * @param message the detail message
     * @param e the exception that is encapsulated by this exception
     */
    public InvalidSessionException( Throwable e, String message ) {
        super(e, message);
    }
}

