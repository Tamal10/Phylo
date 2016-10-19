/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package phylo;

import java.util.ArrayList;

/**
 *
 * @author TAMAL
 */
public class upgma {
    
    public Tree upgmaTree(Species s[]){
        ArrayList<Node> t= new ArrayList<Node>();
        double distanceMatrix[][]= new double[s.length][s.length];
        for(int i=0;i<s.length;i++){
            for(int j=0;j<s.length;j++){
                distanceMatrix[i][j]=0;
                if(i>=j){
                    distanceMatrix[i][j]=1000000000; continue;
                }
                //if(i==j)  continue;
                for(int k=0;k<s[i].seq.length;k++){
                    if(s[i].seq[k]!=s[j].seq[k]) distanceMatrix[i][j]++;
                }
            }
        }
        int rep[]=new int[s.length];
        for(int i=0;i<rep.length;i++)rep[i]=i;
//        ArrayList<Node> cluster= new ArrayList<Node>();
//        ArrayList<int []> dist= new ArrayList<int []>();
        int clusterSize[]= new int[s.length];
        for(int i=0;i<s.length;i++){
            clusterSize[i]=1;
            t.add(new Node(s[i],i));
//            cluster.add(new Node(s[i],i));
//            dist.add(distanceMatrix[i]);
        }
        
        for(int k=0;k<s.length-1;k++){
            double min=100000000;int min_i=-1,min_j=-1;
//            for(int i=0;i<distanceMatrix.length;i++){
//                for(int j=0;j<distanceMatrix.length;j++)
//                    System.out.print(distanceMatrix[i][j]+"\t");
//                System.out.println("");
//            }
            for(int i=0;i<distanceMatrix.length;i++){
                if(rep[i]==-1) continue;
                for(int j=0;j<distanceMatrix.length;j++){
                    if(distanceMatrix[i][j]<min){
                        min=distanceMatrix[i][j];min_i=i;min_j=j;
                        if(min_i>min_j){
                            int temp=min_i;
                            min_i=min_j;
                            min_j=temp;
                        }
                    }
                }
            }
            for(int i=0;i<distanceMatrix.length;i++){
                if(i<min_i){ 
                    distanceMatrix[i][min_i]=(clusterSize[min_i]*distanceMatrix[i][min_i]+
                        clusterSize[min_j]*distanceMatrix[i][min_j])/(clusterSize[min_i]+clusterSize[min_j]);
                    distanceMatrix[i][min_j]=100000000;
                }
                else if(i==min_i) continue;
                else if(i<min_j){
                    distanceMatrix[min_i][i]=(clusterSize[min_i]*distanceMatrix[min_i][i]+
                        clusterSize[min_j]*distanceMatrix[i][min_j])/(clusterSize[min_i]+clusterSize[min_j]);
                    distanceMatrix[i][min_j]=100000000;
                }
                else if(i==min_j) distanceMatrix[min_i][min_j]=100000000;
                else{
                    distanceMatrix[min_i][i]=(clusterSize[min_i]*distanceMatrix[min_i][i]+
                        clusterSize[min_j]*distanceMatrix[min_j][i])/(clusterSize[min_i]+clusterSize[min_j]);
                }
            }
//            System.out.println(min_i+" "+min_j);
            for(int i=0;i<distanceMatrix.length;i++) distanceMatrix[min_j][i]=1000000000;
            clusterSize[min_i]+=clusterSize[min_j];
            Node x=new Node(null,s.length+k);
//            System.out.println(rep[min_i]);
//            t.get(rep[min_i]).parent=x;
            t.get(rep[min_j]).parent=x;
            t.get(rep[min_i]).parent=x;
            x.child[0]=t.get(rep[min_i]);
            x.child[1]=t.get(rep[min_j]);
            t.add(x);
            rep[min_i]=s.length+k;
            rep[min_j]=-1;
        }
        
        Tree tree= new Tree(t);
        tree.root=t.get(t.size()-1);
        return tree;
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
        upgma u=new upgma();
        Tree t= new Tree(u.upgmaTree(spcs));
        
        t.root=t.T.get(t.T.size()-1);
        for(int i=0;i<t.T.size();i++){
            if(t.T.get(i).parent==null) System.out.println("wtf "+i+" "+t.T.get(i).label );
        }
        t.printTree();
        System.out.println(t.ParsimonizeTree());
        System.out.println("Score: "+t.Score);
    }
}
