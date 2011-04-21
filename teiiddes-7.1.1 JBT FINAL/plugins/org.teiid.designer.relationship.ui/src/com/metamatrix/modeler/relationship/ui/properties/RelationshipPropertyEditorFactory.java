/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.relationship.ui.properties;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.ui.celleditor.ExtendedDialogCellEditor;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import com.metamatrix.metamodels.relationship.Relationship;
import com.metamatrix.metamodels.relationship.RelationshipPackage;
import com.metamatrix.metamodels.relationship.RelationshipRole;
import com.metamatrix.metamodels.relationship.RelationshipType;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectPathLabelProvider;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelWorkspaceDialog;
import com.metamatrix.modeler.internal.ui.viewsupport.WorkspaceTreeAccumulatorSource;
import com.metamatrix.modeler.relationship.ui.UiConstants;
import com.metamatrix.modeler.ui.properties.IPropertyEditorFactory;
import com.metamatrix.ui.internal.viewsupport.StatusInfo;
import com.metamatrix.ui.internal.widget.accumulator.AccumulatorDialog;

/**
 * RelationshipPropertyEditorFactory is the propertyEditorFactory extension for the Relationship
 * metamodel.
 */
public class RelationshipPropertyEditorFactory implements IPropertyEditorFactory {

    /**
     * Construct an instance of RelationshipPropertyFactory.
     */
    public RelationshipPropertyEditorFactory() {
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ui.properties.IPropertyEditorFactory#supportsStructuralFeature(org.eclipse.emf.ecore.EStructuralFeature)
     */
    public boolean supportsStructuralFeature(EStructuralFeature feature) {
        if (Relationship.class == feature.getContainerClass()) {
            int id = feature.getFeatureID();
            if (id == RelationshipPackage.RELATIONSHIP__TARGETS) {
                return true;
            } else if (id == RelationshipPackage.RELATIONSHIP__SOURCES) {
                return true;
            } else if (id == RelationshipPackage.RELATIONSHIP__TYPE) {
                return true;
            }
        } else if (RelationshipType.class == feature.getContainerClass()) {
            int id = feature.getFeatureID();
            if (id == RelationshipPackage.RELATIONSHIP_TYPE__SUB_TYPE) {
                return true;
            } else if (id == RelationshipPackage.RELATIONSHIP_TYPE__SUPER_TYPE) {
                return true;
            }
        }
        return false;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ui.properties.IPropertyEditorFactory#createPropertyEditor(org.eclipse.swt.widgets.Composite, org.eclipse.emf.edit.provider.IItemPropertyDescriptor, org.eclipse.ui.views.properties.IPropertyDescriptor, java.lang.Object)
     */
    public CellEditor createPropertyEditor(
        Composite composite,
        IItemPropertyDescriptor itemPropertyDescriptor,
        IPropertyDescriptor propertyDescriptor,
        Object object) {
            
        if ( object instanceof Relationship ) {
            Relationship relationship = (Relationship) object;
            EStructuralFeature feature = (EStructuralFeature)itemPropertyDescriptor.getFeature(object);
            
            // see if we have the type feature
            if ( feature.getFeatureID() == RelationshipPackage.RELATIONSHIP__TYPE) {
                return createTypeEditor(
                    composite,
                    propertyDescriptor,
                    feature,
                    relationship,
                    null);
            }
            
            // see if we have the targets or sources feature
            RelationshipRole role = null;
            if ( feature.getFeatureID() == RelationshipPackage.RELATIONSHIP__TARGETS) {
                role = relationship.getTargetRole();
            } else if ( feature.getFeatureID() == RelationshipPackage.RELATIONSHIP__SOURCES ) {
                role = relationship.getSourceRole();
            }
 
            if ( role != null ) {
                if (role.getUpperBound() == 1) {
                    // create an editor that selects a single EObject
                    return createParticipantSelectionEditor(
                        composite, 
                        propertyDescriptor, 
                        relationship, 
                        role);
                }
                // create an editor with an EObject accumulator
                return createParticipantAccumulatorEditor(
                    composite,
                    propertyDescriptor,
                    feature,
                    relationship,
                    role);
            }             
        } else if ( object instanceof RelationshipType ) {
            RelationshipType type = (RelationshipType) object;
            EStructuralFeature feature = (EStructuralFeature)itemPropertyDescriptor.getFeature(object);

            if ( feature.getFeatureID() == RelationshipPackage.RELATIONSHIP_TYPE__SUPER_TYPE) {
                return createSuperTypeEditor(
                    composite,
                    propertyDescriptor,
                    type,
                    type);
            } 
            
            if ( feature.getFeatureID() == RelationshipPackage.RELATIONSHIP_TYPE__SUB_TYPE) {
                return createSubTypeAccumulator(
                    composite,
                    propertyDescriptor,
                    type);
            }
                
        }

        return null;
    }

    // ===============================================
    // Private CellEditor Factory Methods

    private CellEditor createTypeEditor(
        final Composite composite,
        final IPropertyDescriptor propertyDescriptor,
        final EStructuralFeature feature,
        final Relationship relationship,
        final RelationshipType illegalTypeInstance) {

        return new ExtendedDialogCellEditor(composite, propertyDescriptor.getLabelProvider()) {
            @Override
            protected Object openDialogBox(Control cellEditorWindow) {
                SelectionDialog selectionDialog =
                    createRelationshipTypeSelector(cellEditorWindow.getShell(), relationship);

                Object originalValue = getValue();
                Object[] selection = new Object[] { originalValue };
                selection[0] = getValue();
                selectionDialog.setInitialSelections(selection);

                selectionDialog.open();
                if ( selectionDialog.getReturnCode() == Window.OK ) {
                    if (selectionDialog.getResult().length != 0) {
                        return selectionDialog.getResult()[0];
                    }
                } 
                return originalValue;
            }
        };
    }

    private CellEditor createSuperTypeEditor(
        final Composite composite,
        final IPropertyDescriptor propertyDescriptor,
        final RelationshipType relationshipType,
        final RelationshipType illegalTypeInstance) {

        return new ExtendedDialogCellEditor(composite, propertyDescriptor.getLabelProvider()) {
            @Override
            protected Object openDialogBox(Control cellEditorWindow) {
                SelectionDialog selectionDialog =
                createRelationshipTypeSupertypeSelector(cellEditorWindow.getShell(), relationshipType, illegalTypeInstance);

                Object originalValue = getValue();
                Object[] selection = new Object[] { originalValue };
                selection[0] = getValue();
                selectionDialog.setInitialSelections(selection);

                selectionDialog.open();
                if ( selectionDialog.getReturnCode() == Window.OK ) {
                    if (selectionDialog.getResult().length != 0) {
                        return selectionDialog.getResult()[0];
                    }
                } 
                return originalValue;
            }
        };
    }
    
    private CellEditor createSubTypeAccumulator(
        final Composite composite,
        final IPropertyDescriptor propertyDescriptor,
        final RelationshipType relationshipType) {

        return new ExtendedDialogCellEditor(composite, propertyDescriptor.getLabelProvider()) {
            @Override
            protected Object openDialogBox(Control cellEditorWindow) {
                AccumulatorDialog accumulatorDialog =
                    createRelationshipTypeAccumulator(cellEditorWindow.getShell(), relationshipType);
                accumulatorDialog.open();
                if (accumulatorDialog.getSelectedItems() != null) {
                    return new BasicEList(accumulatorDialog.getSelectedItems());
                }
                return new BasicEList();
            }
        };
    }

    private CellEditor createParticipantSelectionEditor(
        final Composite composite,
        final IPropertyDescriptor propertyDescriptor,
        final Relationship relationship,
        final RelationshipRole role) {

        return new ExtendedDialogCellEditor(composite, propertyDescriptor.getLabelProvider()) {
            @Override
            protected Object openDialogBox(Control cellEditorWindow) {
                SelectionDialog selectionDialog =
                    createRoleParticipantSelector(cellEditorWindow.getShell(), relationship, role);

                Object originalValue = getValue();
                Object[] selection = new Object[] { originalValue };
                selection[0] = getValue();
                selectionDialog.setInitialSelections(selection);

                selectionDialog.open();
                if ( selectionDialog.getReturnCode() == Window.OK ) {
                    if (selectionDialog.getResult().length != 0) {
                        return selectionDialog.getResult()[0];
                    }
                } 
                return originalValue;
            }
        };
    }

    private CellEditor createParticipantAccumulatorEditor(
        final Composite composite,
        final IPropertyDescriptor propertyDescriptor,
        final EStructuralFeature feature,
        final Relationship relationship,
        final RelationshipRole role) {

        return new ExtendedDialogCellEditor(composite, propertyDescriptor.getLabelProvider()) {
            @Override
            protected Object openDialogBox(Control cellEditorWindow) {
                Object originalValue = getValue();
                Collection initialVals = Collections.EMPTY_LIST;
                if(originalValue instanceof Collection) {
                    initialVals = (Collection)originalValue;
                } else {
                    initialVals = new ArrayList(1);
                    initialVals.add(originalValue);
                }
                
                WorkspaceTreeAccumulatorSource accumulatorSource =
                    new WorkspaceTreeAccumulatorSource(initialVals);
                accumulatorSource.setSelectionValidator(new RelationshipRoleAccumulatorSelectionValidator(accumulatorSource, role));
                String title = UiConstants.Util.getString("RelationshipPropertyEditorFactory.roleAccumulatorTitle", role.getName()); //$NON-NLS-1$
                String availableLabel = UiConstants.Util.getString("RelationshipPropertyEditorFactory.roleAccumulatorChoices"); //$NON-NLS-1$
                String selectedLabel = UiConstants.Util.getString("RelationshipPropertyEditorFactory.roleAccumulatorSelection", role.getName()); //$NON-NLS-1$

                AccumulatorDialog accumulatorDialog = new AccumulatorDialog(
                    cellEditorWindow.getShell(),
                    accumulatorSource,
                    title,
                    initialVals,
                    new ModelObjectPathLabelProvider(),
                    availableLabel,
                    selectedLabel);
                accumulatorDialog.open();
                if (accumulatorDialog.getSelectedItems() != null) {
                    return new BasicEList(accumulatorDialog.getSelectedItems());
                }
                return new BasicEList();
            }
        };
    }

    // ================================================
    // Public Static Dialog Factory Methods

    /**
     * Create a SelectionDialog for modifiying the participant of the specified
     * Relationship and RelationshipRole.  This method should be used when the RelationshipRole's
     * upperLimit is exactly 1.
     * @param shell
     * @param relationship
     * @param role
     * @return
     */
    public static SelectionDialog createRoleParticipantSelector(
        final Shell shell,
        final Relationship relationship,
        final RelationshipRole role) {

        ModelWorkspaceDialog result = new ModelWorkspaceDialog(shell);
        result.setValidator(new RelationshipRoleSelectionValidator(role));

        String title = UiConstants.Util.getString("RelationshipPropertyEditorFactory.selectorTitle"); //$NON-NLS-1$
        String message = UiConstants.Util.getString("RelationshipPropertyEditorFactory.selectorMessage"); //$NON-NLS-1$
        result.setTitle(title);
        result.setMessage(message);
        result.setAllowMultiple(false);
        
        if ( role.isTargetRole() ) {
            result.setInitialElementSelections(relationship.getTargets());
        } else {
            result.setInitialElementSelections(relationship.getSources());
        }
        return result;
    }
    
    /**
     * Create a <code>SelectionDialog</code> for selecting a RelationshipType.
     * @param theShell the dialog window
     * @param theType an optional <code>RelationshipType</code> to display initial selection.  May be <code>null</code>.
     * @return a relationship type selection dialog
     */
    public static SelectionDialog createRelationshipTypeSelector(final Shell theShell,
                                                                 final RelationshipType theType) {
        RelationshipTypeProvider provider = new RelationshipTypeProvider();
        ModelWorkspaceDialog result = new ModelWorkspaceDialog(theShell, null, provider.getLabelProvider(), provider);

        String title = UiConstants.Util.getString("RelationshipPropertyEditorFactory.typeSelectorTitle"); //$NON-NLS-1$
        String message = UiConstants.Util.getString("RelationshipPropertyEditorFactory.typeSelectorMessage"); //$NON-NLS-1$
        result.setTitle(title);
        result.setMessage(message);
        result.setAllowMultiple(false);
    
        result.setInput(ResourcesPlugin.getWorkspace().getRoot());
        result.addFilter(new RelationshipTypeViewerFilter());

        result.setValidator(new ISelectionStatusValidator() {
            public IStatus validate(Object[] selection) {
                if ( selection == null 
                    || selection.length == 0 
                    || selection[0] == null 
                    || !(selection[0] instanceof RelationshipType) ) { 
                    String msg = UiConstants.Util.getString("RelationshipPropertyEditorFactory.typeSelectorError"); //$NON-NLS-1$
                    return new StatusInfo(UiConstants.PLUGIN_ID, IStatus.ERROR, msg);
                } 
                return new StatusInfo(UiConstants.PLUGIN_ID);
            }
        });

        if (theType != null) {
            result.setInitialSelection(theType);
        }

        return result;
    }
    
    /**
     * Create a SelectionDialog for selecting a RelationshipType
     * @param shell
     * @param relationship an optional Relationship to display initial selection.  May be null.
     * @return
     */
    public static SelectionDialog createRelationshipTypeSelector(
        final Shell shell,
        final Relationship relationship) {

        RelationshipType type = (relationship == null) ? null : relationship.getType();
        return createRelationshipTypeSelector(shell, type);
    }

    /**
     * Create a SelectionDialog for selecting a RelationshipType
     * @param shell
     * @param initialType an optional initial RelationshipType to display selected.  May be null.
     * @param type the RelationshipType instance that is being edited.
     * @return
     */
    public static SelectionDialog createRelationshipTypeSupertypeSelector(
        final Shell shell,
        final RelationshipType initialType,
        final RelationshipType type) {

        RelationshipTypeProvider provider = new RelationshipTypeProvider();
        ModelWorkspaceDialog result = new ModelWorkspaceDialog(shell, null, provider.getLabelProvider(), provider);
        result.setValidator(new RelationshipTypeSelectionValidator(type, true, false));
        result.addFilter(new RelationshipTypeViewerFilter());

        String title = UiConstants.Util.getString("RelationshipPropertyEditorFactory.typeSelectorTitle"); //$NON-NLS-1$
        String message = UiConstants.Util.getString("RelationshipPropertyEditorFactory.typeSelectorMessage"); //$NON-NLS-1$
        result.setTitle(title);
        result.setMessage(message);
        result.setAllowMultiple(false);
    
        result.setInput(ResourcesPlugin.getWorkspace().getRoot());

        result.setValidator(new ISelectionStatusValidator() {
            public IStatus validate(Object[] selection) {
                if ( selection == null 
                    || selection.length == 0 
                    || selection[0] == null 
                    || !(selection[0] instanceof RelationshipType) ) { 
                    String msg = UiConstants.Util.getString("RelationshipPropertyEditorFactory.typeSelectorError"); //$NON-NLS-1$
                    return new StatusInfo(UiConstants.PLUGIN_ID, IStatus.ERROR, msg);
                } else if ( selection[0].equals(type) ) {
                    String msg = UiConstants.Util.getString("RelationshipPropertyEditorFactory.illegalTypeError"); //$NON-NLS-1$
                    return new StatusInfo(UiConstants.PLUGIN_ID, IStatus.ERROR, msg);
                }
                return new StatusInfo(UiConstants.PLUGIN_ID);
            }
        });

        if ( initialType != null ) {
            result.setInitialSelection(initialType);
        }
        return result;
    }

    /**
     * Create an AccumulatorDialog for modifiying the list of participants in the specified
     * Relationship and RelationshipRole.  This method should be used when the RelationshipRole's
     * upperLimit is -1 or greater than 1. 
     * @param shell
     * @param relationship
     * @param role
     * @return
     */
    public static AccumulatorDialog createRoleParticipantAccumulator(
        final Shell shell,
        final Relationship relationship,
        final RelationshipRole role) {

        List initialSelectionList;
        if ( role.isSourceRole() ) {
            initialSelectionList = relationship.getSources();
        } else {
            initialSelectionList = relationship.getTargets();
        }
        
        WorkspaceTreeAccumulatorSource accumulatorSource =
            new WorkspaceTreeAccumulatorSource(initialSelectionList);
        accumulatorSource.setSelectionValidator(new RelationshipRoleAccumulatorSelectionValidator(accumulatorSource, role));
        String title = UiConstants.Util.getString("RelationshipPropertyEditorFactory.roleAccumulatorTitle", role.getName()); //$NON-NLS-1$
        String availableLabel = UiConstants.Util.getString("RelationshipPropertyEditorFactory.roleAccumulatorChoices"); //$NON-NLS-1$
        String selectedLabel = UiConstants.Util.getString("RelationshipPropertyEditorFactory.roleAccumulatorSelection", role.getName()); //$NON-NLS-1$

        return new AccumulatorDialog(
            shell,
            accumulatorSource,
            title,
            initialSelectionList,
            new ModelObjectPathLabelProvider(),
            availableLabel,
            selectedLabel);
    }


    /**
     * Create an AccumulatorDialog for modifiying the list of Subtypes for the specified
     * RelationshipType. 
     * @param shell
     * @param type
     * @return
     */
    public static AccumulatorDialog createRelationshipTypeAccumulator(
        final Shell shell,
        final RelationshipType type) {

        List initialSelectionList = type.getSubType();
        
        RelationshipTypeProvider provider = new RelationshipTypeProvider();
        WorkspaceTreeAccumulatorSource accumulatorSource =
            new WorkspaceTreeAccumulatorSource(initialSelectionList, provider.getLabelProvider(), provider);
        accumulatorSource.setSelectionValidator(new RelationshipTypeSelectionValidator(type, false, true));
        accumulatorSource.setViewerFilter(new RelationshipTypeViewerFilter());
        String title = UiConstants.Util.getString("RelationshipPropertyEditorFactory.subtypeAccumulatorTitle", type.getName()); //$NON-NLS-1$
        String availableLabel = UiConstants.Util.getString("RelationshipPropertyEditorFactory.subtypeAccumulatorChoices"); //$NON-NLS-1$
        String selectedLabel = UiConstants.Util.getString("RelationshipPropertyEditorFactory.subtypeAccumulatorSelection", type.getName()); //$NON-NLS-1$

        return new AccumulatorDialog(
            shell,
            accumulatorSource,
            title,
            initialSelectionList,
            new ModelObjectPathLabelProvider(),
            availableLabel,
            selectedLabel);
    }
    
}
