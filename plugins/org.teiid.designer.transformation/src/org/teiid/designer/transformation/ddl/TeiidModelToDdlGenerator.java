/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.ddl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.teiid.core.designer.ModelerCoreException;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.core.designer.util.ModelType;
import org.teiid.core.designer.util.StringConstants;
import org.teiid.core.designer.util.StringUtilities;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.util.ModelContents;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.extension.ExtensionPlugin;
import org.teiid.designer.extension.ModelExtensionAssistantAggregator;
import org.teiid.designer.extension.definition.ModelObjectExtensionAssistant;
import org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition;
import org.teiid.designer.metamodels.relational.BaseTable;
import org.teiid.designer.metamodels.relational.Column;
import org.teiid.designer.metamodels.relational.DirectionKind;
import org.teiid.designer.metamodels.relational.ForeignKey;
import org.teiid.designer.metamodels.relational.NullableType;
import org.teiid.designer.metamodels.relational.PrimaryKey;
import org.teiid.designer.metamodels.relational.Procedure;
import org.teiid.designer.metamodels.relational.ProcedureParameter;
import org.teiid.designer.metamodels.relational.SearchabilityType;
import org.teiid.designer.metamodels.relational.Table;
import org.teiid.designer.metamodels.relational.UniqueConstraint;
import org.teiid.designer.metamodels.relational.UniqueKey;
import org.teiid.designer.metamodels.relational.extension.RelationalModelExtensionAssistant;
import org.teiid.designer.metamodels.relational.extension.RestModelExtensionAssistant;
import org.teiid.designer.metamodels.relational.extension.RestModelExtensionConstants;
import org.teiid.designer.metamodels.relational.util.RelationalUtil;
import org.teiid.designer.metamodels.transformation.TransformationMappingRoot;
import org.teiid.designer.relational.RelationalConstants;
import org.teiid.designer.transformation.TransformationPlugin;
import org.teiid.designer.transformation.util.TransformationHelper;
import org.teiid.designer.type.IDataTypeManagerService;
import org.teiid.designer.type.IDataTypeManagerService.DataTypeName;
//import org.teiid.query.ui.sqleditor.component.QueryDisplayFormatter;

/**
 * Generator for converting a teiid xmi model into DDL
 */
public class TeiidModelToDdlGenerator implements TeiidDDLConstants, TeiidReservedConstants, RelationalConstants  {
	
	private StringBuilder ddlBuffer = new StringBuilder();

    private boolean includeTables = true;

    private boolean includeProcedures = true;
    
    private boolean isVirtual = false;
    
    private RelationalModelExtensionAssistant assistant;
    
    private List<IStatus> issues = new ArrayList<IStatus>();
    
    private Set<String> namespaces = new HashSet<String>();
    
    private ModelExtensionAssistantAggregator medAggregator = ExtensionPlugin.getInstance().getModelExtensionAssistantAggregator();

	/**
	 * @param modelResource
	 * @return the generated DDL for the given model
	 * @throws ModelWorkspaceException
	 */
	public String generate(ModelResource modelResource) throws ModelWorkspaceException {
	    CoreArgCheck.isNotNull(modelResource);

		final ModelContents contents = ModelContents.getModelContents(modelResource);
		isVirtual = modelResource.getModelType().getValue() == ModelType.VIRTUAL;

		append(StringConstants.NEW_LINE);
		
		for( Object obj : contents.getAllRootEObjects() ) {
			String statement = getStatement((EObject)obj);
			if( ! StringUtilities.isEmpty(statement) ) {
				append(statement);
				append(StringConstants.NEW_LINE);
			}
		}
		if( ! namespaces.isEmpty() ) {
			for( String namespace : namespaces ) {
				ddlBuffer.insert(0, namespace + NEW_LINE);
			}
			ddlBuffer.insert(0, NEW_LINE);
		}
		return ddlBuffer.toString();
	}
	
	private String getStatement(EObject eObj) {
		if( eObj instanceof Table ) {
			if( isVirtual ) {
				// generate DDL for a View including SQL statement
				return view((Table)eObj);
			} else {
				// Generate simple CREATE FOREIGN TABLE
				return table((Table)eObj);
			}
		} else if( eObj instanceof Procedure) {
			// Generate CREATE FOREIGN PROCEDURE 
			return procedure((Procedure)eObj);
		}
		
		return null;
	}
	
	private String getColumnDdl(Column col) {
        
        StringBuilder sb = new StringBuilder();

        sb.append(getName(col));
        sb.append(SPACE);

        String teiidDdlDataType = resolveExportedDataType(col.getType());
        sb.append(getColumnDatatypeDdl(teiidDdlDataType, col.getLength(), col.getPrecision(), col.getScale()));

        String properties = getColumnProperties(col);
        if (! StringUtilities.isEmpty(properties)) sb.append(SPACE).append(properties);

        String options = getColumnOptions(col);
        if( !StringUtilities.isEmpty(options) ) sb.append(SPACE).append(options);
        
		return sb.toString();
	}
	
