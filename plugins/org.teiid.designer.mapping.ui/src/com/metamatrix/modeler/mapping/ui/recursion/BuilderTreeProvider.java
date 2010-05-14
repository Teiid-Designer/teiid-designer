/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.mapping.ui.recursion;

import java.util.List;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import com.metamatrix.metamodels.transformation.MappingClass;
import com.metamatrix.metamodels.transformation.MappingClassColumn;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.query.internal.ui.builder.util.ElementViewerFactory;

/**
 * BuilderTreeProvider is the modeler's content and label provider for the Criteria and
 * Expression builder dialogs in mapping.ui .  Creating this object will cause it to be
 * hooked up properly to the ElementViewerFactory.
 * 
 * Dan's sugg: In rewriting the transformation.ui version to work for Choice Node/Mapping classes
 *             a good start is to expect that the args to these methods will be either
 *             MappingClass or MappingClassColumn objects.
 *             Also, expet to drop the 'GroupSymbol'
 */
public class BuilderTreeProvider implements ITreeContentProvider, ILabelProvider {

    ILabelProvider emfLabelProvider;
    MappingClass mcCurrent;
    int iCounter = 0;
    
    /**
     * Construct an instance of BuilderTreeProvider.
     */
    public BuilderTreeProvider() {
//        System.out.println("[BuilderTreeProvider.ctor] TOP"); //$NON-NLS-1$
        ElementViewerFactory.setContentProvider(this);
        ElementViewerFactory.setLabelProvider(this);
        emfLabelProvider = ModelUtilities.getEMFLabelProvider();
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
     */
    public Object[] getChildren(Object parentElement) {
        Object[] result = new Object[0];
        
        if ( parentElement instanceof MappingClass ) {
//            System.out.println("[BuilderTreeProvider.getChildren] About to return 'getColumns'"); //$NON-NLS-1$
            result = ((MappingClass)parentElement).getColumns().toArray();
        } else {
//            System.out.println("[BuilderTreeProvider.getChildren] parentElement NOT a MappingClass, no action"); //$NON-NLS-1$
        }
        return result;

    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
     */
    public Object getParent( Object element ) {
        Object oResult = null;

        if ( element instanceof MappingClassColumn ) {
            oResult = ((MappingClassColumn)element).getMappingClass();
        } 
        else        
        if ( element instanceof EObject ) {
          oResult = ((EObject)element).eContainer();

          if ( oResult == null ) {
              oResult = ModelUtilities.getModelResourceForModelObject((EObject) element);
          }
      }
        
//      return result;
        
        return oResult;


    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
     */
    public boolean hasChildren(Object element) {
//        System.out.println("[BuilderTreeProvider.hasChildren] TOP: element is: " + element ); //$NON-NLS-1$
        return getChildren(element).length > 0;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
     */
    public Image getImage(Object obj) {

        return emfLabelProvider.getImage( obj );

    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
     */
    public String getText(Object obj) {
//        System.out.println("[BuilderTreeProvider.getText] TOP: Arg is: " + obj ); //$NON-NLS-1$
        
//        System.out.println("[BuilderTreeProvider.getText] TOP: About to return: " + emfLabelProvider.getText( obj ) ); //$NON-NLS-1$        
        return emfLabelProvider.getText( obj );

//=============================================================
////        if ( obj instanceof GroupSymbol ) {
////            GroupSymbol symbol = (GroupSymbol) obj;
////            
////            String result = null;
////            // if symbol has a non-null definition, then it is an alias:
////            if ( symbol.getDefinition() == null ) {
////                result = symbol.getName();
////            } else {
////                result = symbol.getDefinition();
////                result += " AS " + symbol.getName(); //$NON-NLS-1$  "AS" is SQL, not English - do not internationalize
////            }
////            
////            return result;
////        } else if ( obj instanceof ModelResource ) {
////            return ModelerCore.getModelEditor().getModelName((ModelResource) obj);
////        }
////        return emfLabelProvider.getText(obj);
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
     */
    public Object[] getElements( Object inputElement ) {
//        System.out.println("[BuilderTreeProvider.getElements] TOP"); //$NON-NLS-1$
        Object[] result = new Object[] { mcCurrent };
//        System.out.println("[BuilderTreeProvider.getElements] About to return" + result[0] ); //$NON-NLS-1$
        iCounter++;
        
        if ( iCounter > 100 ) {
            Thread.dumpStack();
        }
        return result;                
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
     */
    public void dispose() {

    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
     */
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
//        System.out.println("[BuilderTreeProvider.inputChanged] TOP"); //$NON-NLS-1$
       
        if ( newInput != null && newInput instanceof List) {
            mcCurrent = (MappingClass)((List)newInput).get(0);
        }

    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
     */
    public void addListener(ILabelProviderListener listener) {

    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
     */
    public boolean isLabelProperty(Object element, String property) {
        return emfLabelProvider.isLabelProperty(element, property);
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
     */
    public void removeListener(ILabelProviderListener listener) {

    }

}
