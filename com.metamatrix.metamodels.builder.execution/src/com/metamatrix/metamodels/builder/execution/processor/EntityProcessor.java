package com.metamatrix.metamodels.builder.execution.processor;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;

import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.metamodels.builder.DebugConstants;
import com.metamatrix.metamodels.builder.MetamodelBuilderPlugin;
import com.metamatrix.metamodels.builder.MetamodelEntityBuilder;
import com.metamatrix.metamodels.builder.MetamodelEntityRecord;
import com.metamatrix.metamodels.builder.execution.MetamodelBuilderConstants;
import com.metamatrix.metamodels.builder.execution.MetamodelBuilderExecutionPlugin;
import com.metamatrix.metamodels.builder.translator.RecordGenerator;
import com.metamatrix.metamodels.builder.translator.ResultSetTranslator;
import com.metamatrix.metamodels.internal.builder.execution.util.MetamodelBuilderUtil;

/** 
 * EntityProcessor - executes queries against the provided schema, using the sqlConnection.
 * Tables in the schema are processed in the order provided in MetamodelBuilderConstants for
 * the type of model.
 */
public class EntityProcessor extends AbstractProcessor implements MetamodelBuilderConstants {
	
	private final MetamodelEntityBuilder entityBuilder;
	private final String[] processingOrder;
    private final MultiStatus status;
    private boolean builderDebugEnabled = false;
    
	// ==================================================================================
    //                        C O N S T R U C T O R S
    // ==================================================================================

	public EntityProcessor(Connection sqlConnection, MetamodelEntityBuilder entityBuilder, 
			               int modelType, String modelAndSchemaName, MultiStatus status) {
		super(sqlConnection,modelAndSchemaName);
        ArgCheck.isNotNull(status);
        ArgCheck.isNotNull(entityBuilder);
        this.status = status;
		this.entityBuilder = entityBuilder;
		if(modelType==RELATIONAL_MODEL) {
			this.processingOrder = RELATIONAL_PROCESSING_ORDER;
		} else if(modelType==EXTENSION_MODEL) {
			this.processingOrder = EXTENSIONS_PROCESSING_ORDER;
		} else {
			this.processingOrder = new String[] {};
		} 
	}
	
	/* (non-Javadoc)
	 * @see com.metamatrix.metamodels.builder.processor.Processor#getRecordCount(org.eclipse.core.runtime.IStatus)
	 */
	public int getRecordCount( ) {
		int totalRows = 0;
		this.builderDebugEnabled = MetamodelBuilderPlugin.Util.isDebugEnabled(DebugConstants.METAMODEL_BUILDER);
		
		if(this.builderDebugEnabled) {
			final String msg = ">>> Counting total records for: " + this.modelAndSchemaName;   //$NON-NLS-1$
	        MetamodelBuilderExecutionPlugin.Util.log(IStatus.INFO, msg);
        }

		//----------------------------------------------
		// Get the available Tables for this schema
		//----------------------------------------------
  		List tableNames = null;
		try {
			tableNames = getSchemaTables( );
		} catch (SQLException e) {
			// Log the exception
			final String msg = "Error retrieving the tables for "+this.modelAndSchemaName;   //$NON-NLS-1$
            MetamodelBuilderUtil.addStatus(status, IStatus.ERROR, msg, e);
    		if(this.builderDebugEnabled) {
    	        MetamodelBuilderExecutionPlugin.Util.log(IStatus.ERROR, msg);
            }
			return totalRows;
		}
		
		//----------------------------------------------
		// Count the table rows
		//----------------------------------------------
		if(tableNames!=null) {
			for(int i=0; i<processingOrder.length; i++) {
				if(tableNames.contains(processingOrder[i])) {
					if(this.builderDebugEnabled) {
						final String msg = "Counting rows for table: "+processingOrder[i];  //$NON-NLS-1$
				        MetamodelBuilderExecutionPlugin.Util.log(IStatus.INFO, msg);
			        }
					int tableRows = countTableRows(processingOrder[i]);
					if(tableRows>0) {
						totalRows += tableRows;
					}
					if(status.getSeverity()==IStatus.ERROR) {
						return totalRows;
					}
				} else {
					if(this.builderDebugEnabled) {
						final String msg = "Skipping Table Row Count: "+processingOrder[i] + ", Not found in model";  //$NON-NLS-1$  //$NON-NLS-2$
				        MetamodelBuilderExecutionPlugin.Util.log(IStatus.INFO, msg);
					}
				}
			}
		}
		
        if(this.builderDebugEnabled) {
	        final String msg = ">>> Finished counting total records for "+this.modelAndSchemaName;  //$NON-NLS-1$
	        MetamodelBuilderExecutionPlugin.Util.log(IStatus.INFO, msg);
        }
		return totalRows;
	}
	
