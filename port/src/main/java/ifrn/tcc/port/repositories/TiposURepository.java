package ifrn.tcc.port.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import ifrn.tcc.port.Models.TiposU;

public interface TiposURepository extends JpaRepository<TiposU, Long> {
	TiposU findByNome(String nome);
}


