package ua.ies.project.service;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import ua.ies.project.model.Building;
import ua.ies.project.repository.BuildingRepository;

@Service
public class BuildingServiceImpl implements BuildingService {

	@Autowired
	private BuildingRepository buildingRepository;

	//obter todos os buildings
	@Override
	public List<Building> getAllBuildings() {
		return buildingRepository.findAll();
	}

	//guardar o building
	@Override
	public void saveBuilding(Building building) {
		this.buildingRepository.save(building);
	}

	//obter o building por ID
	@Override
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

	//eliminar o building
	@Override
	public void deleteBuildingById(long id) {
		this.buildingRepository.deleteById(id);
	}

	//encontar a pagina; existe ordem de paginacao
	@Override
	public Page<Building> findPaginated(int pageNo, int pageSize, String sortField, String sortDirection) {
		Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() :
			Sort.by(sortField).descending();
		
		Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
		return this.buildingRepository.findAll(pageable);
	}
}