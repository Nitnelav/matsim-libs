/* *********************************************************************** *
 * project: org.matsim.*
 * CalcAverageTolledTripLength.java
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2007 by the members listed in the COPYING,        *
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

package org.matsim.analysis;

import java.util.TreeMap;

import org.matsim.core.api.network.Link;
import org.matsim.core.api.network.Network;
import org.matsim.core.basic.v01.IdImpl;
import org.matsim.core.events.AgentArrivalEvent;
import org.matsim.core.events.LinkEnterEvent;
import org.matsim.core.events.handler.AgentArrivalEventHandler;
import org.matsim.core.events.handler.LinkEnterEventHandler;
import org.matsim.roadpricing.RoadPricingScheme;
import org.matsim.roadpricing.RoadPricingScheme.Cost;

/**
 * Calculates the distance of a trip which occurred on tolled links.
 * Requires roadpricing to be on.
 *
 * @author mrieser
 */
public class CalcAverageTolledTripLength implements LinkEnterEventHandler, AgentArrivalEventHandler {

	private double sumLength = 0.0;
	private int cntTrips = 0;
	private RoadPricingScheme scheme = null;
	private Network network = null;
	private TreeMap<String, Double> agentDistance = null;

	public CalcAverageTolledTripLength(final Network network, final RoadPricingScheme scheme) {
		this.scheme = scheme;
		this.network = network;
		this.agentDistance = new TreeMap<String, Double>();
	}

	public void handleEvent(final LinkEnterEvent event) {
		Cost cost = this.scheme.getLinkCost(new IdImpl(event.getLinkId().toString()), event.getTime());
		if (cost != null) {
			Link link = event.getLink();
			if (link == null) {
				link = this.network.getLink(new IdImpl(event.getLinkId().toString()));
			}
			if (link != null) {
				Double length = this.agentDistance.get(event.getPersonId().toString());
				if (length == null) {
					length = 0.0;
				}
				length += link.getLength();
				this.agentDistance.put(event.getPersonId().toString(), length);
			}
		}
	}

	public void handleEvent(final AgentArrivalEvent event) {
		Double length = this.agentDistance.get(event.getPersonId().toString());
		if (length != null) {
			this.sumLength += length;
			this.agentDistance.put(event.getPersonId().toString(), 0.0);
		}
		this.cntTrips++;
	}

	public void reset(final int iteration) {
		this.sumLength = 0.0;
		this.cntTrips = 0;
	}

	public double getAverageTripLength() {
		if (this.cntTrips == 0) return 0;
		return (this.sumLength / this.cntTrips);
	}
}
