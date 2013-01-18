/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.komodo.teiid.model.vdb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.komodo.common.util.CollectionUtil;
import org.komodo.common.util.HashCode;
import org.komodo.common.util.Precondition;

/**
 * The Teiid VDB business object.
 */
public class Vdb extends VdbAdminObject {

    /**
     * The VDB manifest (<code>vdb.xml</code>) VDB-related identifiers.
     */
    public interface ManifestId {

        /**
         * The VDB element attribute identifiers.
         */
        interface Attributes {

            /**
             * The VDB name attribute identifier.
             */
            String NAME = "name"; //$NON-NLS-1$

            /**
             * The VDB version attribute identifier.
             */
            String VERSION = "version"; //$NON-NLS-1$
        }

        /**
         * The VDB data role element identifier. Zero or more data roles are allowed.
         */
        String DATA_POLICY = "data-role"; //$NON-NLS-1$

        /**
         * The VDB description element identifier. The description is optional.
         */
        String DESCRIPTION = "description"; //$NON-NLS-1$

        /**
         * The VDB entry element identifier. Zero or more VDB entries are allowed.
         */
        String ENTRY = "entry"; //$NON-NLS-1$

        /**
         * The import VDB element identifier. Zero or more import VDB names are allowed.
         */
        String IMPORT_VDB = "import-vdb"; //$NON-NLS-1$

        /**
         * The VDB schema/model element identifier. Zero or more VDB models are allowed.
         */
        String SCHEMA = "model"; //$NON-NLS-1$

        /**
         * The VDB translator element identifier. Zero or more VDB translators are allowed.
         */
        String TRANSLATOR = "translator"; //$NON-NLS-1$

        /**
         * The VDB element identifier that is the one and only child element under <code>xs:schema</code>.
         */
        String VDB_ELEMENT = "vdb"; //$NON-NLS-1$
    }

    /**
     * VDB property names.
     */
    public interface PropertyName extends VdbAdminObject.PropertyName {

        /**
         * A collection of data policies.
         */
        String DATA_POLICIES = Vdb.class.getSimpleName() + ".dataPolicies"; //$NON-NLS-1$

        /**
         * A collection of entries.
         */
        String ENTRIES = Vdb.class.getSimpleName() + ".entries"; //$NON-NLS-1$

        /**
         * A collection of import VDBs.
         */
        String IMPORT_VDBS = Vdb.class.getSimpleName() + ".importVdbs"; //$NON-NLS-1$

        /**
         * A collection of models.
         */
        String MODELS = Vdb.class.getSimpleName() + ".models"; //$NON-NLS-1$

        /**
         * A collection of translators.
         */
        String TRANSLATORS = Vdb.class.getSimpleName() + ".translators"; //$NON-NLS-1$

        /**
         * The VDB version.
         */
        String VERSION = Vdb.class.getSimpleName() + ".version"; //$NON-NLS-1$
    }

    /**
     * The default version number for a VDB. Value is {@value}.
     */
    public static final int DEFAULT_VERSION = 1;

    private static final List<DataPolicy> NO_DATA_POLICIES = Collections.emptyList();
    private static final List<Entry> NO_ENTRIES = Collections.emptyList();
    private static final List<ImportVdb> NO_IMPORT_VDBS = Collections.emptyList();
    private static final List<Schema> NO_MODELS = Collections.emptyList();
    private static final List<Translator> NO_TRANSLATORS = Collections.emptyList();

    private List<DataPolicy> dataPolicies;
    private List<Entry> entries;
    private List<ImportVdb> importVdbs;
    private List<Schema> models;
    private List<Translator> translators;
    private int version = DEFAULT_VERSION;

    /**
     * Generates a property change event if the collection of data policies is changed.
     * 
     * @param newDataPolicy the data policy being added (cannot be <code>null</code>)
     */
    public void addDataPolicy(final DataPolicy newDataPolicy) {
        Precondition.notNull(newDataPolicy, "newDataPolicy"); //$NON-NLS-1$

        if (this.dataPolicies == null) {
            this.dataPolicies = new ArrayList<DataPolicy>();
        }

        final List<DataPolicy> oldValue = getDataPolicies();

        if (this.dataPolicies.add(newDataPolicy)) {
            firePropertyChangeEvent(PropertyName.DATA_POLICIES, oldValue, getDataPolicies());
        } else if (this.dataPolicies.isEmpty()) {
            this.dataPolicies = null;
        }
    }

