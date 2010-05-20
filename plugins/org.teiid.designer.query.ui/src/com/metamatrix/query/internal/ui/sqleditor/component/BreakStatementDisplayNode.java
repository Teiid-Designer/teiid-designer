/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.query.internal.ui.sqleditor.component;

import java.util.ArrayList;
import com.metamatrix.query.sql.ReservedWords;
import org.teiid.query.sql.proc.BreakStatement;

/**
 * The <code>BreakStatementDisplayNode</code> class is used to represent 
 * an BreakStatement LanguageObject.
 */
public class BreakStatementDisplayNode extends StatementDisplayNode {
    
    ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    ///////////////////////////////////////////////////////////////////////////

    /**
     *  BreakStatementDisplayNode constructor
     *  @param parentNode the parent DisplayNode of this.
     *  @param statement The BreakStatement language object used to construct this display node.
     */
    public BreakStatementDisplayNode(DisplayNode parentNode, BreakStatement statement) {
        this.parentNode = parentNode;
        this.languageObject = statement;
        createDisplayNodeList();
    }
    
    ///////////////////////////////////////////////////////////////////////////
    // PRIVATE METHODS
    ///////////////////////////////////////////////////////////////////////////


    /**
     *   Create the DisplayNode list for this type of DisplayNode.  This is a list of
     *  all the lowest level nodes for this DisplayNode.
     */
    private void createDisplayNodeList() {
        displayNodeList = new ArrayList();

        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,ReservedWords.BREAK));
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SEMICOLON));
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this, CR));
    }    

}
