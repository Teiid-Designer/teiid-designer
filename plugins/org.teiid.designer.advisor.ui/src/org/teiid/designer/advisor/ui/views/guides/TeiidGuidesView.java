package org.teiid.designer.advisor.ui.views.guides;

import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.help.IWorkbenchHelpSystem;
import org.eclipse.ui.part.ViewPart;

import com.metamatrix.ui.internal.util.UiUtil;

/**
 * This sample class demonstrates how to plug-in a new workbench view. The view shows data obtained from the model. The sample
 * creates a dummy model on the fly, but a real implementation would connect to the model available either in this or another
 * plug-in (e.g. the workspace). The view is connected to the model using a content provider.
 * <p>
 * The view uses a label provider to define how model objects should be presented in the view. Each view can present the same
 * model objects using different labels and icons, if needed. Alternatively, a single label provider can be shared between views
 * in order to ensure that objects of the same type are presented in the same way everywhere.
 * <p>
 */

public class TeiidGuidesView extends ViewPart {
    private static final String ADVISOR_HELP_ID = "org.teiid.designer.dsp.ui.dspAdvisorOverview"; //$NON-NLS-1$
    private Composite control;

    class NameSorter extends ViewerSorter {
    }

    /**
     * The constructor.
     */
    public TeiidGuidesView() {
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

        new TeiidGuidesPanel(control);

        IWorkbenchHelpSystem helpSystem = UiUtil.getWorkbench().getHelpSystem();
        helpSystem.setHelp(control, ADVISOR_HELP_ID);
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
