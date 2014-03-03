/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.advisor.ui.actions;

import java.util.Properties;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.teiid.designer.advisor.ui.util.DesignerPropertiesMapperUtil;

public class AdvisorActionProvider implements ILabelProvider, ITreeContentProvider {
	Properties designerProperties;

	public AdvisorActionProvider() {
		super();
	}
	
	public void setProperties(Properties properties) {
		designerProperties = properties;
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
			return addPropertyToDisplayName((AdvisorActionInfo)element);
			//return ((AdvisorActionInfo)element).getDisplayName();
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
	
	private String addPropertyToDisplayName(AdvisorActionInfo actionInfo) {
		StringBuilder sb = new StringBuilder(actionInfo.getDisplayName());
		String valueLabel = DesignerPropertiesMapperUtil.getActionsValueLabel(actionInfo.getId(), designerProperties);
		if( valueLabel != null && valueLabel.length() > 0 ) {
			if( ! DesignerPropertiesMapperUtil.IGNORE.equals(valueLabel) ) {
				sb.append(" >>    ").append("( " + valueLabel + " )"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
		} else {
			sb.append(" >>    ").append("<undefined>"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return sb.toString();
	}

}
