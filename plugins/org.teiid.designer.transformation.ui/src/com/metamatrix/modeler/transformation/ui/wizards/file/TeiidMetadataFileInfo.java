/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.ui.wizards.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.metamodels.relational.aspects.validation.RelationalStringNameValidator;
import com.metamatrix.modeler.core.validation.rules.StringNameValidator;
import com.metamatrix.modeler.transformation.ui.UiConstants;


/**
 * Business object used to manage Teiid-specific Metadata File information used during import
 * 
 */
public class TeiidMetadataFileInfo implements UiConstants {
	private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(TeiidMetadataFileInfo.class);
	
	public static final char DEFAULT_DELIMITER = ',';
	public static final char COMMA = ',';
	public static final char SPACE = ' ';
	public static final char SEMICOLON = ':';
	public static final char TAB = '\t';
	public static final char BAR = '|';
	public static final char DEFAULT_QUOTE= '"';
	public static final char DEFAULT_ESCAPE= '\\';
	public static final int DEFAULT_HEADER_LINE_NUMBER = 1;

	private static final StringNameValidator validator = new RelationalStringNameValidator(false, true);
	
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
     * The unique data file name containing Teiid-formatted relational table data (never <code>null</code> or empty).
     */
	private File dataFile;
	
    /**
     * The unique view table name containing the generated SELECT SQL statement that converts file data into
     * relational columns (never <code>null</code> or empty).
     */
	private String viewTableName;
	
	/**
     * indicator that the data file includes a header containing column names and the header line number should be
     * inserted in the view tables's SQL string
     */
	private boolean includeHeader = true;
	
	/**
     * include the QUOTE parameter in the view tables's SQL string
     */
	private boolean includeQuote = false;
	
	/**
     * include the ESCAPE parameter in the view tables's SQL string
     */
	private boolean includeEscape = false;
	
	/**
     * include the SKIP parameter in the view tables's SQL string
     */
	private boolean includeSkip = false;
	
	/**
     * indicator that the data file includes a header containing column names.
     */
	private boolean useHeaderForColumnNames = true;
	
    /**
     * The line number of the header containing column names. Must be 1 or greater.
     */
	private int headerLineNumber = DEFAULT_HEADER_LINE_NUMBER;
	
	/**
     * The line number of first data row.
     */
	private int firstDataRow = 2;
	
	/**
     * indicator that the data file contains columns that are delimited by special character.
     */
	private boolean delimitedColumns = true;
	
	/**
     * The unique delimiter used in the Teiid metadata file separating the column names and the column data values.
     * (never <code>null</code> or empty)
     * 
     */
	private char delimiter = DEFAULT_DELIMITER;
	
	/**
     * The unique quote character used in the Teiid metadata file to surround complex column data values.
     * (never <code>null</code> or empty)
     * 
     */
	private char quote = DEFAULT_QUOTE;
	
	/**
     * The unique escape character used in the Teiid metadata file to surround complex column data values.
     * (never <code>null</code> or empty)
     * 
     */
	private char escape = DEFAULT_ESCAPE;
	
	/**
     * indicator that the data file contains columns with fixed widths.
     */
	private boolean fixedWidthColumns = false;
	
	/**
     * The number of fixed width columns
     */
	private int numberOfFixedWidthColumns = 10;
	
	/**
	 * Indicator for the import processor to attempt to create a View Table given the info in this object.
	 */
	private boolean doProcess;
	
	/**
	 * Current <code>IStatus</code> representing the state of the input values for this instance of
	 * <code>TeiidMetadataFileInfo</code>
	 */
	private IStatus status;
	
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
	
	private boolean ignoreReload = false;

	/**
	 * 
	 * @param dataFile the Teiid-formatted data file
	 */
	public TeiidMetadataFileInfo(File dataFile) {
		super();
		CoreArgCheck.isNotNull(dataFile, "dataFile is null"); //$NON-NLS-1$
		
		this.dataFile = dataFile;
		initialize();
	}
	
