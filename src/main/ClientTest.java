package main;

import static org.junit.Assert.*;

import org.junit.Test;

public class ClientTest {
	@Test
	public void testClient() {
		Client test = new Client();
		test.main(new String[0]);
		
	}
}
