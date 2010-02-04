/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.relational.aspects.uml;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import com.metamatrix.core.MetaMatrixRuntimeException;
import com.metamatrix.core.modeler.util.ArgCheck;
import com.metamatrix.metamodels.relational.Catalog;
import com.metamatrix.metamodels.relational.RelationalMetamodelConstants;
import com.metamatrix.metamodels.relational.RelationalPlugin;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;
import com.metamatrix.modeler.core.metamodel.aspect.uml.UmlPackage;

/**
 * SchemaAspect
 */
public class CatalogAspect extends RelationalEntityAspect implements UmlPackage {
    /**
     * Construct an instance of SchemaAspect.
     * @param entity
     */
    public CatalogAspect(MetamodelEntity entity){
        super();
        setMetamodelEntity(entity);
    }
    
    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodels.aspects.UmlDiagramAspect#getStereotype(java.lang.Object)
     */
    public String getStereotype(Object eObject) {
        return RelationalPlugin.getPluginResourceLocator().getString("_UI_Catalog_type"); //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodels.aspects.UmlDiagramAspect#setSignature(java.lang.Object, java.lang.String)
     */
    public IStatus setSignature(Object eObject, String newSignature) {
        try {
            Catalog catalog = assertCatalog(eObject);
            catalog.setName(newSignature);
        } catch (Throwable e) {
            return new Status(IStatus.ERROR, RelationalMetamodelConstants.PLUGIN_ID, 0, e.getMessage(), e);
        }
        
        return new Status(IStatus.OK, RelationalMetamodelConstants.PLUGIN_ID, 0, RelationalPlugin.Util.getString("Aspect.ok"), null); //$NON-NLS-1$
    }

    public String getSignature(Object eObject, int showMask) {
        Catalog catalog = assertCatalog(eObject);
        StringBuffer result = new StringBuffer();
        switch (showMask) {
            case 1 :
                //Name
                result.append(catalog.getName() );
                break;
            case 2 :
                //Stereotype
                result.append("<<"); //$NON-NLS-1$
                result.append(getStereotype(catalog) );    
                result.append(">>"); //$NON-NLS-1$    
                break;
            case 3 :
                //Name and Stereotype
                result.append("<<"); //$NON-NLS-1$
                result.append(getStereotype(catalog) );   
                result.append(">> "); //$NON-NLS-1$ 
                result.append(catalog.getName() );   
                break;
            default :
                throw new MetaMatrixRuntimeException(RelationalPlugin.Util.getString("Aspect.invalidShowMask", showMask)); //$NON-NLS-1$
        }
        return result.toString();
    }

    public String getEditableSignature(Object eObject) {
        return getSignature(eObject, UmlPackage.SIGNATURE_NAME);
    }

    protected Catalog assertCatalog(Object eObject) {
        ArgCheck.isInstanceOf(Catalog.class, eObject);
    
        return (Catalog)eObject;
    }
}
