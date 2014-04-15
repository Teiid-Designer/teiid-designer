package org.teiid.designer.ui.viewsupport;


import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.teiid.designer.core.translators.TranslatorOverrideProperty;
import org.teiid.designer.ui.PluginConstants;
import org.teiid.designer.ui.UiPlugin;

public class TranslatorPropertyLabelProvider extends ColumnLabelProvider {

    private final boolean nameColumn;

    public TranslatorPropertyLabelProvider( boolean nameColumn ) {
        this.nameColumn = nameColumn;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.viewers.ColumnLabelProvider#getImage(java.lang.Object)
     */
    @Override
    public Image getImage( Object element ) {
        TranslatorOverrideProperty property = (TranslatorOverrideProperty)element;
        String overridenValue = property.getOverriddenValue();
        Image image = null;

        if (!this.nameColumn) {
            if (property.getDefinition().isValidValue(overridenValue) == null) {
                if (property.hasOverridenValue()) {
                    if (!property.isCustom() || !property.getDefinition().getDefaultValue().equals(overridenValue)) {
                        image = UiPlugin.getDefault().getImage(PluginConstants.Images.RESTORE_DEFAULT_VALUE);
                    }
                }
            } else {
                image = UiPlugin.getDefault().getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJS_ERROR_TSK);
            }
        }

        return image;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.viewers.ColumnLabelProvider#getText(java.lang.Object)
     */
    @Override
    public String getText( Object element ) {
        TranslatorOverrideProperty property = (TranslatorOverrideProperty)element;

        if (this.nameColumn) {
            return property.getDefinition().getDisplayName();
        }

        boolean masked = property.getDefinition().isMasked();
        final String maskedValue = "*****"; //$NON-NLS-1$

        // return override value if it exists
        if (property.hasOverridenValue()) {
            return (masked ? maskedValue : property.getOverriddenValue());
        }

        // return default value
        return (masked ? maskedValue : property.getDefinition().getDefaultValue());
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.viewers.CellLabelProvider#getToolTipText(java.lang.Object)
     */
    @Override
    public String getToolTipText( Object element ) {
        TranslatorOverrideProperty property = (TranslatorOverrideProperty)element;

        if (this.nameColumn) {
            return property.getDefinition().getDescription();
        }

        if (property.hasOverridenValue()) {
            if (!property.isCustom() || !property.getDefinition().getDefaultValue().equals(property.getOverriddenValue())) {
                return property.getDefinition().isValidValue(property.getOverriddenValue());
            }
        }

        // default value is being used
        return "Using default value"; //$NON-NLS-1$
    }
}