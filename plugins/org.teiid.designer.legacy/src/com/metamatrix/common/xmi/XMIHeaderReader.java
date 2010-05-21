/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package com.metamatrix.common.xmi;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;
import org.teiid.core.TeiidException;

public class XMIHeaderReader {

    private static final String XML_DECLARATION_PREFIX_STRING = "<?xml version="; //$NON-NLS-1$

    // ==================================================================================
    // C O N S T R U C T O R S
    // ==================================================================================

    public XMIHeaderReader() {
    }

    // ==================================================================================
    // P U B L I C M E T H O D S
    // ==================================================================================

    public static XMIHeader readHeader( InputStream istream ) throws TeiidException {
        XMIHeaderReader reader = new XMIHeaderReader();
        return reader.read(istream);
    }

    public static XMIHeader readHeader( File file ) throws TeiidException {
        XMIHeaderReader reader = new XMIHeaderReader();
        return reader.read(file);
    }

    /**
     * Read only the <XMI.header> section of the file and return the <code>XMIHeader</code> object representing its contents
     * 
     * @param istream the InputStream from which we read the header
     * @return the XMIHeader object representing the contents of this section
     * @throws MetaMatrixException if there is an error reading from the stream
     */
    public XMIHeader read( InputStream istream ) throws TeiidException {
        if (istream == null) {
            // TODO: ADD I18n ??
            final String msg = "XMIHeaderReader.The_InputStream_reference_may_not_be_null._1"; //$NON-NLS-1$
            throw new IllegalArgumentException(msg);
        }

        DefaultHandler handler = new TerminatingXMIHeaderContentHandler();
        try {
            Thread.currentThread().setContextClassLoader(XMIHeaderReader.class.getClassLoader());
            SAXParserFactory spf = SAXParserFactory.newInstance();
            spf.setNamespaceAware(true);
            SAXParser parser = spf.newSAXParser();
            parser.parse(new InputSource(istream), handler);
        } catch (SAXException e) {
            if (TerminatingXMIHeaderContentHandler.HEADER_FOUND_EXCEPTION_MESSAGE.equals(e.getMessage())) {
                // The header was successfully found
            } else if (TerminatingXMIHeaderContentHandler.XMI_NOT_FOUND_EXCEPTION_MESSAGE.equals(e.getMessage())) {
                // The file is probably an XML file but not an XMI file
            } else if (e instanceof SAXParseException) {
                // The file is probably a text file but not an XML file
            }
        } catch (IOException e) {
            // The file is not a file that can be interpretted by the SAX parser
        } catch (Throwable e) {
            // TODO: ADD I18n ??
            final String msg = "XMIHeaderReader.Error_in_parsing_file_1"; //$NON-NLS-1$
            throw new TeiidException(e, msg);
        }
        return ((TerminatingXMIHeaderContentHandler)handler).getXmiHeader();
    }

    /**
     * Read only the <XMI.header> section of the file and return the <code>XMIHeader</code> object representing its contents
     * 
     * @param file the File from which we read the header
     * @return the XMIHeader object representing the contents of this section
     * @throws MetaMatrixException if there is an error reading the file
     */
    public XMIHeader read( File file ) throws TeiidException {
        if (file == null) {
            // TODO: ADD I18n ??
            final String msg = "XMIHeaderReader.The_file_reference_may_not_be_null_2"; //$NON-NLS-1$
            throw new IllegalArgumentException(msg);
        }
        if (!file.exists()) {
            final Object[] params = new Object[] {file};
            // TODO: ADD I18n ??
            final String msg = "XMIHeaderReader.The_file_0_does_not_exist_and_therefore_cannot_be_read._3"; //$NON-NLS-1$
            throw new IllegalArgumentException(msg);
        }
        if (!file.canRead()) {
            final Object[] params = new Object[] {file};
            // TODO: ADD I18n ??
            final String msg = "XMIHeaderReader.The_file_0_does_not_have_read_privileges._4"; //$NON-NLS-1$
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
            final String msg = "XMIHeaderReader.Error_in_parsing_file_1"; //$NON-NLS-1$
            throw new TeiidException(e, msg);
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
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
                }
            }
        }
        return false;
    }

}
