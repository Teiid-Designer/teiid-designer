/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.jboss.ide.eclipse.as.storage.source.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osgi.util.NLS;
import org.jboss.ide.eclipse.as.storage.IStorageSource;
import org.jboss.ide.eclipse.as.storage.IStorageUnit.Category;
import org.jboss.ide.eclipse.as.storage.Messages;
import org.jboss.ide.eclipse.as.storage.StorageUnitStream;

/**
 * Directory implementation of a storage source that writes the data out
 * to a directory hierarchy.
 * <p>
 * The directory will be written to (or read from)
 * $HOME/.designer unless a custom directory is
 * selected using the location property.
 */
public class DirectoryStorage implements IStorageSource {

    private static final String DOT_JBT = ".jbosstools"; //$NON-NLS-1$
    /**
     * The property id for setting a custom location for the storage source
     * to be written to.
     */
    public static final String LOCATION_PROPERTY = "locationProperty"; //$NON-NLS-1$

    private final String defaultTargetLocation = System.getProperty("user.home") + File.separator + DOT_JBT; //$NON-NLS-1$

    private String targetLocation = defaultTargetLocation;

    @Override
    public String id() {
        return DirectoryStorage.class.getName();
    }

    @Override
    public void setProperty(String name, Object value) throws Exception {
        if (LOCATION_PROPERTY.equals(name)) {
            if (value == null)
                throw new Exception(NLS.bind(Messages.DirectoryStoragePropNullError, name));

            String location = value.toString();
            File locationDir = new File(location);
            if (! locationDir.isDirectory())
                throw new Exception(NLS.bind(Messages.DirectoryStorageInvalidLocation, location));

            targetLocation = location;
        }
    }

    @Override
    public void write(Category category, Set<StorageUnitStream> exportStreams) throws Exception {
        if (category == null || exportStreams == null || exportStreams.isEmpty())
            return;

        File targetDir = new File(targetLocation);

        File categoryDir = new File(targetDir.getAbsolutePath(), category.getId());
        if (! categoryDir.exists())
            categoryDir.mkdir();

        if (exportStreams.isEmpty())
            return;

        for (StorageUnitStream storageUnitStream : exportStreams) {
            File streamFile = new File(categoryDir.getAbsolutePath(), storageUnitStream.getId());
            if (streamFile.exists())
                streamFile.delete();

            OutputStream os = null;
            InputStream exportStream = storageUnitStream.getStream();
            try {
                os = new FileOutputStream(streamFile);
                byte[] buf = new byte[8192];
                int c = 0;
                while ((c = exportStream.read(buf, 0, buf.length)) > 0) {
                    os.write(buf, 0, c);
                    os.flush();
                }
            } finally {
                if (os != null)
                    os.close();
            }
        }
    }

    @Override
    public Set<StorageUnitStream> read(Category category) throws Exception {
        if (category == null)
            return Collections.emptySet();

        File targetDir = new File(targetLocation);

        File categoryDir = new File(targetDir.getAbsolutePath(), category.getId());
        if (! categoryDir.exists())
            return null;

        Set<StorageUnitStream> storageUnitStreams = new HashSet<StorageUnitStream>();
        for (File pageFile : categoryDir.listFiles()) {
            storageUnitStreams.add(new StorageUnitStream(pageFile.getName(), new FileInputStream(pageFile)));   
        }

        return storageUnitStreams;
    }

}
