/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.ui.wizards.file;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.teiid.query.sql.symbol.ElementSymbol;

import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.core.util.StringUtilities;
import com.metamatrix.modeler.transformation.ui.wizards.xmlfile.XmlAttribute;
import com.metamatrix.modeler.transformation.ui.wizards.xmlfile.XmlElement;

/**
 * A <code>TeiidColumnInfo</code> defines extension properties for metaclasses within a metamodel.
 */
public class TeiidColumnInfo {
	public static final String DEFAULT_DATATYPE = "string"; //$NON-NLS-1$
	public static final String INTEGER_DATATYPE = "integer"; //$NON-NLS-1$
	
	public static final int DEFAULT_WIDTH = 10;
	
	private static final IPath EMPTY_PATH = new Path(StringUtilities.EMPTY_STRING);
	
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
	private IPath fullXmlPath = EMPTY_PATH;
	
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
	
	/**
	 * 
	 * @param name the column name (never <code>null</code> or empty).
	 */
	public TeiidColumnInfo(String name) {
		this(name, DEFAULT_DATATYPE);
	}
	
	/**
	 * 
	 * @param name the column name (never <code>null</code> or empty).
	 * @param datatype the column datatype (never <code>null</code> or empty).
	 */
	public TeiidColumnInfo(String name, String datatype) {
		super();
        CoreArgCheck.isNotEmpty(name, "name is null"); //$NON-NLS-1$
        CoreArgCheck.isNotEmpty(datatype, "datatype is null"); //$NON-NLS-1$

		initNameSymbol(name);
		this.datatype = datatype;
		validate();
	}
	
	/**
	 * 
	 * @param name the column name (never <code>null</code> or empty).
	 * @param datatype the column datatype (never <code>null</code> or empty).
	 */
	public TeiidColumnInfo(String name, String datatype, int width) {
		this(name, datatype);
        CoreArgCheck.isPositive(width, "width is zero or less"); //$NON-NLS-1$
        
		this.width = width;
	}
	
	/**
	 * 
	 * @param name the column name (never <code>null</code> or empty).
	 * @param datatype the column datatype (never <code>null</code> or empty).
	 */
	public TeiidColumnInfo(String name, boolean ordinality, String datatype, String defaultValue, String fullXmlPath ) {
		this(name, datatype);
        this.forOrdinality = ordinality;
        if( defaultValue == null ) {
        	this.defaultValue = StringUtilities.EMPTY_STRING;
        } else {
        	this.defaultValue = defaultValue;
        }
        if( fullXmlPath == null ) {
        	this.fullXmlPath = EMPTY_PATH;
        } else {
        	this.fullXmlPath = new Path(fullXmlPath);
        }
        validate();
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
        if( relativePath == null ) {
        	this.fullXmlPath = this.rootXmlPath;
        } else {
        	this.fullXmlPath = this.rootXmlPath.append(relativePath);
        }
	}
	
	/**
	 * 
	 * @return xmlPath the column xmlPath
	 */
	public String getRelativePath() {
		if( this.xmlElement != null ) {
			// Get difference between the xmlElement full path minus the root (if applicable)
			String fullPath = getFullXmlPath();
			String rootPath = this.rootXmlPath.toString();
			
			if( fullPath.startsWith(rootPath) ) {
				return fullPath.substring(rootPath.length(), fullPath.length());
			}
		}
		if( !this.fullXmlPath.isEmpty() ) {
			if( !this.rootXmlPath.isEmpty() && this.fullXmlPath.toString().indexOf(rootXmlPath.toString()) > -1) {
				return '/' + this.fullXmlPath.removeFirstSegments(this.rootXmlPath.segmentCount()).toString();
			} else {
				return this.fullXmlPath.toString();
			}
		}
		return StringUtilities.EMPTY_STRING;
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
		if( isXmlAttribute() ) {
			String theFullPath = null;
			if( this.xmlElement != null ) {
				theFullPath = this.xmlElement.getFullPath() + "/@" + this.xmlAttribute.getName(); //$NON-NLS-1$
			} else {
				theFullPath = this.fullXmlPath.toString();
			}
			return theFullPath;
		} else {
			if( this.xmlElement != null ) {
				return this.xmlElement.getFullPath();
			}
			
			return this.fullXmlPath.toString();
		}
	}
	
	/**
	 * 
	 * @param xmlPath the column xmlPath
	 */
	public void setFullXmlPath(String fullPath) {
        if( fullPath == null ) {
        	this.fullXmlPath = EMPTY_PATH;
        } else {
        	this.fullXmlPath = new Path(fullPath);
        }
	}
	
	public void setRootPath(String thePath) {
		if( thePath != null && thePath.length() > 0 ) {
			// Need to remove the OLD root path from FULL path
			IPath oldRelativePath = new Path(getRelativePath());
			
			String tmpRoot = thePath;
			if( thePath.endsWith("/")) { //$NON-NLS-1$
				tmpRoot = tmpRoot.substring(0, thePath.length()-1);
			}
			
			String newFullPath = tmpRoot + oldRelativePath;
			
			setFullXmlPath(newFullPath); //tmpRoot + oldRelativePath.toString());
			
			this.rootXmlPath = new Path(thePath);
		} else {
			this.rootXmlPath = EMPTY_PATH;
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
	
	/**
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

        /*
         * No validation is currently required since the name is automatically
         * 'fixed' by the teiid element symbol. If validation is required on
         * other fields then a failure should be captured by setting the status,
         * eg.
         * 
         * setStatus(new Status(IStatus.ERROR, UiConstants.PLUGIN_ID,
         * UiConstants
         * .Util.getString("TeiidColumnInfo.status.invalidColumnName",
         * getName()))); //$NON-NLS-1$
         */
		
		setStatus(Status.OK_STATUS);
	}
	
    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder text = new StringBuilder();
        text.append("Teiid Metadata Column Info: "); //$NON-NLS-1$
        text.append("name =").append(getName()); //$NON-NLS-1$
        text.append("symbol name = ").append(getSymbolName()); //$NON-NLS-1$
        text.append(", datatype =").append(getDatatype()); //$NON-NLS-1$

        return text.toString();
    }
}
