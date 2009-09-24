/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.xml.namespace;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.metamatrix.metamodels.xml.XmlElement;
import com.metamatrix.metamodels.xml.XmlNamespace;

/**
 * NamespaceContext
 */
public class NamespaceContext {
    
    /**
     * Control that defines whether URIs should be compared using case sensitive matching.
     * See {@link http://www.w3.org/TR/1999/REC-xml-names-19990114/#dt-identical} for the
     * specification.  Particularly, "URI references which identify namespaces are considered
     * identical when they are exactly the same character-for-character." 
     */
    private static final boolean URI_MATCHING_IS_CASE_SENSITIVE = true;
    
    private final List nsDeclarations;
    private final NamespaceContext inherited;
    private final XmlElement element;
    
//    /**
//     * This is sort of a backwards way to do this; should be removed once the schema models can correctly
//     * store the relevant information.  RMH 6/16/03
//     */
//    private SchemaIncludeMap schemaIncludeMap;
//
    /**
     * Construct an instance of NamespaceContext.  This is package-level so that it is accessible
     * to test cases only
     */
    public NamespaceContext( final XmlElement element, final NamespaceContext inheritedNS ) {
        super();
        this.element = element;
        this.inherited = inheritedNS;
        this.nsDeclarations = this.element.getDeclaredNamespaces();
//        this.schemaIncludeMap = includeMap;
    }
    
//    /**
//     * Return the schema include map that can be used to determine what the actual target namespace
//     * is for a schema component.
//     * @return
//     * @since 3.1SP2
//     */
//    public SchemaIncludeMap getSchemaIncludeMap() {
//        if ( this.schemaIncludeMap != null ) {
//            return this.schemaIncludeMap;
//        }
//        if ( this.inherited != null ) {
//            return this.inherited.getSchemaIncludeMap();
//        }
//        return null;
//    }
//
    /**
     * @return the list of {@link XmlNamespace} instances declared in this set; never null
     */
    public List getXmlNamespaces() {
        return nsDeclarations;
    }
    
    /**
     * @return
     */
    public NamespaceContext getInherited() {
        return inherited;
    }

    /**
     * @return
     */
    public XmlElement getXmlElement() {
        return this.element;
    }

    /**
     * @return the list of {@link XmlNamespace} instances declared in this set, including those
     * that are inherited; never null
     */
    public List getAllXmlNamespaces() {
        final List results = new LinkedList(nsDeclarations);
        if ( this.inherited != null ) {
            results.addAll( this.inherited.getAllXmlNamespaces() );
        }
        return results;
    }
    
