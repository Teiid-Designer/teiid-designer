/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.modelgenerator.ldap.ui.wizards.pages.table;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.teiid.designer.modelgenerator.ldap.ui.ModelGeneratorLdapUiConstants;
import org.teiid.designer.modelgenerator.ldap.ui.ModelGeneratorLdapUiPlugin;
import org.teiid.designer.modelgenerator.ldap.ui.wizards.AbstractLdapLabelProvider;
import org.teiid.designer.modelgenerator.ldap.ui.wizards.ILdapEntryNode;
import org.teiid.designer.modelgenerator.ldap.ui.wizards.LdapImportWizardManager;

/**
 * Tree viewer label provider for LDAP connection
 */
public class LdapConnectionLabelProvider extends AbstractLdapLabelProvider {

    private Map<ImageDescriptor, Image> imgRegistry = new HashMap<ImageDescriptor, Image>();

    /**
     * Create new instance
     *
     * @param manager
     */
    public LdapConnectionLabelProvider(LdapImportWizardManager manager) {
        super(manager);
    }

    @Override
    public String getText(Object element) {
        if (element instanceof ILdapEntryNode) {
            ILdapEntryNode entryNode = (ILdapEntryNode)element;
            if (entryNode.isRoot()) {
                return entryNode.getLabel();
            }

            return entryNode.getSourceBaseName();
        }

        return null;
    }

    @Override
    public Image getImage(Object element) {
        if (!(element instanceof ILdapEntryNode))
            return null;

        ILdapEntryNode entryNode = (ILdapEntryNode)element;
        if (entryNode.isRoot()) {
            ImageDescriptor descriptor = ModelGeneratorLdapUiPlugin.getDefault().getImageDescriptor(ModelGeneratorLdapUiConstants.Images.LDAP_OBJECTS_ICON);
            return getImage(descriptor);
        }

        ImageDescriptor descriptor = ModelGeneratorLdapUiPlugin.getDefault().getImageDescriptor(ModelGeneratorLdapUiConstants.Images.LDAP_TABLE_ICON);
        return getImage(descriptor);
    }
}
