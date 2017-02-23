/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.ui.editors.summary;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.edit.provider.INotifyChangedListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.EditorPart;
import org.teiid.core.designer.ModelerCoreException;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.core.designer.util.StringConstants;
import org.teiid.designer.metamodels.core.ModelAnnotation;
import org.teiid.designer.metamodels.core.ModelImport;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.notification.util.NotificationUtilities;
import org.teiid.designer.core.transaction.SourcedNotification;
import org.teiid.designer.core.util.ModelStatisticsVisitor;
import org.teiid.designer.core.util.ModelVisitorProcessor;
import org.teiid.designer.core.util.PrimaryMetamodelStatisticsVisitor;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.ui.PluginConstants;
import org.teiid.designer.ui.forms.DescriptionSectionWrapper;
import org.teiid.designer.ui.viewsupport.ModelIdentifier;
import org.teiid.designer.ui.viewsupport.ModelUtilities;
import org.teiid.designer.ui.UiConstants;
import org.teiid.designer.ui.UiPlugin;
import org.teiid.designer.ui.editors.AbstractModelEditorPageActionBarContributor;
import org.teiid.designer.ui.editors.ModelEditorPage;
import org.teiid.designer.ui.editors.ModelEditorPageOutline;
import org.teiid.designer.ui.event.ModelResourceEvent;
import org.teiid.designer.ui.common.eventsupport.SelectionProvider;
import org.teiid.designer.ui.common.graphics.GlobalUiColorManager;
import org.teiid.designer.ui.common.util.LayoutDebugger;
import org.teiid.designer.ui.common.util.UiUtil;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.common.util.WidgetUtil;
import org.teiid.designer.ui.common.widget.Label;


/** 
 * @since 5.0
 */
public class ModelSummaryEditorPage extends EditorPart implements ModelEditorPage, IResourceChangeListener, INotifyChangedListener {
    private static final String PAGE_NAME           = "Summary"; //$NON-NLS-1$
    private static final String PAGE_TOOLTIP        = "View Model Information"; //$NON-NLS-1$
    private static final String GENERAL_INFO        = "General Information";  //$NON-NLS-1$
    private static final String STATISTICS          = "Statistics";  //$NON-NLS-1$
    private static final String IMPORTS             = "Imports";  //$NON-NLS-1$
    private static final String MODEL_SUMMARY       = "Model Summary";  //$NON-NLS-1$
    private static final String LAST_MODIFIED       = "Last Saved: ";  //$NON-NLS-1$
    
    private Image TITLE_IMAGE = UiPlugin.getDefault().getImage(PluginConstants.Images.MODEL);
    private Color bkgdColor;
    private final static Color TEXT_COLOR = GlobalUiColorManager.EMPHASIS_COLOR;
    
//    private SimpleDatatypeEditorPanel editorPanel;
    private Composite mainControl;
    private ScrolledForm    topForm;
    private Form    bottomForm;
    private SashForm mainSashForm;
    private SashForm bottomSashForm;
    
    Section generalSection;
//    private Section sourcesSection;
//    private Section descriptionSection;
    DescriptionSectionWrapper descriptionWrapper;
//    private Section statisticsSection;
    
    // Displays a list of all transformation targets for a virtual model
//    private TableViewer targetTablesViewer;
    // Displays a list of all transformation sources for a virtual table
//    private TreeViewer sourcesTreeViewer;
    
//    private ImageHyperlink removeButton;
    
//    private SqlEditorPanel sqlPanel;
    
    FormToolkit formToolkit;

    private MyModelSelectionProvider mySelProv = new MyModelSelectionProvider();

    ModelResource modelResource;
    IResource iResource;
    
    private static final String LF = "\r\n"; //$NON-NLS-1$
    private static int columnTwoIndex = 15;

    StyledText statisticsText;
    private Map<String, EClass> statisticsTypeMap;
    
    StyledText importsText;
    Label locationText;

    private boolean descriptionChanged = false;
    private boolean locationChanged = false;
    
    /** 
     * 
     * @since 5.0
     */
    public ModelSummaryEditorPage() {
        super();
    }

    //
    // Implementation of ISavable methods:
    //
    @Override
	public void doSave(IProgressMonitor monitor) {
        // do nothing
    }

    @Override
	public void doSaveAs() {
        // do nothing
    }

