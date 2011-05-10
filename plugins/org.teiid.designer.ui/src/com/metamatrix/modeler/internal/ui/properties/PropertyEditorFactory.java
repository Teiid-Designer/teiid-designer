/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.properties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.ui.celleditor.ExtendedComboBoxCellEditor;
import org.eclipse.emf.common.ui.celleditor.ExtendedDialogCellEditor;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.ui.EMFEditUIPlugin;
import org.eclipse.emf.edit.ui.provider.PropertyDescriptor.EDataTypeCellEditor;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.xsd.XSDComponent;
import org.eclipse.xsd.XSDFeature;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTypeDefinition;

import com.metamatrix.metamodels.core.extension.XClass;
import com.metamatrix.modeler.core.ModelEditor;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.internal.ui.viewsupport.DatatypeSelectionDialog;
import com.metamatrix.modeler.internal.ui.viewsupport.IFilter;
import com.metamatrix.modeler.internal.ui.viewsupport.MetamodelTreeViewer;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectListDialog;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectPathLabelProvider;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.properties.IPropertyEditorFactory;
import com.metamatrix.ui.internal.viewsupport.StatusInfo;
import com.metamatrix.ui.internal.widget.accumulator.AccumulatorDialog;
import com.metamatrix.ui.internal.widget.accumulator.IAccumulatorSource;

/**
 * PropertyEditorFactory is a static class for generating CellEditors for a given model object and IPropertyDescriptor.
 */
public abstract class PropertyEditorFactory implements UiConstants.ExtensionPoints.PropertyEditorFactoryExtension {

    private static final int COMBO_BOX_CHOICE_LIMIT = 10;
    static final ILabelProvider pathLabelProvider = new ModelObjectPathLabelProvider();
    private static final ModelEditor me = ModelerCore.getModelEditor();

    private static final ArrayList customPropertyFactories = new ArrayList();

    /**
     * Load all the contributions to the PropertyEditorFactory extension point.
     */
    static {
        // get the PropertyEditorFactory extension point from the plugin class
        IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(UiConstants.PLUGIN_ID, ID);

        // get the all extensions to the ModelObjectActionContributor extension point
        IExtension[] extensions = extensionPoint.getExtensions();

        if (extensions.length > 0) {

            // for each extension get their contributor
            for (int i = 0; i < extensions.length; i++) {
                IConfigurationElement[] elements = extensions[i].getConfigurationElements();
                Object extension = null;

                for (int j = 0; j < elements.length; j++) {
                    try {
                        extension = elements[j].createExecutableExtension(CLASSNAME);

                        if (extension instanceof IPropertyEditorFactory) {
                            customPropertyFactories.add(extension);
                        } else {
                            UiConstants.Util.log(IStatus.ERROR,
                                                 UiConstants.Util.getString("PropertyEditorFactory.wrongContributorClass", //$NON-NLS-1$
                                                                            new Object[] {extension.getClass().getName()}));
                        }
                    } catch (Exception theException) {
                        UiConstants.Util.log(IStatus.ERROR,
                                             theException,
                                             UiConstants.Util.getString("PropertyEditorFactory.contributorProblem", //$NON-NLS-1$
                                                                        new Object[] {elements[j].getAttribute(CLASSNAME)}));
                    }
                }
            }
        }
    }

    public static CellEditor createPropertyEditor( final Composite composite,
                                                   final IItemPropertyDescriptor itemPropertyDescriptor,
                                                   final IPropertyDescriptor propertyDescriptor,
                                                   final Object object ) {

        return createPropertyEditor(composite, itemPropertyDescriptor, propertyDescriptor, object, false);
    }

