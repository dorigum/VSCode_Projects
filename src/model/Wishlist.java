package model;

import java.util.Date;

public class Wishlist {
	private long wishlistId;
	private long memberId;
	private long menuId;
	private Date createdAt;

	// DB 조회용
	public Wishlist(long wishlistId, long memberId, long menuId, Date createdAt) {
		this.wishlistId = wishlistId;
		this.memberId = memberId;
		this.menuId = menuId;
		this.createdAt = createdAt;
	}

	// 추가용
	public Wishlist(long memberId, long menuId) {
		this.memberId = memberId;
		this.menuId = menuId;
	}

	public long getWishlistId() {
		return wishlistId;
	}

	public long getMemberId() {
		return memberId;
	}

	public long getMenuId() {
		return menuId;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	@Override
	public String toString() {
		return String.format("찜 번호: %d | 메뉴ID: %d", wishlistId, menuId);
	}
}