/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.xml.wizards;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import com.metamatrix.core.PluginUtil;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.modelgenerator.xml.IUiConstants;
import com.metamatrix.modeler.modelgenerator.xml.XmlImporterUiPlugin;
import com.metamatrix.modeler.schema.tools.model.schema.RootElement;
import com.metamatrix.ui.internal.viewsupport.StatusInfo;
import com.metamatrix.ui.internal.widget.accumulator.AccumulatorPanel;
import com.metamatrix.ui.internal.widget.accumulator.IAccumulatedValuesChangeListener;
import com.metamatrix.ui.internal.widget.accumulator.IAccumulatorSource;

/**
 * RootElementsPage is the wizard page contribution for building Virtual XMLDocument models from XML Schema files in the
 * workspace.
 */

public class RootElementsPage extends WizardPage implements IUiConstants, IUiConstants.HelpContexts, IUiConstants.Images {

    private RootElementsPanel panel;
    private XsdAsRelationalImportWizard wizard;
    private static PluginUtil util = XmlImporterUiPlugin.getDefault().getPluginUtil();

    /**
     * Constructor for RootElementsPage.
     * 
     * @param pageName
     */
    public RootElementsPage( XsdAsRelationalImportWizard wizard ) {
        super(RootElementsPage.class.getSimpleName());
        setTitle(util.getString("RootElementsPage.title")); //$NON-NLS-1$
        setDescription(util.getString("RootElementsPage.description")); //$NON-NLS-1$
        this.wizard = wizard;
        wizard.getStateManager();
    }

    /**
     * @see IDialogPage#createControl(Composite)
     */
    public void createControl( Composite parent ) {
        panel = new RootElementsPanel(parent, wizard);
        setControl(panel);
    }

    @Override
    public void dispose() {
        super.dispose();
        Control c = getControl();
        if (c != null) {
            c.dispose();
        }
    }
}

