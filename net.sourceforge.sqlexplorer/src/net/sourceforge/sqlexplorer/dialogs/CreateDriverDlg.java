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

import java.io.File;
import java.net.MalformedURLException;
import java.sql.Driver;
import java.util.StringTokenizer;
import net.sourceforge.sqlexplorer.DriverModel;
import net.sourceforge.sqlexplorer.Messages;
import net.sourceforge.sqlexplorer.MyURLClassLoader;
import net.sourceforge.sqlexplorer.plugin.SQLExplorerPlugin;
import net.sourceforge.squirrel_sql.fw.persist.ValidationException;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import net.sourceforge.squirrel_sql.fw.util.DuplicateObjectException;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

public class CreateDriverDlg extends TitleAreaDialog {
    @Override
    protected void setShellStyle( int newShellStyle ) {
        super.setShellStyle(newShellStyle | SWT.RESIZE);// Make the dialog
        // resizable
    }

    // private Button okButton;
    // private Image dlgTitleImage = null;
    private DriverModel driverModel;

    private ISQLDriver driver;

    private static final int SIZING_TEXT_FIELD_WIDTH = 250;

    Button _extraClasspathDeleteBtn;

    private Button _extraClasspathUpBtn;

    private Button _extraClasspathDownBtn;

    private Button newBtn;

    Button _javaClasspathListDriversBtn;

    Button _extraClasspathListDriversBtn;

    DefaultFileListBoxModel defaultModel = new DefaultFileListBoxModel();

    ListViewer extraClassPathList;

    ListViewer javaClassPathList;

    int type;

    Text nameField;

    // Text jarField;

    Button jarSearch;

    Combo combo;

    Text exampleUrlField;

    // private String _currentJarFileText = ""; //$NON-NLS-1$

    public CreateDriverDlg( Shell parentShell,
                            DriverModel dm,
                            int type,
                            ISQLDriver dv ) {
        super(parentShell);
        driverModel = dm;
        driver = dv;
        this.type = type;
    }

    @Override
    protected void configureShell( Shell shell ) {
        super.configureShell(shell);
        if (type == 1) {
            shell.setText(Messages.getString("Create_new_Driver_!_2")); //$NON-NLS-1$
        } else if (type == 2) {
            shell.setText(Messages.getString("Modify_Driver_!_3")); //$NON-NLS-1$
        } else if (type == 3) {
            shell.setText(Messages.getString("Copy_Driver_!_4")); //$NON-NLS-1$
        }
    }

    @Override
    protected Control createContents( Composite parent ) {

        Control contents = super.createContents(parent);

        // dlgTitleImage=ImageDescriptor.createFromURL(JFaceDbcImages.getDriverIcon()).createImage();
        if (type == 1) {
            setTitle(Messages.getString("New_Driver_6")); //$NON-NLS-1$
            setMessage(Messages.getString("Create_a_new_driver_7")); //$NON-NLS-1$
        } else if (type == 2) {
            setTitle(Messages.getString("Modify_driver_8")); //$NON-NLS-1$
            setMessage(Messages.getString("Modify_the_driver_9")); //$NON-NLS-1$
        } else if (type == 3) {
            setTitle(Messages.getString("Copy_Driver_10")); //$NON-NLS-1$
            setMessage(Messages.getString("Copy_the_driver_11")); //$NON-NLS-1$
        }
        // setTitleImage(dlgTitleImage);
        return contents;
    }

