/* *********************************************************************** *
 * project: org.matsim.*
 * OTFServerRemote.java
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2008 by the members listed in the COPYING,        *
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

package org.matsim.utils.vis.otfvis.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Collection;

import org.matsim.utils.collections.QuadTree;
import org.matsim.utils.vis.otfvis.data.OTFConnectionManager;
import org.matsim.utils.vis.otfvis.data.OTFServerQuad;

public interface OTFServerRemote extends Remote {
	public enum TimePreference{EARLIER, LATER};
	public boolean requestNewTime(int time, TimePreference searchDirection) throws RemoteException;
	
	public OTFServerQuad getQuad(String id, OTFConnectionManager connect) throws RemoteException;
	public byte[] getQuadConstStateBuffer(String id) throws RemoteException;
	public byte[] getQuadDynStateBuffer(String id, QuadTree.Rect bounds) throws RemoteException;

	public int getLocalTime() throws RemoteException;
	public boolean isLive() throws RemoteException;
	
	public Collection<Double> getTimeSteps() throws RemoteException;
}

