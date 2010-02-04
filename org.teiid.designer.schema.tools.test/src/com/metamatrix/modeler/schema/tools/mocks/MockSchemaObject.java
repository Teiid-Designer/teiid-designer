/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.schema.tools.mocks;

import java.util.List;
import org.eclipse.xsd.XSDComplexTypeContent;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTypeDefinition;
import com.metamatrix.modeler.schema.tools.model.schema.Column;
import com.metamatrix.modeler.schema.tools.model.schema.ISchemaModelCopyTraversalContext;
import com.metamatrix.modeler.schema.tools.model.schema.RootElement;
import com.metamatrix.modeler.schema.tools.model.schema.SchemaObject;
import com.metamatrix.modeler.schema.tools.model.schema.SchemaObjectKey;
import com.metamatrix.modeler.schema.tools.processing.RelationshipProcessor;

public class MockSchemaObject implements SchemaObject {

	public MockSchemaObject() {
		super();
		// TODO Auto-generated constructor stub
	}

	public SchemaObject copy(ISchemaModelCopyTraversalContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	public void addAttribute(Column col) {
		// TODO Auto-generated method stub

	}

	public String getSimpleName() {
		// TODO Auto-generated method stub
		return null;
	}

	public XSDTypeDefinition getType() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getElementTypeNamespace() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getNamespace() {
		// TODO Auto-generated method stub
		return null;
	}

	public void addParent(SchemaObject parent, int minOccurs, int maxOccurs) {
		// TODO Auto-generated method stub

	}

	public List getParents() {
		// TODO Auto-generated method stub
		return null;
	}

	public List getChildren() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isCanBeRoot() {
		// TODO Auto-generated method stub
		return false;
	}

	public List getAttributes() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setMustBeQualified() {
		// TODO Auto-generated method stub

	}

	public void setFileName(String fileName) {
		// TODO Auto-generated method stub

	}

	public String getFileName() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isWithinSelectedHierarchy() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setWithinSelectedHierarchy(boolean under) {
		// TODO Auto-generated method stub

	}

	public void setAllParentRepresentations(int representation,
			RelationshipProcessor processor) {
		// TODO Auto-generated method stub

	}

	public boolean isUseAsRoot() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setUseAsRoot(boolean useAsRoot) {
		// TODO Auto-generated method stub

	}

	public RootElement getRootRepresentation() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isSimpleElement(RelationshipProcessor processor) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean representAsTable() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setRepresentAsTable(boolean table) {
		// TODO Auto-generated method stub

	}

	public String getInputXPath() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getOutputXPath() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getRelativeXpath() {
		// TODO Auto-generated method stub
		return null;
	}

	public String recursiveGetXpath() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getCatalog() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getMinOccurs() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getMaxOccurs() {
		// TODO Auto-generated method stub
		return 0;
	}

	public List getAllModelColumns() {
		// TODO Auto-generated method stub
		return null;
	}

	public SchemaObjectKey getKey() {
		// TODO Auto-generated method stub
		return null;
	}

	public void cascadeRootSelection(boolean b) {
		// TODO Auto-generated method stub

	}

	public XSDSchema getSchema() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean hasComplexTypeDefinition() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean hasSimpleTypeDefinition() {
		// TODO Auto-generated method stub
		return false;
	}

	public List getAttributeList() {
		// TODO Auto-generated method stub
		return null;
	}

	public XSDComplexTypeContent getContent() {
		// TODO Auto-generated method stub
		return null;
	}

	public XSDSimpleTypeDefinition getTextType() {
		// TODO Auto-generated method stub
		return null;
	}

}
