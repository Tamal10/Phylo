/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phylo;
import java.util.ArrayList;
import java.util.Scanner;

//import org.apache.poi.sl.usermodel.Sheet;
//import org.apache.poi.ss.usermodel.Row;
//import org.apache.poi.xssf.usermodel.XSSFSheet;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.lang.Math;
import java.util.Random;
/**
 *
 * @author TAMAL
 */
public class Phylo {

    /**
     * @param args the command line arguments
     */
    
    static int noOfSpecies=5;
    static int seqLen;
    static float height_mean_factor=(float) 2;
    static int height_var_factor=1;
    static int popSize=25;
    static int tournament_candidate=7;
    static int t=50; //number of iteration to hill climb
    static int time=800; //total time
    static int stime=500;
    static int crossOver_exploitation=10; //percentage value
    static Species s[];
    
    static int bestn=5;
    static  int diversen=10;
    
    static ArrayList<Species> input=new ArrayList<Species>();
    Phylo(int n, Species s[]){
        noOfSpecies=n;
        Phylo.s=s;
    }
    
    ArrayList<Node> genLeafNodes(Species s[]){
        ArrayList<Node> leaves= new ArrayList<Node>();
        for(int i=0;i<s.length;i++)
            leaves.add(new Node(s[i],i));
        return leaves;
    }
    
    static void scatter(){
        long startTime=System.currentTimeMillis();
        Population p= new Population(Phylo.popSize,s);

        p.RandomPopulation(1,1);
        
        Tree Best=null;
        
        for(int i=0;i<popSize;i++){
        	Tree T=p.pop.get(i);
        	T.ParsimonizeTree(); //assess fitness
        	T.HillClimb(t); //HillClimbing
//        	T.ParsimonizeTree();
//                System.out.println(T.Score+" ");
        	if(Best==null || T.Score<Best.Score)
        		Best=T;
        }
        System.out.println("Best Score: "+Best.Score);
        
        do{
           ArrayList<Tree> b= p.BestN(bestn);
           ArrayList<Tree> m= p.diverseN(diversen);
           b.addAll(m);
           p= new Population(b.size()+m.size(),b,s);
           Phylo.popSize=b.size()+m.size();
           
           ArrayList<Tree> Q= new ArrayList<Tree>();
           Random rnd= new Random();
           for(int i=0;i<15;i++){
               
                    Tree P1= p.pop.get(rnd.nextInt(p.pop.size()));
                    Tree P2= p.pop.get(rnd.nextInt(p.pop.size()));
                    Tree C1= p.CrossOver(P1,P2);
                    Tree C2= p.CrossOver(P2,P1);

                    C1.HillClimb(t);
                    C2.HillClimb(t);
    //                System.out.println(P1.Score+" Scores "+P2.Score);
                    C1.ParsimonizeTree();
                    C2.ParsimonizeTree();
    //        	   System.out.println(C1.Score+" Scores AP "+C2.Score);
                    if(C1.Score < Best.Score)
                            Best=C1;
                    if(C2.Score < Best.Score)
                            Best=C2;
                    Q.add(C1);
                    Q.add(C2);
               
           }
           p.pop.addAll(Q);
           p.popSize=p.pop.size();
           Phylo.popSize=p.popSize;       
        }while(stime-->0);
        long endTime=System.currentTimeMillis();
        endTime-=startTime;
        Best.printTree();
        System.out.println("Best Score: "+Best.Score);
        System.out.println("Time: "+endTime);
    }
    
    static void memetic(){
        long startTime=System.currentTimeMillis();
        Population p= new Population(Phylo.popSize,s);

        p.RandomPopulation(1,1);		//initialize pop

        Tree Best=null;

        for(int i=0;i<popSize;i++){
        	Tree T=p.pop.get(i);
        	T.ParsimonizeTree(); //assess fitness
        	T.HillClimb(t); //HillClimbing
//        	T.ParsimonizeTree();
//                System.out.println(T.Score+" ");
        	if(Best==null || T.Score<Best.Score)
        		Best=T;
        }
        System.out.println("Best Score: "+Best.Score);
//        Best.printTree();
        do
        {
//            p.pop.get(0).HillClimb(10);
//            if(p.pop.get(0).Score<Best.Score) Best=p.pop.get(0);
        	//select parent
//            System.out.println(p.pop.size()+" "+popSize);
        	Tree P1=p.tournamentSelection(Phylo.tournament_candidate);
        	Tree P2=p.tournamentSelection(Phylo.tournament_candidate);
        	
        	while(P1==P2)
        		P2=p.tournamentSelection(Phylo.tournament_candidate);
        	
        	//cross-over
        	Tree C1=p.CrossOver(P1, P2);
        	Tree C2=p.CrossOver(P2, P1);
        	   
        	C1.HillClimb(t);
        	C2.HillClimb(t);
//                System.out.println(P1.Score+" Scores "+P2.Score);
        	C1.ParsimonizeTree();
        	C2.ParsimonizeTree();
//        	   System.out.println(C1.Score+" Scores AP "+C2.Score);
        	if(C1.Score < Best.Score)
        		Best=C1;
        	if(C2.Score < Best.Score)
        		Best=C2;
        	
        	p.SelectForDeath();
        	p.SelectForDeath();
        	
        	p.pop.add(C1);
        	p.pop.add(C2);
        	
        }while(time-->0);
        
        long endTime=System.currentTimeMillis();
        System.out.println("\nScore: "+Best.Score+"\nOther Scores:");
        System.out.println("Run Time: "+(-startTime+endTime)/1000+" s");
        Best.printTree();
    }
    
