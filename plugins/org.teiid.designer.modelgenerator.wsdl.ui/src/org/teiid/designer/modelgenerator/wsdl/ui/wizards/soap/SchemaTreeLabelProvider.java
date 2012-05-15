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
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.impl.XSDParticleImpl;

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
		// TODO Auto-generated method stub
		if( object instanceof XSDParticle) {
			boolean doShow = false;
			Object content = ((XSDParticleImpl) object).getContent();
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
		return super.getImage(object);
	}

	
}
