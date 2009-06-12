/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.uml2.aspects.uml;

import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.uml2.uml.Realization;
import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.metamodels.uml2.Uml2Plugin;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;

/**
 * @author DIdoux To change the template for this generated type comment go to Window&gt;Preferences&gt;Java&gt;Code
 *         Generation&gt;Code and Comments
 */
public class Uml2RealizationUmlAspect extends AbstractUml2DependencyUmlAspect {

    /**
	 * 
	 */
    public Uml2RealizationUmlAspect( MetamodelEntity entity ) {
        super();
        setMetamodelEntity(entity);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlDiagramAspect#getStereotype(java.lang.Object)
     */
    public String getStereotype( Object eObject ) {
        return Uml2Plugin.getPluginResourceLocator().getString("_UI_Realization_type"); //$NON-NLS-1$;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlDiagramAspect#getSignature(java.lang.Object, int)
     */
    public String getSignature( Object eObject,
                                int showMask ) {
        return ""; //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlDiagramAspect#getEditableSignature(java.lang.Object)
     */
    public String getEditableSignature( Object eObject ) {
        return ""; //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlDiagramAspect#setSignature(java.lang.Object, java.lang.String)
     */
    public IStatus setSignature( Object eObject,
                                 String newSignature ) {
        throw new UnsupportedOperationException(
                                                Uml2Plugin.Util.getString("Uml2RealizationUmlAspect.Signature_may_not_be_set_on_a__1", getStereotype(eObject))); //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlDependency#getEndObjects(java.lang.Object, int)
     */
    public List getEndObjects( Object relationship,
                               int end ) {
        Realization r = assertRealization(relationship);
        return r.getOwnedElements();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlDependency#getSource(java.lang.Object)
     */
    public List getSource( Object relationship ) {
        Realization r = assertRealization(relationship);
        return r.getSources();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlDependency#getTarget(java.lang.Object)
     */
    public List getTarget( Object relationship ) {
        Realization r = assertRealization(relationship);
        return r.getTargets();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlDependency#isAbstraction(java.lang.Object)
     */
    public boolean isAbstraction( Object relationship ) {
        return false;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlDependency#isPermission(java.lang.Object)
     */
    public boolean isPermission( Object relationship ) {
        return false;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlDependency#isRealization(java.lang.Object)
     */
    public boolean isRealization( Object relationship ) {
        return true;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlDependency#isSubstitution(java.lang.Object)
     */
    public boolean isSubstitution( Object relationship ) {
        return false;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlDependency#isUsage(java.lang.Object)
     */
    public boolean isUsage( Object relationship ) {
        return false;
    }

    protected Realization assertRealization( Object eObject ) {
        ArgCheck.isInstanceOf(Realization.class, eObject);
        return (Realization)eObject;
    }

}
