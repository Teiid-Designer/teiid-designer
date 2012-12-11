/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.komodo.repository;

import java.io.FileInputStream;
import java.io.InputStream;
import org.overlord.sramp.repository.DerivedArtifacts;
import org.overlord.sramp.repository.DerivedArtifactsFactory;
import org.overlord.sramp.repository.PersistenceFactory;
import org.overlord.sramp.repository.PersistenceManager;
import org.overlord.sramp.repository.QueryManager;
import org.overlord.sramp.repository.QueryManagerFactory;
import org.overlord.sramp.repository.query.ArtifactSet;
import org.overlord.sramp.repository.query.SrampQuery;
import org.s_ramp.xmlns._2010.s_ramp.BaseArtifactType;
import org.s_ramp.xmlns._2010.s_ramp.UserDefinedArtifactType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teiid.komodo.repository.artifact.Artifact;
import org.teiid.komodo.repository.artifact.ArtifactFactory;
import org.teiid.komodo.repository.artifact.RepositoryConstants;
import org.teiid.komodo.repository.util.Precondition;
import org.teiid.komodo.repository.util.StringUtil;

/**
 * The artifact repository manager. 
 */
public class RepositoryManager implements RepositoryConstants {

    interface Cli {
        String ARTIFACT_TYPE = "--artifact-type"; //$NON-NLS-1$
        String ARTIFACT_TYPE2 = "-at"; //$NON-NLS-1$
        String EXISTS = "--exists"; //$NON-NLS-1$
        String FILE_PATH = "--file-path"; //$NON-NLS-1$
        String FILE_PATH2 = "-fp"; //$NON-NLS-1$
        String HELP = "--help"; //$NON-NLS-1$
        String HELP2 = "-h"; //$NON-NLS-1$
        String PERSIST = "--persist"; //$NON-NLS-1$
        String REPO_PATH = "--repo-path"; //$NON-NLS-1$
        String REPO_PATH2 = "-rp"; //$NON-NLS-1$
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(RepositoryManager.class);

    /**
     * @param args the CLI repository manager arguments
     */
    public static void main(final String[] args) {
        boolean showHelp = false;
        final boolean embedded = true;
        String cmd = null;
        String artifactType = null;
        String filePath = null;
        String repoPath = null;

        if ((args == null) || (args.length == 0) || Cli.HELP.equals(args[0]) || Cli.HELP2.equals(args[0])) {
            showHelp = true;
        } else {
            cmd = args[0];

            if (Cli.EXISTS.equals(cmd)) {
                if (args.length != 5) {
                    showHelp = true;
                } else {
                    int i = 1;

                    while (StringUtil.isEmpty(artifactType) || StringUtil.isEmpty(repoPath)) {
                        if (Cli.ARTIFACT_TYPE.equals(args[i]) || Cli.ARTIFACT_TYPE2.equals(args[i])) {
                            artifactType = args[++i];
                        } else if (Cli.REPO_PATH.equals(args[i]) || Cli.REPO_PATH2.equals(args[i])) {
                            repoPath = args[++i];
                        } else {
                            showHelp = true;
                            break;
                        }

                        ++i;
                    }
                }
            } else if (Cli.PERSIST.equals(cmd)) {
                if (args.length != 7) {
                    showHelp = true;
                } else {
                    int i = 1;

                    while (StringUtil.isEmpty(artifactType) || StringUtil.isEmpty(repoPath) || StringUtil.isEmpty(filePath)) {
                        if (Cli.ARTIFACT_TYPE.equals(args[i]) || Cli.ARTIFACT_TYPE2.equals(args[i])) {
                            artifactType = args[++i];
                        } else if (Cli.REPO_PATH.equals(args[i]) || Cli.REPO_PATH2.equals(args[i])) {
                            repoPath = args[++i];
                        } else if (Cli.FILE_PATH.equals(args[i]) || Cli.FILE_PATH2.equals(args[i])) {
                            filePath = args[++i];
                        } else {
                            showHelp = true;
                            break;
                        }

                        ++i;
                    }
                }
            } else {
                showHelp = true;
            }
        }

        if (showHelp) {
            System.out.println("Komodo Repository usage: org.teiid.komodo.repository.RepositoryManager <command> <args>\n"); // TODO i18n
            System.out.println("Commands:\n"); // TODO i18n

            System.out.println("\t--exists -at|--artifact-type <artifactType> -rp|--repo-path <artifact_repository_path>"); // TODO i18n
            System.out.println("\t\tIndicates if an artifact of the specified type exists at the specified repository location\n"); // TODO i18n

            System.out.println("\t--persist -at|--artifact-type <artifactType> -rp|--repo-path <artifact_repository_path> -fp|--file-path <local_file_path>"); // TODO i18n
            System.out.println("\t\tPersists the file resource at the specified location using the specified artifact type\n"); // TODO i18n

            System.out.println("\t--help\n\n"); //$NON-NLS-1$
            System.out.println("\t\tPrints this help information\n"); // TODO i18n

            System.out.println("Notes:\n"); // TODO i18n

            System.out.println("1. The valid artifact types are the following:"); // TODO i18n
            System.out.println("\tvdb"); //$NON-NLS-1$
        } else {
            RepositoryManager repoMgr = null;

            if (embedded) {
                repoMgr = new RepositoryManager(DerivedArtifactsFactory.newInstance(), PersistenceFactory.newInstance(),
                                                QueryManagerFactory.newInstance());

                try {
                    if (Cli.EXISTS.equals(cmd)) {
                        final boolean result = repoMgr.exists(repoPath, artifactType);
                        System.out.println("artifact exists: " + result); // TODO i18n
                    } else if (Cli.PERSIST.equals(cmd)) {
                        final Artifact artifact = ArtifactFactory.create(repoPath, artifactType);
                        repoMgr.persist(artifact, new FileInputStream(filePath));
                        final boolean result = repoMgr.exists(artifact);
                        System.out.println("artifact persisted: " + result); // TODO i18n
                    } else {
                        System.exit(-1);
                    }
                } catch (final Exception e) {
                    e.printStackTrace(System.err);
                    System.exit(-1);
                }
            }
        }

        System.exit(0);
    }

