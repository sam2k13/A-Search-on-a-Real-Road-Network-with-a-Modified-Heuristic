//CSCI 4511 - Final Project
//Sam Kleist
//4733752
//kleis043@umn.edu

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.*;
import java.io.File;
import java.awt.Graphics;
import javax.swing.JFrame;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.Scanner;
import java.text.DecimalFormat;
import java.math.RoundingMode;

class Coordinate{
	
		public int x;
		public int y;
		
}

class Node{
	
	public Node parent;
	public Coordinate coordinate;
	public int cost;
	public int roadCost;
	
}

class Road{

	public int roadType;
	public Coordinate start;
	public Coordinate end;	
}

class Map{
	
	public Road [] roads;
	
	public String directionsHTML = "<svg height='2000' width='7000'>\n\t";
	
	public Map(){
		roads = new Road[1357];	
		try{			
			Scanner scanner = new Scanner(new File("map.txt"));
			for(int i = 0; i < 1357; i++){	
				
				roads[i] = new Road();
				roads[i].start =  new Coordinate();
				roads[i].end =  new Coordinate();
				roads[i].roadType = scanner.nextInt();			
				roads[i].start.x = scanner.nextInt();
				roads[i].start.y = scanner.nextInt();
				roads[i].end.x = scanner.nextInt();
				roads[i].end.y = scanner.nextInt();
				
				directionsHTML += "<line x1='" + roads[i].start.x  + "' y1='" + (roads[i].start.y - 5000)+ "' x2='" + roads[i].end.x + "' y2='" + (roads[i].end.y - 5000) + "' style='stroke:rgb(0,0,0);stroke-width:2' />\n\t";
				//System.out.println(roads[i].start.x);
				
			}
			
		}
		catch(Exception e){
			System.out.println(e);
		}
	}
	
	public int calculateDistance(Coordinate x, Coordinate y){
		return (int)Math.sqrt(Math.pow((double)(x.x-y.x),2f) + Math.pow((double)(x.y - y.y),2f));	
	}
		
}

class ASearch{

	public int leftTurnCost = 0, rightTurnCost = 0;
	
	public Map map;
	
	public ASearch(){
		map = new Map();
			
	}
	
	List<Coordinate> getNeighbors(Node node){
		List<Coordinate> neighbors = new ArrayList<Coordinate>();
		for(int i = 0; i < map.roads.length; i++){
			if(map.roads[i].start.x == node.coordinate.x && map.roads[i].start.y == node.coordinate.y){
				//System.out.println("Found one at start");
				neighbors.add(map.roads[i].end);			
			}
			if(map.roads[i].end.x == node.coordinate.x &&  map.roads[i].end.y == node.coordinate.y && map.roads[i].roadType == 2){
				//System.out.println("Found one at end" + map.roads[i].start.x);
				neighbors.add(map.roads[i].start);			
			}
		}
		return neighbors;	
	}
	
	void exploreNode(){}
	
	boolean isExplored(){
		
		return true;
	}
	
	void addToFringe(){}
	
