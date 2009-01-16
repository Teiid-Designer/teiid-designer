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

package com.metamatrix.modeler.core.metamodel.core.aspects.uml;

import org.eclipse.emf.ecore.EClassifier;

import com.metamatrix.metamodels.core.extension.ExtensionPackage;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelAspect;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelAspectFactory;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;
import com.metamatrix.modeler.core.ModelerCore;

/**
 * RelationalUmlAspectFactoryImpl
 */
public class ExtensionUmlAspectFactoryImpl implements MetamodelAspectFactory {
    public MetamodelAspect create(EClassifier classifier, MetamodelEntity entity) {
        switch (classifier.getClassifierID()) {
            case ExtensionPackage.XATTRIBUTE:       return new XAttributeUmlAspect(entity);
            case ExtensionPackage.XCLASS:           return new XClassUmlAspect(entity);
            case ExtensionPackage.XPACKAGE:         return new XPackageUmlAspect(entity);
            case ExtensionPackage.XENUM:            return new XEnumUmlAspect(entity);
            case ExtensionPackage.XENUM_LITERAL:    return new XEnumLiteralUmlAspect(entity);
            default:
                throw new IllegalArgumentException(ModelerCore.Util.getString("ExtensionUmlAspectFactoryImpl.Invalid_Classifer_ID_for_creating_UML_Aspect")+classifier); //$NON-NLS-1$
        }
    }

}
