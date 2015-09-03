/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.mapping.ui.editor;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.designer.diagram.ui.editor.DiagramController;
import org.teiid.designer.diagram.ui.editor.DiagramEditor;
import org.teiid.designer.mapping.factory.DefaultMappableTree;
import org.teiid.designer.mapping.factory.IMappableTree;
import org.teiid.designer.mapping.factory.TreeMappingAdapter;
import org.teiid.designer.mapping.ui.UiConstants;
import org.teiid.designer.mapping.ui.UiPlugin;
import org.teiid.designer.metamodels.transformation.InputSet;
import org.teiid.designer.metamodels.transformation.MappingClass;
import org.teiid.designer.ui.common.widget.ScrollableTitleAreaDialog;
import org.teiid.designer.ui.editors.ModelEditor;
import org.teiid.designer.ui.editors.ModelEditorPage;

public class EditInputSetDialog extends ScrollableTitleAreaDialog {
//	private static final int WIDTH = 700;
//    private static final int HEIGHT = 400;
    
    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(EditInputSetDialog.class);
    private static final String TITLE = getString("title"); //$NON-NLS-1$
    private static final String MESSAGE = getString("message"); //$NON-NLS-1$
    
    private InputSet inputSet;
    EditInputSetPanel editorPanel;
    
    private static String getString( final String id ) {
        return UiConstants.Util.getString(I18N_PREFIX + id);
    }
    
    /**
     * 
     * @param parent
     * @param modelName
     * @param props
     */
    public EditInputSetDialog( Shell parent, InputSet inputSet) {
        super(parent);
        this.inputSet = inputSet;
    }
    
    /**
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     */
    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText(TITLE);  //$NON-NLS-1$
    }
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.window.Window#setShellStyle(int)
	 */
	@Override
	protected void setShellStyle(int newShellStyle) {
		super.setShellStyle(newShellStyle | SWT.RESIZE | SWT.MAX);

	}
	
    /**
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Control createDialogArea( Composite parent ) {
		setTitle(TITLE);
		setMessage(MESSAGE);
		
		Composite dialogComposite = (Composite) super.createDialogArea(parent);
        
        //------------------------------        
        // Set layout for the Composite
        //------------------------------        
        ((GridData)dialogComposite.getLayoutData()).grabExcessHorizontalSpace = true;
        ((GridData)dialogComposite.getLayoutData()).grabExcessVerticalSpace= true;
        ((GridData)dialogComposite.getLayoutData()).widthHint = 500;
        ((GridData)dialogComposite.getLayoutData()).heightHint = 250;
        
        editorPanel = new EditInputSetPanel(dialogComposite);
        
        setMappingAdapters(this.inputSet);
        MappingClass currentMappingClass = inputSet.getMappingClass();
        InputSetAdapter inputSetAdapter = new InputSetAdapter(currentMappingClass);
        editorPanel.setBusinessObject(inputSetAdapter);
        
        sizeScrolledPanel();
        
        return dialogComposite;
    }
    
    public void setMappingAdapters( InputSet inputSet ) {
        this.inputSet = inputSet;
        // obtain the root tree node that is being displayed in the mapping diagram
        IWorkbenchWindow window = UiPlugin.getDefault().getCurrentWorkbenchWindow();
        IEditorPart editorPart = window.getActivePage().getActiveEditor();
        if (editorPart instanceof ModelEditor) {
            ModelEditorPage editorPage = (ModelEditorPage)((ModelEditor)editorPart).getCurrentPage();
            if (editorPage instanceof DiagramEditor) {
                DiagramController controller = ((DiagramEditor)editorPage).getDiagramController();
                if (controller instanceof MappingDiagramController) {
                    // get the adapters from the diagram controller
                    TreeMappingAdapter mapping = ((MappingDiagramController)controller).getMappingAdapter();
                    IMappableTree mappableTree = ((MappingDiagramController)controller).getMappableTree();
                    editorPanel.setMappingAdapters(mapping, mappableTree);
                } else {
                    // need to get one somehow
                    EObject docRoot = inputSet.getMappingClass().getMappingClassSet().getTarget();
                    TreeMappingAdapter mapping = new TreeMappingAdapter(docRoot);
                    IMappableTree mappableTree = new DefaultMappableTree(docRoot);
                    editorPanel.setMappingAdapters(mapping, mappableTree);
                }
            }
        }
    }
}
