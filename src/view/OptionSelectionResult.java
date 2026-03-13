package view;

public class OptionSelectionResult {
    private final boolean confirmed;
    private final OptionSelection selection;

    private OptionSelectionResult(boolean confirmed, OptionSelection selection) {
        this.confirmed = confirmed;
        this.selection = selection;
    }

    public static OptionSelectionResult confirmed(OptionSelection selection) {
        return new OptionSelectionResult(true, selection);
    }

    public static OptionSelectionResult cancelled(OptionSelection selection) {
        return new OptionSelectionResult(false, selection);
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public OptionSelection getSelection() {
        return selection;
    }
}
