/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.modelgenerator.wsdl.ui.wizards.soap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.edit.provider.IStructuredItemContentProvider;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.xsd.XSDAttributeUse;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDParticle;
import org.teiid.designer.modelgenerator.wsdl.ui.wizards.soap.SchemaTreeModel.SchemaNode;

public class SchemaTreeContentProvider extends AdapterFactoryContentProvider {
	
	@SuppressWarnings("unused")
	private static final Class<?> IStructuredItemContentProviderClass = IStructuredItemContentProvider.class;

	public SchemaTreeContentProvider(AdapterFactory adapterFactory) {
		super(adapterFactory);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider#getChildren(java.lang.Object)
	 */
	@Override
	public Object[] getChildren(Object object) {
		
		//Object[] result = super.getChildren(object); 
		
		Collection<SchemaNode> result = ((SchemaNode)object).getChildren(); 
		
		Collection<Object> filteredChildren = new ArrayList<Object>();
		
		for( Object child : result ) {
			if( showObject(child) ) {
				filteredChildren.add(child);
			}
		}
		
		return filteredChildren.toArray( new Object[filteredChildren.size()]);
	}

	@Override
	  public Object [] getElements(Object object) {
		List<SchemaNode> elementList = (ArrayList<SchemaNode>)object;
		Object[] result = new Object[elementList.size()];
		int i = 0;
		for (Object obj:elementList){
			result[i++]=obj;
		}
		
		Collection<Object> filteredElements = new ArrayList<Object>();
		
		for( Object child : result ) {
			if( showObject(child) ) {
				filteredElements.add(child);
			}
		}
		
		return filteredElements.toArray( new Object[filteredElements.size()]);
	  }
	
		
	@Override
	public boolean hasChildren(Object object) {
		
		return ((SchemaNode)object).getChildren().size() > 0; 
	}

	private boolean showObject(Object object) {
		SchemaNode node = (SchemaNode) object;
		if( node.getElement() instanceof XSDComplexTypeDefinition ) {
			String name = ((XSDComplexTypeDefinition) node.getElement()).getName();
			if(name !=null && "ANYTYPE".equals(name.toUpperCase())) { //$NON-NLS-1$
				return false;
			}
		}
		if( node.getElement() instanceof XSDParticle ||  node.getElement() instanceof XSDAttributeUse ) {
			return true;
		}
		
		return true;
	}
}
