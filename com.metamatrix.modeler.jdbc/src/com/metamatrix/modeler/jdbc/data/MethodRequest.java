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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;

import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.core.util.ReflectionHelper;
import com.metamatrix.modeler.internal.jdbc.JdbcUtil;
import com.metamatrix.modeler.jdbc.JdbcPlugin;

/**
 * MethodRequest
 */
public class MethodRequest extends Request {
    
    private final String methodName;
    private final Object[] params;

    /**
     * Construct an instance of MethodRequest.
     * 
     */
    public MethodRequest( final String name, final Object target, final String methodName, final Object[] params ) {
        super(name,target);
        ArgCheck.isNotNull(name);
        ArgCheck.isNotZeroLength(name);
        ArgCheck.isNotNull(target);
        ArgCheck.isNotNull(methodName);
        ArgCheck.isNotZeroLength(methodName);
        ArgCheck.isNotNull(params);
        this.methodName = methodName;
        this.params = params;
    }
    
    public String getMethodName() {
        return this.methodName;
    }
    
    public Object[] getParameters() {
        return this.params;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.internal.jdbc.Request#performInvocation(java.sql.Connection, com.metamatrix.modeler.internal.jdbc.Response)
     */
    @Override
    protected IStatus performInvocation(final Response results) {
        final Object target = getTarget();
        final List statuses = new ArrayList();
        Method bestMethod = null;

        // Find the method on the target ...
        final ReflectionHelper helper = new ReflectionHelper(target.getClass());
        ResultSet methodResultSet = null;
        try {
            bestMethod = helper.findBestMethodOnTarget(this.methodName,this.params);
            final Object methodResult = bestMethod.invoke(target,this.params);

            // If the statement resulted in a ResultSet ...
            if ( methodResult instanceof ResultSet ) {
                methodResultSet = (ResultSet)methodResult;
                Response.addResults(results,methodResultSet,this.isMetadataRequested());
            } else {
                Response.addResults(results,methodResult,this.isMetadataRequested());
            }
        } catch (InvocationTargetException e) {
            final Throwable actualException = e.getTargetException();
            statuses.add(JdbcUtil.createIStatus(actualException,actualException.getLocalizedMessage()));
        } catch (Throwable e) {
            final String methodSignature = bestMethod != null ? bestMethod.toString() :
                                                                target.getClass().getName() + '.' + methodName;
            final String text = JdbcPlugin.Util.getString("MethodRequest.Error_while_invoking_method",methodSignature,target); //$NON-NLS-1$
            statuses.add(JdbcUtil.createIStatus(e,text));
        } finally {
            if ( methodResultSet != null ) {
                try {
                    methodResultSet.close();
                } catch (SQLException e1) {
                }
            }
        }
        
        // Process the status(es) that may have been created due to problems/warnings
        if ( statuses.size() == 1 ) {
            return (IStatus)statuses.get(0);
        }
        if ( statuses.size() > 1 ) {
            final String methodSignature = bestMethod != null ? bestMethod.toString() :
                                                                target.getClass().getName() + '.' + methodName;
            final String text = JdbcPlugin.Util.getString("MethodRequest.Error_while_processing_method",methodSignature,target); //$NON-NLS-1$
            return JdbcUtil.createIStatus(statuses,text);
        }
        
        // If there are no errors, return null
        return null;
    }
    
}
