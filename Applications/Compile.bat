cd VehicleCommunicationApp1
START /B /WAIT cmd /c mvn clean install
cd ..

cd RSUComApp1
START /B /WAIT cmd /c mvn clean install
cd ..

cd RSUComApp4
START /B /WAIT cmd /c mvn clean install
cd ..

cd RSUComApp5
START /B /WAIT cmd /c mvn clean install
cd ..

cd VehicleSendApp1
START /B /WAIT cmd /c mvn clean install
cd ..

cd VehicleSendApp2
START /B /WAIT cmd /c mvn clean install
cd ..

PAUSE