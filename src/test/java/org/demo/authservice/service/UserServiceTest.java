package org.demo.authservice.service;

import org.demo.authservice.entity.Role;
import org.demo.authservice.entity.User;
import org.demo.authservice.service.impl.UserServiceImpl;
import org.demo.authservice.utils.StoreUtil;
import org.demo.authservice.utils.TokenUtil;
import org.junit.*;
import org.junit.rules.ExpectedException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Zhi Chen
 * @date 2022/8/27
 * @description
 */
public class UserServiceTest {
    private static UserServiceImpl userService;
    private long startTime;

    @Rule
    public ExpectedException exceptions = ExpectedException.none();

    @BeforeClass
    public static void createService() {
        userService = new UserServiceImpl();
        userService.start();
    }

    @AfterClass
    public static void destroyService() {
        userService.destroy();
    }

    @Before
    public void start(){
        System.out.println("开始执行测试方法");
        startTime = System.currentTimeMillis();
    }

    @After
    public void end(){
        System.out.println("执行测试方法结束,执行耗时：" + (System.currentTimeMillis() - startTime) + "ms");
    }

    @Test
    public void testCreateUserNotExit() {
        String userName = "Marry";
        String password = "123456";
        Assert.assertTrue(userService.createUser(userName, password));

    }

    @Test
    public void testCreateUserExist() {
        String userName = "Jack";
        String password = "123456";
        userService.createUser(userName, password);
        Assert.assertFalse(userService.createUser(userName, password));
    }

    @Test
    public void testDeleteExistUser() {
        String userName = "Hello";
        String password = "123456";
        userService.createUser(userName, password);
        Assert.assertTrue(userService.deleteUser(new User(userName, password)));
    }

    @Test
    public void testDeleteNotExistUser() {
        String userName = "Hello";
        String password = "123456";
        Assert.assertFalse(userService.deleteUser(new User(userName, password)));
    }

    @Test
    public void testCreateNotExistRole() {
        Assert.assertTrue(userService.createRole("systemManager"));
    }

    @Test
    public void testCreateExistRole() {
        userService.createRole("admin");
        Assert.assertFalse(userService.createRole("admin"));
    }

    @Test
    public void testDeleteExistRole() {
        userService.createRole("admin1");
        Assert.assertTrue(userService.deleteRole(new Role("admin1")));
    }

    @Test
    public void testDeleteNotExistRole() {
        Assert.assertFalse(userService.deleteRole(new Role("subManager")));
    }

    @Test
    public void testAddNewRoleToUser() {
        String userName = "Jack";
        String password = "123456";
        userService.createUser(userName, password);
        User user = new User(userName, password);
        String roleName = "admin";
        userService.createRole("admin");
        Role role = new Role(roleName);
        userService.addRoleToUser(user, role);
        Assert.assertTrue(StoreUtil.getUser("Jack").hasRole(role));
    }

    @Test
    public void testAddDuplicatedRole() {
        String userName = "David";
        String password = "123456";
        userService.createUser(userName, password);
        User user = new User(userName, password);
        String roleName = "admin";
        userService.createRole("admin");
        Role role = new Role(roleName);
        userService.addRoleToUser(user, role);
        // add again
        userService.addRoleToUser(user, role);
        Assert.assertTrue(StoreUtil.getUser("David").getRoles().size() == 1);
    }

    @Test
    public void testAuthenticateUser() {
        String userName = "Jack";
        String password = "123456";
        userService.createUser(userName, password);
        String token = userService.authenticateUser("Jack", "123456");
        Assert.assertTrue(token.length() > 0);
    }

    @Test
    public void testAuthenticateUserNotExitUser() {
        exceptions.expect(Exception.class);
        exceptions.expectMessage("user is not exist");
        String userName = "Jack";
        String password = "123456";
        userService.createUser(userName, password);
        String token = userService.authenticateUser("jack", "123456");
    }

    @Test
    public void testAuthenticateUserWrongPassword() {
        exceptions.expect(Exception.class);
        exceptions.expectMessage("password is not right");
        String userName = "Jack";
        String password = "123456";
        userService.createUser(userName, password);
        String token = userService.authenticateUser("Jack", "12345");
    }

    @Test
    public void testTokenExpired() throws InterruptedException {
        TokenUtil.setExpiredTime(3000);
        String userName = "Jack1";
        String password = "123456";
        userService.createUser(userName, password);
        String token = userService.authenticateUser("Jack1", "123456");
        Thread.sleep(5000);
        Assert.assertFalse(TokenUtil.validateToken(token));
    }

    @Test
    public void testInvalidateToken() {
        String userName = "Jack2";
        String password = "123456";
        userService.createUser(userName, password);
        String token = userService.authenticateUser("Jack2", "123456");
        userService.invalidateToken(token);
        Assert.assertFalse(TokenUtil.validateToken(token));
    }

