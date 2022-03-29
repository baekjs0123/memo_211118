<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<div class="d-flex justify-content-center">
	<div class="w-50">
		<h1>글 상세 / 수정</h1>
		<input type="text" name="subject" class="form-control" placeholder="제목을 입력해주세요." value="${post.subject}">
		<textarea class="form-control" name="content" rows="15" placeholder="내용을 입력해주세요.">${post.content}</textarea>
		
		<div class="d-flex justify-content-end mt-3">
			<input name="file" type="file" accept=".jpg,.jpeg,.png,.gif">
		</div>
		
		<%-- 이미지가 있을 때만 노출 --%>
		<c:if test="${not empty post.imagePath}">
		<div class="image-area">
			<img src="${post.imagePath}" alt="업로드 이미지" width="300">
		</div>
		</c:if>
		
		<div class="clearfix mt-5">
			<button type="button" id="postDeleteBtn" class="btn btn-secondary" data-post-id="${post.id}">삭제</button>
			
			<div class="float-right">
				<button type="button" id="postListBtn" class="btn btn-dark">목록</button>
				<button type="button" id="saveBtn" class="btn btn-primary" data-post-id="${post.id}">수정</button>
			</div>
		</div>
	</div>
</div>

<script>
$(document).ready(function() {
	// 삭제 버튼 클릭
	$('#postDeleteBtn').on('click', function() {
		let postId = $(this).data('post-id');
		//alert("postId : " + postId);
		
		$.ajax({
			type: "delete"
			, url: "/post/delete"
			, data: {"postId":postId}
			, success: function(data) {
				if (data.result == "success") {
					alert("성공적으로 삭제되었습니다.");
					location.href = "/post/post_list_view";
				} else {
					alert(data.error_message);
				}
			}
			, error: function(e) {
				alert("메모를 삭제하는데 실패했습니다. 관리자에게 문의해주세요.");
			}
		});
	});
	
	// 목록 버튼 클릭
	$('#postListBtn').on('click', function() {
		location.href = "/post/post_list_view";
	});
	
	// 글 내용 저장
	$('#saveBtn').on('click', function() {
		let subject = $('input[name=subject]').val().trim();
		if (subject == '') {
			alert("제목을 입력해주세요");
			return;
		}
		
		let content = $('textarea[name=content]').val();
		if (content == '') {
			alert("내용을 입력해주세요");
			return;
		}
		
		// 파일이 업로드 된 경우 확장자 체크
		let filePath = $('input[name=file]').val();
		//alert(filePath);
		if (filePath != '') {
			//console.log(filePath.split('.').pop());
			let ext = filePath.split('.').pop().toLowerCase();
			if ($.inArray(ext,['gif', 'jpg', 'jpeg', 'png']) == -1) {
				alert("이미지 파일만 업로드 할 수있습니다.");
				$('input[name=file]').val(''); // 파일을 비운다.
				return;
			}
		}
		
		let formData = new FormData();
		let postId = $(this).data('post-id');
		//alert(postId);
		formData.append("postId", postId);
		formData.append("subject", subject);
		formData.append("content", content);
		formData.append("file", $('input[name=file]')[0].files[0]);
		
		$.ajax({
			type: "put"
			, url: "/post/update"
			, data: formData
			, enctype: "multipart/form-data"
			, processData: false
			, contentType: false
			, success: function(data) {
				if (data.result == "success") {
					alert("수정되었습니다.");
					location.reload(); // 새로고침
				} else {
					alert(data.error_message);
				}	
			}
			, error: function(e) {
				alert("메모 수정에 실패했습니다. 관리자에게 문의 해주세요.");
			}
		});
	});
	
});
</script>