/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.table;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.internal.core.ObjectExtension;
import com.metamatrix.modeler.internal.ui.PluginConstants;
import com.metamatrix.modeler.internal.ui.properties.ModelObjectPropertySource;
import com.metamatrix.modeler.internal.ui.properties.udp.ExtensionPropertyDescriptor;
import com.metamatrix.modeler.internal.ui.properties.udp.ExtensionPropertySource;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectUtilities;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.ui.UiConstants;

/**
 * ModelRowElement represents a row of properties in the ModelTableEditor's TableViewer.
 */
public class ModelRowElement {

    private EObject modelObject;
    private ModelObjectTableModel tableModel;
    
    /** the PropertySource for this EObject, obtained from the EMF PropertySourceProvider */ 
    private IPropertySource propertySource;
    
    /**
     * Construct an instance of ModelRowElement representing the specified modelObject.
     * @param modelObject
     * @param tableModel
     */
    public ModelRowElement(EObject modelObject, ModelObjectTableModel tableModel) {
        this.modelObject = modelObject;
        this.tableModel = tableModel;
        this.propertySource = tableModel.getPropertySource(modelObject);
    }

    /**
     * Get the true Object value for the modelObject property that can be used in a cell editor.
     * @param propertyID
     * @return
     */
    public final Object getValueObject(String propertyID) {
        if ( propertyID.equals(UiConstants.LOCATION_KEY) ) {
            // return the name of this object's parent
            return ModelUtilities.getEMFLabelProvider().getText(modelObject.eContainer());
        } else if ( propertyID.equals(UiConstants.DESCRIPTION_KEY) ) {
            // return this object's description
            return ModelObjectUtilities.getDescription(modelObject);
        } else { 
            // get the value from the PropertySource
            Object value = propertySource.getPropertyValue(propertyID); //tableModel.getPropertyId(propertyID));
            
            if ( value instanceof ItemPropertyDescriptor.PropertyValueWrapper ) {
                value = ((ItemPropertyDescriptor.PropertyValueWrapper) value).getEditableValue(this.modelObject); 
            }
            return value;
        }
    }
    
    /**
     * Determine if the proposed value is invalid and, if so, return an error message that
     * can be displayed to the user.
     * @param propertyID
     * @param value
     * @return
     */
    public final String getInvalidValueMessage(String propertyID, Object value) {
        String result = null;

        // need to convert String literals to EDataType values
        Object object = propertySource.getEditableValue();

        IItemPropertySource itemSource = (IItemPropertySource) ModelerCore.getMetamodelRegistry().getAdapterFactory().adapt(object, IItemPropertySource.class);
        IItemPropertyDescriptor itemDescriptor = itemSource.getPropertyDescriptor(object, propertyID);

        if (itemDescriptor != null) {
            EStructuralFeature feature = (EStructuralFeature) itemDescriptor.getFeature(object);

            if ( value instanceof String ) { 
                if ( ModelerCore.getModelEditor().isDatatypeFeature( modelObject, feature) ) {
                    try {
                    	// Shorten datatype if it's the long version
                    	String theValue = getDatatypeShortString((String)value);
                        EObject testValue = ModelerCore.getDatatypeManager(modelObject,true).findDatatype( theValue );
                        if ( testValue == null ) {
                            result = UiConstants.Util.getString("ModelRowElement.invalidValueError", value, propertyID); //$NON-NLS-1$
                        }
                    } catch ( ModelerCoreException mce ) {
                        ModelerCore.Util.log(IStatus.ERROR, mce, mce.getMessage());                                                           
                    }
                } else {
                    if ( feature.getEType() instanceof EDataType ) {
                        final EDataType dt = (EDataType)feature.getEType();
                        final EPackage ePackage = dt.getEPackage();
                        final EFactory fac = ePackage.getEFactoryInstance();
                        // Try to convert to correct type from String
                        try {
                            fac.createFromString(dt, (String)value);
                        // Handle exception - error in conversion
                        } catch (Exception e) {
                            result = UiConstants.Util.getString("ModelRowElement.invalidValueError", value, propertyID); //$NON-NLS-1$
                        }
                    }
                }
            } else {
                result = UiConstants.Util.getString("ModelRowElement.unsupportedError", propertyID); //$NON-NLS-1$
            }
        }

        return result;
    }
    
    /**
     * If a colon is detected in the long string, shorten it so that the value can be set properly.
     * @param longString the full datatype string
     * @return the shortened datatype string
     */
    private String getDatatypeShortString(String longString) {
    	if(longString==null) return null;
    	
    	String modValue = longString;
    	int colonIndex = longString.indexOf(':');
    	if(colonIndex!=-1) {
    		modValue = longString.substring(0,colonIndex).trim();
    	}
    	return modValue;
    }
    
    /**
     * Determine if the proposed value is invalid and, if so, return an error message that
     * can be displayed to the user.
     * @param propertyID
     * @param value
     * @return
     */
    public final String getReferenceColumnMessage(String propertyID, Object value) {
        String result = null;

        // need to convert String literals to EDataType values
        Object object = propertySource.getEditableValue();
        
        IItemPropertySource itemSource = (IItemPropertySource) ModelerCore.getMetamodelRegistry().getAdapterFactory().adapt(object, IItemPropertySource.class);
        
        IItemPropertyDescriptor itemDescriptor = itemSource.getPropertyDescriptor(object, propertyID);

        if (itemDescriptor != null) {
            EStructuralFeature feature = (EStructuralFeature) itemDescriptor.getFeature(object);

            if ( value instanceof String ) { 
                if ( !ModelerCore.getModelEditor().isDatatypeFeature( modelObject, feature) ) {
                    if ( !(feature.getEType() instanceof EDataType) ) {

                        // if not a DataType, it will be an EReference, protect it:
                        result = ClipboardPasteStatusRecord.REFERENCE_COLUMN_MSG;                        
                    }
                }
            }
        }

        return result;
    }
        
