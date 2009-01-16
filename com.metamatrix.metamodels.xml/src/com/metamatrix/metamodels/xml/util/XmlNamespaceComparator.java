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

package com.metamatrix.metamodels.xml.util;

import java.util.Comparator;

import com.metamatrix.metamodels.xml.XmlDocumentPlugin;
import com.metamatrix.metamodels.xml.XmlNamespace;

/**
 * XmlNamespaceComparator
 */
public class XmlNamespaceComparator implements Comparator {

    /**
     * Construct an instance of XmlNamespaceComparator.
     * 
     */
    public XmlNamespaceComparator() {
        super();
    }

    /**
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare(final Object o1, final Object o2) {
        if ( o1 == o2 ) {
            return 0;
        }
        if ( o1 == null ) {
            if ( o2 == null ) {
                return 0;
            }
            return -1;
        }
        if ( o2 == null ) {
            return 1;
        }
        // Both are non-null
        if ( o1 instanceof XmlNamespace && o2 instanceof XmlNamespace ) {
            final XmlNamespace n1 = (XmlNamespace)o1;
            final XmlNamespace n2 = (XmlNamespace)o2;
            final String prefix1 = n1.getPrefix();
            final String prefix2 = n2.getPrefix();
            if ( prefix1 == null ) {
                if ( prefix2 != null ) {
                    return -1;  // no prefix is less than prefix
                }
                // Otherwise, prefix 2 is also null
            }
            if ( prefix2 == null ) {
                if ( prefix1 != null ) {
                    return 1;  // no prefix is less than prefix
                }
            }
            // Either both prefixes are null, or they are both non-null, so use the URI
            final String uri1 = n1.getUri();
            final String uri2 = n2.getUri();
            if ( uri1 == null ) {
                if ( uri2 != null ) {
                    return -1;
                }
                // both are null
                return 0;
            }
            if ( uri2 == null ) {
                return 1;
            }
            return uri1.compareTo(uri2);
        }
        final Object[] params = new Object[]{o1.getClass(),o2.getClass()};
        final String msg = XmlDocumentPlugin.Util.getString("XmlNamespaceComparator.Unable_to_compare_instances",params); //$NON-NLS-1$
        throw new ClassCastException(msg);
    }

}
