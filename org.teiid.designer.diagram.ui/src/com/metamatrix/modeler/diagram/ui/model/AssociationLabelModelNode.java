/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.model;

/**
 * AssociationLabelModelNode
 */
public class AssociationLabelModelNode extends LabelModelNode {
	public static final int GENERAL = -1;
    public static final int NAME = 0;
    public static final int SOURCE_ROLE_NAME = 1;
    public static final int TARGET_ROLE_NAME = 2;
    public static final int SOURCE_MULTIPLICITY = 3;
    public static final int TARGET_MULTIPLICITY = 4;
    public static final int STEREOTYPE = 5;
    
    private int labelTypeId = GENERAL;
    /**
     * Construct an instance of AssociationLabelModelNode.
     * 
     */
    public AssociationLabelModelNode(String labelString , int typeId ) {
        super( labelString );
        setLabelType(typeId);
    }

    /**
     * @return
     */
    public int getLabelType() {
        return labelTypeId;
    }

    /**
     * @param i
     */
    public void setLabelType(int i) {
        labelTypeId = i;
    }

}
