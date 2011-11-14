/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.teiid.core.types.DataTypeManager;
import org.teiid.designer.extension.ExtensionConstants.MedOperations;
import org.teiid.designer.extension.ExtensionPlugin;
import org.teiid.designer.extension.definition.ModelExtensionAssistant;
import org.teiid.designer.extension.definition.ModelExtensionDefinition;
import org.teiid.designer.extension.definition.ModelObjectExtensionAssistant;
import org.teiid.designer.extension.registry.ModelExtensionRegistry;

import com.metamatrix.core.util.StringUtilities;
import com.metamatrix.metamodels.relational.Procedure;
import com.metamatrix.metamodels.relational.extension.SourceFunctionModelExtensionConstants;
import com.metamatrix.metamodels.relational.util.ParameterData;
import com.metamatrix.metamodels.relational.util.PushdownFunctionData;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.workspace.ModelObjectAnnotationHelper;
import com.metamatrix.modeler.internal.ui.PluginConstants;
import com.metamatrix.modeler.internal.ui.editors.ModelEditor;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.internal.ui.viewsupport.RelationalObjectFactory;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.modeler.ui.actions.INewChildAction;
import com.metamatrix.modeler.ui.actions.INewSiblingAction;
import com.metamatrix.modeler.ui.editors.ModelEditorManager;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.text.StyledTextEditor;

/**
 * Action and dialog designed to help create relational source model Pushdown functions.
 * 
 * Basically, this provides users the ability to model functions defined in databases and use the function call within
 * virtual table or procedure SQL statements/transformations. (see {@link UdfManager})
 */
public class CreatePushdownFunctionAction extends Action implements INewChildAction, INewSiblingAction, SourceFunctionModelExtensionConstants {
	private IFile selectedModel;
	public static ModelObjectAnnotationHelper ANNOTATION_HELPER = new ModelObjectAnnotationHelper();
	public static final String TITLE = UiConstants.Util.getString("CreatePushdownFunctionAction.title"); //$NON-NLS-1$
	 
	private Collection<String> datatypes;
	 
	public CreatePushdownFunctionAction() {
		super(TITLE);
		setImageDescriptor(UiPlugin.getDefault().getImageDescriptor( PluginConstants.Images.NEW_PUSHDOWN_FUNCTION));
		
		Set<String> unsortedDatatypes = DataTypeManager.getAllDataTypeNames();
		datatypes = new ArrayList<String>();
		
		String[] sortedStrings = unsortedDatatypes.toArray(new String[unsortedDatatypes.size()]);
		Arrays.sort(sortedStrings);
		for( String dType : sortedStrings ) {
			datatypes.add(dType);
		}
	}

//	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
//       boolean enable = isApplicable(selection);
//       setEnabled(enable);
//	}
	
    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ui.actions.INewChildAction#canCreateChild(org.eclipse.emf.ecore.EObject)
     */
    public boolean canCreateChild(EObject parent) {
    	return false;
    }
    
    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ui.actions.INewChildAction#canCreateChild(org.eclipse.core.resources.IFile)
     */
    public boolean canCreateChild(IFile modelFile) {
    	return isApplicable(new StructuredSelection(modelFile));
    }
    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ui.actions.INewSiblingAction#canCreateChild(org.eclipse.emf.ecore.EObject)
     */
    public boolean canCreateSibling(EObject parent) {
    	//Convert eObject selection to IFile
    	ModelResource mr = ModelUtilities.getModelResourceForModelObject(parent);
    	if( mr != null ) {
    		IFile modelFile = null;
    		
    		try {
				modelFile = (IFile)mr.getCorrespondingResource();
			} catch (ModelWorkspaceException ex) {
				UiConstants.Util.log(ex);
			}
    		if( modelFile != null ) {
    			return isApplicable(new StructuredSelection(modelFile));
    		}
    	}
    	
    	return false;
    }

