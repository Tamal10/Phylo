/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phylo;

import java.util.ArrayList;
import java.util.Random;


/**
 *
 * @author TAMAL
 */
public class Tree implements Comparable<Tree>{
    ArrayList <Node> T;
    Node root;
    int Score;
    
    ArrayList<Pair> split= new ArrayList<Pair>();
    
    Tree(Tree Tr){
        this.T=Tr.T;
        Score=0;
    }
    Tree(ArrayList<Node> T){
        this.T=T;
        if(T.size()>0)root=T.get(T.size()-1);
        Score=0;
    }
    
    void print(Node c){
        if(c.child[0]==null && c.child[1]==null){
            System.out.print(c.label);
            return;
        }
        System.out.print("(");
        print(c.child[0]);
        System.out.print(",");
        print(c.child[1]);
        System.out.print(")");
    }
    void print(){
        for(int i=0;i<T.size();i++){
            T.get(i).print();
        }
         System.out.println("");
    }
    private void SwapRandomLeaves()
    {
    	//printTree(root,0);
    	int leaf_count=(T.size()+1)/2;
    	Random r=new Random();
		//choosing two random indices of leaves
		int l1=r.nextInt(leaf_count);
		int l2=r.nextInt(leaf_count);
		
		while(T.get(l1).parent==T.get(l2).parent)
		{
			l2=r.nextInt(leaf_count);
		}
		//leaves found and now we have to swap them
    	Node leaf1=T.get(l1);
    	Node leaf2=T.get(l2);
    	Node p1=leaf1.parent;
    	Node p2=leaf2.parent;
    	
    	//System.out.println("Swapping "+leaf1.label+" and "+leaf2.label);
    	leaf1.parent=p2;
    	leaf2.parent=p1;
    	if(p1.child[0]==leaf1)
    		p1.child[0]=leaf2;
    	else p1.child[1]=leaf2;
    	
    	if(p2.child[0]==leaf2)
    		p2.child[0]=leaf1;
    	else p2.child[1]=leaf1;
    	//System.out.println();
    	//printTree(root,0);
    		
    }
   public void SmallTweak()
    {
    	//first we flip a coin if it is head then we do swap else we rotate
        
    	Random rnd=new Random();
    	int coin=rnd.nextInt(10);
    	Node rp;
        spr();
    	
    	int trial=7;
    	if(coin<=3)
    		SwapRandomLeaves();
        else if(coin <=7){
//    		do{
//    			//System.out.println("Rotating");
//    			int index=rnd.nextInt(T.size()/2-1)+(T.size()+1)/2;
//        		rp=T.get(index);
//        		if(--trial==0){
//        			SwapRandomLeaves();
//        			return;
//        		}
//    		}
//    		while(rp.child[1]==null || rp.child[1].child[0]==null);
//    		//System.out.println("Rotating "+rp.label);
//    		//printTree(T.get(T.size()-1),0);
//    		
//    		Left_Rotate(rp);
//    		printTree(T.get(T.size()-1),0);
    		int index=rnd.nextInt(T.size()/2-1)+(T.size()+1)/2;
                rp=T.get(index);
                if(rp.parent==null) return;
                
                if(rp.child[0].child[0]==null && rp.child[1].child[0]==null){
                    rp=rp.parent;
                }
                    
                if(rp.child[0].child[0]==null && rp.child[0].child[1]==null){
//                    rp=rp.parent;
                    Left_Rotate(rp);
                    return;
                }
                if(rp.child[1].child[0]==null && rp.child[1].child[1]==null){
//                    rp=rp.parent;
                    Right_Rotate(rp);
                    return;
                }
                coin=rnd.nextInt(2);
                if(coin==0) Left_Rotate(rp);
                else Right_Rotate(rp);
        }
        else
              spr();
           
    	
    }
    
    private void Left_Rotate(Node rp)
    {
    	Node temp=rp.child[1];
    	rp.child[1]=temp.child[0];
        //System.out.println(rp.child[1]);
    	rp.child[1].parent=rp;
    	temp.child[0]=rp;
    	temp.parent=rp.parent;
    	rp.parent=temp;
    	if(temp.parent==null)
    	{
    		int a=T.indexOf(temp);
    		int b=T.indexOf(rp);
    		T.set(a, rp);
    		T.set(b, temp);
    	}
    	else {
    		if(temp.parent.child[0]==rp)
    			temp.parent.child[0]=temp;
    		else 
    			temp.parent.child[1]=temp;
		
    	int ind1=T.indexOf(temp);
    	int ind2=T.indexOf(rp);
    	T.set(ind1, rp);
    	T.set(ind2, temp);
        }
    }
    
