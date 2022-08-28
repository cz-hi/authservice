package org.demo.authservice.entity;

/**
 * @author Zhi Chen
 * @date 2022/8/27
 * @description store information associated with user's token
 */
public class Session {
    private User user;
    private long time;

    public Session(User user, long time) {
        this.user = user;
        this.time = time;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