    @Override
    protected void okPressed() {
        String name = nameField.getText().trim();
        String driverClassName = combo.getText();
        driverClassName = (driverClassName != null ? driverClassName.trim() : "");//$NON-NLS-1$
        String url = exampleUrlField.getText().trim();
        if (name.equals("")) {//$NON-NLS-1$
            MessageDialog.openError(this.getShell(), Messages.getString("Error..._2"), "Name is empty");//$NON-NLS-1$ //$NON-NLS-2$
            return;
        }
        if (driverClassName.equals("")) {//$NON-NLS-1$
            MessageDialog.openError(this.getShell(), Messages.getString("Error..._2"), "Driver Class Name is empty");//$NON-NLS-1$ //$NON-NLS-2$
            return;
        }
        if (url.equals("")) {//$NON-NLS-1$
            MessageDialog.openError(this.getShell(), Messages.getString("Error..._2"), "URL is empty");//$NON-NLS-1$ //$NON-NLS-2$
            return;
        }

        try {
            driver.setName(name);
            driver.setJarFileNames(defaultModel.getFileNames());

            driver.setDriverClassName(driverClassName);

            driver.setUrl(url);

            if ((type == 1) || (type == 3)) {
                driverModel.addDriver(driver);
            }
        } catch (ValidationException excp) {
            SQLExplorerPlugin.error("Validation Exception", excp); //$NON-NLS-1$

            MessageDialog.openError(this.getShell(),
                                    Messages.getString("Error..._2"), Messages.getString("Error_Validation_Exception_12")); //$NON-NLS-1$ //$NON-NLS-2$

        } catch (DuplicateObjectException excp1) {
            SQLExplorerPlugin.error("Duplicate Exception", excp1); //$NON-NLS-1$
            MessageDialog.openError(this.getShell(),
                                    Messages.getString("Error..._2"), Messages.getString("Error_DuplicateObjectException_13")); //$NON-NLS-1$ //$NON-NLS-2$
        } catch (java.lang.Exception e) {
            SQLExplorerPlugin.error("Exception when adding driver", e); //$NON-NLS-1$ 
            MessageDialog.openError(this.getShell(), Messages.getString("Error..._2"), "Error Adding Driver:" + e.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$
        }

        close();
    }

    void validate() {
        if ((nameField.getText().trim().length() > 0) && (exampleUrlField.getText().trim().length() > 0)
            && (combo.getText().trim().length() > 0)) setDialogComplete(true);
        else setDialogComplete(false);
    }

    protected void setDialogComplete( boolean value ) {
        Button okBtn = getButton(IDialogConstants.OK_ID);
        if (okBtn != null) okBtn.setEnabled(value);
    }

    @Override
    protected void createButtonsForButtonBar( Composite parent ) {
        super.createButtonsForButtonBar(parent);
        validate();
    }

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
        layout.numColumns = 3;
        layout.marginWidth = 10;
        nameGroup.setLayout(layout);
        GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
        nameGroup.setLayoutData(data);

        Composite topComposite = new Composite(nameGroup, SWT.NONE);
        data = new GridData(GridData.FILL_VERTICAL | GridData.FILL_HORIZONTAL);
        data.horizontalSpan = 3;
        topComposite.setLayoutData(data);
        topComposite.setLayout(new GridLayout());

        Group topGroup = new Group(topComposite, SWT.NULL);
        topGroup.setText(Messages.getString("Driver_14")); //$NON-NLS-1$

        data = new GridData(GridData.FILL_VERTICAL | GridData.FILL_HORIZONTAL);
        data.horizontalSpan = 3;
        data.widthHint = SIZING_TEXT_FIELD_WIDTH;
        topGroup.setLayoutData(data);
        layout = new GridLayout();
        layout.numColumns = 3;
        layout.marginWidth = 5;
        topGroup.setLayout(layout);

        Label label = new Label(topGroup, SWT.WRAP);
        label.setText(Messages.getString("Name_15")); //$NON-NLS-1$
        nameField = new Text(topGroup, SWT.BORDER);
        data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
        data.horizontalSpan = 2;
        data.widthHint = SIZING_TEXT_FIELD_WIDTH;
        nameField.setLayoutData(data);

        nameField.addKeyListener(new KeyListener() {
            public void keyPressed( org.eclipse.swt.events.KeyEvent e ) {
                CreateDriverDlg.this.validate();
            }

            public void keyReleased( org.eclipse.swt.events.KeyEvent e ) {
                CreateDriverDlg.this.validate();
            }
        });

        Label label5 = new Label(topGroup, SWT.WRAP);
        label5.setText(Messages.getString("Example_URL_16")); //$NON-NLS-1$
        exampleUrlField = new Text(topGroup, SWT.BORDER);
        data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
        data.widthHint = SIZING_TEXT_FIELD_WIDTH;
        data.horizontalSpan = 2;
        exampleUrlField.setLayoutData(data);
        exampleUrlField.addKeyListener(new KeyListener() {
            public void keyPressed( org.eclipse.swt.events.KeyEvent e ) {
                CreateDriverDlg.this.validate();
            }

            public void keyReleased( org.eclipse.swt.events.KeyEvent e ) {
                CreateDriverDlg.this.validate();
            }
        });

        Composite centralComposite = new Composite(nameGroup, SWT.NONE);
        data = new GridData(GridData.FILL_VERTICAL | GridData.FILL_HORIZONTAL);
        data.horizontalSpan = 3;
        data.verticalSpan = 4;
        data.heightHint = 200;
        centralComposite.setLayoutData(data);
        centralComposite.setLayout(new FillLayout());

        TabFolder tabFolder = new TabFolder(centralComposite, SWT.NULL);
        TabItem item1 = new TabItem(tabFolder, SWT.NULL);
        item1.setText(Messages.getString("Java_Class_Path_17")); //$NON-NLS-1$
        TabItem item2 = new TabItem(tabFolder, SWT.NULL);
        item2.setText(Messages.getString("Extra_Class_Path_18")); //$NON-NLS-1$
        createJavaClassPathPanel(tabFolder, item1);
        createExtraClassPathPanel(tabFolder, item2);

        Label label4 = new Label(nameGroup, SWT.WRAP);
        label4.setText(Messages.getString("Driver_Class_Name_19")); //$NON-NLS-1$
        combo = new Combo(nameGroup, SWT.BORDER | SWT.DROP_DOWN);
        data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
        data.widthHint = SIZING_TEXT_FIELD_WIDTH;
        data.horizontalSpan = 2;
        // int size=driverModel.size();
        combo.setLayoutData(data);

        combo.addSelectionListener(new org.eclipse.swt.events.SelectionListener() {
            public void widgetDefaultSelected( org.eclipse.swt.events.SelectionEvent e ) {
            }

            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e ) {
                CreateDriverDlg.this.validate();
            }
        });

