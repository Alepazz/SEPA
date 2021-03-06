package it.unibo.arces.wot.framework.interaction;

import it.unibo.arces.wot.sepa.commons.exceptions.SEPAPropertiesException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPAProtocolException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPASecurityException;
import it.unibo.arces.wot.sepa.commons.sparql.Bindings;
import it.unibo.arces.wot.sepa.commons.sparql.RDFTermLiteral;
import it.unibo.arces.wot.sepa.commons.sparql.RDFTermURI;
import it.unibo.arces.wot.sepa.pattern.ApplicationProfile;
import it.unibo.arces.wot.sepa.pattern.Producer;

public class EventPublisher {
	private ApplicationProfile app;
	
	private String thing;
	private EventPubliserWithOutput publisherWithOutput;
	private EventPubliserWithoutOutput publisherWithoutOutput;
	
	public EventPublisher(String thingURI) throws SEPAPropertiesException, SEPAProtocolException, SEPASecurityException {
		
		this.app = new ApplicationProfile("td.jsap");
		this.thing = thingURI;
		this.publisherWithOutput = new EventPubliserWithOutput();
		this.publisherWithoutOutput = new EventPubliserWithoutOutput();
	}
	
	public EventPublisher(ApplicationProfile app,String thingURI) throws SEPAProtocolException, SEPASecurityException {
		
		this.app = app;
		this.thing = thingURI;
		this.publisherWithOutput = new EventPubliserWithOutput();
		this.publisherWithoutOutput = new EventPubliserWithoutOutput();
	}
	
	class EventPubliserWithOutput extends Producer {

		public EventPubliserWithOutput() throws SEPAProtocolException, SEPASecurityException {
			super(app, "POST_EVENT_WITH_OUTPUT");
		}
	}
	
	class EventPubliserWithoutOutput extends Producer {

		public EventPubliserWithoutOutput() throws SEPAProtocolException, SEPASecurityException {
			super(app, "POST_EVENT");
		}
	}
		
	public void post(String eventURI) {
		Bindings bind = new Bindings();
		bind.addBinding("event", new RDFTermURI(eventURI));
		bind.addBinding("thing", new RDFTermURI(thing));
		publisherWithoutOutput.update(bind);
	}

	public void post(String eventURI,String value) {
		Bindings bind = new Bindings();
		bind.addBinding("thing", new RDFTermURI(thing));
		bind.addBinding("event", new RDFTermURI(eventURI));
		bind.addBinding("value", new RDFTermLiteral(value));
		publisherWithOutput.update(bind);
	}

}
