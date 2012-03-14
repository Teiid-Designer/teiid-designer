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
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDParticle;

public class SchemaTreeContentProvider extends AdapterFactoryContentProvider {

	public SchemaTreeContentProvider(AdapterFactory adapterFactory) {
		super(adapterFactory);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider#getChildren(java.lang.Object)
	 */
	@Override
	public Object[] getChildren(Object object) {
		Object[] result = super.getChildren(object);
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
		Object[] result = super.getElements(object);
		Collection<Object> filteredElements = new ArrayList<Object>();
		
		for( Object child : result ) {
			if( showObject(child) ) {
				filteredElements.add(child);
			}
		}
		
		return filteredElements.toArray( new Object[filteredElements.size()]);
	  }
	
	
	private boolean showObject(Object object) {
		if( object instanceof XSDComplexTypeDefinition ) {
			if( ((XSDComplexTypeDefinition)object).getName().toUpperCase().equals("ANYTYPE") ) { //$NON-NLS-1$
				return false;
			}
		}
		if( object instanceof XSDParticle ) {
			return true;
		}
		
		return true;
	}
}
