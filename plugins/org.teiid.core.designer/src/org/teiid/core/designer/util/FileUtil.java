/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.core.designer.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringWriter;
import javax.activation.MimetypesFileTypeMap;
import org.teiid.core.designer.TeiidDesignerRuntimeException;
import org.teiid.core.designer.util.FileUtils.Constants;

/**
 * @since 8.0
 */
public final class FileUtil {

    /**
     * Constants for common file extensions.
     * 
     * @since 6.0.0
     */
/**
 * @since 8.0
 */
    public interface Extensions {

        /**
         * A jar file extension with the value of "{@value} ."
         * 
         * @since 6.0.0
         */
        String JAR = ".jar"; //$NON-NLS-1$

        /**
         * A zip file extension with the value of "{@value} ."
         * 
         * @since 6.0.0
         */
        String ZIP = ".zip"; //$NON-NLS-1$
    }

    /**
     * Checks the specified file name to see if it has an archive extension.
     * 
     * @param name the file name being checked
     * @param checkZipExtension indicates if zip files should be considered an archive file
     * @return <code>true</code> if file name has an archive extension
     * @since 6.0.0
     * @see Extensions#JAR
     * @see Extensions#ZIP
     */
    public final static boolean isArchiveFileName( String name,
                                                   boolean checkZipExtension ) {
        if (name.endsWith(Extensions.JAR)) {
            return (name.length() > Extensions.JAR.length());
        }

        if (checkZipExtension) {
            return isZipFileName(name);
        }

        return false;
    }

    /**
     * @param name the name being tested (never <code>null</code>)
     * @return <code>true</code> if the name ends with a zip file extension and has a simple name with length of one or more
     * @see Extensions#ZIP
     */
    public final static boolean isZipFileName( String name ) {
        return (name.endsWith(Extensions.ZIP) && (name.length() > Extensions.ZIP.length()));
    }

    /**
     * Obtains the file extension of the specified <code>File</code>. The extension is considered to be all the characters after
     * the last occurrence of {@link Constants#FILE_EXTENSION_SEPARATOR_CHAR} in the pathname of the input.
     * 
     * @param theFile the file whose extension is being requested
     * @return the extension or <code>null</code> if not found
     * @since 4.2
     */
    public static String getExtension( File theFile ) {
        return getExtension(theFile.getName());
    }

    /**
     * Obtains the file extension of the specified file name. The extension is considered to be all the characters after the last
     * occurrence of {@link Constants#FILE_EXTENSION_SEPARATOR_CHAR}.
     * 
     * @param theFileName the file whose extension is being requested
     * @return the extension or <code>null</code> if not found
     * @since 4.2
     */
    public static String getExtension( String theFileName ) {
        String result = CoreStringUtil.Constants.EMPTY_STRING;
        final int index = theFileName.lastIndexOf(Constants.FILE_EXTENSION_SEPARATOR_CHAR);

        // make sure extension char is found and is not the last char in the path
        if ((index != -1) && ((index + 1) != theFileName.length())) {
            result = theFileName.substring(index + 1);
        }

        return result;
    }
    
    /**
     * Read a file, extract its contents, ensuring the file reader is closed.
     *
     * @param file 
     * 
     * @return contents of file as a {@link String}
     * 
     * @throws FileNotFoundException
     */
    public static String readSafe(File file) throws FileNotFoundException {
        String result;
        FileReader reader = null;
        try {
            reader = new FileReader(file);
            result = read(reader);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {                
                }
            }
        }
                
        return result;
    }
    
    /**
     * Read the given {@link Reader} and return its contents
     * as a {@link String}
     * 
     * @param reader
     * 
     * @return contents as a {@link String}
     */
    public static String read(Reader reader) {
        StringWriter writer = new StringWriter();
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(reader);
            while (bufferedReader.ready()) {
                String line = bufferedReader.readLine();
                writer.write(line);
                writer.write(CoreStringUtil.LINE_SEPARATOR);
            }
        } catch (IOException e) {
            throw new TeiidDesignerRuntimeException(e);
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (Exception e) {
                }
            }
        }
        return writer.toString();
    }

    /**
     * @param file
     * @return Try and determine the file type of the given file
     */
    public static String guessFileType(File file) {
        String mimeType = null;

        InputStream mimeTypesStream = FileUtil.class.getResourceAsStream("mime.types"); //$NON-NLS-1$

        try {
            MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap(mimeTypesStream);
            
			mimeType = mimeTypesMap.getContentType(file);
			if (mimeType != null) {
				return mimeType;
			}

			mimeType = mimeTypesMap.getContentType(file.getName());
			if (mimeType != null) {
				return mimeType;
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {
	    	try {
				mimeTypesStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

        return mimeType;
    }

    /**
     * Prevents instantiation.
     * 
     * @since 6.0
     */
    private FileUtil() {
        // nothing to do
    }
}
