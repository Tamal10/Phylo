/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phylo;

import java.util.ArrayList;

/**
 *
 * @author TAMAL
 */
public class Tree {
    ArrayList <Node> T;
    Node root;
    
    Tree(Tree Tr){
        this.T=Tr.T;
    }
    Tree(ArrayList<Node> T){
        this.T=T;
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
    }
}
