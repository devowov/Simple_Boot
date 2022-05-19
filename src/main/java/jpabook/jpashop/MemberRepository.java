package jpabook.jpashop;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

// Entity를 찾아주는 역할(dao)

@Repository // Spring이 제공하는 기본 타입( Component의 대상이 됨)
public class MemberRepository {

    @PersistenceContext
    private EntityManager em;

    public Long save(Member member){
        em.persist(member);
        // command와 Query를 분리함
        return member.getId();
    }

    public Member find(Long id){
        return em.find(Member.class,id);
    }
}
