package dendrologist;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.ArrayList;

/**
 * Models an AVL tree.
 * @param <E> data type of elements of the tree
 * @author William Duncan, Lance Captain
 * @see AVLTreeAPI
 * <pre>
 * Date: 99-99-9999
 * Course: CSC 3102 
 * Programming Project # 2
 * Instructor: Dr. Duncan 
 * </pre>
 *
 * DO NOT REMOVE THIS NOTICE (GNU GPL V2):
 * Contact Information: duncanw@lsu.edu
 * Copyright (c) 2022 William E. Duncan
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>
 * </pre>
 */
public class AVLTree<E extends Comparable<E>> implements AVLTreeAPI<E>
{    
    /**
     * The root node of this tree
     */
   private Node root;
   /**
    * The number of nodes in this tree
    */
   private int count;    
   /**
    * A comparator lambda function that compares two elements of this
    * AVL tree; cmp.compare(x,y) gives 1. negative when x less than y
    * 2. positive when x greater than y 3. 0 when x equal y
    */   
   private Comparator<? super E> cmp;   
      
   /**
    * A node of a tree stores a data item and references
    * to the child nodes to the left and to the right.
    */      
   private class Node
   {
       /**
        * the data in this node
        */
       public E data;
       /**
        * the left child
        */
       public Node left;
       /**
        * the right child
        */
       public Node right;
       /**
        * the balanced factor of this node
        */
       BalancedFactor bal;
   }
   /**
      Constructs an empty tree
   */
   public AVLTree()
   {
      root = null;
      count = 0;
      cmp = (x,y) -> x.compareTo(y);
   }
   /**
    * A parameterized constructor that uses an externally defined comparator    
    * @param fn - a trichotomous integer value comparator function   
    */
   public AVLTree(Comparator<? super E> fn)
   {
       root = null;
       count = 0;
       cmp = fn;
   }


   @Override
   public boolean isEmpty()
   {
      return (root == null);
   }

   @Override
   public void insert(E obj)
   {
      Node newNode = new Node();
      newNode.bal = BalancedFactor.EH;
      newNode.data = obj;
      AtomicBoolean forTaller = new AtomicBoolean();
      if (!inTree(obj))
         count++;
      root = insert(root,newNode, forTaller);

   }

   @Override
   public boolean inTree(E item)
   {
      Node tmp;
      if (isEmpty())
         return false;
      /*find where it is */
      tmp = root;
      while (true)
      {
         int d = cmp.compare(tmp.data,item);
         if (d == 0)
            return true;
         else if (d > 0)
         {
            if (tmp.left == null)
               return false;
            else
            /* continue searching */
               tmp = tmp.left;
         }
         else
         {
            if (tmp.right ==  null)
               return false;
            else
            /* continue searching for insertion pt. */
               tmp = tmp.right;
         }
      }
   }

   @Override
   public void remove(E item)
   {
      AtomicBoolean shorter = new AtomicBoolean();
      AtomicBoolean success = new AtomicBoolean();
      Node newRoot;
      if (!inTree(item))
         return;
      newRoot = remove(root,item, shorter, success);
      if (success.get())
      {
         root = newRoot;
         count--;
      }
   }      

   @Override
   public E retrieve(E item) throws AVLTreeException
   {
      Node tmp;
      if (isEmpty())
         throw new AVLTreeException("AVL Tree Exception: tree empty on call to retrieve()");
      /*find where it is */
      tmp = root;
      while (true)
      {
         int d = cmp.compare(tmp.data,item);
         if (d == 0)
            return tmp.data;
         else if (d > 0)
         {
            if (tmp.left == null)
               throw new AVLTreeException("AVL Tree Exception: key not in tree call to retrieve()");
            else
            /* continue searching */
               tmp = tmp.left;
         }
         else
         {
            if (tmp.right ==  null)
               throw new AVLTreeException("AVL Tree Exception: key not in tree call to retrieve()");
            else
            /* continue searching for insertion pt. */
               tmp = tmp.right;
         }
      }
   }

