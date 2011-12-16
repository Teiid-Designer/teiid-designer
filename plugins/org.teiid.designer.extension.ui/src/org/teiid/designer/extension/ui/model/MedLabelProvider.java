/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension.ui.model;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition;
import org.teiid.designer.extension.ui.Activator;
import org.teiid.designer.extension.ui.Messages;
import org.teiid.designer.extension.ui.UiConstants;

import com.metamatrix.core.util.CoreStringUtil;

/**
 * 
 */
public class MedLabelProvider extends LabelProvider {

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object)
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
     * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
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

}
