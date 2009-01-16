/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */
package com.metamatrix.modeler.internal.dqp.ui.views;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.part.ViewPart;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.jdbc.api.PlanNode;
import com.metamatrix.jdbc.api.XMLOutputVisitor;
import com.metamatrix.jdbc.api.tools.QueryPlanDisplayHelper;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.ui.internal.dialog.TextAreaDialog;
import com.metamatrix.ui.internal.util.SystemClipboardUtilities;

/**
 * A panel that contains query results and the query plan.
 * 
 * @since 4.3
 */
public class ResultsPanel extends Composite implements DqpUiConstants {

    private static String PREFIX = I18nUtil.getPropertyPrefix(ResultsPanel.class);

    static Object[] NO_KIDS = new Object[0];

    static String BEGIN_DELIM = "${"; //$NON-NLS-1$

    static String END_DELIM = "}"; //$NON-NLS-1$

    public static int ID_RESULTS_VIEW = 0;

    public static int ID_QUERY_PLAN_DOCUMENT_VIEW = 1;

    public static int ID_QUERY_PLAN_TREE_VIEW = 2;

    public static int ID_DEBUG_LOG = 3;

    private final ViewPart pnlResults;

    private Control detailsControl;

    private Group grpDebugLog;

    private Group grpQueryPlanDocument;

    private Group grpQueryPlanTree;

    private SashForm mainSplitter;

    private SashForm queryPlanSplitter;

    private SashForm treeViewSplitter;

    private StyledText txtDebugLog;

    private StyledText txtQueryPlan;

    private String debugLog;

    private PlanNode queryPlan;

    private TreeViewer treeViewer;

    private TreeViewer propertiesViewer;

    private QueryPlanProvider provider = new QueryPlanProvider(false);

