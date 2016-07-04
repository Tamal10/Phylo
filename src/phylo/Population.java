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
public class Population {
    int popSize;
    ArrayList<Tree> pop;
    
    Population(int n, ArrayList<Tree> p){
        popSize=n;
        pop=p;
    }
    Population(int n){
        popSize=n;
        pop= new ArrayList<Tree>();
    }
    
    void RandomPopulation(){
        for(int i=0;i<popSize;i++)
            pop.add(GenRandomIndividual());
    }
    
    Tree GenRandomIndividual(){
        Tree t;
        Random rn= new Random();
        ArrayList<Node> ind= new ArrayList<Node>();
        ArrayList<Node> spcs= new ArrayList<Node>();
        for(int i=0;i<Phylo.noOfSpecies;i++){
            spcs.add(new Node(Phylo.s[i], new String("L"+i)));
        }
        int count=0;
        while(spcs.size()>1){
            Node newInt= new Node(null,new String("I"+count));
            count++;
            for(int i=0;i<2;i++){
                int index= rn.nextInt(spcs.size());
                Node removed=spcs.remove(index);
                removed.parent=newInt;
                newInt.child[i]=removed;
                if(removed.spc!=null)
                    ind.add(0, removed);
                else
                    ind.add(removed);  
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
        for(int i=0; i< popSize;i++){
            pop.get(i).print(pop.get(i).root);
            System.out.println("");
           // System.out.println(pop.get(i).root.label);
            System.out.println("");
            
        }
    }
    
}
