/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.workspace;

import java.io.File;
import java.io.InputStream;
import org.teiid.core.designer.CoreModelerPlugin;
import org.teiid.core.designer.TeiidDesignerException;
import org.teiid.core.designer.util.FileUtils;
import org.teiid.core.designer.util.StringConstants;
import org.teiid.designer.common.xmi.XMIHeader;
import org.teiid.designer.common.xmi.XMIHeaderReader;
import org.teiid.designer.runtime.spi.ITeiidVdb;

/**
 * @since 8.0
 */
public class ModelFileUtil implements StringConstants {

/**
 * @since 8.0
 */
    public interface XmiHeaderCache {
        XMIHeader getCachedXmiHeader( File resource );

        void setXmiHeaderToCache( File resource,
                                  XMIHeader header );
    }

    public static final String UML_MODEL_URI = "http://www.eclipse.org/uml2/3.0.0/UML"; //$NON-NLS-1$
    public static final String XML_SERVICE_MODEL_URI = "http://www.metamatrix.com/metamodels/XmlService"; //$NON-NLS-1$
    public static final String RELATIONSHIP_MODEL_URI = "http://www.metamatrix.com/metamodels/Relationship"; //$NON-NLS-1$

    private static XmiHeaderCache CACHE;

    public static void setCache( XmiHeaderCache cache ) {
        ModelFileUtil.CACHE = cache;
    }

    /**
     * Return true if the File represents a MetaMatrix model file or an xsd file this method does not check if the file exists in
     * a project with model nature. Returns a false for vdb files.
     * 
     * @param resource The file that may be a model file
     * @return true if it is a ModelFile.
     */
    public static boolean isModelFile( final File resource ) {
        if (resource == null) {
            return false;
        }

        // If the file does not yet exist then the only thing
        // we can do is to check the name and extension.
        if (!resource.exists()) {
            final String extension = FileUtils.getExtension(resource.getAbsolutePath());
            return isModelFileExtension(extension, true);
        }

        // If this is an xsd resource return true
        if (isXsdFile(resource)) {
            return true;
        }

        // If this is an vdb resource return false
        if (isVdbArchiveFile(resource)) {
            return false;
        }

        // If the resource does not have the correct lower-case extension then return false
        if (!XMI.equals(getFileExtension(resource))) {
            return false;
        }

        XMIHeader header = getXmiHeader(resource);
        // If the header is not null then we know the file is, at least,
        // a well formed xml document.
        if (header != null) {
            // If the XMI version for the header is not null, then return
            // false if the file represents an older 1.X model file
            if (header.getXmiVersion() != null && header.getXmiVersion().startsWith("1.")) { //$NON-NLS-1$
                return false;
            }
            
            String uri = header.getPrimaryMetamodelURI();
            
            if( uri == null ) {
            	return false;
            }
            
            if( uri.equalsIgnoreCase(XML_SERVICE_MODEL_URI) || 
            	uri.equalsIgnoreCase(RELATIONSHIP_MODEL_URI) ||
            	uri.equalsIgnoreCase(UML_MODEL_URI) ) {
            	return false;
            }
            // If the UUID for the header is not null, then the file is a
            // MetaMatrix model file containing a ModelAnnotation element.
            if (header.getUUID() != null) {
                return true;
            }
        }

        return false;
    }

    /**
     * Return true if the IResource represents a xsd file.
     * 
     * @param resource The file that may be a xsd file
     * @return true if it is a xsd
     */
    public static boolean isXsdFile( final File resource ) {
        // Check that the resource has the correct lower-case extension
        if (XSD.equals(getFileExtension(resource))) {
            return true;
        }
        return false;
    }

    /**
     * <p>
     * </p>
     * 
     * @since 4.0
     */
    public static boolean isVdbArchiveFile( final File resource ) {
        // Check that the resource has the correct lower-case extension
        if (ITeiidVdb.VDB_EXTENSION.equals(getFileExtension(resource))) {
            return true;
        }
        return false;
    }

    /**
     * Returns the file extension portion of this file, or an empty string if there is none.
     * <p>
     * The file extension portion is defined as the string following the last period (".") character in the file name. If there is
     * no period in the file name, the file has no file extension portion. If the name ends in a period, the file extension
     * portion is the empty string.
     * </p>
     * 
     * @param resource
     * @return the file extension or <code>null</code>
     * @since 4.3
     */
    public static String getFileExtension( final File resource ) {
        if (resource != null) {
            return FileUtils.getExtension(resource);
        }
        return ""; //$NON-NLS-1$
    }

    /**
     * Return true if a file with the specified name and extension represents a MetaMatrix model file.
     * 
     * @param name
     * @param extension
     * @return
     */
    public static boolean isModelFileExtension( final String extension,
                                                boolean caseSensitive ) {
        // Check if the extension is one of the well-known extensions
        // The method assumes the extension is lower-case. Relaxing
        // this assumption may cause the Modeler to work incorrectly.
        if (extension == null) {
            return false;
        }

        final String exten = (caseSensitive ? extension : extension.toLowerCase());
        if (XMI.equals(exten)) {
            return true;
        }
        if (XSD.equals(exten)) {
            return true;
        }
        if (ITeiidVdb.VDB_EXTENSION.equals(exten)) {
            return false;
        }
        return false;
    }

    /**
     * Return the XMIHeader for the specified File or null if the file does not represent a MetaMatrix model file.
     * 
     * @param resource The file of a metamatrix model file.
     * @return The XMIHeader for the model file
     */
    public static XMIHeader getXmiHeader( final File resource ) {
        if (resource != null && resource.isFile() && resource.exists() && resource.canRead()) {
            // check cache
            if (CACHE != null) {
                XMIHeader header = CACHE.getCachedXmiHeader(resource);
                if (header != null) {
                    return header;
                }
            }
            try {
                XMIHeader header = XMIHeaderReader.readHeader(resource);
                // add to cache
                if (CACHE != null) {
                    CACHE.setXmiHeaderToCache(resource, header);
                }
                return header;
            } catch (TeiidDesignerException e) {
                CoreModelerPlugin.Util.log(e);
            } catch (IllegalArgumentException iae) {
                // Swallowing this exception because we're doing all three checks that would produce it.
                // If this exception is caught, it's because the files really were closed/deleted in another thread and this
                // thread didn't know about it.
                // Fixes Defect 22117
            }
        }

        return null;
    }

    /**
     * Return the XMIHeader for the specified inputstream of a model file.
     * 
     * @param resourceStream The inputStream of a metamatrix model file.
     * @return The XMIHeader for the model file
     */
    public static XMIHeader getXmiHeader( final InputStream resourceStream ) {
        if (resourceStream != null) {
            try {
                return XMIHeaderReader.readHeader(resourceStream);
            } catch (TeiidDesignerException e) {
                CoreModelerPlugin.Util.log(e);
            }
        }
        return null;
    }

}
