/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.xml;

import java.util.Collection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.xsd.XSDSchema;
import org.teiid.core.designer.ModelerCoreException;

/**
 * XmlDocumentBuilder
 *
 * @since 8.0
 */
public interface XmlDocumentBuilder {

    /**
     * Build the XML Document from the given Root XmlElement down.
     * 
     * @param XmlElement root - the root element of the document
     * @param IProgressMonitor monitor - progress monitor (may be null)
     * @return int the number of elements built
     */
    int buildDocument( final XmlElement root,
                       final IProgressMonitor monitor ) throws ModelerCoreException;

    /**
     * Update the XML Document from the given Root XmlElement down.
     * 
     * @param XmlElement root - the root element of the document
     * @param IProgressMonitor monitor - progress monitor (may be null)
     * @return int the number of elements updated
     */
    int updateFromSchema( final XmlElement root, // NO_UCD
                          final IProgressMonitor monitor ) throws ModelerCoreException; // NO_UCD

    /**
     * Sets the numberOfLevelsToBuild.
     * 
     * @param numberOfLevelsToBuild The numberOfLevelsToBuild to set
     */
    void setNumberOfLevelsToBuild( final int numberOfLevelsToBuild );

    /**
     * Returns the collection of ObjectID objects for any recursive nodes in the tree
     * 
     * @return Colection of ObjectIDs... never null
     */
    Collection getRecursiveElementObjectIDs(); // NO_UCD

    /**
     * Set the root document elements to use when building the document
     * 
     * @param xmlFragments the root xml fragments - may NOT be null.
     * @throws ModelerCoreException if any item in collection not instanceof xmlFragment
     */
    void setFragments( final Collection xmlFragments ) throws ModelerCoreException; // NO_UCD

    /**
     * Build xml fragment for each root XSDElementDefinition
     * 
     * @param schema - may not be null
     * @return Collection of XmlFragments for each root XSDElementDefinition
     * @throws ModelerCoreException if there are problems building fragments
     */
    Collection buildXmlFragments( final XSDSchema schema ) throws ModelerCoreException; // NO_UCD

    /**
     * Setter for the addNamespaces attribute. If true, namespaces will automatically be added at the document level for each
     * schema referenced in the build process. Default is true, attribute only needs to be set if no namespaces are desired
     * 
     * @param addNamespaces
     */
    public void setAddNamespaces( final boolean addNamespaces );

}
