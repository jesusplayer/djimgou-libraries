package com.djimgou.session.jdbc;

import org.springframework.session.Session;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import java.time.Duration;
import java.util.Collections;
import java.util.Enumeration;

class CusHttpSession implements HttpSession {
    Session session;

    public CusHttpSession(Session sess) {
        this.session = sess;
    }

    @Override
    public long getCreationTime() {
        return session.getCreationTime().toEpochMilli();
    }

    @Override
    public String getId() {
        return session.getId();
    }

    @Override
    public long getLastAccessedTime() {
        return session.getLastAccessedTime().toEpochMilli();
    }

    @Override
    public ServletContext getServletContext() {
        return null;
    }

    @Override
    public void setMaxInactiveInterval(int i) {
        session.setMaxInactiveInterval(Duration.ofDays(i));
    }

    @Override
    public int getMaxInactiveInterval() {
        return session.getMaxInactiveInterval().getNano();
    }

    @Override
    public HttpSessionContext getSessionContext() {
        return null;
    }

    @Override
    public Object getAttribute(String ss) {
        return session.getAttribute(ss);
    }

    @Override
    public Object getValue(String ss) {
        return session.getAttribute(ss);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return Collections.enumeration(session.getAttributeNames());
    }

    @Override
    public String[] getValueNames() {
        return new String[0];
    }

    @Override
    public void setAttribute(String s, Object o) {
        session.setAttribute(s, o);
    }

    @Override
    public void putValue(String s, Object o) {
        session.setAttribute(s, o);
    }

    @Override
    public void removeAttribute(String s) {
        session.removeAttribute(s);
    }

    @Override
    public void removeValue(String s) {
        session.removeAttribute(s);
    }

    @Override
    public void invalidate() {
    }

    @Override
    public boolean isNew() {
        return false;
    }
}
