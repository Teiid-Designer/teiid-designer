package net.sourceforge.sqlexplorer.dialogs;

/*
 * Copyright (C) 2002-2004 Andrea Mazzolini
 * andreamazzolini@users.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

//
import net.sourceforge.sqlexplorer.DriverModel;
import net.sourceforge.sqlexplorer.IConstants;
import net.sourceforge.sqlexplorer.Messages;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class PasswordConnDlg extends TitleAreaDialog {

    ISQLAlias alias;
    private ISQLDriver driver;
    private DriverModel driverModel;
    private Text userTxt;
    private Text pswdTxt;
    Button fAutoCommitBox, fCommitOnCloseBox;
    String user, passwd;
    private boolean autoCommit = false;
    private boolean commitOnClose = false;
    IPreferenceStore store;
    private static final int SIZING_TEXT_FIELD_WIDTH = 250;

    public PasswordConnDlg( Shell parentShell,
                            ISQLAlias al,
                            DriverModel dm,
                            IPreferenceStore store ) {
        super(parentShell);
        alias = al;
        driverModel = dm;
        this.store = store;
        net.sourceforge.squirrel_sql.fw.id.IIdentifier id = alias.getDriverIdentifier();
        driver = driverModel.getDriver(id);
    }

    @Override
    protected void configureShell( Shell shell ) {
        super.configureShell(shell);
        shell.setText(Messages.getString("Connection_1")); //$NON-NLS-1$
    }

    @Override
    protected Control createContents( Composite parent ) {

        Control contents = super.createContents(parent);

        // dlgTitleImage=ImageDescriptor.createFromURL(JFaceDbcImages.getAliasWizard()).createImage();

        setTitle(Messages.getString("Connection_4")); //$NON-NLS-1$
        setMessage(Messages.getString("Insert_Password_1")); //$NON-NLS-1$
        // setTitleImage(dlgTitleImage);
        // ctGroup.setFocus();
        return contents;
    }

    @Override
    protected void createButtonsForButtonBar( Composite parent ) {
        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
    }

    // public boolean close() {
    // if (dlgTitleImage != null)
    // dlgTitleImage.dispose();
    // return super.close();
    // }

    @Override
    protected Control createDialogArea( Composite parent ) {
        // top level composite
        Composite parentComposite = (Composite)super.createDialogArea(parent);

        // create a composite with standard margins and spacing
        Composite composite = new Composite(parentComposite, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
        layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
        layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));
        composite.setFont(parentComposite.getFont());

        Composite nameGroup = new Composite(composite, SWT.NONE);
        layout = new GridLayout();
        layout.numColumns = 2;
        layout.marginWidth = 10;
        nameGroup.setLayout(layout);
        GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
        nameGroup.setLayoutData(data);

        Label label = new Label(nameGroup, SWT.WRAP);
        label.setText(Messages.getString("Alias_1")); //$NON-NLS-1$
        Label aliasTxt = new Label(nameGroup, SWT.WRAP);
        aliasTxt.setText(alias.getName());
        Label label2 = new Label(nameGroup, SWT.WRAP);
        label2.setText(Messages.getString("Driver_2")); //$NON-NLS-1$
        Label driverTxt = new Label(nameGroup, SWT.WRAP);
        driverTxt.setText(driver.getName());
        Label label3 = new Label(nameGroup, SWT.WRAP);
        label3.setText(Messages.getString("Url_3")); //$NON-NLS-1$
        Label urlTxt = new Label(nameGroup, SWT.WRAP);
        urlTxt.setText(alias.getUrl());
        Label label4 = new Label(nameGroup, SWT.WRAP);
        label4.setText(Messages.getString("User_4")); //$NON-NLS-1$
        userTxt = new Text(nameGroup, SWT.BORDER);
        String salias = alias.getUserName();
        String name = salias;
        String password = alias.getPassword();
        userTxt.setText(name);
        data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
        data.widthHint = SIZING_TEXT_FIELD_WIDTH;
        data.horizontalSpan = 1;
        userTxt.setLayoutData(data);

        Label label5 = new Label(nameGroup, SWT.WRAP);
        label5.setText(Messages.getString("Password_5")); //$NON-NLS-1$
        pswdTxt = new Text(nameGroup, SWT.BORDER);
        pswdTxt.setText(password);
        pswdTxt.setEchoChar('*');

        data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
        data.widthHint = SIZING_TEXT_FIELD_WIDTH;
        data.horizontalSpan = 1;
        pswdTxt.setLayoutData(data);
        pswdTxt.setFocus();

        fAutoCommitBox = new Button(nameGroup, SWT.CHECK);
        fAutoCommitBox.setText(Messages.getString("PasswordConnDlg.AutoCommit_1")); //$NON-NLS-1$
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalAlignment = GridData.BEGINNING;
        gd.horizontalSpan = 1;
        fAutoCommitBox.setLayoutData(gd);

        fCommitOnCloseBox = new Button(nameGroup, SWT.CHECK);
        fCommitOnCloseBox.setText(Messages.getString("PasswordConnDlg.Commit_On_Close_2")); //$NON-NLS-1$
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalAlignment = GridData.BEGINNING;
        gd.horizontalSpan = 2;
        fCommitOnCloseBox.setLayoutData(gd);

        fAutoCommitBox.addSelectionListener(new SelectionListener() {
            public void widgetDefaultSelected( SelectionEvent e ) {
            }

            public void widgetSelected( SelectionEvent e ) {
                if (fAutoCommitBox.getSelection()) {
                    fCommitOnCloseBox.setEnabled(false);
                } else fCommitOnCloseBox.setEnabled(true);
            }
        });

        fAutoCommitBox.getDisplay().asyncExec(new Runnable() {
            public void run() {
                fCommitOnCloseBox.setSelection(store.getBoolean(IConstants.COMMIT_ON_CLOSE));
                fAutoCommitBox.setSelection(store.getBoolean(IConstants.AUTO_COMMIT));
                if (fAutoCommitBox.getSelection()) {
                    fCommitOnCloseBox.setEnabled(false);
                } else fCommitOnCloseBox.setEnabled(true);
            }
        });

        return parentComposite;
    }

    public String getPassword() {
        return passwd;
    }

    @Override
    protected void okPressed() {
        passwd = pswdTxt.getText();
        user = userTxt.getText();
        autoCommit = fAutoCommitBox.getSelection();
        commitOnClose = fCommitOnCloseBox.getSelection();
        super.okPressed();
    }

    public String getUser() {
        return user;
    }

    public boolean getAutoCommit() {
        return autoCommit;
    }

    public boolean getCommitOnClose() {
        return commitOnClose;
    }

}
