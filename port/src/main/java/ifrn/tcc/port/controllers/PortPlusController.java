package ifrn.tcc.port.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
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
import ifrn.tcc.port.Models.TiposU;
import ifrn.tcc.port.Models.Usuario;
import ifrn.tcc.port.repositories.CursoRepository;
import ifrn.tcc.port.repositories.MaterialRepository;
import ifrn.tcc.port.repositories.ModuloRepository;
import ifrn.tcc.port.repositories.TiposURepository;
import ifrn.tcc.port.repositories.UsuarioRepository;

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

	@Autowired
	private UsuarioRepository ur;
	@Autowired
	private TiposURepository tr;

//Cadastro
	@PostMapping("/cadastrar")
	public String CadastroU(Usuario usuario, BindingResult result, RedirectAttributes attributes) {
		if (result.hasErrors()) {
			attributes.addFlashAttribute("mensagem", "Os campos não podem ficar em branco");
			return login(usuario);
		}
		ArrayList<TiposU> tiposU = new ArrayList<TiposU>();
		if (usuario.getTipo() == 1) {
			TiposU tipoU = tr.findByNome("ROLE_INSTRUTOR");
			tiposU.add(tipoU);
		}
		if (usuario.getTipo() == 2) {
			TiposU tipoU = tr.findByNome("ROLE_ALUNO");
			tiposU.add(tipoU);
		}
		usuario.setTipos(tiposU);
		usuario.setSenha(new BCryptPasswordEncoder().encode(usuario.getSenha()));
		ur.save(usuario);
		return "redirect:/portPlus";
	}

// Login
	@GetMapping("/login")
	public String login(Usuario usuario) {
		return "portPlus/login";
	}

//Logout
	@GetMapping("/logout")
	public String logout(HttpServletRequest request, HttpServletResponse response) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		if (auth != null) {
			new SecurityContextLogoutHandler().logout(request, response, auth);
		}

		return "redirect:/portPlus";
	}

//Perfil do usuario(dados pessoais)
	@GetMapping("/perfil")
	public String acessarPerfil() {
		return "portPlus/mostrarPerfil/perfil1";
	}

//	Acessar o form de dados gerais do curso
	@GetMapping("/criarCurso")
	public String FormCriarCuso(Curso curso) {
		return "portPlus/criarCurso/CriarCurso";
	}

