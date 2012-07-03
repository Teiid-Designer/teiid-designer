/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package com.metamatrix.modeler.transformation.ui.wizards.xmlfile;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;
import org.teiid.query.sql.symbol.ElementSymbol;

import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.core.util.StringUtilities;
import com.metamatrix.modeler.transformation.ui.Messages;
import com.metamatrix.modeler.transformation.ui.UiConstants;

public class TeiidXmlColumnInfo {
	public static final String DEFAULT_DATATYPE = "string"; //$NON-NLS-1$
	public static final String INTEGER_DATATYPE = "integer"; //$NON-NLS-1$
	
	public static final int DEFAULT_WIDTH = 10;
	
	private static final IPath EMPTY_PATH = new Path(StringUtilities.EMPTY_STRING);
	private static final String TEXT_SEGMENT = "text()"; //$NON-NLS-1$
	
    /**
     * The unique column name (never <code>null</code> or empty).
     */
	private ElementSymbol nameSymbol;
	
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
	private String defaultValue = StringUtilities.EMPTY_STRING;
	
	 /**
     * The full xml path
     */
	private IPath relativePath = EMPTY_PATH;
	
	 /**
     * The root xml path
     */
	private IPath rootXmlPath = EMPTY_PATH;
	
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
		setRelativePathInternal(attribute);
		initNameSymbol(xmlAttribute.getName());
		this.datatype = datatype;
		this.defaultValue = StringUtilities.EMPTY_STRING;
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
		this.defaultValue = StringUtilities.EMPTY_STRING;
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
        	this.defaultValue = StringUtilities.EMPTY_STRING;
        } else {
        	this.defaultValue = defaultValue;
        }
        initializing = false;
	}

	/** 
	 * Initialise the {@link ElementSymbol} to hold the
	 * name. This validates the symbol's character composition.
	 * 
	 * The '.' character is the only puntuation symbol that will cause
	 * problems for an element symbol so these are replaced these with '_'.
	 */
	private void initNameSymbol(final String name) {
	    nameSymbol = new ElementSymbol(name.replaceAll("\\.", "_"));  //$NON-NLS-1$//$NON-NLS-2$
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
        	this.defaultValue = StringUtilities.EMPTY_STRING;
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
        this.relativePath = new Path(relativePath);
        validate();
	}
	
	
	private void setRelativePathInternal(Object obj) {
		IPath rootPath = this.rootXmlPath;
		if( obj instanceof XmlElement ) {
			XmlElement element = (XmlElement)obj;
			IPath fullPath = new Path(element.getFullPath());
			IPath relativePath = fullPath.makeRelativeTo(rootPath).append(TEXT_SEGMENT);
			setRelativePath(relativePath.toString());
			return;
		}
		
		if( obj instanceof XmlAttribute ) {
			XmlAttribute attr = (XmlAttribute)obj;
			XmlElement element = attr.getElement();
			
			IPath fullPath = new Path(element.getFullPath());
			IPath relativePath = fullPath.makeRelativeTo(rootPath).append('@' + attr.getName());
			setRelativePath(relativePath.toString());
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
		return this.rootXmlPath.append(this.relativePath).toString();
	}
	
	public void setRootPath(String thePath) {
		boolean rootPathChanged = false;
		IPath newRootPath = null;
		
		
		if( thePath != null && thePath.length() > 0 ) {
			newRootPath = new Path(thePath);

		} else {
			newRootPath = EMPTY_PATH;
		}
		
		if( !this.rootXmlPath.equals(newRootPath) ) {
			rootPathChanged = true;
		}
		
		if( rootPathChanged ) {
			// Only re-calculate the relative path if it is VALID
			if(!initializing && fullPathExists() ) {
				// Recalculate the relative path?
				// ROOT PATH should always be VALID and be a part of the backing XmlElement.getFullXmlPath() value
				if( isXmlAttribute() ) {
					String lastSegment = this.relativePath.lastSegment().toString();
					
					IPath fullPath = new Path(this.xmlElement.getFullPath());
					IPath relativePath = fullPath.makeRelativeTo(newRootPath).append(lastSegment);
					setRelativePath(relativePath.toString());
				} else {
					IPath fullPath = new Path(this.xmlElement.getFullPath());
					if( this.relativePath != null && !this.relativePath.isEmpty() && this.relativePath.lastSegment().equalsIgnoreCase(TEXT_SEGMENT)) {
						IPath shortPath = fullPath;
						IPath newFullPath = shortPath.append(TEXT_SEGMENT);
						IPath relativePath = newFullPath.makeRelativeTo(newRootPath);
						setRelativePath(relativePath.toString());
					} else {
						IPath theFullPath = new Path(this.xmlElement.getFullPath());
						IPath relativePath = theFullPath.makeRelativeTo(newRootPath);
						setRelativePath(relativePath.toString());
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
		IPath fullXmlElementPath = new Path(this.xmlElement.getFullPath());
		boolean pathOK = true;
		if( isXmlAttribute() ) {
			IPath thisFullPath = new Path(getFullXmlPath());
			IPath shortPath = thisFullPath.removeLastSegments(1);
			int nSegs = shortPath.segmentCount();
			if( fullXmlElementPath.matchingFirstSegments(shortPath) != nSegs ) {
				pathOK = false;
			}
		} else {
			IPath thisFullPath = new Path(getFullXmlPath());
			if( thisFullPath.lastSegment().equalsIgnoreCase(TEXT_SEGMENT)) {
				IPath shortPath = thisFullPath.removeLastSegments(1);
				int nSegs = shortPath.segmentCount();
				if( fullXmlElementPath.matchingFirstSegments(shortPath) != nSegs ) {
					pathOK = false;
				}
			} else {
				pathOK = thisFullPath.equals(fullXmlElementPath);
			}
		}
		
		return pathOK;
	}
	
    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder text = new StringBuilder();
        text.append("Teiid XML Metadata Column Info: "); //$NON-NLS-1$
        text.append("\tname =").append(getName()); //$NON-NLS-1$
        text.append("\n\tsymbol name = ").append(getSymbolName()); //$NON-NLS-1$
        text.append("\n\tdatatype =").append(getDatatype()); //$NON-NLS-1$
        text.append("\n\tfullPath =").append(getFullXmlPath()); //$NON-NLS-1$

        return text.toString();
    }
}