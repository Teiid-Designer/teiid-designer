/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.salesforce.ui.util;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.ui.views.navigator.ResourcePatternFilter;

public class XMLExtensionsFilter extends ViewerFilter {

    private ResourcePatternFilter filter;

    private static String[] patterns;

    static {
        patterns = new String[] {"XML*Extensions.xmi", //$NON-NLS-1$
            ".project" //$NON-NLS-1$
        };
    }

    public XMLExtensionsFilter() {
        filter = new ResourcePatternFilter();
        filter.setPatterns(patterns);
    }

    @Override
    public boolean select( Viewer arg0,
                           Object arg1,
                           Object arg2 ) {
        return filter.select(arg0, arg1, arg2);
    }
}
