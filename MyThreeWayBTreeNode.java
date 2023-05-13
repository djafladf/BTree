package org.dfpl.db.hash.m19011677;

import java.util.ArrayList;
import java.util.List;
import javax.lang.model.util.ElementScanner14;

@SuppressWarnings("unused")
public class MyThreeWayBTreeNode {
	
	MyThreeWayBTreeNode parent;
	List<MyThreeWayBTreeNode> children;
	List<Integer> keyList;

	// 현재 Node가 parent의 몇번 째 자식인지
	int ChildInd;

	public MyThreeWayBTreeNode(MyThreeWayBTreeNode parent)
	{
		this.parent = parent;
		ChildInd = 0;
		keyList = new ArrayList<>();
		children = new ArrayList<>();
	}
	
	// 추가 될 Leaf를 찾음
	public void FindAddLeaf(int a)
	{
		Branch(a).Add(a);
	}

	// Root 갱신에 사용.
	public MyThreeWayBTreeNode FindRoot(MyThreeWayBTreeNode Leaf)
	{
		if(Leaf.parent == null){return Leaf;}
		else return FindRoot(this.parent);
	}

	// Child들의 ChildInd값을 다시 계산함.
	public void RenewChildInd()
	{
		int x = 0;
		for(var a : children)
		{
			a.parent = this;
			a.ChildInd = x++;
		}
	}

	// 값 추가 연산
	public void Add(int a)
	{
		// 값을 넣을 위치를 찾음
		int x = 0;
		for(var b : keyList)
		{
			if(a < b) break;
			x++;
		}
		keyList.add(x,a);

		if(keyList.size() == 3) // 최대 조건 위반
		{	
			
			int mid = keyList.get(1); keyList.remove(1);
			
			// root에서 연산 시 부모가 없음으로 새로 생성해준다.
			if(parent == null) parent = new MyThreeWayBTreeNode(null);

			MyThreeWayBTreeNode L = new MyThreeWayBTreeNode(parent); L.Add(keyList.get(0));
			MyThreeWayBTreeNode R = new MyThreeWayBTreeNode(parent); R.Add(keyList.get(1));

			// 부모가 최대 조건을 어길 때, 자식이 4개가 되는데
			// 왼쪽 2개를 왼쪽에, 오른쪽 2개를 오른쪽에 붙여준다.
			if(children.size() != 0)	
			{
				L.children.add(this.children.get(0)); L.children.add(this.children.get(1));
				L.RenewChildInd();
				R.children.add(this.children.get(2)); R.children.add(this.children.get(3));
				R.RenewChildInd();
			}
			// parent의 children이 비어있다면(root 갱신) children에 L, R을 넣어줌.
			// 그렇지 않으면(root 이외의 연산) 현재 this의 parent의 children에 Index와 Index+1에 L,R을 넣어줌.
			// set으로 this와 parent간의 연결을 끊음.
			
			if(parent.children.size() == 0) parent.children.add(0,L);
			else {parent.children.set(ChildInd,L); L.ChildInd = ChildInd;}
			parent.children.add(ChildInd+1,R);

			parent.RenewChildInd();
			parent.Add(mid);
		}
	}
	// 병합 진행
	public void Merge()
	{
		// 형제 노드와 자신을 합침. 이 연산 후 자신의 노드는 없는 것으로 취급
		parent.children.remove(ChildInd);
		MyThreeWayBTreeNode Brother;
		if(ChildInd == parent.children.size()) Brother = parent.children.get(ChildInd - 1);
		else {Brother = parent.children.get(ChildInd);Brother.ChildInd--;}

		// 형제 노드에 자신의 자식을 모두 이어 붙임
		if(ChildInd == parent.children.size()){Brother.children.addAll(Brother.children.size(),children); ChildInd--;}
		else Brother.children.addAll(0,children);
		Brother.RenewChildInd();

		// 형제 노드에 부모의 값을 넣음()
		int z = parent.keyList.get(ChildInd);
		parent.keyList.remove(ChildInd);
		Brother.Add(z); 
		
	}

