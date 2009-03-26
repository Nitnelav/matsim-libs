/* *********************************************************************** *
 * project: org.matsim.*
 * EventsReaderXMLv1.java
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2007, 2008 by the members listed in the COPYING,  *
 *                   LICENSE and WARRANTY file.                            *
 * email           : info at matsim dot org                                *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *   See also COPYING, LICENSE and WARRANTY file                           *
 *                                                                         *
 * *********************************************************************** */

package org.matsim.core.events;

import java.util.Stack;

import org.matsim.core.basic.v01.IdImpl;
import org.matsim.core.utils.io.MatsimXmlParser;
import org.xml.sax.Attributes;

public class EventsReaderXMLv1 extends MatsimXmlParser {

	static public final String EVENT = "event";
	static public final String EVENTS = "events";

	private final Events events;

	public EventsReaderXMLv1(final Events events) {
		this.events = events;
		this.setValidating(false);// events-files have not DTD, thus they cannot validate
	}

	@Override
	public void startTag(final String name, final Attributes atts, final Stack<String> context) {
		if (EVENT.equals(name)) {
			startEvent(atts);
		}
	}

	@Override
	public void endTag(final String name, final String content, final Stack<String> context) {
	}

	private void startEvent(final Attributes atts) {
		double time = Double.parseDouble(atts.getValue("time"));
		String eventType = atts.getValue("type");

		if (LinkLeaveEvent.EVENT_TYPE.equals(eventType)) {
			this.events.processEvent(new LinkLeaveEvent(time,
					new IdImpl(atts.getValue(LinkLeaveEvent.ATTRIBUTE_PERSON)),
					new IdImpl(atts.getValue(LinkLeaveEvent.ATTRIBUTE_LINK))));
		} else if (LinkEnterEvent.EVENT_TYPE.equals(eventType)) {
			this.events.processEvent(new LinkEnterEvent(time,
					new IdImpl(atts.getValue(LinkEnterEvent.ATTRIBUTE_PERSON)),
					new IdImpl(atts.getValue(LinkEnterEvent.ATTRIBUTE_LINK))));
		} else if (ActEndEvent.EVENT_TYPE.equals(eventType)) {
			this.events.processEvent(new ActEndEvent(time,
					new IdImpl(atts.getValue(ActEndEvent.ATTRIBUTE_PERSON)),
					new IdImpl(atts.getValue(ActEndEvent.ATTRIBUTE_LINK)),
					atts.getValue(ActEndEvent.ATTRIBUTE_ACTTYPE)));
		} else if (ActStartEvent.EVENT_TYPE.equals(eventType)) {
			this.events.processEvent(new ActStartEvent(time,
					new IdImpl(atts.getValue(ActStartEvent.ATTRIBUTE_PERSON)),
					new IdImpl(atts.getValue(ActStartEvent.ATTRIBUTE_LINK)),
					atts.getValue(ActStartEvent.ATTRIBUTE_ACTTYPE)));
		} else if (AgentArrivalEvent.EVENT_TYPE.equals(eventType)) {
			this.events.processEvent(new AgentArrivalEvent(time,
					new IdImpl(atts.getValue(AgentArrivalEvent.ATTRIBUTE_PERSON)),
					new IdImpl(atts.getValue(AgentArrivalEvent.ATTRIBUTE_LINK))));
		} else if (AgentDepartureEvent.EVENT_TYPE.equals(eventType)) {
			this.events.processEvent(new AgentDepartureEvent(time,
					new IdImpl(atts.getValue(AgentDepartureEvent.ATTRIBUTE_PERSON)),
					new IdImpl(atts.getValue(AgentDepartureEvent.ATTRIBUTE_LINK))));
		} else if (AgentWait2LinkEvent.EVENT_TYPE.equals(eventType)) {
			this.events.processEvent(new AgentWait2LinkEvent(time,
					new IdImpl(atts.getValue(AgentWait2LinkEvent.ATTRIBUTE_PERSON)),
					new IdImpl(atts.getValue(AgentWait2LinkEvent.ATTRIBUTE_LINK))));
		} else if (AgentStuckEvent.EVENT_TYPE.equals(eventType)) {
			this.events.processEvent(new AgentStuckEvent(time,
					new IdImpl(atts.getValue(AgentStuckEvent.ATTRIBUTE_PERSON)),
					new IdImpl(atts.getValue(AgentStuckEvent.ATTRIBUTE_LINK))));
		} else if (AgentMoneyEvent.EVENT_TYPE.equals(eventType)) {
			this.events.processEvent(new AgentMoneyEvent(time,
					new IdImpl(atts.getValue(AgentStuckEvent.ATTRIBUTE_PERSON)),
					Double.parseDouble(atts.getValue(AgentMoneyEvent.ATTRIBUTE_AMOUNT))));
		}
	}

}
