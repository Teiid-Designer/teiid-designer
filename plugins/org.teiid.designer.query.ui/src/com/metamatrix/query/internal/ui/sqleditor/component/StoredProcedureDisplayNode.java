/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.query.internal.ui.sqleditor.component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import com.metamatrix.query.sql.ReservedWords;
import org.teiid.query.sql.lang.SPParameter;
import org.teiid.query.sql.lang.StoredProcedure;
import org.teiid.query.sql.visitor.SQLStringVisitor;

/**
 * The <code>StoredProcedureDisplayNode</code> class is used to represent a StoredProcedure command.
 */
public class StoredProcedureDisplayNode extends DisplayNode {

    // /////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    // /////////////////////////////////////////////////////////////////////////

    /**
     * StoredProcedureDisplayNode constructor
     * 
     * @param parentNode the parent DisplayNode of this.
     * @param insert The StoredProcedure language object used to construct this display node.
     */
    public StoredProcedureDisplayNode( DisplayNode parentNode,
                                       StoredProcedure storedProc ) {
        this.parentNode = parentNode;
        this.languageObject = storedProc;
        createDisplayNodeList();
    }

    // /////////////////////////////////////////////////////////////////////////
    // PRIVATE METHODS
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Create the DisplayNode list for this type of DisplayNode. This is a list of all the lowest level nodes for this
     * DisplayNode.
     */
    private void createDisplayNodeList() {
        displayNodeList = new ArrayList();
        StoredProcedure storedProc = (StoredProcedure)(this.getLanguageObject());

        // position of the child in childNodeList
        // int childIndex = 0;

        // int indent = this.getIndentLevel();
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this, ReservedWords.EXEC));
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this, SPACE));
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this, storedProc.getProcedureName()));
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this, LTPAREN));

        List params = storedProc.getInputParameters();
        if (params != null) {
            Iterator iter = params.iterator();

            while (iter.hasNext()) {
                SPParameter param = (SPParameter)iter.next();

                if (storedProc.displayNamedParameters()) {
                    String part = SQLStringVisitor.escapeSinglePart(param.getParameterSymbol().getShortName());
                    displayNodeList.add(DisplayNodeFactory.createDisplayNode(this, part));
                    displayNodeList.add(DisplayNodeFactory.createDisplayNode(this, SPACE));
                    displayNodeList.add(DisplayNodeFactory.createDisplayNode(this, "=")); //$NON-NLS-1$
                    displayNodeList.add(DisplayNodeFactory.createDisplayNode(this, SPACE));
                }
                if (param.getExpression() == null) {
                    if (param.getName() != null) {
                        String part = SQLStringVisitor.escapeSinglePart(storedProc.getParamFullName(param));
                        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this, part));
                    } else {
                        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this, "?")); //$NON-NLS-1$
                    }
                } else {
                    String pVal = param.getExpression().toString();
                    displayNodeList.add(DisplayNodeFactory.createDisplayNode(this, pVal));
                }
                if (iter.hasNext()) {
                    displayNodeList.add(DisplayNodeFactory.createDisplayNode(this, COMMA));
                    displayNodeList.add(DisplayNodeFactory.createDisplayNode(this, SPACE));
                }
            }
        }
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this, RTPAREN));

        if (storedProc.getOption() != null) {
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this, SPACE));
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this, storedProc.getOption()));
        }
    }

}
