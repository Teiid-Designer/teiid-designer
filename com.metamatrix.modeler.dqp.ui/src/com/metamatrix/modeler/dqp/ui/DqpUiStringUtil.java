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

package com.metamatrix.modeler.dqp.ui;



/** 
 * @since 4.3
 */
public class DqpUiStringUtil {

    public static String getString(final String i18n_prefix, final String id) {
        return getString(i18n_prefix + id);
    }
    
    public static String getString(final String i18n_prefix, final String id, Object obj) {
        return getString(i18n_prefix + id, obj);
    }
    
    public static String getString(final String id, Object obj) {
        return DqpUiConstants.UTIL.getString(id, obj);
    }
    
    public static String getString(final String id) {
        return DqpUiConstants.UTIL.getString(id);
    }

}
