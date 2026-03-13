package repository;

import model.OptionGroup;
import java.util.List;

public interface OptionGroupRepository {
    List<OptionGroup> findAll();
    OptionGroup findById(long groupId);
    void save(OptionGroup optionGroup);
    void delete(long groupId);
}