    /**
     * Determine if the proposed value is invalid and, if so, return an error message that
     * can be displayed to the user.
     * @param propertyID
     * @param value
     * @return
     */
    public final String getInvalidValueMessage(ExtensionPropertyDescriptor propertyID, Object value) {
        String result = null;
        EObject eObj = propertyID.getExtensionObject();
        if(eObj!=null && eObj instanceof ObjectExtension) {
        	ObjectExtension objExt = (ObjectExtension)eObj;
            ExtensionPropertySource eps = new ExtensionPropertySource(objExt.getExtendedObject());
            // Determine if the supplied value can be converted to appropriate type
            if(!eps.canConvertToCorrectDatatype(propertyID,value)) {
                result = UiConstants.Util.getString("ModelRowElement.invalidValueError", value, propertyID.toString()); //$NON-NLS-1$
            }
        }
        return result;
    }

    /**
     * Get the value for the modelObject for the specified propertyID.
     * @return
     */
    public final void setValue(String propertyID, Object value){
        if ( propertyID.equals(UiConstants.DESCRIPTION_KEY) ) {
            String desc = ModelObjectUtilities.getDescription(modelObject);
            if ( desc == null ) {
                // don't set empty string
                if ( value != null && ! PluginConstants.EMPTY_STRING.equals(value) ) {
                    ModelObjectUtilities.setDescription(modelObject, (String) value, this);
                }
            } else {
                if ( ! desc.equals(value) ) { 
                    ModelObjectUtilities.setDescription(modelObject, (String) value, this);
                }
            }
            
        } else if ( propertyID.equals(UiConstants.LOCATION_KEY) ) {
            // do nothing : can't set a location

        } else {
            try {        
                
                Object object = propertySource.getEditableValue();
                IItemPropertySource itemSource = (IItemPropertySource) ModelerCore.getMetamodelRegistry().getAdapterFactory().adapt(object, IItemPropertySource.class);
                IItemPropertyDescriptor itemDescriptor = itemSource.getPropertyDescriptor(object, propertyID);
                if ( itemDescriptor != null ) {
                    EStructuralFeature feature = (EStructuralFeature) itemDescriptor.getFeature(object);

                    if ( value instanceof String ) { 
                        if ( ModelerCore.getModelEditor().isDatatypeFeature( modelObject, feature) ) {
                        	String theValue = getDatatypeShortString((String)value);
                            try {
                                value = ModelerCore.getDatatypeManager(modelObject,true).findDatatype( theValue );
                            } catch ( ModelerCoreException mce ) {
                                ModelerCore.Util.log(IStatus.ERROR, mce, mce.getMessage());                                       
                            }
                        }
                    }
                }
                    
                Object currentValue = propertySource.getPropertyValue(tableModel.getPropertyId(propertyID));
                if ( currentValue == null ) {
                    if ( value != null ) {
                        propertySource.setPropertyValue(tableModel.getPropertyId(propertyID), value);
                    }
                } else {
                    if ( ! currentValue.equals(value) ) {
                        propertySource.setPropertyValue(tableModel.getPropertyId(propertyID), value);
                    }
                }                        

            } catch (Exception e) {
                String message = UiConstants.Util.getString("ModelRowElement.setValueError", value, propertyID, modelObject.eClass().getName()); //$NON-NLS-1$
                UiConstants.Util.log(IStatus.ERROR, e, message);
                String title = UiConstants.Util.getString("ModelRowElement.setValueErrorTitle");  //$NON-NLS-1$
                MessageDialog.openError(null, title, message);
            }

        }
    }

    /**
     * Get the value for the modelObject for the specified propertyID.
     * @return
     */
    public final void setValue(ExtensionPropertyDescriptor descriptor, Object value){
        try {  
            if(propertySource instanceof ModelObjectPropertySource) {
                ((ModelObjectPropertySource)propertySource).setPropertyValue(descriptor,value);
            }
        } catch (Exception e) {
          String message = UiConstants.Util.getString("ModelRowElement.setValueError", value, descriptor.toString(), modelObject.eClass().getName()); //$NON-NLS-1$
          UiConstants.Util.log(IStatus.ERROR, e, message);
          String title = UiConstants.Util.getString("ModelRowElement.setValueErrorTitle");  //$NON-NLS-1$
          MessageDialog.openError(null, title, message);
        }
    }

    /**
     * Get string representation of the value for the modelObject for the indexed property.
     * @return
     */
    public final Object getValue(int index){
        Object result = null;
        Object propertyId = tableModel.getPropertyIdAtIndex(index);
        if (tableModel.isLocationColumn(propertyId)) {
            result = ModelUtilities.getEMFLabelProvider().getText(modelObject.eContainer());
        } else if (tableModel.isDescriptionColumn(propertyId)) {
            result = ModelObjectUtilities.getDescription(modelObject);
        } else {
            IPropertyDescriptor descriptor = this.tableModel.getPropertyDescriptor(propertyId);
            result = descriptor.getLabelProvider().getText(propertySource.getPropertyValue(propertyId));
        }
        
        return result;
    }
    
    /**
     * Convenience method for obtaining a propertyID for a column index.
     * @param index
     * @return
     */
    public final Object getPropertyIdForColumn(int index) {
        return tableModel.getPropertyIdAtIndex(index);
    }

    /**
     * Get the EObject represented by this row.
     * @return
     */
    public EObject getModelObject() {
        return modelObject;
    }

}
