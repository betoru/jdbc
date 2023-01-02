package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV3;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 트랜잭션 - @Transaction AOP 적용
 */
@Slf4j
public class MemberServiceV3_3 {

//  private final TransactionTemplate txTemplate;
  private final MemberRepositoryV3 memberRepository;

  //생성자에 로직이 필요해서 어노테이션을 빼고 생성자를 생성함
  //그 로직은 TransactionTemplate 이걸 주입받지 않고 PlatformTransactionManager 를 주입받아서 만들어줌
  public MemberServiceV3_3(MemberRepositoryV3 memberRepository) {
    this.memberRepository = memberRepository;
  }
  @Transactional
  public void accountTransfer(String fromId, String toId, int money) throws SQLException {
    bizLogic(fromId, toId, money);
  }
  private void bizLogic(String fromId, String toId, int money)
      throws SQLException {
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
