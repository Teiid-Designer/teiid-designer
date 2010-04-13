/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.vdb;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
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
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.teiid.designer.vdb.VdbEntry.Synchronization;
import org.teiid.designer.vdb.manifest.ModelElement;
import org.teiid.designer.vdb.manifest.VdbElement;

/**
 * 
 */
@ThreadSafe
public class Vdb {

    /**
     * The property name sent in events to {@link #addChangeListener(PropertyChangeListener) change listeners} when the
     * {@link #getDescription() description} in a VDB is changed
     */
    public static final String DESCRIPTION = "description"; //$NON-NLS-1$

    /**
     * The property name sent in events to {@link #addChangeListener(PropertyChangeListener) change listeners} when an entry is
     * added to a VDB
     * 
     * @see #addEntry(IPath, IProgressMonitor)
     * @see #addModelEntry(IPath, IProgressMonitor)
     */
    public static final String ENTRY_ADDED = "entryAdded"; //$NON-NLS-1$

    /**
     * The property name sent in events to {@link #addChangeListener(PropertyChangeListener) change listeners} when an
     * {@link #removeEntry(VdbEntry) entry is removed} from a VDB
     */
    public static final String ENTRY_REMOVED = "entryRemoved"; //$NON-NLS-1$

    /**
     * The property name sent in events to {@link #addChangeListener(PropertyChangeListener) change listeners} when a VDB entry's
     * {@link VdbEntry#synchronize(IProgressMonitor) synchronization state} changes
     */
    public static final String SYNCHRONIZATION = "synchronization"; //$NON-NLS-1$

    /**
     * The property name sent in events to {@link #addChangeListener(PropertyChangeListener) change listeners} when a VDB model
     * entry's {@link VdbModelEntry#isVisible() visibility} changes
     */
    public static final String VISIBLE = "visible"; //$NON-NLS-1$

    /**
     * The property name sent in events to {@link #addChangeListener(PropertyChangeListener) change listeners} when a VDB physical
     * model entry's {@link VdbModelEntry#getDataSource() data source} changes
     */
    public static final String DATA_SOURCE = "dataSource"; //$NON-NLS-1$

    /**
     * The property name sent in events to {@link #addChangeListener(PropertyChangeListener) change listeners} when a VDB is
     * {@link #close() closed}
     */
    public static final String CLOSED = "closed"; //$NON-NLS-1$

    /**
     * The property name sent in events to {@link #addChangeListener(PropertyChangeListener) change listeners} when a VDB is
     * {@link #save(IProgressMonitor) save}
     */
    public static final String SAVED = "saved"; //$NON-NLS-1$

    private final IPath name;
    private final CopyOnWriteArraySet<VdbEntry> entries = new CopyOnWriteArraySet<VdbEntry>();
    private final CopyOnWriteArraySet<VdbModelEntry> modelEntries = new CopyOnWriteArraySet<VdbModelEntry>();
    private final CopyOnWriteArrayList<PropertyChangeListener> listeners = new CopyOnWriteArrayList<PropertyChangeListener>();
    private final AtomicBoolean modified = new AtomicBoolean();
    private final AtomicReference<String> description = new AtomicReference<String>();

