/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.komodo.repository.sramp.teiid;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.Map;
import org.komodo.common.util.HashCode;
import org.komodo.common.util.Precondition;
import org.komodo.repository.artifact.Artifact;
import org.komodo.repository.artifact.teiid.VdbArtifact;
import org.komodo.repository.sramp.SrampArtifact;
import org.komodo.repository.sramp.SrampArtifactProperties;
import org.komodo.repository.sramp.SrampRepository;
import org.komodo.teiid.model.vdb.DataPolicy;
import org.komodo.teiid.model.vdb.Entry;
import org.komodo.teiid.model.vdb.ImportVdb;
import org.komodo.teiid.model.vdb.Schema;
import org.komodo.teiid.model.vdb.Translator;
import org.komodo.teiid.model.vdb.Vdb;
import org.komodo.teiid.model.vdb.VdbObject;
import org.overlord.sramp.common.SrampModelUtils;
import org.s_ramp.xmlns._2010.s_ramp.BaseArtifactType;
import org.s_ramp.xmlns._2010.s_ramp.ExtendedArtifactType;

/**
 * A VDB artifact.
 */
final class SrampVdbArtifact extends Vdb implements VdbArtifact, SrampArtifact {

    private final ExtendedArtifactType delegate;
    private final SrampRepository repository;

