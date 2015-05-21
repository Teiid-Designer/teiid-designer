/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.transformation.ui.wizards.xmlfile;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.core.designer.util.StringConstants;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.query.IQueryFactory;
import org.teiid.designer.query.IQueryService;
import org.teiid.designer.query.proc.ITeiidXmlColumnInfo;
import org.teiid.designer.query.sql.symbol.IElementSymbol;
import org.teiid.designer.transformation.ui.Messages;
import org.teiid.designer.transformation.ui.UiConstants;


/**
 * @since 8.0
 */
public class TeiidXmlColumnInfo implements ITeiidXmlColumnInfo {
	
	private static final String TEXT_SEGMENT = "text()"; //$NON-NLS-1$
	private static final String AT_SIGN = "@"; //$NON-NLS-1$ 
	private static final String DOT_DOT = ".."; //$NON-NLS-1$ 
	
    /**
     * The unique column name (never <code>null</code> or empty).
     */
	private IElementSymbol nameSymbol;
	
	 /**
     * The unique column datatype (never <code>null</code> or empty).
     */
	private String datatype;
	
	 /**
     * The column width value
     */
	private int width = DEFAULT_WIDTH;
	
	
	 /**
     * The unique column datatype (never <code>null</code> or empty).
     */
	private boolean forOrdinality;
	
	 /**
     * The unique column datatype (never <code>null</code> or empty).
     */
	private String defaultValue = StringConstants.EMPTY_STRING;
	
	 /**
     * The full xml path
     */
	private String relativePath = StringConstants.EMPTY_STRING;
	
	 /**
     * The root xml path
     */
	private String rootXmlPath = StringConstants.EMPTY_STRING;
	
	 /**
     * The xml element
     */
	private XmlElement xmlElement;
	
	/**
	 * The xml attribute
	 */
	private XmlAttribute xmlAttribute;
	
	/**
	 * Current <code>IStatus</code> representing the state of the input values for this instance of
	 * <code>TeiidColumnInfo</code>
	 */
	private IStatus status;
	
	private boolean pathOverriden = false;
	
	boolean initializing = false;
	
	/**
	 * 
	 * @param name the column name (never <code>null</code> or empty).
	 */
	public TeiidXmlColumnInfo(XmlElement element, String rootPath) {
		this(element, rootPath, DEFAULT_DATATYPE);
	}
	
	/**
	 * 
	 * @param name the column name (never <code>null</code> or empty).
	 */
	public TeiidXmlColumnInfo(XmlAttribute attribute, String rootPath) {
		this(attribute, rootPath, DEFAULT_DATATYPE);
	}
	
	/**
	 * 
	 * @param name the column name (never <code>null</code> or empty).
	 * @param datatype the column datatype (never <code>null</code> or empty).
	 */
	public TeiidXmlColumnInfo(XmlAttribute attribute,  String rootPath, String datatype) {
		super();
        CoreArgCheck.isNotNull(attribute, "attribute is null"); //$NON-NLS-1$
        CoreArgCheck.isNotEmpty(datatype, "datatype is null"); //$NON-NLS-1$
        
        initializing = true;
		this.xmlAttribute = attribute;
		this.xmlElement = attribute.getElement();
		setRootPath(rootPath);
		initNameSymbol(xmlAttribute.getName());
		setRelativePathInternal(attribute);
		this.datatype = datatype;
		this.defaultValue = StringConstants.EMPTY_STRING;
		validate();
		initializing = false;
	}
	
	/**
	 * 
	 * @param name the column name (never <code>null</code> or empty).
	 * @param datatype the column datatype (never <code>null</code> or empty).
	 */
	public TeiidXmlColumnInfo(XmlElement element, String rootPath, String datatype) {
		super();
        CoreArgCheck.isNotNull(element, "element is null"); //$NON-NLS-1$
        CoreArgCheck.isNotEmpty(datatype, "datatype is null"); //$NON-NLS-1$
        
        initializing = true;
        this.xmlElement = element;
        setRootPath(rootPath);
        setRelativePathInternal(element);
		initNameSymbol(element.getName());
		this.datatype = datatype;
		this.defaultValue = StringConstants.EMPTY_STRING;
		validate();
		initializing = false;
	}
	
	/**
	 * 
	 * @param name the column name (never <code>null</code> or empty).
	 * @param datatype the column datatype (never <code>null</code> or empty).
	 */
	public TeiidXmlColumnInfo(
			XmlElement element, 
			XmlAttribute attribute, 
			String name, 
			boolean ordinality, 
			String datatype, 
			String defaultValue,
			String rootPath,
			String fullXmlPath ) {
		super();
		
		initializing = true;
		this.xmlElement = element;
		this.xmlAttribute = attribute;
		setRootPath(rootPath);
        setRelativePathInternal(element);
		initNameSymbol(name);
		this.datatype = datatype;
        this.forOrdinality = ordinality;
        if( defaultValue == null ) {
        	this.defaultValue = StringConstants.EMPTY_STRING;
        } else {
        	this.defaultValue = defaultValue;
        }
        initializing = false;
	}

