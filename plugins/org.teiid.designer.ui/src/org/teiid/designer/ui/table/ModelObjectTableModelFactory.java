/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ui.table;

import java.util.ArrayList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.teiid.designer.ui.UiConstants;
import org.teiid.designer.ui.UiPlugin;

/**
 * @since 8.0
 */
public class ModelObjectTableModelFactory implements UiConstants {
    private ModelObjectTableModelFactory() {        
    }
    
    public static ModelObjectTableModel createModelObjectTableModel(boolean supportsDescriptions, EClass tabClass, ArrayList instanceList) {
        UiPlugin.getDefault().getEObjectPropertiesOrderPreferences().addOrUpdateProperty(tabClass.getName(), LOCATION_KEY);
        UiPlugin.getDefault().getEObjectPropertiesOrderPreferences().addOrUpdateEObject(tabClass.getName(), (EObject) instanceList.get(0));        
        if (supportsDescriptions) {
            UiPlugin.getDefault().getEObjectPropertiesOrderPreferences().addOrUpdateProperty(tabClass.getName(), DESCRIPTION_KEY);
        }
        ModelObjectTableModel model = new ModelObjectTableModel(tabClass.getName(), instanceList, supportsDescriptions);
        return model;
    }
}
