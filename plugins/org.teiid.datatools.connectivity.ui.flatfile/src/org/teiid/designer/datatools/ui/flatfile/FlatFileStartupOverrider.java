/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.datatools.ui.flatfile;

import java.lang.reflect.Field;
import org.eclipse.datatools.connectivity.oda.design.ui.manifest.DataSourceWizardInfo;
import org.eclipse.datatools.connectivity.oda.design.ui.manifest.UIExtensionManifest;
import org.eclipse.datatools.connectivity.oda.design.ui.manifest.UIManifestExplorer;
import org.eclipse.ui.IStartup;
import org.teiid.datatools.connectivity.ui.Activator;

/**
 * Called as a startup class and overrides the FolderSelectionWizardPage
 * with a replacement class allowing invalid flat file paths to be entered.
 */
public class FlatFileStartupOverrider implements IStartup {

    @Override
    public void earlyStartup() {
        UIManifestExplorer manifestExplorer = UIManifestExplorer.getInstance();

        try {
            UIExtensionManifest manifest = manifestExplorer.getExtensionManifest("org.eclipse.datatools.connectivity.oda.flatfile"); //$NON-NLS-1$
            DataSourceWizardInfo wizardInfo = manifest.getDataSourceWizardInfo();
            Field pageClassField = wizardInfo.getClass().getDeclaredField("m_pageClassName"); //$NON-NLS-1$
            pageClassField.setAccessible(true);

            /* Replaces org.eclipse.datatools.connectivity.oda.flatfile.ui.wizards.FolderSelectionWizardPage */
            pageClassField.set(wizardInfo, ReplacementFolderSelectionWizardPage.class.getName());
        } catch (Exception ex) {
            Activator.log(ex);
        }
    }
}
