package dataStructures;
import java.util.Arrays;
import java.util.Random;

@SuppressWarnings({"Convert2Lambda", "Anonymous2MethodRef", "RedundantTypeArguments"})
public class ExTester
{ // TODO remove this comment
	//ActualWAVLTree fakeTree;
    ActualWAVLTree actualTree;
    WAVLTree wavlTree;

    int actualOperations;
    int wavlOperations;

    int[] valuesTemp;
    int[] values;
    int[] values3;
    int[] values4;


    public ExTester() {

        //fakeTree = null;
        actualTree = null;
        wavlTree = null;

        // create array of values between 800-1800
        // like this - 800, 801, 802, 803, 804
        valuesTemp = new int[1000];
        for (int j=0; j<valuesTemp.length; j++) {
            valuesTemp[j] = 800 + j;
        }

        // mix the values - create a new list of values taken
        // one from the start one from the end, alternately
        // i.e. values[0], values[-1], values[1], values[-2] ...
        values = new int[1000];
        {
            int k = 0;
            for (int j=0; j< (values.length / 2); j++) {
                values[k] = valuesTemp[j];
                k++;
                values[k] = valuesTemp[valuesTemp.length-1-j];
                k++;
            }
        }

        // create custom array of values

        values3 = new int[] {17,6,1,19,18,3,2,10,13,12,
                20,15,4,11,7,16,9,5,8,14,21};
        values4 = new int[] {1,2,3,4,5,6,7,8,9,10};

        actualOperations = 0;
        wavlOperations = 0;

    }
    
    boolean caseIsBalanced() 
    {
    	WAVLTree.WAVLNode root = wavlTree.getRoot();
    	int n = wavlTree.size();
    	if (height(root) > 3*(Math.log(n) / Math.log(2)) ) {
    		return false;
    	}
    	return isBalanced(root);
    }
    
    /* Returns true if binary tree with root as root is height-balanced */
    boolean isBalanced(WAVLTree.WAVLNode node)
    {
        int lh; /* for height of left subtree */
  
        int rh; /* for height of right subtree */
        
        /* If tree is empty then return true */
        if (node == null || node.isInnerNode() == false)
            return true;
  
        /* Get the height of left and right sub trees */
        lh = height(node.getLeft());
        rh = height(node.getRight());
  
        if (Math.abs(lh - rh) <= 1
                && isBalanced(node.getLeft())
                && isBalanced(node.getRight())) 
            return true;
  
        /* If we reach here then tree is not height-balanced */
        return false;
    }
    
	/* UTILITY FUNCTIONS TO TEST isBalanced() FUNCTION */
    /*  The function Compute the "height" of a tree. Height is the
        number of nodes along the longest path from the root node
        down to the farthest leaf node.*/
    private int height(WAVLTree.WAVLNode node) 
    {
        /* base case tree is empty */
        if (node == null)
            return 0;
  
        /* If tree is not empty then height = 1 + max of left
         height and right heights */
        return 1 + Math.max(height(node.getLeft()), height(node.getRight()));
    }
    
    //case 13: select
    private boolean caseSelect() {
        actualTree = new ActualWAVLTree();
        wavlTree = new WAVLTree();

        actualTree.insert(1, "1");
        wavlTree.insert(1, "1");
        
        actualTree.insert(57, "57");
        wavlTree.insert(57, "57");
        
        actualTree.insert(33, "33");
        wavlTree.insert(33, "33");
        
        return actualTree.select(2).equals(wavlTree.select(2));
    }

    //case 12: one item in the tree
    private boolean caseOneItem() {
        actualTree = new ActualWAVLTree();
        wavlTree = new WAVLTree();

        actualTree.insert(1, "1");
        wavlTree.insert(1, "1");

        return actualTree.min().equals(wavlTree.min()) &&
                actualTree.max().equals(wavlTree.max());
    }

    //case 11 - deleting a non-existing item
    private boolean caseDeleteNonExistent() {
        return (wavlTree.delete(22)==-1);
    }

