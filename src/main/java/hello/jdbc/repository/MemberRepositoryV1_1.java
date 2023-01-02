package hello.jdbc.repository;

import hello.jdbc.connection.DBConnectionUtil;
import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.NoSuchElementException;

/**
 * JDBC - DataSource 애플리케이션에 적용, JdbcUtils 사용
 */
@Slf4j
public class MemberRepositoryV1_1 {
  //DataSource 의존관계 주입
  private final DataSource dataSource;

  public MemberRepositoryV1_1(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  public Member save(Member member) throws SQLException {
    String sql = "insert into member(member_id, money) values(?,?)";
    Connection con = null;
    //PreparedStatement 와 Statement 의 차이점
    //PreparedStatement 는 sql 을 미리 컴파일 해놓고 실행할 때 바인딩 값을 넣어서 실행한다.
    //Statement 는 단순히 sql 을 실행
    PreparedStatement pstm = null;

    try {
      con = getConnection();
      pstm = con.prepareStatement(sql);
      pstm.setString(1, member.getMemberId());
      pstm.setInt(2, member.getMoney());
      pstm.executeUpdate();
      return member;
    } catch (SQLException e) {
      log.error("db error", e);
      throw e;
    }finally {
      close(con, pstm, null);
    }
  }

  //조회
  public Member findById(String memberId) throws SQLException {
    String sql = "select * from member where member_id =?";
    Connection con = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    try {
      con = getConnection();
      pstmt = con.prepareStatement(sql);
      pstmt.setString(1, memberId);
      rs = pstmt.executeQuery();
      if (rs.next()) {
        Member member = new Member();
        member.setMemberId(rs.getString("member_id"));
        member.setMoney(rs.getInt("money"));
        return member;
      } else {
        throw new NoSuchElementException("member not found memberId=" + memberId);
      }
    } catch (SQLException e) {
      log.info("db error",e);
      throw e;
    } finally {
      close(con, pstmt, rs);
    }
  }

  public void update(String memberId, int money) throws SQLException {
    String sql = "update member set money=? where member_id=?";
    Connection con = null;
    PreparedStatement pstmt = null;
    try {
      con = getConnection();
      pstmt = con.prepareStatement(sql);
      pstmt.setInt(1, money);
      pstmt.setString(2, memberId);
      int resultSize = pstmt.executeUpdate();
      log.info("resultSize={}", resultSize);

    } catch (SQLException e) {
      log.info("db error",e);
      throw e;
    }
    finally {
      close(con, pstmt, null);
    }
  }

  public void delete(String memberId) throws SQLException {
    String sql = "delete from member where member_id=?";
    Connection con = null;
    PreparedStatement pstmt = null;
    try {
      con = getConnection();
      pstmt = con.prepareStatement(sql);
      pstmt.setString(1, memberId);
      pstmt.executeUpdate();
    } catch (SQLException e) {
      log.info("db error",e);
      throw e;
    }
    finally {
      close(con, pstmt, null);
    }
  }

  private void close(Connection con, Statement stmt, ResultSet rs) {
    //스프링이 제공하는 JdbcUtils 메서드 사용
    JdbcUtils.closeResultSet(rs);
    JdbcUtils.closeStatement(stmt);
    JdbcUtils.closeConnection(con);
  }
  private Connection getConnection() throws SQLException {
    Connection con = dataSource.getConnection();
    log.info("get connection={} class={}", con, con.getClass());
    return con;
  }

}
