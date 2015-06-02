/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.vdb;

import net.jcip.annotations.ThreadSafe;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.teiid.designer.vdb.manifest.EntryElement;


/**
 * VdbFileEntry - represents VDB file entries, either UDF jars or otherFiles
 *
 * @since 8.0
 */
@ThreadSafe
public final class VdbSchemaEntry extends VdbIndexedEntry {

    /**
     * Constructs a schema entry and adds it to the specified VDB.
     *
     * @param vdb the VDB where the resource is be added to (may not be <code>null</code>)
     * @param path the schema path (may not be <code>null</code>)
     * @param monitor the progress monitor or <code>null</code>
     * @throws Exception
     */
    public VdbSchemaEntry( final Vdb vdb, final IPath path, final IProgressMonitor monitor ) throws Exception {
        super(vdb, path, monitor);
    }

    /**
     * Constructs a schema entry and adds it to the specified VDB.
     *
     * @param vdb the VDB where the resource is be added to (may not be <code>null</code>)
     * @param element the EntryElement
     * @param monitor the progress monitor or <code>null</code>
     * @throws Exception
     */
    public VdbSchemaEntry( final Vdb vdb, final EntryElement element, final IProgressMonitor monitor ) throws Exception {
        super(vdb, element, monitor);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.teiid.designer.vdb.VdbEntry#synchronize(org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void synchronize(final IProgressMonitor monitor) throws Exception {
        if (getSynchronization() != Synchronization.NotSynchronized)
            return;

        synchronizeSchemaEntry(monitor);
        super.synchronize(monitor);
    }

    /**
     * @param monitor
     * @throws Exception
     */
    public void synchronizeSchemaEntry(IProgressMonitor monitor) throws Exception {
        synchronizeIndex(monitor);
    }
}
