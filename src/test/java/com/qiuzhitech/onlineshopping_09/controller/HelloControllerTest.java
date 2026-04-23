package com.qiuzhitech.onlineshopping_09.controller;

import com.qiuzhitech.onlineshopping_09.config.MyUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * com.qiuzhitech.onlineshopping_09.controller.HelloController 单元测试
 * 使用 @SpringBootTest 加载完整 Spring 上下文，通过 @Resource 注入真实 MyUser Bean，
 * 使用 @MockBean 对 Dependency 进行 Mock，使用 MockMvc 测试 HTTP 接口
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("com.qiuzhitech.onlineshopping_09.controller.HelloController 单元测试")
class HelloControllerTest {

    @Resource
    private MockMvc mockMvc;

    @MockBean
    private Dependency dependency;

    @Resource
    private HelloController helloController;

    @Resource(name = "Lyon")
    MyUser myUser;

    // ==================== hello() 接口测试 ====================

    @Test
    @DisplayName("GET /hello 应返回包含真实 MyUser(Lyon) 信息的字符串")
    void hello_shouldReturnHelloWorld() throws Exception {
        mockMvc.perform(get("/hello"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello Get World!" + myUser.toString()));
    }

    @Test
    @DisplayName("hello() 方法直接调用应返回包含真实 MyUser(Lyon) 信息的字符串")
    void hello_directCall_shouldReturnHelloWorld() {
        String result = helloController.hello();
        assertEquals("Hello Get World!" + myUser.toString(), result);
    }

    // ==================== echo() 接口测试 ====================

    @Test
    @DisplayName("GET /echo/{abc} 应返回 'Hello From: {abc}'")
    void echo_shouldReturnHelloFrom() throws Exception {
        mockMvc.perform(get("/echo/Peng"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello From: Peng"));
    }

    @Test
    @DisplayName("echo() 方法直接调用 - 普通字符串参数")
    void echo_directCall_withNormalString() {
        String result = helloController.echo("World");
        assertEquals("Hello From: World", result);
    }

    @Test
    @DisplayName("echo() 方法直接调用 - 空字符串参数")
    void echo_directCall_withEmptyString() {
        String result = helloController.echo("");
        assertEquals("Hello From: ", result);
    }

    @Test
    @DisplayName("echo() 方法直接调用 - 数字字符串参数")
    void echo_directCall_withNumericString() {
        String result = helloController.echo("123");
        assertEquals("Hello From: 123", result);
    }

    // ==================== sumPlug2() 方法测试 ====================

    @Test
    @DisplayName("sumPlug2() 使用真实 Dependency - 1+2+2 应等于 5")
    void sumPlug2_withRealDependency_shouldReturn5() {
        Dependency realDependency = new Dependency();
        HelloController controller = new HelloController(realDependency);

        int result = controller.sumPlug2(1, 2);

        assertEquals(5, result);
    }

    @Test
    @DisplayName("sumPlug2() 使用真实 Dependency - 3+4+2 应等于 9")
    void sumPlug2_withRealDependency_shouldReturn9() {
        Dependency realDependency = new Dependency();
        HelloController controller = new HelloController(realDependency);

        int result = controller.sumPlug2(3, 4);

        assertEquals(9, result);
    }

    @Test
    @DisplayName("sumPlug2() 使用 Mock Dependency - Mock 返回 100，结果应为 102")
    void sumPlug2_withMockedDependency_shouldReturnMockValuePlus2() {
        when(dependency.sum(anyInt(), anyInt())).thenReturn(100);

        int result = helloController.sumPlug2(3, 6);

        assertEquals(102, result);
        verify(dependency, times(1)).sum(3, 6);
    }

    @Test
    @DisplayName("sumPlug2() 使用 Mock Dependency - Mock 返回 0，结果应为 2")
    void sumPlug2_withMockedDependency_mockReturns0_shouldReturn2() {
        when(dependency.sum(anyInt(), anyInt())).thenReturn(0);

        int result = helloController.sumPlug2(0, 0);

        assertEquals(2, result);
        verify(dependency, times(1)).sum(0, 0);
    }

    @Test
    @DisplayName("sumPlug2() 使用 Mock Dependency - 负数输入")
    void sumPlug2_withMockedDependency_negativeInput() {
        when(dependency.sum(-3, -2)).thenReturn(-5);

        int result = helloController.sumPlug2(-3, -2);

        assertEquals(-3, result); // -5 + 2 = -3
        verify(dependency, times(1)).sum(-3, -2);
    }

    @Test
    @DisplayName("sumPlug2() 调用时，dependency.sum() 应该被精确调用一次")
    void sumPlug2_shouldCallDependencySumExactlyOnce() {
        when(dependency.sum(anyInt(), anyInt())).thenReturn(10);

        helloController.sumPlug2(4, 6);

        verify(dependency, times(1)).sum(4, 6);
        verifyNoMoreInteractions(dependency);
    }

    @Test
    @DisplayName("sumPlug2() 使用 Spy Dependency - 验证真实方法被调用")
    void sumPlug2_withSpyDependency_shouldCallRealMethod() {
        Dependency spyDependency = spy(new Dependency());
        HelloController controller = new HelloController(spyDependency);

        int result = controller.sumPlug2(5, 5);

        assertEquals(12, result); // 5 + 5 + 2 = 12
        verify(spyDependency, times(1)).sum(5, 5);
    }
}