//	Salvar dados gerais do curso e redirect para criar modulos"
	@PostMapping("/criarCurso")
	public String salvarCurso(@Valid Curso curso, BindingResult result, @RequestParam("file") MultipartFile arquivo) {
		if (result.hasErrors()) {
			return FormCriarCuso(curso);
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

	@GetMapping("/meusCursos/{idC}/editar")
	public ModelAndView EditarCurso(@PathVariable Long idC) {
		ModelAndView md = new ModelAndView();
		Optional<Curso> opt = cr.findById(idC);
		if (opt.isEmpty()) {
			md.setViewName("redirect:/portPlus/meusCursos");
			return md;
		}
		Curso curso = opt.get();
		md.setViewName("portPlus/criarCurso/criarCurso");
		md.addObject("curso", curso);

		return md;
	}

//Apagar Curso
	@GetMapping("/meusCursos/{idC}/apagar")
	public String ApagarCurso(@PathVariable Long idC, Long idMod) {
		Optional<Curso> opt = cr.findById(idC);
		if (!opt.isEmpty()) {
			Curso curso = opt.get();
			List<Modulo> modulos = mr.findByCurso(curso);
			if (!modulos.isEmpty()) {
				for (int i = 0; i < modulos.size(); i++) {
					Modulo modulo = modulos.get(i);
					List<Material> material = mtr.findByModulo(modulo);
					mtr.deleteAll(material);
				}
			}
			mr.deleteAll(modulos);
			cr.delete(curso);
			return "redirect:/portPlus/meusCursos";
		}

		return "redirect:/portPlus/meusCursos";
	}

// Dados do curso que foram preenchidos na pag anterior, criação  e listagem dos modulos
	@GetMapping("/criarCurso/{idC}")
	public ModelAndView addModulo(@PathVariable Long idC, Modulo modulo) {
		ModelAndView md = new ModelAndView();
		Optional<Curso> opt = cr.findById(idC);

		if (opt.isEmpty()) {
			md.setViewName("redirect:/portPlus/criarCurso");
			return md;
		}

		md.setViewName("portPlus/criarCurso/criarModulo");
		Curso curso = opt.get();
		md.addObject("curso", curso);

		List<Modulo> modulos = mr.findByCurso(curso);
		md.addObject("modulos", modulos);

		return md;
	}

	@GetMapping("/criarCurso/{idC}/{idMod}/detalhes")
	public ModelAndView DetalhesMod(@PathVariable Long idC, @PathVariable Long idMod) {
		ModelAndView md = new ModelAndView();
		Optional<Curso> opt = cr.findById(idC);
		Optional<Modulo> optm = mr.findById(idMod);

		if (opt.isEmpty()) {
			md.setViewName("redirect:/portPlus/criarCurso");
			return md;
		}
		if (optm.isEmpty()) {
			md.setViewName("redirect:/portPlus/criarCurso/{idC}");
			return md;
		}
		Curso curso = opt.get();
		Modulo modulo = optm.get();
		if (curso.getId() != modulo.getCurso().getId()) {
			md.setViewName("redirect:/portPlus/criarCurso/{idC}/{idMod}");
			return md;
		}
		md.setViewName("portPlus/criarCurso/detalhesMod");
		md.addObject("curso", curso);
		md.addObject("modulo", modulo);

		List<Material> materiais = mtr.findByModulo(modulo);
		md.addObject("materiais", materiais);

		return md;

	}

//	Salvar o modulo
	@PostMapping("/criarCurso/{idC}")
	public String salvarMod(@PathVariable Long idC, @Valid Modulo modulo, BindingResult result) {
		if (result.hasErrors()) {
			return "redirect:/portPlus/criarCurso/{idC}";
		}
		Optional<Curso> opt = cr.findById(idC);

		if (opt.isEmpty()) {
			return "redirect:/portPlus/criarCurso";
		}
		Curso curso = opt.get();
		modulo.setCurso(curso);
		mr.save(modulo);
		return "redirect:/portPlus/criarCurso/{idC}";
	}

//	Apagar o modulo
	@GetMapping("/criarCurso/{idC}/{idMod}/Apagar")
	public String ApagarModulo(@PathVariable Long idC, @PathVariable Long idMod) {
		Optional<Modulo> optm = mr.findById(idMod);
		Optional<Curso> opt = cr.findById(idC);
		if (opt.isEmpty()) {
			return "redirect:/portPlus/criarCurso";
		}

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
		if (opt.isEmpty()) {
			md.setViewName("redirect:/portPlus/criarCurso");
			return md;
		}
		md.setViewName("/portPlus/criarCurso/addvideo");
		Curso curso = opt.get();
		md.addObject("curso", curso);

		Optional<Modulo> optm = mr.findById(idMod);
		if (optm.isEmpty()) {
			md.setViewName("redirect:/portPlus/criarCurso/{idC}");
			return md;
		}
		Modulo modulo = optm.get();
		md.addObject("modulo", modulo);

		return md;
	}

	@GetMapping("/criarCurso/{idC}/{idMod}/adicionarConteudo")
	public ModelAndView addConteudo(@PathVariable Long idC, @PathVariable Long idMod, Material material) {
		ModelAndView md = new ModelAndView();
		Optional<Curso> opt = cr.findById(idC);
		if (opt.isEmpty()) {
			md.setViewName("redirect:/portPlus/criarCurso");
			return md;
		}
		md.setViewName("/portPlus/criarCurso/addConteudo");
		Curso curso = opt.get();
		md.addObject("curso", curso);

		Optional<Modulo> optm = mr.findById(idMod);
		if (optm.isEmpty()) {
			md.setViewName("redirect:/portPlus/criarCurso/{idC}");
			return md;
		}
		Modulo modulo = optm.get();
		md.addObject("modulo", modulo);

		return md;
	}

//	Salvar o material
	@PostMapping("/criarCurso/{idC}/{idMod}")
	public ModelAndView salvarMat(@PathVariable Long idC, @PathVariable Long idMod, @Valid Material material,
			BindingResult result) {
		if (result.hasErrors()) {
			if (material.getConteudo() == null) {
				return addvideo(idC, idMod, material);
			}
			if (material.getVideo() == null) {
				return addConteudo(idC, idMod, material);
			}
		}
		ModelAndView mv = new ModelAndView();
		Optional<Modulo> optm = mr.findById(idMod);
		Modulo modulo = optm.get();
		material.setModulo(modulo);

		mtr.save(material);

		mv.setViewName("redirect:/portPlus/criarCurso/{idC}");
		return mv;
	}

//Apagar material
	@GetMapping("/criarCurso/{idC}/{idMod}/detalhes/{idMat}/apagar")
	public String ApagarMaterial(@PathVariable Long idC, @PathVariable Long idMod, @PathVariable Long idMat) {
		Optional<Material> optmt = mtr.findById(idMat);
		Optional<Curso> opt = cr.findById(idC);
		Optional<Modulo> optm = mr.findById(idMod);

		if (opt.isEmpty() || optm.isEmpty() || optmt.isEmpty()) {
			return "redirect:/portPlus/criarCurso/{idC}/{idMod}/detalhes";
		}
		if (!optmt.isEmpty()) {
			mtr.deleteById(idMat);
		}
		return "redirect:/portPlus/criarCurso/{idC}/{idMod}/detalhes";
	}

	// Editar material
	@GetMapping("/criarCurso/{idC}/{idMod}/detalhes/{idMat}/editar")
	public ModelAndView EditarMaterial(@PathVariable Long idC, @PathVariable Long idMod, @PathVariable Long idMat) {
		ModelAndView md = new ModelAndView();
		Optional<Curso> opt = cr.findById(idC);
		Optional<Modulo> optm = mr.findById(idMod);

		if (opt.isEmpty() || optm.isEmpty()) {
			md.setViewName("redirect:/portPlus/criarCurso/{idC}/{idMod}");
			return md;
		}
		Curso curso = opt.get();
		Modulo modulo = optm.get();
		if (curso.getId() != modulo.getCurso().getId()) {
			md.setViewName("redirect:/portPlus/criarCurso/{idC}/{idMod}");
			return md;
		}

		Optional<Material> optmt = mtr.findById(idMat);
		Material material = optmt.get();
		if (!optmt.isEmpty()) {
			if (material.getVideo() == null) {
				md.setViewName("portPlus/criarCurso/addConteudo");
			}
			if (material.getConteudo() == null) {
				md.setViewName("portPlus/criarCurso/addVideo");
			}
		}
		md.addObject("curso", curso);
		md.addObject("modulo", modulo);
		md.addObject("material", material);

		return md;
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

		if (!md.isEmpty()) {
			for (int i = 0; i < md.size(); i++) {
				Modulo modulo = md.get(i);
				modulo.setCurso(curso);
				mr.save(modulo);
				List<Material> materiais = mtr.findByModulo(modulo);
				if (materiais.isEmpty()) {
					attributes.addFlashAttribute("mensagem",
							"Adicione no mínimo um material no módulo: " + modulo.getTituloMod());
					return "redirect:/portPlus/criarCurso/{idC}";
				}
			}
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
	public ModelAndView ApresentMaterial(@PathVariable Long idC, @PathVariable String titulo, @PathVariable Long idMod,
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
