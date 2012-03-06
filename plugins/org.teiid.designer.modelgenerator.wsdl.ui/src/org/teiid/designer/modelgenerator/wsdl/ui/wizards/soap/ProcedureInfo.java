/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.modelgenerator.wsdl.ui.wizards.soap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.metamatrix.core.util.StringUtilities;
import com.metamatrix.modeler.internal.transformation.util.SqlConstants;
import com.metamatrix.modeler.modelgenerator.wsdl.model.Operation;


public abstract class ProcedureInfo implements SqlConstants {

    public static final String SQL_BEGIN = "CREATE VIRTUAL PROCEDURE\nBEGIN\n"; //$NON-NLS-1$
    public static final String SQL_END = "\nEND"; //$NON-NLS-1$
    public static final String XSI_NAMESPACE_PREFIX = "xsi"; //$NON-NLS-1$
    
    public static final int REQUEST = 0;
    public static final int RESPONSE = 1;
    
//	CREATE VIRTUAL PROCEDURE
//	BEGIN
//	    SELECT XMLELEMENT(NAME getPrice, XMLNAMESPACES(DEFAULT 'http://quickstart.samples/xsd'), XMLELEMENT(NAME symbol, StockQuoteServiceXML.getPrice.create_getPrice.symbol)) AS xml_out;
//	END
	private Collection<ColumnInfo> columnInfoList;
	
	private Map<String, String> namespaceMap;
	
	private Operation operation;
	
	private ProcedureGenerator generator;
	
	/**
     * The unique procedureName defining a procedure containing the generated SELECT SQL statement
     */
	private String procedureName;
	
    /**
     * An initial xquery root path expression
     * 
     * XMLTABLE([<NSP>,] xquery-expression [<PASSING>] [COLUMNS <COLUMN>, ... )] AS name
     * 
     * Usually of the form '$d/MedlineCitationSet/MedlineCitation'. In this case, the expression defines the initial path
     * inside the XML structure that the COLUMN PATH's are relative to
     */
	private String rootPath = StringUtilities.EMPTY_STRING;
	
	private int type;
	
	/**
	 * Current <code>IStatus</code> representing the state of the input values for this instance of
	 * <code>ProcedureInfo</code>
	 */
	private IStatus status;

	public ProcedureInfo(Operation operation, int type, ProcedureGenerator generator) {
		super();
		this.operation = operation;
		this.columnInfoList = new ArrayList<ColumnInfo>();
		this.namespaceMap = new HashMap<String, String>();
		this.type = type;
		this.generator = generator;
	}
	
	protected ProcedureGenerator getGenerator() {
		return this.generator;
	}
	
	public void addNamespace(String key, String value) {
		this.namespaceMap.put(key, value);
	}
	
	public void addNamespaces(Map<String, String> namespaces) {
		this.namespaceMap.putAll(namespaces);
	}
	
	public Map<String, String> getNamespaceMap() {
		return this.namespaceMap;
	}
	
	public int getType() {
		return this.type;
	}
	
	/**
	 * 
	 * @return columnInfoList the <code>TeiidColumnInfo[]</code> array parsed from the header in the data file
	 */
	public ColumnInfo[] getColumnInfoList() {
		return this.columnInfoList.toArray(new ColumnInfo[this.columnInfoList.size()]);
	}
	
	public void addColumn(String name, boolean ordinality, String datatype, String defaultValue, String path) {
		this.columnInfoList.add(new ColumnInfo(name, ordinality, datatype, defaultValue, path));
		validate();
	}
	
	public void removeColumn(ColumnInfo theInfo) {
		this.columnInfoList.remove(theInfo);
		validate();
	}
	
	public void moveColumnUp(ColumnInfo columnInfo) {
		int startIndex = getColumnIndex(columnInfo);
		
		// 
		if( startIndex > 0 ) {
			// Make Copy of List & get columnInfo of startIndex-1
			ColumnInfo priorInfo = getColumnInfoList()[startIndex-1];
			ColumnInfo[] infos = getColumnInfoList();
			infos[startIndex-1] = columnInfo;
			infos[startIndex] = priorInfo;
			
			Collection<ColumnInfo> colInfos = new ArrayList<ColumnInfo>(infos.length);
			for( ColumnInfo info : infos) {
				colInfos.add(info);
			}
			
			this.columnInfoList = colInfos;
		}
	}
	
