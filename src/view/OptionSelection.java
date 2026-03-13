package view;

import model.MenuOption;
import model.OptionGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class OptionSelection {
    private final List<OptionGroup> optionGroups;
    private final Map<Long, MenuOption> selectedByGroup = new LinkedHashMap<>();

    public OptionSelection(List<OptionGroup> optionGroups) {
        this.optionGroups = optionGroups == null ? Collections.emptyList() : optionGroups;
        initializeDefaultSelections();
    }

    public List<OptionGroup> getOptionGroups() {
        return optionGroups;
    }

    public MenuOption getSelectedOption(long groupId) {
        return selectedByGroup.get(groupId);
    }

    public boolean isSelected(long groupId, long optionId) {
        MenuOption selectedOption = selectedByGroup.get(groupId);
        return selectedOption != null && selectedOption.getOptionId() == optionId;
    }

    public void changeSelection(OptionGroup optionGroup, MenuOption menuOption) {
        if (optionGroup == null || menuOption == null) {
            return;
        }
        selectedByGroup.put(optionGroup.getGroupId(), menuOption);
    }

    public List<MenuOption> getSelectedOptions() {
        return new ArrayList<>(selectedByGroup.values());
    }

    private void initializeDefaultSelections() {
        for (OptionGroup optionGroup : optionGroups) {
            MenuOption defaultOption = findDefaultOption(optionGroup);
            if (defaultOption != null) {
                selectedByGroup.put(optionGroup.getGroupId(), defaultOption);
            }
        }
    }

    private MenuOption findDefaultOption(OptionGroup optionGroup) {
        List<MenuOption> options = optionGroup.getOptions();
        if (options == null || options.isEmpty()) {
            return null;
        }

        for (MenuOption option : options) {
            if (option.getDisplayOrder() == 1) {
                return option;
            }
        }
        return options.get(0);
    }
}
