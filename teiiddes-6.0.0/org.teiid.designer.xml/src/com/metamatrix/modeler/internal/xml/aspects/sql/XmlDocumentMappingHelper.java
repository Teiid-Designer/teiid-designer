/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.xml.aspects.sql;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.mapping.Mapping;
import org.eclipse.xsd.XSDComponent;
import org.eclipse.xsd.XSDTypeDefinition;
import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.metamodels.transformation.MappingClass;
import com.metamatrix.metamodels.transformation.MappingClassColumn;
import com.metamatrix.metamodels.transformation.StagingTable;
import com.metamatrix.metamodels.transformation.TreeMappingRoot;
import com.metamatrix.metamodels.xml.XmlContainerNode;
import com.metamatrix.metamodels.xml.XmlDocumentEntity;
import com.metamatrix.metamodels.xml.XmlElement;
import com.metamatrix.metamodels.xml.util.XmlDocumentUtil;

/**
 * XmlDocumentMappingHelper
 */
public class XmlDocumentMappingHelper {

    private final List treeMappingRoots;    // instances of TreeMappingRoot
    private final Map xmlDocNodeToMappingClassColumn;
    private final Map xmlDocNodeToMappingClass;
    private final Map xmlDocNodeToStagingTables;

    /**
     * Construct an instance of XmlDocumentMappingHelper.
     * @see #initialize()
     */
    public XmlDocumentMappingHelper( final List treeMappingRoots ) {
        ArgCheck.isNotNull(treeMappingRoots);
        this.treeMappingRoots = treeMappingRoots;
        this.xmlDocNodeToMappingClassColumn = new HashMap();
        this.xmlDocNodeToMappingClass = new HashMap();
        this.xmlDocNodeToStagingTables = new HashMap();
    }

    /**
     * Method to initialize the helper.  This method <i>must</i> be called before the
     * {@link #getMappingClass(XmlDocumentEntity)} or {@link #getMappingClassColumn(XmlDocumentEntity)}
     * methods (or they will always return null!).
     */
    public void initialize() {
        this.xmlDocNodeToMappingClass.clear();
        this.xmlDocNodeToMappingClassColumn.clear();
        // Iterate through the TreeMappingRoot recursively ...
        final Iterator iter = this.treeMappingRoots.iterator();
        while (iter.hasNext()) {
            final TreeMappingRoot treeMappingRoot = (TreeMappingRoot)iter.next();
            process(treeMappingRoot);
        }
    }

    /**
     * Method to obtain the {@link MappingClassColumn} that is mapped to the supplied
     * {@link XmlDocumentEntity}.
     * @param xmlNode the XML document node
     * @return the mapping class column bound to the supplied node, or null if the
     * node has no such binding
     */
    public MappingClassColumn getMappingClassColumn( final XmlDocumentEntity xmlNode ) {
        return (MappingClassColumn)this.xmlDocNodeToMappingClassColumn.get(xmlNode);
    }

    /**
     * Method to obtain the {@link MappingClass} that is mapped to the supplied
     * {@link XmlDocumentEntity}.
     * @param xmlNode the XML document node
     * @return the mapping class bound to the supplied node, or null if the
     * node has no such binding
     */
    public MappingClass getMappingClass( final XmlDocumentEntity xmlNode ) {
        return (MappingClass)this.xmlDocNodeToMappingClass.get(xmlNode);
    }

    /**
     * Method to obtain the {@link MappingClass} that is mapped to the supplied
     * {@link XmlDocumentEntity}.
     * @param xmlNode the XML document node
     * @return the mapping class bound to the supplied node, or null if the
     * node has no such binding
     */
    public StagingTable[] getStagingTables( final XmlDocumentEntity xmlNode ) {
        return (StagingTable[])this.xmlDocNodeToStagingTables.get(xmlNode);
    }

