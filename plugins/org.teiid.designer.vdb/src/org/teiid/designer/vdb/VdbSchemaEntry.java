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
     * @throws Exception
     */
    public VdbSchemaEntry( final Vdb vdb, final IPath path ) throws Exception {
        super(vdb, path);
    }

    /**
     * Constructs a schema entry and adds it to the specified VDB.
     *
     * @param vdb the VDB where the resource is be added to (may not be <code>null</code>)
     * @param element the EntryElement
     * @throws Exception
     */
    public VdbSchemaEntry( final Vdb vdb, final EntryElement element ) throws Exception {
        super(vdb, element);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.teiid.designer.vdb.VdbEntry#synchronize()
     */
    @Override
    public void synchronize() throws Exception {
        if (getSynchronization() != Synchronization.NotSynchronized)
            return;

        synchronizeSchemaEntry();
        super.synchronize();
    }

    /**
     * @throws Exception
     */
    public void synchronizeSchemaEntry() throws Exception {
        synchronizeIndex();
    }
}