    static void plainHill(){
//        Tree t= new NJ().NJTree(s);
        Tree t= new upgma().upgmaTree(s);
//        Population p= new Population(s.length,s);
//        Tree t= p.GenRandomIndividual();
        Tree Best=t;
        t.ParsimonizeTree();
        Random rnd= new Random();
        System.out.println("UPGMA score: "+ Best.Score);
        for(int i=0;i<1000;i++){
            Tree copy=t.getCopy();
            copy.spr();
            copy.ParsimonizeTree();
//            System.out.println(i+" Best Score "+Best.Score);
            if(copy.Score<Best.Score) {Best=copy; t=copy;}
            else if(rnd.nextInt(2)==0) t=copy;
        }
        System.out.println("Score "+Best.Score);
        Best.printTree();
    }
    
    public static void main(String[] args) {
        // TODO code application logic here
    	int count=0;
        FileReader f;
        String fname;
            String choice;
            if(args.length>0){
                 fname=args[0];
                 if(args.length>1)
                    choice = args[1];
                 else
                     choice="m";
                 System.out.println(args[0]+"     "+args[1]);
            }
            else{
                System.out.println("Enter Dataset No: ");
                Scanner scn= new Scanner(System.in);
                fname= scn.nextLine();
                fname="Sample_data_"+fname+".txt";
                System.out.println("Enter Choice (m for memetic and s for scatter)");
                choice=scn.nextLine();
                
            }
    	try {
            
//            f= new FileReader(args[0]);
            f= new FileReader(fname);
			Scanner scn=new Scanner(f);
			while(scn.hasNext())
			{
				String line=scn.nextLine();
				Scanner scn2=new Scanner(line);
				line.trim();
				while(scn2.hasNext())
				{
					int flag2=0;
					String spc=scn2.next();
					String seq=scn2.next();
					if(count==0)
						seqLen=seq.length();
					else if(seq.length()>seqLen)
						seqLen=seq.length();
					input.add(new Species(spc,seq));
					char [] seq2=seq.toCharArray();
					
					count++;
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//    	System.out.println("I am here ");
        noOfSpecies=count;
        s= new Species[count];
        for(int i=0;i<count;i++)
        {
        	if(input.get(i).seq.length!=seqLen){
        		char[] temp=new char[seqLen];
        		for(int j=0;j<input.get(i).seq.length;j++)
        			temp[j]=input.get(i).seq[j];
        		for(int j=input.get(i).seq.length;j<seqLen;j++)
        			temp[j]='-';
        		input.get(i).seq=temp;
        	}
        	s[i]=input.get(i);
//        	System.out.println(s[i].seq);
        }
//        System.out.println("Species: "+s.length);
       /* s[0]= new Species("cow","ATCGGTCT");
        s[1]= new Species("bat","AAT--ACT");
        s[2]= new Species("man","CTA-G-GT");
        s[3]= new Species("spider","T-GA-TAT");
        s[4]=new Species("whale","CCTTAAGG");
        s[5]=new Species("tiger","GTAATGCC");
        s[6]=new Species("lion","TTAATGCC");
        s[7]=new Species("pokemon","GTGGAGCC");
        s[8]=new Species("pokemon1","GTGTTGCC");
        s[9]=new Species("pokemon2","CCGGAGCC");
        s[10]=new Species("pokemon3","GTGGTGCC");
        s[11]=new Species("pokemon4","AAAAAGCC");*/
        
//        plainHill();
        if(choice.equals("m"))
            memetic();
        else if(choice.equals("s")) 
          scatter();
        //write to a excel file
//        String fileName="Result.xlsx";
//        FileInputStream fis;
//        FileOutputStream fos;
//        XSSFWorkbook wb=null;
//        try {
//          fis= new FileInputStream(fileName);
//          wb = new XSSFWorkbook(fis);
//          fis.close();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        
//        String sheetName="result";
//        XSSFSheet sheet =  wb.getSheet(sheetName);
//        int row_count= sheet.getLastRowNum();
//        System.out.println("r:"+row_count);
//        Row r=sheet.createRow(row_count+1);
//        r.createCell(0).setCellValue(Best.Score);
//        r.createCell(1).setCellValue((-startTime+endTime)/1000);
//        try {
//            fos= new FileOutputStream(fileName);
//            wb.write(fos);
//            
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        
//        for(int i=0;i<Best.T.size();i++){
//            System.out.println(Best.T.get(i).spc);
//        }
    }
}
