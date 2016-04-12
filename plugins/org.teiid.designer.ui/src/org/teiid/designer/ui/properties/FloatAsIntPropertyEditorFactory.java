package org.teiid.designer.ui.properties;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.ui.provider.PropertyDescriptor.EDataTypeCellEditor;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.teiid.designer.metamodels.relational.RelationalPackage;
import org.teiid.designer.ui.UiConstants;

/**
 * An {@link IPropertyEditorFactory} that converts numbers bigger than {@link Integer#MAX_VALUE} into a negative integer
 * for storage. And reverses the process for retrieval. The only negative number not converted is -1.
 */
public class FloatAsIntPropertyEditorFactory implements IPropertyEditorFactory {

    private static final Collection<EStructuralFeature> FLOAT_AS_INT_FEATURES =
        Collections.unmodifiableCollection(Arrays.asList(new EStructuralFeature[] {
            RelationalPackage.eINSTANCE.getTable_Cardinality(),
            RelationalPackage.eINSTANCE.getColumn_DistinctValueCount(),
            RelationalPackage.eINSTANCE.getColumn_NullValueCount()
    }));

    /**
     * @param feature the feature being checked (cannot be <code>null</code>)
     * @return <code>true</code> if the feature is supported by this editor factory
     */
    public static boolean supports(final EStructuralFeature feature) {
        return FLOAT_AS_INT_FEATURES.contains(feature);
    }

    /**
     * Converts negative values (other than -1) to positive for display.
     */
    public static final LabelProvider LABEL_PROVIDER = new LabelProvider() {

        @Override
        public String getText(final Object element) {
            if (element instanceof Integer) {
                final int value = (Integer)element;

                if (value >= -1) {
                    return Integer.toString(value);
                }

                final float floatValue = Float.intBitsToFloat(value & 0x7fffffff);
                return String.format("%.0f", floatValue); //$NON-NLS-1$
            }

            return super.getText(element);
        }

    };

    @Override
    public CellEditor createPropertyEditor(final Composite composite,
                                           final IItemPropertyDescriptor itemPropertyDescriptor,
                                           final IPropertyDescriptor propertyDescriptor,
                                           final Object object) {
        final EStructuralFeature feature = (EStructuralFeature)itemPropertyDescriptor.getFeature(object);
        final EDataType dataType = (EDataType)feature.getEType();
        return new Editor(dataType, composite);
    }

    @Override
    public boolean supportsStructuralFeature(final EStructuralFeature feature) {
        return supports(feature);
    }

    private class Editor extends EDataTypeCellEditor {

        private Editor(final EDataType dataType,
                       final Composite parent) {
            super(dataType, parent);
            setValidator(new Validator());
        }

        @Override
        public Object doGetValue() {
            final String valueAsText = text.getText();
            final long value = Long.parseLong(valueAsText);

            if ((value == -1) || (value < 0)) {
                return -1;
            }

            if (value <= Integer.MAX_VALUE) {
                return (int)value;
            }

            return (Float.floatToRawIntBits(value) | 0x80000000);
        }

        @Override
        public void doSetValue(final Object value) {
            final String valueAsText = LABEL_PROVIDER.getText(value);
            super.doSetValue(valueAsText);
        }

    }

    private static class Validator implements ICellEditorValidator, IInputValidator {

        @Override
        public String isValid(final String newText) {
            return isValid((Object)newText);
        }

        @Override
        public String isValid(final Object object) {
            try {
                final long value = Long.parseLong(object.toString());

                if (value < -1) {
                    return UiConstants.Util.getString("FloatAsIntPropertyEditorFactory.invalidValue"); //$NON-NLS-1$
                }

                return null;
            } catch (final Exception e) {
                String message = e.getClass().getName();
                final int index = message.lastIndexOf('.');

                if (index >= 0) {
                    message = message.substring(index + 1);
                }

                if (e.getLocalizedMessage() != null) {
                    message = message + ": " + e.getLocalizedMessage(); //$NON-NLS-1$
                }

                return message;
            }
        }

    }

}