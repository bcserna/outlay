package app.outlay.example;


import app.outlay.core.data.AppPreferences;
import app.outlay.core.executor.DefaultSubscriber;
import app.outlay.domain.interactor.DeleteExpenseUseCase;
import app.outlay.domain.interactor.GetCategoriesUseCase;
import app.outlay.domain.interactor.SaveExpenseUseCase;
import app.outlay.domain.model.Category;
import app.outlay.domain.model.Expense;
import app.outlay.domain.repository.ExpenseRepository;
import app.outlay.mvp.presenter.EnterExpensePresenter;
import app.outlay.mvp.view.EnterExpenseView;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class EnterExpensePresenterTest {

    private EnterExpensePresenter enterExpensePresenter;
    private List<Category> categories = new ArrayList<>();
    private Expense expense = new Expense();

    @Mock
    private GetCategoriesUseCase getCategoriesUseCaseMock;

    @Mock
    private SaveExpenseUseCase saveExpenseUseCaseMock;

    @Mock
    private DeleteExpenseUseCase deleteExpenseUseCaseMock;

    @Mock
    private AppPreferences appPreferencesMock;

    @Mock
    private ExpenseRepository expenseRepositoryMock;

    @Mock
    private EnterExpenseView enterExpenseViewMock;

    @Captor
    private ArgumentCaptor<DefaultSubscriber<List<Category>>> CategoriesSubscriberCaptor;

    @Captor
    private ArgumentCaptor<DefaultSubscriber<Expense>> ExpenseSubscriberCaptor;

    @Before
    public void init(){
        enterExpensePresenter = new EnterExpensePresenter(getCategoriesUseCaseMock, saveExpenseUseCaseMock, deleteExpenseUseCaseMock, appPreferencesMock, expenseRepositoryMock );
        enterExpensePresenter.attachView(enterExpenseViewMock);

        Category category = new Category();
        category.setColor(1);
        category.setIcon("IconString");
        category.setId("1");
        category.setOrder(2);
        category.setTitle("TestCategory");

        categories.add(category);

        expense.setAmount(BigDecimal.valueOf(0));
        expense.setCategory(category);
    }

    @Test
    public void getCategories_UsesGetCategoriesUseCase(){
        enterExpensePresenter.getCategories();

        verify(getCategoriesUseCaseMock).execute(any());
    }

    @Test
    public void getCategories_DefaultSubscriber_ShowsViewCategories_OnNext(){
        enterExpensePresenter.getCategories();

        verify(getCategoriesUseCaseMock).execute(CategoriesSubscriberCaptor.capture());
        CategoriesSubscriberCaptor.getValue().onNext(categories);
        verify(enterExpenseViewMock).showCategories(eq(categories));
    }

    @Test
    public void createExpense_UsesCreateExpenseUseCase(){
        enterExpensePresenter.createExpense(expense);

        verify(saveExpenseUseCaseMock).execute(eq(expense), any());
    }

    @Test
    public void createExpense_DefaultSubscriber_AlertExpenseSuccessViewExpense_OnNext(){
        enterExpensePresenter.createExpense(expense);

        verify(saveExpenseUseCaseMock).execute(eq(expense),ExpenseSubscriberCaptor.capture());
        ExpenseSubscriberCaptor.getValue().onNext(expense);
        verify(enterExpenseViewMock).alertExpenseSuccess(eq(expense));
    }

    @Test
    public void deleteExpense_UsesDeleteExpenseUseCase(){
        enterExpensePresenter.deleteExpense(expense);

        verify(deleteExpenseUseCaseMock).execute(eq(expense), any());
    }

}
