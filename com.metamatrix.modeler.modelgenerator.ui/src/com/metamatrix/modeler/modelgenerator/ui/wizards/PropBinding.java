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

package com.metamatrix.modeler.modelgenerator.ui.wizards;


/**
 * PropertyBinding Business Object
 * 
 * A PropBinding has the following properties:
 * (1) Source UML property
 * (2) Target Relational propery
 * 
 * @author Mark Drilling
 *
 * 
 */
public class PropBinding {
        
    // Binding 
    private Object srcUmlProp;
    private Object tgtRelProp;
    
    /**
     * Create a Binding given only the source
     * 
     * @param sourceProp the source uml property
     */
    public PropBinding(Object sourceProp) {
        setSourceProp(sourceProp);
    }

    /**
     * Create a Binding given both the source and target properties
     * 
     * @param sourceProp the source
     * @param targetProp the target
     */
    public PropBinding(Object sourceProp, Object targetProp) {
        setSourceProp(sourceProp);
        setTargetProp(targetProp);
    }

    /**
     * @return true if bound, false otherwise
     */
    public boolean isBound() {
        return (this.tgtRelProp!=null) ? true : false;
    }
    
    /**
     * @return binding source property
     */
    public Object getSourceProp() {
        return this.srcUmlProp;
    }

    /**
     * @return binding target property
     */
    public Object getTargetProp() {
        return this.tgtRelProp;
    }

    
    /**
     * Set the source property
     * @param srcProp
     */
    public void setSourceProp(Object srcProp) {
        if(srcProp!=null) {
            this.srcUmlProp = srcProp;
        }
    }
    /**
     * Set the target property
     * @param tgtProp
     */
    public void setTargetProp(Object tgtProp) {
        if(tgtProp!=null) {
            this.tgtRelProp = tgtProp;
        }
    }


}
