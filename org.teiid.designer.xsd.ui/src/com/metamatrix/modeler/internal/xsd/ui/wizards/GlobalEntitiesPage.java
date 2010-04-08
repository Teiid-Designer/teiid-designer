/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.xsd.ui.wizards;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDSchema;
import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.internal.ui.PluginConstants;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelProjectSelectionStatusValidator;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.viewsupport.ModelingResourceFilter;
import com.metamatrix.modeler.xsd.ui.ModelerXsdUiConstants;
import com.metamatrix.ui.internal.InternalUiConstants;
import com.metamatrix.ui.internal.util.WidgetUtil;
import com.metamatrix.ui.internal.viewsupport.StatusInfo;
import com.metamatrix.ui.internal.widget.accumulator.AccumulatorPanel;
import com.metamatrix.ui.internal.widget.accumulator.IAccumulatedValuesChangeListener;
import com.metamatrix.ui.internal.widget.accumulator.IAccumulatorSource;

public class GlobalEntitiesPage extends WizardPage implements InternalUiConstants.Widgets, IAccumulatedValuesChangeListener {

    private static final String TITLE = ModelerXsdUiConstants.Util.getString("ComplexSchemaTypesPage.title"); //$NON-NLS-1$
    private static final String DESCRIPTION = ModelerXsdUiConstants.Util.getString("ComplexSchemaTypesPage.description"); //$NON-NLS-1$

    private static final int STATUS_OK = 0;
    private static final int STATUS_NO_LOCATION = 1;
    private static final int STATUS_NO_FILENAME = 2;
    private static final int STATUS_FILE_EXISTS = 3;
    private static final int STATUS_BAD_FILENAME = 4;
    private static final int STATUS_CLOSED_PROJECT = 5;
    private static final int STATUS_NO_PROJECT_NATURE = 6;

    private final String leftHeader = ModelerXsdUiConstants.Util.getString("ComplexSchemaTypesPage.leftHeader"); //$NON-NLS-1$
    private final String rightHeader = ModelerXsdUiConstants.Util.getString("ComplexSchemaTypesPage.rightHeader"); //$NON-NLS-1$
    private final Collection globalElements = new HashSet();
    private final Collection globalTypes = new HashSet();

    private final ILabelProvider accumulatorLabelProvider = ModelUtilities.getEMFLabelProvider();

    Text containerText;
    Resource xsdRsrc;
    Text fileText;
    private IPath filePath;
    protected int currentStatus = STATUS_OK;
    private String fileNameMessage = null;
    private String fileExtension = ".xmi"; //$NON-NLS-1$

    private TableViewer typeViewer;
    private AccumulatorPanel panel;
    private Collection typesToCreate;

    /**
     * Constructor for NewModelWizardSpecifyModelPage
     * 
     * @param The current ISelection selection
     */
    public GlobalEntitiesPage( Resource rsrc ) {
        super("complexTypesPage"); //$NON-NLS-1$
        setTitle(TITLE);
        setDescription(DESCRIPTION);

        initialize(rsrc);
    }

