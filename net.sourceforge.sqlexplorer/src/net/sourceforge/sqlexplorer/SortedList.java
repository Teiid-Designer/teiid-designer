package net.sourceforge.sqlexplorer;

import java.util.AbstractList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;



public class SortedList extends AbstractList {

	protected int _size;
	protected Object[] _data;
	protected Comparator _comp;
	private int _index;
	public SortedList(Comparator comp)
	{
		_comp = comp;
		_data = new Object[25];
	}
	public SortedList(Comparator comp, Collection collection)
	{
		_comp = comp;
		_data = new Object[collection.size() + 25];
		addAll(collection);
	}
	/**
	 * @see java.util.List#get(int)
	 */
	@Override
    public Object get(int index)
	{
		return _data[index];
	}
	/**
	 * @see java.util.Collection#size()
	 */
	@Override
    public int size()
	{
		return _size;
	}
	/**
	 * @see java.util.Collection#add(java.lang.Object)
	 */
	@Override
    public boolean add(Object o)
	{
		int index = indexOfIntern(o);
		if(index < 0) index = -(index+1);
		try
		{
			if(index != _size)
			{
				System.arraycopy(_data, index, _data, index+1, _size-index);
			}
			_data[index] = o;
		} catch(ArrayIndexOutOfBoundsException ex)
		{
			Object[] data = new Object[_size+25];
			System.arraycopy(_data, 0, data, 0, index);
			System.arraycopy(_data, index, data, index+1, _size-index);
			data[index] = o;
			_data = data;
		}
		_index = index;
		_size++;
		return true;
	}
	
	public void addAll(Iterator it)
	{
		while(it.hasNext())
		{
			add(it.next());
		}
	}
	
	public int getInsertIndex()
	{
		return _index;
	}
	
	@Override
    public int indexOf(Object o)
	{
		int index = indexOfIntern(o);
		return  index < 0?-1:index;
	}	
	/**
	 * can return number lower then -1!!!
 */
	protected int indexOfIntern(Object o)
	{
		int low = 0;
		int high = _size - 1;
		while (low <= high)
		{
			int mid = (low + high) >> 1;
			Object midVal = _data[mid];
			int cmp = _comp.compare(midVal, o);
			if (cmp < 0)
				low = mid + 1;
			else if (cmp > 0)
				high = mid - 1;
			else
				return mid; // key found
		}
		return -(low + 1);
	}
	/**
	 * @see java.util.Collection#contains(java.lang.Object)
	 */
	@Override
    public boolean contains(Object o)
	{
		return indexOfIntern(o) >= 0;
	}

	/**
	 * @see java.util.List#remove(int)
	 */
	@Override
    public Object remove(int index)
	{
		if(index >= _size || index < 0) throw new ArrayIndexOutOfBoundsException("index greater then size or below zero for remove " + index); //$NON-NLS-1$
		
		
		Object data = _data[index];
		
		System.arraycopy(_data, index+1, _data, index, _size-index-1);
		--_size;
		return data;
	}

	/**
	 * @see java.util.Collection#remove(java.lang.Object)
	 */
	@Override
    public boolean remove(Object o)
	{
		int index = indexOfIntern(o);
		if(index >= 0) remove(index);
		return index >= 0;
	}


}
