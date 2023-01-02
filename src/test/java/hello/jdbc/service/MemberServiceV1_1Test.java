package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV1_1;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.sql.SQLException;

import static hello.jdbc.connection.ConnectionConst.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

/**
 * fileName       : MemberServiceV1_1Test
 * author         : baehyoyeol
 * date           : 2022/12/30
 * description    : 기본 기능만 동작함, 트랜잭션이 없어서 문제 발생 테스트
 */
class MemberServiceV1_1Test {
    public static final String MEMBER_A = "memberA";
    public static final String MEMBER_B = "memberB";
    public static final String MEMBER_EX = "ex";

    private MemberRepositoryV1_1 memberRepository;
    private MemberServiceV1_1 memberService;

    @BeforeEach
    void before() {
        //생성자 주입
        //repository 에서 사용 할 dataSource 를 생성
        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        memberRepository = new MemberRepositoryV1_1(dataSource);
        memberService = new MemberServiceV1_1(memberRepository);
    }

    @AfterEach
    void after() throws SQLException {
        memberRepository.delete(MEMBER_A);
        memberRepository.delete(MEMBER_B);
        memberRepository.delete(MEMBER_EX);
    }

    @Test
    @DisplayName("정상 이체")
    void accountTransfer() throws SQLException {

        //given
        Member memberA = new Member(MEMBER_A, 10000);
        Member memberB = new Member(MEMBER_B, 10000);
        memberRepository.save(memberA);
        memberRepository.save(memberB);

        //when
        memberService.accountTransfer(memberA.getMemberId(), memberB.getMemberId(), 2000);


        //then
        Member findMemberA = memberRepository.findById(memberA.getMemberId());
        Member findMemberB = memberRepository.findById(memberB.getMemberId());
        Assertions.assertThat(findMemberA.getMoney()).isEqualTo(8000);
        Assertions.assertThat(findMemberB.getMoney()).isEqualTo(12000);
    }

    @Test
    @DisplayName("이체 중 예외")
    void accountTransferEx() throws SQLException {
        //given
        Member memberA = new Member(MEMBER_A, 10000);
        Member memberEX = new Member(MEMBER_EX, 10000);
        memberRepository.save(memberA);
        memberRepository.save(memberEX);

        //when
//        assertThrows(IllegalStateException.class, () -> {
//            memberService.accountTransfer(memberA.getMemberId(), memberEX.getMemberId(), 2000);
//        });
        assertThatThrownBy(() -> memberService.accountTransfer(memberA.getMemberId(), memberEX.getMemberId(), 2000))
              .isInstanceOf(IllegalStateException.class);

        //then
        Member findMemberA = memberRepository.findById(memberA.getMemberId());
        Member findMemberB = memberRepository.findById(memberEX.getMemberId());
        Assertions.assertThat(findMemberA.getMoney()).isEqualTo(8000);
        Assertions.assertThat(findMemberB.getMoney()).isEqualTo(10000);
    }
}