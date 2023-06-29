package dblab.sharing_flatform.factory;

import dblab.sharing_flatform.domain.address.Address;
import dblab.sharing_flatform.domain.member.Member;

import java.util.List;

public class MemberFactory {

    public static Member createMember() {
        return new Member("username1", "password1", "01066096301", createAddress(), List.of(), List.of());
    }

    public static Address createAddress() {
        return new Address("전주시", "덕진구", "금암동 1길", "45532");
    }

}
