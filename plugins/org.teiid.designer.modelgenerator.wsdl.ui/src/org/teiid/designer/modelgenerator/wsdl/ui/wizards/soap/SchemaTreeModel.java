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
import org.eclipse.xsd.impl.XSDModelGroupImpl;
import org.eclipse.xsd.impl.XSDParticleImpl;

import com.metamatrix.modeler.modelgenerator.wsdl.model.Part;

public class SchemaTreeModel {

	Collection<SchemaNode> nodeList = new ArrayList<SchemaNode>();
	String defaultNamespace = null;
	String rootPath = null;
	public static Map<String, String> namespaceMap = new HashMap<String, String>();
	public Part[] partArray = null;

	public Map<String, String> getNamespaceMap() {
		return namespaceMap;
	}

	public String getDefaultNamespace() {
		return defaultNamespace;
	}

	public void setDefaultNamespace(String defaultNamespace) {
		this.defaultNamespace = defaultNamespace;
	}

	public Part[] getPartArray() {
		return partArray;
	}

	public void setPartArray(Part[] partArray) {
		this.partArray = partArray;
	}

	public void setNamespaceMap(Map<String, String> namespaceMap) {
		this.namespaceMap = namespaceMap;
	}

	public String getRootPath() {
		return this.rootPath;
	}

	public void setRootPath(String rootPath) {
		this.rootPath = rootPath;
	}

	public Collection<SchemaNode> getNodeList() {
		return this.nodeList;
	}

	public void setNodeList(Collection<SchemaNode> nodeList) {
		this.nodeList = nodeList;
	}
	
	public String getRootNodeXpath() {
		for (SchemaNode node:this.nodeList){
			if (node.isRoot){
				return node.getRelativeXpath();
			}
		}
		
		return ""; //$NON-NLS-1$
	}
	
	public String determineRootPath(){
		StringBuilder commonRoot = new StringBuilder();
		
		List<String> segmentList = new ArrayList();
		for (SchemaNode node:this.nodeList){
			if (node.children.isEmpty()){
				String path = node.getFullPathMinusLastSegment();
				if (!path.equals("")){ //$NON-NLS-1$
					segmentList.add(path);
				}
			}
		}
		
		//We parse paths to get all segments. We need to find the shortest
		//path up front, since we cannot have a common root greater than
		//the shortest path.
		String[][] segments = new String[segmentList.size()][];
		int shortestPathLength = 0;
		for(int i = 0; i < segmentList.size(); i++){
			segments[i] = segmentList.get(i).split("/");  //$NON-NLS-1$
			if (i==0) shortestPathLength = segments[i].length;
			if (shortestPathLength>segments[i].length){
				shortestPathLength = segments[i].length;
			}
		}
		
		for(int j = 0; j < shortestPathLength; j++){
			String thisSegment = segments[0][j]; 
			boolean allMatched = true; 
			for(int i = 0; i < segments.length && allMatched; i++){ 
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

		public Collection<SchemaNode> getChildren() {
			return children;
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
					name = (xed.getName() != null ? xed.getName() : xed.getAliasName()); //$NON-NLS-1$
				}
			
			return "/" + getNamespacePrefix(element) + name; 
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
		
		public String getFullPathMinusLastSegment() {
			
			String relativePath = getRelativeXpath();
			if (!relativePath.isEmpty()){ 
				int i = relativePath.lastIndexOf("/"); //$NON-NLS-1$
				if (i==0) {
					relativePath = ""; //$NON-NLS-1$
				}else{
					relativePath = relativePath.substring(0,i-1);
				}
				
			}
			return getParentXpath()+relativePath;
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
				if (name==null){
				name = ((XSDElementDeclarationImpl) ((XSDParticleImpl) parentElement)
						.getContent()).getResolvedElementDeclaration().getName();
				}
				stack.push("/"+ getNamespacePrefix(parentElement) +name); //$NON-NLS-1$
			} else if (parentElement instanceof XSDElementDeclarationImpl) {
				name = ((XSDElementDeclarationImpl) parentElement).getName();
				stack.push("/"+ getNamespacePrefix(parentElement) +name);
			} else if (parentElement instanceof XSDModelGroupImpl){
					//fall through to getParentXpath() logic
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
	
    private String getNamespacePrefix(Object obj) {
		
		String nsPrefix = ""; //$NON-NLS-1$
		String ns = ""; //$NON-NLS-1$
		if (obj instanceof XSDParticleImpl
				&& ((XSDParticleImpl) obj).getContent() instanceof XSDElementDeclarationImpl) {
			ns = ((XSDElementDeclarationImpl) ((XSDParticleImpl) obj)
					.getContent()).getTargetNamespace();
		} else if (obj instanceof XSDElementDeclarationImpl) {
			ns = ((XSDElementDeclarationImpl) obj).getTargetNamespace();
		}
		
		for (Object nsKey: SchemaTreeModel.namespaceMap.keySet()){
			if (SchemaTreeModel.namespaceMap.get(nsKey).equals(ns)){
				nsPrefix = (String) nsKey;
				break;
			}
		}
		
	//  This is default.. no need to alias
	//	if (nsPrefix.equals(ResponseInfo.DEFAULT_NS)) nsPrefix = ""; //$NON-NLS-1$ //$NON-NLS-2$
	//  We will always prefix, since we can't count on a service default for a given element
		return nsPrefix == "" ? nsPrefix : nsPrefix + ":"; //$NON-NLS-1$ //$NON-NLS-2$
		
		}

}
