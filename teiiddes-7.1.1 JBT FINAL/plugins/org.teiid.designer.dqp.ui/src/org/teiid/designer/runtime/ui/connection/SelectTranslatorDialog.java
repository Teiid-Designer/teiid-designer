package org.teiid.designer.runtime.ui.connection;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.teiid.designer.runtime.TeiidTranslator;

import com.metamatrix.modeler.dqp.DqpPlugin;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.dqp.ui.DqpUiPlugin;
import com.metamatrix.modeler.internal.dqp.ui.workspace.TeiidViewTreeProvider;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.widget.MessageLabel;

/**
 * Provides dialog for selecting a Translator from existing Teiid Servers.
 * 
 */
public class SelectTranslatorDialog extends ElementTreeSelectionDialog
		implements ISelectionChangedListener {
	private static final String DEFAULT_TITLE = DqpUiConstants.UTIL
			.getString("SelectTranslatorDialog.title"); //$NON-NLS-1$ 
	private static final String DEFAULT_MESSAGE = DqpUiConstants.UTIL
			.getString("SelectTranslatorDialog.defaultMessage"); //$NON-NLS-1$ 

	private Text translatorNameText;
	private MessageLabel statusMessageLabel;
	private TeiidTranslator selectedTranslator;

	private static final String UNDEFINED = DqpUiConstants.UTIL
			.getString("SelectJndiDataSourceDialog.undefined"); //$NON-NLS-1$ 

	private static final TeiidViewTreeProvider treeProvider = new TeiidViewTreeProvider(
			false, true, false);

	public SelectTranslatorDialog(Shell parent) {
		this(parent, DEFAULT_TITLE, treeProvider, treeProvider);
	}

	public SelectTranslatorDialog(Shell parent, ILabelProvider labelProvider,
			ITreeContentProvider contentProvider) {
		this(parent, null, labelProvider, contentProvider);
	}

	public SelectTranslatorDialog(Shell parent, String title,
			ILabelProvider labelProvider, ITreeContentProvider contentProvider) {
		super(parent, labelProvider, contentProvider);
		setTitle(title);
		setMessage(DEFAULT_MESSAGE);
		setInput(DqpPlugin.getInstance().getServerManager());
		setAllowMultiple(false);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite panel = new Composite(parent, SWT.NONE);
		panel.setLayout(new GridLayout());
		GridData panelData = new GridData(GridData.FILL_BOTH);
		panel.setLayoutData(panelData);

		Group selectedGroup = WidgetFactory.createGroup(panel,
				"Selected Translator", GridData.FILL_HORIZONTAL); //$NON-NLS-1$
		// selectedGroup.setText(null);
		selectedGroup.setLayout(new GridLayout(2, false));

		this.translatorNameText = WidgetFactory.createTextField(selectedGroup,
				GridData.FILL_HORIZONTAL, UNDEFINED);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.heightHint = convertHeightInCharsToPixels(1);
		this.translatorNameText.setLayoutData(data);
		this.translatorNameText.setEditable(false);
		this.translatorNameText.setBackground(panel.getBackground());
		this.translatorNameText.setText(UNDEFINED);

		super.createDialogArea(panel);

		this.statusMessageLabel = new MessageLabel(panel);
		GridData statusData = new GridData(GridData.FILL_HORIZONTAL);
		data.heightHint = convertHeightInCharsToPixels(1);
		this.statusMessageLabel.setLayoutData(statusData);
		this.statusMessageLabel.setEnabled(false);
		this.statusMessageLabel.setText(UNDEFINED);

		// listen to selection in the tree
		getTreeViewer().addSelectionChangedListener(this);

		getTreeViewer().getTree().setEnabled(true);
		getTreeViewer().setSorter(new ViewerSorter());
		((TeiidViewTreeProvider) getTreeViewer().getContentProvider())
				.setShowTranslators(true);
		getTreeViewer().expandToLevel(3);

		return panel;

	}

	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		TreeSelection selection = (TreeSelection) event.getSelection();
		if (selection.isEmpty()) {
			this.selectedTranslator = null;
			this.translatorNameText.setText(UNDEFINED);
			updateOnSelection(null);
			return;
		}

		Object firstElement = selection.getFirstElement();

		if (!(firstElement instanceof TeiidTranslator)) {
			this.selectedTranslator = null;
			this.translatorNameText.setText(UNDEFINED);
		} else {
			this.selectedTranslator = (TeiidTranslator) selection
					.getFirstElement();
			this.translatorNameText.setText(selectedTranslator.getName());
		}

		updateOnSelection(firstElement);
	}

	private void updateOnSelection(Object selectedObject) {
		IStatus status = new Status(IStatus.INFO, DqpUiPlugin.PLUGIN_ID,
				DqpUiPlugin.UTIL
						.getString("SelectTranslatorDialog.okSelectionMessage")); //$NON-NLS-1$
		if (selectedObject != null) {
			if (!(selectedObject instanceof TeiidTranslator)) {
				status = new Status(
						IStatus.ERROR,
						DqpUiPlugin.PLUGIN_ID,
						DqpUiPlugin.UTIL
								.getString("SelectTranslatorDialog.invalidSelectionMessage")); //$NON-NLS-1$
				getOkButton().setEnabled(false);
			} else {
				getOkButton().setEnabled(true);
			}
		} else {
			status = new Status(
					IStatus.ERROR,
					DqpUiPlugin.PLUGIN_ID,
					DqpUiPlugin.UTIL
							.getString("SelectTranslatorDialog.invalidSelectionMessage")); //$NON-NLS-1$
			getOkButton().setEnabled(false);
		}

		this.statusMessageLabel.setErrorStatus(status);
	}

	/**
	 * Returns the current TeiidTranslator
	 * @return the TeiidTranslator. may return null
	 */
	public TeiidTranslator getSelectedTranslator() {
		return this.selectedTranslator;
	}

}
