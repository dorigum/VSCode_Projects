package model;

import java.util.ArrayList;
import java.util.List;

/**
 * 옵션 그룹 (예: 온도, 사이즈, 카페인유무 등)
 * OPTION_GROUP 테이블과 매핑됩니다.
 */
public class OptionGroup {
    private long groupId;
    private String groupName;
    private List<MenuOption> options = new ArrayList<>();

    // 기본 생성자
    public OptionGroup() {}

    // 데이터베이스 조회용 생성자 (ID 포함)
    public OptionGroup(long groupId, String groupName) {
        this.groupId = groupId;
        this.groupName = groupName;
    }

    // 신규 생성용 생성자 (ID 제외)
    public OptionGroup(String groupName) {
        this.groupName = groupName;
    }

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<MenuOption> getOptions() {
        return options;
    }

    public void setOptions(List<MenuOption> options) {
        this.options = options;
    }

    public void addOption(MenuOption option) {
        this.options.add(option);
    }

    @Override
    public String toString() {
        return groupName + " (옵션 " + options.size() + "개)";
    }
}
