/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.transformation.ui.wizards.xmlfile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.ByteOrderMark;
import org.apache.commons.io.input.BOMInputStream;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.core.designer.util.StringConstants;
import org.teiid.core.designer.util.StringUtilities;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.query.IProcedureService;
import org.teiid.designer.query.IQueryService;
import org.teiid.designer.query.proc.ITeiidXmlColumnInfo;
import org.teiid.designer.query.proc.ITeiidXmlFileInfo;
import org.teiid.designer.transformation.ui.UiConstants;
import org.teiid.designer.transformation.ui.wizards.file.TeiidFileInfo;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.LocatorImpl;

/**
 * Business object used to manage Teiid-specific XML Data File information used during import
 * 
 *
 * @since 8.0
 */
public class TeiidXmlFileInfo extends TeiidFileInfo implements UiConstants, ITeiidXmlFileInfo<TeiidXmlColumnInfo> {
	private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(TeiidXmlFileInfo.class);


    private static final String XSI_NAMESPACE_PREFIX = "xsi"; //$NON-NLS-1$
	
    private static String getString( final String id ) {
        return Util.getString(I18N_PREFIX + id);
    }
    
    private static String getString( final String id, final Object arg1) {
        return Util.getString(I18N_PREFIX + id, arg1);
    }
    
    /** UTF-8 BOM */
    public static final ByteOrderMark UTF_8    = new ByteOrderMark("UTF-8",    0xEF, 0xBB, 0xBF);
    /** UTF-16BE BOM (Big Endian) */
    public static final ByteOrderMark UTF_16BE = new ByteOrderMark("UTF-16BE", 0xFE, 0xFF);
    /** UTF-16LE BOM (Little Endian) */
    public static final ByteOrderMark UTF_16LE = new ByteOrderMark("UTF-16LE", 0xFF, 0xFE);
    
    public static final ByteOrderMark UTF_32BE = new ByteOrderMark("UTF-32BE", 0x00, 0x00, 0xFE, 0xFF);
    /** UTF-16LE BOM (Little Endian) */
    public static final ByteOrderMark UTF_32LE = new ByteOrderMark("UTF-32LE",  0x00, 0x00, 0xFF, 0xFE);
    
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
	private String rootPath = StringConstants.EMPTY_STRING;
	
	/**
	 * Common Root path
	 */
	private String commonRootPath = StringConstants.EMPTY_STRING;
	
	/**
	 * Indicator for the import processor to attempt to create a View Table given the info in this object.
	 */
	private boolean doProcess;
	
	/**
	 * The cached <code>Collection</code> of the first 6 lines to use for UI display purposes
	 */
	private String[] cachedFirstLines;
	
	/**
	 * The  <code>List</code> of <code>TeiidXmlColumnInfo</code> objects parsed from the defined header information.
	 */
	private List<TeiidXmlColumnInfo> columnInfoList;
	
	private Map<String, Object> parameterMap = new HashMap<String,Object>();
 	
	private XmlElement rootNode;
	
	private Map<String, String> namespaceMap;
	
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
		this.parameterMap = new HashMap<String, Object>(parameterMap);
		this.columnInfoList = new ArrayList<TeiidXmlColumnInfo>();
		for( ITeiidXmlColumnInfo iColInfo : info.getColumnInfoList() ) {
		    TeiidXmlColumnInfo colInfo = (TeiidXmlColumnInfo) iColInfo;
		    
			this.columnInfoList.add(new TeiidXmlColumnInfo(
						colInfo.getXmlElement(),
						colInfo.getXmlAttribute(),
						colInfo.getSymbolName(), 
						colInfo.getOrdinality(), 
						colInfo.getDatatype(), 
						colInfo.getDefaultValue(),
						getRootPath(),
						colInfo.getFullXmlPath()));
		}
		
		setStatus(info.getStatus());
		if( info.getViewTableName() != null ) {
			setViewTableName(info.getViewTableName());
		} else {
			setViewTableName(StringConstants.EMPTY_STRING);
		}
		
