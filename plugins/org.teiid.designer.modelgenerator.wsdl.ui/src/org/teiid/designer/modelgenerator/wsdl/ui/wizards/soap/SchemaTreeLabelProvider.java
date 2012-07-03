/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.modelgenerator.wsdl.ui.wizards.soap;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.xsd.XSDAttributeUse;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.impl.XSDParticleImpl;
import org.teiid.designer.modelgenerator.wsdl.ui.wizards.soap.SchemaTreeModel.SchemaNode;

import com.metamatrix.modeler.modelgenerator.wsdl.ui.ModelGeneratorWsdlUiConstants;
import com.metamatrix.modeler.modelgenerator.wsdl.ui.internal.util.ModelGeneratorWsdlUiUtil;

public class SchemaTreeLabelProvider extends AdapterFactoryLabelProvider {
	private final Image XSD_COMPLEX_ELEMENT_ICON_IMG = 
			ModelGeneratorWsdlUiUtil.getImage(ModelGeneratorWsdlUiConstants.Images.XSD_COMPLEX_ELEMENT_ICON);
	
	public SchemaTreeLabelProvider(AdapterFactory adapterFactory) {
		super(adapterFactory);
	}

	@Override
	public Image getImage(Object object) {
		if( object instanceof SchemaNode ) {
			// TODO Auto-generated method stub
			SchemaNode node = (SchemaNode)object;
			if( node.getElement() instanceof XSDParticle) {
				boolean doShow = false;
				Object content = ((XSDParticleImpl) node.getElement()).getContent();
				if( content instanceof XSDElementDeclaration ) {
					doShow =  ! (((XSDElementDeclaration )content).getType() instanceof XSDComplexTypeDefinition);
				}
				else {
					doShow = content instanceof XSDModelGroup;
				}
				
				if( ! doShow ) {
					return XSD_COMPLEX_ELEMENT_ICON_IMG;
				}
			}
			return super.getImage(node.getElement());
		}
		
		return super.getImage(object);
	}

	@Override
	public String getText(Object object) {
		if( object instanceof SchemaNode ) {
			SchemaNode node = (SchemaNode) object;
			if( node.getElement() instanceof XSDAttributeUse ) {
				XSDAttributeUse attributeUse = (XSDAttributeUse)node.getElement();
				String dTypeString = attributeUse.getAttributeDeclaration().getType().getAliasName();
				String name = super.getText(node.getElement());
				if( dTypeString != null ) {
					name = name + " : " + dTypeString; //$NON-NLS-1$
				}
				return name;
			}
			return super.getText(node.getElement());
		} else {
			return super.getText(object);
		}
	}
	
	

	
}
