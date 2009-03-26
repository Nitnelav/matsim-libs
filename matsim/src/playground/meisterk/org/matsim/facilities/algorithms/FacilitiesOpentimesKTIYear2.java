/* *********************************************************************** *
 * project: org.matsim.*
 * FacilitiesOpentimesKTIYear2.java
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

package playground.meisterk.org.matsim.facilities.algorithms;

import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.matsim.api.basic.v01.facilities.BasicOpeningTime;
import org.matsim.api.basic.v01.facilities.BasicOpeningTime.DayType;
import org.matsim.core.api.facilities.ActivityOption;
import org.matsim.core.api.facilities.Facility;
import org.matsim.core.api.facilities.OpeningTime;
import org.matsim.core.facilities.FacilitiesImpl;
import org.matsim.core.facilities.FacilitiesReaderMatsimV1;
import org.matsim.core.facilities.OpeningTimeImpl;
import org.matsim.core.facilities.algorithms.AbstractFacilityAlgorithm;
import org.matsim.world.Location;

import playground.meisterk.org.matsim.run.facilities.FacilitiesProductionKTI;

/**
 * Assign every shop an opening time based on shopsOf2005 survey.
 *
 * @author meisterk
 *
 */
public class FacilitiesOpentimesKTIYear2 extends AbstractFacilityAlgorithm {

	private final FacilitiesImpl shopsOf2005 = new FacilitiesImpl("shopsOf2005", FacilitiesImpl.FACILITIES_NO_STREAMING);

	private final String shopsOf2005Filename = "/home/meisterk/sandbox00/ivt/studies/switzerland/facilities/shopsOf2005/facilities_shopsOf2005.xml";

	private static final Logger log = Logger.getLogger(FacilitiesOpentimesKTIYear2.class);

	public FacilitiesOpentimesKTIYear2() {
		super();
	}

	public void init() {

		System.out.println("Reading shops Of 2005 xml file... ");
		FacilitiesReaderMatsimV1 facilities_reader = new FacilitiesReaderMatsimV1(this.shopsOf2005);
		facilities_reader.readFile(this.shopsOf2005Filename);
		System.out.println("Reading shops Of 2005 xml file...done.");

	}

