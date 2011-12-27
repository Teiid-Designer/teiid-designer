/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.schema.tools.model.schema;

import java.util.List;
import org.eclipse.xsd.XSDComplexTypeContent;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTypeDefinition;
import com.metamatrix.modeler.schema.tools.processing.RelationshipProcessor;

public interface SchemaObject {

	public abstract SchemaObject copy(ISchemaModelCopyTraversalContext ctx);

	public abstract boolean equals(Object obj);

	public abstract void addAttribute(Column col);

	public abstract String getSimpleName();

	public abstract XSDTypeDefinition getType();

	// for debugging only
	public abstract String getElementTypeNamespace();

	public abstract String getName();
	
	public abstract String getNamespace();

	public abstract void addParent(SchemaObject parent, int minOccurs,
			int maxOccurs);

	public abstract List getParents();

	public abstract List getChildren();

	public abstract boolean isCanBeRoot();

	public abstract List getAttributes();

	public abstract String toString();

	public abstract void setMustBeQualified();

	public abstract void setFileName(String fileName);

	public abstract String getFileName();

	public abstract boolean isWithinSelectedHierarchy();

	public abstract void setWithinSelectedHierarchy(boolean under);

	public abstract void setAllParentRepresentations(int representation,
			RelationshipProcessor processor);

	public abstract boolean isUseAsRoot();

	public abstract void setUseAsRoot(boolean useAsRoot);

	public abstract RootElement getRootRepresentation();

	public abstract boolean isSimpleElement(RelationshipProcessor processor);

	public abstract boolean representAsTable();
	
	public abstract void setRepresentAsTable(boolean table);

	public abstract String getInputXPath();
	
	public abstract String getOutputXPath();

	public abstract String getRelativeXpath();

	public abstract String recursiveGetXpath();
	
	public abstract String getCatalog();
	
	public abstract int getMinOccurs();
	
	public abstract int getMaxOccurs();

	public abstract List getAllModelColumns();
	
	public abstract SchemaObjectKey getKey();

	public abstract void cascadeRootSelection(boolean b);

	public abstract XSDSchema getSchema();

	public abstract boolean hasComplexTypeDefinition();

	public abstract boolean hasSimpleTypeDefinition();

	public abstract List getAttributeList();

	public abstract XSDComplexTypeContent getContent();

	public abstract XSDSimpleTypeDefinition getTextType();

}
