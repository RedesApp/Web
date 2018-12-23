package cl.redesUsach.redes.services;

import cl.redesUsach.redes.models.Lugar;
import cl.redesUsach.redes.repositories.LugarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/lugares")
public class LugarService {

    @Autowired
    LugarRepository lugarRepository;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Iterable<Lugar> getAllSignals(){
        return lugarRepository.findAll();
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public Lugar create(@RequestBody Lugar resource) {
        return lugarRepository.save(resource);
    }

}
