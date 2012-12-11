/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.komodo.repository.artifact;

import org.s_ramp.xmlns._2010.s_ramp.BaseArtifactEnum;
import org.s_ramp.xmlns._2010.s_ramp.BaseArtifactType;
import org.s_ramp.xmlns._2010.s_ramp.UserDefinedArtifactType;
import org.teiid.komodo.repository.util.Precondition;

/**
 * A Teiid VDB artifact.
 */
public class VdbArtifact implements Artifact {

    /**
     * The S-RAMP artifact type for a VDB artifact.
     */
    public static final String ARTIFACT_TYPE = RepositoryConstants.Teiid.ARTIFACT_TYPE_PREFIX + "VDB"; //$NON-NLS-1$

    private final UserDefinedArtifactType delegate;

    /**
     * @param simpleName the VDB name without any path information (cannot be <code>null</code> or empty)
     * @param parentPath = the path where the VDB should be stored (can be <code>null</code> or empty)
     */
    public VdbArtifact(final String simpleName,
                       final String parentPath) {
        Precondition.notEmpty(simpleName, "simpleName"); //$NON-NLS-1$
        this.delegate = new UserDefinedArtifactType();
        this.delegate.setName(Artifact.Utils.constructFullName(simpleName, parentPath));
        this.delegate.setArtifactType(BaseArtifactEnum.USER_DEFINED_ARTIFACT_TYPE);
        this.delegate.setUserType(ARTIFACT_TYPE);
    }

    /**
     * @param delegate the VDB S-RAMP artifact (cannot be <code>null</code>)
     */
    public VdbArtifact(final UserDefinedArtifactType delegate) {
        Precondition.notNull(delegate, "delegate"); //$NON-NLS-1$
        Precondition.matchesExactly(delegate.getUserType(), ARTIFACT_TYPE, "delegate user type"); // TODO i18n
        this.delegate = delegate;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.teiid.komodo.repository.artifact.Artifact#getDelegate()
     */
    @Override
    public BaseArtifactType getDelegate() {
        return this.delegate;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.teiid.komodo.repository.artifact.Artifact#getFullName()
     */
    @Override
    public String getFullName() {
        return getDelegate().getName();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.teiid.komodo.repository.artifact.Artifact#getParentPath()
     */
    @Override
    public String getParentPath() {
        return Utils.getParentPath(getDelegate().getName());
    }

    /**
     * {@inheritDoc}
     *
     * @see org.teiid.komodo.repository.artifact.Artifact#getName()
     */
    @Override
    public String getSimpleName() {
        return Utils.getSimpleName(getDelegate().getName());
    }

    /**
     * {@inheritDoc}
     *
     * @see org.teiid.komodo.repository.artifact.Artifact#getType()
     */
    @Override
    public String getType() {
        return ARTIFACT_TYPE;
    }
}
