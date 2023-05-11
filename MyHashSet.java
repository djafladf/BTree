package org.dfpl.db.hash.m19011677;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class MyHashSet<Integer> implements Set<Integer> {
	
	private MyThreeWayBTree[] hashTable;

	public MyHashSet() {
		// 해시테이블 배열 크기는 3로 고정합니다.
		// hash function은 key를 3로 나눈 값이며,
		// 충돌시 3-way B-Tree에 저장됩니다.
		hashTable = new MyThreeWayBTree[3];
		for(int i = 0; i < 3; i++) hashTable[i] = new MyThreeWayBTree();
	}

	@Override
	public int size() {
		int size = 0;
		for (MyThreeWayBTree t : hashTable) {
			size += t.size();
		}
		return size;
	}

	@Override
	public boolean isEmpty() {
		for(int i = 0; i < 3; i++) if(!hashTable[i].isEmpty()) return false;
		return true;
	}

	@Override
	public boolean contains(Object o) {
		return hashTable[(int)o % 3].contains(o);
	}
	class HashSetIter<Integer> implements Iterator<Integer>
	{
		int CurInd;
		Iterator<Integer> CurIter;

		public HashSetIter()
		{
			CurInd = 0;
			CurIter = (Iterator<Integer>) hashTable[0].iterator();
		}
		@Override
		public boolean hasNext() {
			return CurInd != 3;
		}

		@Override
		public Integer next() {
			Integer i = CurIter.next();
			if(!CurIter.hasNext())
			{
				if(++CurInd < 3) CurIter = (Iterator<Integer>) hashTable[CurInd].iterator();
			} 
			return i;
		}

	}

	@Override
	public Iterator<Integer> iterator() {
		return new HashSetIter();
	}

	@Override
	public boolean add(Integer e) {
		hashTable[(int)e % 3].add((int)e);
		return true;
	}

	@Override
	public boolean remove(Object o) {
		
		return false;
	}

	@Override
	public Object[] toArray() {
		
		throw new UnsupportedOperationException("Unimplemented method 'toArray'");
	}

	@Override
	public <T> T[] toArray(T[] a) {
		
		throw new UnsupportedOperationException("Unimplemented method 'toArray'");
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		
		throw new UnsupportedOperationException("Unimplemented method 'containsAll'");
	}

	@Override
	public boolean addAll(Collection<? extends Integer> c) {
		
		throw new UnsupportedOperationException("Unimplemented method 'addAll'");
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		
		throw new UnsupportedOperationException("Unimplemented method 'retainAll'");
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		
		throw new UnsupportedOperationException("Unimplemented method 'removeAll'");
	}

	@Override
	public void clear() {
		
		throw new UnsupportedOperationException("Unimplemented method 'clear'");
	}
}
