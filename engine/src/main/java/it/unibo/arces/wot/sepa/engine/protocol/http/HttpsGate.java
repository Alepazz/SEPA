package it.unibo.arces.wot.sepa.engine.protocol.http;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

import org.apache.http.ExceptionLogger;
import org.apache.http.impl.nio.bootstrap.HttpServer;
import org.apache.http.impl.nio.bootstrap.ServerBootstrap;
import org.apache.http.impl.nio.reactor.IOReactorConfig;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import it.unibo.arces.wot.sepa.commons.exceptions.SEPAProtocolException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPASecurityException;
import it.unibo.arces.wot.sepa.engine.bean.EngineBeans;
import it.unibo.arces.wot.sepa.engine.core.EngineProperties;
import it.unibo.arces.wot.sepa.engine.protocol.http.handler.EchoHandler;
import it.unibo.arces.wot.sepa.engine.protocol.http.handler.RegisterHandler;
import it.unibo.arces.wot.sepa.engine.protocol.http.handler.SecureQueryHandler;
import it.unibo.arces.wot.sepa.engine.protocol.http.handler.SecureUpdateHandler;
import it.unibo.arces.wot.sepa.engine.protocol.http.handler.JWTRequestHandler;
import it.unibo.arces.wot.sepa.engine.scheduling.Scheduler;
import it.unibo.arces.wot.sepa.engine.security.AuthorizationManager;

public class HttpsGate {
	protected static final Logger logger = LogManager.getLogger("HttpsGate");

	protected EngineProperties properties;
	protected Scheduler scheduler;

	protected String serverInfo = "SEPA Gate-HTTPS/1.1";
	protected HttpServer server = null;

	protected IOReactorConfig config = IOReactorConfig.custom().setTcpNoDelay(true).setSoReuseAddress(true).build();

	protected AuthorizationManager oauth;

	public HttpsGate(EngineProperties properties, Scheduler scheduler, AuthorizationManager oauth) throws SEPASecurityException, SEPAProtocolException {
		this.oauth = oauth;

		try {
			server = ServerBootstrap.bootstrap().setListenerPort(properties.getHttpsPort()).setServerInfo(serverInfo)
					.setIOReactorConfig(config).setSslContext(oauth.getSSLContext())
					.setExceptionLogger(ExceptionLogger.STD_ERR)
					.registerHandler(properties.getRegisterPath(), new RegisterHandler(oauth))
					.registerHandler(properties.getSecurePath() + properties.getQueryPath(),
							new SecureQueryHandler(scheduler, oauth))
					.registerHandler(properties.getSecurePath() + properties.getUpdatePath(),
							new SecureUpdateHandler(scheduler, oauth))
					.registerHandler(properties.getTokenRequestPath(), new JWTRequestHandler(oauth))
					.registerHandler("/echo", new EchoHandler()).create();
		} catch (KeyManagementException | NoSuchAlgorithmException | IllegalArgumentException e) {
			throw new SEPASecurityException(e);
		}
		
		try {
			server.start();
		} catch (IOException e) {
			throw new SEPAProtocolException(e);
		}

		if (server.getEndpoint().getException() != null) {
			throw new SEPAProtocolException(server.getEndpoint().getException());
		}

		String address = server.getEndpoint().getAddress().toString();
		try {
			address = Inet4Address.getLocalHost().getHostAddress();
		} catch (UnknownHostException e1) {
			throw new SEPAProtocolException(e1);
		}

		EngineBeans.setSecureQueryURL("https://" + address + ":" + properties.getHttpsPort()
				+ properties.getSecurePath() + properties.getQueryPath());
		EngineBeans.setSecureUpdateURL("https://" + address + ":" + properties.getHttpsPort()
				+ properties.getSecurePath() + properties.getUpdatePath());
		EngineBeans.setRegistrationURL(
				"https://" + address + ":" + properties.getHttpsPort() + properties.getRegisterPath());
		EngineBeans.setTokenRequestURL(
				"https://" + address + ":" + properties.getHttpsPort() + properties.getTokenRequestPath());

		System.out.println("SPARQL 1.1 SE Query  | " + EngineBeans.getSecureQueryURL());
		System.out.println("SPARQL 1.1 SE Update | " + EngineBeans.getSecureUpdateURL());
		System.out.println("Client registration  | " + EngineBeans.getRegistrationURL());
		System.out.println("Token request        | " + EngineBeans.getTokenRequestURL());

	}
	
	public void shutdown() {
		server.shutdown(5, TimeUnit.SECONDS);
		
		try {
			server.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
		} catch (InterruptedException e) {
			logger.info(serverInfo+" interrupted: " + e.getMessage());
		}
	}
}
