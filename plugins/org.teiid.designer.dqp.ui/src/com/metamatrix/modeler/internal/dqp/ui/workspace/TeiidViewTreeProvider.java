/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.dqp.ui.workspace;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.teiid.adminapi.AdminComponentException;
import org.teiid.designer.runtime.Server;
import org.teiid.designer.runtime.ServerManager;
import org.teiid.designer.runtime.TeiidDataSource;
import org.teiid.designer.runtime.TeiidTranslator;
import org.teiid.designer.runtime.TeiidVdb;

import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.dqp.DqpPlugin;
import com.metamatrix.modeler.dqp.internal.workspace.SourceConnectionBinding;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.dqp.ui.DqpUiPlugin;

/**
 * Class provides content and label information for ConnectorBindings and ModelInfos in ConnectorsView
 * 
 * @since 5.0
 */
public class TeiidViewTreeProvider implements ITreeContentProvider, ILabelProvider {

    private ServerManager serverMgr;

    private boolean showModelMappings = false;

//    private boolean showWorkspaceItems = false;
    private boolean showTranslators = false;

    /**
     * @since 5.0
     */
    public TeiidViewTreeProvider() {
        this(true);
    }

    /**
     * @since 5.0
     */
    public TeiidViewTreeProvider( boolean showModelMappings ) {
        super();
        this.showModelMappings = showModelMappings;
    }

    /**
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
     * @since 4.2
     */
    public Object[] getChildren( Object parentElement ) {

        if (parentElement instanceof Server) {
            Object[] result = null;

            try {
                Collection<TeiidTranslator> translators = new ArrayList<TeiidTranslator>();
                Collection<TeiidDataSource> dataSources = ((Server)parentElement).getAdmin().getDataSources();
                Collection<TeiidVdb> vdbs = ((Server)parentElement).getAdmin().getVdbs();
                
                if( showTranslators ) {
                	translators = ((Server)parentElement).getAdmin().getTranslators();
                }
                
                
                Collection<Object> allObjects = new ArrayList<Object>();
                allObjects.addAll(translators);
                allObjects.addAll(dataSources);
                allObjects.addAll(vdbs);
                
                result = allObjects.toArray();
                
            } catch (AdminComponentException ace) {
                return new Object[0];
            } catch (Exception e) {
                DqpPlugin.Util.log(e);
                return new Object[0];
            }
   
            return result;

        } else if (parentElement instanceof TeiidTranslator) {
            @SuppressWarnings("unused")
			TeiidTranslator translator = (TeiidTranslator)parentElement;
            Collection<SourceConnectionBinding> bindings = Collections.emptyList();

            // TODO: Do we show any more here???
            if (showModelMappings && serverMgr != null) {
                try {
//                    SourceConnectionBindingsManager sourceBindingsMgr = connector.getType().getAdmin().getSourceConnectionBindingsManager();
//                    bindings = sourceBindingsMgr.getSourceBindings(connector);
                } catch (Exception e) {
                    return new Object[0];
                }
            }

            if (bindings.isEmpty()) {
                return new Object[0];
            }

            return bindings.toArray();
        } else if (parentElement instanceof SourceConnectionBinding) {
            return new Object[0];
        } 

        return new Object[0];
    }

    /**
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
     * @since 4.2
     */
    public Object getParent( Object element ) {
        return null;
    }

    /**
     * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
     * @since 4.2
     */
    public boolean hasChildren( Object element ) {
        return getChildren(element).length > 0;
    }

    /**
     * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
     * @since 4.2
     */
    public Image getImage( Object element ) {
        if (element instanceof Server) {
            if (serverMgr.isDefaultServer((Server)element)) {
                return DqpUiPlugin.getDefault().getAnImage(DqpUiConstants.Images.SET_DEFAULT_SERVER_ICON);
            }
            return DqpUiPlugin.getDefault().getAnImage(DqpUiConstants.Images.SERVER_ICON);
        }
        if (element instanceof TeiidTranslator) {
            return DqpUiPlugin.getDefault().getAnImage(DqpUiConstants.Images.CONNECTOR_BINDING_ICON);
        }
        
        if (element instanceof TeiidDataSource) {
            return DqpUiPlugin.getDefault().getAnImage(DqpUiConstants.Images.CONNECTION_SOURCE_ICON);
        }
        
        
        if (element instanceof TeiidVdb) {
            return DqpUiPlugin.getDefault().getAnImage(DqpUiConstants.Images.DEPLOY_VDB);
        }

        if (element instanceof SourceConnectionBinding) {
            return DqpUiPlugin.getDefault().getAnImage(DqpUiConstants.Images.SOURCE_CONNECTOR_BINDING_ICON);
        }
        return null;
    }

    /**
     * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
     * @since 4.2
     */
    public String getText( Object element ) {
        if (element instanceof Server) {
            return ((Server)element).getUrl();
        }
        if (element instanceof TeiidTranslator) {
            return ((TeiidTranslator)element).getName();
        }
        
        if (element instanceof TeiidDataSource) {
            return ((TeiidDataSource)element).getDisplayName();
        }
        
        if (element instanceof TeiidVdb) {
            return ((TeiidVdb)element).getName();
        }
        
        if (element instanceof SourceConnectionBinding) {
            SourceConnectionBinding binding = (SourceConnectionBinding)element;
            return binding.getModelName();
        }
        if (element instanceof String) {
            return (String)element;
        }
        return DqpUiConstants.UTIL.getString(I18nUtil.getPropertyPrefix(TeiidViewTreeProvider.class), new Object[] {
            element.toString(), element.getClass().getName()});
    }

    /**
     * Indicates if at least one binding is loaded in the configuration.
     * 
     * @return <code>true</code> if configuration contains at least one binding; <code>false</code>.
     * @since 4.3
     */
    public boolean containsBindings() {
        boolean result = false;

        if (this.serverMgr != null) {
            Object[] types = getElements(this.serverMgr);

            if ((types != null) && (types.length != 0)) {
                for (int i = 0; i < types.length; ++i) {
                    if (hasChildren(types[i])) {
                        result = true;
                        break;
                    }
                }
            }
        }

        return result;
    }

    /**
     * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
     * @since 4.2
     */
    public Object[] getElements( Object inputElement ) {
        if (inputElement instanceof ServerManager) {
            serverMgr = (ServerManager)inputElement;
            return serverMgr.getServers().toArray();
        }

        return new Object[0];
    }

    /**
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
     * @since 4.2
     */
    public void dispose() {
    }

    /**
     * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object,
     *      java.lang.Object)
     * @since 4.2
     */
    public void inputChanged( Viewer viewer,
                              Object oldInput,
                              Object newInput ) {
    }

    /**
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
     * @since 4.2
     */
    public void addListener( ILabelProviderListener listener ) {
    }

    /**
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
     * @since 4.2
     */
    public boolean isLabelProperty( Object element,
                                    String property ) {
        return false;
    }

    /**
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
     * @since 4.2
     */
    public void removeListener( ILabelProviderListener listener ) {
    }

    public void setShowTranslators( boolean value ) {
        this.showTranslators = value;
    }

}
