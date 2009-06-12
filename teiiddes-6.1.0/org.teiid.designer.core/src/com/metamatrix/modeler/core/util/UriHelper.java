/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.util;

import java.io.File;

import org.eclipse.emf.common.util.URI;

import com.metamatrix.core.id.UUID;
import com.metamatrix.core.util.ArgCheck;


/** 
 * @since 5.0
 */
public class UriHelper {
    
    // ==================================================================================
    //                        C O N S T R U C T O R S
    // ==================================================================================

    /** 
     * @since 5.0
     */
    private UriHelper() {
        super();
    }
    
    // ==================================================================================
    //                      P U B L I C   M E T H O D S
    // ==================================================================================
    
    public static URI makeAbsoluteUri(final URI baseUri, final URI theImportUri) {
        ArgCheck.isNotNull(baseUri);
        ArgCheck.isNotNull(theImportUri);
        String importUriString = URI.decode(theImportUri.toString());
        
        // If the import URI represents a remote URL then return it unaltered
        if (importUriString.startsWith("http:")) { //$NON-NLS-1$
            return theImportUri;
        }
        
        // If the import URI represents a UUID then return it unaltered
        if (importUriString.startsWith(UUID.PROTOCOL)) {
            return theImportUri;
        }
       
        // Take the import URI and resolve it against the base URI
        URI importUri = theImportUri;
        if (baseUri.isHierarchical() && !baseUri.isRelative() && importUri.isRelative()) {
            importUri = importUri.resolve(baseUri);
        }
        
        return importUri;
    }
    
    public static URI makeAbsoluteUri(final URI baseUri, final String theImportUriString) {
        ArgCheck.isNotNull(baseUri);
        ArgCheck.isNotNull(theImportUriString);
        String importUriString = URI.decode(theImportUriString);
        
        // If the import URI represents a remote URL then return it unaltered
        if (importUriString.startsWith("http:")) { //$NON-NLS-1$
            return URI.createURI(importUriString);
        }
        
        // If the import URI represents a UUID then return it unaltered
        if (importUriString.startsWith(UUID.PROTOCOL)) {
            return URI.createURI(importUriString);
        }

        // If the import URI represents a file-type absolute URI then return it 
        // after removing the "file:" prefix
        if (importUriString.startsWith("file:")) { //$NON-NLS-1$
            return URI.createURI(importUriString);
        }
       
        // Take the import URI and resolve it against the base URI
        return makeAbsoluteUri(baseUri,URI.createFileURI(importUriString));
    }
    
    public static String makeAbsolutePath(final URI baseUri, final URI theImportUri) {
        ArgCheck.isNotNull(baseUri);
        ArgCheck.isNotNull(theImportUri);
        String importUriString = URI.decode(theImportUri.toString());
                
        // If the import URI represents a remote URL then return it unaltered
        if (importUriString.startsWith("http:")) { //$NON-NLS-1$
            return importUriString;
        }
        
        // If the import URI represents a UUID then return it unaltered
        if (importUriString.startsWith(UUID.PROTOCOL)) {
            return importUriString;
        }
        
        URI importUri = makeAbsoluteUri(baseUri,theImportUri);
        
        // Normalize the format of the returned string
        File f = new File(importUri.toFileString());
        return f.getAbsolutePath();
    }
    
    public static String makeAbsolutePath(final URI baseUri, final String theImportUriString) {
        ArgCheck.isNotNull(baseUri);
        ArgCheck.isNotNull(theImportUriString);
        return makeAbsolutePath(baseUri.toFileString(),theImportUriString);
    }
    
    public static String makeAbsolutePath(final String theBaseUriString, final String theImportUriString) {
        ArgCheck.isNotNull(theBaseUriString);
        ArgCheck.isNotNull(theImportUriString);
        String baseUriString   = URI.decode(theBaseUriString);
        String importUriString = URI.decode(theImportUriString);
        
        // Assume that the base URI string represents the absolute path to a file
        URI baseUri = URI.createFileURI(baseUriString);
        
        // If the import URI represents a remote URL then return it unaltered
        if (importUriString.startsWith("http:")) { //$NON-NLS-1$
            return importUriString;
        }
        
        // If the import URI represents a UUID then return it unaltered
        if (importUriString.startsWith(UUID.PROTOCOL)) {
            return importUriString;
        }
        
        // If the import URI represents a file-type absolute URI then return it 
        // after removing the "file:" prefix
        if (importUriString.startsWith("file:")) { //$NON-NLS-1$
            return URI.createURI(importUriString).toFileString();
        }
       
        // Take the import URI and resolve it against the base URI
        URI importUri = URI.createFileURI(importUriString);
        if (baseUri.isHierarchical() && !baseUri.isRelative() && importUri.isRelative()) {
            importUri = importUri.resolve(baseUri);
        }
        
        // Normalize the format of the returned string
        File f = new File(importUri.toFileString());
        return f.getAbsolutePath();
    }
    