	private String resolveExportedDataType(EObject dataTypeEObject) {
		String dataTypeName = ModelerCore.getBuiltInTypesManager().getName(dataTypeEObject);
		if( dataTypeName.equalsIgnoreCase(DataTypeName.VARBINARY.name()) ) {
			return dataTypeName;
		}
		
		String runtimeTypeName = ModelerCore.getBuiltInTypesManager().getRuntimeTypeName(dataTypeEObject);
		
		if( runtimeTypeName == null) {
			// Check with 
			runtimeTypeName = ModelerCore.getDatatypeManager().getRuntimeTypeName(dataTypeEObject);
		}
		
		if( runtimeTypeName != null && runtimeTypeName.equalsIgnoreCase("XMLLITERAL")) {
			return DataTypeName.XML.name();
		}
		
		return runtimeTypeName;
	}
	
	private String getColumnDatatypeDdl(String name, int length, int precision, int scale) {
		StringBuilder sb = new StringBuilder();
		sb.append(name);
		
		final boolean isLengthType = ModelerCore.getTeiidDataTypeManagerService().isLengthDataType(name);
		final boolean isPrecisionType = ModelerCore.getTeiidDataTypeManagerService().isPrecisionDataType(name);
		final boolean isScaleType = ModelerCore.getTeiidDataTypeManagerService().isScaleDataType(name);
		
		if( isLengthType ) {
			if( length > 0 ) {
				sb.append(OPEN_BRACKET).append(length).append(CLOSE_BRACKET);
			}
		} else if( isPrecisionType && precision > 0 ) {
			sb.append(OPEN_BRACKET).append(precision);
			if( isScaleType && scale > 0 ) {
				sb.append(COMMA).append(SPACE).append(scale).append(CLOSE_BRACKET);
			} else {
				sb.append(CLOSE_BRACKET);
			}
		}
		return sb.toString();
	}
	
	private String getParameterDatatypeDdl(String name, int length, int precision, int scale) {
		StringBuilder sb = new StringBuilder();
		sb.append(name);
		
		final boolean isLengthType = ModelerCore.getTeiidDataTypeManagerService().isLengthDataType(name);
		final boolean isPrecisionType = ModelerCore.getTeiidDataTypeManagerService().isPrecisionDataType(name);
		final boolean isScaleType = ModelerCore.getTeiidDataTypeManagerService().isScaleDataType(name);
		
		if( isLengthType ) {
			if( length > 0 ) {
				sb.append(OPEN_BRACKET).append(length).append(CLOSE_BRACKET);
			}
		} else if( isPrecisionType && precision > 0 ) {
			sb.append(OPEN_BRACKET).append(precision);
			if( isScaleType && scale > 0 ) {
				sb.append(COMMA).append(SPACE).append(scale).append(CLOSE_BRACKET);
			} else {
				sb.append(CLOSE_BRACKET);
			}
		}
		return sb.toString();
	}
	
	private String getParameterDdl(ProcedureParameter param) {
        
        StringBuilder sb = new StringBuilder();
        sb.append(getName(param));
        sb.append(SPACE);
        String teiidDdlDataType = resolveExportedDataType(param.getType());
        sb.append(getParameterDatatypeDdl(teiidDdlDataType, param.getLength(), param.getPrecision(), param.getScale()));
        
		return sb.toString();
	}
	
	private String getName(EObject eObj) {
		return ModelerCore.getModelEditor().getName(eObj);
	}
	
	private String getDescription(EObject eObj) {
    	try {
			return ModelerCore.getModelEditor().getDescription(eObj);
		} catch (ModelerCoreException e) {
			issues.add(new Status(IStatus.ERROR, TransformationPlugin.PLUGIN_ID, "Error finding description for " + getName(eObj), e)); //$NON-NLS-1$
		}
    	
    	return null;
	}
	
    private void append(Object o) {
        ddlBuffer.append(o);
    }
	
    private String table(Table table) {
        if (! includeTables)
            return null;
        
        StringBuilder sb = new StringBuilder();

		// generate DDL for a Table
        sb.append(CREATE_FOREIGN_TABLE).append(SPACE);
        sb.append(getName(table));
        sb.append(SPACE + OPEN_BRACKET);
		@SuppressWarnings("unchecked")
		List<Column> columns = table.getColumns();
		int nColumns = columns.size();
		int count = 0;
		for( Column col : columns ) {
			if( count == 0 ) sb.append(NEW_LINE);
			
			String columnStr = getColumnDdl(col);
			count++;
			sb.append(TAB).append(columnStr);
			
			if( count < nColumns ) sb.append(COMMA + NEW_LINE);
		}
	
		// Add PK/FK/UC's
		if( table instanceof BaseTable) {
			String constraints = getContraints((BaseTable)table);
			if( constraints != null ) {
				sb.append(constraints);
			}
		}

		sb.append(NEW_LINE + CLOSE_BRACKET);
		
		String options = getTableOptions(table);
		if( !StringUtilities.isEmpty(options)) {
			sb.append(SPACE).append(options);
		}
		
		sb.append(NEW_LINE);

		return sb.toString();
    }
    
