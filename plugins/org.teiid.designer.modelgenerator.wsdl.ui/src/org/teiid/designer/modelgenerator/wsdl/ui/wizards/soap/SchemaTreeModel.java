/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.modelgenerator.wsdl.ui.wizards.soap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.impl.XSDElementDeclarationImpl;
import org.eclipse.xsd.impl.XSDParticleImpl;

public class SchemaTreeModel {

	Map<Object, SchemaNode> mapNode = new HashMap<Object, SchemaNode>();
	String rootPath = null;

	public String getRootPath() {
		return this.rootPath;
	}

	public void setRootPath(String rootPath) {
		this.rootPath = rootPath;
	}

	public Map<Object, SchemaNode> getMapNode() {
		return this.mapNode;
	}

	public void setMapNode(Map<Object, SchemaNode> mapNode) {
		this.mapNode = mapNode;
	}
	
	public String getRootNodeXpath() {
		for (Object nodeKey:this.mapNode.keySet()){
			SchemaNode node = this.mapNode.get(nodeKey);
			if (node.isRoot){
				return node.getRelativeXpath();
			}
		}
		
		return ""; //$NON-NLS-1$
	}
	
	public String determineRootPath(){
		StringBuilder commonRoot = new StringBuilder();
		
		List<String> segmentList = new ArrayList();
		for (Object nodeKey:this.mapNode.keySet()){
			SchemaNode node = this.mapNode.get(nodeKey);
			if (node.children.isEmpty()){
				String path = node.getFullPath();
				if (!path.equals("")){ //$NON-NLS-1$
					segmentList.add(path);
				}
			}
		}
		
		String[][] segments = new String[segmentList.size()][];
		for(int i = 0; i < segmentList.size(); i++){
			segments[i] = segmentList.get(i).split("/");  //$NON-NLS-1$
		}
		for(int j = 0; j < segments[0].length; j++){
			String thisSegment = segments[0][j]; 
			boolean allMatched = true; 
			for(int i = 1; i < segments.length && allMatched; i++){ 
				if(segments[i].length < j){
					allMatched = false; 
					break; 
				}
				allMatched &= segments[i][j].equals(thisSegment); 
			}
			if(allMatched){ 
				commonRoot.append("/").append(thisSegment) ; //$NON-NLS-1$
			}else{
				break;
			}
		}
		//Change any double slashes to single slashes
		commonRoot = new StringBuilder(commonRoot.toString().replaceAll("//", "/")); //$NON-NLS-1$ //$NON-NLS-2$
		return commonRoot.toString();
	}
	
	
	public class SchemaNode {
		protected Object element;
		protected SchemaNode parent;
		protected boolean isRoot = false;
		protected Collection<SchemaNode> children = new ArrayList<SchemaTreeModel.SchemaNode>();

		public SchemaNode() {
			parent = null;
			element = null;
		}

		public SchemaNode(Object element, SchemaNode parent, SchemaNode child, boolean isRoot) {
			this.element = element;
			this.parent = parent;
			this.setRoot(isRoot);
			if (child != null) {
				children.add(child);
			}
		}

		public void addChild(SchemaNode child) {
			this.children.add(child);
		}

		public void setParent(SchemaNode parent) {
			this.parent = parent;
		}

		public void setElement(Object element) {
			this.element = element;
		}

		public SchemaNode getParent() {
			return parent;
		}

		public Object getElement() {
			return element;
		}

		public boolean isRoot() {
			return this.isRoot;
		}

		public void setRoot(boolean isRoot) {
			this.isRoot = isRoot;
		}

		public String getRelativeXpath() {
			String name = ""; //$NON-NLS-1$
			if (element instanceof XSDTypeDefinition) {
				name = ((XSDTypeDefinition)element).getName();
				if (name==null){
					name = ((XSDTypeDefinition)element).getAliasName();
				}
			}else 
				if (element instanceof XSDParticleImpl &&
				   ((XSDParticleImpl)element).getTerm() instanceof XSDElementDeclaration) {
					XSDElementDeclaration xed = (XSDElementDeclaration) ((XSDParticleImpl)element).getTerm();
					name = "/" + (xed.getName() != null ? xed.getName() : xed.getAliasName()); //$NON-NLS-1$
				}
			
			return name; 
		}

		public String getParentXpath() {
			Stack stack = new Stack();
			SchemaNode parent = this.getParent();
			StringBuilder xpath = new StringBuilder();
			if (parent == null) {
				return ""; //$NON-NLS-1$
			}

			getParentXpath(stack, parent);
			int stackSize=stack.size();
			for (int i=0; i<stackSize; i++){
				xpath.append(stack.pop());
			}
			
			return xpath.toString(); 
		}
		
		public String getFullPath() {
		
			return getParentXpath()+getRelativeXpath();
		}

		private void getParentXpath(Stack stack, SchemaNode parent) {
			if (parent == null) {
				return;
			}
			Object parentElement = parent.getElement();
			String name = null;
			if (parentElement instanceof XSDParticleImpl
					&& ((XSDParticleImpl) parentElement).getContent() instanceof XSDElementDeclarationImpl) {
				name = ((XSDElementDeclarationImpl) ((XSDParticleImpl) parentElement)
						.getContent()).getName();
				stack.push("/"+name); //$NON-NLS-1$
			} else if (parentElement instanceof XSDElementDeclarationImpl) {
				name = ((XSDElementDeclarationImpl) parentElement).getName();
				stack.push("/"+name); //$NON-NLS-1$
			} else {
				return;
			}

			if (parent.getParent() != null) {
				getParentXpath(stack, parent.getParent());
			}
		}

		public String toString() {
			return "" + getElement(); //$NON-NLS-1$
		}
	}

}