	/** 
	 * Initialise the {@link ElementSymbol} to hold the
	 * name. This validates the symbol's character composition.
	 * 
	 * The '.' character is the only punctuation symbol that will cause
	 * problems for an element symbol so these are replaced these with '_'.
	 */
	private void initNameSymbol(final String name) {
	    IQueryService queryService = ModelerCore.getTeiidQueryService();
        IQueryFactory factory = queryService.createQueryFactory();
	    nameSymbol = factory.createElementSymbol(name.replaceAll("\\.", "_"));  //$NON-NLS-1$//$NON-NLS-2$
	}
	
	/**
	 * Get the fully validated column name. This should be used in SQL string
	 * generation.
	 * 
	 * @return name the column name
	 */
	public String getSymbolName() {
		return this.nameSymbol.toString();
	}
	
    /**
     * Get the column name for display in the UI. This removes any quotes for
     * aesthetic reasons. Use {@link #getSymbolName()} for retrieving the 
     * fully validated column name.
     * 
     * @return the column name sans quotes.
     */
	public String getName() {
	    String name = this.nameSymbol.toString();
	    return name.replaceAll("\"", ""); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * 
	 * @param name the column name (never <code>null</code> or empty).
	 */
	public void setName(String name) {
		CoreArgCheck.isNotNull(name, "name is null"); //$NON-NLS-1$
		
		initNameSymbol(name);
		validate();
	}

	/**
	 * 
	 * @return datatype the column datatype
	 */
	public String getDatatype() {
		return this.datatype;
	}

	/**
	 * 
	 * @param datatype the column datatype (never <code>null</code> or empty).
	 */
	public void setDatatype(String datatype) {
		CoreArgCheck.isNotNull(datatype, "datatype is null"); //$NON-NLS-1$
		this.datatype = datatype;
		validate();
	}
	
	/**
	 * 
	 * @return name the column name
	 */
	public int getWidth() {
		return this.width;
	}

	/**
	 * 
	 * @param name the column name (never <code>null</code> or empty).
	 */
	public void setWidth(int width) {
		CoreArgCheck.isPositive(width, "width is less than 1"); //$NON-NLS-1$
		this.width = width;
		validate();
	}
	
	/**
	 * 
	 * @return defaultValue the column defaultValue
	 */
	public String getDefaultValue() {
		return this.defaultValue;
	}
	
	/**
	 * 
	 * @param defaultValue the column defaultValue
	 */
	public void setDefaultValue(String defaultValue) {
		if( defaultValue == null ) {
        	this.defaultValue = StringConstants.EMPTY_STRING;
        } else {
        	this.defaultValue = defaultValue;
        }
		validate();
	}

	/**
	 * 
	 * @param xmlPath the column xmlPath
	 */
	public void setRelativePath(String relativePath) {
        this.relativePath = relativePath;
        validate();
	}
	
	
	private void setRelativePathInternal(Object obj) {
		String rootPath = this.rootXmlPath;
		if( obj instanceof XmlElement ) {
			XmlElement element = (XmlElement)obj;
			String fullPath = element.getFullPath();
			StringBuffer relativePathBuff = new StringBuffer(getRelativePath(fullPath,rootPath));
			if(!relativePathBuff.toString().isEmpty() && !relativePathBuff.toString().endsWith(XmlElement.SEPARATOR)) {
				relativePathBuff.append(XmlElement.SEPARATOR);
			}
			relativePathBuff.append(TEXT_SEGMENT);
			setRelativePath(relativePathBuff.toString());
			return;
		}
		
		if( obj instanceof XmlAttribute ) {
			XmlAttribute attr = (XmlAttribute)obj;
			XmlElement element = attr.getElement();
			
			String fullPath = element.getFullPath();			
			StringBuffer relativePathBuff = new StringBuffer(getRelativePath(fullPath,rootPath));
			if(!relativePathBuff.toString().isEmpty() && !relativePathBuff.toString().endsWith(XmlElement.SEPARATOR)) {
				relativePathBuff.append(XmlElement.SEPARATOR);
			}
			relativePathBuff.append( AT_SIGN + attr.getName());
			setRelativePath(relativePathBuff.toString());
			return;
		}
	}
	
	/**
	 * 
	 * @return xmlPath the column xmlPath
	 */
	public String getRelativePath() {
		return this.relativePath.toString();
	}
	
	public void setXmlElement(XmlElement element) {
		this.xmlElement = element;
	}
	
	public void setXmlAttribute(XmlAttribute attribute) {
		this.xmlAttribute = attribute;
		this.xmlElement = attribute.getElement();
	}
	
	/**
	 * 
	 * @return xmlPath the column xmlPath
	 */
	public String getFullXmlPath() {
		// Return ROOT PATH + relative path
		return this.rootXmlPath + XmlElement.SEPARATOR + this.relativePath;
	}
	
	public void setRootPath(String thePath) {
		boolean rootPathChanged = false;
		String newRootPath = null;
		
		
		if( thePath != null && thePath.length() > 0 ) {
			newRootPath = thePath;
		} else {
			newRootPath = StringConstants.EMPTY_STRING;
		}
		
		if( !this.rootXmlPath.equalsIgnoreCase(newRootPath) ) {
			rootPathChanged = true;
		}
		
		if( rootPathChanged ) {
			// Only re-calculate the relative path if it is VALID
			if(!initializing && fullPathExists() ) {
				// Recalculate the relative path?
				// ROOT PATH should always be VALID and be a part of the backing XmlElement.getFullXmlPath() value
				if( isXmlAttribute() ) {
					String fullPath = this.xmlElement.getFullPath() + XmlElement.SEPARATOR + AT_SIGN + getXmlAttribute().getName();
					String relativePath = getRelativePath(fullPath,newRootPath);
					setRelativePath(relativePath);
				} else {
					String fullPath = this.xmlElement.getFullPath();
					if( this.relativePath != null && !this.relativePath.isEmpty() && TEXT_SEGMENT.equalsIgnoreCase(getLastSegment(this.relativePath))) {
						String shortPath = fullPath;
						String newFullPath = shortPath + XmlElement.SEPARATOR + TEXT_SEGMENT;
						String relativePath = getRelativePath(newFullPath,newRootPath);
						setRelativePath(relativePath);
					} else {
						String theFullPath = this.xmlElement.getFullPath();
						String relativePath = getRelativePath(theFullPath,newRootPath);
						setRelativePath(relativePath);
					}
				}
			}
			
			this.rootXmlPath = newRootPath;
		}
		
		if( !initializing ) {
			validate();
		}
	}
	
	/**
	 * 
	 * @return xmlElement the xmlElement
	 */
	public XmlElement getXmlElement() {
		return this.xmlElement;
	}
	
	/**
	 * 
	 * @return xmlElement the xmlAttribute
	 */
	public XmlAttribute getXmlAttribute() {
		return this.xmlAttribute;
	}
	
	public boolean isXmlAttribute() {
		return this.xmlAttribute != null;
	}
	
	/**r
	 * 
	 * @return forOrdinality the column forOrdinality
	 */
	public boolean getOrdinality() {
		return this.forOrdinality;
	}

	/**
	 * 
	 * @param forOrdinality the column forOrdinality
	 */
	public void setOrdinality(boolean value) {
		this.forOrdinality = value;
		validate();
	}
	
	/**
	 * 
	 * @return forOrdinality the column forOrdinality
	 */
	public boolean isPathOverridden() {
		return this.pathOverriden;
	}

	/**
	 * 
	 * @param forOrdinality the column forOrdinality
	 */
	public void setPathOverridden(boolean value) {
		this.pathOverriden = value;
		validate();
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
	
	private void validate() {
		boolean pathOK = fullPathExists();
		
		if( !pathOK ) {
			setStatus(new Status(IStatus.WARNING, UiConstants.PLUGIN_ID,
					NLS.bind(Messages.InvalidPathWarning, getRelativePath(), getName())) );
			return;
		}
		
		setStatus(Status.OK_STATUS);
	}
	
	private boolean fullPathExists() {
        // Validate that the relative path exists in the full xml path
		String fullXmlElementPath = this.xmlElement.getFullPath();
		boolean pathOK = true;
		if( isXmlAttribute() ) {
			String thisFullPath = getFullXmlPath();
			thisFullPath = collapseDotDots(thisFullPath);
			String shortPath = removeLastSegment(thisFullPath);
			int nSegs = getPathSegments(shortPath).size();
			if( getMatchingSegmentCount(fullXmlElementPath,shortPath) != nSegs ) {
				pathOK = false;
			}
		} else {
			String thisFullPath = getFullXmlPath();
			if( TEXT_SEGMENT.equalsIgnoreCase(getLastSegment(thisFullPath))) {
				thisFullPath = collapseDotDots(thisFullPath);
				String shortPath = removeLastSegment(thisFullPath);
				int nSegs = getPathSegments(shortPath).size();
				if( getMatchingSegmentCount(fullXmlElementPath,shortPath) != nSegs ) {
					pathOK = false;
				}
			} else {
				pathOK = thisFullPath.equals(fullXmlElementPath);
			}
		}
		
		return pathOK;
	}
	
	/**
	 * Form the relative path from the full path and the root path
	 * @param fullPath the full path
	 * @param rootPath the root path
	 * @return the relative path
	 */
	private String getRelativePath(String fullPath, String rootPath) {
		StringBuffer resultBuff = new StringBuffer();
		
		// Get number of matching segments
		int nMatch = getMatchingSegmentCount(fullPath,rootPath);
		// Extra segments (may need '..')
		int extraSegs = getPathSegments(rootPath).size()-nMatch;
				
		List<String> segments = getPathSegments(fullPath);
		for(int i=0; i<segments.size(); i++) {
			if(i>=nMatch) {
				// add separator after first matching segment
				if(i!=nMatch) resultBuff.append(XmlElement.SEPARATOR);
				// add segment
				resultBuff.append( segments.get(i) );
			}
		}
		// Add ../ for extra segments
		for(int i=0; i<extraSegs; i++) {
			resultBuff.insert(0, DOT_DOT+XmlElement.SEPARATOR);
		}
		
		return resultBuff.toString();
	}
	
	/**
	 * Remove the last segment from the supplied path
	 * @param path the path
	 * @return the path, minus last segment
	 */
	private String removeLastSegment(String path) {
		StringBuffer resultBuff = new StringBuffer();
		List<String> segments = getPathSegments(path);
		for(int i=0; i<segments.size()-1; i++) {
			resultBuff.append(XmlElement.SEPARATOR+segments.get(i));
		}
		return resultBuff.toString();
	}

	/**
	 * Get the last segment of the supplied path
	 * @param path the path
	 * @return the last path segment
	 */
	private String getLastSegment(String path) {
		String lastSegment = StringConstants.EMPTY_STRING;
		List<String> segments = getPathSegments(path);
		if(segments.size()>0) {
			lastSegment = segments.get(segments.size()-1);
		}
		return lastSegment;
	}
	
	/**
	 * Get the list of path segments (separated by XmlElement.SEPARATOR)
	 * @param path the path
	 * @return the list of segments
	 */
	private List<String> getPathSegments(String path) {
		List<String> segments = new ArrayList<String>();
		StringTokenizer st = new StringTokenizer(path,XmlElement.SEPARATOR);
		while(st.hasMoreTokens()) {
			segments.add(st.nextToken());
		}
		return segments;
	}
	
	/**
	 * Get the number of segments of the two path strings that match
	 * @param path1 the first path
	 * @param path2 the second path
	 * @return number of matching segments
	 */
	private int getMatchingSegmentCount(String path1, String path2) {
		int matchingSegs = 0;
		List<String> path1Segs = getPathSegments(path1);
		List<String> path2Segs = getPathSegments(path2);
		int path1Size = path1Segs.size();
		int path2Size = path2Segs.size();
		if(path1Size<=path2Size) {
			for(int i=0; i<path1Segs.size(); i++) {
				String path1Seg = path1Segs.get(i);
				String path2Seg = path2Segs.get(i);
				if(path1Seg!=null && path1Seg.equalsIgnoreCase(path2Seg)) {
					matchingSegs++;
				}
			}
		} else {
			for(int i=0; i<path2Segs.size(); i++) {
				String path1Seg = path1Segs.get(i);
				String path2Seg = path2Segs.get(i);
				if(path2Seg!=null && path2Seg.equalsIgnoreCase(path1Seg)) {
					matchingSegs++;
				}
			}
		}
		return matchingSegs;
	}
	
	/**
	 * Removes .. in relative path by removing the .. and the segment preceding it.
	 * Used to compare a relative path to full path in validation
	 * @param originalStr the original string
	 * @return the string after collapsing ..
	 */
	private String collapseDotDots(String originalStr) {
		String theString = originalStr;
		
		while(theString.indexOf(XmlElement.SEPARATOR+DOT_DOT)!=-1) {
			int dotdotIndex = theString.indexOf(XmlElement.SEPARATOR+DOT_DOT);
			String leadingStr = theString.substring(0, dotdotIndex);
			String trailingStr = theString.substring(dotdotIndex+3);

			int lastDelim = leadingStr.lastIndexOf(XmlElement.SEPARATOR);
			if(lastDelim!=-1) {
				String newLeading = leadingStr.substring(0,lastDelim);
				theString = newLeading+trailingStr;
			} else {
				theString = leadingStr+trailingStr;
			}
		}
		
		return theString;		
	}
	
    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder text = new StringBuilder();
        text.append("Column Info: "); //$NON-NLS-1$
        text.append("name = ").append(getSymbolName()); //$NON-NLS-1$
        text.append(", ordinal = ").append(getOrdinality()); //$NON-NLS-1$
        text.append(", datatype = ").append(getDatatype()); //$NON-NLS-1$
        text.append(", default = ").append(getDefaultValue()); //$NON-NLS-1$
        text.append(", PATH = ").append(getRelativePath()); //$NON-NLS-1$

        return text.toString();
    }
}