		if( info.getViewProcedureName() != null ) {
			setViewProcedureName(info.getViewProcedureName());
		} else {
			setViewProcedureName(StringConstants.EMPTY_STRING);
		}
		
		validate();
	}
	
	private void initialize() {
		setStatus(Status.OK_STATUS);
		this.cachedFirstLines = new String[0];
		this.columnInfoList = new ArrayList<TeiidXmlColumnInfo>();
		this.namespaceMap = new HashMap<String, String>();
		this.parsingStatus = Status.OK_STATUS;
		parseXmlFile();
		
		setViewTableName("new_table"); //$NON-NLS-1$

		validate();
	}
	
	
	public void setIsUrl(boolean value) {
		this.isUrl = value;
	}
	
	@Override
    public boolean isUrl() {
		return this.isUrl;
	}
	
	public void setXmlFileUrl(String theUrlValue) {
		this.xmlFileUrl = theUrlValue;
	}
	
	@Override
    public String getXmlFileUrl() {
		return this.xmlFileUrl;
	}
	
	/**
	 * 
	 * @return rootPath the root path xquery expression
	 */
	@Override
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
		for( TeiidXmlColumnInfo colInfo : columnInfoList ) {
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
	@Override
    public String[] getCachedFirstLines() {
		return this.cachedFirstLines;
	}
	
	/**
	 * 
	 * @return columnInfoList the <code>TeiidXmlColumnInfo[]</code> array parsed from the header in the data file
	 */
	@Override
    public List<TeiidXmlColumnInfo> getColumnInfoList() {
		return this.columnInfoList;
	}
	
	public void addNewColumn(Object obj) {
		if( obj instanceof XmlElement ) {
			TeiidXmlColumnInfo newColumnInfo = new TeiidXmlColumnInfo((XmlElement)obj, getRootPath());
			this.columnInfoList.add(newColumnInfo);
		} else if( obj instanceof XmlAttribute ) {
			TeiidXmlColumnInfo newColumnInfo = new TeiidXmlColumnInfo((XmlAttribute)obj, getRootPath());
			this.columnInfoList.add(newColumnInfo);
		}
		validate();
	}

	public void removeColumn(ITeiidXmlColumnInfo theInfo) {
		this.columnInfoList.remove(theInfo);
		validate();
	}
	
	public void columnChanged(ITeiidXmlColumnInfo columnInfo) {
		validate();
	}
	
    @Override
	public void validate() {
		// Validate XQuery Root Path Expression
		if( this.getRootPath() == null || this.getRootPath().length() == 0 ) {
			setStatus(new Status(IStatus.ERROR, PLUGIN_ID, getString("status.rootPathUndefined"))); //$NON-NLS-1$
			return;
		}
		
		
		// must have one or more columns defined
		if( this.columnInfoList.isEmpty() ) {
			setStatus(new Status(IStatus.ERROR, PLUGIN_ID, getString("status.noColumnsDefined"))); //$NON-NLS-1$
			return;
		}
		
		// Validate Column names
		// Check for ERRORS FIRST
		for( ITeiidXmlColumnInfo info : this.getColumnInfoList()) {
			if( info.getStatus().getSeverity() == IStatus.ERROR ) {
				this.setStatus(info.getStatus());
				return;
			}
		}
		
		for( ITeiidXmlColumnInfo info : this.getColumnInfoList()) {
			if( info.getStatus().getSeverity() != IStatus.OK ) {
				this.setStatus(info.getStatus());
				return;
			}
		}
		
		// Check for duplicate column names
		// Walk through list of columns and cache the names
		Collection<String> toUpperNames = new ArrayList<String>(this.getColumnInfoList().size());
		
		for( ITeiidXmlColumnInfo info : this.getColumnInfoList()) {
			if( toUpperNames.contains(info.getName().toUpperCase()) ) {
				setStatus(new Status(IStatus.ERROR, PLUGIN_ID, getString("status.duplicateColumnNames", info.getName()))); //$NON-NLS-1$
				return;
			}
			toUpperNames.add(info.getName().toUpperCase());
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
	@Override
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
	@Override
    public int getNumberOfCachedFileLines() {
		return this.numberOfCachedLines;
	}
	
	/**
	 * 
	 * @return numberOfCachedLines the total number of lines from data file
	 */
	@Override
    public int getNumberOfLinesInFile() {
		return this.numberOfLinesInFile;
	}
	
	public void setOrdinality(TeiidXmlColumnInfo columnInfo, boolean value) {
		// Need to synchronize the setting of this value for a column info.
		// Basically only ONE Column can be set to TRUE .... AND ... the datatype MUST be an INTEGER
		
		if( value == false ) {
			// Only need to set the columnInfo value
			columnInfo.setOrdinality(false);
		} else  {
			for( TeiidXmlColumnInfo info : this.columnInfoList) {
				if( !(info == columnInfo) ) {
					if( info.getOrdinality() ) {
						info.setOrdinality(false);
					}
				}
			}
			
			if( ! columnInfo.getDatatype().equalsIgnoreCase(ITeiidXmlColumnInfo.INTEGER_DATATYPE) ) {
				columnInfo.setDatatype(ITeiidXmlColumnInfo.INTEGER_DATATYPE);
			}
			columnInfo.setOrdinality(true);
		}
		validate();
	}
	
	
	public void moveColumnUp(TeiidXmlColumnInfo columnInfo) {
		int startIndex = getColumnIndex(columnInfo);
		
		// 
		if( startIndex > 0 ) {
			// Make Copy of List & get columnInfo of startIndex-1
			TeiidXmlColumnInfo priorInfo = getColumnInfoList().get(startIndex-1);
            TeiidXmlColumnInfo[] infos = getColumnInfoList().toArray(new TeiidXmlColumnInfo[0]);
			infos[startIndex-1] = columnInfo;
			infos[startIndex] = priorInfo;
			
			List<TeiidXmlColumnInfo> colInfos = new ArrayList<TeiidXmlColumnInfo>(infos.length);
			for( TeiidXmlColumnInfo info : infos) {
				colInfos.add(info);
			}
			
			this.columnInfoList = colInfos;
		}
	}
	
	public void moveColumnDown(TeiidXmlColumnInfo columnInfo) {
		int startIndex = getColumnIndex(columnInfo);
		if( startIndex < (getColumnInfoList().size()-1) ) {
			// Make Copy of List & get columnInfo of startIndex-1
			TeiidXmlColumnInfo afterInfo = getColumnInfoList().get(startIndex+1);
            TeiidXmlColumnInfo[] infos = getColumnInfoList().toArray(new TeiidXmlColumnInfo[0]);
			infos[startIndex+1] = columnInfo;
			infos[startIndex] = afterInfo;
			
			List<TeiidXmlColumnInfo> colInfos = new ArrayList<TeiidXmlColumnInfo>(infos.length);
			for( TeiidXmlColumnInfo info : infos) {
				colInfos.add(info);
			}
			
			this.columnInfoList = colInfos;
		}
	}
	
	public boolean canMoveUp(ITeiidXmlColumnInfo columnInfo) {
		return getColumnIndex(columnInfo) > 0;
	}
	
	public boolean canMoveDown(ITeiidXmlColumnInfo columnInfo) {
		return getColumnIndex(columnInfo) < getColumnInfoList().size()-1;
	}
	
	private int getColumnIndex(ITeiidXmlColumnInfo columnInfo) {
		int i=0;
		for( ITeiidXmlColumnInfo colInfo : getColumnInfoList() ) {
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
    @Override
    public String getSqlStringTemplate() {
    	return getSqlString("myRelModel"); //$NON-NLS-1$
    }
    
	/**
	 * Returns the current generated SQL string based on an unknown relational model name
	 * @return the generated SQL string based on the values stored on this instance
	 */
    @Override
    public String getSqlString(String relationalModelName) {
        IQueryService queryService = ModelerCore.getTeiidQueryService();
        IProcedureService procedureService = queryService.getProcedureService();
        return procedureService.getSQLStatement(this, relationalModelName);
    }
    
    /**
	 * Returns the current generated SQL string based on an unknown relational model name
     * @param relationalModelName 
     * @param relationalViewModelName 
     * @param virtualProcedureName 
	 * @return the generated SQL string based on the values stored on this instance
     * @since 8.6
	 */
    public String getSqlString(String relationalModelName, String relationalViewModelName, String virtualProcedureName) {
        IQueryService queryService = ModelerCore.getTeiidQueryService();
        IProcedureService procedureService = queryService.getProcedureService();
        return procedureService.getSQLStatement(this, relationalModelName, relationalViewModelName, virtualProcedureName);
    }
    
    
    public XmlElement getRootNode() {
    	return this.rootNode;
    }
    
    @Override
    public String getCommonRootPath() {
    	return this.commonRootPath;
    }
    
    @Override
    public IStatus getParsingStatus() {
    	return this.parsingStatus;
    }
    
    @Override
    public String getNamespaceString() {
    	//
    	// EXAMPLE:  XMLNAMESPACES('http://www.kaptest.com/schema/1.0/party' AS pty)
    	//
    	
    	if( this.namespaceMap.isEmpty() ) {
    		return null;
    	}
    	
    	StringBuffer sb = new StringBuffer();
    	
    	sb.append(XMLNAMESPACES).append(L_PAREN);
    	int i=0;
    	for( String prefix : this.namespaceMap.keySet() ) {
    		// Check for prefix identified by value of 0 length string on first key
    		if( i == 0 && prefix.length() == 0 ) {
    			String uri = this.namespaceMap.get(prefix);
	    		sb.append(DEFAULT).append(SPACE).append(S_QUOTE).append(uri).append(S_QUOTE);
    		} else {
	    		if( prefix.equalsIgnoreCase(XSI_NAMESPACE_PREFIX)) {
	    			continue;
	    		}
	    		if( i > 0 ) {
	    			sb.append(COMMA).append(SPACE);
	    		}
	    		String uri = this.namespaceMap.get(prefix);
	    		sb.append(S_QUOTE).append(uri).append(S_QUOTE).append(SPACE).append(AS).append(SPACE).append(prefix);
    		}
    		i++;
    	}
    	sb.append(R_PAREN).append(SPACE).append(COMMA).append(SPACE);
    	
    	return sb.toString();
    }

    private void parseXmlFile() {
    	String fileString = getFileAsString(getDataFile());
    	
    	if( parsingStatus.getSeverity() == IStatus.ERROR ) return;
    	
    	if( StringUtilities.isEmpty(fileString)) {
			String message = Util.getString("TeiidXmlFileInfo.errorXmlFileIsEmpty", getDataFile().getName()); //$NON-NLS-1$
			parsingStatus = new Status(IStatus.ERROR, UiConstants.PLUGIN_ID, message);
			return;
    	}
    	
		XmlParser xmlParser = new XmlParser();
		XmlFileContentHandler contentHandler = new XmlFileContentHandler();
		contentHandler.setDocumentLocator(new LocatorImpl());
		xmlParser.setContentHandler(contentHandler);
		
		try {
			xmlParser.doParse(fileString);
		} catch (RuntimeException ex) {
			String message = Util.getString("TeiidXmlFileInfo.parsingError", ex.getMessage()); //$NON-NLS-1$
			parsingStatus =  new Status(IStatus.ERROR, UiConstants.PLUGIN_ID, message, ex);
			return;
		} catch (IOException ex) {
			String message = Util.getString("TeiidXmlFileInfo.parsingError", ex.getMessage()); //$NON-NLS-1$
			parsingStatus =  new Status(IStatus.ERROR, UiConstants.PLUGIN_ID, message, ex);
			return;
		} catch (SAXException ex) {
			String message = Util.getString("TeiidXmlFileInfo.parsingError", ex.getMessage()); //$NON-NLS-1$
			parsingStatus =  new Status(IStatus.ERROR, UiConstants.PLUGIN_ID, message, ex);
			return;
		}
		
		this.namespaceMap.clear();
		this.namespaceMap.putAll(contentHandler.getNamespaceMap());
		
		rootNode = contentHandler.getRootElement();
		if( rootNode == null ) {
			String message = getString("noRootNodeParsingError"); //$NON-NLS-1$
			parsingStatus = new Status(IStatus.ERROR, UiConstants.PLUGIN_ID, message);
			return;
		}
		
		determineCommonRootPath();
		
		setRootPath(this.commonRootPath);

		parsingStatus = Status.OK_STATUS;
    }
    
	private void determineCommonRootPath(){
		StringBuilder commonRoot = new StringBuilder();
		
		List<String> segmentList = new ArrayList<String>();
		segmentList.add(rootNode.getFullPath());
		for( Object node : rootNode.getChildrenDTDElements() ) {
			addChildPaths((XmlElement)node, segmentList);
		}
		
		//We parse paths to get all segments. We need to find the shortest
		//path up front, since we cannot have a common root greater than
		//the shortest path.
		String[][] segments = new String[segmentList.size()][];
		int shortestPathLength = 0;
		for(int i = 0; i < segmentList.size(); i++){
			segments[i] = segmentList.get(i).split("/"); //$NON-NLS-1$
			if (i==0) shortestPathLength = segments[i].length;
			if (shortestPathLength>segments[i].length){
				shortestPathLength = segments[i].length;
			}
		}
		
		for(int j = 0; j < shortestPathLength; j++){
			String thisSegment = segments[0][j]; 
			boolean allMatched = true; 
			for(int i = 0; i < segments.length && allMatched; i++){ 
				if(segments[i].length < j){
					allMatched = false; 
					break; 
				}
				allMatched &= segments[i][j].equals(thisSegment); 
			}
			if(allMatched){ 
				commonRoot.append("/").append(thisSegment) ; //$NON-NLS-1$
			}else{
				break;
			}
		}
		//Change any double slashes to single slashes
		commonRoot = new StringBuilder(commonRoot.toString().replaceAll("//", "/")); //$NON-NLS-1$ //$NON-NLS-2$
		
		commonRootPath = commonRoot.toString();
	}
	
	private void addChildPaths(XmlElement element, List<String> segmentList ) {
		segmentList.add(element.getFullPath());
		for( Object node : element.getChildrenDTDElements() ) {
			addChildPaths((XmlElement)node, segmentList);
		}
	}

	/* (non-Javadoc)
	 * @see org.teiid.designer.query.proc.ITeiidXmlFileInfo#getParameterMap()
	 */
	@Override
	public Map<String, Object> getParameterMap() {
		return this.parameterMap;
	}

	/**
	 * @param parameterMap
	 */
	public void setParameterMap(Map<String, Object> parameterMap) {
		if( parameterMap == null ) {
			parameterMap = Collections.emptyMap();
		} else {
			this.parameterMap=parameterMap;
		}
	}
	
	private String getFileAsString(File file) {
		FileInputStream fileInputStream = null;
		BOMInputStream inputStream = null;
		String fileText = null;
		try {
			int ch;
			StringBuffer strContent = new StringBuffer("");
			fileInputStream = new FileInputStream(file);
			
			// there may be a BOM (byte order mark) so exclude them all
			inputStream = new BOMInputStream(
					fileInputStream, UTF_8, UTF_16BE, UTF_16LE, UTF_32BE, UTF_32LE);
			
			while ((ch = inputStream.read()) != -1) {
				strContent.append((char) ch);
			}

			fileText = strContent.toString();
		} catch (FileNotFoundException e) {
			String message = Util.getString("TeiidXmlFileInfo.couldNotFindXmlFile", getDataFile().getName()); //$NON-NLS-1$
			parsingStatus = new Status(IStatus.ERROR, UiConstants.PLUGIN_ID, message, e);
		} catch (IOException e) {
			String message = Util.getString("TeiidXmlFileInfo.errorReadingXmlFile", getDataFile().getName()); //$NON-NLS-1$
			parsingStatus = new Status(IStatus.ERROR, UiConstants.PLUGIN_ID, message, e);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					String message = Util.getString("TeiidXmlFileInfo.errorReadingXmlFile", getDataFile().getName()); //$NON-NLS-1$
					parsingStatus = new Status(IStatus.WARNING, UiConstants.PLUGIN_ID, message, e);
				}
			}
		}

		return fileText;
	}
}