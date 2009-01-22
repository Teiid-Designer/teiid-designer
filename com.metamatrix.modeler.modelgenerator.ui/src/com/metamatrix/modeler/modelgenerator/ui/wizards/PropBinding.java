/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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
