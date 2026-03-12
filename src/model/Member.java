package model;

import java.util.Date;

public class Member {
	private long memberId;
	private String phone; // 회원가입 시 ID로 사용
	private String password;
	private int age;
	private int pointBalance;
	private String role; // USER / ADMIN
	private Date createdAt;

	// DB 조회 및 전체 생성용
	public Member(long memberId, String phone, String password, int age, int pointBalance, String role,
			Date createdAt) {
		this.memberId = memberId;
		this.phone = phone;
		this.password = password;
		this.age = age;
		this.pointBalance = pointBalance;
		this.role = role;
		this.createdAt = createdAt;
	}

	// 회원가입용 생성자
	public Member(String phone, String password, int age) {
		this.phone = phone;
		this.password = password;
		this.age = age;
		this.pointBalance = 0;
		this.role = "USER";
	}

	// Getters
	public long getMemberId() {
		return memberId;
	}

	public String getPhone() {
		return phone;
	}

	public String getPassword() {
		return password;
	}

	public int getAge() {
		return age;
	}

	public int getPointBalance() {
		return pointBalance;
	}

	public String getRole() {
		return role;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	@Override
	public String toString() {
		return String.format("회원번호: %d | 전화번호: %s | 포인트: %d원 | 등급: %s", memberId, phone, pointBalance, role);
	}
}