    private String view(Table table) {
        if (! includeTables)
            return null;

        StringBuilder sb = new StringBuilder();

		// generate DDL for a View including SQL statement
		sb.append(CREATE_VIEW).append(SPACE);
        sb.append(getName(table));
        sb.append(SPACE + OPEN_BRACKET);
		@SuppressWarnings("unchecked")
		List<Column> columns = table.getColumns();
		int nColumns = columns.size();
		int count = 0;
		for( Column col : columns ) {
			if( count == 0 ) sb.append(NEW_LINE);
			
			String columnStr = getColumnDdl(col);
			count++;
			sb.append(TAB).append(columnStr);
			
			if( count < nColumns ) sb.append(COMMA + NEW_LINE);

		}
		
		// Add PK/FK/UC's
		if( table instanceof BaseTable) {
			String constraints = getContraints((BaseTable)table);
			if( constraints != null ) {
				sb.append(constraints);
			}
		}
		
		sb.append(NEW_LINE + CLOSE_BRACKET);
		
		String options = getTableOptions(table);
		if( !StringUtilities.isEmpty(options)) {
			sb.append(SPACE).append(options);
		}
		TransformationMappingRoot tRoot = (TransformationMappingRoot)TransformationHelper.getTransformationMappingRoot(table);
		String sqlString = TransformationHelper.getSelectSqlString(tRoot);
		if( sqlString != null ) {
//			QueryDisplayFormatter formatter = new QueryDisplayFormatter(sqlString);
//			String formatedSQL = formatter.getFormattedSql();
//			sb.append(SPACE).append(NEW_LINE + Reserved.AS).append(NEW_LINE + TAB).append(formatedSQL);
			sb.append(SPACE).append(NEW_LINE + Reserved.AS).append(NEW_LINE + TAB).append(sqlString);
			sb.append(SEMI_COLON + NEW_LINE);
		}
		
		return sb.toString();
    }
    
    /*
     * 
	 *   Source Procedure ("CREATE FOREIGN PROCEDURE") - a stored procedure in source
	 *   Source Function ("CREATE FOREIGN FUNCTION") - A function that is supported by the source, where Teiid will pushdown to source instead of evaluating in Teiid engine
	 *   Virtual Procedure ("CREATE VIRTUAL PROCEDURE") - Similar to stored procedure, however this is defined using the Teiid's Procedure language and evaluated in the Teiid's engine.
	 *   Function/UDF ("CREATE VIRTUAL FUNCTION") - A user defined function, that can be defined using the Teiid procedure language or can have the implementation defined using a JAVA Class.
     */
    private String procedure(Procedure procedure) {
        if (! includeProcedures)
            return null;
        
        StringBuilder sb = new StringBuilder();
        boolean isFunction = procedure.isFunction();

		// generate DDL for a Table
        if( isFunction ) {
        	if( isVirtual ) sb.append(CREATE_VIRTUAL_FUNCTION).append(SPACE);
        	else sb.append(CREATE_FOREIGN_FUNCTION).append(SPACE);
        } else {
        	if( isVirtual ) sb.append(CREATE_VIRTUAL_PROCEDURE).append(SPACE);
        	else sb.append(CREATE_FOREIGN_PROCEDURE).append(SPACE);
        }
        sb.append(getName(procedure));
        sb.append(SPACE + OPEN_BRACKET);
		@SuppressWarnings("unchecked")
		List<ProcedureParameter> params = procedure.getParameters();
		int nParams = params.size();
		int count = 0;
		
		// Check for an "RETURN" parameter direction and cache it's datatype
		String returnType = null;
		
		for( ProcedureParameter param : params ) {
			if( param.getDirection() == DirectionKind.RETURN_LITERAL) {
				returnType = resolveExportedDataType(param.getType());
				break;
			}
		}
		
		if( returnType != null ) {
			nParams = nParams-1;
		}
		
		for( ProcedureParameter param : params ) {
			if( param.getDirection() == DirectionKind.RETURN_LITERAL) {
				continue;
			}
			String paramStr = getParameterDdl(param);
			count++;
			sb.append(paramStr);
			
			addOptionsForEObject(param, sb);
			
			if( count < nParams ) sb.append(COMMA + SPACE);
		}
		
		sb.append(CLOSE_BRACKET);
	
		// Add the RETURNS clause to handle the result set
		// CREATE VIRTUAL PROCEDURE testProc (p1 string(4000)) RETURNS TABLE ( xml_out xml)
		// CREATE VIRTUAL PROCEDURE getTweets(query varchar) RETURNS (created_on varchar(25), from_user varchar(25), to_user varchar(25))
		// CREATE FOREIGN PROCEDURE func (x integer, y integer) returns table (z integer);
		// CREATE FOREIGN PROCEDURE func (x integer, y integer) returns integer;
	    // CREATE VIRTUAL FUNCTION celsiusToFahrenheit(celsius decimal) RETURNS decimal OPTIONS (JAVA_CLASS 'org.something.TempConv',  JAVA_METHOD 'celsiusToFahrenheit');
	    // CREATE VIRTUAL FUNCTION sumAll(arg integer) RETURNS integer OPTIONS (JAVA_CLASS 'org.something.SumAll',  JAVA_METHOD 'addInput', AGGREGATE 'true', VARARGS 'true', "NULL-ON-NULL" 'true');
		
		if( procedure.getResult() != null ) {
			sb.append(SPACE + RETURNS + SPACE + TABLE + SPACE);
			sb.append(OPEN_BRACKET);
			count = 0;
			int nCols = procedure.getResult().getColumns().size();
			for( Object col : procedure.getResult().getColumns() ) {
				Column nextCol = (Column)col;
				count++;
				String columnStr = getColumnDdl(nextCol);
				sb.append(columnStr);
				if( count < nCols ) sb.append(COMMA + SPACE);
				
				addOptionsForEObject(nextCol, sb);
			}
			sb.append(CLOSE_BRACKET);
			addOptionsForEObject(procedure.getResult(), sb);
		} else if( returnType != null ) {
			sb.append(SPACE + RETURNS + SPACE + returnType);
		}
		
		String options = getProcedureOptions(procedure);
		if( !StringUtilities.isEmpty(options)) {
			sb.append(NEW_LINE);
			sb.append(SPACE).append(options);
		}
//		else {
//			sb.append(CLOSE_BRACKET);
//		}
		
		// Depending on the procedure type, need to append either one of the following:
		//   > returns datatype
		//   > returns a result set, either named or not
		//   > an AS <SQL STATEMENT> if a virtual procedure
		//   > ???
		if( isVirtual && !isFunction ) {
			TransformationMappingRoot tRoot = (TransformationMappingRoot)TransformationHelper.getTransformationMappingRoot(procedure);
			String sqlString = TransformationHelper.getSelectSqlString(tRoot).replace(CREATE_VIRTUAL_PROCEDURE, StringConstants.EMPTY_STRING);
			
			if( sqlString != null ) {
				if( sqlString.indexOf('\n') == 0 ) {
					sqlString = sqlString.replace(StringConstants.NEW_LINE, StringConstants.EMPTY_STRING);
				}
				sb.append(NEW_LINE + TAB).append(Reserved.AS).append(NEW_LINE).append(sqlString);
				if( ! sqlString.endsWith(SEMI_COLON)) sb.append(SEMI_COLON);
				sb.append(NEW_LINE);
			}
		} else {
			sb.append(NEW_LINE);
		}

		return sb.toString();
    }