    public static CellEditor createPropertyEditor( final Composite composite,
                                                   final IItemPropertyDescriptor itemPropertyDescriptor,
                                                   final IPropertyDescriptor propertyDescriptor,
                                                   final Object object,
                                                   final boolean lazyLoadChoices ) {

        if (!itemPropertyDescriptor.canSetProperty(object)) {
            return null;
        }

        CellEditor result = null;

        Object genericFeature = itemPropertyDescriptor.getFeature(object);
        if (genericFeature instanceof EReference[]) {
            result = createComboEditor(composite, itemPropertyDescriptor, propertyDescriptor, object);

        } else if (genericFeature instanceof EStructuralFeature) {
            final EStructuralFeature feature = (EStructuralFeature)genericFeature;

            // see if there is a custom editor factory to handle this feature
            if (!customPropertyFactories.isEmpty()) {
                for (Iterator iter = customPropertyFactories.iterator(); iter.hasNext();) {
                    IPropertyEditorFactory factory = (IPropertyEditorFactory)iter.next();
                    try {
                        if (factory.supportsStructuralFeature(feature)) {
                            return factory.createPropertyEditor(composite, itemPropertyDescriptor, propertyDescriptor, object);
                        }
                    } catch (Exception e) {
                        UiConstants.Util.log(IStatus.ERROR,
                                             UiConstants.Util.getString("PropertyEditorFactory.errorInFactory", //$NON-NLS-1$
                                                                        new Object[] {factory.getClass().getName()}));
                    }
                }
            }

            final EClassifier eType = feature.getEType();
            final String eTypeInstanceClassName = eType.getInstanceClassName();
            EObject target = null;
            if (propertyDescriptor instanceof ModelObjectPropertyDescriptor) {
                target = (EObject)((ModelObjectPropertyDescriptor)propertyDescriptor).getObject();
            }

            if (me.isDatatypeFeature(target, feature)
                || (object instanceof XSDSimpleTypeDefinition && eTypeInstanceClassName.equals(EObject.class.getName()))) {

                result = createDatatypeEditor(composite, itemPropertyDescriptor, propertyDescriptor, object);
            } else if (object instanceof XClass && eTypeInstanceClassName.equals(EClass.class.getName())) {
                // The reference's type is a metaclass in a metamodel
                result = createMetaclassEditor(composite, itemPropertyDescriptor, propertyDescriptor, object);

            } else {

                // check for EEnum first, since several XSD enumerations are coded non-standard
                if (eType instanceof EEnum) {
                    if (feature.isMany() && object instanceof EObject) {
                        // get the choice of values and see if they are EEnumLiteral instances
                        Iterator iter = itemPropertyDescriptor.getChoiceOfValues(object).iterator();
                        boolean containsLiterals = false;
                        while (iter.hasNext()) {
                            if (iter.next() instanceof EEnumLiteral) {
                                containsLiterals = true;
                                break;
                            }
                        }

                        if (containsLiterals) {
                            // this is standard; use the accumulator
                            result = createAccumulatorEnumEditor(composite,
                                                                 propertyDescriptor,
                                                                 feature,
                                                                 itemPropertyDescriptor,
                                                                 object);
                        } else {
                            // this is a non-standard; use the combo box editor
                            result = createComboEditor(composite, itemPropertyDescriptor, propertyDescriptor, object);
                        }
                    } else {
                        result = createComboEditor(composite, itemPropertyDescriptor, propertyDescriptor, object);
                    }

                } else if (eType instanceof EDataType) {
                    EDataType eDataType = (EDataType)eType;

                    if (eDataType.isSerializable()) {
                        if (feature.isMany() && object instanceof EObject) {
                            if ((feature instanceof EAttribute)) {
                                Collection choices = itemPropertyDescriptor.getChoiceOfValues(object);
                                if (choices == null || choices.isEmpty()) {
                                    // Defect 15449 - we have no editor for multiple string values, so use EMFs.
                                    result = ((ModelObjectPropertyDescriptor)propertyDescriptor).createDelegatePropertyEditor(composite);
                                } else {
                                    // Defect 15449 - This has only been tested for a case where there were no allowable values.
                                    // Randall says we have
                                    // no properties that will travel down this block of code with getChoiceOfValues( ) returning
                                    // values.
                                    // Should that ever occur (say, in EMF 2.0), this block of code will need to be re-tested.
                                    result = createAccumulatorEnumEditor(composite,
                                                                         propertyDescriptor,
                                                                         feature,
                                                                         itemPropertyDescriptor,
                                                                         object);
                                }
                            } else {
                                result = createAccumulatorEnumEditor(composite,
                                                                     propertyDescriptor,
                                                                     feature,
                                                                     itemPropertyDescriptor,
                                                                     object);
                            }
                        } else if (eDataType == EcorePackage.eINSTANCE.getEBoolean()
                                   || eDataType == EcorePackage.eINSTANCE.getEBooleanObject()) {
                            result = createBooleanEditor(composite, propertyDescriptor);
                        } else {
                            final Collection choiceOfValues = itemPropertyDescriptor.getChoiceOfValues(object);
                            // property is single-valued.
                            if (choiceOfValues != null) {
                                if (choiceOfValues.size() < COMBO_BOX_CHOICE_LIMIT) {
                                    result = createComboEditor(composite, itemPropertyDescriptor, propertyDescriptor, object);
                                } else {
                                    result = createListEditor(composite,
                                                              propertyDescriptor,
                                                              feature,
                                                              itemPropertyDescriptor,
                                                              object,
                                                              choiceOfValues);
                                }
                            } else {
                                result = new EDataTypeCellEditor(eDataType, composite);
                            }
                        }
                    }

                } else {

                    if (object instanceof EObject) {
                        if (feature.isMany()) {
                            // property is multi-valued.
                            boolean valid = true;

                            if (valid) {
                                // create a cell editor that launches the accumulator
                                result = createAccumulatorEditor(composite,
                                                                 propertyDescriptor,
                                                                 feature,
                                                                 itemPropertyDescriptor,
                                                                 object);

                            }
                        } else {

                            boolean useComboBox = false;
                            Collection choiceOfValues = null;
                            if (!lazyLoadChoices) {
                                choiceOfValues = itemPropertyDescriptor.getChoiceOfValues(object);
                                // property is single-valued.
                                if (choiceOfValues.size() < COMBO_BOX_CHOICE_LIMIT) {
                                    useComboBox = true;
                                }
                            }

                            // property is single-valued.
                            if (useComboBox) {
                                result = createComboEditor(composite, itemPropertyDescriptor, propertyDescriptor, object);
                            } else {
                                result = createListEditor(composite,
                                                          propertyDescriptor,
                                                          feature,
                                                          itemPropertyDescriptor,
                                                          object,
                                                          choiceOfValues);

                            }
                        }

                    }
                    if (result == null) {
                        result = createComboEditor(composite, itemPropertyDescriptor, propertyDescriptor, object);
                    }
                }
            }
        }

        return result;
    }

