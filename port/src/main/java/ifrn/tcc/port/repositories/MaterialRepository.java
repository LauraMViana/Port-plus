package ifrn.tcc.port.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ifrn.tcc.port.Models.Material;
import ifrn.tcc.port.Models.Modulo;

public interface MaterialRepository extends JpaRepository<Material, Long> {
	List<Material> findByModulo(Modulo modulo);
}
