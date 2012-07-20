package org.teiid.designer.ui.bot.ext.teiid.editor;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.jboss.tools.ui.bot.ext.Timing;
import org.jboss.tools.ui.bot.ext.helper.StyledTextHelper;
import org.teiid.designer.ui.bot.ext.teiid.SWTBotTeiidCanvas;
import org.teiid.designer.ui.bot.ext.teiid.SWTTeiidBot;

public class ModelEditor extends Editor {

	private ModelEditor(String name){
		super(name);
	}
	
	public static ModelEditor getInstance(String name){
		ModelEditor editor = new ModelEditor(name);
		editor.show();
		return editor;
	}
	
	public void showTransformation(){
		getBot().sleep(Timing.time1S());
		SWTTeiidBot teiidBot = new SWTTeiidBot();
		SWTBotTeiidCanvas canvas = teiidBot.getTeiidCanvas(0);
		canvas.tFigure().doubleClick();
	}
	
	public void showMappingTransformation(String objectName) {
		getBot().sleep(Timing.time1S());
		SWTTeiidBot teiidBot = new SWTTeiidBot();
		SWTBotTeiidCanvas canvas = teiidBot.getTeiidCanvas(0);	
		canvas.mappingFigure(objectName).doubleClick();
	}
	
	public CriteriaBuilder criteriaBuilder(){
		SWTBot bot = getEditor().bot();
		bot.toolbarButtonWithTooltip("Criteria Builder").click();
		
		SWTBotShell shell = bot.shell("Criteria Builder");
		shell.activate();
		return new CriteriaBuilder(shell);
	}
	
	public void setTransformationProcedureBody(String procedure){
		String transformationText = getTransformation();
		transformationText = transformationText.replaceAll("<--.*-->;", procedure);
		getBot().styledText(0).setText(transformationText);
		
		getBot().styledText(0).navigateTo(2, procedure.length() / 2);
		StyledText textWidget = getBot().styledText(0).widget;

		StyledTextHelper.mouseClickOnCaret(textWidget);
	}
	
	public String getTransformation(){
		return getBot().styledText(0).getText();
	}
	
	public void setTransformation(String text){
		getBot().styledText(0).setText(text);
	}
	
	public void saveAndValidateSql() {
		clickButtonOnToolbar("Save/Validate SQL");
	}
	
	public void clickButtonOnToolbar(String button) {
		SWTBot bot = getEditor().bot();
		bot.toolbarButtonWithTooltip(button).click();
	}
}
