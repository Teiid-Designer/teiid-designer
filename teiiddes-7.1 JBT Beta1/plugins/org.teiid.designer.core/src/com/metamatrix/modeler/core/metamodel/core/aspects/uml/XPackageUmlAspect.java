/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.metamodel.core.aspects.uml;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.teiid.core.TeiidRuntimeException;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.metamodels.core.CoreMetamodelPlugin;
import com.metamatrix.metamodels.core.extension.XPackage;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;
import com.metamatrix.modeler.core.metamodel.aspect.uml.UmlPackage;

/**
 * SchemaAspect
 */
public class XPackageUmlAspect extends AbstractExtensionUmlAspect implements UmlPackage {
    /**
     * Construct an instance of SchemaAspect.
     * @param entity
     */
    public XPackageUmlAspect(MetamodelEntity entity){
        super();
        setMetamodelEntity(entity);
    }
    
    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodels.aspects.UmlDiagramAspect#getStereotype(java.lang.Object)
     */
    public String getStereotype(Object eObject) {
        return CoreMetamodelPlugin.getPluginResourceLocator().getString("_UI_XPackage_type"); //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodels.aspects.UmlDiagramAspect#setSignature(java.lang.Object, java.lang.String)
     */
    public IStatus setSignature(Object eObject, String newSignature) {
        try {
            final XPackage xpkg = assertXPackage(eObject);
            xpkg.setName(newSignature);
        } catch (Throwable e) {
            return new Status(IStatus.ERROR, ModelerCore.PLUGIN_ID, 0, e.getMessage(), e);
        }
        
        return new Status(IStatus.OK, ModelerCore.PLUGIN_ID, 0, ModelerCore.Util.getString("XPackageUmlAspect.Signature_changed"), null); //$NON-NLS-1$
    }

    public String getSignature(Object eObject, int showMask) {
        final XPackage xpkg = assertXPackage(eObject);
        StringBuffer result = new StringBuffer();
        switch (showMask) {
            case 1 :
                //Name
                result.append(xpkg.getName() );
                break;
            case 2 :
                //Stereotype
                result.append("<<"); //$NON-NLS-1$
                result.append(getStereotype(xpkg) );    
                result.append(">>"); //$NON-NLS-1$    
                break;
            case 3 :
                //Name and Stereotype
                result.append("<<"); //$NON-NLS-1$
                result.append(getStereotype(xpkg) );   
                result.append(">> "); //$NON-NLS-1$ 
                result.append(xpkg.getName() );   
                break;
            default :
                throw new TeiidRuntimeException(ModelerCore.Util.getString("XPackageUmlAspect.Invalid_showMask_for_getSignature") + showMask ); //$NON-NLS-1$
        }
        return result.toString();
    }

    public String getEditableSignature(Object eObject) {
        return getSignature(eObject, UmlPackage.SIGNATURE_NAME);
    }

    protected XPackage assertXPackage(Object eObject) {
        CoreArgCheck.isInstanceOf(XPackage.class, eObject);
    
        return (XPackage)eObject;
    }
}
