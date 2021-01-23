// Ritik Vatsal | 2019321 | Assignment 4
// Design Patterns - Structural, Adapter, Behavioural, Strategy Design, Command Design

package com.company;

import java.time.*;
import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;


public class Main {
    static int cnt=0;
    public static Stack<Integer> RandNumbers=new Stack<>();
    static int boom=0;
    static Random randDeg=new Random();
    static int SearchHistory;
    public static void main(String[] args) throws InterruptedException {

        int n;
        Scanner in = new Scanner(System.in);
        System.out.println("---- MultiThreading A4 | Ritik Vatsal | 2019321 ----");
        System.out.println("Please Enter Number of Nodes...");
        n=in.nextInt();
        int limit = (int) (1000000/1.32);
        Node root=new Node();

        Integer  number[]= new Integer[1000000];
        long Timer=System.nanoTime();
        for (int i = 0; i < 1000000 ; i++) {
            number[i]=i+1;
        }
        List<Integer> randNum= Arrays.asList(number);
        Random randDeg=new Random();
        Collections.shuffle(randNum);
        for (int i = 0; i < 1000000; i++) {
            RandNumbers.push(randNum.get(i));
        }
        if (n==1){root.setInfo(RandNumbers.pop(),0);}
        else if (n<6){root.setInfo(RandNumbers.pop(),randDeg.nextInt(n-1)+1);}
        else{root.setInfo(RandNumbers.pop(),randDeg.nextInt(3)+2);}
        cnt=cnt+1+root.getDegree();
//        System.out.println(root.getDegree() + " " + cnt);
        if (n>=limit){
            n=limit;
        }
        root=builder(root,n);
        long TimerStop=System.nanoTime();


        System.out.println();
//        System.out.println(boom);
        int ht=height(root);
//        find(0, root, ht);
        System.out.println("Height of the Tree is | " + ht);
        System.out.println("Time Taken | "+ (TimerStop-Timer)/1000000 + "ms");

        System.out.println("\nPlease enter the number of Nodes...");
        int m=in.nextInt();
        System.out.println("Now Enter the Values...");
        int[] search=new int[m];
        for (int i = 0; i < m; i++) {
            search[i]=in.nextInt();
        }
        int val;

        Timer=System.nanoTime();
        for (int i = 0; i <m ; i++) {
            val=search[i];
            System.out.println("Searching " + val);
            find(val, root,ht);
            if (SearchHistory==0){
                System.out.println("Value NOT in TREE :(");
            }
            SearchHistory=0;
        }
        TimerStop=System.nanoTime();
        long sequTime=(TimerStop-Timer)/1000000;
        System.out.println("Time Taken in Sequential | "+ sequTime + "ms");


        System.out.println("---- Select Method ----\n1. Threads\n2. ForkJoinPool");
        int choice=in.nextInt();
        System.out.println("Enter Number of threads (1-4) ....");
        int power=in.nextInt();
        if (choice==1)
        {

            int wrk=(int)(m/power);
            int[][] vals=new int[power][];
            for (int i = 0; i <power ; i++) {
                if (i==power-1){
                    vals[i]=new int[m-(i*wrk)];
                    for (int j = 0; j <m-(i*wrk) ; j++) {
                        vals[i][j]=search[(i*wrk)+j];
                    }
                }
                else{
                    vals[i]=new int[wrk];
                    for (int j = 0; j <wrk ; j++) {
                        vals[i][j]=search[(i*wrk)+j];
                    }
                }
            }
            Timer=System.nanoTime();

            Thread[] thrAr= new Thread[power];
            for (int i = 0; i < power; i++) {
                threading thread1=new threading();
                thread1.setTHread(vals[i], root, ht );

                thrAr[i]=new Thread(thread1);
                thrAr[i].start();

            }
            for (int i = 0; i < power ; i++)
            {
                thrAr[i].join();
            }
            TimerStop=System.nanoTime();
            long thrdtim=(TimerStop-Timer)/1000000;
            double speedup=((double)sequTime/thrdtim);
            System.out.println("Time Taken in Thread | "+ thrdtim + "ms");
            System.out.println("\nSequential Time > "+ sequTime+" | Thread Time > "+ thrdtim);
            System.out.println("The Thread Parallel Speedup is " + speedup );
        }

        else if (choice==2)
        {

            int wrk=(int)(m/power);
            int[][] vals=new int[power][];
            Timer=System.nanoTime();
            threading fork1=new threading();
            fork1.setFork(search,root,ht,power);
            fork1.settot(m);
            ForkJoinPool fjp= ForkJoinPool.commonPool();
            int res= (int) fjp.invoke(fork1);
            TimerStop=System.nanoTime();
            long forktim=(TimerStop-Timer)/1000000;
            System.out.println("Time Taken in ForkJoinPool | "+ forktim + "ms");
            System.out.println("\nSequential Time > "+ sequTime+" | Fork Time > "+ forktim);
            double speedup=((double)sequTime/forktim);
            System.out.println("The Fork Parallel Speedup " + speedup );

        }

    }

