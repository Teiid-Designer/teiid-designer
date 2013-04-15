/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.teiidimporter.ui.wizard;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.designer.compare.DifferenceReport;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelWorkspaceItem;
import org.teiid.designer.core.workspace.ModelWorkspaceManager;
import org.teiid.designer.datatools.connection.ConnectionInfoHelper;
import org.teiid.designer.ddl.importer.DdlImporter;
import org.teiid.designer.metamodels.core.ModelType;
import org.teiid.designer.runtime.DqpPlugin;
import org.teiid.designer.runtime.importer.ImportManager;
import org.teiid.designer.runtime.spi.ITeiidDataSource;
import org.teiid.designer.runtime.spi.ITeiidTranslator;
import org.teiid.designer.runtime.spi.TeiidPropertyDefinition;
import org.teiid.designer.teiidimporter.ui.Messages;
import org.teiid.designer.teiidimporter.ui.UiConstants;
import org.teiid.designer.teiidimporter.ui.panels.PropertyItem;
import org.teiid.designer.ui.common.util.WidgetUtil;
import org.teiid.designer.ui.viewsupport.ModelUtilities;

/**
 *  TeiidImportManager
 *  manager object for use with the TeiidImportWizard
 *  
 *  @since 8.1
 */
public class TeiidImportManager implements ITeiidImportServer, UiConstants {

    private static final String IMPORT_VDB_NAME = Messages.TeiidImportManager_ImportVDBName;
    private static final String PREVIEW_DATASOURCE_PREFIX = "PREVIEW_";  //$NON-NLS-1$
    
    private IPath targetModelLocation = null;
    private String targetModelName = null;
    private String translatorName = null;
    private String dataSourceName = null;
    private String dataSourceDriverName = null;
    private Properties dataSourceProps = null;
    IStatus vdbDeploymentStatus = null;
    private ConnectionInfoHelper connectionInfoHelper = new ConnectionInfoHelper();
    private DdlImporter ddlImporter;
    private File ddlFile;
    
    /**
     * Set the data source name
     * @param dsName the data source name
     */
    public void setDataSourceName(String dsName) {
        // If different datasource is selected, reset deployment status
        if(areDifferent(this.dataSourceName,dsName)) {
            this.vdbDeploymentStatus = null;
        }
        this.dataSourceName = dsName;
    }
    
    /**
     * Get the current Connection name
     * @return the Connection name
     */
    public String getDataSourceName() {
        return this.dataSourceName;
    }
    
    /**
     * Set the DataSource driver name.  Whenever it's reset, set deployment status to invalid so that user
     * must reValidate
     * @param driverName the data source driver name
     */
    public void setDataSourceDriverName(String driverName) {
        // If different driver is selected, reset deployment status
        if(areDifferent(this.dataSourceDriverName,driverName)) {
            this.vdbDeploymentStatus = null;
        }
        this.dataSourceDriverName = driverName;
    }
    
    /**
     * Get the current DataSource driver name
     * @return the DataSource driver name
     */
    public String getDataSourceDriverName() {
        return this.dataSourceDriverName;
    }
    
    /**
     * Set the DataSource properties.  Whenever it's reset, set deployment status to invalid so that user
     * must reValidate
     * @param props the data source properties
     */
    public void setDataSourceProperties(Properties props) {
        this.dataSourceProps = props;
        this.vdbDeploymentStatus = null;
    }
    
    /**
     * Get the current DataSource properties
     * @return the DataSource properties
     */
    public Properties getDataSourceProperties() {
        return this.dataSourceProps;
    }
    
    /**
     * @return the translatorName
     */
    public String getTranslatorName() {
        return this.translatorName;
    }

    /**
     * @param translatorName the translatorName to set
     */
    public void setTranslatorName(String translatorName) {
        // If different translator is selected, reset deployment status
        if(areDifferent(this.translatorName,translatorName)) {
            this.vdbDeploymentStatus = null;
        }
        this.translatorName = translatorName;
    }
    
