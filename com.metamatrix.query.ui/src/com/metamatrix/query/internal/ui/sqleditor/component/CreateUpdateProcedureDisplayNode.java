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

package com.metamatrix.query.internal.ui.sqleditor.component;

import java.util.ArrayList;

import com.metamatrix.query.sql.ReservedWords;
import com.metamatrix.query.sql.proc.Block;
import com.metamatrix.query.sql.proc.CreateUpdateProcedureCommand;

/**
 * The <code>CreateUpdateProcedureDisplayNode</code> class is used to represent a CreateUpdateProcedureCommand.
 */
public class CreateUpdateProcedureDisplayNode extends DisplayNode {

    // ===========================================================================================================================
    // Constructors

    /**
     * @param parentNode
     *            the parent DisplayNode of this.
     * @param procCommand
     *            The CreateUpdateProcedureCommand language object used to construct this display node.
     */
    public CreateUpdateProcedureDisplayNode(DisplayNode parentNode,
                                            CreateUpdateProcedureCommand procCommand) {
        this.parentNode = parentNode;
        this.languageObject = procCommand;
        createChildNodes();
    }

    // ===========================================================================================================================
    // Methods

    /**
     * Create the child nodes for this type of DisplayNode. For a CreateUpdateProcedureDisplayNode, there is one child - the
     * BlockDisplayNode
     */
    private void createChildNodes() {
        childNodeList = new ArrayList();
        CreateUpdateProcedureCommand command = (CreateUpdateProcedureCommand)this.getLanguageObject();
        // int indent = this.getIndentLevel();

        // ----------------------------------------------------
        // Add Block to childNodeList
        // ----------------------------------------------------
        Block block = command.getBlock();
        if (block != null) {
            childNodeList.add(DisplayNodeFactory.createDisplayNode(this, block));
        }

        // ----------------------------------------------------
        // Create the Display Node List
        // ----------------------------------------------------
        createDisplayNodeList(command);
    }

    /**
     * Create the DisplayNode list for this type of DisplayNode. This is a list of all the lowest level nodes for this
     * DisplayNode.
     */
    private void createDisplayNodeList(CreateUpdateProcedureCommand command) {
        displayNodeList = new ArrayList();

        // int indent = this.getIndentLevel();
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this, ReservedWords.CREATE));
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this, SPACE));
        if (!command.isUpdateProcedure()) {
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this, ReservedWords.VIRTUAL));
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this, SPACE));
        }
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this, ReservedWords.PROCEDURE));
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this, SPACE));
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this, CR));

        // Add the Block
        DisplayNode childNode = (DisplayNode)getChildren().get(0);
        displayNodeList.addAll(childNode.getDisplayNodeList());
    }
}
