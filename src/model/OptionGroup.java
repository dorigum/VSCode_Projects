package model;
import model.Option;
import java.util.List;

public class OptionGroup {
    private long optionGroupId;
    private String groupName;   
    private List<Option> options;

    public long getOptionGroupId() { return optionGroupId; }
    public String getGroupName() { return groupName; }
    public List<Option> getOptions() { return options; }


}


// CREATE TABLE `OPTION_GROUP` (
//   `group_id` int PRIMARY KEY AUTO_INCREMENT,
//   `group_name` varchar(30) NOT NULL COMMENT '사이즈 / 온도 / 카페인 / 휘핑'
// );