/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension.ui.model;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Image;
import org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition;
import org.teiid.designer.extension.ui.Activator;
import org.teiid.designer.extension.ui.Messages;
import org.teiid.designer.extension.ui.UiConstants;

import com.metamatrix.core.util.CoreStringUtil;

/**
 * 
 */
public class MedLabelProvider extends ColumnLabelProvider {

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.viewers.ColumnLabelProvider#getImage(java.lang.Object)
     */
    @Override
    public Image getImage( Object element ) {
        if (element instanceof MedModelNode) {
            MedModelNode node = (MedModelNode)element;

            if (node.isDescription() || node.isMetamodelUri() || node.isNamespacePrefix() || node.isNamespaceUri()
                    || node.isVersion()) {
                return Activator.getDefault().getImage(UiConstants.ImageIds.ATTRIBUTE);
            }

            if (node.isMed()) {
                return Activator.getDefault().getImage(UiConstants.ImageIds.MED);
            }

            if (node.isMetaclass()) {
                return Activator.getDefault().getImage(UiConstants.ImageIds.METACLASS);
            }

            if (node.isPropertyDefinition()) {
                return Activator.getDefault().getImage(UiConstants.ImageIds.PROPERTY_DEFINITION);
            }
        }

        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.viewers.ColumnLabelProvider#getText(java.lang.Object)
     */
    @Override
    public String getText( Object element ) {
        if (element instanceof MedModelNode) {
            MedModelNode node = (MedModelNode)element;

            if (node.isNamespacePrefix()) {
                return Messages.namespacePrefixNodeLabel;
            }

            if (node.isNamespaceUri()) {
                return Messages.namespaceUriNodeLabel;
            }

            if (node.isMetamodelUri()) {
                return Messages.metamodelUriNodeLabel;
            }

            if (node.isDescription()) {
                return Messages.descriptionNodeLabel;
            }

            if (node.isVersion()) {
                return Messages.versionNodeLabel;
            }

            if (node.isMed()) {
                return Messages.medNodeLabel;
            }

            if (node.isPropertyDefinition()) {
                ModelExtensionPropertyDefinition propDefn = (ModelExtensionPropertyDefinition)node.getData();
                String id = propDefn.getSimpleId();
                return (CoreStringUtil.isEmpty(id) ? Messages.missingPropertyIdNodeLabel : id);
            }

            if (node.isMetaclass()) {
                return node.getData().toString();
            }
        }

        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.viewers.CellLabelProvider#getToolTipImage(java.lang.Object)
     */
    @Override
    public Image getToolTipImage( Object object ) {
        if (CoreStringUtil.isEmpty(getToolTipText(object))) {
            return null;
        }

        return getImage(object);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.viewers.CellLabelProvider#getToolTipText(java.lang.Object)
     */
    @Override
    public String getToolTipText( Object element ) {
        if (element instanceof MedModelNode) {
            MedModelNode node = (MedModelNode)element;
            Object data = node.getData();

            if (node.isDescription()) {
                return CoreStringUtil.isEmpty((String)data) ? Messages.descriptionNodeNoValueToolTip
                                                           : NLS.bind(Messages.descriptionNodeToolTip, data);
            }

            if (node.isMetamodelUri()) {
                return CoreStringUtil.isEmpty((String)data) ? Messages.metamodelUriNodeToolTip
                                                           : NLS.bind(Messages.metamodelUriNodeNoValueToolTip, data);
            }

            if (node.isNamespacePrefix()) {
                return CoreStringUtil.isEmpty((String)data) ? Messages.namespacePrefixNodeNoValueToolTip
                                                           : NLS.bind(Messages.namespacePrefixNodeToolTip, data);
            }

            if (node.isNamespaceUri()) {
                return CoreStringUtil.isEmpty((String)data) ? Messages.namespaceUriNodeNoValueToolTip
                                                           : NLS.bind(Messages.namespaceUriNodeToolTip, data);
            }

            if (node.isVersion()) {
                return NLS.bind(Messages.versionNodeToolTip, data);
            }

            if (node.isMetaclass()) {
                String metaclass = node.getMetaclass();
                int index = metaclass.lastIndexOf('.');
                return NLS.bind(Messages.metaclassNodeToolTip, metaclass.substring(index + 1));
            }

            if (node.isPropertyDefinition()) {
                String metaclass = node.getMetaclass();
                int index = metaclass.lastIndexOf('.');
                ModelExtensionPropertyDefinition propDefn = node.getPropertyDefinition();
                return NLS.bind(Messages.propertyNodeToolTip, metaclass.substring(index + 1), propDefn.getSimpleId());
            }

        }

        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.viewers.CellLabelProvider#getToolTipTimeDisplayed(java.lang.Object)
     */
    @Override
    public int getToolTipTimeDisplayed( Object object ) {
        return 2000;
    }

}