   public void traverse(Function func)
   {
      traverse(root,func);
   }

   @Override
   public int size()
   {
      return count;
   }
   
/*===> BEGIN: Augmented public methods <===*/   
   @Override
   public void preorderTraverse(Function func)
   {
      //Implement this method
	  preorderTraverse(root, func);
   }
   @Override
   public void postorderTraverse(Function func)
   {
      //Implement this
	  postorderTraverse(root, func);
   }
   @Override
   public ArrayList<E> getChildren(E entry) throws AVLTreeException
   {
	  if (isEmpty())
		  throw new AVLTreeException("AVL Tree Exception: tree empty on call to retrieve()");
	   
	  ArrayList<E> children = new ArrayList<E>(2);
	  Node tmp = root;
	  Node p = null;
	  if(inTree(entry)) {
		  while (tmp != null)
	      {
	         int d = cmp.compare(entry, tmp.data);
	         if (d < 0)
	         {
	        	 p = tmp;
	        	 tmp = tmp.left;
	         }
	         else if (d > 0)
	         {
	        	 p = tmp;
	        	 tmp = tmp.right;
	         }
	         else
	         {
	        	 if(tmp.left != null) {
	        		 children.add(p.left.data);
	        	 }
	        	 else { 
	        		 children.add(null);
	        	 }
	        	 if(tmp.right != null) {
	        		 children.add(p.right.data);
	        	 }
	        	 else {
	        		 children.add(null);
	        	 }
	        	 return children;
	         }
	      }
	  }
	  throw new AVLTreeException("Entry not found");
   }
   
   public E getParent(E entry) throws AVLTreeException
   {
      //Implement this method
	  if(isEmpty()) {
		  throw new AVLTreeException("AVL Tree Exception: tree empty on call to retrieve()");
	  }
	  
	  Node tmp = root;
	  Node p = null;
	  
	  if(inTree(entry)) {
		  while (tmp != null)
	      {
			 int d = cmp.compare(entry, tmp.data);
	         if (d < 0)
	         {
	        	 p = tmp;
	        	 tmp = tmp.left;
	         }
	         else if (d > 0)
	         {
	        	 p = tmp;
	        	 tmp = tmp.right;
	         }
	         else
	         {
	        	 if(p != null) {
	        		 return p.data;
	        	 } else throw new AVLTreeException("Node not in tree");
	         }
	      }
	  }		
      throw new AVLTreeException("Entry not in tree");
   }
   
   public int ancestors(E entry) throws AVLTreeException
   {
      //Implement this method
	   if (isEmpty())
		   throw new AVLTreeException("AVL Tree Exception: tree empty on call to retrieve()");
	  
	  int ancestor = 0;
	  
	  
	  Node tmp = root;
	  
	  while (tmp != null)
      {
		 int d = cmp.compare(entry, tmp.data);
         if (d < 0)
         {
        	 tmp = tmp.left;
        	 ancestor++;
         }
         else if (d > 0)
         {
        	 tmp = tmp.right;
        	 ancestor++;
         }
         else return ancestor;
      }
	  
      return ancestor;
   }

   public int descendants(E entry) throws AVLTreeException
   {
      //Implement this method
	   
	   
	   Node tmp = root;
	   if (isEmpty())
		   throw new AVLTreeException("AVL Tree Exception: tree empty on call to retrieve()");
	   
		  while (tmp != null)
	      {
			 int d = cmp.compare(entry, tmp.data);
	         if (d < 0)
	         {
	        	 tmp = tmp.left;
	         }
	         else if(d > 0)
	         {
	        	 tmp = tmp.right;
	         }
	         else {
	        	 return countDesc(tmp.left) + countDesc(tmp.right);
	         }
	      }
		 throw new AVLTreeException("Entry not in the tree");
   }
   
   @Override
   public int height()
   {
	   return height(root);
   }

   @Override
   public int diameter()
   {
	   if (root == null) {
		   return 0;
	   }
	   else {
		   return 3 + height(root.left) + height(root.right);
	   }
	   
   }
   
