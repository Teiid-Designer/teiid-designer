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
import java.util.ArrayList;
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
import org.eclipse.core.runtime.Path;
import org.teiid.designer.roles.DataRole;
import org.teiid.designer.vdb.VdbEntry.Synchronization;
import org.teiid.designer.vdb.manifest.DataRoleElement;
import org.teiid.designer.vdb.manifest.EntryElement;
import org.teiid.designer.vdb.manifest.ModelElement;
import org.teiid.designer.vdb.manifest.PropertyElement;
import org.teiid.designer.vdb.manifest.VdbElement;
import org.xml.sax.SAXException;

import com.metamatrix.core.modeler.util.FileUtils;
import com.metamatrix.core.modeler.util.OperationUtil;
import com.metamatrix.core.modeler.util.OperationUtil.Unreliable;
import com.metamatrix.core.util.StringUtilities;

/**
 * 
 */
@ThreadSafe
// TODO: File constructor
public final class Vdb {

    /**
     * The file extension of VDBs ( {@value} )
     */
    public static final String FILE_EXTENSION = ".vdb"; //$NON-NLS-1$

    private static final String MANIFEST = "META-INF/vdb.xml"; //$NON-NLS-1$

    final IFile file;

    private final File folder;
    final CopyOnWriteArraySet<VdbEntry> entries = new CopyOnWriteArraySet<VdbEntry>();
    final CopyOnWriteArraySet<VdbModelEntry> modelEntries = new CopyOnWriteArraySet<VdbModelEntry>();
    final CopyOnWriteArraySet<VdbDataRole> dataPolicyEntries = new CopyOnWriteArraySet<VdbDataRole>();
    private final CopyOnWriteArrayList<PropertyChangeListener> listeners = new CopyOnWriteArrayList<PropertyChangeListener>();
    final AtomicBoolean modified = new AtomicBoolean();
    private final AtomicReference<String> description = new AtomicReference<String>();
    private final boolean preview;
    private final int version;