    // case 10:
    // inserting and then re-inserting the same numbers
    // checking the values didn't change in the second insertion
    private boolean caseIdempotent() {
        int n=0;
        actualTree = new ActualWAVLTree();
        wavlTree = new WAVLTree();
        for (int aValues3 : values3) {

            actualOperations += actualTree.insert(aValues3, "" + aValues3);
            wavlOperations += wavlTree.insert(aValues3, "" + aValues3);

            if (!actualTree.max().equals(wavlTree.max()) ||
                    !actualTree.min().equals(wavlTree.min())) {
                // if the maximum / minimum are wrong
            	System.out.println("min/max problem");

                n++;
            }

            for (int val1 : values3) {
                if ((TesterUtils.intValue(actualTree.search(val1)) == val1) !=
                        (TesterUtils.intValue(wavlTree.search(val1)) == val1)) {
                	System.out.println("search: should be " + val1 +", tester got " + 
                        TesterUtils.intValue(actualTree.search(val1)) + "and ours got: " + 
                        TesterUtils.intValue(wavlTree.search(val1)));

                    n++;
                }
            }

            int cont;
            cont = actualTree.insert(aValues3, "" + (-1));
            if (cont != -1)
                n++;
            cont = wavlTree.insert(aValues3, "" + (-1));
            if (cont != -1)
                n++;

            for (int val2 : values3) {
                if ((TesterUtils.intValue(actualTree.search(val2)) == val2) !=
                        (TesterUtils.intValue(wavlTree.search(val2)) == val2)) {
                	System.out.println("search error2: " + TesterUtils.intValue(wavlTree.search(val2))); 
                    n++;
                }
            }
        }


        return (n == 0);
    }

    // case 9: deleting all the items in the tree
    // This test throws an exception if the tested tree reports the wrong size
    private boolean caseDelAll() {
        int n=0;
        for (int aValues4 : values4) {
            actualOperations += actualTree.delete(values4[aValues4 - 1]);
            wavlOperations += wavlTree.delete(values4[aValues4 - 1]);
            if (wavlTree.size() > 0) {
                // while wavlTree is not empty, checking the min & max values
                if ( (!actualTree.max().equals(wavlTree.max())) ||
                        (!actualTree.min().equals(wavlTree.min())) ) {
                    n++;
                }
            } else {
                // if all items were deleted from wavlTree, check if RBTree is empty as well
                if (!wavlTree.empty() || !actualTree.empty()) {
                    n++;
                }
            }
            for (int val : values4) {
                // checking that all the values that were supposed to be deleted are not in the RBTree
                if ((actualTree.search(val) == null) !=
                        (wavlTree.search(val) == null)) {
                    n++;
                }
            }
        }
        return (n == 0);
    }

    // case 8: Insert random ordered ints
    private boolean caseInsertRand() {
        int n=0;
        actualTree = new ActualWAVLTree();
        wavlTree = new WAVLTree();
        for (int aValues4 : values4) {
            actualOperations += actualTree.insert(aValues4, "" + aValues4);
            wavlOperations += wavlTree.insert(aValues4, "" + aValues4);

            if ((!actualTree.max().equals(wavlTree.max())) ||
                    (!actualTree.min().equals(wavlTree.min()))) {
                // if the maximum / minimum are wrong

                n++;
            }

            for (int val : values4) {
                if ((TesterUtils.intValue(actualTree.search(val)) == val) !=
                        (TesterUtils.intValue(wavlTree.search(val)) == val)) {
                    n++;
                }
            }
        }
        return (n == 0);
    }

    //case 7: making a value array by keys
    // This test throws an exception when the tested tree has null elements.
    private boolean caseValArr() {
        return TesterUtils.arraysIdentical(TesterUtils.stringToInt(actualTree.infoToArray()), TesterUtils.stringToInt(wavlTree.infoToArray()));
    }

