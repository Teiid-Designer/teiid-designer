/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.vdb.file;

import java.io.InputStream;
import java.util.List;
import org.eclipse.core.resources.IFile;

/**
 * Callback interface for operations involving searching a vdb file.
 * @see VdbFileProcessor
 */
public interface IVdbFileCallback {

    /**
     * @return vdb file
     */
    IFile getVdb();

    /**
     * @return list of filenames of interest to this callback, eg. manifest
     */
    List<String> getFilesOfInterest();

    /**
     * Process the given input stream which represents one of the files
     * of interest to this callback.
     *
     * @param fileName
     * @param inputStream
     */
    void processStream(String fileName, InputStream inputStream);

    /**
     * An exception occurred whilst using this callback
     *
     * @param ex
     */
    void exceptionThrown(Exception ex);

}
