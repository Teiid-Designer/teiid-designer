/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.komodo.repository.artifact;

import java.util.UUID;
import org.komodo.common.util.Precondition;
import org.s_ramp.xmlns._2010.s_ramp.BaseArtifactEnum;
import org.s_ramp.xmlns._2010.s_ramp.ExtendedArtifactType;

/**
 * A factory for creating artifacts.
 */
public class ArtifactFactory {

    /**
     * @param artifactType the type of the artifact to create (cannot be <code>null</code>)
     * @return the created artifact (never <code>null</code>)
     */
    public static ExtendedArtifactType create(final Artifact.Type artifactType) {
        Precondition.notNull(artifactType, "artifactType"); //$NON-NLS-1$

        final ExtendedArtifactType artifact = new ExtendedArtifactType();
        artifact.setArtifactType(BaseArtifactEnum.EXTENDED_ARTIFACT_TYPE);
        artifact.setExtendedType(artifactType.getId());
        artifact.setUuid(UUID.randomUUID().toString());

        return artifact;
    }

    /**
     * Don't allow public construction.
     */
    private ArtifactFactory() {
        // nothing to do
    }
}
