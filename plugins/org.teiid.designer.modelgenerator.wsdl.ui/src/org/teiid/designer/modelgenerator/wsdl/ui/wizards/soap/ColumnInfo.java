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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;
import org.teiid.designer.modelgenerator.wsdl.ui.Messages;

import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.core.util.StringUtilities;
import com.metamatrix.metamodels.relational.aspects.validation.RelationalStringNameValidator;
import com.metamatrix.modeler.core.validation.rules.StringNameValidator;
import com.metamatrix.modeler.modelgenerator.wsdl.ui.ModelGeneratorWsdlUiConstants;

public class ColumnInfo implements ModelGeneratorWsdlUiConstants {
	public static final String DEFAULT_DATATYPE = "string"; //$NON-NLS-1$
	public static final String INTEGER_DATATYPE = "integer"; //$NON-NLS-1$
	
	public static final int DEFAULT_WIDTH = 10;
	private static final char SEPARATOR = '/';

    /** Constant value indicating no segments */
    private static final String[] NO_SEGMENTS = new String[0];
	
	private static final StringNameValidator nameValidator = new RelationalStringNameValidator(false, true);
	
	private final SchemaPath EMPTY_PATH = new SchemaPath(StringUtilities.EMPTY_STRING);
	
	/**
	 * The collection of AttributeInfo objects
	 */
	private Collection<AttributeInfo> attributeInfoList;
	
    /**
     * The unique column name (never <code>null</code> or empty).
     */
	private String name;
	
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
	private SchemaPath fullXmlPath = EMPTY_PATH;
	
	 /**
     * The root xml path
     */
	private SchemaPath rootXmlPath = EMPTY_PATH;
	
	 /**
     * The xml element
     */
	private Object xmlElement;
	
	/**
	 * Current <code>IStatus</code> representing the state of the input values for this instance of
	 * <code>TeiidColumnInfo</code>
	 */
	private IStatus status;
	
	/**
	 * 
	 * @param name the column name (never <code>null</code> or empty).
	 */
	public ColumnInfo(String name) {
		this(name, DEFAULT_DATATYPE);
	}
	
	/**
	 * 
	 * @param name the column name (never <code>null</code> or empty).
	 * @param datatype the column datatype (never <code>null</code> or empty).
	 */
	public ColumnInfo(String name, String datatype) {
		super();
        CoreArgCheck.isNotEmpty(name, "name is null"); //$NON-NLS-1$
        CoreArgCheck.isNotEmpty(datatype, "datatype is null"); //$NON-NLS-1$
        
		this.name = name;
		this.datatype = datatype;
		
		this.attributeInfoList = new ArrayList<AttributeInfo>();
		validate();
	}
	
	/**
	 * 
	 * @param name the column name (never <code>null</code> or empty).
	 * @param datatype the column datatype (never <code>null</code> or empty).
	 */
	public ColumnInfo(String name, String datatype, int width) {
		this(name, datatype);
        CoreArgCheck.isPositive(width, "width is zero or less"); //$NON-NLS-1$
        
		this.width = width;
	}
	