    // ======================================
    // Cell Editor factory methods

    private static CellEditor createComboEditor( final Composite composite,
                                                 final IItemPropertyDescriptor itemPropertyDescriptor,
                                                 final IPropertyDescriptor propertyDescriptor,
                                                 final Object object ) {

        return new ExtendedComboBoxCellEditor(composite, new ArrayList(itemPropertyDescriptor.getChoiceOfValues(object)),
                                              propertyDescriptor.getLabelProvider(), true);
    }

    private static CellEditor createBooleanEditor( final Composite composite,
                                                   final IPropertyDescriptor propertyDescriptor ) {

        return new ExtendedComboBoxCellEditor(composite, Arrays.asList(new Object[] {new Boolean(false), new Boolean(true)}),
                                              propertyDescriptor.getLabelProvider(), true);
    }

    private static CellEditor createDatatypeEditor( final Composite composite,
                                                    final IItemPropertyDescriptor itemPropertyDescriptor,
                                                    final IPropertyDescriptor propertyDescriptor,
                                                    final Object object ) {

        final Object feature = itemPropertyDescriptor.getFeature(object);
        if (feature instanceof EStructuralFeature) {
            return new ExtendedDialogCellEditor(composite, propertyDescriptor.getLabelProvider()) {
                @Override
                protected Object openDialogBox( Control cellEditorWindow ) {
                    DatatypeSelectionDialog dialog = new DatatypeSelectionDialog(composite.getShell(), (EObject)object,
                                                                                 (EStructuralFeature)feature);

                    Object originalValue = getValue();
                    Object[] selection = new Object[] {originalValue};
                    selection[0] = getValue();
                    dialog.setInitialSelections(selection);

                    int status = dialog.open();
                    if (status == Window.OK) {
                        Object[] result = dialog.getResult();
                        if (result.length == 0) {
                            // null out the value
                            return null;
                        }
                        // return the selected value
                        return result[0];
                    }
                    // return the original object
                    return originalValue;
                }
            };

        }

        return new ExtendedDialogCellEditor(composite, propertyDescriptor.getLabelProvider()) {
            @Override
            protected Object openDialogBox( Control cellEditorWindow ) {
                DatatypeSelectionDialog dialog = new DatatypeSelectionDialog(composite.getShell(), (EObject)object);
                Object originalValue = getValue();
                Object[] selection = new Object[] {originalValue};
                selection[0] = getValue();
                dialog.setInitialSelections(selection);

                int status = dialog.open();
                if (status == Window.OK) {
                    Object[] result = dialog.getResult();
                    if (result.length == 0) {
                        // null out the value
                        return null;
                    }
                    // return the selected value
                    return result[0];
                }
                // return the original object
                return originalValue;
            }
        };
    }

