package net.sourceforge.sqlexplorer.sessiontree.model.utility;
/*
 * Copyright (C) 2002-2004 Andrea Mazzolini
 * andreamazzolini@users.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import net.sourceforge.sqlexplorer.sqleditor.SQLCodeScanner;


public class Dictionary {
	public Dictionary(){}
	private static TernarySearchTree keywordsTree=new TernarySearchTree();
	static{
		String[] str=SQLCodeScanner.getFgKeywords();
		for(int i=0;i<str.length;i++)
			keywordsTree.put(str[i],str[i]);
		
	}
	private TernarySearchTree tree=new TernarySearchTree();
	private TernarySearchTree catalogSchemaTree=new TernarySearchTree();
	private TernarySearchTree externalObjectTree=new TernarySearchTree();
	private HashMap realTables= new HashMap();
	private HashMap realCatalogSchemas= new HashMap();
	private HashMap realExternalObjects=new HashMap();
	
	//private TernarySearchTree col_tree=new TernarySearchTree();
	private HashMap col_map= new HashMap();

	
	public void putTableName(String key, Object value) {
		tree.put(key.toLowerCase(),value);
		realTables.put(key.toLowerCase(),key);
	}
	public void putCatalogSchemaName(String key, Object value) {
		catalogSchemaTree.put(key.toLowerCase(),value);
		realCatalogSchemas.put(key.toLowerCase(),key);
	}
	
	public void putExternalObjectName(String key, Object value) {
		externalObjectTree.put(key.toLowerCase(),value);
		realExternalObjects.put(key.toLowerCase(),key);
	}
	
	public Object getByTableName(String key){
		return tree.get(key);
	}
	public Object getByCatalogSchemaName(String key){
		return catalogSchemaTree.get(key);
	}
	public Object getByExternalObjectName(String key){
			return catalogSchemaTree.get(key);
	}
	
	public void putColumnsByTableName(String key, Object value) {
		//col_tree.put(key,value);
		col_map.put(key,value);
	}
	
	public Object getColumnListByTableName(String key){
		return col_map.get(key);
	}

	
	/*public long table_size(){
		return set.size();
	}*/
	public Iterator getTableNames(){
		return realTables.keySet().iterator();	
	}
	public Iterator getCatalogSchemaNames(){
		return realCatalogSchemas.keySet().iterator();
	}
	public Iterator getExternalObjectNames(){
		return realExternalObjects.keySet().iterator();
	}
	
	public ArrayList getTableObjectList(String tableName){
		return (ArrayList)tree.get(tableName.toLowerCase());
	}
	
	public String[] matchTablePrefix(String prefix){
		prefix=prefix.toLowerCase();
		DoublyLinkedList linkedList=tree.matchPrefix(prefix);
		int size=linkedList.size();
		DoublyLinkedList.DLLIterator iterator= linkedList.iterator();
		String [] result=new String[size];
		int k=0;
		while(iterator.hasNext()){
			result[k++]=(String)realTables.get(iterator.next());
		}
		return result;
	}
	public String[] matchCatalogSchemaPrefix(String prefix){
		prefix=prefix.toLowerCase();
		DoublyLinkedList linkedList=catalogSchemaTree.matchPrefix(prefix);
		int size=linkedList.size();
		DoublyLinkedList.DLLIterator iterator= linkedList.iterator();
		String [] result=new String[size];
		int k=0;
		while(iterator.hasNext()){
			result[k++]=(String)realCatalogSchemas.get(iterator.next());
		}
		return result;
	}
	public String[] matchExternalObjectPrefix(String prefix){
		prefix=prefix.toLowerCase();
		DoublyLinkedList linkedList=externalObjectTree.matchPrefix(prefix);
		int size=linkedList.size();
		DoublyLinkedList.DLLIterator iterator= linkedList.iterator();
		String [] result=new String[size];
		int k=0;
		while(iterator.hasNext()){
			result[k++]=(String)realExternalObjects.get(iterator.next());
		}
		return result;
	}
	public static String[] matchKeywordsPrefix(String prefix){
		prefix=prefix.toLowerCase();
		DoublyLinkedList linkedList=keywordsTree.matchPrefix(prefix);
		int size=linkedList.size();
		DoublyLinkedList.DLLIterator iterator= linkedList.iterator();
		String [] result=new String[size];
		int k=0;
		while(iterator.hasNext()){
			result[k++]=(String)iterator.next();
		}
		return result;
	}

}
