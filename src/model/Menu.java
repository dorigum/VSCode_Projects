package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.stream.Collectors;

public class Menu {
    private long menuId;
    private int categoryId;
    private String categoryName; // 카테고리 이름 추가
    private String menuName;
    private int price;
    private String description;
    private boolean isAvailable;
    private Date createdAt;
    private List<OptionGroup> optionGroups = new ArrayList<>();

    // 메뉴 등록용 생성자
    public Menu(int categoryId, String menuName, int price, String description) {
        this.categoryId = categoryId;
        this.menuName = menuName;
        this.price = price;
        this.description = description;
        this.isAvailable = true;
    }

    // DB 조회용 전체 생성자 (categoryName 포함)
    public Menu(long menuId, int categoryId, String categoryName, String menuName, int price, String description, boolean isAvailable, Date createdAt) {
        this.menuId = menuId;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.menuName = menuName;
        this.price = price;
        this.description = description;
        this.isAvailable = isAvailable;
        this.createdAt = createdAt;
    }

    // Getters & Setters
    public long getMenuId() { return menuId; }
    public int getCategoryId() { return categoryId; }
    public String getCategoryName() { return categoryName; }
    public String getMenuName() { return menuName; }
    public int getPrice() { return price; }
    public String getDescription() { return description; }
    public boolean isAvailable() { return isAvailable; }
    public Date getCreatedAt() { return createdAt; }
    public List<OptionGroup> getOptionGroups() { return optionGroups; }
    public void setOptionGroups(List<OptionGroup> optionGroups) { this.optionGroups = optionGroups; }
    public void addOptionGroup(OptionGroup group) { this.optionGroups.add(group); }

    @Override
    public String toString() {
        String availability = isAvailable ? "[판매중]" : "[품절]";
        String optionsInfo = "";
        if (optionGroups != null && !optionGroups.isEmpty()) {
            optionsInfo = " | 선택 가능한 옵션: " + optionGroups.stream()
                    .map(OptionGroup::getGroupName)
                    .collect(Collectors.joining(", "));
        }
        return String.format("%s %-15s | %-6s | %,d원 | 설명: %s%s", 
                availability, menuName, categoryName, price, description, optionsInfo);
    }
}
