/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.viewsupport;

import java.util.Properties;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;

import com.metamatrix.core.event.IChangeListener;
import com.metamatrix.core.event.IChangeNotifier;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.internal.core.workspace.DotProjectUtils;
import com.metamatrix.modeler.internal.ui.explorer.ModelExplorerContentProvider;
import com.metamatrix.modeler.internal.ui.explorer.ModelExplorerLabelProvider;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.viewsupport.DesignerProperties;
import com.metamatrix.modeler.ui.viewsupport.DesignerPropertiesUtil;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.viewsupport.ClosedProjectFilter;
import com.metamatrix.ui.internal.viewsupport.StatusInfo;
import com.metamatrix.ui.internal.widget.Label;

/**
 * This dialog allows users to either SELECT or CREATE NEW Designer model
 * project
 * 
 */
public class DefineModelProjectDialog extends TitleAreaDialog implements
		IChangeListener, UiConstants {

	private static final String PREFIX = I18nUtil.getPropertyPrefix(DefineModelProjectDialog.class);

	private IProject project;

	private ILabelProvider labelProvider = new ModelExplorerLabelProvider();

	private Button newProjectButton;
	private Button browseButton;
	private Text selectedProjectText;

	DesignerProperties designerProperties;

	/**
	 * @since 5.5.3
	 */
	public DefineModelProjectDialog(Shell parentShell) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}

	/**
	 * @since 5.5.3
	 */
	public DefineModelProjectDialog(Shell parentShell, Properties properties) {
		this(parentShell);
		this.designerProperties = (DesignerProperties)properties;
	}

	/**
	 * @see org.eclipse.jface.dialogs.Dialog#close()
	 * @since 5.5.3
	 */
	@Override
	public boolean close() {

		if (this.labelProvider != null) {
			this.labelProvider.dispose();
		}

		return super.close();
	}

	/**
	 * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
	 * @since 5.5.3
	 */
	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText(Util.getString(PREFIX + "title")); //$NON-NLS-1$
	}

	/**
	 * @see org.eclipse.jface.dialogs.Dialog#createButtonBar(org.eclipse.swt.widgets.Composite)
	 * @since 5.5.3
	 */
	@Override
	protected Control createButtonBar(Composite parent) {
		Control buttonBar = super.createButtonBar(parent);
		getButton(OK).setEnabled(false);

		// set the first selection so that initial validation state is set
		// (doing it here since the selection handler uses OK
		// button)

		return buttonBar;
	}

	/**
	 * @see org.eclipse.jface.dialogs.TitleAreaDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 * @since 5.5.3
	 */
	@Override
	protected Control createDialogArea(Composite parent) {

		Composite pnlOuter = (Composite) super.createDialogArea(parent);
		Composite panel = new Composite(pnlOuter, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 4;
		panel.setLayout(gridLayout);
		panel.setLayoutData(new GridData(GridData.FILL_BOTH));

		// set title
		setTitle(Util.getString(PREFIX + "subTitle")); //$NON-NLS-1$
		setMessage(Util.getString(PREFIX + "initialMessage")); //$NON-NLS-1$

		Label label = WidgetFactory.createLabel(panel, Util.getString(PREFIX + "modelProject")); //$NON-NLS-1$
		label.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));

		// textfield for named type
		this.selectedProjectText = WidgetFactory.createTextField(panel,GridData.FILL_HORIZONTAL/* GridData.HORIZONTAL_ALIGN_FILL */);
		this.selectedProjectText.setToolTipText(Util.getString(PREFIX+ "projectNameTooltip")); //$NON-NLS-1$
		this.selectedProjectText.setEditable(false);
		this.selectedProjectText.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
		this.selectedProjectText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));

		// browse type button
		this.newProjectButton = WidgetFactory.createButton(panel, Util.getString(PREFIX+ "newButton")); //$NON-NLS-1$
		this.newProjectButton.setToolTipText(Util.getString(PREFIX+ "newButtonTooltip")); //$NON-NLS-1$
		this.newProjectButton.setEnabled(true);
		this.newProjectButton.setLayoutData(new GridData(SWT.CENTER, SWT.NONE,false, false));
		this.newProjectButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent theEvent) {
				handleNewProjectPressed();
			}
		});
		
		
		this.browseButton = WidgetFactory.createButton(panel, Util.getString(PREFIX+ "browseButton")); //$NON-NLS-1$
		this.browseButton.setToolTipText(Util.getString(PREFIX+ "browseButtonTooltip")); //$NON-NLS-1$
		this.browseButton.setEnabled(true);
		this.browseButton.setLayoutData(new GridData(SWT.CENTER, SWT.NONE,false, false));
		this.browseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent theEvent) {
				handleBrowseWorkspaceForObjectPressed();
			}
		});

		return panel;
	}

	@Override
	protected Control createContents(Composite parent) {
		Control control = super.createContents(parent);

		if (this.designerProperties != null) {
			// Check for existing project??
            this.project = DesignerPropertiesUtil.getProject(this.designerProperties);
            if (this.project != null) {
                this.selectedProjectText.setText(this.project.getName());
            }
			updateState();
		}
		return control;
	}

	public IProject getProject() {
		return this.project;
	}

	/**
	 * @see com.metamatrix.core.event.IChangeListener#stateChanged(com.metamatrix.core.event.IChangeNotifier)
	 * @since 5.5.3
	 */
	public void stateChanged(IChangeNotifier theSource) {
		updateState();
	}

	private void updateState() {
		IStatus status = Status.OK_STATUS;

		if (status.getSeverity() == IStatus.ERROR) {
			getButton(OK).setEnabled(false);
			setErrorMessage(status.getMessage());
		} else {
			getButton(OK).setEnabled(true);
			setErrorMessage(null);
			setMessage(Util.getString(PREFIX + "okMsg")); //$NON-NLS-1$
		}
	}
	
	private void setProject(Properties properties) {
		this.project = DesignerPropertiesUtil.getProject(properties);
		this.selectedProjectText.setText(this.project.getName());
	}
	
	private void handleNewProjectPressed() {
		
		Properties properties = this.designerProperties;
		if( properties == null ) {
			properties = new Properties();
		}
		ModelerUiViewUtils.launchWizard("newModelProject", new StructuredSelection(), properties, true); //$NON-NLS-1$
		setProject(properties);
	}

	private void handleBrowseWorkspaceForObjectPressed() {
		ModelWorkspaceDialog sdDialog = createProjectSelector();

		// add filters
		((ModelWorkspaceDialog) sdDialog).addFilter(new ClosedProjectFilter());

		sdDialog.open();

		if (sdDialog.getReturnCode() == Window.OK) {
			Object[] selections = sdDialog.getResult();
			// should be single selection
			// Check for duplicate project, if NOT then need to clear all properties and then set, else ignore
			IProject selectedProject = (IProject) selections[0];
			if( this.project != null && 
				selectedProject != null && 
				selectedProject.getName().equals(this.project.getName())) {
				return;
			}
			
			// projects are different, clear properties
			this.designerProperties.clear();
			this.project = selectedProject;
			this.selectedProjectText.setText(this.project.getName());
			DesignerPropertiesUtil.setProjectName(this.designerProperties, this.project.getName());
			
			if( this.project != null ) {
				// Check for source and view folders
				try {

					for( IResource res : project.members() ) {
						if( res instanceof IContainer ) {
							if ( ((IContainer)res).getName().equalsIgnoreCase("sources") ) { //$NON-NLS-1$
								DesignerPropertiesUtil.setSourcesFolderName(this.designerProperties, "sources"); //$NON-NLS-1$
							} else if(((IContainer)res).getName().equalsIgnoreCase("views")) { //$NON-NLS-1$
								DesignerPropertiesUtil.setViewsFolderName(this.designerProperties, "views"); //$NON-NLS-1$
							}
							
						}
					}
				} catch (CoreException ex) {
					// TODO Auto-generated catch block
					ex.printStackTrace();
				}
			}

			updateState();
		}

	}

	public ModelWorkspaceDialog createProjectSelector() {

		ModelWorkspaceDialog result = new ModelWorkspaceDialog(getShell(),
				null, new ModelExplorerLabelProvider(),
				new ModelExplorerContentProvider());

		String title = Util.getString(PREFIX + "selectionDialog.title"); //$NON-NLS-1$
		String message = Util.getString(PREFIX + "selectionDialog.message"); //$NON-NLS-1$
		result.setTitle(title);
		result.setMessage(message);
		result.setAllowMultiple(false);

		result.setInput(ResourcesPlugin.getWorkspace().getRoot());

		result.setValidator(new ISelectionStatusValidator() {
			public IStatus validate(Object[] selection) {
				if (selection == null
						|| selection.length == 0
						|| selection[0] == null
						|| !(selection[0] instanceof IProject)
						|| !DotProjectUtils.isModelerProject((IProject)selection[0]) ) {
					String msg = Util.getString(PREFIX+ "selectionDialog.invalidSelection"); //$NON-NLS-1$
					return new StatusInfo(UiConstants.PLUGIN_ID,IStatus.ERROR, msg);
				}
				return new StatusInfo(UiConstants.PLUGIN_ID);
			}
		});

		return result;
	}
}