    public ResultsPanel( ViewPart theView,
                         Composite theParent ) {
        super(theParent, SWT.NONE);
        this.pnlResults = theView;
        setLayout(new GridLayout());
        setLayoutData(new GridData(GridData.FILL_BOTH));

        // main splitter shows either results or query plan
        this.mainSplitter = new SashForm(this, SWT.VERTICAL);
        this.mainSplitter.setLayout(new GridLayout());
        this.mainSplitter.setLayoutData(new GridData(GridData.FILL_BOTH));

        // query splitter shows either document view or tree view
        this.queryPlanSplitter = new SashForm(this.mainSplitter, SWT.VERTICAL);
        this.queryPlanSplitter.setLayout(new GridLayout());
        this.queryPlanSplitter.setLayoutData(new GridData(GridData.FILL_BOTH));
        this.queryPlanSplitter.setVisible(false);

        this.grpQueryPlanDocument = new Group(this.queryPlanSplitter, SWT.NONE);
        this.grpQueryPlanDocument.setLayout(new GridLayout());
        this.grpQueryPlanDocument.setLayoutData(new GridData(GridData.FILL_BOTH));
        this.grpQueryPlanDocument.setText(UTIL.getStringOrKey(PREFIX + "grpQueryPlanDocument")); //$NON-NLS-1$
        this.grpQueryPlanDocument.setVisible(false);

        // tree view splitter always shows both tree and properties
        this.treeViewSplitter = new SashForm(this.queryPlanSplitter, SWT.VERTICAL);
        this.treeViewSplitter.setLayout(new GridLayout());
        this.treeViewSplitter.setLayoutData(new GridData(GridData.FILL_BOTH));
        this.treeViewSplitter.setVisible(false);

        this.grpQueryPlanTree = new Group(this.treeViewSplitter, SWT.NONE);
        this.grpQueryPlanTree.setLayout(new GridLayout());
        this.grpQueryPlanTree.setLayoutData(new GridData(GridData.FILL_BOTH));
        this.grpQueryPlanTree.setText(UTIL.getStringOrKey(PREFIX + "grpQueryPlanTree")); //$NON-NLS-1$

        this.treeViewer = new TreeViewer(this.grpQueryPlanTree, SWT.H_SCROLL | SWT.V_SCROLL | SWT.SINGLE | SWT.BORDER);
        this.treeViewer.setContentProvider(provider);
        this.treeViewer.setLabelProvider(provider);
        this.treeViewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
        this.treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged( SelectionChangedEvent theEvent ) {
                handlePlanNodeSelected();
            }
        });
        this.treeViewer.addDoubleClickListener(new IDoubleClickListener() {
            public void doubleClick( DoubleClickEvent theEvent ) {
                handlePlanNodeDoubleClicked();
            }
        });

        Group grpProps = new Group(this.treeViewSplitter, SWT.NONE);
        grpProps.setLayout(new GridLayout());
        grpProps.setLayoutData(new GridData(GridData.FILL_BOTH));
        grpProps.setText(UTIL.getStringOrKey(PREFIX + "grpProps")); //$NON-NLS-1$

        this.propertiesViewer = new TreeViewer(grpProps, SWT.H_SCROLL | SWT.V_SCROLL | SWT.SINGLE | SWT.BORDER);
        provider = new QueryPlanProvider(true);
        this.propertiesViewer.setContentProvider(provider);
        this.propertiesViewer.setLabelProvider(provider);
        this.propertiesViewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));

        // debug log
        this.grpDebugLog = new Group(this.mainSplitter, SWT.NONE);
        this.grpDebugLog.setLayout(new GridLayout());
        this.grpDebugLog.setLayoutData(new GridData(GridData.FILL_BOTH));
        this.grpDebugLog.setText(UTIL.getStringOrKey(PREFIX + "grpDebugLog")); //$NON-NLS-1$
        this.grpDebugLog.setVisible(false);
    }

    /**
     * Obtains the <code>Control</code> that contains the results.
     * 
     * @return the results control
     */
    public Control getResultsDetailControl() {
        return this.detailsControl;
    }

    /**
     * Obtains the parent of the panel containing the results.
     * 
     * @return the results control parent
     * @since 4.3
     */
    public Composite getResultsDetailParent() {
        return this.mainSplitter;
    }

    /**
     * Obtain the view identifier for the currently showing details.
     * 
     * @return the details view ID
     * @since 4.3
     * @see #ID_QUERY_PLAN_DOCUMENT_VIEW
     * @see #ID_QUERY_PLAN_TREE_VIEW
     * @see #ID_RESULTS_VIEW
     */
    public int getDetailsViewlId() {
        int result = -1;

        if (this.detailsControl.isVisible()) {
            result = ID_RESULTS_VIEW;
        } else if (this.grpQueryPlanTree.isVisible()) {
            result = ID_QUERY_PLAN_TREE_VIEW;
        } else if (this.grpQueryPlanDocument.isVisible()) {
            result = ID_QUERY_PLAN_DOCUMENT_VIEW;
        }

        return result;
    }

    /**
     * Copies the debug log to the clipboard.
     * 
     * @since 5.0.2
     */
    public void copyDebugLogToClipboard() {
        if (this.txtDebugLog != null) {
            final String log = this.debugLog;

            if (log.length() != 0) {
                Runnable copyOperation = new Runnable() {
                    public void run() {
                        SystemClipboardUtilities.setContents(log);
                    }
                };

                // show busy cursor while copying
                BusyIndicator.showWhile(getShell().getDisplay(), copyOperation);
            }
        }
    }

    /**
     * Copies the query plan document to the clipboard.
     * 
     * @since 4.3
     */
    public void copyQueryPlanDocumentToClipboard() {
        if (this.txtQueryPlan != null) {
            final String planText = this.txtQueryPlan.getText();

            if (planText.length() != 0) {
                Runnable copyOperation = new Runnable() {
                    public void run() {
                        SystemClipboardUtilities.setContents(planText);
                    }
                };

                // show busy cursor while copying
                BusyIndicator.showWhile(getShell().getDisplay(), copyOperation);
            }
        }
    }

    void handlePlanNodeSelected() {
        Object input = null;
        IStructuredSelection selection = (IStructuredSelection)this.treeViewer.getSelection();

        if (!selection.isEmpty()) {
            input = selection.getFirstElement();
        }

        // load the properties viewer with the selected model object from the plan node viewer
        this.propertiesViewer.setInput(input);
    }

    void handlePlanNodeDoubleClicked() {
        Object input = null;
        IStructuredSelection selection = (IStructuredSelection)this.treeViewer.getSelection();

        if (!selection.isEmpty()) {
            input = selection.getFirstElement();
        }

        String text = getProvider().getText(input);
        String headerText = UTIL.getStringOrKey(PREFIX + "selectedQueryPlanNode"); //$NON-NLS-1$
        TextAreaDialog dialog = new TextAreaDialog(Display.getCurrent().getActiveShell(),
                                                   UTIL.getStringOrKey(PREFIX + "queryPlanNodeViewerTitle"), //$NON-NLS-1$
                                                   text, headerText);
        dialog.setWordWrap(true);
        dialog.open();

    }

    /**
     * @return QueryPlanProvider
     */
    public QueryPlanProvider getProvider() {
        return provider;
    }

    /**
     * Indicates if this results panel has a debug log.
     * 
     * @return <code>true</code> if has a debug log; <code>false</code> otherwise.
     * @since 5.0.2
     */
    public boolean hasDebugLog() {
        return (this.debugLog != null);
    }

    /**
     * Indicates if this results panel has a query plan.
     * 
     * @return <code>true</code> if has a query plan; <code>false</code> otherwise.
     * @since 4.3
     */
    public boolean hasQueryPlan() {
        return (this.queryPlan != null);
    }

    /**
     * Sets the debug log.
     * 
     * @param theLog the log
     * @since 5.0.2
     */
    public void setDebugLog( String theLog ) {
        this.debugLog = theLog;

        if (this.debugLog != null) {
            if (this.txtDebugLog == null) {
                this.txtDebugLog = new StyledText(this.grpDebugLog, SWT.READ_ONLY | SWT.MULTI | SWT.BORDER | SWT.H_SCROLL
                                                                    | SWT.V_SCROLL);
                this.txtDebugLog.setLayoutData(new GridData(GridData.FILL_BOTH));
            }

            this.txtDebugLog.setText(this.debugLog);
        } else if (this.txtDebugLog != null) {
            // clear out existing text
            this.txtDebugLog.setText(""); //$NON-NLS-1$
        }
    }

    /**
     * Sets the <code>Control</code> that displays the results.
     * 
     * @param theControl the control
     * @since 4.3
     */
    public void setResultsDetailControl( Control theControl ) {
        this.detailsControl = theControl;
    }

    /**
     * Sets the query plan that will be shown.
     * 
     * @param theQueryPlan the query plan or <code>null</code> if no query plan should be shown
     * @since 4.3
     */
    public void setQueryPlan( PlanNode theQueryPlan ) {
        this.queryPlan = theQueryPlan;

        if (this.queryPlan != null) {
            // document version of query plan
            if (this.txtQueryPlan == null) {
                this.txtQueryPlan = new StyledText(this.grpQueryPlanDocument, SWT.READ_ONLY | SWT.MULTI | SWT.BORDER
                                                                              | SWT.H_SCROLL | SWT.V_SCROLL);
                this.txtQueryPlan.setLayoutData(new GridData(GridData.FILL_BOTH));
            }

            QueryPlanDisplayHelper helper = new QueryPlanDisplayHelper();
            XMLOutputVisitor v = new XMLOutputVisitor(helper);
            v.visit(theQueryPlan);
            this.txtQueryPlan.setText(v.getText());

            // tree version of query plan
            this.treeViewer.setInput(theQueryPlan);
        } else if (this.txtQueryPlan != null) {
            // clear out existing text
            this.txtQueryPlan.setText(""); //$NON-NLS-1$
        }
    }

    /**
     * Shows the detail view with the specified ID.
     * 
     * @param theViewId the details view ID
     * @since 4.3
     * @see #ID_QUERY_PLAN_DOCUMENT_VIEW
     * @see #ID_QUERY_PLAN_TREE_VIEW
     * @see #ID_RESULTS_VIEW
     */
    public void show( final int thePanelId ) {
        if (hasQueryPlan() || hasDebugLog()) {
            final Control resultsPanel = this.detailsControl;
            final Control queryPlanDocPanel = this.grpQueryPlanDocument;
            final Control debugLogPanel = this.grpDebugLog;
            final Composite splitterMain = this.mainSplitter;
            final Composite splitterPlan = this.queryPlanSplitter;
            final Composite splitterTree = this.treeViewSplitter;

            this.pnlResults.getSite().getShell().getDisplay().asyncExec(new Runnable() {
                public void run() {
                    if (resultsPanel.getDisplay().isDisposed()) {
                        return;
                    }

                    if (thePanelId == ID_RESULTS_VIEW) {
                        resultsPanel.setVisible(true);
                        splitterPlan.setVisible(false);
                        debugLogPanel.setVisible(false);
                    } else if (thePanelId == ID_QUERY_PLAN_TREE_VIEW) {
                        resultsPanel.setVisible(false);
                        splitterPlan.setVisible(true);
                        queryPlanDocPanel.setVisible(false);
                        splitterTree.setVisible(true);
                        debugLogPanel.setVisible(false);
                        splitterPlan.layout();
                    } else if (thePanelId == ID_QUERY_PLAN_DOCUMENT_VIEW) {
                        resultsPanel.setVisible(false);
                        splitterPlan.setVisible(true);
                        queryPlanDocPanel.setVisible(true);
                        splitterTree.setVisible(false);
                        debugLogPanel.setVisible(false);
                        splitterPlan.layout();
                    } else if (thePanelId == ID_DEBUG_LOG) {
                        resultsPanel.setVisible(false);
                        splitterPlan.setVisible(false);
                        debugLogPanel.setVisible(true);
                        queryPlanDocPanel.setVisible(false);
                        splitterTree.setVisible(false);
                    } else {
                        // show results
                        resultsPanel.setVisible(true);
                        splitterPlan.setVisible(false);
                        debugLogPanel.setVisible(false);
                    }

                    splitterMain.layout();
                }
            });
        }
    }

    private static final class NodeProperty {
        String name;
        Object value;

        NodeProperty( String name,
                      Object value ) {
            this.name = name;
            this.value = value;
        }
    }

    private class QueryPlanProvider extends LabelProvider implements ITreeContentProvider {

        private QueryPlanDisplayHelper helper;

        private boolean showOnlyProps;

        public QueryPlanProvider( boolean theShowPropsFlag ) {

            this.helper = new QueryPlanDisplayHelper();
            this.showOnlyProps = theShowPropsFlag;
        }

        /**
         * @see org.eclipse.jface.viewers.IContentProvider#dispose()
         * @since 4.3
         */
        @Override
        public void dispose() {
        }

        /**
         * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
         * @since 4.3
         */
        public Object getParent( Object theElement ) {
            return ((theElement instanceof PlanNode) ? ((PlanNode)theElement).getParent() : null);
        }

        /**
         * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
         * @since 4.3
         */
        public Object[] getElements( Object theInputElement ) {
            Object[] result = NO_KIDS;

            if (theInputElement != null) {
                List kids = new ArrayList();

                if (theInputElement instanceof PlanNode) {
                    PlanNode node = (PlanNode)theInputElement;

                    if (this.showOnlyProps) {
                        // Get the list of exposed property names
                        List orderedPropertyNames = helper.getOrderedProperties(node);
                        Map props = node.getProperties();

                        if (orderedPropertyNames != null && !orderedPropertyNames.isEmpty() && props != null && !props.isEmpty()) {
                            for (Iterator itr = orderedPropertyNames.iterator(); itr.hasNext();) {
                                String propName = (String)itr.next();
                                kids.add(new NodeProperty(propName, props.get(propName)));
                            }
                        }
                    } else {
                        kids = node.getChildren();
                    }
                } else if (theInputElement instanceof NodeProperty) {
                    Object val = ((NodeProperty)theInputElement).value;
                    if (val instanceof List) {
                        kids = (List)val;
                    } else {
                        kids = Arrays.asList(new Object[] {val});
                    }
                }

                result = kids.toArray();
            }

            return result;
        }

        /**
         * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
         * @since 4.3
         */
        public Object[] getChildren( Object theParentElement ) {
            return getElements(theParentElement);
        }

        /**
         * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
         * @since 4.3
         */
        @Override
        public String getText( Object theElement ) {
            if ((theElement != null) && (theElement instanceof PlanNode)) {
                PlanNode node = (PlanNode)theElement;
                String name = helper.getName(node);
                String varProp = ""; //$NON-NLS-1$
                Object propObject = null;
                StringBuffer propValue = new StringBuffer();

                while (name.indexOf("${") > -1) { //$NON-NLS-1$
                    varProp = name.substring(name.indexOf(BEGIN_DELIM) + 2, name.indexOf(END_DELIM));
                    propObject = node.getProperties().get(varProp);

                    if (propObject instanceof ArrayList) {
                        ArrayList list = (ArrayList)propObject;

                        for (int size = list.size(), i = 0; i < size; ++i) {
                            if (i > 0) {
                                propValue.append(" ,"); //$NON-NLS-1$   
                            }

                            propValue.append(list.get(i));
                        }
                    } else {
                        propValue.append(propObject);
                    }

                    name = new StringBuffer().append(name.substring(0, name.indexOf(BEGIN_DELIM))).append(propValue).append(name.substring(name.indexOf(END_DELIM) + 1,
                                                                                                                                           name.length())).toString().trim();

                    // remove special characters
                    name = StringUtil.replaceAll(name, "\n", ""); //$NON-NLS-1$ //$NON-NLS-2$
                    name = StringUtil.replaceAll(name, "\t", ""); //$NON-NLS-1$ //$NON-NLS-2$
                }

                return name;
            } else if (theElement instanceof NodeProperty) {
                String propertyName = ((NodeProperty)theElement).name;
                // Try to get the mapped name for the property
                String mappedName = helper.getPropertyName(propertyName);
                if (mappedName == null) {
                    return propertyName;
                }
                return mappedName;
            }

            return super.getText(theElement);
        }

        /**
         * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
         * @since 4.3
         */
        public boolean hasChildren( Object theElement ) {
            return (getChildren(theElement).length != 0);
        }

        /**
         * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object,
         *      java.lang.Object)
         * @since 4.3
         */
        public void inputChanged( Viewer theViewer,
                                  Object theOldInput,
                                  Object theNewInput ) {
        }
    }
}
