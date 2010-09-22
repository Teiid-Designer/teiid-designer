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
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Composite;
import org.teiid.core.util.FileUtils;
import org.teiid.designer.dqp.webservice.war.WebArchiveBuilder;
import org.teiid.designer.dqp.webservice.war.WebArchiveBuilderFactory;
import com.metamatrix.modeler.dqp.ui.DqpUiPlugin;

/**
 * @since 7.1
 */
public class WarDeploymentInfoDataPage extends WarDeploymentInfoPanel {

    private String ERROR_MESSAGE = null;
    private IStatus status;

    /**
     * @param parent
     * @param dialog
     * @param theVdb
     * @param theVdbContext
     * @since 7.1
     */
    public WarDeploymentInfoDataPage( Composite parent,
                                      WarDeploymentInfoDialog dialog,
                                      IFile theVdb,
                                      IStatus initialStatus ) {
        super(parent, dialog, theVdb);
    }

    /**
     * @see com.metamatrix.modeler.internal.webservice.ui.war.wizards.WarDeploymentInfoPanel#validatePage()
     * @since 7.1
     */
    @Override
    protected void validatePage() {

        boolean isValid = validateContext() && validatePort() && validateHost() && validateJNDI() && validateTNS()
                          && validateWARFileFolder() && validateSecurityRole() && validateSecurityRealm() 
                          && validateSecurityUsername() && validateSecurityPassword();

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
     * @since 7.1
     */
    private void createStatus( int statusCode,
                               String msg,
                               int validationStatusCode ) {
        status = new Status(statusCode, DqpUiPlugin.PLUGIN_ID, validationStatusCode, msg, null);
    }

    /**
     * validate context name
     * 
     * @return boolean
     * @since 7.1
     */
    private boolean validateContext() {

        String text = txfContext.getText();
        if (text == null || text.length() == 0) {
            ERROR_MESSAGE = getString("contextErrorMessage");//$NON-NLS-1$
            createStatus(IStatus.ERROR, ERROR_MESSAGE, InternalModelerWarUiConstants.VALIDATECONTEXT);
            return false;
        }

        WebArchiveBuilder webArchiveBuilder = WebArchiveBuilderFactory.create();
        status = webArchiveBuilder.validateContextName(text);

        if (!status.isOK()) {
            return false;
        }

        WarDataserviceModel.getInstance().setContextName(text);
        this.settings.put(this.CONTEXTNAME, text);

        return true;
    }

    /**
     * validate port
     * 
     * @return boolean
     * @since 7.1
     */
    private boolean validatePort() {

        String text = txfPort.getText();
        if (text == null || text.length() == 0) {
            ERROR_MESSAGE = getString("portErrorMessage");//$NON-NLS-1$
            createStatus(IStatus.ERROR, ERROR_MESSAGE, InternalModelerWarUiConstants.VALIDATEPORT);
            return false;
        }

        WarDataserviceModel.getInstance().setPort(text);
        this.settings.put(this.PORT, text);

        return true;
    }

    /**
     * validate port
     * 
     * @return boolean
     * @since 7.1
     */
    private boolean validateJNDI() {

        String text = txfJNDIName.getText();
        if (text == null || text.length() == 0) {
            ERROR_MESSAGE = getString("jndiErrorMessage");//$NON-NLS-1$
            createStatus(IStatus.ERROR, ERROR_MESSAGE, InternalModelerWarUiConstants.VALIDATEJNDI);
            return false;
        }

        WarDataserviceModel.getInstance().setJndiNameDefault(text);
        this.settings.put(this.JNDI_NAME, text);

        return true;
    }

    /**
     * validate host name
     * 
     * @return boolean
     * @since 7.1
     */
    private boolean validateHost() {

        String text = txfHost.getText();
        if (text == null || text.length() == 0) {
            ERROR_MESSAGE = getString("hostErrorMessage");//$NON-NLS-1$
            createStatus(IStatus.ERROR, ERROR_MESSAGE, InternalModelerWarUiConstants.VALIDATEHOST);
            return false;
        }

        WarDataserviceModel.getInstance().setHostName(text);
        this.settings.put(this.HOST, text);

        return true;
    }

    /**
     * validate target namespace
     * 
     * @return boolean
     * @since 7.1
     */
    private boolean validateTNS() {

        String text = txfNamespace.getText();
        if (text == null || text.length() == 0) {
            ERROR_MESSAGE = getString("tnsErrorMessage");//$NON-NLS-1$
            createStatus(IStatus.ERROR, ERROR_MESSAGE, InternalModelerWarUiConstants.VALIDATETNS);
            return false;
        }

        WarDataserviceModel.getInstance().setTns(text);
        this.settings.put(this.NAMESPACE, text);

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
        if (this.basicSecurityButton.getSelection()){
        
        	if (text == null || text.length() == 0) {
                ERROR_MESSAGE = getString("securityRealmMessage");//$NON-NLS-1$
                createStatus(IStatus.ERROR, ERROR_MESSAGE, InternalModelerWarUiConstants.VALIDATEREALM);
                return false;
            }

        }
        
        WarDataserviceModel.getInstance().setSecurityRealmDefault(text);
        this.settings.put(this.SECURITY_REALM, text);

        return true;
    }
    
    /**
     * validate security username
     * 
     * @return boolean
     * @since 7.1.1
     */
    private boolean validateSecurityUsername() {

        String text = txfSecurityUsername.getText();
        if (this.wsSecurityButton.getSelection()){
        
        	if (text == null || text.length() == 0) {
                ERROR_MESSAGE = getString("securityUsernameMessage");//$NON-NLS-1$
                createStatus(IStatus.ERROR, ERROR_MESSAGE, InternalModelerWarUiConstants.VALIDATEUSERNAME);
                return false;
            }

        }
        
        WarDataserviceModel.getInstance().setSecurityUsernameDefault(text);
        this.settings.put(this.SECURITY_USERNAME, text);

        return true;
    }
    
    /**
     * validate security password
     * 
     * @return boolean
     * @since 7.1.1
     */
    private boolean validateSecurityPassword() {

    	 String text = txfSecurityPassword.getText();
         if (this.wsSecurityButton.getSelection()){
         
        	if (text == null || text.length() == 0) {
                ERROR_MESSAGE = getString("securityPasswordMessage");//$NON-NLS-1$
                createStatus(IStatus.ERROR, ERROR_MESSAGE, InternalModelerWarUiConstants.VALIDATEPASSWORD);
                return false;
            }

        }
        
        WarDataserviceModel.getInstance().setSecurityPasswordDefault(text);
        this.settings.put(this.SECURITY_PASSWORD, text);

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
        if (this.basicSecurityButton.getSelection()){
        
        	if (text == null || text.length() == 0) {
                ERROR_MESSAGE = getString("securityRoleMessage");//$NON-NLS-1$
                createStatus(IStatus.ERROR, ERROR_MESSAGE, InternalModelerWarUiConstants.VALIDATEREALM);
                return false;
            }

        }
        
        WarDataserviceModel.getInstance().setSecurityRoleDefault(text);
        this.settings.put(this.SECURITY_ROLE, text);

        return true;
    }

    /**
     * check to see if a war folder location exist
     * 
     * @return boolean
     * @since 7.1
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

        WarDataserviceModel.getInstance().setWarFileLocation(text);
        this.settings.put(this.WARFILELOCATION, text);

        return true;
    }

}
