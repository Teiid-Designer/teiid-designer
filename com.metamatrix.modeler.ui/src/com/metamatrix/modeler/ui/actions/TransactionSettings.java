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

package com.metamatrix.modeler.ui.actions;


/** 
 * @since 4.3
 */
public class TransactionSettings {
    
    
    boolean bDoTransaction = true;
    boolean bIsSignificant = true;
    boolean bIsUndoable = true;
    String sDescription;
    Object oSource;


    public TransactionSettings() {
        
    }
    
    public TransactionSettings( Object oSource ) {
        this.oSource = oSource;                
    }
    
    public TransactionSettings( boolean bDoTransaction, 
                                boolean bIsSignificant,
                                boolean bIsUndoable,
                                String sDescription,
                                Object oSource ) {
        
        this.bDoTransaction = bDoTransaction;
        this.bIsSignificant = bIsSignificant;
        this.bIsUndoable = bIsUndoable;
        this.sDescription = sDescription;
        this.oSource = oSource;        
    }
    
    
    public void setDoTransaction( boolean bDoTransaction ) {
        this.bDoTransaction = bDoTransaction;        
    }

    public boolean doTransaction() {
        return bDoTransaction;
    }

    public void setisSignificant( boolean bIsSignificant ) {
        this.bIsSignificant = bIsSignificant;        
    }

    public boolean isSignificant() {
        return bIsSignificant;
    }
    
    public void setIsUndoable( boolean bIsUndoable ) {
        this.bIsUndoable = bIsUndoable;        
    }

    public boolean isUndoable() {
        return bIsUndoable;
    }

    public void setDescription( String sDescription ) {
        this.sDescription = sDescription;        
    }

    public String getDescription() {
        return sDescription;
    }

    public void setSource( Object oSource ) {
        this.oSource = oSource;        
    }

    public Object getSource() {
        return oSource;
    }

}
