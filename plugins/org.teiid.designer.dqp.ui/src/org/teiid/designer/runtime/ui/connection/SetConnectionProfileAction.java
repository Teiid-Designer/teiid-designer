/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime.ui.connection;

import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.ProfileManager;
import org.eclipse.datatools.connectivity.drivers.DriverInstance;
import org.eclipse.datatools.connectivity.drivers.DriverManager;
import org.eclipse.datatools.connectivity.drivers.jdbc.IJDBCDriverDefinitionConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.core.designer.util.StringUtilities;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.datatools.connection.ConnectionInfoProviderFactory;
import org.teiid.designer.datatools.connection.IConnectionInfoHelper;
import org.teiid.designer.datatools.connection.IConnectionInfoProvider;
import org.teiid.designer.datatools.ui.DatatoolsUiConstants;
import org.teiid.designer.datatools.ui.DatatoolsUiPlugin;
import org.teiid.designer.datatools.ui.dialogs.SelectConnectionProfileDialog;
import org.teiid.designer.jdbc.JdbcFactory;
import org.teiid.designer.jdbc.JdbcSource;
import org.teiid.designer.jdbc.impl.JdbcFactoryImpl;
import org.teiid.designer.ui.actions.IConnectionAction;
import org.teiid.designer.ui.actions.SortableSelectionAction;
import org.teiid.designer.ui.common.actions.ModelActionConstants;
import org.teiid.designer.ui.common.eventsupport.SelectionUtilities;
import org.teiid.designer.ui.editors.ModelEditor;
import org.teiid.designer.ui.editors.ModelEditorManager;
import org.teiid.designer.ui.util.JndiNameHelper;
import org.teiid.designer.ui.viewsupport.DesignerPropertiesUtil;
import org.teiid.designer.ui.viewsupport.ModelIdentifier;


/**
 * @since 8.0
 */
public class SetConnectionProfileAction extends SortableSelectionAction  implements IConnectionAction {
    private static final String label = DatatoolsUiConstants.UTIL.getString("SetConnectionProfileAction.title"); //$NON-NLS-1$

    private static final String NO_PROFILE_PROVIDER_FOUND_KEY = "NoProfileProviderFound"; //$NON-NLS-1$
    
    private Properties designerProperties;
    private IConnectionProfile connectionProfile;
    private static JndiNameHelper jndiHelper = new JndiNameHelper();

    /**
     * @since 5.0
     */
    public SetConnectionProfileAction() {
        super(label, SWT.DEFAULT);
        setImageDescriptor(DatatoolsUiPlugin.getDefault().getImageDescriptor(DatatoolsUiConstants.Images.SET_CONNECTION_ICON));
        setId(ModelActionConstants.Resource.SET_CONNECTION_PROFILE);
    }

    /**
     * @see org.teiid.designer.ui.actions.SortableSelectionAction#isValidSelection(org.eclipse.jface.viewers.ISelection)
     * @since 5.0
     */
    @Override
    public boolean isValidSelection( ISelection selection ) {
        // Enable for single/multiple Virtual Tables
        return sourceModelSelected(selection);
    }

    /**
     * @see org.eclipse.jface.action.IAction#run()
     * @since 5.0
     */
    @Override
    public void run() {
        // A) get the selected model and extract a "ConnectionProfileInfo" from it using the ConnectionProfileInfoHandler

        // B) Use ConnectionProfileHandler.getConnectionProfile(connectionProfileInfo) to query the user to
        // select a ConnectionProfile (or create new one)

        // C) Get the resulting ConnectionProfileInfo from the dialog and re-set the model's connection info
        // via the ConnectionProfileInfoHandler
        IFile modelFile = (IFile)SelectionUtilities.getSelectedObjects(getSelection()).get(0);

        boolean requiredStart = ModelerCore.startTxn(true, true, "Set Connection Profile", this); //$NON-NLS-1$
        boolean succeeded = false;
        try {
            ModelEditor editor = ModelEditorManager.getModelEditorForFile(modelFile, true);
            if (editor != null) {
                boolean isDirty = editor.isDirty();

                setConnectionProfile(modelFile);

                if (!isDirty && editor.isDirty()) {
                    editor.doSave(new NullProgressMonitor());
                }
                succeeded = true;
            }
        } catch (Exception e) {
        	logException(e);

            return;
        } finally {
            // if we started the txn, commit it.
            if (requiredStart) {
                if (succeeded) {
                    ModelerCore.commitTxn();
                } else {
                    ModelerCore.rollbackTxn();
                }
            }
        }
    }
    