   @Override
   public boolean isFibonacci()
   {
      //implement this method
	  
	   if(root == null) {
		   return true;
	   }
	  int fib = fibonacci(height(root) + 3) - 1;
	  
	  Queue<Node> q = new LinkedList<>();
	  q.add(root);
	  int l = -1;
	  while(!q.isEmpty()) {
		  Node tmp = q.remove();
		  int c = height(tmp);
		  if(l != 1 && c != l + 1) {
			  return false;
		  }
		  l = c;
		  if(tmp.left != null) {
			  q.offer(tmp.left);
		  }
		  if(tmp.right != null) {
			  q.offer(tmp.right);
		  }
	  }
      return true;
   }      
   
   @Override
   public boolean isComplete()
   {
       if (root == null) return true;
       return isComplete(root,0);
   }
  
/*===> END: Augmented public methods <===*/      

   /**
    * A enumerated type for the balanced factor of a node
    */
   private enum BalancedFactor
   {
      LH(-1),EH(0),RH(1);
      BalancedFactor(int aValue)
      {
         value = aValue;
      }
      private int value;
   }

/* private methods definitions */           
   
   /**
    * An auxiliary method that inserts a new node in the tree or
    * updates a node if the data is already in the tree.
    * @param curRoot a root of a subtree
    * @param newNode the new node to be inserted
    * @param taller indicates whether the subtree becomes
    * taller after the insertion
    * @return a reference to the new node
    */
   private Node insert(Node curRoot, Node newNode, AtomicBoolean taller)
   {
      if (curRoot == null)
      {
         curRoot = newNode;
         taller.set(true);
         return curRoot;
      }
      int d = cmp.compare(newNode.data,curRoot.data);
      if (d < 0)
      {
         curRoot.left = insert(curRoot.left, newNode, taller);
         if (taller.get())
            switch(curRoot.bal)
            {
               case LH: // was left-high -- rotate
                  curRoot = leftBalance(curRoot,taller);
                  break;
               case EH: //was balanced -- now LH
                  curRoot.bal = BalancedFactor.LH;
                  break;
               case RH: //was right-high -- now EH
                  curRoot.bal = BalancedFactor.EH;
                  taller.set(false);
                  break;
            }
         return curRoot;
      }
      else if (d > 0)
      {
         curRoot.right = insert(curRoot.right,newNode,taller);
         if (taller.get())
            switch(curRoot.bal)
            {
               case LH: // was left-high -- now EH
                  curRoot.bal = BalancedFactor.EH;
                  taller.set(false);
                  break;
              case EH: // was balance -- now RH
                 curRoot.bal = BalancedFactor.RH;
                 break;
              case RH: //was right high -- rotate
                 curRoot = rightBalance(curRoot,taller);
                 break;
            }
         return curRoot;
      }
      else
      {
         curRoot.data = newNode.data;
         taller.set(false);
         return curRoot;
      }
   }

   /**
    * An auxiliary method that left-balances the specified node
    * @param curRoot the node to be left-balanced
    * @param taller indicates whether the tree becomes taller
    * @return the root of the subtree after left-balancing
    */
   private Node leftBalance(Node curRoot, AtomicBoolean taller)
   {
      Node rightTree;
      Node leftTree;
      leftTree = curRoot.left;
      switch(leftTree.bal)
      {
         case LH: //left-high -- rotate right
            curRoot.bal = BalancedFactor.EH;
            leftTree.bal = BalancedFactor.EH;
            // Rotate right
            curRoot = rotateRight(curRoot);
            taller.set(false);
            break;
         case EH: // This is an error
            System.out.println("AVL Tree Error: error in balance tree in call to leftBalance()");
            System.exit(1);
            break;
         case RH: // right-high - requires double rotation: first left, then right
            rightTree = leftTree.right;
            switch(rightTree.bal)
            {
               case LH: 
                  curRoot.bal = BalancedFactor.RH;
                  leftTree.bal = BalancedFactor.EH;
                  break;
               case EH:
                  curRoot.bal = BalancedFactor.EH;
                  leftTree.bal = BalancedFactor.EH;   /* LH */ 
                  break;
               case RH:
                  curRoot.bal = BalancedFactor.EH;
                  leftTree.bal = BalancedFactor.LH;
                  break;
            }
            rightTree.bal = BalancedFactor.EH;
            // rotate left
            curRoot.left = rotateLeft(leftTree);
            //rotate right
            curRoot = rotateRight(curRoot);
            taller.set(false);
      }
      return curRoot;
   }

