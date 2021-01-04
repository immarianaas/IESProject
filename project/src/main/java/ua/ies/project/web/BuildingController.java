package ua.ies.project.web;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
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
	








	//GRAPHS ########################################################################################################

	//Para o graphStatsCO2
	@GetMapping("/roomStatsBuildingCO2/{id}")
	public String showRoomsBuildingCo2(@PathVariable (value = "id") long id,  Model model, @CurrentSecurityContext(expression="authentication.name") String username) {
		System.out.println("Building ID " + id);
		
		User ux = userRepository.findByUsername(username);
        Set<Building> buildings =  ux.getBuildings();

		Set<Room> allRooms = new HashSet<>();
   
        for(Building b : buildings){
			if(b.getId() == id){
				Set<Room> rooms = b.getRooms();
				allRooms.addAll(rooms);

			}
		
		}
		//model.addAttribute("showRoomsbyBuilging", allRooms);



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
        }


        Map<String, Integer> graphDataX = new TreeMap<>();
        for (SensorData sd : allSensorsData) {
            Co2 co2Object= null;

            try{
                co2Object= co2Repository.findById(sd.getId()).get();
            }catch(Exception e){
                continue;
            }

            if(sd.getWarn()){
                String formattedDate = new SimpleDateFormat("MM-dd-yyyy HH:mm").format(sd.getTimestamp());
				long roomId = sd.getSensor().getRoom().getId();
                String y = formattedDate + roomId + "W" ;
                graphDataX.put(y, (int)co2Object.getValue());
            }else{
				String formattedDate = new SimpleDateFormat("MM-dd-yyyy HH:mm").format(sd.getTimestamp());
				long roomId = sd.getSensor().getRoom().getId();
                String y = formattedDate +  roomId;

                graphDataX.put(y, (int)co2Object.getValue());  
            }
            System.out.println("  SIZZE MAP ------------------------" + graphDataX.size() );

            if(graphDataX.size() == 10){
                System.out.println(graphDataX + "  FINAL");//DEL
                model.addAttribute("graphDataCO2BC", graphDataX);//DEL

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
		return "graphStatsCO2";
	}




	//Para o graphStatsPeopleCounter
	@GetMapping("/roomStatsBuildingPC/{id}")
	public String showRoomsBuildingPC(@PathVariable (value = "id") long id,  Model model, @CurrentSecurityContext(expression="authentication.name") String username) {
		System.out.println("Building ID " + id);
		
		User ux = userRepository.findByUsername(username);
        Set<Building> buildings =  ux.getBuildings();

		Set<Room> allRooms = new HashSet<>();
   
        for(Building b : buildings){
			if(b.getId() == id){
				Set<Room> rooms = b.getRooms();
				allRooms.addAll(rooms);

			}
		
		}
		//model.addAttribute("showRoomsbyBuilging", allRooms);



        Set<SensorData> allSensorsData = new HashSet<>();
    

		Set<Room> rooms = allRooms;
		if(rooms.size() != 0){
			for(Room r : rooms){
				
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


        Map<String, Integer> graphDataX = new TreeMap<>();
        for (SensorData sd : allSensorsData) {
            PeopleCounter pcObject= null;

            try{
                pcObject= pcRepository.findById(sd.getId()).get();
            }catch(Exception e){
                continue;
            }

            if(sd.getWarn()){
                String formattedDate = new SimpleDateFormat("MM-dd-yyyy HH:mm").format(sd.getTimestamp());
				long roomId = sd.getSensor().getRoom().getId();
                String y = formattedDate + roomId + "W" ;
                graphDataX.put(y, (int)pcObject.getValue());
            }else{
				String formattedDate = new SimpleDateFormat("MM-dd-yyyy HH:mm").format(sd.getTimestamp());
				long roomId = sd.getSensor().getRoom().getId();
                String y = formattedDate +  roomId;

                graphDataX.put(y, (int)pcObject.getValue());  
            }
            System.out.println("  SIZZE MAP ------------------------" + graphDataX.size() );

            if(graphDataX.size() == 10){
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
		return "graphStatsPeopleCounter";
	}




	//Para o graphStatsBodyTemperature
	@GetMapping("/roomStatsBuildingBT/{id}")
	public String showRoomsBuildingBT(@PathVariable (value = "id") long id,  Model model, @CurrentSecurityContext(expression="authentication.name") String username) {
		System.out.println("Building ID " + id);
		
		User ux = userRepository.findByUsername(username);
        Set<Building> buildings =  ux.getBuildings();

		Set<Room> allRooms = new HashSet<>();
   
        for(Building b : buildings){
			if(b.getId() == id){
				Set<Room> rooms = b.getRooms();
				allRooms.addAll(rooms);

			}
		
		}
		//model.addAttribute("showRoomsbyBuilging", allRooms);



        Set<SensorData> allSensorsData = new HashSet<>();
    

		Set<Room> rooms = allRooms;
		if(rooms.size() != 0){
			for(Room r : rooms){
				
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


        Map<String, Integer> graphDataX = new TreeMap<>();
        for (SensorData sd : allSensorsData) {
            BodyTemperature co2Object= null;

            try{
                co2Object= btRepository.findById(sd.getId()).get();
            }catch(Exception e){
                continue;
            }

            if(sd.getWarn()){
                String formattedDate = new SimpleDateFormat("MM-dd-yyyy HH:mm").format(sd.getTimestamp());
				long roomId = sd.getSensor().getRoom().getId();
                String y = formattedDate + roomId + "W" ;
                graphDataX.put(y, (int)co2Object.getValue());
            }else{
				String formattedDate = new SimpleDateFormat("MM-dd-yyyy HH:mm").format(sd.getTimestamp());
				long roomId = sd.getSensor().getRoom().getId();
                String y = formattedDate +  roomId;

                graphDataX.put(y, (int)co2Object.getValue());  
            }
            System.out.println("  SIZZE MAP ------------------------" + graphDataX.size() );

            if(graphDataX.size() == 10){
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