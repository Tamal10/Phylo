/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phylo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;


/**
 *
 * @author TAMAL
 */
public class Population {
    int popSize;
    ArrayList<Tree> pop;
    ArrayList<Node> leaves;
    Species spc[];
    int count=1;

    int [][]rfmatrix;
    
    Population(int n, ArrayList<Tree> p, Species s[]){
        popSize=n;
        pop=p;
        leaves= new ArrayList<Node>();
        spc=s;
        rfmatrix= new int[Phylo.popSize][Phylo.popSize];
        for(int i=0;i<s.length;i++)
            leaves.add(new Node(s[i],i));
    }
    Population(int n, Species s[]){
        popSize=n;
        pop= new ArrayList<Tree>();
        leaves= new ArrayList<Node>();
        spc=s;
        rfmatrix= new int[Phylo.popSize][Phylo.popSize];
        for(int i=0;i<s.length;i++)
            leaves.add(new Node(s[i],i));
    }
    
    void RandomPopulation(int up, int nj){
        
        if(up==1){
            upgma u=new upgma();
            Tree t=u.upgmaTree(spc);
            System.out.println("UPGMA Score: "+t.ParsimonizeTree());
            pop.add(t);
        }
        if(nj==1){
            NJ n= new NJ();
            Tree t= n.NJTree(spc);
            System.out.println("NJ Scoe: "+t.ParsimonizeTree());
            pop.add(t);
        }
        for(int i=0;i<popSize-up-nj;i++)
        {
            Tree t=GenRandomIndividual();
//            System.out.println(t.ParsimonizeTree()+" "+t.Score);
            pop.add(t);
//            System.out.println("pop:"+i);
            //ParsimonyScore.ParsimonizeTree(pop.get(i));
        }
        
    }
    
    Tree GenRandomIndividual(){
        Tree t;
        Random rn= new Random();
        
        ArrayList<Node> ind=new ArrayList<Node>();
        ArrayList<Node> spcs=new ArrayList<Node>();
        for(int i=0;i<leaves.size();i++)
        {
        	Node _t=new Node(leaves.get(i).spc,leaves.get(i).label);
        	ind.add(_t);
        	spcs.add(_t);
        }
        
        int count=0;
        while(spcs.size()>1){
            Node newInt= new Node(null,leaves.size()+count);
            count++;
            for(int i=0;i<2;i++){
                int index= rn.nextInt(spcs.size());
                Node removed=spcs.remove(index);
                if(removed.spc!=null)
                {    ind.get(removed.label).parent=newInt;
                    newInt.child[i]=ind.get(removed.label); 
                }else{
                    removed.parent=newInt;
                    newInt.child[i]=removed;
                    ind.add(removed);  
                }
            }
            spcs.add(newInt);
        //    System.out.println(count+ " " + spcs.size());
        }
        Node r=spcs.remove(0);
        ind.add(r);
        t= new Tree(ind);
        t.root=r;
        return t;
    }
    
