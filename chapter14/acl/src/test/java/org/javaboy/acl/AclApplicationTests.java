package org.javaboy.acl;

import org.javaboy.acl.model.NoticeMessage;
import org.javaboy.acl.service.NoticeMessageService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.jdbc.JdbcMutableAclService;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class AclApplicationTests {

    @Autowired
    JdbcMutableAclService jdbcMutableAclService;

    @Autowired
    NoticeMessageService noticeMessageService;
    @Test
    @WithMockUser(username = "manager")
    public void test07() {
        NoticeMessage noticeMessage = new NoticeMessage();
        noticeMessage.setId(99);
        noticeMessage.setContent("999");
        noticeMessageService.save(noticeMessage);
    }

    @Test
    @WithMockUser(username = "hr")
    public void test05() {
        // 基于test04 hr 有该NoticeMessage(1)的写权限，则可写
        NoticeMessage msg = noticeMessageService.findById(1);
        assertNotNull(msg);
        assertEquals(1, msg.getId());
        msg.setContent("javaboy-1111");
        noticeMessageService.update(msg);
        msg = noticeMessageService.findById(1);
        assertNotNull(msg);
        assertEquals("javaboy-1111", msg.getContent());
    }

    @Test
    @WithMockUser(username = "javaboy")
    @Transactional
    @Rollback(value = false)    // 测试方便就不自动回滚了
    public void test06() {
        ObjectIdentity objectIdentity = new ObjectIdentityImpl(NoticeMessage.class, 99);
        Permission p = BasePermission.CREATE;
        MutableAcl acl = jdbcMutableAclService.createAcl(objectIdentity);
        acl.insertAce(acl.getEntries().size(), p, new PrincipalSid("manager"), true);
        jdbcMutableAclService.updateAcl(acl);
    }

    @Test
    @WithMockUser(username = "javaboy")
    @Transactional
    @Rollback(value = false)    // 测试方便就不自动回滚了
    public void test04() {
        // 授予hr 对ObjectIdentity = 1(也就是NoticeMessage id = 1)有写的权限
        ObjectIdentity objectIdentity = new ObjectIdentityImpl(NoticeMessage.class, 1);
        Permission p = BasePermission.WRITE;
        MutableAcl acl = (MutableAcl) jdbcMutableAclService.readAclById(objectIdentity);
        acl.insertAce(acl.getEntries().size(), p, new PrincipalSid("hr"), true);
        jdbcMutableAclService.updateAcl(acl);
    }

    @Test
    @WithMockUser(username = "hr")
    public void test03() {
        // 由于test02 已经给hr用户对于NoticeMessage id = 1授权了READ，则该用户可读取这一条记录
        List<NoticeMessage> all = noticeMessageService.findAll();
        assertNotNull(all);
        assertEquals(1, all.size());
        assertEquals(1, all.get(0).getId());

        NoticeMessage byId = noticeMessageService.findById(1);
        assertNotNull(byId);
        assertEquals(1, byId.getId());
    }


    @Test
    @WithMockUser(username = "javaboy")
    @Transactional
    @Rollback
    public void test02() {
        // mock user是javaboy，也就是这个Acl对象创建好之后，它的owner是javaboy
        // 会自动向acl_sid表中添加一条记录，值为javaboy
        // 在方法执行过程中，会分别向acl_entry、acl_object_identity以及acl_sid三张表中添加记录，因此需要添加事务
        ObjectIdentity objectIdentity = new ObjectIdentityImpl(NoticeMessage.class, 1);
        Permission p = BasePermission.READ;
        MutableAcl acl = jdbcMutableAclService.createAcl(objectIdentity);
        // javaboy创建了一个权限：hr用户 有 ObjectIdentity = 1 的读权限
        acl.insertAce(acl.getEntries().size(), p, new PrincipalSid("hr"), true);
        jdbcMutableAclService.updateAcl(acl);
    }

    @Test
    @WithMockUser(username = "manager")
    public void test01() {
        List<NoticeMessage> all = noticeMessageService.findAll();
        assertEquals(0, all.size());
    }


}