    @Override
	public boolean isSaveAsAllowed() {
        // default
        return false;
    }

    // defect 18127 -- this method needs to be overridden to keep the correct title.
    @Override
	public String getTitle() {
        return PAGE_NAME;
    }

    // defect 18127 -- this method needs to be overridden to keep the correct title.
    @Override
	public String getTitleToolTip() {
        return PAGE_TOOLTIP; 
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchPart#getTitleImage()
     */
    @Override
	public Image getTitleImage() {
        return TITLE_IMAGE;
    }

    //
    // Implementation of ModelEditorPage methods:
    //
    public boolean canDisplay(IEditorInput input) {
        if (input instanceof IFileEditorInput) {
            IFile theFile = ((IFileEditorInput)input).getFile();
            
            boolean isValidFile = ModelUtilities.isModelFile(theFile);
            
            return isValidFile;
        }
        return false;
    }

    public boolean canOpenContext(Object input) {
        boolean canOpen = false;
        if( input instanceof ModelResource ) {
            canOpen = true;
        } else if( input instanceof IFile ) {
            canOpen = modelResource != null;
        }

        return canOpen;
    }
    
    /**
     * (non-Javadoc) 
     * @see org.teiid.designer.ui.editors.ModelEditorPage#initializeEditorPage(java.lang.Object, boolean)
     * @since 5.0.2
     */
    public void initializeEditorPage() {
        //openContext(input);
    }
    
    /* (non-Javadoc)
     * @see org.teiid.designer.ui.editors.ModelEditorPage#openContext(java.lang.Object)
     */
    public void openContext(Object input, boolean forceRefresh) {
        openContext(input);
    }


    public void openContext(final Object input) {
        if( input instanceof IResource ) {
            // Do this
        } else if( input instanceof EObject ) {
            
        } else if( input instanceof ModelResource) {
            
        }
    }

    public Control getControl() {
        return mainControl;
    }

    public ISelectionProvider getModelObjectSelectionProvider() {
        return mySelProv;
    }

    public ISelectionChangedListener getModelObjectSelectionChangedListener() {
        // ignore, for now
        return null;
    }

    public AbstractModelEditorPageActionBarContributor getActionBarContributor() {
        return null;
    }

    public void setLabelProvider(ILabelProvider provider) {
        // do nothing
    }

    public INotifyChangedListener getNotifyChangedListener() {
        return this;
    }

    public ModelEditorPageOutline getOutlineContribution() {
        return null;
    }

    public void updateReadOnlyState(boolean isReadOnly) {
//        if (editorPanel != null
//         && !editorPanel.isDisposed()) {
//            // assume if editor panel is good, so are buttons:
//            editorPanel.setReadOnly(isReadOnly);
//            rmvBtn.setEnabled(!isReadOnly);
//            newBtn.setEnabled(!isReadOnly);
//        } // endif
    }

    public void setTitleText(String title) {
        // do nothing
    }

    public void preDispose() {
        // do nothing
    }

    public void openComplete() {
        // do nothing
    }

    /**
     * @return False. 
     * @see org.teiid.designer.ui.editors.ModelEditorPage#isSelectedFirst(org.eclipse.ui.IEditorInput)
     * @since 5.0.1
     */
    public boolean isSelectedFirst(IEditorInput input) {
        return true;
    }
    
    @Override
	public void init(IEditorSite site, IEditorInput input) {
        setSite(site);
        setInput(input);
        if (input instanceof IFileEditorInput) {
            IFileEditorInput ifei = (IFileEditorInput)input;
            IFile modelFile = ifei.getFile();
            this.iResource = modelFile;
            this.modelResource = ModelUtilities.getModelResource(modelFile);
            if( this.modelResource != null ) {
            	TITLE_IMAGE = ModelIdentifier.getModelImage(modelResource);
            }

        }
    }
    
    @Override
	public boolean isDirty() {
        return false;
    }
    
    @Override
	public void createPartControl(Composite parent) {
        mainControl = parent;
        
        formToolkit = UiPlugin.getDefault().getFormToolkit(parent.getDisplay());
        bkgdColor = formToolkit.getColors().getBackground();
        
//        parent.setLayoutData(new GridData(GridData.FILL_VERTICAL | GridData.HORIZONTAL_ALIGN_BEGINNING));
//        parent.setLayout(new GridLayout());
        mainSashForm = new SashForm(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
        mainSashForm.setLayout(new GridLayout());


        topForm = formToolkit.createScrolledForm(mainSashForm);
        
        GridLayout topFormLayout = new GridLayout();
        
        topFormLayout.numColumns = 1;
        topFormLayout.marginWidth = 5;
        topFormLayout.horizontalSpacing = 0;
        topFormLayout.verticalSpacing = 0;
        topFormLayout.makeColumnsEqualWidth = true;
        topForm.getBody().setLayout(topFormLayout);
        topForm.getBody().setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, true));

        createGeneralSection(topForm.getBody());
        
        Form leftForm = formToolkit.createForm(mainSashForm); //bottomSashForm);
        
        GridLayout leftFormLayout = new GridLayout();
        leftFormLayout.numColumns = 1;
        leftFormLayout.marginWidth = 5;
        leftFormLayout.horizontalSpacing = 15;
        leftFormLayout.verticalSpacing = 5;
        leftFormLayout.makeColumnsEqualWidth = false;
        leftForm.getBody().setLayout(leftFormLayout);
        createDescriptionSection(leftForm.getBody());
        
        SashForm middleSashForm = new SashForm(mainSashForm, SWT.HORIZONTAL); // | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
        Form leftFormA = formToolkit.createForm(middleSashForm);
        
        GridLayout leftFormLayoutA = new GridLayout();
        leftFormLayoutA.numColumns = 1;
        leftFormLayoutA.marginWidth = 5;
        leftFormLayoutA.horizontalSpacing = 15;
        leftFormLayoutA.verticalSpacing = 5;
        leftFormLayoutA.makeColumnsEqualWidth = false;
        leftFormA.getBody().setLayout(leftFormLayoutA);
        createImportsSection(leftFormA.getBody());
        
        Form rightFormA = formToolkit.createForm(middleSashForm);
        GridLayout rightFormLayoutA = new GridLayout();
        rightFormLayoutA.numColumns = 1;
        rightFormLayoutA.marginWidth = 5;
        rightFormLayoutA.horizontalSpacing = 15;
        rightFormLayoutA.verticalSpacing = 5;
        rightFormLayoutA.makeColumnsEqualWidth = false;
        rightFormA.getBody().setLayout(rightFormLayoutA);
        createStatisticsSection(rightFormA.getBody());
        
//        bottomSashForm = new SashForm(mainSashForm, SWT.HORIZONTAL); // | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
//        Form leftForm = formToolkit.createForm(mainSashForm); //bottomSashForm);
//        
//        GridLayout leftFormLayout = new GridLayout();
//        leftFormLayout.numColumns = 1;
//        leftFormLayout.marginWidth = 5;
//        leftFormLayout.horizontalSpacing = 15;
//        leftFormLayout.verticalSpacing = 5;
//        leftFormLayout.makeColumnsEqualWidth = false;
//        leftForm.getBody().setLayout(leftFormLayout);
//        createDescriptionSection(leftForm.getBody());
        
//        Form rightForm = formToolkit.createForm(bottomSashForm);
//        GridLayout rightFormLayout = new GridLayout();
//        rightFormLayout.numColumns = 1;
//        rightFormLayout.marginWidth = 5;
//        rightFormLayout.horizontalSpacing = 15;
//        rightFormLayout.verticalSpacing = 5;
//        rightFormLayout.makeColumnsEqualWidth = false;
//        rightForm.getBody().setLayout(rightFormLayout);
//        createStatisticsSection(rightForm.getBody());
        
        topForm.setText(MODEL_SUMMARY);
       // mainSashForm.setWeights(new int[] {45, 30, 25});
//        bottomSashForm.setWeights(new int[] {70,30});
        topForm.pack(true);
//        leftForm.pack(true);
        
        initializeListeners();
    }
    
