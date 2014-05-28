/******************************************************************************* 
 * Copyright (c) 2014 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.ide.eclipse.as.storage;

import java.util.Set;
import org.jboss.ide.eclipse.as.storage.IStorageUnit.Category;

/**
 * A global storage source for settings and preferences
 * independent of Eclipse's runtime plugin metadata.
 * The settings are exported and imported by the
 * {@link IStorageManager} to the requisite plugin
 * registries used throughout the application.
 */
public interface IStorageSource {

    /**
     * @return a unique identifier for this type of storage source
     */
    String id();

    /**
     * Storage sources may require extra information in order to complete
     * their operations. In which case, this information can be provided
     * as properties.
     *
     * @param name
     * @param value
     * @throws Exception 
     */
    public void setProperty(String name, Object value) throws Exception;

    /**
     * Write the given page to the {@link IStorageSource}
     * @param category
     * @param exportStreams
     *
     * @throws Exception
     */
    void write(Category category, Set<StorageUnitStream> exportStreams) throws Exception;

    /**
     * Read the given page category's data from the {@link IStorageSource}
     *
     * @param category
     * @return set of {@link StorageUnitStream}s the page's data or null if no data exists
     * @throws Exception 
     */
    Set<StorageUnitStream> read(Category category) throws Exception;

}