    private void logException(Exception e) {
    	String msg = e.getMessage();
    	if( msg !=  null && msg.equalsIgnoreCase(NO_PROFILE_PROVIDER_FOUND_KEY) ) {
    		MessageDialog.openWarning(getShell(),
                                    DatatoolsUiConstants.UTIL.getString("SetConnectionProfileAction.noProfileProviderTitle"), //$NON-NLS-1$
                                    DatatoolsUiConstants.UTIL.getString("SetConnectionProfileAction.noProfileProviderMessage")); //$NON-NLS-1$
    	} else {
            MessageDialog.openError(getShell(),
                                    DatatoolsUiConstants.UTIL.getString("SetConnectionProfileAction.exceptionMessage"), e.getMessage()); //$NON-NLS-1$
            IStatus status = new Status(IStatus.ERROR, DatatoolsUiConstants.PLUGIN_ID,
                                        DatatoolsUiConstants.UTIL.getString("SetConnectionProfileAction.exceptionMessage"), e); //$NON-NLS-1$
            DatatoolsUiConstants.UTIL.log(status);
    	}
    }

    /**
     * Set the Model's connection profile
     * @param modelFile the model file
     * @return 'true' if the ConnectionFile was successfully reset, 'false' if not.
     * @throws Exception indicates problem
     */
    public static boolean setConnectionProfile( IFile modelFile ) throws Exception {

        SelectConnectionProfileDialog dialog = new SelectConnectionProfileDialog(Display.getCurrent().getActiveShell());

        dialog.open();

        if (dialog.getReturnCode() == Window.OK) {
            Object[] result = dialog.getResult();
            if (result != null && result.length == 1 && result[0] instanceof IConnectionProfile) {
                IConnectionProfile profile = (IConnectionProfile)result[0];
                
                // Cache existing jndi Name
                ModelResource modelResc = ModelUtil.getModelResource(modelFile, true);
                String existingJndiName = jndiHelper.getExistingJndiName(modelResc);
                // Update the JdbcSource properties (if exist) with the new profile.  User is prompted if the driver class will be changed.
                updateJdbcSourceAndConnectionInfo(modelFile,profile);
                
                // Check for JNDI name in model
                if( StringUtilities.isEmpty(existingJndiName)) {
                	jndiHelper.ensureJndiNameExists(modelResc, true);
                } else {
                	// Note that a connection profile may have a JNDI name in it.. so check if it's different
                	// If it is.. ignore the re-set of the existing jndi name
                	String newJndiName = jndiHelper.getExistingJndiName(modelResc);
                	if( !StringUtilities.areDifferent(newJndiName, existingJndiName)) {
                		jndiHelper.setJNDINameInTxn(modelResc, existingJndiName);
                	}
                }
                
                return true;
            }
        }

        return false;
    }
    
    public boolean setConnectionProfile() {
    	ISelection selection = getSelection();
    	
    	if( selection == null || selection.isEmpty() ) {
    		// Tell user they have not yet import to create their Source model yet.
    		MessageDialog.openInformation(Display.getCurrent().getActiveShell(), 
    				DatatoolsUiConstants.UTIL.getString("SetConnectionProfileAction.sourceModelUndefined.title"),  //$NON-NLS-1$
    						DatatoolsUiConstants.UTIL.getString("SetConnectionProfileAction.sourceModelUndefined.title")); //$NON-NLS-1$
    		return false;
    	}
        IFile modelFile = (IFile)SelectionUtilities.getSelectedObjects(selection).get(0);
        
    	if( this.connectionProfile == null ) {
            SelectConnectionProfileDialog dialog = new SelectConnectionProfileDialog(Display.getCurrent().getActiveShell());

            dialog.open();

            if (dialog.getReturnCode() == Window.OK) {
                Object[] result = dialog.getResult();
                if (result != null && result.length == 1) {
                	this.connectionProfile = (IConnectionProfile)result[0];
                }
            }
    	}

        if( this.connectionProfile != null ) {
            try {
				updateJdbcSourceAndConnectionInfo(modelFile, this.connectionProfile);
			} catch (Exception e) {
				logException(e);
				return false;
			}

            return true;
        }
    	
    	return false;
    }
    
    

