/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ui.properties.udp;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.metamodels.core.ModelAnnotation;
import org.teiid.designer.ui.UiConstants;
import org.teiid.designer.ui.UiPlugin;
import org.teiid.designer.ui.editors.ModelEditorManager;
import org.teiid.designer.ui.properties.PropertyEditorFactory;
import org.teiid.designer.ui.properties.ReadOnlyPropertyDescriptor;
import org.teiid.designer.ui.viewsupport.ModelUtilities;


/**
 * ExtensionPropertyDescriptor is a wrapper for EMF property descriptors for an Extension object.
 * The wrapper allows extension properties to be editable alongside the normal model object 
 * properties.  The wrapper holds a reference to the Extension object and it's delegate 
 * IPropertyDescriptor.  All calls are delegated to EMF except the getId() method, which returns
 * the wrapper itself and allows the ModelObjectPropertySource to recognize that this Descriptor
 * is for the extensions object rather than the real model object.  This allows the source to
 * get and set property values on the correct object.
 */
public class ExtensionPropertyDescriptor implements IPropertyDescriptor {

    // ===================================
    // Static attributes

    /** the category name for all extension properties that do not have explicit category names */
    private static final String EXTENSIONS_CATEGORY = UiConstants.Util.getString("ExtensionPropertyDescriptor.category");  //$NON-NLS-1$

    public static final AdapterFactoryContentProvider extensionPropertySourceProvider 
        = ModelUtilities.getEmfAdapterFactoryContentProvider();


    private EObject extension;
    private EObject extendedObject;
    private IPropertyDescriptor delegate;
    private boolean showReadOnlyDialog = true;

    
    // =====================================
    // Constructors

    /**
     * Construct an instance of ExtensionPropertyDescriptor.
     */
    public ExtensionPropertyDescriptor(EObject extensionObject, EObject extendedObject, IPropertyDescriptor delegate) {
        this.extension = extensionObject;
        this.extendedObject = extendedObject;
        this.delegate = delegate;
    }

    // =====================================
    // Specialized methods

    public void setShowReadOnlyDialog(boolean enable) {
        showReadOnlyDialog = enable;
    }
    
    /**
     * Obtain the extension object that was passed into this wrapper upon construction.
     */
    public EObject getExtensionObject() {
        return this.extension;
    }
    
    /**
     * Obtain the IPropertyDescriptor that was passed into this wrapper upon construction.
     */
    public IPropertyDescriptor getDelegate() {
        return this.delegate;
    }

    /* (non-Javadoc)
     * Overridden to return this instead of the delegate's ID, so the PropertySource can recognise
     * that this is an extension property
     * @see org.eclipse.ui.views.properties.IPropertyDescriptor#getId()
     */
    @Override
	public Object getId() {
        return this;
    }

    /**
     * Return the Id of the delegate
     * @see org.eclipse.ui.views.properties.IPropertyDescriptor#getId()
     */
    public Object getDelegateId() {
        return delegate.getId();
    }

    @Override
    public String toString() {
        return delegate.getId().toString();
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.views.properties.IPropertyDescriptor#createPropertyEditor(org.eclipse.swt.widgets.Composite)
     */
    @Override
	public CellEditor createPropertyEditor(Composite parent) {
        // check failure conditions: ModelResource is null, or read-only, or not open in an editor 
        ModelResource modelResource = ModelUtilities.getModelResourceForModelObject(extendedObject);

        if ( modelResource == null ) {
            // if the modelResource is null, we can't edit the properties
            return null;
        }

        if (ModelUtil.isIResourceReadOnly(modelResource.getResource())) {
            if ( showReadOnlyDialog ) {
                Shell shell = UiPlugin.getDefault().getCurrentWorkbenchWindow().getShell();
                MessageDialog.openError(shell, ReadOnlyPropertyDescriptor.READ_ONLY_TITLE, ReadOnlyPropertyDescriptor.READ_ONLY_MESSAGE);
            }
            return null;
        }
        
        IFile file = (IFile) modelResource.getResource();
        if ( file != null ) {
            if ( ! ModelEditorManager.isOpen(file) ) {
                // can't modify a property value on an EObject if it's ModelEditor is not open.
                Shell shell = UiPlugin.getDefault().getCurrentWorkbenchWindow().getShell();
                if ( MessageDialog.openQuestion(shell, ModelEditorManager.OPEN_EDITOR_TITLE, ModelEditorManager.OPEN_EDITOR_MESSAGE) ) {
                    ModelEditorManager.open(extendedObject, true);
                }
                return null;
            }
        }
        
        CellEditor result = null;
        
        if (this.extension instanceof ModelAnnotation) {
            AdapterFactory factory = ExtensionPropertyDescriptor.extensionPropertySourceProvider.getAdapterFactory();
            IItemPropertySource itemPropertySource = (IItemPropertySource)factory.adapt(this.extension, IItemPropertySource.class);
            
            if (itemPropertySource != null) {
                IItemPropertyDescriptor itemDescriptor = itemPropertySource.getPropertyDescriptor(this.extension, this.delegate.getId());
                
                if (itemDescriptor != null) {
                    if (!itemDescriptor.canSetProperty(this.extension)) {
                         return null;
                    }
                    Object feature = itemDescriptor.getFeature(this.extension);

                    if (feature instanceof EStructuralFeature) {
                        result = PropertyEditorFactory.createPropertyEditor(parent, itemDescriptor, this, this.extension);
                    }
                }
            }
        }
        
        if (result == null) {
            result = this.delegate.createPropertyEditor(parent);
        }
        
        return result;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.views.properties.IPropertyDescriptor#getCategory()
     */
    @Override
	public String getCategory() {
        if ( delegate.getCategory() == null ) {
            return EXTENSIONS_CATEGORY;
        }
        return delegate.getCategory();
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.views.properties.IPropertyDescriptor#getDescription()
     */
    @Override
	public String getDescription() {
        return delegate.getDescription();
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.views.properties.IPropertyDescriptor#getDisplayName()
     */
    @Override
	public String getDisplayName() {
        return delegate.getDisplayName();
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.views.properties.IPropertyDescriptor#getFilterFlags()
     */
    @Override
	public String[] getFilterFlags() {
        return delegate.getFilterFlags();
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.views.properties.IPropertyDescriptor#getHelpContextIds()
     */
    @Override
	public Object getHelpContextIds() {
        return delegate.getFilterFlags();
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.views.properties.IPropertyDescriptor#getLabelProvider()
     */
    @Override
	public ILabelProvider getLabelProvider() {
        return delegate.getLabelProvider();
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.views.properties.IPropertyDescriptor#isCompatibleWith(org.eclipse.ui.views.properties.IPropertyDescriptor)
     */
    @Override
	public boolean isCompatibleWith(IPropertyDescriptor anotherProperty) {
        return delegate.isCompatibleWith(anotherProperty);
    }

}
