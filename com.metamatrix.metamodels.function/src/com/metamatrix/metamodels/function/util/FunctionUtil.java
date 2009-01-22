/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.function.util;

import java.util.Iterator;

import com.metamatrix.metamodels.function.FunctionParameter;
import com.metamatrix.metamodels.function.ReturnParameter;
import com.metamatrix.metamodels.function.ScalarFunction;

/**
 * FunctionUtil
 */
public class FunctionUtil {

    private FunctionUtil() {
        super();
    }
    
    public static String getSignature( final ScalarFunction function ) {
        final String name = function.getName();
        final StringBuffer sb = new StringBuffer();
        sb.append(name);
        
        // Add the parameters ...
        sb.append('(');
        boolean isFirst = true;
        final Iterator iter = function.getInputParameters().iterator();
        while (iter.hasNext()) {
            final FunctionParameter param = (FunctionParameter)iter.next();
            if ( !isFirst ) {
                sb.append(',');
            }
            final String paramSig = getSignature(param);
            sb.append(paramSig);
            isFirst = false;
        }
        sb.append(')');
        
        // Add the return parameter ...
        final ReturnParameter returnParam = function.getReturnParameter();
        if ( returnParam == null ) {
            sb.append(':');
            sb.append("void"); //$NON-NLS-1$
        } else {
            sb.append(':');
            final String paramSig = getSignature(returnParam);
            sb.append(paramSig);
        }
        
        return sb.toString();
    }
    
    public static String getSignature( final FunctionParameter param ) {
        final String type = param.getType();
        return type;
    }

    public static String getSignature( final ReturnParameter param ) {
        final String type = param.getType();
        return type;
    }

    public static String getValidationSignature( final ScalarFunction function ) {
        final String name = function.getInvocationClass();
        final StringBuffer sb = new StringBuffer();
        sb.append(name);
        sb.append('#');
        sb.append(function.getInvocationMethod());
        
        // Add the parameters ...
        sb.append('(');
        boolean isFirst = true;
        final Iterator iter = function.getInputParameters().iterator();
        while (iter.hasNext()) {
            final FunctionParameter param = (FunctionParameter)iter.next();
            if ( !isFirst ) {
                sb.append(',');
            }
            final String paramSig = getValidationSignature(param);
            sb.append(paramSig);
            isFirst = false;
        }
        sb.append(')');
        
        // Add the return parameter ...
        final ReturnParameter returnParam = function.getReturnParameter();
        if ( returnParam == null ) {
            sb.append(':');
            sb.append("void"); //$NON-NLS-1$
        } else {
            sb.append(':');
            final String paramSig = getValidationSignature(returnParam);
            sb.append(paramSig);
        }
        
        return sb.toString();
    }
    
    public static String getValidationSignature( final FunctionParameter param ) {
        final String type = param.getType();
        return type;
    }

    public static String getValidationSignature( final ReturnParameter param ) {
        final String type = param.getType();
        return type;
    }

}
