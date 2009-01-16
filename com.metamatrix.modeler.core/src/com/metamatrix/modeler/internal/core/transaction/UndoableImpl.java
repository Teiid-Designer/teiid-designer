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

package com.metamatrix.modeler.internal.core.transaction;

import java.util.Collection;

import org.eclipse.emf.common.command.CompoundCommand;

import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.core.util.StringUtilities;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.container.Container;
import com.metamatrix.modeler.core.transaction.Undoable;
import com.metamatrix.modeler.core.transaction.UnitOfWork;

/**
 * UndoableImpl
 */
public class UndoableImpl implements Undoable {
    /**
     */
    private static final long serialVersionUID = 1L;
    private final CompoundCommand command;
    private final Collection resources;
    private final Object id;
    private final Container container;
    private final Object source;
    private static final int MAX_LABEL_LENGTH = 40;
    private static final int END_LABEL_LENGTH = 25;
    private static final String ELIPSIS = "..."; //$NON-NLS-1$
    
    private String description;
    
    private boolean significant;
    /**
     * Construct an instance of UndoableImpl.
     * 
     */
    public UndoableImpl(final Container container, final CompoundCommand command, final Collection resources, Object id, final Object source) {
        ArgCheck.isNotNull(container);
        ArgCheck.isNotNull(command);
        ArgCheck.isNotNull(resources);
        ArgCheck.isNotNull(id);
        
        this.container = container;
        this.command = command;
        this.resources = resources;
        this.id = id;
        this.source = source;
        this.significant = true;
    }
    
    public UndoableImpl(final Container container, final CompoundCommand command, final Collection resources, Object id) {
        this(container, command, resources, id, null);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.transaction.Undoable#undo()
     */
    public void undo() throws ModelerCoreException {
        if(command.canUndo() ){
            boolean requiredStart = false;
            final UnitOfWork uow = container.getEmfTransactionProvider().getCurrent();
            if(uow.requiresStart() ){
                uow.begin();
                uow.setUndoable(false);
                requiredStart = true;
            }
            
            command.undo();
            
            if(requiredStart){
                try {
                    uow.commit();
                } catch (ModelerCoreException e) {
                    uow.rollback();
                    throw e;
                }
            }
            
            return;
        }
        
        throw new ModelerCoreException(ModelerCore.Util.getString("UndoableImpl.Can_not_perform_undo_for_{0}_1", command.getLabel() )   ); //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.transaction.Undoable#canUndo()
     */
    public boolean canUndo() {
        return command.canUndo();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.transaction.Undoable#redo()
     */
    public void redo() throws ModelerCoreException {
        if(command.canExecute() ){
            boolean requiredStart = false;
            final UnitOfWork uow = container.getEmfTransactionProvider().getCurrent();
            if(!uow.isStarted() ){
                uow.begin();
                uow.setUndoable(false);
                requiredStart = true;
            }
            
            command.execute();
            
            if(requiredStart){
                try {
                    uow.commit();
                } catch (ModelerCoreException e) {
                    uow.rollback();
                    throw e;
                }
            }
            
            return;
        }
        
        throw new ModelerCoreException(ModelerCore.Util.getString("UndoableImpl.Can_not_perform_redo_for_{0}_2", command.getLabel() )   ); //$NON-NLS-1$

    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.transaction.Undoable#canRedo()
     */
    public boolean canRedo() {
        return command.canExecute();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.transaction.Undoable#die()
     */
    public void die() {
        resources.clear();
        significant = true;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.transaction.Undoable#getPresentationName()
     */
    public String getPresentationName() {
        if(description == null){
            return command.getLabel();
        }
        
        return description;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.transaction.Undoable#getUndoPresentationName()
     */
    public String getUndoPresentationName() {
        String undoPresentationName = StringUtil.Constants.EMPTY_STRING;
        
        if(description == null){
            undoPresentationName = StringUtilities.condenseToLength(command.getLabel(), MAX_LABEL_LENGTH, END_LABEL_LENGTH, ELIPSIS);
        } else {
            undoPresentationName = StringUtilities.condenseToLength(description, MAX_LABEL_LENGTH, END_LABEL_LENGTH, ELIPSIS);
        }

        return ModelerCore.Util.getString("UndoableImpl.Undo__3") + undoPresentationName; //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.transaction.Undoable#getRedoPresentationName()
     */
    public String getRedoPresentationName() {
        String undoPresentationName = StringUtil.Constants.EMPTY_STRING;
        
        if(description == null){
            undoPresentationName = StringUtilities.condenseToLength(command.getLabel(), MAX_LABEL_LENGTH, END_LABEL_LENGTH, ELIPSIS);
        } else {
            undoPresentationName = StringUtilities.condenseToLength(description, MAX_LABEL_LENGTH, END_LABEL_LENGTH, ELIPSIS);
        }

        return ModelerCore.Util.getString("UndoableImpl.Redo__4") + undoPresentationName; //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.transaction.Undoable#getId()
     */
    public Object getId() {
        return this.id;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.transaction.Undoable#getResources()
     */
    public Collection getResources() {
        return this.resources;
    }

    /**
     * @return
     */
    public CompoundCommand getCommand() {
        return this.command;
    }
    
    @Override
    public String toString(){
        if(this.description != null){
            return description;
        }
        
        return this.command.toString();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.transaction.Undoable#isSignificant()
     */
    public boolean isSignificant() {
        return significant;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.transaction.Undoable#setSignificant(boolean)
     */
    public void setSignificant(final boolean isSignificant) {
        this.significant = isSignificant;
    }

    /**
     * @param string
     */
    public void setDescription(String string) {
        description = string;
    }

    /** 
     * @see com.metamatrix.modeler.core.transaction.Undoable#getSource()
     * @since 4.3
     */
    public Object getSource() {
        return source;
    }

}