    /**
     * Determine if the Importer Server is Valid
     * @return 'true' if we have a valid server, 'false' if not.
     */
    public boolean isValidImportServer() {
        return getServerImportManager().isValidImportServer();
    }
    
    /**
     * Deploy a dynamic VDB using the current DataSource and Translator
     * @return the deployment status
     */
    public IStatus deployDynamicVdb() {
        vdbDeploymentStatus = null;
 
        final String translatorName = getTranslatorName();
        final String dataSourceName = getDataSourceName();
        boolean infoGood = false;
        if(translatorName!=null && dataSourceName!=null) {
            infoGood=true;
        }
        // Create Runnable if the profile is valid
        if(isValidImportServer() && infoGood) {

            IRunnableWithProgress op = new IRunnableWithProgress() {
                @Override
                public void run( IProgressMonitor monitor ) throws InvocationTargetException {
                    try {
                        monitor.beginTask(Messages.TeiidImportManager_deployVdbMsg, 100); 
                        vdbDeploymentStatus = getServerImportManager().deployDynamicVdb(IMPORT_VDB_NAME,dataSourceName,translatorName,monitor); 
                    } catch (Throwable e) {
                        throw new InvocationTargetException(e);
                    } finally {
                        monitor.done();
                    }
                }
            };
            
            try {
                new ProgressMonitorDialog(Display.getCurrent().getActiveShell()).run(true, true, op);
            } catch (InvocationTargetException e) {
                Throwable cause = e.getCause();
                vdbDeploymentStatus = new Status(IStatus.ERROR, UiConstants.PLUGIN_ID, 0, cause.getLocalizedMessage(), cause);
                UTIL.log(vdbDeploymentStatus);
            } catch (InterruptedException e) {
                vdbDeploymentStatus = new Status(IStatus.ERROR,UiConstants.PLUGIN_ID, Messages.TeiidImportManager_deployVdbInterruptedMsg);
                UTIL.log(vdbDeploymentStatus);
            }
        }
        
        return vdbDeploymentStatus;
    }
    
    /**
     * Undeploy the dynamic VDB and datasource
     * @return the deployment status
     */
    public IStatus undeployDynamicVdb() {
        this.vdbDeploymentStatus = null;
        return getServerImportManager().undeployVdb(IMPORT_VDB_NAME);
    }
    
    /**
     * Inject the current Connection Profile into the supplied model, then save the model
     * @param model the supplied ModelResourve
     * @throws Exception the exception
     */
//    public void injectConnectionProfile(ModelResource model) throws Exception {
//        if(this.connectionTemplateName!=null) {
//            ConnectionInfoProviderFactory manager = new ConnectionInfoProviderFactory();
////            IConnectionInfoProvider connInfoProvider = manager.getProvider(connectionTemplate);
////            // Inject the connection profile properties into the physical model
////            connInfoProvider.setConnectionInfo(model, connectionProfile);
//            
//            model.save(new NullProgressMonitor(), false);
//        }
//    }
    
    /**
     * Return the schema DDL for the currently deployed dynamic import VDB
     * @return the schema DDL
     */
    public String getDdl( ) {
        String ddl = null;
        try {
            ddl = getServerImportManager().getSchema(IMPORT_VDB_NAME);
        } catch (Exception ex) {
            UTIL.log(ex);
        	ddl = Messages.TeiidImportManager_getDdlErrorMsg;
        }
        return ddl;
    }
    
    /*
     * Determine if two string values are different
     * @param str1 string1
     * @param str2 string2
     * @return 'true' if the strings are different, 'false' if not
     */
    private boolean areDifferent(String str1, String str2) {
        // str1 is empty, but str2 is not
        if(CoreStringUtil.isEmpty(str1) && !CoreStringUtil.isEmpty(str2)) {
            return true;
        }
        // str2 is empty, but str1 is not
        if(CoreStringUtil.isEmpty(str2) && !CoreStringUtil.isEmpty(str1)) {
            return true;
        }
        // both empty
        if(CoreStringUtil.isEmpty(str1) && CoreStringUtil.isEmpty(str2)) {
            return false;
        }
        // both empty
        if(str1.equalsIgnoreCase(str2)) {
            return false;
        }
        return true;
    }
    