    void print(){
       
        for(int i=0; i< pop.size();i++){
            pop.get(i).print();
            pop.get(i).printTree(pop.get(i).root,0);
            System.out.println();
        }
    }
    Tree tournamentSelection(int numberOfCandidate)
    {
    	Random rnd=new Random();
    	Tree t=pop.get(rnd.nextInt(pop.size()));
    	
    	for(int i=0;i<numberOfCandidate;i++){
    		Tree temp=pop.get(rnd.nextInt(pop.size()));
    		if(temp.root.score<t.root.score)
    			t=temp;
    	}
    	return t;
    }
    Tree SelectForDeath()
    {
        Random rnd=new Random();
        if(rnd.nextInt(2)==0){
            Tree worst=pop.get(0);
            for(int i=1;i<pop.size();i++){
                if(pop.get(i).Score>worst.Score) worst=pop.get(i);
            }
            pop.remove(worst);
            return worst;
        }
        else{
            rnd=new Random();
            return pop.remove(rnd.nextInt(pop.size()));
        }
    }
    private void getLeaves(Node root,ArrayList<Node> L)
    {
    	if(root.child[0]==null && root.child[1]==null){
    		L.add(root);
    		return;
    	}
    	getLeaves(root.child[0],L);
    	getLeaves(root.child[1],L);
    }
    Tree CrossOver(Tree pr1,Tree pr2)
    {
    	Tree P1=pr1.getCopy();//child1
    	Tree P2=pr2.getCopy();//child2
    	
    	Node iNode=P1.selectRandomInternalNode(); //select random internalNode
    	//System.out.println("Selected : "+iNode.label);
    	ArrayList<Node> L=new ArrayList<Node>();
    	getLeaves(iNode,L);                       //find corresponding leaves
    	//while(L.size()!=0)
        for(int i=0;i<L.size();i++)
    	{
    		Node l=L.remove(0);
    		L.add(P2.T.get(l.label));
    	}
    	
    	//System.out.println("\nSource");
    	////P1.printTree();
    	//System.out.println("\nDestination:\n");
    	//P2.printTree();
   
    	//delete those leaves from P2
    	ArrayList<Node> spareInternalNodes=new ArrayList<Node>();
    	for(int i=0;i<L.size();i++)
    	{
    		Node leaf_1=L.get(i);
    		Node leaf_2=P2.T.get(leaf_1.label);
    		leaf_2.dirty=1;
    		Node parent=leaf_2.parent;
    		parent.dirty=1;
    		Node sibling = null;
    		Node grandParent=parent.parent;
    		
    		for(int j=0;j<2;j++){
	    		if(parent.child[j]==leaf_2)
	    		{
	    			parent.child[j]=null;
	    			sibling=parent.child[1-j];
	    			break;
	    		}
                }
    		for(int j=0;j<2;j++){
    			if(grandParent!=null && grandParent.child[j]==parent)
    			{
    				grandParent.child[j]=sibling;
    				break;
    			}
    			else if(grandParent==null)
    			{
    				
    				int a=P2.T.indexOf(sibling);
    				P2.T.set(a, P2.root);
    				P2.T.set(P2.T.size()-1, sibling);
    				P2.root=sibling;
    				break;
    			}
                }
    		leaf_2.parent=null;
    		sibling.parent=grandParent;
    		spareInternalNodes.add(parent);
    	}
    	
    	//P2.printTree();
    	//leaves deleted
    	int trial=10;
    	do{
    		iNode=P2.selectRandomInternalNode();
    		if(--trial==0)
    			return P1;
    		//System.out.println("Hel");
    	}while(iNode.dirty==1 || iNode.parent==null || iNode==P2.root);
    	Random rnd=new Random();
    	//create new subtree
    	int count=0;
    	ArrayList<Node> checkList=new ArrayList<Node>(L);
    	
    	while(L.size()>1)
    	{
	    	int l1=rnd.nextInt(L.size());
	    	Node removed_1=L.remove(l1);
	    	int l2=rnd.nextInt(L.size());
	    	Node removed_2=L.remove(l2);
	    	
	    	Node p=spareInternalNodes.remove(0);
	    	removed_1.parent=p;
	    	removed_2.parent=p;
	    	p.child[0]=removed_1;
	    	p.child[1]=removed_2;
	    	
	    	L.add(p);
	    	checkList.add(p);
	    	removed_1.dirty=0;
	    	removed_2.dirty=0;
    	}
    	//System.out.println("From List");
    	
    	//System.out.println("Selected : "+iNode.label);
    	Node r=spareInternalNodes.remove(0);
    	r.dirty=0;
    	
    	//System.out.println();
    	for(int i=0;i<2;i++)
	    	if(iNode.parent.child[i]==iNode)
	    	{
	    		iNode.parent.child[i]=r;
	    		r.parent=iNode.parent;
	    		r.child[i]=iNode;
	    		iNode.parent=r;
	    		r.child[1-i]=L.get(0);
	    		L.get(0).parent=r;
	    		checkList.add(r);
	    		L.get(0).dirty=0;
	    		break;
	    	}
    	
    	//P2.printTree();
    	return P2;
    }
    
