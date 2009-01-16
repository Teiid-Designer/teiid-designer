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
