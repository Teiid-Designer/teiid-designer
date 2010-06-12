package org.teiid.designer.runtime.connection;

import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.ecore.EObject;

import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.dqp.DqpPlugin;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.core.workspace.WorkspaceResourceFinderUtil;
import com.metamatrix.modeler.jdbc.JdbcSource;

/**
 * 
 *
 */
public class JdbcSourceUtils {
    /**
     * This is the name of the property that stores driver class name on the jdbc import settings of a physical models. 
     */
    public static final String JDBC_IMPORT_DRIVER_CLASS = "com.metamatrix.modeler.jdbc.JdbcSource.driverClass"; //$NON-NLS-1$

    /**
     * This is the name of the property that stores driver class name on the jdbc import settings of a physical models.
     */    
    public static final String JDBC_IMPORT_URL = "com.metamatrix.modeler.jdbc.JdbcSource.url"; //$NON-NLS-1$

    /**
     * This is the name of the property that stores user name on the jdbc import settings of a physical models.
     */    
    public static final String JDBC_IMPORT_USERNAME = "com.metamatrix.modeler.jdbc.JdbcSource.username"; //$NON-NLS-1$
    
    /**
     * This is the name of the property that stores driver name on the jdbc import settings of a physical models.
     */
    public static final String JDBC_IMPORT_DRIVER_NAME = "com.metamatrix.modeler.jdbc.JdbcSource.driverName"; //$NON-NLS-1$


    /**
     * Get a <code>Map</code> of property name to values for JDBC connection properties stored on a model reference on the vdb
     * manifest model.
     * 
     * @param modelPath the IPath of the model
     * @return a map of JDBC connection properties (never <code>null</code> but maybe empty)
     * @throws ModelWorkspaceException 
     * @since 5.0
     */
    public static Properties getModelJdbcProperties( IPath modelPath ) throws ModelWorkspaceException {
        
        IResource resource = WorkspaceResourceFinderUtil.findIResourceByPath(modelPath);

        if( resource == null ) {
        	return null;
        }
        
        
        Properties result = new Properties();

        // Find Model's JDBC PRoperties here!!!!

        JdbcSource jdbcSource = findJdbcSource(resource);  

         if (jdbcSource.getDriverClass() != null) {
        	 result.put(JDBC_IMPORT_DRIVER_CLASS, jdbcSource.getDriverClass());
         } 
         
         if (jdbcSource.getUrl() != null) {
        	 result.put(JDBC_IMPORT_URL, jdbcSource.getUrl());
         }
         
         if (jdbcSource.getUsername() != null) {
        	 result.put(JDBC_IMPORT_USERNAME, jdbcSource.getUsername());
         }
         
         if (jdbcSource.getDriverName() != null) {
        	 result.put(JDBC_IMPORT_DRIVER_NAME, jdbcSource.getDriverName());
         }


        return result;
    }
    
    /**
     * @param resource
     * @return the JdbcSource object
     * @throws ModelWorkspaceException
     */
    public static JdbcSource findJdbcSource(final IResource resource) throws ModelWorkspaceException {
    	
    	ModelResource mr = ModelUtil.getModelResource((IFile)resource, true);
    	if( mr != null ) {
    		Collection allEObjects = mr.getEObjects();
    		for( Iterator iter = allEObjects.iterator(); iter.hasNext();) {
    			EObject nextEObject = (EObject)iter.next();
    			if( nextEObject instanceof JdbcSource ) {
    				return (JdbcSource)nextEObject;
    			}
    		}
    	} else {
    		throw new ModelWorkspaceException(DqpPlugin.Util.getString("errorFindingModelResourceForModelFile", resource.getName())); //$NON-NLS-1$
    	}
    	
    	return null;
    }
}
