package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV2;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 트랜잭션 처리를 위한 서비스 - 파라미터 연동, 풀을 고려한 종료 처리
 */
@Slf4j
@RequiredArgsConstructor
public class MemberServiceV2 {

  private final DataSource dataSource;
  private final MemberRepositoryV2 memberRepository;

  public void accountTransfer(String fromId, String toId, int money) throws SQLException {

    Connection con = dataSource.getConnection();

    try {
      con.setAutoCommit(false); //트랜잭션 시작
      //비즈니스 로직 시작
      bizLogic(con, fromId, toId, money);
      con.commit(); //성공시 트랜잭션 커밋
    } catch (Exception e) {
      con.rollback(); //실패시 트랜잭션 롤백
      throw new IllegalStateException(e);
    } finally {
      release(con);
    }


  }

  private void bizLogic(Connection con, String fromId, String toId, int money)
      throws SQLException {
    Member fromMember = memberRepository.findById(con, fromId); //원래는 exception을 막 던지면 안돼.
    Member toMember = memberRepository.findById(con, toId); //원래는 exception을 막 던지면 안돼.

    memberRepository.update(con, fromId, fromMember.getMoney() - money);
    validation(toMember);
    memberRepository.update(con, toId, toMember.getMoney() + money);
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
