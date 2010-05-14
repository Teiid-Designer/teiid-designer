/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.viewsupport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ListDialog;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.types.DatatypeManager;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.ui.filter.StructuredViewerDatatypeFilterer;
import com.metamatrix.modeler.internal.ui.filter.StructuredViewerFilterer;
import com.metamatrix.modeler.internal.ui.filter.StructuredViewerTextFilterer;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.widget.EditableIntegerSpinner;

/**
 * DatatypeSelectionDialog
 */
public class DatatypeSelectionDialog extends ListDialog implements UiConstants {

    /** The logging prefix. */
    private static final String PREFIX = "DatatypeSelectionDialog."; //$NON-NLS-1$

    private static final String STRING_STRING = "string"; //$NON-NLS-1$
    private static final String ONE_SPACE = " "; //$NON-NLS-1$

    private static final String BUILT_IN = getString("builtInLabel"); //$NON-NLS-1$
    private static final String PATH_GROUP_TITLE = getString("pathGroupLabel"); //$NON-NLS-1$
    private static final String EMPTY_STRING = ""; //$NON-NLS-1$

    private static String getString( final String key ) {
        return UiConstants.Util.getString(PREFIX + key);
    }

    private static String getString( final String key,
                                     final Object obj ) {
        return UiConstants.Util.getString(PREFIX + key, obj);
    }

    private EditableIntegerSpinner ispin = null;
    private int MIN = 0;
    private int MAX = 99999;
    private boolean editLength = false;
    boolean setLengthForAll = false;
    private boolean multipleObjects = false;
    private int initialLength = 0;
    Button setLengthForAllCheckBox;
    CLabel pathLabel;
    private StructuredViewerDatatypeFilterer entFilter;
    private String originalMessage;

    /**
     * Construct an instance of DatatypeSelectionDialog.
     * 
     * @param parent the Shell for this dialog
     */
    public DatatypeSelectionDialog( final Shell parent ) {
        this(parent, null);
    }

    /**
     * Construct an instance of DatatypeSelectionDialog.
     * 
     * @param parent the Shell for this dialog
     * @param objectForType an EObject that should be displayed in the message for setting the type on or <code>null</code>
     */
    public DatatypeSelectionDialog( final Shell parent,
                                    final EObject objectForType ) {
        super(parent);
        setContentProvider(new IStructuredContentProvider() {
            public Object[] getElements( Object inputElement ) {
                Object[] result = new Object[0];
                try {
                    // Use the Datatype Manager that corresponds to the container where 'objectForType' exists
                    DatatypeManager dtmgr = ModelerCore.getDatatypeManager(objectForType, true);
                    // Get the list of all datatypes
                    List tmp = new ArrayList(Arrays.asList(dtmgr.getAllDatatypes()));
                    // Remove xs:anySimpleType or xs:anyType from the list
                    removeUrTypesFromList(tmp, dtmgr);
                    result = tmp.toArray();
                } catch (ModelerCoreException e) {
                    Util.log(e);
                }
                return result;
            }

            public void dispose() {
            }

            public void inputChanged( Viewer v,
                                      Object o,
                                      Object o2 ) {
            }
        });

        setLabelProvider(ModelUtilities.getEMFLabelProvider());

        setAddCancelButton(true);
        setTitle(getString("selectDatatypeTitle")); //$NON-NLS-1$

        String msg = (objectForType == null) ? getString("selectDatatypeMessageNoObject") //$NON-NLS-1$
        : getString("selectDatatypeMessage", //$NON-NLS-1$
                    objectForType.eClass().getName());
        setMessage(msg);
        setInput(Collections.EMPTY_LIST);
        initFilter();
    }

