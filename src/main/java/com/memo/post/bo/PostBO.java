package com.memo.post.bo;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.memo.common.FileManagerService;
import com.memo.post.dao.PostDAO;
import com.memo.post.model.Post;

@Service
public class PostBO {
	//private Logger logger = LoggerFactory.getLogger(PostBO.class);
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private static final int POST_MAX_SIZE = 3; 

	@Autowired
	private PostDAO postDAO;
	
	@Autowired
	private FileManagerService fileManagerService;
	
	
	public List<Post> getPostListByUserId(int userId, Integer prevId, Integer nextId) {
		// 페이징 계산
		// 10 9 8 | 7 6 5 | 4 3 2 | 1
		// 7 6 5 페이지에서
		// 1) 다음을 눌렀을 때 : nextId-5 5보다 작은 3개(DESC)
		// 2) 이전을 눌렀을 때 : prevId-7 7보다 큰 3개(ASC) => 코드에서 list reverse
		
		String direction = null; // 방향
		Integer standardId = null; // 기준 postId
		if (prevId != null) {
			// '이전' 클릭
			direction = "prev";
			standardId = prevId;
			
			// 7보다 큰 3개(ASC)	8 9 10 => reverse  시킨다. 10 9 8
			List<Post> postList = postDAO.selectPostListByUserId(userId, direction, standardId, POST_MAX_SIZE);
			Collections.reverse(postList);
			return postList;
		} else if (nextId != null) {
			// '다음' 클릭
			direction = "next";
			standardId = nextId;
		}
		
		// 처음 왔을 때 
		return postDAO.selectPostListByUserId(userId, direction, standardId, POST_MAX_SIZE);
	}
	
	public boolean isLastPage(int userId, int nextId) {
		// 제일 작은 id 하나를 가져와서 nextId 비교하고 같으면 마지막 페이지이다.
		return nextId == postDAO.selectPostIdByUserIdAndSort(userId, "ASC");
	}
	
	public boolean isFirstPage(int userId, int prevId) {
		// 제일 큰 id 하나를 가져와서 prevId 비교하고 같으면 첫번째 페이지이다. 
		return prevId == postDAO.selectPostIdByUserIdAndSort(userId, "DESC");
	}
	
	public Post getPostById(int postId) {
		return postDAO.selectPostById(postId);
	}
	
	public int addPost(String loginId, int userId, String subject,
			String content, MultipartFile file) {
		
		String imagePath = null;
		if (file != null) {
			try {
				imagePath = fileManagerService.saveFile(loginId, file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return postDAO.insertPost(userId, subject, content, imagePath);
	}
	
	public int updatePost(String userLoginId, int userId, int postId,
			String subject, String content, MultipartFile file) {
		
		Post post = getPostById(postId);
		if (post == null) {
			logger.error("[update post] 수정할 메모가 존재하지 않음. postId:{}, userId:{}", postId, userId);
			return 0;
		}
		String imagePath = null;
		
		// file이 있으면 수정하고, 없으면 수정하지 않는다.
		if (file != null) {
			// 1. 서버에 이미지를 업로드 하고 imagePath를 받아온다.
			try {
				imagePath = fileManagerService.saveFile(userLoginId, file);
				
				// 2. 기존에 이미지가 있다면 파일을 제거한다.
				// -- 1번에서 업로드가 실패할 수 있으므로 성공 후에 제거
				if (post.getImagePath() != null && imagePath != null) {
					fileManagerService.deleteFile(post.getImagePath());
				}
			} catch (IOException e) {
				logger.error("[update post] 파일 수정중 에러. postId:{}, error:{}", postId, e);
			}
		}
	
		// DB upadate
		return postDAO.updatePost(userId, postId, subject, content, imagePath);
	}
	
	public int deletePost(int postId, int userId) {
		// 파일이 있으면 파일도 삭제한다.
		Post post = getPostById(postId);
		if (post == null) {
			logger.warn("[delete post] 삭제할 메모가 존재하지 않습니다. postId:{}", postId);
			return 0;
		}
		
		if (post.getImagePath() != null) {
			try {
				fileManagerService.deleteFile(post.getImagePath());
			} catch (IOException e) {
				logger.error("[delete post] 이미지 삭제 실패. postId:{}", postId);
			}
		}
		return postDAO.deletePost(postId, userId);
	}
}