class RootElementsPanel extends Composite
    implements IUiConstants, IUiConstants.HelpContexts, IUiConstants.Images, IAccumulatedValuesChangeListener {
    private static PluginUtil util = XmlImporterUiPlugin.getDefault().getPluginUtil();

    private static final String MAPPING_OPTIONS = util.getString("RootElementsPage.mappingOptions"); //$NON-NLS-1$
    private static final String USE_STRING = util.getString("RootElementsPage.useString"); //$NON-NLS-1$
    private static final String USE_SCHEMA_TYPE = util.getString("RootElementsPage.useSchemaType"); //$NON-NLS-1$
    private final static int ACCUMULATOR_RESET_BUTTON_VERTICAL_MARGIN = 4;
    public final static int DOCUMENTS = 1;

    private TableViewer documentsListViewer;
    private AccumulatorPanel documentsAccumulatorPanel = null;
    private ILabelProvider accumulatorsLabelProvider;
    Button useSchemaTypeButton;
    private Button useStringTypeButton;
    XsdAsRelationalImportWizard wizard;
    private StateManager manager;
    private boolean selectionsChanged = false;

    public RootElementsPanel( Composite parent,
                              XsdAsRelationalImportWizard wizard ) {
        super(parent, SWT.NULL);
        this.wizard = wizard;
        this.manager = wizard.getStateManager();
        initialize();
    }

    private void initialize() {
        GridLayout layout = new GridLayout();
        layout.marginHeight = 0;
        this.setLayout(layout);

        accumulatorsLabelProvider = ModelUtilities.getEMFLabelProvider();

        Group documentsAccumulatorComposite = new Group(this, SWT.NONE);
        String documentsGroupName = util.getString("RootElementsPage.virtualXMLDocumentsLabel"); //$NON-NLS-1$
        documentsAccumulatorComposite.setText(documentsGroupName);
        GridLayout documentsAccumulatorCompositeLayout = new GridLayout();
        documentsAccumulatorComposite.setLayout(documentsAccumulatorCompositeLayout);
        documentsAccumulatorCompositeLayout.marginWidth = 0;
        documentsAccumulatorCompositeLayout.marginHeight = 2;
        IAccumulatorSource documentsAccumulatorSource = new RootElementAccumulatorSource(this);
        String documentsAvailableHdr = util.getString("RootElementsPage.documentsAccumulatorLeftLabel"); //$NON-NLS-1$
        String documentsSelectedHdr = util.getString("RootElementsPage.documentsAccumulatorRightLabel"); //$NON-NLS-1$
        documentsAccumulatorPanel = new AccumulatorPanel(documentsAccumulatorComposite, documentsAccumulatorSource,
                                                         new ArrayList(), accumulatorsLabelProvider, documentsAvailableHdr,
                                                         documentsSelectedHdr, ACCUMULATOR_RESET_BUTTON_VERTICAL_MARGIN, -1, -1,
                                                         -1);
        documentsAccumulatorPanel.addAccumulatedValuesChangeListener(this);

        Group typeGroup = new Group(this, SWT.NONE);
        typeGroup.setLayout(new GridLayout(1, true));
        typeGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        typeGroup.setText(MAPPING_OPTIONS);

        useStringTypeButton = new Button(typeGroup, SWT.RADIO);
        useStringTypeButton.setSelection(true);
        useStringTypeButton.setText(USE_STRING);

        useSchemaTypeButton = new Button(typeGroup, SWT.RADIO);
        useSchemaTypeButton.setSelection(false);
        useSchemaTypeButton.setText(USE_SCHEMA_TYPE);

        useSchemaTypeButton.addSelectionListener(new SelectionListener() {
            public void widgetSelected( SelectionEvent e ) {
                wizard.setUseSchemaTypes(useSchemaTypeButton.getSelection());
            }

            public void widgetDefaultSelected( SelectionEvent e ) {
            }
        });

        useStringTypeButton.addSelectionListener(new SelectionListener() {
            public void widgetSelected( SelectionEvent e ) {
                wizard.setUseSchemaTypes(useSchemaTypeButton.getSelection());
            }

            public void widgetDefaultSelected( SelectionEvent e ) {
            }
        });
    }

    @Override
    public void setVisible( boolean visible ) {
        super.setVisible(visible);
        if (visible) {

            Collection availableRoots;
            try {
                availableRoots = manager.getPotentialRoots();
            } catch (Exception e) {
                IStatus status = new Status(IStatus.ERROR, wizard.getClass().getName(), IStatus.ERROR, e.getMessage(), e);
                Shell shell = this.getShell();
                ErrorDialog.openError(shell, util.getString("RootElementsPage.exception"), e.getMessage(), status); //$NON-NLS-1$  
                return;
            }
            Collection selectedRoots = new ArrayList();
            if (availableRoots == null) {
                availableRoots = new ArrayList();
            }
            emptyList(documentsListViewer);
            Iterator iter = availableRoots.iterator();
            while (iter.hasNext()) {
                RootElement root = (RootElement)iter.next();
                if (root.isUseAsRoot()) {
                    selectedRoots.add(root);
                } else {
                    documentsListViewer.add(root);
                }
            }
            documentsAccumulatorPanel.repopulateSelectedItems(selectedRoots);
            documentsAccumulatorPanel.availableItemsHaveChanged();
        }
    }

    public void accumulatedValuesChanged( AccumulatorPanel source ) {
    }

    private void emptyList( TableViewer listViewer ) {
        Table list = listViewer.getTable();
        if (list != null) {
            int count = list.getItemCount();
            for (int i = count - 1; i >= 0; i--) {
                Object item = listViewer.getElementAt(i);
                listViewer.remove(item);
            }
        }
    }

    public void documentsAccumulatedValuesRemoved( Collection values ) {
        Iterator it = values.iterator();
        while (it.hasNext()) {
            Object value = it.next();
            documentsListViewer.add(value);
        }
    }

    public void documentsAccumulatedValuesAdded( Collection values ) {
        Iterator it = values.iterator();
        while (it.hasNext()) {
            Object value = it.next();
            documentsListViewer.remove(value);
        }
    }

    public Collection getDocumentsAvailableValues() {
        int count = documentsListViewer.getTable().getItemCount();
        ArrayList values = new ArrayList(count);
        for (int i = 0; i < count; i++) {
            Object value = documentsListViewer.getElementAt(i);
            values.add(value);
        }
        return values;
    }

    public int getDocumentsAvailableValuesCount() {
        int count = documentsListViewer.getTable().getItemCount();
        return count;
    }

    public Collection getDocumentsSelectedAvailableValues() {
        int[] selectionIndices = documentsListViewer.getTable().getSelectionIndices();
        ArrayList selectedValues = new ArrayList(selectionIndices.length);
        for (int i = 0; i < selectionIndices.length; i++) {
            int index = selectionIndices[i];
            selectedValues.add(documentsListViewer.getElementAt(index));
        }
        return selectedValues;
    }

    public int getDocumentsSelectedAvailableValuesCount() {
        if (documentsListViewer != null) {
            int count = documentsListViewer.getTable().getSelectionCount();
            return count;
        } // endif

        return 0;
    }

    public Control documentsCreateControl( Composite parent ) {
        documentsListViewer = new TableViewer(parent, SWT.MULTI);
        documentsListViewer.setLabelProvider(accumulatorsLabelProvider);
        return documentsListViewer.getControl();
    }

    public void documentsAddSelectionListener( SelectionListener listener ) {
        documentsListViewer.getTable().addSelectionListener(listener);
    }

    public void selectedDocumentsChanged() {
        Collection selectedRoots = documentsAccumulatorPanel.getSelectedItems();
        manager.setSelectedRoots(selectedRoots);
    }

    public boolean isSelectionsChanged() {
        return selectionsChanged;
    }

    public void setSelectionsChanged( boolean selectionsChanged ) {
        this.selectionsChanged = selectionsChanged;
    }
}// end RootElementsPanel

