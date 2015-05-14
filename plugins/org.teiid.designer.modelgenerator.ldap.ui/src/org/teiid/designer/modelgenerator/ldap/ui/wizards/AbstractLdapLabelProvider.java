/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.modelgenerator.ldap.ui.wizards;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * Abstract implementation of LDAP label provider
 */
public abstract class AbstractLdapLabelProvider extends LabelProvider {

    private final LdapImportWizardManager importManager;

    private Map<ImageDescriptor, Image> imgRegistry = new HashMap<ImageDescriptor, Image>();

    /**
     * Create new instance
     *
     * @param manager
     */
    public AbstractLdapLabelProvider(LdapImportWizardManager manager) {
        this.importManager = manager;
    }

    /**
     * @return the importManager
     */
    public LdapImportWizardManager getImportManager() {
        return this.importManager;
    }

    protected Image getImage(ImageDescriptor descriptor) {
        if (descriptor == null)
            return null;

        Image img = imgRegistry.get(descriptor);
        if (img == null) {
            img = descriptor.createImage();
            imgRegistry.put(descriptor, img);
        }

        return img;
    }

    @Override
    public void dispose() {
        super.dispose();
        for (Image img : imgRegistry.values()) {
            img.dispose();
        }

        imgRegistry.clear();
    }
}