	/* (non-Javadoc)
	 * @see com.metamatrix.metamodels.builder.processor.Processor#process(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public IStatus process(IProgressMonitor monitor) {
		this.builderDebugEnabled = MetamodelBuilderPlugin.Util.isDebugEnabled(DebugConstants.METAMODEL_BUILDER);
		
		if(this.builderDebugEnabled) {
			final String msg = ">>> Started Processing the Tables for: " + this.modelAndSchemaName;   //$NON-NLS-1$
	        MetamodelBuilderExecutionPlugin.Util.log(IStatus.INFO, msg);
        }

		if( monitor != null ) {
			monitor.subTask("Processing Entities for "+this.modelAndSchemaName); //$NON-NLS-1$
    	}
		
		//----------------------------------------------
		// Get the available Tables for this schema
		//----------------------------------------------
  		List tableNames = null;
		try {
			tableNames = getSchemaTables( );
		} catch (SQLException e) {
			// Log the exception
			final String msg = "Error retrieving the tables for "+this.modelAndSchemaName;   //$NON-NLS-1$
            MetamodelBuilderUtil.addStatus(status, IStatus.ERROR, msg, e);
    		if(this.builderDebugEnabled) {
    	        MetamodelBuilderExecutionPlugin.Util.log(IStatus.ERROR, msg);
            }
			return status;
		}
		
		//----------------------------------------------
		// Process Tables in the specified order
		//----------------------------------------------
		if(tableNames!=null) {
			for(int i=0; i<processingOrder.length; i++) {
				if(tableNames.contains(processingOrder[i])) {
					if(this.builderDebugEnabled) {
						final String msg = "Processing Table: "+processingOrder[i];  //$NON-NLS-1$
				        MetamodelBuilderExecutionPlugin.Util.log(IStatus.INFO, msg);
			        }
					processTable(processingOrder[i],monitor);
					if(status.getSeverity()==IStatus.ERROR) {
						return status;
					}
				} else {
					if(this.builderDebugEnabled) {
						final String msg = "Skipping Table: "+processingOrder[i] + ", Not found in model";  //$NON-NLS-1$  //$NON-NLS-2$
				        MetamodelBuilderExecutionPlugin.Util.log(IStatus.INFO, msg);
					}
				}
			}
		}
		
        if( monitor!=null && monitor.isCanceled() ) {
        	final String msg = "Entity Processing Cancelled";   //$NON-NLS-1$
            MetamodelBuilderUtil.addStatus(status, IStatus.CANCEL, msg);
			if(this.builderDebugEnabled) {
		        MetamodelBuilderExecutionPlugin.Util.log(IStatus.CANCEL, msg);
			}
            return status;
        }
        if(this.builderDebugEnabled) {
	        final String msg = ">>> Finished Processing the Tables for "+this.modelAndSchemaName;  //$NON-NLS-1$
	        MetamodelBuilderExecutionPlugin.Util.log(IStatus.INFO, msg);
        }
		return status;
	}
	
	private void processTable(String tableName,IProgressMonitor monitor) {
		if(isValidTable(tableName)) {
			ResultSet rs = null;
			try {
				rs = executeTableQuery(tableName);
				List entityRecords = RecordGenerator.generateEntityRecords(rs,this.status,monitor);
				// Iterate for progress monitor
				Iterator iter = entityRecords.iterator();
				while(iter.hasNext()) {
					this.entityBuilder.create((MetamodelEntityRecord)iter.next(),monitor);
				}
				rs.close();
			} catch (SQLException e) {
				final String msg = "Error Processing Table "+this.modelAndSchemaName+DELIM+tableName;  //$NON-NLS-1$
		        MetamodelBuilderUtil.addStatus(status, IStatus.ERROR, msg, e);
		        if(this.builderDebugEnabled) {
			        MetamodelBuilderExecutionPlugin.Util.log(IStatus.ERROR, msg);
		        }
		        return;
			} 
	        if(this.builderDebugEnabled) {
		        final String msg = "Processing complete for Table "+this.modelAndSchemaName+DELIM+tableName;   //$NON-NLS-1$
		        MetamodelBuilderExecutionPlugin.Util.log(IStatus.INFO, msg);
	        }
	        return;
		}
        final String msg = "Table not found: "+this.modelAndSchemaName+DELIM+tableName;   //$NON-NLS-1$
        MetamodelBuilderUtil.addStatus(status, IStatus.WARNING, msg);
		if(this.builderDebugEnabled) {
	        MetamodelBuilderExecutionPlugin.Util.log(IStatus.WARNING, msg);
        }
	}
	
	private int countTableRows(String tableName) {
		int tableRows = 0;
		if(isValidTable(tableName)) {
			ResultSet rs = null;
			try {
				rs = executeTableQuery(tableName);
				int rsRowCount = ResultSetTranslator.getRowCount(rs);
				if(rsRowCount>0) {
					tableRows += rsRowCount;
				}
				rs.close();
			} catch (SQLException e) {
				final String msg = "Error doing rowCount on table "+this.modelAndSchemaName+DELIM+tableName;  //$NON-NLS-1$
		        MetamodelBuilderUtil.addStatus(status, IStatus.WARNING, msg, e);
		        if(this.builderDebugEnabled) {
			        MetamodelBuilderExecutionPlugin.Util.log(IStatus.WARNING, msg);
		        }
		        return tableRows;
			} 
	        if(this.builderDebugEnabled) {
		        final String msg = "Row count complete for Table "+this.modelAndSchemaName+DELIM+tableName;   //$NON-NLS-1$
		        MetamodelBuilderExecutionPlugin.Util.log(IStatus.INFO, msg);
	        }
	        return tableRows;
		}
        final String msg = "Table not found when counting rows: "+this.modelAndSchemaName+DELIM+tableName;   //$NON-NLS-1$
        MetamodelBuilderUtil.addStatus(status, IStatus.WARNING, msg);
		if(this.builderDebugEnabled) {
	        MetamodelBuilderExecutionPlugin.Util.log(IStatus.WARNING, msg);
        }
		return tableRows;
	}
	
	private boolean isValidTable(String tableName) { 
		boolean isValid = false;
		for(int i=0; i<processingOrder.length; i++) {
			if(processingOrder[i].equalsIgnoreCase(tableName)) {
				isValid=true;
				break;
			}
		}
		return isValid;
	}
	
}