    /**
     * @see IDialogPage#createControl(Composite)
     */
    public void createControl( Composite parent ) {
        Composite container = new Composite(parent, SWT.NULL);

        GridLayout layout = new GridLayout();
        container.setLayout(layout);
        // -----------------
        Composite topComposite = new Composite(container, SWT.NULL);
        GridData topCompositeGridData = new GridData(GridData.FILL_HORIZONTAL);
        topComposite.setLayoutData(topCompositeGridData);
        GridLayout topLayout = new GridLayout();
        topLayout.numColumns = 3;
        topComposite.setLayout(topLayout);
        GridData gd = null;

        Label locationLabel = new Label(topComposite, SWT.NULL);
        locationLabel.setText(UiConstants.Util.getString("NewModelWizard.location")); //$NON-NLS-1$

        containerText = new Text(topComposite, SWT.BORDER | SWT.SINGLE);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        containerText.setLayoutData(gd);
        containerText.addModifyListener(new ModifyListener() {
            public void modifyText( ModifyEvent e ) {
                checkStatus();
            }
        });
        containerText.setEditable(false);

        Button browseButton = new Button(topComposite, SWT.PUSH);
        GridData buttonGridData = new GridData();
        // buttonGridData.horizontalAlignment = GridData.HORIZONTAL_ALIGN_END;
        browseButton.setLayoutData(buttonGridData);
        browseButton.setText(UiConstants.Util.getString("NewModelWizard.browse")); //$NON-NLS-1$
        browseButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent e ) {
                handleBrowse();
            }
        });

        Label fileLabel = new Label(topComposite, SWT.NULL);
        fileLabel.setText(UiConstants.Util.getString("NewModelWizard.fileName")); //$NON-NLS-1$

        fileText = new Text(topComposite, SWT.BORDER | SWT.SINGLE);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        fileText.setLayoutData(gd);
        fileText.addModifyListener(new ModifyListener() {
            public void modifyText( ModifyEvent e ) {
                checkStatus();
            }
        });
        // -----------------

        Composite bottomComposite = new Composite(container, SWT.NULL);
        GridData bottomCompositeGridData = new GridData(GridData.FILL_BOTH);
        bottomComposite.setLayoutData(bottomCompositeGridData);
        GridLayout bottomLayout = new GridLayout();
        bottomLayout.numColumns = 2;
        bottomComposite.setLayout(bottomLayout);

        final GlobalEntitiesAccumulatorSource source = new GlobalEntitiesAccumulatorSource(this, bottomComposite);
        panel = new AccumulatorPanel(bottomComposite, source, new ArrayList(), accumulatorLabelProvider, leftHeader, rightHeader);
        panel.addAccumulatedValuesChangeListener(this);
        setControl(container);
        typeViewer.add(globalElements.toArray());
        typeViewer.add(globalTypes.toArray());

        setDefaults();
        checkStatus();
    }

    public void accumulatedValuesChanged( AccumulatorPanel source ) {
        typesToCreate = source.getItemsMovedToSelected();
        checkStatus();
    }

    public Collection getTypesToConvert() {
        if (this.typesToCreate == null) {
            return Collections.EMPTY_LIST;
        }

        return typesToCreate;
    }

    /**
     * Tests if the current workbench selection is a suitable container to use. All selections must be Relational (Virtual or
     * Physical). All Tables and Procedure Results within the selection are added to the Collection of root objects to use for
     * building.
     */
    private void initialize( Resource rsrc ) {
        if (rsrc != null) {
            addComplexTypes(rsrc);
            this.xsdRsrc = rsrc;
        }
    }

    private void setDefaults() {
        this.getControl().getDisplay().asyncExec(new Runnable() {
            public void run() {
                if (xsdRsrc == null) {
                    return;
                }

                final ModelResource xsdMR = ModelerCore.getModelEditor().findModelResource(xsdRsrc);
                if (xsdMR != null) {
                    IContainer parentContainer = (IContainer)xsdMR.getParent().getResource();
                    if (parentContainer != null && parentContainer.getProject() != null) {
                        final IProject project = parentContainer.getProject();
                        containerText.setText(project.getName() + File.separator
                                              + parentContainer.getProjectRelativePath().toString());
                    }

                    fileText.setText(xsdMR.getPath().removeFileExtension().lastSegment());
                }
            }

        });
    }

    private void addComplexTypes( final Resource xsdRsrc ) {
        final Iterator eObjects = xsdRsrc.getContents().iterator();
        while (eObjects.hasNext()) {
            final Object next = eObjects.next();
            if (next instanceof XSDSchema) {
                final XSDSchema schema = (XSDSchema)next;
                final Iterator children = schema.eContents().iterator();
                while (children.hasNext()) {
                    final Object child = children.next();
                    if (child instanceof XSDComplexTypeDefinition) {
                        globalTypes.add(child);
                    } else if (child instanceof XSDElementDeclaration) {
                        globalElements.add(child);
                    }
                }
            }
        }
    }

    /**
     * If genOut is selected the user must supply a model name. If genInput is selected the user must supply a model name. User
     * must select at least one of genOut or genInput
     */
    void checkStatus() {
        String container = getContainerName();
        if (CoreStringUtil.isEmpty(container)) {
            setMessage("No Model Location Selected", IMessageProvider.ERROR); //$NON-NLS-1$
            currentStatus = STATUS_NO_LOCATION;
            setPageComplete(false);
            return;
        }
        IProject project = getTargetProject();
        if (project == null) {
            currentStatus = STATUS_NO_LOCATION;
            return;
        } else if (!project.isOpen()) {
            currentStatus = STATUS_CLOSED_PROJECT;
            return;
        } else {
            try {
                if (project.getNature(PluginConstants.MODEL_PROJECT_NATURE_ID) == null) {
                    currentStatus = STATUS_NO_PROJECT_NATURE;
                    return;
                }
            } catch (CoreException ex) {
                currentStatus = STATUS_NO_PROJECT_NATURE;
                return;
            }
        }

        String fileText = getFileText();
        if (fileText.length() == 0) {
            currentStatus = STATUS_NO_FILENAME;
            setMessage("No File Name Provided", IMessageProvider.ERROR); //$NON-NLS-1$  
            return;
        }
        fileNameMessage = ModelUtilities.validateModelName(fileText, fileExtension);
        if (fileNameMessage != null) {
            currentStatus = STATUS_BAD_FILENAME;
            setMessage("File name is not valid. " + fileNameMessage, IMessageProvider.ERROR); //$NON-NLS-1$ 
            return;
        }
        String fileName = getFileName();
        filePath = new Path(container).append(fileName);
        if (ResourcesPlugin.getWorkspace().getRoot().exists(filePath)) {
            currentStatus = STATUS_FILE_EXISTS;
            setMessage("File already exists. Input another valid name.", IMessageProvider.ERROR); //$NON-NLS-1$ 
            return;
        }

        if (globalElements.isEmpty() && globalTypes.isEmpty()) {
            setMessage(ModelerXsdUiConstants.Util.getString("ComplexSchemaTypesPage.none"), IMessageProvider.ERROR); //$NON-NLS-1$           
            setPageComplete(false);
            return;
        } else if (typesToCreate == null || typesToCreate.isEmpty()) {
            setMessage(ModelerXsdUiConstants.Util.getString("ComplexSchemaTypesPage.noneSelected"), IMessageProvider.ERROR); //$NON-NLS-1$           
            setPageComplete(false);
            return;

        }

        setMessage(ModelerXsdUiConstants.Util.getString("ComplexSchemaTypesPage.done"), IMessageProvider.NONE); //$NON-NLS-1$
        setPageComplete(true);
    }

    void typesToConvertChanged() {

    }

    void typesToConvertRemoved( final Collection values ) {
        Iterator it = values.iterator();
        while (it.hasNext()) {
            Object value = it.next();
            typeViewer.add(value);
        }
    }

    void typesToConvertAdded( final Collection values ) {
        Iterator it = values.iterator();
        while (it.hasNext()) {
            Object value = it.next();
            typeViewer.remove(value);
        }
    }

    Collection getAvailableTypes() {
        final int count = typeViewer.getTable().getItemCount();
        final ArrayList values = new ArrayList(count);
        for (int i = 0; i < count; i++) {
            Object value = typeViewer.getElementAt(i);
            values.add(value);
        }
        return values;
    }

    int getAvailableTypesCount() {
        return typeViewer.getTable().getItemCount();
    }

    Collection getSelectedAvailableTypes() {
        final int[] selectionIndices = typeViewer.getTable().getSelectionIndices();
        final ArrayList selectedValues = new ArrayList(selectionIndices.length);
        for (int i = 0; i < selectionIndices.length; i++) {
            final int index = selectionIndices[i];
            selectedValues.add(typeViewer.getElementAt(index));
        }
        return selectedValues;
    }

    int getSelectedAvailableTypeCount() {
        if (typeViewer != null) {
            return typeViewer.getTable().getSelectionCount();
        } // endif

        return 0;
    }

    Control createTypeControl( final Composite parent ) {
        typeViewer = new TableViewer(parent, SWT.MULTI);
        typeViewer.setLabelProvider(accumulatorLabelProvider);
        return typeViewer.getControl();
    }

    void addTypeSelectionListener( final SelectionListener listener ) {
        typeViewer.getTable().addSelectionListener(listener);
    }

    /**
     * Uses the standard container selection dialog to choose the new value for the container field.
     */
    void handleBrowse() {
        final IContainer folder = WidgetUtil.showFolderSelectionDialog((IContainer)getTargetContainer(),
                                                                       new ModelingResourceFilter(),
                                                                       new ModelProjectSelectionStatusValidator());

        if (folder != null && containerText != null) {
            containerText.setText(folder.getFullPath().makeRelative().toString());
        }

        checkStatus();
    }

    public IProject getTargetProject() {
        IProject result = null;
        String containerName = getContainerName();

        if (!CoreStringUtil.isEmpty(containerName)) {
            IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
            IResource resource = root.findMember(new Path(containerName));

            if (resource.exists()) {
                result = resource.getProject();
            }
        }

        return result;
    }

    public IResource getTargetContainer() {
        IResource result = null;
        String containerName = getContainerName();

        if (!CoreStringUtil.isEmpty(containerName)) {
            IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
            IResource resource = root.findMember(new Path(containerName));

            if (resource.exists()) {
                result = resource;
            }
        }

        return result;
    }

    public String getContainerName() {
        String result = null;

        result = containerText.getText().trim();

        return result;
    }

    public String getFileName() {
        String result = fileText.getText().trim();
        if (!result.endsWith(fileExtension)) {
            result += fileExtension;
        }
        return result;
    }

    public String getFileText() {
        return fileText.getText().trim();
    }

    public IPath getFilePath() {
        return this.filePath;
    }

    class GlobalEntitiesAccumulatorSource implements IAccumulatorSource {
        private final IStatus OK_STATUS = new StatusInfo(ModelerXsdUiConstants.PLUGIN_ID);
        private final GlobalEntitiesPage caller;

        TableViewer viewer;

        public GlobalEntitiesAccumulatorSource( GlobalEntitiesPage cllr,
                                                Composite parent ) {
            super();
            this.caller = cllr;
        }

        public void accumulatedValuesRemoved( Collection values ) {
            caller.typesToConvertRemoved(values);
            caller.typesToConvertChanged();
        }

        public void accumulatedValuesAdded( Collection values ) {
            caller.typesToConvertAdded(values);
            caller.typesToConvertChanged();
        }

        public Collection getAvailableValues() {
            return caller.getAvailableTypes();
        }

        public int getAvailableValuesCount() {
            return caller.getAvailableTypesCount();
        }

        public Collection getSelectedAvailableValues() {
            return caller.getSelectedAvailableTypes();
        }

        public int getSelectedAvailableValuesCount() {
            return caller.getSelectedAvailableTypeCount();
        }

        public Control createControl( Composite parent ) {
            return caller.createTypeControl(parent);
        }

        public void addSelectionListener( SelectionListener listener ) {
            caller.addTypeSelectionListener(listener);
        }

        /**
         * @see com.metamatrix.ui.internal.widget.accumulator.IAccumulatorSource#supportsAddAll()
         */
        public boolean supportsAddAll() {
            return true;
        }

        /**
         * @see com.metamatrix.ui.internal.widget.accumulator.IAccumulatorSource#getSelectionStatus()
         */
        public IStatus getSelectionStatus() {
            return OK_STATUS;
        }
    }

}
