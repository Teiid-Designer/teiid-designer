/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.ui.tree;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import com.metamatrix.ui.internal.InternalUiConstants;

/** Support for trees with many, many children.
  *  Using this as a content provider will automatically break up sets
  *  of children into groups of MAX_NODES or less.
  *  
  * Note that some special additions to client code may be necessary.
  *  Here are some times to consider:
  * <ul>
  * <LI>A tree that needs to refreshed or be otherwise dynamic will need to 
  *     call inputChanged on this class when something happens, to clear away 
  *     cached TreeSplitters.  Then the tree will likely need to be refreshed
  *     as well.
  * <LI>Any ViewerFilters need to allow TreeSplitter objects through.
  * <LI>CheckboxTreeViewers need to have a strategy to deal with TreeSplitters:
  *     what does selection of a splitter mean?  Users can call
  *     TreeSplitter.isMaterialized to determine if the contents below have
  *     been expanded, and behave accordingly (such as assuming its children
  *     have the same checked status as the TreeSplitter).
  * </ul>
  * 
  * @see com.metamatrix.ui.tree.TreeViewerUtil
  * @see com.metamatrix.ui.tree.TreeSplitter
  * @author PForhan
  */
public class LargeTreeContentProvider implements ITreeContentProvider
{
    //
    // Class constants:
    //
    public static final int MAX_NODES = 100;

    //
    // Instance variables:
    //
    private ITreeContentProvider cProvider;
    private final ILabelProvider lProvider;
    private Map parentToSplitArray = new HashMap();

    //
    // Constructors:
    //

    /** Create a content provider that automatically splits up large trees
      * 
      * @param labelProvider The label provider to use when generating the
      *   names of TreeSplitter nodes.
      * @param realProvider The underlying content provider.
      */
    public LargeTreeContentProvider(ILabelProvider labelProvider, ITreeContentProvider realProvider) {
        lProvider = labelProvider;
        cProvider = realProvider;
    }

    //
    // Implementation of ITreeContentProvider methods:
    //
    public Object[] getChildren(Object parentElement) {
        Object[] rv = (Object[]) parentToSplitArray.get(parentElement);
        
        if (rv != null) {
            // have split this before, use what we have:
            return rv;
        } // endif
        
        if (parentElement instanceof TreeSplitter) {
            TreeSplitter ts = (TreeSplitter) parentElement;
            rv = ts.getChildren();

        } else {
            rv = cProvider.getChildren(parentElement);
        } // endif

        if (rv == null) {
            return TreeViewerUtil.EMPTY_OBJECT_ARRAY;
        } // endif

        return splitIfNecessary(parentElement, rv);
    }

    public Object[] splitIfNecessary(Object parentElement, Object[] children) {
        if (children.length > MAX_NODES) {
            children = TreeViewerUtil.split(this, parentElement, children, 0, children.length);
            parentToSplitArray.put(parentElement, children);
//            System.out.println("children len is "+children.length);
        } // endif

        return children;
    }

    /** Used to provide labels to new TreeSplitters.
      * 
      * @param c1 Object at beginning of range
      * @param c2 Object at end of range
      * @param count Count of objects in range
      * @return an i18'd String using the parameters as values.
      * TODO pforhan -- I hate, hate, hate the way this is (and yes, I wrote it) -- it makes me pass LTCPs to TreeSplitters and TreeViewerUtil...
      */
    String getLabelString(Object c1, Object c2, int count) {
        return InternalUiConstants.Util.getString("LargeTreeContentProvider.splitterLabel", lProvider.getText(c1), lProvider.getText(c2), new Integer(count)); //$NON-NLS-1$
    }

    public Object[] getElements(Object inputElement) {
        Object[] rv = (Object[]) parentToSplitArray.get(inputElement);
        
        if (rv != null) {
            // have split this before, use what we have:
            return rv;
        } // endif

        if (inputElement instanceof TreeSplitter) {
            TreeSplitter ts = (TreeSplitter) inputElement;
            rv = ts.getChildren();

        } else {
            rv = cProvider.getElements(inputElement);
        } // endif

        return splitIfNecessary(inputElement, rv);
    }

    public Object getParent(Object element) {
        if (element instanceof TreeSplitter) {
            TreeSplitter ts = (TreeSplitter) element;
            return ts.getParent();
        } // endif

        // not a splitter, delegate:
        return cProvider.getParent(element);
    }

    public boolean hasChildren(Object element) {
        if (element instanceof TreeSplitter) {
            TreeSplitter ts = (TreeSplitter) element;
            return ts.getChildCount() > 0; // should always be true...
        } // endif

        // not a splitter, delegate:
        return cProvider.hasChildren(element);
    }

    public void dispose() {
        parentToSplitArray.clear();
        cProvider.dispose();
        cProvider = null;
    }

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        parentToSplitArray.clear();
        cProvider.inputChanged(viewer, oldInput, newInput);
    }
    
//    public static void main(String[] args) {
//        Display display = Display.getDefault();
//        Shell shell = new Shell(display);
//        shell.setText("Big Tree Testing"); //$NON-NLS-1$
//        shell.setLayout(new FillLayout());
//        final CheckboxTreeViewer tv = new CheckboxTreeViewer(shell);
//        final Object root = "Root Object"; //$NON-NLS-1$
//        ITreeContentProvider content = new ITreeContentProvider() {
//            final int LARGE_DATA_SET_SIZE = 21102;
//            final Object[] children;
//            {
//                children = new Object[LARGE_DATA_SET_SIZE];
//                for (int i = 0; i < children.length; i++) {
//                    children[i] = getRandomString(5);//+"; Child #"+i;
//                } // endfor
//                
//                Arrays.sort(children);
//                System.out.println("last thing is "+children[children.length -1]); //$NON-NLS-1$
//            }
//            private String getRandomString(int len) {
//                StringBuffer sb = new StringBuffer(len);
//                for (int i = 0; i < len; i++) {
//                    sb.append((char)('a'+(Math.random()*26)));
//                } // endfor
//
//                return sb.toString();
//            }
//            public Object[] getChildren(Object parentElement) {
//                if (parentElement != root) {
//                    return null;
//                } // endif
//
//                return children;
//            }
//            public Object getParent(Object element) {
//                if (element == root) {
//                    return null;
//                } // endif
//
//                return root;
//            }
//            public boolean hasChildren(Object element) {
//                if (element == root) {
//                    return true;
//                } // endif
//
//                return false;
//            }
//            public Object[] getElements(Object inputElement) {
//                return getChildren(inputElement);
//            }
//            public void dispose() {
//            }
//            public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
//            }
//        };
//        tv.setContentProvider(new LargeTreeContentProvider((ILabelProvider) tv.getLabelProvider(), content));
//        tv.setInput(root);
//        tv.addCheckStateListener(new ICheckStateListener() {
//            public void checkStateChanged(CheckStateChangedEvent event) {
//                TreeViewerUtil.setSubtreeChecked(tv, event.getElement(), event.getChecked(), true);
//            }
//        });
//
//        shell.pack();
//        shell.open ();
//        while (!shell.isDisposed ()) {
//            if (!display.readAndDispatch ()) display.sleep ();
//        }
//        display.dispose ();
//    }
}
