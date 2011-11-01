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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.LocatorImpl;

import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.core.util.StringUtilities;
import com.metamatrix.modeler.transformation.ui.UiConstants;
import com.metamatrix.modeler.transformation.ui.UiPlugin;
import com.metamatrix.modeler.transformation.ui.wizards.file.TeiidColumnInfo;
import com.metamatrix.modeler.transformation.ui.wizards.file.TeiidFileInfo;

/**
 * Business object used to manage Teiid-specific XML Data File information used during import
 * 
 */
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
    public static final String DEFAULT_XQUERY = "/"; //$NON-NLS-1$
    public static final String GET = "GET"; //$NON-NLS-1$
    public static final String NULL = "null"; //$NON-NLS-1$
	
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
    
    /**
     * An initial xquery root path expression
     * 
     * XMLTABLE([<NSP>,] xquery-expression [<PASSING>] [COLUMNS <COLUMN>, ... )] AS name
     * 
     * Usually of the form '$d/MedlineCitationSet/MedlineCitation'. In this case, the expression defines the initial path
     * inside the XML structure that the COLUMN PATH's are relative to
     */
	private String rootPath = StringUtilities.EMPTY_STRING;
	
	/**
	 * Indicator for the import processor to attempt to create a View Table given the info in this object.
	 */
	private boolean doProcess;
	
	/**
	 * The cached <code>Collection</code> of the first 6 lines to use for UI display purposes
	 */
	private String[] cachedFirstLines;
	
	/**
	 * The  <code>Collection</code> of <code>TeiidColumnInfo</code> objects parsed from the defined header information.
	 */
	private Collection<TeiidColumnInfo> columnInfoList;
 	
	
	private XmlElement rootNode;
	
	private IStatus parsingStatus;
	
	private String xmlFileUrl;
	
	private boolean isUrl = false;

	/**
	 * 
	 * @param dataFile the Teiid-formatted data file
	 */
	public TeiidXmlFileInfo(File dataFile) {
		super(dataFile, false);
		CoreArgCheck.isNotNull(dataFile, "dataFile is null"); //$NON-NLS-1$

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
		this.rootPath = info.getRootPath(); 
		this.columnInfoList = new ArrayList<TeiidColumnInfo>();
		for( TeiidColumnInfo colInfo : info.getColumnInfoList() ) {
			this.columnInfoList.add(new TeiidColumnInfo(colInfo.getName(), 
						colInfo.getOrdinality(), 
						colInfo.getDatatype(), 
						colInfo.getDefaultValue(),
						colInfo.getFullXmlPath()));
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
		this.cachedFirstLines = new String[0];
		this.columnInfoList = new ArrayList<TeiidColumnInfo>();
		
		parsingStatus = parseXmlFile();
		
		String fileName = getDataFile().getName();
		if(fileName.toLowerCase().endsWith(".xml")) { //$NON-NLS-1$
			fileName = fileName.substring(0, fileName.length()-4);
		}
		setViewTableName(fileName + "View"); //$NON-NLS-1$
		validate();
	}
	
	
	public void setIsUrl(boolean value) {
		this.isUrl = value;
	}
	
	public boolean isUrl() {
		return this.isUrl;
	}
	
	public void setXmlFileUrl(String theUrlValue) {
		this.xmlFileUrl = theUrlValue;
	}
	
	public String getXmlFileUrl() {
		return this.xmlFileUrl;
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
		for( TeiidColumnInfo colInfo : getColumnInfoList() ) {
			colInfo.setRootPath(this.rootPath);
		}
		
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
		this.columnInfoList.add(new TeiidColumnInfo(name, ordinality, datatype, defaultValue, path));
		validate();
	}
	
	public TeiidColumnInfo addColumn(String name, boolean ordinality, String datatype, String defaultValue, String rootPath, String fullPath) {
		TeiidColumnInfo newColumnInfo = new TeiidColumnInfo(name, ordinality, datatype, defaultValue, fullPath);
		if( rootPath != null ) {
			newColumnInfo.setRootPath(rootPath);
		}
		this.columnInfoList.add(newColumnInfo);
		validate();
		
		return newColumnInfo;
	}
	
	public TeiidColumnInfo addColumn(String name, boolean ordinality, String datatype, String defaultValue, String rootPath, XmlElement element) {
		TeiidColumnInfo newColumnInfo = new TeiidColumnInfo(name, ordinality, datatype, defaultValue, element.getFullPath());
		newColumnInfo.setXmlElement(element);
		if( rootPath != null ) {
			newColumnInfo.setRootPath(rootPath);
		}
		this.columnInfoList.add(newColumnInfo);
		validate();
		
		return newColumnInfo;
	}
	
	public void removeColumn(TeiidColumnInfo theInfo) {
		this.columnInfoList.remove(theInfo);
		validate();
	}
	
	public void columnChanged(TeiidColumnInfo columnInfo) {
		validate();
	}
	
	public void validate() {
		// Validate XQuery Root Path Expression
		if( this.getRootPath() == null || this.getRootPath().length() == 0 ) {
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
	
	
	public void moveColumnUp(TeiidColumnInfo columnInfo) {
		int startIndex = getColumnIndex(columnInfo);
		
		// 
		if( startIndex > 0 ) {
			// Make Copy of List & get columnInfo of startIndex-1
			TeiidColumnInfo priorInfo = getColumnInfoList()[startIndex-1];
			TeiidColumnInfo[] infos = getColumnInfoList();
			infos[startIndex-1] = columnInfo;
			infos[startIndex] = priorInfo;
			
			Collection<TeiidColumnInfo> colInfos = new ArrayList<TeiidColumnInfo>(infos.length);
			for( TeiidColumnInfo info : infos) {
				colInfos.add(info);
			}
			
			this.columnInfoList = colInfos;
		}
	}
	
	public void moveColumnDown(TeiidColumnInfo columnInfo) {
		int startIndex = getColumnIndex(columnInfo);
		if( startIndex < (getColumnInfoList().length-1) ) {
			// Make Copy of List & get columnInfo of startIndex-1
			TeiidColumnInfo afterInfo = getColumnInfoList()[startIndex+1];
			TeiidColumnInfo[] infos = getColumnInfoList();
			infos[startIndex+1] = columnInfo;
			infos[startIndex] = afterInfo;
			
			Collection<TeiidColumnInfo> colInfos = new ArrayList<TeiidColumnInfo>(infos.length);
			for( TeiidColumnInfo info : infos) {
				colInfos.add(info);
			}
			
			this.columnInfoList = colInfos;
		}
	}
	
	public boolean canMoveUp(TeiidColumnInfo columnInfo) {
		return getColumnIndex(columnInfo) > 0;
	}
	
	public boolean canMoveDown(TeiidColumnInfo columnInfo) {
		return getColumnIndex(columnInfo) < getColumnInfoList().length-1;
	}
	
	private int getColumnIndex(TeiidColumnInfo columnInfo) {
		int i=0;
		for( TeiidColumnInfo colInfo : getColumnInfoList() ) {
			if( colInfo == columnInfo) {
				return i;
			}
			i++;
		}
		
		// Shouldn't ever get here!
		return -1;
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
    	##
    	##  IF URL XML file, use invokeHTTP() method
    	##
    	## EXAMPLE:
    	##
    	## EXEC PlantWSProcedures.invokeHttp('GET', null, 'http://www.w3schools.com/xml/plant_catalog.xml')) AS f,
    	
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
    	if( isUrl() ) {
    		string_2 = S_QUOTE + GET + S_QUOTE + COMMA + SPACE + NULL + COMMA + SPACE + S_QUOTE + getXmlFileUrl() + S_QUOTE;
    	}
    	
    	sb = new StringBuffer();
    	String xQueryExp = DEFAULT_XQUERY;
    	if( getRootPath() != null && getRootPath().length() > 0 ) {
    		xQueryExp = getRootPath();
    	}
    	sb.append(S_QUOTE).append(xQueryExp).append(S_QUOTE);
    	String string_3 = sb.toString();
    	
    	sb = new StringBuffer();
    	i=0;
    	for( TeiidColumnInfo columnInfo : getColumnInfoList()) {
    		if( columnInfo.getOrdinality() ) {
    			sb.append(columnInfo.getName()).append(SPACE).append(FOR_ORDINALITY);
    		} else {
	    		sb.append(columnInfo.getName()).append(SPACE).append(columnInfo.getDatatype());
	    		
	    		String defValue = columnInfo.getDefaultValue();
	    		if( defValue != null && defValue.length() > 0) {
	    			sb.append(SPACE).append(DEFAULT).append(SPACE).append(S_QUOTE).append(defValue).append(S_QUOTE);
	    		}
	    		
	    		String relPath = columnInfo.getRelativePath();
	    		if( relPath != null && relPath.length() > 1 ) {
	    			sb.append(SPACE).append(PATH).append(SPACE).append(S_QUOTE).append(relPath).append(S_QUOTE);
	    		}
	    		
	    		
    		}
    		if(i < (nColumns-1)) {
    			sb.append(COMMA).append(SPACE);
    		}

    		i++;
    	}

    	String string_4 = sb.toString();

    	String finalSQLString = null;
    	
    	if( isUrl() ) {
	    	// SELECT {0} FROM (EXEC {1}.getTextFiles({2})) AS f, XMLTABLE('{3}' PASSING XMLPARSE(DOCUMENT f.file) AS d COLUMNS {4}) AS {5}
	    	finalSQLString = UiPlugin.Util.getString(
	    			"TeiidXmlFileInfo.xmlInvokeHttpTableSqlTemplate", //$NON-NLS-1$
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
    	} else {
    		finalSQLString = UiPlugin.Util.getString(
	    			"TeiidXmlFileInfo.xmlGetTextFilesTableSqlTemplate", //$NON-NLS-1$
	    			string_0,
	    			relationalModelName,
	    			string_2,
	    			string_3,
	    			string_4,
	    			alias);
    	}
    	return finalSQLString;
    }
    
    public XmlElement getRootNode() {
    	return this.rootNode;
    }
    
    public IStatus getParsingStatus() {
    	return this.parsingStatus;
    }

    public IStatus parseXmlFile() {
		XmlParser xmlParser = new XmlParser();
		XmlFileContentHandler contentHandler = new XmlFileContentHandler();
		contentHandler.setDocumentLocator(new LocatorImpl());
		xmlParser.setContentHandler(contentHandler);
		try {
			xmlParser.doParse(getDataFile());
		} catch (RuntimeException ex) {
			String message = Util.getString("TeiidXmlFileInfo.parsingError", ex.getMessage()); //$NON-NLS-1$
			return new Status(IStatus.ERROR, UiConstants.PLUGIN_ID, message, ex);
		} catch (IOException ex) {
			String message = Util.getString("TeiidXmlFileInfo.parsingError", ex.getMessage()); //$NON-NLS-1$
			return new Status(IStatus.ERROR, UiConstants.PLUGIN_ID, message, ex);
		} catch (SAXException ex) {
			String message = Util.getString("TeiidXmlFileInfo.parsingError", ex.getMessage()); //$NON-NLS-1$
			return new Status(IStatus.ERROR, UiConstants.PLUGIN_ID, message, ex);
		}
		rootNode = contentHandler.getRootElement();
		if( rootNode == null ) {
			String message = getString("noRootNodeParsingError"); //$NON-NLS-1$
			return new Status(IStatus.ERROR, UiConstants.PLUGIN_ID, message);
		}

		return Status.OK_STATUS;
    }
}