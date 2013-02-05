/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.komodo.repository.sramp.teiid;

import org.komodo.common.util.Precondition;
import org.komodo.repository.artifact.Artifact;
import org.komodo.repository.artifact.teiid.DataPolicyArtifact;
import org.komodo.repository.artifact.teiid.EntryArtifact;
import org.komodo.repository.artifact.teiid.ImportVdbArtifact;
import org.komodo.repository.artifact.teiid.PermissionArtifact;
import org.komodo.repository.artifact.teiid.SchemaArtifact;
import org.komodo.repository.artifact.teiid.SourceArtifact;
import org.komodo.repository.artifact.teiid.TranslatorArtifact;
import org.komodo.repository.artifact.teiid.VdbArtifact;
import org.komodo.repository.sramp.SrampRepository;
import org.s_ramp.xmlns._2010.s_ramp.BaseArtifactType;
import org.s_ramp.xmlns._2010.s_ramp.ExtendedArtifactType;

/**
 * An Komodo artifact factory.
 */
public final class SrampTeiidArtifactFactory {

    private final SrampRepository repository;

    /**
     * @param repository the repository where the S-RAMP artifacts exists (cannot be <code>null</code>)
     */
    public SrampTeiidArtifactFactory(final SrampRepository repository) {
        Precondition.notNull(repository, "repository"); //$NON-NLS-1$
        this.repository = repository;
    }

    /**
     * @param srampArtifact the S-RAMP delegate artifact (cannot be <code>null</code>)
     * @return the Komodo artifact (never <code>null</code>)
     */
    public Artifact create(final BaseArtifactType srampArtifact) {
        Precondition.notNull(srampArtifact, "srampArtifact"); //$NON-NLS-1$

        if (srampArtifact instanceof ExtendedArtifactType) {
            final ExtendedArtifactType extendedSrampArtifact = (ExtendedArtifactType)srampArtifact;

            if (VdbArtifact.TYPE.getId().equals(((ExtendedArtifactType)srampArtifact).getExtendedType())) {
                return new SrampVdbArtifact(extendedSrampArtifact, this.repository);
            }

            if (DataPolicyArtifact.TYPE.getId().equals(extendedSrampArtifact.getExtendedType())) {
                return new SrampDataPolicyArtifact(extendedSrampArtifact, this.repository);
            }

            if (TranslatorArtifact.TYPE.getId().equals(extendedSrampArtifact.getExtendedType())) {
                return new SrampTranslatorArtifact(extendedSrampArtifact, this.repository);
            }

            if (SourceArtifact.TYPE.getId().equals(extendedSrampArtifact.getExtendedType())) {
                return new SrampSourceArtifact(extendedSrampArtifact, this.repository);
            }

            if (SchemaArtifact.TYPE.getId().equals(extendedSrampArtifact.getExtendedType())) {
                return new SrampSchemaArtifact(extendedSrampArtifact, this.repository);
            }

            if (PermissionArtifact.TYPE.getId().equals(extendedSrampArtifact.getExtendedType())) {
                return new SrampPermissionArtifact(extendedSrampArtifact, this.repository);
            }

            if (EntryArtifact.TYPE.getId().equals(extendedSrampArtifact.getExtendedType())) {
                return new SrampEntryArtifact(extendedSrampArtifact, this.repository);
            }

            if (ImportVdbArtifact.TYPE.getId().equals(extendedSrampArtifact.getExtendedType())) {
                return new SrampImportVdbArtifact(extendedSrampArtifact, this.repository);
            }
        }

        throw new IllegalArgumentException(); // TODO add message
    }

}
