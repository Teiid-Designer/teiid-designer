/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.mapping.ui.editor;

import org.eclipse.emf.ecore.EObject;
import com.metamatrix.core.util.HashCodeUtil;

/**
 * The <code>MappingExtent</code> class provides information about one XML document mapping reference. The mapping reference will
 * either be a mapping class <code>EObject</code>, a mapping attribute <code>EObject</code>, or <code>null</code>. A mapping
 * reference is either required (the default) or optional. <code>MappingExtent</code>s are displayed in the diagram portion of the
 * {@link MappingTransfromView} and is used as a source point when connecting an extent to a mapping class or a mapping attribute.
 * 
 * @author Barry LaFond & Dan Florian
 * @since 3.0`
 * @version 1.0
 */

public class MappingExtent {
    // jh Lyra: I am dropping 'final' so SummaryExtent can subclass this.

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    /** Indicates if the extent is completely mapped. Defaults to <code>true</code>. */
    protected boolean completelyMapped = true;

    /** The document node reference meta object. Some Element or Attribute? */
    protected EObject documentNodeRef;

    /** The height. */
    protected double height = 0;

    /** The associated mapping reference meta object. */
    protected EObject mappingRef;

    /** Indicates if the schema requries this extent to be mapped. Defaults to <code>false</code>. */
    protected boolean mappingRequired = false;

    /** Number of unmapped nodes that don't require to be mapped. */
    protected int unmappedOptional = 0;

    /** Number of unmapped nodes that require to be mapped. */
    protected int unmappedRequired = 0;

    /** The y-coordinate offset from y=0. */
    protected double offset = 0;

    /** The path to root string */
    protected String pathToDocumentRoot;

    /** Xsd fully qualified element reference */
    protected String xsdQualifiedName;

    /** Xsd fully qualified element reference */
    protected String xsdTargetNamespace;

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    public MappingExtent( EObject theMappingReference ) {
        mappingRef = theMappingReference;
    }

    /**
     * Constructs a <code>MappingExtent</code>.
     * 
     * @param theOffset the y offset
     * @param theHeight the height
     * @param theMappingReference the mapping <code>EObject</code> reference
     * @param theDocumentNodeReference the document node <code>EObject</code> reference
     * @throws IllegalArgumentException if the offset or height is less than zero or if the mapping reference is <code>null</code>
     */
    public MappingExtent( double theOffset,
                          double theHeight,
                          EObject theMappingReference,
                          EObject theDocumentNodeReference ) {
        this(theMappingReference);
        documentNodeRef = theDocumentNodeReference;
        setOffset(theOffset);
        increaseHeight(theHeight);
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Signifies if the mapping classes, offsets, and required mapping are equal.
     */
    @Override
    public boolean equals( Object theObject ) {
        boolean result = false;
        if (theObject instanceof MappingExtent) {
            MappingExtent extent = (MappingExtent)theObject;
            if (offset == extent.getOffset() && height == extent.getHeight()) {
                if (mappingRef == null) {
                    result = (extent.getMappingReference() == null);
                } else {
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
                            } else {
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
     * 
     * @return the document node reference or <code>null</code>
     */
    public EObject getDocumentNodeReference() {
        return documentNodeRef;
    }

    /**
     * Gets the height.
     * 
     * @return the height
     */
    public double getHeight() {
        return height;
    }

    /**
     * Gets the mapping reference <code>EObject</code>.
     * 
     * @return the mapping reference or <code>null</code>
     */
    public EObject getMappingReference() {
        return mappingRef;
    }

    /**
     * Gets the y-coordinate offset from
     * 
     * <pre>
     * y = 0
     * </pre>
     * 
     * .
     * 
     * @return the offset
     */
    public double getOffset() {
        return offset;
    }

    /**
     * Gets the number of unmapped nodes that are <strong>not</strong> required to be mapped.
     * 
     * @return the count
     */
    public int getUnmappedOptionalNodeCount() {
        return unmappedOptional;
    }

    /**
     * Gets the number of unmapped nodes that are required to be mapped.
     * 
     * @return the count
     */
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

    public void increaseHeight( double theIncrease ) {
        if (theIncrease < 0) {
            throw new IllegalArgumentException("Height increase <" + theIncrease + "> is less than zero."); //$NON-NLS-2$ //$NON-NLS-1$
        }
        height += theIncrease;
    }

    public void incrementUnmappedOptionNodeCount() {
        unmappedOptional++;
    }

    public void incrementUnmappedRequiredNodeCount() {
        unmappedRequired++;
    }

    /**
     * Indicates if the extent has been completely mapped.
     * 
     * @return <code>true</code> if all mappings are complete; <code>false</code> otherwise.
     */
    public boolean isCompletelyMapped() {
        return completelyMapped;
    }

    /**
     * Indicates if the schema requires this extent to be mapped.
     * 
     * @return <code>true</code> if extents must be mapped; <code>false</code> otherwise.
     */
    public boolean isMappingRequired() {
        return mappingRequired;
    }

    /**
     * Gets a parameterized string representation suitable for debugging.
     * 
     * @return a string representation
     */
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

    public void setOffset( double theOffset ) {
        offset = theOffset;
    }

    public void setCompletelyMapped( boolean theMappedFlag ) {
        completelyMapped = theMappedFlag;
    }

    public void setMappingRequired( boolean theRequiredFlag ) {
        mappingRequired = theRequiredFlag;
    }

    /**
     * Gets the number of unmapped nodes that are <strong>not</strong> required to be mapped.
     * 
     * @return the count
     */
    public void setUnmappedOptionalNodeCount( int theCount ) {
        unmappedOptional = (theCount < 0) ? 0 : theCount;
    }

    /**
     * Gets the number of unmapped nodes that are required to be mapped.
     * 
     * @return the count
     */
    public void setUnmappedRequiredNodeCount( int theCount ) {
        unmappedRequired = (theCount < 0) ? 0 : theCount;
    }

    /**
     * Gets a string representation.
     * 
     * @return a string representation
     */
    @Override
    public String toString() {
        return paramString();
    }

    /**
     * @return
     */
    public String getPathToDocumentRoot() {
        return pathToDocumentRoot;
        //		return "/Root/Sample/StringTo/XmlDocument/Node/attribute"; //$NON-NLS-1$
    }

    /**
     * @param string
     */
    public void setPathToDocumentRoot( String string ) {
        pathToDocumentRoot = string;
    }

    /**
     * @return
     */
    public String getXsdQualifiedName() {
        return xsdQualifiedName;
    }

    /**
     * @param string
     */
    public void setXsdQualifiedName( String string ) {
        xsdQualifiedName = string;
    }

    /**
     * @return
     */
    public String getXsdTargetNamespace() {
        return xsdTargetNamespace;
    }

    /**
     * @param string
     */
    public void setXsdTargetNamespace( String string ) {
        xsdTargetNamespace = string;
    }

}
