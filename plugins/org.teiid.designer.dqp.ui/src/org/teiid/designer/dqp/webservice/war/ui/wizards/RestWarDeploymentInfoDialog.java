/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.dqp.webservice.war.ui.wizards;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.teiid.designer.dqp.webservice.war.WebArchiveBuilder;
import org.teiid.designer.dqp.webservice.war.WebArchiveBuilderFactory;
import org.teiid.designer.dqp.webservice.war.objects.RestProcedure;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.dqp.ui.DqpUiPlugin;
import com.metamatrix.modeler.webservice.WebServicePlugin;
import com.metamatrix.ui.internal.util.WidgetFactory;

/**
 * @since 7.4
 */
public class RestWarDeploymentInfoDialog extends TitleAreaDialog implements InternalModelerWarUiConstants {

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTANTS
    // /////////////////////////////////////////////////////////////////////////////////////////////
    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(RestWarDeploymentInfoDialog.class);
    private static final String TITLE = getString("title"); //$NON-NLS-1$
    private static final String MESSAGE_TITLE = getString("messageTitle"); //$NON-NLS-1$
    private static final String INITIAL_MESSAGE = getString("initialMessage"); //$NON-NLS-1$
    private static final String OVERWRITE_TARGET_WAR_TITLE = getString("overwriteTargetWar.title"); //$NON-NLS-1$
    private static final String OVERWRITE_TARGET_WAR_MESSAGE = getString("overwriteTargetWar.message"); //$NON-NLS-1$
    private static final String CREATING_WAR_FILE_MESSAGE_ID = "WarDeploymentInfoDialog.creatingWarFileMessage"; //$NON-NLS-1$

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    // /////////////////////////////////////////////////////////////////////////////////////////////
    private Button okButton;
    private RestWarDeploymentInfoDataPage warDeploymentInfoDataPage;

    private IFile theVdb;
    private Map<String, List<RestProcedure>> restProcedureMap;
    private IStatus deploymentStatus;
    private String warFileName;

    private IStatus initialStatus;
    
    private Properties designerProperties;

    /**
     * @param parent
     * @param title
     * @since 7.4
     */
    public RestWarDeploymentInfoDialog( Shell parent,
                                        IFile theVdb,
                                        Map<String, List<RestProcedure>> restProcedureMap,
                                        IStatus initialStatus,
                                        Properties designerProperties) {
        super(parent); // );

        ImageDescriptor id = DqpUiPlugin.getDefault().getImageDescriptor(InternalModelerWarUiConstants.WebServicesImages.WAR_FILE_ICON);
        if (id != null) setDefaultImage(id.createImage());

        this.theVdb = theVdb;
        this.restProcedureMap = restProcedureMap;
        this.initialStatus = initialStatus;
        deploymentStatus = new Status(IStatus.OK, DqpUiConstants.PLUGIN_ID, IStatus.OK, "WAR file created successfully", null);//$NON-NLS-1$
        this.designerProperties = designerProperties;
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // INSTANCE METHODS
    // /////////////////////////////////////////////////////////////////////////////////////////////
    public void setOkButtonEnable( boolean isEnable ) {
        if (okButton != null) {
            okButton.setEnabled(isEnable);
        }
    }

    /**
     * @see org.eclipse.jface.window.Window#setShellStyle(int)
     * @since 7.4
     */
    @Override
    protected void setShellStyle( int theNewShellStyle ) {
        super.setShellStyle(theNewShellStyle | SWT.RESIZE);
    }

    @Override
    protected void configureShell( Shell shell ) {
        super.configureShell(shell);
        shell.setText(TITLE);
    }

    @Override
    protected Control createDialogArea( Composite parent ) {
        Composite contents = WidgetFactory.createPanel(parent, SWT.NONE, GridData.FILL_BOTH, 1);
        warDeploymentInfoDataPage = null;
        warDeploymentInfoDataPage = new RestWarDeploymentInfoDataPage(contents, this, this.theVdb, this.initialStatus, this.designerProperties);

        ImageDescriptor id = DqpUiPlugin.getDefault().getImageDescriptor(InternalModelerWarUiConstants.WebServicesImages.WAR_FILE_ICON);
        if (id != null) this.setTitleImage(id.createImage());

        this.setTitle(MESSAGE_TITLE);
        this.setMessage(INITIAL_MESSAGE);

        return contents;
    }

    protected static String getString( final String id ) {
        return DqpUiConstants.UTIL.getString(I18N_PREFIX + id);
    }

    @Override
    protected void createButtonsForButtonBar( Composite parent ) {
        okButton = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);

        loadDefault();
    }

