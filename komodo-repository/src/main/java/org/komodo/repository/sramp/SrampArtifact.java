/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.komodo.repository.sramp;

import org.s_ramp.xmlns._2010.s_ramp.BaseArtifactType;

/**
 * An S-RAMP artifact.
 */
public interface SrampArtifact {

    /**
     * @return the S-RAMP artifact (never <code>null</code>)
     */
    BaseArtifactType getDelegate();

}
