/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.jdbc.data;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.core.util.ReflectionHelper;
import org.teiid.designer.jdbc.JdbcPlugin;
import org.teiid.designer.jdbc.JdbcUtil;

/**
 * MethodRequest
 *
 * @since 8.0
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
        CoreArgCheck.isNotNull(name);
        CoreArgCheck.isNotZeroLength(name);
        CoreArgCheck.isNotNull(target);
        CoreArgCheck.isNotNull(methodName);
        CoreArgCheck.isNotZeroLength(methodName);
        CoreArgCheck.isNotNull(params);
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
     * @see org.teiid.designer.jdbc.Request#performInvocation(java.sql.Connection, org.teiid.designer.jdbc.Response)
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
