package tobikster.blstream.simplelist.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import tobikster.blstream.simplelist.R;
import tobikster.blstream.simplelist.dialogs.CreateNoteDialog;
import tobikster.blstream.simplelist.fragments.NotesListFragment;
import tobikster.blstream.simplelist.model.Note;

public class MainActivity extends AppCompatActivity implements CreateNoteDialog.InteractionListener {

	private NotesListFragment mNotesListFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if(savedInstanceState == null) {
			mNotesListFragment = NotesListFragment.newInstance();
			getSupportFragmentManager().beginTransaction().replace(R.id.container, mNotesListFragment).commit();
		}
	}

	@Override
	public void onCreateNoteConfirmed(CreateNoteDialog dialog, String noteTitle) {
		if(noteTitle.equals("")) {
			Toast.makeText(this, R.string.alert_note_empty, Toast.LENGTH_LONG).show();
		}
		else {
			mNotesListFragment.addOrModifyNote(noteTitle);
			dialog.dismiss();
		}
	}

	@Override
	public void onCreateDialogCanceled(CreateNoteDialog dialog) {
		dialog.dismiss();
	}
}
