package org.teiid.designer.ui.bot.ext.teiid.editor;

import static org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable.syncExec;
import static org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory.widgetOfType;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.teiid.designer.ui.bot.ext.teiid.matcher.FigureMatcherFactory.withBounds;
import static org.teiid.designer.ui.bot.ext.teiid.matcher.FigureMatcherFactory.withLabel;

import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.ui.parts.GraphicalEditor;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefFigureCanvas;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefViewer;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.results.Result;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCTabItem;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.ui.IEditorReference;
import org.hamcrest.Matcher;
import org.jboss.tools.ui.bot.ext.gef.SWTBotGefFigure;
import org.jboss.tools.ui.bot.ext.helper.StyledTextHelper;
import org.teiid.designer.ui.bot.ext.teiid.matcher.IsTransformation;
import org.teiid.designer.ui.bot.ext.teiid.matcher.WaitForFigure;

/**
 * This class represents Model Editor in Teiid Designer perspective.
 * 
 * @author apodhrad
 *
 */
public class ModelEditor extends SWTBotEditor {

	public static final String TRANSFORMATION_DIAGRAM = "Transformation Diagram";
	public static final String MAPPING_DIAGRAM = "Mapping Diagram";
	public static final String TABLE_EDITOR = "Table Editor";

	private SWTBotGefFigureCanvas canvas;
	private SWTBotGefViewer viewer;

	public ModelEditor(SWTBotEditor editor, SWTWorkbenchBot bot) {
		this(editor.getReference(), bot);
	}

	public ModelEditor(IEditorReference editorReference, SWTWorkbenchBot bot) {
		super(editorReference, bot);
		Matcher matcher = widgetOfType(FigureCanvas.class);
		canvas = new SWTBotGefFigureCanvas((FigureCanvas) bot.widget(matcher, 0));

	}
	
	private GraphicalEditor getGraphicalEditor(String tabLabel) {
		final SWTBotCTabItem tabItem = showTab(tabLabel);
		GraphicalEditor graphicalEditor = syncExec(new Result<GraphicalEditor>() {

			@Override
			public GraphicalEditor run() {
				Object obj = tabItem.widget.getData();
				if (obj instanceof GraphicalEditor) {
					return (GraphicalEditor) obj;
				}
				return null;
			}
		});
		return graphicalEditor;
	}
	
	public SWTBotGefViewer getGraphicalViewer(String tabLabel) {
		final GraphicalEditor graphicalEditor = getGraphicalEditor(tabLabel);
		GraphicalViewer graphicalViewer = syncExec(new Result<GraphicalViewer>() {

			@Override
			public GraphicalViewer run() {
				Object obj = graphicalEditor.getAdapter(GraphicalViewer.class);
				if (obj instanceof GraphicalViewer) {
					return (GraphicalViewer) obj;
				}
				return null;
			}
		});

		return new SWTBotGefViewer(graphicalViewer);
	}

	public SWTBotCTabItem showTab(String label) {
		SWTBotCTabItem tabItem = bot.cTabItem(label);
		tabItem.activate();
		tabItem.show();
		return tabItem;
	}

	public void showTransformation() {
		viewer = getGraphicalViewer(TRANSFORMATION_DIAGRAM);
		viewer.editParts(IsTransformation.isTransformation()).get(0).select();
		viewer.clickContextMenu("Edit");
	}

	public void showMappingTransformation(String label) {
		viewer = getGraphicalViewer(MAPPING_DIAGRAM);
		viewer.getEditPart(label).select();
		viewer.clickContextMenu("Edit");
	}

	public CriteriaBuilder criteriaBuilder() {
		bot.toolbarButtonWithTooltip("Criteria Builder").click();
		SWTBotShell shell = bot.shell("Criteria Builder");
		shell.activate();
		return new CriteriaBuilder(shell);
	}

	public void setTransformationProcedureBody(String procedure) {
		String transformationText = getTransformation();
		transformationText = transformationText.replaceAll("<--.*-->;", procedure);
		bot.styledText(0).setText(transformationText);

		bot.styledText(0).navigateTo(2, procedure.length() / 2);
		StyledText textWidget = bot.styledText(0).widget;

		StyledTextHelper.mouseClickOnCaret(textWidget);
	}

	public String getTransformation() {
		return bot.styledText(0).getText();
	}

	public void setTransformation(String text) {
		bot.styledText(0).setText(text);
	}

	public void saveAndValidateSql() {
		clickButtonOnToolbar("Save/Validate SQL");
	}

	public void clickButtonOnToolbar(String button) {
		bot.toolbarButtonWithTooltip(button).click();
	}

	public void showTransformation(String label) {
		SWTBotGefFigure figureBot = figureWithLabel(label);
		editFigure(figureBot);
	}

	public void editFigure(SWTBotGefFigure figureBot) {
		Rectangle rectangle = figureBot.getAbsoluteBounds();
		canvas.mouseMoveLeftClick(rectangle.x + 1, rectangle.y + 1);
		canvas.contextMenu("Edit").click();
		bot().waitUntil(new DefaultCondition() {

			@Override
			public boolean test() throws Exception {
				try {
					bot.styledText();
					log.info("APLog: OK ");
					return true;
				} catch (WidgetNotFoundException wnfe) {
					return false;
				}
			}

			@Override
			public String getFailureMessage() {
				return "Process wasn't completed";
			}
		});
		bot().styledText();
	}

	public SWTBotGefFigure figureWithLabel(String label) {
		return figureWithLabel(label, 0);
	}

	public SWTBotGefFigure figureWithLabel(String label, int index) {
		Matcher matcher = withLabel(label);
		return new SWTBotGefFigure(figure(matcher, index));
	}

	public SWTBotGefFigure tFigure() {
		Matcher matcher = allOf(instanceOf(ImageFigure.class), withBounds(40, 60));
		return new SWTBotGefFigure(figure(matcher, 0));
	}

	public IFigure figure(Matcher matcher, int index) {
		WaitForFigure waitForFigure = new WaitForFigure(matcher, (FigureCanvas) canvas.widget);
		bot().waitUntil(waitForFigure);
		return waitForFigure.get(index);
	}
}
