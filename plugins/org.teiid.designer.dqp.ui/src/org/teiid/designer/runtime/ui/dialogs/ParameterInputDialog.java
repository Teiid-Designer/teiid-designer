/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime.ui.dialogs;

import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.teiid.core.designer.event.IChangeListener;
import org.teiid.core.designer.event.IChangeNotifier;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.designer.runtime.ui.DqpUiConstants;
import org.teiid.designer.ui.common.util.WidgetFactory;


/**
 * @since 8.0
 */
public class ParameterInputDialog extends TitleAreaDialog implements
                                                         DqpUiConstants,
                                                         IChangeListener {

    private static final String PREFIX = I18nUtil.getPropertyPrefix(ParameterInputDialog.class);

    private List<EObject> parameters;
    private List<String> parameterValues;
    private PreviewParameterPanel pnlParams;

    /**
     * @since 5.5.3
     */
    public ParameterInputDialog(Shell parentShell,
                                List<EObject> parameters) {
        super(parentShell);
        this.parameters = parameters;
        setShellStyle(getShellStyle() | SWT.RESIZE);
    }

    /**
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     * @since 5.5.3
     */
    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText(UTIL.getString(PREFIX + "title")); //$NON-NLS-1$
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite pnlOuter = (Composite)super.createDialogArea(parent);
        Composite composite = new Composite(pnlOuter, SWT.NONE);
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 1;
        composite.setLayout(gridLayout);
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));

        setTitle(UTIL.getString(PREFIX + "header")); //$NON-NLS-1$

        Composite paramGroup = WidgetFactory.createGroup(composite, UTIL.getString(PREFIX + "inputParameters"), 0, 1, 3); //$NON-NLS-1$
        paramGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        this.pnlParams = new PreviewParameterPanel(paramGroup, this.parameters);
        this.pnlParams.addChangeListener(this);

        return composite;
    }

    /**
     * @see org.eclipse.jface.dialogs.Dialog#close()
     * @since 5.5.3
     */
    @Override
    public boolean close() {
        if (getReturnCode() == OK) {
            this.parameterValues = this.pnlParams.getColumnValues();
        }

        return super.close();
    }

    public List<String> getParameterValues() {
        assert (getReturnCode() == OK);
        return this.parameterValues;
    }

    @Override
    protected Control createButtonBar(Composite theParent) {
        Control theCntl = super.createButtonBar(theParent);
        getButton(OK).setEnabled(false);
        
        // get the initial validation state (doing it here since the selection handler uses OK button)
        stateChanged(null);

        return theCntl;
    }

    /**
     * @see org.teiid.core.designer.event.IChangeListener#stateChanged(org.teiid.core.designer.event.IChangeNotifier)
     * @since 5.5.3
     */
    @Override
	public void stateChanged(IChangeNotifier theSource) {
        IStatus status = this.pnlParams.getStatus();

        if (status.getSeverity() == IStatus.ERROR) {
            getButton(OK).setEnabled(false);
            setErrorMessage(status.getMessage());
        } else {
            getButton(OK).setEnabled(true);
            setErrorMessage(null);
            setMessage(UTIL.getString(PREFIX + "okMsg")); //$NON-NLS-1$
        }
    }
}
