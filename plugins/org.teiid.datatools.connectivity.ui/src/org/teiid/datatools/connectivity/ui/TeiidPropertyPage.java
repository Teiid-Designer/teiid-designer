package org.teiid.datatools.connectivity.ui;

import org.eclipse.datatools.connectivity.ui.wizards.ExtensibleProfileDetailsPropertyPage;
import org.eclipse.datatools.help.ContextProviderDelegate;
import org.eclipse.help.IContext;
import org.eclipse.help.IContextProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class TeiidPropertyPage extends ExtensibleProfileDetailsPropertyPage 
    implements IContextProvider {

    private ContextProviderDelegate contextProviderDelegate = new ContextProviderDelegate(
            Activator.getDefault().getBundle().getSymbolicName());

    public TeiidPropertyPage() {
        super(ITeiidDriverConstants.TEIID_CATEGORY);
    }

    @Override
    public IContext getContext( Object target ) {
        return contextProviderDelegate.getContext(target);
    }

    @Override
    public int getContextChangeMask() {
        return contextProviderDelegate.getContextChangeMask();
    }

    @Override
    public String getSearchExpression( Object target ) {
        return contextProviderDelegate.getSearchExpression(target);
    }

    @Override
    protected Control createContents( Composite parent ) {
        Control contents = super.createContents(parent);
        return contents;
    }
}