    private String getColumnProperties(Column col) {
        StringBuffer sb = new StringBuffer();

        //
        // NULLABLE / NOT NULL
        //
        NullableType nullableType = col.getNullable();
        if (nullableType.equals(NullableType.NO_NULLS_LITERAL))
            sb.append(NOT_NULL).append(SPACE);

        //
        // DEFAULT
        //
        String defaultValue = col.getDefaultValue();
        if (!StringUtilities.isEmpty(defaultValue)) {
            sb.append(TeiidSQLConstants.Reserved.DEFAULT).append(SPACE).append(defaultValue).append(SPACE);
        }

        //
        // AUTO_INCREMENT
        //
        boolean autoIncremented = col.isAutoIncremented();
        if (autoIncremented)
            sb.append(AUTO_INCREMENT).append(SPACE);

        if (col.getOwner() instanceof BaseTable) {
            BaseTable table = (BaseTable) col.getOwner();

//            //
//            // PRIMARY KEY
//            //
//            PrimaryKey key = table.getPrimaryKey();
//            if (key != null) {
//                @SuppressWarnings("rawtypes")
//				EList columns = key.getColumns();
//                if (columns != null && columns.contains(col))
//                    sb.append(PRIMARY_KEY).append(SPACE);
//            }
//
//            //
//            // UNIQUE
//            //
//            EList<UniqueConstraint> uniqueConstraints = table.getUniqueConstraints();
//            if (uniqueConstraints != null && ! uniqueConstraints.isEmpty()) {
//                for (UniqueConstraint uc : uniqueConstraints) {
//                    if (uc.getColumns().contains(col)) {
//                        sb.append(TeiidSQLConstants.Reserved.UNIQUE).append(SPACE);
//                        break; // Don't care if column is in more than 1 unique constraint
//                    }
//                }
//            }

            //
            // INDEX
            //
            if (! col.getIndexes().isEmpty())
                sb.append(TeiidSQLConstants.NonReserved.INDEX).append(SPACE);
        }

        return sb.toString().trim();
    }

    private String getColumnOptions(Column col) {
    	OptionsStatement options = new OptionsStatement();
    	options.add(NAMEINSOURCE, col.getNameInSource(), null);
    	options.add(NATIVE_TYPE, col.getNativeType(), null);
    	options.add(CASE_SENSITIVE, Boolean.toString(col.isCaseSensitive()), Boolean.TRUE.toString());
    	options.add(SELECTABLE, Boolean.toString(col.isSelectable()), Boolean.TRUE.toString());
    	options.add(UPDATABLE, Boolean.toString(col.isUpdateable()), Boolean.TRUE.toString());
    	options.add(SIGNED, Boolean.toString(col.isSigned()), Boolean.TRUE.toString());
    	options.add(CURRENCY, Boolean.toString(col.isCurrency()), Boolean.FALSE.toString());
    	options.add(FIXED_LENGTH, Boolean.toString(col.isFixedLength()), Boolean.FALSE.toString());
    	String desc = getDescription(col);
    	if( !StringUtilities.isEmpty(desc) ) {
    		options.add(ANNOTATION, desc, EMPTY_STRING);
    	}
    	if( !col.getSearchability().equals(SearchabilityType.SEARCHABLE) ) {
    		options.add(SEARCHABLE, col.getSearchability().getLiteral(), SearchabilityType.SEARCHABLE_LITERAL.toString());
    	}
    	
    	// Need to check with other assistants too
    	try {
			Map<String, String> props = getOptionsForObject(col);
			for( String key : props.keySet() ) {
				String value = props.get(key);
				options.add(key, value, null);
			}
		} catch (Exception e) {
			issues.add(new Status(IStatus.ERROR, TransformationPlugin.PLUGIN_ID, "Error finding options for " + getName(col), e)); //$NON-NLS-1$
		}

    	return options.toString();
    }
    
