/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

package com.metamatrix.metamodels.uml2.aspects.uml;

import org.eclipse.uml2.uml.Package;

import com.metamatrix.core.MetaMatrixRuntimeException;
import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.metamodels.uml2.Uml2Plugin;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;
import com.metamatrix.modeler.core.metamodel.aspect.uml.UmlPackage;

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
                throw new MetaMatrixRuntimeException(msg);
        }
        return result.toString();
    }

    protected Package assertPackage(Object eObject) {
        ArgCheck.isInstanceOf(Package.class, eObject);
        return (Package)eObject;
    }
    
	@Override
    public String getStereotype(Object eObject) {
		return Uml2Plugin.getPluginResourceLocator().getString("_UI_Package_type"); //$NON-NLS-1$
	}
    
}