    /**
     * override the method
     * 
     * @see org.eclipse.jface.dialogs.Dialog#okPressed()
     * @since 7.4
     */
    @Override
    protected void okPressed() {
        // create VDB resource

        final IRunnableWithProgress op = new IRunnableWithProgress() {
            @Override
            public void run( final IProgressMonitor monitor ) throws InvocationTargetException {
                try {
                    execute(monitor);
                } catch (final Exception err) {
                    throw new InvocationTargetException(err);
                } finally {
                    monitor.done();
                }
            }
        };
        try {
            new ProgressMonitorDialog(getShell()).run(false, true, op);
        } catch (Throwable err) {
            if (err instanceof InvocationTargetException) {
                err = ((InvocationTargetException)err).getTargetException();
            }
            DqpUiConstants.UTIL.log(err);
        }

    }

    @Override
    public int open() {
        int rc = super.open();

        if (rc != CANCEL) {
            return deploymentStatus.getSeverity();
        }

        return rc;
    }

    void execute( IProgressMonitor monitor ) {
        try {

            WebArchiveBuilder webArchiveBuilder = WebArchiveBuilderFactory.createRestWebArchiveBuilder();
            monitor.worked(25);
            boolean createWar = true;
            // Check if file already exists, check whether it already exists...
            boolean targetExists = webArchiveBuilder.targetWarFileExists(RestWarDataserviceModel.getInstance().getProperties());
            if (targetExists) {
                // Ask user whether to proceed by overwriting the existing file
                createWar = MessageDialog.openQuestion(this.getShell(), OVERWRITE_TARGET_WAR_TITLE, OVERWRITE_TARGET_WAR_MESSAGE);

            }
            if (createWar) {
                monitor.beginTask(DqpUiConstants.UTIL.getString(CREATING_WAR_FILE_MESSAGE_ID, warFileName), 100);
                deploymentStatus = webArchiveBuilder.createWebArchive(RestWarDataserviceModel.getInstance().getProperties(),
                                                                      monitor);
                // log status
                DqpUiConstants.UTIL.log(deploymentStatus);
                setMessage(deploymentStatus.getMessage(), deploymentStatus.getSeverity());

                super.okPressed();
            }
        } catch (RuntimeException err) {
            // BusyCursor.endBusy();
            DqpUiConstants.UTIL.log(err);
            setMessage("Error while generating the WAR file check log for detail message.", InternalModelerWarUiConstants.ERROR); //$NON-NLS-1$
        }
    }

    /**
     * override the method
     * 
     * @see org.eclipse.jface.dialogs.Dialog#cancelPressed()
     * @since 7.4
     */
    @Override
    protected void cancelPressed() {
        super.cancelPressed();
    }

    /**
     * load default setting values for WAR file location, License file location, context name.
     * 
     * @since 7.4
     */
    private void loadDefault() {
        loadWarFileLocationDefault();
        loadContextNameDefault();
        loadJndiNameDefault();
        loadIncludeJarsDefault();

        RestWarDataserviceModel.getInstance().setRestProcedureArrayList(restProcedureMap);
        RestWarDataserviceModel.getInstance().setVdbFile(theVdb);

        warDeploymentInfoDataPage.loadData();
        warDeploymentInfoDataPage.setWarFileNameInDialog();
    }

    /**
     * set default war file location
     * 
     * @since 7.4
     */
    private void loadWarFileLocationDefault() {
        try {
            String warDir = WebServicePlugin.getDefaultWarFileSaveLocation();
            RestWarDataserviceModel.getInstance().setWarFilenameDefault(warDir);
        } catch (Throwable theThrowable) {
            DqpUiConstants.UTIL.log(theThrowable);
        }
    }

    /**
     * set default context name, by default context name is vdb name.
     * 
     * @since 7.4
     */
    private void loadContextNameDefault() {
        try {
            RestWarDataserviceModel.getInstance().setContextNameDefault("");//$NON-NLS-1$

            if (theVdb == null) {
                RestWarDataserviceModel.getInstance().setContextNameDefault("");//$NON-NLS-1$ 
                return;
            }

            String name = theVdb.getName().substring(0, theVdb.getName().lastIndexOf("."));//$NON-NLS-1$        
            RestWarDataserviceModel.getInstance().setContextNameDefault(name);

        } catch (Throwable theThrowable) {
            DqpUiConstants.UTIL.log(theThrowable);
        }
    }

    /**
     * set default jndiName, by default the jndiName is "{REPLACE_WITH_VDB_JNDI_NAME}".
     * 
     * @since 7.4
     */
    private void loadJndiNameDefault() {
        RestWarDataserviceModel.getInstance().setJndiNameDefault("{REPLACE_WITH_VDB_JNDI_NAME}");//$NON-NLS-1$
    }

    /**
     * set default for includeJars, by default includeJars is true.
     * 
     * @since 7.4
     */
    private void loadIncludeJarsDefault() {
        RestWarDataserviceModel.getInstance().setIncludeJars(true);
    }

    public void setWarFileName( String name ) {
        warFileName = name;
    }

    public String getWarFileName() {
        return warFileName;
    }

}
