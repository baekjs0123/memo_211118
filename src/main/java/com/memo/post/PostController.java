package com.memo.post;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.memo.post.bo.PostBO;
import com.memo.post.model.Post;

@RequestMapping("/post")
@Controller
public class PostController {

	@Autowired
	private PostBO postBO;
	
	@RequestMapping("/post_list_view")
	public String postListView(Model model, HttpServletRequest request) {
		// 세션의 값을 확인해서 지금 로그인이 되었는지
		// -> 비로그인: 로그인 페이지로 리다이렉트
		HttpSession sessoin = request.getSession();
		Integer userId = (Integer)sessoin.getAttribute("userId");
		if (userId == null) { // 비로그인
			return "redirect:/user/sign_in_view";
		}
		
		List<Post> postList = postBO.getPostListByUserId(userId);
		model.addAttribute("postList", postList);
		model.addAttribute("viewName", "post/post_list");
		
		return "template/layout";
	}
	
	@RequestMapping("/post_create_view")
	public String postCreateView(Model model) {
		// TODO 세션이 있는 경우에만 글쓰기 가능
		
		model.addAttribute("viewName", "post/post_create");
		return "template/layout";
	}
	
	@RequestMapping("/post_detail_view")
	public String postDetailView(
			@RequestParam("postId") int postId,
			Model model) {
		
		// TODO 세션이 있는 경우에만 글 상세페이지로 이동 가능
		
		// db select
		Post post = postBO.getPostById(postId);
		model.addAttribute("post", post);
		
		model.addAttribute("viewName", "post/post_detail");
		return "template/layout";
	}
}
