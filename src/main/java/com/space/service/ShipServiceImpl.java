package com.space.service;

import com.space.controller.ShipOrder;
import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.repository.ShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class ShipServiceImpl implements ShipService{

    @Autowired
    private ShipRepository shipRepository;

    @Override
    public List<Ship> findAllShips(String name, String planet, ShipType shipType, Long after, Long before,
                                   Boolean isUsed, Double minSpeed, Double maxSpeed, Integer minCrewSize,
                                   Integer maxCrewSize, Double minRating, Double maxRating, ShipOrder order,
                                   Integer pageNumber, Integer pageSize) {

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(order.getFieldName()));
        Specification<Ship> specification = Specification.where(filterByName(name)
                .and(filterByPlanet(planet)))
                .and(filterByShipType(shipType))
                .and(filterByDate(after, before))
                .and(filterByUsage(isUsed))
                .and(filterBySpeed(minSpeed, maxSpeed))
                .and(filterByCrewSize(minCrewSize, maxCrewSize))
                .and(filterByRating(minRating, maxRating));
        Page<Ship> pageShip = shipRepository.findAll(specification, pageable);
        return pageShip.getContent();
    }

    @Override
    public Integer findAllShipsCount(String name, String planet, ShipType shipType,
                                     Long after, Long before, Boolean isUsed, Double minSpeed,
                                     Double maxSpeed, Integer minCrewSize, Integer maxCrewSize,
                                     Double minRating, Double maxRating) {

        Specification<Ship> specification = Specification.where(filterByName(name)
                .and(filterByPlanet(planet)))
                .and(filterByShipType(shipType))
                .and(filterByDate(after, before))
                .and(filterByUsage(isUsed))
                .and(filterBySpeed(minSpeed, maxSpeed))
                .and(filterByCrewSize(minCrewSize, maxCrewSize))
                .and(filterByRating(minRating, maxRating));
        List <Ship> ships = shipRepository.findAll(specification);
        return ships.size();
    }

    @Override
    public Ship saveShip(Ship ship) {
        if (ship.getUsed() == null)
            ship.setUsed(false);
        Double rating = getRating(ship.getSpeed(), ship.getUsed(), ship.getProdDate());
        ship.setRating(rating);
        return shipRepository.save(ship);
    }

    @Override
    public Ship findShipById(Long id) {
        return shipRepository.getById(id);
    }

    @Override
    public Ship updateShipById(Long id, Ship ship) {
        Ship shipExist = shipRepository.getById(id);

        if (ship.getName() != null)
            shipExist.setName(ship.getName());

        if (ship.getPlanet() != null)
            shipExist.setPlanet(ship.getPlanet());

        if (ship.getShipType() != null)
            shipExist.setShipType(ship.getShipType());

        if (ship.getProdDate() != null)
            shipExist.setProdDate(ship.getProdDate());

        if (ship.getSpeed() != null)
            shipExist.setSpeed(ship.getSpeed());

        if (ship.getUsed() != null)
            shipExist.setUsed(ship.getUsed());

        if (ship.getCrewSize() != null)
            shipExist.setCrewSize(ship.getCrewSize());

        Double rating = getRating(shipExist.getSpeed(), shipExist.getUsed(), shipExist.getProdDate());
        shipExist.setRating(rating);
        return shipRepository.save(shipExist);
    }

    @Override
    public void deleteShipById(Long id) {
        shipRepository.deleteById(id);
    }

    @Override
    public Double getRating(double speed, boolean isUsed, Date prodDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(prodDate);
        int year = calendar.get(Calendar.YEAR);
        BigDecimal raiting = BigDecimal.valueOf((80 * speed * (isUsed ? 0.5 : 1)) / (3019 - year + 1));
        raiting = raiting.setScale(2, RoundingMode.HALF_UP);
        return raiting.doubleValue();
    }

    public boolean checkParamsShipsForCreate(Ship ship){
        if (ship==null)
            return false;

        Calendar cal = Calendar.getInstance();
        cal.setTime(ship.getProdDate());

        return (ship.getName() != null && (ship.getName().length() >= 1 && ship.getName().length() <= 50))
                && (ship.getPlanet() != null && (ship.getPlanet().length() >= 1 && ship.getPlanet().length() <= 50))
                && (ship.getProdDate() != null && cal.get(Calendar.YEAR) >= 2800 && cal.get(Calendar.YEAR) <= 3019)
                && (ship.getSpeed() != null && (ship.getSpeed() >= 0.01D && ship.getSpeed() <= 0.99D))
                && (ship.getCrewSize() != null && (ship.getCrewSize() >= 1 && ship.getCrewSize() <= 9999));
    }

    @Override
    public boolean checkParamsShipsForUpdate(Ship ship) {
        Calendar cal = Calendar.getInstance();

        if (ship.getProdDate() != null)
            cal.setTime(ship.getProdDate());

        if(ship.getName() != null && (ship.getName().length() < 1 || ship.getName().length() > 50))
            return false;

        if (ship.getPlanet() != null && (ship.getPlanet().length() < 1 || ship.getPlanet().length() > 50))
            return false;

        if(ship.getProdDate() != null && cal.get(Calendar.YEAR) < 2800 || cal.get(Calendar.YEAR) > 3019)
            return false;

        if(ship.getSpeed() != null && (ship.getSpeed() < 0.01D || ship.getSpeed() > 0.99D))
            return false;

        if(ship.getCrewSize() != null && (ship.getCrewSize() < 1 || ship.getCrewSize() > 9999))
            return false;

        return true;
    }

    @Override
    public boolean checkId(String id){
       try{
           Long idLong = Long.parseLong(id);
           return (id != null && !id.equals("") && !id.equals("0"));
       }catch (NumberFormatException e){
           return false;
       }
    }

    @Override
    public boolean checkExist(Long id) {
        return (shipRepository.existsById(id));
    }

    @Override
    public Specification<Ship> filterByName(String name) {
        return (root, query, cb) -> name == null ? null : cb.like(root.get("name"), "%" + name + "%");
    }

    @Override
    public Specification<Ship> filterByPlanet(String planet) {
        return (root, query, cb) -> planet == null ? null : cb.like(root.get("planet"), "%" + planet + "%");
    }

    @Override
    public Specification<Ship> filterByShipType(ShipType shipType) {
        return (root, query, cb) -> shipType == null ? null : cb.equal(root.get("shipType"), shipType);
    }

    @Override
    public Specification<Ship> filterByDate(Long after, Long before) {
        return (root, query, cb) -> {
            if (after == null && before == null)
                return null;
            if (after == null) {
                Date before1 = new Date(before);
                return cb.lessThanOrEqualTo(root.get("prodDate"), before1);
            }
            if (before == null) {
                Date after1 = new Date(after);
                return cb.greaterThanOrEqualTo(root.get("prodDate"), after1);
            }
            Date before1 = new Date(before);
            Date after1 = new Date(after);
            return cb.between(root.get("prodDate"), after1, before1);
        };
    }

    @Override
    public Specification<Ship> filterByUsage(Boolean isUsed) {
        return (root, query, cb) -> {
            if (isUsed == null)
                return null;
            if (isUsed)
                return cb.isTrue(root.get("isUsed"));
            else return cb.isFalse(root.get("isUsed"));
        };
    }

    @Override
    public Specification<Ship> filterBySpeed(Double min, Double max) {
        return (root, query, cb) -> {
            if (min == null && max == null)
                return null;
            if (min == null)
                return cb.lessThanOrEqualTo(root.get("speed"), max);
            if (max == null)
                return cb.greaterThanOrEqualTo(root.get("speed"), min);

            return cb.between(root.get("speed"), min, max);
        };
    }

    @Override
    public Specification<Ship> filterByCrewSize(Integer min, Integer max) {
        return (root, query, cb) -> {
            if (min == null && max == null)
                return null;
            if (min == null)
                return cb.lessThanOrEqualTo(root.get("crewSize"), max);
            if (max == null)
                return cb.greaterThanOrEqualTo(root.get("crewSize"), min);

            return cb.between(root.get("crewSize"), min, max);
        };
    }

    @Override
    public Specification<Ship> filterByRating(Double min, Double max) {
        return (root, query, cb) -> {
            if (min == null && max == null)
                return null;
            if (min == null)
                return cb.lessThanOrEqualTo(root.get("rating"), max);
            if (max == null)
                return cb.greaterThanOrEqualTo(root.get("rating"), min);

            return cb.between(root.get("rating"), min, max);
        };
    }
}
