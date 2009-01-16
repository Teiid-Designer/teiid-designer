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

package com.metamatrix.modeler.mapping.ui.actions;

import java.util.Iterator;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ISelection;

import com.metamatrix.metamodels.transformation.MappingClass;
import com.metamatrix.metamodels.transformation.StagingTable;
import com.metamatrix.modeler.diagram.ui.util.RelationalUmlEObjectHelper;
import com.metamatrix.modeler.internal.mapping.factory.ModelMapperFactory;
import com.metamatrix.modeler.transformation.ui.actions.TransformationSelectionHelper;
import com.metamatrix.modeler.xsd.util.ModelerXsdUtils;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;


/** 
 * @since 4.2
 */
public class MappingSelectionHelper extends TransformationSelectionHelper {
    public static final int TYPE_EXTENT = 20;
    public static final int TYPE_COARSE_EXTENT = 21;
    public static final int TYPE_MAPPING_CLASS = 22;
    public static final int TYPE_MAPPING_CLASS_CHILD = 23;
    public static final int TYPE_STAGING_TABLE = 24;
    public static final int TYPE_STAGING_TABLE_CHILD = 25;
    public static final int TYPE_ENUM_TYPE = 26;
    public static final int TYPE_ENUM_VALUE = 27;
    
    /** 
     * @param tRoot
     * @param selection
     * @since 4.2
     */
    public MappingSelectionHelper(EObject tRoot, ISelection selection) {
        super(tRoot, selection);
    }
    
    public MappingSelectionHelper(ISelection selection) {
        super(null, selection);
    }
    
    /** 
     * @see com.metamatrix.modeler.transformation.ui.actions.TransformationSelectionHelper#getEObjectType(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
    public int getEObjectType(EObject eObj) {
        int eObjType = TYPE_UNKNOWN;
        if( eObj instanceof StagingTable  ) {
            eObjType = TYPE_STAGING_TABLE;
        } else if( eObj instanceof MappingClass ) {
            eObjType = TYPE_MAPPING_CLASS;
        } else if( ModelMapperFactory.isXmlTreeNode(eObj)) {
            eObjType = TYPE_EXTENT;
        } else if (ModelerXsdUtils.isEnumeratedType(eObj)) {
            eObjType = TYPE_ENUM_TYPE;
        } else if (ModelerXsdUtils.isEnumeratedTypeValue(eObj)) {
            eObjType = TYPE_ENUM_VALUE;
        } else {
            // Don't know what it is, so ask someone else
            int umlType = RelationalUmlEObjectHelper.getEObjectType(eObj);
            if( umlType == RelationalUmlEObjectHelper.UML_ASSOCIATION ||
                umlType == RelationalUmlEObjectHelper.UML_GENERALIZATION ||
                umlType == RelationalUmlEObjectHelper.UML_OPERATION ||
                umlType == RelationalUmlEObjectHelper.UML_ATTRIBUTE ) {
                // Check to see whose child it is.
                EObject eContainer = eObj.eContainer();
                if( eContainer instanceof StagingTable  ) {
                    eObjType = TYPE_STAGING_TABLE_CHILD;
                } else if( eContainer instanceof MappingClass ) {
                    eObjType = TYPE_MAPPING_CLASS_CHILD;
                // defect 15883 - incorrect menu showing up in Mapping diagram:
                } else {
                    eObjType = super.getEObjectType(eObj);
                } // endif

            } else {
                eObjType = super.getEObjectType(eObj);
            }
        }
        return eObjType;
    }
    
    public boolean someExtentSelected() {
        boolean foundExtent = false;
        
        if( getCount() == 1 ) {
            EObject eObj = SelectionUtilities.getSelectedEObject(getSelection());
            if( eObj != null ) {
                int type = getEObjectType(eObj);
                if( type == TYPE_EXTENT )
                    foundExtent = true;
            }
        } else {
            Iterator iter = SelectionUtilities.getSelectedEObjects(getSelection()).iterator();
            EObject nextEObj = null;
            while( iter.hasNext() && !foundExtent) {
                nextEObj = (EObject)iter.next();
                if( getEObjectType(nextEObj) == TYPE_EXTENT )
                    foundExtent = true;
            }
        }
        
        return foundExtent;
    }
}
