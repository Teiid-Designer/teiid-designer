/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.komodo.repository.deriver;

import java.util.Map;
import org.komodo.common.util.CollectionUtil;
import org.komodo.common.util.Precondition;
import org.komodo.repository.RepositoryConstants;
import org.komodo.repository.RepositoryManager;
import org.komodo.repository.artifact.Artifact;
import org.overlord.sramp.common.SrampModelUtils;
import org.s_ramp.xmlns._2010.s_ramp.BaseArtifactType;

/**
 * Utilities for use with derivers.
 */
public class DeriverUtil implements RepositoryConstants {

    private static final String ARTIFACT_QUERY_PATTERN = "%s[%s]"; //$NON-NLS-1$

    private static final String ATTRIBUTE_QUERY_PATTERN = "string(./%s)"; //$NON-NLS-1$

    private static final String DERIVED_ARTIFACTS_PATTERN = Sramp.USER_DEFINED_ARTIFACT_PATH + "[relatedDocument[@uuid = '%s']]"; //$NON-NLS-1$

    private static final String ELEMENT_QUERY_PATTERN = "./%s"; //$NON-NLS-1$

    private static final String UUID_QUERY_PATTERN = Sramp.ROOT_PATH + "[@uuid = '%s']"; //$NON-NLS-1$

    /**
     * Creates a one way relationship. Creates the relationship instance if necessary.
     * 
     * @param sourceArtifact the artifact where the relationship starts (cannot be <code>null</code>)
     * @param targetArtifact the artifact where the relationship ends (cannot be <code>null</code>)
     * @param relationshipType the relationship type (cannot be <code>null</code>)
     */
    public static void addRelationship(final BaseArtifactType sourceArtifact,
                                       final BaseArtifactType targetArtifact,
                                       final Artifact.RelationshipType relationshipType) {
        Precondition.notNull(sourceArtifact, "sourceArtifact"); //$NON-NLS-1$
        Precondition.notNull(targetArtifact, "targetArtifact"); //$NON-NLS-1$
        Precondition.notNull(relationshipType, "relationshipType"); //$NON-NLS-1$

        SrampModelUtils.addGenericRelationship(sourceArtifact, relationshipType.getId(), targetArtifact.getUuid());
    }

    /**
     * Creates a two way relationship. Creates the relationship instance if necessary.
     * 
     * @param sourceArtifact the artifact where the relationship starts (cannot be <code>null</code>)
     * @param targetArtifact the artifact where the relationship ends (cannot be <code>null</code>)
     * @param relationshipType the relationship type (cannot be <code>null</code>)
     * @param inverseRelationshipType the relationship type of the inverse relationship (cannot be <code>null</code>)
     */
    public static void addRelationship(final BaseArtifactType sourceArtifact,
                                       final BaseArtifactType targetArtifact,
                                       final Artifact.RelationshipType relationshipType,
                                       final Artifact.RelationshipType inverseRelationshipType) {
        Precondition.notNull(sourceArtifact, "sourceArtifact"); //$NON-NLS-1$
        Precondition.notNull(targetArtifact, "targetArtifact"); //$NON-NLS-1$
        Precondition.notNull(relationshipType, "relationshipType"); //$NON-NLS-1$
        Precondition.notNull(inverseRelationshipType, "inverseRelationshipType"); //$NON-NLS-1$

        SrampModelUtils.addGenericRelationship(sourceArtifact, relationshipType.getId(), targetArtifact.getUuid());
        SrampModelUtils.addGenericRelationship(targetArtifact, inverseRelationshipType.getId(), sourceArtifact.getUuid());
    }

    /**
     * @param settings the settings used to build query (cannot be <code>null</code> and must have parameters)
     * @return the query string (never <code>null</code> or empty)
     */
    public static String buildQuery(final RepositoryManager.QuerySettings settings) {
        Precondition.notNull(settings, "settings"); //$NON-NLS-1$

        // construct query path
        String path = Sramp.USER_DEFINED_ARTIFACT_PATH;

        if (settings.artifactType != null) {
            path += '/' + settings.artifactType.getId();
        }

        if (CollectionUtil.isEmpty(settings.params)) {
            return path;
        }

        // construct parameters
        final StringBuilder params = new StringBuilder();
        final int numParams = settings.params.size();
        int i = 1;

        for (final Map.Entry<String, String> entry : settings.params.entrySet()) {
            params.append('@').append(entry.getKey()).append(" = '").append(entry.getValue()).append('\''); //$NON-NLS-1$

            if (i != numParams) {
                params.append(" and "); //$NON-NLS-1$
            }

            ++i;
        }

        return String.format(ARTIFACT_QUERY_PATTERN, path, params.toString());
    }

    /**
     * @param qualifiedName the qualified name of the attribute whose query string is being requested (cannot be <code>null</code> 
     * or empty)
     * @return the query string (never <code>null</code>)
     */
    public static String getAttributeQueryString(final String qualifiedName) {
        Precondition.notEmpty(qualifiedName, "qualifiedName"); //$NON-NLS-1$
        System.err.println(String.format(ATTRIBUTE_QUERY_PATTERN, qualifiedName));
        return String.format(ATTRIBUTE_QUERY_PATTERN, qualifiedName);
    }

    /**
     * @param uuid the UUID of the artifact whose derived artifacts query string is being requested (cannot be <code>null</code> 
     * or empty)
     * @return the query string (never <code>null</code>)
     */
    public static String getDerivedArtifactsQueryString(final String uuid) {
        Precondition.notEmpty(uuid, "uuid"); //$NON-NLS-1$
        return String.format(DERIVED_ARTIFACTS_PATTERN, uuid);
    }

    /**
     * @param qualifiedName the qualified name of the element whose query string is being requested (cannot be <code>null</code> 
     * or empty)
     * @return the query string (never <code>null</code>)
     */
    public static String getElementQueryString(final String qualifiedName) {
        Precondition.notEmpty(qualifiedName, "qualifiedName"); //$NON-NLS-1$
        return String.format(ELEMENT_QUERY_PATTERN, qualifiedName);
    }

    /**
     * @param uuid the UUID of the artifact whose query string is being requested (cannot be <code>null</code> or empty)
     * @return the query string (never <code>null</code>)
     */
    public static String getUuidQueryString(final String uuid) {
        Precondition.notEmpty(uuid, "uuid"); //$NON-NLS-1$
        return String.format(UUID_QUERY_PATTERN, uuid);
    }

    /**
     * Don't allow public construction.
     */
    private DeriverUtil() {
        // nothing to do
    }
}