    private void Right_Rotate(Node rp)
    {
    	Node temp=rp.child[0];
    	rp.child[0]=temp.child[1];
        //System.out.println(rp.child[0]);
    	rp.child[0].parent=rp;
    	temp.child[1]=rp;
    	temp.parent=rp.parent;
    	rp.parent=temp;
    	if(temp.parent==null)
    	{
    		int a=T.indexOf(temp);
    		int b=T.indexOf(rp);
    		T.set(a, rp);
    		T.set(b, temp);
    	}
    	else {
    		if(temp.parent.child[0]==rp)
    			temp.parent.child[0]=temp;
    		else 
    			temp.parent.child[1]=temp;
        
    	int ind1=T.indexOf(temp);
    	int ind2=T.indexOf(rp);
    	T.set(ind1, rp);
    	T.set(ind2, temp);
        }
    }
    public void HillClimb(int iteration)
    {
    	   
    	Tree current=this;
    	Tree copyTree=current.getCopy();
    	current.ParsimonizeTree();
    	int count=0;
    	while(iteration-->0)
    	{
            
    		copyTree.SmallTweak();
    		copyTree.ParsimonizeTree();
    		
    		if(current.Score>copyTree.Score){
                        
    			current=copyTree.getCopy();
                }
    	}
    	this.T=current.T;
//        this.Score=current.Score;
    }
    public int ParsimonizeTree()
	{

                if(Score!=0)return Score;
//    		for(int i=0;i<8;i++)
		for(int i=0;i<Phylo.seqLen;i++)
		{
			//for each site
			PostOrder(root,i);
			PreOrder(root,i);
			                 
			//Finding the sequence of internal node
			for(int j=0;j<T.size();j++)
			{
//                            System.out.println(i+" "+j+" "+T.size() + " "+T.get(j));
				if(T.get(j).spc==null)T.get(j).spc=new Species();
				if(T.get(j).child[0]!=null || T.get(j).child[1]!=null)
				{
					switch(T.get(j).helper)
					{
					case 1:
						T.get(j).spc.seq[i]='A';
						break;
					case 2:
						T.get(j).spc.seq[i]='T';
						break;
					case 4:
						T.get(j).spc.seq[i]='G';
						break;
					case 8:
						T.get(j).spc.seq[i]='C';
						break;
					case 16:
						T.get(j).spc.seq[i]='-';
						break;
					case 32:
						T.get(j).spc.seq[i]='M';
						break;
					case 64:
						T.get(j).spc.seq[i]='R';
						break;
					case 128:
						T.get(j).spc.seq[i]='K';
						break;
					case 256:
						T.get(j).spc.seq[i]='W';
						break;
					
					}
					T.get(j).finalScore+=T.get(j).score;
				}
				
				T.get(j).helper=0;
			}

			
		}
		Score=root.finalScore;
		return root.finalScore;
	}
	private void PreOrder(Node root,int site)
	{
		//System.out.println("Helper:"+root.helper);
		int count=1;
		if(root.parent!=null)
		{
			if((root.parent.helper & root.helper) !=0)
			{
				root.helper &=root.parent.helper;
			}
		}
		while(root.helper%2==0)
		{
			count=count*2;
			root.helper/=2;
		}
		root.helper=count;
		
		if(root.child[0]!=null)PreOrder(root.child[0],site);
		if(root.child[1]!=null)PreOrder(root.child[1],site);
	}
	private int PostOrder(Node root,int site)
	{
		if(root.child[0]==null && root.child[1]==null)
		{
			char c=root.spc.seq[site];
			
			switch(c)
			{
			case 'A':
				root.helper=1;
				return 1;
			case 'T':
				root.helper=2;
				return 2;
			case 'G':
				root.helper=4;
				return 4;
			case 'C':
				root.helper=8;
				return 8;
			case '-':
				root.helper=16;
				return 16;
			case 'M':
				root.helper=32;
				return 32;
			case 'R':
				root.helper=64;
				return 64;
			case 'K':
				root.helper=128;
				return 128;
			case 'W':
				root.helper=256;
				return 256;
			}
		}
		else
		{
			int left_helper=PostOrder(root.child[0],site);
			int right_helper=PostOrder(root.child[1],site);
			root.score=root.child[0].score+root.child[1].score;
			
			if((left_helper & right_helper)!=0)
				root.helper=left_helper & right_helper;
			else {
				root.helper=left_helper | right_helper;
				root.score++;
			}
			if(root.child[0].height>root.child[1].height)
				root.height=root.child[0].height+1;
			else
				root.height=root.child[1].height+1;
		}
		return root.helper;
	}
	public void printTree()
	{
		printTree(root,0);
	}
	public void printTree(Node root,int depth)
	{
		System.out.println(root.label);
		
		if(root.child[0]!=null){
			for(int i=0;i<depth*5;i++)System.out.print(" ");
			System.out.print("----");
			printTree(root.child[0],depth+1);
		}
		if(root.child[1]!=null){
			for(int i=0;i<depth*5;i++){
				System.out.print(" ");
			}
			System.out.print("----");
			printTree(root.child[1],depth+1);
		
		}
	}
	public Tree getCopy()
	{
		Tree B=new Tree(new ArrayList<Node>());
		int i;
		for(i=0;i<T.size();i++)
		{
			Node t=new Node(T.get(i).spc,T.get(i).label);
			B.T.add(t);
		}
		
		for(i=(T.size()+1)/2;i<T.size();i++)
		{
			if(T.get(i).child[0]!=null)
				B.T.get(i).child[0]=B.T.get(T.indexOf(T.get(i).child[0]));
			if(T.get(i).child[1]!=null)
				B.T.get(i).child[1]=B.T.get(T.indexOf(T.get(i).child[1]));
		}
		for(i=0;i<T.size()-1;i++){
				//if(T.get(i).parent!=null)
//                                System.out.println(i+" "+T.get(i).parent+" "+T.indexOf(T.get(i).parent));
					B.T.get(i).parent=B.T.get(T.indexOf(T.get(i).parent));
				//else B.T.get(i).parent=null;
		}
		
		B.root=B.T.get(B.T.size()-1);
                B.Score=0;
		return B;
	}
	public Node selectRandomInternalNode()
	{
		/*This function selects an internal node with Normal pdf 
		with a previously defined mean and variance*/
		Node r=null;
		int trial=5;
		Random rnd=new Random();
                return T.get(rnd.nextInt((T.size()-3)/2)+(T.size()-1)/2);
//		while(r==null)
//		{
//			int coin=rnd.nextInt(100);
//			int height=(int) (rnd.nextGaussian()*Phylo.height_var_factor+root.height/Phylo.height_mean_factor);
//			int count=0;
//			int index=-1;
//			int min=-1;
//			for(int i=0;i<T.size()-1;i++)
//				if(T.get(i).height==height && T.get(i)!=root && T.get(i).child[0]!=null && T.get(i).child[1]!=null)
//				{
//					count++;
//					if(min==-1 || T.get(i).score<min)
//					{
//						min=T.get(i).score;
//						index=i;
//					}
//				}
//			
//			if(count>0 && index!=-1)
//			{
//				if(coin>Phylo.crossOver_exploitation)
//				{
//					int ind=rnd.nextInt(count)+1;
//					
//					for(int i=(T.size()+1)/2;i<T.size();i++){
//						
//						if(T.get(i).height==height && T.get(i)!=root && T.get(i).child[0]!=null && T.get(i).child[1]!=null)
//							ind--;
//						
//						if(ind==0)
//						{
//							r=T.get(i);
//							break;
//						}
//					}
//				}
//				else
//					r=T.get(index);
//			}
//			else
//			{
//				r=T.get(rnd.nextInt((T.size()-1)/2)+(T.size()+1)/2-1);
//				break;
//			}
//		}
//		return r;
	}
        
