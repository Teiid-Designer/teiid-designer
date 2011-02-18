/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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
