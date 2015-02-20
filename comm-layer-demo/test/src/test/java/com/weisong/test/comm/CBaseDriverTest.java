package com.weisong.test.comm;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.codahale.metrics.Histogram;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Snapshot;
import com.weisong.test.comm.exception.CTimeoutException;
import com.weisong.test.comm.impl.CBaseAgent;
import com.weisong.test.comm.impl.CBaseEndpoint;
import com.weisong.test.comm.impl.CBaseProxy;
import com.weisong.test.comm.impl.CDefaultDriver;
import com.weisong.test.comm.impl.CDefaultNotificationHandler;
import com.weisong.test.comm.impl.CPduProcessor;
import com.weisong.test.comm.message.CNotification;
import com.weisong.test.comm.message.CResponse;
import com.weisong.test.comm.message.builtin.CCommonMsgs;
import com.weisong.test.comm.message.builtin.CCommonMsgs.Status.StatusValue;

abstract public class CBaseDriverTest {

	final static protected int oneMillion = 1000000; 
	
	final protected int numberPerRound = 100000;
	
	static private class AsyncContext {
		private CCommonMsgs.Ping.Response response;
		private Throwable error;
	}
	
	protected CHub proxyHub;
	protected CBaseProxy[] proxies;
	protected CDefaultDriver[] drivers;
	protected CBaseAgent[] agents;
	protected CBaseEndpoint[] endpoints;
	protected String[] epAddrs;
	
	abstract protected void createObjects() throws Exception;
	
	@Before
	public void prepare() throws Exception {
		createObjects();
		waitForEndpoints();
	}
	
	@Test
	public void testSend() throws Exception {
		for(final CDriver driver : drivers) {
			for(int i = 0; i < epAddrs.length; i++) {
				final CCommonMsgs.Ping.Request request = new CCommonMsgs.Ping.Request(driver.getAddress(), epAddrs[i]);
				CCommonMsgs.Ping.Response response = driver.send(request);
				Assert.assertNotNull(response);
			}
		}
	}
	
	@Test
	public void testSendAsync() throws Exception {
		for(final CDriver driver : drivers) {
			for(int i = 0; i < epAddrs.length; i++) {
				final AsyncContext ctx = new AsyncContext();
				final CCommonMsgs.Ping.Request request = new CCommonMsgs.Ping.Request(driver.getAddress(), epAddrs[i]);
				driver.sendAsync(request, new CDriver.Callback<CCommonMsgs.Ping.Response>() {
					@Override
					public void onSuccess(CCommonMsgs.Ping.Response response) {
						Assert.assertNotNull(response);
						ctx.response = response;
					}

					@Override
					public void onFailure(Throwable ex) {
						ex.printStackTrace();
						Assert.fail("Async invocation failed.");
					}
				});
				
				for(int n = 0; n < 10; n++) {
					if(ctx.response != null) {
						break;
					}
					Thread.sleep(100);
				}
				
				if(ctx.response == null) {
					Assert.fail("Async request timeout!");
				}
				
			}
		}
	}
	
	@Test
	public void testOneway() throws Exception {
		for(CDriver driver : drivers) {
			CCommonMsgs.Status message = new CCommonMsgs.Status(
					epAddrs[0], driver.getAddress(), StatusValue.Ok);
			driver.send(message);
		}
		
		Thread.sleep(2000);
	}

	static private class SendWorker extends Thread {
		
		static private int idSeed = 0;
		
		private boolean shutdown;
		private boolean sync;
		private Histogram histogram;
		private AtomicInteger pending, count;
		private CDriver[] drivers;
		private String[] epAddrs;
		
		public SendWorker(CDriver[] drivers, String[] epAddrs, Histogram histogram, AtomicInteger count, AtomicInteger pending, boolean sync) {
			this.drivers = drivers;
			this.epAddrs = epAddrs;
			this.histogram = histogram;
			this.count = count;
			this.pending = pending;
			this.sync = sync;
			
			setName(String.format("%s-send-worker-%d", sync ? "sync" : "async", ++idSeed));
		}
		
		public void shutdown() {
			shutdown = true;
		}
		
		public void run() {
			while(shutdown == false) {
				for(final CDriver driver : drivers) {
					for(int i = 0; i < epAddrs.length; i++) {
						try {
							final CCommonMsgs.Ping.Request request = new CCommonMsgs.Ping.Request(driver.getAddress(), epAddrs[i]);
							request.timeout = 10000L; // 10 sec
							if(sync) {
								CResponse response = driver.send(request);
								Assert.assertNotNull(response);
								count.incrementAndGet();
								Long time = System.nanoTime() - request.timestamp;
								histogram.update(time);
							}
							else {
								if(pending.intValue() > 10000) {
									Thread.sleep(100);
								}
								pending.incrementAndGet();
								driver.sendAsync(request, new CDriver.Callback<CCommonMsgs.Ping.Response>() {
									@Override
									public void onSuccess(CCommonMsgs.Ping.Response response) {
										pending.decrementAndGet();
										count.incrementAndGet();
										Long time = System.nanoTime() - request.timestamp;
										histogram.update(time);
										Assert.assertNotNull(response);
									}

									@Override
									public void onFailure(Throwable ex) {
										pending.decrementAndGet();
										count.incrementAndGet();
										Long time = System.nanoTime() - request.timestamp;
										histogram.update(time);
										System.err.println("Async invocation failed, " + ex.getMessage());
									}
								});
							}
						}
						catch(Exception ex) {
							ex.printStackTrace();
						}
					}
				}
			}
			System.out.println(getName() + " stopped");
		}
	}