        public void printAll(){
            
            for(int i=0;i<T.size();i++){
                System.out.print("index "+ i +" label " + T.get(i).label + " parent ");
                if(T.get(i).parent==null) System.out.print("null" + " lchild ");
                else    System.out.print(T.get(i).parent.label+ " lchild ");
                if(T.get(i).child[0]==null) System.out.print("null rchild ");
                else System.out.print(T.get(i).child[0].label+" rchild ");
                if(T.get(i).child[1]==null) System.out.println("null");
                else System.out.println(T.get(i).child[1].label);
            }
        }
        
        void getSubtreeNodesIndex(int index, ArrayList<Integer> l){
//            ArrayList<Integer> l= new ArrayList<Integer>();
            l.add(index);
            if(T.get(index).child[0]==null) return;
            if(l.contains(T.indexOf(T.get(index).child[0]))){ 
                System.out.println("cycle formed "+ T.get(index).label+" "+T.get(index).child[0].label);
            }
            if(l.contains(T.indexOf(T.get(index).child[1]))){ 
                System.out.println("cycle formed "+ T.get(index).label+" "+T.get(index).child[1].label);
            }
            getSubtreeNodesIndex(T.indexOf(T.get(index).child[0]),l);
            getSubtreeNodesIndex(T.indexOf(T.get(index).child[1]),l);
            
            return ;
        }
        
