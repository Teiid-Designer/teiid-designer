/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.w3c.dom.Element;

import org.eclipse.xsd.XSDConcreteComponent;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.command.CommandParameter;
import org.eclipse.emf.edit.domain.EditingDomain;

/** 
 * @since 4.3
 */
public class XsdConcreteComponentAddCommand extends AddCommand {

    // Adding an XSDConcreteComponent to an EList will cause the component's xml element to be set,
    // which will be wrapped in a SetCommand. If we undo
    // this operation, the SetCommand will undo first, causing the value
    // to lose it's element. When the value is then removed from the EList,
    // it will not have a DOM representation to remove itself from. 
    // To combat this, we store all of the XSDConcreteComponents' elements
    // during a 'doExecute' or 'doRedo' and set them back during a 'doUndo'
    // (before the components are removed from the collection)
    
    //To combat this, we call updateElement on any XSDConcreteComponents that are to be removed
    // from the list BEFORE the actual remove occurs.

    private List elements;
        
    /**
     * This constructs a primitive command to add a collection of values to the specified many-valued feature of the owner.
     */
    public XsdConcreteComponentAddCommand(EditingDomain domain, EList list, Collection collection)
    {
      super(domain, list, collection, CommandParameter.NO_INDEX);
    }

    /**
     * This constructs a primitive command to insert a collection of values into the specified many-valued feature of the owner.
     */
    public XsdConcreteComponentAddCommand(EditingDomain domain, EList list, Collection collection, int index) {
        super(domain, list, collection, index);
    }

    /* (non-Javadoc)
     * @see org.eclipse.emf.edit.command.OverrideableCommand#doUndo()
     */
    @Override
    public void doExecute() {
        super.doExecute();
        loadElements();        
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.emf.edit.command.OverrideableCommand#doUndo()
     */
    @Override
    public void doRedo() {
        super.doRedo();
        loadElements();        
    }    

    private void loadElements() {
        this.elements = new ArrayList();
        for (final Iterator it = this.collection.iterator(); it.hasNext();) {
            Object o = it.next();
            if (o instanceof XSDConcreteComponent) {
                this.elements.add(((XSDConcreteComponent) o).getElement());
            }
        }
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.emf.edit.command.OverrideableCommand#doUndo()
     */
    @Override
    public void doUndo() {
        int foundIndex = 0;
        for (final Iterator it = this.collection.iterator(); it.hasNext();) {
            Object o = it.next();
            if (o instanceof XSDConcreteComponent) {
                XSDConcreteComponent component = (XSDConcreteComponent) o;
                component.setElement((Element) this.elements.get(foundIndex++));                
            }
        }
        super.doUndo();
    }               

}
