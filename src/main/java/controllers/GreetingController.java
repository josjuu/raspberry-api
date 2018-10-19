package controllers;

import classes.Csv;
import classes.Statues;

import java.util.List;

import org.springframework.web.bind.annotation.*;

@RestController
public class GreetingController {
    private final Csv csv = new Csv();

    //Get all
    @RequestMapping("/statueses")
    public List<Statues> GetAll() {
        return this.csv.statueses;
    }

    //Get 1
    @RequestMapping(value = "/statueses/{id}")
    public Statues Get(@PathVariable String id) {
        for (Statues statues : this.csv.statueses) {
            if (statues.id == Integer.parseInt(id)) {
                return statues;
            }
        }

        return null;
    }

    //Post
    @PostMapping("/statueses")
    public Statues Post(@RequestBody Statues newStatues) {
        this.csv.addNewRecord(newStatues);
        int lastId = this.csv.getLastId();

        for (Statues statues : this.csv.statueses) {
            if (statues.id == lastId) {
                return statues;
            }
        }

        return null;
    }
}