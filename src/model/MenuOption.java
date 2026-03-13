package model;

/**
 * 메뉴 옵션 (예: HOT, ICE, Regular, Large 등)
 * MENU_OPTION 테이블과 매핑됩니다.
 */
public class MenuOption {
    private long optionId;
    private long groupId;      // OptionGroup ID
    private String optionName;
    private int extraPrice;
    private int displayOrder;

    // 기본 생성자
    public MenuOption() {}

    // 전체 필드 생성자
    public MenuOption(long optionId, long groupId, String optionName, int extraPrice, int displayOrder) {
        this.optionId = optionId;
        this.groupId = groupId;
        this.optionName = optionName;
        this.extraPrice = extraPrice;
        this.displayOrder = displayOrder;
    }

    // 데이터베이스 조회용 생성자 (ID 포함)
    public MenuOption(long optionId, long groupId, String optionName, int extraPrice) {
        this(optionId, groupId, optionName, extraPrice, 0);
    }

    // 신규 생성용 생성자 (ID 제외)
    public MenuOption(long groupId, String optionName, int extraPrice, int displayOrder) {
        this.groupId = groupId;
        this.optionName = optionName;
        this.extraPrice = extraPrice;
        this.displayOrder = displayOrder;
    }

    public long getOptionId() {
        return optionId;
    }

    public void setOptionId(long optionId) {
        this.optionId = optionId;
    }

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public String getOptionName() {
        return optionName;
    }

    public void setOptionName(String optionName) {
        this.optionName = optionName;
    }

    public int getExtraPrice() {
        return extraPrice;
    }

    public void setExtraPrice(int extraPrice) {
        this.extraPrice = extraPrice;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    @Override
    public String toString() {
        return String.format("%s (+%,d원)", optionName, extraPrice);
    }
}
