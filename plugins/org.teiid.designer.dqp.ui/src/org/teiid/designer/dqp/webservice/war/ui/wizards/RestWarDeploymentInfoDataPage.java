/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.dqp.webservice.war.ui.wizards;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Composite;
import org.teiid.core.util.FileUtils;
import org.teiid.designer.dqp.webservice.war.WebArchiveBuilder;
import org.teiid.designer.dqp.webservice.war.WebArchiveBuilderFactory;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;

/**
 * @since 7.4
 */
public class RestWarDeploymentInfoDataPage extends RestWarDeploymentInfoPanel {

    private String ERROR_MESSAGE = null;
    private IStatus status;

    /**
     * @param parent
     * @param dialog
     * @param theVdb
     * @param theVdbContext
     * @since 7.4
     */
    public RestWarDeploymentInfoDataPage( Composite parent,
                                          RestWarDeploymentInfoDialog dialog,
                                          IFile theVdb,
                                          IStatus initialStatus,
                                          Properties designerProperties) {
        super(parent, dialog, theVdb, designerProperties);
    }

    /**
     * @see com.metamatrix.modeler.internal.webservice.ui.war.wizards.WarDeploymentInfoPanel#validatePage()
     * @since 7.4
     */
    @Override
    protected void validatePage() {

        boolean isValid = validateContext() && validateJNDI() && validateWARFileFolder();

        if (!isValid) {
            setDialogMessage(status);
        } else {
            setDialogMessage(isValid);
        }
    }

    /**
     * @param statusCode
     * @param msg
     * @param validationStatusCode
     * @since 7.4
     */
    private void createStatus( int statusCode,
                               String msg,
                               int validationStatusCode ) {
        status = new Status(statusCode, DqpUiConstants.PLUGIN_ID, validationStatusCode, msg, null);
    }

    /**
     * validate context name
     * 
     * @return boolean
     * @since 7.4
     */
    private boolean validateContext() {

        String text = txfContext.getText();
        if (text == null || text.length() == 0) {
            ERROR_MESSAGE = getString("contextErrorMessage");//$NON-NLS-1$
            createStatus(IStatus.ERROR, ERROR_MESSAGE, InternalModelerWarUiConstants.VALIDATECONTEXT);
            return false;
        }

        WebArchiveBuilder webArchiveBuilder = WebArchiveBuilderFactory.createRestWebArchiveBuilder();
        status = webArchiveBuilder.validateContextName(text);

        if (!status.isOK()) {
            return false;
        }

        RestWarDataserviceModel.getInstance().setContextName(text);
        this.settings.put(this.CONTEXTNAME, text);

        return true;
    }

    /**
     * validate JNDI
     * 
     * @return boolean
     * @since 7.4
     */
    private boolean validateJNDI() {

        String text = txfJNDIName.getText();
        if (text == null || text.length() == 0) {
            ERROR_MESSAGE = getString("jndiErrorMessage");//$NON-NLS-1$
            createStatus(IStatus.ERROR, ERROR_MESSAGE, InternalModelerWarUiConstants.VALIDATEJNDI);
            return false;
        }

        RestWarDataserviceModel.getInstance().setJndiNameDefault(text);
        this.settings.put(this.JNDI_NAME, text);

        return true;
    }

    /**
     * check to see if a war folder location exist
     * 
     * @return boolean
     * @since 7.4
     */
    private boolean validateWARFileFolder() {
        // Get directory text from the textField
        String text = txfWarFileDeploymentLocation.getText();
        if (text == null || text.length() == 0) {
            ERROR_MESSAGE = getString("warFilePleaseEnterFile.message"); //$NON-NLS-1$
            createStatus(IStatus.ERROR, ERROR_MESSAGE, InternalModelerWarUiConstants.VALIDATEWARFILE);
            return false;
        }

        // Test the directory validity
        text = text.trim();
        File file = new File(text);
        // If the directory already exists, check it's permissions
        if (file.exists()) {
            // Check the directory permissions if it exists
            try {
                FileUtils.testDirectoryPermissions(text);
            } catch (Exception e) {
                String message = getString("warFileSaveFolderExistsButPermissionProblem.message"); //$NON-NLS-1$
                createStatus(IStatus.ERROR, message, InternalModelerWarUiConstants.VALIDATEWARFILE);
                return false;
            }
        } else {
            // to a create to ensure its a good name
            boolean success = false;
            try {
                success = file.createNewFile();
            } catch (IOException e) {
            }
            if (!success) {
                String message = getString("warFileSaveFolderNameProblem.message"); //$NON-NLS-1$
                createStatus(IStatus.ERROR, message, InternalModelerWarUiConstants.VALIDATEWARFILE);
                return false;
            }
            // test if file can be deleted
            success = false;
            try {
                success = file.delete();
            } catch (Exception e) {
            }
            if (!success) {
                String message = getString("warFileSaveFolderExistsButPermissionProblem.message"); //$NON-NLS-1$
                createStatus(IStatus.ERROR, message, InternalModelerWarUiConstants.VALIDATEWARFILE);
                return false;
            }

        }

        RestWarDataserviceModel.getInstance().setWarFileLocation(text);
        this.settings.put(this.WARFILELOCATION, text);

        return true;
    }

}
