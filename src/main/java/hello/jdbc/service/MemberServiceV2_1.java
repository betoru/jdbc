package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV2_1;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 기본적인 트랜잭션을 위해 서비스에서 repository 호출 시 connection 을 넘겨준다.
 */
@Slf4j
public class MemberServiceV2_1 {

  //커넥션을 넘겨주기 위해 DataSource 를 추가한다.
  private final DataSource dataSource;

  private final MemberRepositoryV2_1 memberRepository;

  public MemberServiceV2_1(DataSource dataSource, MemberRepositoryV2_1 memberRepository) {
    this.dataSource = dataSource;
    this.memberRepository = memberRepository;
  }

  public void accountTransfer(String fromId, String toId, int money) throws SQLException {
    Connection con = dataSource.getConnection();
    try {
      con.setAutoCommit(false);//트랜잭션 시작

      //비즈니스 로직 수행
      bizLogic(con, fromId, toId, money);
      con.commit();//모든 로직이 정상 수행됐다면 트랜잭션 커밋

    } catch (Exception e) {
      con.rollback();//예외가 발생하면 롤백
      throw new IllegalStateException(e);
    } finally {
      if (con != null) {
        release(con);
      }
    }
  }

  private void bizLogic(Connection con, String fromId, String toId, int money) throws SQLException {
    Member fromMember = memberRepository.findById(con, fromId); //원래는 exception을 막 던지면 안돼.
    Member toMember = memberRepository.findById(con, toId); //원래는 exception을 막 던지면 안돼.

    memberRepository.update(con, fromId, fromMember.getMoney() - money);
    validation(toMember);
    memberRepository.update(con, toId, toMember.getMoney() + money);
  }

  private static void release(Connection con) {
    try {
      //close 하면 커넥션이 풀로 돌아가는데 변경된 autoCommit 상태도 유지된 채 돌아간다.
      con.setAutoCommit(true);
      con.close();
    } catch (Exception e) {
      log.info("error", e);
    }
  }

  private void validation(Member toMember) {
    if (toMember.getMemberId().equals("ex")) {
      throw new IllegalStateException("이체중 예외 발생");
    }
  }


}
