/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.modelgenerator.ldap.ui.wizards.pages.columns;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.teiid.designer.modelgenerator.ldap.ui.ModelGeneratorLdapUiConstants;
import org.teiid.designer.modelgenerator.ldap.ui.ModelGeneratorLdapUiPlugin;
import org.teiid.designer.modelgenerator.ldap.ui.wizards.AbstractLdapLabelProvider;
import org.teiid.designer.modelgenerator.ldap.ui.wizards.ILdapAttributeNode;
import org.teiid.designer.modelgenerator.ldap.ui.wizards.ILdapEntryNode;
import org.teiid.designer.modelgenerator.ldap.ui.wizards.LdapImportWizardManager;

/**
 *
 */
public class LdapEntryLabelProvider extends AbstractLdapLabelProvider {

    /**
     * @param manager
     */
    public LdapEntryLabelProvider(LdapImportWizardManager manager) {
        super(manager);
    }

    @Override
    public String getText(Object element) {
        if (element instanceof ILdapEntryNode) {
            return ((ILdapEntryNode)element).getLabel();
        }

        if (element instanceof ILdapAttributeNode) {
            return ((ILdapAttributeNode)element).getId();
        }

        return null;
    }

    @Override
    public Image getImage(Object element) {
        ImageDescriptor descriptor = null;

        ModelGeneratorLdapUiPlugin plugin = ModelGeneratorLdapUiPlugin.getDefault();
        if (element instanceof ILdapEntryNode) {
            ILdapEntryNode entryNode = (ILdapEntryNode)element;
            if (entryNode.isRoot())
                descriptor = plugin.getImageDescriptor(ModelGeneratorLdapUiConstants.Images.LDAP_OBJECTS_ICON);
            else
                descriptor = plugin.getImageDescriptor(ModelGeneratorLdapUiConstants.Images.LDAP_TABLE_ICON);
        } else if (element instanceof ILdapAttributeNode) {
            descriptor = plugin.getImageDescriptor(ModelGeneratorLdapUiConstants.Images.LDAP_COLUMN_ICON);
        }

        return getImage(descriptor);
    }
}
