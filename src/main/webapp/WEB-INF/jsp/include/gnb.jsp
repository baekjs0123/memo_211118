<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>

<div class="gnb d-flex justify-content-between">
	<div class="logo d-flex align-items-center">
		<h1 class="font-weight-bold">메모 게시판</h1>
	</div>
	<div class="login-info">
		<c:if test="${not empty userName}">
		<div class="mt-5 mr-4">
			<span>${userName}님 안녕하세요</span>
			<a href="/user/sign_out" class="font-weight-bold">로그아웃</a>
		</div>
		</c:if>
	</div>
</div>