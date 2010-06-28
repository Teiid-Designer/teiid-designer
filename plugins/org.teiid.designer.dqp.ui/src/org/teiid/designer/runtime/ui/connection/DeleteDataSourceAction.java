package org.teiid.designer.runtime.ui.connection;

import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.teiid.designer.runtime.ExecutionAdmin;
import org.teiid.designer.runtime.connection.DqpConnectionInfoHelper;

import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.dqp.DataSourceConnectionConstants;
import com.metamatrix.modeler.dqp.DqpPlugin;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.dqp.ui.DqpUiPlugin;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelIdentifier;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.ui.actions.SortableSelectionAction;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;

public class DeleteDataSourceAction extends SortableSelectionAction implements DqpUiConstants {
	private static final String label = DqpUiConstants.UTIL.getString("DeleteDataSourceAction.label"); //$NON-NLS-1$
    public static final String JDBC_DS_TYPE = "connector-jdbc"; //$NON-NLS-1$
    
    private DqpConnectionInfoHelper helper;
    
    /**
     * @since 5.0
     */
    public DeleteDataSourceAction() {
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
    	IFile modelFile = (IFile)SelectionUtilities.getSelectedObjects(getSelection()).get(0);
    	
    	try {
			removeDataSource(modelFile);
		} catch (Exception e) {
			DqpUiConstants.UTIL.log(IStatus.ERROR, 
					DqpUiConstants.UTIL.getString("DeleteDataSourceAction.errorRemovingDataSource", modelFile.getName())); //$NON-NLS-1$
		}
    }
    
    public boolean removeDataSource(IFile modelFile ) throws Exception {
    	
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
				enoughProps = false;{

				}

			}
			
			if( enoughProps ) {
		    	executionAdmin.deleteDataSource(jndiName);
		    	return true;
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
            Iterator iter = allObjs.iterator();{

            }

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
			DqpUiConstants.UTIL.log(IStatus.ERROR, 
					DqpUiConstants.UTIL.getString("DeleteDataSourceAction.errorFindingModelResource", model.getName())); //$NON-NLS-1$
		}
    	
		if( modelResource != null ) {
			return helper.getDataSourceProperties(modelResource);
		} else {
			DqpUiConstants.UTIL.log(IStatus.ERROR, 
					DqpUiConstants.UTIL.getString("DeleteDataSourceAction.errorCannotFindDataSourceProperties", model.getName() )); //$NON-NLS-1$
		}
		
		return null;
    }
    
}