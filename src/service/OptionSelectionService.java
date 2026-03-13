package service;

import model.MenuOption;
import model.OptionGroup;
import model.OptionSelection;

import java.util.ArrayList;
import java.util.List;

public class OptionSelectionService {
    public OptionSelection createDefaultSelection(List<OptionGroup> optionGroups) {
        List<MenuOption> selectedOptions = new ArrayList<>();
        if (optionGroups == null) {
            return new OptionSelection(new ArrayList<>(), selectedOptions);
        }

        for (OptionGroup optionGroup : optionGroups) {
            MenuOption defaultOption = findDefaultOption(optionGroup);
            if (defaultOption != null) {
                selectedOptions.add(defaultOption);
            }
        }
        return new OptionSelection(optionGroups, selectedOptions);
    }

    public OptionSelection changeSelection(OptionSelection currentSelection, OptionGroup optionGroup, MenuOption menuOption) {
        List<MenuOption> updatedOptions = currentSelection.getSelectedOptions();
        updatedOptions.removeIf(option -> option.getGroupId() == optionGroup.getGroupId());
        updatedOptions.add(menuOption);
        return new OptionSelection(currentSelection.getOptionGroups(), updatedOptions);
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
