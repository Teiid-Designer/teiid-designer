/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.komodo.repository.artifact;

import org.s_ramp.xmlns._2010.s_ramp.UserDefinedArtifactType;
import org.teiid.komodo.repository.util.Precondition;

/**
 * A factory for creating artifacts.
 */
public class ArtifactFactory {

    /**
     * @param fullName the name of the artifact including the parent path (cannot be <code>null</code> or empty)
     * @param artifactType the artifact type (cannot be <code>null</code> or empty)
     * @return the VDB artifact (never <code>null</code>)
     */
    public static VdbArtifact create(String fullName,
                                     String artifactType) {
        Precondition.notEmpty(fullName, "fullName"); //$NON-NLS-1$
        Precondition.notEmpty(artifactType, "artifactType"); //$NON-NLS-1$

        if (VdbArtifact.ARTIFACT_TYPE.equals(artifactType)) {
            return new VdbArtifact(Artifact.Utils.getSimpleName(fullName), Artifact.Utils.getParentPath(fullName));
        }

        return null;
    }

    /**
     * @param delegate the S-RAMP artifact being used to create the Teiid artifact (cannot be <code>null</code>)
     * @return the VDB artifact (never <code>null</code>)
     */
    public static VdbArtifact create(UserDefinedArtifactType delegate) {
        Precondition.notNull(delegate, "delegate"); //$NON-NLS-1$

        if (VdbArtifact.ARTIFACT_TYPE.equals(delegate.getUserType())) {
            return new VdbArtifact(delegate);
        }

        return null;
    }

    /**
     * Don't allow construction.
     */
    private ArtifactFactory() {
        // nothing to do
    }
}
