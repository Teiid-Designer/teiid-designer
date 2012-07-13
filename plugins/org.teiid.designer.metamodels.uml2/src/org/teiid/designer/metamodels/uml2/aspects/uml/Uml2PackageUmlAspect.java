/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.uml2.aspects.uml;

import org.eclipse.uml2.uml.Package;
import org.teiid.core.TeiidRuntimeException;
import org.teiid.core.util.CoreArgCheck;
import org.teiid.designer.core.metamodel.aspect.MetamodelEntity;
import org.teiid.designer.core.metamodel.aspect.uml.UmlPackage;
import org.teiid.designer.metamodels.uml2.Uml2Plugin;


/**
 * SchemaAspect
 */
public class Uml2PackageUmlAspect extends AbstractUml2NamedElementUmlAspect implements UmlPackage {
    /**
     * Construct an instance of SchemaAspect.
     * @param entity
     */
    public Uml2PackageUmlAspect(MetamodelEntity entity){
        super(entity);
    }
    
    @Override
	public String getSignature(Object eObject, int showMask) {
        final Package pkg = assertPackage(eObject);
        StringBuffer result = new StringBuffer();
        //EClass stereotype = null;
        switch (showMask) {
            case 1 :
                //Name
                appendName(pkg,result);
                break;
            case 2 :
                //Stereotype
                appendStereotype(pkg,result,true);
                break;
            case 3 :
                //Name and Stereotype
                appendStereotype(pkg,result,true);
                appendName(pkg,result);
                break;
            default :
                final int params = showMask;
                final String msg = Uml2Plugin.Util.getString("Uml2PackageUmlAspect.Invalid_showMask_for_getSignature_0_1",params); //$NON-NLS-1$
                throw new TeiidRuntimeException(msg);
        }
        return result.toString();
    }

    protected Package assertPackage(Object eObject) {
        CoreArgCheck.isInstanceOf(Package.class, eObject);
        return (Package)eObject;
    }
    
	@Override
    public String getStereotype(Object eObject) {
		return Uml2Plugin.getPluginResourceLocator().getString("_UI_Package_type"); //$NON-NLS-1$
	}
    
}
