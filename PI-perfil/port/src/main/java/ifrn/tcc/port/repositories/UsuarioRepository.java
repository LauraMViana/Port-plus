package ifrn.tcc.port.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import ifrn.tcc.port.Models.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

	Usuario findByEmail(String email);

}


