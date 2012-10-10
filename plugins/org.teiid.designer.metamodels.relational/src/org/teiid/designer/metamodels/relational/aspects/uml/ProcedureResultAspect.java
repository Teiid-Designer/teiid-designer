/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.relational.aspects.uml;

import java.util.Collection;
import java.util.Collections;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.teiid.core.TeiidRuntimeException;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.designer.core.metamodel.aspect.MetamodelEntity;
import org.teiid.designer.core.metamodel.aspect.uml.UmlClassifier;
import org.teiid.designer.metamodels.relational.ProcedureResult;
import org.teiid.designer.metamodels.relational.RelationalMetamodelConstants;
import org.teiid.designer.metamodels.relational.RelationalPlugin;


/**
 * ProcedureResultAspect
 *
 * @since 8.0
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
        CoreArgCheck.isInstanceOf(ProcedureResult.class, eObject);
        return (ProcedureResult)eObject;
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
        return RelationalPlugin.getPluginResourceLocator().getString("_UI_ProcedureResult_type"); //$NON-NLS-1$
    }

    @Override
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
                throw new TeiidRuntimeException(RelationalPlugin.Util.getString("Aspect.invalidShowMask", showMask)); //$NON-NLS-1$
        }
        return result.toString();
    }

    @Override
	public String getEditableSignature(Object eObject) {
        return getSignature(eObject, UmlClassifier.SIGNATURE_NAME);
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.uml.UmlDiagramAspect#setSignature(java.lang.Object, java.lang.String)
     */
    @Override
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
     * @see org.teiid.designer.core.metamodel.aspect.uml.UmlClassifier#isAbstract(java.lang.Object)
     */
    @Override
	public boolean isAbstract(Object eObject) {
        return false;
    }

}
