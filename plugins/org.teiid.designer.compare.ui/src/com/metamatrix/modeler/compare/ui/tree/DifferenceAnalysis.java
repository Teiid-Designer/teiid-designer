/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.compare.ui.tree;

import org.eclipse.emf.mapping.Mapping;
import org.eclipse.emf.mapping.MappingHelper;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProvider;
import com.metamatrix.modeler.compare.DifferenceDescriptor;
import com.metamatrix.modeler.compare.DifferenceType;

/**
 * DifferenceAnalysis
 */
public class DifferenceAnalysis {
    
    public static ILabelProvider getMappingDecoratingLabelProvider(int terminology) {
        return new DecoratingLabelProvider(getMappingLabelProvider(), getMappingLabelDecorator(terminology));
    }
    
    public static ILabelProvider getMappingLabelProvider() {
        return new MappingLabelProvider();
    }
    
    public static ILabelDecorator getMappingLabelDecorator(int terminology) {
        return new MappingLabelDecorator(terminology);
    }
    
    public static boolean isAdd(Mapping mapping) {
        boolean isAdd = false;
        DifferenceDescriptor descriptor = getDifferenceDescriptor(mapping);
        if(descriptor!=null) {
            final DifferenceType type = descriptor.getType();
            if ( type.getValue() == DifferenceType.ADDITION ) {
                isAdd = true;
            } 
        }
        return isAdd;
    }
    
    public static boolean isChange(Mapping mapping) {
        boolean isChange = false;
        DifferenceDescriptor descriptor = getDifferenceDescriptor(mapping);
        if(descriptor!=null) {
            final DifferenceType type = descriptor.getType();
            if ( type.getValue() == DifferenceType.CHANGE ) {
                isChange = true;
            } 
        }
        return isChange;
    }
    
    public static boolean isDelete(Mapping mapping) {
        boolean isDelete = false;
        DifferenceDescriptor descriptor = getDifferenceDescriptor(mapping);
        if(descriptor!=null) {
            final DifferenceType type = descriptor.getType();
            if ( type.getValue() == DifferenceType.DELETION ) {
                isDelete = true;
            } 
        }
        return isDelete;
    }
    
    public static boolean isUnchanged(Mapping mapping) {
        boolean isNoChange = false;
        DifferenceDescriptor descriptor = getDifferenceDescriptor(mapping);
        if(descriptor!=null) {
            final DifferenceType type = descriptor.getType();
            if ( type.getValue() == DifferenceType.NO_CHANGE ) {
                isNoChange = true;
            } 
        }
        return isNoChange;
    }
    
    public static boolean isChangeBelow(Mapping mapping) {
        boolean isChange = false;
        DifferenceDescriptor descriptor = getDifferenceDescriptor(mapping);
        if(descriptor!=null) {
            final DifferenceType type = descriptor.getType();
            if ( type.getValue() == DifferenceType.CHANGE_BELOW ) {
                isChange = true;
            } 
        }
        return isChange;
    }

    public static DifferenceDescriptor getDifferenceDescriptor(Mapping mapping) {
        DifferenceDescriptor descriptor = null;
        final MappingHelper helper = mapping.getHelper();
        if(helper!=null && helper instanceof DifferenceDescriptor) {
            descriptor = (DifferenceDescriptor)helper;
        }
        return descriptor;
    }
    
}
