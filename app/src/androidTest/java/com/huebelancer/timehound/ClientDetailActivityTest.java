package com.huebelancer.timehound;

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

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasMinimumChildCount;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withResourceName;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

/**
 * Created by mahuebel on 10/21/17.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class ClientDetailActivityTest {


    @Rule
    public ActivityTestRule<ClientsActivity> mActivityRule =
            new ActivityTestRule(ClientsActivity.class);


    @Rule
    public ActivityTestRule<ClientDetailActivity> mDetailRule =
            new ActivityTestRule(ClientDetailActivity.class);


    private final String testClient = "Test Client";

    @Test
    public void testDoesClockWork() {

        addOrShowTestClient();

        onView(withText(testClient)).perform(click());

        try {
            onView(withResourceName("ic_alarm_black_24dp")).check(matches(isDisplayed()));
        } catch (Exception e) {
            onView(withId(R.id.addClockFab)).perform(click());
            onView(withResourceName("ic_alarm_black_24dp")).check(matches(isDisplayed()));
        }

        onView(withId(R.id.addClockFab)).perform(click());

        onView(withResourceName("ic_alarm_off_black_24dp")).check(matches(isDisplayed()));

        onView(withId(R.id.addClockFab)).perform(click());

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
}