    /**
     * Add the supplied {@link XmlNamespace} to this set as long as there is no conflict.
     * @param ns the namespace declaration to be added
     * @return true if the namespace declaration is non-null and could be added without a conflict,
     * or false otherwise
     */
    public boolean addXmlNamespace( final XmlNamespace ns ) {
        if ( ns != null ) {
            // See if there is already a declaration that conflicts ...
            final Iterator iter = this.nsDeclarations.iterator();
            while (iter.hasNext()) {
                final XmlNamespace existingNS = (XmlNamespace)iter.next();
                // Considered a conflict if
                // - there is already a namespace with the same prefix on this owner
                if ( existingNS.getPrefix() != null && existingNS.getPrefix().equals(ns.getPrefix()) ) {
                    return false;
                }
                // - if there is already a namespace with the same (case-insensitive) non-empty URI
                final String existingUri = existingNS.getUri();
                if ( existingUri.length() != 0 && existingUri.equalsIgnoreCase(ns.getUri()) ) {
                    return false;
                }
                
            }
            
            // Add the namespace declaration to the XML Element's list ...
            return this.nsDeclarations.add(ns);
        }
        return false;
    }
//    
//    /**
//     * Helper method that adds a namespace declaration, and is equivalent to the call:
//     * <code>
//     *   return this.addDeclaredXmlNamespace( new XmlNamespace(prefix,uri) );
//     * </code>
//     * @param prefix the prefix for the namespace declaration
//     * @param uri the URI of the referenced schema
//     * @return true if the namespace declaration is non-null and could be added without a conflict,
//     * or false otherwise
//     */
//    public XmlNamespace declareNamespace( final String prefix, final String uri ) {
//        final XmlNamespace ns = new XmlNamespace(prefix,uri);
//        if ( this.addDeclaredXmlNamespace(ns) ) {
//            return ns;
//        }
//        return null;
//    }
//
//
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        if ( this.element != null ) {
            sb.append( this.element.getName() );
        }
        sb.append(" ("); //$NON-NLS-1$
        boolean first = true;
        final Iterator iter = this.nsDeclarations.iterator();
        while (iter.hasNext()) {
            final XmlNamespace ns = (XmlNamespace)iter.next();
            if ( !first ) {
                sb.append(","); //$NON-NLS-1$
            }
            sb.append(ns.getPrefix());
            sb.append(":"); //$NON-NLS-1$
            sb.append(ns.getUri());
            first = false;
        }
        sb.append(")"); //$NON-NLS-1$
        return sb.toString();
    }

    /**
     * Traverse up the stack and find the <i>first</i> namespace declaration that matches the supplied URI.
     * A namespace declaration is considered a match if
     * <ul>
     *   <li>The namespace URI is a <i>case-insensitive</i> match</li>
     *   <li>The prefix for the best match is not reused lower in the stack (e.g., lower in the document)</li>
     * </ul>
     * @param uri the URI to be matched
     * @return the XmlNamespace object that is a match, or null if there is no XmlNamespace
     * that matches the URI.
     */
    public XmlNamespace getBestNamespace( final String uri ) {
        final Set prefixesFound = new HashSet();
        return getBestNamespace(uri,prefixesFound);
    }
    
    /**
     * This method does the real work
     * @param uri
     * @param prefixesFound
     * @return
     */
    protected XmlNamespace getBestNamespace( final String uri, final Set prefixesFound ) {
        final Iterator iter = this.nsDeclarations.iterator();
        while (iter.hasNext()) {
            final XmlNamespace ns = (XmlNamespace)iter.next();
            boolean urisMatch = false;
            if ( URI_MATCHING_IS_CASE_SENSITIVE ) {
                urisMatch = ns.getUri().equals(uri);
            } else {
                urisMatch = ns.getUri().equalsIgnoreCase(uri);
            }
            if ( urisMatch ) {
                // There is a match, so see if the ns's prefix was seen lower than this
                if ( !prefixesFound.contains(ns.getPrefix())) {
                    // The prefix was not seen so far, so this is a good match!
                    return ns;
                }
                // The prefix was overridden, so the matched namespace cannot be used; just continue
            }
            prefixesFound.add(ns.getPrefix());
        }
        if ( this.inherited != null ) {
            return this.inherited.getBestNamespace(uri,prefixesFound);
        }
        return null;
    }

//    /**
//     * Looks for an prefix that is unused, starting with a specified prefix.
//     * @return
//     */
//    public String findUnusedPrefix( final String startingPrefix ) {
//        String prefix = startingPrefix;
//        int counter = 0;
//        
//        // As long as there is a matching prefix
//        while ( hasMatchingPrefix(prefix) ) {
//            // Change the prefix ...
//            prefix = startingPrefix + (++counter);
//        }
//        return prefix;
//    }
//    
//    protected boolean hasMatchingPrefix( final String prefix ) {
//        final Iterator iter = this.nsDeclarations.iterator();
//        while (iter.hasNext()) {
//            final XmlNamespace ns = (XmlNamespace)iter.next();
//            if ( ns.getPrefix().equalsIgnoreCase(prefix) ) {
//                return true;
//            }
//        }
//        if ( this.inherited != null ) {
//            return this.inherited.hasMatchingPrefix(prefix);
//        }
//        return false;
//    }
    
}
