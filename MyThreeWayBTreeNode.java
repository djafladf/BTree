package org.dfpl.db.hash.m19011677;

import java.util.ArrayList;
import java.util.List;

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
		// System.out.println("Merge Sequence 1");
		// 형제 노드와 자신을 합침. 이 연산 후 자신의 노드는 없는 것으로 취급
		parent.children.remove(ChildInd);
		MyThreeWayBTreeNode Brother;
		if(ChildInd == parent.children.size()) Brother = parent.children.get(ChildInd - 1);
		else {Brother = parent.children.get(ChildInd);Brother.ChildInd--;}
		// System.out.println("Merge Sequence 2");

		// 형제 노드에 자신의 자식을 모두 이어 붙임
		if(ChildInd == parent.children.size()){Brother.children.addAll(Brother.children.size(),children); ChildInd--;}
		else Brother.children.addAll(0,children);
		Brother.RenewChildInd();

		// System.out.println("Merge Sequence 3");

		// 형제 노드에 부모의 값을 넣음
		int z = parent.keyList.get(ChildInd);
		parent.keyList.remove(ChildInd);
		Brother.Add(z); 

		// System.out.println("Merge End");
		
	}

	public int Del(int ind)
	{
		int ret = keyList.get(ind);
		int i;
		keyList.remove(ind);
		if(children.size() == 0) // Leaf 노드에서
		{
			if(keyList.size() != 0 || parent == null) return ret;	// 최소 조건을 만족하는 경우 반환
			
			int cnt, cnt2;
			if(ChildInd != 0)
			{ 
				if(parent.children.get(ChildInd-1).keyList.size() > 1)	// 왼쪽 형제 Node에서 값을 빌려옴
				{
					// System.out.println("RemoveType LeftBrotherBorrow");
					Add(parent.keyList.get(ChildInd-1));
					parent.keyList.set(ChildInd-1,parent.children.get(ChildInd-1).Del(1));
					return ret;
				}
			}
			if(ChildInd != parent.children.size() - 1)
			{
				if(parent.children.get(ChildInd+1).keyList.size() > 1)	// 오른쪽 형제 Node에서 값을 빌려옴
				{
					// System.out.println("RemoveType RightBrotherBorrow");
					Add(parent.keyList.get(ChildInd));
					parent.keyList.set(ChildInd,parent.children.get(ChildInd+1).Del(0));
					return ret;
				}
			}

			// 부모의 값 중 하나를 왼쪽 자식에 빌려줌( 부모 값의 개수 2 -> 1, 자식의 갯수 3 -> 2 : 최소 조건 유지)
			if(parent.children.size() == 3)
			{
				// System.out.println("RemoveType ParentBorrow");
				parent.children.remove(ChildInd);
				if(ChildInd == 2) ChildInd--;
				parent.children.get(ChildInd).Add(parent.keyList.get(ChildInd));
				parent.keyList.remove(ChildInd);
				parent.RenewChildInd();
				return ret;
			}
			else	// 부모가 최소 조건을 만족하거나 Root일 때까지 Merge
			{
				// System.out.println("RemoveType Merge");
				MyThreeWayBTreeNode ccnt = this;
				while(ccnt.keyList.size() == 0 || ccnt.keyList.size() != ccnt.children.size()-1)
				{
					if(ccnt.parent == null) break;
					ccnt.Merge();
					ccnt = ccnt.parent;
				}
				ccnt.RenewChildInd();
				// System.out.println("Merge End");

				// 기존 Root의 값이 비게 되었을 때 자신의 자식을 Root로 변환되었음을 명시
				// 이 후 FindRoot에서 Root가 갱신
				if(ccnt.parent == null && ccnt.keyList.size() == 0 && ccnt.children.get(0).parent == ccnt)
				{
					// System.out.println("Tree Size Down!");
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
			// LMax나 RMin에서 값을 빌릴 수 있을 때
			if(LMax.keyList.size() > 1){/*System.out.println("RemoveType LMaxBorrow");*/Add(LMax.Del(LMax.keyList.size()-1)); return ret;}
			if(RMin.keyList.size() > 1){/*System.out.println("RemoveType RMinBorrow");*/Add(RMin.Del(0)); return ret;}

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
