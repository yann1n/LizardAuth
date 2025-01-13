package org.Lizard.lizardAuth.models;

public class PlayerData {

    private final String username;
    private final String hashedPassword;
    private final String salt;
    private boolean isLicensed;

    public PlayerData(String username, String hashedPassword, String salt, boolean isLicensed) {
        this.username = username;
        this.hashedPassword = hashedPassword;
        this.salt = salt;
        this.isLicensed = isLicensed;
    }

    public String getUsername() {
        return username;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public String getSalt() {
        return salt;
    }

    public boolean isLicensed() {
        return isLicensed;
    }

    public void setLicensed(boolean licensed) {
        isLicensed = licensed;
    }
}