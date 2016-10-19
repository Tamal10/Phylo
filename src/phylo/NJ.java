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
public class NJ {
    public Tree NJTree(Species s[]){
        Tree t=null;
        ArrayList<Node> na= new ArrayList<Node>();
        
        for(int i=0;i<s.length;i++){
            na.add(new Node(s[i],i));
        }
        t= new Tree(na);
        int dirty[]= new int[s.length];
        for(int i=0;i<s.length;i++)
            dirty[i]=i;
        double distanceMatrix[][]= new double[s.length][s.length];
        for(int i=0;i<s.length;i++){
//            dirty[i]=0;
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
        
        for(int i=0;i<s.length-1;i++){
            double R[]=new double[s.length];
            for(int j=0;j<s.length;j++){
                if(dirty[j]==-1) continue;
                
                for(int k=0;k<s.length;k++){
                    if(dirty[k]!=-1 && j!=k){
                        if(k>j)
                            R[j]+=distanceMatrix[j][k];
                        else
                            R[j]+=distanceMatrix[k][j];
                    }
                }
            }
            
            double S[][]= new double[s.length][s.length];
            double min=1000000000; int c1=0,c2=0;
            for(int j=0;j<s.length;j++){
                if(dirty[j]==-1) continue;
                for(int k=j+1;k<s.length;k++){
                    if(j!=k && dirty[k]!=-1){
                        if(j<k)
                            S[j][k]=(s.length-i-2)*distanceMatrix[j][k]-R[j]-R[k];
                        else
                            S[j][k]=(s.length-i-2)*distanceMatrix[k][j]-R[j]-R[k];
                        if(S[j][k]<min){
                            min= S[j][k];
                            c1=j;
                            c2=k;
                        }
                    }
                }
            }
            
           
            for(int j=0;j<s.length;j++){
                if(j!=c1 && j!=c2 && dirty[j]!=-1){
                    if(j<c1 && j<c2){
                        distanceMatrix[j][c1]=0.5*(distanceMatrix[j][c1]+distanceMatrix[j][c2]-min);
                    }
                    else if(j>c1 && j<c2){
                        distanceMatrix[c1][j]=0.5*(distanceMatrix[c1][j]+distanceMatrix[j][c2]-min);
                    }
                    else{
                        distanceMatrix[c1][j]=0.5*(distanceMatrix[c1][j]+distanceMatrix[c2][j]-min);
                    }
                }
            }
//            System.out.println("Selected "+c1+ " "+dirty[c1]+" "+c2+" "+dirty[c2]);
            Node x= new Node(null,s.length+i);
            t.T.get(dirty[c1]).parent=x;
            t.T.get(dirty[c2]).parent=x;
            x.child[0]=t.T.get(dirty[c1]);
            x.child[1]=t.T.get(dirty[c2]);
            t.T.add(x);
            
            dirty[c2]=-1;
            dirty[c1]=s.length+i;
            
//            for(int j=0;j<s.length;j++){
//                for(int k=0;k<s.length;k++){
//                    System.out.print(distanceMatrix[j][k]+" ");
//                }
//                System.out.println("");
//            }
            
        }
        t.root=t.T.get(t.T.size()-1);
        return t;
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
        NJ u=new NJ();
        Tree t= u.NJTree(spcs);
        
        t.root=t.T.get(t.T.size()-1);
        for(int i=0;i<t.T.size();i++){
            if(t.T.get(i).parent==null) System.out.println("wtf "+i+" "+t.T.get(i).label );
        }
        t.printTree();
        System.out.println(t.ParsimonizeTree());
        System.out.println("Score: "+t.Score);
    }
    
}
