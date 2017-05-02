package app.outlay.example;

import app.outlay.core.executor.DefaultSubscriber;
import app.outlay.domain.interactor.GetExpensesUseCase;;
import app.outlay.domain.model.Category;
import app.outlay.domain.model.Expense;
import app.outlay.domain.model.Report;
import app.outlay.mvp.presenter.ExpensesListPresenter;
import app.outlay.view.fragment.ExpensesListFragment;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ExpensesListPresenterTest {

    private ExpensesListPresenter expensesListPresenter;
    private Report report = new Report();
    private Date dateFrom = new Date();
    private Date dateTo = new Date();
    private String categoryId = "testString";

    @Mock
    private GetExpensesUseCase getExpensesUseCaseMock;

    @Mock
    private ExpensesListFragment expensesListFragmentMock;

    @Captor
    private ArgumentCaptor<DefaultSubscriber<Report>> ReportSubscriberCaptor;


    @Before
    public void Init(){
        expensesListPresenter = new ExpensesListPresenter(getExpensesUseCaseMock);
        expensesListPresenter.attachView(expensesListFragmentMock);

        report.setStartDate(dateFrom);
        report.setEndDate(dateTo);

        ArrayList<Expense> expenseList = new ArrayList<>();
        Expense expense =  new Expense();

        Category category = new Category();
        category.setColor(1);
        category.setIcon("IconString");
        category.setId("1");
        category.setOrder(2);
        category.setTitle("TestCategory");

        expense.setAmount(BigDecimal.valueOf(0));
        expense.setCategory(category);
        expense.setId("1");

        expenseList.add(expense);

        report.setExpenses(expenseList);
    }

    @Test
    public void findExpenses_UsesGetExpensesUseCase(){
        expensesListPresenter.findExpenses(dateFrom, dateTo, categoryId);

        verify(getExpensesUseCaseMock).execute(any(),any());
    }

    @Test
    public void findExpenses_DefaultSubscriber_ShowsReportView_OnNext(){
        expensesListPresenter.findExpenses(dateFrom, dateTo, categoryId);

        verify(getExpensesUseCaseMock).execute(any(),ReportSubscriberCaptor.capture());
        ReportSubscriberCaptor.getValue().onNext(report);
        verify(expensesListFragmentMock).showReport(eq(report));
    }
}
