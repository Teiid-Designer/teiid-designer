/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package com.metamatrix.modeler.transformation.ui.wizards.xmlfile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.core.util.StringUtilities;
import com.metamatrix.modeler.transformation.ui.UiConstants;
import com.metamatrix.modeler.transformation.ui.UiPlugin;
import com.metamatrix.modeler.transformation.ui.wizards.file.TeiidColumnInfo;
import com.metamatrix.modeler.transformation.ui.wizards.file.TeiidFileInfo;

public class TeiidXmlFileInfo extends TeiidFileInfo implements UiConstants {
	private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(TeiidXmlFileInfo.class);
	
	public static final char DOT = '.';
    public static final char COMMA = ',';
    public static final char SPACE = ' ';
    public static final char S_QUOTE = '\'';
    public static final String AS = "AS"; //$NON-NLS-1$
    public static final String COLUMNS = "COLUMNS"; //$NON-NLS-1$
    public static final String PATH = "PATH"; //$NON-NLS-1$
    public static final String DEFAULT = "DEFAULT"; //$NON-NLS-1$
    public static final String FOR_ORDINALITY = "FOR ORDINALITY"; //$NON-NLS-1$
	
    private static String getString( final String id ) {
        return Util.getString(I18N_PREFIX + id);
    }
    
    /**
     * The number of cached lines of the data file for display
     */
    private int numberOfCachedLines = 20;
    
    /**
     * The number of lines in data file
     */
    private int numberOfLinesInFile = 0;
    
//    /**
//     * The unique data file name containing Teiid-formatted relational table data (never <code>null</code> or empty).
//     */
//	private File dataFile;
	
//    /**
//     * The unique view table name containing the generated SELECT SQL statement that converts file data into
//     * relational columns (never <code>null</code> or empty).
//     */
//	private String viewTableName = StringUtilities.EMPTY_STRING;
	
    /**
     * An initial xquery expression
     * 
     * XMLTABLE([<NSP>,] xquery-expression [<PASSING>] [COLUMNS <COLUMN>, ... )] AS name
     * 
     * Usually of the form '$d/MedlineCitationSet/MedlineCitation'. In this case, the expression defines the initial path
     * inside the XML structure that the COLUMN PATH's are relative to
     */
	private String xqueryExpression = StringUtilities.EMPTY_STRING;
	
	/**
	 * Indicator for the import processor to attempt to create a View Table given the info in this object.
	 */
	private boolean doProcess;
	
//	/**
//	 * Current <code>IStatus</code> representing the state of the input values for this instance of
//	 * <code>TeiidMetadataFileInfo</code>
//	 */
//	private IStatus status;
	
	/**
	 * The cached <code>Collection</code> of the first 6 lines to use for UI display purposes
	 */
	private String[] cachedFirstLines;
	
	/**
	 * The  <code>Collection</code> of <code>TeiidColumnInfo</code> objects parsed from the defined header information.
	 */
	private Collection<TeiidColumnInfo> columnInfoList;
 	
	/**
	 * The  <code>Map</code> of column names to column datatypes defined by the user in the UI
	 */
	private Map<String, String> columnDatatypeMap;

	/**
	 * 
	 * @param dataFile the Teiid-formatted data file
	 */
	public TeiidXmlFileInfo(File dataFile) {
		super(dataFile, false);
		CoreArgCheck.isNotNull(dataFile, "dataFile is null"); //$NON-NLS-1$
//		
//		this.dataFile = dataFile;
		initialize();
	}
	
	/**
	 * 
	 * @param info the data file info object
	 */
	public TeiidXmlFileInfo(TeiidXmlFileInfo info) {
		super(info.getDataFile(), false);
		
		inject(info);
	}
	
	/**
	 * This method allows setting the values in the current info object using the values from an external info object
	 * 
	 * @param info the data file info object
	 */
	public void inject(TeiidXmlFileInfo info) {
		CoreArgCheck.isNotNull(info.getDataFile(), "dataFile is null"); //$NON-NLS-1$
		
		this.cachedFirstLines = info.cachedFirstLines;
		this.numberOfLinesInFile = info.getNumberOfLinesInFile();
		this.xqueryExpression = info.getXQueryExpression(); 
		this.columnInfoList = new ArrayList<TeiidColumnInfo>();
		for( TeiidColumnInfo colInfo : info.getColumnInfoList() ) {
			this.columnInfoList.add(new TeiidColumnInfo(colInfo.getName(), 
						colInfo.getOrdinality(), 
						colInfo.getDatatype(), 
						colInfo.getDefaultValue(),
						colInfo.getXmlPath()));
		}
		
		setStatus(info.getStatus());
		if( info.getViewTableName() != null ) {
			setViewTableName(info.getViewTableName());
		} else {
			setViewTableName(StringUtilities.EMPTY_STRING);
		}
		
		validate();
	}
	
