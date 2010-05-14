/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.viewsupport;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.viewers.deferred.AbstractConcurrentModel;
import org.eclipse.jface.viewers.deferred.DeferredContentProvider;
import org.eclipse.jface.viewers.deferred.IConcurrentModelListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ListDialog;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.internal.core.workspace.WorkspaceResourceFinderUtil;
import com.metamatrix.modeler.internal.ui.filter.StructuredViewerTextFilterer;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;

/**
 * ModelObjectListDialog is a SelectionDialog for displaying and selecting a list of EObjects.
 */
public class ModelObjectListDialog extends ListDialog implements IFilter.IConstants, ISelectionChangedListener {

    private static final String EMPTY_STRING = ""; //$NON-NLS-1$

    /** Properties key prefix. */
    private static final String PREFIX = I18nUtil.getPropertyPrefix(ModelObjectListDialog.class);
    private static final String MESSAGE_ID = PREFIX + "selectMessage"; //$NON-NLS-1$
    private static final String BUTTON_NULL = UiConstants.Util.getString(PREFIX + "button.nullValue"); //$NON-NLS-1$
    private static final String GROUP_SELECTED = UiConstants.Util.getString(PREFIX + "groupSelected"); //$NON-NLS-1$
    private static final String LABEL_NAME = UiConstants.Util.getString(PREFIX + "labelName"); //$NON-NLS-1$
    private static final String LABEL_PATH = UiConstants.Util.getString(PREFIX + "labelPath"); //$NON-NLS-1$
    private static final String LOADING_TABLE_MSG = UiConstants.Util.getString(PREFIX + "loadingTable"); //$NON-NLS-1$
    static final Object[] LOADING_TABLE_ARRAY = new Object[] {LOADING_TABLE_MSG};

    /** An empty array signifying that the property should be set to <code>null</code>. */
    static final Object[] NULL_VALUE = new Object[0];

    /** Cached OK button needed for enabling/disabling. */
    private Button btnOk;

    /** Checkbox allowing value to be set to null. */
    private Button btnNullValue;

    private CLabel nameLabel;
    private CLabel pathLabel;

    /** Property indicating that the null value checkbox should be shown. */
    private boolean showNullValueAssigner = true;

    /** Indicates if the null value checkbox is selected. */
    private boolean nullValue = false;

    private boolean virtual;

    private StructuredViewerTextFilterer filter;

    private IFilter contentFilter = PASSING_FILTER;

    /**
     * Construct an instance of ModelObjectListDialog.
     * 
     * @param parent
     */
    public ModelObjectListDialog( Shell parent,
                                  ILabelProvider labelProvider ) {
        this(parent, labelProvider, true, false);
    }

    /**
     * Constructs a <code>ModelObjectListDialog</code>.
     * 
     * @param theParent the parent container
     * @param theLabelProvider the list label provider
     * @param theShowNullValueAssignerFlag the flag indicating if the set to null checkbox should be shown
     * @since 4.2
     */
    public ModelObjectListDialog( Shell theParent,
                                  ILabelProvider theLabelProvider,
                                  boolean theShowNullValueAssignerFlag,
                                  boolean virtual ) {
        super(theParent);
        this.virtual = virtual;
        ILabelProvider cached = new TextCachingLabelProvider(theLabelProvider);

        if (virtual) {
            // set an empty provider for now:
            setContentProvider(new IStructuredContentProvider() {
                public Object[] getElements( Object inputElement ) {
                    return NULL_VALUE;
                }

                public void dispose() {
                }

                public void inputChanged( Viewer viewer,
                                          Object oldInput,
                                          Object newInput ) {
                }
            });
        } else {
            // not virtual:
            setContentProvider(new ContentProvider(cached));
        } // endif

        setLabelProvider(cached);
        setShowNullValueAssigner(theShowNullValueAssignerFlag);
        setShellStyle(getShellStyle() | SWT.RESIZE);
    }

    public void setFeatureName( String name ) {
        setTitle(name);
        setMessage(UiConstants.Util.getString(MESSAGE_ID, name));
    }

    /**
     * Shows/hides the set value to <code>null</code> checkbox.
     * 
     * @param theShowFlag the flag indicating if the checkbox should be shown
     * @since 4.2
     */
    public void setShowNullValueAssigner( boolean theShowFlag ) {
        this.showNullValueAssigner = theShowFlag;
    }

