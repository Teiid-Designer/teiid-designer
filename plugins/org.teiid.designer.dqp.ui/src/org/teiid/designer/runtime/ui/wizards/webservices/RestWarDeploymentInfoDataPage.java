/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime.ui.wizards.webservices;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Composite;
import org.teiid.core.designer.CoreModelerPlugin;
import org.teiid.core.designer.TeiidDesignerException;
import org.teiid.designer.runtime.ui.DqpUiConstants;
import org.teiid.designer.runtime.ui.wizards.webservices.util.WebArchiveBuilder;
import org.teiid.designer.runtime.ui.wizards.webservices.util.WebArchiveBuilderFactory;

/**
 * @since 8.0
 */
public class RestWarDeploymentInfoDataPage extends RestWarDeploymentInfoPanel {

    private static final String TEMP_FILE = "delete.me"; //$NON-NLS-1$
    private static final String TEMP_FILE_RENAMED = "delete.me.old"; //$NON-NLS-1$
    
    private String ERROR_MESSAGE = null;
    private IStatus status;

    /**
     * @param parent
     * @param dialog
     * @param theVdb
     * @param initialStatus
     * @param designerProperties
     *
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
     * @see org.teiid.designer.runtime.ui.wizards.webservices.webservice.ui.war.wizards.WarDeploymentInfoPanel#validatePage()
     * @since 7.4
     */
    @Override
    protected void validatePage() {

        boolean isValid = validateContext() && validateJNDI() && validateWARFileFolder() &&
                                       validateSecurityRole() && validateSecurityRealm();

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
                RestWarDeploymentInfoDataPage.testDirectoryPermissions(text);
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

    /**
     * validate target security realm
     *
     * @return boolean
     * @since 7.1
     */
    private boolean validateSecurityRealm() {

        String text = txfSecurityRealm.getText();
        if (this.basicSecurityButton.getSelection()) {

            if (text == null || text.length() == 0) {
                ERROR_MESSAGE = getString("securityRealmMessage");//$NON-NLS-1$
                createStatus(IStatus.ERROR, ERROR_MESSAGE, InternalModelerWarUiConstants.VALIDATEREALM);
                return false;
            }

        }

        RestWarDataserviceModel.getInstance().setSecurityRealmDefault(text);
        this.settings.put(this.SECURITY_REALM, text);

        return true;
    }

    /**
     * validate target security role
     *
     * @return boolean
     * @since 7.1
     */
    private boolean validateSecurityRole() {

        String text = txfSecurityRole.getText();
        if (this.basicSecurityButton.getSelection()) {

            if (text == null || text.length() == 0) {
                ERROR_MESSAGE = getString("securityRoleMessage");//$NON-NLS-1$
                createStatus(IStatus.ERROR, ERROR_MESSAGE, InternalModelerWarUiConstants.VALIDATEREALM);
                return false;
            }

        }

        RestWarDataserviceModel.getInstance().setSecurityRoleDefault(text);
        this.settings.put(this.SECURITY_ROLE, text);

        return true;
    }

    /**
     * Test whether it's possible to read and write files in the specified directory. 
     * @param dirPath Name of the directory to test
     * @throws TeiidDesignerException
     * @since 4.3
     */
    public static void testDirectoryPermissions(String dirPath) throws TeiidDesignerException {
        
        //try to create a file
        File tmpFile = new File(dirPath + File.separatorChar + TEMP_FILE);
        boolean success = false;
        try {
            success = tmpFile.createNewFile();
        } catch (IOException e) {
        }
        if (!success) {
            final String msg = CoreModelerPlugin.Util.getString("FileUtils.Unable_to_create_file_in", dirPath); //$NON-NLS-1$            
            throw new TeiidDesignerException(msg);
        }
        

        //test if file can be written to
        if (!tmpFile.canWrite()) {
            final String msg = CoreModelerPlugin.Util.getString("FileUtils.Unable_to_write_file_in", dirPath); //$NON-NLS-1$            
            throw new TeiidDesignerException(msg);
        }

        //test if file can be read
        if (!tmpFile.canRead()) {
            final String msg = CoreModelerPlugin.Util.getString("FileUtils.Unable_to_read_file_in", dirPath); //$NON-NLS-1$            
            throw new TeiidDesignerException(msg);
        }

        //test if file can be renamed
        File newFile = new File(dirPath + File.separatorChar + TEMP_FILE_RENAMED);
        success = false;
        try {
            success = tmpFile.renameTo(newFile);
        } catch (Exception e) {
        }
        if (!success) {
            final String msg = CoreModelerPlugin.Util.getString("FileUtils.Unable_to_rename_file_in", dirPath); //$NON-NLS-1$            
            throw new TeiidDesignerException(msg);
        }

        //test if file can be deleted
        success = false;
        try {
            success = newFile.delete();
        } catch (Exception e) {
        }
        if (!success) {
            final String msg = CoreModelerPlugin.Util.getString("FileUtils.Unable_to_delete_file_in", dirPath); //$NON-NLS-1$            
            throw new TeiidDesignerException(msg);
        }
    }

}
