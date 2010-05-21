/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package com.metamatrix.common.protocol;

import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 * Base URL Connection Class
 * 
 * @since 4.4
 */
public abstract class MMURLConnection extends URLConnection {
    protected static final String READ = "read"; //$NON-NLS-1$
    protected static final String WRITE = "write"; //$NON-NLS-1$
    protected static final String LIST = "list"; //$NON-NLS-1$
    protected static final String DELETE = "delete"; //$NON-NLS-1$

    public static final String FILE_LIST_SORT = "sort"; //$NON-NLS-1$
    public static final String DATE = "date"; //$NON-NLS-1$
    public static final String ALPHA = "alpha"; //$NON-NLS-1$
    public static final String REVERSEALPHA = "reversealpha"; //$NON-NLS-1$

    private String action = READ;
    private Properties properties = null;

    public MMURLConnection( final URL url ) {
        super(url);

        // If the URL has any query part parse it and handle it.
        final String query = url.getQuery();
        if (query != null) {
            final StringTokenizer st = new StringTokenizer(query, "&"); //$NON-NLS-1$
            while (st.hasMoreTokens()) {
                final String option = st.nextToken();
                final int index = option.indexOf('=');
                if (index != -1) {
                    final String task = option.substring(0, index);
                    final String value = option.substring(index + 1);
                    if (task.equals("action")) action = value; //$NON-NLS-1$
                    else {
                        if (properties == null) properties = new Properties();
                        properties.setProperty(task, value);
                    }
                }
            }
        }
    }

    /**
     * @return action
     */
    protected String getAction() {
        return action;
    }

    /**
     * @return properties
     */
    protected Properties getProperties() {
        return properties;
    }
}
