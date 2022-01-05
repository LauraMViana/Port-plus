package ifrn.tcc.port.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ifrn.tcc.port.Models.Curso;
import ifrn.tcc.port.Models.Material;
import ifrn.tcc.port.Models.Modulo;
import ifrn.tcc.port.repositories.CursoRepository;
import ifrn.tcc.port.repositories.MaterialRepository;
import ifrn.tcc.port.repositories.ModuloRepository;

@Controller
@RequestMapping("/portPlus")
public class PortPlusController {

//Caminho para salvar a logotipo do curso
	private static String caminhologotipoCurso = "src/main/resources/static/Imagens/";

	@Autowired
	private CursoRepository cr;

	@Autowired
	private ModuloRepository mr;

	@Autowired
	private MaterialRepository mtr;

	// Login
	@GetMapping("/login")
	public String login() {
		return "portPlus/login";
	}

//Perfil do usuario(dados pessoais)
	@GetMapping("/perfil")
	public String acessarPerfil1() {
		return "portPlus/mostrarPerfil/perfil1";
	}

//	Acessar o form de dados gerais do curso
	@GetMapping("/criarCurso")
	public String acessarForm(Curso curso) {
		return "portPlus/criarCurso/CriarCurso";
	}

//	Salvar dados gerais do curso e redirect para criar modulos"
	@PostMapping("/criarCurso")
	public String salvarCurso(@Valid Curso curso, BindingResult result, @RequestParam("file") MultipartFile arquivo) {
		if (result.hasErrors()) {
			return acessarForm(curso);
		}
		cr.save(curso);
		Long idCurso = curso.getId();
		try {
			if (!arquivo.isEmpty()) {
				byte[] bytes = arquivo.getBytes();
				Path caminho = Paths
						.get(caminhologotipoCurso + String.valueOf(curso.getId()) + arquivo.getOriginalFilename());
				Files.write(caminho, bytes);
				curso.setNomeLogo(String.valueOf(curso.getId()) + arquivo.getOriginalFilename());
				cr.save(curso);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "redirect:/portPlus/criarCurso/" + idCurso;

	}

// Dados do curso que foram preenchidos na pag anterior, criação  e listagem dos modulos
	@GetMapping("/criarCurso/{idC}")
	public ModelAndView addModulo(@PathVariable Long idC, Modulo modulo) {
		ModelAndView md = new ModelAndView();
		Optional<Curso> opt = cr.findById(idC);
		md.setViewName("portPlus/criarCurso/criarModulo");
		Curso curso = opt.get();
		md.addObject("curso", curso);

		List<Modulo> modulos = mr.findByCurso(curso);
		md.addObject("modulos", modulos);

		return md;
	}

//	Salvar o modulo
	@PostMapping("/criarCurso/{idC}")
	public String salvarMod(@PathVariable Long idC, @Valid Modulo modulo, BindingResult result) {
		if (result.hasErrors()) {
			return "redirect:/portPlus/criarCurso/{idC}";
		}
		Optional<Curso> opt = cr.findById(idC);
		Curso curso = opt.get();
		modulo.setCurso(curso);
		mr.save(modulo);
		return "redirect:/portPlus/criarCurso/{idC}";
	}

//	Apagar o modulo
	@GetMapping("/criarCurso/{idC}/{idMod}/Apagar")
	public String ApagarMod(@PathVariable Long idC, @PathVariable Long idMod) {
		Optional<Modulo> optm = mr.findById(idMod);
		if (!optm.isEmpty()) {
			Modulo modulo = optm.get();
			List<Material> materiais = mtr.findByModulo(modulo);
			mtr.deleteAll(materiais);
			mr.deleteById(idMod);
		}
		return "redirect:/portPlus/criarCurso/{idC}";
	}

//	Pagina para add materiais aos modulos
	@GetMapping("/criarCurso/{idC}/{idMod}/adicionarVideo")
	public ModelAndView addvideo(@PathVariable Long idC, @PathVariable Long idMod, Material material) {
		ModelAndView md = new ModelAndView();
		Optional<Curso> opt = cr.findById(idC);

		md.setViewName("/portPlus/criarCurso/addvideo");
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

		md.setViewName("/portPlus/criarCurso/addConteudo");
		Curso curso = opt.get();
		md.addObject("curso", curso);

		Optional<Modulo> optm = mr.findById(idMod);
		Modulo modulo = optm.get();
		md.addObject("modulo", modulo);

		return md;
	}

//	Salvar o material
	@PostMapping("/criarCurso/{idC}/{idMod}")
	public ModelAndView salvarMat(@PathVariable Long idC, @PathVariable Long idMod, @Valid Material material,
			BindingResult result) {
		ModelAndView mv = new ModelAndView();
		Optional<Modulo> optm = mr.findById(idMod);
		Modulo modulo = optm.get();
		material.setModulo(modulo);
		mtr.save(material);
		mv.setViewName("redirect:/portPlus/criarCurso/{idC}");
		return mv;
	}

//Concluir a criacao do curso
	@GetMapping("/criarCurso/{idC}/concluir")
	public String concluir(@PathVariable Long idC, Long idMod, RedirectAttributes attributes) {
		Optional<Curso> opt = cr.findById(idC);
		Curso curso = opt.get();
		List<Modulo> md = mr.findByCurso(curso);
		if (md.isEmpty()) {
			attributes.addFlashAttribute("mensagem", "Adicione um módulo ao curso!");
			return "redirect:/portPlus/criarCurso/{idC}";
		}

		return "redirect:/portPlus";
	}

//Lista De Cursos Disponivel 
	@GetMapping()
	public ModelAndView IndexC() {
		List<Curso> cursos = cr.findAll();
		ModelAndView mv = new ModelAndView("portPlus/index");
		mv.addObject("cursos", cursos);
		return mv;
	}

// Cursos criados -Pag do professor
	@GetMapping("/meusCursos")
	public ModelAndView ListaCurso() {
		List<Curso> cursos = cr.findAll();
		ModelAndView mv = new ModelAndView("portPlus/meusCursos");
		mv.addObject("cursos", cursos);
		return mv;
	}

//Exibir a imagem do logo
	@GetMapping("{imagem}")
	@ResponseBody
	public byte[] imagem(@PathVariable("imagem") String imagem) throws IOException {
		File imagemArquivo = new File(caminhologotipoCurso + imagem);
		if (imagem != null || imagem.trim().length() > 0) {
			System.out.println("NO IF");
			return Files.readAllBytes(imagemArquivo.toPath());
		}
		return null;
	}

//Detalhamento do Curso(Titulo,descrição e modulos)
	@GetMapping("/meusCursos/{id}/{titulo}")
	public ModelAndView detalhamentoCurso(@PathVariable Long id, @PathVariable String titulo, Long idMat, Long idMod,
			Modulo modulo) {
		ModelAndView md = new ModelAndView();
		Optional<Curso> opt = cr.findById(id);

		if (opt.isEmpty()) {
			md.setViewName("redirect:/portPlus");
			return md;
		}
		md.setViewName("portPlus/apresentarCurso/apresentCurso");
		Curso curso = opt.get();
		md.addObject("curso", curso);

		List<Modulo> modulos = mr.findByCurso(curso);
		md.addObject("modulo", modulos);

		return md;
	}

//Apresentação do material-> Videos e conteudos de cada modulo.
	@GetMapping("/meusCursos/{idC}/{titulo}/{idMod}/{tituloMod}")
	public ModelAndView aptMat(@PathVariable Long idC, @PathVariable String titulo, @PathVariable Long idMod,
			Long idMat) {
		ModelAndView mv = new ModelAndView();
		Optional<Curso> opt = cr.findById(idC);

		if (opt.isEmpty()) {
			mv.setViewName("redirect:/portPlus");
			return mv;
		}
		mv.setViewName("portPlus/apresentarCurso/apresentMaterial");
		Curso curso = opt.get();
		mv.addObject("curso", curso);

		Optional<Modulo> optm = mr.findById(idMod);
		Modulo modulo = optm.get();
		mv.addObject("modulo", modulo);

		List<Material> material = mtr.findByModulo(modulo);
		mv.addObject("mat", material.get(0));
		mv.addObject("material", material);

		return mv;
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

		mv.setViewName("portPlus/apresentarCurso/apresentMat");
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
