package com.space.controller;

import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.service.ShipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/rest/ships")
public class ShipController {

    @Autowired
    private ShipService shipService;

    @GetMapping("")
    public ResponseEntity<List<Ship>> getShips(@RequestParam(required = false) String name,
                                               @RequestParam(required = false) String planet,
                                               @RequestParam(required = false) ShipType shipType,
                                               @RequestParam(required = false) Long after,
                                               @RequestParam(required = false) Long before,
                                               @RequestParam(required = false) Boolean isUsed,
                                               @RequestParam(required = false) Double minSpeed,
                                               @RequestParam(required = false) Double maxSpeed,
                                               @RequestParam(required = false) Integer minCrewSize,
                                               @RequestParam(required = false) Integer maxCrewSize,
                                               @RequestParam(required = false) Double minRating,
                                               @RequestParam(required = false) Double maxRating,
                                               @RequestParam(required = false, defaultValue = "ID") ShipOrder order,
                                               @RequestParam(required = false, defaultValue = "0") Integer pageNumber,
                                               @RequestParam(required = false, defaultValue = "3") Integer pageSize) {

        List <Ship> ships = shipService.findAllShips(name, planet, shipType,
                after, before, isUsed, minSpeed, maxSpeed, minCrewSize,
                maxCrewSize, minRating, maxRating, order, pageNumber, pageSize);
        return new ResponseEntity<>(ships, HttpStatus.OK);
    }

    @GetMapping("/count")
    public ResponseEntity<Integer> getShipsCount(@RequestParam(required = false) String name,
                                              @RequestParam(required = false) String planet,
                                              @RequestParam(required = false) ShipType shipType,
                                              @RequestParam(required = false) Long after,
                                              @RequestParam(required = false) Long before,
                                              @RequestParam(required = false) Boolean isUsed,
                                              @RequestParam(required = false) Double minSpeed,
                                              @RequestParam(required = false) Double maxSpeed,
                                              @RequestParam(required = false) Integer minCrewSize,
                                              @RequestParam(required = false) Integer maxCrewSize,
                                              @RequestParam(required = false) Double minRating,
                                              @RequestParam(required = false) Double maxRating) {

        Integer count = shipService.findAllShipsCount(name, planet, shipType, after, before,
                isUsed, minSpeed, maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating);
        return new ResponseEntity<>(count, HttpStatus.OK);
    }

    @PostMapping("")
    public ResponseEntity<Ship> createShip(@RequestBody Ship ship) {
        if (ship!=null && ship.getName() == null
                || ship.getPlanet() == null
                || ship.getShipType() == null
                || ship.getProdDate() == null
                || ship.getSpeed() == null
                || ship.getCrewSize() == null
                || !shipService.checkParamsShipsForCreate(ship)
        ){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Ship shipNew = shipService.saveShip(ship);
        return new ResponseEntity<>(shipNew, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ship> getShip(@PathVariable String id) {
        if(!shipService.checkId(id)){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if(!shipService.checkExist(Long.parseLong(id))){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Ship ship = shipService.findShipById(Long.parseLong(id));
        return new ResponseEntity<>(ship, HttpStatus.OK);
    }

    @PostMapping("/{id}")
    public ResponseEntity<Ship> updateShip(@PathVariable String id, @RequestBody Ship ship) {
        if(ship == null && shipService.checkId(id))
            return new ResponseEntity<>(HttpStatus.OK);

        if(!shipService.checkId(id) || !shipService.checkParamsShipsForUpdate(ship)){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if(!shipService.checkExist(Long.parseLong(id))){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Ship shipUpdate = shipService.updateShipById(Long.parseLong(id), ship);
        return new ResponseEntity<>(shipUpdate, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteShip(@PathVariable String id) {
        if(!shipService.checkId(id)){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if(!shipService.checkExist(Long.parseLong(id))){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        shipService.deleteShipById(Long.parseLong(id));
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