    /**
     * @see org.teiid.designer.ui.actions.ISelectionAction#isApplicable(org.eclipse.jface.viewers.ISelection)
     * @since 5.0
     */
    @Override
    public boolean isApplicable( ISelection selection ) {
        return sourceModelSelected(selection);
    }

    private boolean sourceModelSelected( ISelection theSelection ) {
        boolean result = false;
        List<?> allObjs = SelectionUtilities.getSelectedObjects(theSelection);
        if (!allObjs.isEmpty() && allObjs.size() == 1) {
            Iterator<?> iter = allObjs.iterator();
            result = true;
            Object nextObj = null;
            while (iter.hasNext() && result) {
                nextObj = iter.next();

                if (nextObj instanceof IFile) {
                    result = ModelIdentifier.isRelationalSourceModel((IFile)nextObj);
                } else {
                    result = false;
                }
            }
        }

        return result;
    }

    /**
     * @param model the model file
     * @param connectionProfile the connection profile
     * @param driverClassChanged 'true' if driver class was changed, 'false' if not.
     * @throws Exception indicates a problem
     */
    public static void setConnectionInfo( IFile model,
                                          IConnectionProfile connectionProfile,
                                          boolean driverClassChanged) throws Exception {

        ModelResource mr = ModelUtil.getModelResource(model, true);

        ConnectionInfoProviderFactory manager = new ConnectionInfoProviderFactory();
        IConnectionInfoProvider provider = manager.getProvider(connectionProfile);

        if (null == provider) {
            throw new Exception(NO_PROFILE_PROVIDER_FOUND_KEY);
        }
            
        provider.setConnectionInfo(mr, connectionProfile);
    }

    private Shell getShell() {
        return Display.getCurrent().getActiveShell();
    }
    
    /**
     * Update the JdbcSource within the specified Model, using the supplied connection profile.  If the driver class in the provided
     * profile is different than the JdbcSource, the user is prompted to confirm.  
     * @param model the model file
     * @param profile the connection profile
     */
    private static void updateJdbcSourceAndConnectionInfo( IFile model, IConnectionProfile profile ) throws Exception {
        boolean driverClassChanged = false;
        ModelResource modelResc = ModelUtil.getModelResource(model, true);
        JdbcSource jdbcSource = null;
        if(modelResc!=null) {
            jdbcSource = getJdbcSource(modelResc);
            // JdbcSource was found - update it
            if(jdbcSource!=null) {
                Properties profileProps = profile.getBaseProperties();
                
                // Driver Class from ConnectionProfile
                String driverClassCP = profileProps.getProperty(IJDBCDriverDefinitionConstants.DRIVER_CLASS_PROP_ID);
                String driverClassJdbcSrc = jdbcSource.getDriverClass();
                
                // Check driver classes.  If different, warn user that import settings will be blown away
                if(driverClassCP!=null && !driverClassCP.equalsIgnoreCase(driverClassJdbcSrc)) {
                    String title = DatatoolsUiConstants.UTIL.getString("SetConnectionProfileAction.confirmDriverclassChangeDialogTitle");  //$NON-NLS-1$
                    String msg = DatatoolsUiConstants.UTIL.getString("SetConnectionProfileAction.confirmDriverclassChangeDialogMsg");  //$NON-NLS-1$
                    if(!MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), title, msg)) {
                        return;
                    }
                    driverClassChanged = true;
                }
                
                String profileName = profile.getName();
                if(!CoreStringUtil.isEmpty(profileName)) {
                    jdbcSource.setName(profileName);
                }
                
                String driverID = profileProps.getProperty("org.eclipse.datatools.connectivity.driverDefinitionID"); //$NON-NLS-1$
                DriverInstance driver = DriverManager.getInstance().getDriverInstanceByID(driverID);
                if(driver!=null) {
                    String driverName = driver.getName();
                    if(!CoreStringUtil.isEmpty(driverName)) {
                        jdbcSource.setDriverName(driverName);
                    }
                }
                
                String url = profileProps.getProperty(IJDBCDriverDefinitionConstants.URL_PROP_ID);
                if(!CoreStringUtil.isEmpty(url)) {
                    jdbcSource.setUrl(url);
                }
                String driverClass = profileProps.getProperty(IJDBCDriverDefinitionConstants.DRIVER_CLASS_PROP_ID);
                if(!CoreStringUtil.isEmpty(driverClass)) {
                    jdbcSource.setDriverClass(driverClass);
                }
                String userName = profileProps.getProperty(IJDBCDriverDefinitionConstants.USERNAME_PROP_ID);
                if(!CoreStringUtil.isEmpty(userName)) {
                    jdbcSource.setUsername(userName);
                }
                        
                // If driver classes was changed, reset the import settings
                if(driverClassChanged) {
                    JdbcFactory jdbcFactory = new JdbcFactoryImpl();
                    jdbcSource.setImportSettings(jdbcFactory.createJdbcImportSettings());
                }
            }
            
