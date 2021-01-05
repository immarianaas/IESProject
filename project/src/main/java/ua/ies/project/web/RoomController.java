package ua.ies.project.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.ui.Model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ua.ies.project.model.Building;
import ua.ies.project.model.Room;
import ua.ies.project.model.Sensor;
import ua.ies.project.model.User;
import ua.ies.project.repository.BuildingRepository;
import ua.ies.project.repository.SensorDataRepository;
import ua.ies.project.repository.SensorRepository;
import ua.ies.project.repository.UserRepository;

@Controller
public class RoomController {

	@Autowired
	private BuildingRepository buildingRepository;

	@Autowired
	private UserRepository userRepository;
    	//----------- QUERIES--------------------

    /*
    @GetMapping("/searchRoomsAvailableByBuilding")
    public String getRoomsAvailable( @CurrentSecurityContext(expression="authentication.name") String username, Model model){
        System.out.println("OLSSSSSSSSSSSSSSSsssssA" + username);

        User u = userRepository.findByUsername(username);
        System.out.println(u.getUsername());
        Map<String, Set<Room>> mapbuildignsAndRooms = new HashMap<String, Set<Room>>();
        
        for(Building b :u.getBuildings()){
            System.out.println("OLSSSSSSSSSSSSSSSsssssA");


            mapbuildignsAndRooms.put(b.getBuildingName(), b.getRooms());


        }
        model.addAttribute("mapbuildignsAndRooms", mapbuildignsAndRooms);

        
        
    return "air_quality";
    }
   
    	//Para o graphStatsCO2
	@GetMapping("/searchByRoom/{id}/Building/{idBuilding}")
	public String showRoomsBuildingCo2(@PathVariable (value = "id") long id, ) {
		System.out.println("Building ID " + id);
		
		User ux = userRepository.findByUsername(username);
        Set<Building> buildings =  ux.getBuildings();

		Set<Room> allRooms = new HashSet<>();
		String buildingName ="";
        for(Building b : buildings){
			if(b.getId() == id){
				buildingName = b.getBuildingName();
				Set<Room> rooms = b.getRooms();
				allRooms.addAll(rooms);

			}
		
		}
		model.addAttribute("graphDataCO2BC_buildingName", buildingName);

		//model.addAttribute("showRoomsbyBuilging", allRooms);



        Set<SensorData> allSensorsData = new HashSet<>();
		
		//rooms info
		ArrayList<Room> roomsInfo = new ArrayList<>();

		Set<Room> rooms = allRooms;
		if(rooms.size() != 0){
			for(Room r : rooms){
				roomsInfo.add(r);
				
				Set<Sensor> sensors = r.getSensors();

				if(sensors.size() != 0){
					for(Sensor s : sensors){
						System.out.println("TYEP  " + s.getType());
	
						if(s.getType().equals("CO2")){
							allSensorsData.addAll(s.getSensorsData());
					}
				}
			}
		}

		model.addAttribute("roomsInfoCO2", roomsInfo);

		}
		


		//bar chart and pie chart
		Map<String, Integer> graphDataX = new TreeMap<>();

		//IDEAS
		//map area chart
		//Map<Long, Arr




		ArrayList<String> alerts = new ArrayList<>();

        for (SensorData sd : allSensorsData) {
            Co2 co2Object= null;

            try{
                co2Object= co2Repository.findById(sd.getId()).get();
            }catch(Exception e){
                continue;
			}
			long roomId_ = sd.getSensor().getRoom().getId();


			


            if(sd.getWarn()){
                String formattedDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(sd.getTimestamp());
				long roomId = sd.getSensor().getRoom().getRoom_number();

                String y = formattedDate +"/"+ roomId +"/"+ "W" ;
				graphDataX.put(y, (int)co2Object.getValue());
				

				String alert = "Alert: Room " + roomId + " with high levels of Co2 at " +  formattedDate;
				alerts.add(alert);
			
			}else{
				String formattedDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(sd.getTimestamp());
				long roomId = sd.getSensor().getRoom().getRoom_number();
                String y = formattedDate +"/" + roomId;

                graphDataX.put(y, (int)co2Object.getValue());  
            }
            System.out.println("  SIZZE MAP ------------------------" + graphDataX.size() );

			int x;
			if(numcolumnsCo2 == 0 ){
				x =10;
			}else{
				x = numcolumnsCo2;
			}

            if(graphDataX.size() == x){
                System.out.println(graphDataX + "  FINAL");//DEL

				//pie graph
                Map<Integer, Integer> resultMap = new TreeMap<>();

                for (String key : graphDataX.keySet()) {
                    Integer value = graphDataX.get(key);
                    
                    if (resultMap.containsKey(value)) {
                        resultMap.put(value, resultMap.get(value) + 1);
                    } else {
                        resultMap.put(value, 1);
                    }
                }
                model.addAttribute("graphDataCO2_pieBC", resultMap);
                break;
            }

		}
		
		model.addAttribute("graphDataCO2BC", graphDataX);//DEL
		model.addAttribute("alertCO2", alerts);


		model.addAttribute("graphDataCO2BC_id", id);//DELbuildingName
		return "graphStatsCO2";
	}

 */

}