    private String getContraints(BaseTable table) {
    	StringBuffer sb = new StringBuffer();
    	
		boolean hasPK = table.getPrimaryKey() != null;
		boolean hasFKs = table.getForeignKeys().size() > 0;
		
		int nColumns = 0;
		int count = 0;

		Collection<UniqueConstraint> uniqueConstraints = getUniqueUniqueContraints(table);
		boolean hasUCs = uniqueConstraints.size() > 0;
		
		if( hasPK ) {
			PrimaryKey pk = table.getPrimaryKey();
			// CONSTRAINT PK_ACCOUNTHOLDINGS PRIMARY KEY(TRANID),
			String pkName = getName(pk);
			sb.append(COMMA);
			StringBuilder theSB = new StringBuilder(NEW_LINE + TAB + CONSTRAINT + SPACE + pkName + SPACE + PRIMARY_KEY);
			nColumns = pk.getColumns().size();
			count = 0;
			for( Object col : pk.getColumns() ) {
				count++;
				if( count == 1 ) theSB.append(OPEN_BRACKET);
				theSB.append(getName((EObject)col));
				if( count < nColumns ) theSB.append(COMMA + SPACE);
				else theSB.append(CLOSE_BRACKET);
			}
			sb.append(theSB.toString());
			
			if( hasFKs || hasUCs ) sb.append(COMMA);
		}
		
		// FK
		// CONSTRAINT CUSTOMER_ACCOUNT_FK FOREIGN KEY(CUSTID) REFERENCES ACCOUNT (CUSTID)
		if( hasFKs ) {
			int nFKs = table.getForeignKeys().size();
			int countFK = 0;
			for( Object obj : table.getForeignKeys()) {
				countFK++;
				ForeignKey fk = (ForeignKey)obj;
				String fkName = getName(fk);
				StringBuilder theSB = new StringBuilder(NEW_LINE + TAB + CONSTRAINT + SPACE + fkName + SPACE + FOREIGN_KEY);
				nColumns = fk.getColumns().size();
				count = 0;
				for( Object col : fk.getColumns() ) {
					count++;
					if( count == 1 ) theSB.append(OPEN_BRACKET);
					theSB.append(getName((EObject)col));
					if( count < nColumns ) theSB.append(COMMA + SPACE);
					else theSB.append(CLOSE_BRACKET);
				}
				// REFERENCES
				if( fk.getTable() != null ) {
					UniqueKey uk = fk.getUniqueKey();
					BaseTable fkTableRef = (BaseTable)uk.getTable();
					
					String fkTableRefName = getName(fkTableRef);
					theSB.append(SPACE).append(REFERENCES).append(SPACE).append(fkTableRefName);
					if( uk instanceof UniqueConstraint ) {
						// Unique Constraint
						UniqueConstraint ucRef = fkTableRef.getUniqueConstraints().get(0);
						nColumns = ucRef.getColumns().size();
						count = 0;
						for( Object col : ucRef.getColumns() ) {
							count++;
							if( count == 1 ) theSB.append(OPEN_BRACKET);
							theSB.append(getName((EObject)col));
							if( count < nColumns ) theSB.append(COMMA + SPACE);
							else theSB.append(CLOSE_BRACKET);
						}
						// TODO: Not sure how to handle the case where there are multiple UC's.
						
					} else { 
						// Primary Key
						PrimaryKey pkRef = fkTableRef.getPrimaryKey();
						nColumns = pkRef.getColumns().size();
						count = 0;
						for( Object col : pkRef.getColumns() ) {
							count++;
							if( count == 1 ) theSB.append(OPEN_BRACKET);
							theSB.append(getName((EObject)col));
							if( count < nColumns ) theSB.append(COMMA + SPACE);
							else theSB.append(CLOSE_BRACKET);
						}
					}

				}
				
				sb.append(theSB.toString());
				if( countFK < nFKs ) sb.append(COMMA);
			}
			if( hasUCs ) sb.append(COMMA);
		}
		// UC's
		// CONSTRAINT PK_ACCOUNTHOLDINGS UNIQUE(TRANID)
		if( hasUCs ) {
			int nUCs = uniqueConstraints.size();
			int ucCount = 0;
			for( Object obj: uniqueConstraints ) {
				ucCount++;
				UniqueConstraint uc = (UniqueConstraint)obj;
				String name = getName(uc);

				StringBuilder theSB = new StringBuilder(NEW_LINE + TAB + CONSTRAINT + SPACE + name + SPACE + UNIQUE);
				nColumns = uc.getColumns().size();
				count = 0;
				for( Object col : uc.getColumns() ) {
					count++;
					if( count == 1 ) theSB.append(OPEN_BRACKET);
					theSB.append(getName((EObject)col));
					if( count < nColumns ) theSB.append(COMMA + SPACE);
					else theSB.append(CLOSE_BRACKET);
				}
				if( ucCount < nUCs ) sb.append(COMMA);
				sb.append(theSB.toString());
			}
		}
		return sb.toString();
    }
    