	public void run(final Facility facility) {

		DayType[] days = new DayType[] { DayType.mon, DayType.tue, DayType.wed, DayType.thu, DayType.fri, DayType.sat, DayType.sun };
		DayType[] weekDays = new DayType[] { DayType.mon, DayType.tue, DayType.wed, DayType.thu, DayType.fri };
		double startTime = -1.0;
		double endTime = -1.0;

		Map<DayType, SortedSet<BasicOpeningTime>> closestShopOpentimes = new TreeMap<DayType, SortedSet<BasicOpeningTime>>();

		List<Location> closestShops = this.shopsOf2005.getNearestLocations(facility.getCoord());
		ActivityOption shopsOf2005ShopAct = ((Facility) closestShops.get(0)).getActivityOption(FacilitiesProductionKTI.ACT_TYPE_SHOP);
		if (shopsOf2005ShopAct != null) {
			closestShopOpentimes = shopsOf2005ShopAct.getOpeningTimes();
		} else {
			log.info("shop activity object of closest shop facility is null.");
		}
		Map<String, ActivityOption> activities = facility.getActivityOptions();

		// remove all existing opentimes
		for (ActivityOption a : activities.values()) {
			a.setOpeningTimes(new TreeMap<DayType, SortedSet<BasicOpeningTime>>());
		}

		// if only presence code and work are present
		switch(activities.size()){
			case 2:
				// standard daily opentimes for industry sector
				if (activities.containsKey(FacilitiesProductionKTI.WORK_SECTOR2)) {
					for (DayType day : weekDays) {
						activities.get(FacilitiesProductionKTI.WORK_SECTOR2).addOpeningTime(new OpeningTimeImpl(
								day,
								7.0 * 3600,
								18.0 * 3600));
					}
					// open times of the closest shop for services sector
				} else if (activities.containsKey(FacilitiesProductionKTI.WORK_SECTOR3)) {
					// eliminate lunch break
					for (DayType day : days) {
						SortedSet<BasicOpeningTime> dailyOpentime = closestShopOpentimes.get(day);
						if (dailyOpentime != null) {
							switch(dailyOpentime.size()) {
								case 2:
									startTime = Math.min(
											((OpeningTime) dailyOpentime.toArray()[0]).getStartTime(),
											((OpeningTime) dailyOpentime.toArray()[1]).getStartTime());
									endTime = Math.max(
											((OpeningTime) dailyOpentime.toArray()[0]).getEndTime(),
											((OpeningTime) dailyOpentime.toArray()[1]).getEndTime());
									break;
								case 1:
									startTime = ((OpeningTime) dailyOpentime.toArray()[0]).getStartTime();
									endTime = ((OpeningTime) dailyOpentime.toArray()[0]).getEndTime();
									break;
							}
							activities.get(FacilitiesProductionKTI.WORK_SECTOR3).addOpeningTime(new OpeningTimeImpl(
									day,
									startTime,
									endTime));
						}
					}
				}
				break;
				// if presence code, work and one other imputed activity are present
			case 3:
				for (String activityType : activities.keySet()) {
					if (
							Pattern.matches(FacilitiesProductionKTI.ACT_TYPE_SHOP + ".*", activityType)) {
						activities.get(activityType).setOpeningTimes(closestShopOpentimes);
						activities.get(FacilitiesProductionKTI.WORK_SECTOR3).setOpeningTimes(closestShopOpentimes);
					} else if (
							Pattern.matches(FacilitiesProductionKTI.EDUCATION_KINDERGARTEN, activityType) ||
							Pattern.matches(FacilitiesProductionKTI.EDUCATION_PRIMARY, activityType)) {
						for (DayType day : weekDays) {
							activities.get(activityType).addOpeningTime(new OpeningTimeImpl(
									day,
									8.0 * 3600,
									12.0 * 3600));
							activities.get(activityType).addOpeningTime(new OpeningTimeImpl(
									day,
									13.5 * 3600,
									17.0 * 3600));
							activities.get(FacilitiesProductionKTI.WORK_SECTOR3).addOpeningTime(new OpeningTimeImpl(
									day,
									8.0 * 3600,
									17.0 * 3600));
						}
					} else if (
							Pattern.matches(FacilitiesProductionKTI.EDUCATION_SECONDARY, activityType) ||
							Pattern.matches(FacilitiesProductionKTI.EDUCATION_OTHER, activityType)) {
						for (DayType day : weekDays) {
							activities.get(activityType).addOpeningTime(new OpeningTimeImpl(
									day,
									8.0 * 3600,
									18.0 * 3600));
							activities.get(FacilitiesProductionKTI.WORK_SECTOR3).addOpeningTime(new OpeningTimeImpl(
									day,
									8.0 * 3600,
									18.0 * 3600));
						}
					} else if (
							Pattern.matches(FacilitiesProductionKTI.EDUCATION_HIGHER, activityType)) {
						for (DayType day : weekDays) {
							activities.get(activityType).addOpeningTime(new OpeningTimeImpl(
									day,
									7.0 * 3600,
									22.0 * 3600));
							activities.get(FacilitiesProductionKTI.WORK_SECTOR3).addOpeningTime(new OpeningTimeImpl(
									day,
									7.0 * 3600,
									22.0 * 3600));
						}
						activities.get(activityType).addOpeningTime(new OpeningTimeImpl(
								DayType.sat,
								8.0 * 3600,
								12.0 * 3600));
						activities.get(FacilitiesProductionKTI.WORK_SECTOR3).addOpeningTime(new OpeningTimeImpl(
								DayType.sat,
								8.0 * 3600,
								12.0 * 3600));

					} else if (
							Pattern.matches(FacilitiesProductionKTI.LEISURE_SPORTS, activityType)) {
						for (DayType day : days) {
							if (
									day.equals(DayType.mon) ||
									day.equals(DayType.tue) ||
									day.equals(DayType.wed) ||
									day.equals(DayType.thu) ||
									day.equals(DayType.fri)) {
								startTime = 9.0 * 3600;
								endTime = 22.0 * 3600;
							} else if (
									day.equals(DayType.sat) ||
									day.equals(DayType.sun)) {
								startTime = 9.0 * 3600;
								endTime = 18.0 * 3600;
							}
							activities.get(activityType).addOpeningTime(new OpeningTimeImpl(
									day,
									startTime,
									endTime));
							activities.get(FacilitiesProductionKTI.WORK_SECTOR3).addOpeningTime(new OpeningTimeImpl(
									day,
									startTime,
									endTime));
						}
					} else if (
							Pattern.matches(FacilitiesProductionKTI.LEISURE_GASTRO, activityType)) {
						for (DayType day : days) {
							startTime = 9.0 * 3600;
							endTime = 24.0 * 3600;
							activities.get(activityType).addOpeningTime(new OpeningTimeImpl(
									day,
									startTime,
									endTime));
							activities.get(FacilitiesProductionKTI.WORK_SECTOR3).addOpeningTime(new OpeningTimeImpl(
									day,
									startTime,
									endTime));
						}
					} else if (
							Pattern.matches(FacilitiesProductionKTI.LEISURE_CULTURE, activityType)) {
						for (DayType day : days) {
							startTime = 14.0 * 3600;
							endTime = 24.0 * 3600;
							activities.get(activityType).addOpeningTime(new OpeningTimeImpl(
									day,
									startTime,
									endTime));
							activities.get(FacilitiesProductionKTI.WORK_SECTOR3).addOpeningTime(new OpeningTimeImpl(
									day,
									startTime,
									endTime));
						}
					}
				}
		}
	}

}