    /**
     * Construct an instance of DatatypeSelectionDialog.
     * 
     * @param parent the Shell for this dialog
     * @param objectForType an EObject that should be displayed in the message for setting the type on.
     * @param feature the EStructuralFeature representing the type
     */
    public DatatypeSelectionDialog( final Shell parent,
                                    final EObject objectForType,
                                    final EStructuralFeature feature ) {
        super(parent);
        setContentProvider(new IStructuredContentProvider() {
            public Object[] getElements( Object inputElement ) {
                Object[] result = new Object[0];
                try {
                    // Use the Datatype Manager that corresponds to the container where 'objectForType' exists
                    DatatypeManager dtmgr = ModelerCore.getDatatypeManager(objectForType, true);
                    // Get the list of all datatypes
                    List tmp = new ArrayList(Arrays.asList(dtmgr.getAllowableTypeValues(objectForType, feature)));
                    // Remove xs:anySimpleType or xs:anyType from the list
                    removeUrTypesFromList(tmp, dtmgr);
                    result = tmp.toArray();
                } catch (ModelerCoreException e) {
                    UiConstants.Util.log(e);
                }
                return result;
            }

            public void dispose() {
            }

            public void inputChanged( Viewer v,
                                      Object o,
                                      Object o2 ) {
            }
        });
        setLabelProvider(ModelUtilities.getEMFLabelProvider());
        setAddCancelButton(true);
        setTitle(getString("selectDatatypeTitle")); //$NON-NLS-1$
        setMessage(getString("selectDatatypeMessage", (objectForType).eClass().getName())); //$NON-NLS-1$
        setInput(Collections.EMPTY_LIST);
        initFilter();
    }

    /**
     * Return a new list of datatypes with the ur-types (xs:anySimpleType, xs:anyType) removed
     * 
     * @return
     * @since 4.3
     */
    void removeUrTypesFromList( final List types,
                                final DatatypeManager dtmgr ) {
        try {
            // Remove xs:anySimpleType or xs:anyType from the list
            for (Iterator i = types.iterator(); i.hasNext();) {
                EObject eObj = (EObject)i.next();
                if (dtmgr.getAnySimpleType() == eObj || dtmgr.getAnyType() == eObj) {
                    i.remove();
                }
            }
        } catch (ModelerCoreException e) {
            UiConstants.Util.log(e);
        }
    }

    /**
     * Construct an instance of DatatypeSelectionDialog. A runtimeTypeName is also supplied - it is used to narrow the selection
     * datatypes available in the dialog. Only the datatypes which are compatible with the supplied runtimetype will be shown.
     * 
     * @param parent the Shell for this dialog
     * @param objectForType an EObject that should be displayed in the message for setting the type on.
     * @param runtimeTypeName the runtimeTypeName used to narrow the selections
     */
    public DatatypeSelectionDialog( final Shell parent,
                                    final EObject objectForType,
                                    final String runtimeTypeName ) {
        super(parent);
        // modTODO: use the runtime typeName to narrow down the datatypes
        setContentProvider(new IStructuredContentProvider() {
            public Object[] getElements( Object inputElement ) {
                Object[] result = new Object[0];
                try {
                    // Use the Datatype Manager that corresponds to the container where 'objectForType' exists
                    DatatypeManager dtmgr = ModelerCore.getDatatypeManager(objectForType, true);
                    // Get the list of all datatypes
                    List tmp = new ArrayList(Arrays.asList(dtmgr.getAllDatatypes()));
                    // Remove xs:anySimpleType or xs:anyType from the list
                    removeUrTypesFromList(tmp, dtmgr);
                    result = tmp.toArray();
                } catch (ModelerCoreException e) {
                    UiConstants.Util.log(e);
                }
                return result;
            }

            public void dispose() {
            }

            public void inputChanged( Viewer v,
                                      Object o,
                                      Object o2 ) {
            }
        });
        setLabelProvider(ModelUtilities.getEMFLabelProvider());
        setAddCancelButton(true);
        setTitle(getString("selectDatatypeTitle")); //$NON-NLS-1$
        setMessage(getString("selectDatatypeMessage", (objectForType).eClass().getName())); //$NON-NLS-1$
        setInput(Collections.EMPTY_LIST);
        initFilter();
    }