    private String getTableOptions(Table table) {
    	OptionsStatement options = new OptionsStatement();

    	options.add(NAMEINSOURCE, table.getNameInSource(), null);
    	options.add(MATERIALIZED, Boolean.toString(table.isMaterialized()), Boolean.FALSE.toString());
    	options.add(UPDATABLE, Boolean.toString(table.isSupportsUpdate()), Boolean.TRUE.toString());
    	if( table.getCardinality() > 0 ) {
    		options.add(CARDINALITY, Integer.toString(table.getCardinality()), Integer.toString(0));
    	}
    	if( table.getMaterializedTable() != null ) {
    		options.add(MATERIALIZED_TABLE, table.getMaterializedTable().getName(), null);
    	}
    	
    	// Need to check with other assistants too
    	try {
			Map<String, String> props = getOptionsForObject(table);
			for( String key : props.keySet() ) {
				String value = props.get(key);
				options.add(key, value, null);
			}
		} catch (Exception e) {
			issues.add(new Status(IStatus.ERROR, TransformationPlugin.PLUGIN_ID, "Error finding options for " + getName(table), e)); //$NON-NLS-1$
		}
    	
    	String desc = getDescription(table);
    	if( !StringUtilities.isEmpty(desc) ) {
    		options.add(ANNOTATION, desc, null);
    	}

    	return options.toString();
    }
    
    private Map<String, String> getOptionsForObject(EObject modelObject) throws Exception {
    	Map<String, String> options = new HashMap<String, String>();

    	Collection<String> extensionNamespaces = medAggregator.getSupportedNamespacePrefixes(modelObject);
    	for( String ns : extensionNamespaces ) {
    		ModelObjectExtensionAssistant assistant = medAggregator.getModelObjectExtensionAssistant(ns);
    		if( assistant != null ) {
    			Collection<ModelExtensionPropertyDefinition> defns = assistant.getPropertyDefinitions(modelObject);

    			// If relational, we're handling this via getPropetyValue()...
    			if(ns.equals(RELATIONAL_PREFIX)) {
    				String propId = BASE_TABLE_EXT_PROPERTIES.NATIVE_QUERY;
    				String nativeQuery = assistant.getOverriddenValue(modelObject, propId);
    				if(!CoreStringUtil.isEmpty(nativeQuery)) {
    					propId = propId.replace(RELATIONAL_PREFIX, TEIID_REL_PREFIX);
    					options.put(propId, nativeQuery);
    				}
    				propId = BASE_TABLE_EXT_PROPERTIES.VIEW_TABLE_GLOBAL_TEMP_TABLE;
    				String globalTempTable = assistant.getOverriddenValue(modelObject, propId);
    				if(!CoreStringUtil.isEmpty(globalTempTable)) {
    					propId = propId.replace(RELATIONAL_PREFIX, TEIID_REL_PREFIX);
    					options.put(propId, globalTempTable);
    				}
    			} else if(ns.equals(SALESFORCE_PREFIX) )  {
        			for( ModelExtensionPropertyDefinition ext : defns) {
        				String propId = ext.getId();
        				String value = assistant.getOverriddenValue(modelObject, propId);

        				if( value != null ) {
        					propId = propId.replace(SALESFORCE_PREFIX, TEIID_SF_PREFIX);
        					options.put(propId, value);
        				}
        			}
    			} else if(ns.equals(MONGODB_PREFIX)) {
        			for( ModelExtensionPropertyDefinition ext : defns) {
        				String propId = ext.getId();
        				String value = assistant.getOverriddenValue(modelObject, propId);

        				if( value != null ) {
        					propId = propId.replace(MONGODB_PREFIX, TEIID_MONGO_PREFIX);
        					options.put(propId, value);
        				}
        			}
    			} else if(ns.equals(EXCEL_PREFIX)) {
        			for( ModelExtensionPropertyDefinition ext : defns) {
        				String propId = ext.getId();
        				String value = assistant.getOverriddenValue(modelObject, propId);

        				if( value != null ) {
        					propId = propId.replace(EXCEL_PREFIX, TEIID_EXCEL_PREFIX);
        					options.put(propId, value);
        				}
        			}
    			}

    		}
    	}
    	
    	return options;
    	
    }
    
    private void addOptionsForEObject(EObject eObj, StringBuilder sb) {
    	// Need to check with other assistants too
    	try {
    		OptionsStatement options = new OptionsStatement();
			Map<String, String> props = getOptionsForObject(eObj);
			for( String key : props.keySet() ) {
				String value = props.get(key);
				options.add(key, value, null);
			}
			if( !StringUtilities.isEmpty(options.toString())) {
				sb.append(SPACE).append(options);
			}
		} catch (Exception e) {
			issues.add(new Status(IStatus.ERROR, TransformationPlugin.PLUGIN_ID, "Error finding options for " + getName(eObj), e)); //$NON-NLS-1$
		}
    }
    
