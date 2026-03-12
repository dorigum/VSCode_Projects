package view;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

import controller.AdminController;
import controller.MemberController;
import exception.InfrastructureException;
import service.AdminServiceImpl;
import service.MemberServiceImpl;

public class StartView {
    public static void main(String[] args) {
        try {
            System.setOut(new PrintStream(System.out, true, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new InfrastructureException("콘솔 인코딩 설정에 실패했습니다.", e);
        }

        MenuView menuView = new MenuView();
        AdminController adminController = new AdminController(new AdminServiceImpl());
        MemberController memberController = new MemberController(new MemberServiceImpl());

        menuView.run(adminController, memberController);
    }

    public static void closeScanner() {
        MenuView.close();
    }
}
