package org.dfpl.db.hash.m19011677;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class MyHashSet<Integer> implements Set<Integer> {
	
	private MyThreeWayBTree[] hashTable;

	public MyHashSet() {
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
		// 현재 몇 번 HashTable인지.
		int CurInd;
		// 현재 HashTable의 Iterator
		Iterator<Integer> CurIter;

		public HashSetIter()
		{
			CurInd = 0;
			CurIter = (Iterator<Integer>) hashTable[0].iterator();
		}
		@Override
		public boolean hasNext() {
			// 현재 Iterator의 Next가 null일 경우
			// 바로 다음 HashTable의 Iterator를 호출하며
			// 이를 CurIter가 Next가 있을 때나 HashTable[1]의 Iterator일 때 까지 반복한다.(2에선 굳이 이런 연산이 필요 없다.)
			while(!CurIter.hasNext() && CurInd < 2)
			{
				CurIter = (Iterator<Integer>) hashTable[++CurInd].iterator();
			}
			return CurIter.hasNext();
		}

		@Override
		public Integer next() 
		{
			Integer i = CurIter.next();
			return i;
		}

	}

	//Test 용으로 값 반환을 안하는 함수를 임시로 사용.
	@Override
	public void clear() {
		for(int i = 0; i < 3; i++) System.out.println(hashTable[i].returnRoot().keyList);
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
		hashTable[(int)o % 3].remove((int)o);
		return true;
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
}
