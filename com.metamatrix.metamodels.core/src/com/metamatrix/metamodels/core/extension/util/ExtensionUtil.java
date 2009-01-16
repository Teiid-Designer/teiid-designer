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

package com.metamatrix.metamodels.core.extension.util;

import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;
import com.metamatrix.metamodels.core.extension.XAttribute;
import com.metamatrix.metamodels.core.extension.XClass;
import com.metamatrix.metamodels.core.extension.XEnum;
import com.metamatrix.metamodels.core.extension.XEnumLiteral;
import com.metamatrix.metamodels.core.extension.XPackage;


/**
 * Utility class for the Extension Metamodel classes
 */
public class ExtensionUtil {

    public static boolean addChildToParent(final EObject child, final EObject parent) {
        if(child instanceof XClass) {
            if(parent instanceof XPackage) {
                ((XPackage)parent).getEClassifiers().add((XClass)child);
            }else {
                return false;
            }
        }else if(child instanceof XAttribute){
            if(parent instanceof XClass) {
                ((XClass)parent).getEStructuralFeatures().add((XAttribute)child);
            }else {
                return false;
            }
        }else if(child instanceof XEnum){
            if(parent instanceof XPackage) {
                ((XPackage)parent).getEClassifiers().add((XEnum)child);
            }else {
                return false;
            }
        }else if(child instanceof XEnumLiteral){
            if(parent instanceof XEnum) {
                ((XEnum)parent).getELiterals().add((EEnumLiteral)child);
            }else {
                return false;
            }
        }else {
            return false;
        }

        return true;
    }
}
