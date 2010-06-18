package org.teiid.designer.datatools.ui.dialogs;

import java.util.ArrayList;

import org.eclipse.datatools.connectivity.ICategory;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.ProfileManager;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osgi.util.TextProcessor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

public class ConnectionProfileTreeProvider extends LabelProvider
	 implements ITreeContentProvider {    /**
     * @since 5.0
     */
    public ConnectionProfileTreeProvider() {
        super();
    }

	@Override
	public Object[] getChildren(Object parentElement) {
		Object[] children = new Object[0];
		
		if( parentElement instanceof ProfileManager ) {
			return ((ProfileManager)parentElement).getRootCategories();
		}
		
		if (parentElement instanceof ICategory) {
			ICategory icat = (ICategory) parentElement;
			ArrayList arry = new ArrayList();

			arry.addAll(icat.getChildCategories());
			arry.addAll(icat.getAssociatedProfiles());

			children = arry.toArray();
		}
		else if (parentElement instanceof IConnectionProfile) {
			children = new Object[0];
		}
		return children;
	}

	@Override
	public Object getParent(Object element) {
		Object parent = null;

		if (element instanceof ICategory) {
			parent = ((ICategory) element).getParent();
		} else if (element instanceof IConnectionProfile) {
			parent = ((IConnectionProfile) element).getCategory();
		}

		return parent;
	}

	@Override
	public boolean hasChildren(Object element) {
		return getChildren(element).length > 0;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Image getImage(Object element) {
		Image image;
		if (element instanceof ICategory) {
			image = PlatformUI.getWorkbench().getSharedImages().getImage(
					ISharedImages.IMG_OBJ_FOLDER);
		} else if (element instanceof IConnectionProfile) {
			image = PlatformUI.getWorkbench().getSharedImages().getImage(
					ISharedImages.IMG_OBJ_ELEMENT);
		} else {
			image = null;
		}
		return image;
	}

	@Override
	public String getText(Object element) {
		String text = null;
		if (element instanceof ProfileManager) {
			text = "Profile Manager"; //$NON-NLS-1$
		} else if (element instanceof IConnectionProfile) {
			IConnectionProfile profile = (IConnectionProfile) element;
			text = profile.getName();
		} else if (element instanceof ICategory) {
			text = ((ICategory)element).getName();
		} else {
			text = super.getText(element);
		}
		
		if (text != null && text.trim().length() > 0) {
			text = TextProcessor.process(text);
		}
		
		return text;
	}

	@Override
	public void addListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub
		
	}

	
	
}