    /**
     * Return Temporary DDL so we can test the wizard for now.
     * @return the temporary DDL
     */
    public String getTemporaryDDL() {
        StringBuffer sb = new StringBuffer();
        
        sb.append("CREATE TABLE ACCOUNT\n"); //$NON-NLS-1$
        sb.append("(\n"); //$NON-NLS-1$
        sb.append("ACCOUNT_ID   NUMBER(10) DEFAULT ('0') NOT NULL,\n"); //$NON-NLS-1$
        sb.append("SSN          CHAR(10),\n"); //$NON-NLS-1$
        sb.append("STATUS       CHAR(10),\n"); //$NON-NLS-1$
        sb.append("TYPE         CHAR(10),\n"); //$NON-NLS-1$
        sb.append("DATEOPENED   DATE DEFAULT ('CURRENT_TIMESTAMP') NOT NULL,\n"); //$NON-NLS-1$
        sb.append("DATECLOSED   DATE DEFAULT ('0000-00-00 00:00:00') NOT NULL\n"); //$NON-NLS-1$
        sb.append(");\n"); //$NON-NLS-1$
        sb.append("\n"); //$NON-NLS-1$
        sb.append("CREATE TABLE HOLDINGS\n"); //$NON-NLS-1$
        sb.append("(\n"); //$NON-NLS-1$
        sb.append("TRANSACTION_ID   NUMBER(10) NOT NULL,\n"); //$NON-NLS-1$
        sb.append("ACCOUNT_ID       NUMBER(10),\n"); //$NON-NLS-1$
        sb.append("PRODUCT_ID       NUMBER(10),\n"); //$NON-NLS-1$
        sb.append("PURCHASE_DATE    DATE DEFAULT ('CURRENT_TIMESTAMP') NOT NULL,\n"); //$NON-NLS-1$
        sb.append("SHARES_COUNT     NUMBER(10)\n"); //$NON-NLS-1$
        sb.append(");\n"); //$NON-NLS-1$
        sb.append("\n"); //$NON-NLS-1$
        
        return sb.toString();
    }
    
    /**
     * Get the server ImportManager instance
     * @return the ImportManager
     */
    public ImportManager getServerImportManager() {
        return DqpPlugin.getInstance().getServerManager().getImportManager();
    }
    
    /**
     * Determine if the VDB is Deployed
     * @return 'true' if deployed, 'false' if not.
     */
    public boolean isVdbDeployed() {
        return (this.vdbDeploymentStatus!=null && this.vdbDeploymentStatus.isOK()) ? true : false;
    }
    
    /**
     * Get the VDB Deployment status.
     * @return the Status
     */
    public IStatus getVdbDeploymentStatus() {
        return this.vdbDeploymentStatus;
    }
    
    /**
     * Set the Target Model Location
     * @param targetPath the location path for the target
     */
    public void setTargetModelLocation(IPath targetPath) {
        this.targetModelLocation=targetPath;
        if(this.ddlImporter!=null) {
            this.ddlImporter.setModelFolder(targetPath.toOSString());
        }
    }
    
    /**
     * Get the current target path
     * @return the path for the target model
     */
    public IPath getTargetModelLocation() {
        return this.targetModelLocation;
    }
    
    /**
     * Set the Target Model Name
     * @param targetModelName the name for the target model
     */
    public void setTargetModelName(String targetModelName) {
        this.targetModelName=targetModelName;
        if(this.ddlImporter!=null) {
            this.ddlImporter.setModelName(targetModelName);
        }
    }    
    
    /**
     * Get the current target model name
     * @return the name for the target model
     */
    public String getTargetModelName() {
        return this.targetModelName;
    }
        
