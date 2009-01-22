/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.wizards;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.types.DatatypeManager;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.ui.internal.InternalUiConstants;
import com.metamatrix.ui.internal.viewsupport.StatusInfo;
import com.metamatrix.ui.internal.widget.accumulator.AccumulatorPanel;
import com.metamatrix.ui.internal.widget.accumulator.IAccumulatedValuesChangeListener;
import com.metamatrix.ui.internal.widget.accumulator.IAccumulatorSource;

/**
 * This page is used to capture the user preferences for Generation of XSD from Relational objects.
 */

public class ConvertEnterpriseTypesPage extends WizardPage
    implements InternalUiConstants.Widgets, UiConstants, IAccumulatedValuesChangeListener {

    private final DatatypeManager dtMgr = ModelerCore.getWorkspaceDatatypeManager();
    private final String leftHeader = Util.getString("ConvertEnterpriseTypesPage.leftHeader"); //$NON-NLS-1$
    private final String rightHeader = Util.getString("ConvertEnterpriseTypesPage.rightHeader"); //$NON-NLS-1$
    private final Collection nonEnterpriseTypes = new HashSet();

    private final ILabelProvider accumulatorLabelProvider = ModelUtilities.getEMFLabelProvider();

    private TableViewer typeViewer;
    private AccumulatorPanel panel;
    private Collection typesToConvert;

    /**
     * Constructor for NewModelWizardSpecifyModelPage
     * 
     * @param The current ISelection selection
     */
    public ConvertEnterpriseTypesPage( Resource rsrc ) {
        super("typesPage"); //$NON-NLS-1$
        setTitle(Util.getString("ConvertEnterpriseTypesPage.title")); //$NON-NLS-1$
        setDescription(Util.getString("ConvertEnterpriseTypesPage.desc")); //$NON-NLS-1$

        initialize(rsrc);
    }

    /**
     * @see IDialogPage#createControl(Composite)
     */
    public void createControl( Composite parent ) {
        Composite container = new Composite(parent, SWT.NULL);

        GridLayout layout = new GridLayout();
        container.setLayout(layout);

        Composite topComposite = new Composite(container, SWT.NULL);
        GridData topCompositeGridData = new GridData(GridData.FILL_VERTICAL);
        topComposite.setLayoutData(topCompositeGridData);
        GridLayout topLayout = new GridLayout();
        topLayout.numColumns = 2;
        topComposite.setLayout(topLayout);

        final EnterpriseTypeAccumulatorSource source = new EnterpriseTypeAccumulatorSource(this, topComposite);
        panel = new AccumulatorPanel(topComposite, source, new ArrayList(), accumulatorLabelProvider, leftHeader, rightHeader);
        panel.addAccumulatedValuesChangeListener(this);
        setControl(container);
        typeViewer.add(nonEnterpriseTypes.toArray());
        checkStatus();
    }

    public void accumulatedValuesChanged( AccumulatorPanel source ) {
        typesToConvert = source.getItemsMovedToSelected();
        checkStatus();
    }

    public Collection getTypesToConvert() {
        if (this.typesToConvert == null) {
            return Collections.EMPTY_LIST;
        }

        return typesToConvert;
    }

    /**
     * Tests if the current workbench selection is a suitable container to use. All selections must be Relational (Virtual or
     * Physical). All Tables and Procedure Results within the selection are added to the Collection of root objects to use for
     * building.
     */
    private void initialize( Resource rsrc ) {
        if (rsrc != null) {
            addNonEnterpriseTypes(rsrc);
        }
    }

    private void addNonEnterpriseTypes( final Resource xsdRsrc ) {
        final Iterator eObjects = xsdRsrc.getContents().iterator();
        while (eObjects.hasNext()) {
            final Object next = eObjects.next();
            if (next instanceof XSDSchema) {
                final Iterator children = ((XSDSchema)next).eContents().iterator();
                while (children.hasNext()) {
                    final Object child = children.next();
                    if (child instanceof XSDSimpleTypeDefinition && !dtMgr.isEnterpriseDatatype((EObject)child)) {
                        nonEnterpriseTypes.add(child);
                    }

                }
            }
        }
    }

    /**
     * If genOut is selected the user must supply a model name. If genInput is selected the user must supply a model name. User
     * must select at least one of genOut or genInput
     */
    private void checkStatus() {
        if (nonEnterpriseTypes == null || nonEnterpriseTypes.isEmpty()) {
            setMessage(Util.getString("ConvertEnterpriseTypesPage.none"), IMessageProvider.ERROR); //$NON-NLS-1$           
            setPageComplete(false);
            return;
        } else if (typesToConvert == null || typesToConvert.isEmpty()) {
            setMessage(Util.getString("ConvertEnterpriseTypesPage.noneSelected"), IMessageProvider.ERROR); //$NON-NLS-1$           
            setPageComplete(false);
            return;

        }

        setMessage(Util.getString("ConvertEnterpriseTypesPage.done"), IMessageProvider.NONE); //$NON-NLS-1$
        setPageComplete(true);
    }

    void typesToConvertChanged() {

    }

    void typesToConvertRemoved( final Collection values ) {
        Iterator it = values.iterator();
        while (it.hasNext()) {
            Object value = it.next();
            typeViewer.add(value);
        }
    }

    void typesToConvertAdded( final Collection values ) {
        Iterator it = values.iterator();
        while (it.hasNext()) {
            Object value = it.next();
            typeViewer.remove(value);
        }
    }

    Collection getAvailableTypes() {
        final int count = typeViewer.getTable().getItemCount();
        final ArrayList values = new ArrayList(count);
        for (int i = 0; i < count; i++) {
            Object value = typeViewer.getElementAt(i);
            values.add(value);
        }
        return values;
    }

    int getAvailableTypesCount() {
        return typeViewer.getTable().getItemCount();
    }

    Collection getSelectedAvailableTypes() {
        final int[] selectionIndices = typeViewer.getTable().getSelectionIndices();
        final ArrayList selectedValues = new ArrayList(selectionIndices.length);
        for (int i = 0; i < selectionIndices.length; i++) {
            final int index = selectionIndices[i];
            selectedValues.add(typeViewer.getElementAt(index));
        }
        return selectedValues;
    }

    int getSelectedAvailableTypeCount() {
        if (typeViewer != null) {
            return typeViewer.getTable().getSelectionCount();
        } // endif

        return 0;
    }

    Control createTypeControl( final Composite parent ) {
        typeViewer = new TableViewer(parent, SWT.MULTI);
        typeViewer.setLabelProvider(accumulatorLabelProvider);
        return typeViewer.getControl();
    }

    void addTypeSelectionListener( final SelectionListener listener ) {
        typeViewer.getTable().addSelectionListener(listener);
    }

    class EnterpriseTypeAccumulatorSource implements IAccumulatorSource {
        private final IStatus OK_STATUS = new StatusInfo(UiConstants.PLUGIN_ID);
        private final ConvertEnterpriseTypesPage caller;

        TableViewer viewer;

        public EnterpriseTypeAccumulatorSource( ConvertEnterpriseTypesPage cllr,
                                                Composite parent ) {
            super();
            this.caller = cllr;
        }

        public void accumulatedValuesRemoved( Collection values ) {
            caller.typesToConvertRemoved(values);
            caller.typesToConvertChanged();
        }

        public void accumulatedValuesAdded( Collection values ) {
            caller.typesToConvertAdded(values);
            caller.typesToConvertChanged();
        }

        public Collection getAvailableValues() {
            return caller.getAvailableTypes();
        }

        public int getAvailableValuesCount() {
            return caller.getAvailableTypesCount();
        }

        public Collection getSelectedAvailableValues() {
            return caller.getSelectedAvailableTypes();
        }

        public int getSelectedAvailableValuesCount() {
            return caller.getSelectedAvailableTypeCount();
        }

        public Control createControl( Composite parent ) {
            return caller.createTypeControl(parent);
        }

        public void addSelectionListener( SelectionListener listener ) {
            caller.addTypeSelectionListener(listener);
        }

        /* (non-Javadoc)
         * @see com.metamatrix.ui.internal.widget.accumulator.IAccumulatorSource#supportsAddAll()
         */
        public boolean supportsAddAll() {
            return true;
        }

        /* (non-Javadoc)
         * @see com.metamatrix.ui.internal.widget.accumulator.IAccumulatorSource#getSelectionStatus()
         */
        public IStatus getSelectionStatus() {
            return OK_STATUS;
        }
    }

}
