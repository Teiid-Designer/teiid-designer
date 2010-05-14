/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.compare.ui.tree;

import java.util.List;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.mapping.Mapping;
import org.eclipse.emf.mapping.MappingHelper;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import com.metamatrix.modeler.compare.DifferenceDescriptor;
import com.metamatrix.modeler.compare.DifferenceReport;
import com.metamatrix.modeler.compare.DifferenceType;
import com.metamatrix.modeler.compare.ui.PluginConstants;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceItem;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;

/**
 * MappingLabelProvider is a specialization of LabelProvider.
 */
public class MappingLabelProvider extends LabelProvider 
    implements PluginConstants {
    
    final WorkbenchLabelProvider workbenchProvider = new WorkbenchLabelProvider();
    private ILabelProvider lpImageLabelProvider;
    /**
     * Constructor.
     * @param showDatatypes flag whether to show datatypes as part of text string
     */
    public MappingLabelProvider( ) {
//        lpImageLabelProvider = new ModelExplorerLabelProvider();
        lpImageLabelProvider =  ModelUtilities.getEMFLabelProvider();
    }
    

    @Override
    public Image getImage(final Object node) {
        if( node instanceof DifferenceReport ) {
            return null;
        } else if( node instanceof Mapping ) {
            Mapping mapping = (Mapping)node;
            MappingHelper helper = mapping.getHelper();
            if(helper instanceof DifferenceDescriptor) {
                final DifferenceDescriptor desc = (DifferenceDescriptor)helper;
                final DifferenceType type = desc.getType();
                if ( type.getValue() == DifferenceType.DELETION ) {
                    final List<EObject> inputs = mapping.getInputs();
                    final EObject input = inputs.isEmpty() ? null : inputs.get(0);
                    if(input!=null) {
                        return lpImageLabelProvider.getImage(input); 
                    }
                } else {
                    final List<EObject> outputs = mapping.getOutputs();
                    final EObject output = outputs.isEmpty() ? null : outputs.get(0);
                    final List<EObject> inputs = mapping.getInputs();
                    final EObject input = inputs.isEmpty() ? null : inputs.get(0);
                    if(output!=null) {
                        return lpImageLabelProvider.getImage(output); 
                    } else if(input!=null) {
                        return lpImageLabelProvider.getImage(input); 
                    }
                } 
            }
        } else if (node instanceof EObject) {
            return lpImageLabelProvider.getImage(node);
        }

        if ( node instanceof ModelWorkspaceItem ) {
            return workbenchProvider.getImage(((ModelWorkspaceItem)node).getResource());
        } 
        return null;
    }
    
    @Override
    public String getText(final Object node) {
        if( node instanceof DifferenceReport ) {
            DifferenceReport diffReport = (DifferenceReport)node;
            String result = diffReport.getTitle();

            if (result == null) {
                result = diffReport.getResultUri();
            }
            
            return result;
        } else if( node instanceof Mapping ) {
            Mapping mapping = (Mapping)node;
            MappingHelper helper = mapping.getHelper();
            if(helper instanceof DifferenceDescriptor) {
                final DifferenceDescriptor desc = (DifferenceDescriptor)helper;
                final DifferenceType type = desc.getType();
                int typeValue = type.getValue();
                if ( typeValue == DifferenceType.ADDITION ) {
                    final List<EObject> outputs = mapping.getOutputs();
                    final EObject output = outputs.isEmpty() ? null : outputs.get(0);
                    if(output!=null) {
                        return ModelUtilities.getEMFLabelProvider().getText(output); 
                    }
                } else if(typeValue == DifferenceType.DELETION) {
                    final List<EObject> inputs = mapping.getInputs();
                    final EObject input = inputs.isEmpty() ? null : inputs.get(0);
                    if(input!=null) {
                        return ModelUtilities.getEMFLabelProvider().getText(input); 
                    }
                } else if(typeValue == DifferenceType.CHANGE) {
                    final List<EObject> outputs = mapping.getOutputs();
                    final EObject output = outputs.isEmpty() ? null : outputs.get(0);
                    if(output!=null) {
                        return ModelUtilities.getEMFLabelProvider().getText(output); 
                    }
                } else if(typeValue == DifferenceType.NO_CHANGE) {
                    final List<EObject> outputs = mapping.getOutputs();
                    final EObject output = outputs.isEmpty() ? null : outputs.get(0);
                    if(output!=null) {
                        return ModelUtilities.getEMFLabelProvider().getText(output); 
                    }
                } else if(typeValue == DifferenceType.CHANGE_BELOW) {
                    final List<EObject> outputs = mapping.getOutputs();
                    final EObject output = outputs.isEmpty() ? null : outputs.get(0);
                    final List<EObject> inputs = mapping.getInputs();
                    final EObject input = inputs.isEmpty() ? null : inputs.get(0);
                    if(output!=null) {
                        return ModelUtilities.getEMFLabelProvider().getText(output); 
                    } else if(input!=null) {
                        return ModelUtilities.getEMFLabelProvider().getText(input); 
                    }
                }
            }
        } else if (node instanceof EObject) {
            return ModelUtilities.getEMFLabelProvider().getText(node);
        }
        
        if ( node instanceof ModelWorkspaceItem ) {
            return workbenchProvider.getText(((ModelWorkspaceItem)node).getResource());
        } 
        return "UNKNOWN"; //$NON-NLS-1$
    } 
       
}
