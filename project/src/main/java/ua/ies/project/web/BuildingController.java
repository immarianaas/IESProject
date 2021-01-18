package ua.ies.project.web;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
import java.util.Map.Entry;

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
	public String shownewSensorForm(@PathVariable ( value = "id") long id, Model model, @CurrentSecurityContext(expression="authentication.name") String username) {
		Room r = roomRepository.getOne(id);
		Sensor s = new Sensor();
		model.addAttribute("room", r);
		model.addAttribute("sensor", s);
		return "newSensor";
	}

	@GetMapping("/addUserToBuilding/{id}")
	public String addUserToBuilding(@PathVariable(value="id") long id, Model model, @CurrentSecurityContext(expression="authentication.name") String username) {
		Building b = buildingRepository.getOne(id);
		model.addAttribute("building", b);
		model.addAttribute("error", "");
		return "add_user";  // TODO PAGE
	}

	@PostMapping("/addUserToBuildingPost/{id}")
	public String addUserToBuildingPost(@PathVariable(value="id") long id, Model model, @CurrentSecurityContext(expression="authentication.name") String username, @RequestBody String user) {
		Building b = buildingRepository.getOne(id);
		User u = userRepository.findByUsername(user.replace("username=", ""));
		model.addAttribute("building", b);
		
		System.out.println("\n\n\nuser received: " + user.replace("username=", "") + "\n\n\n\n");
		if (u == null) {
			model.addAttribute("error", "There is no user with that username.");
			return "add_user";
		}
		model.addAttribute("error", "");
		u.addBuilding(b);
		b.addUser(u);
		userRepository.save(u);
		buildingRepository.save(b);
		return "add_user";
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

	
	

//-------------------------------------------__AIR__QUALITY---------------------------------------------------------------------------------
	@GetMapping("/roomsStatsSearchByDateBuilding/{id}")
	public String roomsStatsSearchByDateBuilding( @PathVariable (value = "id") long id, Model model, 
	@CurrentSecurityContext(expression="authentication.name") String username,  
	@RequestParam(required = false) String dateInit, 
	@RequestParam(required = false) String dateEnd) throws ParseException {
			model.addAttribute("buildingID", id);
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
			String dataFormatted1 = date01[2] + "-"+date01[0] + "-"+ date01[1] + " " + hours01[0]+":"+hours01[1]+":00.0";
			Date dateInit1= new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(dataFormatted1);

			//format data 2
			String[] allDate2 = dateEnd.split(" ");
			String[] date02 = allDate2[0].split("/"); 
			String[] hours02 = allDate2[1].split(":"); 
			String dataFormatted2 = date02[2] + "-"+date02[0] + "-"+ date02[1] + " " + hours02[0]+":"+hours02[1]+":00.0"; 

			Date dateEnd1= new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(dataFormatted2);

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

				//average -> times
				double sum = 0;
				for (String key: graphXCO2_date.keySet())
					sum += graphXCO2_date.get(key);
				
				double media = 0;
				media = sum / graphXCO2_date.size() ;
				model.addAttribute("co2_average_byDates", media);


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
				model.addAttribute("graphPieCO2Building_byDates", resultMapCO2_pieBCByDates);
				break;
			}

			model.addAttribute("graph_Bars_CO2Building_byDates", graphXCO2_date);
			model.addAttribute("alerts_CO2Building_byDates", alerts);


		}

		return "CO2_Building_RoomByDates";		
		//CO2_Info_byRoom
	}
	

