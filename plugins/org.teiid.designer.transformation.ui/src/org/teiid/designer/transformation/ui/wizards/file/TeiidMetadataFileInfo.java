/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.ui.wizards.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.designer.core.validation.rules.StringNameValidator;
import org.teiid.designer.metamodels.relational.aspects.validation.RelationalStringNameValidator;
import org.teiid.designer.query.proc.ITeiidColumnInfo;
import org.teiid.designer.query.proc.ITeiidMetadataFileInfo;
import org.teiid.designer.transformation.ui.UiConstants;

/**
 * Business object used to manage Teiid-specific Metadata File information used during import
 * 
 *
 * @since 8.0
 */
@SuppressWarnings("javadoc")
public class TeiidMetadataFileInfo extends TeiidFileInfo implements UiConstants, ITeiidMetadataFileInfo {
	private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(TeiidMetadataFileInfo.class);
	
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
     * include the NO TRIM parameter in the view tables's SQL string
     */
	private boolean includeNoTrim = false;
	
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
	private String delimiter = DEFAULT_DELIMITER;
	
	/**
     * The unique quote character used in the Teiid metadata file to surround complex column data values.
     * (never <code>null</code> or empty)
     * 
     */
	private String quote = DEFAULT_QUOTE;
	
	/**
     * The unique escape character used in the Teiid metadata file to surround complex column data values.
     * (never <code>null</code> or empty)
     * 
     */
	private String escape = DEFAULT_ESCAPE;
	
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
	 * The cached <code>Collection</code> of the first 6 lines to use for UI display purposes
	 */
	private String[] cachedFirstLines;
	
	/**
	 * The  <code>Collection</code> of <code>TeiidColumnInfo</code> objects parsed from the defined header information.
	 */
	private List<ITeiidColumnInfo> columnInfoList;
	
	private boolean ignoreReload = false;
 
	private String charset = "UTF-8";  //$NON-NLS-1$
	
	/**
	 * 
	 * @param dataFile the Teiid-formatted data file
	 */
	public TeiidMetadataFileInfo(File dataFile) {
		super(dataFile, true);
		CoreArgCheck.isNotNull(dataFile, "dataFile is null"); //$NON-NLS-1$

		initialize();
	}
	
	/**
	 * 
	 * @param dataFile the Teiid-formatted data file
	 * @param charset charset
	 */
	public TeiidMetadataFileInfo(File dataFile, String charset) {
	    super(dataFile, true);

	    CoreArgCheck.isNotNull(dataFile, "dataFile is null"); //$NON-NLS-1$
	    if(charset != null && charset.length() > 0) {
	        this.charset = charset;
	    }
	    
	    initialize();
	}
	
	/**
	 * 
	 * @param info the data file info object
	 */
	public TeiidMetadataFileInfo(TeiidMetadataFileInfo info) {
		super(info.getDataFile(), true);
		
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
		this.includeNoTrim = info.doIncludeNoTrim();
		this.cachedFirstLines = info.cachedFirstLines;
		this.numberOfLinesInFile = info.getNumberOfLinesInFile();
		this.columnInfoList = new ArrayList<ITeiidColumnInfo>();
		for( ITeiidColumnInfo colInfo : info.getColumnInfoList() ) {
			this.columnInfoList.add(new TeiidColumnInfo(colInfo.getSymbolName(), colInfo.getDatatype(), colInfo.getWidth()));
		}
		
		setViewTableName(info.getViewTableName());
		
		setStatus(info.getStatus());
		
		ignoreReload = false;
	}
	
	private void initialize() {
		setStatus(Status.OK_STATUS);
		this.cachedFirstLines = new String[0];
		this.columnInfoList = new ArrayList<ITeiidColumnInfo>();
		
		loadHeader();
		
		String fileName = getDataFile().getName();
		if(fileName.toLowerCase().endsWith(".txt")) { //$NON-NLS-1$
			fileName = fileName.substring(0, fileName.length()-4);
		}
		setViewTableName(fileName + "View"); //$NON-NLS-1$
	}

