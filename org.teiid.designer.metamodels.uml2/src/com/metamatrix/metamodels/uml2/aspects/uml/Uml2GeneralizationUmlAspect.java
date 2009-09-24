/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.uml2.aspects.uml;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Generalization;

import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.metamodels.uml2.Uml2Plugin;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;
import com.metamatrix.modeler.core.metamodel.aspect.uml.UmlGeneralization;

/**
 * Aspect for Generalization "is-a-kind-of" relationship
 * A generalization is a relationship between a general thing (superclass or parent)
 * and a more specific kind of that thing (subclass or child).
 */
public class Uml2GeneralizationUmlAspect
	extends AbstractUml2UmlAspect
	implements UmlGeneralization {

	/**
	 * @param entity
	 */
	public Uml2GeneralizationUmlAspect(MetamodelEntity entity) {
		super();
		setMetamodelEntity(entity);
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlGeneralization#getSpecific(java.lang.Object)
	 */
	public EObject getSpecific(Object eObject) {
		final Generalization generalization = assertGeneralization(eObject);
		return generalization.getSpecific();
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlGeneralization#getGeneral(java.lang.Object)
	 */
	public EObject getGeneral(Object eObject) {
		final Generalization generalization = assertGeneralization(eObject);
		return generalization.getGeneral();
	}
	
	protected Generalization assertGeneralization(Object eObject) {
		ArgCheck.isInstanceOf(Generalization.class, eObject);
		return (Generalization) eObject;
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlDiagramAspect#getEditableSignature(java.lang.Object)
	 */
	public String getEditableSignature(Object eObject) {
		return ""; //$NON-NLS-1$;
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlDiagramAspect#getSignature(java.lang.Object, int)
	 */
	public String getSignature(Object eObject, int showMask) {
		return ""; //$NON-NLS-1$;
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlDiagramAspect#getStereotype(java.lang.Object)
	 */
	public String getStereotype(Object eObject) {
		return Uml2Plugin.getPluginResourceLocator().getString("_UI_Generalization_type"); //$NON-NLS-1$;
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlDiagramAspect#setSignature(java.lang.Object, java.lang.String)
	 */
	public IStatus setSignature(Object eObject, String newSignature) {
		throw new UnsupportedOperationException(Uml2Plugin.Util.getString("Uml2GeneralizationUmlAspect.Signature_may_not_be_set_on_a__1",getStereotype(eObject))); //$NON-NLS-1$
	}

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlRelationship#getName(java.lang.Object)
     */
    public String getName(Object eObject) {
        return StringUtil.Constants.EMPTY_STRING;
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlRelationship#getToolTip(java.lang.Object)
     */
    public String getToolTip(Object eObject) {
        final StringBuffer sb = new StringBuffer(200);
        sb.append(this.getStereotype(eObject));
        sb.append(' ');
        Classifier classifier = (Classifier)this.getSpecific(eObject);
        sb.append(classifier.getName());
        sb.append(" --> "); //$NON-NLS-1$
        classifier = (Classifier)this.getGeneral(eObject);
        sb.append(classifier.getName());
        return sb.toString();
    }

}