//-------------------------------------------------------

	public static int numcolumns = 10;
	public static int timesAverage = 900000;
	//CO2_Building.html
	@GetMapping("/roomsStatsCO2Building/{id}")
	public String showRoomsBuildingCo2(@PathVariable (value = "id") long id,  Model model, @CurrentSecurityContext(expression="authentication.name") String username,
	@RequestParam(required = false) String numColms,
	@RequestParam(required = false) String timesAvg ) {	
		model.addAttribute("buildingID", id);
	
		
		try {
			numcolumns = Integer.parseInt(numColms);
		}
		catch (NumberFormatException e)
		{		}

		try {
			timesAverage = Integer.parseInt(timesAvg)*60000;
			
		}
		catch (NumberFormatException e)
		{
		
		}

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
		Map<Long, Integer> dates_and_co2Values = new TreeMap<>();
		//alerts list
		ArrayList<String> alerts = new ArrayList<>();
        for (SensorData sd : allSensorsData) {
			Co2 co2Object= null;
            try{
                co2Object= co2Repository.findById(sd.getId()).get();
            }catch(Exception e){
                continue;
			}
			dates_and_co2Values.put(sd.getTimestamp().getTime(),(int)co2Object.getValue());
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
            System.out.println("  SIZE MAP ---" + graphDataX.size() );
			
			
            if(graphDataX.size() == numcolumns){
				//average
				double sum = 0;
				for (String key: graphDataX.keySet())
					sum += graphDataX.get(key);
				
				double media = 0;
				media = sum / graphDataX.size() ;
				
				model.addAttribute("co2_average", media);
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
                model.addAttribute("graphPieCO2Building", resultMap);
                break;
            }
		}
		//AVERAGE DATES
	 	ArrayList<Long> allDates = new ArrayList<>();
		ArrayList<Integer> co2Values = new ArrayList<>(); 
	 	for(Entry<Long, Integer> entry:dates_and_co2Values.entrySet()) {
			allDates.add(entry.getKey());
			co2Values.add(entry.getValue());
		}
		Map<String, Double> dates_averageValues = new HashMap<>();
		int a = 0;
		System.out.println("CO2 list size "+co2Values.size() + " mapListtSize " + allDates.size());

	
		for(int i =0; i< allDates.size(); i++) {
			//ESTÁ PARA 15 min

			if(allDates.get(i)-allDates.get(a)>=timesAverage) {
				double d1 = allDates.get(i)-allDates.get(a);
				double sumValues = 0;
				int cont =0;
				for(int y =a; y<=i;y++) {
					sumValues+=co2Values.get(y);
					cont++;	
				}
				double media = sumValues/cont;
				//date format
				Date dateEnd = new Date(allDates.get(i));
				DateFormat dE = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

				Date dateInit = new Date(allDates.get(a));
				DateFormat dI = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
				dates_averageValues.put(dE.format(dateInit) + "   ---   " +dI.format(dateEnd), media);
				a = i;
				sumValues=0;
				media=0;
			}
		}
		model.addAttribute("datesCO2_AvgByRange", dates_averageValues);
		model.addAttribute("graph_Bars_CO2Building", graphDataX);
		model.addAttribute("alerts_CO2Building", alerts);
		model.addAttribute("buildingCO2_id", id);
		return "CO2_Building";
	}


	//#################################################PEOPLE_COUNTER###############################################################
	//SEARCH ROOMS BY DATE; CO2_Building_RoomByDates
	@GetMapping("/roomsStatsSearchByDatePCBuilding/{id}")
	public String roomsStatsSearchByDateBuildingPC( @PathVariable (value = "id") long id, Model model, 
	@CurrentSecurityContext(expression="authentication.name") String username,  
	@RequestParam(required = false) String dateInit, 
	@RequestParam(required = false) String dateEnd) throws ParseException {
			model.addAttribute("buildingID", id);

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
							if(s.getType().equals("PEOPLE_COUNTER")){
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
			String dataFormatted2 = date02[2] + "-"+date02[0] + "-"+ date02[1] + " " + hours02[0]+":"+hours02[1]+":00.0"; 

			Date dateEnd1= new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(dataFormatted2);

			PeopleCounter pcObject= null;

			ArrayList<String> alerts = new ArrayList<>();

			Map<String, Integer> graphXPC_date = new TreeMap<>();


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
					pcObject= pcRepository.findById(sd.getId()).get();
				}catch(Exception e){
					continue;
				}

				if(sd.getWarn()){
					String formattedDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(sd.getTimestamp());
					long roomId = sd.getSensor().getRoom().getRoom_number();
	
					String y = formattedDate +"/"+ roomId +"/"+ "W" ;
					graphXPC_date.put(y, (int)pcObject.getValue());
					
	
					String alert = "Alert: Room " + roomId + " total people inside not allowed " +  formattedDate;
					alerts.add(alert);
				
				}else{
					String formattedDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(sd.getTimestamp());
					long roomId = sd.getSensor().getRoom().getRoom_number();
					String y = formattedDate +"/" + roomId;
	
					graphXPC_date.put(y, (int)pcObject.getValue());  
				}

				//average -> times
				double sum = 0;
				for (String key: graphXPC_date.keySet())
					sum += graphXPC_date.get(key);
				
				double media = 0;
				media = sum / graphXPC_date.size() ;
				model.addAttribute("pc_average_byDates", media);


				//pie graph
				Map<Integer, Integer> resultMapPC_pieBCByDates = new TreeMap<>();

				for (String key : graphXPC_date.keySet()) {
					Integer value = graphXPC_date.get(key);
					
					if (resultMapPC_pieBCByDates.containsKey(value)) {
						resultMapPC_pieBCByDates.put(value, resultMapPC_pieBCByDates.get(value) + 1);
					} else {
						resultMapPC_pieBCByDates.put(value, 1);
					}
				}
				model.addAttribute("graphPiePCBuilding_byDates", resultMapPC_pieBCByDates);
				break;
			}

			model.addAttribute("graph_Bars_PCBuilding_byDates", graphXPC_date);
			model.addAttribute("alerts_PCBuilding_byDates", alerts);


		}

		return "PeopleCounter_Building_RoomByDates";		
		//CO2_Info_byRoom
	}