    //case 6: making a key array
    private boolean caseKeyArray() {    	
        return TesterUtils.arraysIdentical(actualTree.keysToArray(), wavlTree.keysToArray());
        
    }

    // case 4: Re-Insert
    private boolean caseReInsert() {
        int n=0;
        // add values going from the middle outwards
        // i.e. 1000, 1002, 998, ..
        // do it in 2 chunks
        int chunk_size = values.length/4;
        for (int j=0; j<2; j++)
        {
            // insert a chunk of values
            int start = j*chunk_size;
            for (int k=start; k < (start+chunk_size); k++)
            {
                // re-inserting the values that were deleted to both
                // RBTree and wavlTree
                actualOperations += actualTree.insert(values[values.length - 1 - k], ("" + values[values.length - 1 - k]));
                wavlOperations += wavlTree.insert(values[values.length-1-k], ("" + values[values.length - 1 - k]));
            }
            
            // check correctness
            for (int value : values) {
                if ((TesterUtils.intValue(actualTree.search(value)) == value) !=
                        (TesterUtils.intValue(wavlTree.search(value)) == value)) {
                    n++;
                }
            }
        }
        return (n==0);
    }
    
    // case 3 & case 5: Delete
    private boolean caseDelete() {
        int n=0;

        // delete values going from the middle outwards
        // i.e. 1000, 1002, 998, ..
        // do it in 2 chunks
        int chunk_size = values.length/4;
        for (int j=0; j<2; j++)
        {
            // delete a chunk of values
            int start = j*chunk_size;
            for (int k=start; k < (start+chunk_size); k++) {
                actualOperations += actualTree.delete(values[values.length - 1 - k]);
                wavlOperations += wavlTree.delete(values[values.length-1-k]);
            }
            // check correctness
            for (int value : values) {
                // #if a value exists in RBTree and not in wavlTree
                // or value doesn't exist in RBTree and exists in wavlTree
                if ((TesterUtils.intValue(actualTree.search(value)) == value) !=
                        (TesterUtils.intValue(wavlTree.search(value)) == value)) {
                    n++;
                }
            }
        }
        /*no mismatches occurred*/
        return (n==0);
    }

    //case 2: min max sanity
    private boolean caseMinMax() {
        int n = 0;
        // #the min() and max() functions return the values,
        // the values i entered are the string form of the key
        // this is the only reason why this check is valid for this year implementation
        if (Integer.parseInt(wavlTree.min()) > Integer.parseInt(wavlTree.max())){
            n++;
        } else if ((Integer.parseInt(wavlTree.min()) == Integer.parseInt(wavlTree.max())) &&
                wavlTree.size() != 1) {
            n++;
        }
					/*minimum key is smaller than the maximum key*/
        return (n==0);
    }

    /*
     * case 1: Insert Sanity
     */
    private boolean caseInsertSanity() {
        int n = 0;
        //wavlTree = new WAVLTree();
        for (int value : values) {
            //inserting the values both to the tree and list
            //#(key=i, value="i")
            actualOperations += actualTree.insert(value, ("" + value));
            wavlOperations += wavlTree.insert(value, ("" + value));
        }
        for (int value : values) {
            //checking if all values were inserted

            if (TesterUtils.intValue(wavlTree.search(value)) != value) {
                //#if the key's values arn't alike
                n++;
            }
        }
        
        /*all the keys are in the tree*/
        return (n==0);
    }

    /*
     * case 0: empty tree
     * Initialization
     */
    private boolean caseInit() {
        actualTree = new ActualWAVLTree();
        wavlTree = new WAVLTree();
        //empty & size = 0
        return wavlTree.empty() && actualTree.size() == wavlTree.size();
    }

