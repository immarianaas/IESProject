package ua.ies.project.web;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import ua.ies.project.model.BodyTemperature;
import ua.ies.project.model.Building;
import ua.ies.project.model.Co2;
import ua.ies.project.model.PeopleCounter;
import ua.ies.project.model.Room;
import ua.ies.project.model.Sensor;
import ua.ies.project.model.SensorData;
import ua.ies.project.model.User;
import ua.ies.project.repository.BodyTemperatureRepository;
import ua.ies.project.repository.BuildingRepository;
import ua.ies.project.repository.Co2Repository;
import ua.ies.project.repository.PeopleCounterRepository;
import ua.ies.project.repository.RoomRepository;
import ua.ies.project.repository.SensorDataRepository;
import ua.ies.project.repository.SensorRepository;
import ua.ies.project.repository.UserRepository;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;

@Controller
public class BuildingController {

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
	


	@Autowired
	private BodyTemperatureRepository btRepository;

	public boolean checkIfMine(String uname, long data_id) {
        SensorData sd = sensorDataRepository.findById(data_id).get();
        for (User u : sd.getSensor().getRoom().getBuilding().getUsers()) {
            if (u.getUsername().equals(uname)) return true;
        }
        return false;
	}
	
	//PARECE SER MAIS O dashboard do webController
	@GetMapping("/allBuildings")
	public String viewHomePage(Model model, @CurrentSecurityContext(expression="authentication.name") String username, @PathVariable Long id) {
		User u = userRepository.findByUsername(username);
		model.addAttribute("listBuildings", u.getBuildings());


		return "dashboard";
	}
	

	
	
	@GetMapping("/newBuildingForm")
	public String shownewBuildingForm(Model model, @CurrentSecurityContext(expression="authentication.name") String username) {
		Building building = new Building();
		model.addAttribute("building", building);
		return "newBuilding";
	}

	@GetMapping("/newSensorForm/{id}") //este id vai ser o id do room
	public String shownewSensorForm(@PathVariable ( value = "id") long id, Model model,@CurrentSecurityContext(expression="authentication.name") String username) {
		Room r = roomRepository.getOne(id);
		Sensor s = new Sensor();
		model.addAttribute("room", r);
		model.addAttribute("sensor", s);
		return "newSensor";
	}



	@GetMapping("/newRoomForm/{id}")
	public String shownewBuildingForm(@PathVariable ( value = "id") long id, Model model,@CurrentSecurityContext(expression="authentication.name") String username) {
		Room room = new Room();
		Building b = getBuildingById(id);
		model.addAttribute("room", room);
		model.addAttribute("building", b);
		return "newRoom";
	}




	// ---
	@GetMapping("/updateBuilding/{id}")
	public String showFormBuildingUpdate(@PathVariable ( value = "id") long id, Model model, @CurrentSecurityContext(expression="authentication.name") String username) {
		Building b = getBuildingById(id);
		//System.out.println(b);
		model.addAttribute("building", b);
		return "updateBuilding";
	}


	// ---
	@GetMapping("/updateRoom/{id}")
	public String showFormRoomUpdate(@PathVariable ( value = "id") long id, Model model, @CurrentSecurityContext(expression="authentication.name") String username) {
		Room r = roomRepository.getOne(id);
		//System.out.println(b);
		model.addAttribute("room", r);
		return "updateRoom";
	}


	// ---
	
	@PostMapping("/saveBuilding")
	//public String saveBuilding(@ModelAttribute("building") Building building,  Model model, @CurrentSecurityContext(expression="authentication.name") String username, @RequestBody Building newbuilding) {
	public String saveBuilding(@ModelAttribute("building") Building newbuilding,  Model model, @CurrentSecurityContext(expression="authentication.name") String username) {
	//public String saveBuilding( Model model, @CurrentSecurityContext(expression="authentication.name") String username, @RequestBody Building newbuilding) {
		//user authenticated
		User user = userRepository.findByUsername(username);
		
		//save building
		newbuilding = buildingRepository.save(newbuilding);
		
		//add new building to user
		user.addBuilding(newbuilding);
		
		//save user in building table
        user = userRepository.save(user); // faz update!

		newbuilding.addUser(user);
		//save new building
		buildingRepository.save(newbuilding);
		
		return "redirect:/dashboard";
		
	}

