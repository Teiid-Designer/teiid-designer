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
