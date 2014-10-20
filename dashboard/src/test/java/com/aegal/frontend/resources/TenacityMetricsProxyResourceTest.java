package com.aegal.frontend.resources;

import static org.junit.Assert.assertEquals;
import io.dropwizard.testing.junit.ResourceTestRule;

import javax.ws.rs.core.Response.Status;

import org.junit.ClassRule;
import org.junit.Test;

import com.sun.jersey.api.client.ClientResponse;

public class TenacityMetricsProxyResourceTest {

    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new TenacityMetricsProxyResource())
            .build();

    
    @Test
    public void testVerifyPort() {
	ClientResponse response = resources.client().resource("/tenacity/metrics.stream.proxy/localhost/no-number").get(ClientResponse.class);
	assertEquals(Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    
    @Test(expected=IllegalArgumentException.class)
    public void testVerifyHostName() {
	resources.client().resource("/tenacity/metrics.stream.proxy/localhost%3F25someattack/123").get(ClientResponse.class);
    }
    
    @Test
    public void testVerifyHostNameIPAddressWorks() {
	resources.client().resource("/tenacity/metrics.stream.proxy/10.10.10.11/123").get(ClientResponse.class);
	resources.client().resource("/tenacity/metrics.stream.proxy/2001:0db8:0:0:8d3:0:0:0/123").get(ClientResponse.class);
    }
    
}