   /**
    * An auxiliary method that right-balances the specified node
    * @param curRoot the node to be right-balanced
    * @param taller indicates whether the tree becomes taller
    * @return the root of the subtree after right-balancing
    */   
   private Node rightBalance(Node curRoot, AtomicBoolean taller)
   {
      Node rightTree;
      Node leftTree;
      rightTree = curRoot.right;
      switch(rightTree.bal)
      {
         case RH: //right-high -- rotate left
            curRoot.bal = BalancedFactor.EH;
            rightTree.bal = BalancedFactor.EH;
            // Rotate left
            curRoot = rotateLeft(curRoot);
            taller.set(false);
            break;
         case EH: // This is an error
            System.out.println("AVL Tree Error: error in balance tree in call to rightBalance()");
            break;
         case LH: // left-high - requires double rotation: first right, then left
            leftTree = rightTree.left;
            switch(leftTree.bal)
            {
               case RH: 
                  curRoot.bal = BalancedFactor.LH;
                  rightTree.bal = BalancedFactor.EH;
                  break;
               case EH:
                  curRoot.bal = BalancedFactor.EH;
                  rightTree.bal = BalancedFactor.EH;    /* RH */
                  break;
               case LH:
                  curRoot.bal = BalancedFactor.EH;
                  rightTree.bal = BalancedFactor.RH;
                  break;
            }
            leftTree.bal = BalancedFactor.EH;
            // rotate right
            curRoot.right = rotateRight(rightTree);
            //rotate left
            curRoot = rotateLeft(curRoot);
            taller.set(false);
      }
      return curRoot;
   }

   /**
    * An auxiliary method that Left-rotates the subtree at this node
    * @param node the node at which the left-rotation occurs.
    * @return the new node of the subtree after the left-rotation
    */
   private Node rotateLeft(Node node)
   {
      Node tmp;
      tmp = node.right;
      node.right = tmp.left;
      tmp.left = node;
      return tmp;
   }

   /**
    * An auxiliary method that right-rotates the subtree at this node
    * @param node the node at which the right-rotation occurs.
    * @return the new node of the subtree after the right-rotation
    */
   private Node rotateRight(Node node)
   {
      Node tmp;
      tmp = node.left;
      node.left = tmp.right;
      tmp.right = node;
      return tmp;
   }

   /**
    * An auxiliary method that in-order traverses the subtree at the specified node
    * @param node the root of a subtree
    * @param func the function to be applied to the data in each node
    */
   private void traverse(Node node, Function func)
   {
      if (node != null)
      {
         traverse(node.left,func);
         func.apply(node.data);
         traverse(node.right,func);
      }
   }

