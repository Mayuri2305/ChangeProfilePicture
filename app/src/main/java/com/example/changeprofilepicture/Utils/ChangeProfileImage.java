package com.example.changeprofilepicture.Utils;

import com.example.changeprofilepicture.Data.ProfilePicture;


public class ChangeProfileImage {
    private ProfilePicture profilePicture;
    private static final ChangeProfileImage ourInstance = new ChangeProfileImage();

    public static ChangeProfileImage getInstance() {
        return ourInstance;
    }

    private ChangeProfileImage() {
    }

    public ProfilePicture getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(ProfilePicture profilePicture) {
        this.profilePicture = profilePicture;
    }
}