//-------------------------------------------------------

	public static int numcolumnspc = 10;
	public static int timesAveragepc = 900000;
	//CO2_Building.html
	@GetMapping("/roomsStatsPeopleCounterBuilding/{id}")
	public String showRoomsBuildingPeopleCounter(@PathVariable (value = "id") long id,  Model model, @CurrentSecurityContext(expression="authentication.name") String username,
	@RequestParam(required = false) String numResults,
	@RequestParam(required = false) String timesAvg ) {	
		model.addAttribute("buildingIDPC", id);

		
		try {
			numcolumnspc = Integer.parseInt(numResults);
		}
		catch (NumberFormatException e)
		{		}

		try {
			timesAveragepc = Integer.parseInt(timesAvg)*60000;
			
		}
		catch (NumberFormatException e)
		{
		
		}

		

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
		model.addAttribute("graphDataPC_buildingName", buildingName);
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
	
						if(s.getType().equals("PEOPLE_COUNTER")){
							allSensorsData.addAll(s.getSensorsData());
					}
				}
			}
		}

		model.addAttribute("roomsInfoPC", roomsInfo);
		}
		
		//bar chart and pie chart
		Map<String, Integer> graphDataX = new TreeMap<>();
		//for average
		Map<Long, Integer> dates_and_co2Values = new TreeMap<>();
		
		//alerts list
		ArrayList<String> alerts = new ArrayList<>();
		
        for (SensorData sd : allSensorsData) {
			PeopleCounter pcObject= null;
            try{
                pcObject= pcRepository.findById(sd.getId()).get();
            }catch(Exception e){
                continue;
			}
			dates_and_co2Values.put(sd.getTimestamp().getTime(),(int)pcObject.getValue());
            if(sd.getWarn()){
                String formattedDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(sd.getTimestamp());
				long roomId = sd.getSensor().getRoom().getRoom_number();
                String y = formattedDate +"/"+ roomId +"/"+ "W" ;
				graphDataX.put(y, (int)pcObject.getValue());
				String alert = "Alert: Room " + roomId + "  people inside not allowed at " +  formattedDate;
				alerts.add(alert);
			}else{
				String formattedDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(sd.getTimestamp());
				long roomId = sd.getSensor().getRoom().getRoom_number();
                String y = formattedDate +"/" + roomId;
                graphDataX.put(y, (int)pcObject.getValue());  
            }
            System.out.println("  SIZE MAP ---" + graphDataX.size() );
		
            if(graphDataX.size() == numcolumnspc){
				//average
				double sum = 0;
				for (String key: graphDataX.keySet())
					sum += graphDataX.get(key);
				
				double media = 0;
				media = sum / graphDataX.size() ;
				
				model.addAttribute("pc_average", media);
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
                model.addAttribute("graphPiePCBuilding", resultMap);
                break;
            }
		}
		//AVERAGE DATES
	 	ArrayList<Long> allDates = new ArrayList<>();
		ArrayList<Integer> co2Values = new ArrayList<>(); 
	 	for(Entry<Long, Integer> entry:dates_and_co2Values.entrySet()) {
			allDates.add(entry.getKey());
			co2Values.add(entry.getValue());
		}
		Map<String, Double> dates_averageValues = new HashMap<>();
		int a = 0;
		System.out.println("PC list size "+co2Values.size() + " mapListtSize " + allDates.size());
		for(int i =0; i< allDates.size(); i++) {
			//ESTÁ PARA 15 min
			
			if(allDates.get(i)-allDates.get(a)>=timesAveragepc) {
				double d1 = allDates.get(i)-allDates.get(a);
				System.out.println("DATESS TRUEEE SUBTRACTION  "+d1);
				double sumValues = 0;
				int cont =0;
				for(int y =a; y<=i;y++) {
					sumValues+=co2Values.get(y);
					cont++;	
				}
				double media = sumValues/cont;
				//date format
				Date dateEnd = new Date(allDates.get(i));
				DateFormat dE = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

				Date dateInit = new Date(allDates.get(a));
				DateFormat dI = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
				dates_averageValues.put(dE.format(dateInit) + "   ---   " +dI.format(dateEnd), media);
				a = i;
				sumValues=0;
				media=0;
			}
		}
		model.addAttribute("datesPC_AvgByRange", dates_averageValues);
		model.addAttribute("graph_Bars_PCBuilding", graphDataX);
		model.addAttribute("alerts_PCBuilding", alerts);
		model.addAttribute("buildingPC_id", id);
		return "PeopleCounter_Building";
	}


	//#################################################TEMPERATURES CONTROL###############################################################

	//SEARCH ROOMS BY DATE; CO2_Building_RoomByDates
	@GetMapping("/roomsStatsSearchByDateBodyTemperaturesControlBuilding/{id}")
	public String roomsStatsSearchByDateBuildingTC( @PathVariable (value = "id") long id, Model model, 
	@CurrentSecurityContext(expression="authentication.name") String username,  
	@RequestParam(required = false) String dateInit, 
	@RequestParam(required = false) String dateEnd) throws ParseException {
			model.addAttribute("buildingID", id);

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
							if(s.getType().equals("BODY_TEMPERATURE")){
								allSensorsData.addAll(s.getSensorsData());
						}
					}
				}
			}
			//format data 1
			String[] allDate1 = dateInit.split(" "); 
			String[] date01 = allDate1[0].split("/"); 
			String[] hours01 = allDate1[1].split(":"); 
			String dataFormatted1 = date01[2] + "-"+date01[0] + "-"+ date01[1] + " " + hours01[0]+":"+hours01[1]+":00.0";
			Date dateInit1= new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(dataFormatted1);

			//format data 2
			String[] allDate2 = dateEnd.split(" ");
			String[] date02 = allDate2[0].split("/"); 
			String[] hours02 = allDate2[1].split(":"); 
			String dataFormatted2 = date02[2] + "-"+date02[0] + "-"+ date02[1] + " " + hours02[0]+":"+hours02[1]+":00.0"; 

			Date dateEnd1= new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(dataFormatted2);

			BodyTemperature btObject= null;

			ArrayList<String> alerts = new ArrayList<>();

			Map<String, Integer> graphX_date = new TreeMap<>();


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
					btObject= btRepository.findById(sd.getId()).get();
				}catch(Exception e){
					continue;
				}

				if(sd.getWarn()){
					String formattedDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(sd.getTimestamp());
					long roomId = sd.getSensor().getRoom().getRoom_number();
	
					String y = formattedDate +"/"+ roomId +"/"+ "W" ;
					graphX_date.put(y, (int)btObject.getValue());
					
	
					String alert = "Alert: Room " + roomId + " body temperature not allowed detected at " +  formattedDate;
					alerts.add(alert);
				
				}else{
					String formattedDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(sd.getTimestamp());
					long roomId = sd.getSensor().getRoom().getRoom_number();
					String y = formattedDate +"/" + roomId;
	
					graphX_date.put(y, (int)btObject.getValue());  
				}

				//average -> times
				double sum = 0;
				for (String key: graphX_date.keySet())
					sum += graphX_date.get(key);
				
				double media = 0;
				media = sum / graphX_date.size() ;
				model.addAttribute("BT_average_byDates", media);


				//pie graph
				Map<Integer, Integer> resultMapBT_pieByDates = new TreeMap<>();

				for (String key : graphX_date.keySet()) {
					Integer value = graphX_date.get(key);
					
					if (resultMapBT_pieByDates.containsKey(value)) {
						resultMapBT_pieByDates.put(value, resultMapBT_pieByDates.get(value) + 1);
					} else {
						resultMapBT_pieByDates.put(value, 1);
					}
				}
				model.addAttribute("graphPieBTBuilding_byDates", resultMapBT_pieByDates);
				break;
			}

			model.addAttribute("graph_Bars_BTBuilding_byDates", graphX_date);
			model.addAttribute("alerts_PCBuilding_byDates", alerts);


		}

		return "BodyTemperatures_Building_RoomByDates";		
		//CO2_Info_byRoom
	}
