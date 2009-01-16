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

package com.metamatrix.modeler.transformation.ui.editors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.util.TransferDropTargetListener;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;

import org.eclipse.emf.ecore.EObject;


import com.metamatrix.metamodels.transformation.SqlTransformationMappingRoot;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.internal.transformation.util.SqlAspectHelper;
import com.metamatrix.modeler.internal.transformation.util.SqlConstants;
import com.metamatrix.modeler.internal.transformation.util.TransformationHelper;
import com.metamatrix.modeler.internal.ui.util.EObjectTransfer;
import com.metamatrix.modeler.transformation.ui.actions.ITransformationDiagramActionConstants;
import com.metamatrix.modeler.transformation.ui.actions.TransformationSourceManager;
import com.metamatrix.query.internal.ui.sqleditor.component.DisplayNodeConstants;
import com.metamatrix.query.ui.sqleditor.SqlEditorPanel;
import com.metamatrix.ui.internal.viewsupport.UiBusyIndicator;


/** 
 * @since 4.3
 */
public class SqlPanelDropTargetListener implements
                                       TransferDropTargetListener {
    private static final Transfer[] tranfers = new Transfer[] {EObjectTransfer.getInstance()};
    private SqlEditorPanel sqlPanel;
    private SqlTransformationMappingRoot transformation;
    private Object txnSource;

    /** 
     * 
     * @since 4.3
     */
    public SqlPanelDropTargetListener(SqlEditorPanel sqlPanel, SqlTransformationMappingRoot transformation, Object txnSource) {
        super();
        this.sqlPanel = sqlPanel;
        this.transformation = transformation;
        this.txnSource = txnSource;
    }

    /** 
     * @see org.eclipse.ui.texteditor.ITextEditorDropTargetListener#getTransfers()
     * @since 4.3
     */
    public Transfer getTransfer() {
        return EObjectTransfer.getInstance();
    }
    
    public Transfer[] getTransfers() {
        return tranfers;
    }

    /** 
     * @see org.eclipse.swt.dnd.DropTargetListener#dragEnter(org.eclipse.swt.dnd.DropTargetEvent)
     * @since 4.3
     */
    public void dragEnter(DropTargetEvent event) {
//        System.out.println("SQLPDTL.dragEnter()");
    }

    /** 
     * @see org.eclipse.swt.dnd.DropTargetListener#dragLeave(org.eclipse.swt.dnd.DropTargetEvent)
     * @since 4.3
     */
    public void dragLeave(DropTargetEvent event) {
//        System.out.println("SQLPDTL.dragLeave()");
    }

    /** 
     * @see org.eclipse.swt.dnd.DropTargetListener#dragOperationChanged(org.eclipse.swt.dnd.DropTargetEvent)
     * @since 4.3
     */
    public void dragOperationChanged(DropTargetEvent event) {
//        System.out.println("SQLPDTL.dragOperationChanged()");
    }

    /** 
     * @see org.eclipse.swt.dnd.DropTargetListener#dragOver(org.eclipse.swt.dnd.DropTargetEvent)
     * @since 4.3
     */
    public void dragOver(DropTargetEvent event) {
        if( isEnabled(event) && isInsertOK(event) ) {
            event.detail = DND.DROP_COPY;
        } else {
            event.detail = DND.DROP_NONE;
        }
    }
    
    private Point getMousePoint(DropTargetEvent event) {
        Point thePoint = Display.getCurrent().map(sqlPanel.getShell(), sqlPanel.getTextViewer().getTextWidget(), event.x, event.y);
        //Point startingPt = new Point(thePoint.x, thePoint.y);
        Point correctionPt = getXYPanelOffset();
        thePoint.x = thePoint.x - sqlPanel.getShell().getBounds().x - correctionPt.x;
        thePoint.y = thePoint.y - sqlPanel.getShell().getBounds().y - correctionPt.y;
        //Point eventPt = new Point(event.x, event.y);
        //System.out.println(" SqlPanel final Pt = " + thePoint + " event = " + eventPt + "  Start" + startingPt + 
        //                   "  Corr Pt = " + correctionPt + "  sqlPanelShell = " + sqlPanel.getShell().getBounds());
        return thePoint;
    }
    
    // Note that this is a HACK!!!.  For some reason, the SqlEditorPanel's composite isn't nested correctly
    // so the Display.map() method can't determine the real offset. SO we have to manually walk up three parents
    // as well as start off with an initial offset. This'll break, I'm sure based on some UI resolution setting, so be prepared.
    protected Point getXYPanelOffset() {
        Point newPt = new Point(3, 8);
        newPt.y = newPt.y + sqlPanel.getLocation().y;
        newPt.y = newPt.y + sqlPanel.getParent().getLocation().y;
        newPt.y = newPt.y + sqlPanel.getParent().getParent().getLocation().y;
        newPt.y = newPt.y + sqlPanel.getParent().getParent().getParent().getLocation().y;
//        newPt.y = newPt.y + sqlPanel.getParent().getParent().getParent().getParent().getLocation().y;
        return newPt;
    }
    
    // Really just checks if "columns" case, that it's OK to insert
    // FROM case is more relaxed
    protected boolean isInsertOK(DropTargetEvent event) {
        if( dragSourcesAreTables(event) ) {
            if( isMouseInFrom(event) || sqlIsEmpty() ) {
                return true;
            }
            return false;
        }
        
        if( dragSourcesAreColumns(event) && isMouseInSelect(event) ) {
            int offset = sqlPanel.getCorrectedCaretOffset(getDropOffset(event));
            return sqlPanel.isInsertOK(offset);
        }
        
        return false;
    }
    
    public boolean isMouseInFrom(DropTargetEvent event) {
        int offset = 0;
        try {
            offset = sqlPanel.getCorrectedCaretOffset(getDropOffset(event));
            if( sqlPanel.isIndexWithin(offset, DisplayNodeConstants.FROM) ) {
                return true;
            }
        } catch (IllegalArgumentException ex ) {
            // we expect this exception whenever the mouse is not within a DisplayComponent (i.e. Over some text object in
            // Sql Editor text widget.
        }

        return false;
    }
    
    private int getDropOffset(DropTargetEvent event) throws IllegalArgumentException {
        return sqlPanel.getTextViewer().getTextWidget().getOffsetAtLocation(getMousePoint(event));
    }
    
    public boolean isMouseInSelect(DropTargetEvent event) {
        int offset = 0;
        try {
            offset = sqlPanel.getCorrectedCaretOffset(getDropOffset(event));
            if( sqlPanel.isIndexWithin(offset, DisplayNodeConstants.SELECT) ) {
                return true;
            }
        } catch (IllegalArgumentException ex ) {
            // we expect this exception whenever the mouse is not within a DisplayComponent (i.e. Over some text object in
            // Sql Editor text widget.
        }

        return false;
    }
    
    private boolean dragSourcesAreTables(DropTargetEvent event) {
        boolean result = false;
        List eObjList = getEventEObjects(event);
        if( !eObjList.isEmpty() ) {
            result = TransformationSourceManager.canAdd(transformation, eObjList, this);
        }
        
        return result;
    }
    
    private boolean dragSourcesAreColumns(DropTargetEvent event) {
        boolean result = false;
        List eObjList = getEventEObjects(event);
        if( !eObjList.isEmpty() ) {
            EObject nextEObj = null;
            result = true;
            for (Iterator iter = eObjList.iterator(); iter.hasNext(); ) {
                nextEObj = (EObject)iter.next();
                if( !com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspectHelper.isColumn(nextEObj) || SqlAspectHelper.isInputParameter(nextEObj) ) {
                    result = false;
                    break;
                }
            }
        }
        
        return result;
    }
    
    public List getEventEObjects(DropTargetEvent event) {
        Transfer[] transfers = ((DropTarget)event.getSource()).getTransfer();
        for( int i=0; i<transfers.length; i++ ) {
            if( transfers[i] instanceof EObjectTransfer ) {
                EObjectTransfer transfer = (EObjectTransfer)transfers[i];
                if( transfer.getObject() != null && transfer.getObject() instanceof List) {
                    return (List)transfer.getObject();
                }
                return Collections.EMPTY_LIST;
            }
            
        }
        return Collections.EMPTY_LIST;
    }
    
    public boolean sqlIsEmpty() {
        String sqlText = sqlPanel.getText();
        
        if( sqlText== null ||
            sqlText.length() == 0 ||
            sqlText.equals(SqlConstants.BLANK)) {
            return true;
        }
        
        return false;
    }

    /** 
     * @see org.eclipse.swt.dnd.DropTargetListener#drop(org.eclipse.swt.dnd.DropTargetEvent)
     * @since 4.3
     */
    public void drop(DropTargetEvent event) {
//        System.out.println("SQLPDTL.drop()");
        event.detail = DND.DROP_COPY;
        final DropTargetEvent dtEvent = event;
        if( dragSourcesAreTables(event) ) {
            if( isMouseInFrom(event) || sqlIsEmpty() ) {
                UiBusyIndicator.showWhile(null, new Runnable() {
                    public void run() {
                        executeDropInFrom(getEventEObjects(dtEvent));
                    }
                });
            }
        } else if( dragSourcesAreColumns(event) && isMouseInSelect(event) ) {
            boolean requiredStart = ModelerCore.startTxn(true, true, "Add To Select", txnSource); //$NON-NLS-1$
            boolean succeeded = false;
            try {
                // Add each source
                succeeded = executeDropInSelect(getEventEObjects(event), event);
            } finally {
                //if we started the txn, commit it.
                if(requiredStart){
                    if(succeeded) {
                        ModelerCore.commitTxn();
                    } else {
                        ModelerCore.rollbackTxn();
                    }
                }
            }
        }
    }

    /** 
     * @see org.eclipse.swt.dnd.DropTargetListener#dropAccept(org.eclipse.swt.dnd.DropTargetEvent)
     * @since 4.3
     */
    public void dropAccept(DropTargetEvent event) {
    }

    /** 
     * @see org.eclipse.jface.util.TransferDropTargetListener#isEnabled(org.eclipse.swt.dnd.DropTargetEvent)
     * @since 4.3
     */
    public boolean isEnabled(DropTargetEvent event) {
        if( dragSourcesAreTables(event) ) {
            if( isMouseInFrom(event) ) {
                return true;
            }
            if( sqlIsEmpty() ) {
                return true;
            }
            return false;
        }
        
        if( dragSourcesAreColumns(event) && isMouseInSelect(event) ) {
            return true;
        }
        
        return false;
    }
    
    public void executeDropInFrom(List dropList) {
        if( dropList.isEmpty() )
            return;
        
        // We need to see if we can add the list of objects to the transformation
        boolean canAdd = false;

        if( transformation != null )
            canAdd = TransformationSourceManager.canAdd(transformation, dropList, this);
        
        if( canAdd ) {
            //start txn
            boolean canUndo = ITransformationDiagramActionConstants.DiagramActions.UNDO_ADD_TRANSFORMATION_SOURCE;
            boolean requiredStart = ModelerCore.startTxn(true, canUndo, "Add Sources", null); //$NON-NLS-1$
            boolean succeeded = false;
            try {
                // Add each source
                TransformationSourceManager.addSources(transformation, dropList);
                succeeded = true;
            } finally {
                //if we started the txn, commit it.
                if(requiredStart){
                    if(succeeded) {
                        ModelerCore.commitTxn();
                    } else {
                        ModelerCore.rollbackTxn();
                    }
                }
            }
            
            
        } else {
            // Need to throw up a dialog stating that user can't add these objects to the 
            TransformationSourceManager.warnUserAboutInvalidSources(dropList);
        }
    }
    
    public boolean executeDropInSelect(List dropList, DropTargetEvent event) {
        if( dropList.isEmpty() )
            return false;

        // If any are not columns, disable and break
        List elemNames = new ArrayList(dropList.size());
        List parentNames = new ArrayList(dropList.size());
        Iterator iter = dropList.iterator();
        while(iter.hasNext()) {
            EObject eObj = (EObject)iter.next();
            // Column fullname
            String elemFullName = TransformationHelper.getSqlEObjectFullName(eObj);
            // Table fullname
            EObject parentEObj = eObj.eContainer();
            String groupFullName = TransformationHelper.getSqlEObjectFullName(parentEObj);
            // Add the element and table names to the lists
            if(elemFullName!=null && groupFullName!=null) {
                elemNames.add(elemFullName);
                parentNames.add(groupFullName);
            }
        }
        
        // If editor cursor is within the SELECT, add elements at the cursor, otherwise add to end
        // Get Offset
        try {
            int offset = sqlPanel.getCorrectedCaretOffset(getDropOffset(event));
            if(sqlPanel.isIndexWithin(offset, DisplayNodeConstants.SELECT)) {
                sqlPanel.insertElements(elemNames,parentNames,offset,null);
            } else {
                sqlPanel.insertElementsAtEndOfSelect(elemNames,parentNames,null);
            }
        } catch (IllegalArgumentException ex ) {
            return false;
        }
        
        return true;
    }
    
    /** 
     * @param transformation The transformation to set.
     * @since 4.3
     */
    public void setTransformation(SqlTransformationMappingRoot transformation) {
        this.transformation = transformation;
    }
    
    public SqlTransformationMappingRoot getTransformation() {
        return this.transformation;
    }
}
