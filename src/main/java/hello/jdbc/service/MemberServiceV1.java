package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV1;
import java.sql.SQLException;
import lombok.RequiredArgsConstructor;

/**
 * 서비스레이어는 순수자바를 지향
 * 순수자바는 자바의 표준 API만을 사용하여 개발하는 것을 말한다.
 * SQLException을 보면 JDBC 기술에 종속되며, MongoDB나 JPA를 쓰면 다른 예외가 올라온다.
 * 그래서 컴파일 오류가 발생하게 된다.
 * 참고)RuntimeException을 상속받은 예외를 사용하면 된다.
 */
public class MemberServiceV1 {

  private final MemberRepositoryV1 memberRepository;

  public MemberServiceV1(MemberRepositoryV1 memberRepository) {
    this.memberRepository = memberRepository;
  }

  public void accountTransfer(String fromId, String toId, int money) throws SQLException {

    Member fromMember = memberRepository.findById(fromId); //원래는 exception을 막 던지면 안돼.
    Member toMember = memberRepository.findById(toId); //원래는 exception을 막 던지면 안돼.

    memberRepository.update(fromId, fromMember.getMoney() - money);
    validation(toMember);
    memberRepository.update(toId, toMember.getMoney() + money);

  }
  private void validation(Member toMember) {
    if (toMember.getMemberId().equals("ex")) {
      throw new IllegalStateException("이체중 예외 발생");
    }
  }


}
