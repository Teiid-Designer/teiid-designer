package org.teiid.designer.runtime.ui.connection;

import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.teiid.designer.runtime.ExecutionAdmin;
import org.teiid.designer.runtime.ExecutionConfigurationEvent;
import org.teiid.designer.runtime.TeiidDataSource;
import org.teiid.designer.runtime.connection.DqpConnectionInfoHelper;

import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.dqp.DataSourceConnectionConstants;
import com.metamatrix.modeler.dqp.DqpPlugin;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.dqp.ui.DqpUiPlugin;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelIdentifier;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.modeler.ui.actions.SortableSelectionAction;
import com.metamatrix.ui.internal.dialog.AbstractPasswordDialog;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;
import com.metamatrix.ui.internal.widget.Dialog;

public class CreateDataSourceAction  extends SortableSelectionAction implements DqpUiConstants {
    private static final String label = "Create Teiid Data Source"; //$NON-NLS-1$
    public static final String JDBC_DS_TYPE = "connector-jdbc"; //$NON-NLS-1$
    
    private DqpConnectionInfoHelper helper;
    
    private String pwd;
    
    /**
     * @since 5.0
     */
    public CreateDataSourceAction() {
        super(label, SWT.DEFAULT);
        setImageDescriptor(DqpUiPlugin.getDefault().getImageDescriptor(Images.SOURCE_BINDING_ICON));
        this.helper = new DqpConnectionInfoHelper();
    }

    /**
     * @see com.metamatrix.modeler.ui.actions.SortableSelectionAction#isValidSelection(org.eclipse.jface.viewers.ISelection)
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
    	//    select a ConnectionProfile (or create new one)
    	
    	// C) Get the resulting ConnectionProfileInfo from the dialog and re-set the model's connection info
    	//    via the ConnectionProfileInfoHandler
    	IFile modelFile = (IFile)SelectionUtilities.getSelectedObjects(getSelection()).get(0);
    	
    	try {
			createDataSource(modelFile);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public boolean createDataSource(IFile modelFile ) throws Exception {
    	
    	Properties properties = getConnectionProperties(modelFile);
    	
    	
    	if( properties != null && !properties.isEmpty() ) {
    		ExecutionAdmin executionAdmin = DqpPlugin.getInstance().getServerManager().getDefaultServer().getAdmin();
	    	String name = modelFile.getFullPath().removeFileExtension().lastSegment();
    		String jndiName = this.helper.generateUniqueConnectionJndiName(name, modelFile.getFullPath(), DqpPlugin.workspaceUuid().toString());

	    	boolean enoughProps = true;
        	
			if ( properties.get(DataSourceConnectionConstants.DRIVER_CLASS) == null ) {
				enoughProps = false;
			}
			
			if ( properties.get(DataSourceConnectionConstants.URL) == null ) {
				enoughProps = false;
			}
			
			if (properties.get(DataSourceConnectionConstants.USERNAME) == null) {
				enoughProps = false;
			}
			
			if( properties.get(DataSourceConnectionConstants.PASSWORD) == null) {
                Shell sh = UiPlugin.getDefault().getCurrentWorkbenchWindow().getShell();

                int result = new AbstractPasswordDialog(sh) {
                    @Override
                    protected boolean isPasswordValid( final String password ) {
                    	pwd = password;
                        return true;
                    }
                }.open();
                if( result == Dialog.OK) {
                	properties.put(DataSourceConnectionConstants.PASSWORD, this.pwd);
                }
				
			}
			
			if( enoughProps ) {
		    	// Insure this name exists as data source on server
		    	String dsTypeName = JDBC_DS_TYPE; //ModelerDqpUtils.findMatchingDataSourceTypeName(matchableStrings, defaultAdmin.getDataSourceTypeNames());
		    	TeiidDataSource tds = executionAdmin.getOrCreateDataSource(modelFile.getProjectRelativePath().lastSegment(), jndiName, dsTypeName, properties);
		    	
		    	if( tds != null ) {
		    		DqpPlugin.getInstance().getServerManager().notifyListeners(ExecutionConfigurationEvent.createAddDataSourceEvent(tds));
		    		return true;
		    	}
			}
    		
    	}
		
		return false;
    }
    /**
     * @see com.metamatrix.modeler.ui.actions.ISelectionAction#isApplicable(org.eclipse.jface.viewers.ISelection)
     * @since 5.0
     */
    @Override
    public boolean isApplicable( ISelection selection ) {
        return sourceModelSelected(selection);
    }

    private boolean sourceModelSelected( ISelection theSelection ) {
        boolean result = false;
        List allObjs = SelectionUtilities.getSelectedObjects(theSelection);
        if (!allObjs.isEmpty() && allObjs.size() == 1) {
            Iterator iter = allObjs.iterator();
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
    
    public Properties getConnectionProperties(IFile model) throws ModelWorkspaceException {
    	DqpConnectionInfoHelper helper = new DqpConnectionInfoHelper();
    	
    	ModelResource modelResource = null;
    	
    	try {
    		modelResource = ModelUtilities.getModelResource(model, true);
		} catch (ModelWorkspaceException e) {
			// TODO LOG THIS EXCEPTION
			e.printStackTrace();
		}
    	
		if( modelResource != null ) {
			return helper.getDataSourceProperties(modelResource);
		} else {
			// TODO: THROW EXCEPTION OR LOG ERROR HERE!!!
		}
		
		return null;
    }
    
}