    private boolean empiricTesting() {
        int n = 0;

        actualTree = new ActualWAVLTree();
        wavlTree = new WAVLTree();


        System.out.println("################################");
        System.out.println("##     Empirical Testing:     ##");

        Random gen = new Random(1);

        for (int j = 0; j < 10; j++) {

            if (!actualTree.empty() || !wavlTree.empty()) {
                n++;
                actualTree = new ActualWAVLTree();
                wavlTree = new WAVLTree();
            }

            int totalActOps = 0;
            int totalWavlOps = 0;
            int maxActOps = -1;
            int maxWavlOps = -1;

            for (int i = 0; i < (j+1)*10000; i++ ) {
                int val = gen.nextInt();
                int actOps = actualTree.insert(val,"" + val);
                int wavlOps = wavlTree.insert(val,"" + val);
                if(i==1000) {
                	System.out.println("*************");
                	System.out.println(wavlTree.getRoot().getSubtreeSize());
                	System.out.println(actualTree.size());
                }

//                if ((actOps == -1) != (wavlOps == -1)) {
//                    n++;
//                    break;
//                }


                maxActOps = actOps > maxActOps ? actOps : maxActOps;
                maxWavlOps = wavlOps > maxWavlOps ? wavlOps : maxWavlOps;

                if (actOps > -1) {
                    totalActOps += actOps;
                    totalWavlOps += wavlOps;
                }

            }
            System.out.println("##  j = " + (j+1) + ":");
            System.out.println("##    Reference Insert - max ops: " + maxActOps + ", avg ops: " + (totalActOps/((j+1.0)*10000)));
            System.out.println("##    Tested Insert - max ops: " + maxWavlOps + ", avg ops: " + (totalWavlOps/((j+1.0)*10000)));


            totalActOps = 0;
            totalWavlOps = 0;
            maxActOps = -1;
            maxWavlOps = -1;

            for (int i = 0; i < (j+1)*10000; i++ ) {
                int val = TesterUtils.intValue(actualTree.min());
                if (val == -1) {
                    if (actualTree.empty() && !wavlTree.empty())
                        n++;
                    break;
                }
                int actOps = actualTree.delete(val);
                int wavlOps = wavlTree.delete(val);

                maxActOps = actOps > maxActOps ? actOps : maxActOps;
                maxWavlOps = wavlOps > maxWavlOps ? wavlOps : maxWavlOps;

                if (actOps > -1) {
                    totalActOps += actOps;
                    totalWavlOps += wavlOps;
                }

            }
            System.out.println("##    Reference Delete - max ops: " + maxActOps + ", avg ops: " + (totalActOps/((j+1.0)*10000)));
            System.out.println("##    Tested Delete - max ops: " + maxWavlOps + ", avg ops: " + (totalWavlOps/((j+1.0)*10000)));

            System.out.println();
        }

        return (n == 0);
    }

