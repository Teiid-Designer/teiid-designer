/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.modelgenerator.ldap.ui.wizards.pages.columns;

import java.util.HashMap;
import java.util.Map;
import org.apache.directory.studio.common.core.jobs.StudioProgressMonitor;
import org.apache.directory.studio.connection.core.event.ConnectionEventRegistry;
import org.apache.directory.studio.ldapbrowser.core.jobs.InitializeAttributesRunnable;
import org.apache.directory.studio.ldapbrowser.core.jobs.InitializeChildrenRunnable;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.IValue;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.teiid.designer.modelgenerator.ldap.ui.wizards.AbstractLdapContentProvider;
import org.teiid.designer.modelgenerator.ldap.ui.wizards.ILdapAttributeNode;
import org.teiid.designer.modelgenerator.ldap.ui.wizards.ILdapEntryNode;
import org.teiid.designer.modelgenerator.ldap.ui.wizards.LdapImportWizardManager;

/**
 *
 */
public class LdapEntryContentProvider extends AbstractLdapContentProvider {

    /**
     * @param manager
     */
    public LdapEntryContentProvider(LdapImportWizardManager manager) {
        super(manager);
    }

    @Override
    public Object[] getElements(Object inputElement) {
        return getChildren(getImportManager());
    }

    @Override
    public Object[] getChildren(Object parentElement) {
        if (parentElement instanceof LdapImportWizardManager) {
            return getImportManager().getSelectedEntries().toArray();
        }

		if (parentElement instanceof ILdapEntryNode) {
			ILdapEntryNode parentNode = (ILdapEntryNode) parentElement;
			IEntry entry = parentNode.getEntry();

			if (! entry.isChildrenInitialized()) {
			    // Making sure children are initialized
			    NullProgressMonitor ipm = new NullProgressMonitor();
                StudioProgressMonitor monitor = new StudioProgressMonitor(ipm);
                try {
                    ConnectionEventRegistry.suspendEventFiringInCurrentThread();
                    InitializeChildrenRunnable runnable = new InitializeChildrenRunnable(true, entry);
                    runnable.run(monitor);
                } finally {
                    ConnectionEventRegistry.resumeEventFiringInCurrentThread();
                }
			}

			IEntry[] children = entry.getChildren();
			if (children == null || children.length == 0)
			    return EMPTY_ARRAY;

			Map<Integer, ILdapAttributeNode> childAttributes = new HashMap<Integer, ILdapAttributeNode>();

			for (IEntry child : children) {
			    // Making sure attributes are initialized
			    if (! child.isAttributesInitialized()) {
			        NullProgressMonitor ipm = new NullProgressMonitor();
			        StudioProgressMonitor monitor = new StudioProgressMonitor(ipm);
			        try {
			            ConnectionEventRegistry.suspendEventFiringInCurrentThread();
			            InitializeAttributesRunnable.initializeAttributes(child, monitor);
			        } finally {
			            ConnectionEventRegistry.resumeEventFiringInCurrentThread();
			        }
			    }

			    for (IAttribute attribute : child.getAttributes()) {
			        ILdapAttributeNode newAttribute = getImportManager().newAttribute(parentNode, attribute);
                    /*
                     * Check whether this attribute has already been added to the
                     * child attribute map. We want the existing attribute in the set
                     * in order to increment its cost statistics
                     */
                    ILdapAttributeNode childAttribute = childAttributes.get(newAttribute.hashCode());
                    if (childAttribute == null) {
                        childAttributes.put(newAttribute.hashCode(), newAttribute);
                        childAttribute = newAttribute;
                    }

                    IValue[] values = attribute.getValues();
                    if (values.length == 0) {
                        childAttribute.incrementNullValueCount();
                        continue;
                    }

                    /*
                     * Need to apply the values of this attribute in order
                     * to determine the number of distinct values.
                     */
                    
                    for (IValue value : values) {
                        childAttribute.addValue(value);
                    }
			    }
			}

			return childAttributes.values().toArray();
		}

        return EMPTY_ARRAY;
    }

    @Override
    public Object getParent(Object element) {
        if (element instanceof ILdapAttributeNode)
            return ((ILdapAttributeNode)element).getAssociatedEntry();

        return null;
    }

    @Override
    public boolean hasChildren(Object element) {
        if (element instanceof ILdapEntryNode)
            return true;

        return false;
    }

}
