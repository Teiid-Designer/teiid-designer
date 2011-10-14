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
import com.metamatrix.modeler.transformation.ui.wizards.xmlfile.TeiidXmlFileImportProcessor;
import com.metamatrix.ui.internal.util.UiUtil;
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
    TeiidMetadataTargetModelsPage targetPage;
    TeiidMetadataFileOptionsPage fileOptionsPage;
    
    private TeiidMetadataImportInfo filesInfo;
    
    boolean isFlatFileOption = true;
    
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
        this.filesInfo = new TeiidMetadataImportInfo();
        
        // Set initial view model and view model location values if present from selection
        if( isViewRelationalModel ) {
        	this.filesInfo.setViewModelName( ((IFile)seletedObj).getName());
        	this.filesInfo.setViewModelLocation(((IFile)seletedObj).getFullPath().removeLastSegments(1));
        	this.filesInfo.setViewModelExists(true);
        }
        if( folder != null ) {
        	this.filesInfo.setSourceModelLocation(folder.getFullPath());
        	this.filesInfo.setViewModelLocation(folder.getFullPath());
        }
        
        this.fileOptionsPage = new TeiidMetadataFileOptionsPage(this);
        addPage(this.fileOptionsPage);
        
        this.targetPage = new TeiidMetadataTargetModelsPage(this.filesInfo);
        addPage(this.targetPage);
        
        this.sourcePage = new TeiidMetadataSourceSelectionPage(this.filesInfo);
        addPage(this.sourcePage);

	}

	@Override
	public boolean finish() {
		if( this.filesInfo.isFlatFileMode() ) {
			final TeiidMetadataImportProcessor processor = new TeiidMetadataImportProcessor(this.filesInfo, this.getShell());
			
			UiUtil.runInSwtThread(new Runnable() {
				@Override
				public void run() {
					processor.execute();
				}
			}, false);
			
			return true;
		} else {
			final TeiidXmlFileImportProcessor processor = new TeiidXmlFileImportProcessor(this.filesInfo, this.getShell());
			
			UiUtil.runInSwtThread(new Runnable() {
				@Override
				public void run() {
					processor.execute();
				}
			}, false);
			
			return true;
		}
	}

	public void setFileOption(boolean useFlatFile) {
		this.filesInfo.setFlatFileMode(useFlatFile);
	}
	
}
