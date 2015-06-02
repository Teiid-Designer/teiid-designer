package org.teiid.designer.transformation.ui.teiidddl;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.core.designer.util.FileUtils;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.transformation.ui.PluginConstants;
import org.teiid.designer.transformation.ui.UiConstants;
import org.teiid.designer.transformation.ui.UiPlugin;
import org.teiid.designer.ui.common.InternalUiConstants;
import org.teiid.designer.ui.common.eventsupport.SelectionUtilities;
import org.teiid.designer.ui.common.util.WidgetUtil;
import org.teiid.designer.ui.common.wizard.AbstractWizard;
import org.teiid.designer.ui.viewsupport.ModelIdentifier;
import org.teiid.designer.ui.viewsupport.ModelUtilities;
//import org.teiid.designer.core.xslt.Style;
//import org.teiid.designer.core.xslt.StyleRegistry;
//import org.teiid.designer.ddl.DdlOptions;
//import org.teiid.designer.ddl.DdlPlugin;
//import org.teiid.designer.ddl.DdlWriter;

public class ExportTeiidDdlWizard extends AbstractWizard implements
		FileUtils.Constants, IExportWizard, InternalUiConstants.Widgets, CoreStringUtil.Constants, UiConstants {
	
	private enum ExportChoice {
		CLIPBOARD(getString("ExportTeiidDdlWizard_clipboardChoiceLabel")), //$NON-NLS-1$

		FILE(getString("ExportTeiidDdlWizard_fileChoiceLabel")); //$NON-NLS-1$

		private final String label;

		private ExportChoice(String label) {
			this.label = label;
		}

		public String getLabel() {
			return label;
		}
	}

	private static final String TITLE = getString("ExportTeiidDdlWizard_title"); //$NON-NLS-1$

	private static final ImageDescriptor IMAGE = UiPlugin.getDefault().getImageDescriptor(PluginConstants.Images.EXPORT_DDL_ICON);


	private static final String EXPORT_ERROR_MESSAGE = getString("ExportTeiidDdlWizard_exportErrorMessage"); //$NON-NLS-1$


    
    /**
     * @since 4.0
     */
    private static String getString( final String id ) {
        return UiConstants.Util.getString(id);
    }

    private TeiidDdlExporter exporter;
    
	private IStructuredSelection selection;

	private WizardPage pg;
	private ExportTeiidDdlModelSelectionPage modelSelectionPage;
	
	private ExportTeiidShowDdlPage ddlSummaryPage;
	
	private ExportTeiidDdlOutputPage ddlOutputPage;

	private boolean invalidSelection;

	/**
	 * @since 4.0
	 */
	public ExportTeiidDdlWizard() {
		super(UiPlugin.getDefault(), TITLE, IMAGE);
	}

	/**
	 * @see org.eclipse.jface.wizard.IWizard#performFinish()//	private ModelWorkspaceSelections selections;
	 * @since 4.0
	 */
	@Override
	public boolean finish() {

		try {
			switch (exporter.getExportType()) {
				case FILE:
					exporter.exportToFile();
					break;
				case CLIPBOARD:
					exporter.exportToClipboard();
					break;
			}

			return true;
		} catch (Throwable err) {
			if (err instanceof InvocationTargetException) {
				err = ((InvocationTargetException) err).getTargetException();
			}
			Util.log(err);
			WidgetUtil.showError(EXPORT_ERROR_MESSAGE);
			return false;
		}
	}

	/**ExportTeiidDdlOutputPage
	 * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench,
	 *      org.eclipse.jface.viewers.IStructuredSelection)
	 * @since 4.0
	 */
	@Override
	public void init(final IWorkbench workbench, final IStructuredSelection selection) {

		invalidSelection = false;
		ModelResource selectedModelResource = null;
		// invalidSelectionMessage = null;
		// Check for selection to be a single Relational Model
		if (!SelectionUtilities.isSingleSelection(selection)) {
			invalidSelection = true;
			// invalidSelectionMessage =
			// "Cannot Export DDL.\n\nMultiple resources are selected.\n\nOnly single relational metamodels can be exported as DDL format.";
		} else if (!SelectionUtilities.isAllIResourceObjects(selection)) {
			// invalidSelectionMessage =
			// "Cannot Export DDL.\n\nSelected object is not a valid resource.\n\nOnly single relational metamodels can be exported as DDL format.";
			invalidSelection = true;
		} else {
			Object obj = SelectionUtilities.getSelectedObject(selection);
			if (obj instanceof IResource) {
				IResource iRes = (IResource) obj;

				if (!ModelIdentifier.isRelationalSourceModel(iRes)
						&& !ModelIdentifier.isRelationalViewModel(iRes)) {
					// invalidSelectionMessage =
					// "Cannot Export DDL.\n\nOnly single relational view or source metamodels can be exported as DDL format.";
					invalidSelection = true;
				} else {
					selectedModelResource = ModelUtilities.getModelResource(obj);
				}
			}

		}

		this.selection = selection;
		this.exporter = new TeiidDdlExporter();
		this.exporter.setModelResource(selectedModelResource);
		
		modelSelectionPage = new ExportTeiidDdlModelSelectionPage(exporter);
		this.modelSelectionPage.setPageComplete(false);
		addPage(modelSelectionPage);
		
		this.ddlSummaryPage = new ExportTeiidShowDdlPage(exporter);
		this.ddlSummaryPage.setPageComplete(false);
		addPage(ddlSummaryPage);
		
		this.ddlOutputPage = new ExportTeiidDdlOutputPage(exporter);
		this.ddlOutputPage.setPageComplete(false);
		addPage(ddlOutputPage);
	}
}
