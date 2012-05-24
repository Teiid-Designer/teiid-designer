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
		if( object instanceof SchemaNode ) {
			SchemaNode node = (SchemaNode)object;

			Collection<SchemaNode> result = node.getChildren(); 
			
			Collection<Object> filteredNodes = new ArrayList<Object>();
			
			if( node.getChildren().isEmpty() && node.getElement() instanceof XSDParticle ) {
				return super.getChildren(node.getElement());
			} else {
				for( Object child : result ) {
					if( showObject(child) ) {
						filteredNodes.add(child);
					}
				}
			}
			return filteredNodes.toArray( new Object[filteredNodes.size()]);
		} else {
			Object[] result = super.getChildren(object);
			
			Collection<Object> filteredNodes = new ArrayList<Object>();
			
			for( Object child : result ) {
				if( showObject(child) ) {
					filteredNodes.add(child);
				}
			}
			return filteredNodes.toArray( new Object[filteredNodes.size()]);
		}
	}

	@Override
	public Object[] getElements(Object object) {
		if (object instanceof SchemaNodeWrapper) {
			Collection<Object> filteredNodes = new ArrayList<Object>();

			for (SchemaNode child : ((SchemaNodeWrapper)object).getChildren()) {
				filteredNodes.add(child);
			}

			return filteredNodes.toArray(new Object[filteredNodes.size()]);
		}
		
		return new Object[0];
	}
	
		
	@Override
	public boolean hasChildren(Object object) {
		if( object instanceof SchemaNode ) {
			SchemaNode node = (SchemaNode)object;
			if( node.getElement() instanceof XSDAttributeUse ) {
				return super.hasChildren(node.getElement());	
			} else if( node.getChildren().isEmpty() && node.getElement() instanceof XSDParticle ) {
				return super.hasChildren(node.getElement());
			}
			return node.getChildren().size() > 0; 
		}
		
		return false;
	}

	private boolean showObject(Object object) {
		if( object instanceof SchemaNode ) {
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
		
		return false;
	}
}
