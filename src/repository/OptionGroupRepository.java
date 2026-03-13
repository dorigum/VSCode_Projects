package repository;

import model.OptionGroup;
import java.util.List;

public interface OptionGroupRepository {
    List<OptionGroup> findAll();
    List<OptionGroup> findOptionGroupsWithOptionsByMenuId(long menuID);
    OptionGroup findById(long groupId);
    void save(OptionGroup optionGroup);
    void delete(long groupId);
}
