/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.komodo.repository.artifact;

import org.komodo.common.util.Precondition;
import org.s_ramp.xmlns._2010.s_ramp.BaseArtifactEnum;
import org.s_ramp.xmlns._2010.s_ramp.UserDefinedArtifactType;

/**
 * A factory for creating artifacts.
 */
public class ArtifactFactory {

    /**
     * @param artifactType the type of the artifact to create (cannot be <code>null</code>)
     * @return the created artifact (never <code>null</code>)
     */
    public static UserDefinedArtifactType create(final Artifact.Type artifactType) {
        Precondition.notNull(artifactType, "artifactType"); //$NON-NLS-1$

        final UserDefinedArtifactType artifact = new UserDefinedArtifactType();
        artifact.setArtifactType(BaseArtifactEnum.USER_DEFINED_ARTIFACT_TYPE);
        artifact.setUserType(artifactType.getName());

        return artifact;
    }

    /**
     * Don't allow public construction.
     */
    private ArtifactFactory() {
        // nothing to do
    }
}
