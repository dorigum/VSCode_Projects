package view;

public final class FailView {
    private FailView() {
    }

    public static void fail(String message) {
        System.out.println("실패: " + message);
    }
}
