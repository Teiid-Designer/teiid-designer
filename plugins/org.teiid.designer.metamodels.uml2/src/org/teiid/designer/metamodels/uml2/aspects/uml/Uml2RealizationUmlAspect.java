/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.uml2.aspects.uml;

import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.uml2.uml.Realization;
import org.teiid.core.util.CoreArgCheck;
import org.teiid.designer.core.metamodel.aspect.MetamodelEntity;
import org.teiid.designer.metamodels.uml2.Uml2Plugin;


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
     * @See org.teiid.designer.core.metamodel.aspect.uml.UmlDiagramAspect#getStereotype(java.lang.Object)
     */
    @Override
	public String getStereotype( Object eObject ) {
        return Uml2Plugin.getPluginResourceLocator().getString("_UI_Realization_type"); //$NON-NLS-1$;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.metamodel.aspect.uml.UmlDiagramAspect#getSignature(java.lang.Object, int)
     */
    @Override
	public String getSignature( Object eObject,
                                int showMask ) {
        return ""; //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.metamodel.aspect.uml.UmlDiagramAspect#getEditableSignature(java.lang.Object)
     */
    @Override
	public String getEditableSignature( Object eObject ) {
        return ""; //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.metamodel.aspect.uml.UmlDiagramAspect#setSignature(java.lang.Object, java.lang.String)
     */
    @Override
	public IStatus setSignature( Object eObject,
                                 String newSignature ) {
        throw new UnsupportedOperationException(
                                                Uml2Plugin.Util.getString("Uml2RealizationUmlAspect.Signature_may_not_be_set_on_a__1", getStereotype(eObject))); //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.metamodel.aspect.uml.UmlDependency#getEndObjects(java.lang.Object, int)
     */
    @Override
	public List getEndObjects( Object relationship,
                               int end ) {
        Realization r = assertRealization(relationship);
        return r.getOwnedElements();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.metamodel.aspect.uml.UmlDependency#getSource(java.lang.Object)
     */
    @Override
	public List getSource( Object relationship ) {
        Realization r = assertRealization(relationship);
        return r.getSources();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.metamodel.aspect.uml.UmlDependency#getTarget(java.lang.Object)
     */
    @Override
	public List getTarget( Object relationship ) {
        Realization r = assertRealization(relationship);
        return r.getTargets();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.metamodel.aspect.uml.UmlDependency#isAbstraction(java.lang.Object)
     */
    @Override
	public boolean isAbstraction( Object relationship ) {
        return false;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.metamodel.aspect.uml.UmlDependency#isPermission(java.lang.Object)
     */
    @Override
	public boolean isPermission( Object relationship ) {
        return false;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.metamodel.aspect.uml.UmlDependency#isRealization(java.lang.Object)
     */
    @Override
	public boolean isRealization( Object relationship ) {
        return true;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.metamodel.aspect.uml.UmlDependency#isSubstitution(java.lang.Object)
     */
    @Override
	public boolean isSubstitution( Object relationship ) {
        return false;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.metamodel.aspect.uml.UmlDependency#isUsage(java.lang.Object)
     */
    @Override
	public boolean isUsage( Object relationship ) {
        return false;
    }

    protected Realization assertRealization( Object eObject ) {
        CoreArgCheck.isInstanceOf(Realization.class, eObject);
        return (Realization)eObject;
    }

}
