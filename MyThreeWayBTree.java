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

	// Iterator로 모든 값을 돌며 순회한 횟수만큼 반환.
	@Override
	public int size() {
		if(root.keyList.size() == 0) return 0;
		HashIter a = new HashIter();
		int i = 0;
		while(a.hasNext()){ a.next(); i++;}
		return i;
	}

	// Root가 비어있다면 빈 Tree
	@Override
	public boolean isEmpty() {
		return root.keyList.size() == 0;
	}

	// Iterator로 돌며 같은 값을 만나면 True 반환
	// 더 큰 값을 만나거나, 다음 값이 없으면 False 반환
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


	// 동일한 값이 없을 경우 Tree에 추가
	// 이 때 Root값이 변경 되었을 수도 있음으로, Root를 갱신해준다.
	@Override
	public boolean add(Integer e) {
		if(contains(e)) return false;
		root.FindAddLeaf(e);
		root = root.FindRoot(root);
		return true;
	}

	// Contain과 동일.
	@Override
	public boolean remove(Object o) {
		if(root.keyList.size() == 0) return false;
		HashIter a = new HashIter();
		while(a.hasNext())
		{
			if(a.CurLeaf.keyList.get(a.CurInd) == (int)o)
			{
				a.remove();
				return true;
			}
			if(a.CurLeaf.keyList.get(a.CurInd) > (int) o) return false;
			a.next();
		}
		return false;
	}

	// Iterator 반환
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

		// 생성 시, 가장 작은 값을 가진 Node로 Leaf를 이동시킴
		public HashIter()
		{
			CurLeaf = root;
			CurInd = 0;
			while(CurLeaf.children.size() != 0 ){CurLeaf = CurLeaf.children.get(0);}
		}
		@Override
		public boolean hasNext() 
		{
			return CurLeaf != null && CurLeaf.keyList.size() != 0;
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
				int i = CurLeaf.ChildInd; 
				// Root만으로 이루어진 Tree일 때 보정 용
				if(CurLeaf.children.size() == 0) i = 100;

				// 부모가 바로 오른쪽 위의 부모이거나, Root일 때까지
				while(CurLeaf.parent != null)
				{
					i = CurLeaf.ChildInd;
					CurLeaf = CurLeaf.parent;
					if(i != CurLeaf.keyList.size()) break;
				}
				// 가장 큰 값에서 Root로 돌아왔을 경우 순회를 종료한다.
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
			root = root.FindRoot(root);
			while(Cnt.hasNext())
			{
				if(Cnt.CurLeaf.keyList.get(Cnt.CurInd) > CurV) break;
				Cnt.next();
			}
			CurLeaf = Cnt.CurLeaf;
			CurInd = Cnt.CurInd;
		}
	}
	
	// test용
	public MyThreeWayBTreeNode returnRoot(){ return root;}

	// test용
	public void test(MyThreeWayBTreeNode Leaf)
	{
		root = root.FindRoot(root);
		System.out.println("Cur : " + Leaf.keyList);
		if(Leaf.children.size() == 0) return;
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
