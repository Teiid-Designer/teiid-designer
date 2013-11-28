package org.teiid.designer.runtime.ui.connection;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
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
import org.teiid.designer.runtime.DqpPlugin;
import org.teiid.designer.runtime.spi.ITeiidServer;
import org.teiid.designer.runtime.spi.ITeiidTranslator;
import org.teiid.designer.runtime.ui.DqpUiConstants;
import org.teiid.designer.runtime.ui.views.TeiidServerContentProvider;
import org.teiid.designer.runtime.ui.views.TeiidServerLabelProvider;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.common.widget.MessageLabel;


/**
 * Provides dialog for selecting a Translator from existing Teiid Instances.
 * 
 *
 * @since 8.0
 */
public class SelectTranslatorDialog extends ElementTreeSelectionDialog implements ISelectionChangedListener {

    private static final String DEFAULT_TITLE = DqpUiConstants.UTIL.getString("SelectTranslatorDialog.title"); //$NON-NLS-1$ 
    private static final String DEFAULT_MESSAGE = DqpUiConstants.UTIL.getString("SelectTranslatorDialog.defaultMessage"); //$NON-NLS-1$ 
    private static final String UNDEFINED = DqpUiConstants.UTIL.getString("SelectJndiDataSourceDialog.undefined"); //$NON-NLS-1$ 

    private Text translatorNameText;
    private MessageLabel statusMessageLabel;
    private ITeiidTranslator selectedTranslator;

    public SelectTranslatorDialog( Shell parent ) {
        super(parent, new TeiidServerLabelProvider(), new TeiidServerContentProvider(false, true, false));
        setTitle(DEFAULT_TITLE);
        setMessage(DEFAULT_MESSAGE);
        setInput(DqpPlugin.getInstance().getServerManager());
        setAllowMultiple(false);
    }

    @Override
    protected Control createDialogArea( Composite parent ) {
        Composite panel = new Composite(parent, SWT.NONE);
        panel.setLayout(new GridLayout());
        GridData panelData = new GridData(GridData.FILL_BOTH);
        panel.setLayoutData(panelData);

        Group selectedGroup = WidgetFactory.createGroup(panel, "Selected Translator", GridData.FILL_HORIZONTAL, 1, 2); //$NON-NLS-1$

        this.translatorNameText = WidgetFactory.createTextField(selectedGroup, GridData.FILL_HORIZONTAL, UNDEFINED);
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

        getTreeViewer().expandToLevel(3);

        return panel;
    }
    
    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.ui.dialogs.ElementTreeSelectionDialog#createTreeViewer(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected TreeViewer createTreeViewer( Composite parent ) {
        TreeViewer viewer = super.createTreeViewer(parent);
        viewer.addSelectionChangedListener(this);
        viewer.getTree().setEnabled(true);
        viewer.setSorter(new ViewerSorter());
        viewer.setFilters(new ViewerFilter[] { new ViewerFilter() {
            @Override
            public boolean select( Viewer viewer,
                                   Object parentElement,
                                   Object element ) {
                if (element instanceof ITeiidServer) {
                    return element.equals(DqpPlugin.getInstance().getServerManager().getDefaultServer());
                }

                return true;
            }
        } });

        return viewer;
    }

    @Override
    public void selectionChanged( SelectionChangedEvent event ) {
        TreeSelection selection = (TreeSelection)event.getSelection();
        if (selection.isEmpty()) {
            this.selectedTranslator = null;
            this.translatorNameText.setText(UNDEFINED);
            updateOnSelection(null);
            return;
        }

        Object firstElement = selection.getFirstElement();

        if (!(firstElement instanceof ITeiidTranslator)) {
            this.selectedTranslator = null;
            this.translatorNameText.setText(UNDEFINED);
        } else {
            this.selectedTranslator = (ITeiidTranslator)selection.getFirstElement();
            this.translatorNameText.setText(selectedTranslator.getName());
        }

        updateOnSelection(firstElement);
    }

    private void updateOnSelection( Object selectedObject ) {
        IStatus status = new Status(IStatus.INFO,
                                    DqpUiConstants.PLUGIN_ID,
                                    DqpUiConstants.UTIL.getString("SelectTranslatorDialog.okSelectionMessage")); //$NON-NLS-1$
        if (selectedObject != null) {
            if (!(selectedObject instanceof ITeiidTranslator)) {
                status = new Status(IStatus.ERROR,
                                    DqpUiConstants.PLUGIN_ID,
                                    DqpUiConstants.UTIL.getString("SelectTranslatorDialog.invalidSelectionMessage")); //$NON-NLS-1$
                getOkButton().setEnabled(false);
            } else {
                getOkButton().setEnabled(true);
            }
        } else {
            status = new Status(IStatus.ERROR,
                                DqpUiConstants.PLUGIN_ID,
                                DqpUiConstants.UTIL.getString("SelectTranslatorDialog.invalidSelectionMessage")); //$NON-NLS-1$
            getOkButton().setEnabled(false);
        }

        this.statusMessageLabel.setErrorStatus(status);
    }

    /**
     * Returns the current TeiidTranslator
     * 
     * @return the TeiidTranslator. may return null
     */
    public ITeiidTranslator getSelectedTranslator() {
        return this.selectedTranslator;
    }

}