    /**
     * Method to obtain the root {@link MappingClass} that compliments the
     * mapping class bound to the supplied {@link XmlElement}.  If the
     * mapping class bound to this XML element is marked with recursionAllowed
     * and recursive as true, then there should be an XML element upward within
     * the parent hierarchy that is also bound to a mapping class.  This
     * complimentary mapping class represents the re-entrant point in the document
     * for the recursion.  Null will be returned if the supplied XML element
     * is null, is not bound to a mapping class, the mapping class it is bound to is
     * not marked as recursionAllowed and recursive, or the parent mapping class could
     * not be found.
     * @param element the XML element node
     * @return the parent mapping class
     */
    public MappingClass getRecusionRootMappingClass( final XmlElement xmlElement ) {
        MappingClass mc = (MappingClass)this.xmlDocNodeToMappingClass.get(xmlElement);

        // The mapping class must be marked for recursion before proceeding
        if (mc != null && mc.isRecursionAllowed() && mc.isRecursive()) {

            // Get the XSD type of the Xml element
            final XSDComponent xsdComponent = xmlElement.getXsdComponent();
            XSDTypeDefinition type = XmlDocumentUtil.findXSDType(xsdComponent);

            // The search logic currently works by matching XSD types
            if (xsdComponent == null) {
                return null;
            }

            // Perform an upward search on the XML document trying to match XSD types
            EObject owner = xmlElement.eContainer();
            while (owner != null) {
                if (owner instanceof XmlElement) {
                    // The XML element must be bound to a mapping class ...
                    XSDComponent ownerXsdComponent = ((XmlElement)owner).getXsdComponent();
                    XSDTypeDefinition ownerType = XmlDocumentUtil.findXSDType(ownerXsdComponent);

                    // If the types match then check if it is bound to a mapping class
                    if (type != null && type == ownerType) {
                        mc = (MappingClass)this.xmlDocNodeToMappingClass.get(owner);
                        if (mc != null) {
                            return mc;
                        }
                        // Check if the mapping class is bound to the parent container node
                        if (owner.eContainer() instanceof XmlContainerNode) {
                            mc = (MappingClass)this.xmlDocNodeToMappingClass.get(owner.eContainer());
                            if (mc != null) {
                                return mc;
                            }
                        }
                    }
                }
                owner = owner.eContainer();
            }
        }
        return null;
    }

    /**
     * Recursive method to process the supplied Mapping object and its nested mappings.
     * This is the method that populates the XmlDocumentEntity-to-MappingClass and
     * XmlDocumentEntity-to-MappingClassColumn maps.
     * @param mapping the mapping to be processed; may not be null
     */
    protected void process( final Mapping mapping ) {
        final List inputs = mapping.getInputs();
        final List outputs = mapping.getOutputs();
        // If there is NOT at least one input and at least one output, then skip entirely ...
        if ( inputs.isEmpty() || outputs.isEmpty() ) {
            return;
        }
        // There may be more than one output (i.e., in the case of a choice, one mapping class column
        // may be mapped to more than one XmlDocumentNode), but there should only be one input.
        // (See defect 10880)
        final Object mcObject = inputs.get(0);
        final Iterator outputIter = outputs.iterator();
        while (outputIter.hasNext()) {
            final Object xmlDocNode = outputIter.next();
            if ( mcObject instanceof StagingTable ) {
                this.addStagingTableToMap(xmlDocNode,(StagingTable)mcObject);
            } else if ( mcObject instanceof MappingClass ) {
                this.xmlDocNodeToMappingClass.put(xmlDocNode,mcObject);
            } else if ( mcObject instanceof MappingClassColumn ) {
                this.xmlDocNodeToMappingClassColumn.put(xmlDocNode,mcObject);
            }
        }

        // Process the nested mappings
        final Iterator iter = mapping.getNested().iterator();
        while (iter.hasNext()) {
            final Mapping nested = (Mapping)iter.next();
            process(nested);
        }
    }

    private void addStagingTableToMap(final Object xmlDocNode, final StagingTable table) {
        if (xmlDocNode == null || table == null) {
            return;
        }

        StagingTable[] tables = (StagingTable[]) this.xmlDocNodeToStagingTables.get(xmlDocNode);

        // Create a new StagingTable array if it does not yet exist
        if (tables == null) {
            tables = new StagingTable[]{table};
        }
        // Add the value to the existing array using the ArrayList utility class
        else {
            ArrayList tmp = new ArrayList();
            tmp.addAll(Arrays.asList(tables));
            tmp.add(table);
            tables = new StagingTable[tmp.size()];
            tmp.toArray(tables);
        }
        this.xmlDocNodeToStagingTables.put(xmlDocNode,tables);
    }


}
