package tobikster.blstream.simplelist;

import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiScrollable;
import android.support.test.uiautomator.UiSelector;
import android.test.InstrumentationTestCase;

/**
 * Created by tobikster on 2015-11-17.
 */
public class SimpleListTest extends InstrumentationTestCase {
	private UiDevice mDevice;

	@Override
	public void setUp() throws Exception {
		super.setUp();

		mDevice = UiDevice.getInstance(getInstrumentation());
	}

	public void testStartSimpleList() throws Exception {
		mDevice.pressHome();
		mDevice.findObject(new UiSelector().resourceId("ginlemon.flowerpro:id/leftButton")).click();
		mDevice.findObject(new UiSelector().descriptionContains("utility")).click();
		UiScrollable gridView = new UiScrollable(new UiSelector().resourceId("ginlemon.flowerpro:id/gridView1"));
		gridView.scrollTextIntoView("SimpleList");
		gridView.getChildByText(new UiSelector().className("android.widget.TextView"), "SimpleList").clickAndWaitForNewWindow();

		mDevice.pressBack();

		mDevice.findObject(new UiSelector().descriptionContains("internet")).click();
		gridView.scrollTextIntoView("lokomo");
		gridView.getChildByText(new UiSelector().className("android.widget.TextView"), "lokomo").clickAndWaitForNewWindow();
	}
}