	private void initialize() {
		setStatus(Status.OK_STATUS);
		this.columnDatatypeMap = new HashMap<String, String>();
		this.cachedFirstLines = new String[0];
		this.columnInfoList = new ArrayList<TeiidColumnInfo>();
		
		loadHeader();
		
		String fileName = getDataFile().getName();
		if(fileName.toLowerCase().endsWith(".xml")) { //$NON-NLS-1$
			fileName = fileName.substring(0, fileName.length()-4);
		}
		setViewTableName(fileName + "View"); //$NON-NLS-1$
		validate();
	}
	
	/**
	 * 
	 * @param columnName the column name (never <code>null</code> or empty).
	 * @param datatype the column datatype (never <code>null</code> or empty).
	 */
	public void setDatatype(String columnName, String datatype) {
		CoreArgCheck.isNotEmpty(columnName, "columnName is null"); //$NON-NLS-1$
		CoreArgCheck.isNotEmpty(datatype, "datatype is null"); //$NON-NLS-1$
		
		this.columnDatatypeMap.put(columnName, datatype);
		validate();
	}
	
	/**
	 * 
	 * @param columnName the column name (never <code>null</code> or empty).
	 * @return datatype the column datatype (will not be null)
	 */
	public String getDatatype(String columnName) {
		return this.columnDatatypeMap.get(columnName);
	}
	
//	/**
//	 * 
//	 * @return viewTableName the view table name (never <code>null</code> or empty).
//	 */
//	public String getViewTableName() {
//		return this.viewTableName;
//	}

//	/**
//	 * 
//	 * @param viewTableName (never <code>null</code> or empty).
//	 */
//	@Override
//	public void setViewTableName(String viewTableName) {
//		CoreArgCheck.isNotEmpty(viewTableName, "viewTableName is null"); //$NON-NLS-1$
//		
//		this.viewTableName = viewTableName;
//		validate();
//	}
	
	/**
	 * 
	 * @return viewTableName the view table name (never <code>null</code> or empty).
	 */
	public String getXQueryExpression() {
		return this.xqueryExpression;
	}

	/**
	 * 
	 * @param viewTableName (never <code>null</code> or empty).
	 */
	public void setXQueryExpression(String expression) {
		CoreArgCheck.isNotEmpty(expression, "xqueryExpression is null"); //$NON-NLS-1$
		
		this.xqueryExpression = expression;
		validate();
	}

	private void loadHeader() {
		this.cachedFirstLines = new String[0];
		Collection<String> lines = new ArrayList<String>(7);
		
        if(this.getDataFile() != null && this.getDataFile().exists()){
            FileReader fr=null;
            BufferedReader in=null;

            try{
            	int iLines = 0;
                fr=new FileReader(this.getDataFile());
                in = new BufferedReader(fr);
                String str;
                while ((str = in.readLine()) != null) {
                	iLines++;

                	if( iLines <= numberOfCachedLines ) {
                		lines.add(str);
                	}
                }
                this.numberOfLinesInFile = iLines;
                
                this.cachedFirstLines = lines.toArray(new String[0]);
            }catch(Exception e){
            	Util.log(IStatus.ERROR, e, 
                		Util.getString(I18N_PREFIX + "problemLoadingFileContentsMessage", this.getDataFile().getName())); //$NON-NLS-1$
            }
            finally{
                try{
                    fr.close();
                }catch(java.io.IOException e){}
                try{
                    in.close();
                }catch(java.io.IOException e){}

            }
        }
	}
	
	/**
	 * 
	 * @return cachedFirstLines the <code>String[]</code> array from the data file
	 */
	public String[] getCachedFirstLines() {
		return this.cachedFirstLines;
	}
	
	/**
	 * 
	 * @return columnInfoList the <code>TeiidColumnInfo[]</code> array parsed from the header in the data file
	 */
	public TeiidColumnInfo[] getColumnInfoList() {
		return this.columnInfoList.toArray(new TeiidColumnInfo[this.columnInfoList.size()]);
	}
	