    private String getProcedureOptions(Procedure procedure) {
    	OptionsStatement options = new OptionsStatement();
    	
    	options.add(NAMEINSOURCE, procedure.getNameInSource(), null);
    	
		String nativeQuery = getPropertyValue(procedure, PROCEDURE_EXT_PROPERTIES.NATIVE_QUERY);
		if(!CoreStringUtil.isEmpty(nativeQuery)) {
			options.add(NATIVE_QUERY_PROP, nativeQuery, null);
		}
		
		// Physical Model only
		if( !isVirtual ) {
			String nonPreparedValue =  getPropertyValue(procedure, PROCEDURE_EXT_PROPERTIES.NON_PREPARED);
			setBooleanProperty(NON_PREPARED_PROP, nonPreparedValue, false, options);
		}
		// Functions have many additional extension properties
		boolean isFunction = procedure.isFunction();
		if(isFunction) {
			String value =  getPropertyValue(procedure, PROCEDURE_EXT_PROPERTIES.FUNCTION_CATEGORY);
			options.add(FUNCTION_CATEGORY_PROP, value, null);
			
			value =  getPropertyValue(procedure, PROCEDURE_EXT_PROPERTIES.JAVA_CLASS);
			options.add(JAVA_CLASS, value, null);
			
			value =  getPropertyValue(procedure, PROCEDURE_EXT_PROPERTIES.JAVA_METHOD);
			options.add(JAVA_METHOD, value, null);

			value =  getPropertyValue(procedure, PROCEDURE_EXT_PROPERTIES.VARARGS);
			setBooleanProperty(VARARGS_PROP, value, false, options);

			
			value =  getPropertyValue(procedure, PROCEDURE_EXT_PROPERTIES.NULL_ON_NULL);
			setBooleanProperty(NULL_ON_NULL_PROP, value, false, options);

			
			value =  getPropertyValue(procedure, PROCEDURE_EXT_PROPERTIES.DETERMINISTIC);
			setBooleanProperty(DETERMINISM_PROP, value, false, options);

			value =  getPropertyValue(procedure, PROCEDURE_EXT_PROPERTIES.AGGREGATE);
			if( value != null ) {
				boolean booleanValue = Boolean.getBoolean(value);
				if( booleanValue ) {
					setBooleanProperty(AGGREGATE_PROP, value, false, options);
					
					value =  getPropertyValue(procedure, PROCEDURE_EXT_PROPERTIES.ANALYTIC);
					setBooleanProperty(ANALYTIC_PROP, value, false, options);
					
					value =  getPropertyValue(procedure, PROCEDURE_EXT_PROPERTIES.ALLOWS_ORDER_BY);
					setBooleanProperty(ALLOWS_ORDER_BY_PROP, value, false, options);
					
					value =  getPropertyValue(procedure, PROCEDURE_EXT_PROPERTIES.USES_DISTINCT_ROWS);
					setBooleanProperty(USES_DISTINCT_ROWS_PROP, value, false, options);
					
					value =  getPropertyValue(procedure, PROCEDURE_EXT_PROPERTIES.ALLOWS_DISTINCT);
					setBooleanProperty(ALLOWS_DISTINCT_PROP, value, false, options);
					
					value =  getPropertyValue(procedure, PROCEDURE_EXT_PROPERTIES.DECOMPOSABLE);
					setBooleanProperty(DECOMPOSABLE_PROP, value, false, options);
				}
			}
		} else {
			// REST PROPERTIES??
			String value =  getRestPropertyValue(procedure, RestModelExtensionConstants.PropertyIds.URI);
			if( value != null ) namespaces.add(REST_TEIID_SET_NAMESPACE);
			
			options.add(REST_URI, value, null);
			
			value =  getRestPropertyValue(procedure, RestModelExtensionConstants.PropertyIds.REST_METHOD);
			if( value != null ) namespaces.add(REST_TEIID_SET_NAMESPACE);
			
			options.add(REST_METHOD, value, null);
		}
		
    	// Need to check with other assistants too
    	try {
			Map<String, String> props = getOptionsForObject(procedure);
			for( String key : props.keySet() ) {
				String value = props.get(key);
				options.add(key, value, null);
			}
		} catch (Exception e) {
			issues.add(new Status(IStatus.ERROR, TransformationPlugin.PLUGIN_ID, "Error finding options for " + getName(procedure), e)); //$NON-NLS-1$
		}
		
		return options.toString();
    }
    
    private RelationalModelExtensionAssistant getRelationalModelExtensionAssistant() {
    	if( assistant == null ) {
    		assistant = RelationalUtil.getRelationalExtensionAssistant();
    	}
    	
    	return assistant;
    }
    
