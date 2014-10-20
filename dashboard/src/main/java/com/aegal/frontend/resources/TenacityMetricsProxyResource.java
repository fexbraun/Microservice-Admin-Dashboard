package com.aegal.frontend.resources;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import javax.ws.rs.*;
import javax.ws.rs.core.StreamingOutput;

import com.google.common.base.CharMatcher;
import com.google.common.base.Preconditions;

/**
 * Deploys new applications. User: A.Egal Date: 8/14/14 Time: 10:21 PM
 */
@Path("/tenacity")
public class TenacityMetricsProxyResource {

    private final CharMatcher validHostNameChars = CharMatcher.JAVA_LETTER_OR_DIGIT.or(CharMatcher.anyOf(":-."));
    
    /**
     * This resource method proxies the tenacity metrics stream of a given
     * server.
     * 
     * In a distributed environment the services may not be reachable from the
     * public Internet. Given a scenario where the microservice dashboard is the
     * only public available entry point, we must not call metrics/streams of
     * the services directly from the javascript code. Instead we use this proxy
     * method.
     *
     * @return the metrics stream
     */
    @GET
    @Path("/metrics.stream.proxy/{server}/{port}")
    @Produces("text/event-stream")
    public StreamingOutput getMetricsStream(
	    @PathParam("server") final String hostname,
	    @PathParam("port") final int port) {
	
	Preconditions.checkArgument(validHostNameChars.matchesAllOf(hostname),
		"hostname must not contain any other characters than java letters and digits, dots, hyphens and colons.");
	
	return new StreamingOutput() {

	    @Override
	    public void write(OutputStream output) throws IOException,
		    WebApplicationException {
		try {

		    final InputStream openStream = new URL("http://" + hostname
			    + ":" + port + "/tenacity/metrics.stream")
			    .openStream();

		    int b = 0;
		    while (b >= 0) {
			b = openStream.read();
			output.write(b);
		    }

		} catch (final Exception e) {
		    throw new WebApplicationException(e);
		}

	    }
	};
    }
}