	/**
	 * 
	 * @return headerLineNumber the data file's line number containing the header info
	 */
	@Override
    public int getHeaderLineNumber() {
		return this.headerLineNumber;
	}

	/**
	 * 
	 * @param headerLineNumber the header line number
	 */
	public void setHeaderLineNumber(int headerLineNumber) {
		CoreArgCheck.isPositive(headerLineNumber, "header line number is less than zero"); //$NON-NLS-1$
		
		this.headerLineNumber = headerLineNumber;
		
		if( ignoreReload ) return;
		
		if( delimitedColumns ) {
			defineColumns();
		}
	}

	/**
	 * 
	 * @return firstDataRow the line number of first data row
	 */
	@Override
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
	@Override
    public String getDelimiter() {
		return this.delimiter;
	}

	/**
	 * 
	 * @param delimiter the data file's delimiter character 
	 */
	public void setDelimiter(String delimiter) {
		CoreArgCheck.isNotEmpty("" + delimiter, "delimiter is null"); //$NON-NLS-1$ //$NON-NLS-2$
		
		if( delimiter.equals(this.delimiter) ) {
			return;
		}
		
		this.delimiter = delimiter;
		
		if ( ignoreReload ) return;
		
		if( delimitedColumns ) {
			defineColumns();
		}
	}
	
	/**
	 * 
	 * @param quote the data file's quote character
	 */
	public void setQuote(String quote) {
		CoreArgCheck.isNotEmpty("" + quote, "quote is null"); //$NON-NLS-1$ //$NON-NLS-2$
		
		this.quote = quote;
	}
	
	/**
	 * 
	 * @return the data file's quote character
	 */
	@Override
    public String getQuote() {
		return this.quote;
	}
	
	/**
	 * 
	 * @param escape the data file's escape character
	 */
	public void setEscape(String escape) {
		CoreArgCheck.isNotEmpty("" + escape, "escape is null"); //$NON-NLS-1$ //$NON-NLS-2$
		
		this.escape = escape;
	}
	
	/**
	 * 
	 * @return the data file's escape character
	 */
	@Override
    public String getEscape() {
		return this.escape;
	}
	
	/**
	 * @return the header string
	 */
	@Override
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
		
        if(getDataFile() != null && getDataFile().exists()){
            FileInputStream stream = null;
            InputStreamReader reader = null;
            BufferedReader in=null;

            try{
            	int iLines = 0;
                stream = new FileInputStream(getDataFile());
                reader = new InputStreamReader(stream, this.charset);
                in = new BufferedReader(reader);                
                String str;
                while ((str = in.readLine()) != null) {
                	Collection<String> strings = getRowsFromLine(str);
                	iLines = iLines + strings.size();
                	if( iLines <= numberOfCachedLines ) {
                		lines.addAll(strings);
                	}
                }
                this.numberOfLinesInFile = iLines;
                
                this.cachedFirstLines = lines.toArray(new String[0]);
            }catch(Exception e){
            	Util.log(IStatus.ERROR, e, 
                		Util.getString(I18N_PREFIX + "problemLoadingFileContentsMessage", getDataFile().getName())); //$NON-NLS-1$
            }
            finally{
                try{
                    stream.close();
                }catch(java.io.IOException e){}
                try{
                    reader.close();
                }catch(java.io.IOException e){}
                try{
                    in.close();
                }catch(java.io.IOException e){}
            }
        }
        
