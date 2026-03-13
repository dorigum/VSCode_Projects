package model;

import java.util.ArrayList;
import java.util.List;

public class OptionSelection {
    private final List<OptionGroup> optionGroups;
    private final List<MenuOption> selectedOptions;

    public OptionSelection(List<OptionGroup> optionGroups, List<MenuOption> selectedOptions) {
        this.optionGroups = optionGroups == null ? new ArrayList<>() : new ArrayList<>(optionGroups);
        this.selectedOptions = selectedOptions == null ? new ArrayList<>() : new ArrayList<>(selectedOptions);
    }

    public List<OptionGroup> getOptionGroups() {
        return new ArrayList<>(optionGroups);
    }

    public List<MenuOption> getSelectedOptions() {
        return new ArrayList<>(selectedOptions);
    }

    public MenuOption getSelectedOption(long groupId) {
        for (MenuOption option : selectedOptions) {
            if (option.getGroupId() == groupId) {
                return option;
            }
        }
        return null;
    }

    public boolean isSelected(long groupId, long optionId) {
        MenuOption selectedOption = getSelectedOption(groupId);
        return selectedOption != null && selectedOption.getOptionId() == optionId;
    }
}
