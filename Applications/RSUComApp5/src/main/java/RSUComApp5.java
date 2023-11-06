/*
 * Copyright (c) 2020 Fraunhofer FOKUS and others. All rights reserved.
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contact: mosaic@fokus.fraunhofer.de
 */

import org.eclipse.mosaic.fed.application.app.api.CommunicationApplication;
import org.eclipse.mosaic.fed.application.app.api.os.VehicleOperatingSystem;
import org.eclipse.mosaic.fed.application.app.AbstractApplication;
import org.eclipse.mosaic.fed.application.ambassador.simulation.communication.ReceivedV2xMessage;
import org.eclipse.mosaic.fed.application.ambassador.simulation.communication.ReceivedAcknowledgement;
import org.eclipse.mosaic.fed.application.ambassador.simulation.communication.CamBuilder;
import org.eclipse.mosaic.interactions.communication.V2xMessageTransmission;
import org.eclipse.mosaic.interactions.communication.V2xFullMessageReception;
import org.eclipse.mosaic.lib.enums.AdHocChannel;
import org.eclipse.mosaic.fed.application.ambassador.simulation.communication.AdHocModuleConfiguration;
import org.eclipse.mosaic.app.tutorial.message.InterVehicleMsg;
import org.eclipse.mosaic.lib.util.scheduling.EventProcessor;
import org.eclipse.mosaic.fed.application.app.api.Application;
import org.eclipse.mosaic.lib.util.scheduling.Event;

import org.eclipse.mosaic.app.tutorial.message.InterVehicleMsg;
import org.eclipse.mosaic.fed.application.ambassador.simulation.communication.CellModuleConfiguration;
import org.eclipse.mosaic.fed.application.app.AbstractApplication;
import org.eclipse.mosaic.fed.application.app.api.os.RoadSideUnitOperatingSystem;
import org.eclipse.mosaic.lib.enums.AdHocChannel;
import org.eclipse.mosaic.lib.objects.v2x.MessageRouting;
import org.eclipse.mosaic.lib.util.scheduling.Event;
import org.eclipse.mosaic.rti.TIME;
import org.eclipse.mosaic.lib.geo.GeoPoint;
import org.eclipse.mosaic.lib.geo.GeoCircle;
import org.eclipse.mosaic.lib.objects.v2x.V2xMessage;
import org.eclipse.mosaic.interactions.communication.V2xFullMessageReception;
import org.eclipse.mosaic.rti.DATA;
import org.eclipse.mosaic.fed.application.ambassador.simulation.communication.CellModule;
import org.eclipse.mosaic.lib.enums.AdHocChannel;

import java.util.Arrays;
import java.math.BigInteger;

/**
 * Road Side Unit Application used for MOSAIC Tiergarten Tutorial.
 * Sends inter-application messages via broadcast in order to show
 * how to differentiate between intra vehicle and inter vehicle application messages.
 */
public class RSUComApp5 extends AbstractApplication<RoadSideUnitOperatingSystem> 
                                            implements CommunicationApplication

{
    /**
     * Interval at which messages are sent (every 2 seconds).
     */
    private final static long TIME_INTERVAL = 2 * TIME.SECOND;
    private final static AdHocChannel vehSendChannel = AdHocChannel.SCH1;
    private final static AdHocChannel vehRecvChannel = AdHocChannel.SCH2;
    private static int uniqueIdent = 0;
    private BigInteger mInt;
    static int seqNum = 0;

    private void sendBroadcast(InterVehicleMsg message) {

        GeoCircle transmissionArea = new GeoCircle(getOs().getPosition(), 300);

        
        final MessageRouting routing =
                getOs().getAdHocModule().createMessageRouting().viaChannel(vehRecvChannel).topoBroadCast(1);

        if((routing.getSource().getSourceName().equals("rsu_1")) && (getOs().getSimulationTime() > 240 * TIME.SECOND) && (getOs().getSimulationTime() < 260 * TIME.SECOND))
        {
            message = new InterVehicleMsg(routing, (int) (mInt.intValue() & 0xFF), seqNum++, getOs().getPosition(), message.getStringMsg()); 
            
        }
        else
        {
            message = new InterVehicleMsg(routing, (int) (mInt.intValue() & 0xFF), seqNum++, getOs().getPosition(), message.getStringMsg());
            getLog().infoSimTime(this, ",Broadcast V2X Message from ,{}, {}, Latitude= {}, Longitude= {}, ", this.getOs().getId(),
             message.toString(),
             getOs().getPosition().getLatitude(),
             getOs().getPosition().getLongitude()
            );
            getOs().getAdHocModule().sendV2xMessage(message);
        }
        
        
    }

    @Override
    public void onStartup() {
        getLog().infoSimTime(this, "Initialize application");
        getOs().getAdHocModule().enable(new AdHocModuleConfiguration()
                .addRadio()
                .channel(vehSendChannel)
                .distance(300)
                .create());

        String unit = this.getOs().getId();
        StringBuilder sb = new StringBuilder();
        for (char c : unit.toCharArray())
        sb.append((int)c);

        mInt = new BigInteger(sb.toString());

        getLog().infoSimTime(this, "Activated Cellular Module");
    }

    @Override
    public void onShutdown() {
        getLog().infoSimTime(this, "Shutdown application");

    }

    @Override
    public void processEvent(Event event) throws Exception {
    }

      @Override
    public void onMessageTransmitted(V2xMessageTransmission v2xMessageTransmission) {
      
    }

        @Override
    public void onAcknowledgementReceived(ReceivedAcknowledgement acknowledgement) {
      
    }


    @Override
    public void onCamBuilding(CamBuilder camBuilder) {
    }

    @Override
    public void onMessageReceived(ReceivedV2xMessage receivedV2xMessage) {
       InterVehicleMsg message = (InterVehicleMsg) receivedV2xMessage.getMessage();
       getLog().infoSimTime(this, ",Received V2X Message from ,{}, {}, Latitude= {}, Longitude= {}, ", receivedV2xMessage.getMessage().getRouting().getSource().getSourceName(),
             message.toString(),
             getOs().getPosition().getLatitude(),
             getOs().getPosition().getLongitude()
            );
       V2xFullMessageReception msg = new V2xFullMessageReception(getOs().getSimulationTime(), getOs().getId(),(V2xMessage) receivedV2xMessage.getMessage(), receivedV2xMessage.getReceiverInformation());
       if(receivedV2xMessage.getMessage().getRouting().getSource().getSourceName().contains("veh"))
       {
           sendBroadcast(message);
       }
    }
}