/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.query.proc;

import java.io.File;

/**
 *
 */
public interface ITeiidFileInfo {

    /**
     * 
     * @return dataFile the teiid-formatted data <code>File</code>
     */
    File getDataFile();
}
