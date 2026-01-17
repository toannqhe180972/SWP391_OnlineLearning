package com.fa.training.controller;

import com.fa.training.repository.PostRepository;
import com.fa.training.repository.SliderRepository;
import com.fa.training.repository.SubjectRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final SliderRepository sliderRepository;
    private final SubjectRepository subjectRepository;
    private final PostRepository postRepository;

    public HomeController(SliderRepository sliderRepository, SubjectRepository subjectRepository,
            PostRepository postRepository) {
        this.sliderRepository = sliderRepository;
        this.subjectRepository = subjectRepository;
        this.postRepository = postRepository;
    }

    @GetMapping({ "/", "/home" })
    public String home(Model model) {
        model.addAttribute("sliders", sliderRepository.findAll());
        model.addAttribute("featuredSubjects", subjectRepository.findByFeaturedTrue());
        model.addAttribute("hotPosts", postRepository.findByHotTrue());
        return "home";
    }
}
