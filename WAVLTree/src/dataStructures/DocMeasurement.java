package dataStructures;

import java.util.PriorityQueue;
import java.util.Random;

public class DocMeasurement
{
	private final int MULTIPLIER = 10000; 
	private int _i;
	private PriorityQueue<Integer> _keys;
	WAVLTree _tree;
	
	private int totalRebalance_insert, totalRebalance_delete;
	private int maxRebalance_insert, maxRebalance_delete;
	public DocMeasurement(int i)
	{
		_i = i;
		_keys = new PriorityQueue<>();
		
		totalRebalance_insert = totalRebalance_delete = maxRebalance_insert = maxRebalance_delete = 0;
		
		buildTree();
	}
	
	private int getNewKey()
	{
		Random r = new Random();
		int j;
		while(true)
		{
			j = r.nextInt(_i * MULTIPLIER * 100);
			if (!_keys.contains(j))
			{
				return j;
			}
		}
	}
	
	private void buildTree()
	{
		_tree = new WAVLTree();
		
		for (int j = 0; j < _i * MULTIPLIER; j++)
		{
			int newKey = getNewKey();
			_keys.add(newKey);
			
			int count = _tree.insert(newKey, newKey + "");
			totalRebalance_insert += count;
			if (count > maxRebalance_insert)
				maxRebalance_insert = count;
		}
		emptyTree();
	}
	
	private void emptyTree()
	{
		while (!_keys.isEmpty())
		{
			int min = _keys.remove();
			int count = _tree.delete(min);
			totalRebalance_delete += count;
			if (count > maxRebalance_delete)
				maxRebalance_delete = count;
		}
	}

	public void print()
	{
		double insertAvg = (double)totalRebalance_insert / (_i * MULTIPLIER);
		double deleteAvg = (double)totalRebalance_delete / (_i * MULTIPLIER);
		System.out.println("items: " + (_i * MULTIPLIER) + " avg. Insert: " + insertAvg
				+ " avg. Delete: " + deleteAvg + " max Insert: " + maxRebalance_insert 
				+ " max Delete: " + maxRebalance_delete);
	}
}


