/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.tools.genericimport.ui.wizards;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;

import com.metamatrix.core.util.StringUtil;
import com.metamatrix.metamodels.builder.translator.ResultSetTranslator;
import com.metamatrix.metamodels.relational.RelationalPackage;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.internal.dqp.ui.actions.ExecuteVdbAction;
import com.metamatrix.modeler.ui.UiConstants;

/**
 * GenericImportManager - Business Object for interacting with GUI
 */
public class GenericImportManager implements UiConstants {
  
	private static final String MODEL_NAME = "Name";  //$NON-NLS-1$
	private static final String MODEL_IS_PHYSICAL = "IsPhysical";  //$NON-NLS-1$
	private static final String MODEL_PRIMARY_URI = "PrimaryMetamodelURI";  //$NON-NLS-1$
	private static final String SYSTEM_MODEL_QUERY = "SELECT * FROM System.Models";  //$NON-NLS-1$
	private static final String SYSTEM_MODEL_NAME = "System"; //$NON-NLS-1$
	
    //============================================================
    // Instance variables
    //============================================================
    private IFile selectedVDB;
    private IStatus vdbExecutionStatus;
    private ExecuteVdbAction executeVdbAction;
    private Connection sqlConnection;
    
    private String selectedVDBModel;
    private IContainer targetLocation;
            
    //============================================================
    // Constructors
    //============================================================
    /**
     * Constructor.
     */
    public GenericImportManager( ) {
    	// ExecuteVDB action - user interaction disabled
    	this.executeVdbAction = new ExecuteVdbAction(false);
    }
    
    /**
     * Get the selected target location where any new models will be placed.
     * @return the target location for new models 
     */
	public IContainer getTargetLocation() {
		return targetLocation;
	}

    /**
     * Set the target location where any new models will be placed.
     * @param targetContainer the target location
     */
	public void setTargetLocation(IContainer targetContainer) {
		this.targetLocation = targetContainer;
	}
	
    /**
     * Get the user-selected VDB file.  
     * @return the selected source VDB file.
     */
	public IFile getSelectedVDB() {
		return selectedVDB;
	}

	public String getSelectedVDBName() {
		if(this.selectedVDB!=null) {
			String vdbFileName = selectedVDB.getName();
			if(StringUtil.endsWithIgnoreCase(vdbFileName,ModelerCore.VDB_FILE_EXTENSION)) {
				int lastIndex = vdbFileName.lastIndexOf(ModelerCore.VDB_FILE_EXTENSION);
				return vdbFileName.substring(0,lastIndex);
			}
		}
		return null;
	}
	
    /**
     * Set the user-selected VDB file.  Set it up for execution and update the execution status.
     * @param selectedVDB the specified VDB file.
     */
	public void setSelectedVDB(IFile selectedVDB) {
    	this.executeVdbAction = new ExecuteVdbAction(false);
    	// If holding on to a prior connection, close it.
    	if(this.sqlConnection!=null) {
    		try {
				this.sqlConnection.close();
			} catch (SQLException e) {
				// TODO Log error message
				e.printStackTrace();
			}
    		this.sqlConnection = null;
    	}
    	// Set the selected VDB
    	this.selectedVDB = selectedVDB;
		this.executeVdbAction.setSelectedVdbFile(selectedVDB);
		// Setup the VDB for Execution
		this.executeVdbAction.setupVdbForExecution();
		// Update the execution status
		this.vdbExecutionStatus = this.executeVdbAction.getCanExecuteStatus();
	}
	
    /**
     * Get the list of available virtual-relational models from the currently selected VDB.  System
     * models are excluded from the list
     * @return the list of virtual relational models in the VDB
     */
	public List getVirtualRelationalModelsForSelectedVDB() {
		List models = new ArrayList();
		if(canExecuteVdb()) {
			try {
				// Query the system model to get all models in vdb
				Connection conn = getSQLConnection();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(SYSTEM_MODEL_QUERY);
				// Translate ResultSet to list of maps
				List mapList = ResultSetTranslator.translate(rs);
				// Iterate results and add Virtual Relational models to the result list
				Iterator iter = mapList.iterator();
				while(iter.hasNext()) {
					Map rowMap = (Map)iter.next();
					String modelName = (String)rowMap.get(MODEL_NAME);
					Boolean isPhysical = (Boolean)rowMap.get(MODEL_IS_PHYSICAL);
					String metamodelUri = (String)rowMap.get(MODEL_PRIMARY_URI);
					if(RelationalPackage.eNS_URI.equals(metamodelUri) && !isPhysical.booleanValue() && !modelName.equals(SYSTEM_MODEL_NAME)) {
						models.add(modelName);
					}
				}
				rs.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return models;
	}
	    
    /**
     * Get a Connection for the currently selected VDB.  The VDB must be executable, otherwise
     * a null connection is returned.
     * @return the Sql Connection
     */
    public Connection getSQLConnection() {
    	// Get the connection from the action
    	if(this.sqlConnection==null && canExecuteVdb()) {
    		this.sqlConnection = this.executeVdbAction.getVdbConnection();
    	}
    	return this.sqlConnection;
    }

    /**
     * return the VDB canExecute status
     * @return 'true' if the vdb can be executed
     */
    public boolean canExecuteVdb() {
    	boolean canExecute = false;
    	if(this.vdbExecutionStatus!=null && this.vdbExecutionStatus.isOK()) {
    		canExecute = true;
    	}
    	return canExecute;
    }
    
    /**
     * return the VDB execution status
     * @return the vdb execution status
     */
	public IStatus getVdbExecutionStatus() {
		return vdbExecutionStatus;
	}

    /**
     * get the name of the selected Model within the selected VDB.
     * @return the selected model name
     */
	public String getSelectedVDBModel() {
		return selectedVDBModel;
	}

    /**
     * set the name of the model within the vdb to query
     * @param selectedVDBModel the selected VDB model name
     */
	public void setSelectedVDBModel(String selectedVDBModel) {
		this.selectedVDBModel = selectedVDBModel;
	}
	
}
