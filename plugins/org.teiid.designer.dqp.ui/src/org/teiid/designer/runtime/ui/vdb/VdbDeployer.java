/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package org.teiid.designer.runtime.ui.vdb;

import static com.metamatrix.modeler.dqp.ui.DqpUiConstants.UTIL;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.teiid.adminapi.VDB;
import org.teiid.designer.runtime.ExecutionAdmin;
import org.teiid.designer.vdb.TranslatorOverride;
import org.teiid.designer.vdb.Vdb;
import org.teiid.designer.vdb.VdbModelEntry;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.core.util.StringUtilities;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.modeler.dqp.ui.DqpUiPlugin;

/**
 *
 */
public class VdbDeployer {

    static final String PREFIX = I18nUtil.getPropertyPrefix(VdbDeployer.class);

    /**
     * A VDB deployment status.
     */
    public enum DeployStatus {
        /**
         * Indicates Teiid failed to create a DS.
         */
        CREATE_DATA_SOURCE_FAILED,

        /**
         * Indicates the VDB was deployed to Teiid.
         */
        DEPLOYED_VDB,

        /**
         * Indicates the VDB deployment was canceled by the user.
         */
        DEPLOY_VDB_CANCELED,

        /**
         * Indicates Teiid failed to deploy the VDB.
         */
        DEPLOY_VDB_FAILED,

        /**
         * Indicates the an unexpected exception was caught.
         */
        EXCEPTION,

        /**
         * Indicates the user conceled the progress monitor.
         */
        MONITOR_CANCELLED,

        /**
         * Indicates there are missing translator names or translator names that are not on the current Teiid server.
         */
        TRANSLATOR_PROBLEM;

        /**
         * @return <code>true</code> if status indicates the VDB was successfully depoloyed
         */
        public boolean isDeployed() {
            return (this == DEPLOYED_VDB);
        }

        /**
         * @return <code>true</code> if status is an error
         */
        public boolean isError() {
            return ((this == CREATE_DATA_SOURCE_FAILED) || (this == DEPLOY_VDB_FAILED) || (this == EXCEPTION) || (this == TRANSLATOR_PROBLEM));
        }
    }

    private final ExecutionAdmin admin; // the current Teiid server
    private final boolean autoCreateDsOnServer; // indicates if data source should be auto-created on server without asking user
    private Exception error; // non-null if error caught while deploying
    private final Shell shell;
    private DeployStatus status; // non-null after deploying
    private final Vdb vdb; // the workspace VDB
    private VDB deployedVdb; // the VDB deployed on Teiid

    /**
     * @param shell the shell to use for any UI (may not be <code>null</code>)
     * @param vdbBeingDeployed the VDB being deployed (may not be <code>null</code>)
     * @param defaultServerAdmin the server execution admin (may not be <code>null</code>)
     * @param shouldAutoCreateDataSourceOnServer indicates if data sources that match the default name should be auto-created if
     *        they don't exist on Teiid server
     */
    public VdbDeployer( Shell shell,
                        Vdb vdbBeingDeployed,
                        ExecutionAdmin defaultServerAdmin,
                        boolean shouldAutoCreateDataSourceOnServer ) {
        CoreArgCheck.isNotNull(vdbBeingDeployed, "Vdb is null"); //$NON-NLS-1$
        CoreArgCheck.isNotNull(defaultServerAdmin, "ExecutionAdmin is null"); //$NON-NLS-1$
        CoreArgCheck.isNotNull(shell, "Shell is null"); //$NON-NLS-1$

        this.admin = defaultServerAdmin;
        this.shell = shell;
        this.vdb = vdbBeingDeployed;
        this.autoCreateDsOnServer = shouldAutoCreateDataSourceOnServer;
    }

    /**
     * @return the Teiid VDB or <code>null</code> if the workspace VDB has not been deployed
     */
    public VDB getDeployedVdb() {
        return this.deployedVdb;
    }

    /**
     * @return the error caught during deploying the VDB or <code>null</code>
     */
    public Exception getException() {
        return this.error;
    }

    /**
     * @return the deploy status (<code>null</code> if called before deploy is called)
     */
    public DeployStatus getStatus() {
        return this.status;
    }

