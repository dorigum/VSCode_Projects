package model;

import java.util.ArrayList;
import java.util.List;

/**
 * 메뉴 카테고리 (커피, 논커피, 디저트 등)
 * CATEGORY 테이블과 매핑됩니다.
 * 카테고리별 기본 옵션 그룹 목록(OptionGroups)을 포함할 수 있습니다.
 */
public class Category {
    private int categoryId;
    private String categoryName;
    private List<OptionGroup> optionGroups = new ArrayList<>();

    public Category(int categoryId, String categoryName) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }

    public Category(int categoryId, String categoryName, List<OptionGroup> optionGroups) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.optionGroups = optionGroups != null ? optionGroups : new ArrayList<>();
    }

    public int getCategoryId() { return categoryId; }
    public String getCategoryName() { return categoryName; }
    
    public List<OptionGroup> getOptionGroups() {
        return optionGroups;
    }

    public void setOptionGroups(List<OptionGroup> optionGroups) {
        this.optionGroups = optionGroups;
    }

    public void addOptionGroup(OptionGroup group) {
        this.optionGroups.add(group);
    }

    @Override
    public String toString() {
        return String.format("[%d] %s", categoryId, categoryName);
    }
}