    /**
     * Determine if the target model already exists in the workspace
     * @return 'true' if the model exists, 'false' if not.
     */
    public boolean targetModelExists() {
        if( this.targetModelLocation == null || this.targetModelName == null ) {
            return false;
        }
        
        IPath modelPath = new Path(targetModelLocation.toOSString()).append(this.targetModelName);
        if( !modelPath.toString().toUpperCase().endsWith(".XMI")) { //$NON-NLS-1$
            modelPath = modelPath.addFileExtension("xmi"); //$NON-NLS-1$
        }
        
        ModelWorkspaceItem item = ModelWorkspaceManager.getModelWorkspaceManager().findModelWorkspaceItem(modelPath, IResource.FILE);
        if( item != null ) {
            return true;
        }
            
        return false;
    }
    
    /**
     * Determine if the Target Model's Connection Profile is compatible with the currently selected data source.
     * If targetModel has a connection profile:
     *   - allow import if connection-url are same
     * If targetModel does not have a connection profile:
     *   - allow the import
     * In either case, this importer does not inject connection properties into the model that is produced.
     * @return 'true' if compatible, 'false' if not.
     */
    public boolean isTargetModelConnectionProfileCompatible() {
        if( this.targetModelLocation == null ) {
            return false;
        }
        
        IPath modelPath = new Path(targetModelLocation.toOSString()).append(this.targetModelName);
        if( !modelPath.toString().toUpperCase().endsWith(".XMI")) { //$NON-NLS-1$
            modelPath = modelPath.addFileExtension("xmi"); //$NON-NLS-1$
        }
        
        IResource targetModel = ModelerCore.getWorkspace().getRoot().getFile(modelPath);
        ModelResource targetModelResc = ModelUtilities.getModelResourceForIFile((IFile)targetModel, false);
        if( targetModelResc != null ) {
            IConnectionProfile profile = connectionInfoHelper.getConnectionProfile(targetModelResc);
        
            // No connection profile for target model - allow the import
            if( profile == null) {
                return true;
            } else {
                // Get the connection profile URL from target Model props
                Properties profileProps = profile.getBaseProperties();
                String targetModelUrl = profileProps.getProperty(PropertyItem.CONNECTION_URL_DISPLAYNAME);
                
                // Get the importer DataSource Url property
                Properties importDsProps = getDataSourceProperties();
                String importDsUrl = null;
                if(importDsProps!=null) {
                    importDsUrl = importDsProps.getProperty(PropertyItem.CONNECTION_URL_DISPLAYNAME);
                }
                if(importDsUrl!=null && importDsUrl.equalsIgnoreCase(targetModelUrl)) {
                    return true;
                }
            }
        }
        
        return false;
    }

    /* (non-Javadoc)
     * @see org.teiid.designer.importer.ui.wizard.ITeiidImportServer#deleteDataSource(java.lang.String)
     */
    @Override
    public void deleteDataSource(String jndiName) throws Exception {
        getServerImportManager().deleteDataSource(jndiName);
    }

    /* (non-Javadoc)
     * @see org.teiid.designer.importer.ui.wizard.ITeiidImportServer#deployDriver(java.io.File)
     */
    @Override
    public void deployDriver(File jarOrRarFile) throws Exception {
        getServerImportManager().deployDriver(jarOrRarFile);
    }

    /* (non-Javadoc)
     * @see org.teiid.designer.importer.ui.wizard.ITeiidImportServer#getSchema(java.lang.String, int, java.lang.String)
     */
    @Override
    public String getSchema(String vdbName,
                            int vdbVersion,
                            String modelName) throws Exception {
        return getServerImportManager().getSchema(vdbName, vdbVersion, modelName);
    }

    /* (non-Javadoc)
     * @see org.teiid.designer.importer.ui.wizard.ITeiidImportServer#getDataSources()
     */
    @Override
    public Collection<ITeiidDataSource> getDataSources() throws Exception {
    	// Filters the PREVIEW sources from the results
    	Collection<ITeiidDataSource> resultSources = new ArrayList<ITeiidDataSource>();
        Collection<ITeiidDataSource> teiidSources = getServerImportManager().getDataSources();
        for(ITeiidDataSource dSource : teiidSources) {
        	String sourceName = dSource.getName();
        	if(sourceName!=null && !sourceName.startsWith(PREVIEW_DATASOURCE_PREFIX)) {
        		resultSources.add(dSource);
        	}
        }
        return resultSources;
    }