	public void run(int xStart, int yStart, int xEnd, int yEnd){
		Node start,end,current;
	
		Set<Node> explored = new HashSet<Node>();
		Set<Node> fringe = new HashSet<Node>();
		
		start = getNearestNode(xStart,yStart);
		end = getNearestNode(xEnd,yEnd);
		start.cost = map.calculateDistance(start.coordinate, end.coordinate);
		System.out.println("\nNearest Starting Intersestion: (" + start.coordinate.x +", " + start.coordinate.y + ")");
		System.out.println("Nearest Ending Intersestion: (" + end.coordinate.x +", " + end.coordinate.y + ")");
		
		//Let C = Start initially
		current = start;

		explored.add(current);
		int z = 0;
		while(current.coordinate.x != end.coordinate.x || current.coordinate.y != end.coordinate.y) {
			z++;
			//Add neighbors to Fringe
			List<Coordinate> currentNeighbors;
			currentNeighbors = getNeighbors(current);
			ListIterator<Coordinate> litr = null;
			litr = currentNeighbors.listIterator();
			while(litr.hasNext()){
				
				Coordinate neighborCoordinate = litr.next();
				//System.out.println("Neighbor at (" + neighborCoordinate.x + "," + neighborCoordinate.y + ")");
				Node newNode = new Node();
				newNode.coordinate = neighborCoordinate;
				newNode.parent = current;
				newNode.roadCost = current.roadCost + map.calculateDistance(neighborCoordinate, current.coordinate);

				if(current.parent != null && current != null){
					int angle = angleBetween(current.coordinate,newNode.coordinate,current.parent.coordinate);
					if(angle > 0 && angle < 135){
						newNode.roadCost += leftTurnCost;	
					}
					else if(angle > 225 && angle < 360){
						newNode.roadCost += rightTurnCost;
					}
				}
				//newNode.roadCost += 
				newNode.cost = newNode.roadCost + map.calculateDistance(neighborCoordinate, end.coordinate);
				
				Iterator<Node> fringeIterator = fringe.iterator();
				Iterator<Node> exploredIterator = explored.iterator();
				boolean addToFringe = true;
				boolean inFringe = false;
				//System.out.println("Searching Fringe and Explored for: (" + newNode.coordinate.x + ", " + newNode.coordinate.y + ")");
				while(fringeIterator.hasNext()){
					Node fringeNode = fringeIterator.next();
					//System.out.println("Comparing(" + fringeNode.coordinate.x + ", " + fringeNode.coordinate.y + ") and (" + newNode.coordinate.x + ", " + newNode.coordinate.y + ") in fringe");
					if(fringeNode.coordinate.x == newNode.coordinate.x && fringeNode.coordinate.y == newNode.coordinate.y){
						//System.out.println("Found Neighbor (" + newNode.coordinate.x + ", " + newNode.coordinate.y + ") in fringe");
						if(fringeNode.cost > newNode.cost){
							fringeNode.cost = newNode.cost;
							fringeNode.parent = newNode.parent;
							inFringe = true;
							addToFringe = false;
							//break;
						}
						else{
							inFringe = true;
							addToFringe = false;
							//break;
						}	
					}					
				}
				if(!inFringe){
					while(exploredIterator.hasNext()){
						Node exploredNode = exploredIterator.next();
						//System.out.println("Comparing(" + exploredNode.coordinate.x + ", " + exploredNode.coordinate.y + ") and (" + newNode.coordinate.x + ", " + newNode.coordinate.y + ") in explored");
						if(exploredNode.coordinate.x == newNode.coordinate.x && exploredNode.coordinate.y == newNode.coordinate.y){
							//System.out.println("Found Neighbor (" + newNode.coordinate.x + ", " + newNode.coordinate.y + ") in explored");
							if(exploredNode.cost > newNode.cost){
								//break;
							}
							else{
								addToFringe = false;
								//break;
							}
						}
					}					
				}				
				if(addToFringe){
					fringe.add(newNode);
				}
			}

			int minCost = 10000000;
			Node nextNode = null;
			if(fringe.isEmpty()){
				//System.out.println("empty fringe...falure");
				break;	
			}
			for(Node node : fringe){
				//System.out.println("Fringe x: " + node.coordinate.x + " Fringe y: " + node.coordinate.y + " Cost: " + node.cost);
				if(node.cost < minCost){
					minCost = node.cost;
					nextNode = node;
				}
			}
			for(Node node : explored){
				//System.out.println("Explored x: " + node.coordinate.x + " Explored y: " + node.coordinate.y + " Cost: " + node.cost);
			}
			//Remove new current from fringe, add old c to explored
			current = nextNode;
			fringe.remove(current);
			explored.add(current);
			//System.out.println("Current x: " + current.coordinate.x + " Current y: " + current.coordinate.y);
			System.out.print("\rIteration: " + z);
		}
		if(current.coordinate.x == end.coordinate.x && current.coordinate.y == end.coordinate.y){
				//System.out.println("Success");
				
				System.out.println("\n\nTake this path:");
				Node printNode = current;
				List<Node> directions = new ArrayList<Node>();
				directions.add(printNode);
				while(printNode.parent != null){
					for(Node node : explored){
						if(node == printNode.parent){
							directions.add(0,node);
							printNode = node;
							explored.remove(node);
							break;
						}
					}
				}
				
				int rightTurns = 0, leftTurns = 0;
				for(int i = 1; i < directions.size() - 1; i++){
					int angle = angleBetween(directions.get(i).coordinate,directions.get(i + 1).coordinate, directions.get(i - 1).coordinate);
					map.directionsHTML += "<line x1='" + directions.get(i - 1).coordinate.x  + "' y1='" + (directions.get(i - 1).coordinate.y - 5000)+ "' x2='" + directions.get(i).coordinate.x + "' y2='" + (directions.get(i).coordinate.y -5000) + "' style='stroke:rgb(255,0,0);stroke-width:4' />\n\t";
					//System.out.println(angle);
					if(angle > 0 && angle < 135){
						leftTurns += 1;	
					}
					else if(angle > 225 && angle < 360){
						rightTurns += 1;
					}
				}
				map.directionsHTML += "</svg>";
				writeToFile(map.directionsHTML,"map.html");
				ListIterator<Node> listIterator = directions.listIterator();
				while(listIterator.hasNext()){
					Node currentPrintNode = listIterator.next();
					System.out.println("(" + currentPrintNode.coordinate.x + ", " + currentPrintNode.coordinate.y + ")");
				}
				DecimalFormat df = new DecimalFormat("#.##");
				df.setRoundingMode(RoundingMode.CEILING);
				System.out.println("\nDistance: " + df.format((double)(current.cost - rightTurnCost * rightTurns - leftTurnCost * leftTurns)/1600) + " miles");
				System.out.println("\nTotal Turns: " + Integer.toString(rightTurns + leftTurns) + "  Right Turns: " +  rightTurns + "  Left Turns: " + leftTurns);
				
		}
		else{
			System.out.println("\n\nOops, unable to find directions with current road information");
			
		}
		
		


		
	}
	void writeToFile(String text, String fileName){
        BufferedWriter output = null;
        try {
            File file = new File(fileName);
            output = new BufferedWriter(new FileWriter(file));
            output.write(text);
			if ( output != null ) {
				output.close();
			}
			
        } catch ( IOException e ) {
            e.printStackTrace();
        } 
		
	}
	
	
	private int angleBetween(Coordinate center, Coordinate current, Coordinate previous) {
		return ((int)Math.toDegrees(Math.atan2(current.x - center.x,current.y - center.y)-
                        Math.atan2(previous.x- center.x,previous.y- center.y)) + 360) % 360;
	}
	
	
	Node getNearestNode(int x, int y){
		Coordinate node = new Coordinate();
		node.x = x;
		node.y = y;
		int bestDistance = 100000000;
		Coordinate best = new Coordinate();
		for(int i = 0; i < map.roads.length; i++){
			int xDistance = map.calculateDistance(node, map.roads[i].start);
			int yDistance = map.calculateDistance(node, map.roads[i].end);
			if(xDistance < bestDistance){
				best = map.roads[i].start;
				bestDistance = xDistance;
			}
			if(yDistance < bestDistance){
				best = map.roads[i].end;
				bestDistance = yDistance;
			}		
		}
		Node bestNode = new Node();
		bestNode.coordinate = best;
		bestNode.parent = null;
		bestNode.cost = 0;
		return bestNode;
	}
	
	void Choice(){
		
		
	}
	
	
	
	
}




class Project{
	
	public static void main(String [] args)  
	{	
		//Map map = new Map(); 
		//System.out.println(map.calculateDistance(map.roads[0].start,map.roads[0].end));
		Scanner in = new Scanner(System.in);
		System.out.print("Enter Starting X Coordinate: ");
		int xStart = in.nextInt();
		System.out.print("Enter Starting Y Coordinate: ");
		int yStart = in.nextInt();
		System.out.print("Enter Ending X Coordinate: ");
		int xEnd = in.nextInt();
		System.out.print("Enter Ending Y Coordinate: ");
		int yEnd = in.nextInt();
		System.out.print("Enter Cost of Left Turn: ");
		int leftCost = in.nextInt();
		System.out.print("Enter Cost of Right Turn: ");
		int rightCost = in.nextInt();
		ASearch search = new ASearch();
		search.leftTurnCost = leftCost;
		search.rightTurnCost = rightCost;
		search.run(xStart,yStart,xEnd,yEnd);
		
	}
	
}

