/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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