	/**
	 * 
	 * @param info the data file info object
	 */
	public TeiidMetadataFileInfo(TeiidMetadataFileInfo info) {
		super();
		
		inject(info);
	}
	
	/**
	 * This method allows setting the values in the current info object using the values from an external info object
	 * 
	 * @param info the data file info object
	 */
	public void inject(TeiidMetadataFileInfo info) {
		CoreArgCheck.isNotNull(info.getDataFile(), "dataFile is null"); //$NON-NLS-1$
		CoreArgCheck.isNotEmpty("" + info.getDelimiter(), "delimiter is null"); //$NON-NLS-1$ //$NON-NLS-2$
		CoreArgCheck.isPositive(info.getHeaderLineNumber(), "header line number is less than zero"); //$NON-NLS-1$
		
		ignoreReload = true;
		
		this.dataFile = info.getDataFile();
		
		this.delimiter = info.getDelimiter();
		this.quote = info.getQuote();
		this.numberOfFixedWidthColumns = info.getNumberOfFixedWidthColumns();
		
		this.headerLineNumber = info.getHeaderLineNumber();
		this.fixedWidthColumns = info.isFixedWidthColumns();
		this.delimitedColumns = info.doUseDelimitedColumns();
		
		this.useHeaderForColumnNames = info.doUseHeaderForColumnNames();

		this.firstDataRow = info.getFirstDataRow();
		
		this.includeEscape = info.doIncludeEscape();
		this.includeHeader = info.doIncludeHeader();
		this.includeQuote = info.doIncludeQuote();
		this.includeSkip = info.doIncludeSkip();
		this.cachedFirstLines = info.cachedFirstLines;
		this.numberOfLinesInFile = info.getNumberOfLinesInFile();
		this.columnInfoList = new ArrayList<TeiidColumnInfo>();
		for( TeiidColumnInfo colInfo : info.getColumnInfoList() ) {
			this.columnInfoList.add(new TeiidColumnInfo(colInfo.getName(), colInfo.getDatatype(), colInfo.getWidth()));
		}
		
		this.viewTableName = info.getViewTableName();
		
		this.status = info.getStatus();
		
		ignoreReload = false;
	}
	
	private void initialize() {
		this.status = Status.OK_STATUS;
		this.columnDatatypeMap = new HashMap<String, String>();
		this.cachedFirstLines = new String[0];
		this.columnInfoList = new ArrayList<TeiidColumnInfo>();
		
		loadHeader();
		
		String fileName = dataFile.getName();
		if(fileName.toLowerCase().endsWith(".txt")) { //$NON-NLS-1$
			fileName = fileName.substring(0, fileName.length()-4);
		}
		setViewTableName(fileName + "View"); //$NON-NLS-1$
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
	}
	
	/**
	 * 
	 * @param columnName the column name (never <code>null</code> or empty).
	 * @return datatype the column datatype (will not be null)
	 */
	public String getDatatype(String columnName) {
		return this.columnDatatypeMap.get(columnName);
	}
	
	/**
	 * 
	 * @return viewTableName the view table name (never <code>null</code> or empty).
	 */
	public String getViewTableName() {
		return this.viewTableName;
	}

	/**
	 * 
	 * @param viewTableName (never <code>null</code> or empty).
	 */
	public void setViewTableName(String viewTableName) {
		CoreArgCheck.isNotEmpty(viewTableName, "viewTableName is null"); //$NON-NLS-1$
		
		this.viewTableName = viewTableName;
	}

	/**
	 * 
	 * @return headerLineNumber the data file's line number containing the header info
	 */
	public int getHeaderLineNumber() {
		return this.headerLineNumber;
	}

