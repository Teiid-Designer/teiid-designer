/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.core;

import java.text.MessageFormat;

/**
 * 
 */
public class I18n {

    public static String format( final String template,
                                 final Object... parameters ) {
        if (parameters == null || parameters.length == 0) throw new IllegalArgumentException(
                                                                                             "At least one parameter must be supplied"); //$NON-NLS-1$
        return MessageFormat.format(template, parameters);
    }

    private I18n() {
    }
}
