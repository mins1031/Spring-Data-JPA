package study.datajap.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajap.dto.MemberDto;
import study.datajap.entity.Member;
import study.datajap.entity.Team;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.OPTIONAL;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberRepositoryTest {

    @Autowired MemberRepository memberRepository;
    @Autowired TeamRepository teamRepository;
    @PersistenceContext
    EntityManager em;
    //중요한게 있는데 영속성 컨택스트는 하나의 트랜잭션 안에 살아있기떄문에
    //위의 멤버,팀리포지토리 모두 위의 선언된 영속성컨택스트를 사용한다고함...이건 공부가 필요할듯


    @Test
    public void testInterface(){
        System.out.println("memberRepository = " + memberRepository.getClass());
        //memberRepository = class com.sun.proxy.$Proxy99 이렇게 출력됨
        //즉 jpa가 인터페이스의 "구현체"를 프록시객체로 만들어 꽂아주는 형식임
        //구현체 안만들어도됨 ㄷㄷ;;
    }

    @Test
    public void basicCRUD() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        //단건 조회 검증
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();

        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        //변경 감지를 통한 수정.
        findMember1.setUsername("member!!!!");


        //리스트 조회 검증
        List<Member> all = memberRepository.findAll();

        for (Member m: all) {
            System.out.println("member1 = "+ m);
        }
        assertThat(all.size()).isEqualTo(2);

        //카운트 검증
        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        //삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);

        long deleteCount = memberRepository.count();
        assertThat(deleteCount).isEqualTo(0);

    } //memberJpaRepository에 만들었던 CRD 메서드의 기능 테스트
    //이게 레포지토리에 아무것도 구현하지 않고 JpaRepository가 지원하는 것들을 사용함

    @Test
    public void findByUsernameAndGreaterThen(){
        Member m1 = new Member("AAA",11);
        Member m2 = new Member("AAA",20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> members = memberRepository
                .findByUsernameAndAgeGreaterThan("AAA", 15);
        assertThat(members.get(0).getUsername()).isEqualTo("AAA");
        assertThat(members.get(0).getAge()).isEqualTo(20);
        assertThat(members.size()).isEqualTo(1);
    }

    @Test
    public void testNameQuery(){
        Member m1 = new Member("AAA",10);
        Member m2 = new Member("BBB",20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsername("AAA");
        Member findMember = result.get(0);
        assertThat(findMember).isEqualTo(m1);
    } //인터페이스findByUsername가 실행됨.

    @Test
    public void testQuery(){
        Member m1 = new Member("AAA",10);
        Member m2 = new Member("BBB",20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findUser("AAA",10);
        assertThat(result.get(0).getUsername()).isEqualTo(m1);
    }

    @Test
    public void findUsernameList(){
        Member m1 = new Member("AAA",10);
        Member m2 = new Member("BBB",20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<String > result = memberRepository.findUsernameList();
        for (String m : result) {
            System.out.println("m+ "+ m);
        }
    }

    @Test
    public void findMemberDto(){

        Team team = new Team("teamA");
        teamRepository.save(team);

        Member m1 = new Member("AAA",10);
        m1.setTeam(team);
        memberRepository.save(m1);

        List<MemberDto> result = memberRepository.findMemberDto();
        for (MemberDto m : result) {
            System.out.println("m+ "+ m);
        }
    }

    @Test
    public void returnType(){
        Member m1 = new Member("AAA",10);
        Member m2 = new Member("BBB",20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> aaa = memberRepository.findListByUsername("AAA");
        Member aaa2 = memberRepository.findMemberByUsername("AAA");
        Optional<Member> aaa3 = memberRepository.findOptionalByUsername("AAA");
        //위 코드는 같은 값을 다른 타입으로 제공할수 있는 스프링의 장점을 보여주기위한 코드
        //1)다만 리스트의 경우 파라미터를 잘못주어 디비에 없는 값을 호출해도 에러가 아니라
        // empty컬렉션을 제공해줌. 해서 List는 널포인터 오류가 안나고 무조건 리스트가 나옴
        //2) Member로 받는 형태는 null로 나와 오류는 안뜨지만 null값이 입력되 출력됨.
        //3) 그래서 파라미터 값이 있는지 없는지 모르면 Optional을 사용해주는게 맞음.
        //2),3)의경우에 만약 "AAA"의 값이 디비에 2개이상있으면 둘다 오류가 터짐.
    }

    @Test
    public void paging() {
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3,
                Sort.by(Sort.Direction.DESC, "username"));
        //페이징을 0페이지 부터 3개 가져올거임. 이고 정렬은 username타입을 내림차순으로 적용

        Page<Member> pages = memberRepository.findByAge(age, pageRequest);
        //반환 타입을 Page로 받으면 totalCount를 따로 만들어줄필요없이 total을 자동으로
        //해줌;;
        Page<MemberDto> dtoPage = pages.map(m -> new MemberDto(m.getId(), m.getUsername(), null));
        //Page를 사용하면서 엔티티를 Dto로 변환하는 방식을 보여 주심.실무에서 중요함

        List<Member> content = pages.getContent();
        long totalElements = pages.getTotalElements();

       /* for (Member m: content) {
            System.out.println("member = "+m);
        }
        System.out.println("total = " + totalElements);

        assertThat(content.size()).isEqualTo(3);
        assertThat(pages.getTotalElements()).isEqualTo(5);
        assertThat(pages.getNumber()).isEqualTo(0);//
        assertThat(pages.getTotalPages()).isEqualTo(2);//페이지의 총갯수가 2개인가?
        assertThat(pages.isFirst()).isTrue();//첫번째 페이지 인가?
        assertThat(pages.hasNext()).isTrue();//다음 페이지가 있는가?

        */
    }
        @Test
        public void bulkUpdate(){
            memberRepository.save(new Member("member1",10));
            memberRepository.save(new Member("member2",19));
            memberRepository.save(new Member("member3",20));
            memberRepository.save(new Member("member4",21));
            memberRepository.save(new Member("member5",40));

            int resultCount = memberRepository.bulkAgePlus(20);
            em.flush();
            em.clear();
            //벌크연산으로 디비와 영속성컨택스트 데이터 불일치 문제를 예방하려면
            //벌크 연산후엔 무조건 영속성컨택스트를 비워줘야 한다.
            //스프릥 데이터 jpa는 위의 flush,clear없이 인터페이스 메서드의
            //@Modifying(clearAutomatically= true) 설정으로 위의 내용을 적용할수 있음.

            assertThat(resultCount).isEqualTo(3);

            Member testm5 = memberRepository.findMemberByUsername("member5");
            System.out.println(testm5.getAge());
        }
    @Test
    public void findMemberLazy(){
        //given
        //member1 -> teamA
        //member2 -> teamB

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);
        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member1", 10, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        List<Member> members = memberRepository.findAll();

        for (Member m: members) {
            System.out.println("member = " + m.getUsername());
        }

    }

    @Test
    public void queryHint() {
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);
        em.flush();
        em.clear();

        Member findMember = memberRepository.
                findReadOnlyByUsername("member1");
        findMember.setUsername("member2");

        em.flush();

    }
}