            // Set Connection Profile on the model
            SetConnectionProfileAction.setConnectionInfo(model, profile, driverClassChanged);  
            
            // Set flags on model to prevent AutoUpdate and CostUpdate - if driverClass changed
            if(driverClassChanged) {
                ModelUtil.setModelAnnotationPropertyValue(modelResc, IConnectionInfoHelper.JDBCCONNECTION_NAMESPACE+IConnectionInfoHelper.JDBCCONNECTION_ALLOW_AUTOUPDATE_KEY, "false"); //$NON-NLS-1$
                ModelUtil.setModelAnnotationPropertyValue(modelResc, IConnectionInfoHelper.JDBCCONNECTION_NAMESPACE+IConnectionInfoHelper.JDBCCONNECTION_ALLOW_COSTUPDATE_KEY, "false"); //$NON-NLS-1$
            }
        }
        modelResc.save(null, true);
    }
    
    /*
     * Get the JdbcSource object from the supplied ModelResource.  If none is found, null is returned
     * @param modelResc the Model Resource
     * @return the JdbcSource, null if not found
     */
    private static JdbcSource getJdbcSource(ModelResource modelResc) {
        JdbcSource jdbcSource = null;
        
        // Non-null model supplied. Transfer the import settings
        if (modelResc != null) {
            List<?> rootObjs = null;
            try {
                rootObjs = modelResc.getAllRootEObjects();
            } catch (Exception ex) {
                DatatoolsUiConstants.UTIL.log(ex);
                return null;
            }
            if(rootObjs!=null) {
                for (final Iterator<?> modelIter = rootObjs.iterator(); modelIter.hasNext();) {
                    final Object obj = modelIter.next();
                    if (obj instanceof JdbcSource) {
                        jdbcSource=(JdbcSource)obj;
                    }
                }
            }
        }
        return jdbcSource;
    }
    
    public boolean setProperties(Properties properties) {
    	this.designerProperties = properties;

    	if( properties != null ) {
    		String profileName = DesignerPropertiesUtil.getConnectionProfileName(designerProperties);
	    	if( profileName != null ) {
	    		this.connectionProfile = ProfileManager.getInstance().getProfileByName(profileName);
	    	}
	    	
	    	IFile model = DesignerPropertiesUtil.getSourceModel(this.designerProperties);
	    	if( model != null ) {
	    		this.setSelection(new StructuredSelection(model));
	    		return true;
	    	}
    	}
    	
    	return false;
    }


}