	@Override
   public void run() {
		if( selectedModel != null ) {
	        ModelResource mr = ModelUtilities.getModelResource(selectedModel);
	        final Shell shell = UiPlugin.getDefault().getCurrentWorkbenchWindow().getShell();
	        
	        PushdownFunctionData data = new PushdownFunctionData();
	        data.setToDefault(StringUtilities.EMPTY_STRING, StringUtilities.EMPTY_STRING,1, 5);
	        
	        PushdownFunctionInputDialog dialog = new PushdownFunctionInputDialog(shell, data);
	        
	        dialog.open();
	        
	        if (dialog.getReturnCode() == Window.OK) {
	        	createProcedureInTxn(mr, data);
	        }
		}
		
	}

	private ModelObjectExtensionAssistant getAssistant() {
        ModelExtensionRegistry registry = ExtensionPlugin.getInstance().getRegistry();
        return (ModelObjectExtensionAssistant)registry.getModelExtensionAssistant(NAMESPACE_PREFIX);
	}

	private void injectSourceFunctionModelExtension(ModelResource modelResource, EObject procedure, boolean deterministic) throws Exception {
		ModelObjectExtensionAssistant assistant = getAssistant();

        if (assistant == null) {
            // should not happen
        	UiConstants.Util.log(IStatus.ERROR, UiConstants.Util.getString("CreatePushdownFunctionAction.missingSourceFunctionModelExtensionAssistant")); //$NON-NLS-1$
        } else {
            ModelExtensionRegistry registry = ExtensionPlugin.getInstance().getRegistry();
            ModelExtensionDefinition definition = registry.getDefinition(NAMESPACE_PREFIX);

            if (definition == null) {
                // should not happen
            	UiConstants.Util.log(IStatus.ERROR, UiConstants.Util.getString("CreatePushdownFunctionAction.missingSourceFunctionModelExtensionDefinition")); //$NON-NLS-1$
            } else {
                assistant.saveModelExtensionDefinition(procedure);
                assistant.setPropertyValue(procedure, PropertyIds.DETERMINISTIC, Boolean.toString(deterministic));
            }
        }
	}
	
    private void createProcedureInTxn(ModelResource modelResource, PushdownFunctionData data) {
        boolean requiredStart = ModelerCore.startTxn(true, true, "Create Pushdown Function", this); //$NON-NLS-1$
        boolean succeeded = false;
        try {
            ModelEditor editor = ModelEditorManager.getModelEditorForFile((IFile)modelResource.getCorrespondingResource(), true);
            if (editor != null) {
                boolean isDirty = editor.isDirty();

                RelationalObjectFactory factory = new RelationalObjectFactory(modelResource);
                
    	        // Create input parameters
                Procedure pfd = (Procedure)factory.createPushdownFunction(data.getName(), data.getDescription());
    	        for( ParameterData paramData : data.getInputParameters() ) {
    	        	if( paramData.isIncluded() ) {
    	        	EObject dType = data.getDatatype(paramData.getType());
    	        	factory.createParameter(paramData.getName(), null, false, pfd, false, dType, paramData.getLength());
    	        	}
    	        }
    	        // Set output/return parameter
    	        EObject returnParamDatatype = data.getDatatype(data.getReturnParameterType());
    	        factory.createParameter(data.getReturnParameterName(), null, true, pfd, false, returnParamDatatype, data.getReturnParameterLength());
    	        
    	        // Set other properties
    	        if( data.getNameInSource() != null && data.getNameInSource().length() > 0 ) {
    	        	pfd.setNameInSource(data.getNameInSource());
    	        }
    	        // Set extended properties
    	        
    	        injectSourceFunctionModelExtension(modelResource, pfd, data.getDeterministic());
    	        


                if (!isDirty && editor.isDirty()) {
                    editor.doSave(new NullProgressMonitor());
                }
                succeeded = true;
            }
        } catch (Exception e) {
            MessageDialog.openError(Display.getCurrent().getActiveShell(),
            		UiConstants.Util.getString("CreatePushdownFunctionAction.exceptionMessage"), e.getMessage()); //$NON-NLS-1$
            IStatus status = new Status(IStatus.ERROR, UiConstants.PLUGIN_ID,
            		UiConstants.Util.getString("CreatePushdownFunctionAction.exceptionMessage"), e); //$NON-NLS-1$
            UiConstants.Util.log(status);

            return;
        } finally {
            // if we started the txn, commit it.
            if (requiredStart) {
                if (succeeded) {
                    ModelerCore.commitTxn();
                } else {
                    ModelerCore.rollbackTxn();
                }
            }
        }
    }
   
//	public int compareTo(Object o) {
//		if( o instanceof String) {
//			return getText().compareTo((String)o);
//		}
//		
//		if( o instanceof Action ) {
//			return getText().compareTo( ((Action)o).getText() );
//		}
//		return 0;
//	}
	
	
	public boolean isApplicable(ISelection selection) {
       boolean result = false;
       if ( ! SelectionUtilities.isMultiSelection(selection) ) {
           Object obj = SelectionUtilities.getSelectedObject(selection);
           ModelExtensionAssistant assistant = getAssistant();

           if ((assistant == null) || (obj == null)) {
               return false;
           }

           if (assistant.supportsMedOperation(MedOperations.ADD_MED_TO_MODEL, obj)) {
        	   this.selectedModel = (IFile) obj;
               result = true;
           }
       }
       
       return result;
	}