	/**
	 * 
	 * @param headerLineNumber
	 */
	public void setHeaderLineNumber(int headerLineNumber) {
		CoreArgCheck.isPositive(headerLineNumber, "header line number is less than zero"); //$NON-NLS-1$
		
		this.headerLineNumber = headerLineNumber;
		
		if( ignoreReload ) return;
		
		defineColumns();
	}

	/**
	 * 
	 * @return firstDataRow the line number of first data row
	 */
	public int getFirstDataRow() {
		return this.firstDataRow;
	}

	/**
	 * 
	 * @param firstDataRow the line number of first data row
	 */
	public void setFirstDataRow(int firstDataRow) {
		CoreArgCheck.isPositive(firstDataRow, "firstDataRow is not greater than zero"); //$NON-NLS-1$
		
		this.firstDataRow = firstDataRow;
	}
	
	/**
	 * 
	 * @return the data file's delimiter character 
	 */
	public char getDelimiter() {
		return this.delimiter;
	}

	/**
	 * 
	 * @param delimiter the data file's delimiter character 
	 */
	public void setDelimiter(char delimiter) {
		CoreArgCheck.isNotEmpty("" + delimiter, "delimiter is null"); //$NON-NLS-1$ //$NON-NLS-2$
		
		this.delimiter = delimiter;
		
		if ( ignoreReload ) return;
		
		defineColumns();
	}
	
	/**
	 * 
	 * @param quote the data file's quote character
	 */
	public void setQuote(char quote) {
		CoreArgCheck.isNotEmpty("" + quote, "quote is null"); //$NON-NLS-1$ //$NON-NLS-2$
		
		this.quote = quote;
	}
	
	/**
	 * 
	 * @return the data file's quote character
	 */
	public char getQuote() {
		return this.quote;
	}
	
	/**
	 * 
	 * @param escape the data file's escape character
	 */
	public void setEscape(char escape) {
		CoreArgCheck.isNotEmpty("" + escape, "escape is null"); //$NON-NLS-1$ //$NON-NLS-2$
		
		this.escape = escape;
	}
	
	/**
	 * 
	 * @return the data file's escape character
	 */
	public char getEscape() {
		return this.escape;
	}
	/**
	 * 
	 * @return dataFile the teiid-formatted data <code>File</code>
	 */
	public File getDataFile() {
		return this.dataFile;
	}
	
	public String getHeaderString() {
		if( cachedFirstLines.length == 0 ) {
			return null;
		}
		if( headerLineNumber <= this.cachedFirstLines.length) {
			return this.cachedFirstLines[this.headerLineNumber-1];
		}
		
		return null;
	}

