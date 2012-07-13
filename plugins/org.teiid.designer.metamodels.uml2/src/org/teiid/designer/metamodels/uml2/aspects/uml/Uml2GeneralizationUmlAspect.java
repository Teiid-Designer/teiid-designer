/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.uml2.aspects.uml;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Generalization;
import org.teiid.core.util.CoreArgCheck;
import org.teiid.core.util.CoreStringUtil;
import org.teiid.designer.core.metamodel.aspect.MetamodelEntity;
import org.teiid.designer.core.metamodel.aspect.uml.UmlGeneralization;
import org.teiid.designer.metamodels.uml2.Uml2Plugin;


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
	 * @See org.teiid.designer.core.metamodel.aspect.uml.UmlGeneralization#getSpecific(java.lang.Object)
	 */
	@Override
	public EObject getSpecific(Object eObject) {
		final Generalization generalization = assertGeneralization(eObject);
		return generalization.getSpecific();
	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.core.metamodel.aspect.uml.UmlGeneralization#getGeneral(java.lang.Object)
	 */
	@Override
	public EObject getGeneral(Object eObject) {
		final Generalization generalization = assertGeneralization(eObject);
		return generalization.getGeneral();
	}
	
	protected Generalization assertGeneralization(Object eObject) {
		CoreArgCheck.isInstanceOf(Generalization.class, eObject);
		return (Generalization) eObject;
	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.core.metamodel.aspect.uml.UmlDiagramAspect#getEditableSignature(java.lang.Object)
	 */
	@Override
	public String getEditableSignature(Object eObject) {
		return ""; //$NON-NLS-1$;
	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.core.metamodel.aspect.uml.UmlDiagramAspect#getSignature(java.lang.Object, int)
	 */
	@Override
	public String getSignature(Object eObject, int showMask) {
		return ""; //$NON-NLS-1$;
	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.core.metamodel.aspect.uml.UmlDiagramAspect#getStereotype(java.lang.Object)
	 */
	@Override
	public String getStereotype(Object eObject) {
		return Uml2Plugin.getPluginResourceLocator().getString("_UI_Generalization_type"); //$NON-NLS-1$;
	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.core.metamodel.aspect.uml.UmlDiagramAspect#setSignature(java.lang.Object, java.lang.String)
	 */
	@Override
	public IStatus setSignature(Object eObject, String newSignature) {
		throw new UnsupportedOperationException(Uml2Plugin.Util.getString("Uml2GeneralizationUmlAspect.Signature_may_not_be_set_on_a__1",getStereotype(eObject))); //$NON-NLS-1$
	}

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.uml.UmlRelationship#getName(java.lang.Object)
     */
    @Override
	public String getName(Object eObject) {
        return CoreStringUtil.Constants.EMPTY_STRING;
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.uml.UmlRelationship#getToolTip(java.lang.Object)
     */
    @Override
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
