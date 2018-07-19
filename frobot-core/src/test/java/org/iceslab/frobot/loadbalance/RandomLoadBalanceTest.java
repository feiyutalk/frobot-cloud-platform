package org.iceslab.frobot.loadbalance;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNull;
import static junit.framework.TestCase.assertTrue;

public class RandomLoadBalanceTest {
    private LoadBalance loadBalance;
	private List<String> shards;
	
	@Before
	public void setup() {
		loadBalance = new RandomLoadBalance();
	}
	
	@Test
	public void testSelectSizeOfShardsIsZero() {
		shards = new ArrayList<String>();
		String result = loadBalance.select(shards, null);
		assertNull(result);
	}
	
	@Test
	public void testSelectNullShards() {
		shards = null;
		String result = loadBalance.select(shards, null);
		assertNull(result);
	}
	
	@Test
	public void testSelectSizeOfShardsIsOne() {
		shards = new ArrayList<String>();
		shards.add("219.223.216.19:8000");
		String result = loadBalance.select(shards, null);
		assertEquals("219.223.216.19:8000", result);
	}
	
	@Test
	public void testSelectSizeOfShardsMoreThenOne() {
		shards = new ArrayList<String>();
		shards.add("219.223.216.19:8000");
		shards.add("219.238.154.19:8001");
		shards.add("198.223.216.19:8002");
		shards.add("219.223.216.19:8003");
		shards.add("219.227.216.55:8004");
		shards.add("219.223.216.168:8005");
		shards.add("219.223.146.19:8006");
		String result = loadBalance.select(shards, null);
		assertTrue(shards.contains(result));
	}
}
