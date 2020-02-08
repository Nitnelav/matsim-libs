/* *********************************************************************** *
 * project: org.matsim.*
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2013 by the members listed in the COPYING,        *
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

package org.matsim.contrib.dvrp.schedule;

import org.matsim.api.core.v01.network.Link;

import com.google.common.base.MoreObjects;

public class StayTask extends AbstractTask {
	private final Link link;
	private final String name;

	public StayTask(double beginTime, double endTime, Link link) {
		this(beginTime, endTime, link, null);
	}

	public StayTask(double beginTime, double endTime, Link link, String name) {
		super(beginTime, endTime);
		this.link = link;
		this.name = name;
	}

	public final Link getLink() {
		return link;
	}

	public final String getName() {
		return name;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("super", super.toString())
				.add("link", link)
				.add("name", name)
				.toString();
	}
}
