package ifrn.tcc.port.Models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;

@Entity
public class Curso {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String titulo;
	private int cargahoraria;
	private String publicalvo;
	@Lob
	private String descricao;
	@Lob
	private String conteudo;

// private BufferedImage logo;

// public BufferedImage getLogo() {
//		return logo;
//	}
//
//	public void setLogo(BufferedImage logo) {
//		this.logo = logo;
//	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public int getCargahoraria() {
		return cargahoraria;
	}

	public void setCargahoraria(int cargahoraria) {
		this.cargahoraria = cargahoraria;
	}

	public String getPublicalvo() {
		return publicalvo;
	}

	public void setPublicalvo(String publicalvo) {
		this.publicalvo = publicalvo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getConteudo() {
		return conteudo;
	}

	public void setConteudo(String conteudo) {
		this.conteudo = conteudo;
	}

	@Override
	public String toString() {
		return "Curso [id=" + id + ", titulo=" + titulo + ", cargahoraria=" + cargahoraria + ", publicalvo="
				+ publicalvo + ", descricao=" + descricao + ", conteudo=" + conteudo + "]";
	}

}