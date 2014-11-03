package org.sllx.site.user.controller;

import org.sllx.site.core.controller.BaseController;
import org.sllx.site.core.support.Page;
import org.sllx.site.user.entity.User;
import org.sllx.site.user.service.UserService;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

@Controller
@RequestMapping("/user")
public class UserAction extends BaseController {

    @javax.annotation.Resource(name = "userService")
    private UserService userService;

    @RequestMapping(method = RequestMethod.GET)
    public String list(HttpServletRequest request, Page page, ModelMap model) throws IllegalAccessException {
        List<User> users = userService.list(makeParam(request), page);
        model.addAttribute("users", users);
        Link delete = linkTo(UserAction.class).withSelfRel();
        Resource<Object> resource = new Resource<Object>(new Object(),delete);
        model.addAttribute("resource", resource);
        return "user";
    }

    @RequestMapping(value  = "/{id}", method = RequestMethod.GET)
    public String get(@PathVariable int id, User user, ModelMap model){
        user.setId(id);
        user = userService.get(user);
        model.addAttribute("user", user);
        return "user";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String post(User user){
        userService.insert(user);
        return "redirect:/user";
    }

    @RequestMapping(value  = "/{id}", method = RequestMethod.PUT)
    public String put(@PathVariable int id, User user, HttpServletRequest request) throws InvocationTargetException, IllegalAccessException {
        user.setId(id);
        user = userService.get(user);
        reset(user, makeParam(request));
        userService.update(user);
        return "redirect:/user";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public @ResponseBody String delete(@PathVariable int id, User user){
        user.setId(id);
        userService.delete(user);
        return SUCCESS;
    }

    @RequestMapping(value  = "/json/{id}", method = RequestMethod.GET)
    public @ResponseBody User json(@PathVariable int id, User user){
        user.setId(id);
        return userService.get(user);
    }

    @RequestMapping(value = "/file", method = RequestMethod.POST)
    public String upload(@RequestParam MultipartFile uploadFile, ServletConfig config) throws IOException {
        uploadFile.transferTo(new File(config.getServletContext().getRealPath("/") + "/tmp/" + uploadFile.getOriginalFilename()));
        return "redirect:/user";
    }
}
