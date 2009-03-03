/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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
