package dblab.sharing_platform.domain.report;

import dblab.sharing_platform.domain.member.Member;
import dblab.sharing_platform.domain.post.Post;
import dblab.sharing_platform.factory.member.MemberFactory;
import dblab.sharing_platform.factory.post.PostFactory;
import dblab.sharing_platform.factory.report.ReportFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ReportTest {


    @Test
    @DisplayName("게시글 신고 생성")
    public void createPostReport() throws Exception {
        //given
        Member reporter = MemberFactory.createMember("reported");
        Post post = PostFactory.createPost();

        Report report = ReportFactory.createPostReport(reporter, post);

        //then
        assertNotNull(report);
        assertThat(post.getMember()).isEqualTo(report.getReported());
    }

    @Test
    @DisplayName("버그 신고 생성")
    public void createMemberReport() throws Exception {
        //given
        Member member = MemberFactory.createMember("reporter");

        Report report = ReportFactory.createBugReport(member);

        //then
        assertNotNull(report);
    }

}