    ArrayList<Tree> BestN(int n){
        for(int i=0;i<pop.size();i++) pop.get(i).ParsimonizeTree();
        Collections.sort(pop);
        ArrayList<Tree> x= new ArrayList();
        for(int i=0;i<n;i++) {
            x.add(pop.get(i));
//            System.out.println("Best :"+pop.get(i).Score);
            pop.remove(i);
        }
        
        
        return x;
    }
    
    ArrayList<Tree> diverseN(int n){
        calculateRFDistance();
        ArrayList<Pair> m= new ArrayList<Pair>();
        for(int i=0;i<pop.size();i++){
            int sum=0;
            for(int j=0;j<pop.size();j++) 
                sum+=rfmatrix[i][j];
            m.add(new Pair(sum,i));
//            System.out.println(i+ " pop diversity is "+sum);
        }
        Collections.sort(m);
        ArrayList<Tree> x= new ArrayList<Tree>();
        for(int i=0;i<n;i++){
            x.add(pop.get(m.get(i).y));
//            System.out.println(pop.get(m.get(i).y).Score+" "+m.get(i).x);
        }
        return x;
    }
    
    void calculateRFDistance(){
        rfmatrix= new int[pop.size()][pop.size()];
        for(int i=0;i<pop.size();i++){
            for(int j=0;j<pop.size();j++)
                rfmatrix[i][j]=0;
        }
        for(int i=0;i<pop.size()-1;i++){
            count=1;
            PostOrder(pop.get(i),pop.get(i).root);
           
            for(int j=i+1;j<pop.size();j++){
                mapLeaves(pop.get(i),pop.get(j));
                mapPostOrder(i, j, pop.get(j), pop.get(j).root);
            }
        }
        
//        for(int i=0;i<pop.size();i++){
//            for(int j=0;j<pop.size();j++)
//                System.out.print(rfmatrix[i][j]+" ");
//            System.out.println("");
//            
//        }
    }
    
    int [] PostOrder(Tree t, Node n){
//        System.out.println(n.label);
        if(n.child[0]==null && n.child[1]==null){
            n.start=count;n.end=count; count++;
//            System.out.println(n.label+" at leaf "+ count);
            int x[]=new int[2];
            x[0]=n.start;x[1]=n.end;
            return x;
        }
//        System.out.println(n.child[0].label+" "+n.child[1].label);
        int x1[]=PostOrder(t,n.child[0]);
        int x2[]=PostOrder(t,n.child[1]);
        n.start=x1[0];n.end=x2[1];
//        System.out.println(n.label+"   "+n.start+" "+n.end);
//        if(n.parent!=null)
            t.split.add(new Pair(x1[0],x2[1]));
        x1[1]=x2[1];
        return x1;
    }
    
    void mapLeaves(Tree t1,Tree t2){
        for(int i=0;i<Phylo.noOfSpecies;i++){
            t2.T.get(i).start=t1.T.get(i).start;
            t2.T.get(i).end=t1.T.get(i).end;
//            System.out.println("Leaf labels "+ t1.T.get(i).label+" "+t2.T.get(i).label + " "+ t2.T.get(i).start);
            rfmatrix[pop.indexOf(t1)][pop.indexOf(t2)]=0;
            rfmatrix[pop.indexOf(t2)][pop.indexOf(t1)]=0;
        }
    }
    
