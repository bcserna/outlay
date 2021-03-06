package app.outlay.example;

import app.outlay.core.executor.DefaultSubscriber;
import app.outlay.domain.interactor.DeleteCategoryUseCase;
import app.outlay.domain.interactor.GetCategoryUseCase;
import app.outlay.domain.interactor.SaveCategoryUseCase;
import app.outlay.domain.model.Category;
import app.outlay.mvp.presenter.CategoryDetailsPresenter;
import app.outlay.mvp.view.CategoryDetailsView;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class CategoryDetailsPresenterTest {

    private CategoryDetailsPresenter categoryDetailsPresenter;
    private String testString = "testString";
    private Category category = new Category();

    @Mock
    private SaveCategoryUseCase saveCategoryUseCaseMock;

    @Mock
    private DeleteCategoryUseCase deleteCategoryUseCaseMock;

    @Mock
    private GetCategoryUseCase getCategoryUseCaseMock;

    @Mock
    private CategoryDetailsView categoryDetailsView;

    @Captor
    private ArgumentCaptor<DefaultSubscriber<Category>>  CategorySubscriberCaptor;

    @Before
    public void init(){
        categoryDetailsPresenter = new CategoryDetailsPresenter(saveCategoryUseCaseMock, deleteCategoryUseCaseMock, getCategoryUseCaseMock);
        categoryDetailsPresenter.attachView(categoryDetailsView);

        category.setColor(1);
        category.setIcon("IconString");
        category.setId("1");
        category.setOrder(2);
        category.setTitle("TestCategory");
    }

    @Test
    public void getCategory_UsesGetCategoryUseCase(){
        categoryDetailsPresenter.getCategory(testString);

        verify(getCategoryUseCaseMock).execute(eq(testString), any());
    }

    @Test
    public void getCategories_DefaultSubscriber_ShowsViewCategory_OnNext(){
        categoryDetailsPresenter.getCategory(testString);

        verify(getCategoryUseCaseMock).execute(any(), CategorySubscriberCaptor.capture());
        CategorySubscriberCaptor.getValue().onNext(category);
        verify(categoryDetailsView).showCategory(eq(category));
    }

    @Test
    public void updateCategory_UsesSaveCategoryUseCase(){
        categoryDetailsPresenter.updateCategory(category);

        verify(saveCategoryUseCaseMock).execute(eq(category), any());
    }

    @Test
    public void updateCategory_DefaultSubscriber_FinishesView_OnCompleted(){
        categoryDetailsPresenter.updateCategory(category);

        verify(saveCategoryUseCaseMock).execute(eq(category),CategorySubscriberCaptor.capture() );
        CategorySubscriberCaptor.getValue().onCompleted();
        verify(categoryDetailsView).finish();
    }

    @Test
    public void deleteCategory_UsesSaveCategoryUseCase(){
        categoryDetailsPresenter.deleteCategory(category);

        verify(deleteCategoryUseCaseMock).execute(eq(category), any());
    }

    @Test
    public void deleteCategory_DefaultSubscriber_FinishesView_OnCompleted(){
        categoryDetailsPresenter.deleteCategory(category);

        verify(deleteCategoryUseCaseMock).execute(eq(category),CategorySubscriberCaptor.capture() );
        CategorySubscriberCaptor.getValue().onCompleted();
        verify(categoryDetailsView).finish();
    }

}