    /* (non-Javadoc)
     * @see org.teiid.designer.importer.ui.wizard.ITeiidImportServer#getDataSourceTemplateNames()
     */
    @Override
    public Set<String> getDataSourceTemplateNames() throws Exception {
        return getServerImportManager().getDataSourceTemplateNames();
    }

    /* (non-Javadoc)
     * @see org.teiid.designer.importer.ui.wizard.ITeiidImportServer#getTemplatePropertyDefns(java.lang.String)
     */
    @Override
    public Collection<TeiidPropertyDefinition> getTemplatePropertyDefns(String templateName) throws Exception {
        return getServerImportManager().getTemplatePropertyDefns(templateName);
    }

    /* (non-Javadoc)
     * @see org.teiid.designer.importer.ui.wizard.ITeiidImportServer#getOrCreateDataSource(java.lang.String, java.lang.String, java.lang.String, java.util.Properties)
     */
    @Override
    public ITeiidDataSource getOrCreateDataSource(String displayName,
                                                  String jndiName,
                                                  String typeName,
                                                  Properties properties) throws Exception {
        return getServerImportManager().getOrCreateDataSource(displayName, jndiName, typeName, properties);
    }

    /* (non-Javadoc)
     * @see org.teiid.designer.importer.ui.wizard.ITeiidImportServer#getTranslators()
     */
    @Override
    public Collection<ITeiidTranslator> getTranslators() throws Exception {
        return getServerImportManager().getTranslators();
    }

    /* (non-Javadoc)
     * @see org.teiid.designer.importer.ui.wizard.ITeiidImportServer#getDisplayName()
     */
    @Override
    public String getDisplayName() throws Exception {
        return getServerImportManager().getDisplayName();
    }

    /* (non-Javadoc)
     * @see org.teiid.designer.importer.ui.wizard.ITeiidImportServer#undeployDynamicVdb(java.lang.String)
     */
    @Override
    public void undeployDynamicVdb(String vdbName) throws Exception {
        getServerImportManager().undeployDynamicVdb(vdbName);
    }

    /* (non-Javadoc)
     * @see org.teiid.designer.importer.ui.wizard.ITeiidImportServer#getDataSourceProperties(java.lang.String)
     */
    @Override
    public Properties getDataSourceProperties(String sourceName) throws Exception {
        return getServerImportManager().getDataSourceProperties(sourceName);
    }
    
    // ----------------------------------------------------------------------------
    // DDL Import functionality
    // ----------------------------------------------------------------------------
    
    /**
     * Initialize the DdlImporter
     * @param projects the open projects
     */
    public void initDdlImporter(IProject[] projects) {
        ddlImporter = new DdlImporter(projects);
        ddlImporter.setModelType(ModelType.PHYSICAL_LITERAL);
        try {
            ddlFile = File.createTempFile("DdlTemp", ".ddl"); //$NON-NLS-1$ //$NON-NLS-2$
        } catch (IOException ex) {
            UTIL.log(ex);
        }
        this.ddlImporter.setDdlFileName(ddlFile.getAbsolutePath().toString());
    }
    
    /**
     * Get the DdlImporter instance
     * @return the DdlImporter instance
     */
    public DdlImporter getDdlImporter() {
        return ddlImporter;
    }
    
    /**
     * Get the DifferenceReport for the DDL
     * @param shell the shell
     * @param pct the percentage of work represented
     * @return the difference report
     */
    public DifferenceReport getDdlDifferenceReport(Shell shell, int pct) {
        try {
            createDifferenceReport(shell,pct);
        } catch (final Exception error) {
            error.printStackTrace();
            WidgetUtil.showError(error);
        }
        return ddlImporter.getChangeReport();
    }

