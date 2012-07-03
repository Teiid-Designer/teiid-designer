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
import com.metamatrix.modeler.core.validation.rules.StringNameValidator;
import com.metamatrix.modeler.internal.transformation.util.SqlConstants;
import com.metamatrix.modeler.modelgenerator.wsdl.model.Operation;


public abstract class ProcedureInfo implements SqlConstants {

    public static final String SQL_BEGIN = "CREATE VIRTUAL PROCEDURE\nBEGIN\n"; //$NON-NLS-1$
    public static final String SQL_END = "\nEND"; //$NON-NLS-1$
    public static final String XSI_NAMESPACE_PREFIX = "xsi"; //$NON-NLS-1$
    
    public static final int REQUEST = 0;
    public static final int RESPONSE = 1;
    public static final int TYPE_BODY = 0;
    public static final int TYPE_HEADER = 1;
    
    private StringNameValidator nameValidator;
    
    public SchemaTreeModel treeModel = null;
    
//	CREATE VIRTUAL PROCEDURE
//	BEGIN
//	    SELECT XMLELEMENT(NAME getPrice, XMLNAMESPACES(DEFAULT 'http://quickstart.samples/xsd'), XMLELEMENT(NAME symbol, StockQuoteServiceXML.getPrice.create_getPrice.symbol)) AS xml_out;
//	END
	private Collection<ColumnInfo> bodyColumnInfoList;
	
	private Collection<ColumnInfo> headerColumnInfoList;
	
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
	
	private boolean changed;
	
	private boolean initializing;

	public ProcedureInfo(Operation operation, int type, ProcedureGenerator generator) {
		super();
		this.initializing = true;
		this.operation = operation;
		this.bodyColumnInfoList = new ArrayList<ColumnInfo>();
		this.headerColumnInfoList = new ArrayList<ColumnInfo>();
		this.namespaceMap = new HashMap<String, String>();
		this.type = type;
		this.generator = generator;
		this.nameValidator = new StringNameValidator(true);
		this.initializing = false;
	}
	
	public abstract String getDefaultProcedureName();
	
	protected ProcedureGenerator getGenerator() {
		return this.generator;
	}
	
	public SchemaTreeModel getTreeModel() {
		return treeModel;
	}

	public void setTreeModel(SchemaTreeModel treeModel) {
		this.treeModel = treeModel;
	}
	
	public void addNamespace(String key, String value) {
		for (String nsKey:this.namespaceMap.keySet()){
			String ns = this.namespaceMap.get(nsKey);
			if (value.endsWith(ns)){
				return;
			}
		}
		this.namespaceMap.put(key, value);
		setChanged(true);
	}
	
	public void addNamespaces(Map<String, String> namespaces) {
		this.namespaceMap.putAll(namespaces);
		setChanged(true);
	}
	
	public Map<String, String> getNamespaceMap() {
		return this.namespaceMap;
	}
	
	public int getType() {
		return this.type;
	}

	public ColumnInfo[] getBodyColumnInfoList() {
		return this.bodyColumnInfoList.toArray(new ColumnInfo[this.bodyColumnInfoList.size()]);
	}
	
	public ColumnInfo addBodyColumn(String name, boolean ordinality, String datatype, String defaultValue, String path) {
		ColumnInfo newInfo = new ColumnInfo(name, ordinality, datatype, defaultValue, path);
		this.bodyColumnInfoList.add(newInfo);
		setChanged(true);
		return newInfo;
	}
	
	public void removeBodyColumn(ColumnInfo theInfo) {
		this.bodyColumnInfoList.remove(theInfo);
		setChanged(true);
	}
	