	private void doSend(boolean sync, int numOfWorkers, int numOfRounds) throws Exception {

		AtomicInteger count = new AtomicInteger(1);
		AtomicInteger pending = new AtomicInteger(0);
		
		Histogram histogram = createHistogram();
		
		printStatsHeader();

		SendWorker[] workers = new SendWorker[numOfWorkers];
		for(int i = 0; i < workers.length; i++) {
			workers[i] = new SendWorker(drivers, epAddrs, histogram, count, pending, sync);
			workers[i].start();
		}

		int rounds = 0;
		long st = System.currentTimeMillis();
		while(rounds < numOfRounds) {
			if(count.intValue() % numberPerRound == 0) {
				long t = System.currentTimeMillis();
				long throughput = 1000 * count.intValue() / (t - st);
				printStats(throughput, histogram);
				createHistogram();
				st = t; count.set(1); ++rounds;
			}
		}
		
		for(SendWorker w : workers) {
			w.shutdown();
		}
		for(SendWorker w : workers) {
			w.join();
		}
	}
	
	protected MetricRegistry registry = new MetricRegistry();
	private Histogram createHistogram() {
		return registry.histogram(UUID.randomUUID().toString());
	}
	
	private void printStatsHeader() {
		System.out.println("Throughput   min        mean       75%        95%        max");
		System.out.println("---------------------------------------------------------------");
	}
	
	private void printStats(long throughput, Histogram histogram) {
		Snapshot ss = histogram.getSnapshot();
		System.out.println(String.format("%5d/s %8.2fms %8.2fms %8.2fms %8.2fms %8.2fms",
				throughput,
				1.0f * ss.getMin() / oneMillion, 
				ss.getMean() / oneMillion, 
				ss.get75thPercentile() / oneMillion, 
				ss.get95thPercentile() / oneMillion, 
				1.0f * ss.getMax() / oneMillion
		));
	}
	
	@Test
	public void testManySends() throws Exception {
		doSend(true, 10, 5);
	}
	
	@Test
	public void testManySendsAsync() throws Exception {
		doSend(false, 10, 5);
	}
		
	@Test(expected = CTimeoutException.class)
	public void testSendFailed() throws Exception {
		final CCommonMsgs.Ping.Request request = new CCommonMsgs.Ping.Request("fake", "fake");
		CCommonMsgs.Ping.Response response = drivers[0].send(request);
		Assert.assertNotNull(response);
	}
	
	@Test(expected = CTimeoutException.class)
	public void testSendAsyncFailed() throws Throwable {
		final AsyncContext ctx = new AsyncContext();
		final CCommonMsgs.Ping.Request request = new CCommonMsgs.Ping.Request("fake", "fake");
		drivers[0].sendAsync(request, new CDriver.Callback<CCommonMsgs.Ping.Response>() {
			@Override
			public void onSuccess(CCommonMsgs.Ping.Response response) {
				Assert.fail("Async invocation should fail!");
			}

			@Override
			public void onFailure(Throwable error) {
				ctx.error = error;
			}
		});

		for(int i = 0; i < 8; i++) {
			Thread.sleep(1000);
			if(ctx.error != null) {
				throw ctx.error;
			}
		}
		
		Assert.fail("Async request didn't timeout!");
		
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void waitForEndpoints() throws Exception {
		// Wait for endpoints to register
		final Set<String> epAddrSet = (Set) Collections.synchronizedSet(new HashSet<>()); 
		CMessageHandler handler = new CDefaultNotificationHandler() {
			@Override
			public void onNotification(CNotification n) {
				if(epAddrSet.contains(n.srcAddr) == false) {
					epAddrSet.add(n.srcAddr);
					System.out.println(String.format("Discovered %s", n.srcAddr));
				}
			}
			@Override
			public Class<? extends CNotification>[] getSupportedNotifications() {
				return new Class[] {
					CCommonMsgs.Status.class
				};
			}
		};
		for(CDriver d : drivers) {
			d.setMessageHandler(handler);
		}
		
		// Wait for 10 seconds
		int n = 0, max = 100;
		while(++n < max) {
			Thread.sleep(100);
			if(n > 50 && epAddrSet.size() > 0) {
				break;
			}
		}
		
		if(n < max) {
			epAddrs = epAddrSet.toArray(new String[epAddrSet.size()]);
			System.out.println(String.format("Discovered %d endpoint(s)", epAddrs.length));
		}
		else {
			Assert.fail("Failed to discover any endpoints");
		}
		
	}

	@After
	public void after() throws Exception {
		if(endpoints != null) {
			for(CBaseEndpoint e : endpoints) {
				e.shutdown();
			}
		}
		if(proxies != null) {
			for(CBaseProxy p : proxies) {
				p.shutdown();
			}
		}
		if(drivers != null) {
			for(CDefaultDriver d : drivers) {
				d.shutdown();
			}
		}
		if(proxyHub != null && proxyHub instanceof CPduProcessor) {
			((CPduProcessor) proxyHub).shutdown();
		}
	}
	
}