    private static CellEditor createMetaclassEditor( final Composite composite,
                                                     final IItemPropertyDescriptor itemPropertyDescriptor,
                                                     final IPropertyDescriptor propertyDescriptor,
                                                     final Object object ) {

        return new ExtendedDialogCellEditor(composite, propertyDescriptor.getLabelProvider()) {
            @Override
            protected Object openDialogBox( Control cellEditorWindow ) {
                SelectionDialog dialog = MetamodelTreeViewer.createSelectionDialog(composite.getShell(), true);
                Object originalValue = getValue();
                Object[] selection = new Object[] {originalValue};
                selection[0] = getValue();
                dialog.setInitialSelections(selection);

                int status = dialog.open();
                if (status == Window.OK) {
                    Object[] result = dialog.getResult();
                    if (result.length == 0) {
                        // null out the value
                        return null;
                    }
                    // return the selected value
                    return result[0];
                }
                // return the original object
                return originalValue;
            }
        };
    }

    private static CellEditor createAccumulatorEditor( final Composite composite,
                                                       final IPropertyDescriptor propertyDescriptor,
                                                       final EStructuralFeature feature,
                                                       final IItemPropertyDescriptor itemPropertyDescriptor,
                                                       final Object object ) {

        return new ExtendedDialogCellEditor(composite, propertyDescriptor.getLabelProvider()) {
            @Override
            protected Object openDialogBox( Control cellEditorWindow ) {
                EObject eObject = (EObject)object;
                List initialSelectionList = (List)eObject.eGet(feature);
                List initialSelectionListLocal = copyList(initialSelectionList);
                List initialAvailableList = new ArrayList(itemPropertyDescriptor.getChoiceOfValues(object));
                removeSelectedItemsFromAvailable(initialAvailableList, initialSelectionListLocal);
                IAccumulatorSource accumulatorSource = new ModelObjectAccumulatorSourceImpl(pathLabelProvider,
                                                                                            initialAvailableList);
                String availableLabel = EMFEditUIPlugin.INSTANCE.getString("_UI_Choices_label"); //$NON-NLS-1$
                String selectedLabel = EMFEditUIPlugin.INSTANCE.getString("_UI_Feature_label"); //$NON-NLS-1$
                AccumulatorDialog accumulatorDialog = new AccumulatorDialog(cellEditorWindow.getShell(), accumulatorSource,
                                                                            propertyDescriptor.getDisplayName(),
                                                                            initialSelectionListLocal, pathLabelProvider,
                                                                            availableLabel, selectedLabel);
                int status = accumulatorDialog.open();
                List result = initialSelectionList;
                if (status == Window.OK) {
                    result = new BasicEList(accumulatorDialog.getSelectedItems());
                }
                return result;
            }
        };
    }

    private static CellEditor createAccumulatorEnumEditor( final Composite composite,
                                                           final IPropertyDescriptor propertyDescriptor,
                                                           final EStructuralFeature feature,
                                                           final IItemPropertyDescriptor itemPropertyDescriptor,
                                                           final Object object ) {

        return new ExtendedDialogCellEditor(composite, propertyDescriptor.getLabelProvider()) {
            @Override
            protected Object openDialogBox( Control cellEditorWindow ) {
                EObject eObject = (EObject)object;
                List initialSelectionList = (List)eObject.eGet(feature);
                List initialSelectionListLocal = copyList(initialSelectionList);
                Collection choices = itemPropertyDescriptor.getChoiceOfValues(object);
                List choiceList = new ArrayList(choices);
                List initialAvailableList = new ArrayList(choiceList);
                removeSelectedItemsFromAvailable(initialAvailableList, initialSelectionListLocal);
                IAccumulatorSource accumulatorSource = new ModelObjectAccumulatorSourceImpl(
                                                                                            propertyDescriptor.getLabelProvider(),
                                                                                            initialAvailableList);
                String availableLabel = EMFEditUIPlugin.INSTANCE.getString("_UI_Choices_label"); //$NON-NLS-1$
                String selectedLabel = EMFEditUIPlugin.INSTANCE.getString("_UI_Feature_label"); //$NON-NLS-1$
                AccumulatorDialog accumulatorDialog = new AccumulatorDialog(cellEditorWindow.getShell(), accumulatorSource,
                                                                            propertyDescriptor.getDisplayName(),
                                                                            initialSelectionListLocal,
                                                                            propertyDescriptor.getLabelProvider(),
                                                                            availableLabel, selectedLabel);
                int status = accumulatorDialog.open();
                if (status != Window.OK) {
                    return null;
                }

                // EMF expects enumerations to be provided as Integer indexes to their values, so convert
                Collection selectedItems = accumulatorDialog.getSelectedItems();
                List result = new ArrayList(selectedItems.size());
                for (Iterator iter = selectedItems.iterator(); iter.hasNext();) {
                    int index = choiceList.indexOf(iter.next());
                    if (index >= 0) {
                        result.add(new Integer(index));
                    }
                }
                return new BasicEList(result);
            }
        };
    }

