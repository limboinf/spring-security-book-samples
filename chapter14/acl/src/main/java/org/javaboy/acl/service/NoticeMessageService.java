package org.javaboy.acl.service;

import org.javaboy.acl.mapper.NoticeMessageMapper;
import org.javaboy.acl.model.NoticeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * CASE: ACL 在NoticeMessage对象上细粒度控制
 * 测试用例见 AclApplicationTests
 */
@Service
public class NoticeMessageService {

    @Autowired
    NoticeMessageMapper noticeMessageMapper;

    /**
     * 获取该用户或角色可读的数据
     * <br>
     * 在方法执行完成后，过滤返回的集合或数组，筛选出当前用户／角色具有READ权限的数据
     * filterObject 表示方法的返回的集合／数组中的元素，这里就是NoticeMessage 消息对象
     *
     * 如管理员或者消息创建者才能看到对应的消息
     */
    @PostFilter("hasPermission(filterObject, 'READ')")
    public List<NoticeMessage> findAll() {
        return noticeMessageMapper.findAll();
    }

    /**
     * 在方法执行完成后，进行权限校验，如果表达式计算结果为false，即当前用户／角色不具备返回对象的READ权限，将抛出异常。
     */
    @PostAuthorize("hasPermission(returnObject, 'READ')")
    public NoticeMessage findById(Integer id) {
        return noticeMessageMapper.findById(id);
    }

    /**
     * 在方法调用之前，进行权限校验，判断当前用户／角色是否具备noticeMessage对象的CREATE权限
     */
    @PreAuthorize("hasPermission(#noticeMessage, 'CREATE')")
    public NoticeMessage save(NoticeMessage noticeMessage) {
        noticeMessageMapper.save(noticeMessage);
        return noticeMessage;
    }

    /**
     * 在方法调用之前，进行权限校验，判断当前用户／角色是否具备noticeMessage对象的WRITE权限。
     */
    @PreAuthorize("hasPermission(#noticeMessage, 'WRITE')")
    public void update(NoticeMessage noticeMessage) {
        noticeMessageMapper.update(noticeMessage);
    }

}