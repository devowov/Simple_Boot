package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true) // 기본값
@RequiredArgsConstructor // final이 있는 필드만 생성자를 만들어준다.
public class MemberService {

    // @Autowired 필드에 주입하는 방법과 Setter생성 후 Setter에 주입하는 방법으로 나누어진다,
    // 생성자 인잭션을 더 권장(최신 Spring인 경우 annotation없이 생성자 하나만 있으면
    // 자동으로 @Autowired 설정을 해준다.

    private final MemberRepository memberRepository;

    /**
     * 회원가입
     */
    @Transactional
    public Long join(Member member){

        validateDuplicateMember(member);
        memberRepository.save(member);
        return member.getId();
    }

    /**
     * 중복회원검증
     * DB에서 중복에 걸릴 항목을 UK로 걸어두는 최후의 방어도 권장
     * -> 아래의 방법은 WAS에서 동시처리되는 데이터를 잡을 수 없음
     */
    private void validateDuplicateMember(Member member) {
        // 간단하게 멤버 수를 새는것도 괜춘
        List<Member> findMembers = memberRepository.findByName(member.getName());

        if(!findMembers.isEmpty()){
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    /**
     * 회원조회
     */
    public List<Member> findMembers(){
        return memberRepository.findAll();
    }

    public Member findOne(Long memberId){
        return memberRepository.findOne(memberId);
    }
}
