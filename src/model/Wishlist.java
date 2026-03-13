package model;

import java.util.Date;
import java.sql.Timestamp;

public class Wishlist {
	private long wishlistId;
	private long memberId;
	private long menuId;
	private Date createdAt;
	private String menuName; // 추가!
	private int price;

	public Wishlist(long wishlistId, long memberId, long menuId, Timestamp createdAt, String menuName, int price) {
		this.wishlistId = wishlistId;
		this.memberId = memberId;
		this.menuId = menuId;
		this.createdAt = createdAt;
		this.menuName = menuName;
		this.price = price;
	}

	// DB 조회용
	public Wishlist(long wishlistId, long memberId, long menuId, Timestamp createdAt) {
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

	public String getMenuName() {
		return menuName;
	}

	public int getPrice() {
		return price;
	}

	@Override
	public String toString() {
		return String.format("찜 번호: %d | %s | %,d원", wishlistId, menuName, price);
	}
}