    private final DerivedArtifacts derivedArtifacts;

    private final PersistenceManager persistenceManager;

    private final QueryManager queryManager;

    /**
     * @param derivedArtifacts the object that creates derived artifacts (cannot be <code>null</code>)
     * @param persistenceManager the persistence manager (cannot be <code>null</code>)
     * @param queryManager the query manager (cannot be <code>null</code>)
     */
    public RepositoryManager(final DerivedArtifacts derivedArtifacts,
                             final PersistenceManager persistenceManager,
                             final QueryManager queryManager) {
        Precondition.notNull(derivedArtifacts, "derivedArtifacts"); //$NON-NLS-1$
        Precondition.notNull(persistenceManager, "persistenceManager"); //$NON-NLS-1$
        Precondition.notNull(queryManager, "queryManager"); //$NON-NLS-1$

        this.derivedArtifacts = derivedArtifacts;
        this.persistenceManager = persistenceManager;
        this.queryManager = queryManager;
    }

    private ArtifactSet executeQuery(final String xpathTemplate) throws Exception {
        final SrampQuery query = getQueryManager().createQuery(xpathTemplate);
        LOGGER.debug("Executing query: '{}'", xpathTemplate); // TODO i18n
        final ArtifactSet results = query.executeQuery();
        LOGGER.debug("Query returned '{}' result(s)", results.size()); // TODO i18n
        return results;
    }

    /**
     * @param artifact the artifact being checked (cannot be <code>null</code>)
     * @return <code>true</code> if the artifact exists in the repository
     * @throws Exception if there is a problem accessing the repository
     */
    public boolean exists(final Artifact artifact) throws Exception {
        Precondition.notNull(artifact, "artifact"); //$NON-NLS-1$
        return exists(artifact.getFullName(), artifact.getType());
    }

    /**
     * @param artifactFullName the name of artifact, include parent path, being checked (cannot be <code>null</code>)
     * @param artifactType the type of the artifact (cannot be <code>null</code> or empty)
     * @return <code>true</code> if the artifact exists in the repository
     * @throws Exception if there is a problem accessing the repository
     */
    public boolean exists(final String artifactFullName,
                          final String artifactType) throws Exception {
        return (get(artifactFullName, artifactType) != null);
    }

    /**
     * @param artifactFullName the name of artifact, include parent path, being checked (cannot be <code>null</code>)
     * @param artifactType the type of the artifact (cannot be <code>null</code> or empty)
     * @return the artifact or <code>null</code> if it does not exist in the repository
     * @throws Exception if there is a problem accessing the repository
     */
    public Artifact get(final String artifactFullName,
                        final String artifactType) throws Exception {
        final String xpathTemplate = getXpathTemplate(artifactFullName, artifactType);
        final ArtifactSet results = executeQuery(xpathTemplate);

        if (results.size() == 1) {
            final BaseArtifactType artifact = results.iterator().next();

            if ((artifact instanceof UserDefinedArtifactType)
                && artifactType.equals(((UserDefinedArtifactType)artifact).getUserType())) {
                return ArtifactFactory.create((UserDefinedArtifactType)artifact);
            }
        }

        return null;
    }

    /**
     * @return the derived artifacts (never <code>null</code>)
     */
    public DerivedArtifacts getDerivedArtifacts() {
        return this.derivedArtifacts;
    }

    /**
     * @return the persistence manager (never <code>null</code>_
     */
    public PersistenceManager getPersistenceManager() {
        return this.persistenceManager;
    }

    /**
     * @return the query manager (never <code>null</code>)
     */
    public QueryManager getQueryManager() {
        return this.queryManager;
    }

    /**
     * @param artifactFullName the name of the artifact including parent path (cannot be <code>null</code> or empty)
     * @param artifactType the artifact user type (cannot be <code>null</code> or empty)
     * @return the XPath template (never <code>null</code>)
     */
    public String getXpathTemplate(final String artifactFullName,
                                   final String artifactType) {
        Precondition.notEmpty(artifactFullName, "artifactFullName"); //$NON-NLS-1$
        Precondition.notEmpty(artifactType, "artifactType"); //$NON-NLS-1$

        return RepositoryConstants.Sramp.USER_DEFINED_ARTIFACT_PATH + artifactType + "[@name = '" //$NON-NLS-1$
               + artifactFullName + "' ]"; //$NON-NLS-1$
    }

    /**
     * @param artifact the artifact being persisted (cannot be <code>null</code>)
     * @param content the artifact content being saved (cannot be <code>null</code>)
     * @throws Exception if there is a problem saving the artifact to the repository
     */
    public void persist(final Artifact artifact,
                        final InputStream content) throws Exception {
        Precondition.notNull(artifact, "artifact"); //$NON-NLS-1$
        Precondition.notNull(content, "content "); //$NON-NLS-1$
        getPersistenceManager().persistArtifact(artifact.getDelegate(), content);
    }
}
