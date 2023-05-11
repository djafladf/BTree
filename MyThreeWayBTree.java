package org.dfpl.db.hash.m19011677;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.SortedSet;

public class MyThreeWayBTree implements NavigableSet<Integer> {

	// Data Abstraction은 예시일 뿐 자유롭게 B-Tree의 범주 안에서 어느정도 수정가능

	private MyThreeWayBTreeNode root;

	public MyThreeWayBTree()
	{
		this.root = new MyThreeWayBTreeNode(null);
	}

	@Override
	public int size() {
		if(root.keyList.size() == 0) return 0;
		HashIter a = new HashIter();
		int i = 0;
		while(a.hasNext()){ a.next(); i++;}
		return i;
	}

	@Override
	public boolean isEmpty() {
		return root.keyList.size() == 0;
	}

	@Override
	public boolean contains(Object o) {
		if(root.keyList.size() == 0) return false;
		HashIter a = new HashIter();
		while(a.hasNext())
		{
			if(a.CurLeaf.keyList.get(a.CurInd) == (int)o) return true;
			if(a.CurLeaf.keyList.get(a.CurInd) > (int) o) return false;
			a.next();
		}
		return false;
	}

	@Override
	public boolean add(Integer e) {
		if(contains(e)) return false;
		root.FindAddLeaf(e);
		root = root.FindRoot(root);
		return true;
	}

	@Override
	public boolean remove(Object o) {
		return false;
	}

	@Override
	public Iterator<Integer> iterator() {
		return new HashIter();
	}

	class HashIter implements Iterator<Integer>
	{
		// 현재 Iterator가 가르키는 Leaf
		MyThreeWayBTreeNode CurLeaf;
		// 현재 Iterator가 가르키는 Leaf의 Ind
		int CurInd;

		public HashIter()
		{
			CurLeaf = root;
			CurInd = 0;
			while(CurLeaf.children.size() != 0 ){CurLeaf = CurLeaf.children.get(0);}
		}
		@Override
		public boolean hasNext() 
		{
			return CurLeaf != null;
		}

		@Override
		public Integer next() 
		{
			int ret = CurLeaf.keyList.get(CurInd);
			// 다음 순회가 Children Leaf인 경우
			if(CurLeaf.children.size() > CurInd + 1)
			{	
				CurLeaf = CurLeaf.children.get(CurInd+1);
				CurInd = 0;
				while(true){
					if(CurLeaf.children.size() == 0) break;
					CurLeaf = CurLeaf.children.get(0);
				}
				
			}
			// 다음 순회가 동일 Leaf내인 경우(최하단 Leaf에서 수행됨)
			else if(CurLeaf.keyList.size() > CurInd + 1)
			{
				CurInd = CurInd + 1;
			}
			// 다음 순회가 Parent Leaf인 경우
			else
			{
				int i = CurLeaf.ChildInd; if(CurLeaf.children.size() == 0) i = 100;
				while(CurLeaf.parent != null)
				{
					i = CurLeaf.ChildInd;
					CurLeaf = CurLeaf.parent;
					if(i != CurLeaf.keyList.size()) break;
				}
				if(i >= CurLeaf.keyList.size()) CurLeaf = null;
				CurInd = i;
			}
			return ret;
		}
		public void remove()
		{
			int CurV = CurLeaf.keyList.get(CurInd);
			CurLeaf.Del(CurInd);
			
			// 트리가 재구성 됬을 때를 대비 
			HashIter Cnt = new HashIter();
			while(Cnt.hasNext())
			{
				if(Cnt.CurLeaf.keyList.get(Cnt.CurInd) > CurV) break;
				Cnt.next();
			}
			CurLeaf = Cnt.CurLeaf;
			CurInd = Cnt.CurInd;
		}

	}

	// test용(과제와 상관 없음)
	public void test(MyThreeWayBTreeNode Leaf)
	{
		if(Leaf.parent != null) System.out.print("Parent : " + Leaf.parent.keyList + ", ");
		System.out.println("Cur : " + Leaf.keyList);
		for(var a : Leaf.children){
			System.out.print(a.ChildInd); System.out.print(" ");
		}
		System.out.println();
		for(var a : Leaf.children){
			System.out.print(a.keyList); System.out.print(" ");
		}
		System.out.println();

		for(var a : Leaf.children)
		{
			test(a);
		}
	}

	@Override
	public Comparator<? super Integer> comparator() {
		
		throw new UnsupportedOperationException("Unimplemented method 'comparator'");
	}

	@Override
	public Integer first() {
		
		throw new UnsupportedOperationException("Unimplemented method 'first'");
	}

	@Override
	public Integer last() {
		
		throw new UnsupportedOperationException("Unimplemented method 'last'");
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

	@Override
	public Integer lower(Integer e) {
		
		throw new UnsupportedOperationException("Unimplemented method 'lower'");
	}

	@Override
	public Integer floor(Integer e) {
		
		throw new UnsupportedOperationException("Unimplemented method 'floor'");
	}

	@Override
	public Integer ceiling(Integer e) {
		
		throw new UnsupportedOperationException("Unimplemented method 'ceiling'");
	}

	@Override
	public Integer higher(Integer e) {
		
		throw new UnsupportedOperationException("Unimplemented method 'higher'");
	}

	@Override
	public Integer pollFirst() {
		
		throw new UnsupportedOperationException("Unimplemented method 'pollFirst'");
	}

	@Override
	public Integer pollLast() {
		
		throw new UnsupportedOperationException("Unimplemented method 'pollLast'");
	}

	@Override
	public NavigableSet<Integer> descendingSet() {
		
		throw new UnsupportedOperationException("Unimplemented method 'descendingSet'");
	}

	@Override
	public Iterator<Integer> descendingIterator() {
		
		throw new UnsupportedOperationException("Unimplemented method 'descendingIterator'");
	}

	@Override
	public NavigableSet<Integer> subSet(Integer fromElement, boolean fromInclusive, Integer toElement,
			boolean toInclusive) {
		
		throw new UnsupportedOperationException("Unimplemented method 'subSet'");
	}

	@Override
	public NavigableSet<Integer> headSet(Integer toElement, boolean inclusive) {
		
		throw new UnsupportedOperationException("Unimplemented method 'headSet'");
	}

	@Override
	public NavigableSet<Integer> tailSet(Integer fromElement, boolean inclusive) {
		
		throw new UnsupportedOperationException("Unimplemented method 'tailSet'");
	}

	@Override
	public SortedSet<Integer> subSet(Integer fromElement, Integer toElement) {
		
		throw new UnsupportedOperationException("Unimplemented method 'subSet'");
	}

	@Override
	public SortedSet<Integer> headSet(Integer toElement) {
		
		throw new UnsupportedOperationException("Unimplemented method 'headSet'");
	}

	@Override
	public SortedSet<Integer> tailSet(Integer fromElement) {
		
		throw new UnsupportedOperationException("Unimplemented method 'tailSet'");
	}

	
}
