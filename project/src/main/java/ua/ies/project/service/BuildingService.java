package ua.ies.project.service;

import java.util.List;
import org.springframework.data.domain.Page;
import ua.ies.project.model.Building;

public interface BuildingService {
	List<Building> getAllBuildings();
	void saveBuilding(Building buil);
	Building getBuildingById(long id);
	void deleteBuildingById(long id);
	Page<Building> findPaginated(int pageNo, int pageSize, String sortField, String sortDirection);
	//Page<Building> findPaginated(int pageNo, int pageSize, String sortField, String sortDirection);

}