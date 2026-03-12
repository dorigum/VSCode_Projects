package service;

import model.Member;
import model.OrderItem;
import repository.MemberRepository;
import java.util.List;

public class MemberService {
    private MemberRepository memberRepository = new MemberRepository();

    // 로그인 비즈니스 로직
    public Member login(long memberId, String password) {
        Member member = memberRepository.login(memberId, password);
        if (member != null) {
            System.out.println("로그인 성공! 환영합니다, " + member.getMemberId() + "번 회원님.");
            return member;
        } else {
            System.out.println("로그인 실패: 회원 번호 또는 비밀번호를 확인하세요.");
            return null;
        }
    }

    // 주문 내역 출력 로직
    public void showOrderHistory(Member member) {
        if (member == null) return;
        
        List<OrderItem> history = memberRepository.getOrderHistory(member.getMemberId());
        System.out.println("\n===== " + member.getMemberId() + "번 회원님의 주문 상세 내역 =====");
        if (history.isEmpty()) {
            System.out.println("주문 내역이 없습니다.");
        } else {
            for (OrderItem item : history) {
                System.out.println(item);
            }
        }
    }
}
