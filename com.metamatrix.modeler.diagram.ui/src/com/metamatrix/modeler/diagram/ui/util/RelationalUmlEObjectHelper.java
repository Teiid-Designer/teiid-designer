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

package com.metamatrix.modeler.diagram.ui.util;

import org.eclipse.emf.ecore.EObject;

import com.metamatrix.metamodels.diagram.DiagramEntity;
import com.metamatrix.metamodels.diagram.DiagramLink;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelAspect;
import com.metamatrix.modeler.core.metamodel.aspect.uml.UmlAssociation;
import com.metamatrix.modeler.core.metamodel.aspect.uml.UmlClassifier;
import com.metamatrix.modeler.core.metamodel.aspect.uml.UmlComment;
import com.metamatrix.modeler.core.metamodel.aspect.uml.UmlDiagramAspect;
import com.metamatrix.modeler.core.metamodel.aspect.uml.UmlGeneralization;
import com.metamatrix.modeler.core.metamodel.aspect.uml.UmlOperation;
import com.metamatrix.modeler.core.metamodel.aspect.uml.UmlPackage;
import com.metamatrix.modeler.core.metamodel.aspect.uml.UmlProperty;
import com.metamatrix.modeler.core.metamodel.aspect.uml.UmlRelationship;
import com.metamatrix.modeler.diagram.ui.DiagramUiPlugin;


/** 
 * @since 4.2
 */
public class RelationalUmlEObjectHelper {
    public static final int UNKNOWN = -1;
    public static final int UML_PACKAGE = 0;
    public static final int UML_CLASSIFIER = 1;
    public static final int UML_ASSOCIATION = 2;
    public static final int UML_OPERATION = 3;
    public static final int UML_ATTRIBUTE = 4;
    public static final int UML_COMMENT = 5;
    public static final int UML_RELATIONSHIP = 6;
    public static final int UML_GENERALIZATION = 7;
    
    public static final int UML_DIAGRAM = 10;
    public static final int UML_DIAGRAM_ENTITY = 11;
    public static final int UML_DIAGRAM_LINK = 12;

    public static int getEObjectType(final EObject eObj) {
        int type = UNKNOWN;
        
        AspectManager aspManager = DiagramUiPlugin.getDiagramAspectManager();
        MetamodelAspect someAspect = aspManager.getUmlAspect(eObj);
        
        if( someAspect instanceof UmlClassifier )
            type = UML_CLASSIFIER;
        else if( someAspect instanceof UmlPackage )
            type = UML_PACKAGE;
        else if( someAspect instanceof UmlProperty )
            type = UML_ATTRIBUTE;
        else if( someAspect instanceof UmlAssociation )
            type = UML_ASSOCIATION;
        else if( someAspect instanceof UmlOperation )
            type = UML_OPERATION;
        else if( someAspect instanceof UmlComment )
            type = UML_COMMENT;
        else if( someAspect instanceof UmlRelationship )
            type = UML_RELATIONSHIP;
        else if( someAspect instanceof UmlGeneralization )
            type = UML_GENERALIZATION;
        else if( someAspect instanceof UmlDiagramAspect )
            type = UML_DIAGRAM;
        
        if( type == UNKNOWN ) {
            if( eObj instanceof DiagramLink )
                type = UML_DIAGRAM_LINK;
            else if( eObj instanceof DiagramEntity ) {
                type = UML_DIAGRAM_ENTITY;
            }
        }

        return type;
    }

}
