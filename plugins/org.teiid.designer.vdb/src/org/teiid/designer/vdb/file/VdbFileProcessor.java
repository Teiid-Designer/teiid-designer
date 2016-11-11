/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.vdb.file;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.eclipse.core.resources.IFile;
import org.teiid.core.designer.util.CoreArgCheck;

/**
 * Allows iteration of the contents of a vdb file and delegates
 * to an {@link IVdbFileCallback} for its function.
 */
public class VdbFileProcessor {

    private final IVdbFileCallback callback;

    /**
     * @param callback 
     * 
     */
    public VdbFileProcessor(IVdbFileCallback callback) {
        CoreArgCheck.isNotNull(callback);
        CoreArgCheck.isNotNull(callback.getVdb());

        this.callback = callback;
    }

    /**
     * Process the vdb file and delegate to the callback
     */
    public void process() {
        IFile vdbFile = callback.getVdb();

        if (!vdbFile.exists())
            return;

        ZipFile archive = null;
        try {
            archive = new ZipFile(vdbFile.getLocation().toString());
            for (final Enumeration<? extends ZipEntry> iter = archive.entries(); iter.hasMoreElements();) {
                ZipEntry zipEntry = iter.nextElement();
                InputStream entryStream = archive.getInputStream(zipEntry);
                if (callback.getFilesOfInterest().contains(zipEntry.getName())) {
                    callback.processStream(zipEntry.getName(), entryStream);
                    entryStream.close();
                    break;
                }
                entryStream.close();
            }
        } catch (Exception ex) {
            callback.exceptionThrown(ex);
        } finally {
            if (archive != null)
                try {
                    archive.close();
                } catch (IOException ex) {
                    callback.exceptionThrown(ex);
                }
        }
    }
}
