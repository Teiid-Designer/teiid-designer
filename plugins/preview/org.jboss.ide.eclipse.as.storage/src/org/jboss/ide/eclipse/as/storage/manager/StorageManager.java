/******************************************************************************* 
 * Copyright (c) 2012 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.ide.eclipse.as.storage.manager;

import java.util.Set;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.jboss.ide.eclipse.as.storage.IStorageManager;
import org.jboss.ide.eclipse.as.storage.IStorageSource;
import org.jboss.ide.eclipse.as.storage.IStorageUnit;
import org.jboss.ide.eclipse.as.storage.Messages;
import org.jboss.ide.eclipse.as.storage.StoragePlugin;
import org.jboss.ide.eclipse.as.storage.StorageUnitStream;
import org.jboss.ide.eclipse.as.storage.registry.StorageSourceRegistry;
import org.jboss.ide.eclipse.as.storage.registry.StorageUnitRegistry;
import org.jboss.ide.eclipse.as.storage.util.StringConstants;


/**
 *
 */
public class StorageManager implements IStorageManager {

    private static StorageManager instance;

    private final StorageSourceRegistry storageSourceRegistry;

    private final StorageUnitRegistry unitRegistry;

    /**
     * @return singleton instance
     * @throws Exception 
     */
    public static StorageManager getInstance() throws Exception {
        if (instance == null)
            instance = new StorageManager();

        return instance;
    }

    private StorageManager() throws Exception {
        this.storageSourceRegistry = StorageSourceRegistry.getInstance();
        this.unitRegistry = StorageUnitRegistry.getInstance();
    }

    /**
     * @return the registry
     */
    public StorageSourceRegistry getStorageSourceRegistry() {
        return storageSourceRegistry;
    }

    @Override
    public IStatus exportUnits(IStorageSource storageSource) {
        MultiStatus status = new MultiStatus(StoragePlugin.PLUGIN_ID, 0, Messages.StorageManagerExportStatusMsg, null);

        for (IStorageUnit page : unitRegistry.getRegistered()) {
            Set<StorageUnitStream> exportStreams = null;
            try {
                exportStreams = page.toExportStreams();
                storageSource.write(page.getCategory(), exportStreams);
            } catch (Exception ex) {
                status.add(new Status(IStatus.ERROR, StoragePlugin.PLUGIN_ID, StringConstants.EMPTY_STRING, ex));
            } finally {

                // Even though storage source may have closed the stream
                // check that it is closed.
                if (exportStreams != null) {
                    for (StorageUnitStream storageUnitStream : exportStreams)
                        storageUnitStream.dispose();
                }
            }
        }

        return status;
    }

    @Override
    public IStatus importUnits(IStorageSource storageSource) {
        MultiStatus status = new MultiStatus(StoragePlugin.PLUGIN_ID, 0, Messages.StorageManagerImportStatusMsg, null);

        for (IStorageUnit page : unitRegistry.getRegistered()) {
            Set<StorageUnitStream> storageUnitStreams = null;
            try {
                storageUnitStreams = storageSource.read(page.getCategory());
                if (storageUnitStreams == null || storageUnitStreams.isEmpty())
                    continue;

                page.importStream(storageUnitStreams);

            } catch (Exception ex) {
                status.add(new Status(IStatus.ERROR, StoragePlugin.PLUGIN_ID, StringConstants.EMPTY_STRING, ex));
            } finally {
                for (StorageUnitStream storageUnitStream : storageUnitStreams)
                    storageUnitStream.dispose();
            }
        }

        return status;
    }
}