    /**
     * Deploy the selected VDB to the default Teiid server.
     * 
     * @param monitor the progress monitor (can be <code>null</code>)
     */
    public void deploy( IProgressMonitor monitor ) {
        if (monitor == null) {
            monitor = new NullProgressMonitor();
        }

        try {
            // since we need to deploy lets first check to make sure all the data sources exist on server
            if (!this.vdb.getModelEntries().isEmpty()) {
                monitor.beginTask(UTIL.getString(PREFIX + "deployMainTask", getVdbName()), IProgressMonitor.UNKNOWN); //$NON-NLS-1$

                boolean hasJndiProblems = false;
                boolean foundFirstOne = false; // determines if create DS dialog is shown
                boolean createOnServer = false; // user's choice on if they want DSs auto-created on server

                for (VdbModelEntry modelEntry : this.vdb.getModelEntries()) {
                    // see if user canceled monitor
                    if (monitor.isCanceled()) {
                        this.status = DeployStatus.MONITOR_CANCELLED;
                        return; // don't do anything else
                    }

                    String modelName = modelEntry.getName().toFile().getName();
                    monitor.subTask(UTIL.getString(PREFIX + "checkModelTypeTask", modelName)); //$NON-NLS-1$
                    boolean autoCreate = false; // based on DS name and preference value
                    String modelType = modelEntry.getType();
                    boolean sourceModel = (modelType.equalsIgnoreCase(ModelType.PHYSICAL_LITERAL.getName()));

                    // only source models have a data source and translator
                    if (!sourceModel) {
                        continue; // go on to next model (only care about source models)
                    }

                    // check translator
                    monitor.subTask(UTIL.getString(PREFIX + "checkModelTranslatorTask", modelName)); //$NON-NLS-1$

                    if (!hasValidTranslator(modelEntry)) {
                        this.status = DeployStatus.TRANSLATOR_PROBLEM;
                        break; // translator problems are fatal (deployment will fail)
                    }

                    // check DS
                    monitor.subTask(UTIL.getString(PREFIX + "checkModelDataSourceTask", modelName)); //$NON-NLS-1$
                    String jndiName = modelEntry.getJndiName();

                    // DS is empty
                    if (StringUtilities.isEmpty(jndiName)) {
                        hasJndiProblems = true;
                        continue; // go on to next model
                    }

                    // DS not found on server
                    if (!this.admin.dataSourceExists(jndiName)) {
                        // auto-create if user did not change the default DS name
                        String defaultName = VdbModelEntry.createDefaultJndiName(modelEntry.getName());

                        if (jndiName.equals(defaultName) && this.autoCreateDsOnServer) {
                            autoCreate = true; // create without asking user
                        }

                        if (!autoCreate && !foundFirstOne) {
                            // if this is the first DS that isn't found on server ask user if DS should be
                            // auto-created on the server (and do this for any others found to not be on
                            // server)
                            foundFirstOne = true;

                            // if user OK's dialog they want DSs auto-created
                            final boolean[] result = new boolean[1];

                            // make sure in UI thread
                            this.shell.getDisplay().syncExec(new Runnable() {
                                /**
                                 * {@inheritDoc}
                                 * 
                                 * @see java.lang.Runnable#run()
                                 */
                                @Override
                                public void run() {
                                    if (MessageDialog.openQuestion(getShell(),
                                                                   UTIL.getString(PREFIX + "createDataSourcesConfirmation.title"), //$NON-NLS-1$
                                                                   UTIL.getString(PREFIX
                                                                                  + "createDataSourcesConfirmation.message", //$NON-NLS-1$
                                                                                  getVdbName()))) {
                                        result[0] = true;
                                    }
                                }
                            });

                            if (result[0]) {
                                createOnServer = true;
                            }
                        }

                        IFile model = modelEntry.findFileInWorkspace();

                        // TODO must also be able to create DS even if model not found in workspace
                        // if model found in workspace create data source on server
                        if ((autoCreate || createOnServer) && (model != null)) {
                            monitor.subTask(UTIL.getString(PREFIX + "createModelDataSourceTask", modelName)); //$NON-NLS-1$

                            if (this.admin.getOrCreateDataSource(model,
                                                                 jndiName,
                                                                 false,
                                                                 DqpUiPlugin.getDefault().getPasswordProvider(this.shell)) == null) {
                                this.status = DeployStatus.CREATE_DATA_SOURCE_FAILED;
                                break; // don't try again to create a DS
                            }
                        } else if (!hasJndiProblems) {
                            // DS doesn't exist and won't be auto-created, or model not in workspace
                            hasJndiProblems = true;
                        }
                    }
                }

                // ask user if they still want to deploy even though there are JNDI problems
                if (hasJndiProblems) {
                    // make sure in UI thread
                    this.shell.getDisplay().syncExec(new Runnable() {
                        /**
                         * {@inheritDoc}
                         * 
                         * @see java.lang.Runnable#run()
                         */
                        @Override
                        public void run() {
                            if (MessageDialog.openQuestion(getShell(),
                                                           UTIL.getString(PREFIX + "deployWithErrorsConfirmation.title"), //$NON-NLS-1$
                                                           UTIL.getString(PREFIX + "deployWithErrorsConfirmation.message"))) { //$NON-NLS-1$
                                setStatus(null); // user wants to deploy regardless of errors
                            } else {
                                setStatus(DeployStatus.DEPLOY_VDB_CANCELED);
                            }
                        }
                    });
                }
            }

            if (this.status == null) {
                monitor.subTask(UTIL.getString(PREFIX + "deployVdbTask", getVdbName())); //$NON-NLS-1$
                this.deployedVdb = this.admin.deployVdb(this.vdb);
                this.status = ((this.deployedVdb == null) ? DeployStatus.DEPLOY_VDB_FAILED : DeployStatus.DEPLOYED_VDB);
            }
        } catch (Exception e) {
            this.status = DeployStatus.EXCEPTION;
            this.error = e;
        } finally {
            monitor.done();
        }
    }

    /**
     * @return the name of the VDB being deployed (never <code>null</code>)
     */
    public String getVdbName() {
        return this.vdb.getFile().getName();
    }

    /**
     * @return the shell the deployer is using (never <code>null</code>)
     */
    Shell getShell() {
        return this.shell;
    }

    /**
     * @param modelEntry the model entry whose translator entry is being validated
     * @return <code>true</code> if model entry has a valid translator
     */
    private boolean hasValidTranslator( VdbModelEntry modelEntry ) {
        // assertion: must be a source model
        String translatorName = modelEntry.getTranslator();

        // must have a translator
        if (StringUtilities.isEmpty(translatorName)) {
            return false;
        }

        // make sure server has translator with that name
        boolean isValid = (this.admin.getTranslator(translatorName) != null);
        
        // Check for overridden translator names
        if( !isValid && !this.vdb.getTranslators().isEmpty()) {
        	for( TranslatorOverride override : this.vdb.getTranslators() ) {
        		if( override.getName().equalsIgnoreCase(translatorName) ) {
        			isValid = (this.admin.getTranslator(override.getType()) != null);
        			break;
        		}
        	}
        }

        
        return isValid;
    }

    /**
     * @param status the new status (can be <code>null</code>)
     */
    void setStatus( DeployStatus status ) {
        this.status = status;
    }

}
