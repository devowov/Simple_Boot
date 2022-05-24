package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

    private final EntityManager em;

    public void save(Order order){
        em.persist(order);
    }

    public Order findOne(Long id){
        return em.find(Order.class,id);
    }

    public List<Order> findAll(OrderSearch orderSearch){
        String jpql = "select o from Order o join o.member m ";


        //JPQL
        // 동적쿼리화 시키기(iBatis / MyBatis가 동적쿼리 쓰기에는 좋음)
      /*  return em.createQuery("select o from Order o join o.member m " +
                "where o.status = :status " +
                "and m.name like :name",Order.class)
                .setParameter("status",orderSearch.getOrderStatus())
                .setParameter("name",orderSearch.getMemberName())
                .setMaxResults(1000) // 최대 1000건 조회
                .getResultList();*/

        // 휴먼버그가 충분히 일어날 수 있는 쿼리
       /* boolean isFirstCondition = true;
        if(orderSearch.getOrderStatus() !=null){
            if(isFirstCondition){
                jpql += " where";
                isFirstCondition = false;
            }else{
                jpql+= " and";
            }
        }*/

        return em.createQuery(jpql,Order.class)
                .setMaxResults(1000) // 최대 1000건 조회
                .getResultList();
    }

    /**
     * JPA Criteria
     * -> JPA 에서 재공하는 표준 동적쿼리
     * 가독성도 안좋고 유지보수도 별로임....
     */
    public List<Order> findAllByCriteria(OrderSearch orderSearch){
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery(Order.class);
        Root<Order> o = cq.from(Order.class);
        Join<Object,Object> m = o.join("member", JoinType.INNER);

        List<Predicate> criteria = new ArrayList<>();

        // 주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            Predicate status = cb.equal(o.get("status"), orderSearch.getOrderStatus());
            criteria.add(status);
        }

        // 회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            Predicate name = cb.like(m.get("name"),"%"+orderSearch.getMemberName()+"%");
            criteria.add(name);
        }

        cq.where(cb.and(criteria.toArray(new Predicate[criteria.size()])));
        TypedQuery<Order> query = em.createQuery(cq).setMaxResults(1000);
        return query.getResultList();
    }
}