    public static void main(String[] args) {
        // initialize tests success array to false
        final SuccessStatus[] success = new SuccessStatus[16];

        final ExTester tester = new ExTester();

        runWithInterrupt(success,
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            success[0] = tester.caseInit() ? SuccessStatus.PASS : SuccessStatus.FAIL;
                        } catch (Throwable e) {
                            success[0] = SuccessStatus.EXCEPTION;
                        }
                    }
                }), 0);


        runWithInterrupt(success,
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            success[1] = tester.caseInsertSanity() ? SuccessStatus.PASS : SuccessStatus.FAIL;
                        } catch (Throwable e) {
                            success[1] = SuccessStatus.EXCEPTION;
                        }
                    }
                }), 1);

        runWithInterrupt(success,
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            success[2] = tester.caseMinMax() ? SuccessStatus.PASS : SuccessStatus.FAIL;
                        } catch (Throwable e) {
                            success[2] = SuccessStatus.EXCEPTION;
                        }
                    }
                }), 2);

        runWithInterrupt(success,
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            success[3] = tester.caseDelete() ? SuccessStatus.PASS : SuccessStatus.FAIL;
                        } catch (Throwable e) {
                        	System.out.println("Delete ERROR");
                        	e.printStackTrace();
                        	System.out.println(e.toString());
                        	System.out.println(e.getCause());
                            success[3] = SuccessStatus.EXCEPTION;
                        }
                    }
                }), 3);

        runWithInterrupt(success,
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            success[4] = tester.caseReInsert() ? SuccessStatus.PASS : SuccessStatus.FAIL;
                        } catch (Throwable e) {
                            success[4] = SuccessStatus.EXCEPTION;
                        }
                    }
                }), 4);

        runWithInterrupt(success,
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            success[5] = tester.caseDelete() ? SuccessStatus.PASS : SuccessStatus.FAIL;
                        } catch (Throwable e) {
                            success[5] = SuccessStatus.EXCEPTION;
                        }
                    }
                }), 5);

        runWithInterrupt(success,
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            success[6] = tester.caseKeyArray() ? SuccessStatus.PASS : SuccessStatus.FAIL;
                        } catch (Throwable e) {
                            success[6] = SuccessStatus.EXCEPTION;
                        }
                    }
                }), 6);

        runWithInterrupt(success,
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            success[7] = tester.caseValArr() ? SuccessStatus.PASS : SuccessStatus.FAIL;
                        } catch (Throwable e) {
                            success[7] = SuccessStatus.EXCEPTION;
                        }
                    }
                }), 7);

        runWithInterrupt(success,
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            success[8] = tester.caseInsertRand() ? SuccessStatus.PASS : SuccessStatus.FAIL;
                        } catch (Throwable e) {
                            success[8] = SuccessStatus.EXCEPTION;
                        }
                    }
                }), 8);

        runWithInterrupt(success,
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            success[9] = tester.caseDelAll() ? SuccessStatus.PASS : SuccessStatus.FAIL;
                        } catch (Throwable e) {
                            success[9] = SuccessStatus.EXCEPTION;
                        }
                    }
                }), 9);

        runWithInterrupt(success,
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            success[10] = tester.caseIdempotent() ? SuccessStatus.PASS : SuccessStatus.FAIL;
                        } catch (Throwable e) {
                            success[10] = SuccessStatus.EXCEPTION;
                        }
                    }
                }), 10);


        runWithInterrupt(success,
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            success[11] = tester.caseDeleteNonExistent() ? SuccessStatus.PASS : SuccessStatus.FAIL;
                        } catch (Throwable e) {
                            success[11] = SuccessStatus.EXCEPTION;
                        }
                    }
                }), 11);


        runWithInterrupt(success,
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            success[12] = tester.caseOneItem() ? SuccessStatus.PASS : SuccessStatus.FAIL;
                        } catch (Throwable e) {
                            success[12] = SuccessStatus.EXCEPTION;
                        }
                    }
                }), 12);

        runWithInterrupt(success,
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            success[13] = tester.empiricTesting() ? SuccessStatus.PASS : SuccessStatus.FAIL;
                        } catch (Throwable e) {
                            success[13] = SuccessStatus.EXCEPTION;
                        }
                    }
                }), 13);
        
        runWithInterrupt(success,
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            success[14] = tester.caseSelect() ? SuccessStatus.PASS : SuccessStatus.FAIL;
                        } catch (Throwable e) {
                            success[14] = SuccessStatus.EXCEPTION;
                        }
                    }
                }), 14);
        
        runWithInterrupt(success,
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            success[15] = tester.caseIsBalanced() ? SuccessStatus.PASS : SuccessStatus.FAIL;
                        } catch (Throwable e) {
                            success[15] = SuccessStatus.EXCEPTION;
                        }
                    }
                }), 15);

        TesterUtils.printStatus(success, tester.actualOperations, tester.wavlOperations);

    }

    private static void runWithInterrupt(SuccessStatus[] success, Thread thread, int idx) {
        thread.start();

        for (int i = 0; i < 20; i++) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (!thread.isAlive())
                break;
        }
        if (thread.isAlive()){
            thread.stop();
            success[idx] = SuccessStatus.EXCEPTION;
        }
    }
}