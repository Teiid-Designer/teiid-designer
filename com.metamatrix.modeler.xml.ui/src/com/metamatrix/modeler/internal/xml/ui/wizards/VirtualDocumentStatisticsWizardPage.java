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
package com.metamatrix.modeler.internal.xml.ui.wizards;

import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import com.metamatrix.metamodels.xml.BuildStatus;
import com.metamatrix.metamodels.xml.XmlAttribute;
import com.metamatrix.metamodels.xml.XmlBuildable;
import com.metamatrix.metamodels.xml.XmlDocumentEntity;
import com.metamatrix.metamodels.xml.XmlElement;
import com.metamatrix.metamodels.xml.XmlFragment;
import com.metamatrix.metamodels.xml.util.XmlDocumentUtil;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.xml.IVirtualDocumentFragmentSource;
import com.metamatrix.modeler.xml.ui.ModelerXmlUiConstants;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.widget.MessageLabel;

/**
 * NewVirtualDocumentWizardPage is the wizard page contribution for building Virtual XMLDocument models from XML Schema files in
 * the workspace.
 */

public class VirtualDocumentStatisticsWizardPage extends WizardPage
    implements ModelerXmlUiConstants, IVirtualDocumentFragmentSource {

    private static final int MAX_NODES = 4000;
    private static final String TITLE = Util.getString("VirtualDocumentStatisticsWizardPage.title"); //$NON-NLS-1$
    private static final String DESCRIPTION = Util.getString("VirtualDocumentStatisticsWizardPage.description"); //$NON-NLS-1$
    static final String WAITING_TEXT = Util.getString("VirtualDocumentStatisticsWizardPage.waitingText"); //$NON-NLS-1$
    private static final String LABEL_DOCUMENTS = Util.getString("VirtualDocumentStatisticsWizardPage.labelDocuments"); //$NON-NLS-1$
    private static final String LABEL_TOTAL = Util.getString("VirtualDocumentStatisticsWizardPage.labelTotal"); //$NON-NLS-1$
    private static final String LABEL_ATTRIBUTES = Util.getString("VirtualDocumentStatisticsWizardPage.labelAttributes"); //$NON-NLS-1$
    private static final String LABEL_SUBTYPE = Util.getString("VirtualDocumentStatisticsWizardPage.labelSubtypes"); //$NON-NLS-1$
    private static final String LABEL_RECURSIVE = Util.getString("VirtualDocumentStatisticsWizardPage.labelRecursives"); //$NON-NLS-1$
    private static final String LABEL_ELEMENTS = Util.getString("VirtualDocumentStatisticsWizardPage.labelElements"); //$NON-NLS-1$
    private static final String SUBTASK_ANALYZING = Util.getString("VirtualDocumentStatisticsWizardPage.subtaskAnalyzing"); //$NON-NLS-1$
    private static final String SUBTASK_BUILDING = Util.getString("VirtualDocumentStatisticsWizardPage.subtaskBuilding"); //$NON-NLS-1$
    private static final String TEXT_ERROR = Util.getString("VirtualDocumentStatisticsWizardPage.issueError"); //$NON-NLS-1$
    private static final String TEXT_WARNING = Util.getString("VirtualDocumentStatisticsWizardPage.issueWarning"); //$NON-NLS-1$
    static final IStatus ERROR_STATUS = new Status(IStatus.ERROR, ModelerXmlUiConstants.PLUGIN_ID, 0, TEXT_ERROR, null);
    static final IStatus WARNING_STATUS = new Status(IStatus.WARNING, ModelerXmlUiConstants.PLUGIN_ID, 0, TEXT_WARNING, null);
    static final IStatus OK_STATUS = new Status(IStatus.OK, ModelerXmlUiConstants.PLUGIN_ID, 0, "", null); //$NON-NLS-1$

    StatsClass sts;
    private XmlFragment[] fragments;
    private final NewDocumentWizardModel model;

    Text docCount;
    Text attribCount;
    Text elementCount;
    Text subtypeCount;
    Text recursiveCount;
    Text ttlCount;
    MessageLabel message;

    private boolean tooManyTreeNodes = false;

    /**
     * Constructor for VirtualDocumentStatisticsWizardPage.
     * 
     * @param pageName
     */
    public VirtualDocumentStatisticsWizardPage( NewDocumentWizardModel wizardModel ) {
        super("virtualDocumentStatisticsWizardPage"); //$NON-NLS-1$
        setTitle(TITLE);
        setDescription(DESCRIPTION);
        setPageComplete(true);
        model = wizardModel;
    }

    public void createControl( Composite parent ) {
        model.setWizHolder(parent);
        Composite panel = new Composite(parent, SWT.NONE);
        GridLayout gl = new GridLayout(2, false);
        panel.setLayout(gl);

        // Add components:
        Label l = new Label(panel, SWT.NONE);
        l.setText(LABEL_DOCUMENTS);
        GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_END);
        l.setLayoutData(gd);
        docCount = WidgetFactory.createTextField(panel, GridData.FILL_HORIZONTAL, WAITING_TEXT);
        docCount.setEditable(false);
        // ----
        l = new Label(panel, SWT.NONE);
        l.setText(LABEL_ELEMENTS);
        gd = new GridData(GridData.HORIZONTAL_ALIGN_END);
        l.setLayoutData(gd);
        elementCount = WidgetFactory.createTextField(panel, GridData.FILL_HORIZONTAL, WAITING_TEXT);
        elementCount.setEditable(false);
        // ----
        l = new Label(panel, SWT.NONE);
        l.setText(LABEL_RECURSIVE);
        gd = new GridData(GridData.HORIZONTAL_ALIGN_END);
        l.setLayoutData(gd);
        recursiveCount = WidgetFactory.createTextField(panel, GridData.FILL_HORIZONTAL, WAITING_TEXT);
        recursiveCount.setEditable(false);
        // ----
        l = new Label(panel, SWT.NONE);
        l.setText(LABEL_SUBTYPE);
        gd = new GridData(GridData.HORIZONTAL_ALIGN_END);
        l.setLayoutData(gd);
        subtypeCount = WidgetFactory.createTextField(panel, GridData.FILL_HORIZONTAL, WAITING_TEXT);
        subtypeCount.setEditable(false);
        // ----
        l = new Label(panel, SWT.NONE);
        l.setText(LABEL_ATTRIBUTES);
        gd = new GridData(GridData.HORIZONTAL_ALIGN_END);
        l.setLayoutData(gd);
        attribCount = WidgetFactory.createTextField(panel, GridData.FILL_HORIZONTAL, WAITING_TEXT);
        attribCount.setEditable(false);
        // ----
        l = new Label(panel, SWT.NONE);
        l.setText(LABEL_TOTAL);
        gd = new GridData(GridData.HORIZONTAL_ALIGN_END);
        l.setLayoutData(gd);
        ttlCount = WidgetFactory.createTextField(panel, GridData.FILL_HORIZONTAL, WAITING_TEXT);
        ttlCount.setEditable(false);
        // ----
        message = new MessageLabel(panel);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan = 2;
        message.setLayoutData(gd);

        setControl(panel);
    }

    @Override
    public void dispose() {
        super.dispose();
        Control c = getControl();
        if (c != null) {
            c.dispose();
        } // endif
    }

    //
    // Implementation of the IVirtualDocumentFragmentSource
    //
    public XmlFragment[] getFragments( ModelResource modelResource,
                                       IProgressMonitor monitor ) {
        return model.getFragments(modelResource, monitor);
    }

    public void updateSourceFragments( boolean isVisible,
                                       IProgressMonitor monitor ) {
        // isVisible parm not used for now.
        monitor.subTask(SUBTASK_BUILDING);
        XmlFragment[] frags = model.getFragments(null, monitor);
        if (getControl() != null && frags != fragments) { // the fragments have changed
            fragments = frags; // remember the new ones
            monitor.subTask(SUBTASK_ANALYZING);
            analyzeFragments(fragments, monitor);
        } // endif
    }

    private void analyzeFragments( XmlFragment[] fragments,
                                   IProgressMonitor monitor ) {
        sts = new StatsClass();
        updateDisplay(false, -1); // clear the display, just in case
        for (int i = 0; i < fragments.length; i++) {
            // Check for cancellation ...
            if (monitor.isCanceled()) {
                break;
            } // endif
            gatherNodeInfo(fragments[i], sts, monitor);
            monitor.worked(1);
        } // endfor

        tooManyTreeNodes = false;
        if (sts.total > MAX_NODES) {
            tooManyTreeNodes = true;
        }

        updateDisplay(false, fragments.length);
    }

    private void updateDisplay( boolean async,
                                final int fragmentCount ) {
        Runnable r = new Runnable() {
            public void run() {
                // set text fields:
                if (fragmentCount != -1) {
                    // a real count:
                    docCount.setText(Integer.toString(fragmentCount));
                    elementCount.setText(Integer.toString(sts.elements));
                    recursiveCount.setText(Integer.toString(sts.recursives));
                    subtypeCount.setText(Integer.toString(sts.subtypes));
                    attribCount.setText(Integer.toString(sts.attribs));
                    ttlCount.setText(Integer.toString(sts.total));
                    setPageComplete(true);
                    if (sts.total > MAX_NODES) {
                        message.setErrorStatus(ERROR_STATUS);
                        // setting page completion to false is problematic: it isn't
                        // as dynamic as we like, and it seems to affect other pages.
                        // setPageComplete(false);
                    } else if (sts.total > 1000) {
                        message.setErrorStatus(WARNING_STATUS);
                        // setPageComplete(true);
                    } else {
                        message.setErrorStatus(OK_STATUS);
                        // setPageComplete(true);
                    } // endif
                } else {
                    // clear out count values:
                    docCount.setText(WAITING_TEXT);
                    elementCount.setText(WAITING_TEXT);
                    recursiveCount.setText(WAITING_TEXT);
                    subtypeCount.setText(WAITING_TEXT);
                    attribCount.setText(WAITING_TEXT);
                    ttlCount.setText(WAITING_TEXT);
                    message.setErrorStatus(OK_STATUS);
                    setPageComplete(true);
                } // endif
            }
        };

        if (async) {
            Display.getDefault().asyncExec(r);
        } else {
            Display.getDefault().syncExec(r);
        } // endif
    }

    /**
     * The <code>WizardPage</code> implementation of this <code>IWizardPage</code> method returns <code>true</code> if this page
     * is complete (<code>isPageComplete</code>) and there is a next page to flip to. Subclasses may override (extend or
     * reimplement).
     * 
     * @see #getNextPage
     * @see #isPageComplete
     */
    @Override
    public boolean canFlipToNextPage() {
        return isPageComplete() && getNextPage() != null && !tooManyTreeNodes;
    }

    private void gatherNodeInfo( XmlDocumentEntity node,
                                 StatsClass sts,
                                 IProgressMonitor monitor ) {
        // handle children:
        List kids = node.eContents();
        for (int i = 0; i < kids.size(); i++) {
            // Check for cancellation ...
            if (monitor.isCanceled()) return;

            XmlDocumentEntity kid = (XmlDocumentEntity)kids.get(i);
            gatherNodeInfo(kid, sts, monitor);
        } // endfor -- children

        // Check for cancellation ...
        if (monitor.isCanceled()) return;

        // count myself:
        sts.total++;
        if (node instanceof XmlElement) {
            sts.elements++;
        } else if (node instanceof XmlAttribute) {
            sts.attribs++;
        } else {
            sts.other++;
        } // endif

        if (XmlDocumentUtil.isRecursive(node)) {
            sts.recursives++;
        } else if (node instanceof XmlBuildable && ((XmlBuildable)node).getBuildState() == BuildStatus.INCOMPLETE_LITERAL) {
            // note: this is not accurate because build could stop for a diferent reason.
            sts.subtypes++;
        } // endif
    }

    static class StatsClass {
        int other;
        int attribs;
        int elements;
        int subtypes;
        int recursives;
        int total;

        @Override
        public String toString() {
            return " StatsClass:\n  attributes:\t" + attribs + "\n  elements:\t" + elements + "\n  subtypes\t" + subtypes + "\n  recursives:\t" + recursives + "\n  other:\t" + other + "\n  total:\t" + total; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
        }
    }
}
