/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.uml2.aspects.uml;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.Interface;
import org.eclipse.uml2.uml.Property;
import org.teiid.core.TeiidRuntimeException;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.metamodels.uml2.Uml2Plugin;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;
import com.metamatrix.modeler.core.metamodel.aspect.uml.UmlClassifier;

/**
 * Interface Aspect
 */
public class Uml2InterfaceUmlAspect
	extends AbstractUml2NamedElementUmlAspect
	implements UmlClassifier {

	/**
	 * @param entity
	 */
	public Uml2InterfaceUmlAspect(MetamodelEntity entity) {
		super(entity);
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlClassifier#getRelationships(java.lang.Object)
	 */
	public Collection getRelationships(Object eObject) {
		final Collection result = new ArrayList();
		final Interface i = assertUmlInterface(eObject);
		List elist = i.getOwnedMembers();
		Iterator iter = elist.iterator();
		while (iter.hasNext()) {
			Object element = iter.next();
			if(element instanceof Property) {
				Property p = (Property) element;
				Association a = p.getAssociation();
				if( a != null ) {
					result.add(a);
				}
			}
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlClassifier#getSupertypes(java.lang.Object)
	 */
	public Collection getSupertypes(Object eObject) {
		Interface i = assertUmlInterface(eObject);
		return i.getGeneralizations();
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlClassifier#isAbstract(java.lang.Object)
	 */
	public boolean isAbstract(Object eObject) {
		return false;
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlDiagramAspect#getSignature(java.lang.Object, int)
	 */
	public String getSignature(Object eObject, int showMask) {
		final Interface umlInterface = assertUmlInterface(eObject);
		StringBuffer result = new StringBuffer();
		switch (showMask) {
			case 1 :
				//Name
				appendName(umlInterface, result);
				break;
			case 2 :
				//Stereotype
				appendStereotype(umlInterface, result, true);
				appendName(umlInterface, result);
				break;
			case 3 :
				//Name and Stereotype
				appendStereotype(umlInterface, result, true);
				appendName(umlInterface, result);
				break;
			default :
				final int params = showMask;
				final String msg = "Invalid showMask for getSignature" + params; //$NON-NLS-1$
				throw new TeiidRuntimeException(msg);
		}
		return result.toString();
	}
	
	protected Interface assertUmlInterface(Object eObject) {
		CoreArgCheck.isInstanceOf(Interface.class, eObject);
    
		return (Interface)eObject;
	}
	
	@Override
    public String getStereotype(Object eObject) {
		return Uml2Plugin.getPluginResourceLocator().getString("_UI_Interface_type"); //$NON-NLS-1$
	}

}
