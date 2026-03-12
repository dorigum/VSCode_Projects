package view;

import java.util.List;
import model.Menu;
import model.Category;
import model.Member;
import model.Order;

public class EndView {
    /**
     * 성공 메시지 출력
     */
    public static void printMessage(String message) {
        System.out.println(message);
    }

    /**
     * 메뉴 목록 출력
     */
    public static void printMenuList(List<Menu> menus) {
        if (menus.isEmpty()) {
            System.out.println("메뉴가 없습니다.");
        } else {
            menus.forEach(System.out::println);
        }
    }

    /**
     * 카테고리 목록 출력
     */
    public static void printCategoryList(List<Category> categories) {
        if (categories.isEmpty()) {
            System.out.println("카테고리가 없습니다.");
        } else {
            categories.forEach(System.out::println);
        }
    }

    /**
     * 회원 목록 출력
     */
    public static void printMemberList(List<Member> members) {
        if (members.isEmpty()) {
            System.out.println("회원 정보가 없습니다.");
        } else {
            members.forEach(System.out::println);
        }
    }

    /**
     * 주문 목록 출력
     */
    public static void printOrderList(List<Order> orders) {
        if (orders.isEmpty()) {
            System.out.println("주문 내역이 없습니다.");
        } else {
            orders.forEach(System.out::println);
        }
    }
}
