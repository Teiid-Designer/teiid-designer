/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.schema.tools.mocks;

import java.util.List;
import org.eclipse.xsd.XSDComplexTypeContent;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTypeDefinition;
import org.teiid.designer.schema.tools.model.schema.Column;
import org.teiid.designer.schema.tools.model.schema.ISchemaModelCopyTraversalContext;
import org.teiid.designer.schema.tools.model.schema.RootElement;
import org.teiid.designer.schema.tools.model.schema.SchemaObject;
import org.teiid.designer.schema.tools.model.schema.SchemaObjectKey;
import org.teiid.designer.schema.tools.processing.RelationshipProcessor;

public class MockSchemaObject implements SchemaObject {

	public MockSchemaObject() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public SchemaObject copy(ISchemaModelCopyTraversalContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addAttribute(Column col) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getSimpleName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public XSDTypeDefinition getType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getElementTypeNamespace() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getNamespace() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addParent(SchemaObject parent, int minOccurs, int maxOccurs) {
		// TODO Auto-generated method stub

	}

	@Override
	public List getParents() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List getChildren() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isCanBeRoot() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List getAttributes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setMustBeQualified() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setFileName(String fileName) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getFileName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isWithinSelectedHierarchy() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setWithinSelectedHierarchy(boolean under) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setAllParentRepresentations(int representation,
			RelationshipProcessor processor) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isUseAsRoot() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setUseAsRoot(boolean useAsRoot) {
		// TODO Auto-generated method stub

	}

	@Override
	public RootElement getRootRepresentation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isSimpleElement(RelationshipProcessor processor) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean representAsTable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setRepresentAsTable(boolean table) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getInputXPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getOutputXPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRelativeXpath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String recursiveGetXpath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCatalog() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getMinOccurs() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getMaxOccurs() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List getAllModelColumns() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SchemaObjectKey getKey() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void cascadeRootSelection(boolean b) {
		// TODO Auto-generated method stub

	}

	@Override
	public XSDSchema getSchema() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasComplexTypeDefinition() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasSimpleTypeDefinition() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List getAttributeList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public XSDComplexTypeContent getContent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public XSDSimpleTypeDefinition getTextType() {
		// TODO Auto-generated method stub
		return null;
	}

}