        combo.addKeyListener(new KeyListener() {
            public void keyPressed( org.eclipse.swt.events.KeyEvent e ) {
                CreateDriverDlg.this.validate();
            }

            public void keyReleased( org.eclipse.swt.events.KeyEvent e ) {
                CreateDriverDlg.this.validate();
            }
        });

        nameGroup.layout();
        loadData();
        return parentComposite;
    }

    private void loadData() {
        nameField.setText(driver.getName());
        if (driver.getDriverClassName() != null) combo.setText(driver.getDriverClassName());
        exampleUrlField.setText(driver.getUrl());

        String[] fileNames = driver.getJarFileNames();

        for (int i = 0; i < fileNames.length; ++i) {
            defaultModel.addFile(new File(fileNames[i]));
        }
        if (extraClassPathList != null) {
            extraClassPathList.refresh();
            if (defaultModel.size() > 0) extraClassPathList.getList().setSelection(0);
        }

        if (defaultModel.size() > 0) {

            Object obj = (defaultModel.toArray())[0];
            StructuredSelection sel = new StructuredSelection(obj);
            extraClassPathList.setSelection(sel);

        }

    }

    /*
     * private void setJarFileName(String fileName) { if (fileName != null &&
     * !_currentJarFileText.equals(fileName)) { //jarField.setText(fileName);
     *  } }
     */

    @Override
    protected Point getInitialSize() {
        return new Point(600, 500);
    }

    private void createJavaClassPathPanel( TabFolder tabFolder,
                                           TabItem tabItem ) {
        Composite parent = new Composite(tabFolder, SWT.NULL);
        parent.setLayout(new FillLayout());
        tabItem.setControl(parent);
        Composite cmp = new Composite(parent, SWT.NULL);
        GridLayout grid = new GridLayout();
        grid.numColumns = 2;

        cmp.setLayout(grid);
        javaClassPathList = new ListViewer(cmp, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);

        GridData data = new GridData();
        data.grabExcessVerticalSpace = true;
        data.horizontalAlignment = GridData.FILL;
        data.verticalAlignment = GridData.FILL;

        data.grabExcessHorizontalSpace = true;

        javaClassPathList.getControl().setLayoutData(data);
        javaClassPathList.setContentProvider(new FileContentProvider());
        javaClassPathList.setLabelProvider(new FileLabelProvider());
        ClassPathListModel model = new ClassPathListModel();
        javaClassPathList.setInput(model);

        Composite left = new Composite(cmp, SWT.NULL);
        data = new GridData();
        data.horizontalSpan = 1;
        data.grabExcessVerticalSpace = true;
        data.widthHint = 100;
        data.horizontalAlignment = GridData.FILL;
        data.verticalAlignment = GridData.FILL;

        left.setLayoutData(data);

        GridLayout gridLayout = new GridLayout();

        gridLayout.numColumns = 1;

        left.setLayout(gridLayout);

        _javaClasspathListDriversBtn = new Button(left, SWT.NULL);
        _javaClasspathListDriversBtn.setText(Messages.getString("List_Drivers_20")); //$NON-NLS-1$
        _javaClasspathListDriversBtn.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent event ) {
                combo.removeAll();
                File file = (File)((IStructuredSelection)javaClassPathList.getSelection()).getFirstElement();
                if (file != null) {
                    try {
                        // SQLDriverClassLoader cl = new
                        // SQLDriverClassLoader(file.toURL());
                        MyURLClassLoader cl = new MyURLClassLoader(file.toURI().toURL());
                        Class[] classes = cl.getAssignableClasses(Driver.class);
                        for (int i = 0; i < classes.length; ++i) {
                            combo.add(classes[i].getName());
                        }
                    } catch (MalformedURLException ex) {
                        ex.printStackTrace();
                        // displayErrorMessage(ex);
                    }
                }
                if (combo.getItemCount() > 0) {
                    combo.setText(combo.getItem(0));
                }

            }
        });

        data = new GridData();
        data.grabExcessHorizontalSpace = true;
        data.horizontalAlignment = GridData.FILL;
        _javaClasspathListDriversBtn.setLayoutData(data);

        javaClassPathList.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged( SelectionChangedEvent event ) {
                IStructuredSelection selection = (IStructuredSelection)event.getSelection();
                File f = (File)selection.getFirstElement();
                if (f != null) {
                    if (f.isFile()) _javaClasspathListDriversBtn.setEnabled(true);
                    else _javaClasspathListDriversBtn.setEnabled(false);
                } else _javaClasspathListDriversBtn.setEnabled(false);
            }
        });
        if (model.size() > 0) {
            Object obj = (model.toArray())[0];
            StructuredSelection sel = new StructuredSelection(obj);
            javaClassPathList.setSelection(sel);
        }

    }

    private void createExtraClassPathPanel( final TabFolder tabFolder,
                                            TabItem tabItem ) {
        Composite parent = new Composite(tabFolder, SWT.NULL);
        parent.setLayout(new FillLayout());
        tabItem.setControl(parent);
        Composite cmp = new Composite(parent, SWT.NULL);
        GridLayout grid = new GridLayout();
        grid.numColumns = 2;

        cmp.setLayout(grid);
        extraClassPathList = new ListViewer(cmp, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);

        GridData data = new GridData();
        data.grabExcessVerticalSpace = true;
        data.horizontalAlignment = GridData.FILL;
        data.verticalAlignment = GridData.FILL;

        data.grabExcessHorizontalSpace = true;

        extraClassPathList.getControl().setLayoutData(data);

        extraClassPathList.setContentProvider(new FileContentProvider());
        extraClassPathList.setLabelProvider(new FileLabelProvider());

        extraClassPathList.setInput(defaultModel);

        Composite left = new Composite(cmp, SWT.NULL);
        data = new GridData();
        data.horizontalSpan = 1;
        data.grabExcessVerticalSpace = true;
        data.widthHint = 100;
        data.horizontalAlignment = GridData.FILL;
        data.verticalAlignment = GridData.FILL;

        left.setLayoutData(data);

        GridLayout gridLayout = new GridLayout();

        gridLayout.numColumns = 1;

        left.setLayout(gridLayout);

        _extraClasspathListDriversBtn = new Button(left, SWT.NULL);
        _extraClasspathListDriversBtn.setText(Messages.getString("List_Drivers_21")); //$NON-NLS-1$
        _extraClasspathListDriversBtn.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent event ) {
                combo.removeAll();
                File file = (File)((IStructuredSelection)extraClassPathList.getSelection()).getFirstElement();
                if (file != null) {
                    try {
                        // SQLDriverClassLoader cl = new
                        // SQLDriverClassLoader(file.toURL());

                        MyURLClassLoader cl = new MyURLClassLoader(file.toURI().toURL());
                        Class[] classes = cl.getAssignableClasses(Driver.class);

                        // Class[] classes = cl.getDriverClasses(s_log);
                        for (int i = 0; i < classes.length; ++i) {
                            combo.add(classes[i].getName());
                        }
                    } catch (MalformedURLException ex) {
                        // ex.printStackTrace();
                        // displayErrorMessage(ex);
                    }
                }
                if (combo.getItemCount() > 0) {
                    combo.setText(combo.getItem(0));
                }

            }
        });

        data = new GridData();
        data.grabExcessHorizontalSpace = true;
        data.horizontalAlignment = GridData.FILL;
        _extraClasspathListDriversBtn.setLayoutData(data);

        _extraClasspathUpBtn = new Button(left, SWT.NULL);
        _extraClasspathUpBtn.setText(Messages.getString("Up_22")); //$NON-NLS-1$
        _extraClasspathUpBtn.setEnabled(false);
        data = new GridData();
        data.grabExcessHorizontalSpace = true;
        data.horizontalAlignment = GridData.FILL;
        _extraClasspathUpBtn.setLayoutData(data);

        _extraClasspathDownBtn = new Button(left, SWT.NULL);
        _extraClasspathDownBtn.setText(Messages.getString("Down_23")); //$NON-NLS-1$
        _extraClasspathDownBtn.setEnabled(false);
        data = new GridData();
        data.grabExcessHorizontalSpace = true;
        data.horizontalAlignment = GridData.FILL;
        _extraClasspathDownBtn.setLayoutData(data);

        newBtn = new Button(left, SWT.NULL);
        newBtn.setText(Messages.getString("New_24")); //$NON-NLS-1$
        newBtn.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent event ) {
                FileDialog dlg = new FileDialog(tabFolder.getShell(), SWT.OPEN);
                // dlg.setFilterNames(new String[]{"JAR files","Zip files"});
                dlg.setFilterExtensions(new String[] {"*.jar;*.zip"}); //$NON-NLS-1$
                String str = dlg.open();
                if (str != null) {
                    Object obj = new File(str);
                    defaultModel.add(obj);
                    extraClassPathList.refresh();
                    StructuredSelection sel = new StructuredSelection(obj);
                    extraClassPathList.setSelection(sel);
                }
            }
        });
        data = new GridData();
        data.grabExcessHorizontalSpace = true;
        data.horizontalAlignment = GridData.FILL;
        newBtn.setLayoutData(data);

        _extraClasspathDeleteBtn = new Button(left, SWT.NULL);
        _extraClasspathDeleteBtn.setText(Messages.getString("Delete_26")); //$NON-NLS-1$
        _extraClasspathDeleteBtn.setEnabled(false);
        _extraClasspathDeleteBtn.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent event ) {
                File f = (File)((IStructuredSelection)extraClassPathList.getSelection()).getFirstElement();
                if (f != null) {
                    defaultModel.remove(f);
                    extraClassPathList.refresh();
                    if (defaultModel.size() > 0) {
                        Object obj = (defaultModel.toArray())[0];
                        StructuredSelection sel = new StructuredSelection(obj);
                        extraClassPathList.setSelection(sel);
                    }
                }
            }
        });
        data = new GridData();
        data.grabExcessHorizontalSpace = true;
        data.horizontalAlignment = GridData.FILL;
        _extraClasspathDeleteBtn.setLayoutData(data);
        extraClassPathList.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged( SelectionChangedEvent event ) {
                IStructuredSelection selection = (IStructuredSelection)event.getSelection();
                File f = (File)selection.getFirstElement();
                if (f != null) {
                    _extraClasspathDeleteBtn.setEnabled(true);
                    _extraClasspathListDriversBtn.setEnabled(true);
                } else {
                    _extraClasspathListDriversBtn.setEnabled(false);
                    _extraClasspathDeleteBtn.setEnabled(false);
                }
            }
        });

    }
}

