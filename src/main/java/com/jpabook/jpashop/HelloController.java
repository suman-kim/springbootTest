package com.jpabook.jpashop;

        import org.springframework.stereotype.Controller;

        import org.springframework.ui.Model;
        import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HelloController {

    

    @GetMapping("/hello")
    public String hello(Model model){
        model.addAttribute("data","hello");
        //템플릿의 html 이름
        return "hello";
    }
}
