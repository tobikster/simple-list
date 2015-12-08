package tobikster.blstream.simplelist;

import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiScrollable;
import android.support.test.uiautomator.UiSelector;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import tobikster.blstream.simplelist.activities.MainActivity;

import static org.junit.Assert.assertEquals;

/**
 * Created by tobikster on 2015-11-17.
 */
@RunWith(AndroidJUnit4.class)
public class SimpleListTest {

	@Rule
	public final ActivityTestRule<MainActivity> mActivityRule;

	private UiDevice mDevice;

	public SimpleListTest() {
		mActivityRule = new ActivityTestRule<>(MainActivity.class);
	}

	@Before
	public void setUp() throws Exception {
		mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
	}

	@Test
	public void clickAddNoteButtonAndBack() throws Exception {
		mDevice.findObject(new UiSelector().resourceId("tobikster.blstream.simplelist:id/add_note_fab")).click();
		assertEquals(mDevice.findObject(new UiSelector().resourceId("tobikster.blstream.simplelist:id/note_title_editor")).getText(), "Add note");
		mDevice.pressBack();
	}

	//	@Test
	public void testStartSimpleList() throws Exception {
		mDevice.pressHome();
		mDevice.findObject(new UiSelector().resourceId("ginlemon.flowerpro:id/leftButton")).click();
		mDevice.findObject(new UiSelector().descriptionContains("utility")).click();
		UiScrollable gridView = new UiScrollable(new UiSelector().resourceId("ginlemon.flowerpro:id/gridView1"));
		gridView.scrollTextIntoView("SimpleList");
		gridView.getChildByText(new UiSelector().className("android.widget.TextView"), "SimpleList")
		        .clickAndWaitForNewWindow();

		mDevice.pressBack();

		mDevice.findObject(new UiSelector().descriptionContains("internet")).click();
		gridView.scrollTextIntoView("lokomo");
		gridView.getChildByText(new UiSelector().className("android.widget.TextView"), "lokomo")
		        .clickAndWaitForNewWindow();

		mDevice.findObject(new UiSelector().resourceId("pl.lokomo:id/activity_start__search_circle"))
		       .click();
	}
}
