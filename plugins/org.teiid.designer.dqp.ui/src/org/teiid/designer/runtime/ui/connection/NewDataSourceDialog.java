package org.teiid.designer.runtime.ui.connection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.teiid.designer.runtime.ExecutionAdmin;
import org.teiid.designer.runtime.TeiidDataSource;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.widget.Dialog;

public class NewDataSourceDialog extends Dialog implements ModifyListener {

    // ============================================================================================================================
    // Variables
    private static final String TITLE = "Create Data Source";

    private String name = null;

    private Text nameField;

    private String modelName;

    private ExecutionAdmin admin;

    public NewDataSourceDialog( Shell parent,
                                ExecutionAdmin admin,
                                String modelName ) {
        super(parent, TITLE);
        this.admin = admin;
        this.modelName = modelName;
    }

    @Override
    protected Control createDialogArea( final Composite parent ) {
        GridData pgd = new GridData();
        pgd.minimumWidth = 400;
        parent.setLayoutData(pgd);

        final Composite dlgPanel = (Composite)super.createDialogArea(parent);
        GridData gd = new GridData(GridData.FILL_BOTH);
        gd.minimumWidth = 400;
        gd.grabExcessHorizontalSpace = true;
        dlgPanel.setLayoutData(gd);
        GridLayout gl = new GridLayout(2, false);
        dlgPanel.setLayout(gl);

        WidgetFactory.createLabel(dlgPanel, DqpUiConstants.UTIL.getString("NewDataSourceDialog.jndiLabel")); //$NON-NLS-1$
        this.nameField = WidgetFactory.createTextField(dlgPanel, SWT.FILL);
        this.nameField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        this.nameField.addModifyListener(this);
        this.nameField.setText(modelName + "DS");

        this.nameField.setEnabled(true);
        this.nameField.setEditable(true);

        return dlgPanel;
    }

    public String getDataSourceName() {
        return name;
    }

    /* (non-Javadoc)
     * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
     */
    public void modifyText( ModifyEvent e ) {
        name = this.nameField.getText();
        validate();
    }

    private void validate() {
        // Check DS names for the current server

        for (TeiidDataSource tds : admin.getDataSources()) {
            if (tds.getName().equalsIgnoreCase(this.name)) {
                System.out.println("CreateDataSourceAction.validate()  Name [" + this.name + "] already exists on server");
                return;
            }
        }
        // this.getButton(IDialogConstants.OK_ID).setEnabled(true);
    }
}
