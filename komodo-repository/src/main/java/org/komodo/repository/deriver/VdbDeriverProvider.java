/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.komodo.repository.deriver;

import java.util.Collections;
import java.util.Map;
import org.komodo.repository.artifact.Artifact;
import org.overlord.sramp.common.derived.ArtifactDeriver;
import org.overlord.sramp.common.derived.DeriverProvider;

/**
 * This provider contributes an {@link ArtifactDeriver} to the S-RAMP repository
 * for VDB artifacts.
 */
public class VdbDeriverProvider implements DeriverProvider {

    /**
     * {@inheritDoc}
     *
     * @see org.overlord.sramp.common.derived.DeriverProvider#createArtifactDerivers()
     */
    @Override
    public Map<String, ArtifactDeriver> createArtifactDerivers() {
        return Collections.singletonMap(Artifact.Type.VDB.getName(), (ArtifactDeriver)new VdbDeriver());
    }

}
