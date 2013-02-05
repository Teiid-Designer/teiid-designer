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
import java.util.Map;
import org.komodo.common.util.Precondition;
import org.komodo.common.util.StringUtil;
import org.komodo.repository.artifact.Artifact;
import org.komodo.repository.artifact.teiid.PermissionArtifact;
import org.komodo.repository.sramp.SrampArtifact;
import org.komodo.repository.sramp.SrampArtifactProperties;
import org.komodo.repository.sramp.SrampRepository;
import org.komodo.teiid.model.vdb.Permission;
import org.s_ramp.xmlns._2010.s_ramp.BaseArtifactType;
import org.s_ramp.xmlns._2010.s_ramp.ExtendedArtifactType;

/**
 * An S-RAMP data permission artifact.
 */
final class SrampPermissionArtifact extends Permission implements PermissionArtifact, SrampArtifact {

    private final ExtendedArtifactType delegate;
    private SrampArtifactProperties props;
    private final SrampRepository repository;

    /**
     * @param delegate the S-RAMP permission artifact (cannot be <code>null</code>)
     * @param repository the S-RAMP repository where the artifact is found (cannot be <code>null</code>)
     */
    public SrampPermissionArtifact(final ExtendedArtifactType delegate,
                                   final SrampRepository repository) {
        Precondition.notNull(delegate, "delegate"); //$NON-NLS-1$
        Precondition.matchesExactly(delegate.getExtendedType(), PermissionArtifact.TYPE.getId(), "permission artifact type"); //$NON-NLS-1$
        Precondition.notNull(repository, "repository"); //$NON-NLS-1$

        this.delegate = delegate;
        this.repository = repository;
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
        return PermissionArtifact.TYPE;
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
     * @see org.komodo.repository.sramp.SrampArtifact#getDelegate()
     */
    @Override
    public BaseArtifactType getDelegate() {
        return this.delegate;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.teiid.model.Describable#getDescription()
     */
    @Override
    public String getDescription() {
        return this.delegate.getDescription();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.teiid.model.Propertied#getProperties()
     */
    @Override
    public Map<String, String> getProperties() {
        if (this.props == null) {
            this.props = new SrampArtifactProperties(this.delegate.getProperty());
        }

        return this.props;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.teiid.model.Propertied#getProperty(java.lang.String)
     */
    @Override
    public String getProperty(final String name) {
        Precondition.notEmpty(name, "name"); //$NON-NLS-1$
        return getProperties().get(name);
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
     * @see org.komodo.teiid.model.Propertied#removeProperty(java.lang.String)
     */
    @Override
    public void removeProperty(final String name) {
        throw new UnsupportedOperationException(); // TODO add message
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.teiid.model.vdb.Permission#alterable()
     */
    @Override
    public boolean alterable() {
        final String canAlter = getProperty(Permission.PropertyName.ALTERABLE);
        return (StringUtil.isEmpty(canAlter) ? false : Boolean.parseBoolean(canAlter));
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.teiid.model.vdb.Permission#creatable()
     */
    @Override
    public boolean creatable() {
        final String canCreate = getProperty(Permission.PropertyName.CREATABLE);
        return (StringUtil.isEmpty(canCreate) ? false : Boolean.parseBoolean(canCreate));
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.teiid.model.vdb.Permission#deletable()
     */
    @Override
    public boolean deletable() {
        final String canDelete = getProperty(Permission.PropertyName.DELETABLE);
        return (StringUtil.isEmpty(canDelete) ? false : Boolean.parseBoolean(canDelete));
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
     * @see org.komodo.teiid.model.vdb.Permission#executable()
     */
    @Override
    public boolean executable() {
        final String canExecute = getProperty(Permission.PropertyName.EXECUTABLE);
        return (StringUtil.isEmpty(canExecute) ? false : Boolean.parseBoolean(canExecute));
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.teiid.model.vdb.Permission#getCondition()
     */
    @Override
    public String getCondition() {
        return getProperty(Permission.PropertyName.CONDITION);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.teiid.model.vdb.Permission#getResourceName()
     */
    @Override
    public String getResourceName() {
        return getArtifactName();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.teiid.model.vdb.Permission#hashCode()
     */
    @Override
    public int hashCode() {
        // TODO Auto-generated method stub
        return super.hashCode();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.teiid.model.vdb.Permission#languagable()
     */
    @Override
    public boolean languagable() {
        final String canLanguage = getProperty(Permission.PropertyName.LANGUAGABLE);
        return (StringUtil.isEmpty(canLanguage) ? false : Boolean.parseBoolean(canLanguage));
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.teiid.model.vdb.Permission#readable()
     */
    @Override
    public boolean readable() {
        final String canRead = getProperty(Permission.PropertyName.READABLE);
        return (StringUtil.isEmpty(canRead) ? false : Boolean.parseBoolean(canRead));
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.teiid.model.vdb.Permission#setAlterable(boolean)
     */
    @Override
    public void setAlterable(boolean newAlterable) {
        throw new UnsupportedOperationException(); // TODO add message
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.teiid.model.vdb.Permission#setCondition(java.lang.String)
     */
    @Override
    public void setCondition(String newCondition) {
        throw new UnsupportedOperationException(); // TODO add message
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.teiid.model.vdb.Permission#setCreatable(boolean)
     */
    @Override
    public void setCreatable(boolean newCreatable) {
        throw new UnsupportedOperationException(); // TODO add message
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.teiid.model.vdb.Permission#setDeletable(boolean)
     */
    @Override
    public void setDeletable(boolean newDeletable) {
        throw new UnsupportedOperationException(); // TODO add message
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.teiid.model.vdb.Permission#setExecutable(boolean)
     */
    @Override
    public void setExecutable(boolean newExecutable) {
        throw new UnsupportedOperationException(); // TODO add message
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.teiid.model.vdb.Permission#setLanguagable(boolean)
     */
    @Override
    public void setLanguagable(boolean newLanguagable) {
        throw new UnsupportedOperationException(); // TODO add message
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.teiid.model.vdb.Permission#setReadable(boolean)
     */
    @Override
    public void setReadable(boolean newReadable) {
        throw new UnsupportedOperationException(); // TODO add message
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.teiid.model.vdb.Permission#setResourceName(java.lang.String)
     */
    @Override
    public void setResourceName(String newResourceName) {
        throw new UnsupportedOperationException(); // TODO add message
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.teiid.model.vdb.Permission#setUpdatable(boolean)
     */
    @Override
    public void setUpdatable(boolean newUpdatable) {
        throw new UnsupportedOperationException(); // TODO add message
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.teiid.model.vdb.Permission#updatable()
     */
    @Override
    public boolean updatable() {
        // TODO Auto-generated method stub
        return super.updatable();
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
     * @see org.komodo.teiid.model.vdb.VdbObject#setId(java.lang.String)
     */
    @Override
    public void setId(String newId) {
        throw new UnsupportedOperationException(); // TODO add message
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.teiid.model.ModelObject#addListener(java.beans.PropertyChangeListener)
     */
    @Override
    public boolean addListener(PropertyChangeListener newListener) {
        throw new UnsupportedOperationException(); // TODO add message
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.teiid.model.ModelObject#createPropertyChangeEvent(java.lang.String, java.lang.Object, java.lang.Object)
     */
    @Override
    protected PropertyChangeEvent createPropertyChangeEvent(String name,
                                                            Object oldValue,
                                                            Object newValue) {
        throw new UnsupportedOperationException(); // TODO add message
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.teiid.model.ModelObject#firePropertyChangeEvent(java.lang.String, java.lang.Object, java.lang.Object)
     */
    @Override
    protected void firePropertyChangeEvent(String name,
                                           Object oldValue,
                                           Object newValue) {
        throw new UnsupportedOperationException(); // TODO add message
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.teiid.model.ModelObject#removeListener(java.beans.PropertyChangeListener)
     */
    @Override
    public boolean removeListener(PropertyChangeListener registeredListener) {
        throw new UnsupportedOperationException(); // TODO add message
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.teiid.model.Describable#setDescription(java.lang.String)
     */
    @Override
    public void setDescription(final String newDescription) {
        throw new UnsupportedOperationException(); // TODO add message
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.teiid.model.Propertied#setProperty(java.lang.String, java.lang.String)
     */
    @Override
    public void setProperty(final String name,
                            final String newValue) {
        throw new UnsupportedOperationException(); // TODO add message
    }

}