        void spr(){
//            printAll();
            Random rnd=new Random();
            int index= rnd.nextInt(T.size()-1);
//            System.out.println("Selected node is "+T.get(index).label);
            while( T.get(index).parent==null ) index= rnd.nextInt(T.size()-1);
//            System.out.println("label"+T.get(index).label);
            ArrayList<Integer> forbidden=new ArrayList<Integer>();
            getSubtreeNodesIndex(index, forbidden);
            forbidden.add(T.indexOf(T.get(index).parent));
            if(T.get(index).parent.parent!=null)
                forbidden.add(T.indexOf(T.get(index).parent.parent));
//            for(int i=0;i<forbidden.size();i++)
//                System.out.println(T.get(forbidden.get(i)).label);
//            System.out.println("index"+index);
//            System.out.println("forbsize"+forbidden.size());
//            System.out.println("Tsize"+T.size());
//            if(T.size()-forbidden.size()<=3) return;
            ArrayList<Integer> unused= new ArrayList<Integer>();
            for(int i=(T.size()+1)/2;i<T.size()-1;i++){
                if(!forbidden.contains(i)) {unused.add(i); }
            }
            if(unused.size()==0) return;
//            System.out.println("index "+index + "label: " + T.get(index).label);
            Node sibling=null;
            int pindex=T.indexOf(T.get(index).parent);
//            System.out.println("pindex "+pindex + "label: " + T.get(pindex).label);
            if(T.get(pindex).parent==null) return;
            
            if(T.get(index)==T.get(index).parent.child[0]){ 
              
                sibling=T.get(pindex).child[1];
                sibling.parent=T.get(pindex).parent;
                if(sibling.parent!=null){
                   if(sibling.parent.child[0]==T.get(pindex)) sibling.parent.child[0]=sibling;
                   else sibling.parent.child[1]=sibling;
               }
            }else{
                sibling=T.get(index).parent.child[0];
                sibling.parent=T.get(pindex).parent;
                if(sibling.parent!=null){
                    if(sibling.parent.child[0]==T.get(pindex)) sibling.parent.child[0]=sibling;
                    else sibling.parent.child[1]=sibling;
                }
            }
            
            int regraftIndex=rnd.nextInt(unused.size());
//            System.out.println(regraftIndex);
//            while(regraftIndex==pindex || forbidden.contains(regraftIndex)) {
////                System.out.println(forbidden.contains(regraftIndex));
//                regraftIndex=rnd.nextInt(T.size()/2-1)+(T.size()+1)/2;
////                System.out.println(regraftIndex);
//            }
            regraftIndex=unused.get(regraftIndex);
//            System.out.println("regraft node " +regraftIndex);
            int randomChild= rnd.nextInt(2);
//            System.out.println("clbing");
            if(T.get(pindex).child[0]==T.get(index)){
                T.get(pindex).child[1]=T.get(regraftIndex).child[randomChild];
                T.get(regraftIndex).child[randomChild].parent=T.get(pindex);
            }
            else{ 
                T.get(pindex).child[0]=T.get(regraftIndex).child[randomChild];
                T.get(regraftIndex).child[randomChild].parent=T.get(pindex);
            }
            T.get(regraftIndex).child[randomChild]=T.get(pindex);
            T.get(pindex).parent=T.get(regraftIndex);
//            System.out.println("...");
//            printAll();
        }
        
    public static void main(String[] args) {
        Species spcs[]= new Species[12];
        spcs[0]= new Species("cow","ATCGGTCT");
        spcs[1]= new Species("bat","AAT--ACT");
        spcs[2]= new Species("man","CTA-G-GT");
        spcs[3]= new Species("spider","T-GA-TAT");
        spcs[4]=new Species("whale","CCTTAAGG");
        spcs[5]=new Species("tiger","GTAATGCC");
        spcs[6]=new Species("lion","TTAATGCC");
        spcs[7]=new Species("pokemon","GTGGAGCC");
        spcs[8]=new Species("pokemon1","GTGTTGCC");
        spcs[9]=new Species("pokemon2","CCGGAGCC");
        spcs[10]=new Species("pokemon3","GTGGTGCC");
        spcs[11]=new Species("pokemon4","AAAAAGCC");
        Population p= new Population(1,spcs);
        Tree t=p.GenRandomIndividual();
        t.printTree();
        
//        for(int i=0;i<t.T.size();i++){
//            System.out.println(t.T.get(i).label);
//        }
        
        t.spr();
        t.printTree();
    }

    @Override
    public int compareTo(Tree o) {
        return Score-o.Score;
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
