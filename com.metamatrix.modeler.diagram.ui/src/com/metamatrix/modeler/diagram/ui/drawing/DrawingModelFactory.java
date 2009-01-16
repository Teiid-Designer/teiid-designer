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

package com.metamatrix.modeler.diagram.ui.drawing;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.metamodels.diagram.DiagramEntity;
import com.metamatrix.modeler.diagram.ui.drawing.model.EllipseModelNode;
import com.metamatrix.modeler.diagram.ui.drawing.model.NoteModelNode;
import com.metamatrix.modeler.diagram.ui.drawing.model.RectangleModelNode;
import com.metamatrix.modeler.diagram.ui.drawing.model.TextModelNode;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;
import com.metamatrix.modeler.diagram.ui.util.DiagramUiUtilities;

/**
 * DrawingModelFactory
 * This class provides quick static methods to create drawing objects.
 */
public class DrawingModelFactory implements DrawingConstants {

    /*
     * This method is for creating NEW objects to place on diagram.
     */
    public static DiagramModelNode createModelNode(final int objectTypeId, final DiagramModelNode dmn) {
        DiagramModelNode newNode = null;
        Diagram diagram = (Diagram)dmn.getModelObject();
        
        switch( objectTypeId ) {
            case TypeId.NOTE: {
                // Create the entity with null model object;
                DiagramEntity newEntity = DiagramUiUtilities.getDiagramEntity(null, diagram);
                // Now we need to set the initial parameters of the entity
                newEntity.setHeight(100);
                newEntity.setWidth(100);
                newEntity.setXPosition(100);
                newEntity.setYPosition(100);
                newEntity.setUserType(Types.NOTE);
                newEntity.setUserString("NEW NOTE TEXT"); //$NON-NLS-1$
                
                newNode = new NoteModelNode(dmn, newEntity);
            } break;
            
            case TypeId.TEXT: {
                // Create the entity with null model object;
                DiagramEntity newEntity = DiagramUiUtilities.getDiagramEntity(null, diagram);
                // Now we need to set the initial parameters of the entity
                newEntity.setHeight(25);
                newEntity.setWidth(100);
                newEntity.setXPosition(100);
                newEntity.setYPosition(100);
                newEntity.setUserType(Types.TEXT);
                newEntity.setUserString("NEW TEXT TEXT"); //$NON-NLS-1$
    
                newNode = new TextModelNode(dmn, newEntity);
            } break;
            
            case TypeId.RECTANGLE: {
                // Create the entity with null model object;
                DiagramEntity newEntity = DiagramUiUtilities.getDiagramEntity(null, diagram);
                // Now we need to set the initial parameters of the entity
                newEntity.setHeight(200);
                newEntity.setWidth(200);
                newEntity.setXPosition(100);
                newEntity.setYPosition(100);
                newEntity.setUserType(Types.RECTANGLE);
    
                newNode = new RectangleModelNode(dmn, newEntity);
            } break;
            
            case TypeId.ELLIPSE: {
                // Create the entity with null model object;
                DiagramEntity newEntity = DiagramUiUtilities.getDiagramEntity(null, diagram);
                // Now we need to set the initial parameters of the entity
                newEntity.setHeight(100);
                newEntity.setWidth(100);
                newEntity.setXPosition(100);
                newEntity.setYPosition(100);
                newEntity.setUserType(Types.ELLIPSE);
    
                newNode = new EllipseModelNode(dmn, newEntity);
            } break;
            
        }
        
        return newNode;
    }
    
    /*
     * This method is for creating NEW objects to place on diagram.
     */
    public static DiagramModelNode createModelNode(final DiagramEntity de, final DiagramModelNode dmn) {
        //  Already have entity, just need to create Node, and reset properties.
        DiagramModelNode newNode = null;
 
        int objectTypeId = -1;
        if( de.getUserType() != null ) {
            if( de.getUserType().equals(Types.NOTE))
                objectTypeId = TypeId.NOTE;
            else if( de.getUserType().equals(Types.RECTANGLE))
                objectTypeId = TypeId.RECTANGLE;
            else if( de.getUserType().equals(Types.TEXT))
                objectTypeId = TypeId.TEXT;
            else if( de.getUserType().equals(Types.ELLIPSE))
                objectTypeId = TypeId.ELLIPSE;
        }
        
        switch( objectTypeId ) {
            case TypeId.NOTE: {
                newNode = new NoteModelNode(dmn, de);
            } break;
            
            case TypeId.TEXT: {
                newNode = new TextModelNode(dmn, de);
            } break;
            
            case TypeId.RECTANGLE: {
    
                newNode = new RectangleModelNode(dmn, de);
            } break;
            
            case TypeId.ELLIPSE: {
                newNode = new EllipseModelNode(dmn, de);
            } break;
            
        }
        if( newNode != null )
            newNode.recoverObjectProperties();
        return newNode;
    }
    
    public static List getDrawingNodes(final Diagram diagram, final DiagramModelNode dmn ) {
        List drawingNodes = new ArrayList();
        // Walk through the contents of diagram and look for diagram entities that
        // have a userType of note, text, rectangle, or ellipse
        Iterator iter = diagram.eContents().iterator(); // Diagram Entities
        Object nextObj = null;
        DiagramModelNode newModelNode = null;
        while( iter.hasNext() ) {
            nextObj = iter.next();
            if( nextObj instanceof DiagramEntity && isDrawingEntity( (DiagramEntity)nextObj ) ) {
                newModelNode = createModelNode((DiagramEntity)nextObj, dmn);
                if( newModelNode != null )
                    drawingNodes.add(newModelNode);
            }
        }
        
        
        return drawingNodes;
    }
    
    private static boolean isDrawingEntity(DiagramEntity drawingEntity) {
        String userType = drawingEntity.getUserType();
        
        if( userType != null ) {
            if( userType.equals(Types.ELLIPSE) ||
                userType.equals(Types.NOTE) ||
                userType.equals(Types.RECTANGLE) ||
                userType.equals(Types.TEXT) ) {
                return true;
            }
            return false;
        }
        return false;
    }
}