    //
    // Methods:
    //
    private void initFilter() {
        entFilter = new StructuredViewerDatatypeFilterer();
        entFilter.setDelayTime(100);
        setAllowSimple(false); // default to false, since most things don't need simple types.
    }

    public void setAllowSimple( boolean allow ) {
        entFilter.setAllowSimple(allow);
        if (!allow) {
            // notify the user that some stuff is filtered:
            originalMessage = getMessage();
            setMessage(originalMessage + getString("simpleHiddenMessage")); //$NON-NLS-1$
        } else {
            if (originalMessage != null) {
                setMessage(originalMessage);
            } // endif
        } // endif
    }

    /**
     * @see org.eclipse.jface.window.Window#create()
     * @since 4.2
     */
    @Override
    public void create() {
        setShellStyle(getShellStyle() | SWT.RESIZE);
        super.create();

        // set OK button enable state to false if nothing selected
        if (getInitialElementSelections().isEmpty()) {
            getOkButton().setEnabled(false);
        }

        // setup selection listening in order to enable OK button when selection occurs
        getTableViewer().addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged( SelectionChangedEvent theEvent ) {
                getOkButton().setEnabled(!getTableViewer().getSelection().isEmpty());
                updateSpinner(getTableViewer().getSelection());

                // set path label text
                EObject eObj = SelectionUtilities.getSelectedEObject(theEvent.getSelection());
                pathLabel.setText((eObj == null) ? EMPTY_STRING : constructPath(eObj));
            }
        });

        updateSpinner(getTableViewer().getSelection());
    }

    /**
     * This implemenation does nothing, on purpose, to allow us to change the order of construction of the GUI. Yes, slightly
     * evil. See createDialogArea for how this works.
     */
    @Override
    protected Label createMessageArea( Composite composite ) {
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.window.Window#createContents(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Control createDialogArea( Composite parent ) {
        // 
        if (multipleObjects) {
            setLengthForAll = false;
        } else {
            setLengthForAll = true;
        }
        Composite composite = new Composite(parent, 0);
        GridLayout layout = new GridLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 5;
        layout.verticalSpacing = 0;
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));
        composite.setFont(parent.getFont());

        // explicitly call super's cMA, which does something:
        super.createMessageArea(composite);

        // add text filter:
        StructuredViewerFilterer textFilter = new StructuredViewerTextFilterer(StructuredViewerTextFilterer.DEFAULT_PROMPT,
                                                                               StructuredViewerTextFilterer.DEFAULT_CLEAR,
                                                                               ModelUtilities.getEMFLabelProvider());
        textFilter.setDelayTime(50);
        Control filterCtrl = textFilter.addControl(composite);
        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false);
        gd.verticalIndent = 5;
        filterCtrl.setLayoutData(gd);
        // add ent. type filter:
        filterCtrl = entFilter.addControl(composite);
        gd = new GridData(SWT.FILL, SWT.FILL, true, false);
        filterCtrl.setLayoutData(gd);
        // allow super to create stuff:
        Composite superComp = (Composite)super.createDialogArea(composite);
        layout = (GridLayout)superComp.getLayout();
        layout.marginHeight = 5;
        layout.marginWidth = 0;

        // attach the filters:
        textFilter.attachToViewer(getTableViewer(), true);
        entFilter.attachToViewer(getTableViewer(), true);

        // Added 9/27/04 to allow the SetDatatypeAction to retrieve a length value
        // for a datatype of 'string'.
        // To enable, the dialog must have the setEditLength(true) called before dialog.open()
        // and an initial length value set via setInitialLength(int). Default length = 0
        // If visible, spinner will disable for non-string types
        if (editLength) {
            Group lengthGroup = WidgetFactory.createGroup(composite, getString("lengthOptions"), //$NON-NLS-1$
                                                          GridData.FILL_HORIZONTAL,
                                                          1,
                                                          2);

            WidgetFactory.createLabel(lengthGroup, GridData.FILL, getString("stringLengthLabel")); //$NON-NLS-1$

            // int spinner
            ispin = new EditableIntegerSpinner(lengthGroup, MIN, MAX);
            ispin.setWrap(false);

            GridData gridData2 = new GridData();
            gridData2.horizontalAlignment = GridData.CENTER;
            ispin.setLayoutData(gridData2);
            ispin.setValue(initialLength);
            ispin.setEnabled(true);
            if (multipleObjects) {
                setLengthForAllCheckBox = WidgetFactory.createCheckBox(lengthGroup, getString("setLengthForAll"), //$NON-NLS-1$
                                                                       GridData.FILL,
                                                                       2,
                                                                       setLengthForAll);
                setLengthForAllCheckBox.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected( SelectionEvent ev ) {
                        setLengthForAll = setLengthForAllCheckBox.getSelection();
                    }
                });

                editLength = setLengthForAllCheckBox.getSelection();
            } else {
                setLengthForAll = true;
            }
        }

        Group group = WidgetFactory.createGroup(composite, PATH_GROUP_TITLE, GridData.FILL_BOTH);

        pathLabel = new CLabel(group, SWT.NONE);
        pathLabel.setFont(composite.getFont());
        pathLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        // initialize path label text
        List initialSelections = getInitialElementSelections();

        if ((initialSelections != null) && !initialSelections.isEmpty() && (initialSelections.get(0) instanceof EObject)) {
            this.pathLabel.setText(constructPath((EObject)initialSelections.get(0)));
        }

        return composite;
    }

    public int getLength() {
        return ispin.getIntegerValue();
    }

    public void setInitialLength( int newLength ) {
        initialLength = newLength;
    }

    /**
     * @param editLength The editLength to set.
     * @since 4.2
     */
    public void setEditLength( boolean editLength ) {
        this.editLength = editLength;
    }

    void updateSpinner( ISelection selection ) {
        // The spinner needs to be disabled when any type other than "string" is specified
        if (ispin != null) {
            boolean enable = false;
            Object result = SelectionUtilities.getSelectedObject(selection);

            if (typeIsString(result)) enable = true;

            ispin.setEnabled(enable);
            if (setLengthForAllCheckBox != null) {
                setLengthForAllCheckBox.setEnabled(enable);
            }
        }
    }

    private boolean typeIsString( Object type ) {
        if (type != null && type instanceof XSDSimpleTypeDefinition) {
            String simpleType = ModelerCore.getWorkspaceDatatypeManager().getRuntimeTypeName((EObject)type);
            if (simpleType.equalsIgnoreCase(STRING_STRING)) {
                return true;
            }
        }
        return false;
    }

    String constructPath( EObject eo ) {

        IPath result = null;
        String sPath;

        if (isBuiltin(eo)) {
            sPath = BUILT_IN + ONE_SPACE + ModelerCore.getModelEditor().getName(eo);
        } else if (ModelUtil.isXsdFile(eo.eResource())) {
            result = ModelerCore.getModelEditor().getModelRelativePath(eo);
            sPath = result.toString();
        } else {
            result = ModelerCore.getModelEditor().getModelRelativePathIncludingModel(eo);
            sPath = result.toString();
        }

        return sPath;
    }

    private boolean isBuiltin( EObject eo ) {
        boolean bResult = false;

        if (eo instanceof XSDSimpleTypeDefinition) {
            try {
                // Only care about built-in types, so just use the workspace DT Mgr ...
                if (ModelerCore.getWorkspaceDatatypeManager().isBuiltInDatatype(eo)) {
                    bResult = true;
                } else {
                    bResult = false;
                }
            } catch (Exception e) {
                bResult = false;
            }

        }
        return bResult;
    }

    /**
     * @param theMultipleObjects The multipleObjects to set.
     * @since 5.0
     */
    public void setMultipleObjects( boolean theMultipleObjects ) {
        this.multipleObjects = theMultipleObjects;
    }

    /**
     * @return Returns the editLengthEnabled.
     * @since 5.0
     */
    public boolean overrideAllLengths() {
        return this.setLengthForAll;
    }

}
