package org.teiid.designer.ui.bot.ext.teiid.perspective;

import org.teiid.designer.ui.bot.ext.teiid.view.ModelExplorerView;
import org.teiid.designer.ui.bot.ext.teiid.view.TeiidInstanceView;

/**
 * Represents a Teiid perspective. It is a singleton. 
 * 
 * @author Lucia Jelinkova
 *
 */
public class TeiidPerspective extends Perspective {

	private static final String NAME = "Teiid Designer";
	
	private static final TeiidPerspective INSTANCE = new TeiidPerspective();
	
	private TeiidInstanceView teiidInstanceView;
	
	private ModelExplorerView modelExplorerView;
	
	private TeiidPerspective() {
		super(NAME);
	}
	
	public static final TeiidPerspective getInstance(){
		INSTANCE.open();
		return INSTANCE;
	}
	
	public TeiidInstanceView getTeiidInstanceView(){
		if (teiidInstanceView == null){
			teiidInstanceView = new TeiidInstanceView();
		}
		teiidInstanceView.show();
		return teiidInstanceView;
	}
	
	public ModelExplorerView getModelExplorerView() {
		if (modelExplorerView == null){
			modelExplorerView = new ModelExplorerView();
		}
		modelExplorerView.activate();
		return modelExplorerView;
	}
}
