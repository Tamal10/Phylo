/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package phylo;

/**
 *
 * @author TAMAL
 */
public class Pair implements Comparable<Pair>{
    int x;int y;
    Pair(int x, int y){
        this.x=x;
        this.y=y;
    }
    Pair(){
        x=0;y=0;
    }
    
    @Override
    public boolean equals(Object c){
        Pair p=(Pair) c;
        return (p.x==x && p.y==y);
    }

    @Override
    public int compareTo(Pair o) {
        return o.x-x;
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
