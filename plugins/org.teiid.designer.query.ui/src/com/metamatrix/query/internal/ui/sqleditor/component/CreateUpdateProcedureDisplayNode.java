/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.query.internal.ui.sqleditor.component;

import java.util.ArrayList;

import org.teiid.language.SQLConstants;
import org.teiid.query.sql.proc.Block;
import org.teiid.query.sql.proc.CreateUpdateProcedureCommand;

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
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this, SQLConstants.Reserved.CREATE));
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this, SPACE));
        if (!command.isUpdateProcedure()) {
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this, SQLConstants.Reserved.VIRTUAL));
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this, SPACE));
        }
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this, SQLConstants.Reserved.PROCEDURE));
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this, SPACE));
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this, CR));

        // Add the Block
        DisplayNode childNode = getChildren().get(0);
        displayNodeList.addAll(childNode.getDisplayNodeList());
    }
}