   /**
    * An auxiliary method that deletes the specified node from this tree
    * @param node the node to be deleted
    * @param key the item stored in this node
    * @param shorter indicates whether the subtree becomes shorter
    * @param success indicates whether the node was successfully deleted
    * @return a reference to the deleted node
    */
   private Node remove(Node node, E key, AtomicBoolean shorter, AtomicBoolean success)
   {
      Node delPtr;
      Node exchPtr;
      Node newRoot;
      if (node == null)
      {
         shorter.set(false);
         success.set(false);
         return null;
      }
      int d = cmp.compare(key,node.data);
      if (d < 0)
      {
         node.left = remove(node.left,key,shorter,success);
         if (shorter.get())
            node = deleteRightBalance(node,shorter);
      }
      else if (d > 0)
      {
         node.right = remove(node.right,key,shorter,success);
         if (shorter.get())
            node = deleteLeftBalance(node,shorter);
      }
      else
      {
         delPtr = node;
         if (node.right == null)
         {
            newRoot = node.left;
            success.set(true);
            shorter.set(true);
            return newRoot;
         }
         else if(node.left == null)
         {
            newRoot = node.right;
            success.set(true);
            shorter.set(true);
            return newRoot;
         }
         else
         {
            exchPtr = node.left;
            while(exchPtr.right != null)
               exchPtr = exchPtr.right;
            node.data = exchPtr.data;
            node.left = remove(node.left,exchPtr.data,shorter,success);
            if (shorter.get())
               node = deleteRightBalance(node,shorter);
         }
      }
      return node;
   }
   
   /**
    * An auxiliary method that right-balances this subtree after a deletion
    * @param node the node to be right-balanced
    * @param shorter indicates whether the subtree becomes shorter
    * @return a reference to the root of the subtree after right-balancing.
    */
   private Node deleteRightBalance(Node node, AtomicBoolean shorter)
   {
      Node rightTree;
      Node leftTree;
      switch(node.bal)
      {
         case LH: //deleted left -- now balanced
            node.bal = BalancedFactor.EH;
            break;
         case EH: //now right high
            node.bal = BalancedFactor.RH;
            shorter.set(false);
            break;
         case RH: // right high -- rotate left
            rightTree = node.right;
            if (rightTree.bal == BalancedFactor.LH)
            {
               leftTree = rightTree.left;
               switch(leftTree.bal)
               {
                  case LH: 
                     rightTree.bal = BalancedFactor.RH;
                     node.bal = BalancedFactor.EH;
                     break;
                  case EH: 
                     node.bal = BalancedFactor.EH;
                     rightTree.bal = BalancedFactor.EH;
                     break;
                  case RH: 
                     node.bal = BalancedFactor.LH;
                     rightTree.bal = BalancedFactor.EH;
                     break;
               }  
               leftTree.bal = BalancedFactor.EH;
               //rotate right, then left
               node.right = rotateRight(rightTree);
               node = rotateLeft(node);
            }
            else
            {
               switch(rightTree.bal)
               {
                  case LH:
                  case RH:
                     node.bal = BalancedFactor.EH;
                     rightTree.bal = BalancedFactor.EH;
                     break;
                  case EH:
                     node.bal = BalancedFactor.RH;
                     rightTree.bal = BalancedFactor.LH;
                     shorter.set(false);
                     break;
               }
               node = rotateLeft(node);
            }
         }
      return node;
   }

   /**
    * An auxiliary method that left-balances this subtree after a deletion
    * @param node the node to be left-balanced
    * @param shorter indicates whether the subtree becomes shorter
    * @return a reference to the root of the subtree after left-balancing.
    */   
   private Node deleteLeftBalance(Node node, AtomicBoolean shorter)
   {
      Node rightTree;
      Node leftTree;
      switch(node.bal)
      {
         case RH: //deleted right -- now balanced
            node.bal = BalancedFactor.EH;
            break;
         case EH: //now left high
            node.bal = BalancedFactor.LH;
            shorter.set(false);
            break;
         case LH: // left high -- rotate right
            leftTree = node.left;
            if (leftTree.bal == BalancedFactor.RH)
            {
               rightTree = leftTree.right;
               switch(rightTree.bal)
               {
                  case RH: 
                     leftTree.bal = BalancedFactor.LH;
                     node.bal = BalancedFactor.EH;
                     break;
                  case EH: 
                     node.bal = BalancedFactor.EH;
                     leftTree.bal = BalancedFactor.EH;
                     break;
                  case LH: 
                     node.bal = BalancedFactor.RH;
                     leftTree.bal = BalancedFactor.EH;
                     break;
               }  
               rightTree.bal = BalancedFactor.EH;
               //rotate left, then right
               node.left = rotateLeft(leftTree);
               node = rotateRight(node);
            }
            else
            {
               switch(leftTree.bal)
               {
                  case RH:
                  case LH:
                     node.bal = BalancedFactor.EH;
                     leftTree.bal = BalancedFactor.EH;
                     break;
                  case EH:
                     node.bal = BalancedFactor.LH;
                     leftTree.bal = BalancedFactor.RH;
                     shorter.set(false);
                     break;
               }
               node = rotateRight(node);
            }
         }
      return node;
   }
            
/* BEGIN: Augmented Private Auxiliary Methods */

