package ifrn.tcc.port.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import ifrn.tcc.port.Models.Curso;
import ifrn.tcc.port.Models.Material;
import ifrn.tcc.port.Models.Modulo;
import ifrn.tcc.port.repositories.CursoRepository;
import ifrn.tcc.port.repositories.MaterialRepository;
import ifrn.tcc.port.repositories.ModuloRepository;

@Controller
@RequestMapping("/portPlus")
public class PortPlusController {

	@Autowired
	private CursoRepository cr;

	@Autowired
	private ModuloRepository mr;

	@Autowired
	private MaterialRepository mtr;

	private Long idC;

	@GetMapping("/criarCurso/paginaEmDesenvolvimento")
	public String paginaEmDesenvolvimento() {
		return "construcao";

	}

//	Acessar o form de dados gerais do curso
	@GetMapping("/criarCurso")
	public String acessarForm(Curso curso, Modulo modulo, Material material) {
		return "portPlus/CriarCurso";

	}

//	Salvar dados gerais do curso e redirect para criar modulos"
	@PostMapping("/criarCurso")
	public String salvarCurso(Curso curso) {
		cr.save(curso);
		idC = curso.getId();
		return "redirect:/portPlus/criarCurso/" + idC;

	}

// Dados do curso que foram preenchidos na pag anterior, criação  e listagem dos modulos
// nao ta listando os materias :/ (object references an unsaved transient instance - save the transient instance before )
	@GetMapping("/criarCurso/{idC}")
	public ModelAndView addModulo(@PathVariable Long idC, Modulo modulo) {
		ModelAndView md = new ModelAndView();
		Optional<Curso> opt = cr.findById(idC);
		md.setViewName("portPlus/criarModulo");
		Curso curso = opt.get();
		md.addObject("curso", curso);

		List<Modulo> modulos = mr.findByCurso(curso);
		md.addObject("modulos", modulos);

//		List<Material> material = mtr.findByModulo(modulo);
//		md.addObject("material", material);

		return md;
	}

//	Salvar o modulo
	@PostMapping("/criarCurso/{idC}")
	public String salvarMod(@PathVariable Long idC, Modulo modulo) {
		Optional<Curso> opt = cr.findById(idC);
		Curso curso = opt.get();
		modulo.setCurso(curso);
		mr.save(modulo);
		return "redirect:/portPlus/criarCurso/{idC}";
	}

//	Pagina para add materiais aos modulos, porem nao ta pegando o idMod corretamente
	@GetMapping("/criarCurso/{idC}/{idMod}/adicionarVideo")
	public ModelAndView addvideo(@PathVariable Long idC, @PathVariable Long idMod, Material material) {
		ModelAndView md = new ModelAndView();
		Optional<Curso> opt = cr.findById(idC);

		md.setViewName("/portPlus/addvideo");
		Curso curso = opt.get();
		md.addObject("curso", curso);

		Optional<Modulo> optm = mr.findById(idMod);
		Modulo modulo = optm.get();
		md.addObject("modulo", modulo);

		return md;
	}

	@GetMapping("/criarCurso/{idC}/{idMod}/adicionarConteudo")
	public ModelAndView addConteudo(@PathVariable Long idC, @PathVariable Long idMod, Material material) {
		ModelAndView md = new ModelAndView();
		Optional<Curso> opt = cr.findById(idC);

		md.setViewName("/portPlus/addConteudo");
		Curso curso = opt.get();
		md.addObject("curso", curso);

		Optional<Modulo> optm = mr.findById(idMod);
		Modulo modulo = optm.get();
		md.addObject("modulo", modulo);

		return md;
	}

//	Salvar o material
	@PostMapping("/criarCurso/{idC}/{idMod}")
	public String salvarMat(@PathVariable Long idC, @PathVariable Long idMod, Material material) {
		Optional<Modulo> optm = mr.findById(idMod);
		Modulo modulo = optm.get();
		material.setModulo(modulo);
		mtr.save(material);
		return "redirect:/portPlus/criarCurso/{idC}";
	}

//Lista De Cursos Disponivel
	@GetMapping()
	public ModelAndView IndexC() {
		List<Curso> cursos = cr.findAll();
		ModelAndView mv = new ModelAndView("portPlus/index");
		mv.addObject("cursos", cursos);
		return mv;
	}

//Detalhamento do Curso(Titulo,descrição e modulos)
// quando usa o Optional aparece--> ERRO: O ID fornecido não deve ser nulo!
	@GetMapping("/meusCursos/{id}/{titulo}")
	public ModelAndView detalhamentoCurso(@PathVariable Long id, @PathVariable String titulo, Modulo modulo,
			Long idMat) {
		ModelAndView md = new ModelAndView();
		Optional<Curso> opt = cr.findById(id);

		if (opt.isEmpty()) {
			md.setViewName("redirect:/portPlus");
			return md;
		}
		md.setViewName("portPlus/apresentCurso");
		Curso curso = opt.get();
		md.addObject("curso", curso);

		List<Modulo> modulos = mr.findByCurso(curso);
		md.addObject("modulo", modulos);

//		List<Material> material = mtr.findByModulo(modulo);
//		md.addObject("mat", material);

		Optional<Material> op = mtr.findById(idMat);
		Material material2 = op.get();
		md.addObject("material", material2);

		return md;
	}

//Apresentação do material-> Videos e conteudos de cada modulo.
	@GetMapping("/meusCursos/{idC}/{titulo}/{idMod}/{tituloMod}/{idMat}/{tituloMat}")
	public ModelAndView apresentMat(@PathVariable Long idC, @PathVariable String titulo, @PathVariable Long idMod,
			@PathVariable Long idMat, @PathVariable String tituloMat) {
		ModelAndView mv = new ModelAndView();
		Optional<Curso> opt = cr.findById(idC);

		if (opt.isEmpty()) {
			mv.setViewName("redirect:/portPlus");
			return mv;
		}

		mv.setViewName("portPlus/apresentMat");
		Curso curso = opt.get();
		mv.addObject("curso", curso);

		Optional<Modulo> optm = mr.findById(idMod);
		Modulo modulo = optm.get();
		mv.addObject("modulo", modulo);

		List<Material> material = mtr.findByModulo(modulo);
		mv.addObject("material", material);

		Optional<Material> op = mtr.findById(idMat);
		Material material2 = op.get();
		mv.addObject("mat", material2);

		return mv;
	}

}