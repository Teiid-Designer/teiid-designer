/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.advisor.ui.views.status;

import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.help.IWorkbenchHelpSystem;
import org.eclipse.ui.part.ViewPart;

import com.metamatrix.ui.internal.util.UiUtil;

public class ProjectStatusView  extends ViewPart {
    private static final String ACTION_LIBRARY_VIEW_HELP_ID = "org.teiid.designer.advisor.ui.actionLibraryView"; //$NON-NLS-1$

    private Composite control;

    class NameSorter extends ViewerSorter {
    }

    /**
     * The constructor.
     */
    public ProjectStatusView() {
        super();
    }

    /**
     * This is a call-back that will allow us to create the viewer and initialize it.
     */
    @Override
    public void createPartControl( Composite parent ) {
        control = new Composite(parent, SWT.NONE);
        FillLayout layout = new FillLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        control.setLayout(layout);
        
        new ProjectStatusPanel(control);

        IWorkbenchHelpSystem helpSystem = UiUtil.getWorkbench().getHelpSystem();
        helpSystem.setHelp(control, ACTION_LIBRARY_VIEW_HELP_ID);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
     */
    @Override
    public void setFocus() {
        if (control != null) {
            control.setFocus();
        }
    }
}
