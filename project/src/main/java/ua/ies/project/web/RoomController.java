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

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import ua.ies.project.model.BodyTemperature;
import ua.ies.project.model.Co2;
import ua.ies.project.model.PeopleCounter;
import ua.ies.project.model.Room;
import ua.ies.project.model.Sensor;
import ua.ies.project.model.SensorData;
import ua.ies.project.repository.BodyTemperatureRepository;
import ua.ies.project.repository.Co2Repository;
import ua.ies.project.repository.PeopleCounterRepository;
import ua.ies.project.repository.RoomRepository;


@Controller
public class RoomController {

	@Autowired
	private BodyTemperatureRepository btRepository;
	
	
	@Autowired
	private RoomRepository roomRepository;

	@Autowired
	private Co2Repository co2Repository;

	
	@Autowired
	private PeopleCounterRepository pcRepository;

	public static int numcolumns = 10;
	public static int timesAverage = 900000;
	@GetMapping("/searchStatsAirQualityRoom/{id}")
	public String search_graphicalInfoRoomCo2( @PathVariable (value = "id") long id,  Model model, 
	@CurrentSecurityContext(expression="authentication.name") String username,
	@RequestParam(required = false) String numColms,
	@RequestParam(required = false) String timesAvgRoom){
		model.addAttribute("roomIDCo2", id);
		try {
			numcolumns = Integer.parseInt(numColms);
		}
		catch (NumberFormatException e)
		{		}

		try {
			timesAverage = Integer.parseInt(timesAvgRoom)*60000;
			
		}
		catch (NumberFormatException e)
		{
		
		}
		
		Room rx = roomRepository.findById(id).orElseThrow();
		model.addAttribute("roomCo2_id", rx.getId());

		System.out.println("ROOM ->  " +rx.getId());
		Set<Sensor> sensors = rx.getSensors();
		Set<SensorData> allSensorsData = new HashSet<>();
		if(sensors.size() != 0){
			for(Sensor s : sensors){
				System.out.println("TYEP  " + s.getType());

				if(s.getType().equals("CO2")){
					allSensorsData.addAll(s.getSensorsData());
				}
			}
		}
		//bar chart and pie chart
		Map<String, Integer> graphDataX = new TreeMap<>();
		ArrayList<String> alerts = new ArrayList<>();
		Map<Long, Integer> dates_and_co2Values = new TreeMap<>();

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
		
            if(graphDataX.size() ==numcolumns){
				//average
				double sum = 0;
				for (String key: graphDataX.keySet())
					sum += graphDataX.get(key);
				
				double media = 0;
				media = sum / graphDataX.size() ;
				
				model.addAttribute("co2_roomAvg", media);//DEL
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
                model.addAttribute("graphPieCO2Room", resultMap);
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
		model.addAttribute("dates_averageValuesCO2Room", dates_averageValues);

		model.addAttribute("graph_Bars_CO2Room", graphDataX);//DEL
		model.addAttribute("alertCO2Room", alerts);
		model.addAttribute("graphDataCO2Room_id", rx.getId());//DELbuildingName
		return "CO2_Room";		
	}


	
	//pesquisa de graficos por um quarto em especifico
	@GetMapping("/searchStatsByDateCO2Room/{id}")
	public String searchStatsByDateCO2Room( @PathVariable (value = "id") long id, Model model,  
	@RequestParam(required = false) String dateI, 
	@RequestParam(required = false) String dateE) throws ParseException {
		model.addAttribute("roomID_co2", id);

		Room rx = roomRepository.findById(id).orElseThrow();
		System.out.println("ROOM ->  " +rx.getId());

		Set<Sensor> sensors = rx.getSensors();
		Set<SensorData> allSensorsData = new HashSet<>();

		if(sensors.size() != 0){
			for(Sensor s : sensors){
				System.out.println("TYEP  " + s.getType());

				if(s.getType().equals("CO2")){
					allSensorsData.addAll(s.getSensorsData());
			}
		}


		//format data 1
		String[] allDate1 = dateI.split(" "); 
		String[] date01 = allDate1[0].split("/"); 
		String[] hours01 = allDate1[1].split(":"); 
		//2021-01-07 18:29:36
		String dataFormatted1 = date01[2] + "-"+date01[0] + "-"+ date01[1] + " " + hours01[0]+":"+hours01[1]+":00.0";
		Date dateInit1= new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(dataFormatted1);

		//format data 2
		String[] allDate2 = dateE.split(" ");
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
			System.out.println("\n\n\nDate BD  -> "  + dateBD + "  Date 1  -> " + dateI + "  Date 2  -> "+ dateEnd1);

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
			model.addAttribute("roomCO2_pie", resultMapCO2_pieBCByDates);
			break;
		}

		model.addAttribute("roomInfoCO2ByDates", graphXCO2_date);
		model.addAttribute("alertRoom_CO2", alerts);


		System.out.println("\n\n\n\nSENSORES  -> "  + graphXCO2_date + "alerts -> " + alerts);
		}
		return "CO2_Room_DatesRange";
	}

