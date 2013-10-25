package dk.silverbullet.telemed.rest.listener;

import java.util.List;

public interface ChangePasswordListener {
    void changePasswordSucceeded();
    void changePasswordFailed(List<String> errorTexts);
    void communicationError();
}
