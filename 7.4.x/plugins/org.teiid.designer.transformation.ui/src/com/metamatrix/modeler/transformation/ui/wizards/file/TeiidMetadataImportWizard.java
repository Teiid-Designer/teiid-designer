/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.ui.wizards.file;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;

import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelIdentifier;
import com.metamatrix.modeler.transformation.ui.UiConstants;
import com.metamatrix.modeler.transformation.ui.UiPlugin;
import com.metamatrix.ui.internal.util.WidgetUtil;
import com.metamatrix.ui.internal.wizard.AbstractWizard;


/**
 * Import wizard designed to import metadata from one or more Teiid formatted data files and create a relational
 * model containing the standard/generated File Connector procedures and create view relational tables containing
 * the SQL containing the function call which will return the data from the file in relational table format
 */
public class TeiidMetadataImportWizard extends AbstractWizard implements
		IImportWizard, UiConstants {

	private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(TeiidMetadataImportWizard.class);

	private static final String TITLE =  getString("title"); //$NON-NLS-1$

	private static final ImageDescriptor IMAGE = UiPlugin.getDefault().getImageDescriptor(Images.IMPORT_TEIID_METADATA);

    private static String getString( final String id ) {
        return Util.getString(I18N_PREFIX + id);
    }
	
    TeiidMetadataSourceSelectionPage sourcePage;
    TeiidMetadataTargetModelsPage targetsPage;
    
    private TeiidMetadataImportInfo info;
    
	/**
	 * @since 4.0
	 */
	public TeiidMetadataImportWizard() {
		super(UiPlugin.getDefault(), TITLE, IMAGE);
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection inputSelection) {
        IStructuredSelection selection = inputSelection;

        Object seletedObj = selection.getFirstElement();
        IContainer folder = null;
        boolean isViewRelationalModel = false;
        
        try {
            if (seletedObj instanceof IFile) {
                ModelResource modelResource = ModelUtil.getModelResource((IFile)seletedObj, false);
                isViewRelationalModel = ModelIdentifier.isRelationalViewModel(modelResource);
            }
        } catch (Exception e) {
            Util.log(e);
        }
        // If not null, set folder to current selection if a folder or to containing folder if a model object
        if (!selection.isEmpty()) {
            final Object obj = selection.getFirstElement();
            folder = ModelUtil.getContainer(obj);
            try {
                if (folder != null && folder.getProject().getNature(ModelerCore.NATURE_ID) == null) {
                    folder = null;
                }
            } catch (final CoreException err) {
                Util.log(err);
                WidgetUtil.showError(err);
            }
        }
        
        // Construct the business object
        this.info = new TeiidMetadataImportInfo();
        
        // Set initial view model and view model location values if present from selection
        if( isViewRelationalModel ) {
        	this.info.setViewModelName( ((IFile)seletedObj).getName());
        	this.info.setViewModelExists(true);
        }
        if( folder != null ) {
        	this.info.setSourceModelLocation(folder.getFullPath());
        	this.info.setViewModelLocation(folder.getFullPath());
        }
        
        this.sourcePage = new TeiidMetadataSourceSelectionPage(this.info);
        addPage(this.sourcePage);
        
        this.targetsPage = new TeiidMetadataTargetModelsPage(this.info);
        addPage(this.targetsPage);

	}

	@Override
	public boolean finish() {
		TeiidMetadataImportProcessor processor = new TeiidMetadataImportProcessor(this.info);
		
		processor.execute();
		
		return true;
	}

}
