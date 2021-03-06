package app.outlay.example;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import app.outlay.domain.model.Category;
import app.outlay.domain.model.Expense;
import app.outlay.domain.model.Report;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;


@RunWith(MockitoJUnitRunner.class)
public class ReportTest {

    private Report report = new Report();
    private ArrayList<Expense> testExpenses = new ArrayList<>();

    @Before
    public void init() {
        Expense expense1 = new Expense();
        expense1.setAmount(BigDecimal.valueOf(0));
        expense1.setCategory(new Category().setId("1"));
        Expense expense2 = new Expense();
        expense2.setAmount(BigDecimal.valueOf(100));
        expense2.setCategory(new Category().setId("2"));
        Expense expense3 = new Expense();
        expense3.setAmount(BigDecimal.valueOf(-100.0));
        expense3.setCategory(new Category().setId("2"));

        testExpenses.add(expense1);
        testExpenses.add(expense2);
        testExpenses.add(expense3);
    }

    @Test
    public void getExpenses_IfNull_ResultNotNull() {
        List<Expense> expenses = report.getExpenses();
        assertNotNull(expenses);
    }

    @Test
    public void getTotalAmount_CalculatesCorrectAmount() {
        report.setExpenses(testExpenses);

        BigDecimal realAmount = BigDecimal.ZERO;
        for (Expense e : testExpenses)
            realAmount = realAmount.add(e.getAmount());
        BigDecimal calculatedAmount = report.getTotalAmount();

        assertEquals(calculatedAmount, realAmount);
    }

    @Test
    public void getTotalAmount_CalculatesCorrectAmount_IfNoExpenses() {
        BigDecimal totalAmount = report.getTotalAmount();

        assertEquals(totalAmount, BigDecimal.ZERO);
    }

    @Test
    public void groupByCategory_CreatesCorrectNumberOfGroups() {
        report.setExpenses(testExpenses);
        HashSet<Category> realCategories = new HashSet<>();
        for (Expense e : testExpenses)
            realCategories.add(e.getCategory());


        Map<Category, Report> result = report.groupByCategory();
        int createdCategoryCount = result.keySet().size();

        assertEquals(createdCategoryCount, realCategories.size());
    }

    @Test
    public void groupByCategory_CreatesEmptyMap_IfNoExpense() {
        Map<Category, Report> result = report.groupByCategory();

        assertEquals(result.keySet().size(), 0);
    }

}
