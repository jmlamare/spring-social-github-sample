package jml.samples.spring.github;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.social.github.api.GitHub;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/")
public class GitRepositoryController {

	private final GitHub github;

	public GitRepositoryController(GitHub github) {
		this.github = github;
	}
	
	@GetMapping
	public ModelAndView list() {
		Map<?, ?> userProfile = this.github.restOperations().getForObject("https://api.github.com/user", Map.class);
		List<?>   repositories = this.github.restOperations().getForObject(userProfile.get("repos_url").toString(), List.class);
		
		Map<String, Object> model = new HashMap<>();
		model.put("user", userProfile);
		model.put("repositories", repositories);
		return new ModelAndView("gitrepos/index", model);
	}
	
	@ResponseBody
	@GetMapping(path="/user", produces="application/json")
	public Map<?, ?> loadUsers()
	{
		return this.github.restOperations().getForObject("https://api.github.com/user", Map.class);
	}
	
	@ResponseBody
	@GetMapping(path="/repositories", produces="application/json")
	public List<?> loadRepositories()
	{
		Map<?, ?> userProfile = this.github.restOperations().getForObject("https://api.github.com/user", Map.class);
		return this.github.restOperations().getForObject(userProfile.get("repos_url").toString(), List.class);
	}
	
	
}
