package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV1_1;
import lombok.RequiredArgsConstructor;

import java.sql.SQLException;

/**
 * fileName       : MemberServiceV1_1
 * author         : baehyoyeol
 * date           : 2022/12/30
 * description    :
 */
@RequiredArgsConstructor
public class MemberServiceV1_1 {
    private final MemberRepositoryV1_1 memberRepository;

    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        Member fromMember = memberRepository.findById(fromId);
        Member toMember = memberRepository.findById(toId);

        memberRepository.update(fromId, fromMember.getMoney() - money);
        validation(toMember);
        memberRepository.update(toId, toMember.getMoney() + money);
    }

    private static void validation(Member toMember) {
        if (toMember.getMemberId().equals("ex")) {
            throw new IllegalStateException("강제 예외 발생");
        }
    }
}
