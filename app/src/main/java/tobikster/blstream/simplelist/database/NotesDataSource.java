package tobikster.blstream.simplelist.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import java.util.ArrayList;
import java.util.List;

import tobikster.blstream.simplelist.model.Note;

/**
 * Created by tobikster on 2015-10-22.
 */
public class NotesDataSource {

	private SQLiteDatabase mDatabase;
	private NotesDbHelper mDbHelper;
	private static final String[] ALL_COLUMNS = {NotesContract.NotesEntry._ID, NotesContract.NotesEntry.COLUMN_NAME_NOTE_TITLE, NotesContract.NotesEntry.COLUMN_NAME_NOTE_CHECKED};

	public NotesDataSource(Context context) {
		mDbHelper = new NotesDbHelper(context);
	}

	public void open() throws SQLiteException {
		mDatabase = mDbHelper.getWritableDatabase();
	}

	public void close() {
		mDbHelper.close();
	}

	public Note createOrModifyNote(Note newNote) {
		ContentValues values = new ContentValues();
		values.put(NotesContract.NotesEntry.COLUMN_NAME_NOTE_TITLE, newNote.getTitle());
		values.put(NotesContract.NotesEntry.COLUMN_NAME_NOTE_CHECKED, newNote.isChecked() ? 1 : 0);

		long id = newNote.getId();
		if(id > 0) {
			mDatabase.update(NotesContract.NotesEntry.TABLE_NAME, values, NotesContract.NotesEntry._ID + " = " + id, null);
		}
		else {
			id = mDatabase.insert(NotesContract.NotesEntry.TABLE_NAME, null, values);
		}

		Cursor cursor = mDatabase.query(NotesContract.NotesEntry.TABLE_NAME, ALL_COLUMNS, NotesContract.NotesEntry._ID + " = " + id, null, null, null, null);
		cursor.moveToFirst();
		Note note = cursorToNote(cursor);
		cursor.close();
		return note;
	}

	public Note createNote(Note note) {
		ContentValues values = new ContentValues();
		values.put(NotesContract.NotesEntry.COLUMN_NAME_NOTE_TITLE, note.getTitle());
		values.put(NotesContract.NotesEntry.COLUMN_NAME_NOTE_CHECKED, note.isChecked() ? 1 : 0);

		long id = mDatabase.insert(NotesContract.NotesEntry.TABLE_NAME, null, values);

		Cursor cursor = mDatabase.query(NotesContract.NotesEntry.TABLE_NAME, ALL_COLUMNS, NotesContract.NotesEntry._ID + " = " + id, null, null, null, null);
		cursor.moveToFirst();
		note = cursorToNote(cursor);
		cursor.close();
		return note;
	}

	public Note modifyNote(Note note) {
		ContentValues values = new ContentValues();
		values.put(NotesContract.NotesEntry.COLUMN_NAME_NOTE_TITLE, note.getTitle());
		values.put(NotesContract.NotesEntry.COLUMN_NAME_NOTE_CHECKED, note.isChecked() ? 1 : 0);

		mDatabase.update(NotesContract.NotesEntry.TABLE_NAME, values, NotesContract.NotesEntry._ID + " = " + note.getId(), null);
		return note;
	}

	public void deleteNote(Note note) {
		long id = note.getId();
		mDatabase.delete(NotesContract.NotesEntry.TABLE_NAME, NotesContract.NotesEntry._ID + " = " + id, null);
	}

	public List<Note> getAllNotes() {
		List<Note> notes = new ArrayList<>();
		Cursor cursor = mDatabase.query(NotesContract.NotesEntry.TABLE_NAME, ALL_COLUMNS, null, null, null, null, null);
		cursor.moveToFirst();
		while(!cursor.isAfterLast()) {
			Note note = cursorToNote(cursor);
			notes.add(note);
			cursor.moveToNext();
		}
		cursor.close();
		return notes;
	}

	private Note cursorToNote(Cursor cursor) {
		Note note = new Note();
		note.setId(cursor.getLong(0));
		note.setTitle(cursor.getString(1));
		note.setChecked(cursor.getInt(2) > 0);
		return note;
	}
}