    void layout() {
//        mainSashForm.layout(false);
    }
    
    private void createGeneralSection(Composite theParent) {
        generalSection = formToolkit.createSection(theParent, 
        			ExpandableComposite.TITLE_BAR | 
        			//ExpandableComposite.EXPANDED | 
        			Section.DESCRIPTION );

        
        generalSection.setText(GENERAL_INFO);

        generalSection.setDescription(LAST_MODIFIED + getDateAsString(iResource.getLocalTimeStamp()));
        
        generalSection.getDescriptionControl().setForeground(formToolkit.getColors().getColor(IFormColors.TITLE));
        GridData gsGridData = new GridData(GridData.FILL_HORIZONTAL | GridData.HORIZONTAL_ALIGN_BEGINNING);
        gsGridData.heightHint = 120;
        generalSection.setLayoutData(gsGridData);
        
        Composite sectionBody = new Composite(generalSection, SWT.NONE);
        sectionBody.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        sectionBody.setLayout(new GridLayout());

        GridLayout groupLayout = new GridLayout();
        groupLayout.numColumns = 3;
        groupLayout.marginWidth = 5;
        groupLayout.horizontalSpacing = 5;
        groupLayout.verticalSpacing = 5;
        sectionBody.setLayout(groupLayout);
        sectionBody.setBackground(bkgdColor);
        //--------------------------------------
        // Name:
        //--------------------------------------
        Label nameLabel = WidgetFactory.createLabel(sectionBody, "Name:"); //$NON-NLS-1$
        GridData attrGD = new GridData(GridData.BEGINNING, GridData.VERTICAL_ALIGN_FILL, false, false, 1, 1);
        nameLabel.setLayoutData(attrGD);
        nameLabel.setBackground(bkgdColor);
        
        Label nameText = WidgetFactory.createLabel(sectionBody, iResource.getName());
        attrGD = new GridData(GridData.BEGINNING, GridData.VERTICAL_ALIGN_FILL, false, false, 2, 1);
        nameText.setLayoutData(attrGD);
        nameText.setBackground(bkgdColor);
        nameText.setForeground(TEXT_COLOR);
        
        Label locationLabel = WidgetFactory.createLabel(sectionBody, "Location:"); //$NON-NLS-1$
        attrGD = new GridData(GridData.BEGINNING, GridData.VERTICAL_ALIGN_FILL, false, false, 1, 1);
        locationLabel.setLayoutData(attrGD);
        locationLabel.setBackground(bkgdColor);
        
//        Label locationText = WidgetFactory.createLabel(sectionBody, iResource.getFullPath().removeLastSegments(1).toString()); //$NON-NLS-1$
        locationText = WidgetFactory.createLabel(sectionBody, iResource.getLocation().removeLastSegments(1).toString());
        attrGD = new GridData(GridData.BEGINNING, GridData.VERTICAL_ALIGN_FILL, false, false, 2, 1);
        locationText.setLayoutData(attrGD);
        locationText.setBackground(bkgdColor);
        locationText.setForeground(TEXT_COLOR);
        
//        Label namespaceLabel = WidgetFactory.createLabel(sectionBody, "Namespace:"); //$NON-NLS-1$
//        attrGD = new GridData(GridData.BEGINNING, GridData.VERTICAL_ALIGN_FILL, false, false, 1, 1);
//        namespaceLabel.setLayoutData(attrGD);
//        namespaceLabel.setBackground(bkgdColor);
        
//        String namespace = "<unknown>";  //$NON-NLS-1$
//        
//        try {
//            if( modelResource.getTargetNamespace() != null ) {
//                namespace = modelResource.getTargetNamespace().toString();
//            }
//        } catch( ModelWorkspaceException err ) {
//            UiConstants.Util.log(err); 
//        }
////        Text namespaceText = formToolkit.createText(sectionBody, namespace); //$NON-NLS-1$
//        Label namespaceText = WidgetFactory.createLabel(sectionBody, namespace);
//        attrGD = new GridData(GridData.BEGINNING, GridData.VERTICAL_ALIGN_FILL, false, false, 2, 1);
//        namespaceText.setLayoutData(attrGD);
//        namespaceText.setBackground(bkgdColor);
        
        Label modelTypeLabel = WidgetFactory.createLabel(sectionBody, "Model Type:"); //$NON-NLS-1$ (Source or View)
        attrGD = new GridData(GridData.BEGINNING, GridData.VERTICAL_ALIGN_FILL, false, false, 1, 1);
        modelTypeLabel.setLayoutData(attrGD);
        modelTypeLabel.setBackground(bkgdColor);
        
        Label modelTypeText = WidgetFactory.createLabel(sectionBody, ModelIdentifier.getModelType(modelResource).getName());
        attrGD = new GridData(GridData.BEGINNING, GridData.VERTICAL_ALIGN_FILL, false, false, 2, 1);
        modelTypeText.setLayoutData(attrGD);
        modelTypeText.setBackground(bkgdColor);
        modelTypeText.setForeground(TEXT_COLOR);
        
        Label modelClassLabel = WidgetFactory.createLabel(sectionBody, "Model Class:"); //$NON-NLS-1$
        attrGD = new GridData(GridData.BEGINNING, GridData.VERTICAL_ALIGN_FILL, false, false, 1, 1);
        modelClassLabel.setLayoutData(attrGD);
        modelClassLabel.setBackground(bkgdColor);
        
        Label modelClassText = WidgetFactory.createLabel(sectionBody, ModelIdentifier.getPrimaryMetamodelURIFromModelResource(modelResource, false, this));
        attrGD = new GridData(GridData.BEGINNING, GridData.VERTICAL_ALIGN_FILL, false, false, 2, 1);
        modelClassText.setLayoutData(attrGD);
        modelClassText.setBackground(bkgdColor);
        modelClassText.setForeground(TEXT_COLOR);
        
//        sectionBody.pack(true);
        generalSection.setClient(sectionBody);
        
//        Composite topSep = formToolkit.createCompositeSeparator(sectionBody);
//        topSep.setLayoutData(createTableWrapData(nColumns, 5));
        //formToolkit.paintBordersFor(sectionBody);
        
        // Note, This section needs to listen for events so it can update the "Time-stamp" (i.e. Description), target namespace, and location
        // Requires EObject notification & Resource change events
        //LayoutDebugger.debugLayout(theParent, true);
    }
    

    
    private void createStatisticsSection(Composite theParent) {
        Section statisticsSection = formToolkit.createSection(theParent, ExpandableComposite.TITLE_BAR | ExpandableComposite.EXPANDED );
        Color bkgdColor = formToolkit.getColors().getBackground();
        statisticsSection.setText(STATISTICS);
        
//        newSection.getDescriptionControl().setForeground(formToolkit.getColors().getColor(FormColors.TITLE));
        statisticsSection.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        Composite sectionBody = new Composite(statisticsSection, SWT.NONE);
        sectionBody.setLayoutData(new GridData(GridData.FILL_BOTH));
        sectionBody.setLayout(new GridLayout());
        statisticsSection.setClient(sectionBody);
        
        statisticsText = new StyledText(sectionBody, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
        GridData gd = new GridData(GridData.FILL_BOTH);
        gd.widthHint = 400;
        gd.heightHint = 400;
        statisticsText.setLayoutData(gd);
        
        statisticsText.setEditable(false);
        statisticsText.setWordWrap(false);
        statisticsText.setTabs(10);
        statisticsText.setBackground(bkgdColor);
        sectionBody.setBackground(bkgdColor);
        
        resetStatistics();
    }
    
    void resetStatistics() {
        final ModelStatisticsVisitor modelStatisticsVisitor = new PrimaryMetamodelStatisticsVisitor();
        final int mode = ModelVisitorProcessor.MODE_VISIBLE_CONTAINMENTS;   // show only those objects visible to user
        final ModelVisitorProcessor processor = new ModelVisitorProcessor(modelStatisticsVisitor,mode);

        
        try {
            processor.walk(modelResource, ModelVisitorProcessor.DEPTH_INFINITE);
        } catch (ModelerCoreException theException) {
            UiConstants.Util.log(theException);
        }
        
        // Load the object type map
        this.statisticsTypeMap = new HashMap<String, EClass>();
        for ( Iterator iter = modelStatisticsVisitor.getEClassesFound().iterator() ; iter.hasNext() ; ) {
            EClass eClass = (EClass) iter.next();
            statisticsTypeMap.put(eClass.getName(), eClass);
        }
        // write the header
        StringBuffer buff = new StringBuffer(100);
//        buff.append(LF);
        
        // sort the class names, which are the keys of the typeMap
        TreeSet sortedNames = new TreeSet(this.statisticsTypeMap.keySet());
        for ( Iterator iter = sortedNames.iterator() ; iter.hasNext() ; ) {
            EClass eClass = (EClass) this.statisticsTypeMap.get(iter.next());
            
            buff.append(' ');
            int count = modelStatisticsVisitor.getCount(eClass);
            StringBuffer countStr = new StringBuffer(columnTwoIndex);
            countStr.append(Integer.toString(count));
            while( countStr.length() < columnTwoIndex ) {
                countStr.append(StringConstants.SPACE);
            }
            buff.append(countStr);
            if ( count == 1 ) {
                buff.append(CoreStringUtil.computeDisplayableForm(eClass.getName()));
            } else {
                buff.append(CoreStringUtil.computePluralForm(CoreStringUtil.computeDisplayableForm(eClass.getName())));
            }
            buff.append(LF);
        }
        
        
        statisticsText.setText(buff.toString());
    }
    
    
    private void createDescriptionSection(Composite theParent) {
        descriptionWrapper = new DescriptionSectionWrapper(theParent, ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE );
        EObject annotation = null;
        
        try {
            annotation = modelResource.getModelAnnotation();
        } catch (ModelWorkspaceException theException) {
            UiConstants.Util.log(theException);
        }
        
        descriptionWrapper.setTarget(annotation, modelResource);
        descriptionWrapper.getSection().addExpansionListener(new ExpansionAdapter() {
            @Override
			public void expansionStateChanged(ExpansionEvent e) {
                layout();
            }
        });
//        descriptionWrapper.getSection().setBackground(bg);
    }
    
    private void createImportsSection(Composite theParent) {
        Section importsSection = formToolkit.createSection(theParent, ExpandableComposite.TITLE_BAR | ExpandableComposite.EXPANDED );
        Color bkgdColor = formToolkit.getColors().getBackground();
        importsSection.setText(IMPORTS);
        
//        newSection.getDescriptionControl().setForeground(formToolkit.getColors().getColor(FormColors.TITLE));
        importsSection.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        Composite sectionBody = new Composite(importsSection, SWT.NONE);
        sectionBody.setLayoutData(new GridData(GridData.FILL_BOTH));
        sectionBody.setLayout(new GridLayout());
        importsSection.setClient(sectionBody);
        sectionBody.setBackground(bkgdColor);
        
        importsText = new StyledText(sectionBody, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
        GridData gd = new GridData(GridData.FILL_BOTH);
        gd.widthHint = 400;
        gd.heightHint = 200;
        importsText.setLayoutData(gd);
        
        importsText.setEditable(false);
        importsText.setWordWrap(false);
        importsText.setTabs(10);
        importsText.setBackground(bkgdColor);

        resetImports();

    }
    
    private void resetAllData() {
        UiUtil.runInSwtThread(new Runnable() {
            public void run() {
                generalSection.setDescription(LAST_MODIFIED + getDateAsString(iResource.getLocalTimeStamp()));
                resetImports();
                resetStatistics();
                resetLocation();
                descriptionWrapper.reset();
                statisticsText.setForeground(formToolkit.getColors().getForeground());
            }
        }, true);
    }
    
    void resetLocation() {
        UiUtil.runInSwtThread(new Runnable() {
            public void run() {
                locationText.setText(iResource.getLocation().removeLastSegments(1).toString());
            }
        }, true);

    }
    
    private void resetDescription() {
        UiUtil.runInSwtThread(new Runnable() {
            public void run() {
                descriptionWrapper.reset();
            }
        }, true);
    }
    
    void resetImports() {
        UiUtil.runInSwtThread(new Runnable() {
            public void run() {
                List modelImports = Collections.EMPTY_LIST; 
                
                try {
                    modelImports = modelResource.getModelImports();
                } catch (ModelWorkspaceException theException) {
                    UiConstants.Util.log(theException);
                }
                if( modelImports == null || modelImports.isEmpty() ) {
                    importsText.setText("<No Imports>"); //$NON-NLS-1$
                } else {
                    StringBuffer buff = new StringBuffer(100*modelImports.size());
                    for( Iterator iter = modelImports.iterator(); iter.hasNext(); ) {
                        ModelImport nextImport = (ModelImport)iter.next();
                        String thisImportStr = nextImport.getPath();
                        if( thisImportStr.charAt(0) == '/') {
                            thisImportStr = thisImportStr.substring(1, thisImportStr.length());
                        }
                        buff.append(' ').append(thisImportStr).append(LF);
                    }
                    importsText.setText(buff.toString());
                }
            }
        }, true);

    }

    @Override
	public void setFocus() {
        // do nothing
    }
    
    public String getDateAsString( long timestamp ) {
        Date theDate = new Date(timestamp);
        DateFormat formatter = new SimpleDateFormat("EEE, d MMM yyyy hh:mm:ss aaa z"); //$NON-NLS-1$
        formatter.setLenient(false);
        
        return formatter.format(theDate);
    }
    
    private void initializeListeners() {
        ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
    }
    
    private void removeListeners() {
        ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
    }
    
    
    @Override
	public void dispose() {
        super.dispose();
        removeListeners();
    }
    

    public void resourceChanged(IResourceChangeEvent theEvent) {
        switch( theEvent.getType() ) {
            case IResourceChangeEvent.POST_CHANGE: {
                
            }
        }
    }


    public void notifyChanged(Notification theNotification) {
        boolean requiresSave = false;
        
        try {
            requiresSave = this.modelResource.hasUnsavedChanges();
        } catch (ModelWorkspaceException theException) {
            UiConstants.Util.log(theException);
        }
        
        if (theNotification instanceof SourcedNotification) {
            Object source = ((SourcedNotification)theNotification).getSource();
            if( source == null || (source != null && !source.equals(this))) {
                Collection notifications = ((SourcedNotification)theNotification).getNotifications();
                Iterator iter = notifications.iterator();
                Notification nextNotification = null;
                
                while (iter.hasNext() ) {
                    nextNotification = (Notification)iter.next();
                    
                    handleNotification(nextNotification);
                }
            }
        } else {
            Object targetObject = ModelerCore.getModelEditor().getChangedObject(theNotification);
            if(    targetObject != null  ) {
                if( targetObject instanceof EObject  ) {
                    // If notification is from another "model resource" we don't care for Coarse
                    // Mapping diagram.  All objects are in same model.
                    // Check here if the targetObject and document have the same resource, then set to TRUE;
                    ModelResource mr = ModelUtilities.getModelResourceForModelObject((EObject)targetObject);
                    if( mr != null && mr.equals(this.modelResource)) {
                        handleNotification(theNotification);
                    }
                } else if( targetObject instanceof Resource ) {
                    Resource targetResource = (Resource)targetObject;
                    if( targetResource.equals(this.modelResource))
                        handleNotification(theNotification);
                }
            }
        }
        
        if( requiresSave ) {
            statisticsText.setForeground(UiUtil.getSystemColor(SWT.COLOR_GRAY));
        } else {
            statisticsText.setForeground(formToolkit.getColors().getForeground());
            
        }
    }
    
    private void handleNotification(Notification notification) {
        // If we get here, we need to make sure the 
        
        switch( notification.getEventType() ) {
//            case Notification.ADD: {
//                
//            } break;
//            
//            case Notification.ADD_MANY: {
//                
//            } break;
            
            case Notification.SET: {
                EObject eObj = NotificationUtilities.getEObject(notification);
                if( eObj instanceof ModelAnnotation ) {
                    resetDescription();
                }
            } break;
        }
    }
    

    public void processEvent(EventObject obj) {
        if( obj instanceof ModelResourceEvent ) {
            ModelResourceEvent event = (ModelResourceEvent) obj;
            
            // Need to update Ui components for content
            
            switch( event.getType() ) {
                case ModelResourceEvent.RELOADED:
                case ModelResourceEvent.CHANGED: {
                    // Update ALL
                    resetAllData();
                } break;
                case ModelResourceEvent.MOVED: {
                    // Update General:Location field
                    resetLocation();
                } break;
                case ModelResourceEvent.REBUILD_IMPORTS: {
                    // Update Imports List
                    resetImports();
                } break;
                
                default: 
                    break;
                
            }

        }
    }

    class MyModelSelectionProvider extends SelectionProvider implements ISelectionChangedListener {
        // Listener:
        public void selectionChanged(SelectionChangedEvent event) {
            ISelection selection = event.getSelection();
            if (selection.isEmpty()) {
//                if (localTypes.getSelection().isEmpty() && builtInTypes.getSelection().isEmpty()) {
//                    // defect 18562 - only set an empty selection when neither of the two lists 
//                    //  have a selection.  This keeps us from getting selectionChanged events
//                    //  out of order (resulting in a blank properties view).
//                    setSelection(selection);
//                } // endif
            } else {
                setSelection(selection);
            } // endif
        }
    } // endclass MyModelSelectionProvider
}
