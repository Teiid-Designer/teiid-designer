/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package org.teiid.designer.runtime.preview;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.teiid.designer.runtime.Server;
import org.teiid.designer.vdb.Vdb;

/**
 * The <code>PreviewContext</code> stores information required to perform data preview.
 */
public interface PreviewContext {

    /**
     * @param previewVdb the Preview VDB whose connection information is begin validated
     * @param previewServer the preview server (may not be <code>null</code>)
     * @return a status indicating if the connection information is valid
     */
    IStatus ensureConnectionInfoIsValid( Vdb previewVdb,
                                         Server previewServer ) throws Exception;

    /**
     * @param projectOrModel the project or model whose Preview VDB is being requested (may not be <code>null</code>)
     * @return the Preview VDB (never <code>null</code>)
     */
    IFile getPreviewVdb( IResource projectOrModel );

    /**
     * @param pvdbPath the path of the Preview VDB whose deploy name is being requested (may not be <code>null</code>)
     * @return the name (never <code>null</code>)
     */
    String getPreviewVdbDeployedName( IPath pvdbPath );

    /**
     * @param pvdbPath the path of the Preview VDB whose JNDI name is being requested (may not be <code>null</code>)
     * @return the JNDI name (never <code>null</code>)
     */
    String getPreviewVdbJndiName( IPath pvdbPath );

}
