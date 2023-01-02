package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV1;
import hello.jdbc.repository.MemberRepositoryV2_1;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.sql.SQLException;

import static hello.jdbc.connection.ConnectionConst.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * 기본 동작, 트랜잭션이 없어서 문제 발생
 */
@Slf4j
class MemberServiceV2_1Test {

  public static final String MEMBER_A = "memberA";
  public static final String MEMBER_B = "memberB";
  public static final String MEMBER_EX = "ex";

  private MemberRepositoryV2_1 memberRepository;
  private MemberServiceV2_1 memberService;

  //beforeEach 를 통해서 값을 셋업해줄거임
  @BeforeEach
  void before() {
    DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
    memberRepository = new MemberRepositoryV2_1(dataSource);
    memberService = new MemberServiceV2_1(dataSource, memberRepository);
  }

  @AfterEach
  void after() throws SQLException {
    memberRepository.delete(MEMBER_A);
    memberRepository.delete(MEMBER_B);
    memberRepository.delete(MEMBER_EX);
  }

  @Test
  @DisplayName("정상이체")
  void accountTransfer() throws SQLException {

    //given
    Member memberA = new Member(MEMBER_A, 10000);
    Member memberB = new Member(MEMBER_B, 10000);
    memberRepository.save(memberA);
    memberRepository.save(memberB);

    //when
    log.info("start Transaction");
    memberService.accountTransfer(memberA.getMemberId(), memberB.getMemberId(), 2000);
    log.info("end Transaction");

    //then
    Member findMemberA = memberRepository.findById(memberA.getMemberId());
    Member findMemberB = memberRepository.findById(memberB.getMemberId());
    assertThat(findMemberA.getMoney()).isEqualTo(8000);
    assertThat(findMemberB.getMoney()).isEqualTo(12000);
  }

  @Test
  @DisplayName("이체중 예외 발생")
  void accountTransferEx() throws SQLException {
    //given
    Member memberA = new Member(MEMBER_A, 10000);
    Member memberEx = new Member(MEMBER_EX, 10000);
    memberRepository.save(memberA);
    memberRepository.save(memberEx);

    //when
    assertThatThrownBy(() -> memberService.accountTransfer(memberA.getMemberId(), memberEx.getMemberId(), 2000))
        .isInstanceOf(IllegalStateException.class);

    //then
    Member findMemberA = memberRepository.findById(memberA.getMemberId());
    Member findMemberB = memberRepository.findById(memberEx.getMemberId());
    assertThat(findMemberA.getMoney()).isEqualTo(10000);
    assertThat(findMemberB.getMoney()).isEqualTo(10000);
  }
}