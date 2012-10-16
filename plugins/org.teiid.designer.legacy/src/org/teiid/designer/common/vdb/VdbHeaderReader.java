/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package org.teiid.designer.common.vdb;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;
import org.teiid.core.designer.TeiidDesignerException;
import org.teiid.core.designer.TeiidDesignerRuntimeException;
import org.teiid.core.designer.util.CoreArgCheck;

/**
 * @since 8.0
 */
public class VdbHeaderReader {

    private static final String XML_DECLARATION_PREFIX_STRING = "<?xml version="; //$NON-NLS-1$
    private static final String VDB_FILE_EXTENSION = ".vdb"; //$NON-NLS-1$
    private static final String MANIFEST_MODEL_NAME = "MetaMatrix-VdbManifestModel.xmi"; //$NON-NLS-1$
    private static final String LOWER_CASE_MANIFEST_MODEL_NAME = MANIFEST_MODEL_NAME.toLowerCase();

    // ==================================================================================
    // P U B L I C M E T H O D S
    // ==================================================================================

    public static VdbHeader readHeader( final File file ) throws TeiidDesignerException {
        if (file != null && file.isFile() && file.exists() && file.length() > 0) {
            final String lowerCaseFileName = file.getName().toLowerCase();

            // If the java.io.File represents a VDB archive file
            if (lowerCaseFileName.endsWith(VDB_FILE_EXTENSION)) {

                ZipFile zipFile = null;
                InputStream iStream = null;
                VdbHeader header = null;
                try {
                    zipFile = new ZipFile(file);
                    iStream = getManifestStreamFromVdbArchive(zipFile);
                    // If the zip is empty or does not contain a manifest model return null;
                    if (iStream == null) {
                        return null;
                    }
                    VdbHeaderReader reader = new VdbHeaderReader();
                    header = reader.read(iStream);
                } catch (IOException e) {
                    throw new TeiidDesignerRuntimeException(e);
                } finally {
                    if (iStream != null) {
                        try {
                            iStream.close();
                        } catch (IOException err) {
                            // do nothing
                        }
                    }
                    if (zipFile != null) {
                        try {
                            zipFile.close();
                        } catch (IOException e) {
                            // do nothing
                        }
                    }
                }
                return header;
            }

            // Else if the java.io.File is the "MetaMatrix-VdbManifestModel.xmi" itself, then read it
            else if (LOWER_CASE_MANIFEST_MODEL_NAME.equals(lowerCaseFileName)) {
                VdbHeaderReader reader = new VdbHeaderReader();
                return reader.read(file);
            }
        }

        // Otherwise return null since we don't know how to process this file
        return null;
    }

    /**
     * Read only the <XMI.header> section of the file and return the <code>VdbHeader</code> object representing its contents
     * 
     * @param istream the InputStream from which we read the header
     * @return the VdbHeader object representing the contents of this section
     * @throws TeiidDesignerException if there is an error reading from the stream
     */
    private VdbHeader read( InputStream istream ) throws TeiidDesignerException {
        if (istream == null) {
            // TODO: ADD I18n ??
            final String msg = "VdbHeaderReader.The_InputStream_reference_may_not_be_null._1"; //$NON-NLS-1$
            throw new IllegalArgumentException(msg);
        }

        DefaultHandler handler = new TerminatingVdbHeaderContentHandler();
        try {
            Thread.currentThread().setContextClassLoader(VdbHeaderReader.class.getClassLoader());
            SAXParserFactory spf = SAXParserFactory.newInstance();
            spf.setNamespaceAware(true);
            SAXParser parser = spf.newSAXParser();
            parser.parse(new InputSource(istream), handler);
        } catch (SAXException e) {
            if (TerminatingVdbHeaderContentHandler.HEADER_FOUND_EXCEPTION_MESSAGE.equals(e.getMessage())) {
                // The header was successfully found
            } else if (TerminatingVdbHeaderContentHandler.XMI_NOT_FOUND_EXCEPTION_MESSAGE.equals(e.getMessage())) {
                // The file is probably an XML file but not an XMI file
            } else if (e instanceof SAXParseException) {
                // The file is probably a text file but not an XML file
            }
        } catch (IOException e) {
            // The file is not a file that can be interpretted by the SAX parser
        } catch (Throwable e) {
            // TODO: ADD I18n ??
            final String msg = "VdbHeaderReader.Error_in_parsing_file_1"; //$NON-NLS-1$
            throw new TeiidDesignerException(e, msg);
        }
        return ((TerminatingVdbHeaderContentHandler)handler).getVdbHeader();
    }

