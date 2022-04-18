package ethniconnect_backend.Resetpassword;

import ethniconnect_backend.UserCredentials.UserCredentials;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
@CrossOrigin()
@RestController
@RequestMapping(path = "api/v1")
@AllArgsConstructor
public class ResetPasswordController {

    @Autowired
    ResetPasswordService resetPasswordService;

    @PostMapping(path="resetpassword")
    public ResponseEntity<ResetPasswordResponse> resetpassword(@RequestBody ResetPasswordRequest resetPasswordRequest)
    {

        resetPasswordService.resetPassword(resetPasswordRequest);
        ResetPasswordResponse resetPasswordResponse = new ResetPasswordResponse();
        resetPasswordResponse.setEmailId(resetPasswordRequest.getEmailId());
        return new ResponseEntity(resetPasswordResponse,HttpStatus.OK);
    }

    @GetMapping(path = "/resetpassword/confirm")
    public RedirectView confirm(@RequestParam("token") String token) {
         String email=resetPasswordService.confirmToken(token);
        RedirectView redirectView = new RedirectView();
        String redirectUrl = "http://localhost:4200/resetpassword?email="+email;
        redirectView.setUrl(redirectUrl);

        return redirectView;
    }

    @PutMapping(path="updatepassword")
    public String updatepassword(@RequestBody UpdatePassword updatePassword)
    {
         resetPasswordService.updatePassword(updatePassword);
         return "Updated Password";
    }

}