	@PostMapping("/saveBuildingUpdate/{id}")
	//public String saveBuilding(@ModelAttribute("building") Building building,  Model model, @CurrentSecurityContext(expression="authentication.name") String username, @RequestBody Building newbuilding) {
	public String saveBuilding(@ModelAttribute("building") Building newbuilding,  Model model, @CurrentSecurityContext(expression="authentication.name") String username, @PathVariable long id) {
	//public String saveBuilding( Model model, @CurrentSecurityContext(expression="authentication.name") String username, @RequestBody Building newbuilding) {
		//user authenticated
		User user = userRepository.findByUsername(username);
		
		newbuilding.setId(id);
		//save building
		newbuilding = buildingRepository.save(newbuilding);
		
		
		//add new building to user
		user.addBuilding(newbuilding);
		
		//save user in building table
        user = userRepository.save(user); // faz update!

		newbuilding.addUser(user);
		//save new building
		buildingRepository.save(newbuilding);
		
		
		return "redirect:/dashboard";
		
	}


	@PostMapping("/saveRoom/{id}") // o id é do edificio
	public String saveNewRoom(@ModelAttribute("room") Room newroom, Model model,@CurrentSecurityContext(expression="authentication.name") String username, @PathVariable long id) {
		// verificar se a room corresponde ao user
		Building b = getBuildingById(id);

		newroom.setBuilding(b);
		newroom = roomRepository.save(newroom);
		b.addRoom(newroom);
		buildingRepository.save(b);
		return "redirect:/dashboard";
	}

	@PostMapping("/saveNewSensor/{id}")	// -> vai ser o id da room
	public String saveNewSensor(@ModelAttribute("sensor") Sensor newsensor, Model model,@CurrentSecurityContext(expression="authentication.name") String username, @PathVariable long id) {
		Room r = roomRepository.getOne(id);
		newsensor.setRoom(r);
		if (!(newsensor.getType().equals("CO2") || newsensor.getType().equals("BODY_TEMPERATURE") || newsensor.getType().equals("PEOPLE_COUNTER") ))
			return "redirect:/newSensorForm/"+id; // nao vai guardar s en for nenhum destes valores

		Sensor ns = sensorRepository.save(newsensor);
		r.addSensor(ns);
		roomRepository.save(r);
		return "redirect:/dashboard";
	}

	@PostMapping("/saveUpdatedRoom/{id}") // no update -> aqui o id é do room mm
	public String saveUpdatedRoom(@ModelAttribute("room") Room updroom, Model model,@CurrentSecurityContext(expression="authentication.name") String username, @PathVariable long id) {
		// verificar se a room corresponde ao user
		updroom.setId(id);
		roomRepository.save(updroom);
		return "redirect:/dashboard";
	}



	
	public Building getBuildingById(long id) {
		Optional<Building> optional = buildingRepository.findById(id);
		
		Building building = null;
		
		if (optional.isPresent()) {
			building = optional.get();
		} else {
			throw new RuntimeException(" Building not found ->  " + id);
		}
		
		return building;
	}

	/*
	@PutMapping("/updateBuilding/{id}")
	public String showFormForUpdate(@PathVariable ( value = "id") long id, Model model, @CurrentSecurityContext(expression="authentication.name") String username, @RequestBody Building newbuilding) {
		Building building = getBuildingById(id);
		model.addAttribute("building", building);
		
		//user authenticated
		User user = userRepository.findByUsername(username);
		
		//save building
		newbuilding = buildingRepository.save(newbuilding);
		
		//add new building to user
		user.addBuilding(newbuilding);
		
		//save user in building table
        user = userRepository.save(user); // faz update!

		newbuilding.addUser(user);
		//save new building
		buildingRepository.save(building);


		List<Building> listBuildings = buildingRepository.findAll();
		
		model.addAttribute("listBuildings", listBuildings);
		
		return "updateBuilding";
	}
	*/


