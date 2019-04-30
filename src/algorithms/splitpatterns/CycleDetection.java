package algorithms.splitpatterns;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import algorithms.splitpatterns.cycle.Cycle;
import algorithms.splitpatterns.cycle.Edge;
import algorithms.splitpatterns.cycle.Graph;
import algorithms.splitpatterns.cycle.Vertex;


/**
 * To find if the given graph has cycle , We need to maintain one additional set of exited vertices and do dfs.
 * In any step if iteration if a vertex is already in visited set and yet not exited from recusion stack, then
 * we can return true. We can optimize using one array with 3 different values.
 */
public class CycleDetection {	
    protected Graph graph;
//    protected Set<String> visited;
//    protected Set<String> exited;
//    protected List<String> path;
    private String start;
//    protected String endVertex;
//    private List<String> candidatesName = new ArrayList<String>();

    private ArrayList<String> trace=new ArrayList<String>();//从出发节点到当前节点的轨迹
    private boolean hasCycle=false;  
    private int verticesNumber;
    private String oriString;
    private ArrayList<Cycle> cycleList = new ArrayList<>();
    private ArrayList<ArrayList<String>> cycleAllList = new ArrayList<>();
   
    
    int cicle_serial = 0;
    
   
    public CycleDetection(Graph graph) {
        this.graph = graph;
        trace = new ArrayList<>();//路径
        verticesNumber = this.graph.getVerticesNumber();
    }
    public CycleDetection(String inputString,Graph graph) {
        this.graph = graph;
        trace = new ArrayList<>();//路径
        verticesNumber = this.graph.getVerticesNumber();
        oriString = inputString;
        this.start = inputString.substring(0, 1);
    }
    
    
//    public String getStart() {
//    	return this.startVertex;	
//	}
//    public String getEnd() {
//    	return this.endVertex;	
//	}
    public ArrayList<Cycle> getCycleList() {
		return this.cycleList;
	}
     
//    public List<String> exclude() {
//    	for(Vertex vertex : this.graph.getVertices()){
//    		if(vertex.getInNumber()>1 && vertex.getOutNumber()>1){
//    			candidatesName.add(vertex.getName());
//    		}
//    	}
//    	return candidatesName;
//	}
    
//    public boolean exclude(Vertex vertex) {
//    	if(vertex.getInNumber()<1 || vertex.getOutNumber()<1){
//    		return true;	
//    	}
//    	return false;
//    }  
    
    
//    public LinkedList<String> findCycle() {
//    	for(String name : this.graph.getVerticesName()){
//    		Vertex vertex = graph.findVertex(name);
//    		if(!exclude(vertex)){
//    			trace.add(name);
//    			startVertex = name;
//    		}
//    	}
//    	return null;
//		
//	}
    
//    public boolean hasCycle(String name) {
//    	if (!visited.contains(name) && !dfs(name)){
//            return false;
//        }
//    	return true;	
//	}
    
  
    public boolean hasCycle() {
        findCycle(this.graph.getVerticesName(0));
//        System.out.println(this.graph.getVerticesName(0));
        if(!hasCycle){
            System.out.println("No Cycle.");
            return false;
        }else {
        	setStart(findStartVertex(cycleAllList,graph));
        	return true;
		}
    }
   
	public void findCycle(String v)
    {
       int j=0;   
       if((j=this.trace.indexOf(v))!=-1)
       {
//    	  System.out.print("j:"+j);
           hasCycle=true;  
           String first  = trace.get(j);//1231
        	   ArrayList<String> cycle = new ArrayList<>();
        	   ArrayList<String> cycle2 = new ArrayList<>();
//        	   String cycle="#";
//        	   System.out.print("Cycle:");
        	   
        	   while(j<trace.size())
               {
//                  System.out.print(trace.get(j)+" "); // aaa
                  cycle.add(trace.get(j));
//                  cycle+=trace.get(j);
//                  cycle +=",";
                  j++;
               }
        	  

              //calculate the trace number 
//             System.out.print(" size:"+trace.size());
//        	   System.out.print( " toFind: "+cycle.toString());
//        	   System.out.println(); // aaa
        	   cycleAllList.add(cycle);
        	   
        	   cycle2.addAll(cycle);
//        	   cycle.add(first);//1231
        	   System.out.println( "Cycle: "+cycle.toString());
//        	   System.out.println(" cycle number:" + findCycleNumberFromInputString(convertToString(cycle)));//aaa
//        	   int number = countCycleByEdges(cycle);
        	
        	   //the cycle real exit.
        	   int number = findCycleNumberFromInputString(convertToString(cycle));
        	   
        	   System.out.println(number);
        	   if(number != 0){
//        		   System.out.println(cycle.toString());
//        		   System.out.println(" cycle number2:" + countCycleByEdges(cycle));
//        		   System.out.println(cycle.size());
//        		   System.out.println(cycle2.size());
        		   // the cycle size >1 ,not 2->2
        		   if(cycle2.size()>=1){
        			   cicle_serial+=1;
        			   Cycle cycle3 = new Cycle();
        			   cycle3.setSerialNumber(cicle_serial);
                	   cycle3.setTrace(cycle2);
                	   cycle3.setWeight(number);
                	   cycleList.add(cycle3);
        		   }
        		   
//            	   System.out.println(cycleList.size());
        	   }       	  
               return;
        }
        this.trace.add(v);
         
        for(int i=0;i<verticesNumber;i++)
        {
            if(graph.findVertex(v).hasNeighbour(this.graph.getVerticesName(i)))
                findCycle(this.graph.getVerticesName(i));
        }
        trace.remove(trace.size()-1);
    }
	
