/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.common.vdb;

import java.io.InputStream;

/**
 * 
 */
public interface VDBFile {

    /**
     * Get the path to the file represented by this record in the vdb.
     * 
     * @return The path to the file in the vdb
     * @since 4.2
     */
    String getPathInVdb();

    /**
     * Get the contents of the file in the VDB.
     * 
     * @return A inputstream as the vdb resource content.
     * @since 4.2
     */
    InputStream getContent();
}