    /**
     * Indicates if the set value to <code>null</code> checkbox is being shown.
     * 
     * @return <code>true</code>if being shown; <code>false</code> otherwise.
     * @since 4.2
     */
    public boolean isShowNullValueAssigner() {
        return this.showNullValueAssigner;
    }

    @Override
    protected int getTableStyle() {
        int tableStyle = super.getTableStyle();
        if (virtual) {
            tableStyle |= SWT.VIRTUAL; // add virtual attribute
        } // endif

        return tableStyle;
    }

    /**
     * Returns an empty array if the set to <code>null</code> checkbox is selected.
     * 
     * @see org.eclipse.ui.dialogs.SelectionDialog#getResult()
     * @since 4.2
     */
    @Override
    public Object[] getResult() {
        if (this.nullValue) {
            return NULL_VALUE;
        }

        return super.getResult();
    }

    @Override
    protected Label createMessageArea( Composite composite ) {
        Label l = super.createMessageArea(composite);
        Control filtCtrl = filter.addControl(composite);
        filtCtrl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        return l;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.window.Window#createContents(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Control createDialogArea( Composite parent ) {
        // get the filter ready to be added (in createMessageArea, above):
        filter = new StructuredViewerTextFilterer(StructuredViewerTextFilterer.DEFAULT_PROMPT,
                                                  StructuredViewerTextFilterer.DEFAULT_CLEAR);

        Composite composite = (Composite)super.createDialogArea(parent);

        // only create checkbox if property is set
        if (isShowNullValueAssigner()) {
            btnNullValue = new Button(composite, SWT.CHECK);
            btnNullValue.setText(BUTTON_NULL);
            btnNullValue.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected( SelectionEvent theEvent ) {
                    handleNullValueSelected();
                }
            });
        }

        Group selectedInfoGroup = new Group(composite, SWT.NONE);
        selectedInfoGroup.setText(GROUP_SELECTED);
        GridLayout gridLayout = new GridLayout(2, false);
        gridLayout.verticalSpacing = 1;
        gridLayout.marginHeight = 2;
        selectedInfoGroup.setLayout(gridLayout);
        selectedInfoGroup.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
        Label l = new Label(selectedInfoGroup, SWT.NONE);
        l.setText(LABEL_NAME);
        nameLabel = new CLabel(selectedInfoGroup, SWT.NONE);
        nameLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        l = new Label(selectedInfoGroup, SWT.NONE);
        l.setText(LABEL_PATH);
        pathLabel = new CLabel(selectedInfoGroup, SWT.NONE);
        pathLabel.setFont(composite.getFont());
        pathLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        // initialize path label text
        List initialSelections = getInitialElementSelections();

        if ((initialSelections != null) && !initialSelections.isEmpty() && (initialSelections.get(0) instanceof EObject)) {
            EObject e = (EObject)initialSelections.get(0);
            nameLabel.setText(constructName(e));
            pathLabel.setText(constructPath(e));
        }

        final TableViewer tableViewer = getTableViewer();
        ILabelProvider labelProvider = (ILabelProvider)tableViewer.getLabelProvider(); // this should be the Cached one
        filter.setLabelProvider(labelProvider);
        tableViewer.addSelectionChangedListener(this);

        if (virtual) {
            // set up virtual stuff:
            // replace the input object with our own ConcurrentModel:
            Object input = tableViewer.getInput();
            Object selected;
            if (initialSelections != null && !initialSelections.isEmpty()) {
                selected = initialSelections.get(0);
            } else {
                selected = null;
            } // endif

            ContentProvider content = new ContentProvider(input, selected, labelProvider);
            tableViewer.setInput(content);
            // replace the (temporary) contentProvider with a deferred provider
            // and our simple string sorter:
            final DeferredContentProvider dcp = new DeferredContentProvider(content);
            tableViewer.setContentProvider(dcp);
            // attach filter to this:
            filter.attachToVirtualViewer(tableViewer, dcp, true);

            // set up selector if needed:
            if (selected != null) {
                final StructuredSelection ss = new StructuredSelection(selected);
                final Timer t = new Timer(true);
                TimerTask tt = new TimerTask() {
                    @Override
                    public void run() {
                        Display.getDefault().syncExec(new Runnable() {
                            public void run() {
                                if (tableViewer.getTable().isDisposed() || !tableViewer.getSelection().isEmpty()) {
                                    // success, or the user changed selection
                                    t.cancel();

                                } else {
                                    // try again:
                                    tableViewer.setSelection(ss, true);
                                } // endif
                            }
                        }); // endanon asyncExec
                    }
                }; // endanon TimerTask

                // schedule to run in 1/2 second, and every 1/2 second thereafter until it succeeds:
                t.schedule(tt, 500, 500);
            } // endif -- selected not null

        } else {
            // not virtual, do things the standard way:
            filter.attachToViewer(tableViewer, true);
            tableViewer.setSorter(new ViewerSorter());
        } // endif

        return composite;
    }

    /**
     * @see org.eclipse.jface.dialogs.Dialog#createButton(org.eclipse.swt.widgets.Composite, int, java.lang.String, boolean)
     * @since 4.2
     */
    @Override
    protected Button createButton( Composite theParent,
                                   int theId,
                                   String theLabel,
                                   boolean theDefaultButton ) {
        Button btn = super.createButton(theParent, theId, theLabel, theDefaultButton);

        // cache OK button to enable/disable initially based on table selection
        if (theId == IDialogConstants.OK_ID) {
            this.btnOk = btn;
            this.btnOk.setEnabled(!getTableViewer().getSelection().isEmpty());
        }

        return btn;
    }

    /**
     * Handler for when the set value to <code>null</code> checkbox is selected/deselected.
     * 
     * @since 4.2
     */
    void handleNullValueSelected() {
        if (isShowNullValueAssigner()) {
            boolean selected = this.btnNullValue.getSelection();
            getTableViewer().getTable().setEnabled(!selected);
            this.btnOk.setEnabled(selected);
            this.nullValue = selected;

            if (selected) {
                this.nameLabel.setText(EMPTY_STRING);
                this.pathLabel.setText(EMPTY_STRING);
            } else {
                selectionChanged(new SelectionChangedEvent(getTableViewer(), getTableViewer().getSelection()));
            }
        }
    }

    private String constructPath( EObject e ) {
        IPath result = ModelerCore.getModelEditor().getModelRelativePathIncludingModel(e);
        return result.toString();
    }

    private String constructName( EObject e ) {
        return ModelerCore.getModelEditor().getName(e);
    }

    /**
     * @return The filter used to determine which objects will be included in the displayed list; Never null.
     * @since 4.3
     */
    public IFilter getContentFilter() {
        return this.contentFilter;
    }

    /**
     * @param filter The filter used to determine which objects will be included in the displayed list; A null value will apply a
     *        filter that passes all objects.
     * @since 4.3
     */
    public void setContentFilter( final IFilter filter ) {
        this.contentFilter = (filter == null ? PASSING_FILTER : filter);
    }

    // ==========================================================
    // ISelectionChangedListener methods

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
     */
    public void selectionChanged( SelectionChangedEvent event ) {
        // set path label text
        Object obj = SelectionUtilities.getSelectedObject(event.getSelection());
        if (obj instanceof EObject) {
            EObject eObj = (EObject)obj;
            // something is selected:
            nameLabel.setText(constructName(eObj));
            pathLabel.setText(constructPath(eObj));

            // set OK enabled state
            this.btnOk.setEnabled(true);
        } else if (obj != null) {
            nameLabel.setText(obj.toString());
            pathLabel.setText(EMPTY_STRING);
            this.btnOk.setEnabled(true);
        } else {
            this.btnOk.setEnabled(false);
        }
    }

    /**
     * This class is a content provider in regular and virtual models.
     */
    private class ContentProvider extends AbstractConcurrentModel implements IStructuredContentProvider, Comparator {
        // Instance variables:
        private Map elementsCache = new HashMap();
        final Object input;
        private final ILabelProvider labelProvider;
        boolean isRunningUpdate;
        private final Object selected;

        public ContentProvider( ILabelProvider labelProvider ) {
            this(null, null, labelProvider);
        }

        public ContentProvider( Object input,
                                Object selected,
                                ILabelProvider labelProvider ) {
            this.input = input;
            this.selected = selected;
            this.labelProvider = labelProvider;
        }

        public void inputChanged( Viewer viewer,
                                  Object oldInput,
                                  Object newInput ) {
        }

        public void dispose() {
        }

        public Object[] getElements( Object inputElement ) {
            // bail out if no input
            if (inputElement == null) {
                return null;
            }

            // cache items:
            Object[] rv = (Object[])elementsCache.get(inputElement);
            if (rv == null) {
                // need to create:
                Object[] inputElements = null;

                // get the input as an object array
                if (inputElement instanceof Object[]) {
                    inputElements = (Object[])inputElement;
                } else if (inputElement instanceof Collection) {
                    inputElements = processInputs((Collection)inputElement).toArray();
                } else {
                    return null;
                }

                List resourcesInModelContainer = null;
                // get the set of workspaces resources that are in open projects
                try {
                    resourcesInModelContainer = ModelerCore.getModelContainer().getResources();
                } catch (CoreException ce) {
                    ModelerCore.Util.log(ce);
                }
                HashSet hsEmfResources = new HashSet(resourcesInModelContainer);

                ArrayList arylResultElements = new ArrayList(inputElements.length);

                // qualify the elements against the list of resources in open projects
                for (int i = 0; i < inputElements.length; i++) {
                    Object inputObject = inputElements[i];
                    if (inputObject instanceof EObject) {
                        EObject eoTemp = (EObject)inputObject;
                        Resource res = eoTemp.eResource();

                        // handle the 'proxy' case
                        if (res == null && eoTemp.eIsProxy()) {
                            URI uri = ((EObjectImpl)eoTemp).eProxyURI();

                            if (resourceSetContains(uri, hsEmfResources)) {
                                arylResultElements.add(eoTemp);
                            }
                        } else
                        // add if global resource type
                        if (res != null && res.getURI() != null && res.getURI().toString() != null
                            && WorkspaceResourceFinderUtil.isGlobalResource(res.getURI().toString())) {
                            arylResultElements.add(eoTemp);
                        } else
                        // add if in open project
                        if (hsEmfResources.contains(res)) {
                            arylResultElements.add(eoTemp);
                        }
                    } else {
                        arylResultElements.add(inputObject);
                    }
                }
                // return the result as an object array
                rv = arylResultElements.toArray();

                // save in cache:
                elementsCache.put(inputElement, rv);
            } // endif

            return rv;
        }

        private Collection processInputs( Collection inputs ) {
            Collection result = new ArrayList(inputs.size());
            final IFilter filter = getContentFilter();
            for (Iterator iter = inputs.iterator(); iter.hasNext();) {
                final Object obj = iter.next();
                if (filter.passes(obj)) {
                    result.add(obj);
                }
            }
            return result;
        }

        private boolean resourceSetContains( URI uri,
                                             HashSet hsResources ) {
            Iterator it = hsResources.iterator();

            while (it.hasNext()) {
                Resource res = (Resource)it.next();

                if (res.getURI().equals(uri)) {
                    //                    System.out.println("[ModelObjectListDialog.resourceSetContains] About to return TRUE for: " + uri.path() ); //$NON-NLS-1$
                    return true;
                }
            }
            //            System.out.println("[ModelObjectListDialog.resourceSetContains] About to return FALSE for: " + uri.path() ); //$NON-NLS-1$        return false;
            return false;
        }

        // implementation of AbstractConcurrentModel methods:
        public void requestUpdate( final IConcurrentModelListener listener ) {
            // Note: the running flag may be more trouble than it's worth.
            // erratic typing can cause the filtered results to not be correct.
            if (!isRunningUpdate) {
                isRunningUpdate = true;
                listener.setContents(LOADING_TABLE_ARRAY);
                Thread runThread = new Thread("ModelObjectListDialog content update") { //$NON-NLS-1$
                    @Override
                    public void run() {
                        Object[] elements = getElements(input);
                        if (!getTableViewer().getTable().isDisposed()) {
                            listener.setContents(elements);
                        } // endif
                        isRunningUpdate = false;
                    }
                };
                runThread.setPriority(Thread.NORM_PRIORITY - 1);
                runThread.start();
            } // endif
        }

        // implementation of Comparator methods:
        public int compare( Object o1,
                            Object o2 ) {
            // percolate selected to top:
            if (o1 == selected) {
                // selected is always smaller than o2:
                return -1;

            } else if (o2 == selected) {
                // selected is always smaller than o1:
                return 1;
            } // endif

            String s1 = labelProvider.getText(o1);
            String s2 = labelProvider.getText(o2);

            return s1.compareToIgnoreCase(s2);
        }
    } // endclass ContentProvider
}