    @Test
    public void testInvalidateWrongToken() {
        String userName = "Jack3";
        String password = "123456";
        userService.createUser(userName, password);
        String token = userService.authenticateUser("Jack3", "123456");
        userService.invalidateToken(token + "1");
        Assert.assertTrue(TokenUtil.validateToken(token));
    }

    @Test
    public void testCheckRole() {
        String userName = "Jack4";
        String password = "123456";
        userService.createUser(userName, password);
        User user = new User(userName, password);
        String roleName = "admin3";
        userService.createRole("admin3");
        userService.createRole("manager3");
        Role role = new Role(roleName);
        userService.addRoleToUser(user, role);
        String token = userService.authenticateUser("Jack4", "123456");
        Assert.assertTrue(userService.checkRole(token, role));
    }

    @Test
    public void testCheckRoleNotBelong() {
        String userName = "Chen";
        String password = "123456";
        userService.createUser(userName, password);
        User user = new User(userName, password);
        String roleName = "admin2";
        String roleName2 = "manager2";
        userService.createRole("admin2");
        userService.createRole("manager2");
        Role role = new Role(roleName);
        Role role2 = new Role(roleName2);
        userService.addRoleToUser(user, role);
        String token = userService.authenticateUser("Chen", "123456");
        Assert.assertFalse(userService.checkRole(token, role2));
    }

    @Test
    public void testCheckRoleOfInvalidToken() {
        exceptions.expect(Exception.class);
        exceptions.expectMessage("token is not valid");
        String userName = "Jack5";
        String password = "123456";
        userService.createUser(userName, password);
        User user = new User(userName, password);
        String roleName = "admin4";
        userService.createRole("admin4");
        userService.createRole("manager4");
        Role role = new Role(roleName);
        userService.addRoleToUser(user, role);
        String token = userService.authenticateUser("Jack5", "123456");
        userService.checkRole(token + "1", role);
    }

    @Test
    public void testCheckRoleOfExpiredToken() throws InterruptedException {
        exceptions.expect(Exception.class);
        exceptions.expectMessage("token is not valid");
        TokenUtil.setExpiredTime(3000);
        String userName = "Jack6";
        String password = "123456";
        userService.createUser(userName, password);
        User user = new User(userName, password);
        String roleName = "admin5";
        userService.createRole("admin5");
        userService.createRole("manager5");
        Role role = new Role(roleName);
        userService.addRoleToUser(user, role);
        String token = userService.authenticateUser("Jack6", "123456");
        TimeUnit.SECONDS.sleep(4);
        userService.checkRole(token, role);
    }

    @Test
    public void testGetAllRolesOfUser() {
        String userName = "Jack";
        String password = "123456";
        userService.createUser(userName, password);
        User user = new User(userName, password);
        String roleName = "admin";
        String roleName2 = "manager";
        userService.createRole("admin");
        userService.createRole("manager");
        Role role = new Role(roleName);
        Role role2 = new Role(roleName2);
        userService.addRoleToUser(user, role);
        userService.addRoleToUser(user, role2);
        String token = userService.authenticateUser("Jack", "123456");
        Assert.assertTrue(userService.allRoles(token).size() == 2);
    }

    @Test
    public void testGetAllRolesOfUserInvalidToken() {
        exceptions.expect(Exception.class);
        exceptions.expectMessage("token is not valid");
        String userName = "Jack3";
        String password = "123456";
        userService.createUser(userName, password);
        User user = new User(userName, password);
        String roleName = "admin";
        String roleName2 = "manager";
        userService.createRole("admin");
        userService.createRole("manager");
        Role role = new Role(roleName);
        Role role2 = new Role(roleName2);
        userService.addRoleToUser(user, role);
        userService.addRoleToUser(user, role2);
        String token = userService.authenticateUser("Jack3", "123456");
        List<Role> roles = userService.allRoles(token + "1");
    }

    @Test
    public void GetAllRolesOfUserExpiredToken() throws InterruptedException {
        exceptions.expect(Exception.class);
        exceptions.expectMessage("token is not valid");
        TokenUtil.setExpiredTime(3000);
        String userName = "Jack";
        String password = "123456";
        userService.createUser(userName, password);
        User user = new User(userName, password);
        String roleName = "admin";
        String roleName2 = "manager";
        userService.createRole("admin");
        userService.createRole("manager");
        Role role = new Role(roleName);
        Role role2 = new Role(roleName2);
        userService.addRoleToUser(user, role);
        userService.addRoleToUser(user, role2);
        String token = userService.authenticateUser("Jack", "123456");
        TimeUnit.SECONDS.sleep(4);
        List<Role> roles = userService.allRoles(token);
    }

}
