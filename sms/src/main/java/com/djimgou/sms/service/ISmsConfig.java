package com.djimgou.sms.service;

/**
 * @author DJIMGOU NKENNE DANY MARC
 * 04/2022
 */
public interface ISmsConfig {
    String getFrom();

    String getDefaultTo();

    boolean isPersistable();
}
