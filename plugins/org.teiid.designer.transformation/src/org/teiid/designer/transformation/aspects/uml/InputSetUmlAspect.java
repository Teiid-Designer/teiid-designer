/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.aspects.uml;

import java.util.Collection;
import java.util.Collections;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.teiid.core.TeiidRuntimeException;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.metamodel.aspect.uml.UmlClassifier;
import org.teiid.designer.metamodels.transformation.InputSet;
import org.teiid.designer.transformation.TransformationPlugin;


/**
 * InputSetUmlAspect
 *
 * @since 8.0
 */
public class InputSetUmlAspect extends AbstractTransformationUmlAspect implements UmlClassifier {

    public static final String ASPECT_ID = ModelerCore.EXTENSION_POINT.UML_DIAGRAM_ASPECT.ID;
    
    /**
     * Construct an instance of InputSetUmlAspect.
     * 
     */
    public InputSetUmlAspect() {
        super();
        setID(ASPECT_ID);
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.uml.UmlDiagramAspect#setSignature(java.lang.Object, java.lang.String)
     */
    @Override
	public IStatus setSignature(Object eObject, String newSignature) {
//        try {
//            InputSet inputSet = assertInputSet(eObject);
//            inputSet.setName(newSignature);
//        } catch (Throwable e) {
//            return new Status(IStatus.ERROR, TransformationPlugin.PLUGIN_ID, 0, e.getMessage(), e);
//        }
        
        return new Status(IStatus.OK, TransformationPlugin.PLUGIN_ID, 0, TransformationPlugin.Util.getString("InputSetUmlAspect.Signature_set") + newSignature, null); //$NON-NLS-1$
    }
    
    protected String getName( Object eObject ) {
        return org.teiid.designer.metamodels.transformation.TransformationPlugin.Util.getString("_UI_InputSet_type"); //$NON-NLS-1$
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.uml.UmlDiagramAspect#getSignature(java.lang.Object, int)
     */
    @Override
	public String getSignature(Object eObject, int showMask) {
        final InputSet inputSet = assertInputSet(eObject);
        StringBuffer result = new StringBuffer();
        switch (showMask) {
            case 1 :
                //Name
                result.append(getName(inputSet));
                break;
            case 2 :
                //Stereotype
//                result.append("<<"); //$NON-NLS-1$
//                result.append(getStereotype(eObject) );
//                result.append(">>"); //$NON-NLS-1$
                break;
            case 3 :
                //Name and Stereotype
//                result.append("<<"); //$NON-NLS-1$
//                result.append(getStereotype(eObject) );
//                result.append(">> "); //$NON-NLS-1$                
            result.append(getName(inputSet));
                break;
            default :
                throw new TeiidRuntimeException(TransformationPlugin.Util.getString("InputSetUmlAspect.Invalid_show_mask_for_getSignature") + showMask); //$NON-NLS-1$
        }
        return result.toString();
    }

    protected InputSet assertInputSet(Object eObject) {
        CoreArgCheck.isInstanceOf(InputSet.class, eObject);
        return (InputSet)eObject;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.uml.UmlClassifier#getRelationships(java.lang.Object)
     */
    @Override
	public Collection getRelationships(Object eObject) {
        return Collections.EMPTY_LIST;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.uml.UmlClassifier#getSupertypes(java.lang.Object)
     */
    @Override
	public Collection getSupertypes(Object eObject) {
        return Collections.EMPTY_LIST;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.uml.UmlDiagramAspect#getStereotype(java.lang.Object)
     */
    @Override
	public String getStereotype(Object eObject) {
        return org.teiid.designer.metamodels.transformation.TransformationPlugin.Util.getString("_UI_InputSet_type"); //$NON-NLS-1$
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.uml.UmlDiagramAspect#getEditableSignature(java.lang.Object)
     */
    @Override
	public String getEditableSignature(Object eObject) {
//        return getSignature(eObject,UmlClassifier.SIGNATURE_NAME);
        return ""; //$NON-NLS-1$
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.uml.UmlClassifier#isAbstract(java.lang.Object)
     */
    @Override
	public boolean isAbstract(Object eObject) {
        return false;
    }
    
}