//-------------------------------------------------------

	public static int numcolumnsbt = 10;
	public static int timesAveragebt = 900000;
	//CO2_Building.html
	@GetMapping("/roomsStatsBodyTemperaturesControlBuilding/{id}")
	public String showRoomsBuildingBodyTemperature(@PathVariable (value = "id") long id,  Model model, @CurrentSecurityContext(expression="authentication.name") String username,
	@RequestParam(required = false) String numResults,
	@RequestParam(required = false) String timesAvg ) {	
		model.addAttribute("buildingIDBT", id);
	

		try {
			numcolumnsbt = Integer.parseInt(numResults);
		}
		catch (NumberFormatException e)
		{		}

		try {
			timesAveragebt = Integer.parseInt(timesAvg)*60000;
			
		}
		catch (NumberFormatException e)
		{
		
		}

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
		model.addAttribute("graphDataBT_buildingName", buildingName);
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
	
						if(s.getType().equals("BODY_TEMPERATURE")){
							allSensorsData.addAll(s.getSensorsData());
					}
				}
			}
		}

		model.addAttribute("roomsInfoBT", roomsInfo);
		}
		
		//bar chart and pie chart
		Map<String, Integer> graphDataX = new TreeMap<>();
		//for average
		Map<Long, Integer> dates_and_Values = new TreeMap<>();
		//alerts list
		ArrayList<String> alerts = new ArrayList<>();
		
        for (SensorData sd : allSensorsData) {
			BodyTemperature btObject= null;
            try{
                btObject= btRepository.findById(sd.getId()).get();
            }catch(Exception e){
                continue;
			}
			dates_and_Values.put(sd.getTimestamp().getTime(),(int)btObject.getValue());
            if(sd.getWarn()){
                String formattedDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(sd.getTimestamp());
				long roomId = sd.getSensor().getRoom().getRoom_number();
                String y = formattedDate +"/"+ roomId +"/"+ "W" ;
				graphDataX.put(y, (int)btObject.getValue());
				String alert = "Alert: Room " + roomId + " body temperature not allowed detected at " +  formattedDate;
				alerts.add(alert);
			}else{
				String formattedDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(sd.getTimestamp());
				long roomId = sd.getSensor().getRoom().getRoom_number();
                String y = formattedDate +"/" + roomId;
                graphDataX.put(y, (int)btObject.getValue());  
            }
            System.out.println("  SIZE MAP ---" + graphDataX.size() );
		
            if(graphDataX.size() ==numcolumnsbt){
				//average
				double sum = 0;
				for (String key: graphDataX.keySet())
					sum += graphDataX.get(key);
				
				double media = 0;
				media = sum / graphDataX.size() ;
				
				model.addAttribute("bt_average", media);
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
                model.addAttribute("graphPieBTBuilding", resultMap);
                break;
            }
		}
		//AVERAGE DATES
	 	ArrayList<Long> allDates = new ArrayList<>();
		ArrayList<Integer> co2Values = new ArrayList<>(); 
	 	for(Entry<Long, Integer> entry:dates_and_Values.entrySet()) {
			allDates.add(entry.getKey());
			co2Values.add(entry.getValue());
		}
		Map<String, Double> dates_averageValues = new HashMap<>();
		int a = 0;
		for(int i =0; i< allDates.size(); i++) {
		
			if(allDates.get(i)-allDates.get(a)>=timesAveragebt) {
				double d1 = allDates.get(i)-allDates.get(a);
				double sumValues = 0;
				int cont =0;
				for(int y =a; y<=i;y++) {
					sumValues+=co2Values.get(y);
					cont++;	
				}
				double media = sumValues/cont;
				//date format
				Date dateEnd = new Date(allDates.get(i));
				DateFormat dE = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

				Date dateInit = new Date(allDates.get(a));
				DateFormat dI = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
				dates_averageValues.put(dE.format(dateInit) + "   ---   " +dI.format(dateEnd), media);
				a = i;
				sumValues=0;
				media=0;
			}
		}
		model.addAttribute("datesBT_AvgByRange", dates_averageValues);
		model.addAttribute("graph_Bars_BTBuilding", graphDataX);
		model.addAttribute("alerts_BTBuilding", alerts);
		model.addAttribute("buildingBT_id", id);
		return "BodyTemperatures_Building";
	}






	
}