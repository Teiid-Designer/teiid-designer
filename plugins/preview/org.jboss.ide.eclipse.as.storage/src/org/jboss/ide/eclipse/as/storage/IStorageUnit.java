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

/**
 * A unit of the Global Settings Storage.
 *
 * The storage manager will find all registered storage units and
 * export them to a designated storage source or import from
 * the same (or different) storage source.
 * 
 * <ul>
 * <li>Export their runtime data to a {@link IStorageSource}</li>
 * <li>Import runtime data from a {@link IStorageSource}</li>
 * </ul>
 */
public interface IStorageUnit {

    /**
     * The category of a {@link IStorageUnit}
     */
    interface Category {

        /**
         * @return identifier of this category
         */
        String getId();

        /**
         * Units will be prioritised based on this value so higher priority
         * units will be exported and imported first.
         *
         * @return the priority of this category.
         */
        int getPriority();
    }

    /**
     * @return a unique {@link Category} for this unit.
     */
    Category getCategory();

    /**
     * @return a set of {@link StorageUnitStream}s to be exported into the {@link IStorageSource}
     * @throws Exception
     */
    Set<StorageUnitStream> toExportStreams() throws Exception;

    /**
     * Import a page's settings from the {@link IStorageSource}
     *
     * @param storageUnitStreams
     * @throws Exception
     */
    void importStream(Set<StorageUnitStream> storageUnitStreams) throws Exception;
}