	public int Del(int ind)
	{
		
		int ret = keyList.get(ind);
		int i;
		keyList.remove(ind);
		if(children.size() == 0) // Leaf 노드에서
		{
			if(keyList.size() != 0 || parent == null) return ret;	// 최소 조건을 만족하는 경우 반환
			List<MyThreeWayBTreeNode> Borrow = new ArrayList<>(2);
			
			
			if(ChildInd != 0){ Borrow.add(parent.children.get(ChildInd-1));}
			if(ChildInd != parent.children.size() - 1)Borrow.add(parent.children.get(ChildInd+1));

			i = 0;
			int cnt, cnt2;
			for(var a : Borrow){ if(a.keyList.size()> 1)	// 주변 형제 노드에게 값을 빌릴 수 있을 때
				{
					System.out.println("DelType1");
					cnt = a.Del(Borrow.size() -1 - i);
					cnt2 = parent.keyList.get(i);
					Add(cnt2);
					parent.keyList.set(i, cnt);
					return ret;
				}
				i++;
			}
			

			// 부모의 값 중 하나를 왼쪽 자식에 빌려줌( 부모 값의 개수 2 -> 1 : 최소 조건 유지)
			if(parent.children.size() == 3)
			{
				System.out.println("DelType2");
				parent.children.remove(ChildInd);
				parent.children.get(ChildInd).Add(parent.keyList.get(ChildInd));
				parent.keyList.remove(ChildInd);
				parent.RenewChildInd();
				return ret;
			}
			else	// 부모가 최소 조건을 만족하거나 Root일 때(hegith를 1 줄임)까지 Merge
			{
				System.out.println("DelType3(Merge)");
				MyThreeWayBTreeNode ccnt = this;
				while(ccnt.keyList.size() == 0 || ccnt.keyList.size() != ccnt.children.size()-1)
				{
					System.out.println("Merging!");
					if(ccnt.parent == null) break;
					ccnt.Merge();
					ccnt = ccnt.parent;
				}
				System.out.println("MergeEnd!");

				// 트리의 높이가 1 줄었으며, 기존 root값에 추가로 값이 들어오지 않았을 때
				if(ccnt.parent == null && ccnt.keyList.size() == 0 && ccnt.children.get(0).parent == ccnt)
				{
					ccnt.children.get(0).parent = null;
					ccnt.parent = ccnt.children.get(0);
				}
			}
		}
		else	// 내부 노드에서
		{
			// 자식의 조건을 만족하면 return;
			if(keyList.size() == children.size() - 1) return ret;
			// LMax를 가지고 있는 Leaf
			MyThreeWayBTreeNode LMax = children.get(ind);
			while(LMax.children.size()!=0) LMax = LMax.children.get(LMax.children.size()-1);
			// RMax를 가지고 있는 Leaf
			MyThreeWayBTreeNode RMin = children.get(ind+1);
			while(RMin.children.size()!=0) RMin = RMin.children.get(0);
			System.out.println("DelType4");
			// LMax나 RMin에서 값을 빌릴 수 있을 때
			if(LMax.keyList.size() > 1){Add(LMax.Del(LMax.keyList.size()-1)); return ret;}
			if(RMin.keyList.size() > 1){Add(RMin.Del(0)); return ret;}
			
			System.out.println("DelType5");
			// 빌릴 수 없어도 빌린다.	
			Add(LMax.keyList.get(0));
			
			LMax.Del(0);
		}
		return ret;
	}

	// 어떤 자식으로 갈 것인지 정함
	MyThreeWayBTreeNode Branch(int a)
	{
		int i = 0;
		if(children.size() == 0){
			return this; 
		}
		for(var b : keyList)
		{
			if(a < b) break;
			i++;
		}
		return children.get(i).Branch(a);
	}


	// test용
	public void PrintChild()
	{
		for(var a : children)
		{
			System.out.print(a.keyList); System.out.print(" ");
		}
		System.out.println();
	}
	
}
