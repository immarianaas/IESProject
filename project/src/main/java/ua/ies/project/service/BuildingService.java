package ua.ies.project.service;

import java.util.List;
import org.springframework.data.domain.Page;
import ua.ies.project.model.Building;

public interface BuildingService {
	List<Building> getAllEmployees();
	void saveEmployee(Building employee);
	Building getEmployeeById(long id);
	void deleteEmployeeById(long id);
	Page<Building> findPaginated(int pageNo, int pageSize, String sortField, String sortDirection);
}