    int [] mapPostOrder(int i, int j, Tree t, Node n){
        if(n.child[0]==null && n.child[1]==null){
            int x[]=new int[3];
            x[0]=n.start;x[1]=n.end;x[2]=1;
            return x;
        }
        int x[]=new int[3];
        
        int x1[]=mapPostOrder(i,j,t,n.child[0]);
        int x2[]=mapPostOrder(i,j,t,n.child[1]);
        
//        System.out.println("At node "+ n.label+ " "+"lchild "+n.child[0].label+" "+x1[0]+" "+x1[1]+" "+x1[2]+" rchild "+n.child[1].label+" "+x2[0]+" "+x2[1]+" "+x2[2]+" ");
        
        x[2]=x1[2]+x2[2];
        
        x[0]=x1[0]<x2[0]?x1[0]:x2[0];
        x[1]=x1[1]>x2[1]?x1[1]:x2[1];
        
        if(x[1]-x[0]+1==x[2]){
//            System.out.println("ashlam");
            n.start=x[0];n.end=x[1];
//            System.out.println(pop.get(i).split.contains(new Pair(x[0],x[1])));
            if(!pop.get(i).split.contains(new Pair(x[0],x[1])) ){
                rfmatrix[i][j]++;
                rfmatrix[j][i]++;
            }
            
        }
        else{
//            if(n!=t.root)
            {
            rfmatrix[i][j]++;
            rfmatrix[j][i]++;
            }
        }
        
        
        return x;
    }
    
    
    public static void main(String[] args) {
        Species spcs[]= new Species[5];
        spcs[0]= new Species("cow","ATCGGTCT");
        spcs[1]= new Species("bat","AAT--ACT");
        spcs[2]= new Species("man","CTA-G-GT");
        spcs[3]= new Species("spider","T-GA-TAT");
        spcs[4]=new Species("whale","CCTTAAGG");
//        spcs[5]=new Species("tiger","GTAATGCC");
        
        Population p= new Population(2,spcs);
        
        
        
        ArrayList<Node> t1= new ArrayList<Node>();
        for(int i=0;i<5;i++)
            t1.add(new Node(spcs[i], i));
        for(int j=0;j<4;j++)
            t1.add(new Node(null,j+5));
        
        t1.get(8).child[0]=t1.get(5);t1.get(5).parent=t1.get(8);
        t1.get(8).child[1]=t1.get(6);t1.get(6).parent=t1.get(8);
        
        t1.get(5).child[0]=t1.get(0);t1.get(0).parent=t1.get(5);
        t1.get(5).child[1]=t1.get(1);t1.get(1).parent=t1.get(5);
        
        t1.get(6).child[0]=t1.get(2);t1.get(2).parent=t1.get(6);
        t1.get(6).child[1]=t1.get(7);t1.get(7).parent=t1.get(6);
        
        t1.get(7).child[0]=t1.get(3);t1.get(3).parent=t1.get(7);
        t1.get(7).child[1]=t1.get(4);t1.get(4).parent=t1.get(7);
        p.pop.add(new Tree(t1));
        p.pop.get(0).root=t1.get(8);
        
       
        
        ArrayList<Node> t2= new ArrayList<Node>();
        for(int i=0;i<5;i++)
            t2.add(new Node(spcs[i], i));
        for(int j=0;j<4;j++)
            t2.add(new Node(null,j+5));
        
        t2.get(8).child[0]=t2.get(0);t2.get(0).parent=t2.get(8);
        t2.get(8).child[1]=t2.get(7);t2.get(7).parent=t2.get(8);
        
        t2.get(7).child[0]=t2.get(1);t2.get(1).parent=t2.get(5);
        t2.get(7).child[1]=t2.get(6);t2.get(6).parent=t2.get(5);
        
        t2.get(6).child[0]=t2.get(2);t2.get(2).parent=t2.get(6);
        t2.get(6).child[1]=t2.get(5);t2.get(5).parent=t2.get(6);
        
        t2.get(5).child[0]=t2.get(3);t2.get(3).parent=t2.get(5);
        t2.get(5).child[1]=t2.get(4);t2.get(4).parent=t2.get(5);
        
        Tree t= new Tree(t2);
        t.root=t.T.get(8);
        p.pop.add(t);
        p.pop.get(0).printTree();
        p.pop.get(1).printTree();
        p.calculateRFDistance();
        
        for(int i=0;i<2;i++){
            for(int j=0;j<2;j++){
                System.out.print(p.rfmatrix[i][j]+" ");
            }
            System.out.println("");
        }
    }
    
}
