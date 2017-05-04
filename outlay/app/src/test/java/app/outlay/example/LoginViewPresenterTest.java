package app.outlay.example;

import app.outlay.core.executor.DefaultSubscriber;
import app.outlay.domain.interactor.LinkAccountUseCase;
import app.outlay.domain.interactor.ResetPasswordUseCase;
import app.outlay.domain.interactor.UserSignInUseCase;
import app.outlay.domain.interactor.UserSignUpUseCase;
import app.outlay.domain.model.Credentials;
import app.outlay.domain.model.User;
import app.outlay.mvp.presenter.LoginViewPresenter;
import app.outlay.mvp.view.LoginView;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class LoginViewPresenterTest {

    private LoginViewPresenter loginViewPresenter;
    private String userEmail = "user@user.com";
    private String userPassword = "GuestGuest";
    private User user = new User();
    private Throwable e = new Throwable("error msg");

    @Mock
    private UserSignInUseCase userSignInUseCaseMock;

    @Mock
    private UserSignUpUseCase userSignUpUseCaseMock;

    @Mock
    private ResetPasswordUseCase resetPasswordUseCaseMock;

    @Mock
    private LinkAccountUseCase linkAccountUseCaseMock;

    @Mock
    private LoginView loginViewMock;

    @Captor
    private ArgumentCaptor<DefaultSubscriber<User>> UserSubscriberCaptor;

    @Captor
    private ArgumentCaptor<DefaultSubscriber> SubscriberCaptor;

    @Before
    public void init(){
        loginViewPresenter = new LoginViewPresenter(userSignInUseCaseMock, userSignUpUseCaseMock, resetPasswordUseCaseMock, linkAccountUseCaseMock);
        loginViewPresenter.attachView(loginViewMock);

        user.setEmail(userEmail);
        user.setId("1");
    }

    @Test
    public void public_signIn(){
        loginViewPresenter.signIn(userEmail, userPassword);

        //private signIn indirect test
        verify(loginViewMock).setProgress(true);

        verify(userSignInUseCaseMock).execute(any(),UserSubscriberCaptor.capture());
        UserSubscriberCaptor.getValue().onNext(user);

        //private onAuthSuccess indirect test
        verify(loginViewMock).onSuccess(eq(user));

        UserSubscriberCaptor.getValue().onError(e);
        verify(loginViewMock).setProgress(false);
        verify(loginViewMock).error(eq(e));
    }

    @Test
    public void signInGuest_Test(){
        loginViewPresenter.signInGuest();

        //private signIn indirect test
        verify(loginViewMock).setProgress(true);
        verify(userSignInUseCaseMock).execute(eq(Credentials.GUEST),any());
    }

    @Test
    public void signUp_Test(){
        loginViewPresenter.signUp(userEmail,userPassword);

        verify(loginViewMock).setProgress(true);

        verify(userSignUpUseCaseMock).execute(any(),UserSubscriberCaptor.capture());
        UserSubscriberCaptor.getValue().onNext(user);

        //private onAuthSuccess indirect test
        verify(loginViewMock).onSuccess(eq(user));

        UserSubscriberCaptor.getValue().onError(e);
        verify(loginViewMock).setProgress(false);
        verify(loginViewMock).error(eq(e));
    }

    @Test
    public void linkAccount_UsesLinkAccountUseCase(){
        loginViewPresenter.linkAccount(userEmail,userPassword);

        verify(linkAccountUseCaseMock).execute(any(),any());
    }

    @Test
    public void linkAccount_DefaultSubscribe_OnNext(){
        loginViewPresenter.linkAccount(userEmail,userPassword);

        verify(linkAccountUseCaseMock).execute(any(), UserSubscriberCaptor.capture());
        UserSubscriberCaptor.getValue().onNext(user);
        verify(loginViewMock).onSuccess(eq(user));
    }

    @Test
    public void resetPassword_UsesResetPasswordUseCase(){
        loginViewPresenter.resetPassword(userEmail);

        verify(resetPasswordUseCaseMock).execute(any(),any());
    }

    @Test
    public void linkAccount_DefaultSubscribe_OnCompleted(){
        loginViewPresenter.resetPassword(userEmail);

        verify(resetPasswordUseCaseMock).execute(any(),SubscriberCaptor.capture());
        SubscriberCaptor.getValue().onCompleted();
        verify(loginViewMock).info(any());
    }
    @Test
    public void linkAccount_DefaultSubscribe_onError(){
        loginViewPresenter.resetPassword(userEmail);

        verify(resetPasswordUseCaseMock).execute(any(),SubscriberCaptor.capture());
        SubscriberCaptor.getValue().onError(e);
        verify(loginViewMock).error(any());
    }
    
}