class RootElementAccumulatorSource implements IAccumulatorSource {

    private static final IStatus OK_STATUS = new StatusInfo(IUiConstants.PLUGIN_ID);

    private RootElementsPanel caller;

    public RootElementAccumulatorSource( RootElementsPanel cllr ) {
        super();
        this.caller = cllr;
    }

    public void accumulatedValuesRemoved( Collection values ) {
        caller.documentsAccumulatedValuesRemoved(values);
        caller.selectedDocumentsChanged();
    }

    public void accumulatedValuesAdded( Collection values ) {
        caller.documentsAccumulatedValuesAdded(values);
        caller.selectedDocumentsChanged();
    }

    public Collection getAvailableValues() {
        Collection values = null;
        values = caller.getDocumentsAvailableValues();
        return values;
    }

    public int getAvailableValuesCount() {
        int count = -1;
        count = caller.getDocumentsAvailableValuesCount();
        return count;
    }

    public Collection getSelectedAvailableValues() {
        Collection values = null;
        values = caller.getDocumentsSelectedAvailableValues();
        return values;
    }

    public int getSelectedAvailableValuesCount() {
        int count = -1;
        count = caller.getDocumentsSelectedAvailableValuesCount();
        return count;
    }

    public Control createControl( Composite parent ) {
        Control control = null;
        control = caller.documentsCreateControl(parent);
        return control;
    }

    public void addSelectionListener( SelectionListener listener ) {
        caller.documentsAddSelectionListener(listener);
    }

    /**
     * @see com.metamatrix.ui.internal.widget.accumulator.IAccumulatorSource#supportsAddAll()
     */
    public boolean supportsAddAll() {
        return true;
    }

    /**
     * @see com.metamatrix.ui.internal.widget.accumulator.IAccumulatorSource#getSelectionStatus()
     */
    public IStatus getSelectionStatus() {
        return OK_STATUS;
    }
}
