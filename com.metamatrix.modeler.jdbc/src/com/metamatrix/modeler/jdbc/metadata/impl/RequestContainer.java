/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */

package com.metamatrix.modeler.jdbc.metadata.impl;

import com.metamatrix.modeler.jdbc.data.Request;

/**
 * ResultsContainer
 */
public class RequestContainer {

    private final Request[] requests;
    private String[] requestNames;

    /**
     * Construct an instance of ResultsContainer.
     */
    public RequestContainer( final Request[] requests ) {
        super();
        this.requests = (requests != null ? requests : new Request[] {});
    }

    public synchronized String[] getNamesOfResults() {
        if (requestNames == null) {
            final String[] names = new String[requests.length];
            for (int i = 0; i < requests.length; ++i) {
                final Request request = requests[i];
                names[i] = request.getName();
            }
            requestNames = names;
        }
        return requestNames;
    }

    /**
     * Returns a {@link Request} with the supplied name. If the request has not yet been invoked, this method
     * {@link Request#invoke() invokes} the request.
     * 
     * @param name the name of the request
     * @return the Request, or null if there is no Request with that name ( see {@link #getNamesOfResults()}).
     */
    public Request getRequest( final String name ) {
        return getRequest(name, true);
    }

    /**
     * Returns a {@link Request} with the supplied name. If the request has not yet been invoked, this method
     * {@link Request#invoke() invokes} the request.
     * 
     * @param name the name of the request
     * @return the Request, or null if there is no Request with that name ( see {@link #getNamesOfResults()}).
     */
    public Request getRequest( final String name,
                               final boolean includeMetadata ) {
        for (int i = 0; i < requests.length; ++i) {
            final Request request = requests[i];
            if (request.getName().equals(name)) {
                // If the request doesn't have results or errors, then invoke the request
                if (!request.hasResults() && !request.hasProblems()) {
                    request.setMetadataRequested(includeMetadata);
                    request.invoke();
                }
                return request;
            }
        }
        return null;
    }

}
