package dendrologist;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Comparator;
import java.util.Scanner;
import java.util.function.Function;
import java.util.ArrayList;

/**
 * A testbed for an augmented implementation of an AVL tree
 * @author William Duncan, Lance Captain
 * @see AVLTreeAPI, AVLTree
 * <pre>
 * Date: 99-99-9999
 * Course: csc 3102 
 * Programming Project # 2
 * Instructor: Dr. Duncan 
 * </pre>
 */
public class Dendrologist
{
    public static void main(String[] args) throws FileNotFoundException, AVLTreeException 
    {
        String usage = "Dendrologist <order-code> <command-file>\n";
        usage += "  <order-code>:\n";
        usage += "  0 ordered by increasing string length, primary key, and reverse lexicographical order, secondary key\n";
        usage += "  -1 for reverse lexicographical order\n";
        usage += "  1 for lexicographical order\n";
        usage += "  -2 ordered by decreasing string length\n";
        usage += "  2 ordered by increasing string length\n";
        usage += "  -3 ordered by decreasing string length, primary key, and reverse lexicographical order, secondary key\n";
        usage += "  3 ordered by increasing string length, primary key, and lexicographical order, secondary key\n";      
        if (args.length != 2)
        {
            System.out.println(usage);
            throw new IllegalArgumentException("There should be 2 command line arguments.");
        }
        
        //complete the implementation of this method
        String fileName = args[1];
        //int orderCode = Integer.parseInt(args[0]);
        Comparator<String> comp = null;        
        switch(args[0]) {
        	case "0":
        		comp = (x,y) -> {
        			if(x.length() != y.length()) {
        				return x.length() - y.length();
        			}
        			return -x.compareTo(y);
        		};
        		break;
        	case "-1":
        		comp = (x,y) -> {
        			return -x.compareTo(y);
        		};
        		break;
        	case "1":
        		comp = (x,y) -> {
        			return x.compareTo(y);
        		};
        		break;
        	case "-2":
        		comp = (x,y) -> {
        			return y.length() - x.length();
        		};
        		break;
        	case "2":
        		comp = (x,y) -> {
        			return x.length() - y.length();
        		};
        		break;
        	case "-3":
        		comp = (x,y) -> {
        			if(x.length() != y.length()) {
        				return y.length() - x.length();
        			}
        			return -x.compareTo(y);
        		};
        		break;
        	case "3":
        		comp = (x,y) -> {
        			if(x.length() != y.length()) {
        				return x.length() - y.length();
        			}
        			return x.compareTo(y);
        		};
        		break;
        	default: throw new IllegalArgumentException("File Paring Error");
        }
        
        AVLTree<String> avl = new AVLTree(comp);
        Function<String, PrintStream> out = x -> System.out.printf("%s\n", x);
        
        try(Scanner scan = new Scanner(new FileReader(fileName))){
        	while(scan.hasNext()) {
        		String line = scan.nextLine();
        		String[] command = new String[2];
        		command = line.split(" ");
        		
        		switch(command[0]) {
        			case "props":
        				System.out.printf("size = %d, height = %d, diameter = %d\n", avl.size(), avl.height(), avl.diameter());
        				System.out.printf("fibonnaci? = <%b> complete? = <%b>\n", avl.isFibonacci(), avl.isComplete());
        				break;
        			case "insert":
        				if(command[1] == null) {
        					throw new AVLTreeException("Requires a Value");
        				}
        				avl.insert(command[1]);
        				System.out.println("Inserted: " + command[1]);
        				break;
        			case "delete":
        				if(command[1] == null) {
        					throw new AVLTreeException("Requires a Value");
        				}
        				
        				avl.remove(command[1]);
        				System.out.println("Deleted: " + command[1]);
        				break;
        			case "traverse":
        				System.out.println("Pre Order Traversel:");
        				avl.preorderTraverse(out);
        				
        				System.out.println("In Order Traversel:");
        				avl.traverse(out);
        				
        				System.out.println("Post Order Travesal:");
        				avl.postorderTraverse(out);
        				break;
        			case "gen":
        				if(command[1] == null) {
        					throw new AVLTreeException("Requires a Value");
        				}
        				else {
        					if(!avl.inTree(command[1])) {
        						System.out.println("Geneology: " + command[1] + " UNDEFINED");
        					}
        					else {
        						System.out.println("Genelogoy: " + command[1]);
        						String p = (avl.getParent(command[1]) != null ? avl.getParent(command[1]) : "NONE");
        						ArrayList<String> children = avl.getChildren(command[1]);
        						String leftChild = (children.get(0) != null ? children.get(0) : "NONE");
        						String rightChild = (children.get(1) != null ? children.get(1) : "NONE");
        						int ancestors = avl.ancestors(command[1]);
        						int desc = avl.descendants(command[1]);
        						System.out.printf("parent = %s, left-child = %s, right-child = %s ", p, leftChild, rightChild);
        						System.out.printf("#ancestors = %d, #descendents = %d\n", ancestors, desc);
        					}
        				}
        				
        				break;
        			default: throw new IllegalArgumentException("File <- Parsing Error");
        				
        		}
        	}
        }
        catch(IOException e) {
        	System.out.println("Error reading file " + e.getMessage());
        	System.exit(1);
        }

    }   
}

/*
try(Scanner scan = new Scanner(new FileReader(fileName))){
	while(scan.hasNext()) {
		String line = scan.nextLine();
		String[] command = new String[2];
		command = line.split(" ");
		
		switch(command[0]) {
			case "props":
				System.out.printf("size = %d, height = %d, diameter = %d \nfibonnaci? = <%b> complete? = <%b>\n", avl.size(), avl.height(), avl.diameter(), avl.isFibonacci(), avl.isComplete());
				break;
			case "insert":
				if(command[1] == null) {
					throw new AVLTreeException("Requires a Value");
				}
				avl.insert(command[1]);
				System.out.println("Inserted: " + command[1]);
				break;
			case "delete":
				if(command[1] == null) {
					throw new AVLTreeException("Requires a Value");
				}
				
				avl.remove(command[1]);
				System.out.println("Deleted: " + command[1]);
				break;
			case "traverse":
				System.out.println("Pre Order Traversel:");
				avl.preorderTraverse(out);
				
				System.out.println("In orderTraversel:");
				avl.traverse(out);
				
				System.out.println("Post Order Travesal:");
				avl.postorderTraverse(out);
				break;
			case "gen":
				if(command[1] == null) {
					throw new AVLTreeException("Requires a Value");
				}
				else {
					if(!avl.inTree(command[1])) {
						System.out.println("Geneology: " + command[1] + " UNDEFINED");
					}
					else {
						String p = (avl.getParent(command[1]) != null ? avl.getParent(command[1]) : "NONE");
						ArrayList<String> children = avl.getChildren(command[1]);
						String leftChild = (children.get(0) != null ? children.get(0) : "NONE");
						String rightChild = (children.get(1) != null ? children.get(1) : "NONE");
						int ancestors = avl.ancestors(command[1]);
						int desc = avl.descendants(command[1]);
						System.out.printf("parent = %s, left-child = %s, right-child = %s ", p, leftChild, rightChild);
						System.out.printf("#ancestors = %d, #descendents = %d\n", ancestors, desc);
					}
				}
				
				break;
			default: throw new IllegalArgumentException("File <- Parsing Error");
				
		}
	}
}
catch(IOException e) {
	System.out.println("Error reading file " + e.getMessage());
	System.exit(1);
}
*/