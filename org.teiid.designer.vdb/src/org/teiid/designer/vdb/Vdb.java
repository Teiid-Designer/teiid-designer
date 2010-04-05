/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.vdb;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import net.jcip.annotations.ThreadSafe;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.teiid.designer.vdb.VdbEntry.SyncState;
import org.teiid.designer.vdb.manifest.ModelElement;
import org.teiid.designer.vdb.manifest.VdbElement;
import com.metamatrix.core.event.IChangeListener;
import com.metamatrix.core.event.IChangeNotifier;

/**
 * 
 */
@ThreadSafe
public class Vdb implements IChangeNotifier {

    private final IPath name;
    private final CopyOnWriteArraySet<VdbEntry> entries = new CopyOnWriteArraySet<VdbEntry>();
    private final CopyOnWriteArraySet<VdbModelEntry> modelEntries = new CopyOnWriteArraySet<VdbModelEntry>();
    private final CopyOnWriteArrayList<IChangeListener> listeners = new CopyOnWriteArrayList<IChangeListener>();
    private final AtomicBoolean modified = new AtomicBoolean();
    private final AtomicReference<String> description = new AtomicReference<String>();

    /**
     * @param name
     */
    public Vdb( final IPath name ) {
        this.name = name;
        // Open archive and populate model entries
        ZipFile archive = null;
        RuntimeException runtimeError = null;
        InputStream manifestStream = null;
        try {
            archive = new ZipFile(name.toString());
            // Initialize using manifest
            final ZipEntry entry = archive.getEntry("/META-INF/vdb.xml"); //$NON-NLS-1$
            final JAXBContext context = JAXBContext.newInstance(new Class<?>[] {VdbElement.class});
            final SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            final Schema schema = schemaFactory.newSchema(VdbElement.class.getResource("/vdb-deployer.xsd")); //$NON-NLS-1$
            final Unmarshaller unmarchaller = context.createUnmarshaller();
            unmarchaller.setSchema(schema);
            manifestStream = archive.getInputStream(entry);
            final VdbElement manifest = (VdbElement)unmarchaller.unmarshal(manifestStream);
            setDescription(manifest.getDescription());
            for (final ModelElement model : manifest.getModels())
                entries.add(new VdbModelEntry(model, this));
        } catch (final Exception error) {
            runtimeError = new RuntimeException(error);
            throw runtimeError;
        } finally {
            try {
                if (manifestStream != null) manifestStream.close();
                if (archive != null) archive.close();
            } catch (final IOException ignored) {
                if (runtimeError != null) throw runtimeError;
            }
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.metamatrix.core.event.IChangeNotifier#addChangeListener(com.metamatrix.core.event.IChangeListener)
     */
    @Override
    public void addChangeListener( final IChangeListener listener ) {
        listeners.addIfAbsent(listener);
    }

    /**
     * @param name
     * @return the newly added {@link VdbEntry entry}, or the existing entry with the supplied name.
     */
    public VdbEntry addEntry( final IPath name ) {
        for (final VdbEntry entry : entries)
            if (entry.getName().equals(name)) return entry;
        final VdbEntry entry = new VdbEntry(name);
        entries.add(entry);
        return entry;
    }

    /**
     * @param name
     * @return the newly added {@link VdbModelEntry model entry}, or the existing entry with the supplied name.
     */
    public VdbModelEntry addModelEntry( final IPath name ) {
        for (final VdbModelEntry entry : modelEntries)
            if (entry.getName().equals(name)) return entry;
        final VdbModelEntry entry = new VdbModelEntry(name, this);
        entries.add(entry);
        return entry;
    }

    /**
     * 
     */
    public final void close() {
        // TODO: implement
        if (isModified()) setModified(false);
    }

    /**
     * @return description
     */
    public String getDescription() {
        return description.get();
    }

    /**
     * @return the immutable set of entries, not including {@link #getModelEntries() model entries}, within this VDB
     */
    public Set<VdbEntry> getEntries() {
        return Collections.unmodifiableSet(entries);
    }

    /**
     * @return the immutable set of model entries within this VDB
     */
    public Set<VdbModelEntry> getModelEntries() {
        final Set<VdbModelEntry> entries = new HashSet<VdbModelEntry>();
        for (final VdbModelEntry entry : modelEntries)
            if (!entry.isBuiltIn()) entries.add(entry);
        return Collections.unmodifiableSet(entries);
    }

    /**
     * @return the name of this VDB
     */
    public IPath getName() {
        return name;
    }

    /**
     * @return <code>true</code> if this VDB has been modified since its creation of last {@link #save(IProgressMonitor) save}.
     */
    public boolean isModified() {
        return modified.get();
    }

    /**
     * @return <code>true</code> if all model entries in this VDB are either synchronized with their associated models or no
     *         associated model exists..
     */
    public boolean isSynchronized() {
        for (final VdbEntry entry : entries)
            if (entry.getSyncState() == SyncState.OutOfSync) return false;
        return true;
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.metamatrix.core.event.IChangeNotifier#removeChangeListener(com.metamatrix.core.event.IChangeListener)
     */
    @Override
    public void removeChangeListener( final IChangeListener listener ) {
        listeners.remove(listener);
    }

    /**
     * @param entry
     */
    public void removeEntry( final VdbEntry entry ) {
        entry.dispose();
        entries.remove(entry);
    }

    /**
     * @param monitor
     * @return The result of attempting to save this VDB
     */
    public IStatus save( final IProgressMonitor monitor ) {
        // TODO: implement
        return Status.OK_STATUS;
    }

    /**
     * @param description Sets description to the specified value.
     */
    public void setDescription( final String description ) {
        this.description.set(description);
    }

    private void setModified( final boolean modified ) {
        this.modified.set(modified);
        for (final IChangeListener listener : listeners)
            listener.stateChanged(this);
    }

    /**
     * @return <code>true</code> if any model entries needed to be synchronized
     */
    public boolean synchronize() {
        return synchronize(entries);
    }

    /**
     * @param entries
     * @return <code>true</code> if any model entries needed to be synchronized
     */
    public boolean synchronize( final Collection<VdbEntry> entries ) {
        boolean modified = false;
        for (final VdbEntry entry : entries)
            if (entry.getSyncState() == SyncState.OutOfSync) {
                entry.synchronize();
                modified = true;
            }
        if (modified) {
            if (!isModified()) setModified(true);
            return true;
        }
        return false;
    }
}