	public void addColumn(String name, boolean ordinality, String datatype, String defaultValue, String path) {
		this.columnInfoList.add(new TeiidColumnInfo(name, ordinality, datatype, defaultValue, path) );
		validate();
	}
	
	public void removeColumn(TeiidColumnInfo theInfo) {
		this.columnInfoList.remove(theInfo);
		validate();
	}
	
	public void columnChanged(TeiidColumnInfo columnInfo) {
		validate();
	}
	
	public void validate() {
		// Validate XQuery Expression
		if( this.getXQueryExpression() == null || this.getXQueryExpression().length() == 0 ) {
			setStatus(new Status(IStatus.ERROR, PLUGIN_ID, getString("status.xqueryExpressionNullOrEmpty"))); //$NON-NLS-1$
			return;
		}
		
		
		// must have one or more columns defined
		if( this.columnInfoList.isEmpty() ) {
			setStatus(new Status(IStatus.ERROR, PLUGIN_ID, getString("status.noColumnsDefined"))); //$NON-NLS-1$
			return;
		}
		
		// Validate Column names
		// Check for ERRORS FIRST
		for( TeiidColumnInfo info : this.getColumnInfoList()) {
			if( info.getStatus().getSeverity() == IStatus.ERROR ) {
				this.setStatus(info.getStatus());
				return;
			}
		}
		
		for( TeiidColumnInfo info : this.getColumnInfoList()) {
			if( info.getStatus().getSeverity() != IStatus.OK ) {
				this.setStatus(info.getStatus());
				return;
			}
		}
		
		// Validate Paths
		
		setStatus(Status.OK_STATUS);
	}
	
	/**
	 * 
	 * @param doProcess the boolean indicator that the user wishes to create view table from this object
	 */
	public void setDoProcess(boolean doProcess) {
		this.doProcess = doProcess;
	}
	
	/**
	 * 
	 * @return doProcess the boolean indicator that the user wishes to create view table from this object
	 */
	public boolean doProcess() {
		return this.doProcess;
	}
	
	
	/**
	 * 
	 * @param nLines the number of cached lines from data file
	 */
	public void setNumberOfCachedFileLines(int nLines) {
		this.numberOfCachedLines = nLines;
		loadHeader();
	}
	
	/**
	 * 
	 * @return numberOfCachedLines the number of cached lines from data file
	 */
	public int getNumberOfCachedFileLines() {
		return this.numberOfCachedLines;
	}
	
	/**
	 * 
	 * @return numberOfCachedLines the total number of lines from data file
	 */
	public int getNumberOfLinesInFile() {
		return this.numberOfLinesInFile;
	}
	
//	/**
//	 * 
//	 * @return status the <code>IStatus</code> representing the validity of the data in this info object
//	 */
//	public IStatus getStatus() {
//		return this.status;
//	}
//
//	/**
//	 * 
//	 * @param status the <code>IStatus</code> representing the validity of the data in this info object
//	 */
//	public void setStatus(IStatus status) {
//		this.status = status;
//	}
	
	public void setOrdinality(TeiidColumnInfo columnInfo, boolean value) {
		// Need to synchronize the setting of this value for a column info.
		// Basically only ONE Column can be set to TRUE .... AND ... the datatype MUST be an INTEGER
		
		if( value == false ) {
			// Only need to set the columnInfo value
			columnInfo.setOrdinality(false);
		} else  {
			for( TeiidColumnInfo info : this.columnInfoList) {
				if( !(info == columnInfo) ) {
					if( info.getOrdinality() ) {
						info.setOrdinality(false);
					}
				}
			}
			
			if( ! columnInfo.getDatatype().equalsIgnoreCase(TeiidColumnInfo.INTEGER_DATATYPE) ) {
				columnInfo.setDatatype(TeiidColumnInfo.INTEGER_DATATYPE);
			}
			columnInfo.setOrdinality(true);
		}
		validate();
	}
	
    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder text = new StringBuilder();
        text.append("Teiid Metadata File Info: "); //$NON-NLS-1$
        text.append("file name = ").append(getDataFile().getName()); //$NON-NLS-1$
        text.append(", view table name = ").append(getViewTableName()); //$NON-NLS-1$
        
        return text.toString();
    }
    
