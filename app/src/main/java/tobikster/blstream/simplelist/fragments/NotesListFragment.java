package tobikster.blstream.simplelist.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

import tobikster.blstream.simplelist.R;
import tobikster.blstream.simplelist.adapters.NotesListAdapter;
import tobikster.blstream.simplelist.database.NotesDataSource;
import tobikster.blstream.simplelist.dialogs.CreateNoteDialog;
import tobikster.blstream.simplelist.model.Note;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotesListFragment extends Fragment implements SearchView.OnQueryTextListener, AbsListView.MultiChoiceModeListener, AdapterView.OnItemLongClickListener, NotesListAdapter.OnItemInteractionListener {

	@SuppressWarnings("unused")
	public static final String LOGCAT_TAG = "NotesListFragment2";
	private ListView mListView;
	private FloatingActionButton mAddNoteFAB;
	private NotesDataSource mNotesDataSource;
	private List<Note> mNotes;
	private NotesListAdapter mAdapter;
	private ActionMode mActionMode;
	private Note mEditedNote;

	public static NotesListFragment newInstance() {
		return new NotesListFragment();
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mEditedNote = null;
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_notes_list, container, false);

		mListView = (ListView)view.findViewById(R.id.list_view);
		mAddNoteFAB = (FloatingActionButton)view.findViewById(R.id.add_note_fab);

		mNotesDataSource = new NotesDataSource(getActivity());
		mNotesDataSource.open();
		mNotes = mNotesDataSource.getAllNotes();

		return view;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		setHasOptionsMenu(true);

		mAdapter = new NotesListAdapter(getActivity(), mNotes, this);
		mListView.setAdapter(mAdapter);
		mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		mListView.setMultiChoiceModeListener(this);
		mListView.setOnItemLongClickListener(this);

		mAddNoteFAB.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				CreateNoteDialog.newInstance().show(getFragmentManager(), "createNoteDialog");
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();
		mNotesDataSource.open();
	}

	@Override
	public void onPause() {
		super.onPause();
		mNotesDataSource.close();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.notes_list_fragment, menu);

		final MenuItem item = menu.findItem(R.id.action_search);
		final SearchView searchView = (SearchView)MenuItemCompat.getActionView(item);
		searchView.setOnQueryTextListener(this);
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		return false;
	}

	@Override
	public boolean onQueryTextChange(String query) {
		mAdapter.getFilter().filter(query);
		return true;
	}

	public void addNote(String noteTitle) {
		Note note = new Note(noteTitle);
		note = mNotesDataSource.createNote(note);
		mAdapter.addNote(note);
	}

	public void editNote(String noteTitle) {
		mEditedNote.setTitle( noteTitle);
		Note note = mNotesDataSource.modifyNote(mEditedNote);
		mAdapter.modifyNote(note);
		mEditedNote = null;
	}

	@Override
	public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
		mode.setTitle(Integer.toString(mListView.getCheckedItemCount()));
	}

	@Override
	public boolean onCreateActionMode(ActionMode mode, Menu menu) {
		mode.getMenuInflater().inflate(R.menu.note_context_menu, menu);
		return true;
	}

	@Override
	public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
		menu.findItem(R.id.action_modify_note).setEnabled(!(mListView.getCheckedItemCount() > 1));
		return true;
	}

	@Override
	public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
		boolean eventConsumed  = false;
		switch(item.getItemId()) {
			case R.id.action_modify_note:
				if(mListView.getCheckedItemCount() == 1) {
					SparseBooleanArray checkedItems = mListView.getCheckedItemPositions();
					for(int i = 0; mEditedNote == null && i < mAdapter.getCount(); ++i) {
						if(checkedItems.get(i, false)) {
							mEditedNote = mAdapter.getItem(i);
						}
					}
					if(mEditedNote != null) {
						CreateNoteDialog.newInstance(mEditedNote.getTitle()).show(getFragmentManager(), "editNoteDialog");
					}
				}
				mode.finish();
				eventConsumed = true;
				break;

			case R.id.action_delete_note:
				SparseBooleanArray checkedItems = mListView.getCheckedItemPositions();
				for(int i = mAdapter.getCount() - 1; i >= 0; --i) {
					if(checkedItems.get(i, false)) {
						Note note = mAdapter.removeNote(i);
						mNotesDataSource.deleteNote(note);
					}
				}
				mode.finish();
				eventConsumed = true;
				break;
		}
		return eventConsumed;
	}

	@Override
	public void onDestroyActionMode(ActionMode mode) {
		mListView.clearChoices();
		for(int i = 0; i < mListView.getCount(); ++i) {
			mListView.setItemChecked(i, false);
		}
		mActionMode = null;
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		if(mActionMode == null) {
			mActionMode = getActivity().startActionMode(this);
		}
		mListView.setItemChecked(position, !mListView.isItemChecked(position));
		mActionMode.invalidate();
		if(mListView.getCheckedItemCount() == 0) {
			mActionMode.finish();
		}
		return true;
	}

	@Override
	public void onItemCheckboxStateChanged(int position, boolean state) {
		Note note = mAdapter.getItem(position);
		note.setChecked(state);
		mNotesDataSource.modifyNote(note);
		mAdapter.notifyDataSetChanged();
	}

	public void cancelEditNote() {
		mEditedNote = null;
	}
}
