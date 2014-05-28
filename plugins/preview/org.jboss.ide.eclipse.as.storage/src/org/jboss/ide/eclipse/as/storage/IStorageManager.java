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

import org.eclipse.core.runtime.IStatus;

/**
 *
 */
public interface IStorageManager {

    /**
     * Export all {@link IStorageUnit}s to the given {@link IStorageSource}
     *
     * @param storageSource
     * @return status of export
     */
    IStatus exportUnits(IStorageSource storageSource);

    /**
     * Import all {@link IStorageUnit}s from the given {@link IStorageSource}
     *
     * @param storageSource
     * @return status of import
     */
    IStatus importUnits(IStorageSource storageSource);
}