	//###########################################PEOPLE COUNTER#################################################################
	public static int numcolumnspc = 10;
	public static int timesAveragepc = 900000;
	@GetMapping("/searchStatsPeopleCounterRoom/{id}")
	public String search_graphicalInfoRoomCPC( @PathVariable (value = "id") long id,  Model model, 
	@CurrentSecurityContext(expression="authentication.name") String username,
	@RequestParam(required = false) String numColms,
	@RequestParam(required = false) String timesAvgRoom){
		model.addAttribute("roomIDPC", id);

		try {
			numcolumnspc = Integer.parseInt(numColms);
		}
		catch (NumberFormatException e)
		{		}

		try {
			timesAveragepc = Integer.parseInt(timesAvgRoom)*60000;
			
		}
		catch (NumberFormatException e)
		{
		
		}
		
		Room rx = roomRepository.findById(id).orElseThrow();
		model.addAttribute("roomPC_id", rx.getId());

		System.out.println("ROOM ->  " +rx.getId());
		Set<Sensor> sensors = rx.getSensors();
		Set<SensorData> allSensorsData = new HashSet<>();
		if(sensors.size() != 0){
			for(Sensor s : sensors){
				System.out.println("TYEP  " + s.getType());

				if(s.getType().equals("PEOPLE_COUNTER")){
					allSensorsData.addAll(s.getSensorsData());
				}
			}
		}
		//bar chart and pie chart
		Map<String, Integer> graphDataX = new TreeMap<>();
		ArrayList<String> alerts = new ArrayList<>();
		Map<Long, Integer> dates_and_pcValues = new TreeMap<>();

        for (SensorData sd : allSensorsData) {
            PeopleCounter pcObject= null;
            try{
                pcObject= pcRepository.findById(sd.getId()).get();
            }catch(Exception e){
                continue;
			}
			dates_and_pcValues.put(sd.getTimestamp().getTime(),(int)pcObject.getValue());

            if(sd.getWarn()){
                String formattedDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(sd.getTimestamp());
				long roomId = sd.getSensor().getRoom().getRoom_number();

                String y = formattedDate +"/"+ roomId +"/"+ "W" ;
				graphDataX.put(y, (int)pcObject.getValue());
				

				String alert = "Alert: Room " + roomId + " with people inside not allowed at " +  formattedDate;
				alerts.add(alert);
			
			}else{
				String formattedDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(sd.getTimestamp());
				long roomId = sd.getSensor().getRoom().getRoom_number();
                String y = formattedDate +"/" + roomId;

                graphDataX.put(y, (int)pcObject.getValue());  
			}
			
            if(graphDataX.size() == numcolumnspc){
				//average
				double sum = 0;
				for (String key: graphDataX.keySet())
					sum += graphDataX.get(key);
				
				double media = 0;
				media = sum / graphDataX.size() ;
				
				model.addAttribute("PC_roomAvg", media);//DEL
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
                model.addAttribute("graphPiePCRoom", resultMap);
                break;
            }

		}

		//AVERAGE DATES
		ArrayList<Long> allDates = new ArrayList<>();
		ArrayList<Integer> pcValues = new ArrayList<>(); 
			for(Entry<Long, Integer> entry:dates_and_pcValues.entrySet()) {
			allDates.add(entry.getKey());
			pcValues.add(entry.getValue());
		}


		Map<String, Double> dates_averageValues = new HashMap<>();
		int a = 0;
		System.out.println("PC list size "+pcValues.size() + " mapListtSize " + allDates.size());
		for(int i =0; i< allDates.size(); i++) {
			
			if(allDates.get(i)-allDates.get(a)>=timesAveragepc) {
				double d1 = allDates.get(i)-allDates.get(a);
				System.out.println("DATESS TRUEEE SUBTRACTION  "+d1);
				double sumValues = 0;
				int cont =0;
				for(int y =a; y<=i;y++) {
					sumValues+=pcValues.get(y);
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
		model.addAttribute("dates_averageValuesPCRoom", dates_averageValues);

		model.addAttribute("graph_Bars_PCRoom", graphDataX);//DEL
		model.addAttribute("alertPCRoom", alerts);
		model.addAttribute("graphDataPCRoom_id", rx.getId());//DELbuildingName
		return "PeopleCounter_Room";		
	}


	
	//pesquisa de graficos por um quarto em especifico
	@GetMapping("/searchStatsByDatePCRoom/{id}")
	public String searchStatsByDatePCRoom( @PathVariable (value = "id") long id, Model model,  
	@RequestParam(required = false) String dateI, 
	@RequestParam(required = false) String dateE) throws ParseException {
		model.addAttribute("roomID_pc", id);

		Room rx = roomRepository.findById(id).orElseThrow();
		System.out.println("ROOM ->  " +rx.getId());

		Set<Sensor> sensors = rx.getSensors();
		Set<SensorData> allSensorsData = new HashSet<>();

		if(sensors.size() != 0){
			for(Sensor s : sensors){
				if(s.getType().equals("PEOPLE_COUNTER")){
					allSensorsData.addAll(s.getSensorsData());
			}
		}


		//format data 1
		String[] allDate1 = dateI.split(" "); 
		String[] date01 = allDate1[0].split("/"); 
		String[] hours01 = allDate1[1].split(":"); 
		//2021-01-07 18:29:36
		String dataFormatted1 = date01[2] + "-"+date01[0] + "-"+ date01[1] + " " + hours01[0]+":"+hours01[1]+":00.0";
		Date dateInit1= new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(dataFormatted1);

		//format data 2
		String[] allDate2 = dateE.split(" ");
		String[] date02 = allDate2[0].split("/"); 
		String[] hours02 = allDate2[1].split(":"); 
		String dataFormatted2 = date02[2] + "-"+date02[0] + "-"+ date02[1] + " " + hours02[0]+":"+hours02[1]+":00.0"; 

		Date dateEnd1= new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(dataFormatted2);

		PeopleCounter pcObject= null;
		
		ArrayList<String> alerts = new ArrayList<>();

		Map<String, Integer> graphX_date = new TreeMap<>();


		for (SensorData sd : allSensorsData) {
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");  
			String strDate = dateFormat.format(sd.getTimestamp());  
			Date dateBD= new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(strDate);
			System.out.println("\n\n\nDate BD  -> "  + dateBD + "  Date 1  -> " + dateI + "  Date 2  -> "+ dateEnd1);

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
				graphX_date.put(y, (int)pcObject.getValue());
				

				String alert = "Alert: Room " + roomId + " with people inside not allowed at " +  formattedDate;
				alerts.add(alert);
			
			}else{
				String formattedDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(sd.getTimestamp());
				long roomId = sd.getSensor().getRoom().getRoom_number();
				String y = formattedDate +"/" + roomId;

				graphX_date.put(y, (int)pcObject.getValue());  
			}
			//pie graph
			Map<Integer, Integer> resultMap_pieBCByDates = new TreeMap<>();

			for (String key : graphX_date.keySet()) {
				Integer value = graphX_date.get(key);
				
				if (resultMap_pieBCByDates.containsKey(value)) {
					resultMap_pieBCByDates.put(value, resultMap_pieBCByDates.get(value) + 1);
				} else {
					resultMap_pieBCByDates.put(value, 1);
				}
			}
			model.addAttribute("roomPC_pie", resultMap_pieBCByDates);
			break;
		}

		model.addAttribute("roomInfoPCByDates", graphX_date);
		model.addAttribute("alertRoom_PC", alerts);


		System.out.println("\n\n\n\nSENSORES  -> "  + graphX_date + "alerts -> " + alerts);
		}
		return "PeopleCounter_Room_DatesRange";
	}

	//###########################################BODY TEMPERATURE#################################################################

	public static int numcolumnsbt = 10;
	public static int timesAveragebt = 900000;
	@GetMapping("/searchStatsBodyTempControlRoom/{id}")
	public String searchStatsBodyTempControlRoom( @PathVariable (value = "id") long id,  Model model, 
	@CurrentSecurityContext(expression="authentication.name") String username,
	@RequestParam(required = false) String numColms,
	@RequestParam(required = false) String timesAvgRoom){
		model.addAttribute("roomIDPC", id);

		try {
			numcolumnsbt = Integer.parseInt(numColms);
		}
		catch (NumberFormatException e)
		{		}

		try {
			timesAveragebt = Integer.parseInt(timesAvgRoom)*60000;
			
		}
		catch (NumberFormatException e)
		{
		
		}

		
		Room rx = roomRepository.findById(id).orElseThrow();
		model.addAttribute("roomBT_id", rx.getId());

		System.out.println("ROOM ->  " +rx.getId());
		Set<Sensor> sensors = rx.getSensors();
		Set<SensorData> allSensorsData = new HashSet<>();
		if(sensors.size() != 0){
			for(Sensor s : sensors){

				if(s.getType().equals("BODY_TEMPERATURE")){
					allSensorsData.addAll(s.getSensorsData());
				}
			}
		}
		//bar chart and pie chart
		Map<String, Integer> graphDataX = new TreeMap<>();
		ArrayList<String> alerts = new ArrayList<>();
		Map<Long, Integer> dates_and_Values = new TreeMap<>();

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
			
            if(graphDataX.size() == numcolumnsbt){
				//average
				double sum = 0;
				for (String key: graphDataX.keySet())
					sum += graphDataX.get(key);
				
				double media = 0;
				media = sum / graphDataX.size() ;
				
				model.addAttribute("BT_roomAvg", media);//DEL
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
                model.addAttribute("graphPieBTRoom", resultMap);
                break;
            }

		}

		//AVERAGE DATES
		ArrayList<Long> allDates = new ArrayList<>();
		ArrayList<Integer> pcValues = new ArrayList<>(); 
			for(Entry<Long, Integer> entry:dates_and_Values.entrySet()) {
			allDates.add(entry.getKey());
			pcValues.add(entry.getValue());
		}


		Map<String, Double> dates_averageValues = new HashMap<>();
		int a = 0;
		System.out.println("BT list size "+pcValues.size() + " mapListtSize " + allDates.size());
		for(int i =0; i< allDates.size(); i++) {
			
			if(allDates.get(i)-allDates.get(a)>=timesAveragebt) {
				double d1 = allDates.get(i)-allDates.get(a);
				System.out.println("DATESS TRUEEE SUBTRACTION  "+d1);
				double sumValues = 0;
				int cont =0;
				for(int y =a; y<=i;y++) {
					sumValues+=pcValues.get(y);
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
		model.addAttribute("dates_averageValuesBTRoom", dates_averageValues);

		model.addAttribute("graph_Bars_BTRoom", graphDataX);//DEL
		model.addAttribute("alertBTRoom", alerts);
		model.addAttribute("graphDataBTRoom_id", rx.getId());//DELbuildingName
		return "BodyTemperature_Room";		
	}


	
	//pesquisa de graficos por um quarto em especifico
	@GetMapping("/searchStatsByDateBTRoom/{id}")
	public String searchStatsByDateBTRoom( @PathVariable (value = "id") long id, Model model,  
	@RequestParam(required = false) String dateI, 
	@RequestParam(required = false) String dateE) throws ParseException {
		model.addAttribute("roomID_bt", id);

		Room rx = roomRepository.findById(id).orElseThrow();
		System.out.println("ROOM ->  " +rx.getId());

		Set<Sensor> sensors = rx.getSensors();
		Set<SensorData> allSensorsData = new HashSet<>();

		if(sensors.size() != 0){
			for(Sensor s : sensors){
				if(s.getType().equals("BODY_TEMPERATURE")){
					allSensorsData.addAll(s.getSensorsData());
			}
		}


		//format data 1
		String[] allDate1 = dateI.split(" "); 
		String[] date01 = allDate1[0].split("/"); 
		String[] hours01 = allDate1[1].split(":"); 
		//2021-01-07 18:29:36
		String dataFormatted1 = date01[2] + "-"+date01[0] + "-"+ date01[1] + " " + hours01[0]+":"+hours01[1]+":00.0";
		Date dateInit1= new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(dataFormatted1);

		//format data 2
		String[] allDate2 = dateE.split(" ");
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
			System.out.println("\n\n\nDate BD  -> "  + dateBD + "  Date 1  -> " + dateI + "  Date 2  -> "+ dateEnd1);

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
			//pie graph
			Map<Integer, Integer> resultMap_pieBCByDates = new TreeMap<>();

			for (String key : graphX_date.keySet()) {
				Integer value = graphX_date.get(key);
				
				if (resultMap_pieBCByDates.containsKey(value)) {
					resultMap_pieBCByDates.put(value, resultMap_pieBCByDates.get(value) + 1);
				} else {
					resultMap_pieBCByDates.put(value, 1);
				}
			}
			model.addAttribute("roomBT_pie", resultMap_pieBCByDates);
			break;
		}

		model.addAttribute("roomInfoBTByDates", graphX_date);
		model.addAttribute("alertRoom_BT", alerts);


		System.out.println("\n\n\n\nSENSORES  -> "  + graphX_date + "alerts -> " + alerts);
		}
		return "BodyTemperature_Room_DatesRange";
	}
	
	






}