package com.project.MedicalClinic.user;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class PasswordModel {

    @NotNull(message = "is required")
    @Size(min = 1, message = "is required")
    private String currentPassword;

    @NotNull(message = "is required")
    @Pattern(regexp="^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message="Parola trebuie sa contina cel putin o litera mare, una mica, un caracter special, un numar si lungimea de minim de 8 caractere")
    @Size(min = 1, message = "is required")
    private String newPassword;

    @NotNull(message = "is required")
    @Size(min = 1, message = "is required")
    private String confirmNewPassword;

    public PasswordModel() {}

    public PasswordModel(String currentPassword, String newPassword, String confirmNewPassword) {
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
        this.confirmNewPassword = confirmNewPassword;
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmNewPassword() {
        return confirmNewPassword;
    }

    public void setConfirmNewPassword(String confirmNewPassword) {
        this.confirmNewPassword = confirmNewPassword;
    }
}
