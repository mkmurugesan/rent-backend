package com.rent.app.controllers;

import com.rent.app.models.House;
import com.rent.app.models.Rent;
import com.rent.app.models.RentStatus;
import com.rent.app.repository.HouseRepository;
import com.rent.app.repository.RentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1")
@Slf4j
public class RentController {

    @Autowired
    RentRepository rentRepository;

    @Autowired
    HouseRepository houseRepository;
    private Optional<House> house;

    @GetMapping(value = "/rent", produces = "application/json")
    public List<Rent> getRents() {
        return rentRepository.findAll();
    }

    @GetMapping("/rent/{id}")
    public Rent getRent(@PathVariable Long id) {
        Optional<Rent> rent = rentRepository.findById(id);
        if (rent.isPresent()) {
            return rent.get();
        }
        return null;
    }

    @GetMapping("/rent/house/{id}")
    public List<Rent> getRentByHouse(@PathVariable Long id) {
        Optional<List<Rent>> rent = rentRepository.findByHouseNumber(id);
        if (rent.isPresent()) {
            return rent.get();
        }
        return null;
    }

    @PostMapping("/rent")
    public Rent createRent(@Valid @RequestBody Rent rent) {
        rent.setRentStatus(RentStatus.GENERATED);
        return rentRepository.save(rent);
    }

    @PatchMapping("/rent")
    public Rent updateRent(@Valid @RequestBody Rent rent) {
        if (RentStatus.PAID == rent.getRentStatus() || RentStatus.UNPAID == rent.getRentStatus()) {
            Optional<House> house = houseRepository.findById(rent.getHouseNumber());
            if (house.isPresent()) {
                if (RentStatus.PAID == rent.getRentStatus()) {
                    house.get().setOverallMeterReading(rent.getCurrentMeterReading());
                }
                if (RentStatus.UNPAID == rent.getRentStatus()) {
                    house.get().setOverallMeterReading(rent.getPreviousMeterReading());
                }
                houseRepository.save(house.get());
            }
        }
        return rentRepository.save(rent);
    }

    @DeleteMapping("/rent/{id}")
    public String createHouse(@PathVariable Long id) {
        rentRepository.deleteById(id);
        return "Successfully Deleted";
    }
}
