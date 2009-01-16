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

package com.metamatrix.modeler.transformation.ui.reconciler;

import com.metamatrix.query.sql.symbol.SingleElementSymbol;

public interface ISqlListViewer {
    
    /**
     * Update the view to reflect the fact that a symbol was added 
     * to the symbol list
     * 
     * @param symbol
     */
    public void addSymbol(SingleElementSymbol symbol);
    
    /**
     * Update the view to reflect the fact that a symbol was added 
     * to the symbol list
     * 
     * @param symbol
     */
    public void insertSymbol(SingleElementSymbol symbol,int index);
    
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
    public void removeSymbol(SingleElementSymbol symbol);
    
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
    public void updateSymbol(SingleElementSymbol symbol);
    
    /**
     * Update the view to reflect the fact that one of the symbols
     * was modified 
     * 
     * @param updateLabels
     */
    public void refresh(boolean updateLabels);
}