//	/**
//	 * 
//	 * @return dataFile the teiid-formatted data <code>File</code>
//	 */
//	public File getDataFile() {
//		return this.dataFile;
//	}
    
	/**
	 * Returns the current generated SQL string based on an unknown relational model name
	 * @return the generated SQL string
	 */
    public String getSqlStringTemplate() {
    	return getSqlString("myRelModel"); //$NON-NLS-1$
    }
    
	/**
	 * Returns the current generated SQL string based on an unknown relational model name
	 * @return the generated SQL string based on the values stored on this instance
	 */
    public String getSqlString(String relationalModelName) {
    	/*
    	##  SELECT
    	##		title.pmid AS pmid, title.journal AS journal, title.title AS title
    	##	FROM
    	##		(EXEC getMeds.getTextFiles('medsamp2011.xml')) AS f, 
    	##           XMLTABLE('$d/MedlineCitationSet/MedlineCitation' PASSING 
    	##                 XMLPARSE(DOCUMENT f.file) AS d 
    	##				   COLUMNS pmid biginteger PATH 'PMID', journal string PATH 'Article/Journal/Title', title string PATH 'Article/ArticleTitle') AS title
    	##
    	## XMLTABLE([<NSP>,] xquery-expression [<PASSING>] [COLUMNS <COLUMN>, ... )] AS name
    	##
    	## COLUMN := name (FOR ORDINALITY | (datatype [DEFAULT expression] [PATH string]))
    	##
    	##  name     datatype  PATH            string
    	## journal    string   PATH   'Article/Journal/Title'
    	##
    	##
    	*/
    	
    	String alias = "A"; //$NON-NLS-1$
    	StringBuffer sb = new StringBuffer();
    	int i=0;
    	int nColumns = getColumnInfoList().length;
    	for( TeiidColumnInfo columnInfo : getColumnInfoList()) {
    		String name = columnInfo.getName();
    		sb.append(alias).append(DOT).append(name).append(SPACE).append(AS).append(SPACE).append(name);
    		
    		if(i < (nColumns-1)) {
    			sb.append(COMMA).append(SPACE);
    		}
    		i++;
    	}
    	String string_0 = sb.toString();
    	
    	String string_2 = S_QUOTE + getDataFile().getName() + S_QUOTE;
    	
    	sb = new StringBuffer();
    	sb.append(S_QUOTE).append(getXQueryExpression()).append(S_QUOTE);
    	String string_3 = sb.toString();
    	
    	sb = new StringBuffer();
    	i=0;
    	for( TeiidColumnInfo columnInfo : getColumnInfoList()) {
    		if( columnInfo.getOrdinality() ) {
    			sb.append(columnInfo.getName()).append(SPACE).append(FOR_ORDINALITY);
    		} else {
	    		sb.append(columnInfo.getName()).append(SPACE).append(columnInfo.getDatatype());
	    		
	    		if( columnInfo.getDefaultValue() != null && columnInfo.getDefaultValue().length() > 0) {
	    			sb.append(SPACE).append(DEFAULT).append(SPACE).append(S_QUOTE).append(columnInfo.getDefaultValue()).append(S_QUOTE);
	    		}
	    		
	    		if( columnInfo.getXmlPath()!= null && columnInfo.getXmlPath().length() > 1 ) {
	    			sb.append(SPACE).append(PATH).append(SPACE).append(S_QUOTE).append(columnInfo.getXmlPath()).append(S_QUOTE);
	    		}
	    		
	    		
    		}
    		if(i < (nColumns-1)) {
    			sb.append(COMMA).append(SPACE);
    		}

    		i++;
    	}

    	String string_4 = sb.toString();

    	// SELECT {0} FROM (EXEC {1}.getTextFiles({2})) AS f, XMLTABLE('{3}' PASSING XMLPARSE(DOCUMENT f.file) AS d COLUMNS {4}) AS {5}
    	String finalSQLString = UiPlugin.Util.getString(
    			"TeiidXmlFileInfo.xmlTableSqlTemplate", //$NON-NLS-1$
    			string_0,
    			relationalModelName,
    			string_2,
    			string_3,
    			string_4,
    			alias);
    	
//    	finalSQLString = "SELECT \n\ttitle.pmid AS pmid, title.journal AS journal, title.title AS title\n" + 
//    			"FROM \n\t(EXEC getMeds.getTextFiles('medsamp2011.xml')) AS f," +  
//    			"XMLTABLE('$d/MedlineCitationSet/MedlineCitation' PASSING " +
//    			"XMLPARSE(DOCUMENT f.file) AS d " + 
//    			"COLUMNS pmid biginteger PATH 'PMID', journal string PATH 'Article/Journal/Title', title string PATH 'Article/ArticleTitle') AS title";
    	
    	return finalSQLString;
    }

}