    private static CellEditor createListEditor( final Composite composite,
                                                final IPropertyDescriptor propertyDescriptor,
                                                final EStructuralFeature feature,
                                                final IItemPropertyDescriptor itemPropertyDescriptor,
                                                final Object object,
                                                final Collection choiceOfValues ) {

        return new ExtendedDialogCellEditor(composite, propertyDescriptor.getLabelProvider()) {
            @Override
            protected Object openDialogBox( Control cellEditorWindow ) {
                // defect 18611 - make MOLD open faster.
                ModelObjectListDialog dialog = new ModelObjectListDialog(composite.getShell(),
                                                                         propertyDescriptor.getLabelProvider(), true, false);
                dialog.setContentFilter(new IFilter() {

                    public boolean passes( final Object object ) {
                        if (object instanceof XSDComponent) {
                            if (object instanceof XSDTypeDefinition || object instanceof XSDFeature) {
                                return true;
                            }
                            return false;
                        }
                        return true;
                    }
                });

                Collection inputValues = (choiceOfValues != null ? choiceOfValues : itemPropertyDescriptor.getChoiceOfValues(object));

                // Filter out the null value - not allowable in a list view
                if (inputValues.contains(null)) {
                    // need to create a new list as the original maybe unmodifiable and calling remove will throw exception
                    inputValues = new ArrayList(inputValues);
                    inputValues.remove(null);
                }

                dialog.setInput(inputValues);

                Object originalValue = getValue();
                Object[] selection = new Object[] {originalValue};
                dialog.setInitialSelections(selection);

                dialog.setFeatureName(propertyDescriptor.getDisplayName());

                int status = dialog.open();
                if (status == Window.OK) {
                    Object[] result = dialog.getResult();

                    // If an empty array is returned from the dialog that means the user has requested to
                    // null the value out. The superclass editor ignores null values so added the code here.
                    if (result.length == 0) {
                        // null out the value if valid value
                        if (isCorrect(null)) {
                            markDirty();
                            doSetValue(null);
                            fireApplyEditorValue();
                        }

                        return null;
                    }

                    // return the selected value
                    return result[0];
                }
                // return the original object
                return originalValue;
            }
        };
    }

    static List copyList( List inList ) {
        List outList = new ArrayList(inList.size());
        Iterator it = inList.iterator();
        while (it.hasNext()) {
            outList.add(it.next());
        }
        return outList;
    }

    static void removeSelectedItemsFromAvailable( List available,
                                                  List selected ) {
        Iterator it = selected.iterator();
        while (it.hasNext()) {
            Object item = it.next();
            if (available.contains(item)) {
                available.remove(item);
            }
        }
    }

    /**
     * Construct an instance of PropertyEditorManager.
     */
    private PropertyEditorFactory() {
    }

}

class ModelObjectAccumulatorSourceImpl implements IAccumulatorSource {

    // We will be using a Table to show the data. However, we will NOT be using a TableViewer with
    // the table. The reason is this: Whenever an item is removed from this table (moved to the
    // "selected" side in the accumulator), then later reinserted into this side, we would like to be
    // able to reinsert it into its original position. That is not feasible with a TableViewer.
    // With a TableViewer one must either supply a sorter, which is not what we want, or always
    // insert the items at the end of the table, which is likewise not what we want.
    //
    // But, seeing as SWT provides no means to correlate an Object itself with a row in the
    // Table, we will keep an up-to-date list of Objects represented in the table (currentValues).
    // Table only provides means to get Strings and Images pertaining to rows in a table, but not
    // Objects that they are supposed to represent, which I find very strange. BWP.

    private static final IStatus OK_STATUS = new StatusInfo(UiConstants.PLUGIN_ID);

    private ILabelProvider labelProvider;
    private List initialValues;
    private List currentValues = new ArrayList();
    private Table table;

