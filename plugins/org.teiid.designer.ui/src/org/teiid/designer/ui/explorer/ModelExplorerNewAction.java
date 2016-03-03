package org.teiid.designer.ui.explorer;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchWindow;
import org.teiid.designer.metamodels.core.ModelType;
import org.teiid.designer.ui.PluginConstants;
import org.teiid.designer.ui.UiConstants;
import org.teiid.designer.ui.UiPlugin;
import org.teiid.designer.ui.actions.FindModelObjectHandler;
import org.teiid.designer.ui.common.product.ProductCustomizerMgr;
import org.teiid.designer.ui.viewsupport.DesignerPropertiesUtil;
import org.teiid.designer.ui.viewsupport.IPropertiesContext;
import org.teiid.designer.ui.viewsupport.ModelerUiViewUtils;
import org.teiid.designer.ui.wizards.NewModelWizard;
import org.teiid.designer.ui.wizards.NewModelWizardInput;

public class ModelExplorerNewAction extends Action implements IMenuCreator{
	static final String RELATIONAL = "Relational";  //$NON-NLS-1$
	static final String XML = "XML";  //$NON-NLS-1$
	static final String XML_SCHEMA = "XML Schema (XSD)";  //$NON-NLS-1$
	static final String WEB_SERVICE = "Web Service";  //$NON-NLS-1$
	static final String FUNCTION = "Function";  //$NON-NLS-1$
	
	private List<IAction> actions;
	
	
	
	public ModelExplorerNewAction() {
		super("", SWT.DROP_DOWN);
		setImageDescriptor(UiPlugin.getDefault().getImageDescriptor(PluginConstants.Images.NEW_MODEL_PROJECT_ICON));
		actions = createActions();
		setMenuCreator(this);

		setToolTipText("New..."); //$NON-NLS-1$
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		ModelerUiViewUtils.launchWizard("newModelProject", new StructuredSelection(), new Properties(), false); //$NON-NLS-1$
	}

	/**
	 * Helper method that wraps the given action in an ActionContributionItem
	 * and then adds it to the given menu.
	 * 
	 * @param parent
	 *            The menu to which the given action is to be added
	 * @param action
	 *            The action that is to be added to the given menu
	 */
	protected void addActionToMenu(Menu parent, IAction action) {
		ActionContributionItem item = new ActionContributionItem(action);
		item.fill(parent, -1);
	}

	/**
	 * @return A list of actions that can switch to one of the supported layout
	 *         modes
	 */
	protected List<IAction> createActions() {
		ArrayList<IAction> list = new ArrayList<IAction>();
		
		list.add( new Action("New Model Project", UiPlugin.getDefault().getImageDescriptor(PluginConstants.Images.NEW_MODEL_PROJECT_ICON)) {

			@Override
			public void run() {
				ModelerUiViewUtils.launchWizard("newModelProject", new StructuredSelection(), new Properties(), false); //$NON-NLS-1$
			}
			
		});
		
		list.add( new Action("New Source Model", UiPlugin.getDefault().getImageDescriptor(PluginConstants.Images.NEW_SOURCE_MODEL_ICON)) {

			@Override
			public void run() {
				launchNewModelWizard(ModelType.PHYSICAL_LITERAL, RELATIONAL, new Properties());
			}
			
		});
		list.add( new Action("New View Model", UiPlugin.getDefault().getImageDescriptor(PluginConstants.Images.NEW_VIEW_MODEL_ICON)) {

			@Override
			public void run() {
				launchNewModelWizard(ModelType.VIRTUAL_LITERAL, RELATIONAL, new Properties());
			}
			
		});
		list.add( new Action("New Web Service Model", UiPlugin.getDefault().getImageDescriptor(PluginConstants.Images.NEW_WEB_SERVICE_MODEL_ICON)) {

			@Override
			public void run() {
				launchNewModelWizard(ModelType.VIRTUAL_LITERAL, WEB_SERVICE, new Properties());
			}
			
		});
		list.add( new Action("New XML Document Model", UiPlugin.getDefault().getImageDescriptor(PluginConstants.Images.NEW_XML_DOCUMENT_MODEL_ICON)) {

			@Override
			public void run() {
				launchNewModelWizard(ModelType.VIRTUAL_LITERAL, XML, new Properties());
			}
			
		});
		list.add( new Action("New VDB", UiPlugin.getDefault().getImageDescriptor(PluginConstants.Images.NEW_VDB_ICON)) {

			@Override
			public void run() {
				ModelerUiViewUtils.launchWizard("newVdbWizard", new StructuredSelection(), new Properties(), false); //$NON-NLS-1$
			}
			
		});

		return list;
	}
	
	private void addFindModelObjectAction(Menu menu) {
		new Separator().fill(menu, -1);
		
		IAction action = new Action("Find model object...", UiPlugin.getDefault().getImageDescriptor(PluginConstants.Images.FIND_MODEL_OBJECT)) {

			@Override
			public void run() {
				FindModelObjectHandler.findObject();
			}
			
		};
		
		ActionContributionItem item = new ActionContributionItem(action);
		item.fill(menu, -1);
	}
	
	private void addGoToExamplesAction(Menu menu) {
		new Separator().fill(menu, -1);
		
		IAction goToExamplesAction = new Action("Go to examples...") {

			@Override
			public void run() {
				ModelerUiViewUtils.openTeiidDesignerExamplesPage(); 
			}
			
		};
		
		ActionContributionItem item = new ActionContributionItem(goToExamplesAction);
		item.fill(menu, -1);
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	private Menu fillMenu(Menu menu) {
		for (IAction action : actions) {
			addActionToMenu(menu, action);
		}

		addFindModelObjectAction(menu);
		addGoToExamplesAction(menu);

		setEnabled(!actions.isEmpty());

		return menu;
	}

	/**
	 * @see org.eclipse.jface.action.IMenuCreator#getMenu(Control)
	 */
	public Menu getMenu(Control parent) {
		return fillMenu(new Menu(parent));
	}

	/**
	 * @see org.eclipse.jface.action.IMenuCreator#getMenu(Menu)
	 */
	public Menu getMenu(Menu parent) {
		return fillMenu(new Menu(parent));
	}
	
	void launchNewModelWizard(ModelType type, String modelClass, Properties props) {
        final IWorkbenchWindow iww = UiPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();
        boolean successful = false;
        try {
            NewModelWizard wizard = new NewModelWizard(new NewModelWizardInput(modelClass, type, null), props);

            String viewId = ProductCustomizerMgr.getInstance().getProductCharacteristics().getPrimaryNavigationViewId();
            ISelection theSelection = iww.getSelectionService().getSelection(viewId);

            wizard.init(iww.getWorkbench(), (IStructuredSelection)theSelection);
            WizardDialog dialog = new WizardDialog(iww.getShell(), wizard);
            wizard.updateForProperties();
            
    		String openProjectStatus = DesignerPropertiesUtil.getProjectStatus(props);
    		if( openProjectStatus == null || !IPropertiesContext.NO_OPEN_PROJECT.equalsIgnoreCase(openProjectStatus) ){
	            int result = dialog.open();
	            if (result == Window.OK) {
	                successful = true;
	            }
    		} else {
    			return;
    		}
        } catch (Exception e) {
            UiConstants.Util.log(IStatus.ERROR, e, e.getMessage());
            MessageDialog.openError(iww.getShell(), "New Model Error", e.getMessage());
        } finally {
            notifyResult(successful);
        }
	}
}