		if( delimitedColumns ) {
			defineColumns();
		}
	}
	
	private Collection<String> getRowsFromLine(String str) {
		Collection<String> strings = new ArrayList<String>();
		String theString = str;
		
		while( theString.length() > 0 ) {
			if ( (theString.indexOf(CR_1) > -1 || theString.indexOf(CR_2) > -1) ) {
				int lf_1_index = theString.indexOf(CR_1);
				int lf_2_index = theString.indexOf(CR_2);
				if( lf_1_index > -1 && lf_2_index > lf_1_index) {
					// first linefeed is "\r\n"
					String seg = theString.substring(0, lf_1_index);
					strings.add(seg);
					int strLen = theString.length();
					int lengthLeft = (strLen-1) - (lf_1_index + 4);
					if( lengthLeft > 0 ) {
						theString = theString.substring((lf_1_index + 4), (strLen)).trim();
					} else {
						theString = EMPTY_STR;
					}
				} else if( lf_2_index > -1 ) {
					// first linefeed is "\r\n"
					String seg = theString.substring(0, lf_2_index);
					strings.add(seg);
					int strLen = theString.length();
					int lengthLeft = (strLen-1) - (lf_2_index + 2);
					if( lengthLeft > 0 ) {
						theString = theString.substring((lf_2_index + 2), (strLen)).trim();
					} else {
						
					}
				}
			} else {
				strings.add(theString);
				theString = EMPTY_STR;
			}
		}
		
		return strings;
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
	 * @return columnInfoList the <code>TeiidColumnInfo[]</code> array parsed from the header in the data file
	 */
	@Override
    public List<ITeiidColumnInfo> getColumnInfoList() {
		return this.columnInfoList;
	}
	
	public void removeColumn(ITeiidColumnInfo theInfo) {
		this.columnInfoList.remove(theInfo);
		validate();
	}
	
	public void addColumn(TeiidColumnInfo theInfo) {
		this.columnInfoList.add(theInfo);
		validate();
	}
	
	public void clearColumns() {
		this.columnInfoList.clear();
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
				if( nextTok != null && nextTok.length() > 0 ) {
					this.columnInfoList.add(new TeiidColumnInfo(nextTok));
				}
			}
		} else {
//			for( int i=0; i<this.numberOfFixedWidthColumns; i++ ) {
//				String colName = "col" + (i+1); //$NON-NLS-1$
//				this.columnInfoList.add(new TeiidColumnInfo(colName));
//			}
		}
		validate();
	}
	
	@Override
	public void validate() {
		if( this.useHeaderForColumnNames ) {
			if( this.getHeaderString() == null || this.getHeaderString().length() == 0 ) {
				setStatus(new Status(IStatus.ERROR, PLUGIN_ID, getString("status.noHeaderFound"))); //$NON-NLS-1$
				return;
			}
		}
		
		if( this.columnInfoList.size() == 1 ) {
			// COULD HAVE ONE COLUMN, SO VALIDATE
			String message = TeiidMetadataFileInfo.validator.checkValidName(this.columnInfoList.iterator().next().getSymbolName());
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
	@Override
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
	@Override
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
	@Override
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
	@Override
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
	@Override
    public boolean doIncludeQuote() {
		return this.includeQuote;
	}
	
	/**
	 * 
	 * @param includeNoTrim the boolean indicator that the generated view table SQL should include the NO TRIM parameter
	 */
	public void setIncludeNoTrim(boolean includeNoTrim) {
		this.includeNoTrim = includeNoTrim;
	}
	
	/**
	 * 
	 * @return includeNoTrim the boolean indicator that the generated view table SQL should include the NO TRIM parameter
	 */
	@Override
    public boolean doIncludeNoTrim() {
		return this.includeNoTrim;
	}
	
	/**
	 * 
	 * @param useHeaderForColumnNames the boolean indicator that the data file contains a header with column names and should 
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
	@Override
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

//		if( ignoreReload ) return;
//		
//		if( delimitedColumns ) {
//			defineColumns();
//		}
	}
	
	/**
	 * 
	 * @return fixedWidthColumns the boolean indicator that the data file contains a header with column names
	 */
	@Override
    public boolean isFixedWidthColumns() {
		return this.fixedWidthColumns;
	}
	
	/**
	 * 
	 * @param numberOfFixedWidthColumns the number of fixed with columns
	 */
	public void setNumberOfFixedWidthColumns(int numberOfFixedWidthColumns) {
		this.numberOfFixedWidthColumns = numberOfFixedWidthColumns;
		
		if( ignoreReload ) return;
		
		if( delimitedColumns ) {
			defineColumns();
		}
	}
	
	/**
	 * 
	 * @return numberOfFixedWidthColumns the number of fixed with columns
	 */
	@Override
    public int getNumberOfFixedWidthColumns() {
		return this.numberOfFixedWidthColumns;
	}

	
	/**
	 * 
	 * @param delimitedColumns the boolean indicator that the data file contains columns separated by a delimiter
	 */
	public void setUseDelimitedColumns(boolean delimitedColumns) {
		boolean oldValue = this.delimitedColumns;
		if( oldValue != delimitedColumns ) {
			this.delimitedColumns = delimitedColumns;
			
			this.useHeaderForColumnNames = !delimitedColumns;
			this.fixedWidthColumns = !delimitedColumns;
		}
	}
	
	/**
	 * 
	 * @return delimitedColumns the boolean indicator that the data file contains columns separated by a delimiter
	 */
	@Override
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
	
	public void moveColumnUp(TeiidColumnInfo columnInfo) {
		int startIndex = getColumnIndex(columnInfo);
		
		// 
		if( startIndex > 0 ) {
			// Make Copy of List & get columnInfo of startIndex-1
			ITeiidColumnInfo priorInfo = getColumnInfoList().get(startIndex-1);
			ITeiidColumnInfo[] infos = getColumnInfoList().toArray(new ITeiidColumnInfo[0]);
			infos[startIndex-1] = columnInfo;
			infos[startIndex] = priorInfo;
			
			List<ITeiidColumnInfo> colInfos = new ArrayList<ITeiidColumnInfo>(infos.length);
			for( ITeiidColumnInfo info : infos) {
				colInfos.add(info);
			}
			
			this.columnInfoList = colInfos;
		}
	}
	
	public void moveColumnDown(TeiidColumnInfo columnInfo) {
		int startIndex = getColumnIndex(columnInfo);
		if( startIndex < (getColumnInfoList().size()-1) ) {
			// Make Copy of List & get columnInfo of startIndex-1
			ITeiidColumnInfo afterInfo = getColumnInfoList().get(startIndex+1);
			ITeiidColumnInfo[] infos = getColumnInfoList().toArray(new ITeiidColumnInfo[0]);
			infos[startIndex+1] = columnInfo;
			infos[startIndex] = afterInfo;
			
			List<ITeiidColumnInfo> colInfos = new ArrayList<ITeiidColumnInfo>(infos.length);
			for( ITeiidColumnInfo info : infos) {
				colInfos.add(info);
			}
			
			this.columnInfoList = colInfos;
		}
	}
	
	public boolean canMoveUp(ITeiidColumnInfo columnInfo) {
		return getColumnIndex(columnInfo) > 0;
	}
	
	public boolean canMoveDown(ITeiidColumnInfo columnInfo) {
		return getColumnIndex(columnInfo) < getColumnInfoList().size()-1;
	}
	
	private int getColumnIndex(ITeiidColumnInfo columnInfo) {
		int i=0;
		for( ITeiidColumnInfo colInfo : getColumnInfoList() ) {
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
        text.append(", delimiter = ").append(getDelimiter()); //$NON-NLS-1$
        text.append(", header line # = ").append(getHeaderLineNumber()); //$NON-NLS-1$
        text.append(", view table name = ").append(getViewTableName()); //$NON-NLS-1$
        
        return text.toString();
    }
    
    /**
     * Parse the supplied row string from data file and return an array of strings the column values from the parsed data row
     * 
     * @param rowString
     * @return 
     */
    public String[] parseRow(String rowString) {
    	Collection<String> values = new ArrayList<String>();
    	String leftOver = rowString;
		if( this.fixedWidthColumns ) {
			for( ITeiidColumnInfo columnInfo : getColumnInfoList()) {
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
    
	/**
	 * Returns the current generated SQL string based on an unknown relational model name
	 * @return the generated SQL string
	 */
    @Override
    public String getSqlStringTemplate() {
    	return getSqlString("myRelModel"); //$NON-NLS-1$
    }
    
    @Override
    public String getSqlString(String relationalModelName) {
    	/*
    	 * 
    	 * TEXTTABLE(expression COLUMNS <COLUMN>, ... [DELIMITER char] [(QUOTE|ESCAPE) char] [HEADER [integer]] [SKIP integer]) AS name
    	 * 
    	 * DELIMITER sets the field delimiter character to use. Defaults to ','.
    	 * 
    	 * QUOTE sets the quote, or qualifier, character used to wrap field values. Defaults to '"'.
    	 * 
    	 * ESCAPE sets the escape character to use if no quoting character is in use. This is used in situations where the delimiter or new line characters are escaped with a preceding character, e.g. \
    	 * 
    	 * 
			SELECT A.lastName, A.firstName, A.middleName, A.AId FROM
        (EXEC EmployeeData.getTextFiles('EmployeeData.txt')) AS f, TEXTTABLE(file COLUMNS lastName string, firstName string, middleName string, HEADER 3) AS A
    	 
    	 *
    	 * SELECT {0} FROM (EXEC {1}.getTextFiles({2})) AS f, TEXTTABLE(file COLUMNS {3}  HEADER {4}) AS {5}
    	 */
    	String alias = "A"; //$NON-NLS-1$
    	StringBuffer sb = new StringBuffer();
    	int i=0;
    	int nColumns = getColumnInfoList().length;
    	for( TeiidColumnInfo columnStr : getColumnInfoList()) {
    		sb.append(alias).append(DOT).append(columnStr.getSymbolName());
    		
    		if(i < (nColumns-1)) {
    			sb.append(COMMA).append(SPACE);
    		}
    		i++;
    	}
    	String string_0 = sb.toString();
    	
    	sb = new StringBuffer();
    	i=0;
    	for( TeiidColumnInfo columnStr : getColumnInfoList()) {
    		sb.append(columnStr.getSymbolName()).append(SPACE).append(columnStr.getDatatype());
			if( isFixedWidthColumns()) {
				sb.append(SPACE).append(WIDTH).append(SPACE).append(Integer.toString(columnStr.getWidth()));
			}
    		if(i < (nColumns-1)) {
    			sb.append(COMMA).append(SPACE);
    		}

    		i++;
    	}
    	String string_2 = S_QUOTE + getDataFile().getName() + S_QUOTE;
    	String string_3 = sb.toString();
    	
    	sb = new StringBuffer();
    	
    	if( doUseDelimitedColumns() && getDelimiter() != TeiidMetadataFileInfo.DEFAULT_DELIMITER ) {
    		sb.append("DELIMITER"); //$NON-NLS-1$
    		sb.append(SPACE).append('\'').append(getDelimiter()).append('\'');
    	}
    	
    	if( doIncludeQuote() ) {
    		if( getQuote() != TeiidMetadataFileInfo.DEFAULT_QUOTE) {
	    		sb.append("QUOTE"); //$NON-NLS-1$
	    		sb.append(SPACE).append('\'').append(getQuote()).append('\'');
    		}
    	} else if(doIncludeEscape() ) {
    		if( getEscape() != TeiidMetadataFileInfo.DEFAULT_ESCAPE) {
	    		sb.append("ESCAPE"); //$NON-NLS-1$
	    		sb.append(SPACE).append('\'').append(getEscape()).append('\'');
    		}
    	}
    	
    	if( doIncludeHeader() ) {
    		sb.append(SPACE).append("HEADER"); //$NON-NLS-1$
    		if( getHeaderLineNumber() > 1 ) {
    			sb.append(SPACE).append(Integer.toString(getHeaderLineNumber()));
    		}
    	}
    	if( getFirstDataRow() > 1 && (doIncludeSkip() || this.isFixedWidthColumns()) ) {
    		sb.append(SPACE).append("SKIP"); //$NON-NLS-1$
    		sb.append(SPACE).append(Integer.toString(getFirstDataRow()-1));
    	}
    	
    	if( doIncludeNoTrim() && getFirstDataRow() > 1 ) {
    		sb.append(SPACE).append("NO TRIM"); //$NON-NLS-1$
    	}
    	String string_4 = sb.toString();
    	
    	String finalSQLString = UiConstants.Util.getString(
    			"TeiidMetadataFileInfo.textTableSqlTemplate", //$NON-NLS-1$
    			string_0,
    			relationalModelName,
    			string_2,
    			string_3,
    			string_4,
    			alias);
    	
    	return finalSQLString;
    	
    }
    
    public String getCharset() {
        return this.charset;
    }
    
}