    /**
     * Read only the <XMI.header> section of the file and return the <code>VdbHeader</code> object representing its contents
     * 
     * @param file the File from which we read the header
     * @return the VdbHeader object representing the contents of this section
     * @throws TeiidDesignerException if there is an error reading the file
     */
    public VdbHeader read( File file ) throws TeiidDesignerException {
        if (file == null) {
            //// TODO: ADD I18n ??
            final String msg = "VdbHeaderReader.The_file_reference_may_not_be_null_2"; //$NON-NLS-1$
            throw new IllegalArgumentException(msg);
        }
        if (!file.exists()) {
            //final Object[] params = new Object[] {file};
            // TODO: ADD I18n ??
            final String msg = "VdbHeaderReader.The_file_0_does_not_exist_and_therefore_cannot_be_read._3"; //$NON-NLS-1$
            throw new IllegalArgumentException(msg);
        }
        if (!file.canRead()) {
            //final Object[] params = new Object[] {file};
            // TODO: ADD I18n ??
            final String msg = "VdbHeaderReader.The_file_0_does_not_have_read_privileges._4"; //$NON-NLS-1$
            throw new IllegalArgumentException(msg);
        }

        // If the file does not start with an XML declaration tag ...
        if (!isXmlFile(file)) {
            return null;
        }

        // Attempt to read the XML file and interpret it as an XMI file
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        try {
            fis = new FileInputStream(file);
            bis = new BufferedInputStream(fis);
            return read(bis);
        } catch (FileNotFoundException e) {
            // TODO: ADD I18n ??
            final String msg = "VdbHeaderReader.Error_in_parsing_file_1"; //$NON-NLS-1$
            throw new TeiidDesignerException(e, msg);
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    // do nothing
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    // do nothing
                }
            }
        }
    }

    // ==================================================================================
    // P R I V A T E M E T H O D S
    // ==================================================================================

    private static boolean isXmlFile( File file ) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            byte[] buf = new byte[32];
            fis.read(buf);
            if (new String(buf).startsWith(XML_DECLARATION_PREFIX_STRING)) {
                return true;
            }
        } catch (IOException e) {
            // do nothing
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    // do nothing
                }
            }
        }
        return false;
    }

    /**
     * Return a java.io.InputStream reference for the MetaMatrix-VdbManifestModel.xmi model file contained within the Vdb archive.
     * If the specified file is not a Vdb archive file or the archive does not contain a manifest model then null is returned.
     * 
     * @param zipFile the ZipFile for the VDB archive
     * @return the inputstream for the manifest file entry
     */
    private static InputStream getManifestStreamFromVdbArchive( final ZipFile zipFile ) {
        return getEntryStreamFromArchive(zipFile, MANIFEST_MODEL_NAME);
    }

    /**
     * Return a java.io.InputStream reference for the specified zip entry name
     * 
     * @param zipFile the ZipFile for the VDB archive
     * @param zipEntryName the fully qualified name of the zip entry
     * @return the inputstream for the zipfile entry
     */
    private static InputStream getEntryStreamFromArchive( final ZipFile zipFile,
                                                          final String zipEntryName ) {
        CoreArgCheck.isNotNull(zipFile);
        CoreArgCheck.isNotEmpty(zipEntryName);

        // the zip file that would be initialized if the file being read is an archive
        try {
            // Iterate over all entries in the zip file ...
            for (final Enumeration entries = zipFile.entries(); entries.hasMoreElements();) {
                ZipEntry entry = (ZipEntry)entries.nextElement();
                if (entry == null) {
                    continue;
                }
                // If the specified zip entry is found ...
                if (entry.getName().equalsIgnoreCase(zipEntryName)) {
                    // return the contents of the entry
                    return zipFile.getInputStream(entry);
                }
            }
        } catch (IOException e) {
            throw new TeiidDesignerRuntimeException(e);
        }
        return null;
    }

}