	public void moveBodyColumnUp(ColumnInfo columnInfo) {
		int startIndex = getBodyColumnIndex(columnInfo);
		
		// 
		if( startIndex > 0 ) {
			// Make Copy of List & get columnInfo of startIndex-1
			ColumnInfo priorInfo = getBodyColumnInfoList()[startIndex-1];
			ColumnInfo[] infos = getBodyColumnInfoList();
			infos[startIndex-1] = columnInfo;
			infos[startIndex] = priorInfo;
			
			Collection<ColumnInfo> colInfos = new ArrayList<ColumnInfo>(infos.length);
			for( ColumnInfo info : infos) {
				colInfos.add(info);
			}
			
			this.bodyColumnInfoList = colInfos;
			setChanged(true);
		}
	}
	
	public void moveBodyColumnDown(ColumnInfo columnInfo) {
		int startIndex = getBodyColumnIndex(columnInfo);
		if( startIndex < (getBodyColumnInfoList().length-1) ) {
			// Make Copy of List & get columnInfo of startIndex-1
			ColumnInfo afterInfo = getBodyColumnInfoList()[startIndex+1];
			ColumnInfo[] infos = getBodyColumnInfoList();
			infos[startIndex+1] = columnInfo;
			infos[startIndex] = afterInfo;
			
			Collection<ColumnInfo> colInfos = new ArrayList<ColumnInfo>(infos.length);
			for( ColumnInfo info : infos) {
				colInfos.add(info);
			}
			
			this.bodyColumnInfoList = colInfos;
			setChanged(true);
		}
	}
	
	public boolean canMoveBodyColumnUp(ColumnInfo columnInfo) {
		return getBodyColumnIndex(columnInfo) > 0;
	}
	
	public boolean canMoveBodyColumnDown(ColumnInfo columnInfo) {
		return getBodyColumnIndex(columnInfo) < getBodyColumnInfoList().length-1;
	}
	
	private int getBodyColumnIndex(ColumnInfo columnInfo) {
		int i=0;
		for( ColumnInfo colInfo : getBodyColumnInfoList() ) {
			if( colInfo == columnInfo) {
				return i;
			}
			i++;
		}
		
		// Shouldn't ever get here!
		return -1;
	}
	
	public ColumnInfo[] getHeaderColumnInfoList() {
		return this.headerColumnInfoList.toArray(new ColumnInfo[this.headerColumnInfoList.size()]);
	}
	
	public ColumnInfo addHeaderColumn(String name, boolean ordinality, String datatype, String defaultValue, String path) {
		ColumnInfo newInfo = new ColumnInfo(name, ordinality, datatype, defaultValue, path);
		this.headerColumnInfoList.add(newInfo);
		setChanged(true);
		return newInfo;
	}
	
	public void removeHeaderColumn(ColumnInfo theInfo) {
		this.headerColumnInfoList.remove(theInfo);
		setChanged(true);
	}
	
	public void moveHeaderColumnUp(ColumnInfo columnInfo) {
		int startIndex = getHeaderColumnIndex(columnInfo);
		
		// 
		if( startIndex > 0 ) {
			// Make Copy of List & get columnInfo of startIndex-1
			ColumnInfo priorInfo = getHeaderColumnInfoList()[startIndex-1];
			ColumnInfo[] infos = getHeaderColumnInfoList();
			infos[startIndex-1] = columnInfo;
			infos[startIndex] = priorInfo;
			
			Collection<ColumnInfo> colInfos = new ArrayList<ColumnInfo>(infos.length);
			for( ColumnInfo info : infos) {
				colInfos.add(info);
			}
			
			this.headerColumnInfoList = colInfos;
			setChanged(true);
		}
	}
	
	public void moveHeaderColumnDown(ColumnInfo columnInfo) {
		int startIndex = getHeaderColumnIndex(columnInfo);
		if( startIndex < (getHeaderColumnInfoList().length-1) ) {
			// Make Copy of List & get columnInfo of startIndex-1
			ColumnInfo afterInfo = getHeaderColumnInfoList()[startIndex+1];
			ColumnInfo[] infos = getHeaderColumnInfoList();
			infos[startIndex+1] = columnInfo;
			infos[startIndex] = afterInfo;
			
			Collection<ColumnInfo> colInfos = new ArrayList<ColumnInfo>(infos.length);
			for( ColumnInfo info : infos) {
				colInfos.add(info);
			}
			
			this.headerColumnInfoList = colInfos;
			setChanged(true);
		}
	}
	
