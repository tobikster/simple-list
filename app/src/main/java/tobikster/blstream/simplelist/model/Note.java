package tobikster.blstream.simplelist.model;

/**
 * Created by tobikster on 2015-10-21.
 */
public
class Note {
	private long mId;
	private String mTitle;
	private boolean mChecked;

	public Note() {
		this(null, false);
	}

	public Note(String title) {
		this(title, false);
	}

	public Note(String title, boolean checked) {
		this(-1, title, checked);
	}

	public Note(long id, String title, boolean checked) {
		mId = id;
		mTitle = title;
		mChecked = checked;
	}

	public void setId(long id) {
		mId = id;
	}

	public long getId() {
		return mId;
	}

	public void setTitle(String title) {
		mTitle = title;
	}

	public String getTitle() {
		return mTitle;
	}

	public boolean isChecked() {
		return mChecked;
	}

	public void setChecked(boolean checked) {
		mChecked = checked;
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof Note && ((Note)o).getId() == mId;
	}
}
