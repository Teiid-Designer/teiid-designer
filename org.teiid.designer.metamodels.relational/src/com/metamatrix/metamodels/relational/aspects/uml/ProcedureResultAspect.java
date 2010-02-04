/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.relational.aspects.uml;

import java.util.Collection;
import java.util.Collections;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import com.metamatrix.core.MetaMatrixRuntimeException;
import com.metamatrix.core.modeler.util.ArgCheck;
import com.metamatrix.metamodels.relational.ProcedureResult;
import com.metamatrix.metamodels.relational.RelationalMetamodelConstants;
import com.metamatrix.metamodels.relational.RelationalPlugin;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;
import com.metamatrix.modeler.core.metamodel.aspect.uml.UmlClassifier;

/**
 * ProcedureResultAspect
 */
public class ProcedureResultAspect extends RelationalEntityAspect implements UmlClassifier {

    /**
     * Construct an instance of ProcedureResultAspect.
     * @param entity
     */
    public ProcedureResultAspect(MetamodelEntity entity) {
        super();
        setMetamodelEntity(entity);
    }

    protected ProcedureResult assertProcedureResult(Object eObject) {
        ArgCheck.isInstanceOf(ProcedureResult.class, eObject);
        return (ProcedureResult)eObject;
    }
    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlClassifier#getRelationships(java.lang.Object)
     */
    public Collection getRelationships(Object eObject) {
        return Collections.EMPTY_LIST;
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlClassifier#getSupertypes(java.lang.Object)
     */
    public Collection getSupertypes(Object eObject) {
        return Collections.EMPTY_LIST;
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlDiagramAspect#getStereotype(java.lang.Object)
     */
    public String getStereotype(Object eObject) {
        return RelationalPlugin.getPluginResourceLocator().getString("_UI_ProcedureResult_type"); //$NON-NLS-1$
    }

    public String getSignature(Object eObject, int showMask) {
        ProcedureResult procResult = assertProcedureResult(eObject);
        StringBuffer result = new StringBuffer();
        switch (showMask) {
            case 1 :
                //Name
                result.append(procResult.getName() );
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
                result.append(procResult.getName() );        
                break;
            default :
                throw new MetaMatrixRuntimeException(RelationalPlugin.Util.getString("Aspect.invalidShowMask", showMask)); //$NON-NLS-1$
        }
        return result.toString();
    }

    public String getEditableSignature(Object eObject) {
        return getSignature(eObject, UmlClassifier.SIGNATURE_NAME);
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlDiagramAspect#setSignature(java.lang.Object, java.lang.String)
     */
    public IStatus setSignature(Object eObject, String newSignature) {
        try {
            ProcedureResult result = assertProcedureResult(eObject);
            result.setName(newSignature);
        } catch (Throwable e) {
            return new Status(IStatus.ERROR, RelationalMetamodelConstants.PLUGIN_ID, 0, e.getMessage(), e);
        }
        
        return new Status(IStatus.OK, RelationalMetamodelConstants.PLUGIN_ID, 0, RelationalPlugin.Util.getString("Aspect.ok"), null); //$NON-NLS-1$
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlClassifier#isAbstract(java.lang.Object)
     */
    public boolean isAbstract(Object eObject) {
        return false;
    }

}