	private String findStartVertex(ArrayList<ArrayList<String>> cycleAllList,Graph graph) {
		ArrayList<String> startVertexList = new ArrayList<>();
		for(int i=0;i<cycleAllList.size();i++){
			if(cycleAllList.get(i).size()>1){
				if(startVertexList.indexOf(cycleAllList.get(i).get(0))==-1){
					startVertexList.add(cycleAllList.get(i).get(0));
				}
			}
		}
		String startVertex = startVertexList.get(0);
		for (int i=1;i<startVertexList.size();i++){		
			if(graph.findVertex(startVertexList.get(i)).getOutNumber()>graph.findVertex(startVertex).getOutNumber()){
				startVertex = startVertexList.get(i);
			}
		}	
		return startVertex;
	}
    
    private int countCycleByEdges(ArrayList<String> cycle) {
		// TODO Auto-generated method stub
    	int smallWeight = 1000;
    	int weight=0;
    	for (int i=0;i<cycle.size()-1;i++){
    		Vertex pre = graph.findVertex(cycle.get(i));
    		Vertex next = graph.findVertex(cycle.get(i+1));
    		Edge edge = new Edge(pre, next);
    		Edge edge2 = pre.getEdge(edge);
    		if(edge2!=null){
    			weight = edge2.getWeight();
    			if(weight<smallWeight){
    				smallWeight = weight;
    			}
    		}else {
    			smallWeight =  0;
			}
    	}
    	return smallWeight;
	}
    
	private String convertToString(ArrayList<String> cycle) {
		// TODO Auto-generated method stub
    	String string = cycle.get(0);
    	for (int i=0;i<cycle.size();i++){
    		if(i==0){
    			string = string +",";
    		}else if(i==cycle.size()-1){
    			string += cycle.get(i);
    		}else {
    			string+=cycle.get(i);
        		string+=",";
			}
    	}
    	return string;
		
	}
	private int findCycleNumberFromInputString(String sToFind) {
//    	String a[] = s.split("#");
//    	String sToFind = a[1];
//    	System.out.println("sToFind: " + sToFind);
        int num = 0;
        String newString = this.oriString;
//        System.out.println("newString: "+newString);
        while (newString.contains(sToFind)) {
//        	System.out.println("indexOf: "+this.oriString.indexOf(sToFind));
        	newString = newString.substring(newString.indexOf(sToFind) + sToFind.length());
//        	System.out.println("newString: "+newString);
            num ++;
        }
        return num;
    }
	
	public void sortCycleList(){
		 Collections.sort(cycleList,new Comparator<Cycle>() {
				@Override
				public int compare(Cycle o1, Cycle o2) {
					return o2.getWeight()-o1.getWeight();
				}
	        });
	}
	
	public void printCycleList() {
		for(int i=0;i<cycleList.size();i++){
			System.out.print("No"+ cycleList.get(i).getSerialNumber() + "  ");
			System.out.print("cycle: "+ cycleList.get(i).getTrace() + "  ");
			System.out.println("weight: "+cycleList.get(i).getWeight());
		}
	}
	
	public void splitCycleList() {
		for(int i=0;i<cycleList.size();i++){
			int weight = cycleList.get(i).getWeight();
			while(weight>0){
				System.out.println("cycle: "+ cycleList.get(i).getTrace());
				weight--;
			}
		}
	}
	public String getStart() {
		return start;
	}
	public void setStart(String start) {
		this.start = start;
	}
	
	
	
	
      

//	private void countCycleNumber(ArrayList<String> cycle) {
//		// TODO Auto-generated method stub
//		
//		
//	}
//    
//    public boolean findCycle() {
//        for (String name : this.graph.getVerticesName()) {
////        	System.out.println("IfhasCycle: "+name);
//            if(hasCycle(name)){
//            	return true;
//            }
//        } 
//        return true;
//    }
    
//    private boolean dfs(String start) {
//        visited.add(start); 
////        System.out.print("visited list: " );
////        for(String s: visited){	
////        	System.out.print(s +" ");
////        }
////        System.out.println();
//        for (String neighbor : graph.findVertex(start).getNeighborsName()) {       	
////        	System.out.println("neighbor: " +neighbor);
//        	//已经访问过，并且。。。
//            if (visited.contains(neighbor) && !exited.contains(neighbor)){
//            	startVertex = neighbor;//起点，返回到的点
//            	endVertex = start;
////            	System.out.println("start: "+startVertex);
////            	System.out.println("end: "+endVertex);
//                return true;
//            }
//            if (!visited.contains(neighbor) && dfs(neighbor)){
////            	System.out.println("2"+neighbor);
//                return true;
//            }
//        }
//        exited.add(start);
//        System.out.println("exited: " +start);
//        return false;
//    }
}

//void findCycle(String name)   //递归DFS  
//{  
//  if(visited.contains(name))  
//  {  
//      int j;  
//      if((j=trace.indexOf(name))!=-1)  
//      {  
//          hasCycle=true;  
//          System.out.print("Cycle:");  
//          while(j<trace.size())  
//          {  
//              System.out.print(trace.get(j)+" ");  
//              j++;  
//          }  
//          System.out.print("\n");  
//          return;  
//      }  
//      return;  
//  }  
//  visited.add(name);  
//  trace.add(name);  
//    
//  for(int i=0;i<n;i++)  
//  {  
//      if(e[v][i]==1)  
//          findCycle(i);  
//  }  
//  trace.remove(trace.size()-1);  
//}  