    /**
     * Constructs a VDB for an existing archive.
     * 
     * @param file
     * @param monitor
     */
    public Vdb( final IFile file,
                final IProgressMonitor monitor ) {
        this(file.getFullPath());
        if (file.getLocation().toFile().length() == 0) return;
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
                modelEntries.add(new VdbModelEntry(model, this, monitor));
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
     * Constructs a new VDB.
     * 
     * @param name
     */
    public Vdb( final IPath name ) {
        this.name = name;
    }

    /**
     * @param listener
     */
    public final void addChangeListener( final PropertyChangeListener listener ) {
        listeners.addIfAbsent(listener);
    }

    /**
     * @param name
     * @param monitor
     * @return the newly added {@link VdbEntry entry}, or the existing entry with the supplied name.
     */
    public final VdbEntry addEntry( final IPath name,
                                    final IProgressMonitor monitor ) {
        for (final VdbEntry entry : entries)
            if (entry.getName().equals(name)) return entry;
        final VdbEntry entry = new VdbEntry(name, this, monitor);
        entries.add(entry);
        setModified(ENTRY_ADDED, null, entry);
        return entry;
    }

    /**
     * @param name
     * @param monitor
     * @return the newly added {@link VdbModelEntry model entry}, or the existing entry with the supplied name.
     */
    public final VdbModelEntry addModelEntry( final IPath name,
                                              final IProgressMonitor monitor ) {
        for (final VdbModelEntry entry : modelEntries)
            if (entry.getName().equals(name)) return entry;
        final VdbModelEntry entry = new VdbModelEntry(name, this, monitor);
        modelEntries.add(entry);
        setModified(ENTRY_ADDED, null, entry);
        return entry;
    }

    /**
     * 
     */
    public final void close() {
        entries.clear();
        modelEntries.clear();
        listeners.clear();
        if (isModified()) modified.set(false);
        notifyChangeListeners(CLOSED, null, null);
    }

    /**
     * @return description
     */
    public final String getDescription() {
        return description.get();
    }

    /**
     * @return the immutable set of entries, not including {@link #getModelEntries() model entries}, within this VDB
     */
    public final Set<VdbEntry> getEntries() {
        return Collections.unmodifiableSet(entries);
    }

    /**
     * @return the immutable set of model entries within this VDB
     */
    public final Set<VdbModelEntry> getModelEntries() {
        final Set<VdbModelEntry> entries = new HashSet<VdbModelEntry>();
        for (final VdbModelEntry entry : modelEntries)
            if (!entry.isBuiltIn()) entries.add(entry);
        return Collections.unmodifiableSet(entries);
    }

    /**
     * @return the name of this VDB
     */
    public final IPath getName() {
        return name;
    }

    /**
     * @return <code>true</code> if this VDB has been modified since its creation of last {@link #save(IProgressMonitor) save}.
     */
    public final boolean isModified() {
        return modified.get();
    }

    /**
     * @return <code>true</code> if all model entries in this VDB are either synchronized with their associated models or no
     *         associated model exists..
     */
    public final boolean isSynchronized() {
        for (final VdbModelEntry entry : modelEntries)
            if (entry.getSynchronization() == Synchronization.NotSynchronized) return false;
        for (final VdbEntry entry : entries)
            if (entry.getSynchronization() == Synchronization.NotSynchronized) return false;
        return true;
    }

    void notifyChangeListeners( final Object source,
                                final String propertyName,
                                final Object oldValue,
                                final Object newValue ) {
        PropertyChangeEvent event = null;
        for (final PropertyChangeListener listener : listeners) {
            if (event == null) event = new PropertyChangeEvent(this, propertyName, oldValue, newValue);
            listener.propertyChange(event);
        }
    }

    private void notifyChangeListeners( final String propertyName,
                                        final Object oldValue,
                                        final Object newValue ) {
        notifyChangeListeners(this, propertyName, oldValue, newValue);
    }

    /**
     * @param listener
     */
    public final void removeChangeListener( final PropertyChangeListener listener ) {
        listeners.remove(listener);
    }

    /**
     * @param entry
     */
    public final void removeEntry( final VdbEntry entry ) {
        entry.dispose();
        if (entry instanceof VdbModelEntry) modelEntries.remove(entry);
        else entries.remove(entry);
        setModified(ENTRY_REMOVED, entry, null);
    }

    /**
     * @param monitor
     */
    public final void save( final IProgressMonitor monitor ) {
        // TODO: implement
        if (isModified()) modified.set(false);
        setModified(SAVED, null, null);
    }

    /**
     * @param description Sets description to the specified value.
     */
    public final void setDescription( final String description ) {
        this.description.set(description);
        setModified(DESCRIPTION, null, description);
    }

    private void setModified( final String propertyName,
                              final Object oldValue,
                              final Object newValue ) {
        this.modified.set(true);
        notifyChangeListeners(propertyName, oldValue, newValue);
    }

    /**
     * @param entries
     * @param monitor
     */
    public final void synchronize( final Collection<VdbEntry> entries,
                                   final IProgressMonitor monitor ) {
        for (final VdbEntry entry : entries)
            if (entry.getSynchronization() == Synchronization.NotSynchronized) entry.synchronize(monitor);
    }

    /**
     * @param monitor
     */
    public final void synchronize( final IProgressMonitor monitor ) {
        synchronize(new HashSet<VdbEntry>(modelEntries), monitor);
        synchronize(entries, monitor);
    }
}
