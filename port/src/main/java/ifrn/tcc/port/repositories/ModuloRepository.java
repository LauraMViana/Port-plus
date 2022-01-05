package ifrn.tcc.port.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ifrn.tcc.port.Models.Curso;
import ifrn.tcc.port.Models.Modulo;

public interface ModuloRepository extends JpaRepository<Modulo, Long> {
	List<Modulo> findByCurso(Curso curso);
}