    /*
     * Uses the DdlImporter to process the DDL and create the difference report
     * @param shell the shell
     * @totalWork the total pct of work the operation represents
     */
    private void createDifferenceReport( final Shell shell, final int totalWork ) throws InterruptedException, InvocationTargetException {
        
        // If the DDL Importer has a change report, return.
        if (ddlImporter.getChangeReport() != null) return;
        
        // TODO: This will be replace with 'getDdl', instead of temporary DDL
        String ddl = getDdl();

        // Update the ddl file contents
        try {
            writeDdlToTempFile(ddl);
        } catch (Exception ex) {
            UTIL.log(ex);
            WidgetUtil.showError(ex);
            return;
        }
        
        // Perform the DDL Import
        final List<String> msgs = new ArrayList<String>();
        new ProgressMonitorDialog(shell).run(true, true, new IRunnableWithProgress() {

            @Override
            public void run( final IProgressMonitor monitor ) {
                monitor.beginTask(Messages.TeiidImportManager_ImportingDDLMsg, 100);
                ddlImporter.importDdl(msgs, monitor, totalWork);
                monitor.done();
            }
        });
        
        // Errors Encountered - confirm with user whether to continue
        if (!msgs.isEmpty()
            && new MessageDialog(shell, Messages.TeiidImportManager_ConfirmDialgTitle, null, Messages.TeiidImportManager_ContinueImportMsg,
                                 MessageDialog.CONFIRM, new String[] {IDialogConstants.OK_LABEL, IDialogConstants.CANCEL_LABEL},
                                 SWT.NONE) {

                @Override
                protected Control createCustomArea( final Composite parent ) {
                    final org.eclipse.swt.widgets.List list = new org.eclipse.swt.widgets.List(parent, SWT.BORDER | SWT.V_SCROLL
                                                                                                       | SWT.H_SCROLL);
                    list.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
                    list.setItems(msgs.toArray(new String[msgs.size()]));
                    return list;
                }

                @Override
                protected final int getShellStyle() {
                    return SWT.SHEET;
                }
            }.open() != Window.OK) ddlImporter.undoImport();
        
    }
    
    /*
     * Writes the DDL String to a Temporary File, to pass to the DDL Importer
     * @param ddl the DDL string
     * @return the temp file that was created.
     */
    private void writeDdlToTempFile(String ddl) throws Exception {
        if(ddlFile!=null && ddlFile.canWrite()) {
            FileOutputStream tempOutputStream = new FileOutputStream(ddlFile);
            PrintStream out = null;
            try {
                out = new PrintStream(tempOutputStream);
                out.print(ddl);
            }
            finally {
                if (out != null) out.close();
            }
        }
    }
    
    /**
     * Delete the DDL temp file, if it exists
     */
    public void deleteDdlTempFile() {
        // Delete the temp DDL file
      if(ddlFile!=null && ddlFile.exists()) {
          ddlFile.delete();
      }
    }
    
    /**
     * Save the Model, using the DDL Difference Report
     * @param shell the shell
     * @return 'true' if the operation was successful, 'false' if not.
     */
    public boolean saveUsingDdlDiffReport(Shell shell) {
        try {
            DifferenceReport changeReport = getDdlDifferenceReport(shell,50);
            if(changeReport == null) return false;
            new ProgressMonitorDialog(shell).run(false, false, new IRunnableWithProgress() {

                @Override
                public void run( final IProgressMonitor monitor ) {
                    monitor.beginTask(Messages.TeiidImportManager_ImportingMsg, 100);
                    monitor.worked(50);
                    ddlImporter.save(monitor, 50);
                    monitor.done();
                }
            });
        } catch (final InterruptedException error) {
            undeployDynamicVdb();
            return false;
        } catch (final Exception error) {
            error.printStackTrace();
            WidgetUtil.showError(error);
            undeployDynamicVdb();
            return false;
        }
        
        return true;
    }

}
