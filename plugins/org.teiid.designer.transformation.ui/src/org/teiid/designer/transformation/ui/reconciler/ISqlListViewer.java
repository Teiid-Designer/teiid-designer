/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.ui.reconciler;

import org.teiid.designer.query.sql.lang.IExpression;

/**
 * @since 8.0
 */
public interface ISqlListViewer {
    
    /**
     * Update the view to reflect the fact that a symbol was added 
     * to the symbol list
     * 
     * @param symbol
     */
    public void addSymbol(IExpression symbol);
    
    /**
     * Update the view to reflect the fact that a symbol was added 
     * to the symbol list
     * 
     * @param symbol
     */
    public void insertSymbol(IExpression symbol,int index);
    
    /**
     * Update the view to reflect the fact that symbols were added 
     * to the symbol list
     * 
     * @param symbols
     */
    public void addSymbols(Object[] symbols);
    
    /**
     * Update the view to reflect the fact that a symbol was removed 
     * from the symbol list
     * 
     * @param symbol
     */
    public void removeSymbol(IExpression symbol);
    
    /**
     * Update the view to reflect the fact that symbols were removed 
     * from the symbol list
     * 
     * @param symbol
     */
    public void removeSymbols(Object[] symbols);
    
    /**
     * Update the view to reflect the fact that one of the symbols
     * was modified 
     * 
     * @param symbol
     */
    public void updateSymbol(IExpression symbol);
    
    /**
     * Update the view to reflect the fact that one of the symbols
     * was modified 
     * 
     * @param updateLabels
     */
    public void refresh(boolean updateLabels);
}