	public boolean canMoveHeaderColumnUp(ColumnInfo columnInfo) {
		return getHeaderColumnIndex(columnInfo) > 0;
	}
	
	public boolean canMoveHeaderColumnDown(ColumnInfo columnInfo) {
		return getHeaderColumnIndex(columnInfo) < getHeaderColumnInfoList().length-1;
	}
	
	private int getHeaderColumnIndex(ColumnInfo columnInfo) {
		int i=0;
		for( ColumnInfo colInfo : getHeaderColumnInfoList() ) {
			if( colInfo == columnInfo) {
				return i;
			}
			i++;
		}
		
		// Shouldn't ever get here!
		return -1;
	}
	
	public void columnChanged(ColumnInfo columnInfo) {
		setChanged(true);
	}
	
	public void setOrdinality(ColumnInfo columnInfo, boolean value) {
		// Need to synchronize the setting of this value for a column info.
		// Basically only ONE Column can be set to TRUE .... AND ... the datatype MUST be an INTEGER
		
		if( value == false ) {
			// Only need to set the columnInfo value
			columnInfo.setOrdinality(false);
		} else  {
			for( ColumnInfo info : this.bodyColumnInfoList) {
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
		setChanged(true);
	}
	
    public String getProcedureName() {
		return this.procedureName;
	}
    
	/**
	 * 
	 * @return rootPath the root path xquery expression
	 */
	public String getRootPath() {
		return this.getGenerator().getImportManager().isMessageServiceMode() ? ResponseInfo.SOAPENVELOPE_ROOTPATH: this.rootPath;
	}

	/**
	 * 
	 * @param rootPath
	 */
	public void setRootPath(String path) {
		this.rootPath = path;
		
		// Need to walk through the ColumnInfo objects and have them re-set their paths
		for( ColumnInfo colInfo : getBodyColumnInfoList() ) {
			colInfo.setRootPath(this.rootPath);
		}
		
		// Need to walk through the ColumnInfo objects and have them re-set their paths
		for( ColumnInfo colInfo : getHeaderColumnInfoList() ) {
			colInfo.setRootPath(this.rootPath);
		}
		
		setChanged(true);
	}

	public void setProcedureName(String procedureName) {
		this.procedureName = procedureName;
		setChanged(true);
	}

	public Operation getOperation() {
		return this.operation;
	}
	
	public IStatus validate() {
		return Status.OK_STATUS;
	}
	
	public String getUniqueBodyColumnName(String proposedName) {
		for( ColumnInfo info : getBodyColumnInfoList()) {
			this.nameValidator.addExistingName(info.getName());
		}
		String changedName = this.nameValidator.createUniqueName(proposedName);
		String finalName = changedName == null ? proposedName : changedName;
		this.nameValidator.clearExistingNames();
		return finalName;
		
	}
	
	public String getUniqueHeaderColumnName(String proposedName) {
		for( ColumnInfo info : getHeaderColumnInfoList()) {
			this.nameValidator.addExistingName(info.getName());
		}
		String changedName = this.nameValidator.createUniqueName(proposedName);
		String finalName = changedName == null ? proposedName : changedName;
		this.nameValidator.clearExistingNames();
		return finalName;
	}
	
	public void setChanged(boolean value) {
		this.changed = value;
		if( this.changed && !this.initializing) {
			this.generator.setChanged(changed);
		}
	}
	
	public boolean isChanged() {
		return this.changed;
	}
	
	abstract String getSqlStringTemplate();
	
	abstract String getSqlString(Properties properties);
}