    /**
     * Traverses this subtree preorder and apply this specified
     * function to the entry in each node
     * @param node the root of the subtree
     * @param func the function to apply to the entry in each node
     */
    private void preorderTraverse(Node node, Function func)
    {
       //Implement this method
    	if (node != null)
        {
    		func.apply(node.data);
    		preorderTraverse(node.left,func);
    		preorderTraverse(node.right,func);
        }
    }

    /**
     * Traverses this subtree postorder and apply this specified
     * function to the entry in each node
     * @param node the root of the subtree
     * @param func the function to apply to the entry in each node
     */
    private void postorderTraverse(Node node, Function func)
    {
       //Implement this method
    	if (node != null)
        {
    		postorderTraverse(node.left,func);
    		postorderTraverse(node.right,func);
    		func.apply(node.data);
        }
    }

    /**
     * Recursively counts the number of descendants that the specified node has.
     * @param node the root of a subtree
     * @return the number of descendants of the specified node
     */
    private int countDesc(Node node)
    {
       if (node == null) {
    	   return 0;
       }
       
       if(node.left != null) {
    	   if(node.right != null) {
    		   return 2 + countDesc(node.left) + countDesc(node.right);
    	   } 
    	   else {
    		   return 1 + countDesc(node.left);
    	   }
       } 
       else if(node.right != null) {
    	   return 1 + countDesc(node.right);
       } 
       else {
    	   return 0;
       }
    }

    /**
     * Determines the height of the subtree rooted at the specified node
     * @param node a root of the subtree
     * @return the height of the tree rooted at the specified node
     */
    private int height(Node node)
    {
    	Node tmp = node;
    	int height = -1;
       if (isEmpty())
       {
    	   return -1;
       }
       else
       {
    	   while (tmp != null) {
    		   height++;
    		   if(tmp.bal == BalancedFactor.LH)
    		   {
    			   tmp = tmp.left;
    		   }
    		   else
    		   {
    			   tmp = tmp.right;
    		   }
    	   }
       }
       return height;
    }
   
    /**
     * An auxiliary function that iteratively computes the
     * nth Fibonacci number
     * @param n the term of the Fibonacci sequence to compute
     * @return the nth Fibonacci number or -1 if n < 1
     */
    private int fibonacci(int n)
    {
       //Implement this method
    	int fib = 0;
    	int a = 1;
    	if(n < 1) {
    		return -1;
    	}
    	else if(n == 1) {
    		return 1;
    	}
    	else {
    		for(int i = 0; i < n; i++) {
    			int temp = fib;
    			fib = fib + a;
    			a = temp;
    		}
    		return fib;
    	}
    }     
    
    /**
     * Determines whether the tree rooted at the specified node is complete 
     * @param node the root of a subtree
     * @param index the level-order index of the specified Node
     * @return true if the subtree rooted at the specified node is complete; 
     * otherwise, false
     */
    private boolean isComplete(Node node, int index)
    {
       //Implement this method
    	
    	if (isEmpty()) {
    		return true;
    	}
    		
    	if (index >= size()){
    		return false;
    	}
    	
    	if(node.left != null) {
    		if (node.right != null) {
    			return isComplete(node.left, 2 * index + 1) && isComplete(node.right, 2*index + 2);
    		} else return isComplete(node.left, 2*index + 1);
    	} else if (node.right != null) return false;
    	else return true;
    	
    }
/* END: Augmented Private Auxiliary Methods */      
}