    /**
     * Generates a property change event if the collection of entries is changed.
     * 
     * @param newEntry the entry being added (cannot be <code>null</code>)
     */
    public void addEntry(final Entry newEntry) {
        Precondition.notNull(newEntry, "newEntry"); //$NON-NLS-1$

        if (this.entries == null) {
            this.entries = new ArrayList<Entry>();
        }

        final List<Entry> oldValue = getEntries();

        if (this.entries.add(newEntry)) {
            firePropertyChangeEvent(PropertyName.ENTRIES, oldValue, getEntries());
        } else if (this.entries.isEmpty()) {
            this.entries = null;
        }
    }

    /**
     * Generates a property change event if the collection of import VDBs is changed.
     * 
     * @param newImportVdb the import VDB being added (cannot be <code>null</code>)
     */
    public void addImportVdb(final ImportVdb newImportVdb) {
        Precondition.notNull(newImportVdb, "newImportVdb"); //$NON-NLS-1$

        if (this.importVdbs == null) {
            this.importVdbs = new ArrayList<ImportVdb>();
        }

        final List<ImportVdb> oldValue = getImportVdbs();

        if (this.importVdbs.add(newImportVdb)) {
            firePropertyChangeEvent(PropertyName.IMPORT_VDBS, oldValue, getImportVdbs());
        } else if (this.importVdbs.isEmpty()) {
            this.importVdbs = null;
        }
    }

    /**
     * Generates a property change event if the collection of models is changed.
     * 
     * @param newModel the model being added (cannot be <code>null</code>)
     */
    public void addModel(final Schema newModel) {
        Precondition.notNull(newModel, "newModel"); //$NON-NLS-1$

        if (this.models == null) {
            this.models = new ArrayList<Schema>();
        }

        final List<Schema> oldValue = getModels();

        if (this.models.add(newModel)) {
            firePropertyChangeEvent(PropertyName.MODELS, oldValue, getModels());
        } else if (this.models.isEmpty()) {
            this.models = null;
        }
    }

