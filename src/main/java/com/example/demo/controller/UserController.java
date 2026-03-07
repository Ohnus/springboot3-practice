package com.example.demo.controller;

import com.example.demo.domain.user.dto.UserRequestDto;
import com.example.demo.domain.user.dto.UserResponseDto;
import com.example.demo.domain.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

// Mustache 템플릿을 통해 HTML 반환하므로 @Controller
@RequiredArgsConstructor
@Controller
public class UserController {

    private final UserService userService;

    // 메인 페이지 로그인, 로그아웃 분기
    @GetMapping("/")
    public String main(Authentication authentication, Model model) {

        if (authentication != null && authentication.isAuthenticated()
                && !(authentication.getPrincipal() instanceof UserRequestDto)) {
            model.addAttribute("username", authentication.getName());
        }

        return "main";
    }

    // 회원 가입 페이지 로딩
    @GetMapping("/user/join")
    public String joinPage() {
        return "join";
    }

    // 회원 가입 진행
    @PostMapping("/user/join")
    public String joinProcess(UserRequestDto userRequestDto, HttpServletRequest request) {
        userService.join(userRequestDto);

        // 이미 만들어진 다른 컨트롤러로 redirect
        return "redirect:/login";
    }

    // 회원 수정 페이지 응답
    @GetMapping("/user/update/{username}")
    public String updateUser(@PathVariable("username") String username, Model model) {

        // 본인 또는 ADMIN 권한만 접근 가능
        if (userService.isAccess(username)) {
            UserResponseDto dto = userService.getUserByUsername(username);
            model.addAttribute("USER", dto);
            return "update";
        }

        // 권한 없으면 로그인 페이지로 이동
        return "redirect:/login";
    }

    // 회원 수정 수행
    @PostMapping("/user/update/{username}")
    public String updateProcess(@PathVariable("username") String username, UserRequestDto userRequestDto) {

        // 본인 또는 ADMIN 권한만 접근 가능
        if (userService.isAccess(username)) {
            userService.updateUser(userRequestDto, username);
        }

        return "redirect:/user/update/" + username;
    }

    // 회원 삭제
    @PostMapping("/user/delete/{username}")
    public String deleteProcess(@PathVariable("username") String username,
                                HttpServletRequest request,
                                HttpServletResponse response) {
        if (userService.isAccess(username)) {
            userService.deleteUser(username);

            // 로그인 세션 삭제
            new SecurityContextLogoutHandler().logout(
                    request,
                    response,
                    SecurityContextHolder.getContext().getAuthentication());
        }

        return "redirect:/";
    }

}
