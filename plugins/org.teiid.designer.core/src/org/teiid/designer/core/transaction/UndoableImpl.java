/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.transaction;

import java.util.Collection;
import org.eclipse.emf.common.command.CompoundCommand;
import org.teiid.core.designer.ModelerCoreException;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.core.designer.util.StringUtilities;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.container.Container;

/**
 * UndoableImpl
 *
 * @since 8.0
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
        CoreArgCheck.isNotNull(container);
        CoreArgCheck.isNotNull(command);
        CoreArgCheck.isNotNull(resources);
        CoreArgCheck.isNotNull(id);
        
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
     * @See org.teiid.designer.core.transaction.Undoable#undo()
     */
    @Override
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
     * @See org.teiid.designer.core.transaction.Undoable#canUndo()
     */
    @Override
	public boolean canUndo() {
        return command.canUndo();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.transaction.Undoable#redo()
     */
    @Override
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
     * @See org.teiid.designer.core.transaction.Undoable#canRedo()
     */
    @Override
	public boolean canRedo() {
        return command.canExecute();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.transaction.Undoable#die()
     */
    @Override
	public void die() {
        resources.clear();
        significant = true;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.transaction.Undoable#getPresentationName()
     */
    @Override
	public String getPresentationName() {
        if(description == null){
            return command.getLabel();
        }
        
        return description;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.transaction.Undoable#getUndoPresentationName()
     */
    @Override
	public String getUndoPresentationName() {
        String undoPresentationName = CoreStringUtil.Constants.EMPTY_STRING;
        
        if(description == null){
            undoPresentationName = StringUtilities.condenseToLength(command.getLabel(), MAX_LABEL_LENGTH, END_LABEL_LENGTH, ELIPSIS);
        } else {
            undoPresentationName = StringUtilities.condenseToLength(description, MAX_LABEL_LENGTH, END_LABEL_LENGTH, ELIPSIS);
        }

        return ModelerCore.Util.getString("UndoableImpl.Undo__3") + undoPresentationName; //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.transaction.Undoable#getRedoPresentationName()
     */
    @Override
	public String getRedoPresentationName() {
        String undoPresentationName = CoreStringUtil.Constants.EMPTY_STRING;
        
        if(description == null){
            undoPresentationName = StringUtilities.condenseToLength(command.getLabel(), MAX_LABEL_LENGTH, END_LABEL_LENGTH, ELIPSIS);
        } else {
            undoPresentationName = StringUtilities.condenseToLength(description, MAX_LABEL_LENGTH, END_LABEL_LENGTH, ELIPSIS);
        }

        return ModelerCore.Util.getString("UndoableImpl.Redo__4") + undoPresentationName; //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.transaction.Undoable#getId()
     */
    @Override
	public Object getId() {
        return this.id;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.transaction.Undoable#getResources()
     */
    @Override
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
     * @See org.teiid.designer.core.transaction.Undoable#isSignificant()
     */
    @Override
	public boolean isSignificant() {
        return significant;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.transaction.Undoable#setSignificant(boolean)
     */
    @Override
	public void setSignificant(final boolean isSignificant) {
        this.significant = isSignificant;
    }

    /**
     * @param string
     */
    @Override
	public void setDescription(String string) {
        description = string;
    }

    /** 
     * @see org.teiid.designer.core.transaction.Undoable#getSource()
     * @since 4.3
     */
    @Override
	public Object getSource() {
        return source;
    }

}