	class PushdownFunctionInputDialog extends TitleAreaDialog {

	    private final String TITLE = UiConstants.Util.getString("PushdownFunctionInputDialog.title"); //$NON-NLS-1$
	    
	    private PushdownFunctionData data;

	    private Button deterministic, OKButton;
	    private Collection<ParameterRow> input_rows = new ArrayList<CreatePushdownFunctionAction.ParameterRow>(5);
	    private ParameterRow return_row;
	    private boolean isControlComplete = false;
	    
	    public PushdownFunctionInputDialog(Shell parentShell, PushdownFunctionData data) {
	        super(parentShell);
	        this.setTitle(TITLE);
	        this.data = data;
	        setShellStyle(getShellStyle() | SWT.RESIZE);
	    }
	    
	    @Override
	    protected void configureShell( Shell shell ) {
	        super.configureShell(shell);
	        shell.setText(TITLE);
	    }
	    
	    /* (non-Javadoc)
	     * @see org.eclipse.jface.window.Window#createDialogArea(org.eclipse.swt.widgets.Composite)
	     */
	    @Override
	    protected Control createDialogArea(Composite parent) {
	    	
	        Composite mainPanel = (Composite)super.createDialogArea(parent);
	        this.setTitle(UiConstants.Util.getString("PushdownFunctionInputDialog.headerTitle")); //$NON-NLS-1$
	        this.setMessage(UiConstants.Util.getString("PushdownFunctionInputDialog.initialMessage")); //$NON-NLS-1$
	        //add controls to composite as necessary
	        GridLayout gridLayout = new GridLayout();
	        gridLayout.numColumns = 2;
	        mainPanel.setLayout(gridLayout);
	        GridData mainGD = new GridData(GridData.FILL_BOTH);
	        mainGD.minimumWidth = 500;
	        mainPanel.setLayoutData(mainGD);

	        Group nameGroup = WidgetFactory.createGroup(mainPanel, UiConstants.Util.getString("PushdownFunctionInputDialog.name"), GridData.FILL_HORIZONTAL, 2, 1); //$NON-NLS-1$

	        final Text pdfName = WidgetFactory.createTextField(nameGroup, 0);
	        if( data.getName() != null) {
	        	pdfName.setText(data.getName());
	        }
	        pdfName.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
	        pdfName.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					data.setName(pdfName.getText());
					validate();
				}
			});
	        
	        Group inputParamsGroup = WidgetFactory.createGroup(mainPanel, 
	        		UiConstants.Util.getString("PushdownFunctionInputDialog.inputParameters"), GridData.FILL_HORIZONTAL, 2, 4); //$NON-NLS-1$
	        
	        WidgetFactory.createLabel(inputParamsGroup, UiConstants.Util.getString("PushdownFunctionInputDialog.include")); //$NON-NLS-1$
	        WidgetFactory.createLabel(inputParamsGroup, UiConstants.Util.getString("PushdownFunctionInputDialog.name")); //$NON-NLS-1$
	        WidgetFactory.createLabel(inputParamsGroup, UiConstants.Util.getString("PushdownFunctionInputDialog.dataType")); //$NON-NLS-1$
	        WidgetFactory.createLabel(inputParamsGroup, UiConstants.Util.getString("PushdownFunctionInputDialog.length")); //$NON-NLS-1$

	        int numInitialRows = data.getInputParameters().size();
	        ParameterData[] inputData = data.getInputParameters().toArray(new ParameterData[numInitialRows]);
	        
	        int lastIncludeIndex = 0;
	        boolean foundLastIncludeIndex = false;
	        for( int i=0; i< 5; i++ ) {
	        	ParameterRow inputRow = new ParameterRow(i, inputData[i], this, inputParamsGroup, false);
	        	if( i < numInitialRows ) {
	        		if( lastIncludeIndex == -1 && !inputData[i].isIncluded() ) {
	        			lastIncludeIndex = i-1;
	        			foundLastIncludeIndex = true;
	        		}
	        		
	        		if( foundLastIncludeIndex && i == (lastIncludeIndex+1) ) {
	        			inputRow.enable();
	        		}
	        	} 
	        	this.input_rows.add(inputRow);
	        }
	        if( lastIncludeIndex == 0 ) {
	        	ParameterRow[] rows = this.input_rows.toArray(new ParameterRow[5]);
	        	rows[0].enable();
	        	rows[1].enable();
	        }
	        
	        Group returnParamsGroup = WidgetFactory.createGroup(mainPanel, UiConstants.Util.getString("PushdownFunctionInputDialog.returnParameter"), GridData.FILL_HORIZONTAL, 2, 3); //$NON-NLS-1$
	        
	        WidgetFactory.createLabel(returnParamsGroup, UiConstants.Util.getString("PushdownFunctionInputDialog.name")); //$NON-NLS-1$
	        WidgetFactory.createLabel(returnParamsGroup, UiConstants.Util.getString("PushdownFunctionInputDialog.dataType")); //$NON-NLS-1$
	        WidgetFactory.createLabel(returnParamsGroup, UiConstants.Util.getString("PushdownFunctionInputDialog.length")); //$NON-NLS-1$
	        
	        return_row = new ParameterRow(0, data.getReturnParameterData(), this, returnParamsGroup, true);
	        return_row.enable();
	        
	        Group propertiesGroup = WidgetFactory.createGroup(mainPanel, UiConstants.Util.getString("PushdownFunctionInputDialog.properties"), GridData.FILL_BOTH, 2, 2); //$NON-NLS-1$
	        
	        WidgetFactory.createLabel(propertiesGroup, UiConstants.Util.getString("PushdownFunctionInputDialog.nameInSource")); //$NON-NLS-1$
	        final Text nameInSourceText = WidgetFactory.createTextField(propertiesGroup, 0);
	        if( data.getNameInSource() != null) {
	        	nameInSourceText.setText(data.getNameInSource());
	        }
	        nameInSourceText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
	        nameInSourceText.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					data.setNameInSource(nameInSourceText.getText());
					validate();
				}
			});
	        
	        deterministic = new Button(propertiesGroup, SWT.CHECK | SWT.RIGHT); //
	        deterministic.setText(UiConstants.Util.getString("PushdownFunctionInputDialog.deterministic")); //WidgetFactory.createCheckBox(propertiesGroup, "Deterministic", 0, 2); //$NON-NLS-1$
	        deterministic.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					data.setDeterministic(deterministic.getSelection());
				}
			});
	        

	        
	        Group descriptionGroup = WidgetFactory.createGroup(mainPanel, UiConstants.Util.getString("PushdownFunctionInputDialog.description"), GridData.FILL_HORIZONTAL, 2, 1); //$NON-NLS-1$
	        GridData gdd = new GridData(GridData.FILL_BOTH);
	        gdd.heightHint = 80;
	        descriptionGroup.setLayoutData(gdd);
	        
	        StyledTextEditor textEditor = new StyledTextEditor(descriptionGroup, SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
	        GridData gdt = new GridData(GridData.FILL_BOTH);
	        gdt.heightHint = 80;
	        gdt.horizontalSpan = 1;
	        textEditor.setLayoutData(gdt);
	        textEditor.setEditable(true);
	        textEditor.setAllowFind(false);
	        textEditor.getTextWidget().setWordWrap(true);
	        if( data.getDescription() != null) {
	        	textEditor.setText(data.getDescription());
	        }
	        
	        isControlComplete = true;
	        return mainPanel;
	    }
	    
	    public PushdownFunctionData getData() {
	    	return this.data;
	    }
	    
	    protected void inputIncludeSelected(int rowID, boolean value) {
	    	ParameterRow[] rows = input_rows.toArray(new ParameterRow[5]);
	    	switch(rowID) {
		    	case 0: {
		    		// user checked first parameter.  If FALSE, then it was true and we need to disable ALL other rows
		    		if( !value ) {
		    			for( int i=1; i<5; i++ ) {
		    				rows[i].exclude();
		    				rows[i].disable();
		    			}
		    		} else {
		    			// DO INCLUDE
		    			rows[1].exclude();
		    			rows[1].enable();
		    			for( int i=2; i<5; i++ ) {
		    				rows[i].exclude();
		    				rows[i].disable();
		    			}
		    		}
		    	} break;
		    	case 1: {
		    		// user checked second parameter.  If FALSE, then it was true and we need to disable ALL other rows
		    		if( !value ) {
		    			for( int i=2; i<5; i++ ) {
		    				rows[i].exclude();
		    				rows[i].disable();
		    			}
		    		} else {
		    			// DO INCLUDE
		    			rows[2].exclude();
		    			rows[2].enable();
		    			for( int i=3; i<5; i++ ) {
		    				rows[i].exclude();
		    				rows[i].disable();
		    			}
		    		}
		    	} break;
		    	case 2: {
		    		// user checked second parameter.  If FALSE, then it was true and we need to disable ALL other rows
		    		if( !value ) {
		    			for( int i=3; i<5; i++ ) {
		    				rows[i].exclude();
		    				rows[i].disable();
		    			}
		    		} else {
		    			// DO INCLUDE
		    			rows[3].exclude();
		    			rows[3].enable();
		    			for( int i=4; i<5; i++ ) {
		    				rows[i].exclude();
		    				rows[i].disable();
		    			}
		    		}
		    	} break;
		    	case 3: {
		    		// user checked second parameter.  If FALSE, then it was true and we need to disable ALL other rows
		    		if( !value ) {
		    			for( int i=4; i<5; i++ ) {
		    				rows[i].exclude();
		    				rows[i].disable();
		    			}
		    		} else {
		    			// DO INCLUDE
		    			rows[4].exclude();
		    			rows[4].enable();
		    		}
		    	} break;
	    	}
	    	validate();
	    }
	    
	    protected void validate() {
	    	
	    	String message = UiConstants.Util.getString("PushdownFunctionInputDialog.pressOkToFinish"); //$NON-NLS-1$

	    	if( this.isControlComplete ) {
		    	IStatus status = this.data.validate();
		    	
		    	if( status.getSeverity() != IStatus.OK ) {
		    		this.setErrorMessage(status.getMessage());
		    		this.OKButton.setEnabled(false);
		    	} else {
		    		this.setErrorMessage(null);
		    		setMessage(message);
		    		this.OKButton.setEnabled(true);
		    	}
	    	}
	    			
	    }
	    
		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
		 */
	    @Override
		protected void createButtonsForButtonBar(Composite parent) {
			this.OKButton = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
					true);
			createButton(parent, IDialogConstants.CANCEL_ID,
					IDialogConstants.CANCEL_LABEL, false);
			this.OKButton.setEnabled(false);
		}
	}
	
	class ParameterRow {
		final private int rowID;
		
		private PushdownFunctionInputDialog dialog;
		
		private Button includeCB;
	    private Text nameText;
	    private Combo typeCombo;
	    private Text typeLengthText;
	    
	    private ParameterData data;
		private String defaultType = "string"; //$NON-NLS-1$
		private int defaultLength = 255;
		
		private boolean include;
		
		public ParameterRow(int rowID, ParameterData data, PushdownFunctionInputDialog dialog, Composite parent, boolean isReturn) {
			super();
			this.dialog = dialog;
			this.rowID = rowID;
			this.data = data;
			
			createControl(parent, isReturn);
			
			initData();
			disable();
		}
		
		public ParameterData getData() {
			return this.data;
		}
		
		public void initData() {
			if( data != null ) {
				if( data.getName() != null) {
					if( data.getName() != null ) {
						nameText.setText(data.getName());
					}
				}
	        	if( data.getType() != null ) {
	        		String comboValue = defaultType;
	    	        if( typeCombo != null && data.getType() != null ) {
	    	        	comboValue = data.getType();
	    	        }
	        		int stringIndex = 0;
	    	        int counter = 0;
	        		for( String type : datatypes ) {
	    	        	if( type.equalsIgnoreCase(comboValue) ) {
	    	        		stringIndex = counter;
	    	        	}
	    	        	counter++;
	    	        }
	    	        typeCombo.select(stringIndex);
	        	}
	        	typeLengthText.setText(Integer.toString(data.getLength()));
	        	if( data.isIncluded() ) {
	        		enable();
	        		include();
	        	} else {
	        		exclude();
	        		disable();
	        	}
	        }
		}
		
		public int getRowID() {
			return this.rowID;
		}
		
		private void createControl(Composite parent, boolean isReturn) {
			
			if( !isReturn ) {
				this.includeCB = WidgetFactory.createCheckBox(parent, " ", 0, false); //$NON-NLS-1$
		        this.includeCB.addSelectionListener(new SelectionAdapter() {

		            @Override
		            public void widgetSelected(final SelectionEvent event) {
		            	boolean value = includeCB.getSelection();
		            	if( value ) {
		            		data.include();
		            	} else {
		            		exclude();
		            	}
		                dialog.inputIncludeSelected(rowID, includeCB.getSelection());
		            }
		        });
			}
			
			this.nameText = WidgetFactory.createTextField(parent, 0);
			this.nameText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
	        if( this.data != null && this.data.getName() != null) {
	        	this.nameText.setText(data.getName());
	        }
	        this.nameText.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					data.setName(nameText.getText()); 
					validate();
				}
			});
	        
	        this.typeCombo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
	        //combo_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
	        int stringIndex = 0;
	        int counter = 0;
	        String comboValue = defaultType;
	        if( this.typeCombo != null && this.data.getType() != null ) {
	        	comboValue = this.data.getType();
	        }
	        for( String type : datatypes ) {
	        	this.typeCombo.add(type);
	        	
	        	if( type.equalsIgnoreCase(comboValue) ) {
	        		stringIndex = counter;
	        	}
	        	counter++;
	        }
	        this.typeCombo.select(stringIndex);
	        this.typeCombo.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					data.setType(typeCombo.getItem(typeCombo.getSelectionIndex()));
					validate();
				}
			});
	        this.typeLengthText = WidgetFactory.createTextField(parent);
	        GridData gd1 = new GridData();
	        gd1.minimumWidth = 60;
	        gd1.widthHint = 60;
	        this.typeLengthText.setLayoutData(gd1);
	        if( data != null) {
	        	this.typeLengthText.setText(Integer.toString(data.getLength()));
	        } else {
	        	this.typeLengthText.setText(Integer.toString(defaultLength));
	        }
	        this.typeLengthText.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					if( typeLengthText.getText().length() > 0 ) {
						data.setLength(Integer.parseInt(typeLengthText.getText()) );
					} else {
						data.setLength(0);
					}
					validate();
				}
			});
		}
		
		public void disable() {
			if( this.includeCB != null ) {
				this.includeCB.setEnabled(false);
			}
    		this.nameText.setEnabled(false);
    		this.typeCombo.setEnabled(false);
    		this.typeLengthText.setEnabled(false);
		}
		
		public void enable() {
			if( this.includeCB != null ) {
				this.includeCB.setEnabled(true);
			}
			this.nameText.setEnabled(true);
    		this.typeCombo.setEnabled(true);
    		this.typeLengthText.setEnabled(true);
		}
		
		private void validate() {
			dialog.validate();
		}
		
		public void include() {
			data.include();
			if( this.includeCB != null ) {
				this.includeCB.setSelection(data.isIncluded());
			}
		}
		
		public void exclude() {
			data.exclude();
			if( this.includeCB != null ) {
				this.includeCB.setSelection(data.isIncluded());
			}
		}
		
		public boolean isIncluded() {
			return this.include;
		}
	}
}

