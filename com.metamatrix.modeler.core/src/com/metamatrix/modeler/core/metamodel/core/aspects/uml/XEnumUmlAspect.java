/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.metamodel.core.aspects.uml;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.metamatrix.core.MetaMatrixRuntimeException;
import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.metamodels.core.CoreMetamodelPlugin;
import com.metamatrix.metamodels.core.extension.XEnum;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;
import com.metamatrix.modeler.core.metamodel.aspect.uml.UmlClassifier;

/**
 * TableAspect
 */
public class XEnumUmlAspect extends AbstractExtensionUmlAspect implements UmlClassifier {
    /**
     * Construct an instance of TableAspect.
     * @param entity
     */
    public XEnumUmlAspect(MetamodelEntity entity){
        super();
        setMetamodelEntity(entity);
    }
    
    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodels.aspects.UmlDiagramAspect#getStereotype(java.lang.Object)
     */
    public String getStereotype(Object eObject) {
        return CoreMetamodelPlugin.Util.getString("_UI_XEnum_type"); //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodels.aspects.UmlDiagramAspect#setSignature(java.lang.Object, java.lang.String)
     */
    public IStatus setSignature(Object eObject, String newSignature) {
        try {
            final XEnum xenum = assertXEnum(eObject);
            xenum.setName(newSignature);
        } catch (Throwable e) {
            return new Status(IStatus.ERROR, ModelerCore.PLUGIN_ID, 0, e.getMessage(), e);
        }
        
        return new Status(IStatus.OK, ModelerCore.PLUGIN_ID, 0, ModelerCore.Util.getString("XEnumUmlAspect.Signature_changed"), null); //$NON-NLS-1$
    }

    public Collection getRelationships(Object eObject) {
        return new ArrayList();
//        final XEnum xenum = assertXEnum(eObject);
//        Collection results = new ArrayList();
//        results.addAll(table.getForeignKeys() );
//        return results;
    }

    public Collection getSupertypes(Object eObject) {
        return new ArrayList();
    }

    public String getSignature(Object eObject, int showMask) {
        final XEnum xenum = assertXEnum(eObject);
        StringBuffer result = new StringBuffer();
        switch (showMask) {
            case 1 :
                //Name
                result.append(xenum.getName() );
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
                result.append(xenum.getName() );        
                break;
            default :
                throw new MetaMatrixRuntimeException(ModelerCore.Util.getString("XEnumUmlAspect.Invalid_showMask_for_getSignature") + showMask ); //$NON-NLS-1$
        }
        return result.toString();
    }

    public String getEditableSignature(Object eObject) {
        return getSignature(eObject, UmlClassifier.SIGNATURE_NAME);
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlClassifier#isAbstract(java.lang.Object)
     */
    public boolean isAbstract(Object eObject) {
        return false;
    }

    protected XEnum assertXEnum(Object eObject) {
        ArgCheck.isInstanceOf(XEnum.class, eObject);
    
        return (XEnum)eObject;
    }

}