    SrampVdbArtifact(final ExtendedArtifactType srampArtifact,
                     final SrampRepository repository) {
        Precondition.notNull(srampArtifact, "srampArtifact"); //$NON-NLS-1$
        Precondition.notNull(repository, "repository"); //$NON-NLS-1$

        this.delegate = srampArtifact;
        this.repository = repository;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.teiid.model.vdb.Vdb#addDataPolicy(org.komodo.teiid.model.vdb.DataPolicy)
     */
    @Override
    public final void addDataPolicy(final DataPolicy newDataPolicy) {
        throw new UnsupportedOperationException(); // TODO add message
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.teiid.model.vdb.Vdb#addEntry(org.komodo.teiid.model.vdb.Entry)
     */
    @Override
    public final void addEntry(final Entry newEntry) {
        throw new UnsupportedOperationException(); // TODO add message
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.teiid.model.vdb.Vdb#addImportVdb(org.komodo.teiid.model.vdb.ImportVdb)
     */
    @Override
    public final void addImportVdb(final ImportVdb newImportVdb) {
        throw new UnsupportedOperationException(); // TODO add message
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.teiid.model.vdb.VdbObject#addListener(java.beans.PropertyChangeListener)
     */
    @Override
    public final boolean addListener(final PropertyChangeListener newListener) {
        throw new UnsupportedOperationException(); // TODO add message
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.teiid.model.vdb.Vdb#addSchema(org.komodo.teiid.model.vdb.Schema)
     */
    @Override
    public final void addSchema(final Schema newSchema) {
        throw new UnsupportedOperationException(); // TODO add message
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.teiid.model.vdb.Vdb#addTranslator(org.komodo.teiid.model.vdb.Translator)
     */
    @Override
    public final void addTranslator(final Translator newTranslator) {
        throw new UnsupportedOperationException(); // TODO add message
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.teiid.model.vdb.VdbObject#compareTo(org.komodo.teiid.model.vdb.VdbObject)
     */
    @Override
    public int compareTo(final VdbObject that) {
        final int result = super.compareTo(that);

        if (result == 0) {
            return this.delegate.getUuid().compareTo(((SrampVdbArtifact)that).delegate.getUuid());
        }

        return result;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.teiid.model.vdb.VdbObject#createPropertyChangeEvent(java.lang.String, java.lang.Object, java.lang.Object)
     */
    @Override
    protected final PropertyChangeEvent createPropertyChangeEvent(final String name,
                                                                  final Object oldValue,
                                                                  final Object newValue) {
        throw new UnsupportedOperationException(); // TODO add message
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.teiid.model.vdb.Permission#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object that) {
        if ((that == null) || !getClass().equals(that.getClass())) {
            return false;
        }

        return getArtifactUuid().equals(((Artifact)that).getArtifactUuid());
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.teiid.model.vdb.VdbObject#firePropertyChangeEvent(java.lang.String, java.lang.Object, java.lang.Object)
     */
    @Override
    protected final void firePropertyChangeEvent(final String name,
                                                 final Object oldValue,
                                                 final Object newValue) {
        throw new UnsupportedOperationException(); // TODO add message
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.repository.artifact.Artifact#getArtifactName()
     */
    @Override
    public String getArtifactName() {
        return this.delegate.getName();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.repository.artifact.Artifact#getArtifactType()
     */
    @Override
    public Type getArtifactType() {
        return VdbArtifact.TYPE;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.repository.artifact.Artifact#getArtifactUuid()
     */
    @Override
    public String getArtifactUuid() {
        return this.delegate.getUuid();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.repository.artifact.Artifact#getArtifactVersion()
     */
    @Override
    public String getArtifactVersion() {
        return this.delegate.getVersion();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.teiid.model.vdb.Vdb#getDataPolicies()
     */
    @Override
    public List<DataPolicy> getDataPolicies() {

        // TODO Auto-generated method stub
        return super.getDataPolicies();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.repository.sramp.SrampArtifact#getDelegate()
     */
    @Override
    public BaseArtifactType getDelegate() {
        return this.delegate;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.teiid.model.vdb.VdbAdminObject#getDescription()
     */
    @Override
    public String getDescription() {
        return this.delegate.getDescription();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.teiid.model.vdb.Vdb#getEntries()
     */
    @Override
    public List<Entry> getEntries() {

        // TODO Auto-generated method stub
        return super.getEntries();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.teiid.model.vdb.VdbObject#getId()
     */
    @Override
    public String getId() {
        return getArtifactName();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.teiid.model.vdb.Vdb#getImportVdbs()
     */
    @Override
    public List<ImportVdb> getImportVdbs() {
        //        if (false) {
        //            String pattern = Sramp.USER_DEFINED_ARTIFACT_PATH + "['%s'[@uuid = '%s']]";
        //            return String.format(VdbArtifact.DERIVED_RELATIONSHIP.getId(), uuid);
        //        }
        //
        // TODO Auto-generated method stub
        return super.getImportVdbs();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.teiid.model.vdb.VdbAdminObject#getProperties()
     */
    @Override
    public Map<String, String> getProperties() {
        return new SrampArtifactProperties(this.delegate.getProperty());
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.teiid.model.vdb.VdbAdminObject#getProperty(java.lang.String)
     */
    @Override
    public String getProperty(final String name) {
        return SrampModelUtils.getCustomProperty(this.delegate, name);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.teiid.model.vdb.Vdb#getSchemas()
     */
    @Override
    public List<Schema> getSchemas() {
        // TODO Auto-generated method stub
        return super.getSchemas();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.teiid.model.vdb.Vdb#getTranslators()
     */
    @Override
    public List<Translator> getTranslators() {
        // TODO Auto-generated method stub
        return super.getTranslators();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.teiid.model.vdb.Vdb#getVersion()
     */
    @Override
    public String getVersion() {
        return this.delegate.getVersion();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.teiid.model.vdb.Vdb#hashCode()
     */
    @Override
    public int hashCode() {
        return HashCode.compute(getArtifactUuid());
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.teiid.model.vdb.Vdb#removeDataPolicy(org.komodo.teiid.model.vdb.DataPolicy)
     */
    @Override
    public final void removeDataPolicy(final DataPolicy dataPolicyToDelete) {
        throw new UnsupportedOperationException(); // TODO add message
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.teiid.model.vdb.Vdb#removeEntry(org.komodo.teiid.model.vdb.Entry)
     */
    @Override
    public final void removeEntry(final Entry entryToDelete) {
        throw new UnsupportedOperationException(); // TODO add message
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.teiid.model.vdb.Vdb#removeImportVdb(org.komodo.teiid.model.vdb.ImportVdb)
     */
    @Override
    public final void removeImportVdb(final ImportVdb importVdbToDelete) {
        throw new UnsupportedOperationException(); // TODO add message
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.teiid.model.vdb.VdbObject#removeListener(java.beans.PropertyChangeListener)
     */
    @Override
    public final boolean removeListener(final PropertyChangeListener registeredListener) {
        throw new UnsupportedOperationException(); // TODO add message
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.teiid.model.vdb.VdbAdminObject#removeProperty(java.lang.String)
     */
    @Override
    public final void removeProperty(final String name) {
        throw new UnsupportedOperationException(); // TODO add message
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.teiid.model.vdb.Vdb#removeSchema(org.komodo.teiid.model.vdb.Schema)
     */
    @Override
    public final void removeSchema(final Schema schemaToDelete) {
        throw new UnsupportedOperationException(); // TODO add message
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.teiid.model.vdb.Vdb#removeTranslator(org.komodo.teiid.model.vdb.Translator)
     */
    @Override
    public final void removeTranslator(final Translator translatorToDelete) {
        throw new UnsupportedOperationException(); // TODO add message
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.teiid.model.vdb.VdbAdminObject#setDescription(java.lang.String)
     */
    @Override
    public final void setDescription(final String newDescription) {
        throw new UnsupportedOperationException(); // TODO add message
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.teiid.model.vdb.VdbObject#setId(java.lang.String)
     */
    @Override
    public final void setId(final String newId) {
        throw new UnsupportedOperationException(); // TODO add message
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.teiid.model.vdb.VdbAdminObject#setProperty(java.lang.String, java.lang.String)
     */
    @Override
    public final void setProperty(final String name,
                                  final String newValue) {
        throw new UnsupportedOperationException(); // TODO add message
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.teiid.model.vdb.Vdb#setVersion(java.lang.String)
     */
    @Override
    public final void setVersion(final String newVersion) {
        throw new UnsupportedOperationException(); // TODO add message
    }

}
