/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime.storage.unit;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import org.jboss.ide.eclipse.as.storage.IStorageUnit;
import org.jboss.ide.eclipse.as.storage.StorageUnitStream;
import org.teiid.core.designer.util.FileUtils;
import org.teiid.core.designer.util.StringConstants;
import org.teiid.designer.runtime.DqpPlugin;
import org.teiid.designer.runtime.spi.ITeiidServerManager;

/**
 * Archive Page responsible for exporting / importing all jboss
 * IServer instances and their related teiid instances.
 */
public class DqpStorageUnit implements IStorageUnit, StringConstants {

    private static final String STORAGE_UNIT_ID = "teiidInstances"; //$NON-NLS-1$

    protected static final String TEIID_INSTANCES_FILE_ID = STORAGE_UNIT_ID + DOT + XML;

    private static class DqpUnitCategory implements Category {

        @Override
        public String getId() {
            return STORAGE_UNIT_ID;
        }

        @Override
        public int getPriority() {
            return 0;
        }
    }

    private static DqpUnitCategory STORAGE_CATEGORY = new DqpUnitCategory();

    @Override
    public Category getCategory() {
        return STORAGE_CATEGORY;
    }

    @Override
    public Set<StorageUnitStream> toExportStreams() throws Exception {
        Set<StorageUnitStream> storageUnitStreams = new HashSet<StorageUnitStream>();

        ITeiidServerManager serverManager = DqpPlugin.getInstance().getServerManager();
        File teiidInstanceFile = File.createTempFile("exportTeiidInstanceState", XML); //$NON-NLS-1$
        teiidInstanceFile.deleteOnExit();

        serverManager.saveState(teiidInstanceFile.getAbsolutePath());
        StorageUnitStream teiidStream = new StorageUnitStream(TEIID_INSTANCES_FILE_ID,
                                                                                                           new FileInputStream(teiidInstanceFile));
        storageUnitStreams.add(teiidStream);

        return storageUnitStreams;
    }

    @Override
    public void importStream(Set<StorageUnitStream> storageUnitStreams) throws Exception {
        for (StorageUnitStream storageUnitStream : storageUnitStreams) {
            if (TEIID_INSTANCES_FILE_ID.equals(storageUnitStream.getId())) {
                InputStream inputStream = storageUnitStream.getStream();
                File teiidInstanceFile = File.createTempFile("importTeiidInstanceState", XML); //$NON-NLS-1$
                teiidInstanceFile.deleteOnExit();

                FileUtils.writeStreamToFile(inputStream, teiidInstanceFile);

                ITeiidServerManager serverManager = DqpPlugin.getInstance().getServerManager();
                serverManager.loadServers(teiidInstanceFile);
            }
        }
    }

}
