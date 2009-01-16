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

import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.swt.widgets.TableItem;

/**
 * This class implements an ICellModifier
 * An ICellModifier is called when the user modifes a cell in the 
 * tableViewer
 */

public class BindingCellModifier implements ICellModifier {
//    private static final String NAME_ATTRIBUTE = "name"; //$NON-NLS-1$
	private BindingsTablePanel bindingsTablePanel;
	
	/**
	 * Constructor 
	 * @param BindingsTablePanel an instance of a BindingsTablePanel
	 */
	public BindingCellModifier(BindingsTablePanel bindingsTablePanel) {
		super();
		this.bindingsTablePanel = bindingsTablePanel;
	}

	/**
	 * @see org.eclipse.jface.viewers.ICellModifier#canModify(java.lang.Object, java.lang.String)
	 */
	public boolean canModify(Object element, String property) {
        // Find the index of the column
        int columnIndex = bindingsTablePanel.getColumnNames().indexOf(property);

        boolean result = false;

        switch (columnIndex) {
            case 0 : // Attribute Column
                if(!bindingsTablePanel.isTargetLocked()) {
                    result = true;
                }
                break;
            case 1 : // SQL Symbol Column 
                result = false;
                break;
            default :
                result = false;
        }
        return result;  
	}

	/**
	 * @see org.eclipse.jface.viewers.ICellModifier#getValue(java.lang.Object, java.lang.String)
	 */
	public Object getValue(Object element, String property) {

		// Find the index of the column
		int columnIndex = bindingsTablePanel.getColumnNames().indexOf(property);

		Object result = null;
		Binding binding = (Binding) element;

		switch (columnIndex) {
			case 0 : // Attribute Column
                result = binding.getCurrentAttrName();
				break;
			case 1 : // SQL Symbol Column 
				result = binding.getCurrentSymbol();
				break;
			default :
				result = ""; //$NON-NLS-1$
		}
		return result;	
	}

	/**
	 * @see org.eclipse.jface.viewers.ICellModifier#modify(java.lang.Object, java.lang.String, java.lang.Object)
	 */
	public void modify(Object element, String property, Object value) {	

		// Find the index of the column 
		int columnIndex	= bindingsTablePanel.getColumnNames().indexOf(property);
        
        TableItem item = (TableItem) element;
        Binding binding = (Binding) item.getData();
		
		switch (columnIndex) {
			case 0 : // Attribute Column 
                String currentName = binding.getCurrentAttrName();
                //String currentName = getAttributeShortName(binding.getAttribute());
                String newName = ((String) value).trim();
                
                // If the name is different than the current name, set it
                if(newName != null && newName.length() > 0 && currentName!=null && !currentName.equalsIgnoreCase(newName)) {
                    binding.setNewAttrName(newName);
//                    Object attr = binding.getAttribute();
//                    if(attr!=null && attr instanceof EObject) {
//                        EAttribute nameAttr = getNameEAttribute((EObject)attr);
//                        ModelerCore.getModelEditor().setPropertyValue((EObject)attr, newName, nameAttr);
//                    }
                }
				break;
			case 1 : // SQL Symbol Column 
                //binding.setSqlSymbol(value);
				break;
			default :
		}
        bindingsTablePanel.getBindingList().bindingChanged(binding);
	}
    
//    /**
//     *  get the attribute short Name.
//     * @param attribute the attribute, may be String or EObject
//     * @return the attribute short name
//     */
//    private String getAttributeShortName(Object attribute) {
//        String name = null;
//        if(attribute!=null) {
//            if(attribute instanceof String) {
//                return (String)attribute;
//            } else if( attribute instanceof EObject ) {
//                EObject eObj = (EObject)attribute;
//                if( SqlAspectManager.isColumn(eObj) ) {
//                    SqlColumnAspect columnAspect = (SqlColumnAspect)SqlAspectManager.getSqlAspect(eObj);
//                    name = columnAspect.getName(eObj);
//                }
//            }
//        }
//        return name;
//    }
//    
//    private EAttribute getNameEAttribute(Object obj) {
//        EAttribute nameAttr = null;
//        // modTODO: When avail, handle objs with name SF not called "name"
//        if (obj != null  &&  obj instanceof EObject) {
//            final EObject eObj = (EObject)obj;
//            if (!ModelUtilities.isReadOnly(ModelUtilities.getModelResourceForModelObject(eObj))) {
//                for (final Iterator iter = eObj.eClass().getEAllAttributes().iterator();  iter.hasNext();) {
//                    EAttribute attr = (EAttribute)iter.next();
//                    if (NAME_ATTRIBUTE.equalsIgnoreCase(attr.getName())) {
//                        nameAttr = attr;
//                        break;
//                    }
//                }
//            }
//        }
//        return nameAttr;
//    }

}
