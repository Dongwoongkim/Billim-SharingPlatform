package dblab.sharing_flatform.domain.member;

import dblab.sharing_flatform.domain.address.Address;
import dblab.sharing_flatform.domain.post.Post;
import dblab.sharing_flatform.domain.role.Role;
import dblab.sharing_flatform.domain.role.RoleType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(name = "username", nullable = false)
    private String name;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String phoneNumber;

    @Embedded
    private Address address;

    // role
    @OneToMany
    @JoinColumn(name = "role_id")
    private List<MemberRole> roles = new ArrayList<>();

    // post
    @OneToMany(mappedBy = "member")
    private List<Post> posts = new ArrayList<>();

    public Member(String name, String password, String phoneNumber, Address address, List<Role> roles , List<Post> posts) {
        this.name = name;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.address = address;

        addPosts(posts);
        addRoles(roles);
    }

    private void addRoles(List<Role> roles) {
        List<MemberRole> roleList = roles.stream().map(role -> new MemberRole(this, role)).collect(Collectors.toList());
        this.roles = roleList;
    }

    private void addPosts(List<Post> posts) {
        posts.stream().forEach(
                p -> {
                    posts.add(p);
                    p.addMember(this);
                }
        );
    }
}