	private void loadHeader() {
		this.cachedFirstLines = new String[0];
		Collection<String> lines = new ArrayList<String>(7);
		
        if(this.dataFile != null && this.dataFile.exists()){
            FileReader fr=null;
            BufferedReader in=null;

            try{
            	int iLines = 0;
                fr=new FileReader(this.dataFile);
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
                		Util.getString(I18N_PREFIX + "problemLoadingFileContentsMessage", this.dataFile.getName())); //$NON-NLS-1$
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
        
        defineColumns();
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
	
	/*
	 * This method should be called when any option is changed in this configuration that impacts column info
	 * 
	 * DELIMITED VS FIXED WIDTH COLUMNS
	 * numberOfFixedWidthColumns
	 * useHeaderForColumnNames
	 */
	private void defineColumns() {
		this.columnInfoList.clear();
		
		if( this.useHeaderForColumnNames && getHeaderString() != null && getHeaderString().length() > 0 ) {
			String delim = "" + getDelimiter(); //$NON-NLS-1$
			StringTokenizer strTokeniser = new StringTokenizer(getHeaderString(), delim);
			while( strTokeniser.hasMoreTokens() ) {
				String nextTok = strTokeniser.nextToken().trim();
				// Check for d_quoted column names
				if( nextTok.startsWith("" + getQuote()) ) { //$NON-NLS-1$
					nextTok = nextTok.substring(1, nextTok.length()-1);
				}
				
				this.columnInfoList.add(new TeiidColumnInfo(nextTok));
			}
		} else {
			for( int i=0; i<this.numberOfFixedWidthColumns; i++ ) {
				String colName = "col" + (i+1); //$NON-NLS-1$
				this.columnInfoList.add(new TeiidColumnInfo(colName));
			}
		}
		validate();
	}
	
	private void validate() {
		if( this.useHeaderForColumnNames ) {
			if( this.getHeaderString() == null || this.getHeaderString().length() == 0 ) {
				setStatus(new Status(IStatus.ERROR, PLUGIN_ID, getString("status.noHeaderFound"))); //$NON-NLS-1$
				return;
			}
		}
		
		if( this.columnInfoList.size() == 1 ) {
			// COULD HAVE ONE COLUMN, SO VALIDATE
			String message = TeiidMetadataFileInfo.validator.checkValidName(this.columnInfoList.iterator().next().getName());
			if( message != null ) {
				setStatus(new Status(IStatus.ERROR, PLUGIN_ID, getString("status.noHeaderFound"))); //$NON-NLS-1$
				return;
			}
		}
		
		// Check that if Skipped lines > 0 && useHeader == TRUE that skippedLines > headerLineNumber
		if( this.includeHeader && this.firstDataRow > 0 ) {
			if( this.firstDataRow <= headerLineNumber ) {
				setStatus(new Status(IStatus.ERROR, PLUGIN_ID, 
						Util.getString(I18N_PREFIX + "status.skippedLinesNotGreaterThanHeader", this.firstDataRow, this.headerLineNumber))); //$NON-NLS-1$
				return;
			}
		}
		
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
	 * @param includeHeader the boolean indicator that the generated view table SQL should include the HEADER parameter
	 */
	public void setIncludeHeader(boolean includeHeader) {
		this.includeHeader = includeHeader;
	}
	
	/**
	 * 
	 * @return includeHeader the boolean indicator that the generated view table SQL should include the HEADER parameter
	 */
	public boolean doIncludeHeader() {
		return this.includeHeader;
	}
	
	/**
	 * 
	 * @param includeSkip the boolean indicator that the generated view table SQL should include the SKIP parameter
	 */
	public void setIncludeSkip(boolean includeSkip) {
		this.includeSkip = includeSkip;
	}
	
	/**
	 * 
	 * @return includeSkip the boolean indicator that the generated view table SQL should include the SKIP parameter
	 */
	public boolean doIncludeSkip() {
		return this.includeSkip;
	}
	
	/**
	 * 
	 * @param includeEscape the boolean indicator that the generated view table SQL should include the SKIP parameter
	 */
	public void setIncludeEscape(boolean includeEscape) {
		if( this.delimitedColumns ) {
			
			this.includeEscape = includeEscape;
			if( this.includeEscape && this.includeQuote ) {
				this.includeQuote = false;
			}
		} else {
			this.includeEscape = false;
		}
	}
	
	/**
	 * 
	 * @return includeEscape the boolean indicator that the generated view table SQL should include the SKIP parameter
	 */
	public boolean doIncludeEscape() {
		return this.includeEscape;
	}
	
	/**
	 * 
	 * @param includeQuote the boolean indicator that the generated view table SQL should include the QUOTE parameter
	 */
	public void setIncludeQuote(boolean includeQuote) {
		if( this.delimitedColumns ) {
			this.includeQuote = includeQuote;
			if( this.includeQuote && this.includeEscape ) {
				this.includeEscape = false;
			}
		} else {
			this.includeQuote = false;
		}
	}
	
	/**
	 * 
	 * @return includeQuote the boolean indicator that the generated view table SQL should include the QUOTE parameter
	 */
	public boolean doIncludeQuote() {
		return this.includeQuote;
	}
	
	/**
	 * 
	 * @param includeHeader the boolean indicator that the data file contains a header with column names and should 
	 * be used to load column names
	 */
	public void setDoUseHeaderForColumnNames(boolean useHeaderForColumnNames) {
		this.useHeaderForColumnNames = useHeaderForColumnNames;
	}
	
	/**
	 * 
	 * @return useHeader the boolean indicator that the data file contains a header with column names and should 
	 * be used to load column names
	 */
	public boolean doUseHeaderForColumnNames() {
		return this.useHeaderForColumnNames;
	}
	/**
	 * 
	 * @param fixedWidthColumns the boolean indicator that the data file contains a header with column names
	 */
	public void setFixedWidthColumns(boolean fixedWidthColumns) {
		this.fixedWidthColumns = fixedWidthColumns;

		this.delimitedColumns = !fixedWidthColumns;
		
		if( fixedWidthColumns ) {
			this.includeHeader = false;
			this.useHeaderForColumnNames = false;
		}

		if( ignoreReload ) return;
		
		defineColumns();
	}
	
	/**
	 * 
	 * @return fixedWidthColumns the boolean indicator that the data file contains a header with column names
	 */
	public boolean isFixedWidthColumns() {
		return this.fixedWidthColumns;
	}
	
	/**
	 * 
	 * @param fixedWidthColumns the number of fixed with columns
	 */
	public void setNumberOfFixedWidthColumns(int numberOfFixedWidthColumns) {
		this.numberOfFixedWidthColumns = numberOfFixedWidthColumns;
		
		if( ignoreReload ) return;
		
		defineColumns();
	}
	
	/**
	 * 
	 * @return numberOfFixedWidthColumns the number of fixed with columns
	 */
	public int getNumberOfFixedWidthColumns() {
		return this.numberOfFixedWidthColumns;
	}

	
	/**
	 * 
	 * @param delimitedColumns the boolean indicator that the data file contains columns separated by a delimiter
	 */
	public void setUseDelimitedColumns(boolean delimitedColumns) {
		this.delimitedColumns = delimitedColumns;
		
		this.useHeaderForColumnNames = !delimitedColumns;
		this.fixedWidthColumns = !delimitedColumns;
	}
	
	/**
	 * 
	 * @return delimitedColumns the boolean indicator that the data file contains columns separated by a delimiter
	 */
	public boolean doUseDelimitedColumns() {
		return this.delimitedColumns;
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
        text.append(", delimiter = ").append(getDelimiter()); //$NON-NLS-1$
        text.append(", header line # = ").append(getHeaderLineNumber()); //$NON-NLS-1$
        text.append(", view table name = ").append(getViewTableName()); //$NON-NLS-1$
        
        return text.toString();
    }
    
    /**
     * Parse the supplied row string from data file and return an array of strings the colum n values from the parsed data row
     * 
     * @param rowString
     * @return 
     */
    public String[] parseRow(String rowString) {
    	Collection<String> values = new ArrayList<String>();
    	String leftOver = rowString;
		if( this.fixedWidthColumns ) {
			for( TeiidColumnInfo columnInfo : getColumnInfoList()) {
				int width = columnInfo.getWidth();
				if( leftOver.length() >= width ) {
					String value = leftOver.substring(0, width-1);
					values.add(value);
					leftOver = leftOver.substring(width);
				} else {
					if( leftOver.length() > 0 ) {
						values.add(leftOver);
					}
					break;
				}
			}
		} else {
			String delim = "" + getDelimiter(); //$NON-NLS-1$
			StringTokenizer strTokeniser = new StringTokenizer(rowString, delim);
			while( strTokeniser.hasMoreTokens() ) {
				String value = strTokeniser.nextToken().trim();
				// Check for d_quoted column names
				if( value.startsWith("" + getQuote()) ) { //$NON-NLS-1$
					value = value.substring(1, value.length()-1);
				}
				values.add(value);
			}
		}
		
		return values.toArray(new String[values.size()]);
    }

}