    static void find(int val, Node root, int ht )
    {
        int h = height(root);

        for (int i=1; i<=h; i++){
            Finder(val,root, i, ht);
        }

    }
    static int height(Node root)
    {   int ret=0;
        if (root == null)
            return ret;
        else
        {
            if (root.getData()!=-3){

                for (int i = 0; i < root.getDegree() ; i++) {
                    int height =height(root.child[i]);
                    if (height>ret){
                        ret=height;
                    }
                }
                return (ret+1);
            }
            else return 0;
        }

    }


    static void Finder (int val, Node root ,int level , int ht)
    {
        if (root == null)
            return;
        if (level == 1)
        {
//                System.out.print(root.getData()+" ");
            if (root.getData()==val){
                int deep=height(root);
                deep=ht-deep;
                System.out.println("FOUND "+val+" ! at Depth | " + deep );

                SearchHistory=1;
            }
//                System.out.print(root.getData() + " ");
            boom=boom+1;}
        else if (level > 1)
        {
            for (int i = 0; i <root.getDegree() ; i++) {
                Finder(val, root.child[i], level-1, ht);
            }
        }
    }


    //Recursive Function to Build Tree!
    public static Node builder(Node root,  int n){
        if (n<15000){if (cnt>=n){
            return root;
        }

            for (int i = 0; i < root.getDegree(); i++) {
                root.child[i]=new Node();


                if (cnt<n) {
                    if (cnt+5<=n){root.child[i].setInfo(RandNumbers.pop(), randDeg.nextInt(3) + 2);}
                    else if (cnt+4<=n){root.child[i].setInfo(RandNumbers.pop(), randDeg.nextInt(2) + 2);}
                    else if (cnt+3<=n){root.child[i].setInfo(RandNumbers.pop(), randDeg.nextInt(1)+2 );}
                    else if (cnt+2<=n){root.child[i].setInfo(RandNumbers.pop(), 2);}
                    else if (cnt+1<=n){root.child[i].setInfo(RandNumbers.pop(), 1);}
                }
                else {root.child[i].setInfo(RandNumbers.pop(), 0);}
                cnt=cnt+root.child[i].getDegree();
                cnt++;
                System.out.println(cnt+" f");

                root.child[i]=builder(root.child[i], n);
            }
            return root;}
        else {
            Queue<Node> q=new LinkedList<Node>();
            int mp=n-1;
            q.add(root);
            Node tempNode;
            while(q.size()>0)
            {
                Node temp  = q.poll();
                tempNode=builder(root,mp-n);
                for(int i=0;i<temp.getDegree();i++)
                {
                    Node child=new Node();
                    if (RandNumbers.size()>0){child.setInfo(RandNumbers.pop(),getRand(n));}
                    else{child.setInfo(-3,getRand(n));}
                    temp.child[i]=child;
                    tempNode.getData();
                    q.add(child);
                    cnt=cnt+child.getDegree();

                    cnt++;
                }

            }

            return root;

        }
    }

    public static int getRand(int n){
        int finint=0;



        if (n-cnt<2){finint=randDeg.nextInt(2);}
        else{
            if (n-cnt==2){ finint=2;}
            if (n-cnt==3){ finint=randDeg.nextInt(2)+2;}
            if (n-cnt==4)finint=randDeg.nextInt(3)+2;
            else if (n-cnt>=5)finint=randDeg.nextInt(4)+2;
        }

        return finint;
    }


}

