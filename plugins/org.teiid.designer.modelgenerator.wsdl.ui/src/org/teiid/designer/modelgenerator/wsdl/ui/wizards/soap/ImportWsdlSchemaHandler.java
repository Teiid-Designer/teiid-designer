/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.modelgenerator.wsdl.ui.wizards.soap;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTypeDefinition;

import com.metamatrix.modeler.modelgenerator.wsdl.model.Model;
import com.metamatrix.modeler.modelgenerator.wsdl.model.ModelGenerationException;
import com.metamatrix.modeler.modelgenerator.wsdl.model.Operation;
import com.metamatrix.modeler.modelgenerator.wsdl.model.Part;
import com.metamatrix.modeler.modelgenerator.wsdl.ui.internal.wizards.WSDLImportWizardManager;

public class ImportWsdlSchemaHandler {
	
	WSDLImportWizardManager importManager;

	public ImportWsdlSchemaHandler(WSDLImportWizardManager manager) {
		super();
		this.importManager = manager;
	}
	
	public List<Object> getSchemaForSelectedOperation(final int type, ProcedureGenerator generator) {

		Model wsdlModel = null;
		Object elementDeclaration = null;
		
		try {
			wsdlModel = importManager.getWSDLModel();
		} catch (ModelGenerationException e) {
			throw new RuntimeException(e);
		}

		XSDSchema[] schemas = wsdlModel.getSchemas();

		Operation selectedOperation = generator.getOperation();
		String partElementName = null;
		Part[] partArray = null;

		if (type == ProcedureInfo.REQUEST) {
			if (selectedOperation.getInputMessage() != null) {
				partArray = selectedOperation.getInputMessage().getParts();
			}
		} else {
			if (selectedOperation.getOutputMessage() != null) {
				partArray = selectedOperation.getOutputMessage().getParts();
			}
		}

		List<Object> elementArrayList = new ArrayList<Object>();

		for (Part part : partArray) {
			partElementName = getPartElementName(part);
			elementDeclaration = null;

			boolean foundElement = false;

			for (XSDSchema schema : schemas) {
				EList<XSDTypeDefinition> types = schema.getTypeDefinitions();
				for (XSDTypeDefinition xsdType : types) {
					String elementName = xsdType.getName();
					if (elementName.equals(partElementName)) {
						elementDeclaration = xsdType;
						foundElement = true;
						elementArrayList.add(elementDeclaration);
						break;
					}
				}

				if (foundElement == true)
					continue;

				if (elementDeclaration == null) {

					EList<XSDElementDeclaration> elements = schema.getElementDeclarations();
					for (XSDElementDeclaration element : elements) {
						String elementName = element.getName();
						if (elementName.equals(partElementName)) {
							if (element.getTypeDefinition() instanceof XSDSimpleTypeDefinition) {
								elementDeclaration = element;
							} else {
								elementDeclaration = element.getTypeDefinition();
							}

							foundElement = true;
							elementArrayList.add(elementDeclaration);
							break;
						}
					}
				}

				// We already found our element. No need to look through anymore
				// schemas
				if (foundElement) {
					foundElement = false;
					break;
				}
			}

		}

		return elementArrayList;
	}
	

	private String getPartElementName(Part part) {
		String partElementName = null;
		
		partElementName = part.getTypeName();
	    if (partElementName == null){
			partElementName = part.getElementName();
	    }
		
		return partElementName;
	}

}
