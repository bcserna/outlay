package app.outlay.example;

import app.outlay.core.executor.DefaultSubscriber;
import app.outlay.domain.interactor.DeleteExpenseUseCase;
import app.outlay.domain.interactor.GetCategoriesUseCase;
import app.outlay.domain.interactor.GetExpenseUseCase;
import app.outlay.domain.interactor.SaveExpenseUseCase;
import app.outlay.domain.model.Category;
import app.outlay.domain.model.Expense;
import app.outlay.mvp.presenter.ExpenseDetailsPresenter;
import app.outlay.mvp.view.ExpenseDetailsView;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ExpenseDetailsPresenterTest {

    private ExpenseDetailsPresenter expenseDetailsPresenter;
    private List<Category> categories = new ArrayList<>();
    private Expense expense = new Expense();
    private Date date = new Date();



    @Mock
    private GetCategoriesUseCase getCategoriesUseCaseMock;

    @Mock
    private GetExpenseUseCase getExpenseUseCaseMock;

    @Mock
    private SaveExpenseUseCase saveExpenseUseCaseMock;

    @Mock
    private DeleteExpenseUseCase deleteExpenseUseCaseMock;

    @Mock
    private ExpenseDetailsView expenseDetailsViewMock;

    @Captor
    private ArgumentCaptor<DefaultSubscriber<List<Category>>> CategoriesSubscriberCaptor;

    @Captor
    private ArgumentCaptor<DefaultSubscriber<Expense>> ExpenseSubscriberCaptor;

    @Before
    public void init(){
        expenseDetailsPresenter = new ExpenseDetailsPresenter(getCategoriesUseCaseMock, getExpenseUseCaseMock, saveExpenseUseCaseMock, deleteExpenseUseCaseMock);
        expenseDetailsPresenter.attachView(expenseDetailsViewMock);


        Category category = new Category();
        category.setColor(1);
        category.setIcon("IconString");
        category.setId("1");
        category.setOrder(2);
        category.setTitle("TestCategory");

        categories.add(category);

        expense.setAmount(BigDecimal.valueOf(0));
        expense.setCategory(category);
        expense.setId("1");
    }

    @Test
    public void findExpense_UsesCreateExpenseUseCase(){
        expenseDetailsPresenter.findExpense(expense.getId(), date);

        verify(getExpenseUseCaseMock).execute(any(), any());
    }

    @Test
    public void findExpense_DefaultSubscriber_ShowExpense_OnNext() {
        expenseDetailsPresenter.findExpense(expense.getId(), date);

        verify(getExpenseUseCaseMock).execute(any(), ExpenseSubscriberCaptor.capture());
        ExpenseSubscriberCaptor.getValue().onNext(expense);
        verify(expenseDetailsViewMock).showExpense(eq(expense));
    }
    @Test
    public void getCategories_UsesGetCategoriesUseCase(){
        expenseDetailsPresenter.getCategories();

        verify(getCategoriesUseCaseMock).execute(any());
    }

    @Test
    public void getCategories_DefaultSubscriber_ShowsViewCategories_OnNext(){
        expenseDetailsPresenter.getCategories();

        verify(getCategoriesUseCaseMock).execute(CategoriesSubscriberCaptor.capture());
        CategoriesSubscriberCaptor.getValue().onNext(categories);
        verify(expenseDetailsViewMock).showCategories(eq(categories));
    }

    @Test
    public void updateExpense_UsesCreateExpenseUseCase(){
        expenseDetailsPresenter.updateExpense(expense);

        verify(saveExpenseUseCaseMock).execute(eq(expense), any());
    }

    @Test
    public void deleteExpense_UsesDeleteExpenseUseCase(){
        expenseDetailsPresenter.deleteExpense(expense);

        verify(deleteExpenseUseCaseMock).execute(eq(expense), any());
    }
}