    private String getPropertyValue(EObject eObj, String propertyID ) {
    	
    	try {
			return getRelationalModelExtensionAssistant().getPropertyValue(eObj, propertyID);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	return null;
    }
    
    private String getRestPropertyValue(EObject eObj, String propertyID ) {
    	
    	try {
			return RestModelExtensionAssistant.getRestProperty(eObj, propertyID);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	return null;
    }
    
    private void setBooleanProperty(String propID, String stringValue, boolean defaultValue, OptionsStatement options) {
		if( stringValue != null ) {
			boolean booleanValue = Boolean.parseBoolean(stringValue);
			options.add(propID, String.valueOf(booleanValue), String.valueOf(defaultValue));
		}
    }

    private String escapeStringValue(String str, String tick) {
        return StringUtilities.replaceAll(str, tick, tick + tick);
    }
    
    private String escapeSinglePart(String token) {
        if (TeiidSQLConstants.isReservedWord(token)) {
            return TeiidSQLConstants.Tokens.ID_ESCAPE_CHAR + token + TeiidSQLConstants.Tokens.ID_ESCAPE_CHAR;
        }
        boolean escape = true;
        char start = token.charAt(0);
        if (HASH.equals(Character.toString(start)) || AMPERSAND.equals(Character.toString(start)) || StringUtilities.isLetter(start)) {
            escape = false;
            for (int i = 1; !escape && i < token.length(); i++) {
                char c = token.charAt(i);
                escape = !StringUtilities.isLetterOrDigit(c) && c != '_';
            }
        }
        if (escape) {
            return TeiidSQLConstants.Tokens.ID_ESCAPE_CHAR + escapeStringValue(token, SPEECH_MARK) + TeiidSQLConstants.Tokens.ID_ESCAPE_CHAR;
        }
        return token;
    }
    
    /*
     * Utility to check a unique constraint and determine if it is redundant. Basically if the uc columns match a PK with the same columns
     */
    private Collection<UniqueConstraint> getUniqueUniqueContraints(BaseTable table) {
    	EList<?> ucs = table.getUniqueConstraints();
    	Collection<UniqueConstraint> uniqueConstraints = new ArrayList<UniqueConstraint>();
    	
    	PrimaryKey pk = table.getPrimaryKey();
    	
    	for( Object obj: ucs) {
    		UniqueConstraint uc = (UniqueConstraint)obj;
    		if( pk != null ) {
	    		EList<?> pkColumns = pk.getColumns();
	    		EList<?> ucColumns = uc.getColumns();
	    		
	    		if( pkColumns.size() == ucColumns.size() ) {
	    			boolean matchesAll = true;
	    			for( Object col : ucColumns ) {
	    				if( ! pkColumns.contains(col) ) {
	    					matchesAll = false;
	    				}
	    			}
	    			if( !matchesAll )
	    				uniqueConstraints.add(uc);
	    		} else {
	    			uniqueConstraints.add(uc);
	    		}
    		} else {
    			uniqueConstraints.add(uc);
    		}
    	}
    	
    	return uniqueConstraints;
    }
    
    class OptionsStatement {
    	boolean hasOptions;
    	StringBuilder sb;
    	
    	public OptionsStatement() {
    		super();
    		sb = new StringBuilder();
    		sb.append(Reserved.OPTIONS).append(OPEN_BRACKET);
    	}
    	
    	public void add(String key, String value, String defaultValue) {
    		if( StringUtilities.isEmpty(value) ) return;

    		if(! StringUtilities.areDifferent(value, defaultValue)) return;

    		if( hasOptions ) sb.append(COMMA + SPACE);
    		
    		hasOptions = true;
    		
            sb.append(escapeSinglePart(key)).append(SPACE);
            if (Reserved.FALSE.equalsIgnoreCase(value) || Reserved.TRUE.equalsIgnoreCase(value)) {
                sb.append(QUOTE_MARK + value.toUpperCase() + QUOTE_MARK);
                return;
            }

            // Default to a string value which should be placed in quotes
            sb.append(QUOTE_MARK + value + QUOTE_MARK);
    		
//    		sb.append(key).append(SPACE).append(value);

    	}
    	
    	@Override
        public String toString() {
    		sb.append(CLOSE_BRACKET);
    		
    		if( !hasOptions) return null;
    		
    		return sb.toString();
    	}
    }
    
    /*
     * Need a utility class and methods to determine the procedure type so we can construct the appropriate  DDL
     * TODO: finish this method and implement in the procedure(Procedure) method
     */
    class ProcedureHandler {
    	Procedure proc;
    	// FOREIGN PROCEDURE can have a result set or an out parameter
    	// CREATE FOREIGN PROCEDURE func (x integer, y IN integer) returns table (z integer);
    	// CREATE FOREIGN PROCEDURE func (x integer, y IN integer) returns integer;
    	
    	// CREATE FOREIGN FUNCTION func (x integer, y integer) returns boolean OPTIONS ("teiid_rel:native-query"'$1 << $2');
    	
    	// CREATE VIRTUAL FUNCTION sumAll(arg integer) RETURNS integer OPTIONS (JAVA_CLASS 'org.something.SumAll',  JAVA_METHOD 'addInput', AGGREGATE 'true', VARARGS 'true', "NULL-ON-NULL" 'true');
    	
    	// CREATE VIRTUAL PROCEDURE getTweets(query varchar) 
    	//			RETURNS (created_on varchar(25), from_user varchar(25), to_user varchar(25), profile_image_url varchar(25), source varchar(25), text varchar(140))
    	//		AS
    	//	 SELECT * FROM twitterFeedSummary;


    	public ProcedureHandler(Procedure procedure) {
    		this.proc = procedure;
    		
    		
    	}
    	
    	
    }
}
