/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.xml;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.ui.views.navigator.ResourcePatternFilter;

//This filter is to hide project items from the Browse Workspace Dialog.
//There is a duplicate class in the modelgenerator.wsdl.ui package.
//At some time these should be moved to a common library, but it's not worth it
//for this tiny amount of code.

public class XMLExtensionsFilter extends ViewerFilter {

    private ResourcePatternFilter filter;

    private static String[] patterns;

    static {
        patterns = new String[] {new String("XML*Extensions.xmi"), //$NON-NLS-1$
            new String(".project") //$NON-NLS-1$
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
