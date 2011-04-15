package com.redhat.qe.auto.testng;

import org.testng.SkipException;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;


/**
 * @author jweiss
 * A set of tests for testing TestNG-related stuff (like listeners, or group
 * logic etc).  Edit as you see fit.
 */
@Test
public class TestTests extends TestScript {

	@BeforeSuite
	public void setBuild() {
		System.setProperty("org.apache.commons.logging.simplelog.log.httpclient.wire", "debug");

		System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient", "debug");
	
	}
	
	public void mytest1() {
		log.info("Testing test1.");
		Assert.assertTrue(true, "Yeah!");
	}
	
	
	public void mytest2() {
		log.info("Testing test2.");
		Assert.assertTrue(false, "Boo!");
	}
	
	public void test3() {
		log.info("Testing test3.");
		throw new SkipException("skipping test, because i felt like it.");
	}
}
