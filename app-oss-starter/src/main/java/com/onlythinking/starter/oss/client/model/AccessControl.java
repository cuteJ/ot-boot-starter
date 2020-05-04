package com.onlythinking.starter.oss.client.model;


/**
 * <p> The describe </p>
 *
 * @author Li Xingping
 */
public enum AccessControl {
    Default("default"),

    Private("private"),

    PublicRead("public-read"),

    PublicReadWrite("public-read-write");

    private String cannedAclString;

    AccessControl(String cannedAclString) {
        this.cannedAclString = cannedAclString;
    }

    @Override
    public String toString() {
        return this.cannedAclString;
    }

    public static AccessControl parse(String acl) {
        for (AccessControl cacl : AccessControl.values()) {
            if (cacl.toString().equals(acl)) {
                return cacl;
            }
        }
        throw new IllegalArgumentException("Unable to parse the provided acl " + acl);
    }
}
