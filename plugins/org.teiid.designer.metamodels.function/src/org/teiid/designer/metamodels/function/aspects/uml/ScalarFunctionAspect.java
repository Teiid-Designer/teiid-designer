/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.function.aspects.uml;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.teiid.core.designer.TeiidDesignerRuntimeException;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.designer.core.metamodel.aspect.MetamodelEntity;
import org.teiid.designer.core.metamodel.aspect.uml.UmlClassifier;
import org.teiid.designer.metamodels.function.FunctionPlugin;
import org.teiid.designer.metamodels.function.ScalarFunction;


/**
 * ScalarFunctionAspect
 *
 * @since 8.0
 */
public class ScalarFunctionAspect extends AbstractFunctionAspect implements UmlClassifier {
    /**
     * Construct an instance of ScalarFunctionAspect.
     * @param entity
     */
    public ScalarFunctionAspect(MetamodelEntity entity){
        super(entity);
    }
    
    /**
     * @see org.teiid.designer.core.metamodel.aspect.uml.UmlClassifier#getRelationships(java.lang.Object)
     */
    @Override
	public Collection getRelationships(Object eObject) {
        return Collections.EMPTY_LIST;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.metamodels.aspects.UmlDiagramAspect#getStereotype(java.lang.Object)
     */
    @Override
	public String getStereotype(Object eObject) {
        return FunctionPlugin.getPluginResourceLocator().getString("_UI_ScalarFunction_type"); //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.metamodels.aspects.UmlDiagramAspect#setSignature(java.lang.Object, java.lang.String)
     */
    @Override
	public IStatus setSignature(Object eObject, String newSignature) {
        try {
            ScalarFunction sf = assertScalarFunction(eObject);
            sf.setName(newSignature);
        } catch (Throwable e) {
            return new Status(IStatus.ERROR, FunctionPlugin.PLUGIN_ID, 0, e.getMessage(), e);
        }
        
        return new Status(IStatus.OK, FunctionPlugin.PLUGIN_ID, 0, FunctionPlugin.Util.getString("Aspect.OK_1"), null); //$NON-NLS-1$
    }

    @Override
	public Collection getSupertypes(Object eObject) {
        return new ArrayList();
    }

    @Override
	public String getSignature(Object eObject, int showMask) {
        ScalarFunction sf = assertScalarFunction(eObject);
        StringBuffer result = new StringBuffer();
        switch (showMask) {
            case 1 :
                //Name
                result.append(sf.getName() );
                break;
            case 2 :
                //Stereotype
                result.append("<<"); //$NON-NLS-1$
                result.append(getStereotype(eObject) );
                result.append(">>"); //$NON-NLS-1$
                break;
            case 3 :
                //Name and Stereotype
                result.append("<<"); //$NON-NLS-1$
                result.append(getStereotype(eObject) );
                result.append(">> "); //$NON-NLS-1$                
                result.append(sf.getName() );        
                break;
            default :
                throw new TeiidDesignerRuntimeException(FunctionPlugin.Util.getString("AbstractUmlClassifier.Invalid_showMask_for_getSignature____1",showMask) ); //$NON-NLS-1$
        }
        return result.toString();
    }



    /** 
     * @see org.teiid.designer.core.metamodel.aspect.uml.UmlClassifier#isAbstract(java.lang.Object)
     */
    @Override
	public boolean isAbstract(Object eObject) {
        return false;
    }

    protected ScalarFunction assertScalarFunction(Object eObject) {
        CoreArgCheck.isInstanceOf(ScalarFunction.class, eObject);
        return (ScalarFunction)eObject;
    }

}
