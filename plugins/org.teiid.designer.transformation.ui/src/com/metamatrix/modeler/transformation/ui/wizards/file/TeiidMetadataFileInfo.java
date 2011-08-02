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
	public static final int DEFAULT_HEADER_LINE_NUMBER = 1;

	private static final StringNameValidator validator = new RelationalStringNameValidator(false, true);
	
    private static String getString( final String id ) {
        return Util.getString(I18N_PREFIX + id);
    }
    
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
     * The line number of the header containing column names. Must be 1 or greater.
     */
	private int headerLineNumber = DEFAULT_HEADER_LINE_NUMBER;
	
	/**
     * The unique delimiter used in the Teiid metadata file separating the column names and the column data values.
     * (never <code>null</code> or empty)
     * 
     */
	private char delimiter = DEFAULT_DELIMITER;
	
	/**
	 * Indicator for the import processor to attempt to create a View Table given the info in this object.
	 */
	boolean doProcess;
	
	/**
	 * Current <code>IStatus</code> representing the state of the input values for this instance of
	 * <code>TeiidMetadataFileInfo</code>
	 */
	private IStatus status;
	
	/**
	 * The cached value of the header string extracted from the data file based on the <code>headerLineNumber</code>
	 */
	private String headerString;
	
	/**
	 * The cached <code>Collection</code> of the first 6 lines to use for UI display purposes
	 */
	private Collection<String> firstSixLines;
	
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
		this(info.getDataFile());
		CoreArgCheck.isNotEmpty("" + info.getDelimiter(), "delimiter is null"); //$NON-NLS-1$ //$NON-NLS-2$
		CoreArgCheck.isPositive(info.getHeaderLineNumber(), "header line number is less than zero"); //$NON-NLS-1$
		
		this.setDelimiter(info.getDelimiter());
		this.setHeaderLineNumber(info.getHeaderLineNumber());
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
		
		this.dataFile = info.getDataFile();
		this.setDelimiter(info.getDelimiter());
		this.setHeaderLineNumber(info.getHeaderLineNumber());
		
		initialize();
		
	}
	
	private void initialize() {
		this.columnDatatypeMap = new HashMap<String, String>();
		this.firstSixLines = new ArrayList<String>();
		this.columnInfoList = new ArrayList<TeiidColumnInfo>();
		
		loadHeader();
		
		String fileName = dataFile.getName();
		if(fileName.endsWith(".txt")) { //$NON-NLS-1$
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
		loadHeader();
		parseHeader();
	}

	/**
	 * 
	 * @return the data file's character delimiter
	 */
	public char getDelimiter() {
		return this.delimiter;
	}

	/**
	 * 
	 * @param delimiter the data file's character delimiter
	 */
	public void setDelimiter(char delimiter) {
		CoreArgCheck.isNotEmpty("" + delimiter, "delimiter is null"); //$NON-NLS-1$ //$NON-NLS-2$
		
		this.delimiter = delimiter;
		parseHeader();
	}
	
	/**
	 * 
	 * @return dataFile the teiid-formatted data <code>File</code>
	 */
	public File getDataFile() {
		return this.dataFile;
	}

	private void loadHeader() {
		this.headerString = null;
		this.firstSixLines.clear();
		
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
                	if( iLines == getHeaderLineNumber() ) {
                        this.headerString = str;
                    }
                	this.firstSixLines.add(str);
                	if( iLines > 6 ) {
                		this.firstSixLines.add("....more..."); //$NON-NLS-1$
                		break;
                	}
                }        
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
        
        parseHeader();
	}
	
	/**
	 * 
	 * @return firstSixLines the <code>String[]</code> array from the data file
	 */
	public String[] getFirstSixLines() {
		return this.firstSixLines.toArray(new String[this.firstSixLines.size()]);
	}
	
	/**
	 * 
	 * @return columnInfoList the <code>TeiidColumnInfo[]</code> array parsed from the header in the data file
	 */
	public TeiidColumnInfo[] getColumnInfoList() {
		return this.columnInfoList.toArray(new TeiidColumnInfo[this.columnInfoList.size()]);
	}
	
	private void parseHeader() {
		this.columnInfoList.clear();
		
		if( this.headerString != null && this.headerString.length() > 0 ) {
			String delim = "" + getDelimiter(); //$NON-NLS-1$
			StringTokenizer strTokeniser = new StringTokenizer(this.headerString, delim);
			while( strTokeniser.hasMoreTokens() ) {
				String nextTok = strTokeniser.nextToken().trim();
				this.columnInfoList.add(new TeiidColumnInfo(nextTok));
			}
		}
		validate();
	}
	
	private void validate() {
		if( this.headerString == null || this.headerString.length() == 0 ) {
			setStatus(new Status(IStatus.ERROR, PLUGIN_ID, getString("status.noHeaderFound"))); //$NON-NLS-1$
			return;
		}
		
		if( this.columnInfoList.size() == 1 ) {
			// COULD HAVE ONE COLUMN, SO VALIDATE
			String message = TeiidMetadataFileInfo.validator.checkValidName(this.columnInfoList.iterator().next().getName());
			if( message != null ) {
				setStatus(new Status(IStatus.ERROR, PLUGIN_ID, getString("status.noHeaderFound"))); //$NON-NLS-1$
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

}
