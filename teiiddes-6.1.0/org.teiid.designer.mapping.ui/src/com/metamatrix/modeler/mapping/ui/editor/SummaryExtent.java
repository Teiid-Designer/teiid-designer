/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.mapping.ui.editor;

import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.emf.ecore.EObject;

import com.metamatrix.core.util.HashCodeUtil;

/**
 * The <code>SummaryExtent</code> class provides information about one or more XML document mapping references
 * some of whose mapped columns are mapped to tree nodes that are not yet visible.  In this case a number
 * is displayed showing the count of all such columns. 
 * @author Jerry Helbling
 * @since 4.4
 * @version 1.0
 */
public final class SummaryExtent extends MappingExtent {

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    private HashMap hmapMappingClasses = new HashMap();
    private boolean bSomeMappingClassesAreVisible;
    private int iUnmappedNodes;
    
    public static final String UNMAPPED_MAPPABLE_COLUMN_COUNT = "UNMAPPED_MAPPABLE_COLUMN_COUNT";  //$NON-NLS-1$
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    public SummaryExtent(EObject theMappingReference) {
        super( theMappingReference );
//        System.out.println("[SummaryExtent.ctor 1] BOT");
    }
    
    /**
     * Constructs a <code>SummaryExtent</code>.
     * @param theOffset the y offset
     * @param theHeight the height
     * @param theMappingReference the mapping <code>EObject</code> reference
     * @throws IllegalArgumentException if the offset or height is less than zero or if the mapping
     * reference is <code>null</code>
     */
    public SummaryExtent(double theOffset,
                         double theHeight,
                         EObject theMappingReference) {
        this(theMappingReference);
        setOffset(theOffset);
        increaseHeight(theHeight);
//        System.out.println("[SummaryExtent.ctor 2] BOT");
    }
    
