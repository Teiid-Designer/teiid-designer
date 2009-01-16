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

package com.metamatrix.modeler.jdbc.data;

import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;

import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.modeler.internal.jdbc.JdbcUtil;
import com.metamatrix.modeler.internal.jdbc.data.ResultsImpl;
import com.metamatrix.modeler.jdbc.JdbcPlugin;

/**
 * Request
 */
public abstract class Request {
    
    private Results results;
    
    /**
     * The response for the request; may be null if the request has not yet been invoked
     * or if the request has been invoked but problems were encountered.
     */
    private Response response;

    /**
     * The problems that were encountered during invocation of the request; will be null
     * prior to invocation, and may be null if no problems were encountered upon invocation
     */
    private IStatus problems;
    
    /**
     * The logical name of the request.
     */
    private final String name;
    
    /**
     * The target to which the invocation will be made.
     */
    private final Object target;
    
    /**
     * Flag that defines whether result set metadata should be obtained.  Defaults to 'true'.
     */
    private boolean metadataRequested = true;

    /**
     * Construct an instance of Request.
     */
    public Request( final String name, final Object target  ) {
        super();
        ArgCheck.isNotNull(target);
        this.target = target;
        this.name = name;
    }
    
    /**
     * Return the target of the request to which the invocation will be (or has been) made.
     * @return the target; never null
     */
    public Object getTarget() {
        return target;
    }
    
    /**
     * Invoke this request, and return whether this request was processed
     * without an error
     * @param target of the invocation; either the JDBC {@link Connection} upon which the request
     * should be processed, or a related object (e.g., the {@link java.sql.DatabaseMetaData} reference).
     * @return true if the request completed without any errors or warnings,
     * or false otherwise
     */
    public final boolean invoke() {
        final Response tempResponse = new Response(this);
        this.problems = performInvocation(tempResponse);
        this.response = tempResponse;
        this.results = new ResultsImpl(this.response);
        return this.problems == null;
    }
    
    /**
     * Return the name of this request, which generally is a logical name.
     * @return the request's name; may be null if there is no name
     */
    public String getName() {
        return this.name;
    }
    
    @Override
    public String toString() {
        return this.name;
    }

    /**
     * Template method that must be implemented by subclasses and that is called
     * upon the {@link #invoke()} method.
     * @param target of the invocation; either the JDBC {@link Connection} upon which the request
     * should be processed, or a related object (e.g., the {@link java.sql.DatabaseMetaData} reference).
     * @param response the object into which the response should be placed; never null
     * @return the IStatus with any warnings or errors that may have happened
     * (including any exceptions that may have occurred, or null if the invocation
     * was performed without any problems
     */
    protected abstract IStatus performInvocation( final Response results );
    
    /**
     * Return whether this request has any response.  The response are removed upon {@link #clear()}
     * and are (re)created only upon {@link #invoke()}. 
     * @return true if there are response, or false if there are no response.
     */
    protected boolean hasResponse() {
        return this.response != null;
    }
    
    /**
     * Return the response for this request.  The response are removed upon {@link #clear()}
     * and are (re)created only upon {@link #invoke()}. 
     * @return the response; will be null if {@link #hasResponse()} returns false
     */
    protected Response getResponse() {
        return this.response;
    }
    
    /**
     * Return the results for this request.  The response are removed upon {@link #clear()}
     * and are (re)created only upon {@link #invoke()}. 
     * @return the response; will be null if {@link #hasResults()} returns false
     */
    public Results getResults() {
        return this.results;
    }
    
    /**
     * Return whether this request has any response.  The response are removed upon {@link #clear()}
     * and are (re)created only upon {@link #invoke()}. 
     * @return true if there are results, or false if there are no response.
     */
    public boolean hasResults() {
        return this.results != null;
    }
    
    /**
     * Return whether this request has any problems.  The problems are removed upon {@link #clear()},
     * and may be encountered upon {@link #invoke()}. 
     * @return true if there are problems, or false if there are no problems.
     */
    public boolean hasProblems() {
        return this.problems != null;
    }
    
    /**
     * Return the problems for this request, if any.  The problems are removed upon {@link #clear()}
     * and may be encountered upon {@link #invoke()}. 
     * @return the problems; will be null if {@link #hasProblems()} returns false
     */
    public IStatus getProblems() {
        return this.problems;
    }
    
    /**
     * Method to add {@link IStatus} problems to any existing problems.
     * @param istatuses the list of {@link IStatus} instances
     */
    public void addProblems( final List istatuses ) {
        if ( istatuses == null || istatuses.isEmpty() ) {
            return;
        }
        if ( this.problems == null ) {
            // Process the status(es) that may have been created due to problems/warnings
            if ( istatuses.size() == 1 ) {
                this.problems = (IStatus)istatuses.get(0);
            }
            if ( istatuses.size() > 1 ) {
                final String text = JdbcPlugin.Util.getString("Request.Request.MultipleProblems"); //$NON-NLS-1$
                this.problems = JdbcUtil.createIStatus(istatuses,text);
            }
        } else {
            // There are existing problems, so add to them
            if ( this.problems instanceof MultiStatus ) {
                final MultiStatus multiStatus = (MultiStatus)this.problems;
                final String text = JdbcPlugin.Util.getString("Request.Request.MultipleProblems"); //$NON-NLS-1$
                final IStatus newProblems = JdbcUtil.createIStatus(istatuses,text);
                multiStatus.addAll(newProblems);
                this.problems = multiStatus;
            } else {
                // Single Status ...
                istatuses.add(0,this.problems);
                final String text = JdbcPlugin.Util.getString("Request.Request.MultipleProblems"); //$NON-NLS-1$
                this.problems = JdbcUtil.createIStatus(istatuses,text);
            }
        }
    }
    
    /**
     * Clear the problems and response for this request.  This can be done prior to re-issuing
     * the {@link #invoke() invocation} of this request.  Calling this method has no effect
     * when there are no problems or response.
     */
    public void clear() {
        this.response = null;
        this.problems = null;
        this.results = null;
    }

    public boolean isMetadataRequested() {
        return this.metadataRequested;
    }
    
    public void setMetadataRequested(boolean metadataRequested) {
        this.metadataRequested = metadataRequested;
    }
}
