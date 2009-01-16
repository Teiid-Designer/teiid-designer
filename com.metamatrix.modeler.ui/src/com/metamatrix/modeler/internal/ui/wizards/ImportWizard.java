/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

package com.metamatrix.modeler.internal.ui.wizards;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.internal.IWorkbenchGraphicConstants;
import org.eclipse.ui.internal.WorkbenchImages;
import com.metamatrix.core.util.ClassUtil;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.modeler.ui.product.IModelerProductContexts;
import com.metamatrix.ui.internal.util.WidgetUtil;
import com.metamatrix.ui.internal.wizard.AbstractSelectionWizard;

/**<p>
 * </p>
 * @since 4.0
 */
public final class ImportWizard extends AbstractSelectionWizard implements ClassUtil.Constants,
                                                                           UiConstants,
                                                                           UiConstants.ExtensionPoints.ImportWizards {
    //============================================================================================================================
    // Constants

    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(ImportWizard.class);

    private static final String TITLE = getString("title"); //$NON-NLS-1$

    //============================================================================================================================
    // Static Utility Methods

    /**<p>
     * </p>
     * @since 4.0
     */
    private static String getString(final String id) {
        return Util.getString(I18N_PREFIX + id);
    }

    //============================================================================================================================
    // Constructors

    /**<p>
     * </p>
     * @since 4.0
     */
    public ImportWizard(final IWorkbench workbench,
	                    final IStructuredSelection selection ) {
		super(UiPlugin.getDefault(), workbench, selection, TITLE,
		      WorkbenchImages.getImageDescriptor(IWorkbenchGraphicConstants.IMG_WIZBAN_IMPORT_WIZ), ID,
		      new SingleColumnTableViewerSorter());
	}

    // ============================================================================================================================
    // Overridden Methods

    @Override
    protected IConfigurationElement[] getConfigurationElementsFor() {
    	// TODO Auto-generated method stub
    	IConfigurationElement[] elements = super.getConfigurationElementsFor();

    	// FILTER THESE!!
    	List result = new ArrayList(elements.length);
    	for( int i=0; i< elements.length; i++ ) {
    		String contribID = elements[i].getAttribute(UiConstants.ExtensionPoints.ImportWizards.ID_ID);
    		if (UiPlugin.getDefault().isProductContextValueSupported(IModelerProductContexts.Contributions.IMPORT, contribID)) {
    			result.add(elements[i]);
    		}
    	}

    	return (IConfigurationElement[])result.toArray(new IConfigurationElement[result.size()]);
    }

    //============================================================================================================================
    // Implemented Methods

    /**<p>
     * </p>
     * @see com.metamatrix.ui.internal.wizard.AbstractSelectionWizard#createSelectedWizard()
     * @since 4.0
     */
    @Override
    protected IWizard createSelectedWizard(final IConfigurationElement element) {
        try {
            return (IWizard)element.createExecutableExtension(CLASS);
        } catch (final CoreException err) {
            Util.log(err);
            WidgetUtil.showError(err);
            return null;
        }
    }

    /**<p>
     * </p>
     * @see com.metamatrix.ui.internal.wizard.AbstractSelectionWizard#getSelectedWizardIcon(org.eclipse.core.runtime.IConfigurationElement)
     * @since 4.0
     */
    @Override
    protected String getSelectedWizardIcon(final IConfigurationElement element) {
        return element.getAttribute(ICON);
    }

    /**<p>
     * </p>
     * @see com.metamatrix.ui.internal.wizard.AbstractSelectionWizard#getSelectedWizardName(org.eclipse.core.runtime.IConfigurationElement)
     * @since 4.0
     */
    @Override
    protected String getSelectedWizardName(final IConfigurationElement element) {
        return element.getAttribute(NAME);
    }

    //============================================================================================================================
    // Overridden Methods

    /**<p>
     * </p>
     * @see com.metamatrix.ui.internal.wizard.AbstractSelectionWizard#initializeSelectedWizard(org.eclipse.jface.wizard.IWizard)
     * @since 4.0
     */
    @Override
    protected void initializeSelectedWizard(final IWizard wizard,
                                            final IWorkbench workbench,
                                            final IStructuredSelection selection) {
        ((IImportWizard)wizard).init(workbench, selection);
    }
}