    /**
     * Constructs a <code>MappingExtent</code>.
     * @param theOffset the y offset
     * @param theHeight the height
     * @param theMappingReference the mapping <code>EObject</code> reference
     * @param theDocumentNodeReference the document node <code>EObject</code> reference
     * @throws IllegalArgumentException if the offset or height is less than zero or if the mapping
     * reference is <code>null</code>
     */
    public SummaryExtent(double theOffset,
                         double theHeight,
                         EObject theMappingReference,
                         EObject theDocumentNodeReference) {
        this(theMappingReference);
        documentNodeRef = theDocumentNodeReference;
        setOffset(theOffset);
        increaseHeight(theHeight);
//        System.out.println("[SummaryExtent.ctor 3] BOT");        
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * Signifies if the mapping classes, offsets, and required mapping are equal.
     */
    @Override
    public boolean equals(Object theObject) {
        boolean result = false;
        if (theObject instanceof SummaryExtent) {
            SummaryExtent extent = (SummaryExtent)theObject;
            if (offset == extent.getOffset() && height == extent.getHeight() ) {
                if (mappingRef == null) {
                    result = (extent.getMappingReference() == null);
                }
                else {
                    EObject ref = extent.getMappingReference();
                    if (ref != null) {
                        result = mappingRef.equals(ref);
                    }
                }
                if (result) {
                	result = (isMappingRequired() == extent.isMappingRequired());
                    if (result) {
                    	result = (isCompletelyMapped() == extent.isCompletelyMapped());
                    	if (result) {
                            if (documentNodeRef == null) {
                                result = (extent.getDocumentNodeReference() == null);
                            }
                            else {
                                EObject docRef = extent.getDocumentNodeReference();
                                if (docRef != null) {
                                    result = documentNodeRef.equals(docRef);
                                }
                            }
                    	}
                    }
                }
            }
        }
        return result;
    }

    /**
     * Gets the document node reference <code>EObject</code>.
     * @return the document node reference or <code>null</code>
     */
    @Override
    public EObject getDocumentNodeReference() {
        return documentNodeRef;
    }
    
    public void clearMappingClasses() {
        hmapMappingClasses.clear();
    }
      
    public HashMap getMappingClasses() {
        return hmapMappingClasses;
    }

    public void setMappingClasses( HashMap hmapMappingClasses ) {        
        this.hmapMappingClasses = hmapMappingClasses;        
        Integer ICount = (Integer)hmapMappingClasses
                            .get( SummaryExtent.UNMAPPED_MAPPABLE_COLUMN_COUNT );
        
        if ( ICount != null ) {
            iUnmappedNodes = ICount.intValue();            

            // must now remove this entry so the mapping classes map will only contain
            //  keys that are mapping classes:
            this.hmapMappingClasses.remove( SummaryExtent.UNMAPPED_MAPPABLE_COLUMN_COUNT );
        } else {
            iUnmappedNodes = 0;
        }
    }
    
    public int getUnmappedNodeCount() {
        return iUnmappedNodes;
    }

    /**
     * Gets the height.
     * @return the height
     */
    @Override
    public double getHeight() {
        return height;
    }
    
    /**
     * Gets the mapping reference <code>EObject</code>.
     * @return the mapping reference or <code>null</code>
     */
    @Override
    public EObject getMappingReference() {
        return mappingRef;
    }
    
    /**
     * Gets the y-coordinate offset from <pre>y = 0</pre>.
     * @return the offset
     */
    @Override
    public double getOffset() {
        return offset;
    }
    
    /**
     * Gets the number of unmapped nodes that are <strong>not</strong> required to be mapped.
     * @return the count
     */
    @Override
    public int getUnmappedOptionalNodeCount() {
    	return unmappedOptional;
    }
    
    /**
     * Gets the number of unmapped nodes that are required to be mapped.
     * @return the count
     */
    @Override
    public int getUnmappedRequiredNodeCount() {
    	return unmappedRequired;
    }
    
	@Override
    public int hashCode() {
		int result = HashCodeUtil.hashCode((int)offset, isCompletelyMapped());
	    result = HashCodeUtil.hashCode(result, height);
	    result = HashCodeUtil.hashCode(result, mappingRef);
	    return HashCodeUtil.hashCode(result, isMappingRequired());
	}
	
    @Override
    public void increaseHeight(double theIncrease) {
    	if (theIncrease < 0) {
    	    throw new IllegalArgumentException("Height increase <" + theIncrease + "> is less than zero."); //$NON-NLS-2$ //$NON-NLS-1$
    	}
        height += theIncrease;
    }
    
    @Override
    public void incrementUnmappedOptionNodeCount() {
    	unmappedOptional++;
    }
    
    @Override
    public void incrementUnmappedRequiredNodeCount() {
    	unmappedRequired++;
    }
    
    /**
     * Indicates if the extent has been completely mapped.
     * @return <code>true</code> if all mappings are complete; <code>false</code> otherwise.
     */
    @Override
    public boolean isCompletelyMapped() {
    	return completelyMapped;
    }
    
    /**
     * Indicates if the schema requires this extent to be mapped.
     * @return <code>true</code> if extents must be mapped; <code>false</code> otherwise.
     */
    @Override
    public boolean isMappingRequired() {
    	return mappingRequired;
    }
    
    /**
     * Gets a parameterized string representation suitable for debugging.
     * @return a string representation
     */
    @Override
    public String paramString() {
        return new StringBuffer().append("offset=").append(offset) //$NON-NLS-1$
                                 .append(", height=").append(height) //$NON-NLS-1$
                                 .append(", completely mapped=").append(isCompletelyMapped()) //$NON-NLS-1$
                                 .append(", mapping required=").append(isMappingRequired()) //$NON-NLS-1$
                                 .append(", unmapped optional=").append(getUnmappedOptionalNodeCount()) //$NON-NLS-1$
                                 .append(", unmapped required=").append(getUnmappedRequiredNodeCount()) //$NON-NLS-1$
                                 .append(", mapping reference=").append(mappingRef) //$NON-NLS-1$
                                 .append(", document node reference=").append(documentNodeRef) //$NON-NLS-1$
                                 .toString();
    }
    
    @Override
    public void setOffset(double theOffset) {
    	offset = theOffset;
    }
    
    @Override
    public void setCompletelyMapped(boolean theMappedFlag) {
    	completelyMapped = theMappedFlag;
    }
    
    @Override
    public void setMappingRequired(boolean theRequiredFlag) {
    	mappingRequired = theRequiredFlag;
    }
    
    /**
     * Gets the number of unmapped nodes that are <strong>not</strong> required to be mapped.
     * @return the count
     */
    @Override
    public void setUnmappedOptionalNodeCount(int theCount) {
    	unmappedOptional = (theCount < 0) ? 0 : theCount;
    }
    
    /**
     * Gets the number of unmapped nodes that are required to be mapped.
     * @return the count
     */
    @Override
    public void setUnmappedRequiredNodeCount(int theCount) {
    	unmappedRequired = (theCount < 0) ? 0 : theCount;
    }
    
    /**
     * Gets a string representation.
     * @return a string representation
     */
    @Override
    public String toString() {
        return paramString();
    }
    
	/**
	 * @return
	 */
	@Override
    public String getPathToDocumentRoot() {
		return pathToDocumentRoot;
//		return "/Root/Sample/StringTo/XmlDocument/Node/attribute"; //$NON-NLS-1$
	}

	/**
	 * @param string
	 */
	@Override
    public void setPathToDocumentRoot(String string) {
		pathToDocumentRoot = string;
	}

	/**
	 * @return
	 */
	@Override
    public String getXsdQualifiedName() {
		return xsdQualifiedName;
	}

	/**
	 * @param string
	 */
	@Override
    public void setXsdQualifiedName(String string) {
		xsdQualifiedName = string;
	}

	/**
	 * @return
	 */
	@Override
    public String getXsdTargetNamespace() {
		return xsdTargetNamespace;
	}

	/**
	 * @param string
	 */
	@Override
    public void setXsdTargetNamespace(String string) {
		xsdTargetNamespace = string;
	}
    
    public int getMappingClassColumnCount() {
        int iCount = 0;
        
        Iterator it = getMappingClasses().values().iterator();
        
        while( it.hasNext() ) {
            Integer ICount = (Integer)it.next();
            
            if ( ICount != null ) {
                iCount += ICount.intValue(); 
            }
        }
        return iCount;
    }
    
    public void setSomeMappingClassesAreVisible( boolean b ) {
        bSomeMappingClassesAreVisible = b;
    }

    public boolean getSomeMappingClassesAreVisible() {
        return bSomeMappingClassesAreVisible;
    }
}