	/*
	@PostMapping("/api/buildings/{id}/users")
    public List<EntityModel<Map<String, Object>>> addUserToBuilding(
        @CurrentSecurityContext(expression="authentication.name") String username, 
        @PathVariable Long id, 
        @RequestBody Map<String, Object> rec ) {
            Building b = buildrep.getOne(id);

            User u = null;
            if (rec.keySet().contains("id")) {
                u = userrep.getOne((Long) rec.get("id"));
            } else if (rec.keySet().contains("username")) {
                u = userrep.findByUsername((String) rec.get("username"));
            }

            if (u == null && b == null) return null; // TODO meter um erro qq

            u.addBuilding(b);
            b.addUser(userrep.save(u));
            b = buildrep.save(b);

            List<EntityModel<Map<String, Object>>> l = new ArrayList<EntityModel<Map<String, Object>>>();
            for (User us : b.getUsers()) {
                l.add(UserRestController.getUserEntityModel(us));
            }
            return l;
		}
		
		*/
	


	public static int numcolumnsCo2 = 0;
	@GetMapping("/roomStatsSearchMoreCO2Results/{id}")
	public String numColumnsCo2(@PathVariable (value = "id") long id, String keyword) {
			try {
				numcolumnsCo2 = Integer.parseInt(keyword);
			}
			catch (NumberFormatException e)
			{
				numcolumnsCo2 =0;
			}
	return "redirect:/roomStatsBuildingCO2/{id}";		
	}







	//SEARCH ROOMS BY DATE
	@GetMapping("/roomsStatsSearchByDateBuilding/{id}")
	public String roomsStatsSearchByDateBuilding( @PathVariable (value = "id") long id, Model model, 
	@CurrentSecurityContext(expression="authentication.name") String username,  
	@RequestParam(required = false) String dateInit, 
	@RequestParam(required = false) String dateEnd) throws ParseException {

			System.out.println( "DATE1     " + dateInit + "     DATE2             " + dateEnd );	

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
				model.addAttribute("graphDataCO2_pieBCByDates", resultMapCO2_pieBCByDates);
				break;
				
								

			}

			model.addAttribute("roomsInfoCO2ByDates", graphXCO2_date);
			model.addAttribute("alert_CO2", alerts);


