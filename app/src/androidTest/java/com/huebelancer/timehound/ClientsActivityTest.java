package com.huebelancer.timehound;

/**
 * Created by mahuebel on 10/19/17.
 */

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.huebelancer.timehound.Activities.ClientDetailActivity;
import com.huebelancer.timehound.Activities.ClientsActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

import static android.support.test.espresso.Espresso.*;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasMinimumChildCount;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withResourceName;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class ClientsActivityTest {

    @Rule
    public ActivityTestRule<ClientsActivity> mActivityRule =
            new ActivityTestRule(ClientsActivity.class);


    private final String testClient = "Test Client";

    @Test
    public void testAddClient() {

        addOrShowTestClient();

        onView(withText(testClient)).check(matches(isDisplayed()));
    }

    private void addOrShowTestClient() {
        onView(withId(R.id.floatingActionButton)).perform(click());

        onView(withId(R.id.editText)).perform(typeText(testClient));

        onView(withText("Add")).perform(click());

        try {
            onView(allOf(withId(R.id.recyclerView), hasMinimumChildCount(1)))
                    .check(matches(isDisplayed()));
        } catch (Exception e) {
            e.printStackTrace();
            onView(withText("OK")).perform(click());

            onView(allOf(withId(R.id.recyclerView), hasMinimumChildCount(1)))
                    .check(matches(isDisplayed()));
        }
    }


    @Test
    public void testHiddenClientsAreHidden() {

        addOrShowTestClient();

        onView(withText(testClient)).perform(click());

        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getTargetContext());
        onView(withText(R.string.hide_client)).perform(click());

        Espresso.pressBack();

        onView(withText(testClient)).check(doesNotExist());
    }

    @Test
    public void testHiddenClientsAreShownWhenNeeded() {
        addOrShowTestClient();

        onView(withText(testClient)).perform(click());

        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getTargetContext());
        onView(withText(R.string.hide_client)).perform(click());

        Espresso.pressBack();

        onView(withText(testClient)).check(doesNotExist());

        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getTargetContext());
        onView(withText(R.string.show_clients)).perform(click());

        onView(withText(testClient)).check(matches(isDisplayed()));
    }

    @Test
    public void testDoesClockWork() {

        addOrShowTestClient();

        onView(withText(testClient)).perform(click());

        onView(withId(R.id.detailFAB)).perform(click());

        onView(withId(R.id.addClockFab)).perform(click());

        onView(withId(R.id.chronometer)).check(matches(isDisplayed()));

        onView(allOf(withId(R.id.recyclerView), hasMinimumChildCount(1)))
                .check(matches(isDisplayed()));

        onView(withId(R.id.addClockFab)).perform(click());

    }


    @Test
    public void testDoesBillingWork() {

        addOrShowTestClient();

        onView(withText(testClient)).perform(click());

        onView(withId(R.id.detailFAB)).perform(click());

        onView(withId(R.id.addClockFab)).perform(click());

        onView(withId(R.id.chronometer)).check(matches(isDisplayed()));

        onView(allOf(withId(R.id.recyclerView), hasMinimumChildCount(1)))
                .check(matches(isDisplayed()));

        onView(withId(R.id.addClockFab)).perform(click());

        onView(withId(R.id.detailFAB)).perform(click());

        onView(withId(R.id.dayCard)).check(doesNotExist());

    }


    @Test
    public void testDoesAddingNoteWork() {

        addOrShowTestClient();

        onView(withText(testClient)).perform(click());

//        onView(withId(R.id.container)).check(matches(isDisplayed()));
//        onView(withId(R.id.container)).perform(click());
//        onView(withId(R.id.container)).perform(swipeLeft());

        onView(withText("NOTES")).perform(click());

        onView(withId(R.id.detailFAB)).perform(click());

        String newNote = "Test Note " + (new Date().getTime());

        onView(withId(R.id.editText)).perform(typeText(newNote));

        onView(withText("ADD")).perform(click());

        onView(allOf(withId(R.id.recyclerView), hasMinimumChildCount(1)))
                .check(matches(isDisplayed()));

        onView(withText(newNote)).check(matches(isDisplayed()));
    }



}