class Node {
    private int data;
    private int degree;
    Node child[]= new Node[5];

    public void setInfo(int data, int degree) {
        this.data = data;
        this.degree = degree;

    }

    public int getData() {
        return data;
    }
    public int getDegree() {
        return degree;
    }
}

class Tree{
    private int cnt;
    private Node root;
}

class threading extends RecursiveTask implements Runnable {
    private int[] value;
    private Node inroot;
    private int ht;
    public static int SearchHistory=0;
    private int ForkPower;
    private int total;


    public void setTHread(int[] val ,Node root, int ht) {
        this.ht = ht;
        this.inroot=root;
        this.value=val;

    }
    public void setFork(int[] val ,Node root, int ht, int ForkPower) {
        this.ht = ht;
        this.inroot=root;
        this.value=val;
        this.ForkPower=ForkPower;

    }

    @Override
    public void run(){
        int h = height(0,inroot);

        for (int j = 0; j < value.length ; j++) {for (int i=1; i<=h; i++)
            Finder(value[j],inroot, i, h);
            if (SearchHistory==0){
//                System.out.println("Value "+ value[j]+" NOT in TREE :(");

            }
            SearchHistory=0;
        }


    }

    public void settot(int total) {
        this.total = total;
    }

    @Override
    public Integer compute() {

        if (ForkPower>1 && ForkPower<total){
//            System.out.println("f");
            if (value.length==total){int wrk=(int)(value.length/ForkPower);
                int[][] vals=new int[ForkPower][];
                for (int i = 0; i <ForkPower ; i++) {
                    if (i==ForkPower-1){
                        vals[i]=new int[value.length-(i*wrk)];
                        for (int j = 0; j <value.length-(i*wrk) ; j++) {
                            vals[i][j]=value[(i*wrk)+j];
                        }
                    }
                    else{
                        vals[i]=new int[wrk];
                        for (int j = 0; j <wrk ; j++) {
                            vals[i][j]=value[(i*wrk)+j];
                        }
                    }
                }
//            threading[] forktask=new threading[ForkPower];
                for (int i = 0; i < ForkPower; i++) {
                    threading thread1=new threading();
                    thread1.setFork(vals[i], inroot, ht , ForkPower);
                    thread1.fork();
//                    thread1.helpQuiesce();
                    helpQuiesce();
                }

            }
            else{
                int h = height(0,inroot);

                for (int j = 0; j < value.length ; j++) {for (int i=1; i<=h; i++)
                    Finder(value[j],inroot, i, h);
                    if (SearchHistory==0){
//                        System.out.println("Value "+ value[j]+" NOT in TREE :(");

                    }
                    SearchHistory=0;
                }
            }

        }

        else{int h = height(0,inroot);

            for (int j = 0; j < value.length ; j++) {for (int i=1; i<=h; i++)
                Finder(value[j],inroot, i, h);
                if (SearchHistory==0){
                    System.out.println("Value "+ value[j]+" NOT in TREE :(");
                }
                SearchHistory=0;
            }}
        return 1;
    }

    static void Finder (int val, Node root , int level , int ht)

    {
        if (root == null)
            return;
        if (level == 1)
        {
            if (root.getData()==val){
                int deep=height(0,root);
                deep=ht-deep;
                System.out.println("FOUND "+val+" ! at Depth | " + deep );
                SearchHistory=1;
            }
        }
        else if (level > 1)
        {
            for (int i = 0; i <root.getDegree() ; i++) {
                Finder(val, root.child[i], level-1, ht);
            }
        }
    }

    static int height(int lvl, Node root)
    {   int ret=0;
        if (root == null)
            return ret;
        else
        {
            if (root.getData()!=-3){

                for (int i = 0; i < root.getDegree() ; i++) {
                    int height =height(0,root.child[i]);
                    if (height>ret){
                        ret=height;
                    }
                }
                return (ret+1);
            }
            else return 0;
        }

    }

}