/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.advisor.ui.actions;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;

public class AdvisorActionProvider implements ILabelProvider, ITreeContentProvider {

	public AdvisorActionProvider() {
		super();
	}

	
	@Override
	public void addListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
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

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub

	}

	@Override
	public Image getImage(Object element) {
		if( element instanceof AdvisorActionInfo ) {
			return AdvisorActionFactory.getImage((AdvisorActionInfo)element);
		}
		return null;
	}

	@Override
	public String getText(Object element) {
		if( element instanceof AdvisorActionInfo ) {
			return ((AdvisorActionInfo)element).getDisplayName();
		}
		return null;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		if ((inputElement instanceof AdvisorActionInfo[])) {
			return (AdvisorActionInfo[])inputElement;
		}
		return null;
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if ((parentElement instanceof AdvisorGuides)) {
			return new Object[0];
		}
		return new Object[0];
	}

	@Override
	public Object getParent(Object element) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		return getChildren(element).length > 0;
	}

}
