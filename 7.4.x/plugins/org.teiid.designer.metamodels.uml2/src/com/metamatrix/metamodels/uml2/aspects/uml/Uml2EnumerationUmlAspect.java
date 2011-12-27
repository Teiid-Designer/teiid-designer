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
import org.eclipse.uml2.uml.Enumeration;
import org.teiid.core.TeiidRuntimeException;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.metamodels.uml2.Uml2Plugin;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;
import com.metamatrix.modeler.core.metamodel.aspect.uml.UmlClassifier;

/**
 * Enumeration Aspect
 */
public class Uml2EnumerationUmlAspect
	extends AbstractUml2NamedElementUmlAspect
	implements UmlClassifier {

	/**
	 * @param entity
	 */
	public Uml2EnumerationUmlAspect(MetamodelEntity entity) {
		super(entity);
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlClassifier#getRelationships(java.lang.Object)
	 */
	public Collection getRelationships(Object eObject) {
		return new ArrayList();
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlClassifier#getSupertypes(java.lang.Object)
	 */
	public Collection getSupertypes(Object eObject) {
		return new ArrayList();
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlDiagramAspect#getSignature(java.lang.Object, int)
	 */
	public String getSignature(Object eObject, int showMask) {
		final Enumeration umlClass = assertUmlEnumeration(eObject);
		StringBuffer result = new StringBuffer();
		switch (showMask) {
			case 1 :
				//Name
				appendName(umlClass, result);
				break;
			case 2 :
				//Stereotype
				appendStereotype(umlClass, result, true);
				appendName(umlClass, result);
				break;
			case 3 :
				//Name and Stereotype
				appendStereotype(umlClass, result, true);
				appendName(umlClass, result);
				break;
			default :
				final int params = showMask;
				final String msg = Uml2Plugin.Util.getString("Uml2EnumerationUmlAspect.Invalid_showMask_for_getSignature_{0}_1",params); //$NON-NLS-1$
				throw new TeiidRuntimeException(msg);
		}
		return result.toString();
	}

	@Override
    public String getStereotype(Object eObject) {
		return Uml2Plugin.getPluginResourceLocator().getString("_UI_Enumeration_type"); //$NON-NLS-1$
	}

	protected Enumeration assertUmlEnumeration(Object eObject) {
		CoreArgCheck.isInstanceOf(Enumeration.class, eObject);

		return (Enumeration) eObject;
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlClassifier#isAbstract(java.lang.Object)
	 */
	public boolean isAbstract(Object eObject) {
		return false;
	}

}