    /**
     * @param file
     * @param preview indicates if this is a Preview VDB
     * @param monitor
     */
    public Vdb( final IFile file,
                final boolean preview,
                final IProgressMonitor monitor ) {
        this.file = file;
        // Create folder for VDB in state folder
        folder = VdbPlugin.singleton().getStateLocation().append(file.getFullPath()).toFile();
        folder.mkdirs();
        // Open archive and populate model entries
        if (file.getLocation().toFile().length() == 0L) {
            this.preview = preview;
            this.version = 1;
            return;
        }

        final boolean[] previewable = new boolean[1];
        final int[] vdbVersion = new int[1];
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
                        vdbVersion[0] = manifest.getVersion();
                        // VDB properties
                        for (final PropertyElement property : manifest.getProperties()) {
                            final String name = property.getName();
                            if (Xml.PREVIEW.equals(name)) previewable[0] = Boolean.parseBoolean(property.getValue());
                            else assert false;
                        }
                        for (final EntryElement element : manifest.getEntries())
                            entries.add(new VdbEntry(Vdb.this, element, monitor));
                        for (final ModelElement element : manifest.getModels())
                            modelEntries.add(new VdbModelEntry(Vdb.this, element, monitor));
                        // Initialize model entry imports only after all model entries have been created
                        for (final VdbModelEntry entry : modelEntries)
                            entry.initializeImports();
                        for (final DataRoleElement element : manifest.getDataPolicies()) {
                        	dataPolicyEntries.add(new VdbDataRole(Vdb.this, element));
                        }
                    } else FileUtils.copy(entryStream, new File(getFolder(), zipEntry.getName()));
                }
                modified.set(false);
            }
        });
        this.preview = previewable[0];
        this.version = vdbVersion[0];
    }
    
    /**
     * @param file
     * @param monitor
     */
    public Vdb( final IFile file,
                final IProgressMonitor monitor ) {
        this(file, false, monitor);
    }


    
    /**
     * @param dataPolicy
     * @param monitor
     * @return the new data policy
     */
    public final VdbDataRole addDataPolicy(
    										final DataRole dataPolicy, 
    										final IProgressMonitor monitor ) {
    	VdbDataRole policy = new VdbDataRole(this, dataPolicy, monitor);
    	dataPolicyEntries.add(policy);
    	setModified(this, Event.DATA_POLICY_ADDED, policy, null);
    	return policy;
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
            if (existingEntry.equals(entry)) return existingEntry;
        // Mark VDB as modified
        setModified(this, Event.ENTRY_ADDED, null, entry);
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
        FileUtils.removeDirectoryAndChildren(VdbPlugin.singleton().getStateLocation().append(file.getFullPath().segment(0)).toFile());
        // Mark VDB as unmodified
        if (isModified()) modified.set(false);
        // Notify change listeners VDB is closed
        notifyChangeListeners(this, Event.CLOSED, null, null);
    }

    /**
     * @return the immutable set of entries, not including {@link #getModelEntries() model entries}, within this VDB
     */
    public final Set<VdbDataRole> getDataPolicyEntries() {
        return Collections.unmodifiableSet(dataPolicyEntries);
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
     * Method to return the File objects associated with each model in this VDB.
     * The intention is to allow the Data Policy wizard to display contents of these models in EMF form so users can 
     * pick/chose and set-up their data entitlements.
     * 
     * @return the immutable list of model files within this VDB
     */
    public final Collection<File> getModelFiles() {
    	final Collection<File> modelFiles = new ArrayList<File>();
    	
    	for( VdbModelEntry modelEntry : getModelEntries()) {
    		IPath modelPath = new Path(folder.getAbsolutePath() + modelEntry.getName());
    		modelFiles.add(modelPath.toFile());
    	}
    	
    	return Collections.unmodifiableCollection(modelFiles);
    }

    /**
     * @return the name of this VDB
     */
    public final IPath getName() {
        return file.getFullPath();
    }

    /**
     * @return <code>true</code> if this VDB has been modified since its creation of last {@link #save(IProgressMonitor) save}.
     */
    public final boolean isModified() {
        return modified.get();
    }
    
    /**
     * @return <code>true</code> if this is a Preview VDB
     */
    public final boolean isPreview() {
        return preview;
    }
    
    /**
     * @return the problem markers (never <code>null</code>)
     * @throws Exception if there is a problem obtaining the problem markers
     */
    public IMarker[] getProblems() throws Exception {
        return file.findMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);
    }
    
    /**
     * @return the VDB version
     */
    public int getVersion() {
        return version;
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
        if( !isPreview() ) {
	        for (final PropertyChangeListener listener : listeners) {
	            if (event == null) event = new PropertyChangeEvent(this, propertyName, oldValue, newValue);
	            listener.propertyChange(event);
	        }
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
        setModified(this, Event.ENTRY_REMOVED, entry, null);
    }
    
    /**
     * @param policy
     */
    public final void removeDataPolicy( final VdbDataRole policy ) {
        dataPolicyEntries.remove(policy);
        setModified(this, Event.DATA_POLICY_REMOVED, policy, null);
    }

    /**
     * Must not be called unless this VDB has been {@link #isModified() modified}
     * 
     * @param monitor
     */
    public final void save( final IProgressMonitor monitor ) {
        // Build JAXB model
        final VdbElement vdbElement = new VdbElement(this);
        // Save archive
        final File tmpFolder = VdbPlugin.singleton().getStateLocation().toFile();
        OperationUtil.perform(new Unreliable() {

            ZipOutputStream out = null;

            @Override
            public void doIfFails() {
            }

            @Override
            public void finallyDo() throws Exception {
                if (out != null) out.close();
            }

            @Override
            public void tryToDo() throws Exception {
                final IPath path = file.getFullPath();
                final File tmpArchive = File.createTempFile(path.removeFileExtension().toString(),
                                                            '.' + path.getFileExtension(),
                                                            tmpFolder);
                tmpArchive.getParentFile().mkdirs();
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
                	if( entry instanceof VdbEntry ) {
                		((VdbEntry)entry).save(out, monitor);
                	}
                
                for (final VdbModelEntry entry : modelEntries)
                    entry.save(out, monitor);
                
                // Close zip output stream so its fully writen and any locks are removed.
                out.close();
                out = null;
                // Replace archive in workspace with temporary archive
                final File archiveFile = ResourcesPlugin.getWorkspace().getRoot().findMember(getName()).getLocation().toFile();
                if (!archiveFile.delete()) throw new RuntimeException(VdbPlugin.UTIL.getString("unableToDelete", archiveFile)); //$NON-NLS-1$
                if (!tmpArchive.renameTo(archiveFile)) throw new RuntimeException(
                                                                                  VdbPlugin.UTIL.getString("unableToRename", tmpArchive, archiveFile)); //$NON-NLS-1$
                // Mark as unmodified
                if (isModified()) modified.set(false);
                // Notify change listeners
                notifyChangeListeners(this, Event.SAVED, null, null);
            }
        });
    }

    /**
     * @param description Sets description to the specified value.
     */
    public final void setDescription( String description ) {
        if (StringUtilities.isEmpty(description)) description = null;
        final String oldDescription = this.description.get();
        if (StringUtilities.equals(description, oldDescription)) return;
        this.description.set(description);
        setModified(this, Event.DESCRIPTION, oldDescription, description);
    }

    void setModified( final Object source,
                      final String propertyName,
                      final Object oldValue,
                      final Object newValue ) {
        this.modified.set(true);
        notifyChangeListeners(source, propertyName, oldValue, newValue);
    }

    private final void synchronize( final Collection<VdbEntry> entries,
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

    /**
     *
     */
    public static class Event {

        /**
         * The property name sent in events to {@link #addChangeListener(PropertyChangeListener) change listeners} when the
         * {@link #getDescription() description} in a VDB is changed
         */
        public static final String DESCRIPTION = "description"; //$NON-NLS-1$

        /**
         * The property name sent in events to {@link #addChangeListener(PropertyChangeListener) change listeners} when an entry
         * is added to a VDB
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
         * The property name sent in events to {@link #addChangeListener(PropertyChangeListener) change listeners} when a VDB
         * entry's {@link VdbEntry#getChecksum() checksum} changes
         */
        public static final String ENTRY_CHECKSUM = "entry.checksum"; //$NON-NLS-1$

        /**
         * The property name sent in events to {@link #addChangeListener(PropertyChangeListener) change listeners} when a VDB
         * entry's {@link VdbEntry#getSynchronization() synchronization} changes
         */
        public static final String ENTRY_SYNCHRONIZATION = "entry.synchronization"; //$NON-NLS-1$

        /**
         * The property name sent in events to {@link #addChangeListener(PropertyChangeListener) change listeners} when VDB
         * entry's {@link VdbEntry#getDescription() description} changes
         */
        public static final String ENTRY_DESCRIPTION = "entry.description"; //$NON-NLS-1$

        /**
         * The property name sent in events to {@link #addChangeListener(PropertyChangeListener) change listeners} when a VDB
         * model entry's {@link VdbModelEntry#isVisible() visibility} changes
         */
        public static final String MODEL_VISIBLE = "modelentry.visible"; //$NON-NLS-1$

        /**
         * The property name sent in events to {@link #addChangeListener(PropertyChangeListener) change listeners} when a VDB
         * physical model entry's {@link VdbModelEntry#getSourceName() source name} changes
         */
        public static final String MODEL_SOURCE_NAME = "modelentry.sourceName"; //$NON-NLS-1$

        /**
         * The property name sent in events to {@link #addChangeListener(PropertyChangeListener) change listeners} when a VDB
         * physical model entry's {@link VdbModelEntry#getTranslator() translator} changes
         */
        public static final String MODEL_TRANSLATOR = "modelEntry.translator"; //$NON-NLS-1$

        /**
         * The property name sent in events to {@link #addChangeListener(PropertyChangeListener) change listeners} when a VDB
         * physical model entry's {@link VdbModelEntry#getJndiName() JNDI name} changes
         */
        public static final String MODEL_JNDI_NAME = "modelEntry.jndiName"; //$NON-NLS-1$
    
	    /**
	     * The property name sent in events to {@link #addChangeListener(PropertyChangeListener) change listeners} when an data policy is
	     * added to a VDB
	     * 
	     * @see #addDataPolicy(DataRole, IProgressMonitor)
	     */
	    public static final String DATA_POLICY_ADDED = "dataPolicyAdded"; //$NON-NLS-1$
	
	    /**
	     * The property name sent in events to {@link #addChangeListener(PropertyChangeListener) change listeners} when an
	     * {@link #removeDataPolicy(VdbDataRole) entry is removed} from a VDB
	     */
	    public static final String DATA_POLICY_REMOVED = "dataPolicyRemoved"; //$NON-NLS-1$

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
    }

    /**
     * Constants relating to the vdb.xml file.
     */
    public static class Xml {

        /**
         */
        public static final String PREVIEW = "preview"; //$NON-NLS-1$
    }
}
