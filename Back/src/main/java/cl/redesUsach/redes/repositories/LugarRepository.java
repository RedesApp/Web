package cl.redesUsach.redes.repositories;

import cl.redesUsach.redes.models.Lugar;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LugarRepository extends MongoRepository<Lugar,String> {
}
