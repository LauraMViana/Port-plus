package ifrn.tcc.port.Models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;

@Entity
public class Modulo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@NotBlank
	private String tituloMod;

	@ManyToOne
	private Curso curso;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTituloMod() {
		return tituloMod;
	}

	public void setTituloMod(String tituloMod) {
		this.tituloMod = tituloMod;
	}

	public Curso getCurso() {
		return curso;
	}

	public void setCurso(Curso curso) {
		this.curso = curso;
	}

	@Override
	public String toString() {
		return "Modulo [id=" + id + ", tituloMod=" + tituloMod + ", curso=" + curso + "]";
	}

}