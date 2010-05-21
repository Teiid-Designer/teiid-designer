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
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import net.jcip.annotations.ThreadSafe;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.teiid.designer.vdb.VdbEntry.Synchronization;
import org.teiid.designer.vdb.manifest.EntryElement;
import org.teiid.designer.vdb.manifest.ModelElement;
import org.teiid.designer.vdb.manifest.VdbElement;
import org.teiid.designer.vdb.plugin.VdbPlugin;
import org.xml.sax.SAXException;
import com.metamatrix.core.modeler.util.FileUtils;
import com.metamatrix.core.modeler.util.OperationUtil;
import com.metamatrix.core.modeler.util.OperationUtil.Unreliable;
import com.metamatrix.core.util.StringUtilities;

/**
 * 
 */
@ThreadSafe
public final class Vdb {

    /**
     * The file extension of VDBs ( {@value} )
     */
    public static final String FILE_EXTENSION = ".vdb"; //$NON-NLS-1$

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
     * {@link VdbEntry#getChecksum() checksum} changes
     */
    public static final String CHECKSUM = "checksum"; //$NON-NLS-1$

    /**
     * The property name sent in events to {@link #addChangeListener(PropertyChangeListener) change listeners} when a VDB entry's
     * {@link VdbEntry#getSynchronization() synchronization} changes
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

    private static final String MANIFEST = "META-INF/vdb.xml"; //$NON-NLS-1$

    private final IPath name;
    final IFile file;
    private final File folder;
    final CopyOnWriteArraySet<VdbEntry> entries = new CopyOnWriteArraySet<VdbEntry>();
    final CopyOnWriteArraySet<VdbModelEntry> modelEntries = new CopyOnWriteArraySet<VdbModelEntry>();
    private final CopyOnWriteArrayList<PropertyChangeListener> listeners = new CopyOnWriteArrayList<PropertyChangeListener>();
    final AtomicBoolean modified = new AtomicBoolean();
    private final AtomicReference<String> description = new AtomicReference<String>();

    /**
     * @param file
     * @param monitor
     */
    public Vdb( final IFile file,
                final IProgressMonitor monitor ) {
        this.file = file;
        name = file.getFullPath();
        // Create folder for VDB in state folder
        folder = VdbPlugin.singleton.getStateLocation().append(name).toFile();
        folder.mkdirs();
        // Open archive and populate model entries
        if (file.getLocation().toFile().length() == 0) return;
        OperationUtil.perform(new Unreliable() {

            ZipFile archive = null;
            InputStream entryStream = null;

            @Override
            public void doIfFails() {
            }

            @Override
            public void finallyDo() throws Exception {
                if (entryStream != null) entryStream.close();
                if (archive != null) archive.close();
            }

            @Override
            public void tryToDo() throws Exception {
                archive = new ZipFile(file.getLocation().toString());
                for (final Enumeration<? extends ZipEntry> iter = archive.entries(); iter.hasMoreElements();) {
                    final ZipEntry zipEntry = iter.nextElement();
                    entryStream = archive.getInputStream(zipEntry);
                    if (zipEntry.getName().equals(MANIFEST)) {
                        // Initialize using manifest
                        final Unmarshaller unmarshaller = getJaxbContext().createUnmarshaller();
                        unmarshaller.setSchema(getManifestSchema());
                        final VdbElement manifest = (VdbElement)unmarshaller.unmarshal(entryStream);
                        setDescription(manifest.getDescription());
                        for (final EntryElement element : manifest.getEntries())
                            entries.add(new VdbEntry(Vdb.this, element, zipEntry.getComment(), monitor));
                        for (final ModelElement element : manifest.getModels())
                            modelEntries.add(new VdbModelEntry(Vdb.this, element, zipEntry.getComment(), monitor));
                        // Initialize model entry imports only after all model entries have been created
                        for (final VdbModelEntry entry : modelEntries)
                            entry.initializeImports();
                    } else FileUtils.copy(entryStream, new File(getFolder(), zipEntry.getName()));
                }
                modified.set(false);
            }
        });
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
        return addEntry(new VdbEntry(this, name, monitor), entries, monitor);
    }

    private <T extends VdbEntry> T addEntry( final T entry,
                                             final Set<T> entries,
                                             final IProgressMonitor monitor ) {
        // Return existing entry if it exists
        if (!entries.add(entry)) for (final T existingEntry : entries)
            if (existingEntry.equals(entry)) return entry;
        // Mark VDB as modified
        setModified(this, ENTRY_ADDED, null, entry);
        return entry;
    }

    /**
     * @param name
     * @param monitor
     * @return the newly added {@link VdbModelEntry model entry}, or the existing entry with the supplied name.
     */
    public final VdbModelEntry addModelEntry( final IPath name,
                                              final IProgressMonitor monitor ) {
        return addEntry(new VdbModelEntry(this, name, monitor), modelEntries, monitor);
    }

    /**
     * 
     */
    public final void close() {
        entries.clear();
        modelEntries.clear();
        listeners.clear();
        description.set(StringUtilities.EMPTY_STRING);
        // Clean up state folder
        FileUtils.removeDirectoryAndChildren(VdbPlugin.singleton.getStateLocation().append(name.segment(0)).toFile());
        // Mark VDB as unmodified
        if (isModified()) modified.set(false);
        // Notify change listeners VDB is closed
        notifyChangeListeners(this, CLOSED, null, null);
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
     * @return The workspace file that represents this VDB
     */
    public final IFile getFile() {
        return file;
    }

    final File getFolder() {
        return folder;
    }

    JAXBContext getJaxbContext() throws JAXBException {
        return JAXBContext.newInstance(new Class<?>[] {VdbElement.class});
    }

    Schema getManifestSchema() throws SAXException {
        final SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        return schemaFactory.newSchema(VdbElement.class.getResource("/vdb-deployer.xsd")); //$NON-NLS-1$
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
        setModified(this, ENTRY_REMOVED, entry, null);
    }

    /**
     * @param monitor
     */
    public final void save( final IProgressMonitor monitor ) {
        // Build JAXB model
        final VdbElement vdbElement = new VdbElement(this);
        // Save archive
        final File tmpFolder = new File(System.getProperty("java.io.tmpdir")); //$NON-NLS-1$
        final File tmpArchive = new File(tmpFolder, name.toString());
        tmpArchive.getParentFile().mkdirs();
        OperationUtil.perform(new Unreliable() {

            ZipOutputStream out = null;

            @Override
            public void doIfFails() {
            }

            @Override
            public void finallyDo() throws Exception {
                if (out != null) out.close();
                FileUtils.removeDirectoryAndChildren(new File(tmpFolder, getName().segment(0)));
            }

            @Override
            public void tryToDo() throws Exception {
                out = new ZipOutputStream(new FileOutputStream(tmpArchive));
                // Create VDB manifest
                final ZipEntry zipEntry = new ZipEntry(MANIFEST);
                zipEntry.setComment(getDescription());
                out.putNextEntry(zipEntry);
                try {
                    final Marshaller marshaller = getJaxbContext().createMarshaller();
                    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
                    marshaller.setSchema(getManifestSchema());
                    marshaller.marshal(vdbElement, out);
                } finally {
                    out.closeEntry();
                }
                // Clear all problem markers on VDB file
                for (final IMarker marker : file.findMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE))
                    marker.delete();
                // Save entries
                for (final VdbEntry entry : entries)
                    entry.save(out, monitor);
                for (final VdbModelEntry entry : modelEntries)
                    entry.save(out, monitor);
                // Replace archive in workspace with temporary archive
                final File archiveFile = ResourcesPlugin.getWorkspace().getRoot().findMember(getName()).getLocation().toFile();
                if (!archiveFile.delete()) throw new RuntimeException(VdbPlugin.UTIL.getString("unableToDelete", archiveFile)); //$NON-NLS-1$
                if (!tmpArchive.renameTo(archiveFile)) throw new RuntimeException(
                                                                                  VdbPlugin.UTIL.getString("unableToRename", tmpArchive, archiveFile)); //$NON-NLS-1$
                // Mark as unmodified
                if (isModified()) modified.set(false);
                // Notify change listeners
                notifyChangeListeners(this, SAVED, null, null);
            }
        });
    }

    /**
     * @param description Sets description to the specified value.
     */
    public final void setDescription( final String description ) {
    	final String oldDescription = this.description.get();
        if( StringUtilities.areSame(description, oldDescription, false)) return;
        this.description.set(description);
        setModified(this, DESCRIPTION, null, description);
    }

    void setModified( final Object source,
                      final String propertyName,
                      final Object oldValue,
                      final Object newValue ) {
        this.modified.set(true);
        notifyChangeListeners(source, propertyName, oldValue, newValue);
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
