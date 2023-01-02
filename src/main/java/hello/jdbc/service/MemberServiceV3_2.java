package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 템플릿 콜백 패턴이 적용된 트랜잭션 템플릿 방식
 */
@Slf4j
public class MemberServiceV3_2 {

  //private final DataSource dataSource;
//  private final PlatformTransactionManager transactionManager;
  private final TransactionTemplate txTemplate;
  private final MemberRepositoryV3 memberRepository;

  //생성자에 로직이 필요해서 어노테이션을 빼고 생성자를 생성함
  //그 로직은 TransactionTemplate 이걸 주입받지 않고 PlatformTransactionManager 를 주입받아서 만들어줌
  public MemberServiceV3_2(PlatformTransactionManager transactionManager, MemberRepositoryV3 memberRepository) {
    this.txTemplate = new TransactionTemplate(transactionManager);
    this.memberRepository = memberRepository;
  }

  public void accountTransfer(String fromId, String toId, int money) throws SQLException {
    txTemplate.executeWithoutResult((status) -> {
      try {
        bizLogic(fromId, toId, money);
      } catch (SQLException e) {
        throw new RuntimeException(e);
      }
    });
    /*//트랜잭션 시작, 반환값인 status 는 커밋할 때 넘겨줘야함
    TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());//속성,옵션은 나중에

    try {
      //비즈니스 로직 시작
      bizLogic(fromId, toId, money);
      transactionManager.commit(status); //성공시 트랜잭션 커밋
    } catch (Exception e) {
      transactionManager.rollback(status); //실패시 트랜잭션 롤백
      throw new IllegalStateException(e);
    }*/
    /*
    이건 더아싱 필요가 없어 커밋이나 롤백할 때 release 를 지가 해줄거니까
    finally {
      release(con);
    }*/
  }

  private void bizLogic(String fromId, String toId, int money)
      throws SQLException {
    Member fromMember = memberRepository.findById(fromId); //원래는 exception을 막 던지면 안돼.
    Member toMember = memberRepository.findById(toId); //원래는 exception을 막 던지면 안돼.

    memberRepository.update(fromId, fromMember.getMoney() - money);
    validation(toMember);
    memberRepository.update(toId, toMember.getMoney() + money);
  }

  private void release(Connection con) {
    if (con != null) {
      try {
        con.setAutoCommit(true); //트랜잭션 종료 -> pool에 반납하기 전에 다시 원래대로 돌려놔야함.
        con.close();
      } catch (Exception e) {
        log.info("error", e);
      }
    }
  }

  private void validation(Member toMember) {
    if (toMember.getMemberId().equals("ex")) {
      throw new IllegalStateException("이체중 예외 발생");
    }
  }


}