class DefaultFileListBoxModel extends java.util.Vector {

    private static final long serialVersionUID = 1L;

    public void addFile( File file ) {
        addElement(file);
    }

    /**
     * Return the File at the passed index.
     * 
     * @param idx Index to return File for.
     * @return The File at <TT>idx</TT>.
     * @throws ArrayInexOutOfBoundsException Thrown if <TT>idx</TT> < 0 or >= <TT>getSize()</TT>.
     */
    public File getFile( int idx ) {
        return (File)get(idx);
    }

    /**
     * Return array of File names in list.
     * 
     * @return array of File names in list.
     */
    public String[] getFileNames() {
        String[] fileNames = new String[this.size()];
        for (int i = 0, limit = fileNames.length; i < limit; ++i) {
            fileNames[i] = getFile(i).getAbsolutePath();
        }
        return fileNames;
    }

    public void insertFileAt( File file,
                              int idx ) {
        insertElementAt(file, idx);
    }

    public File removeFile( int idx ) {
        return (File)remove(idx);
    }
}

class ClassPathListModel extends DefaultFileListBoxModel {

    private static final long serialVersionUID = 1L;

    /**
     * Default ctor.
     */
    public ClassPathListModel() {
        super();
        load();
    }

    /**
     * Build list.
     */
    private void load() {
        removeAllElements();
        String cp = System.getProperty("java.class.path"); //$NON-NLS-1$
        StringTokenizer strtok = new StringTokenizer(cp, File.pathSeparator);
        while (strtok.hasMoreTokens()) {
            addFile(new File(strtok.nextToken()));
        }
    }

}

class FileContentProvider implements IStructuredContentProvider {

    public Object[] getElements( Object input ) {
        return ((java.util.Vector)input).toArray();
    }

    public void dispose() {
    }

    public void inputChanged( Viewer viewer,
                              Object oldInput,
                              Object newInput ) {
    }
}

class FileLabelProvider implements ILabelProvider {

    public Image getImage( Object elementx ) {
        return null;
    }

    public String getText( Object element ) {

        return ((File)element).toString();
    }

    public boolean isLabelProperty( Object element,
                                    String property ) {
        return true;
    }

    public void dispose() {
    }

    public void removeListener( ILabelProviderListener listener ) {
    }

    public void addListener( ILabelProviderListener listener ) {
    }

}
