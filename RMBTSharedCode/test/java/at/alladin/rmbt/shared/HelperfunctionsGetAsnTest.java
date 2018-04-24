package at.alladin.rmbt.shared;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.Test;

public class HelperfunctionsGetAsnTest {

	@Test
	public void testGetAsnForMultipleAsnResponse() throws UnknownHostException {
		final InetAddress addr = Inet4Address.getByName("89.201.128.0");
		final long asn = Helperfunctions.getASN(addr);
		assertNotNull(asn);
		assertTrue(asn > 0);
	}
	
	@Test
	public void testGetAsnForSingleAsnResponse() throws UnknownHostException {
		final InetAddress addr = Inet4Address.getByName("213.47.128.0");
		final long asn = Helperfunctions.getASN(addr);
		assertNotNull(asn);
		assertTrue(asn > 0);
	}
}