	public void moveColumnDown(ColumnInfo columnInfo) {
		int startIndex = getColumnIndex(columnInfo);
		if( startIndex < (getColumnInfoList().length-1) ) {
			// Make Copy of List & get columnInfo of startIndex-1
			ColumnInfo afterInfo = getColumnInfoList()[startIndex+1];
			ColumnInfo[] infos = getColumnInfoList();
			infos[startIndex+1] = columnInfo;
			infos[startIndex] = afterInfo;
			
			Collection<ColumnInfo> colInfos = new ArrayList<ColumnInfo>(infos.length);
			for( ColumnInfo info : infos) {
				colInfos.add(info);
			}
			
			this.columnInfoList = colInfos;
		}
	}
	
	public boolean canMoveUp(ColumnInfo columnInfo) {
		return getColumnIndex(columnInfo) > 0;
	}
	
	public boolean canMoveDown(ColumnInfo columnInfo) {
		return getColumnIndex(columnInfo) < getColumnInfoList().length-1;
	}
	
	private int getColumnIndex(ColumnInfo columnInfo) {
		int i=0;
		for( ColumnInfo colInfo : getColumnInfoList() ) {
			if( colInfo == columnInfo) {
				return i;
			}
			i++;
		}
		
		// Shouldn't ever get here!
		return -1;
	}
	
	public void columnChanged(ColumnInfo columnInfo) {
		validate();
	}
	
	public void setOrdinality(ColumnInfo columnInfo, boolean value) {
		// Need to synchronize the setting of this value for a column info.
		// Basically only ONE Column can be set to TRUE .... AND ... the datatype MUST be an INTEGER
		
		if( value == false ) {
			// Only need to set the columnInfo value
			columnInfo.setOrdinality(false);
		} else  {
			for( ColumnInfo info : this.columnInfoList) {
				if( !(info == columnInfo) ) {
					if( info.getOrdinality() ) {
						info.setOrdinality(false);
					}
				}
			}
			
			if( ! columnInfo.getDatatype().equalsIgnoreCase(ColumnInfo.INTEGER_DATATYPE) ) {
				columnInfo.setDatatype(ColumnInfo.INTEGER_DATATYPE);
			}
			columnInfo.setOrdinality(true);
		}
		validate();
	}
	
    public String getProcedureName() {
		return this.procedureName;
	}
    
	/**
	 * 
	 * @return rootPath the root path xquery expression
	 */
	public String getRootPath() {
		return this.rootPath;
	}

	/**
	 * 
	 * @param rootPath
	 */
	public void setRootPath(String path) {
		this.rootPath = path;
		
		// Need to walk through the ColumnInfo objects and have them re-set their paths
		for( ColumnInfo colInfo : getColumnInfoList() ) {
			colInfo.setRootPath(this.rootPath);
		}
		
		validate();
	}

	public void setProcedureName(String procedureName) {
		this.procedureName = procedureName;
	}

	public Operation getOperation() {
		return this.operation;
	}
	
	public String getViewColumnName(String name) {
		StringBuilder builder = new StringBuilder();
		builder.append(this.generator.getViewModelName());
		builder.append('.').append(this.operation.getName()).append('.').append(name);
		
		return builder.toString();
	}
	
	public void validate() {
		// TODO:
		
		// Validate Paths
		
		setStatus(Status.OK_STATUS);
	}
	
	/**
	 * 
	 * @return status the <code>IStatus</code> representing the validity of the data in this info object
	 */
	public IStatus getStatus() {
		return this.status;
	}

	/**
	 * 
	 * @param status the <code>IStatus</code> representing the validity of the data in this info object
	 */
	public void setStatus(IStatus status) {
		this.status = status;
	}
	
	abstract String getSqlStringTemplate();
	
	abstract String getSqlString(Properties properties);
}
