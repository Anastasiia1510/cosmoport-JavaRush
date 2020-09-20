package com.space.service;

import com.space.controller.ShipOrder;
import com.space.model.Ship;
import com.space.model.ShipType;
import org.springframework.data.jpa.domain.Specification;
import java.util.Date;
import java.util.List;

public interface ShipService {

    List<Ship> findAllShips(String name, String planet, ShipType shipType, Long after, Long before,
                            Boolean isUsed, Double minSpeed, Double maxSpeed, Integer minCrewSize,
                            Integer maxCrewSize, Double minRating, Double maxRating, ShipOrder order,
                            Integer pageNumber, Integer pageSize);

    Integer findAllShipsCount(String name, String planet, ShipType shipType,
                              Long after, Long before, Boolean isUsed, Double minSpeed,
                              Double maxSpeed, Integer minCrewSize, Integer maxCrewSize,
                              Double minRating, Double maxRating);

    Ship saveShip(Ship ship);

    Ship findShipById(Long id);

    Ship updateShipById(Long id, Ship ship);

    void deleteShipById(Long id);

    Double getRating(double speed, boolean isUsed, Date prodDate);

    boolean checkId(String id);

    boolean checkExist(Long id);

    boolean checkParamsShipsForCreate(Ship ship);

    boolean checkParamsShipsForUpdate(Ship ship);

    Specification<Ship> filterByPlanet(String planet);

    Specification<Ship> filterByName(String name);

    Specification<Ship> filterByShipType(ShipType shipType);

    Specification<Ship> filterByDate(Long after, Long before);

    Specification<Ship> filterByUsage(Boolean isUsed);

    Specification<Ship> filterBySpeed(Double min, Double max);

    Specification<Ship> filterByCrewSize(Integer min, Integer max);

    Specification<Ship> filterByRating(Double min, Double max);
}