	/**
	 * 
	 * @param name the column name (never <code>null</code> or empty).
	 * @param datatype the column datatype (never <code>null</code> or empty).
	 */
	public ColumnInfo(String name, boolean ordinality, String datatype, String defaultValue, String fullXmlPath ) {
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
        	this.fullXmlPath = new SchemaPath(fullXmlPath);
        }
        validate();
	}

	/**
	 * 
	 * @return name the column name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * 
	 * @param name the column name (never <code>null</code> or empty).
	 */
	public void setName(String name) {
		CoreArgCheck.isNotNull(name, "name is null"); //$NON-NLS-1$
		this.name = name;
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
        if( relativePath != null ) {
            this.rootXmlPath.append(relativePath);
        }
        this.fullXmlPath = new SchemaPath(this.rootXmlPath.toString());
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
			    this.fullXmlPath.removeFirstSegments(this.rootXmlPath.segmentCount());
				return this.fullXmlPath.toString();
			} else {
				return this.fullXmlPath.toString();
			}
		}
		return StringUtilities.EMPTY_STRING;
	}
	
	public void setXmlElement(Object element) {
		this.xmlElement = element;
	}
	/**
	 * 
	 * @return xmlPath the column xmlPath
	 */
	public String getFullXmlPath() {
		// TODO:  FIX THIS
//		if( this.xmlElement != null ) {
//			return this.xmlElement.getFullPath();
//		}
		
		return this.fullXmlPath.toString();
	}
	
	/**
	 * 
	 * @param xmlPath the column xmlPath
	 */
	public void setFullXmlPath(String fullPath) {
        if( fullPath == null ) {
        	this.fullXmlPath = EMPTY_PATH;
        } else {
        	this.fullXmlPath = new SchemaPath(fullPath);
        }
	}
	
	public void setRootPath(String thePath) {
		if( thePath != null && thePath.length() > 0 ) {
			// Need to remove the OLD root path from FULL path
		    SchemaPath oldRelativePath = new SchemaPath(getRelativePath());
			
			String tmpRoot = thePath;
			if( thePath.endsWith("/")) { //$NON-NLS-1$
				tmpRoot = tmpRoot.substring(0, thePath.length()-1);
			}
			
			String newFullPath = tmpRoot + oldRelativePath;
			
			setFullXmlPath(newFullPath); //tmpRoot + oldRelativePath.toString());
			
			this.rootXmlPath = new SchemaPath(thePath);
		} else {
			this.rootXmlPath = EMPTY_PATH;
		}
	}
	

	
	/**
	 * 
	 * @return xmlElement the xmlElement
	 */
	public Object getXmlElement() {
		return this.xmlElement;
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
	
	public AttributeInfo[] getAttributeInfoArray() {
		return this.attributeInfoList.toArray(new AttributeInfo[this.attributeInfoList.size()]);
	}
	
	public void addAttributeInfo(Object xmlElement, String name) {
		this.attributeInfoList.add(new AttributeInfo(xmlElement, name, this));
		validate();
	}
	
	public void removeAttributeInfo(AttributeInfo theInfo) {
		this.attributeInfoList.remove(theInfo);
		validate();
	}
	
	public String getUniqueAttributeName(String proposedName) {
		for( AttributeInfo info : getAttributeInfoArray()) {
			ColumnInfo.nameValidator.addExistingName(info.getName());
		}
		String changedName = ColumnInfo.nameValidator.createUniqueName(proposedName);
		String finalName = changedName == null ? proposedName : changedName;
		ColumnInfo.nameValidator.clearExistingNames();
		return finalName;
		
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

		String result = nameValidator.checkValidName(getName());
		if( result != null ) {
			setStatus(new Status(IStatus.ERROR, PLUGIN_ID, Messages.InvalidColumnName + getName()));
			return;
		}
		
		// Check Datatypes
		if( !ImportManagerValidator.isValidDatatype(getDatatype())) {
			setStatus(new Status(IStatus.ERROR, PLUGIN_ID, 
					NLS.bind(Messages.InvalidDatatype_0_ForColumn_1, getDatatype(), getName())));
			return;
		}
		
		// Validate Paths
		
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
        text.append("Column Info: "); //$NON-NLS-1$
        text.append("name = ").append(getName()); //$NON-NLS-1$
        text.append(", datatype = ").append(getDatatype()); //$NON-NLS-1$
        text.append(", PATH = ").append(getRelativePath()); //$NON-NLS-1$

        return text.toString();
    }
    
    class SchemaPath {
        /** The path segments */
        private String[] segments;
        
        public SchemaPath(String string) {
            super();
            segments = computeSegments(string);
        }
        
        public boolean isEmpty() {
            return segments.length == 0;
        }
        
        public int segmentCount() {
            return segments.length;
        }
        
        public void append(String string) {
            String[] tempSegments = computeSegments(string);
            int segCount = tempSegments.length + segments.length;
            Collection<String> allSegments = new ArrayList<String>(segments.length);
            for( String seg : segments ) {
                allSegments.add(seg);
            }
            for( String seg : tempSegments ) {
                allSegments.add(seg);
            }
            
            segments = allSegments.toArray(new String[segCount]);
        }
        
        public void removeFirstSegments(int nSegs) {
            int newSegCount = segments.length - nSegs;
            Collection<String> newSegments = new ArrayList<String>(newSegCount);
            int i=1;
            for( String seg : segments ) {
                if( i > nSegs) {
                    newSegments.add(seg);
                }
                i++;
            }
            
            segments = newSegments.toArray(new String[newSegCount]);
        }
        
        /**
         * Computes the segment array for the given canonicalized path.
         */
        private String[] computeSegments(String path) {
            // performance sensitive --- avoid creating garbage
            int segmentCount = computeSegmentCount(path);
            if (segmentCount == 0)
                return NO_SEGMENTS;
            String[] newSegments = new String[segmentCount];
            int len = path.length();
            // check for initial slash
            int firstPosition = (path.charAt(0) == SEPARATOR) ? 1 : 0;
            // check for UNC
            if (firstPosition == 1 && len > 1 && (path.charAt(1) == SEPARATOR))
                firstPosition = 2;
            int lastPosition = (path.charAt(len - 1) != SEPARATOR) ? len - 1 : len - 2;
            // for non-empty paths, the number of segments is 
            // the number of slashes plus 1, ignoring any leading
            // and trailing slashes
            int next = firstPosition;
            for (int i = 0; i < segmentCount; i++) {
                int start = next;
                int end = path.indexOf(SEPARATOR, next);
                if (end == -1) {
                    newSegments[i] = path.substring(start, lastPosition + 1);
                } else {
                    newSegments[i] = path.substring(start, end);
                }
                next = end + 1;
            }
            return newSegments;
        }
        
        private int computeSegmentCount(String path) {
            int len = path.length();
            if (len == 0 || (len == 1 && path.charAt(0) == SEPARATOR)) {
                return 0;
            }
            int count = 1;
            int prev = -1;
            int i;
            while ((i = path.indexOf(SEPARATOR, prev + 1)) != -1) {
                if (i != prev + 1 && i != len) {
                    ++count;
                }
                prev = i;
            }
            if (path.charAt(len - 1) == SEPARATOR) {
                --count;
            }
            return count;
        }
        
        public String toString() {
            StringBuffer buf = new StringBuffer();
            
            int segCount = segmentCount();
            int nSeg = 0;
            for( String seg : segments ) {
                if( nSeg == 0 && seg.charAt(0) != SEPARATOR) {
                    buf.append(SEPARATOR);
                }
                buf.append(seg);
                if( nSeg < segCount-1 ) {
                    buf.append(SEPARATOR);
                }
                nSeg++;
            }

            return buf.toString();
        }
    }
}

