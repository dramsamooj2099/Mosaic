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
import org.eclipse.mosaic.lib.objects.vehicle.VehicleData;


import java.util.Arrays;
import java.math.BigInteger;



public class VehicleSendApp1 extends AbstractApplication<VehicleOperatingSystem> 
                              implements CommunicationApplication 
{
     /**
     * Interval at which messages are sent (every 2 seconds).
     */
    private final static long TIME_INTERVAL = 100 * TIME.MILLI_SECOND;
    private final static AdHocChannel vehSendChannel = AdHocChannel.SCH1;
    private final static AdHocChannel vehRecvChannel = AdHocChannel.SCH2;
    private BigInteger mInt;
    static int seqNum = 0;

    @Override
    public void onStartup() {
        getLog().infoSimTime(this, "Initialize application VehicleSendApp1");
        getOs().getAdHocModule().enable(new AdHocModuleConfiguration()
                .addRadio()
                .channel(vehRecvChannel)
                .distance(300)
                .create());
        String unit = this.getOs().getId();
        StringBuilder sb = new StringBuilder();
        for (char c : unit.toCharArray())
        sb.append((int)c);

        mInt = new BigInteger(sb.toString());
        getLog().infoSimTime(this, "Activated Cellular Module");
        
        sample();
    }

    @Override
    public void onMessageReceived(ReceivedV2xMessage receivedV2xMessage) {
        VehicleData vehicleData = getOperatingSystem().getNavigationModule().getVehicleData();
       InterVehicleMsg message = (InterVehicleMsg) receivedV2xMessage.getMessage();
       if(!receivedV2xMessage.getMessage().getRouting().getSource().getSourceName().contains("veh")){
            getLog().infoSimTime(this, ",Received V2X Message from ,{}, {}, Latitude= {}, Longitude= {}, ", receivedV2xMessage.getMessage().getRouting().getSource().getSourceName(),
             message.toString(),
             getOs().getPosition().getLatitude(),
             getOs().getPosition().getLongitude()
            );
       }
    }

    @Override
    public void onAcknowledgementReceived(ReceivedAcknowledgement acknowledgement) {
      
    }

    @Override
    public void onCamBuilding(CamBuilder camBuilder) {
      
    }

    @Override
    public void onMessageTransmitted(V2xMessageTransmission v2xMessageTransmission) {
      
    }

    private void sendAdHocBroadcast() {


       GeoCircle transmissionArea = new GeoCircle(getOperatingSystem().getPosition(), 300);
       VehicleData vehicleData = getOperatingSystem().getNavigationModule().getVehicleData();
        final MessageRouting routing = getOperatingSystem()
                .getAdHocModule()
                .createMessageRouting().viaChannel(vehSendChannel)
                .topoBroadCast(1);
        final InterVehicleMsg message = new InterVehicleMsg(routing, (int) (mInt.intValue() & 0xFF), seqNum++, getOs().getPosition(), "Road OPEN");
        getOs().getAdHocModule().sendV2xMessage(message);
        getLog().infoSimTime(this, ",Broadcast V2X Message from ,{}, {}, Latitude= {}, Longitude= {}, ", this.getOs().getId(),
             message.toString(),
             getOs().getPosition().getLatitude(),
             getOs().getPosition().getLongitude()
            );
    }

    public void sample() {
        getOs().getEventManager().addEvent(
                getOs().getSimulationTime() + TIME_INTERVAL, this
        );
        sendAdHocBroadcast();
    }

    @Override
    public void processEvent(Event event) {
        sample();
    }

    @Override
    public void onShutdown() {
    }

 }