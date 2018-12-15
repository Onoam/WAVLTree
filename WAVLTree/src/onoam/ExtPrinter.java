package onoam;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ExtPrinter extends WAVLTree {
	public void print(WAVLNode node, int height) {
		// originally meant to get height from a method, i.e. height(root);
	       if(getRoot() == OUTER_NODE) {
	           System.out.println("(XXXXXX)");
	           return;
	       }

	       //int height = height(root); //PROBLEM??
	       int width = (int)Math.pow(2, height-1);

	       // Preparing variables for loop.
	       List<WAVLNode> current = new ArrayList<WAVLNode>(1),
	           next = new ArrayList<WAVLNode>(2);
	       current.add(getRoot());

	       final int maxHalfLength = 4;
	       int elements = 1;

	       StringBuilder sb = new StringBuilder(maxHalfLength*width);
	       for(int i = 0; i < maxHalfLength*width; i++)
	           sb.append(' ');
	       String textBuffer;

	       // Iterating through height levels.
	       for(int i = 0; i < height; i++) {

	           sb.setLength(maxHalfLength * ((int)Math.pow(2, height-1-i) - 1));

	           // Creating spacer space indicator.
	           textBuffer = sb.toString();

	           // Print tree node elements
	           for(WAVLNode n : current) {

	               System.out.print(textBuffer);

	               if(n == null) {

	                   System.out.print("        ");
	                   next.add(null);
	                   next.add(null);

	               } else {

	                   System.out.printf( "("+Integer.toString(n.getKey())+","+Integer.toString(n.getRank())+")	");
	                   next.add(n.getLeft());
	                   next.add(n.getRight());

	               }

	               System.out.print(textBuffer);

	           }

	           System.out.println();
	           // Print tree node extensions for next level.
	           if(i < height - 1) {

	               for(WAVLNode n : current) {

	                   System.out.print(textBuffer);

	                   if(n == null)
	                       System.out.print("        ");
	                   else
	                       System.out.printf("%s      %s",
	                               n.getLeft() == null ? " " : "/", n.getRight() == null ? " " : "\\");

	                   System.out.print(textBuffer);

	               }

	               System.out.println();

	           }

	           // Renewing indicators for next run.
	           elements *= 2;
	           current = next;
	           next = new ArrayList<WAVLNode>(elements);

	       }

	   }

	   public static void main(String args[]) {
	       ExtPrinter t = new ExtPrinter();
	       while (true) {
	           System.out.println("(1) Insert");
	           System.out.println("(2) Delete");
	           System.out.println("(3) Break");


	           try {
	               BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
	               String s = bufferRead.readLine();

	               if (Integer.parseInt(s) == 1) {
	                   System.out.print("Value to be inserted: ");
	                   int key =Integer.parseInt(bufferRead.readLine());
	                   t.insert(key, "AMEN");
	               }
	               else if (Integer.parseInt(s) == 2) {
	                   System.out.print("Value to be deleted: ");
	                   int key =Integer.parseInt(bufferRead.readLine());
	                   t.delete(key);
	               }
	               else if (Integer.parseInt(s) == 3) {
	            	   break;
	               }
	               else {
	                   System.out.println("Invalid choice, try again!");
	                   continue;
	               }
	               int height = 1; // change this
	               t.print(t.getRoot(), height);

	           }
	           catch(IOException e) {
	               e.printStackTrace();
	           }
	       }

	   }
	   

}
