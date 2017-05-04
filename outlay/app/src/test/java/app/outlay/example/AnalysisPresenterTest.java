package app.outlay.example;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.*;

import app.outlay.core.executor.DefaultSubscriber;
import app.outlay.domain.interactor.GetCategoriesUseCase;
import app.outlay.domain.interactor.GetExpensesUseCase;
import app.outlay.domain.model.Category;
import app.outlay.domain.model.Report;
import app.outlay.mvp.presenter.AnalysisPresenter;
import app.outlay.mvp.view.AnalysisView;

@RunWith(MockitoJUnitRunner.class)
public class AnalysisPresenterTest {


    @Mock
    private GetCategoriesUseCase getCategoriesUseCaseMock;

    @Mock
    private GetExpensesUseCase getExpensesUseCaseMock;

    @Mock
    private AnalysisView analysisViewMock;

    @Captor
    private ArgumentCaptor<DefaultSubscriber<List<Category>>> CategoriesSubscriberCaptor;

    @Captor
    private ArgumentCaptor<DefaultSubscriber<Report>> ReportSubscriberCaptor;

    private AnalysisPresenter analysisPresenter;
    private Date date = new Date();
    private Category category = new Category();

    @Before
    public void init() {
        analysisPresenter = new AnalysisPresenter(getCategoriesUseCaseMock, getExpensesUseCaseMock);
        analysisPresenter.attachView(analysisViewMock);
    }

    @Test
    public void getCategories_UsesGetCategoriesUseCase() {
        analysisPresenter.getCategories();

        verify(getCategoriesUseCaseMock).execute(any());
    }

    @Test
    public void getCategories_DefaultSubscriber_SetsViewCategories_OnNext() {
        analysisPresenter.getCategories();
        ArrayList<Category> testList = new ArrayList<>();
        testList.add(new Category().setId("test"));

        verify(getCategoriesUseCaseMock).execute(CategoriesSubscriberCaptor.capture());
        CategoriesSubscriberCaptor.getValue().onNext(testList);
        verify(analysisViewMock).setCategories(testList);
    }

    @Test
    public void getExpenses_UsesGetExpensesUseCase() {
        analysisPresenter.getExpenses(date, date, category);

        verify(getExpensesUseCaseMock).execute(any(), any());
    }

    @Test
    public void getExpenses_DefaultSubscriber_ShowsAnalysis_OnNext_IfViewAttached() {
        analysisPresenter.getExpenses(date, date, category);

        verify(getExpensesUseCaseMock).execute(any(), ReportSubscriberCaptor.capture());
        ReportSubscriberCaptor.getValue().onNext(new Report());
        verify(analysisViewMock).showAnalysis(any());
    }

    @Test
    public void getExpenses_DefaultSubscriber_DoesntShowAnalysis_OnNext_IfViewDetached() {
        analysisPresenter.detachView(true);
        analysisPresenter.getExpenses(date, date, category);

        verify(getExpensesUseCaseMock).execute(any(), ReportSubscriberCaptor.capture());
        ReportSubscriberCaptor.getValue().onNext(new Report());
        verify(analysisViewMock, times(0)).showAnalysis(any());
    }
}
