package ua.ies.project.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

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
import ua.ies.project.model.Co2;
import ua.ies.project.model.Room;
import ua.ies.project.model.Sensor;
import ua.ies.project.model.SensorData;
import ua.ies.project.model.User;
import ua.ies.project.repository.BuildingRepository;
import ua.ies.project.repository.Co2Repository;
import ua.ies.project.repository.PeopleCounterRepository;
import ua.ies.project.repository.RoomRepository;
import ua.ies.project.repository.SensorDataRepository;
import ua.ies.project.repository.SensorRepository;
import ua.ies.project.repository.UserRepository;

@Controller
public class RoomController {


	@Autowired
	private BuildingRepository buildingRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private SensorDataRepository sensorDataRepository;

	@Autowired
	private SensorRepository sensorRepository;
	
	@Autowired
	private RoomRepository roomRepository;
	@Autowired

	private Co2Repository co2Repository;

	
	@Autowired
	private PeopleCounterRepository pcRepository;
	
	//VER !!!!!! ????? COMO OBTER O ROOM EM ESPECIFICO -> 
	@GetMapping("/searchRoom/{id}")
	public String search_graphicalInfoRoom( @PathVariable (value = "id") long id,  Model model, @CurrentSecurityContext(expression="authentication.name") String username){
		model.addAttribute("graphDataCO2Room_id", id);//DELbuildingName


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

		ArrayList<String> alerts = new ArrayList<>();
		
        for (SensorData sd : allSensorsData) {
            Co2 co2Object= null;

            try{
                co2Object= co2Repository.findById(sd.getId()).get();
            }catch(Exception e){
                continue;
			}

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

			

            if(graphDataX.size() == 10){
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


		return "CO2_Info_byRoom";		
	}
 //##############################################################################################################################################################################


	
	//pesquisa de graficos por um quarto em especifico
	@GetMapping("/searchGraphicalInfoRoom/{id}")
	public String c( @PathVariable (value = "id") long id, Model model, 
	@CurrentSecurityContext(expression="authentication.name") String username,  
	@RequestParam(required = false) String dateInit, 
	@RequestParam(required = false) String dateEnd) throws ParseException {

		if(dateInit != null && dateEnd != null){
			System.out.println( "ID ROOM     " + id);	

			User ux = userRepository.findByUsername(username);
			Set<Building> buildings =  ux.getBuildings();

			Set<Room> allRooms = new HashSet<>();
			for(Building b : buildings){
				if(b.getId() == id){
					Set<Room> rooms = b.getRooms();
					
					allRooms.addAll(rooms);

				}
			}

			Set<SensorData> allSensorsData = new HashSet<>();
    

			Set<Room> rooms = allRooms;
			if(rooms.size() != 0){
				for(Room r : rooms){
					if(r.getId() == id){
						System.out.println("\n\n\n\nSENSORES r.getId()  -> "  + r.getId() + "ID    -> " + id);


					
					
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
			}


			//format data 1
			String[] allDate1 = dateInit.split(" "); 
			String[] date01 = allDate1[0].split("/"); 
			String[] hours01 = allDate1[1].split(":"); 
			//2021-01-07 18:29:36
			String dataFormatted1 = date01[2] + "-"+date01[0] + "-"+ date01[1] + " " + hours01[0]+":"+hours01[1]+":00.0";
			Date dateInit1= new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(dataFormatted1);

			//format data 2
			String[] allDate2 = dateEnd.split(" ");
			String[] date02 = allDate2[0].split("/"); 
			String[] hours02 = allDate2[1].split(":"); 
			//2021-01-07 18:29:36
			String dataFormatted2 = date02[2] + "-"+date02[0] + "-"+ date02[1] + " " + hours02[0]+":"+hours02[1]+":00.0"; 

			Date dateEnd1= new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(dataFormatted2);
			//Map<String, Integer> graphDataX = new TreeMap<>();

			Co2 co2Object= null;

			ArrayList<String> alerts = new ArrayList<>();

			Map<String, Integer> graphXCO2_date = new TreeMap<>();


			for (SensorData sd : allSensorsData) {
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");  
				String strDate = dateFormat.format(sd.getTimestamp());  
				Date dateBD= new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(strDate);
				System.out.println("\n\n\nDate BD  -> "  + dateBD + "  Date 1  -> " + dateInit + "  Date 2  -> "+ dateEnd1);

                if (dateInit1 != null)
			

                    if (dateBD.compareTo(dateInit1) < 0)
                        continue;
                
                if (dateEnd1 != null)
                    // se a data for maior q o 'end', tambem tirar
                    if (dateBD.compareTo(dateEnd1)>0)
						continue;
				
				try{
					co2Object= co2Repository.findById(sd.getId()).get();
				}catch(Exception e){
					continue;
				}

				if(sd.getWarn()){
					String formattedDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(sd.getTimestamp());
					long roomId = sd.getSensor().getRoom().getRoom_number();
	
					String y = formattedDate +"/"+ roomId +"/"+ "W" ;
					graphXCO2_date.put(y, (int)co2Object.getValue());
					
	
					String alert = "Alert: Room " + roomId + " with high levels of Co2 at " +  formattedDate;
					alerts.add(alert);
				
				}else{
					String formattedDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(sd.getTimestamp());
					long roomId = sd.getSensor().getRoom().getRoom_number();
					String y = formattedDate +"/" + roomId;
	
					graphXCO2_date.put(y, (int)co2Object.getValue());  
				}



				//pie graph
				Map<Integer, Integer> resultMapCO2_pieBCByDates = new TreeMap<>();

				for (String key : graphXCO2_date.keySet()) {
					Integer value = graphXCO2_date.get(key);
					
					if (resultMapCO2_pieBCByDates.containsKey(value)) {
						resultMapCO2_pieBCByDates.put(value, resultMapCO2_pieBCByDates.get(value) + 1);
					} else {
						resultMapCO2_pieBCByDates.put(value, 1);
					}
				}
				model.addAttribute("graphDataCO2_pieBCByDates_room", resultMapCO2_pieBCByDates);
				break;
				
								

			}

			model.addAttribute("roomsInfoCO2ByDates_room", graphXCO2_date);
			model.addAttribute("alert_CO2_room", alerts);



			System.out.println("\n\n\n\nSENSORES to o ROOM  -> "  + graphXCO2_date + "alerts -> " + alerts);  
			}
			return "redirect:/CO2_Info_byRoom/"+id;		

		}else{
			return "redirect:/CO2_Info_byRoom";		


		}

	

	}
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