			System.out.println("\n\n\n\nSENSORES  -> "  + graphXCO2_date + "alerts -> " + alerts);
		}

		return "CO2_Rooms_Building_Dates";		

	}

	/*
	
	//pesquisa de graficos por um quarto em especifico
	@GetMapping("/searchGraphicalInfoRoom/{id}")
	public String search_graphicalInfoRoom( @PathVariable (value = "id") long id, Model model, 
	@CurrentSecurityContext(expression="authentication.name") String username,  
	@RequestParam(required = false) String dateInit, 
	@RequestParam(required = false) String dateEnd) throws ParseException {


		System.out.println( "DATE1     " + dateInit + "     DATE2             " + dateEnd );	

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
			model.addAttribute("graphDataCO2Room_id", id);//DELbuildingName



			System.out.println("\n\n\n\nSENSORES to o ROOM  -> "  + graphXCO2_date + "alerts -> " + alerts);
		}

		return "roomsCO2graphicalStats";		
	

	}
	
	*/










	//GRAPHS ########################################################################################################


	//Para os graficos do graphStatsCO2.html
	@GetMapping("/roomStatsBuildingCO2/{id}")
	public String showRoomsBuildingCo2(@PathVariable (value = "id") long id,  Model model, @CurrentSecurityContext(expression="authentication.name") String username) {
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

		//for average
		ArrayList<Long> allDates_avg = new ArrayList<>(); //DELETE
		ArrayList<Integer> co2Values = new ArrayList<>(); //DELETE

		//alerts list
		ArrayList<String> alerts = new ArrayList<>();
		
        for (SensorData sd : allSensorsData) {
			Co2 co2Object= null;
			allDates_avg.add(sd.getTimestamp().getTime()); //DELETE

			


            try{
                co2Object= co2Repository.findById(sd.getId()).get();
            }catch(Exception e){
                continue;
			}

			co2Values.add((int)co2Object.getValue() );



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
				//average
				int sum = 0;
				for (String key: graphDataX.keySet())
					sum += graphDataX.get(key);
				
				int media = 0;
				media = sum / graphDataX.size() ;
				
				model.addAttribute("co2_average", media);//DEL
				//------------



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

		Collections.sort(allDates_avg);

		//average dates
		System.out.println("allDates _D  "+ allDates_avg);
		/*
		Map<String, Double> map = new HashMap<>();
		int a = 0;
		for(int i =0; i< allDates.size(); i++) {
			//1h = 3600000ms
			double d = allDates.get(i)-allDates.get(a);
			System.out.println("DATESS _D SUBTRACTION  "+d);
			
			if(allDates.get(i)-allDates.get(a)>=3600000) {
				double d1 = allDates.get(i)-allDates.get(a);

				System.out.println("DATESS TRUEEE SUBTRACTION  "+d1);


				double sumValues = 0;
				for(int y =a; y<=i;y++) {
					sumValues+=allDates.get(y);	
				}

				double media = sumValues/(double)(allDates.get(i)-allDates.get(a));

				Date dateEnd = new Date(allDates.get(i));
				DateFormat dE = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

				Date dateInit = new Date(allDates.get(i));
				DateFormat dI = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

				
				map.put(dE.format(dateEnd) + "-" +dI.format(dateInit), media);
				a = i;
				
			}
		}
		System.out.println("DATESS _D  "+ map);
		*/

	 //PROBLEMA COMO SORT

		Map<String, Double> map = new HashMap<>();
		int a = 0;
		System.out.println("CO2 list size "+co2Values.size() + " mapListtSize " + allDates_avg.size());

		for(int i =0; i< allDates_avg.size(); i++) {
			//1h = 3600000ms
			if(allDates_avg.get(i)-allDates_avg.get(a)>=3600000) {
				double d1 = allDates_avg.get(i)-allDates_avg.get(a);
				System.out.println("DATESS TRUEEE SUBTRACTION  "+d1);


				double sumValues = 0;
				for(int y =a; y<=i;y++) {
					sumValues+=co2Values.get(y);	
				}

				double media = sumValues/(double)(co2Values.get(i)-co2Values.get(a));


				Date dateEnd = new Date(allDates_avg.get(i));
				DateFormat dE = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

				Date dateInit = new Date(allDates_avg.get(i));
				DateFormat dI = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

				
				map.put(dE.format(dateEnd) + "-" +dI.format(dateInit), media);
				a = i;
				
			}
		}
		System.out.println("DATESS _D  "+ map);




		
		model.addAttribute("graphDataCO2BC", graphDataX);//DEL
		model.addAttribute("alertCO2", alerts);


		model.addAttribute("graphDataCO2BC_id", id);//DELbuildingName
		return "CO2_MoreResults_allRooms";
	}


	













	



	//#################################################PEOPLE_COUNTER###############################################################


	public static int numcolumnsPC = 0;
	@GetMapping("/roomStatsSearchMorePCResults/{id}")
	public String num(@PathVariable (value = "id") long id, String keyword) {
			try {
				numcolumnsPC = Integer.parseInt(keyword);
			}
			catch (NumberFormatException e)
			{
				numcolumnsPC =0;
			}
	return "redirect:/roomStatsBuildingPC/{id}";		
	}





	//Para o graphStatsPeopleCounter
	@GetMapping("/roomStatsBuildingPC/{id}")
	public String showRoomsBuildingPC(@PathVariable (value = "id") long id,  Model model, @CurrentSecurityContext(expression="authentication.name") String username) {
		System.out.println("Room ->>>>>>> ID " + id);
		
		User ux = userRepository.findByUsername(username);
        Set<Building> buildings =  ux.getBuildings();

		Set<Room> allRooms = new HashSet<>();
		String buildingName = "";
        for(Building b : buildings){
			buildingName = b.getBuildingName();

			if(b.getId() == id){
				Set<Room> rooms = b.getRooms();
				allRooms.addAll(rooms);

			}
		
		}
		model.addAttribute("graphDataPC_buildingName", buildingName);

		//model.addAttribute("showRoomsbyBuilging", allRooms);



        Set<SensorData> allSensorsData = new HashSet<>();
    
		ArrayList<Room> roomsInfo = new ArrayList<>();

		Set<Room> rooms = allRooms;
		if(rooms.size() != 0){
			for(Room r : rooms){
				roomsInfo.add(r);
				
				Set<Sensor> sensors = r.getSensors();

				if(sensors.size() != 0){
					for(Sensor s : sensors){
						System.out.println("TYEP  " + s.getType());
	
						if(s.getType().equals("PEOPLE_COUNTER")){
							allSensorsData.addAll(s.getSensorsData());
					}
				}
			}
		}
        }

		ArrayList<String> alerts = new ArrayList<>();

        Map<String, Integer> graphDataX = new TreeMap<>();
        for (SensorData sd : allSensorsData) {
            PeopleCounter pcObject= null;

            try{
                pcObject= pcRepository.findById(sd.getId()).get();
            }catch(Exception e){
                continue;
            }

            if(sd.getWarn()){
                String formattedDate = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss").format(sd.getTimestamp());
				long roomId = sd.getSensor().getRoom().getRoom_number();

				String y = formattedDate +"/"+ roomId +"/"+ "W" ;
			
				graphDataX.put(y, (int)pcObject.getValue());
				
				String alert = "Alert: Room " + roomId + " with too many people inside at " +  formattedDate;
				alerts.add(alert);
            }else{
				String formattedDate = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss").format(sd.getTimestamp());
				long roomId = sd.getSensor().getRoom().getRoom_number();
                String y = formattedDate +"/" + roomId;

                graphDataX.put(y, (int)pcObject.getValue());  
            }
            System.out.println("  SIZZE MAP ------------------------" + graphDataX.size() );
			int x;
			if(numcolumnsPC == 0 ){
				x =10;
			}else{
				x = numcolumnsPC;
			}

            if(graphDataX.size() == x){
                System.out.println(graphDataX + "  FINAL");
                model.addAttribute("graphDataPC", graphDataX);

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
				model.addAttribute("graphDataPC_pie", resultMap);

                break;
            }

		}
		model.addAttribute("graphDataPC_id", id);//DELbuildingName
		model.addAttribute("alertPC", alerts);
		model.addAttribute("roomsInfoPC", roomsInfo);



		return "graphStatsPeopleCounter";
	}







	public static int numcolumnsBT = 0;
	@GetMapping("/roomStatsSearchMoreBTResults/{id}")
	public String numColumnsBT(@PathVariable (value = "id") long id, String keyword) {
			try {
				numcolumnsBT = Integer.parseInt(keyword);
			}
			catch (NumberFormatException e)
			{
				numcolumnsBT =0;
			}
	return "redirect:/roomStatsBuildingBT/{id}";		
	}


	//Para o graphStatsBodyTemperature
	@GetMapping("/roomStatsBuildingBT/{id}")
	public String showRoomsBuildingBT(@PathVariable (value = "id") long id,  Model model, @CurrentSecurityContext(expression="authentication.name") String username) {
		System.out.println("Building ID " + id);
		
		User ux = userRepository.findByUsername(username);
        Set<Building> buildings =  ux.getBuildings();

		Set<Room> allRooms = new HashSet<>();
		String buildingName = "";
        for(Building b : buildings){
			buildingName = b.getBuildingName();

			if(b.getId() == id){
				Set<Room> rooms = b.getRooms();
				allRooms.addAll(rooms);

			}
		
		}
		model.addAttribute("graphDataBT_buildingName", buildingName);

		//model.addAttribute("showRoomsbyBuilging", allRooms);



        Set<SensorData> allSensorsData = new HashSet<>();
    
		ArrayList<Room> roomsInfo = new ArrayList<>();

		Set<Room> rooms = allRooms;
		if(rooms.size() != 0){
			for(Room r : rooms){
				roomsInfo.add(r);
				
				Set<Sensor> sensors = r.getSensors();

				if(sensors.size() != 0){
					for(Sensor s : sensors){
						System.out.println("TYEP  " + s.getType());
	
						if(s.getType().equals("BODY_TEMPERATURE")){
							allSensorsData.addAll(s.getSensorsData());
					}
				}
			}
		}
        }

		ArrayList<String> alerts = new ArrayList<>();

        Map<String, Integer> graphDataX = new TreeMap<>();
        for (SensorData sd : allSensorsData) {
            BodyTemperature co2Object= null;

            try{
                co2Object= btRepository.findById(sd.getId()).get();
            }catch(Exception e){
                continue;
            }

            if(sd.getWarn()){
                String formattedDate = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss").format(sd.getTimestamp());
				long roomId = sd.getSensor().getRoom().getRoom_number();

				String y = formattedDate +"/"+ roomId +"/"+ "W" ;
				graphDataX.put(y, (int)co2Object.getValue());

				String alert = "Alert: Room " + roomId + " with people with high temperature at " +  formattedDate;
				alerts.add(alert);
            }else{
				String formattedDate = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss").format(sd.getTimestamp());
				long roomId = sd.getSensor().getRoom().getRoom_number();
                String y = formattedDate +"/" + roomId;

                graphDataX.put(y, (int)co2Object.getValue());  
            }
            System.out.println("  SIZZE MAP ------------------------" + graphDataX.size() );

			int x;
			if(numcolumnsBT == 0 ){
				x =10;
			}else{
				x = numcolumnsBT;
			}
            if(graphDataX.size() == x){
                System.out.println(graphDataX + "  FINAL");//DEL
                model.addAttribute("graphDataBT", graphDataX);//DEL

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
                model.addAttribute("graphDataBT_pie", resultMap);
                break;
            }

		}
		model.addAttribute("graphDataBT_id", id);//DELbuildingName
		model.addAttribute("alertBT", alerts);
		model.addAttribute("roomsInfoBT", roomsInfo);



		return "graphStatsBT";
	}

	

	


	





	






	@GetMapping("/deleteRoom/{id}")
	public String deleteRoom(@PathVariable(value = "id") long id, Model model, @CurrentSecurityContext(expression="authentication.name") String username) {
		Room r = roomRepository.getOne(id);
        for (Sensor s : r.getSensors()) {
            sensorDataRepository.deleteAll(s.getSensorsData());
        }
        sensorRepository.deleteAll(r.getSensors());
		roomRepository.delete(r);
		return "redirect:/dashboard";
	}

	@GetMapping("/deleteSensor/{id}")
	public String deleteSensor(@PathVariable(value="id") long id, Model model, @CurrentSecurityContext(expression="authentication.name") String username) {
		Sensor s;
		try {
			s = sensorRepository.getOne(id);
		} catch (Exception e ) { s = null; }
		
		if (s != null) {
			s.getRoom().getSensors().remove(s);
			sensorDataRepository.deleteAll(s.getSensorsData());
			sensorRepository.delete(s);
		}

		return "redirect:/dashboard";
	}
    
	
	@GetMapping("/deleteBuilding/{id}")
	public String deleteBuilding(@PathVariable (value = "id") long id,  Model model, @CurrentSecurityContext(expression="authentication.name") String username) {
		Building b = getBuildingById(id);

		// tirar o building de todos os users q o tem
		for (User u : b.getUsers()) {
			u.getBuildings().remove(b);
			userRepository.save(u);
		}

		for (Room r : b.getRooms()) {
            for (Sensor s : r.getSensors()) {
				// apagar todos os dados dos sensores
                sensorDataRepository.deleteAll(s.getSensorsData());
			}
			// e tambem todos os sensores associados...
            sensorRepository.deleteAll(r.getSensors());
		}
		
		// eliminar todas as salas relacionadas c ele
		roomRepository.deleteAll(b.getRooms());

		this.buildingRepository.deleteById(id);
	
		return "redirect:/dashboard";
	}


	//----------- QUERIES--------------------



	
}