/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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
