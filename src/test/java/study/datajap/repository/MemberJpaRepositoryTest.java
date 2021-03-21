package study.datajap.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajap.entity.Member;
import study.datajap.repository.MemberJpaRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberJpaRepositoryTest {

    @Autowired
    MemberJpaRepository memberJpaRepository;

    @Test
    public void testMember(){
        Member member = new Member("memberA");
        //이렇게 setter사용하는거보다 생성시 생성자에 인자줘서 값넣어주는게 더 좋은방법임
        Member saveMember = memberJpaRepository.save(member);

        Member findMember = memberJpaRepository.find(saveMember.getId());

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);
        //
    }

    @Test
    public void basicCRUD() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);

        //단건 조회 검증
        Member findMember1 = memberJpaRepository.findById(member1.getId()).get();
        Member findMember2 = memberJpaRepository.findById(member2.getId()).get();

        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        //변경 감지를 통한 수정.
        findMember1.setUsername("member!!!!");


        //리스트 조회 검증
       List<Member> all = memberJpaRepository.findAll();

        for (Member m: all) {
            System.out.println("member1 = "+ m);
        }
        assertThat(all.size()).isEqualTo(2);

        //카운트 검증
        long count = memberJpaRepository.count();
        assertThat(count).isEqualTo(2);

        //삭제 검증
        memberJpaRepository.delete(member1);
        memberJpaRepository.delete(member2);

        long deleteCount = memberJpaRepository.count();
        assertThat(deleteCount).isEqualTo(0);

    } //memberJpaRepository에 만들었던 CRD 메서드의 기능 테스트

    @Test
    public void findByUsernameAndGreaterThen(){
        Member m1 = new Member("AAA",11);
        Member m2 = new Member("AAA",20);
        memberJpaRepository.save(m1);
        memberJpaRepository.save(m2);

        List<Member> members = memberJpaRepository
                .findByUsernameAndGreaterThen("AAA", 15);
        assertThat(members.get(0).getUsername()).isEqualTo("AAA");
        assertThat(members.get(0).getAge()).isEqualTo(20);
        assertThat(members.size()).isEqualTo(1);
    }

    @Test
    public void testNamedQuery(){
        Member m1 = new Member("AAA",10);
        Member m2 = new Member("BBB",20);
        memberJpaRepository.save(m1);
        memberJpaRepository.save(m2);

        List<Member> result = memberJpaRepository.findByUsername("AAA");
        Member findMember = result.get(0);
        assertThat(findMember).isEqualTo(m1);
    }

    @Test
    public void paging(){
        memberJpaRepository.save(new Member("member1",10));
        memberJpaRepository.save(new Member("member2",10));
        memberJpaRepository.save(new Member("member3",10));
        memberJpaRepository.save(new Member("member4",10));
        memberJpaRepository.save(new Member("member5",10));

        int age = 10;
        int offset = 0;
        int limit = 3;

        List<Member> members = memberJpaRepository.findBypage(age, offset, limit);
        long totalCount = memberJpaRepository.totalCount(age);

        //페이지 계산 공식 적용...
        // totalPage = totalCount / size ...
        // 마지막 페이지 ...
        // 최초 페이지 .. 다 은근 복잡함

        assertThat(members.size()).isEqualTo(3);
        assertThat(totalCount).isEqualTo(5);
    }//순수 jpa로 작성한 페이징 쿼리

    @Test
    public void bulkUpdate(){
        memberJpaRepository.save(new Member("member1",10));
        memberJpaRepository.save(new Member("member2",19));
        memberJpaRepository.save(new Member("member3",20));
        memberJpaRepository.save(new Member("member4",21));
        memberJpaRepository.save(new Member("member5",40));

        int resultCount = memberJpaRepository.bulkAgePlus(20);

        assertThat(resultCount).isEqualTo(3);
    }
}