    public static URI makeRelativeUri(final URI baseUri, final URI theImportUri) {
        ArgCheck.isNotNull(baseUri);
        ArgCheck.isNotNull(theImportUri);
        String importUriString = URI.decode(theImportUri.toString());
        
        // If the import URI represents a remote URL then return it unaltered
        if (importUriString.startsWith("http:")) { //$NON-NLS-1$
            return theImportUri;
        }
        
        // If the import URI represents a UUID then return it unaltered
        if (importUriString.startsWith(UUID.PROTOCOL)) {
            return theImportUri;
        }
       
        // Take the import URI and resolve it against the base URI
        URI importUri = theImportUri;
        if (importUri.isFile() && !importUri.isRelative()) {
            if (baseUri != null && !baseUri.isRelative() && baseUri.isHierarchical()) {
                URI deresolvedURI = importUri.deresolve(baseUri, true, true, false);
                if (deresolvedURI.hasRelativePath()) {
                    importUri = deresolvedURI;
                }
            }
        } else {
            importUri = URI.createURI(importUriString);
        }
        
        return importUri;
    }
    
    public static URI makeRelativeUri(final URI baseUri, final String theImportUriString) {
        ArgCheck.isNotNull(baseUri);
        ArgCheck.isNotNull(theImportUriString);
        String importUriString = URI.decode(theImportUriString);
        
        // If the import URI represents a remote URL then return it unaltered
        if (importUriString.startsWith("http:")) { //$NON-NLS-1$
            return URI.createURI(importUriString);
        }
        
        // If the import URI represents a UUID then return it unaltered
        if (importUriString.startsWith(UUID.PROTOCOL)) {
            return URI.createURI(importUriString);
        }
        
        // Create the appropriate URI for the import string
        URI importUri = null;
        if (importUriString.startsWith("file:")) { //$NON-NLS-1$
            importUri = URI.createURI(importUriString);
        } else {
            importUri = URI.createFileURI(importUriString);
        }
        
        return makeRelativeUri(baseUri,importUri);
    }
    
    public static String makeRelativePath(final URI baseUri, final URI theImportUri) {
        ArgCheck.isNotNull(baseUri);
        ArgCheck.isNotNull(theImportUri);
        String importUriString = URI.decode(theImportUri.toString());
        
        // If the import URI represents a remote URL then return it unaltered
        if (importUriString.startsWith("http:")) { //$NON-NLS-1$
            return importUriString;
        }
        
        // If the import URI represents a UUID then return it unaltered
        if (importUriString.startsWith(UUID.PROTOCOL)) {
            return importUriString;
        }
        
        URI importUri = makeRelativeUri(baseUri,theImportUri);
        return URI.decode(importUri.toString());
    }
    
    public static String makeRelativePath(final URI baseUri, final String theImportUriString) {
        ArgCheck.isNotNull(baseUri);
        ArgCheck.isNotNull(theImportUriString);
        return makeRelativePath(baseUri.toFileString(),theImportUriString);
    }
    
    public static String makeRelativePath(final String theBaseUriString, final String theImportUriString) {
        ArgCheck.isNotNull(theBaseUriString);
        ArgCheck.isNotNull(theImportUriString);
        String baseUriString   = URI.decode(theBaseUriString);
        String importUriString = URI.decode(theImportUriString);
        
        // Assume that the base URI string represents the absolute path to a file
        URI baseUri = URI.createFileURI(baseUriString);
        
        // If the import URI represents a remote URL then return it unaltered
        if (importUriString.startsWith("http:")) { //$NON-NLS-1$
            return importUriString;
        }
        
        // If the import URI represents a UUID then return it unaltered
        if (importUriString.startsWith(UUID.PROTOCOL)) {
            return importUriString;
        }
        
        // Create the appropriate URI for the import string
        URI importUri = null;
        if (importUriString.startsWith("file:")) { //$NON-NLS-1$
            importUri = URI.createURI(importUriString);
        } else {
            importUri = URI.createFileURI(importUriString);
        }
       
        // Take the import URI and resolve it against the base URI
        if (importUri.isFile() && !importUri.isRelative()) {
            if (baseUri != null && !baseUri.isRelative() && baseUri.isHierarchical()) {
                URI deresolvedURI = importUri.deresolve(baseUri, true, true, false);
                if (deresolvedURI.hasRelativePath()) {
                    importUri = deresolvedURI;
                }
            }
        }
        
        return URI.decode(importUri.toString());
    }

}