    /**
     * Generates a property change event if the collection of translators is changed.
     * 
     * @param newTranslator the translator being added (cannot be <code>null</code>)
     */
    public void addTranslator(final Translator newTranslator) {
        Precondition.notNull(newTranslator, "newTranslator"); //$NON-NLS-1$

        if (this.translators == null) {
            this.translators = new ArrayList<Translator>();
        }

        final List<Translator> oldValue = getTranslators();

        if (this.translators.add(newTranslator)) {
            firePropertyChangeEvent(PropertyName.TRANSLATORS, oldValue, getTranslators());
        } else if (this.translators.isEmpty()) {
            this.translators = null;
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.teiid.model.vdb.VdbAdminObject#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object that) {
        if (super.equals(that)) {
            final Vdb thatVdb = (Vdb)that;

            return ((this.version == thatVdb.version) && CollectionUtil.matches(this.dataPolicies, thatVdb.dataPolicies)
                    && CollectionUtil.matches(this.entries, thatVdb.entries)
                    && CollectionUtil.matches(this.importVdbs, thatVdb.importVdbs)
                    && CollectionUtil.matches(this.models, thatVdb.models) && CollectionUtil.matches(this.translators,
                                                                                                     thatVdb.translators));
        }

        return false;
    }

    /**
     * @return an unmodifiable collection of data policies (never <code>null</code> but can be empty)
     */
    public List<DataPolicy> getDataPolicies() {
        if (this.dataPolicies == null) {
            return NO_DATA_POLICIES;
        }

        return Collections.unmodifiableList(this.dataPolicies);
    }

    /**
     * @return an unmodifiable collection of entries (never <code>null</code> but can be empty)
     */
    public List<Entry> getEntries() {
        if (this.entries == null) {
            return NO_ENTRIES;
        }

        return Collections.unmodifiableList(this.entries);
    }

    /**
     * @return an unmodifiable collection of import VDBs (never <code>null</code> but can be empty)
     */
    public List<ImportVdb> getImportVdbs() {
        if (this.importVdbs == null) {
            return NO_IMPORT_VDBS;
        }

        return Collections.unmodifiableList(this.importVdbs);
    }

    /**
     * @return an unmodifiable collection of models (never <code>null</code> but can be empty)
     */
    public List<Schema> getModels() {
        if (this.models == null) {
            return NO_MODELS;
        }

        return Collections.unmodifiableList(this.models);
    }

    /**
     * @return an unmodifiable collection of translators (never <code>null</code> but can be empty)
     */
    public List<Translator> getTranslators() {
        if (this.translators == null) {
            return NO_TRANSLATORS;
        }

        return Collections.unmodifiableList(this.translators);
    }

    /**
     * @return the version
     */
    public int getVersion() {
        return this.version;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.teiid.model.vdb.VdbAdminObject#hashCode()
     */
    @Override
    public int hashCode() {
        return HashCode.compute(super.hashCode(),
                                this.dataPolicies,
                                this.entries,
                                this.importVdbs,
                                this.models,
                                this.translators,
                                this.version);
    }

    /**
     * Generates a property change event if the collection of data policies is changed.
     * 
     * @param dataPolicyToDelete the data policy being deleted (cannot be <code>null</code>)
     */
    public void removeDataPolicy(final DataPolicy dataPolicyToDelete) {
        Precondition.notNull(dataPolicyToDelete, "dataPolicyToDelete"); //$NON-NLS-1$

        if (this.dataPolicies != null) {
            final List<DataPolicy> oldValue = getDataPolicies();

            if (this.dataPolicies.remove(dataPolicyToDelete)) {
                if (this.dataPolicies.isEmpty()) {
                    this.dataPolicies = null;
                }

                firePropertyChangeEvent(PropertyName.DATA_POLICIES, oldValue, getDataPolicies());
            }
        }
    }

    /**
     * Generates a property change event if the collection of entries is changed.
     * @param entryToDelete the entry being deleted (cannot be <code>null</code>)
     */
    public void removeEntry(final Entry entryToDelete) {
        Precondition.notNull(entryToDelete, "entryToDelete"); //$NON-NLS-1$

        if (this.entries != null) {
            final List<Entry> oldValue = getEntries();

            if (this.entries.remove(entryToDelete)) {
                if (this.entries.isEmpty()) {
                    this.entries = null;
                }

                firePropertyChangeEvent(PropertyName.ENTRIES, oldValue, getEntries());
            }
        }
    }

    /**
     * Generates a property change event if the collection of import VDBs is changed.
     * @param importVdbToDelete the import VDB being deleted (cannot be <code>null</code>)
     */
    public void removeImportVdb(final ImportVdb importVdbToDelete) {
        Precondition.notNull(importVdbToDelete, "importVdbToDelete"); //$NON-NLS-1$

        if (this.importVdbs != null) {
            final List<ImportVdb> oldValue = getImportVdbs();

            if (this.importVdbs.remove(importVdbToDelete)) {
                if (this.importVdbs.isEmpty()) {
                    this.importVdbs = null;
                }

                firePropertyChangeEvent(PropertyName.IMPORT_VDBS, oldValue, getImportVdbs());
            }
        }
    }

    /**
     * Generates a property change event if the collection of models is changed.
     * @param modelToDelete the model being deleted (cannot be <code>null</code>)
     */
    public void removeModel(final Schema modelToDelete) {
        Precondition.notNull(modelToDelete, "modelToDelete"); //$NON-NLS-1$

        if (this.models != null) {
            final List<Schema> oldValue = getModels();

            if (this.models.remove(modelToDelete)) {
                if (this.models.isEmpty()) {
                    this.models = null;
                }

                firePropertyChangeEvent(PropertyName.MODELS, oldValue, getModels());
            }
        }
    }

    /**
     * Generates a property change event if the collection of translators is changed.
     * @param translatorToDelete the translator being deleted (cannot be <code>null</code>)
     */
    public void removeTranslator(final Translator translatorToDelete) {
        Precondition.notNull(translatorToDelete, "translatorToDelete"); //$NON-NLS-1$

        if (this.translators != null) {
            final List<Translator> oldValue = getTranslators();

            if (this.translators.remove(translatorToDelete)) {
                if (this.translators.isEmpty()) {
                    this.translators = null;
                }

                firePropertyChangeEvent(PropertyName.TRANSLATORS, oldValue, getTranslators());
            }
        }
    }

    /**
     * Generates a property change event if the version is changed.
     * @param newVersion the new version
     */
    public void setVersion(final int newVersion) {
        if (this.version != newVersion) {
            final int oldValue = this.version;
            this.version = newVersion;
            firePropertyChangeEvent(PropertyName.VERSION, oldValue, this.version);

            assert (this.version == newVersion);
            assert (this.version != oldValue);
        }
    }

}