    public ModelObjectAccumulatorSourceImpl( ILabelProvider labelProvider,
                                             List initialValues ) {
        super();
        this.labelProvider = labelProvider;
        this.initialValues = initialValues;
    }

    public void accumulatedValuesRemoved( Collection values ) {
        // Any items that were originally in our table we will reinsert into the same relative
        // location. Any ones that did not start here we will insert at the end.
        Iterator it = values.iterator();
        while (it.hasNext()) {
            Object obj = it.next();
            int index = indexForValueInserting(obj);
            TableItem tableItem = new TableItem(table, 0, index);
            Image image = labelProvider.getImage(obj);
            String text = labelProvider.getText(obj);
            tableItem.setImage(image);
            tableItem.setText(text);
            currentValues.add(index, obj);
        }
    }

    public void accumulatedValuesAdded( Collection values ) {
        Iterator it = values.iterator();
        while (it.hasNext()) {
            Object value = it.next();
            int index = indexForValueRemoving(value);
            table.remove(index);
            currentValues.remove(index);
        }
    }

    public Collection getAvailableValues() {
        Collection itemsColl = new ArrayList(currentValues.size());
        Iterator it = currentValues.iterator();
        while (it.hasNext()) {
            itemsColl.add(it.next());
        }
        return itemsColl;
    }

    public int getAvailableValuesCount() {
        int count = table.getItemCount();
        return count;
    }

    public Collection getSelectedAvailableValues() {
        int[] itemIndices = table.getSelectionIndices();
        Collection itemsColl = new ArrayList(itemIndices.length);
        for (int i = 0; i < itemIndices.length; i++) {
            Object obj = currentValues.get(itemIndices[i]);
            itemsColl.add(obj);
        }
        return itemsColl;
    }

    public int getSelectedAvailableValuesCount() {
        int count = table.getSelectionCount();
        return count;
    }

    public Control createControl( Composite parent ) {
        // Create the table
        table = new Table(parent, SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
        GridData tableGridData = new GridData();
        tableGridData.widthHint = 200;
        tableGridData.verticalAlignment = GridData.FILL;
        tableGridData.horizontalAlignment = GridData.FILL;
        tableGridData.grabExcessHorizontalSpace = true;
        tableGridData.grabExcessVerticalSpace = true;
        table.setLayoutData(tableGridData);

        // Populate the table
        int loc = 0;
        Iterator it = this.initialValues.iterator();
        while (it.hasNext()) {
            Object obj = it.next();
            TableItem tableItem = new TableItem(table, 0, loc);
            Image image = labelProvider.getImage(obj);
            String text = labelProvider.getText(obj);
            tableItem.setImage(image);
            tableItem.setText(text);
            currentValues.add(obj);
            loc++;
        }

        return table;
    }

    public void addSelectionListener( SelectionListener listener ) {
        table.addSelectionListener(listener);
    }

    private int indexForValueRemoving( Object value ) {
        int index = currentValues.indexOf(value);
        return index;
    }

    private int indexForValueInserting( Object value ) {
        int index = -1;
        int originalIndex = initialValues.indexOf(value);
        if (originalIndex < 0) {
            index = currentValues.size();
        } else {
            // We will attempt to find in the current list the object that was just before this one
            // in the original list. If found, we know that this one should be inserted right after
            // it. If not found, we will look for the object that was right before that object, etc.
            // If no object that was before this one in the original list was found in the current
            // list, then we will return 0, to insert the object at the beginning of the table.
            boolean found = false;
            int loc = originalIndex - 1;
            while ((!found) && (loc >= 0)) {
                Object objectLookingFor = initialValues.get(loc);
                int curIndexOfObject = currentValues.indexOf(objectLookingFor);
                if (curIndexOfObject >= 0) {
                    found = true;
                    index = curIndexOfObject + 1;
                } else {
                    loc--;
                }
            }
            if (!found) {
                index = 0;
            }
        }
        return index;
    }

    /**
     * @see com.metamatrix.ui.internal.widget.accumulator.IAccumulatorSource#supportsAddAll()
     */
    public boolean supportsAddAll() {
        return true;
    }

    /**
     * @see com.metamatrix.ui.internal.widget.accumulator.IAccumulatorSource#getSelectionStatus()
     */
    public IStatus getSelectionStatus() {
        return OK_STATUS;
    }

} // end ModelObjectAccumulatorSourceImpl
