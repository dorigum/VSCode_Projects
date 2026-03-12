package model;

import java.util.Date;

public class Menu {
    private long menuId;
    private int categoryId;
    private String menuName;
    private int price;
    private String description;
    private boolean isAvailable;
    private Date createdAt;

    // 메뉴 등록용 생성자
    public Menu(int categoryId, String menuName, int price, String description) {
        this.categoryId = categoryId;
        this.menuName = menuName;
        this.price = price;
        this.description = description;
        this.isAvailable = true;
    }

    // DB 조회용 전체 생성자
    public Menu(long menuId, int categoryId, String menuName, int price, String description, boolean isAvailable, Date createdAt) {
        this.menuId = menuId;
        this.categoryId = categoryId;
        this.menuName = menuName;
        this.price = price;
        this.description = description;
        this.isAvailable = isAvailable;
        this.createdAt = createdAt;
    }

    // Getters
    public long getMenuId() { return menuId; }
    public int getCategoryId() { return categoryId; }
    public String getMenuName() { return menuName; }
    public int getPrice() { return price; }
    public String getDescription() { return description; }
    public boolean isAvailable() { return isAvailable; }
    public Date getCreatedAt() { return createdAt; }

    @Override
    public String toString() {
        return String.format("[%d] %-15s | %6d원 | 가능: %s", 
                menuId, menuName, price, isAvailable ? "YES" : "NO");
    }
}
