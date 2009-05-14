/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.util;

import org.eclipse.emf.common.util.URI;

/**
 * EmfUriFinder
 */
public interface UriPathConverter {
    
    /**
     * Resolves the relative URI reference against a <code>base</code> absolute
     * hierarchical URI, returning the resulting absolute URI.  If already
     * absolute, the URI itself is returned. During resolution, empty segments, 
     * self references ("."), and parent references ("..") are interpreted, so that 
     * they can be removed from the path. 
     * <p>
     * Examples are:
     * <li> relativeURI = "./e.xmi" baseURI = "/a/c/d.xmi" resultant = "/a/c/e.xmi"
     * <li> relativeURI = "e.xmi" baseURI = "/a/c/d.xmi" resultant = "/a/c/e.xmi"
     * <li> relativeURI = "../b.xmi" baseURI = "/a/c/d.xmi" resultant = "/a/b.xmi"
     * <li> relativeURI = "../../x/y/z.xmi" baseURI = "/a/c/d.xmi" resultant = "/x/y/z.xmi"
     * </p> 
     * @param relativeURI the URI relative to the base
     * @param baseURI the base URI from which the relative URI is to be interpreted.
     * @exception java.lang.IllegalArgumentException if <code>relativeURI</code> is
     * null.
     */
    URI makeAbsolute(final URI relativeURI, final URI baseURI);
    
    /**
     * Resolves the relative path reference against a <code>base</code> absolute
     * hierarchical path, returning the resulting absolute path.  If already
     * absolute, the path itself is returned. During resolution, empty segments, 
     * self references ("."), and parent references ("..") are interpreted, so that 
     * they can be removed from the path. 
     * <p>
     * Examples are:
     * <li> relativePath = "./e.xmi" basePath = "/a/c/d.xmi" resultant = "/a/c/e.xmi"
     * <li> relativePath = "e.xmi" basePath = "/a/c/d.xmi" resultant = "/a/c/e.xmi"
     * <li> relativePath = "../b.xmi" basePath = "/a/c/d.xmi" resultant = "/a/b.xmi"
     * <li> relativePath = "../../x/y/z.xmi" basePath = "/a/c/d.xmi" resultant = "/x/y/z.xmi"
     * </p> 
     * @param relativePath the path relative to the base
     * @param basePath the base path from which the relative path is to be interpreted.
     * @exception java.lang.IllegalArgumentException if <code>relativeURI</code> is
     * null.
     */
    String makeAbsolute(final String relativePath, final String basePath);
    
    /**
     * Finds the shortest relative or, if necessary, the absolute URI that,
     * when resolved against the given <code>base</code> absolute hierarchical
     * URI using {@link #makeAbsolute(URI,URI)}, will yield this absolute URI.  
     * <p>
     * Examples are:
     * <li> absoluteURI ="/a/c/e.xmi"  baseURI = "/a/c/d.xmi" resultant = "e.xmi"
     * <li> absoluteURI = "/a/b.xmi" baseURI = "/a/c/d.xmi" resultant = "../b.xmi"
     * <li> absoluteURI = "/x/y/z.xmi" baseURI = "/a/c/d.xmi" resultant = "../../x/y/z.xmi"
     * </p> 
     * @param absoluteURI the absolute URI
     * @param baseURI the base URI from which the relative URI is to be created.
     * @exception java.lang.IllegalArgumentException if <code>absoluteURI</code> is null
     */
    URI makeRelative(final URI absoluteURI, final URI baseURI);
    
    /**
     * Finds the shortest relative or, if necessary, the absolute path that,
     * when resolved against the given <code>base</code> absolute hierarchical
     * path using {@link #makeAbsolute(String,String)}, will yield this absolute path.  
     * <p>
     * Examples are:
     * <li> absolutePath ="/a/c/e.xmi"  basePath = "/a/c/d.xmi" resultant = "e.xmi"
     * <li> absolutePath = "/a/b.xmi" basePath = "/a/c/d.xmi" resultant = "../b.xmi"
     * <li> absolutePath = "/x/y/z.xmi" basePath = "/a/c/d.xmi" resultant = "../../x/y/z.xmi"
     * </p> 
     * @param absolutePath the absolute path
     * @param basePath the base path from which the relative path is to be created.
     * @exception java.lang.IllegalArgumentException if <code>absolutePath</code> is null
     */
    String makeRelative(final String absolutePath, final String basePath);

}
