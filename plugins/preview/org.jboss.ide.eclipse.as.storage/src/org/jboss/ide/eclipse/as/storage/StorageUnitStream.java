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

import java.io.IOException;
import java.io.InputStream;

/**
 *
 */
public class StorageUnitStream {

    private final String id;

    private final InputStream stream;

    private boolean disposed;

    /**
     * @param id
     * @param stream
     */
    public StorageUnitStream(String id, InputStream stream) {
        this.id = id;
        this.stream = stream;
    }

    /**
     * @return the id
     */
    public String getId() {
        return this.id;
    }

    /**
     * @return the stream
     */
    public InputStream getStream() {
        return this.stream;
    }

    /**
     * Dispose of this storage stream by closing
     * the stream
     */
    public void dispose() {
        try {
            stream.close();
        } catch (IOException ex) {
            StoragePlugin.log(ex);
        } finally {
            disposed = true;
        }
    }

    /**
     * @return the disposed
     */
    public boolean isDisposed() {
        return this.disposed;
    }
}
