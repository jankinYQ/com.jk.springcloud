package com.jk.springcloud.clients;

import com.jk.springcloud.clients.fallback.UserClientFallbackFactory;
import com.jk.springcloud.common.R;
import com.jk.springcloud.entity.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @program: com.jk.springcloud
 * @description:
 * @author: JanKin
 * @create: 2022-12-04 21:03
 */

@FeignClient(value = "noticeservice")
public interface NoticeClient {

}
